package knightminer.inspirations.recipes.recipe.anvil;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.util.RecipeUtil;
import knightminer.inspirations.recipes.recipe.anvil.conditions.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Builder class to construct an anvil smashing recipe. Any recipe can only have one
 */
public class AnvilSmashingItemRecipeBuilder {
    private ItemStack stackInput;

    private IBlockStateCondition blockInput;

    private ItemStack stackOutput;

    private IBlockState blockOutput;

    private boolean alwaysTransformBlock;

    private int minFallHeight;

    private int maxFallHeight;

    private AnvilSmashingItemRecipeBuilder(ItemStack stackInput) {
        this.stackInput = stackInput;
        this.blockInput = AnyBlockStateCondition.get();
        this.minFallHeight = 0;
        this.maxFallHeight = Integer.MAX_VALUE;
    }

    /**
     * Create a new builder instance that uses the given stack as the input.
     *
     * @param stackInput the input item stack, can't be null
     * @return a new builder instance
     */
    public static AnvilSmashingItemRecipeBuilder create(@Nonnull ItemStack stackInput) {
        return new AnvilSmashingItemRecipeBuilder(stackInput);
    }

    /**
     * Set the condition of the block. There can only be one condition and this will overwrite the previous one.
     *
     * @param condition the condition object
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder withBlockCondition(
            IBlockStateCondition condition) {
        this.blockInput = condition;
        return this;
    }

    /**
     * Require any of the specific blocks for the recipe to work.
     *
     * @param blockInput the allowed blocks
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder requireBlock(@Nonnull Block... blockInput) {
        IBlockState[] states = Arrays.stream(blockInput)
                .map(Block::getDefaultState)
                .toArray(IBlockState[]::new);
        return this
                .withBlockCondition(new ExactBlockStateCondition(states));
    }

    /**
     * Require any of the specific block states for the recipe to work.
     *
     * @param blockInput the allowed block states.
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder requireBlockState(@Nonnull IBlockState... blockInput) {
        return this
                .withBlockCondition(new ExactBlockStateCondition(blockInput));
    }

    /**
     * Require a certain material for the recipe to work.
     *
     * @param materialName the name of the material. Must be the name of a vanilla material.
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder requireMaterial(@Nonnull String materialName) {
        MaterialBlockStateCondition.MaterialWrapper materialWrapper = MaterialBlockStateCondition.MaterialWrapper.fromName(
                materialName);
        Objects.requireNonNull(materialWrapper);
        return this.requireMaterial(materialWrapper);
    }

    /**
     * Require a certain material for the recipe to work. This is meant to be used when non vanilla materials are used.
     *
     * @param material the instance of the material
     * @param key the translation key to determine a human readable representation of the material since they don't have one by default.
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder requireMaterial(@Nonnull Material material, @Nonnull String key) {
        return this.requireMaterial(new MaterialBlockStateCondition.MaterialWrapper(material, key));
    }

    /**
     * Require a certain material for the recipe to work. Use one of the other variants of this method since this is
     * mostly used internally.
     *
     * @param material a wrapper that holds the information for the material
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder requireMaterial(@Nonnull MaterialBlockStateCondition.MaterialWrapper material) {
        return this.withBlockCondition(new MaterialBlockStateCondition(material));
    }

    /**
     * Require any of the given materials. Those materials should only be vanilla ones!
     *
     * @param material the valid materials for the recipe to work
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder requireMaterial(@Nonnull Material... material) {
        MaterialBlockStateCondition.MaterialWrapper[] materialWrappers = Arrays.stream(material)
                .map(MaterialBlockStateCondition.MaterialWrapper::fromMaterial)
                .toArray(MaterialBlockStateCondition.MaterialWrapper[]::new);
        return this
                .withBlockCondition(new MaterialBlockStateCondition(materialWrappers));
    }

    /**
     * Require the block the anvil lands on to be registered under the given ore dictionary name.
     *
     * @param oreDictName the name of the ore dictionary entry
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder requireOreDict(@Nonnull String oreDictName) {
        return this.withBlockCondition(new OreDictBlockStateCondition(oreDictName));
    }

    /**
     * Require the block to have a hardness between the given values (inclusively)
     *
     * @param min the minimum block hardness
     * @param max the maximum block hardness
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder requireHardness(float min, float max) {
        return this
                .withBlockCondition(new HardnessBlockStateCondition(min, max));
    }

    /**
     * Require the block to have a block hardness greater than or equal to the given value.
     *
     * @param min the minimum block hardness
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder requireMiniumHardness(float min) {
        return this.requireHardness(min, -1);
    }

    /**
     * Require the block to have a block hardness less than or equal to the given value.
     *
     * @param max the maximum block hardness
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder requireMaximumHardness(float max) {
        return this.requireHardness(0, max);
    }

    /**
     * Require the block to have a harvest level between the given values (inclusively).
     *
     * @param min the minimum harvest level of the block
     * @param max the maximum harvest level of the block
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder requireHarvestLevel(int min, int max) {
        return this.withBlockCondition(
                new HarvestLevelBlockStateCondition(min, max));
    }

    /**
     * Require the block to have a harvest level greater than or equal to the given value.
     *
     * @param min the minimum harvest level of the block
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder requireMinimumHarvestLevel(int min) {
        return this.requireHarvestLevel(min, Integer.MAX_VALUE);
    }

    /**
     * Require the block to have a harvest level less than or equal to the given value.
     *
     * @param max the maximum harvest level of the block
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder requireMaximumHarvestLevel(int max) {
        return this.requireHarvestLevel(0, max);
    }

    /**
     * Require the block to be harvestable by the given tool class.
     *
     * @param tool the tool class
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder requireTool(@Nonnull String... tool) {
        return this.withBlockCondition(new HarvestToolBlockStateCondition(tool));
    }

    /**
     * Specify the output item stack of the recipe.
     *
     * @param stackOutput the output for the recipe
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder setResult(ItemStack stackOutput) {
        this.stackOutput = stackOutput;
        return this;
    }

    /**
     * Specifiy the output block state of the block the anvil landed on.
     *
     * @param blockOutput the output block state
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder setBlockResult(IBlockState blockOutput) {
        this.blockOutput = blockOutput;
        return this;
    }

    /**
     * Make the block the anvil landed on be destroyed.
     *
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder destroyBlock() {
        return this.setBlockResult(Blocks.AIR.getDefaultState());
    }

    /**
     * Force the block state transformation even if the minimum input stack size for the recipe wasn't met.
     *
     * @param alwaysTransformBlock if the block state transformation should always happen
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder alwaysTransformBlock(boolean alwaysTransformBlock) {
        this.alwaysTransformBlock = alwaysTransformBlock;
        return this;
    }

    /**
     * Require the anvil to fall at least this amount of block.
     *
     * @param minFallHeight the minimum height the anvil must fall
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder requireMinimumFallHeight(int minFallHeight) {
        this.minFallHeight = minFallHeight;
        return this;
    }

    /**
     * The anvil can only fall this amount of blocks at most.
     *
     * @param maxFallHeight the maximum height the anvil is allowed to fall
     * @return the builder object
     */
    public AnvilSmashingItemRecipeBuilder requireMaximumFallHeight(int maxFallHeight) {
        this.maxFallHeight = maxFallHeight;
        return this;
    }

    /**
     * Create a new recipe from the given inputs.
     *
     * @return a new recipe
     */
    public AnvilSmashingItemRecipe build() {
        return new AnvilSmashingItemRecipe(stackInput, blockInput, stackOutput, blockOutput,
                alwaysTransformBlock,
                minFallHeight, maxFallHeight);
    }

    /**
     * Create the recipe and also register it.
     */
    public void register() {
        InspirationsRegistry.registerAnvilSmashingItem(this.build());
    }

    /**
     * Process and register a recipe from a recipe string
     *
     * @param transformation the recipe string
     */
    public static void processAnvilSmashingItemsEntry(String transformation) {
        // skip blank lines and comments
        if (StringUtils.trimToNull(transformation) == null || transformation.startsWith("#")) {
            return;
        }

        String[] transformParts = transformation.split("->");
        int parts = StringUtils.countMatches(transformation, "->");
        if (parts > 2 || parts < 1) {
            Inspirations.log.error("Invalid anvil item smashing {}: must be in the format of modid:input-item[:meta][*count],[blockcondition][->[minheight][,maxheight]]->[modid:output-item[:meta][*count]][,modid:output-block[:meta]]", transformation);
            return;
        }

        // Input
        String[] input = transformParts[0].split(";");
        if (input.length > 2 || input.length < 1) {
            return;
        }

        // Input Stack is required
        ItemStack inputStack = getItemStackWithCountFromString(input[0]);
        if (inputStack == null) {
            return;
        }
        AnvilSmashingItemRecipeBuilder recipe =
                InspirationsRegistry.createAnvilSmashingItemRecipe(inputStack);

        // Block Condition
        if (input.length == 2 && !parseBlockCondition(input[1], recipe)) {
            return;
        }

        // Fall Height
        int offset = 0;
        if (parts > 1) {
            offset = 1;
            if (!parseHeight(transformParts[1], recipe)) {
                return;
            }
        }

        // Output
        if (transformParts.length > 1 + offset) {
            if (!parseOutput(transformParts[1 + offset], recipe)) {
                return;
            }
        }

        recipe.register();
    }

    /**
     * Create the outputs of the recipe.
     *
     * @param outputString the string containing the output part of the recipe
     * @param recipe       the builder for the recipe
     * @return whether the output was parsed successfully
     */
    private static boolean parseOutput(String outputString, AnvilSmashingItemRecipeBuilder recipe) {
        String[] outputParts = outputString.split(";");
        if (outputParts.length > 2) {
            return false;
        }

        // Output Itemstack
        int idx = 0;
        if (outputParts.length == 2 || !outputString.contains(";")) {
            ItemStack outputStack = getItemStackWithCountFromString(outputParts[idx++]);
            if (outputStack == null) {
                return false;
            }
            recipe.setResult(outputStack);
        }

        // Output Block
        if (outputParts.length > idx) {
            IBlockState state = getBlockStateFromString(outputParts[idx]);
            if (state == null) {
                return false;
            }
            recipe.setBlockResult(state);
        }
        return true;
    }

    /**
     * Parse and set the height requirements for the recipe.
     *
     * @param transformParts the part of the recipe that should be parsed
     * @param recipe         the builder for the recipe
     * @return whether the height was parsed successfully
     */
    private static boolean parseHeight(String transformParts, AnvilSmashingItemRecipeBuilder recipe) {
        // minimum one, maximum 2 parts
        String[] heightParts = transformParts.split(",");
        if (heightParts.length < 1 || heightParts.length > 2) {
            return false;
        }

        // minimum
        int idx = 0;
        if (heightParts.length == 2 || !transformParts.contains(",")) {
            int minHeight = parseInteger(heightParts[idx++]);
            if (minHeight < 0) {
                return false;
            }
            recipe.requireMinimumFallHeight(minHeight);
        }

        // maximum
        if (heightParts.length > idx) {
            int maxHeight = parseInteger(heightParts[idx]);
            if (maxHeight < 0) {
                return false;
            }
            recipe.requireMaximumFallHeight(maxHeight);
        }

        return true;
    }

    /**
     * Parse the block condition for the recipe.
     *
     * @param input  the part of the recipe string that defines the condition for the block the anvil lands on
     * @param recipe the builder for the recipe
     * @return whether the condition was parsed successfully
     */
    private static boolean parseBlockCondition(String input, AnvilSmashingItemRecipeBuilder recipe) {
        if (input.startsWith("#")) {
            // condition
            String[] conditionParts = input.split(":");
            if (conditionParts.length != 2) {
                return false;
            }

            switch (conditionParts[0]) {
                case "#ore": {
                    recipe.withBlockCondition(OreDictBlockStateCondition.fromConfig(conditionParts[1]));
                    break;
                }
                case "#material": {
                    recipe.withBlockCondition(MaterialBlockStateCondition.fromConfig(conditionParts[1]));
                    break;
                }
                case "#tool": {
                    recipe.withBlockCondition(HarvestToolBlockStateCondition.fromConfig(conditionParts[1]));
                    break;
                }
                case "#hard": {
                    HardnessBlockStateCondition condition = HardnessBlockStateCondition.fromConfig(conditionParts[1]);
                    if (condition == null) {
                        return false;
                    }
                    recipe.withBlockCondition(condition);
                    break;
                }
                case "#level": {
                    HarvestLevelBlockStateCondition condition = HarvestLevelBlockStateCondition.fromConfig(conditionParts[1]);
                    if (condition == null) {
                        return false;
                    }
                    recipe.withBlockCondition(condition);
                    break;
                }
                default:
                    return false;
            }
        } else {
            // block state
            IBlockState inputState = getBlockStateFromString(input);
            if (inputState == null) {
                return false;
            }
            recipe.requireBlockState(inputState);
        }

        return true;
    }

    /**
     * Get the block state from the string
     *
     * @param input the string
     * @return the block state or null if the string didn't lead to a valid block state
     */
    public static @Nullable IBlockState getBlockStateFromString(String input) {
        String[] split = input.split(":");
        if (split.length < 2 || split.length > 3) {
            return null;
        }
        Block block = GameRegistry.findRegistry(Block.class).getValue(new ResourceLocation(split[0], split[1]));
        if (block == null) {
            return null;
        }

        int meta = split.length == 3 ? parseInteger(split[2]) : 0;
        if (meta < 0) {
            return null;
        }
        return block.getStateFromMeta(meta);
    }

    /**
     * Get an item stack from the input string that also contains an item count.
     *
     * @param input the string
     * @return the item stack or null if the string doesn't represent a valid item stack
     */
    public static @Nullable ItemStack getItemStackWithCountFromString(String input) {
        // either with or without the item count
        String[] inputStackSplit = input.split("\\*");
        if (inputStackSplit.length > 2 || inputStackSplit.length < 1) {
            return null;
        }

        // no wildcard matches, empty stack means not a valid string
        ItemStack stack = RecipeUtil.getItemStackFromString(inputStackSplit[0], false);
        if (stack.isEmpty()) {
            return null;
        }

        // if the item count was specified, set it for the item stack
        if (inputStackSplit.length > 1) {
            int count = parseInteger(inputStackSplit[1]);
            if (count < 0) {
                return null;
            }
            stack.setCount(count);
        }

        return stack;
    }

    /**
     * Parse the string as a float and return -1 if the string was invalid.
     *
     * @param input the string to parse
     * @return the float value of the string or -1 if the format is wrong
     */
    public static float parseFloat(String input) {
        try {
            return Float.parseFloat(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Parse the string as an integer and return -1 if the string was invalid.
     *
     * @param input the string to parse
     * @return the integer value of the string or -1 if the format is wrong
     */
    public static int parseInteger(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}
