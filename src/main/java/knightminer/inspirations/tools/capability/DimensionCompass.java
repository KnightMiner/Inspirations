package knightminer.inspirations.tools.capability;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.network.DimensionCompassPositionPacket;
import knightminer.inspirations.common.network.InspirationsNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Default implementation for a dimension compass
 */
public class DimensionCompass implements ICapabilitySerializable<CompoundTag>, IDimensionCompass {
	public static final ResourceLocation KEY = Inspirations.getResource("dimension_compass");

	/**
	 * Capability instance for dimension compasses
	 */
	public static final Capability<IDimensionCompass> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

	public static void register() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.NORMAL, false, RegisterCapabilitiesEvent.class, event -> event.register(IDimensionCompass.class));
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

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CAPABILITY) {
			return capabilityInstance.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		BlockPos pos = getEnteredPosition();
		if (pos == null) {
			return new CompoundTag();
		}
		return NbtUtils.writeBlockPos(pos);
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		if (nbt.contains("X", Tag.TAG_ANY_NUMERIC) && nbt.contains("Y", Tag.TAG_ANY_NUMERIC) && nbt.contains("Z", Tag.TAG_ANY_NUMERIC)) {
			setEnteredPosition(NbtUtils.readBlockPos(nbt));
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
		if (entity instanceof Player) {
			event.addCapability(KEY, new DimensionCompass());
		}
	}

	/**
	 * Syncs the position to the player
	 * @param player  Player
	 * @param pos     New position
	 */
	private static void sync(Player player, @Nullable BlockPos pos) {
		if (player instanceof ServerPlayer) {
			InspirationsNetwork.INSTANCE.sendTo(new DimensionCompassPositionPacket(pos), ((ServerPlayer) player));
		}
	}

	/**
	 * Called when the player changes dimensions to update the position
	 * @param event  Event
	 */
	private static void dimensionChange(PlayerChangedDimensionEvent event) {
		Player player = event.getPlayer();
		BlockPos pos = player.blockPosition();
		sync(player, pos);
		player.getCapability(CAPABILITY).ifPresent(compass -> compass.setEnteredPosition(pos));
	}

	/**
	 * Less ideal event for end because dimension change is not fired for the end portal
	 * Lowest priority should ensure this is not canceled, and if its not vanilla, the dimension change will update later
	 */
	private static void dimensionTravel(EntityTravelToDimensionEvent event) {
		if (event.getDimension() == Level.OVERWORLD) {
			Entity entity = event.getEntity();
			if (entity.getCommandSenderWorld().dimension() == Level.END) {
				if (entity instanceof ServerPlayer) {
					// probably not needed as the client reset in my experience, but might as well
					entity.getCapability(CAPABILITY).ifPresent(compass -> compass.setEnteredPosition(null));
					InspirationsNetwork.INSTANCE.sendTo(new DimensionCompassPositionPacket((BlockPos)null), ((ServerPlayer) entity));
				}
			}
		}
	}

	/**
	 * Called when the player joins the server to send them the stored position
	 * @param event  Event
	 */
	private static void playerLoggedIn(PlayerLoggedInEvent event) {
		Player player = event.getPlayer();
		player.getCapability(CAPABILITY).ifPresent(compass -> {
			BlockPos pos = compass.getEnteredPosition();
			// defaults to null, so sync should not be needed
			if (pos != null) {
				sync(player, pos);
			}
		});
	}
}
