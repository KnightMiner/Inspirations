package knightminer.inspirations.common.data;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.BeetrootBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;
import slimeknights.mantle.loot.ReplaceItemLootModifier;
import slimeknights.mantle.recipe.helper.ItemOutput;

public class GlobalLootProvider extends GlobalLootModifierProvider {
  public GlobalLootProvider(DataGenerator gen) {
    super(gen, Inspirations.modID);
  }

  @Override
  protected void start() {
    add("heartbeet", ReplaceItemLootModifier.builder(Ingredient.of(Items.BEETROOT), ItemOutput.fromItem(InspirationsTweaks.heartbeet))
        .addCondition(LootTableIdCondition.builder(new ResourceLocation("minecraft:blocks/beetroots")).build())
        .addCondition(ConfigEnabledCondition.HEARTBEETS)
        .addCondition(new LootItemBlockStatePropertyCondition.Builder(Blocks.BEETROOTS).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BeetrootBlock.AGE, 3)).build())
        .addCondition(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.05f, 0.0625f, 0.083333336f, 0.1f).build())
        .build());
  }
}
