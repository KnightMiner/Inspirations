package knightminer.inspirations.tweaks;

import java.lang.reflect.Field;
import java.util.Iterator;

import com.google.common.eventbus.Subscribe;

import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.util.ReflectionUtil;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.tweaks.block.BlockBetterFlowerPot;
import knightminer.inspirations.tweaks.block.BlockCactusCrop;
import knightminer.inspirations.tweaks.block.BlockFittedCarpet;
import knightminer.inspirations.tweaks.block.BlockSugarCaneCrop;
import knightminer.inspirations.tweaks.item.ItemSeed;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsTweaks.pulseID, description = "Various vanilla tweaks")
public class InspirationsTweaks extends PulseBase {
	public static final String pulseID = "InspirationsTweaks";

	@SidedProxy(clientSide = "knightminer.inspirations.tweaks.TweaksClientProxy", serverSide = "knightminer.inspirations.common.CommonProxy")
	public static CommonProxy proxy;

	// blocks
	public static Block carpet;
	public static Block flowerPot;
	public static BlockCrops cactusCrop;
	public static BlockCrops sugarCaneCrop;

	// items
	public static Item potatoSeeds;
	public static Item carrotSeeds;
	public static Item sugarCaneSeeds;
	public static Item cactusSeeds;

	// potions
	public static PotionType haste;
	public static PotionType strongHaste;
	public static PotionType fatigue;
	public static PotionType strongFatigue;

	public static PotionType hunger;
	public static PotionType strongHunger;

	public static PotionType resistance;
	public static PotionType longResistance;
	public static PotionType levitation;
	public static PotionType longLevitation;

	public static PotionType blindness;
	public static PotionType longBlindness;
	public static PotionType decay;
	public static PotionType strongDecay;
	public static PotionType longHunger;

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
		if(Config.betterFlowerPot) {
			flowerPot = register(r, new BlockBetterFlowerPot(), new ResourceLocation("flower_pot"));
		}
		if(Config.enableMoreSeeds) {
			cactusCrop = register(r, new BlockCactusCrop(), "cactus_crop");
			sugarCaneCrop = register(r, new BlockSugarCaneCrop(), "sugar_cane_crop");
		}
	}

	@SubscribeEvent
	public void registerItem(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		if(Config.enableMoreSeeds) {
			cactusSeeds = registerItem(r, new ItemSeed(InspirationsTweaks.cactusCrop, EnumPlantType.Desert), "cactus_seeds");
			sugarCaneSeeds = registerItem(r, new ItemSeed(InspirationsTweaks.sugarCaneCrop, EnumPlantType.Beach), "sugar_cane_seeds");
			carrotSeeds = registerItem(r, new ItemSeeds(Blocks.CARROTS, Blocks.FARMLAND), "carrot_seeds");
			potatoSeeds = registerItem(r, new ItemSeeds(Blocks.POTATOES, Blocks.FARMLAND), "potato_seeds");
		}
	}

	@SubscribeEvent
	public void registerPotionTypes(Register<PotionType> event) {
		IForgeRegistry<PotionType> r = event.getRegistry();

		if(Config.brewMissingPotions) {
			haste = register(r, new PotionType(new PotionEffect(MobEffects.HASTE, 3600)), "haste");
			strongHaste = register(r, new PotionType("haste", new PotionEffect(MobEffects.HASTE, 1800, 1)), "strong_haste");
			fatigue = register(r, new PotionType(new PotionEffect(MobEffects.MINING_FATIGUE, 1200)), "fatigue");
			strongFatigue = register(r, new PotionType("fatigue", new PotionEffect(MobEffects.MINING_FATIGUE, 600, 1)), "strong_fatigue");

			hunger = register(r, new PotionType(new PotionEffect(MobEffects.HUNGER, 900)), "hunger");
			longHunger = register(r, new PotionType("hunger", new PotionEffect(MobEffects.HUNGER, 1800)), "long_hunger");
			strongHunger = register(r, new PotionType("hunger", new PotionEffect(MobEffects.HUNGER, 450, 1)), "strong_hunger");

			resistance = register(r, new PotionType(new PotionEffect(MobEffects.RESISTANCE, 2400)), "resistance");
			longResistance = register(r, new PotionType("resistance", new PotionEffect(MobEffects.RESISTANCE, 6000)), "long_resistance");

			levitation = register(r, new PotionType(new PotionEffect(MobEffects.LEVITATION, 160)), "levitation");
			longLevitation = register(r, new PotionType("levitation", new PotionEffect(MobEffects.LEVITATION, 300)), "long_levitation");

			blindness = register(r, new PotionType(new PotionEffect(MobEffects.BLINDNESS, 600)), "blindness");
			longBlindness = register(r, new PotionType("blindness", new PotionEffect(MobEffects.BLINDNESS, 1200)), "long_blindness");

			decay = register(r, new PotionType(new PotionEffect(MobEffects.WITHER, 600)), "decay");
			strongDecay = register(r, new PotionType("decay", new PotionEffect(MobEffects.WITHER, 300, 1)), "long_decay");
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();

		if(Config.addGrassDrops) {
			MinecraftForge.addGrassSeed(new ItemStack(InspirationsTweaks.carrotSeeds), 4);
			MinecraftForge.addGrassSeed(new ItemStack(InspirationsTweaks.potatoSeeds), 3);
		}

		// brew heartroots into regen potions
		if(Config.brewHeartbeet) {
			Ingredient heartbeet = Ingredient.fromStacks(InspirationsShared.heartbeet);
			PotionHelper.addMix(PotionTypes.WATER, heartbeet, PotionTypes.MUNDANE);
			PotionHelper.addMix(PotionTypes.AWKWARD, heartbeet, PotionTypes.REGENERATION);
		}
		if(Config.betterFlowerPot) {
			// add vanilla plants which are not met by the instanceof checks
			InspirationsRegistry.registerFlower(Blocks.CACTUS, 0, 15);
			InspirationsRegistry.registerFlower(Blocks.TALLGRASS, BlockTallGrass.EnumType.FERN.getMeta(), 4);
		}
		if(Config.brewMissingPotions) {
			// we need to start by removing a couple vanilla ones which we override
			Iterator<?> iterator = PotionHelper.POTION_TYPE_CONVERSIONS.iterator();
			while(iterator.hasNext()) {
				PotionType input = ReflectionUtil.getMixPredicateInput(iterator.next());
				if(input == PotionTypes.LEAPING || input == PotionTypes.LONG_LEAPING) {
					iterator.remove();
				}
			}

			// then add the new recipes
			Ingredient redstone = Ingredient.fromItem(Items.REDSTONE);
			Ingredient glowstone = Ingredient.fromItem(Items.GLOWSTONE_DUST);
			Ingredient spiderEye = Ingredient.fromItem(Items.FERMENTED_SPIDER_EYE);

			// haste
			Ingredient silverfishPowder = Ingredient.fromStacks(InspirationsShared.silverfishPowder);
			PotionHelper.addMix(PotionTypes.WATER, silverfishPowder, PotionTypes.MUNDANE);
			PotionHelper.addMix(PotionTypes.AWKWARD, silverfishPowder, haste);
			PotionHelper.addMix(haste, glowstone, strongHaste);

			// fatigue
			PotionHelper.addMix(haste, spiderEye, fatigue);
			PotionHelper.addMix(strongHaste, spiderEye, strongFatigue);
			PotionHelper.addMix(fatigue, glowstone, strongFatigue);

			// hunger
			PotionHelper.addMix(PotionTypes.REGENERATION, spiderEye, hunger);
			PotionHelper.addMix(PotionTypes.LONG_REGENERATION, spiderEye, longHunger);
			PotionHelper.addMix(PotionTypes.STRONG_REGENERATION, spiderEye, strongHunger);
			PotionHelper.addMix(hunger, redstone, longHunger);
			PotionHelper.addMix(hunger, glowstone, strongHunger);

			// resistance
			Ingredient skulkerShell = Ingredient.fromItem(Items.SHULKER_SHELL);
			PotionHelper.addMix(PotionTypes.WATER, skulkerShell, PotionTypes.MUNDANE);
			PotionHelper.addMix(PotionTypes.AWKWARD, skulkerShell, resistance);
			PotionHelper.addMix(resistance, redstone, longResistance);

			// levitation
			PotionHelper.addMix(PotionTypes.LEAPING, spiderEye, levitation);
			PotionHelper.addMix(PotionTypes.LONG_LEAPING, spiderEye, longLevitation);
			PotionHelper.addMix(levitation, redstone, longLevitation);

			// blindness
			Ingredient inkSac = Ingredient.fromStacks(new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()));
			PotionHelper.addMix(PotionTypes.WATER, inkSac, PotionTypes.MUNDANE);
			PotionHelper.addMix(PotionTypes.AWKWARD, inkSac, blindness);
			PotionHelper.addMix(blindness, redstone, longBlindness);

			// decay
			Ingredient witherBone = new OreIngredient("boneWithered");
			PotionHelper.addMix(PotionTypes.WATER, witherBone, PotionTypes.MUNDANE);
			PotionHelper.addMix(PotionTypes.AWKWARD, witherBone, decay);
			PotionHelper.addMix(decay, glowstone, strongDecay);
		}

		registerDispenserBehavior();
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
		MinecraftForge.EVENT_BUS.register(TweaksEvents.class);
	}

	private static final ResourceLocation SILVERFISH_TABLE = new ResourceLocation("entities/silverfish");
	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		if(Config.brewMissingPotions && SILVERFISH_TABLE.equals(event.getName())) {
			addToVanillaLoot(event, "entities/silverfish");
		}
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
