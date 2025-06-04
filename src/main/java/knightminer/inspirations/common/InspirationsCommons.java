package knightminer.inspirations.common;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.library.recipe.ModItemList;
import knightminer.inspirations.library.recipe.crafting.ShapelessNoContainerRecipe;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.util.RetexturedHelper;

import java.util.function.Predicate;

/**
 * Base module for common code between the modules
 */
public class InspirationsCommons extends ModuleBase {
  public static ShapelessNoContainerRecipe.Serializer shapelessNoContainer;
  public static LootItemConditionType lootConfig;

  /**
   * Enum object for vanilla carpet blocks to aid in registration/lookups
   */
  public static final EnumObject<DyeColor,Block> VANILLA_CARPETS = new EnumObject.Builder<DyeColor,Block>(DyeColor.class)
      .put(DyeColor.WHITE, Blocks.WHITE_CARPET)
      .put(DyeColor.ORANGE, Blocks.ORANGE_CARPET)
      .put(DyeColor.MAGENTA, Blocks.MAGENTA_CARPET)
      .put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_CARPET)
      .put(DyeColor.YELLOW, Blocks.YELLOW_CARPET)
      .put(DyeColor.LIME, Blocks.LIME_CARPET)
      .put(DyeColor.PINK, Blocks.PINK_CARPET)
      .put(DyeColor.GRAY, Blocks.GRAY_CARPET)
      .put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_CARPET)
      .put(DyeColor.CYAN, Blocks.CYAN_CARPET)
      .put(DyeColor.PURPLE, Blocks.PURPLE_CARPET)
      .put(DyeColor.BLUE, Blocks.BLUE_CARPET)
      .put(DyeColor.BROWN, Blocks.BROWN_CARPET)
      .put(DyeColor.GREEN, Blocks.GREEN_CARPET)
      .put(DyeColor.RED, Blocks.RED_CARPET)
      .put(DyeColor.BLACK, Blocks.BLACK_CARPET)
      .build();

  @SubscribeEvent
  void register(RegisterEvent event) {
    // recipe serializers
    ResourceKey<? extends Registry<?>> key = event.getRegistryKey();
    if (key == Registries.RECIPE_SERIALIZER) {
      shapelessNoContainer = register(ForgeRegistries.RECIPE_SERIALIZERS, "shapeless_no_container", new ShapelessNoContainerRecipe.Serializer());

      // recipe ingredient type
      CraftingHelper.register(Inspirations.getResource("mod_item_list"), ModItemList.SERIALIZER);
    }
    // config condition
    else if (key == Registries.LOOT_CONDITION_TYPE) {
      ConfigEnabledCondition.ConditionSerializer confEnabled = new ConfigEnabledCondition.ConditionSerializer();
      CraftingHelper.register(confEnabled);
      lootConfig = register(BuiltInRegistries.LOOT_CONDITION_TYPE, "config", new LootItemConditionType(confEnabled));
    }
  }

  @SubscribeEvent
  void creativeTabs(BuildCreativeModeTabContentsEvent event) {
    ResourceKey<CreativeModeTab> key = event.getTabKey();
    if (key == CreativeModeTabs.NATURAL_BLOCKS) {
      if (Config.enableFlowers.getAsBoolean()) {
        accept(event, InspirationsBuilding.flower);
      }
      if (Config.smoothBlockCropGrowth.getAsBoolean()) {
        event.accept(InspirationsTweaks.cactusSeeds);
        event.accept(InspirationsTweaks.sugarCaneSeeds);
      }
    }
    else if (key == CreativeModeTabs.BUILDING_BLOCKS) {
      if (Config.enableMulch.getAsBoolean()) {
        accept(event, InspirationsBuilding.mulch);
      }
      if (Config.enablePath.getAsBoolean()) {
        accept(event, InspirationsBuilding.path);
      }
      if (Config.enableEnlightenedBush.getAsBoolean()) {
        accept(event, InspirationsBuilding.enlightenedBush, ItemTags.LEAVES);
      }
    }
    else if (key == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
      if (Config.enableRope.getAsBoolean()) {
        event.accept(InspirationsBuilding.rope);
        event.accept(InspirationsBuilding.vine);
      }
      if (Config.enableBookshelf.getAsBoolean()) {
        accept(event, InspirationsBuilding.shelf, ItemTags.PLANKS);
      }
      if (Config.enableColoredBooks.getAsBoolean()) {
        InspirationsBuilding.coloredBook.addVariants(event::accept);
      }
    }
    else if (key == CreativeModeTabs.REDSTONE_BLOCKS) {
      if (Config.enableGlassDoor.getAsBoolean()) {
        event.accept(InspirationsBuilding.glassDoorItem);
        event.accept(InspirationsBuilding.glassTrapdoor);
      }
      if (Config.enableCarpetedTrapdoor.getAsBoolean()) {
        accept(event, InspirationsUtility.carpetedTrapdoors);
      }
      if (Config.enableTorchLever.getAsBoolean()) {
        event.accept(InspirationsUtility.torchLeverItem);
        event.accept(InspirationsUtility.soulLeverItem);
      }
      if (Config.enableCollector.getAsBoolean()) {
        event.accept(InspirationsUtility.collector);
      }
      if (Config.enablePipe.getAsBoolean()) {
        event.accept(InspirationsUtility.pipe);
      }
      if (Config.enableRedstoneBook.getAsBoolean()) {
        event.accept(InspirationsBuilding.redstoneBook);
      }
    }
    else if (key == CreativeModeTabs.FOOD_AND_DRINKS) {
      if (Config.enableHeartbeet.getAsBoolean()) {
        event.accept(InspirationsTweaks.heartbeet);
      }
      if (Config.enableCauldronSoups.getAsBoolean()) {
        event.accept(InspirationsCaudrons.potatoSoupItem);
      }
      if (Config.enableMilkBottles.getAsBoolean()) {
        event.accept(InspirationsCaudrons.milkBottle);
      }
    }
    else if (key == CreativeModeTabs.INGREDIENTS) {
      if (Config.enableCauldronPotions.getAsBoolean()) {
        event.accept(InspirationsCaudrons.splashBottle);
        event.accept(InspirationsCaudrons.lingeringBottle);
      }
      if (Config.enableCauldronDyeing.getAsBoolean()) {
        accept(event, InspirationsCaudrons.simpleDyedWaterBottle);
      }
    }
    else if (key == CreativeModeTabs.TOOLS_AND_UTILITIES) {
      if (Config.enableRedstoneCharger.getAsBoolean()) {
        event.accept(InspirationsTools.redstoneCharger);
        event.accept(InspirationsTools.redstoneArrow);
      }
      if (Config.enableLock.getAsBoolean()) {
        event.accept(InspirationsTools.lock);
        event.accept(InspirationsTools.key);
      }
      if (Config.enableNorthCompass.getAsBoolean()) {
        event.accept(InspirationsTools.northCompass);
      }
      if (Config.enableDimensionCompass.getAsBoolean()) {
        event.accept(InspirationsTools.dimensionCompass);
      }
      if (Config.enableBarometer.getAsBoolean()) {
        event.accept(InspirationsTools.barometer);
      }
      if (Config.enablePhotometer.getAsBoolean()) {
        event.accept(InspirationsTools.photometer);
      }
      if (Config.enableCauldronSoups.getAsBoolean()) {
        event.accept(InspirationsCaudrons.mushroomStewBucket);
        event.accept(InspirationsCaudrons.beetrootSoupBucket);
        event.accept(InspirationsCaudrons.rabbitStewBucket);
        event.accept(InspirationsCaudrons.potatoSoupBucket);
      }
      if (Config.enableCauldronHoney.getAsBoolean()) {
        event.accept(InspirationsCaudrons.honeyBucket);
      }
    }
  }

  /** Adds the given enum object to the tab */
  private static void accept(BuildCreativeModeTabContentsEvent event, EnumObject<?, ? extends ItemLike> object) {
    object.forEach(item -> event.accept(item));
  }

  /** Adds the given enum object to the tab */
  private static void accept(BuildCreativeModeTabContentsEvent event, EnumObject<?, ? extends ItemLike> object, TagKey<Item> textures) {
    Predicate<ItemStack> acceptor =stack -> {
      event.accept(stack);
      // TODO: worth making this a JEI only thing?
      return !Config.showAllVariants.get();
    };
    object.forEach(item -> RetexturedHelper.addTagVariants(acceptor, item, textures));
  }
}
