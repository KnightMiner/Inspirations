package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.data.ConsumerWrapperBuilder;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Utilities to help in the creation of recipes
 */
public interface IRecipeBuilderUtils {
  /** Gets the base condition for the condition utility */
  ICondition baseCondition();

  /**
   * Gets the base recipe consumer
   * @return  Base recipe consumer
   */
  Consumer<IFinishedRecipe> getConsumer();

  /**
   * Gets a resource location under the Inspirations mod ID
   * @param name  Resource path
   * @return  Resource location for Inspirations
   */
  default ResourceLocation resource(String name) {
    return Inspirations.getResource(name);
  }

  /**
   * Gets a resource location string for the given path
   * @param name  Resource path
   * @return  Resource location string Inspirations
   */
  default String resourceName(String name) {
    return Inspirations.resourceName(name);
  }

  /**
   * Prefixes an items resource location with the given folder
   * @param item    Item to fetch resource location from
   * @param prefix  Name to prefix location with
   * @return  Prefixed resource location
   */
  default ResourceLocation prefix(IItemProvider item, String prefix) {
    return resource(prefix + Objects.requireNonNull(item.asItem().getRegistryName()).getPath());
  }

  /**
   * Wraps an items resource location with the given folder and suffix
   * @param item    Item to fetch resource location from
   * @param prefix  Name to prefix location with
   * @param suffix  Suffix for location
   * @return  Prefixed resource location
   */
  default ResourceLocation wrap(IItemProvider item, String prefix, String suffix) {
    return resource(prefix + Objects.requireNonNull(item.asItem().getRegistryName()).getPath() + suffix);
  }

  /**
   * Gets a consumer with the given condition, plus the module condition
   * @param conditions  Conditions to add
   * @return  Consumer with condition
   */
  default Consumer<IFinishedRecipe> withCondition(ICondition... conditions) {
    ConsumerWrapperBuilder builder = ConsumerWrapperBuilder.wrap().addCondition(baseCondition());
    for (ICondition condition : conditions) {
      builder.addCondition(condition);
    }
    return builder.build(Objects.requireNonNull(getConsumer()));
  }
}
