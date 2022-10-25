package knightminer.inspirations.library;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.util.RegistryHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// This is an API.
public class InspirationsRegistry {
  public static final Logger log = LogManager.getLogger(Inspirations.modID + "-" + "api");

  /*
   * Books
   */
  private static final Map<Item,Float> bookCache = new HashMap<>();
  private static List<String> bookKeywords = new ArrayList<>();

  /**
   * Checks if the given item stack is a book
   * @param stack Input stack
   * @return True if its a book
   */
  public static boolean isBook(ItemStack stack) {
    return !stack.isEmpty() && getBookEnchantingPower(stack) >= 0;
  }

  /**
   * Checks if the given item stack is a book
   * @param book Input stack
   * @return True if its a book
   */
  public static float getBookEnchantingPower(ItemStack book) {
    if (book.isEmpty()) {
      return 0;
    }
    return bookCache.computeIfAbsent(book.getItem(), InspirationsRegistry::bookPower);
  }

  /**
   * Helper function to check if a stack is a book, used internally by the book map
   * @param item The item.
   * @return The enchantment power, or -1F.
   */
  private static Float bookPower(Item item) {
    if (RegistryHelper.contains(InspirationsTags.Items.BOOKS, item)) {
      return Config.defaultEnchantingPower.get().floatValue();
    }

    // blocks are not books, catches bookshelves
    if (Block.byItem(item) != Blocks.AIR) {
      return -1f;
    }

    // look through every keyword from the config
    // TODO: remove 1.17, switch to only tags
    for (String keyword : bookKeywords) {
      // if the unlocalized name or the registry name has the keyword, its a book
      if (Objects.requireNonNull(item.getRegistryName()).getPath().contains(keyword)
          || item.getDescriptionId().contains(keyword)) {
        return Config.defaultEnchantingPower.get().floatValue();
      }
    }
    return -1f;
  }

  /**
   * Internal function used to allow the config to set the list of book keywords. Should not need to be called outside of Inspirations.
   * TODO: move to JSON
   * @param keywords Keyword list
   */
  public static void setBookKeywords(List<String> keywords) {
    bookKeywords = keywords;
    // Clear the cache.
    bookCache.clear();
  }
}
