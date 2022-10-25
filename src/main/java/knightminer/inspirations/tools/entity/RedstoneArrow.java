package knightminer.inspirations.tools.entity;

import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tools.block.RedstoneChargeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import static knightminer.inspirations.tools.InspirationsTools.redstoneCharge;

public class RedstoneArrow extends AbstractArrow implements IEntityAdditionalSpawnData {
  public RedstoneArrow(EntityType<RedstoneArrow> entType, Level world) {
    super(entType, world);
  }

  public RedstoneArrow(Level world, double x, double y, double z) {
    super(InspirationsTools.entRSArrow, x, y, z, world);
    init();
  }

  public RedstoneArrow(Level world, LivingEntity shooter) {
    super(InspirationsTools.entRSArrow, shooter, world);
    init();
  }

  @Override
  public Packet<?> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  public void writeSpawnData(FriendlyByteBuf buffer) {
    Entity shooter = this.getOwner();
    buffer.writeInt(shooter != null ? shooter.getId() : 0);
  }

  @Override
  public void readSpawnData(FriendlyByteBuf buffer) {
    Entity shooter = this.level.getEntity(buffer.readInt());
    if (shooter != null) {
      this.setOwner(shooter);
    }
  }

  private void init() {
    this.setBaseDamage(0.25);
  }

  private static final TranslatableComponent NAME = new TranslatableComponent("item.inspirations.charged_arrow");

  @Override
  public Component getName() {
    if (this.hasCustomName()) {
      return super.getName();
    } else {
      return NAME;
    }
  }

  @Override
  protected ItemStack getPickupItem() {
    return new ItemStack(InspirationsTools.redstoneArrow, 1);
  }

  /**
   * Called when the arrow hits a block or an entity
   */
  @Override
  protected void onHitBlock(BlockHitResult raytrace) {
    // get to the block the arrow is on
    Direction sideHit = raytrace.getDirection();
    BlockPos pos = raytrace.getBlockPos().relative(sideHit);

    // if there is a block there, try the block next to that
    if (!level.getBlockState(pos).canBeReplaced(new DirectionalPlaceContext(level, pos, sideHit, ItemStack.EMPTY, sideHit))) {
      pos = pos.relative(sideHit);
      if (!level.getBlockState(pos).canBeReplaced(new DirectionalPlaceContext(level, pos, sideHit, ItemStack.EMPTY, sideHit))) {
        super.onHitBlock(raytrace);
        return;
      }
    }

    level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 0.8F);
    BlockState state = redstoneCharge.defaultBlockState().setValue(RedstoneChargeBlock.FACING, sideHit.getOpposite());
    level.setBlock(pos, state, Block.UPDATE_ALL_IMMEDIATE);
    redstoneCharge.setPlacedBy(level, pos, state, null, ItemStack.EMPTY);

    this.discard();
  }
}
