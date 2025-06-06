package knightminer.inspirations;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.common.CommonsClientEvents;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.InspirationsCommons;
import knightminer.inspirations.common.data.GlobalLootProvider;
import knightminer.inspirations.common.datagen.FluidTransferProvider;
import knightminer.inspirations.common.datagen.InspirationsBlockTagsProvider;
import knightminer.inspirations.common.datagen.InspirationsDamageTypeProvider;
import knightminer.inspirations.common.datagen.InspirationsFluidTagsProvider;
import knightminer.inspirations.common.datagen.InspirationsItemTagsProvider;
import knightminer.inspirations.common.datagen.InspirationsLootTableProvider;
import knightminer.inspirations.common.datagen.RenderItemProvider;
import knightminer.inspirations.common.datagen.SpriteSourcesProvider;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

//import knightminer.inspirations.recipes.InspirationsRecipes;

@Mod(Inspirations.modID)
public class Inspirations {
  public static final String modID = "inspirations";
  public static final Logger log = LogManager.getLogger(modID);

  public Inspirations() {
    ModLoadingContext.get().registerConfig(Type.SERVER, Config.SERVER_SPEC);
    ModLoadingContext.get().registerConfig(Type.CLIENT, Config.CLIENT_SPEC);

    log.info("Loading early config file...");
    CommentedFileConfig earlyConfig = CommentedFileConfig
        .builder(FMLPaths.CONFIGDIR.get().resolve(modID + "-common.toml"))
        .sync()
        .preserveInsertionOrder()
        .writingMode(WritingMode.REPLACE)
        .build();
    earlyConfig.load();
    earlyConfig.save();
    Config.COMMON_SPEC.setConfig(earlyConfig);
    log.info("Config loaded.");

    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
    modBus.register(this);
    modBus.register(new InspirationsCommons());
    modBus.register(new InspirationsBuilding());
    modBus.register(new InspirationsUtility());
    modBus.register(new InspirationsTools());
    modBus.register(new InspirationsTweaks());
    modBus.register(new InspirationsCaudrons());
    modBus.addListener(Config::configChanged);
    MinecraftForge.EVENT_BUS.register(Inspirations.class);

    InspirationsNetwork.INSTANCE.setup();

    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> CommonsClientEvents::onConstruct);
  }

  @SubscribeEvent
  void gatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    PackOutput packOutput = gen.getPackOutput();
    CompletableFuture<Provider> lookupProvider = event.getLookupProvider();
    boolean server = event.includeServer();
    ExistingFileHelper existing = event.getExistingFileHelper();
    BlockTagsProvider blockTags = new InspirationsBlockTagsProvider(packOutput, lookupProvider, existing);
    gen.addProvider(server, blockTags);
    gen.addProvider(server, new InspirationsItemTagsProvider(packOutput, lookupProvider, blockTags.contentsGetter(), existing));
    gen.addProvider(server, new InspirationsFluidTagsProvider(packOutput, lookupProvider, existing));
    gen.addProvider(server, new InspirationsLootTableProvider(packOutput));
    gen.addProvider(server, new GlobalLootProvider(packOutput));

    RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder();
    InspirationsDamageTypeProvider.registerGenerator(registrySetBuilder);
    DatapackBuiltinEntriesProvider datapackRegistryProvider = new DatapackBuiltinEntriesProvider(packOutput, lookupProvider, registrySetBuilder, Set.of(modID));
    gen.addProvider(server, datapackRegistryProvider);
    gen.addProvider(server, new InspirationsDamageTypeProvider(packOutput, datapackRegistryProvider.getRegistryProvider(), existing));
    gen.addProvider(server, new FluidTransferProvider(packOutput));

    boolean client = event.includeClient();
    gen.addProvider(client, new SpriteSourcesProvider(packOutput, existing));
    gen.addProvider(client, new RenderItemProvider(packOutput));
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
