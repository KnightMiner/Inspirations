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

@Pulse(id = InspirationsTools.pulseID, description = "Adds various tools or tweaks to vanilla tools")
public class InspirationsTools extends PulseBase {
	public static final String pulseID = "InspirationsTools";

	@SidedProxy(clientSide = "knightminer.inspirations.tools.ToolsClientProxy", serverSide = "knightminer.inspirations.common.CommonProxy")
	public static CommonProxy proxy;

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
	public static Item waypointCompass;
	public static ArrowItem redstoneArrow;

	// tool materials
	public static ToolMaterial bone;
	public static ToolMaterial blaze;
	public static ToolMaterial wither;

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

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();

		if(Config.separateCrook) {
			bone = EnumHelper.addToolMaterial(Util.prefix("bone"), 1, 225, 4.0F, 1.5F, 10);
			if(Config.netherCrooks) {
				blaze = EnumHelper.addToolMaterial(Util.prefix("blaze"), 2, 300, 6.0F, 2.0F, 20);
				wither = EnumHelper.addToolMaterial(Util.prefix("wither"), 2, 375, 6.0F, 1.5F, 10);
			}
		}
		MinecraftForge.EVENT_BUS.register(ToolsEvents.class);
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		redstoneCharge = registerBlock(r, new BlockRedstoneCharge(), "redstone_charge");
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		arrow = registerItem(r, new ItemModArrow(), "arrow");
		if(Config.enableRedstoneCharge) {
			redstoneCharger = registerItem(r, new ItemRedstoneCharger(), "redstone_charger");
		}
		if(Config.separateCrook) {
			woodenCrook = registerItem(r, new ItemCrook(ToolMaterial.WOOD), "wooden_crook");
			stoneCrook = registerItem(r, new ItemCrook(ToolMaterial.STONE), "stone_crook");
			boneCrook = registerItem(r, new ItemCrook(bone), "bone_crook");
			if(Config.netherCrooks) {
				blazeCrook = registerItem(r, new ItemCrook(blaze), "blaze_crook");
				witherCrook = registerItem(r, new ItemCrook(wither), "wither_crook");
			}
		}

		if(Config.enableNorthCompass) {
			northCompass = registerItem(r, new Item().setCreativeTab(CreativeTabs.TOOLS), "north_compass");
			northCompass.addPropertyOverride(new ResourceLocation("angle"), new NorthCompassGetter());
			if(Config.renameVanillaCompass) {
				Items.COMPASS.setUnlocalizedName(Util.prefix("origin_compass"));
			}
		}

		if(Config.enableBarometer) {
			barometer = registerItem(r, new Item().setCreativeTab(CreativeTabs.TOOLS), "barometer");
			barometer.addPropertyOverride(new ResourceLocation("height"), new BarometerGetter());
		}

		if(Config.enablePhotometer) {
			photometer = registerItem(r, new Item().setCreativeTab(CreativeTabs.TOOLS), "photometer");
			photometer.addPropertyOverride(new ResourceLocation("light"), new PhotometerGetter());
		}

		if(Config.enableWaypointCompass) {
			waypointCompass = registerItem(r, new ItemWaypointCompass(), "waypoint_compass");
		}

		if(Config.shieldEnchantmentTable) {
			register(r, new ItemEnchantableShield(), new ResourceLocation("shield"));
		}
	}

	@SubscribeEvent
	public void registerEntities(Register<EntityEntry> event) {
		IForgeRegistry<EntityEntry> r = event.getRegistry();
		r.register(getEntityBuilder(EntityModArrow.class, "arrow", EntityIds.ARROW)
				.tracker(64, 1, false)
				.build());
	}

	@SubscribeEvent
	public void registerRecipes(Register<IRecipe> event) {
		IForgeRegistry<IRecipe> r = event.getRegistry();
		if(Config.copyWaypointCompass) {
			register(r, new WaypointCompassCopyRecipe(), "waypoint_compass_copy");
		}
	}

	@SubscribeEvent
	public void registerEnchantments(Register<Enchantment> event) {
		IForgeRegistry<Enchantment> r = event.getRegistry();

		if(Config.moreShieldEnchantments) {
			EntityEquipmentSlot[] slots = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
			register(r, new EnchantmentShieldProtection(Enchantment.Rarity.COMMON, EnchantmentProtection.Type.ALL, slots), new ResourceLocation("protection"));
			register(r, new EnchantmentShieldProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.Type.FIRE, slots), new ResourceLocation("fire_protection"));
			register(r, new EnchantmentShieldProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.Type.PROJECTILE, slots), new ResourceLocation("projectile_protection"));
			register(r, new EnchantmentShieldProtection(Enchantment.Rarity.RARE, EnchantmentProtection.Type.EXPLOSION, slots), new ResourceLocation("blast_protection"));
			register(r, new EnchantmentShieldThorns(Enchantment.Rarity.VERY_RARE, slots), new ResourceLocation("thorns"));
		}

		if(Config.moreShieldEnchantments || Config.axeWeaponEnchants) {
			EntityEquipmentSlot[] slots = new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND};
			register(r, new EnchantmentExtendedKnockback(Enchantment.Rarity.UNCOMMON, slots), new ResourceLocation("knockback"));
			register(r, new EnchantmentExtendedFire(Enchantment.Rarity.RARE, slots), new ResourceLocation("fire_aspect"));
			if(Config.axeWeaponEnchants) {
				register(r, new EnchantmentAxeLooting(Enchantment.Rarity.RARE, EnumEnchantmentType.WEAPON, slots), new ResourceLocation("looting"));
			}
		}

		if(Config.axeEnchantmentTable) {
			EntityEquipmentSlot[] slots = new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND};
			register(r, new EnchantmentAxeDamage(Enchantment.Rarity.COMMON, 0, slots), new ResourceLocation("sharpness"));
			register(r, new EnchantmentAxeDamage(Enchantment.Rarity.UNCOMMON, 1, slots), new ResourceLocation("smite"));
			register(r, new EnchantmentAxeDamage(Enchantment.Rarity.UNCOMMON, 2, slots), new ResourceLocation("bane_of_arthropods"));
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();

		if(Config.separateCrook) {
			bone.setRepairItem(new ItemStack(Items.BONE));
			if(Config.netherCrooks) {
				blaze.setRepairItem(new ItemStack(Items.BLAZE_ROD));
				wither.setRepairItem(InspirationsShared.witherBone);
			}
		}

		registerDispenserBehavior();
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
		MinecraftForge.EVENT_BUS.register(ToolsEvents.class);
	}

	private void registerDispenserBehavior() {
		registerDispenserBehavior(arrow, new BehaviorProjectileDispense() {
			@Override
			protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack) {
				EntityModArrow arrow = new EntityModArrow(world, position.getX(), position.getY(), position.getZ(), stack.getMetadata());
				arrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
				return arrow;
			}
		});
		registerDispenserBehavior(redstoneCharger, new Bootstrap.BehaviorDispenseOptional() {
			@Override
			protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
				this.successful = true;
				World world = source.getWorld();
				EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);
				BlockPos pos = source.getBlockPos().offset(facing);

				if (redstoneCharge.canPlaceBlockAt(world, pos)) {
					world.setBlockState(pos, redstoneCharge.getDefaultState().withProperty(BlockRedstoneCharge.FACING, facing));

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
