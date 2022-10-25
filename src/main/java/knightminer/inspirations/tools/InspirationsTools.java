package knightminer.inspirations.tools;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.ModuleBase;
import knightminer.inspirations.common.item.HidableItem;
import knightminer.inspirations.tools.block.RedstoneChargeBlock;
import knightminer.inspirations.tools.capability.DimensionCompass;
import knightminer.inspirations.tools.datagen.ToolsRecipeProvider;
import knightminer.inspirations.tools.enchantment.AxeDamageEnchantment;
import knightminer.inspirations.tools.enchantment.AxeLootBonusEnchantment;
import knightminer.inspirations.tools.enchantment.ExtendedFireAspectEnchantment;
import knightminer.inspirations.tools.enchantment.ExtendedKnockbackEnchantment;
import knightminer.inspirations.tools.enchantment.ShieldProtectionEnchantment;
import knightminer.inspirations.tools.enchantment.ShieldThornsEnchantment;
import knightminer.inspirations.tools.entity.RedstoneArrow;
import knightminer.inspirations.tools.item.DimensionCompassItem;
import knightminer.inspirations.tools.item.EnchantableShieldItem;
import knightminer.inspirations.tools.item.RedstoneArrowItem;
import knightminer.inspirations.tools.item.RedstoneChargerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import slimeknights.mantle.registration.adapter.BlockRegistryAdapter;
import slimeknights.mantle.registration.adapter.EntityTypeRegistryAdapter;
import slimeknights.mantle.registration.adapter.ItemRegistryAdapter;
import slimeknights.mantle.registration.adapter.RegistryAdapter;

@SuppressWarnings("unused")
public class InspirationsTools extends ModuleBase {
  public static final String pulseID = "InspirationsTools";

  // items
  public static Item lock;
  public static Item key;
  public static Item redstoneCharger;
  public static Item northCompass;
  public static Item barometer;
  public static Item photometer;
  public static ArrowItem redstoneArrow;
  // only used in client proxy, should use Items.SHIELD normally
  static Item shield;

  // The "undyed" compass is White.
  public static Item dimensionCompass;

  // blocks
  public static Block redstoneCharge;

  public static EntityType<RedstoneArrow> entRSArrow;

  @SubscribeEvent
  public void setup(FMLCommonSetupEvent event) {
    MinecraftForge.EVENT_BUS.register(ToolsEvents.class);
    DimensionCompass.register();
    registerDispenserBehavior();
  }

  @SubscribeEvent
  public void registerBlocks(Register<Block> event) {
    BlockRegistryAdapter registry = new BlockRegistryAdapter(event.getRegistry());
    redstoneCharge = registry.register(new RedstoneChargeBlock(), "redstone_charge");
  }

  @SubscribeEvent
  @SuppressWarnings("deprecation")
  public void registerItems(Register<Item> event) {
    ItemRegistryAdapter registry = new ItemRegistryAdapter(event.getRegistry());
    Item.Properties materialsProps = new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS);
    Item.Properties toolProps = new Item.Properties().tab(CreativeModeTab.TAB_TOOLS);

    redstoneArrow = registry.register(new RedstoneArrowItem(toolProps), "charged_arrow");

    redstoneCharger = registry.register(new RedstoneChargerItem(), "redstone_charger");

    lock = registry.register(new HidableItem(materialsProps, Config.enableLock), "lock");
    key = registry.register(new HidableItem(materialsProps, Config.enableLock), "key");

    northCompass = registry.register(new HidableItem(toolProps, Config.enableNorthCompass), "north_compass");
    barometer = registry.register(new HidableItem(toolProps, Config.enableBarometer), "barometer");
    photometer = registry.register(new HidableItem(toolProps, Config.enablePhotometer), "photometer");

    dimensionCompass = registry.register(new DimensionCompassItem(toolProps), "dimension_compass");

    if (Config.shieldEnchantmentTable.getAsBoolean()) {
      shield = registry.register(new EnchantableShieldItem(new Item.Properties().durability(Items.SHIELD.getMaxDamage()).tab(CreativeModeTab.TAB_COMBAT)), Items.SHIELD);
    }
  }

  @SubscribeEvent
  void registerEntities(Register<EntityType<?>> event) {
    EntityTypeRegistryAdapter registry = new EntityTypeRegistryAdapter(event.getRegistry());
    entRSArrow = registry.register(EntityType.Builder
                                       .<RedstoneArrow>of(RedstoneArrow::new, MobCategory.MISC)
                                       .sized(0.5F, 0.5F)
                                       .setTrackingRange(4)
                                       .setUpdateInterval(20)
                                       .setCustomClientFactory((packet, world) -> new RedstoneArrow(InspirationsTools.entRSArrow, world)),
                                   "redstone_arrow");
  }

  @SubscribeEvent
  void gatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    if (event.includeServer()) {
      gen.addProvider(new ToolsRecipeProvider(gen));
    }
  }

  @SubscribeEvent
  public void registerEnchantments(Register<Enchantment> event) {
    RegistryAdapter<Enchantment> registry = new RegistryAdapter<>(event.getRegistry());

    if (Config.moreShieldEnchantments.get()) {
      EquipmentSlot[] slots = new EquipmentSlot[]{
          EquipmentSlot.HEAD,
          EquipmentSlot.CHEST,
          EquipmentSlot.LEGS,
          EquipmentSlot.FEET
      };
      for (ProtectionEnchantment ench : new ProtectionEnchantment[]{
          (ProtectionEnchantment)Enchantments.ALL_DAMAGE_PROTECTION,
          (ProtectionEnchantment)Enchantments.FIRE_PROTECTION,
          (ProtectionEnchantment)Enchantments.PROJECTILE_PROTECTION,
          (ProtectionEnchantment)Enchantments.BLAST_PROTECTION
      }) {
        registry.register(new ShieldProtectionEnchantment(ench.getRarity(), ench.type, slots), ench);
      }
      registry.register(new ShieldThornsEnchantment(Enchantments.THORNS.getRarity(), slots), Enchantments.THORNS);
    }

    if (Config.moreShieldEnchantments.get() || Config.axeWeaponEnchants.get()) {
      EquipmentSlot[] slots = new EquipmentSlot[]{EquipmentSlot.MAINHAND};
      registry.register(new ExtendedKnockbackEnchantment(Enchantment.Rarity.UNCOMMON, slots), Enchantments.KNOCKBACK);
      registry.register(new ExtendedFireAspectEnchantment(Enchantment.Rarity.RARE, slots), Enchantments.FIRE_ASPECT);
      if (Config.axeWeaponEnchants.get()) {
        registry.register(new AxeLootBonusEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON, slots), Enchantments.MOB_LOOTING);
      }
    }

    if (Config.axeEnchantmentTable.get()) {
      EquipmentSlot[] slots = new EquipmentSlot[]{EquipmentSlot.MAINHAND};
      registry.register(new AxeDamageEnchantment(Enchantment.Rarity.COMMON, 0, slots), Enchantments.SHARPNESS);
      registry.register(new AxeDamageEnchantment(Enchantment.Rarity.UNCOMMON, 1, slots), Enchantments.SMITE);
      registry.register(new AxeDamageEnchantment(Enchantment.Rarity.UNCOMMON, 2, slots), Enchantments.BANE_OF_ARTHROPODS);
    }
  }

  private void registerDispenserBehavior() {
    DispenserBlock.registerBehavior(redstoneArrow, new AbstractProjectileDispenseBehavior() {
      @Override
      protected Projectile getProjectile(Level world, Position position, ItemStack stack) {
        RedstoneArrow arrow = new RedstoneArrow(world, position.x(), position.y(), position.z());
        arrow.pickup = Arrow.Pickup.ALLOWED;
        return arrow;
      }
    });
    DispenserBlock.registerBehavior(redstoneCharger, new OptionalDispenseItemBehavior() {
      @Override
      protected ItemStack execute(BlockSource source, ItemStack stack) {
        this.setSuccess(true);
        Level world = source.getLevel();
        Direction facing = source.getBlockState().getValue(DispenserBlock.FACING);
        BlockPos pos = source.getPos().relative(facing);

        if (world.getBlockState(pos).canBeReplaced(new DirectionalPlaceContext(
            world, pos, facing, ItemStack.EMPTY, facing
        ))) {
          world.setBlockAndUpdate(pos, redstoneCharge.defaultBlockState().setValue(RedstoneChargeBlock.FACING, facing));
          if (stack.hurt(1, world.random, null)) {
            stack.setCount(0);
          }
        } else {
          this.setSuccess(false);
        }

        return stack;
      }
    });
  }
}
