package knightminer.inspirations.library;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.util.RegistryHelper;

import java.util.HashMap;
import java.util.Map;

// This is an API.
public class InspirationsRegistry {
  public static final Logger log = LogManager.getLogger(Inspirations.modID + "-" + "api");

  /*
   * Books
   */
  private static final Map<Item,Float> bookCache = new HashMap<>();

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
    // TODO: custom enchanting power
    if (RegistryHelper.contains(InspirationsTags.Items.BOOKS, item)) {
      return Config.defaultEnchantingPower.get().floatValue();
    }
    return -1f;
  }
}
