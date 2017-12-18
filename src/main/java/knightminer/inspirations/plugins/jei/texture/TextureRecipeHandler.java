package knightminer.inspirations.plugins.jei.texture;

import javax.annotation.Nonnull;

import knightminer.inspirations.library.recipe.TextureRecipe;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

public class TextureRecipeHandler implements IRecipeWrapperFactory<TextureRecipe> {
	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull TextureRecipe recipe) {
		return new TextureRecipeWrapper(recipe);
	}
}
