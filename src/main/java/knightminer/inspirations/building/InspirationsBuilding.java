package knightminer.inspirations.building;

import com.google.common.eventbus.Subscribe;

import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.building.block.BlockRedstoneCharge;
import knightminer.inspirations.building.block.BlockRope;
import knightminer.inspirations.building.block.BlockTorchLever;
import knightminer.inspirations.building.entity.EntityModArrow;
import knightminer.inspirations.building.item.ItemChargedArrow;
import knightminer.inspirations.building.item.ItemRedstoneCharger;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.EntityIds;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.common.item.ItemBlockTexture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsBuilding.pulseID, description = "Adds features related to redstone")
public class InspirationsBuilding extends PulseBase {
	public static final String pulseID = "InspirationsBuilding";

	@SidedProxy(clientSide = "knightminer.inspirations.building.BuildingClientProxy", serverSide = "knightminer.inspirations.common.CommonProxy")
	public static CommonProxy proxy;

	// blocks
	public static Block bookshelf;
	public static BlockRope rope;
	public static Block redstoneCharge;

	// items
	public static ItemMetaDynamic books;
	public static Item redstoneCharger;
	public static ItemArrow arrow;

	// materials
	public static ItemStack redstoneBook;

	// blocks
	public static Block torchLever;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		if(Config.enableBookshelf) {
			bookshelf = registerBlock(r, new BlockBookshelf(), "bookshelf");
			registerTE(TileBookshelf.class, "bookshelf");
		}

		if(Config.enableRope) {
			rope = registerBlock(r, new BlockRope(), "rope");
		}

		if(Config.enableTorchLever) {
			torchLever = registerBlock(r, new BlockTorchLever(), "torch_lever");
		}

		if(Config.enableRedstoneCharge) {
			redstoneCharge = registerBlock(r, new BlockRedstoneCharge(), "redstone_charge");
		}
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		books = registerItem(r, new ItemMetaDynamic(), "books");
		books.setCreativeTab(CreativeTabs.MATERIALS);
		if(Config.enableColoredBooks) {
			for(EnumDyeColor color : EnumDyeColor.values()) {
				books.addMeta(color.getMetadata(), color.getName());
			}
		}
		if(Config.enableRedstoneBook) {
			redstoneBook = books.addMeta(16, "redstone");
		}

		if(Config.enableRedstoneCharge) {
			redstoneCharger = registerItem(r, new ItemRedstoneCharger(), "redstone_charger");
			arrow = registerItem(r, new ItemChargedArrow(), "arrow");
		}

		// itemblocks
		if(torchLever != null) {
			registerItemBlock(r, torchLever);
		}
		if(bookshelf != null) {
			registerItemBlock(r, new ItemBlockTexture(bookshelf), BlockBookshelf.TYPE);
		}
		if(rope != null) {
			registerEnumItemBlock(r, rope);
		}
	}


	@SubscribeEvent
	public void registerEntities(Register<EntityEntry> event) {
		IForgeRegistry<EntityEntry> r = event.getRegistry();
		if(Config.enableRedstoneCharge) {
			r.register(getEntityBuilder(EntityModArrow.class, "arrow", EntityIds.ARROW)
					.tracker(64, 1, false)
					.build());
		}
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
		registerDispenserBehavior(arrow, new BehaviorProjectileDispense() {
			@Override
			protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack) {
				EntityModArrow arrow = new EntityModArrow(world, position.getX(), position.getY(), position.getZ());
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
