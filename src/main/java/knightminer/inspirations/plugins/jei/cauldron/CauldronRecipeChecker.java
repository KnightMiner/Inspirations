package knightminer.inspirations.plugins.jei.cauldron;

import java.util.ArrayList;
import java.util.List;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ISimpleCauldronRecipe;
import net.minecraft.init.Items;import net.minecraft.init.PotionTypes;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.BannerPattern;

public class CauldronRecipeChecker {
	public static List<CauldronRecipeWrapper> getRecipes() {
		List<CauldronRecipeWrapper> recipes = new ArrayList<>();
		// add vanilla recipes
		recipes.add(new CauldronRecipeWrapper(new ItemStack(Items.GLASS_BOTTLE), PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER)));
		recipes.add(makeArmorWashRecipe(Items.LEATHER_HELMET));
		recipes.add(makeArmorWashRecipe(Items.LEATHER_CHESTPLATE));
		recipes.add(makeArmorWashRecipe(Items.LEATHER_LEGGINGS));
		recipes.add(makeArmorWashRecipe(Items.LEATHER_BOOTS));
		makeBannerRecipes(recipes);

		// grab recipes from the registry
		for(ICauldronRecipe recipe : InspirationsRegistry.getAllCauldronRecipes()) {
			if(recipe instanceof ISimpleCauldronRecipe) {
				ISimpleCauldronRecipe simpleRecipe = (ISimpleCauldronRecipe) recipe;
				if(simpleRecipe.getState() != null && simpleRecipe.getInputState() != null
						&& simpleRecipe.getInput() != null && !simpleRecipe.getInput().isEmpty()) {
					recipes.add(new CauldronRecipeWrapper(simpleRecipe));
				}
			}
		}

		return recipes;
	}

	private static CauldronRecipeWrapper makeArmorWashRecipe(ItemArmor item) {
		ItemStack output = new ItemStack(item);
		List<ItemStack> input = new ArrayList<>();
		for(EnumDyeColor color : EnumDyeColor.values()) {
			ItemStack stack = output.copy();
			item.setColor(stack, color.getColorValue());
			input.add(stack);
		}
		return new CauldronRecipeWrapper(input, output);
	}

	private static void makeBannerRecipes(List<CauldronRecipeWrapper> recipes) {
		for(EnumDyeColor color : EnumDyeColor.values()) {
			EnumDyeColor patternColor = color == EnumDyeColor.WHITE ? EnumDyeColor.GRAY : EnumDyeColor.WHITE;
			ItemStack output = new ItemStack(Items.BANNER, 1, color.getDyeDamage());

			List<ItemStack> inputs = new ArrayList<>();
			for(BannerPattern pattern : BannerPattern.values()) {
				if(pattern == BannerPattern.BASE) {
					continue;
				}
				ItemStack stack = output.copy();
				NBTTagCompound entityTag = stack.getOrCreateSubCompound("BlockEntityTag");
				NBTTagList patternList = new NBTTagList();
				entityTag.setTag("Patterns", patternList);

				NBTTagCompound patternTag = new NBTTagCompound();
				patternTag.setString("Pattern", pattern.getHashname());
				patternTag.setInteger("Color", patternColor.getDyeDamage());
				patternList.appendTag(patternTag);
				inputs.add(stack);
			}

			recipes.add(new CauldronRecipeWrapper(inputs, output));
		}
	}
}