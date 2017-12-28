package knightminer.inspirations.tweaks;

import com.google.common.eventbus.Subscribe;

import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.DyeCauldronRecipe;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.tweaks.block.BlockEnhancedCauldron;
import knightminer.inspirations.tweaks.block.BlockFittedCarpet;
import knightminer.inspirations.tweaks.block.BlockSmashingAnvil;
import knightminer.inspirations.tweaks.item.ItemDyedWaterBottle;
import knightminer.inspirations.tweaks.recipe.ArmorDyeingCauldronRecipe;
import knightminer.inspirations.tweaks.recipe.DyeCauldronWater;
import knightminer.inspirations.tweaks.recipe.FillCauldronFromDyedBottle;
import knightminer.inspirations.tweaks.recipe.FillCauldronFromPotion;
import knightminer.inspirations.tweaks.recipe.FillDyedBottleFromCauldron;
import knightminer.inspirations.tweaks.recipe.FillPotionFromCauldron;
import knightminer.inspirations.tweaks.recipe.PotionCauldronRecipe;
import knightminer.inspirations.tweaks.recipe.TippedArrowCauldronRecipe;
import knightminer.inspirations.tweaks.tileentity.TileCauldron;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

@Pulse(id = InspirationsTweaks.pulseID, description = "Various vanilla tweaks")
public class InspirationsTweaks extends PulseBase {
	public static final String pulseID = "InspirationsTweaks";

	@SidedProxy(clientSide = "knightminer.inspirations.tweaks.TweaksClientProxy", serverSide = "knightminer.inspirations.common.CommonProxy")
	public static CommonProxy proxy;

	// blocks
	public static Block carpet;
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

		if(Config.enableFittedCarpets) {
			carpet = register(r, new BlockFittedCarpet(), new ResourceLocation("carpet"));
		}
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
			dyedWaterBottle = registerItem(r, new ItemDyedWaterBottle(), "dyed_bottle");
		}

		if(carpet != null) {
			registerItemBlock(r, new ItemCloth(carpet));
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();

		// brew heartroots into regen potions
		if(Config.brewHeartbeet) {
			Ingredient heartbeet = Ingredient.fromStacks(InspirationsShared.heartbeet);
			PotionHelper.addMix(PotionTypes.WATER, heartbeet, PotionTypes.MUNDANE);
			PotionHelper.addMix(PotionTypes.AWKWARD, heartbeet, PotionTypes.REGENERATION);
		}

		InspirationsRegistry.registerAnvilBreaking(Material.GLASS);
		registerDispenserBehavior();
		registerCauldronRecipes();
	}

	private void registerCauldronRecipes() {
		if(!Config.enableExtendedCauldron) {
			return;
		}

		if(Config.enableCauldronDyeing) {
			InspirationsRegistry.addCauldronRecipe(FillDyedBottleFromCauldron.INSTANCE);
			InspirationsRegistry.addCauldronRecipe(FillCauldronFromDyedBottle.INSTANCE);
			InspirationsRegistry.addCauldronRecipe(DyeCauldronWater.INSTANCE);
			InspirationsRegistry.addCauldronRecipe(ArmorDyeingCauldronRecipe.INSTANCE);

			for(EnumDyeColor color : EnumDyeColor.values()) {
				InspirationsRegistry.addCauldronRecipe(new DyeCauldronRecipe(
						new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE),
						color,
						new ItemStack(Blocks.WOOL, 1, color.getMetadata())
						));

				InspirationsRegistry.addCauldronRecipe(new DyeCauldronRecipe(
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

			InspirationsRegistry.addCauldronRecipe(PotionCauldronRecipe.INSTANCE);
			InspirationsRegistry.addCauldronRecipe(TippedArrowCauldronRecipe.INSTANCE);
		}
	}

	private static void addPotionBottle(Item potion, ItemStack bottle) {
		InspirationsRegistry.addCauldronRecipe(new FillCauldronFromPotion(potion, bottle));
		InspirationsRegistry.addCauldronRecipe(new FillPotionFromCauldron(potion, bottle));
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
		MinecraftForge.EVENT_BUS.register(TweaksEvents.class);
	}

	private static final IBehaviorDispenseItem DEFAULT = new BehaviorDefaultDispenseItem();
	private void registerDispenserBehavior() {
		if(Config.dispensersPlaceAnvils) {
			registerDispenserBehavior(Blocks.ANVIL, (source, stack) -> {
				// get basic data
				EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);
				World world = source.getWorld();
				BlockPos pos = source.getBlockPos().offset(facing);

				// if we cannot place it, toss the item
				if(!Blocks.ANVIL.canPlaceBlockAt(world, pos)) {
					return DEFAULT.dispense(source, stack);
				}

				// just in case
				int meta = stack.getMetadata();
				if(meta > 3 || meta < 0) {
					meta = 3;
				}

				// determine the anvil to place
				EnumFacing anvilFacing = facing.getAxis().isVertical() ? EnumFacing.NORTH : facing.rotateY();
				IBlockState state = Blocks.ANVIL.getDefaultState()
						.withProperty(BlockAnvil.DAMAGE, meta)
						.withProperty(BlockAnvil.FACING, anvilFacing);

				world.setBlockState(pos, state);
				stack.shrink(1);
				return stack;
			});
		}

	}
}
