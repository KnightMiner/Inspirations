package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.data.VanillaEnum;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class InspirationsItemTagsProvider extends ItemTagsProvider {

  public InspirationsItemTagsProvider(DataGenerator gen, ExistingFileHelper existing, BlockTagsProvider blocks) {
    super(gen, blocks, Inspirations.modID, existing);
  }

  @Override
  public String getName() {
    return "Inspirations Item Tags";
  }

  @Override
  protected void registerTags() {
    registerInspTags();
    registerForgeTags();
    registerVanillaTags();
  }

  private void registerInspTags() {
    this.copy(InspirationsTags.Blocks.MULCH, InspirationsTags.Items.MULCH);
    this.copy(InspirationsTags.Blocks.SMALL_FLOWERS, InspirationsTags.Items.SMALL_FLOWERS);
    this.copy(InspirationsTags.Blocks.CARPETED_TRAPDOORS, InspirationsTags.Items.CARPETED_TRAPDOORS);
    this.copy(InspirationsTags.Blocks.SHELVES, InspirationsTags.Items.BOOKSHELVES);
    this.copy(InspirationsTags.Blocks.ENLIGHTENED_BUSHES, InspirationsTags.Items.ENLIGHTENED_BUSHES);
    this.getOrCreateBuilder(ItemTags.DOORS).add(InspirationsBuilding.glassDoorItem);
    this.copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);

    Builder<Item> bookBuilder = this.getOrCreateBuilder(InspirationsTags.Items.BOOKS)
        .add(InspirationsBuilding.redstoneBook)
        .add(Items.BOOK, Items.WRITABLE_BOOK, Items.WRITTEN_BOOK)
        .add(Items.ENCHANTED_BOOK, Items.KNOWLEDGE_BOOK);
    InspirationsBuilding.coloredBooks.values().forEach(bookBuilder::add);

    // item list of all relevant carpets
    Builder<Item> carpetBuilder = this.getOrCreateBuilder(InspirationsTags.Items.CARPETS);
    InspirationsShared.VANILLA_CARPETS.forEach(block -> carpetBuilder.add(block.asItem()));

    // item list of all relevant shulker boxes
    Builder<Item> shulkerBoxBuilder = this.getOrCreateBuilder(InspirationsTags.Items.SHULKER_BOXES);
    shulkerBoxBuilder.add(Items.SHULKER_BOX);
    VanillaEnum.SHULKER_BOX.forEach(block -> shulkerBoxBuilder.add(block.asItem()));

    // relevant terracotta
    Builder<Item> terracottaBuilder = this.getOrCreateBuilder(InspirationsTags.Items.TERRACOTTA);
    terracottaBuilder.add(Items.TERRACOTTA);
    VanillaEnum.TERRACOTTA.forEach(block -> terracottaBuilder.add(block.asItem()));

    // compasses
    Builder<Item> waypointCompassBuilder = this.getOrCreateBuilder(InspirationsTags.Items.WAYPOINT_COMPASSES);
    InspirationsTools.waypointCompasses.values().forEach(waypointCompassBuilder::add);
  }

  private void registerForgeTags() {
    this.getOrCreateBuilder(Tags.Items.BOOKSHELVES).addTag(InspirationsTags.Items.BOOKSHELVES);

    this.getOrCreateBuilder(InspirationsTags.Items.SPLASH_BOTTLES).add(InspirationsRecipes.splashBottle);
    this.getOrCreateBuilder(InspirationsTags.Items.LINGERING_BOTTLES).add(InspirationsRecipes.lingeringBottle);

    // add dyed bottles to dye tag, forge tag are always INamedTag
    InspirationsRecipes.simpleDyedWaterBottle.forEach((color, bottle) -> this.getOrCreateBuilder(color.getTag()).add(bottle));
  }

  private void registerVanillaTags() {
    this.getOrCreateBuilder(ItemTags.ARROWS).add(InspirationsTools.redstoneArrow);
    this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
    this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
    // Not copy(), we don't want to include the wall torch.
    this.getOrCreateBuilder(ItemTags.PIGLIN_REPELLENTS).add(InspirationsUtility.soulLeverItem);
    this.getOrCreateBuilder(ItemTags.PIGLIN_LOVED).add(InspirationsTools.redstoneCharger);
  }
}
