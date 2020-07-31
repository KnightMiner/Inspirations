package knightminer.inspirations;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.datagen.InspirationsBlockTagsProvider;
import knightminer.inspirations.common.datagen.InspirationsItemTagsProvider;
import knightminer.inspirations.common.datagen.InspirationsLootTableProvider;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

//import knightminer.inspirations.recipes.InspirationsRecipes;

@SuppressWarnings("unused")
@Mod(Inspirations.modID)
public class Inspirations {
  public static final String modID = "inspirations";
  public static final Logger log = LogManager.getLogger(modID);

  // We can't read the config very early on.
  public static boolean configLoaded = false;

  /**
   * To avoid classloading, the function to call to update JEI for config changes.
   * If non-null, this will be {@link knightminer.inspirations.plugins.jei.JEIPlugin updateHiddenItems()}.
   */
  public static Runnable updateJEI = null;

  public Inspirations() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);

    log.info("Loading replacements config file...");
    CommentedFileConfig repl_config = CommentedFileConfig
        .builder(FMLPaths.CONFIGDIR.get().resolve(modID + "-replacements.toml"))
        .sync()
        .preserveInsertionOrder()
        .writingMode(WritingMode.REPLACE)
        .build();
    repl_config.load();
    repl_config.save();
    Config.SPEC_OVERRIDE.setConfig(repl_config);
    log.info("Config loaded.");

    FMLJavaModLoadingContext.get().getModEventBus().register(this);
    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    modBus.register(this);
    modBus.register(new InspirationsShared());
    modBus.register(new InspirationsBuilding());
    modBus.register(new InspirationsUtility());
    modBus.register(new InspirationsTools());
    modBus.register(new InspirationsTweaks());
    //		pulseManager.registerPulse(new InspirationsRecipes());

    InspirationsNetwork.INSTANCE.setup();
  }

  @SubscribeEvent
  void gatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    if (event.includeServer()) {
      BlockTagsProvider blockTags = new InspirationsBlockTagsProvider(gen);
      gen.addProvider(blockTags);
      gen.addProvider(new InspirationsItemTagsProvider(gen, blockTags));
      gen.addProvider(new InspirationsLootTableProvider(gen));
    }
  }

  @SubscribeEvent
  void configChanged(final ModConfig.ModConfigEvent configEvent) {
    configLoaded = true;

    InspirationsRegistry.setBookKeywords(Arrays.stream(Config.bookKeywords.get().split(","))
                                               .map(String::trim)
                                               .collect(Collectors.toList())
                                        );

    // If we have JEI, this will be set. It needs to run on the main thread...
    if (updateJEI != null) {
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().deferTask(updateJEI));
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
