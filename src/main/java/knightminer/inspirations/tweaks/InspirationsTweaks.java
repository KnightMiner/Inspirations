package knightminer.inspirations.tweaks;

import com.google.common.eventbus.Subscribe;

import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.tweaks.block.BlockFittedCarpet;
import knightminer.inspirations.tweaks.block.BlockSmashingAnvil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsTweaks.pulseID, description = "Various vanilla tweaks")
public class InspirationsTweaks extends PulseBase {
	public static final String pulseID = "InspirationsTweaks";

	@SidedProxy(clientSide = "knightminer.inspirations.tweaks.TweaksClientProxy", serverSide = "knightminer.inspirations.common.CommonProxy")
	public static CommonProxy proxy;

	// blocks
	public static Block carpet;
	public static Block anvil;

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
		if(Config.enableAnvilSmashing) {
			anvil = register(r, new BlockSmashingAnvil(), new ResourceLocation("anvil"));
		}
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		if(carpet != null) {
			registerItemBlock(r, new ItemCloth(carpet));
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();

		// brew heartroots into regen potions
		if(Config.brewHeartbeet) {
			Ingredient heartbeet = Ingredient.fromStacks(InspirationsShared.heartbeet);
			PotionHelper.addMix(PotionTypes.WATER, heartbeet, PotionTypes.MUNDANE);
			PotionHelper.addMix(PotionTypes.AWKWARD, heartbeet, PotionTypes.REGENERATION);
		}

		InspirationsRegistry.registerAnvilBreaking(Material.GLASS);
		registerDispenserBehavior();
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
		MinecraftForge.EVENT_BUS.register(TweaksEvents.class);
	}

	private void registerDispenserBehavior() {
		if(Config.dispensersPlaceAnvils) {
			registerDispenserBehavior(Blocks.ANVIL, (source, stack) -> {
				// get basic data
				EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);
				World world = source.getWorld();
				BlockPos pos = source.getBlockPos().offset(facing);

				// if we cannot place it, toss the item
				if(!Blocks.ANVIL.canPlaceBlockAt(world, pos)) {
					return IBehaviorDispenseItem.DEFAULT_BEHAVIOR.dispense(source, stack);
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
