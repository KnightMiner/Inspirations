package knightminer.inspirations.tools.capability;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.network.DimensionCompassPositionPacket;
import knightminer.inspirations.common.network.InspirationsNetwork;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.EventPriority;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Default implementation for a dimension compass
 */
public class DimensionCompass implements ICapabilitySerializable<CompoundNBT>, IDimensionCompass {
	public static final ResourceLocation KEY = Inspirations.getResource("dimension_compass");

	/**
	 * Capability instance for dimension compasses
	 */
	@CapabilityInject(IDimensionCompass.class)
	public static Capability<IDimensionCompass> CAPABILITY = null;

	public static void register() {
		// register a bunch of dumb unused things because I need to register one actually useful thing
		CapabilityManager.INSTANCE.register(IDimensionCompass.class, new IStorage<IDimensionCompass>() {
			@Nullable
			@Override
			public INBT writeNBT(Capability<IDimensionCompass> capability, IDimensionCompass instance, Direction side) {
				return null;
			}

			@Override
			public void readNBT(Capability<IDimensionCompass> capability, IDimensionCompass instance, Direction side, INBT nbt) {}
		}, DimensionCompass::new);

		MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, DimensionCompass::attachCapability);
		MinecraftForge.EVENT_BUS.addListener(DimensionCompass::dimensionChange);
		MinecraftForge.EVENT_BUS.addListener(DimensionCompass::playerLoggedIn);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, DimensionCompass::dimensionTravel);
	}


	private final LazyOptional<IDimensionCompass> capabilityInstance = LazyOptional.of(() -> this);
	private BlockPos enteredPosition;

	@Nullable
	@Override
	public BlockPos getEnteredPosition() {
		return enteredPosition;
	}

	@Override
	public void setEnteredPosition(@Nullable BlockPos pos) {
		enteredPosition = pos;
	}

	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CAPABILITY) {
			return capabilityInstance.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundNBT serializeNBT() {
		BlockPos pos = getEnteredPosition();
		if (pos == null) {
			return new CompoundNBT();
		}
		return NBTUtil.writeBlockPos(pos);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if (nbt.contains("X", NBT.TAG_ANY_NUMERIC) && nbt.contains("Y", NBT.TAG_ANY_NUMERIC) && nbt.contains("Z", NBT.TAG_ANY_NUMERIC)) {
			setEnteredPosition(NBTUtil.readBlockPos(nbt));
		} else {
			setEnteredPosition(null);
		}
	}


	// events

	/**
	 * Called to add the capability handler to all players
	 * @param event  Event
	 */
	private static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();
		if (entity instanceof PlayerEntity) {
			event.addCapability(KEY, new DimensionCompass());
		}
	}

	/**
	 * Syncs the position to the player
	 * @param player  Player
	 * @param pos     New position
	 */
	private static void sync(PlayerEntity player, @Nullable BlockPos pos) {
		if (player instanceof ServerPlayerEntity) {
			InspirationsNetwork.INSTANCE.sendTo(new DimensionCompassPositionPacket(pos), ((ServerPlayerEntity) player));
		}
	}

	/**
	 * Called when the player changes dimensions to update the position
	 * @param event  Event
	 */
	private static void dimensionChange(PlayerChangedDimensionEvent event) {
		PlayerEntity player = event.getPlayer();
		BlockPos pos = player.blockPosition();
		sync(player, pos);
		player.getCapability(CAPABILITY).ifPresent(compass -> {
			compass.setEnteredPosition(pos);
		});
	}

	/**
	 * Less ideal event for end because dimension change is not fired for the end portal
	 * Lowest priority should ensure this is not canceled, and if its not vanilla, the dimension change will update later
	 */
	private static void dimensionTravel(EntityTravelToDimensionEvent event) {
		if (event.getDimension() == World.OVERWORLD) {
			Entity entity = event.getEntity();
			if (entity.getCommandSenderWorld().dimension() == World.END) {
				if (entity instanceof ServerPlayerEntity) {
					// probably not needed as the client reset in my experience, but might as well
					entity.getCapability(CAPABILITY).ifPresent(compass -> compass.setEnteredPosition(null));
					InspirationsNetwork.INSTANCE.sendTo(new DimensionCompassPositionPacket((BlockPos)null), ((ServerPlayerEntity) entity));
				}
			}
		}
	}

	/**
	 * Called when the player joins the server to send them the stored position
	 * @param event  Event
	 */
	private static void playerLoggedIn(PlayerLoggedInEvent event) {
		PlayerEntity player = event.getPlayer();
		player.getCapability(CAPABILITY).ifPresent(compass -> {
			BlockPos pos = compass.getEnteredPosition();
			// defaults to null, so sync should not be needed
			if (pos != null) {
				sync(player, pos);
			}
		});
	}
}
