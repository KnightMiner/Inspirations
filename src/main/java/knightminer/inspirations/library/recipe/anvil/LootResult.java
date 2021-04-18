package knightminer.inspirations.library.recipe.anvil;

import knightminer.inspirations.Inspirations;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.BinomialRange;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.EmptyLootEntry;
import net.minecraft.loot.IRandomRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.ParentedLootEntry;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.TagLootEntry;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.loot.functions.SetDamage;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Walk through loot table pools to produce a list of potential items.
 */
public class LootResult {
  public static final String TRANSLATION = "inspirations.lootpool.";

  private final ItemStack item;
  private final List<ITextComponent> tooltips;

  // Can't use ATs, have to reflect.
  private static final Field ENTRIES = ObfuscationReflectionHelper.findField(LootPool.class, "field_186453_a");
  private static final Field CONDITIONS = ObfuscationReflectionHelper.findField(LootPool.class, "field_186454_b");

  public LootResult(ItemStack item) {
    this.item = item;
    tooltips = new ArrayList<>();
  }

  public LootResult(Item item) {
    this(new ItemStack(item));
  }

  public ItemStack getStack() {
    return item;
  }

  public List<ITextComponent> getTooltips() {
    return tooltips;
  }

  public static List<LootResult> computePoolItems(List<LootPool> pools) {
    return pools.stream()
            .flatMap(LootResult::getItems)
            .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private static Stream<LootResult> getItems(LootPool pool) {
    List<LootEntry> entries;
    List<ILootCondition> conditions;
    List<ILootFunction> functions = Arrays.asList(pool.functions);
    try {
      entries = (List<LootEntry>) ENTRIES.get(pool);
      conditions = (List<ILootCondition>) CONDITIONS.get(pool);
    } catch(IllegalAccessException e) {
      return Stream.empty();
    }
    return entries.stream().flatMap(LootResult::parseEntry).map((res) -> applyFunctions(functions, res));
  }

  private static Stream<LootResult> parseEntry(LootEntry entry) {
    if (entry instanceof ItemLootEntry) {
      return Stream.of(applyFunctions(
              Arrays.asList(((ItemLootEntry) entry).functions),
              new LootResult(((ItemLootEntry) entry).item)
      ));
    } else if (entry instanceof TagLootEntry) {
      List<ILootFunction> funcs = Arrays.asList(((TagLootEntry) entry).functions);
      return ((TagLootEntry) entry).tag
              .getAllElements()
              .stream()
              .map(item -> applyFunctions(funcs, new LootResult(item)));
    } else if (entry instanceof EmptyLootEntry) {
      return Stream.empty();
    } else if (entry instanceof ParentedLootEntry) {
      return Arrays.stream(((ParentedLootEntry) entry).entries).flatMap(LootResult::parseEntry);
    } else {
      Inspirations.log.warn(String.format("Unknown loot entry %s!", entry.getClass().getName()));
      return Stream.empty();
    }
  }

  private static LootResult applyFunctions(List<ILootFunction> functions, LootResult res) {
    for (ILootFunction func: functions) {
      if (func instanceof SetCount) {
        IRandomRange range = ((SetCount) func).countRange;
        if (range instanceof ConstantRange) {
          res.getStack().setCount(range.generateInt(new Random()));
        } else if (range instanceof BinomialRange) {
          res.getTooltips().add(new TranslationTextComponent(
                  TRANSLATION + "count.binomial",
                  ((BinomialRange) range).p,
                  ((BinomialRange) range).n
          ));
        } else if (range instanceof RandomValueRange) {
          res.getStack().setCount(MathHelper.floor(((RandomValueRange) range).getMin()));
          res.getTooltips().add(new TranslationTextComponent(
                  TRANSLATION + "count.uniform",
                  MathHelper.floor(((RandomValueRange) range).getMin()),
                  MathHelper.floor(((RandomValueRange) range).getMax())
          ));
        }
      } else if (func instanceof SetDamage) {
        res.getTooltips().add(new TranslationTextComponent(
                TRANSLATION + "damage",
                MathHelper.floor(((SetDamage) func).damageRange.getMin()),
                MathHelper.floor(((SetDamage) func).damageRange.getMax()),
                res.getStack().getMaxDamage()
        ));
      } else {
        Inspirations.log.warn(String.format("Unknown loot function %s!", func.getFunctionType().getClass().getName()));
      }
    }
    return res;
  }
}
