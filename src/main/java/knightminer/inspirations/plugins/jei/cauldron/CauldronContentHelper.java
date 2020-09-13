package knightminer.inspirations.plugins.jei.cauldron;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.CauldronContentType;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import mezz.jei.api.ingredients.IIngredientHelper;

import javax.annotation.Nullable;

/**
 * Content helper for all cauldron ingredient types
 */
public class CauldronContentHelper implements IIngredientHelper<ICauldronContents> {
  public static final CauldronContentHelper INSTANCE = new CauldronContentHelper();
  private CauldronContentHelper() {}

  /**
   * Helper that aliases some types as another in JEI
   * @param contents  Contents containing type
   * @return  Aliased type if relevant
   */
  private static CauldronContentType<?> getType(ICauldronContents contents) {
    // TODO: consider making this more generic so addons can add more aliases
    CauldronContentType<?> type = contents.getType();
    // if unfermented, treat as potion for recipe lookups
    if (type == CauldronContentTypes.UNFERMENTED_POTION) {
      return CauldronContentTypes.POTION;
    }
    return type;
  }

  @Nullable
  @Override
  public ICauldronContents getMatch(Iterable<ICauldronContents> options, ICauldronContents match) {
    CauldronContentType<?> type = getType(match);
    String name = match.getName();
    for (ICauldronContents content : options) {
      if (type == getType(content) && name.equals(content.getName())) {
        return content;
      }
    }
    return null;
  }

  @Override
  public ICauldronContents copyIngredient(ICauldronContents contents) {
    return contents;
  }

  @Override
  public String getDisplayName(ICauldronContents contents) {
    return contents.getDisplayName().getString();
  }

  @Override
  public String getUniqueId(ICauldronContents contents) {
    return CauldronContentTypes.getName(getType(contents)).toString() + ":" + contents.getName();
  }

  @Override
  public String getWildcardId(ICauldronContents contents) {
    return getUniqueId(contents);
  }

  @Override
  public String getResourceId(ICauldronContents contents) {
    return contents.getName();
  }

  @Override
  public String getModId(ICauldronContents contents) {
    String modId = contents.getModId();
    return modId == null ? Inspirations.modID : modId;
  }

  @Override
  public String getErrorInfo(@Nullable ICauldronContents contents) {
    if (contents == null) {
      return "null";
    }
    return getUniqueId(contents);
  }
}
