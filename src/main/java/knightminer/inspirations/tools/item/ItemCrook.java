package knightminer.inspirations.tools.item;

import java.util.Collections;
import java.util.Set;

import knightminer.inspirations.tools.InspirationsTools;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

public class ItemCrook extends ItemTool {

	private static final Set<Block> EFFECTIVE_BLOCKS = Collections.emptySet();
	public ItemCrook(ToolMaterial material) {
		super(1.5F, -3.0F, material, EFFECTIVE_BLOCKS);
		this.setMaxDamage((int)(material.getMaxUses() * 1.5));
		this.setHarvestLevel("crook", material.getHarvestLevel());
	}

	/**
	 * Check whether this Item can harvest the given Block
	 */
	@Override
	public boolean canHarvestBlock(IBlockState block) {
		Material material = block.getMaterial();
		return material == Material.LEAVES || material == Material.VINE || material == Material.WEB;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state) {
		return canHarvestBlock(state) ? this.efficiency : super.getDestroySpeed(stack, state);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		// deal special effects for some types
		if(this.toolMaterial == InspirationsTools.blaze) {
			target.setFire(5);
		} else if(this.toolMaterial == InspirationsTools.wither) {
			target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 100));
		}
		return super.hitEntity(stack, target, attacker);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		if(player.world.isRemote) {
			return false;
		}

		// moves the clicked entity towards the player
		Vec3d dir = player.getPositionVector().subtract(target.getPositionVector());
		target.addVelocity(dir.x*0.3, dir.y*0.3, dir.z*0.3);
		if(!player.capabilities.isCreativeMode) {
			stack.damageItem(1, player);
		}

		// special effects for blaze and wither
		if(this.toolMaterial == InspirationsTools.blaze) {
			target.setFire(5);
		} else if(this.toolMaterial == InspirationsTools.wither) {
			target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 100));
		}

		return true;
	}
}
