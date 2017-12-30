package knightminer.inspirations.building.client;

import java.util.List;

import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import knightminer.inspirations.building.block.BlockBookshelf;
import knightminer.inspirations.library.util.RecipeUtil;
import knightminer.inspirations.library.util.TagUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import slimeknights.mantle.client.ModelHelper;

public class BookshelfModel extends BakedModelWrapper<IBakedModel> {

	private IModel model;
	private final VertexFormat format;
	public BookshelfModel(IBakedModel standard, IModel model, VertexFormat format) {
		super(standard);
		this.model = model;
		this.format = format;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		IBakedModel bakedModel = this.originalModel;
		if(state instanceof IExtendedBlockState) {
			IExtendedBlockState extendedState = (IExtendedBlockState) state;
			ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

			for(int i = 0; i < 14; i++) {
				// if there is no book in the slot, remove the texture so the quad is removed
				if(extendedState.getValue(BlockBookshelf.BOOKS[i]) != Boolean.TRUE) {
					builder.put("#book" + i, "");
					builder.put("#bookLabel" + i, "");
				}
			}

			String texture = extendedState.getValue(BlockBookshelf.TEXTURE);
			if(texture != null) {
				builder.put("texture", texture);
			}

			bakedModel = getTexturedModel(builder.build());
		}
		return bakedModel.getQuads(state, side, rand);
	}

	private IBakedModel getTexturedModel(ImmutableMap<String, String> textures) {
		IModel retextured = model.retexture(textures);
		return retextured.bake(retextured.getDefaultState(), format, ModelLoader.defaultTextureGetter());
	}

	@Nonnull
	@Override
	public ItemOverrideList getOverrides() {
		return BookshelfTextureOverride.INSTANCE;
	}

	private static class BookshelfTextureOverride extends ItemOverrideList {

		static BookshelfTextureOverride INSTANCE = new BookshelfTextureOverride();

		private BookshelfTextureOverride() {
			super(ImmutableList.of());
		}

		@Nonnull
		@Override
		public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
			if(originalModel instanceof BookshelfModel) {
				// read out the data on the itemstack
				ItemStack blockStack = new ItemStack(TagUtil.getTagSafe(stack).getCompoundTag(RecipeUtil.TAG_TEXTURE));
				if(!blockStack.isEmpty()) {
					// get model from data
					Block block = Block.getBlockFromItem(blockStack.getItem());
					String texture = ModelHelper.getTextureFromBlock(block, blockStack.getItemDamage()).getIconName();
					return ((BookshelfModel) originalModel).getTexturedModel(ImmutableMap.of("texture", texture));
				}
			}

			return originalModel;
		}
	}
}
