package knightminer.inspirations.tools;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.ModuleBase;
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
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
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
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import slimeknights.mantle.registration.adapter.BlockRegistryAdapter;
import slimeknights.mantle.registration.adapter.EntityTypeRegistryAdapter;
import slimeknights.mantle.registration.adapter.ItemRegistryAdapter;
import slimeknights.mantle.registration.adapter.RegistryAdapter;

public class InspirationsTools extends ModuleBase {
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
  public void register(RegisterEvent event) {
    ResourceKey<? extends Registry<?>> registryKey = event.getRegistryKey();
    if (registryKey == Registries.BLOCK) {
      BlockRegistryAdapter registry = new BlockRegistryAdapter(ForgeRegistries.BLOCKS);
      redstoneCharge = registry.register(new RedstoneChargeBlock(
        Block.Properties.of().mapColor(MapColor.NONE).pushReaction(PushReaction.DESTROY).strength(0).lightLevel((state) -> 2)
      ), "redstone_charge");
    }
    else if (registryKey == Registries.ITEM) {
      ItemRegistryAdapter registry = new ItemRegistryAdapter(ForgeRegistries.ITEMS);
      Item.Properties props = new Item.Properties();

      redstoneArrow = registry.register(new RedstoneArrowItem(props), "charged_arrow");
      redstoneCharger = registry.register(new RedstoneChargerItem(new Item.Properties().durability(120)), "redstone_charger");

      lock = registry.register(props, "lock");
      key = registry.register(props, "key");

      northCompass = registry.register(props, "north_compass");
      barometer = registry.register(props, "barometer");
      photometer = registry.register(props, "photometer");
      dimensionCompass = registry.register(new DimensionCompassItem(props), "dimension_compass");

      if (Config.shieldEnchantmentTable.getAsBoolean()) {
        //noinspection deprecation  not how I am using it
        shield = registry.register(new EnchantableShieldItem(new Item.Properties().durability(Items.SHIELD.getMaxDamage())), Items.SHIELD);
      }
    }
    else if (registryKey == Registries.ENTITY_TYPE) {
      EntityTypeRegistryAdapter registry = new EntityTypeRegistryAdapter(ForgeRegistries.ENTITY_TYPES);
      entRSArrow = registry.register(EntityType.Builder
                                         .<RedstoneArrow>of(RedstoneArrow::new, MobCategory.MISC)
                                         .sized(0.5F, 0.5F)
                                         .setTrackingRange(4)
                                         .setUpdateInterval(20)
                                         .setCustomClientFactory((packet, world) -> new RedstoneArrow(InspirationsTools.entRSArrow, world)),
                                     "redstone_arrow");
    }
    else if (registryKey == Registries.ENCHANTMENT) {
      RegistryAdapter<Enchantment> registry = new RegistryAdapter<>(ForgeRegistries.ENCHANTMENTS);

      if (Config.moreShieldEnchantments.getAsBoolean()) {
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

      if (Config.moreShieldEnchantments.getAsBoolean() || Config.axeWeaponEnchants.getAsBoolean()) {
        EquipmentSlot[] slots = new EquipmentSlot[]{EquipmentSlot.MAINHAND};
        registry.register(new ExtendedKnockbackEnchantment(Enchantment.Rarity.UNCOMMON, slots), Enchantments.KNOCKBACK);
        registry.register(new ExtendedFireAspectEnchantment(Enchantment.Rarity.RARE, slots), Enchantments.FIRE_ASPECT);
        if (Config.axeWeaponEnchants.getAsBoolean()) {
          registry.register(new AxeLootBonusEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.WEAPON, slots), Enchantments.MOB_LOOTING);
        }
      }

      if (Config.axeEnchantmentTable.getAsBoolean()) {
        EquipmentSlot[] slots = new EquipmentSlot[]{EquipmentSlot.MAINHAND};
        registry.register(new AxeDamageEnchantment(Enchantment.Rarity.COMMON, 0, slots), Enchantments.SHARPNESS);
        registry.register(new AxeDamageEnchantment(Enchantment.Rarity.UNCOMMON, 1, slots), Enchantments.SMITE);
        registry.register(new AxeDamageEnchantment(Enchantment.Rarity.UNCOMMON, 2, slots), Enchantments.BANE_OF_ARTHROPODS);
      }
    }
  }

  @SubscribeEvent
  void gatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    gen.addProvider(event.includeServer(), new ToolsRecipeProvider(gen.getPackOutput()));
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
