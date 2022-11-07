package knightminer.inspirations.common;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.library.recipe.ModItemList;
import knightminer.inspirations.library.recipe.crafting.ShapelessNoContainerRecipe;
import net.minecraft.core.Registry;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.mantle.registration.adapter.RegistryAdapter;
import slimeknights.mantle.registration.object.EnumObject;

/**
 * Base module for common code between the modules
 */
@SuppressWarnings("unused")
public class InspirationsCommons extends ModuleBase {
  public static LootItemConditionType lootConfig;
  public static LootItemFunctionType textureFunction;

  /**
   * Enum object for vanilla carpet blocks to aid in registration/lookups
   */
  public static final EnumObject<DyeColor,Block> VANILLA_CARPETS = new EnumObject.Builder<DyeColor,Block>(DyeColor.class)
      .put(DyeColor.WHITE, Blocks.WHITE_CARPET.delegate)
      .put(DyeColor.ORANGE, Blocks.ORANGE_CARPET.delegate)
      .put(DyeColor.MAGENTA, Blocks.MAGENTA_CARPET.delegate)
      .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_CARPET.delegate)
      .put(DyeColor.YELLOW, Blocks.YELLOW_CARPET.delegate)
      .put(DyeColor.LIME, Blocks.LIME_CARPET.delegate)
      .put(DyeColor.PINK, Blocks.PINK_CARPET.delegate)
      .put(DyeColor.GRAY, Blocks.GRAY_CARPET.delegate)
      .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_CARPET.delegate)
      .put(DyeColor.CYAN, Blocks.CYAN_CARPET.delegate)
      .put(DyeColor.PURPLE, Blocks.PURPLE_CARPET.delegate)
      .put(DyeColor.BLUE, Blocks.BLUE_CARPET.delegate)
      .put(DyeColor.BROWN, Blocks.BROWN_CARPET.delegate)
      .put(DyeColor.GREEN, Blocks.GREEN_CARPET.delegate)
      .put(DyeColor.RED, Blocks.RED_CARPET.delegate)
      .put(DyeColor.BLACK, Blocks.BLACK_CARPET.delegate)
      .build();

  @SubscribeEvent
  void registerRecipeTypes(RegistryEvent.Register<RecipeSerializer<?>> event) {
    // recipe serializers
    RegistryAdapter<RecipeSerializer<?>> registry = new RegistryAdapter<>(event.getRegistry());
    registry.register(new ShapelessNoContainerRecipe.Serializer(), "shapeless_no_container");

    // no event registries
    // config condition
    ConfigEnabledCondition.ConditionSerializer confEnabled = new ConfigEnabledCondition.ConditionSerializer();
    CraftingHelper.register(confEnabled);
    lootConfig = register(Registry.LOOT_CONDITION_TYPE, "config", new LootItemConditionType(confEnabled));

    // recipe ingredient type
    CraftingHelper.register(Inspirations.getResource("mod_item_list"), ModItemList.SERIALIZER);
  }
}
