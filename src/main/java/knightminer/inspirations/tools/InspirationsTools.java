package knightminer.inspirations.tools;

import com.google.common.eventbus.Subscribe;
import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.common.item.HidableItem;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.tools.block.BlockRedstoneCharge;
import knightminer.inspirations.tools.client.BarometerGetter;
import knightminer.inspirations.tools.client.NorthCompassGetter;
import knightminer.inspirations.tools.client.PhotometerGetter;
import knightminer.inspirations.tools.enchantment.EnchantmentAxeDamage;
import knightminer.inspirations.tools.enchantment.EnchantmentAxeLooting;
import knightminer.inspirations.tools.enchantment.EnchantmentExtendedFire;
import knightminer.inspirations.tools.enchantment.EnchantmentExtendedKnockback;
import knightminer.inspirations.tools.enchantment.EnchantmentShieldProtection;
import knightminer.inspirations.tools.enchantment.EnchantmentShieldThorns;
import knightminer.inspirations.tools.entity.RedstoneArrow;
import knightminer.inspirations.tools.item.ItemCrook;
import knightminer.inspirations.tools.item.ItemRedstoneCharger;
import knightminer.inspirations.tools.item.ItemWaypointCompass;
import knightminer.inspirations.tools.item.RedstoneArrowItem;
import knightminer.inspirations.tools.recipe.WaypointCompassCopyRecipe;
import knightminer.inspirations.tools.recipe.WaypointCompassDyeingRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

@Pulse(id = InspirationsTools.pulseID, description = "Adds various tools or tweaks to vanilla tools")
public class InspirationsTools extends PulseBase {
	public static final String pulseID = "InspirationsTools";

	public static CommonProxy proxy = DistExecutor.runForDist(
		()->()->new ToolsClientProxy(),
		()->()->new CommonProxy()
	);

	// items
	public static Item redstoneCharger;
	public static Item woodenCrook;
	public static Item stoneCrook;
	public static Item boneCrook;
	public static Item blazeCrook;
	public static Item witherCrook;
	public static Item northCompass;
	public static Item barometer;
	public static Item photometer;
	public static ArrowItem redstoneArrow;

	// The "undyed" compass is White.
	public static ItemWaypointCompass[] waypointCompasses = new ItemWaypointCompass[16];

	// tool materials
	public static IItemTier bone = new IItemTier() {
		public int getMaxUses() { return 225; }
		public float getEfficiency() { return 4.0F; }
		public float getAttackDamage() { return 1.5F; }
		public int getHarvestLevel() { return 1; }
		public int getEnchantability() { return 10; }
		public Ingredient getRepairMaterial() {
			return Ingredient.fromTag(ItemTags.getCollection()
				.getOrCreate(new ResourceLocation("forge", "bones")));
		}
	};
	public static IItemTier blaze = new IItemTier() {
		public int getMaxUses() { return 300; }
		public float getEfficiency() { return 6.0F; }
		public float getAttackDamage() { return 2.0F; }
		public int getHarvestLevel() { return 2; }
		public int getEnchantability() { return 20; }
		public Ingredient getRepairMaterial() {
			return Ingredient.fromTag(ItemTags.getCollection()
				.getOrCreate(new ResourceLocation("forge", "rods/blaze")));
		}
	};
	public static IItemTier wither = new IItemTier() {
		public int getMaxUses() { return 375; }
		public float getEfficiency() { return 6.0F; }
		public float getAttackDamage() { return 1.5F; }
		public int getHarvestLevel() { return 2; }
		public int getEnchantability() { return 10; }
		public Ingredient getRepairMaterial() {
			return Ingredient.fromTag(ItemTags.getCollection()
				.getOrCreate(new ResourceLocation("forge", "bones/wither")));
		}
	};

	// blocks
	public static Block redstoneCharge;

	// EntityType.Builder.<ArrowEntity>create(ArrowEntity::new, EntityClassification.MISC).size(0.5F, 0.5F));
    // EntityType.Builder.<SpectralArrowEntity>create(SpectralArrowEntity::new, EntityClassification.MISC).size(0.5F, 0.5F));
	// Entities
	public static EntityType<RedstoneArrow> entRSArrow = buildEntity(EntityType.Builder
		.<RedstoneArrow>create(RedstoneArrow::new, EntityClassification.MISC)
		.size(0.5F, 0.5F)
		.setTrackingRange(4)
		.setUpdateInterval(20)
	,"redstone_arrow");

	@SubscribeEvent
	public void setup(FMLCommonSetupEvent event) {
		proxy.preInit();
		MinecraftForge.EVENT_BUS.register(ToolsEvents.class);
		registerDispenserBehavior();
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		redstoneCharge = registerBlock(r, new BlockRedstoneCharge(), "redstone_charge");
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		// Reuse...
		Item.Properties toolProps = new Item.Properties().group(ItemGroup.TOOLS);

		redstoneArrow = registerItem(r, new RedstoneArrowItem(toolProps), "charged_arrow");

		redstoneCharger = registerItem(r, new ItemRedstoneCharger(), "redstone_charger");

		woodenCrook = registerItem(r, new ItemCrook(ItemTier.WOOD, Config::separateCrook), "wooden_crook");
		stoneCrook = registerItem(r, new ItemCrook(ItemTier.STONE, Config::separateCrook), "stone_crook");
		boneCrook = registerItem(r, new ItemCrook(bone, Config::separateCrook), "bone_crook");

		blazeCrook = registerItem(r, new ItemCrook(blaze, Config::enableNetherCrook), "blaze_crook");
		witherCrook = registerItem(r, new ItemCrook(wither, Config::enableNetherCrook), "wither_crook");

		northCompass = registerItem(r, new HidableItem(toolProps, Config.enableNorthCompass::get), "north_compass");
		northCompass.addPropertyOverride(Util.getResource("angle"), new NorthCompassGetter());

//		if(Config.renameVanillaCompass.get()) {
//				Items.COMPASS.translationKey = Util.prefix("origin_compass");
//		}
		barometer = registerItem(r, new HidableItem(toolProps, Config.enableBarometer::get), "barometer");
		barometer.addPropertyOverride(Util.getResource("height"), new BarometerGetter());

		photometer = registerItem(r, new HidableItem(toolProps, Config.enablePhotometer::get), "photometer");
		photometer.addPropertyOverride(Util.getResource("light"), new PhotometerGetter());


		waypointCompasses[DyeColor.WHITE.getId()] = registerItem(r,
				new ItemWaypointCompass(0xDDDDDD, 0xFFC100), "waypoint_compass");
		waypointCompasses[DyeColor.BLACK.getId()] = registerItem(r,
				new ItemWaypointCompass(0x444444, DyeColor.RED.colorValue), "black_waypoint_compass");

		registerWaypointCompass(r, DyeColor.LIGHT_GRAY, DyeColor.WHITE.colorValue);
		registerWaypointCompass(r, DyeColor.GRAY,       DyeColor.LIGHT_GRAY.colorValue);
		registerWaypointCompass(r, DyeColor.RED,        DyeColor.ORANGE.colorValue);
		registerWaypointCompass(r, DyeColor.ORANGE,     DyeColor.YELLOW.colorValue);
		registerWaypointCompass(r, DyeColor.YELLOW,     0xDBA213);
		registerWaypointCompass(r, DyeColor.LIME,       DyeColor.BROWN.colorValue);
		registerWaypointCompass(r, DyeColor.GREEN,      DyeColor.LIME.colorValue);
		registerWaypointCompass(r, DyeColor.CYAN,       DyeColor.LIGHT_BLUE.colorValue);
		registerWaypointCompass(r, DyeColor.LIGHT_BLUE, 0x77A9FF);
		registerWaypointCompass(r, DyeColor.BLUE,       0x7E54FF);
		registerWaypointCompass(r, DyeColor.PURPLE,     DyeColor.MAGENTA.colorValue);
		registerWaypointCompass(r, DyeColor.MAGENTA,    DyeColor.PINK.colorValue);
		registerWaypointCompass(r, DyeColor.PINK,       0xF2BFCE);
		registerWaypointCompass(r, DyeColor.BROWN,      0xA59072);

//		if(Config.shieldEnchantmentTable) {
//			register(r, new ItemEnchantableShield(), new ResourceLocation("shield"));
//		}
	}

	private void registerWaypointCompass(IForgeRegistry<Item> r, DyeColor body, int needle) {
		waypointCompasses[body.getId()] = registerItem(r,
				new ItemWaypointCompass(body.colorValue, needle),
				body.getTranslationKey() + "_waypoint_compass"
		);
	}

	@SubscribeEvent
	public void registerEntities(Register<EntityType<?>> event) {
		IForgeRegistry<EntityType<?>> r = event.getRegistry();
		r.register(entRSArrow);
	}

	@SubscribeEvent
	public void registerRecipes(Register<IRecipeSerializer<?>> event) {
		IForgeRegistry<IRecipeSerializer<?>> r = event.getRegistry();
		register(r, WaypointCompassCopyRecipe.SERIALIZER, "copy_waypoint_compass");
		register(r, WaypointCompassDyeingRecipe.SERIALIZER, "dye_waypoint_compass");
	}

	@SubscribeEvent
	public void registerEnchantments(Register<Enchantment> event) {
		IForgeRegistry<Enchantment> r = event.getRegistry();

//		if(Config.moreShieldEnchantments) {
//			EntityEquipmentSlot[] slots = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
//			register(r, new EnchantmentShieldProtection(Enchantment.Rarity.COMMON, EnchantmentProtection.Type.ALL, slots), new ResourceLocation("protection"));
//			register(r, new EnchantmentShieldProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.Type.FIRE, slots), new ResourceLocation("fire_protection"));
//			register(r, new EnchantmentShieldProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.Type.PROJECTILE, slots), new ResourceLocation("projectile_protection"));
//			register(r, new EnchantmentShieldProtection(Enchantment.Rarity.RARE, EnchantmentProtection.Type.EXPLOSION, slots), new ResourceLocation("blast_protection"));
//			register(r, new EnchantmentShieldThorns(Enchantment.Rarity.VERY_RARE, slots), new ResourceLocation("thorns"));
//		}
//
//		if(Config.moreShieldEnchantments || Config.axeWeaponEnchants) {
//			EntityEquipmentSlot[] slots = new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND};
//			register(r, new EnchantmentExtendedKnockback(Enchantment.Rarity.UNCOMMON, slots), new ResourceLocation("knockback"));
//			register(r, new EnchantmentExtendedFire(Enchantment.Rarity.RARE, slots), new ResourceLocation("fire_aspect"));
//			if(Config.axeWeaponEnchants) {
//				register(r, new EnchantmentAxeLooting(Enchantment.Rarity.RARE, EnumEnchantmentType.WEAPON, slots), new ResourceLocation("looting"));
//			}
//		}
//
//		if(Config.axeEnchantmentTable) {
//			EntityEquipmentSlot[] slots = new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND};
//			register(r, new EnchantmentAxeDamage(Enchantment.Rarity.COMMON, 0, slots), new ResourceLocation("sharpness"));
//			register(r, new EnchantmentAxeDamage(Enchantment.Rarity.UNCOMMON, 1, slots), new ResourceLocation("smite"));
//			register(r, new EnchantmentAxeDamage(Enchantment.Rarity.UNCOMMON, 2, slots), new ResourceLocation("bane_of_arthropods"));
//		}
	}

	@SubscribeEvent
	public void init(FMLCommonSetupEvent event) {
		proxy.init();
	}

	@SubscribeEvent
	public void postInit(FMLCommonSetupEvent event) {
		proxy.postInit();
	}

	private void registerDispenserBehavior() {
		registerDispenserBehavior(redstoneArrow, new ProjectileDispenseBehavior() {
			@Nonnull
			@Override
			protected IProjectile getProjectileEntity(@Nonnull World world, @Nonnull IPosition position, @Nonnull ItemStack stack) {
				RedstoneArrow arrow = new RedstoneArrow(world, position.getX(), position.getY(), position.getZ());
				arrow.pickupStatus = ArrowEntity.PickupStatus.ALLOWED;
				return arrow;
			}
		});
		registerDispenserBehavior(redstoneCharger, new OptionalDispenseBehavior() {
			@Nonnull
			@Override
			protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
				this.successful = true;
				World world = source.getWorld();
				Direction facing = source.getBlockState().get(DispenserBlock.FACING);
				BlockPos pos = source.getBlockPos().offset(facing);

				if (world.getBlockState(pos).isReplaceable(new DirectionalPlaceContext(
					world, pos, facing, ItemStack.EMPTY, facing
				))) {
					world.setBlockState(pos, redstoneCharge.getDefaultState().with(BlockRedstoneCharge.FACING, facing));
					if (stack.attemptDamageItem(1, world.rand, null)) {
						stack.setCount(0);
					}
				} else {
					this.successful = false;
				}

				return stack;
			}
		});
	}
}
