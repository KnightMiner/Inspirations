package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import knightminer.inspirations.cauldrons.data.VanillaEnum;
import knightminer.inspirations.common.InspirationsCommons;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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
  protected void addTags() {
    registerInspTags();
    registerForgeTags();
    registerVanillaTags();
  }

  private void registerInspTags() {
    this.copy(InspirationsTags.Blocks.MULCH, InspirationsTags.Items.MULCH);
    this.copy(BlockTags.SMALL_FLOWERS, ItemTags.SMALL_FLOWERS);
    this.copy(InspirationsTags.Blocks.CARPETED_TRAPDOORS, InspirationsTags.Items.CARPETED_TRAPDOORS);
    this.copy(InspirationsTags.Blocks.SHELVES, InspirationsTags.Items.BOOKSHELVES);
    this.copy(InspirationsTags.Blocks.ENLIGHTENED_BUSHES, InspirationsTags.Items.ENLIGHTENED_BUSHES);
    this.tag(ItemTags.DOORS).add(InspirationsBuilding.glassDoorItem);
    this.copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);

    TagAppender<Item> bookBuilder = this.tag(InspirationsTags.Items.FORGE_BOOKS)
        .add(InspirationsBuilding.redstoneBook)
        .add(Items.BOOK, Items.WRITABLE_BOOK, Items.WRITTEN_BOOK)
        .add(Items.ENCHANTED_BOOK, Items.KNOWLEDGE_BOOK);
    InspirationsBuilding.coloredBooks.values().forEach(bookBuilder::add);
    this.tag(InspirationsTags.Items.BOOKS).addTag(InspirationsTags.Items.FORGE_BOOKS);

    // item list of all relevant carpets
    TagAppender<Item> carpetBuilder = this.tag(InspirationsTags.Items.CARPETS);
    InspirationsCommons.VANILLA_CARPETS.forEach(block -> carpetBuilder.add(block.asItem()));

    // item list of all relevant shulker boxes
    TagAppender<Item> shulkerBoxBuilder = this.tag(InspirationsTags.Items.SHULKER_BOXES);
    shulkerBoxBuilder.add(Items.SHULKER_BOX);
    VanillaEnum.SHULKER_BOX.forEach(block -> shulkerBoxBuilder.add(block.asItem()));

    // relevant terracotta
    TagAppender<Item> terracottaBuilder = this.tag(InspirationsTags.Items.TERRACOTTA);
    terracottaBuilder.add(Items.TERRACOTTA);
    VanillaEnum.TERRACOTTA.forEach(block -> terracottaBuilder.add(block.asItem()));

    this.tag(InspirationsTags.Items.MILK_CONTAINERS).add(Items.BUCKET, Items.GLASS_BOTTLE, Items.BOWL)
        .addOptional(new ResourceLocation("ceramics:clay_bucket"))
        .addOptional(new ResourceLocation("simplytea:teapot"));
  }

  private void registerForgeTags() {
    this.tag(Tags.Items.BOOKSHELVES).addTag(InspirationsTags.Items.BOOKSHELVES);

    this.tag(InspirationsTags.Items.SPLASH_BOTTLES).add(InspirationsCaudrons.splashBottle);
    this.tag(InspirationsTags.Items.LINGERING_BOTTLES).add(InspirationsCaudrons.lingeringBottle);

    // add dyed bottles to dye tag, forge tag are always INamedTag
    InspirationsCaudrons.simpleDyedWaterBottle.forEach((color, bottle) -> this.tag(color.getTag()).add(bottle));
  }

  private void registerVanillaTags() {
    this.tag(ItemTags.ARROWS).add(InspirationsTools.redstoneArrow);
    this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
    this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
    // Not copy(), we don't want to include the wall torch.
    this.tag(ItemTags.PIGLIN_REPELLENTS).add(InspirationsUtility.soulLeverItem);
    this.tag(ItemTags.PIGLIN_LOVED).add(InspirationsTools.redstoneCharger);
  }
}
