package knightminer.inspirations.recipes;

import com.google.common.eventbus.Subscribe;

import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.CauldronBrewingRecipe;
import knightminer.inspirations.library.recipe.cauldron.CauldronDyeRecipe;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import knightminer.inspirations.recipes.block.BlockSmashingAnvil;
import knightminer.inspirations.recipes.item.ItemDyedWaterBottle;
import knightminer.inspirations.recipes.recipe.ArmorDyeingCauldronRecipe;
import knightminer.inspirations.recipes.recipe.DyeCauldronWater;
import knightminer.inspirations.recipes.recipe.FillCauldronFromDyedBottle;
import knightminer.inspirations.recipes.recipe.FillCauldronFromFluidContainer;
import knightminer.inspirations.recipes.recipe.FillCauldronFromPotion;
import knightminer.inspirations.recipes.recipe.FillDyedBottleFromCauldron;
import knightminer.inspirations.recipes.recipe.FillFluidContainerFromCauldron;
import knightminer.inspirations.recipes.recipe.FillPotionFromCauldron;
import knightminer.inspirations.recipes.recipe.TippedArrowCauldronRecipe;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import knightminer.inspirations.shared.InspirationsShared;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsRecipes.pulseID, description = "Adds additional recipe types, including cauldrons and anvil smashing")
public class InspirationsRecipes extends PulseBase {
	public static final String pulseID = "InspirationsRecipes";

	@SidedProxy(clientSide = "knightminer.inspirations.recipes.RecipesClientProxy", serverSide = "knightminer.inspirations.common.CommonProxy")
	public static CommonProxy proxy;

	// blocks
	public static Block anvil;
	public static Block cauldron;

	// items
	public static ItemDyedWaterBottle dyedWaterBottle;


	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		if(Config.enableAnvilSmashing) {
			anvil = register(r, new BlockSmashingAnvil(), new ResourceLocation("anvil"));
		}
		if(Config.enableExtendedCauldron) {
			cauldron = register(r, new BlockEnhancedCauldron(), new ResourceLocation("cauldron"));
			registerTE(TileCauldron.class, "cauldron");
		}
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		if(Config.enableCauldronDyeing) {
			InspirationsRecipes.dyedWaterBottle = registerItem(r, new ItemDyedWaterBottle(), "dyed_bottle");
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();

		InspirationsRegistry.registerAnvilBreaking(Material.GLASS);
		registerCauldronRecipes();
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
		MinecraftForge.EVENT_BUS.register(RecipesEvents.class);
		registerCauldronBrewingRecipes();
	}

	private void registerCauldronRecipes() {
		if(!Config.enableExtendedCauldron) {
			return;
		}

		if(Config.enableCauldronDyeing) {
			InspirationsRegistry.addCauldronRecipe(FillDyedBottleFromCauldron.INSTANCE);
			InspirationsRegistry.addCauldronRecipe(FillCauldronFromDyedBottle.INSTANCE);
			InspirationsRegistry.addCauldronRecipe(ArmorDyeingCauldronRecipe.INSTANCE);

			for(EnumDyeColor color : EnumDyeColor.values()) {
				InspirationsRegistry.addCauldronRecipe(new DyeCauldronWater(color));
				InspirationsRegistry.addCauldronRecipe(new CauldronDyeRecipe(
						new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE),
						color,
						new ItemStack(Blocks.WOOL, 1, color.getMetadata())
						));

				InspirationsRegistry.addCauldronRecipe(new CauldronDyeRecipe(
						new ItemStack(Blocks.CARPET, 1, OreDictionary.WILDCARD_VALUE),
						color,
						new ItemStack(Blocks.CARPET, 1, color.getMetadata())
						));
			}
		}

		if(Config.enableCauldronBrewing) {
			addPotionBottle(Items.POTIONITEM, new ItemStack(Items.GLASS_BOTTLE));
			addPotionBottle(Items.SPLASH_POTION, InspirationsShared.splashBottle);
			addPotionBottle(Items.LINGERING_POTION, InspirationsShared.lingeringBottle);
			InspirationsRegistry.addCauldronRecipe(TippedArrowCauldronRecipe.INSTANCE);
		}

		if(Config.enableCauldronFluids) {
			InspirationsRegistry.addCauldronRecipe(FillCauldronFromFluidContainer.INSTANCE);
			InspirationsRegistry.addCauldronRecipe(FillFluidContainerFromCauldron.INSTANCE);
		}
	}

	private void registerCauldronBrewingRecipes() {
		if(Config.enableCauldronBrewing) {
			for(PotionHelper.MixPredicate<PotionType> recipe : PotionHelper.POTION_TYPE_CONVERSIONS) {
				InspirationsRegistry.addCauldronRecipe(new CauldronBrewingRecipe(recipe.input, recipe.reagent, recipe.output));
			}
		}
	}

	private static void addPotionBottle(Item potion, ItemStack bottle) {
		InspirationsRegistry.addCauldronRecipe(new FillCauldronFromPotion(potion, bottle));
		InspirationsRegistry.addCauldronRecipe(new FillPotionFromCauldron(potion, bottle));
	}
}
