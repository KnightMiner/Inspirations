package knightminer.inspirations.recipes;

import knightminer.inspirations.common.ClientEvents;

//@EventBusSubscriber(modid = Inspirations.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class RecipesClientEvents extends ClientEvents {
	/* TODO: reimplement
	private static final ResourceLocation POTION_MODEL = new ResourceLocation("bottle_drinkable");
	public static final ResourceLocation CAULDRON_MODEL = Util.getResource("cauldron");

	@SubscribeEvent
	static void registerModels(ModelRegistryEvent event) {
		setModelStateMapper(InspirationsRecipes.cauldron, new CauldronStateMapper(CAULDRON_MODEL));
		if(Config.enableCauldronDyeing) {
			registerItemModel(InspirationsRecipes.dyedWaterBottle, POTION_MODEL);
		}
	}

	@SubscribeEvent
	static void registerBlockColors(ColorHandlerEvent.Block event) {
		BlockColors blockColors = event.getBlockColors();

		// coloring of liquid inside, either for potions or dyes
		registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
			if(tintIndex == 1) {
				TileEntity te = world.getTileEntity(pos);
				if(te instanceof CauldronTileEntity) {
					return ((CauldronTileEntity) te).getColor();
				}
			}

			return -1;
		}, InspirationsRecipes.cauldron);
	}

	@SubscribeEvent
	static void registerItemColors(ColorHandlerEvent.Item event) {
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
	 * /
	@SubscribeEvent
	static void onModelBake(ModelBakeEvent event) {
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
	static void registerTextures(TextureStitchEvent.Pre event) {
		TextureMap map = event.getMap();
		registerFluidTexture(map, InspirationsRecipes.milk);
	}

	/**
	 * Mapper for redstone torch levers, to combine the two blocks as if its all one block
	 * /
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
				map.put(LEVEL, CauldronLevel.forLevel(state.getValue(EnhancedCauldronBlock.LEVELS)));
				map.remove(BlockCauldron.LEVEL);
				map.remove(EnhancedCauldronBlock.LEVELS);
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
	}*/
}
