package knightminer.inspirations.shared;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.ModuleBase;
import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.data.FillTexturedBlockLootFunction;
import knightminer.inspirations.library.recipe.ModItemList;
import knightminer.inspirations.library.recipe.crafting.ShapelessNoContainerRecipe;
import knightminer.inspirations.library.recipe.crafting.TextureRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.mantle.registration.adapter.RegistryAdapter;
import slimeknights.mantle.registration.object.EnumObject;

/**
 * Base module for common code between the modules
 */
@SuppressWarnings("unused")
public class InspirationsShared extends ModuleBase {
  public static LootConditionType lootConfig;
  public static LootFunctionType textureFunction;

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
  void registerRecipeTypes(RegistryEvent.Register<IRecipeSerializer<?>> event) {
    // recipe serializers
    RegistryAdapter<IRecipeSerializer<?>> registry = new RegistryAdapter<>(event.getRegistry());
    registry.register(new ShapelessNoContainerRecipe.Serializer(), "shapeless_no_container");
    registry.register(new TextureRecipe.Serializer(), "texture_recipe");

    // no event registries
    // config condition
    ConfigEnabledCondition.Serializer confEnabled = new ConfigEnabledCondition.Serializer();
    CraftingHelper.register(confEnabled);
    lootConfig = register(Registry.LOOT_CONDITION_TYPE, "config", new LootConditionType(confEnabled));

    // recipe ingredient type
    CraftingHelper.register(Inspirations.getResource("mod_item_list"), ModItemList.SERIALIZER);

    // texture block function
    textureFunction = register(Registry.LOOT_FUNCTION_TYPE, "fill_textured_block", new LootFunctionType(new FillTexturedBlockLootFunction.Serializer()));
  }
}
