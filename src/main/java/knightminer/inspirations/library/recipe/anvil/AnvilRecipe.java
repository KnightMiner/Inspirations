package knightminer.inspirations.library.recipe.anvil;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class AnvilRecipe implements IRecipe<AnvilInventory> {

	private final ResourceLocation id;
	private final NonNullList<Ingredient> ingredients;
	private final String group;

	private AnvilRecipe(ResourceLocation id, String group, NonNullList<Ingredient> ingredients) {
		this.id = id;
		this.group = group;
		this.ingredients = ingredients;
	}

	/**
	 * Test if the given recipe matches the input. When successful, inv.used is modified to reflect the used items.
	 */
	@Override
	public boolean matches(@Nonnull AnvilInventory inv, @Nonnull World worldIn) {
		List<ItemStack> items = inv.getItems();
		Arrays.fill(inv.used, false);

		for(Ingredient ing: ingredients) {
			if (ing instanceof BlockIngredient) {
				// It's a block, just test the state.
				if (!((BlockIngredient) ing).testBlock(inv.getState())) {
					return false;
				}
			} else {
				// It's an item. We want to see if any item matches,
				// but not reuse items twice - since they're consumed.
				boolean found = false;
				for(int i = 0; i < items.size(); i++) {
					if (!inv.used[i] && ing.test(items.get(i))) {
						inv.used[i] = true;
						found = true;
						break;
					}
				}
				if (!found) {
					return false;
				}
			}
		}
		return true;
	}

	public BlockState getBlockReslt(@Nonnull AnvilInventory inv) {
		return Blocks.AIR.getDefaultState();
	}

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
			JsonArray inputJSON = JSONUtils.getJsonArray(json, "inputs");
			for(int i = 0; i < inputJSON.size(); i++) {
			Ingredient ingredient = Ingredient.deserialize(inputJSON.get(i));
			if (!ingredient.hasNoMatchingItems()) {
				inputs.add(ingredient);
			}
			}
			return new AnvilRecipe(recipeId, group, inputs);
		}

		@Nullable
		@Override
		public AnvilRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
			String group = buffer.readString();
			int ingredientCount = buffer.readVarInt();
			NonNullList<Ingredient> inputs = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
			for(int i = 0; i < ingredientCount; i++) {
				inputs.set(i, Ingredient.read(buffer));
			}
			return new AnvilRecipe(recipeId, group, inputs);
		}

		@Override
		public void write(PacketBuffer buffer, AnvilRecipe recipe) {
			buffer.writeString(recipe.group);
			buffer.writeVarInt(recipe.ingredients.size());
			for(Ingredient ingredient: recipe.ingredients) {
				ingredient.write(buffer);
			}
		}
	}
}
