package knightminer.inspirations;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.block.type.ShelfType;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.datagen.InspirationsBlockTagsProvider;
import knightminer.inspirations.common.datagen.InspirationsFluidTagsProvider;
import knightminer.inspirations.common.datagen.InspirationsItemTagsProvider;
import knightminer.inspirations.common.datagen.InspirationsLootTableProvider;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.shared.SharedClientEvents;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.registration.RegistrationHelper;

import javax.annotation.Nullable;
import java.util.Locale;

//import knightminer.inspirations.recipes.InspirationsRecipes;

@SuppressWarnings("unused")
@Mod(Inspirations.modID)
public class Inspirations {
  public static final String modID = "inspirations";
  public static final Logger log = LogManager.getLogger(modID);

  public Inspirations() {
    ModLoadingContext.get().registerConfig(Type.SERVER, Config.SERVER_SPEC);
    ModLoadingContext.get().registerConfig(Type.CLIENT, Config.CLIENT_SPEC);

    log.info("Loading replacements config file...");
    CommentedFileConfig replacementConfig = CommentedFileConfig
        .builder(FMLPaths.CONFIGDIR.get().resolve(modID + "-replacements.toml"))
        .sync()
        .preserveInsertionOrder()
        .writingMode(WritingMode.REPLACE)
        .build();
    replacementConfig.load();
    replacementConfig.save();
    Config.OVERRIDE_SPEC.setConfig(replacementConfig);
    log.info("Config loaded.");

    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    modBus.register(this);
    modBus.register(new InspirationsShared());
    modBus.register(new InspirationsBuilding());
    modBus.register(new InspirationsUtility());
    modBus.register(new InspirationsTools());
    modBus.register(new InspirationsTweaks());
    modBus.register(new InspirationsRecipes());
    modBus.addListener(Config::configChanged);
    MinecraftForge.EVENT_BUS.register(Inspirations.class);

    InspirationsNetwork.INSTANCE.setup();

    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> SharedClientEvents::onConstruct);
  }

  @SubscribeEvent
  void gatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    if (event.includeServer()) {
      ExistingFileHelper existing = event.getExistingFileHelper();
      BlockTagsProvider blockTags = new InspirationsBlockTagsProvider(gen, existing);
      gen.addProvider(blockTags);
      gen.addProvider(new InspirationsItemTagsProvider(gen, existing, blockTags));
      gen.addProvider(new InspirationsFluidTagsProvider(gen, existing));
      gen.addProvider(new InspirationsLootTableProvider(gen));
    }
  }

  /** Shared missing mapping handler for blocks and items */
  @Nullable
  private static Block missingBlock(String name) {
    switch (name) {
      case "bookshelf": return InspirationsBuilding.shelf.get(ShelfType.NORMAL);
      case "ancient_bookshelf":  return InspirationsBuilding.shelf.get(ShelfType.ANCIENT);
      case "rainbow_bookshelf":  return InspirationsBuilding.shelf.get(ShelfType.RAINBOW);
      case "tomes_bookshelf":  return InspirationsBuilding.shelf.get(ShelfType.TOMES);
    }
    return null;
  }

  @SubscribeEvent
  static void missingBlockMappings(MissingMappings<Block> event) {
    RegistrationHelper.handleMissingMappings(event, modID, Inspirations::missingBlock);
  }

  @SubscribeEvent
  static void missingItemMappings(MissingMappings<Item> event) {
    RegistrationHelper.handleMissingMappings(event, modID, name -> {
      switch (name) {
        case "white_waypoint_compass":
        case "orange_waypoint_compass":
        case "magenta_waypoint_compass":
        case "light_blue_waypoint_compass":
        case "yellow_waypoint_compass":
        case "lime_waypoint_compass":
        case "pink_waypoint_compass":
        case "gray_waypoint_compass":
        case "light_gray_waypoint_compass":
        case "cyan_waypoint_compass":
        case "purple_waypoint_compass":
        case "blue_waypoint_compass":
        case "brown_waypoint_compass":
        case "green_waypoint_compass":
        case "red_waypoint_compass":
        case "black_waypoint_compass":
          return InspirationsTools.dimensionCompass;
      }
      Block block = missingBlock(name);
      return block != null ? block.asItem() : null;
    });
  }

  @SubscribeEvent
  static void missingFluidMappings(MissingMappings<Fluid> event) {
    RegistrationHelper.handleMissingMappings(event, modID, name -> {
      switch (name) {
        case "milk":
          return ForgeMod.MILK.get();
        case "flowing_milk":
          return ForgeMod.FLOWING_MILK.get();
      }
      return null;
    });
  }


  /* Utilities */

  /**
   * Gets a resource location under the Inspirations name
   * @param name Resource path
   * @return Resource location
   */
  public static ResourceLocation getResource(String name) {
    return new ResourceLocation(modID, name);
  }

  /**
   * Gets a resource location as a string, under the Inspirations namespace
   * @param name Resource path
   * @return Resource location string
   */
  public static String resourceName(String name) {
    return String.format("%s:%s", modID, name.toLowerCase(Locale.US));
  }

  /**
   * Gets the given name prefixed with Inspirations.
   * @param name Name to prefix
   * @return Prefixed name
   */
  public static String prefix(String name) {
    return String.format("%s.%s", modID, name.toLowerCase(Locale.US));
  }
}
