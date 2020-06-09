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
import net.minecraft.tags.Tag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IRegistryDelegate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

public abstract class BlockIngredient extends Ingredient {
	public final StatePropertiesPredicate predicate;

	protected BlockIngredient(StatePropertiesPredicate blockstateMatcher) {
		super(Stream.empty());
		this.predicate = blockstateMatcher;
	}

	protected abstract boolean matchesBlock(Block block);

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
		return BlockIngredientSerialiser.INSTANCE;
	}

	public static class BlockIngredientSerialiser implements IIngredientSerializer<BlockIngredient> {
		public static BlockIngredientSerialiser INSTANCE = new BlockIngredientSerialiser();

		@Nonnull
		@Override
		public BlockIngredient parse(@Nonnull JsonObject json) {
			StatePropertiesPredicate predicate = StatePropertiesPredicate.deserializeProperties(json);
			if (json.has("block") && json.has("tag")) {
				throw new JsonParseException("A Block Ingredient entry is either a tag or a block, not both");
			} else if (json.has("block")) {
				ResourceLocation blockName = new ResourceLocation(JSONUtils.getString(json, "block"));
				Block block = ForgeRegistries.BLOCKS.getValue(blockName);
				if (block == null) {
					throw new JsonSyntaxException("Unknown block '" + blockName + "'");
				} else {
					return new DirectBlockIngredient(block, predicate);
				}
			} else if (json.has("tag")) {
				ResourceLocation tagName = new ResourceLocation(JSONUtils.getString(json, "tag"));
				Tag<Block> tag = BlockTags.getCollection().get(tagName);
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
			StatePropertiesPredicate predicate = StatePropertiesPredicate.deserializeProperties(predicateData);
			boolean isTag = buffer.readBoolean();
			ResourceLocation loc = buffer.readResourceLocation();
			if (isTag) {
				return new TaggedBlockIngredient(loc, predicate);
			} else {
				// Direct block.
				return new DirectBlockIngredient(loc, predicate);
			}
		}

		@Override
		public void write(@Nonnull PacketBuffer buffer, @Nonnull BlockIngredient ingredient) {
			// This is ugly, but we'd otherwise need to mess with the internals to get out the data.
			JsonObject predicateData = new JsonObject();
			predicateData.add("properties", ingredient.predicate.toJsonElement());
			buffer.writeString(predicateData.toString());
			if (ingredient instanceof TaggedBlockIngredient) {
				buffer.writeBoolean(true);
				buffer.writeResourceLocation(((TaggedBlockIngredient) ingredient).tag.getId());
			} else if (ingredient instanceof DirectBlockIngredient) {
				buffer.writeBoolean(false);
				buffer.writeResourceLocation(((DirectBlockIngredient) ingredient).block.name());
			} else {
				throw new IllegalArgumentException("Unknown BlockIngredient " + ingredient.getClass().getName());
			}
		}
	}


	public static class DirectBlockIngredient extends BlockIngredient {
		public final IRegistryDelegate<Block> block;

		protected DirectBlockIngredient(Block block, StatePropertiesPredicate predicate) {
			super(predicate);
			this.block = block.delegate;
		}

		protected DirectBlockIngredient(ResourceLocation blockName, StatePropertiesPredicate predicate) {
			super(predicate);
			Block block = ForgeRegistries.BLOCKS.getValue(blockName);
			if (block == null) {
				throw new JsonSyntaxException("Unknown block '" + blockName + "'");
			}
			this.block = block.delegate;
		}

		@Override
		protected boolean matchesBlock(Block block) {
			return block.delegate.get() == this.block.get();
		}
	}

	public static class TaggedBlockIngredient extends BlockIngredient {
		public final Tag<Block> tag;

		protected TaggedBlockIngredient(Tag<Block> tag, StatePropertiesPredicate predicate) {
			super(predicate);
			this.tag = tag;
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
