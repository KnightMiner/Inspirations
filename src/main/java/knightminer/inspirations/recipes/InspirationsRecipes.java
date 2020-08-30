package knightminer.inspirations.recipes;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.ModuleBase;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.contents.ICauldronContents;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.recipe.EmptyPotionCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.recipe.FillPotionCauldronRecipe;
import knightminer.inspirations.recipes.block.EnhancedCauldronBlock;
import knightminer.inspirations.recipes.data.RecipesRecipeProvider;
import knightminer.inspirations.recipes.item.EmptyBottleItem;
import knightminer.inspirations.recipes.item.MixedDyedBottleItem;
import knightminer.inspirations.recipes.item.SimpleDyedBottleItem;
import knightminer.inspirations.recipes.recipe.cauldron.DyeCauldronWaterRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.EmptyBucketCauldronRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.FillBucketCauldronRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.FillDyedBottleRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.MixCauldronDyeRecipe;
import knightminer.inspirations.recipes.tileentity.CauldronTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.data.DataGenerator;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.item.SoupItem;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import slimeknights.mantle.registration.FluidBuilder;
import slimeknights.mantle.registration.adapter.BlockRegistryAdapter;
import slimeknights.mantle.registration.adapter.FluidRegistryAdapter;
import slimeknights.mantle.registration.adapter.ItemRegistryAdapter;
import slimeknights.mantle.registration.adapter.RegistryAdapter;
import slimeknights.mantle.registration.adapter.TileEntityTypeRegistryAdapter;
import slimeknights.mantle.registration.object.EnumObject;

@SuppressWarnings({"WeakerAccess", "unused"})
public class InspirationsRecipes extends ModuleBase {
  public static final ResourceLocation STILL_FLUID = Inspirations.getResource("block/fluid/colorless");
  public static final ResourceLocation FLOWING_FLUID = Inspirations.getResource("block/fluid/colorless_flow");

  // blocks
  public static Block fullAnvil;
  public static Block chippedAnvil;
  public static Block damagedAnvil;

  public static EnhancedCauldronBlock cauldron;
  public static EnhancedCauldronBlock boilingCauldron;
  public static TileEntityType<CauldronTileEntity> tileCauldron;

  // items
  public static Item splashBottle;
  public static Item lingeringBottle;
  public static EnumObject<DyeColor,SimpleDyedBottleItem> simpleDyedWaterBottle = EnumObject.empty();
  public static MixedDyedBottleItem mixedDyedWaterBottle;
  public static SoupItem potatoSoupItem;

  // fluids
  public static ForgeFlowingFluid mushroomStew;
  public static BucketItem mushroomStewBucket;
  public static FlowingFluidBlock mushroomStewBlock;
  public static ForgeFlowingFluid beetrootSoup;
  public static BucketItem beetrootSoupBucket;
  public static FlowingFluidBlock beetrootSoupBlock;
  public static ForgeFlowingFluid rabbitStew;
  public static BucketItem rabbitStewBucket;
  public static FlowingFluidBlock rabbitStewBlock;
  public static ForgeFlowingFluid potatoSoup;
  public static BucketItem potatoSoupBucket;
  public static FlowingFluidBlock potatoSoupBlock;
  //public static Fluid milk;

  // cauldron serializers
  public static CauldronRecipe.Serializer cauldronSerializer;
  public static EmptyPotionCauldronRecipe.Serializer emptyPotionSerializer;
  public static FillPotionCauldronRecipe.Serializer fillPotionSerializer;
  public static DyeCauldronWaterRecipe.Serializer dyeCauldronWaterSerializer;
  public static MixCauldronDyeRecipe.Serializer mixCauldronDyeSerializer;

  public static SpecialRecipeSerializer<EmptyBucketCauldronRecipe> emptyBucketSerializer;
  public static SpecialRecipeSerializer<FillBucketCauldronRecipe> fillBucketSerializer;
  public static SpecialRecipeSerializer<FillDyedBottleRecipe> fillDyedBottleSerializer;

  public static BasicParticleType boilingParticle;

  @SubscribeEvent
  void registerFluids(Register<Fluid> event) {
    FluidRegistryAdapter adapter = new FluidRegistryAdapter(event.getRegistry());

    mushroomStew = adapter.register(new FluidBuilder(coloredFluid().color(0xFFCD8C6F).temperature(373))
                                        .block(() -> mushroomStewBlock)
                                        .bucket(() -> mushroomStewBucket), "mushroom_stew");
    beetrootSoup = adapter.register(new FluidBuilder(coloredFluid().color(0xFF84160D).temperature(373))
                                        .block(() -> beetrootSoupBlock)
                                        .bucket(() -> beetrootSoupBucket), "beetroot_soup");
    rabbitStew = adapter.register(new FluidBuilder(coloredFluid().color(0xFF984A2C).temperature(373))
                                      .block(() -> rabbitStewBlock)
                                      .bucket(() -> rabbitStewBucket), "rabbit_stew");
    potatoSoup = adapter.register(new FluidBuilder(coloredFluid().color(0xFFF2DA9F).temperature(373))
                                      .block(() -> potatoSoupBlock)
                                      .bucket(() -> potatoSoupBucket), "potato_soup");
    //milk = adapter.register(new FluidBuilder(FluidAttributes.builder(Inspirations.getResource("block/milk"), Inspirations.getResource("block/milk_flow")).color(0xFFCD8C6F)), "milk");
  }

  @SubscribeEvent
  void registerBlocks(Register<Block> event) {
    BlockRegistryAdapter registry = new BlockRegistryAdapter(event.getRegistry());

    mushroomStewBlock = registry.registerFluidBlock(() -> mushroomStew, Material.WATER, 0, "mushroom_stew");
    beetrootSoupBlock = registry.registerFluidBlock(() -> beetrootSoup, Material.WATER, 0, "beetroot_soup");
    rabbitStewBlock = registry.registerFluidBlock(() -> rabbitStew, Material.WATER, 0, "rabbit_stew");
    potatoSoupBlock = registry.registerFluidBlock(() -> potatoSoup, Material.WATER, 0, "potato_soup");

    /*
    if (Config.enableAnvilSmashing.get()) {
      registry.registerOverride(SmashingAnvilBlock::new, Blocks.ANVIL);
      registry.registerOverride(SmashingAnvilBlock::new, Blocks.CHIPPED_ANVIL);
      registry.registerOverride(SmashingAnvilBlock::new, Blocks.DAMAGED_ANVIL);
    }
    */
    if (Config.extendedCauldron.get()) {
      cauldron = registry.registerOverride(EnhancedCauldronBlock::new, Blocks.CAULDRON);
    }
    boilingCauldron = registry.register(new EnhancedCauldronBlock(AbstractBlock.Properties.from(Blocks.CAULDRON)), "boiling_cauldron");
  }

  @SubscribeEvent
  void registerItems(Register<Item> event) {
    ItemRegistryAdapter registry = new ItemRegistryAdapter(event.getRegistry());

    // buckets
    mushroomStewBucket = registry.registerBucket(() -> mushroomStew, "mushroom_stew");
    beetrootSoupBucket = registry.registerBucket(() -> beetrootSoup, "beetroot_soup");
    rabbitStewBucket = registry.registerBucket(() -> rabbitStew, "rabbit_stew");
    potatoSoupBucket = registry.registerBucket(() -> potatoSoup, "potato_soup");

    // potato soup
    potatoSoupItem = registry.register(
        new SoupItem(new Item.Properties().maxStackSize(1)
                                          .group(ItemGroup.FOOD)
                                          .food(new Food.Builder().hunger(8).saturation(0.6F).build())),
        "potato_soup");

    // empty bottles
    Item.Properties brewingProps = new Item.Properties().group(ItemGroup.BREWING);
    splashBottle = registry.register(new EmptyBottleItem(brewingProps, Items.SPLASH_POTION.delegate), "splash_bottle");
    lingeringBottle = registry.register(new EmptyBottleItem(brewingProps, Items.LINGERING_POTION.delegate), "lingering_bottle");

    // dyed bottles
    Item.Properties bottleProps = new Item.Properties()
        .group(ItemGroup.MATERIALS)
        .maxStackSize(16)
        .containerItem(Items.GLASS_BOTTLE);
    simpleDyedWaterBottle = registry.registerEnum(color -> new SimpleDyedBottleItem(bottleProps, color), DyeColor.values(), "dyed_bottle");
    mixedDyedWaterBottle = registry.register(new MixedDyedBottleItem(bottleProps), "mixed_dyed_bottle");

    // boiling cauldron item
    Item cauldronItem = Items.CAULDRON;
    if (Config.extendedCauldron.getAsBoolean()) {
      cauldronItem = registry.registerBlockItem(cauldron, brewingProps);
    }
    Item.BLOCK_TO_ITEM.put(boilingCauldron, cauldronItem);
  }

  @SubscribeEvent
  void registerTileEntities(Register<TileEntityType<?>> event) {
    TileEntityTypeRegistryAdapter registry = new TileEntityTypeRegistryAdapter(event.getRegistry());

    if (Config.extendedCauldron.get()) {
      tileCauldron = registry.register(CauldronTileEntity::new, "cauldron", blocks -> blocks.add(cauldron, boilingCauldron));
    }
  }

  @SubscribeEvent
  void registerParticleTypes(Register<ParticleType<?>> event) {
    RegistryAdapter<ParticleType<?>> registry = new RegistryAdapter<>(event.getRegistry());
    boilingParticle = registry.register(new BasicParticleType(false), "boiling");
  }

  @SubscribeEvent
  void registerSerializers(Register<IRecipeSerializer<?>> event) {
    RegistryAdapter<IRecipeSerializer<?>> registry = new RegistryAdapter<>(event.getRegistry());
    cauldronSerializer = registry.register(new CauldronRecipe.Serializer(), "cauldron");
    emptyPotionSerializer = registry.register(new EmptyPotionCauldronRecipe.Serializer(), "cauldron_empty_potion");
    fillPotionSerializer = registry.register(new FillPotionCauldronRecipe.Serializer(), "cauldron_fill_potion");
    dyeCauldronWaterSerializer = registry.register(new DyeCauldronWaterRecipe.Serializer(), "cauldron_dye_water");
    mixCauldronDyeSerializer = registry.register(new MixCauldronDyeRecipe.Serializer(), "cauldron_mix_dye");

    emptyBucketSerializer = registry.register(new SpecialRecipeSerializer<>(EmptyBucketCauldronRecipe::new), "cauldron_empty_bucket");
    fillBucketSerializer = registry.register(new SpecialRecipeSerializer<>(FillBucketCauldronRecipe::new), "cauldron_fill_bucket");
    fillDyedBottleSerializer = registry.register(new SpecialRecipeSerializer<>(FillDyedBottleRecipe::new), "cauldron_fill_dyed_bottle");

    // add water as an override to potions
    ICauldronContents water = CauldronContentTypes.FLUID.of(Fluids.WATER);
    CauldronContentTypes.POTION.setResult(Potions.WATER, water);

    // add all dyes as overrides into color
    for (DyeColor color : DyeColor.values()) {
      CauldronContentTypes.COLOR.setResult(color.getColorValue(), CauldronContentTypes.DYE.of(color));
    }
  }

  @SubscribeEvent
  void gatherData(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    if (event.includeServer()) {
      gen.addProvider(new RecipesRecipeProvider(gen));
    }
  }

  /**
   * Creates a fluid attribute for the generic colorless fluid
   * @return  Fluid attributes builder
   */
  private static FluidAttributes.Builder coloredFluid() {
    return FluidAttributes.builder(STILL_FLUID, FLOWING_FLUID);
  }

	/* TODO: reimplement
	@SubscribeEvent
	public void registerRecipes(Register<IRecipe<ICraftingRecipe>> event) {
		if(!Config.patchVanillaDyeRecipes()) {
			return;
		}
		IForgeRegistry<IRecipe> r = event.getRegistry();
		String[] recipes = {
				"purple_dye",
				"cyan_dye",
				"light_gray_dye_from_ink_bonemeal",
				"light_gray_dye_from_gray_bonemeal",
				"gray_dye",
				"pink_dye_from_red_bonemeal",
				"lime_dye",
				"light_blue_dye_from_lapis_bonemeal",
				"magenta_dye_from_purple_and_pink",
				"magenta_dye_from_lapis_red_pink",
				"magenta_dye_from_lapis_ink_bonemeal",
				"orange_dye_from_red_yellow"
		};
		for(String recipeName : recipes) {
			IRecipe irecipe = r.getValue(new ResourceLocation(recipeName));
			if(irecipe instanceof ShapelessRecipe) {
				// simply find all current ingredients and wrap them in my class which removes bottles
				ShapelessRecipe recipe = (ShapelessRecipe) irecipe;
				NonNullList<Ingredient> newIngredients = NonNullList.create();
				recipe.getIngredients().forEach(i->newIngredients.add(new DyeIngredientWrapper(i)));
				recipe.getIngredients().clear();
				recipe.getIngredients().addAll(newIngredients);
			} else {
				// another mod modified or removed recipe
				String error = irecipe == null ? "recipe removed" : "recipe unexpected class " + irecipe.getClass();
				Inspirations.log.warn("Error modifying dye recipe '{}', {}", recipeName, error);
			}
		}
	}*/
}
