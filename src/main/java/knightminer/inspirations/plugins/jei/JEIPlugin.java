package knightminer.inspirations.plugins.jei;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.util.RetexturedHelper;

import java.util.function.Consumer;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
  @Override
  public ResourceLocation getPluginUid() {
    return Inspirations.getResource("jei");
  }

  @Override
  public void registerItemSubtypes(ISubtypeRegistration registry) {
    IIngredientSubtypeInterpreter<ItemStack> texture = (ingredient, context) -> RetexturedHelper.getTextureName(ingredient);
    Consumer<ItemLike> setTextureSubtype = item -> registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, item.asItem(), texture);

    // building
    InspirationsBuilding.shelf.values().forEach(setTextureSubtype);
    InspirationsBuilding.enlightenedBush.values().forEach(setTextureSubtype);
    registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, InspirationsBuilding.coloredBook, (stack, context) -> context == UidContext.Ingredient ? Integer.toHexString(((DyeableLeatherItem)stack.getItem()).getColor(stack)) : "");
  }
}
