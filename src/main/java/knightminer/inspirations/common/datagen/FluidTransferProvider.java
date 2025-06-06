package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.library.InspirationsTags;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.mantle.datagen.MantleValues;
import slimeknights.mantle.fluid.transfer.AbstractFluidContainerTransferProvider;
import slimeknights.mantle.fluid.transfer.FillFluidContainerTransfer;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;

/** Handles fluid transfer in Mantle */
public class FluidTransferProvider extends AbstractFluidContainerTransferProvider {
  public FluidTransferProvider(PackOutput packOutput) {
    super(packOutput, Inspirations.modID);
  }

  @Override
  public String getName() {
    return "Inspirations fluid transfer provider";
  }

  @Override
  protected void addTransfers() {
    // we already do emptying all our fluid items through forge, just need to add logic to fill them as the relevant containers are not fluid handlers
    addFill("potato_soup", InspirationsCaudrons.potatoSoupItem, Items.BOWL,         InspirationsTags.Fluids.POTATO_SOUP, MantleValues.BOWL,   false);
    addFill("milk_bottle", InspirationsCaudrons.milkBottle,     Items.GLASS_BOTTLE, Tags.Fluids.MILK,                    MantleValues.BOTTLE, true);
  }

  /** Adds a recipe to fill the passed container with fluid */
  private void addFill(String name, ItemLike item, ItemLike container, TagKey<Fluid> fluid, int amount, boolean optional) {
    ICondition[] conditions = optional ? new ICondition[]{new TagFilledCondition<>(fluid)} : new ICondition[0];
    addTransfer(name, new FillFluidContainerTransfer(Ingredient.of(container), ItemOutput.fromItem(item), FluidIngredient.of(fluid, amount)), conditions);
  }
}
