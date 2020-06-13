package knightminer.inspirations.library.recipe.anvil;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.BlockIngredient;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import knightminer.inspirations.library.recipe.RecipeTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AnvilRecipe implements IRecipe<AnvilInventory> {
	public static final String FROM_INPUT = "<input>";

	private final ResourceLocation id;
	private final NonNullList<Ingredient> ingredients;
	private final String group;
	// If null, keep the existing block.
	@Nullable
	private final Block result;
	// Properties to assign to the result, unparsed.
	// If value == "<input>", copy over.
	private final List<Pair<String, String>> properties;

	// When matching, holds the number of times each item was used.
	@Nullable
	private int[] used=null;

	private AnvilRecipe(
			ResourceLocation id,
			String group,
			NonNullList<Ingredient> ingredients,
			@Nullable Block result,
			List<Pair<String, String>> properties
	) {
		this.id = id;
		this.group = group;
		this.ingredients = ingredients;
		this.result = result;
		this.properties = properties;
	}

	/**
	 * Test if the given recipe matches the input.
	 * When successful, inv.used is modified to reflect the used items.
	 */
	@Override
	public boolean matches(@Nonnull AnvilInventory inv, @Nonnull World worldIn) {
		// Used is set to true if that item was used in this recipe. First reset it.
		used = new int[inv.getItems().size()];
		Arrays.fill(used, 0);
		boolean result = ingredients.stream().allMatch(ing -> checkIngredient(inv, ing));
		if (!result) {
			// Clear this, since it's not useful.
			used = null;
		}
		return result;
	}

	private boolean checkIngredient(AnvilInventory inv, Ingredient ing) {
		assert this.used != null;
		int[] used = this.used.clone();

		if (ing instanceof BlockIngredient) {
			// It's a block, just test the state.
			return ((BlockIngredient) ing).testBlock(inv.getState());
		} else if (ing instanceof CompoundIngredient) {
			for(Ingredient subIng: ((CompoundIngredient) ing).getChildren()) {
				if (checkIngredient(inv, subIng)) {
					// Keep the state for this one.
					return true;
				}
				// Restore the state, since the compound didn't match.
				this.used = used.clone();
			}
			return false;
		} else {
			// It's an item. We want to see if any item matches,
			// but not reuse items twice - since they're consumed.
			boolean found = false;
			for(int i = 0; i < this.used.length; i++) {
				ItemStack item = inv.getItems().get(i);
				if (this.used[i] < item.getCount() && ing.test(item)) {
					this.used[i]++;
					found = true;
					break;
				}
			}
			if (!found) {
				// Restore the state, since the ingredient didn't match.
				this.used = used.clone();
			}
			return found;
		}
	}

	/**
	 *  Equivalent to getCraftingResult, but for blocks.
	 * @param inv The inventory that was matched.
	 * @return The block which should replace the existing one.
	 */
	@Nonnull
	public BlockState getBlockResult(@Nonnull AnvilInventory inv) {
		BlockState state = result == null ? inv.getState() : result.getDefaultState();

		StateContainer<Block, BlockState> cont = state.getBlock().getStateContainer();
		StateContainer<Block, BlockState> inpContainer = inv.getState().getBlock().getStateContainer();

		for(Pair<String, String> prop: properties) {
			String key = prop.getFirst();
			String value = prop.getSecond();
			if (value.equals(FROM_INPUT)) {
				Property<?> inpProp = inpContainer.getProperty(key);
				if (inpProp == null) {
					InspirationsRegistry.log.warn(
							"No property \"{}\" to copy from block {} in Anvil recipe {}!",
							key, inv.getState().getBlock().getRegistryName(), id
					);
					continue;
				}
				// Convert to a string, so differing types and identical but distinct IProperty objects
				// still work.
				value = getProperty(state, inpProp);
			}
			Property<?> targProp = cont.getProperty(key);
			if(targProp == null) {
				InspirationsRegistry.log.warn(
						"Property \"{}\" is not valid for block {} in Anvil recipe {}!",
						key, state.getBlock().getRegistryName(), id
				);
				continue;
			}
			state = setProperty(state, targProp, value);
		}
		return state;
	}

	/**
	 * Consume the items used by the recipe, killing empty items.
	 * @param items Item entities involved.
	 */
	public void consumeItemEnts(List<ItemEntity> items) {
		if (used == null || items.size() != used.length) {
			return;
		}
		for(int i = 0; i < items.size(); i++) {
			if(used[i] > 0) {
				ItemEntity item = items.get(i);
				ItemStack newStack = item.getItem().copy();
				newStack.shrink(used[i]);
				if(newStack.isEmpty()) {
					item.remove();
				} else {
					item.setItem(newStack);
				}
			}
		}
	}
	/**
	 * Consume the items used by the recipe.
	 * @param items ItemStacks in the same order as passed to match().
	 */
	public void consumeItemStacks(List<ItemStack> items) {
		if (used == null || items.size() != used.length) {
			return;
		}
		for(int i = 0; i < items.size(); i++) {
			items.get(i).shrink(used[i]);
		}
	}

	/**
	 * Setting the property needs a generic arg, so the parsed value can have the same type as the property.
	 */
	private <T extends Comparable<T>> BlockState setProperty(BlockState state, Property<T>prop, String value) {
		Optional<T> parsedValue = prop.parseValue(value);
		if (parsedValue.isPresent()) {
			return state.with(prop, parsedValue.get());
		} else {
			InspirationsRegistry.log.warn(
					"Invalid value \"{}\" for block property {} of {} in anvil recipe {}!",
					value, prop.getName(), state.getBlock().getRegistryName(), id);
			return state;
		}
	}

	/**
	 * Getting the property needs a generic arg, so the parsed value can have the same type as the property.
	 */
	private <T extends Comparable<T>> String getProperty(BlockState state, Property<T> prop) {
		return state.get(prop).toString();
	}

	/**
	 * Not used, call getBlockResult.
	 * @param inv The inventory that was matched.
	 * @deprecated Use getBlockResult
	 */
	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull AnvilInventory inv) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height) {
		return true;
	}

	@Nonnull
	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public NonNullList<ItemStack> getRemainingItems(@Nonnull AnvilInventory inv) {
		return NonNullList.create();
	}

	@Nonnull
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return ingredients;
	}

	@Override
	public boolean isDynamic() {
		return false;
	}

	@Nonnull
	@Override
	public String getGroup() {
		return group;
	}

	@Nonnull
	@Override
	public ItemStack getIcon() {
		return new ItemStack(Items.ANVIL);
	}

	@Nonnull
	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Nonnull
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return RecipeSerializers.ANVIL_SMASHING;
	}

	@Nonnull
	@Override
	public IRecipeType<?> getType() {
		return RecipeTypes.ANVIL;
	}

	public static class Serializer
			extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>>
			implements IRecipeSerializer<AnvilRecipe>
	{
		@Nonnull
		@Override
		public AnvilRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
			String group = JSONUtils.getString(json, "group", "");
			NonNullList<Ingredient> inputs = NonNullList.create();
			JsonArray inputJSON = JSONUtils.getJsonArray(json, "ingredients");
			for(int i = 0; i < inputJSON.size(); i++) {
				Ingredient ingredient = Ingredient.deserialize(inputJSON.get(i));
				if (!ingredient.hasNoMatchingItems()) {
					inputs.add(ingredient);
				}
			}

			// Generate the output blockstate.
			JsonObject result = JSONUtils.getJsonObject(json, "result");
			String blockName = JSONUtils.getString(result, "block", FROM_INPUT);

			Block block;

			if (blockName.equals(FROM_INPUT)) {
				// We keep the block, maybe tranferring properties.
				block = null;
			} else {
				block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
				if (block == null) {
					throw new JsonParseException("Unknown block \"" + blockName + "\"");
				}
			}

			JsonObject props = JSONUtils.getJsonObject(result, "properties", new JsonObject());
			List<Pair<String, String>> propsMap = new ArrayList<>();
			for(Map.Entry<String, JsonElement> entry: props.entrySet()) {
				if (!entry.getValue().isJsonPrimitive()) {
					throw new JsonParseException("Expected simple value for property \"" + entry.getKey() + "\", but got a " + entry.getValue().getClass().getSimpleName());
				}
				propsMap.add(Pair.of(entry.getKey(), entry.getValue().getAsString()));
			}

			return new AnvilRecipe(recipeId, group, inputs, block, propsMap);
		}

		@Nullable
		@Override
		public AnvilRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
			String group = buffer.readString();
			String resultName = buffer.readString();
			Block result;
			if(resultName.isEmpty()) {
				result = null;
			} else {
				// Should never be missing, since we've already validated it.
				result = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(resultName));
			}

			int ingredientCount = buffer.readVarInt();
			int propsCount = buffer.readVarInt();

			NonNullList<Ingredient> inputs = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
			for(int i = 0; i < ingredientCount; i++) {
				inputs.set(i, Ingredient.read(buffer));
			}
			List<Pair<String, String>> props = new ArrayList<>(propsCount);
			for(int i = 0; i < propsCount; i++) {
				props.add(Pair.of(buffer.readString(), buffer.readString()));
			}
			return new AnvilRecipe(recipeId, group, inputs, result, props);
		}

		@Override
		public void write(PacketBuffer buffer, AnvilRecipe recipe) {
			buffer.writeString(recipe.group);
			if (recipe.result == null) { // Copy result
				buffer.writeString("");
			} else {
				buffer.writeString(recipe.result.getRegistryName().toString());
			}
			buffer.writeVarInt(recipe.ingredients.size());
			buffer.writeVarInt(recipe.properties.size());
			for(Ingredient ingredient: recipe.ingredients) {
				ingredient.write(buffer);
			}
			for(Pair<String, String> prop: recipe.properties) {
				buffer.writeString(prop.getFirst());
				buffer.writeString(prop.getSecond());
			}
		}
	}
}
