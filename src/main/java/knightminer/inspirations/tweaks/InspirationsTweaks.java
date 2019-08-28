package knightminer.inspirations.tweaks;

import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.common.item.HidableBlockItem;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.tweaks.block.BlockCactusCrop;
import knightminer.inspirations.tweaks.block.BlockFittedCarpet;
import knightminer.inspirations.tweaks.block.BlockFlatCarpet;
import knightminer.inspirations.tweaks.block.BlockSugarCaneCrop;
import knightminer.inspirations.tweaks.item.ItemSeed;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potions;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsTweaks.pulseID, description = "Various vanilla tweaks")
public class InspirationsTweaks extends PulseBase {
	public static final String pulseID = "InspirationsTweaks";

	@SuppressWarnings("Convert2MethodRef")
	public static Object proxy = DistExecutor.callWhenOn(Dist.CLIENT, ()->()->new TweaksClientProxy());

	// blocks
	public static BlockFittedCarpet[] fitCarpets = new BlockFittedCarpet[16];
	public static BlockFlatCarpet[] flatCarpets = new BlockFlatCarpet[16];
	public static CropsBlock cactusCrop;
	public static CropsBlock sugarCaneCrop;

	// items
	public static Item potatoSeeds;
	public static Item carrotSeeds;
	public static Item sugarCaneSeeds;
	public static Item cactusSeeds;


	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		registerCarpet(r, DyeColor.WHITE, Blocks.WHITE_CARPET);
		registerCarpet(r, DyeColor.ORANGE, Blocks.ORANGE_CARPET);
		registerCarpet(r, DyeColor.MAGENTA, Blocks.MAGENTA_CARPET);
		registerCarpet(r, DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_CARPET);
		registerCarpet(r, DyeColor.YELLOW, Blocks.YELLOW_CARPET);
		registerCarpet(r, DyeColor.LIME, Blocks.LIME_CARPET);
		registerCarpet(r, DyeColor.PINK, Blocks.PINK_CARPET);
		registerCarpet(r, DyeColor.GRAY, Blocks.GRAY_CARPET);
		registerCarpet(r, DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_CARPET);
		registerCarpet(r, DyeColor.CYAN, Blocks.CYAN_CARPET);
		registerCarpet(r, DyeColor.PURPLE, Blocks.PURPLE_CARPET);
		registerCarpet(r, DyeColor.BLUE, Blocks.BLUE_CARPET);
		registerCarpet(r, DyeColor.BROWN, Blocks.BROWN_CARPET);
		registerCarpet(r, DyeColor.GREEN, Blocks.GREEN_CARPET);
		registerCarpet(r, DyeColor.RED, Blocks.RED_CARPET);
		registerCarpet(r, DyeColor.BLACK, Blocks.BLACK_CARPET);


		cactusCrop = register(r, new BlockCactusCrop(), "cactus_crop");
		sugarCaneCrop = register(r, new BlockSugarCaneCrop(), "sugar_cane_crop");
	}

	private void registerCarpet(IForgeRegistry<Block> r, DyeColor color, Block origCarpet) {
		// The flat version overrides vanilla (with no blockstate values).
		// The fitted version goes in our mod namespace.

		BlockFlatCarpet flatCarpet = flatCarpets[color.getId()] = new BlockFlatCarpet(color, origCarpet);
		BlockFittedCarpet fitCarpet = fitCarpets[color.getId()] = new BlockFittedCarpet(color, origCarpet);
		register(r, flatCarpet, origCarpet.getRegistryName());
		register(r, fitCarpet, color.getName() + "_fitted_carpet");
	}

	@SubscribeEvent
	public void registerItem(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		for (BlockFlatCarpet carpet : flatCarpets) {
			BlockItem item = register(r, new BlockItem(carpet, new Item.Properties().group(ItemGroup.DECORATIONS)), carpet.getRegistryName());
			Item.BLOCK_TO_ITEM.put(carpet, item);
			Item.BLOCK_TO_ITEM.put(fitCarpets[carpet.getColor().getId()], item);
		}

		cactusSeeds = registerItem(r, new HidableBlockItem(
				InspirationsTweaks.cactusCrop,
				new Item.Properties().group(ItemGroup.FOOD)
		), "cactus_seeds");

		sugarCaneSeeds = registerItem(r, new HidableBlockItem(
				InspirationsTweaks.sugarCaneCrop,
				new Item.Properties().group(ItemGroup.FOOD)
		), "sugar_cane_seeds");

		carrotSeeds = registerItem(r, new ItemSeed((CropsBlock) Blocks.CARROTS, PlantType.Crop), "carrot_seeds");
		potatoSeeds = registerItem(r, new ItemSeed((CropsBlock) Blocks.POTATOES, PlantType.Crop), "potato_seeds");
	}

	@SubscribeEvent
	public void setup(FMLCommonSetupEvent event) {
		// TODO: Forge needs to implement the ability to inject these into the loot tables.
		// See MinecraftForge#
		//if(Config.addGrassDrops()) {
		//	MinecraftForge.addGrassSeed(new ItemStack(InspirationsTweaks.carrotSeeds), 4);
		//	MinecraftForge.addGrassSeed(new ItemStack(InspirationsTweaks.potatoSeeds), 3);
		//}

		// brew heartbeets into regen potions
		Ingredient heartbeet = Ingredient.fromItems(InspirationsShared.heartbeet);
		BrewingRecipeRegistry.addRecipe(
				new NormalBrewingRecipe(Potions.WATER, heartbeet, Potions.MUNDANE, Config::brewHeartbeet)
		);
		BrewingRecipeRegistry.addRecipe(
				new NormalBrewingRecipe(Potions.AWKWARD, heartbeet, Potions.REGENERATION, Config::brewHeartbeet
				));

		registerDispenserBehavior();

		MinecraftForge.EVENT_BUS.register(TweaksEvents.class);
	}

	private static final IDispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();

	private void registerDispenserBehavior() {
		IDispenseItemBehavior behavior = (source, stack) -> {
			if (!Config.dispensersPlaceAnvils.get()) {
				DEFAULT.dispense(source, stack);
			}
			// get basic data
			Direction facing = source.getBlockState().get(DispenserBlock.FACING);
			World world = source.getWorld();
			BlockPos pos = source.getBlockPos().offset(facing);

			DirectionalPlaceContext context = new DirectionalPlaceContext(world, pos, facing, stack, facing.getOpposite());

			if (((BlockItem) stack.getItem()).tryPlace(context) == ActionResultType.SUCCESS) {
				return stack;
			} else {
				// if we cannot place it, toss the item
				return DEFAULT.dispense(source, stack);
			}
		};

		DispenserBlock.registerDispenseBehavior(Blocks.ANVIL, behavior);
		DispenserBlock.registerDispenseBehavior(Blocks.CHIPPED_ANVIL, behavior);
		DispenserBlock.registerDispenseBehavior(Blocks.DAMAGED_ANVIL, behavior);

	}
}
