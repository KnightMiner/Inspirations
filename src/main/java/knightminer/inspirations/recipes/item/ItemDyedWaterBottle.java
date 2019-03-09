package knightminer.inspirations.recipes.item;

import knightminer.inspirations.library.Util;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemDyedWaterBottle extends Item {

	public static final String TAG_COLOR = "color";

	public ItemDyedWaterBottle() {
		this.setCreativeTab(CreativeTabs.MATERIALS);
		this.setMaxStackSize(16);
		this.setContainerItem(Items.GLASS_BOTTLE);
		this.setHasSubtypes(true);
	}

	/* Color logic */
	/**
	 * Return the color for the specified armor ItemStack.
	 */
	public int getColor(ItemStack stack) {
		int meta = stack.getMetadata();
		if(meta < 16) {
			return EnumDyeColor.byDyeDamage(meta).colorValue;
		}

		NBTTagCompound tags = stack.getTagCompound();

		if (tags != null) {
			NBTTagCompound display = tags.getCompoundTag("display");
			if(display != null && display.hasKey(TAG_COLOR, 3)) {
				return display.getInteger(TAG_COLOR);
			}
		}

		return -1;
	}

	public ItemStack getStackWithColor(int color) {
		EnumDyeColor dyeColor = Util.getDyeForColor(color);
		if(dyeColor != null) {
			return new ItemStack(this, 1, dyeColor.getDyeDamage());
		}

		ItemStack result = new ItemStack(this, 1, 16);
		NBTTagCompound tags = new NBTTagCompound();
		NBTTagCompound display = new NBTTagCompound();
		display.setInteger(TAG_COLOR, color);
		tags.setTag("display", display);
		result.setTagCompound(tags);
		return result;
	}


	/* Color types */

	/**
	 * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
	 * different names based on their damage or NBT.
	 */
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getMetadata();
		if(meta < 16) {
			return super.getUnlocalizedName(stack) + "." + EnumDyeColor.byDyeDamage(meta).getName();
		}

		return super.getUnlocalizedName(stack);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if(this.isInCreativeTab(tab)) {
			for(EnumDyeColor color : EnumDyeColor.values()) {
				subItems.add(new ItemStack(this, 1, color.getDyeDamage()));
			}
		}
	}

	/** Dye sheep on right click with a bottle */
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		int meta = stack.getMetadata();
		if (meta <= 15 && target instanceof EntitySheep) {
			EntitySheep sheep = (EntitySheep)target;
			EnumDyeColor color = EnumDyeColor.byDyeDamage(meta);
			if (!sheep.getSheared() && sheep.getFleeceColor() != color) {
				sheep.setFleeceColor(color);
				player.playSound(SoundEvents.ITEM_BOTTLE_EMPTY, 1.0F, 1.0F);

				// give back bottle;
				ItemStack bottle = new ItemStack(getContainerItem());
				if (stack.getCount() == 1) {
					player.setHeldItem(hand, bottle);
				} else {
					stack.shrink(1);
					ItemHandlerHelper.giveItemToPlayer(player, bottle);
				}
			}
			return true;
		}
		return false;
	}
}
