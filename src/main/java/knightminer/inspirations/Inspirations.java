package knightminer.inspirations;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.data.FillBlockTexture;
import knightminer.inspirations.common.network.InspirationsNetwork;
import knightminer.inspirations.common.data.ConfigEnabled;
import knightminer.inspirations.common.data.PulseLoaded;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.ModItemList;
import knightminer.inspirations.library.recipe.ShapelessNoContainerRecipe;
import knightminer.inspirations.library.recipe.TextureRecipe;
//import knightminer.inspirations.plugins.LeatherWorksPlugin;
//import knightminer.inspirations.plugins.RatsPlugin;
//import knightminer.inspirations.plugins.TwilightForestPlugin;
//import knightminer.inspirations.plugins.top.TheOneProbePlugin;
import knightminer.inspirations.building.InspirationsBuilding;
//import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.pulsar.control.PulseManager;

import java.util.Arrays;
import java.util.stream.Collectors;

@Mod(Inspirations.modID)
public class Inspirations {
	public static final String modID = "inspirations";

	public static final Logger log = LogManager.getLogger(modID);

	public static PulseManager pulseManager;

	// We can't read the config very early on.
	public static boolean configLoaded = false;

	public Inspirations() {
		pulseManager = new PulseManager(Config.pulseConfig);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

		log.info("Loading replacements config file...");
		CommentedFileConfig repl_config = CommentedFileConfig
				.builder(FMLPaths.CONFIGDIR.get().resolve(modID + "-replacements.toml"))
				.sync().
				preserveInsertionOrder().
				writingMode(WritingMode.REPLACE).
				build();
		repl_config.load();
		repl_config.save();
		Config.SPEC_OVERRIDE.setConfig(repl_config);
		log.info("Config loaded.");

		MinecraftForge.EVENT_BUS.register(pulseManager);
		MinecraftForge.EVENT_BUS.addListener(this::registerRecipeTypes);
		FMLJavaModLoadingContext.get().getModEventBus().register(this);

		pulseManager.registerPulse(new InspirationsShared());
		pulseManager.registerPulse(new InspirationsBuilding());
		pulseManager.registerPulse(new InspirationsUtility());
		pulseManager.registerPulse(new InspirationsTools());
//		pulseManager.registerPulse(new InspirationsRecipes());
		pulseManager.registerPulse(new InspirationsTweaks());
		// plugins
//		pulseManager.registerPulse(new TheOneProbePlugin());
//		pulseManager.registerPulse(new LeatherWorksPlugin());
//		pulseManager.registerPulse(new RatsPlugin());
//		pulseManager.registerPulse(new TwilightForestPlugin());

//		// needs to be done statically, but only the recipes module uses it
//		if(pulseManager.isPulseLoaded(InspirationsRecipes.pulseID) && Config.INSTANCE.enableCauldronFluids.get()) {
//			FluidRegistry.enableUniversalBucket();
//		}
		pulseManager.enablePulses();

		InspirationsNetwork.instance.setup();
	}

	@SubscribeEvent
	public void configChanged(final ModConfig.ModConfigEvent configEvent) {
		configLoaded = true;

		InspirationsRegistry.setConfig("biggerCauldron", Config.enableBiggerCauldron());
		InspirationsRegistry.setConfig("expensiveCauldronBrewing", Config.expensiveCauldronBrewing());
		InspirationsRegistry.setBookKeywords(Arrays
				.stream(Config.bookKeywords.get().split(","))
				.map(String::trim)
				.collect(Collectors.toList())
		);
		InspirationsRegistry.setDefaultEnchantingPower(Config.defaultEnchantingPower.get().floatValue());
	}

	@SubscribeEvent
	public void registerRecipeTypes(RegistryEvent.Register<IRecipeSerializer<?>> event) {
		IForgeRegistry<IRecipeSerializer<?>> r = event.getRegistry();

		r.register(ShapelessNoContainerRecipe.SERIALIZER);
		r.register(TextureRecipe.SERIALIZER);

	}

	@SubscribeEvent
	public void registerMisc(FMLCommonSetupEvent event) {
		// These don't have registry events yet.
		PulseLoaded.Serializer pulseLoaded = new PulseLoaded.Serializer();
		ConfigEnabled.Serializer confEnabled = new ConfigEnabled.Serializer();

		CraftingHelper.register(pulseLoaded);
		CraftingHelper.register(confEnabled);
		CraftingHelper.register(Util.getResource("mod_item_list"), ModItemList.SERIALIZER);

		LootConditionManager.registerCondition(pulseLoaded);
		LootConditionManager.registerCondition(confEnabled);
		LootFunctionManager.registerFunction(new FillBlockTexture.Serializer(Util.getResource("fill_textured_block")));
	}
}
