package knightminer.inspirations.library.recipe.anvil;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.BlockIngredient;
import knightminer.inspirations.library.recipe.RecipeSerializers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;

public class AnvilRecipe implements IRecipe<AnvilInventory> {

	private final ResourceLocation id;
	private final NonNullList<Ingredient> ingredients;
	private final String group;
	private final BlockState result;

	private AnvilRecipe(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, BlockState result) {
		this.id = id;
		this.group = group;
		this.ingredients = ingredients;
		this.result = result;
	}

	/**
	 * Test if the given recipe matches the input.
	 * When successful, inv.used is modified to reflect the used items.
	 */
	@Override
	public boolean matches(@Nonnull AnvilInventory inv, @Nonnull World worldIn) {
		// Used is set to true if that item was used in this recipe. First reset it.
		Arrays.fill(inv.used, false);
		return ingredients.stream().allMatch(ing -> checkIngredient(inv, ing));
	}

	private boolean checkIngredient(AnvilInventory inv, Ingredient ing) {
		if (ing instanceof BlockIngredient) {
			// It's a block, just test the state.
			return ((BlockIngredient) ing).testBlock(inv.getState());
		} else if (ing instanceof CompoundIngredient) {
			// Recurse, checking if any of these ingredients are present.
			boolean[] used = inv.used.clone();
			for(Ingredient subIng: ((CompoundIngredient) ing).getChildren()) {
				if (checkIngredient(inv, subIng)) {
					// Keep the state for this one.
					return true;
				}
				// Restore the state, since the compound didn't match.
				inv.used = used.clone();
			}
			return false;
		} else {
			// It's an item. We want to see if any item matches,
			// but not reuse items twice - since they're consumed.
			boolean found = false;
			for(int i = 0; i < inv.used.length; i++) {
				if (!inv.used[i] && ing.test(inv.getItems().get(i))) {
					inv.used[i] = true;
					found = true;
					break;
				}
			}
			return found;
		}
	}

	/**
	 *  Equivalent to getCraftingResult, but for blocks.
	 * @param inv The inventory that was matched.
	 * @return The block which should replace the existing one.
	 */
	public BlockState getBlockResult(@Nonnull AnvilInventory inv) {
		return result;
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
		return new ItemStack(Items.FURNACE);
	}

	@Nonnull
	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Nonnull
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return RecipeSerializers.ANVIL;
	}

	@Nonnull
	@Override
	public IRecipeType<?> getType() {
		return InspirationsRegistry.ANVIL_RECIPE_TYPE;
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
			ResourceLocation blockName = new ResourceLocation(JSONUtils.getString(result, "block"));
			Block resultBlock = ForgeRegistries.BLOCKS.getValue(blockName);
			if (resultBlock == null || resultBlock == Blocks.AIR) {
				throw new JsonParseException("Unknown block \"" + blockName + "\"");
			}
			BlockState state = resultBlock.getDefaultState();

			// Now, parse and apply the properties object to get a final state.
			JsonObject props = JSONUtils.getJsonObject(result, "properties", new JsonObject());
			StateContainer<Block, BlockState> cont = resultBlock.getStateContainer();
			for(Map.Entry<String, JsonElement> propEntry: props.entrySet()) {
				IProperty<?> prop = cont.getProperty(propEntry.getKey());
				if (prop == null) {
					throw new JsonParseException("Block \"" + blockName + "\" has no property \"" + propEntry.getKey() + "\"!");
				}
				if (!propEntry.getValue().isJsonPrimitive()) {
					throw new JsonParseException("Expected simple value for property \"" + propEntry.getKey() + "\", but got a " + propEntry.getValue().getClass().getSimpleName());
				}
				state = setProperty(state, prop, propEntry.getValue().getAsString());
			}

			return new AnvilRecipe(recipeId, group, inputs, state);
		}

		/**
		 * Setting the property needs a generic arg, so the parsed value can have the same type as the property.
		 */
		private <T extends Comparable<T>> BlockState setProperty(BlockState state, IProperty<T>prop, String value) {
			T parsedValue = prop
					.parseValue(value)
					.orElseThrow(() -> new JsonParseException("Invalid value \"" + value + "\" for property \"" + prop.getName() + "\""));
			return state.with(prop, parsedValue);
		}

		@Nullable
		@Override
		public AnvilRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
			String group = buffer.readString();
			BlockState result = Block.getStateById(buffer.readInt());
			int ingredientCount = buffer.readVarInt();
			NonNullList<Ingredient> inputs = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
			for(int i = 0; i < ingredientCount; i++) {
				inputs.set(i, Ingredient.read(buffer));
			}
			return new AnvilRecipe(recipeId, group, inputs, result);
		}

		@Override
		public void write(PacketBuffer buffer, AnvilRecipe recipe) {
			buffer.writeString(recipe.group);
			buffer.writeInt(Block.getStateId(recipe.result));
			buffer.writeVarInt(recipe.ingredients.size());
			for(Ingredient ingredient: recipe.ingredients) {
				ingredient.write(buffer);
			}
		}
	}
}
