package knightminer.inspirations.tweaks;

import com.google.common.collect.ImmutableSet;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.common.item.HidableItem;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.tweaks.block.BlockCropBlock;
import knightminer.inspirations.tweaks.block.CactusCropBlock;
import knightminer.inspirations.tweaks.block.DryHopperBlock;
import knightminer.inspirations.tweaks.block.FittedCarpetBlock;
import knightminer.inspirations.tweaks.block.FlatCarpetBlock;
import knightminer.inspirations.tweaks.block.SugarCaneCropBlock;
import knightminer.inspirations.tweaks.block.WetHopperBlock;
import knightminer.inspirations.tweaks.datagen.TweaksRecipeProvider;
import knightminer.inspirations.tweaks.item.SeedItem;
import knightminer.inspirations.tweaks.recipe.NormalBrewingRecipe;
import knightminer.inspirations.tweaks.util.SmoothGrowthListener;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsTweaks.pulseID, description = "Various vanilla tweaks")
public class InspirationsTweaks extends PulseBase {
	public static final String pulseID = "InspirationsTweaks";

	public static Object proxy = DistExecutor.callWhenOn(Dist.CLIENT, ()->()->new TweaksClientProxy());

	// blocks
	public static FittedCarpetBlock[] fitCarpets = new FittedCarpetBlock[16];
	public static FlatCarpetBlock[] flatCarpets = new FlatCarpetBlock[16];
	public static BlockCropBlock cactus;
	public static BlockCropBlock sugarCane;
	public static HopperBlock wetHopper;
	public static HopperBlock dryHopper;

	// items
	//public static Item potatoSeeds;
	//public static Item carrotSeeds;
	public static Item sugarCaneSeeds;
	public static Item cactusSeeds;
	//public static Item silverfishPowder;
	public static Item heartbeet;


	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		if (Config.enableFittedCarpets.get()) {
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
		}

		if (Config.waterlogHopper.get()) {
			dryHopper = register(r, new DryHopperBlock(Block.Properties.from(Blocks.HOPPER)), Blocks.HOPPER.getRegistryName());
			wetHopper = register(r, new WetHopperBlock(Block.Properties.from(Blocks.HOPPER)), Util.getResource("wet_hopper"));
		}

		cactus = register(r, new CactusCropBlock(), "cactus");
		sugarCane = register(r, new SugarCaneCropBlock(), "sugar_cane");
	}

	private void registerCarpet(IForgeRegistry<Block> r, DyeColor color, Block origCarpet) {
		// The flat version overrides vanilla (with no blockstate values).
		// The fitted version goes in our mod namespace.

		FlatCarpetBlock flatCarpet = flatCarpets[color.getId()] = new FlatCarpetBlock(color, origCarpet);
		FittedCarpetBlock fitCarpet = fitCarpets[color.getId()] = new FittedCarpetBlock(color, origCarpet);
		register(r, flatCarpet, origCarpet.getRegistryName());
		register(r, fitCarpet, color.getName() + "_fitted_carpet");
	}

	@SubscribeEvent
	public void registerItem(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		if (Config.enableFittedCarpets.get()) {
			for(FlatCarpetBlock carpet : flatCarpets) {
				BlockItem item = register(r, new BlockItem(carpet, new Item.Properties().group(ItemGroup.DECORATIONS)), carpet.getRegistryName());
				Item.BLOCK_TO_ITEM.put(carpet, item);
				Item.BLOCK_TO_ITEM.put(fitCarpets[carpet.getColor().getId()], item);
			}
		}

		if (Config.waterlogHopper.get()) {
			register(r, new BlockItem(dryHopper, new Item.Properties().group(ItemGroup.REDSTONE)), Items.HOPPER.getRegistryName());
		}

		Item.Properties props = new Item.Properties().group(ItemGroup.FOOD);
		cactusSeeds = registerItem(r, new SeedItem(cactus, props), "cactus_seeds");
		sugarCaneSeeds = registerItem(r, new SeedItem(sugarCane, props), "sugar_cane_seeds");

		/*
		carrotSeeds = registerItem(r, new SeedItem((CropsBlock) Blocks.CARROTS, PlantType.Crop), "carrot_seeds");
		potatoSeeds = registerItem(r, new SeedItem((CropsBlock) Blocks.POTATOES, PlantType.Crop), "potato_seeds");
		*/

		heartbeet = registerItem(r, new HidableItem(
				new Item.Properties().group(ItemGroup.FOOD).food(
				new Food.Builder().hunger(2).saturation(2.4f).effect(new EffectInstance(Effects.REGENERATION, 100), 1).build()),
				Config.enableHeartbeet::get
		), "heartbeet");
//		silverfishPowder = registerItem(r, new HidableItem(
//				new Item.Properties().group(ItemGroup.BREWING),
//				() -> false // TODO: Make this have a purpose...
//		),  "silverfish_powder");
	}

	@SubscribeEvent
	public void registerTileEntities(Register<TileEntityType<?>> event) {
		if (Config.waterlogHopper.get()) {
			// We need to inject our replacement hopper blocks into the valid ones for the TE type.
			// It's an immutable set, so we need to replace it entirely.
			synchronized(TileEntityType.HOPPER) {
				TileEntityType.HOPPER.validBlocks = new ImmutableSet.Builder<Block>()
						.addAll(TileEntityType.HOPPER.validBlocks)
						.add(dryHopper)
						.add(wetHopper)
						.build();
			}
		}
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
		Ingredient heartbeet = Ingredient.fromItems(InspirationsTweaks.heartbeet);
		BrewingRecipeRegistry.addRecipe(
				new NormalBrewingRecipe(Potions.WATER, heartbeet, Potions.MUNDANE, Config::brewHeartbeet)
		);
		BrewingRecipeRegistry.addRecipe(
				new NormalBrewingRecipe(Potions.AWKWARD, heartbeet, Potions.REGENERATION, Config::brewHeartbeet
				));

		registerCompostables();
		registerDispenserBehavior();

		MinecraftForge.EVENT_BUS.register(TweaksEvents.class);
		MinecraftForge.EVENT_BUS.addListener(new SmoothGrowthListener(Blocks.CACTUS, cactus, false));
		MinecraftForge.EVENT_BUS.addListener(new SmoothGrowthListener(Blocks.SUGAR_CANE, sugarCane, true));
	}

	@SubscribeEvent
	public void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		if (event.includeServer()) {
			gen.addProvider(new TweaksRecipeProvider(gen));
		}
	}


	@SubscribeEvent
	public static void loadLoot(LootTableLoadEvent event) {
		addToVanillaLoot(event, "entities/cave_spider");
		addToVanillaLoot(event, "entities/skeleton");
	}

	private static final IDispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();

	private void registerCompostables() {
		ComposterBlock.registerCompostable(0.3F, cactusSeeds);
		ComposterBlock.registerCompostable(0.3F, sugarCaneSeeds);
		ComposterBlock.registerCompostable(0.8F, heartbeet);
	}

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
