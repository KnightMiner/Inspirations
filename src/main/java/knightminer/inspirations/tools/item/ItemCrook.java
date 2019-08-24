package knightminer.inspirations.tools.item;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

import knightminer.inspirations.common.IHidable;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.tools.InspirationsTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

public class ItemCrook extends ToolItem implements IHidable {
    private final Supplier<Boolean> enabled;

	private static final Set<Block> EFFECTIVE_BLOCKS = Collections.emptySet();
	public ItemCrook(IItemTier tier, Supplier<Boolean> enableFunc) {
		super(1.5F, -3.0F, tier, EFFECTIVE_BLOCKS, new Item.Properties()
				.group(ItemGroup.TOOLS)
				.maxDamage(tier.getMaxUses() * 2)
				.addToolType(InspirationsRegistry.CROOK_TYPE, tier.getHarvestLevel())
		);
		enabled = enableFunc;
	}

    public boolean isEnabled() {
        return enabled.get();
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if(shouldAddtoItemGroup(group)) {
            super.fillItemGroup(group, items);
        }
    }

	/**
	 * Check whether this Item can harvest the given Block
	 */
	@Override
	public boolean canHarvestBlock(BlockState block) {
		Material material = block.getMaterial();
		return material == Material.LEAVES ||
				material == Material.PLANTS ||
				material == Material.TALL_PLANTS ||
				material == Material.WEB;
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, BlockState state) {
		return canHarvestBlock(state) ? this.efficiency : super.getDestroySpeed(stack, state);
	}

	@Override
	public boolean hitEntity(@Nonnull ItemStack stack, LivingEntity target, LivingEntity attacker) {
		// deal special effects for some types
		if(this.getTier() == InspirationsTools.blaze) {
			target.setFire(5);
		} else if(this.getTier() == InspirationsTools.wither) {
			target.addPotionEffect(new EffectInstance(Effects.WITHER, 100));
		}
		return super.hitEntity(stack, target, attacker);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
		if(player.world.isRemote) {
			return false;
		}

		// moves the clicked entity towards the player
		Vec3d dir = player.getPositionVector().subtract(target.getPositionVector());
		target.addVelocity(dir.x*0.3, dir.y*0.3, dir.z*0.3);
		if(!player.isCreative()) {
			stack.damageItem(1, player, (play) -> play.sendBreakAnimation(hand));
		}

		// special effects for blaze and wither
		if(this.getTier() == InspirationsTools.blaze) {
			target.setFire(5);
		} else if(this.getTier() == InspirationsTools.wither) {
			target.addPotionEffect(new EffectInstance(Effects.WITHER, 100));
		}

		return true;
	}
}
