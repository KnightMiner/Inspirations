package knightminer.inspirations;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import knightminer.inspirations.building.InspirationsBuilding;
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
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
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

    FMLJavaModLoadingContext.get().getModEventBus().register(this);
    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    modBus.register(this);
    modBus.register(new InspirationsShared());
    modBus.register(new InspirationsBuilding());
    modBus.register(new InspirationsUtility());
    modBus.register(new InspirationsTools());
    modBus.register(new InspirationsTweaks());
    modBus.register(new InspirationsRecipes());
    modBus.addListener(Config::configChanged);

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

  @SubscribeEvent
  void missingItemMappings(MissingMappings<Item> event) {
    for (Mapping<Item> mapping : event.getAllMappings()) {
      if (modID.equals(mapping.key.getNamespace())) {
        // vanilla added their own chain, replace ours with it
        if ("waypoint_compass".equals(mapping.key.getPath())) {
          mapping.remap(InspirationsTools.waypointCompasses.get(DyeColor.WHITE));
        }
      }
    }
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
