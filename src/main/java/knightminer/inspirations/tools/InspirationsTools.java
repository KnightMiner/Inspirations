package knightminer.inspirations.tools;

import com.google.common.eventbus.Subscribe;

import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.EntityIds;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.tools.entity.EntityModArrow;
import knightminer.inspirations.tools.item.ItemModArrow;
import knightminer.inspirations.tools.item.ItemRedstoneCharger;
import knightminer.inspirations.utility.block.BlockRedstoneCharge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsTools.pulseID, description = "Adds various tools or tweaks to vanilla tools")
public class InspirationsTools extends PulseBase {
	public static final String pulseID = "InspirationsTools";

	@SidedProxy(clientSide = "knightminer.inspirations.tools.ToolsClientProxy", serverSide = "knightminer.inspirations.common.CommonProxy")
	public static CommonProxy proxy;

	// items
	public static Item redstoneCharger;

	// blocks
	public static Block redstoneCharge;

	public static ItemArrow arrow;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		if(Config.enableRedstoneCharge) {
			redstoneCharge = registerBlock(r, new BlockRedstoneCharge(), "redstone_charge");
		}
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		arrow = registerItem(r, new ItemModArrow(), "arrow");
		if(Config.enableRedstoneCharge) {
			redstoneCharger = registerItem(r, new ItemRedstoneCharger(), "redstone_charger");
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
