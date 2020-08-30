package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.library.InspirationsTags;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.data.VanillaEnum;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.tools.InspirationsTools;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import slimeknights.mantle.registration.object.EnumObject;

public class InspirationsItemTagsProvider extends ItemTagsProvider {

  public InspirationsItemTagsProvider(DataGenerator gen, BlockTagsProvider blocks) {
    super(gen, blocks);
    this.modId = Inspirations.modID;
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
    this.copy(InspirationsTags.Blocks.BOOKSHELVES, InspirationsTags.Items.BOOKSHELVES);
    this.copy(InspirationsTags.Blocks.ENLIGHTENED_BUSHES, InspirationsTags.Items.ENLIGHTENED_BUSHES);

    this.getOrCreateBuilder(InspirationsTags.Items.BOOKS)
        .add(toArray(InspirationsBuilding.coloredBooks))
        .add(InspirationsBuilding.redstoneBook)
        .add(Items.BOOK, Items.WRITABLE_BOOK, Items.WRITTEN_BOOK)
        .add(Items.ENCHANTED_BOOK, Items.KNOWLEDGE_BOOK);

    // item list of all relevant carpets
    TagsProvider.Builder<Item> carpetBuilder = this.getOrCreateBuilder(InspirationsTags.Items.CARPETS);
    InspirationsShared.VANILLA_CARPETS.forEach(block -> carpetBuilder.add(block.asItem()));

    // item list of all relevant shulker boxes
    TagsProvider.Builder<Item> shulkerBoxBuilder = this.getOrCreateBuilder(InspirationsTags.Items.SHULKER_BOXES);
    shulkerBoxBuilder.add(Items.SHULKER_BOX);
    VanillaEnum.SHULKER_BOX.forEach(block -> shulkerBoxBuilder.add(block.asItem()));

    // relevant terracotta
    TagsProvider.Builder<Item> terracottaBuilder = this.getOrCreateBuilder(InspirationsTags.Items.TERRACOTTA);
    terracottaBuilder.add(Items.TERRACOTTA);
    VanillaEnum.TERRACOTTA.forEach(block -> terracottaBuilder.add(block.asItem()));

    // compasses
    this.getOrCreateBuilder(InspirationsTags.Items.WAYPOINT_COMPASSES).add(InspirationsTools.waypointCompasses);

  }

  private void registerForgeTags() {
    this.getOrCreateBuilder(Tags.Items.BOOKSHELVES).addTag(InspirationsTags.Items.BOOKSHELVES);

    this.getOrCreateBuilder(InspirationsTags.Items.SPLASH_BOTTLES).add(InspirationsRecipes.splashBottle);
    this.getOrCreateBuilder(InspirationsTags.Items.LINGERING_BOTTLES).add(InspirationsRecipes.lingeringBottle);

    // add dyed bottles to dye tag, forge tag are always INamedTag
    InspirationsRecipes.simpleDyedWaterBottle.forEach((color, bottle) -> this.getOrCreateBuilder((INamedTag<Item>)color.getTag()).add(bottle));

		/*
		for(DyeColor color : DyeColor.values()) {
			INamedTag<Item> tag = ItemTags.makeWrapperTag("forge:dyes/" + color.getString());
			this.func_240522_a_(tag).func_240534_a_(InspirationsRecipes.simpleDyedWaterBottle.get(color));
		}
		this.func_240522_a_(Tags.Items.DYES).func_240534_a_(InspirationsRecipes.simpleDyedWaterBottle.values().toArray(new Item[0]));
		 */
  }

  private void registerVanillaTags() {
    this.getOrCreateBuilder(ItemTags.ARROWS).add(InspirationsTools.redstoneArrow);
    this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
    this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
  }

  /**
   * Converts an enum object into an array of values
   * @param object Enum object
   * @return Array of enum object values
   */
  private static Item[] toArray(EnumObject<?,? extends Item> object) {
    return object.values().toArray(new Item[0]);
  }
}
