package knightminer.inspirations.library.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public abstract class BlockIngredient extends Ingredient {
	public final StatePropertiesPredicate predicate;

	protected BlockIngredient(StatePropertiesPredicate blockstateMatcher) {
		super(Stream.empty());
		this.predicate = blockstateMatcher;
	}

	protected abstract boolean matchesBlock(Block block);

	@Nonnull
	protected abstract List<Block> getMatchingBlocks();

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public boolean hasNoMatchingItems() {
		return false;
	}

	/**
	 * Dummy implementation, should not be used on normal recipes.
	 */
	@Nonnull
	@Override
	public ItemStack[] getMatchingStacks() {
		return new ItemStack[0];
	}


	/**
	 * Dummy implementation, should not be used on normal recipes.
	 */
	@Override
	public boolean test(@Nullable ItemStack testItem) {
		if (testItem == null) {
			return false;
		}
		Block block = Block.getBlockFromItem(testItem.getItem());
		return block != Blocks.AIR && testBlock(block.getDefaultState());
	}

	/**
	 * Check if the given state matches this ingredient.
	 * @param state The state to match.
	 * @return If it matched.
	 */
	public boolean testBlock(BlockState state) {
		if (!matchesBlock(state.getBlock())) {
			return false;
		}
		return predicate.matches(state);
	}

	@Nonnull
	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	public static IIngredientSerializer<BlockIngredient> SERIALIZER = new Serializer();

	private static class Serializer implements IIngredientSerializer<BlockIngredient> {

		@Nonnull
		@Override
		public BlockIngredient parse(@Nonnull JsonObject json) {
			JsonObject props = JSONUtils.getJsonObject(json, "properties", new JsonObject());
			StatePropertiesPredicate predicate = StatePropertiesPredicate.deserializeProperties(props);

			if (json.has("block") && json.has("tag")) {
				throw new JsonParseException("A Block Ingredient entry is either a tag or a block, not both");
			} else if (json.has("block")) {
				ResourceLocation blockName = new ResourceLocation(JSONUtils.getString(json, "block"));
				Block block = ForgeRegistries.BLOCKS.getValue(blockName);
				if (block == null) {
					throw new JsonSyntaxException("Unknown block '" + blockName + "'");
				} else {
					return new BlockIngredientList(block, predicate);
				}
			} else if (json.has("tag")) {
				ResourceLocation tagName = new ResourceLocation(JSONUtils.getString(json, "tag"));
				ITag<Block> tag = BlockTags.getCollection().get(tagName);
				if (tag == null) {
					throw new JsonSyntaxException("Unknown block tag '" + tagName + "'");
				} else {
					return new TaggedBlockIngredient(tag, predicate);
				}
			} else {
				throw new JsonParseException("An Block Ingredient entry needs either a tag or a block");
			}
		}

		@Nonnull
		@Override
		public BlockIngredient parse(@Nonnull PacketBuffer buffer) {
			JsonObject predicateData = JSONUtils.fromJson(buffer.readString(32768));
			StatePropertiesPredicate predicate = StatePropertiesPredicate.deserializeProperties(predicateData.getAsJsonObject(""));
			int size = buffer.readVarInt();
			List<Block> blocks = new ArrayList<>(size);
			for(int i = 0; i < size; i++) {
				blocks.add(buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCKS));
			}
			return new BlockIngredientList(blocks, predicate);
		}

		@Override
		public void write(@Nonnull PacketBuffer buffer, @Nonnull BlockIngredient ingredient) {
			// This is ugly, but we'd otherwise need to mess with the internals to get out the data.
			JsonObject predicateData = new JsonObject();
			predicateData.add("", ingredient.predicate.toJsonElement());
			buffer.writeString(predicateData.toString());
			List<Block> blocks = ingredient.getMatchingBlocks();
			buffer.writeVarInt(blocks.size());
			for(Block block: blocks) {
				buffer.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, block);
			}
		}
	}

	public static class BlockIngredientList extends BlockIngredient {
		public final List<Block> blocks;

		BlockIngredientList(List<Block> blocks, StatePropertiesPredicate predicate) {
			super(predicate);
			this.blocks = blocks;
		}

		BlockIngredientList(Block block, StatePropertiesPredicate predicate) {
			super(predicate);
			this.blocks = Collections.singletonList(block);
		}

		protected BlockIngredientList(ResourceLocation blockName, StatePropertiesPredicate predicate) {
			super(predicate);
			Block block = ForgeRegistries.BLOCKS.getValue(blockName);
			if (block == null) {
				throw new JsonSyntaxException("Unknown block '" + blockName + "'");
			}
			this.blocks = Collections.singletonList(block);
		}

		@Nonnull
		@Override
		protected List<Block> getMatchingBlocks() {
			return blocks;
		}

		@Override
		protected boolean matchesBlock(Block block) {
			return blocks.stream().anyMatch(block.delegate.get()::equals);
		}
	}

	public static class TaggedBlockIngredient extends BlockIngredient {
		public final ITag<Block> tag;

		protected TaggedBlockIngredient(ITag<Block> tag, StatePropertiesPredicate predicate) {
			super(predicate);
			this.tag = tag;
		}

		@Nonnull
		@Override
		protected List<Block> getMatchingBlocks() {
			return tag.getAllElements();
		}

		protected TaggedBlockIngredient(ResourceLocation tagName, StatePropertiesPredicate predicate) {
			super(predicate);
			this.tag = BlockTags.getCollection().get(tagName);
			if (this.tag == null) {
				throw new JsonSyntaxException("Unknown block tag '" + tagName + "'");
			}
		}

		@Override
		protected boolean matchesBlock(Block block) {
			return tag.contains(block);
		}
	}
}
