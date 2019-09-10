package knightminer.inspirations.tools;

import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.common.item.HidableItem;
import knightminer.inspirations.library.Util;
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
import knightminer.inspirations.tools.item.ItemEnchantableShield;
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
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;

import javax.annotation.Nonnull;

@Pulse(id = InspirationsTools.pulseID, description = "Adds various tools or tweaks to vanilla tools")
public class InspirationsTools extends PulseBase {
	public static final String pulseID = "InspirationsTools";

	@SuppressWarnings("Convert2MethodRef")
	public static Object proxy = DistExecutor.callWhenOn(Dist.CLIENT, ()->()->new ToolsClientProxy());

	// items
	public static Item lock;
	public static Item key;
	public static Item redstoneCharger;
	public static Item northCompass;
	public static Item barometer;
	public static Item photometer;
	public static ArrowItem redstoneArrow;

	// The "undyed" compass is White.
	public static ItemWaypointCompass[] waypointCompasses = new ItemWaypointCompass[16];

	// blocks
	public static Block redstoneCharge;

	public static EntityType<RedstoneArrow> entRSArrow = buildEntity(EntityType.Builder
		.<RedstoneArrow>create(RedstoneArrow::new, EntityClassification.MISC)
		.size(0.5F, 0.5F)
		.setTrackingRange(4)
		.setUpdateInterval(20)
		.setCustomClientFactory((packet, world) -> new RedstoneArrow(InspirationsTools.entRSArrow, world))
	,"redstone_arrow");

	@SubscribeEvent
	public void setup(FMLCommonSetupEvent event) {
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

		lock = registerItem(r, new HidableItem(
			new Item.Properties().group(ItemGroup.MATERIALS),
			Config.enableLock::get
		), "lock");
		key = registerItem(r, new HidableItem(
			new Item.Properties().group(ItemGroup.MATERIALS),
			Config.enableLock::get
		),  "key");

		northCompass = registerItem(r, new HidableItem(toolProps, Config.enableNorthCompass::get), "north_compass");
		northCompass.addPropertyOverride(Util.getResource("angle"), new NorthCompassGetter());

//		if(Config.renameVanillaCompass.get()) {
//				Items.COMPASS.translationKey = Util.prefix("origin_compass");
//		}
		barometer = registerItem(r, new HidableItem(toolProps, Config.enableBarometer::get), "barometer");
		barometer.addPropertyOverride(Util.getResource("height"), new BarometerGetter());

		photometer = registerItem(r, new HidableItem(toolProps, Config.enablePhotometer::get), "photometer");
		photometer.addPropertyOverride(Util.getResource("light"), new PhotometerGetter());


		// White is the undyed version, so it's available without Config.dyeWaypointCompass() and has no color
		// in the name.
		waypointCompasses[DyeColor.WHITE.getId()] = registerItem(r,
				new ItemWaypointCompass(0xDDDDDD, 0xFFC100, Config.enableWaypointCompass::get
		), "waypoint_compass");
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

		if(Config.shieldEnchantmentTable()) {
			register(r, new ItemEnchantableShield(new Item.Properties()
					.maxDamage(Items.SHIELD.getMaxDamage())
					.group(ItemGroup.COMBAT)),
					Items.SHIELD.getRegistryName()
			);
		}
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

		if(Config.moreShieldEnchantments.get()) {
			EquipmentSlotType[] slots = new EquipmentSlotType[]{
					EquipmentSlotType.HEAD,
					EquipmentSlotType.CHEST,
					EquipmentSlotType.LEGS,
					EquipmentSlotType.FEET
			};
			for(ProtectionEnchantment ench: new ProtectionEnchantment[] {
					(ProtectionEnchantment) Enchantments.PROTECTION,
					(ProtectionEnchantment) Enchantments.FIRE_PROTECTION,
					(ProtectionEnchantment) Enchantments.PROJECTILE_PROTECTION,
					(ProtectionEnchantment) Enchantments.BLAST_PROTECTION
			}) {
				register(r, new EnchantmentShieldProtection(ench.getRarity(), ench.protectionType, slots), ench.getRegistryName());
			}
			register(r, new EnchantmentShieldThorns(Enchantments.THORNS.getRarity(), slots), Enchantments.THORNS.getRegistryName());
		}

		if(Config.moreShieldEnchantments.get() || Config.axeWeaponEnchants.get()) {
			EquipmentSlotType[] slots = new EquipmentSlotType[] {EquipmentSlotType.MAINHAND};
			register(r, new EnchantmentExtendedKnockback(Enchantment.Rarity.UNCOMMON, slots), new ResourceLocation("knockback"));
			register(r, new EnchantmentExtendedFire(Enchantment.Rarity.RARE, slots), new ResourceLocation("fire_aspect"));
			if(Config.axeWeaponEnchants.get()) {
				register(r, new EnchantmentAxeLooting(Enchantment.Rarity.RARE, EnchantmentType.WEAPON, slots), new ResourceLocation("looting"));
			}
		}

		if(Config.axeEnchantmentTable.get()) {
			EquipmentSlotType[] slots = new EquipmentSlotType[] {EquipmentSlotType.MAINHAND};
			register(r, new EnchantmentAxeDamage(Enchantment.Rarity.COMMON, 0, slots), new ResourceLocation("sharpness"));
			register(r, new EnchantmentAxeDamage(Enchantment.Rarity.UNCOMMON, 1, slots), new ResourceLocation("smite"));
			register(r, new EnchantmentAxeDamage(Enchantment.Rarity.UNCOMMON, 2, slots), new ResourceLocation("bane_of_arthropods"));
		}
	}

	private void registerDispenserBehavior() {
		DispenserBlock.registerDispenseBehavior(redstoneArrow, new ProjectileDispenseBehavior() {
			@Nonnull
			@Override
			protected IProjectile getProjectileEntity(@Nonnull World world, @Nonnull IPosition position, @Nonnull ItemStack stack) {
				RedstoneArrow arrow = new RedstoneArrow(world, position.getX(), position.getY(), position.getZ());
				arrow.pickupStatus = ArrowEntity.PickupStatus.ALLOWED;
				return arrow;
			}
		});
		DispenserBlock.registerDispenseBehavior(redstoneCharger, new OptionalDispenseBehavior() {
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
