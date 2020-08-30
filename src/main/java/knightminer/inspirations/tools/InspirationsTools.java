package knightminer.inspirations.tools;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.ModuleBase;
import knightminer.inspirations.common.item.HidableItem;
import knightminer.inspirations.tools.block.RedstoneChargeBlock;
import knightminer.inspirations.tools.datagen.ToolsRecipeProvider;
import knightminer.inspirations.tools.enchantment.AxeDamageEnchantment;
import knightminer.inspirations.tools.enchantment.AxeLootBonusEnchantment;
import knightminer.inspirations.tools.enchantment.ExtendedFireAspectEnchantment;
import knightminer.inspirations.tools.enchantment.ExtendedKnockbackEnchantment;
import knightminer.inspirations.tools.enchantment.ShieldProtectionEnchantment;
import knightminer.inspirations.tools.enchantment.ShieldThornsEnchantment;
import knightminer.inspirations.tools.entity.RedstoneArrow;
import knightminer.inspirations.tools.item.EnchantableShieldItem;
import knightminer.inspirations.tools.item.RedstoneArrowItem;
import knightminer.inspirations.tools.item.RedstoneChargerItem;
import knightminer.inspirations.tools.item.WaypointCompassItem;
import knightminer.inspirations.tools.recipe.CopyWaypointCompassRecipe;
import knightminer.inspirations.tools.recipe.DyeWaypointCompassRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.data.DataGenerator;
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
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
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

  // The "undyed" compass is White.
  public static WaypointCompassItem[] waypointCompasses = new WaypointCompassItem[16];

  // blocks
  public static Block redstoneCharge;

  public static EntityType<RedstoneArrow> entRSArrow;

  @SubscribeEvent
  public void setup(FMLCommonSetupEvent event) {
    MinecraftForge.EVENT_BUS.register(ToolsEvents.class);
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
    Item.Properties materialsProps = new Item.Properties().group(ItemGroup.MATERIALS);
    Item.Properties toolProps = new Item.Properties().group(ItemGroup.TOOLS);

    redstoneArrow = registry.register(new RedstoneArrowItem(toolProps), "charged_arrow");

    redstoneCharger = registry.register(new RedstoneChargerItem(), "redstone_charger");

    lock = registry.register(new HidableItem(materialsProps, Config.enableLock), "lock");
    key = registry.register(new HidableItem(materialsProps, Config.enableLock), "key");

    northCompass = registry.register(new HidableItem(toolProps, Config.enableNorthCompass), "north_compass");
    barometer = registry.register(new HidableItem(toolProps, Config.enableBarometer), "barometer");
    photometer = registry.register(new HidableItem(toolProps, Config.enablePhotometer), "photometer");

    // TODO: reevaluate
    // TODO: enum object
    waypointCompasses[DyeColor.WHITE.getId()] = registry.register(new WaypointCompassItem(0xDDDDDD, 0xFFC100, Config.enableWaypointCompass), "waypoint_compass");
    waypointCompasses[DyeColor.BLACK.getId()] = registry.register(new WaypointCompassItem(0x444444, DyeColor.RED.getColorValue()), "black_waypoint_compass");
    registerWaypointCompass(registry, DyeColor.LIGHT_GRAY, DyeColor.WHITE.getColorValue());
    registerWaypointCompass(registry, DyeColor.GRAY, DyeColor.LIGHT_GRAY.getColorValue());
    registerWaypointCompass(registry, DyeColor.RED, DyeColor.ORANGE.getColorValue());
    registerWaypointCompass(registry, DyeColor.ORANGE, DyeColor.YELLOW.getColorValue());
    registerWaypointCompass(registry, DyeColor.YELLOW, 0xDBA213);
    registerWaypointCompass(registry, DyeColor.LIME, DyeColor.BROWN.getColorValue());
    registerWaypointCompass(registry, DyeColor.GREEN, DyeColor.LIME.getColorValue());
    registerWaypointCompass(registry, DyeColor.CYAN, DyeColor.LIGHT_BLUE.getColorValue());
    registerWaypointCompass(registry, DyeColor.LIGHT_BLUE, 0x77A9FF);
    registerWaypointCompass(registry, DyeColor.BLUE, 0x7E54FF);
    registerWaypointCompass(registry, DyeColor.PURPLE, DyeColor.MAGENTA.getColorValue());
    registerWaypointCompass(registry, DyeColor.MAGENTA, DyeColor.PINK.getColorValue());
    registerWaypointCompass(registry, DyeColor.PINK, 0xF2BFCE);
    registerWaypointCompass(registry, DyeColor.BROWN, 0xA59072);

    if (Config.shieldEnchantmentTable.get()) {
      registry.register(new EnchantableShieldItem(new Item.Properties().maxDamage(Items.SHIELD.getMaxDamage()).group(ItemGroup.COMBAT)), Items.SHIELD);
    }
  }

  private void registerWaypointCompass(ItemRegistryAdapter registry, DyeColor body, int needle) {
    waypointCompasses[body.getId()] = registry.register(new WaypointCompassItem(body.getColorValue(), needle), body.getString() + "_waypoint_compass");
  }

  @SubscribeEvent
  void registerEntities(Register<EntityType<?>> event) {
    EntityTypeRegistryAdapter registry = new EntityTypeRegistryAdapter(event.getRegistry());
    entRSArrow = registry.register(EntityType.Builder
                                       .<RedstoneArrow>create(RedstoneArrow::new, EntityClassification.MISC)
                                       .size(0.5F, 0.5F)
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
  void registerRecipes(Register<IRecipeSerializer<?>> event) {
    RegistryAdapter<IRecipeSerializer<?>> registry = new RegistryAdapter<>(event.getRegistry());
    registry.register(new SpecialRecipeSerializer<>(CopyWaypointCompassRecipe::new), "copy_waypoint_compass");
    registry.register(new DyeWaypointCompassRecipe.Serializer(), "dye_waypoint_compass");
  }

  @SubscribeEvent
  public void registerEnchantments(Register<Enchantment> event) {
    RegistryAdapter<Enchantment> registry = new RegistryAdapter<>(event.getRegistry());

    if (Config.moreShieldEnchantments.get()) {
      EquipmentSlotType[] slots = new EquipmentSlotType[]{
          EquipmentSlotType.HEAD,
          EquipmentSlotType.CHEST,
          EquipmentSlotType.LEGS,
          EquipmentSlotType.FEET
      };
      for (ProtectionEnchantment ench : new ProtectionEnchantment[]{
          (ProtectionEnchantment)Enchantments.PROTECTION,
          (ProtectionEnchantment)Enchantments.FIRE_PROTECTION,
          (ProtectionEnchantment)Enchantments.PROJECTILE_PROTECTION,
          (ProtectionEnchantment)Enchantments.BLAST_PROTECTION
      }) {
        registry.register(new ShieldProtectionEnchantment(ench.getRarity(), ench.protectionType, slots), ench);
      }
      registry.register(new ShieldThornsEnchantment(Enchantments.THORNS.getRarity(), slots), Enchantments.THORNS);
    }

    if (Config.moreShieldEnchantments.get() || Config.axeWeaponEnchants.get()) {
      EquipmentSlotType[] slots = new EquipmentSlotType[]{EquipmentSlotType.MAINHAND};
      registry.register(new ExtendedKnockbackEnchantment(Enchantment.Rarity.UNCOMMON, slots), Enchantments.KNOCKBACK);
      registry.register(new ExtendedFireAspectEnchantment(Enchantment.Rarity.RARE, slots), Enchantments.FIRE_ASPECT);
      if (Config.axeWeaponEnchants.get()) {
        registry.register(new AxeLootBonusEnchantment(Enchantment.Rarity.RARE, EnchantmentType.WEAPON, slots), Enchantments.LOOTING);
      }
    }

    if (Config.axeEnchantmentTable.get()) {
      EquipmentSlotType[] slots = new EquipmentSlotType[]{EquipmentSlotType.MAINHAND};
      registry.register(new AxeDamageEnchantment(Enchantment.Rarity.COMMON, 0, slots), Enchantments.SHARPNESS);
      registry.register(new AxeDamageEnchantment(Enchantment.Rarity.UNCOMMON, 1, slots), Enchantments.SMITE);
      registry.register(new AxeDamageEnchantment(Enchantment.Rarity.UNCOMMON, 2, slots), Enchantments.BANE_OF_ARTHROPODS);
    }
  }

  private void registerDispenserBehavior() {
    DispenserBlock.registerDispenseBehavior(redstoneArrow, new ProjectileDispenseBehavior() {
      @Override
      protected ProjectileEntity getProjectileEntity(World world, IPosition position, ItemStack stack) {
        RedstoneArrow arrow = new RedstoneArrow(world, position.getX(), position.getY(), position.getZ());
        arrow.pickupStatus = ArrowEntity.PickupStatus.ALLOWED;
        return arrow;
      }
    });
    DispenserBlock.registerDispenseBehavior(redstoneCharger, new OptionalDispenseBehavior() {
      @Override
      protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
        this.setSuccessful(true);
        World world = source.getWorld();
        Direction facing = source.getBlockState().get(DispenserBlock.FACING);
        BlockPos pos = source.getBlockPos().offset(facing);

        if (world.getBlockState(pos).isReplaceable(new DirectionalPlaceContext(
            world, pos, facing, ItemStack.EMPTY, facing
        ))) {
          world.setBlockState(pos, redstoneCharge.getDefaultState().with(RedstoneChargeBlock.FACING, facing));
          if (stack.attemptDamageItem(1, world.rand, null)) {
            stack.setCount(0);
          }
        } else {
          this.setSuccessful(false);
        }

        return stack;
      }
    });
  }
}
