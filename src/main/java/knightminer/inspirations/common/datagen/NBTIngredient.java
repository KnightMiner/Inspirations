package knightminer.inspirations.common.datagen;

import net.minecraft.world.item.ItemStack;

// extend to make the structure public
public class NBTIngredient extends net.minecraftforge.common.crafting.NBTIngredient {
  public NBTIngredient(ItemStack stack) {
    super(stack);
  }
}
