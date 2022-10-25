package knightminer.inspirations.tools.entity;

import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tools.block.RedstoneChargeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import static knightminer.inspirations.tools.InspirationsTools.redstoneCharge;

public class RedstoneArrow extends AbstractArrowEntity implements IEntityAdditionalSpawnData {
  public RedstoneArrow(EntityType<RedstoneArrow> entType, World world) {
    super(entType, world);
  }

  public RedstoneArrow(World world, double x, double y, double z) {
    super(InspirationsTools.entRSArrow, x, y, z, world);
    init();
  }

  public RedstoneArrow(World world, LivingEntity shooter) {
    super(InspirationsTools.entRSArrow, shooter, world);
    init();
  }

  @Override
  public IPacket<?> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  public void writeSpawnData(PacketBuffer buffer) {
    Entity shooter = this.getOwner();
    buffer.writeInt(shooter != null ? shooter.getId() : 0);
  }

  @Override
  public void readSpawnData(PacketBuffer buffer) {
    Entity shooter = this.level.getEntity(buffer.readInt());
    if (shooter != null) {
      this.setOwner(shooter);
    }
  }

  private void init() {
    this.setBaseDamage(0.25);
  }

  private static TranslationTextComponent NAME = new TranslationTextComponent("item.inspirations.charged_arrow");

  @Override
  public ITextComponent getName() {
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
  protected void onHitBlock(BlockRayTraceResult raytrace) {
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

    level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 0.8F);
    BlockState state = redstoneCharge.defaultBlockState().setValue(RedstoneChargeBlock.FACING, sideHit.getOpposite());
    level.setBlock(pos, state, Constants.BlockFlags.DEFAULT_AND_RERENDER);
    redstoneCharge.setPlacedBy(level, pos, state, null, ItemStack.EMPTY);


    this.remove();
  }
}
