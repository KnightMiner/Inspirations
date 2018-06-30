package knightminer.inspirations.recipes;

import java.util.LinkedHashMap;
import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RecipesClientProxy extends ClientProxy {
	private static final ResourceLocation POTION_MODEL = new ResourceLocation("bottle_drinkable");
	public static final ResourceLocation CAULDRON_MODEL = Util.getResource("cauldron");

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		setModelStateMapper(InspirationsRecipes.cauldron, new CauldronStateMapper(CAULDRON_MODEL));
		if(Config.enableCauldronDyeing) {
			registerItemModel(InspirationsRecipes.dyedWaterBottle, POTION_MODEL);
		}
	}

	@SubscribeEvent
	public void registerBlockColors(ColorHandlerEvent.Block event) {
		BlockColors blockColors = event.getBlockColors();

		// coloring of liquid inside, either for potions or dyes
		registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
			if(tintIndex == 1) {
				TileEntity te = world.getTileEntity(pos);
				if(te instanceof TileCauldron) {
					return ((TileCauldron) te).getColor();
				}
			}

			return -1;
		}, InspirationsRecipes.cauldron);
	}

	@SubscribeEvent
	public void registerItemColors(ColorHandlerEvent.Item event) {
		ItemColors itemColors = event.getItemColors();

		// dyed water bottles
		registerItemColors(itemColors, (stack, tintIndex) -> {
			if(tintIndex == 0) {
				return InspirationsRecipes.dyedWaterBottle.getColor(stack);
			}
			return -1;
		}, InspirationsRecipes.dyedWaterBottle);
	}


	/**
	 * Replaces the bookshelf models with the dynamic texture model, which also handles books
	 */
	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		if(InspirationsRecipes.cauldron == null) {
			return;
		}

		boolean boiling = false;
		do {
			replaceTexturedModel(event, new ModelResourceLocation(CAULDRON_MODEL, String.format("boiling=%s,contents=fluid,level=empty", boiling)), "water", false);
			for(int i = (Config.enableBiggerCauldron ? 0 : 1); i <= 3; i++) {
				replaceTexturedModel(event, new ModelResourceLocation(CAULDRON_MODEL, String.format("boiling=%s,contents=fluid,level=%s", boiling, i)), "water", false);
			}
			boiling = !boiling;
		} while(boiling);
	}

	@SubscribeEvent
	public void registerTextures(TextureStitchEvent.Pre event) {
		TextureMap map = event.getMap();
		registerFluidTexture(map, InspirationsRecipes.milk);
	}

	/**
	 * Mapper for redstone torch levers, to combine the two blocks as if its all one block
	 */
	public static class CauldronStateMapper extends StateMapperBase {
		private static final PropertyEnum<CauldronLevel> LEVEL = PropertyEnum.create("level", CauldronLevel.class);
		private ResourceLocation location;
		public CauldronStateMapper(ResourceLocation location) {
			this.location = location;
		}

		@Nonnull
		@Override
		protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
			LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
			if(Config.enableBiggerCauldron) {
				map.put(LEVEL, CauldronLevel.forLevel(state.getValue(BlockEnhancedCauldron.LEVELS)));
				map.remove(BlockCauldron.LEVEL);
				map.remove(BlockEnhancedCauldron.LEVELS);
			} else {
				map.put(LEVEL, CauldronLevel.forLevel(state.getValue(BlockCauldron.LEVEL)));
				map.remove(BlockCauldron.LEVEL);
			}

			return new ModelResourceLocation(location, this.getPropertyString(map));
		}

		private static enum CauldronLevel implements IStringSerializable {
			EMPTY,
			LEVEL0,
			LEVEL1,
			LEVEL2,
			LEVEL3;

			public static CauldronLevel forLevel(int level) {
				// validate meta
				if(level < 0 || level > 4) {
					level = 0;
				}
				// if a regular cauldron, add one to the index, as the second spot is 0 instead of 1
				if(!Config.enableBiggerCauldron && level != 0) {
					level = level + 1;
				}
				return values()[level];
			}

			@Override
			public String getName() {
				if(this == EMPTY) {
					return "empty";
				}
				// removes 1 so the meta matches up with the vanilla levels
				return "" + (this.ordinal() - 1);
			}
		}
	}
}
