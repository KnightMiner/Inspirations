package knightminer.inspirations.library.recipe;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.recipe.ICauldronTransform;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Contains all recipe types used in Inspirations
 */
public class RecipeTypes {
  private static final DeferredRegister<RecipeType<?>> REGISTER = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, Inspirations.modID);
  /** Recipe type for cauldron recipes */
  public static final RegistryObject<RecipeType<ICauldronRecipe>> CAULDRON = register("cauldron");
  public static final RegistryObject<RecipeType<ICauldronTransform>> CAULDRON_TRANSFORM = register("cauldron_transform");

  public static void init() {
    REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
  }

  /**
   * Registers an Inspirations recipe type
   * @param name  Type name
   * @param <R>   Recipe base class
   * @return  Registered recipe type
   */
  private static <R extends Recipe<?>> RegistryObject<RecipeType<R>> register(String name) {
    return REGISTER.register(name, () -> new RecipeType<>() {
      @Override
      public String toString() {
        return Inspirations.modID + ":" + name;
      }
    });
  }
}
