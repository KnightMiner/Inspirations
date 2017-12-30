package knightminer.inspirations.shared;

import com.google.common.eventbus.Subscribe;

import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.EntityIds;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.shared.entity.EntityModArrow;
import knightminer.inspirations.shared.item.ItemModArrow;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.item.ItemEdible;
import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsShared.pulseID, description = "Blocks and items used by all modules", forced = true)
public class InspirationsShared extends PulseBase {
	public static final String pulseID = "InspirationsShared";

	@SidedProxy(clientSide = "knightminer.inspirations.shared.SharedClientProxy", serverSide = "knightminer.inspirations.common.CommonProxy")
	public static CommonProxy proxy;

	// items
	public static ItemArrow arrow;
	public static ItemMetaDynamic materials;
	public static ItemEdible edibles;

	// materials
	public static ItemStack lock;
	public static ItemStack key;
	public static ItemStack splashBottle;
	public static ItemStack lingeringBottle;

	// edibles
	public static ItemStack heartbeet;
	public static ItemStack boiledEgg;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		arrow = registerItem(r, new ItemModArrow(), "arrow");

		materials = registerItem(r, new ItemMetaDynamic(), "materials");
		materials.setCreativeTab(CreativeTabs.MATERIALS);

		edibles = registerItem(r, new ItemEdible(), "edibles");
		edibles.setCreativeTab(CreativeTabs.FOOD);

		// add items from modules
		if(isUtilityLoaded() && Config.enableLock) {
			lock = materials.addMeta(0, "lock");
			key = materials.addMeta(1, "key");
		}

		if(isTweaksLoaded() && Config.enableHeartbeet) {
			heartbeet = edibles.addFood(0, 2, 2.4f, "heartbeet", new PotionEffect(MobEffects.REGENERATION, 100));
		}
		if(isRecipesLoaded()) {
			if(Config.enableCauldronBrewing) {
				splashBottle = materials.addMeta(2, "splash_bottle");
				lingeringBottle = materials.addMeta(3, "lingering_bottle");
			}
		}
	}

	@SubscribeEvent
	public void registerEntities(Register<EntityEntry> event) {
		IForgeRegistry<EntityEntry> r = event.getRegistry();
		r.register(getEntityBuilder(EntityModArrow.class, "arrow", EntityIds.ARROW)
				.tracker(64, 1, false)
				.build());
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();

		registerDispenserBehavior();
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
	}

	private void registerDispenserBehavior() {
		registerDispenserBehavior(InspirationsShared.arrow, new BehaviorProjectileDispense() {
			@Override
			protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack) {
				EntityModArrow arrow = new EntityModArrow(world, position.getX(), position.getY(), position.getZ(), stack.getMetadata());
				arrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
				return arrow;
			}
		});
	}
}
