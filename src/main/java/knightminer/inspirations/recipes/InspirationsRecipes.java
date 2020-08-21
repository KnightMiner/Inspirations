package knightminer.inspirations.recipes;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.ModuleBase;
import knightminer.inspirations.common.item.HidableItem;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronRecipe;
import knightminer.inspirations.recipes.data.RecipesRecipeProvider;
import knightminer.inspirations.recipes.item.MixedDyedBottleItem;
import knightminer.inspirations.recipes.item.SimpleDyedBottleItem;
import knightminer.inspirations.recipes.recipe.cauldron.EmptyBucketCauldronRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.FillBucketCauldronRecipe;
import knightminer.inspirations.recipes.recipe.cauldron.contents.CauldronWater;
import net.minecraft.block.Block;
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
import net.minecraft.potion.Potions;
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
import slimeknights.mantle.registration.object.EnumObject;

@SuppressWarnings({"WeakerAccess", "unused"})
public class InspirationsRecipes extends ModuleBase {
  public static final ResourceLocation STILL_FLUID = Inspirations.getResource("block/fluid/colorless");
  public static final ResourceLocation FLOWING_FLUID = Inspirations.getResource("block/fluid/colorless_flow");

  // blocks
  public static Block fullAnvil;
  public static Block chippedAnvil;
  public static Block damagedAnvil;

  //public static EnhancedCauldronBlock cauldron;

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
  public static SpecialRecipeSerializer<EmptyBucketCauldronRecipe> emptyBucketSerializer;
  public static SpecialRecipeSerializer<FillBucketCauldronRecipe> fillBucketSerializer;

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
    if (Config.enableExtendedCauldron()) {
      cauldron = registry.registerOverride(EnhancedCauldronBlock::new, Blocks.CAULDRON);
    }
     */
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
    splashBottle = registry.register(new HidableItem(brewingProps, Config::enableCauldronPotions), "splash_bottle");
    lingeringBottle = registry.register(new HidableItem(brewingProps, Config::enableCauldronPotions), "lingering_bottle");

    // dyed bottles
    Item.Properties bottleProps = new Item.Properties()
        .group(ItemGroup.MATERIALS)
        .maxStackSize(16)
        .containerItem(Items.GLASS_BOTTLE);
    simpleDyedWaterBottle = registry.registerEnum(color -> new SimpleDyedBottleItem(bottleProps, color), DyeColor.values(), "dyed_bottle");
    mixedDyedWaterBottle = registry.register(new MixedDyedBottleItem(bottleProps), "mixed_dyed_bottle");
  }

  @SubscribeEvent
  void registerSerializers(Register<IRecipeSerializer<?>> event) {
    RegistryAdapter<IRecipeSerializer<?>> registry = new RegistryAdapter<>(event.getRegistry());
    cauldronSerializer = registry.register(new CauldronRecipe.Serializer(), "cauldron");
    emptyBucketSerializer = registry.register(new SpecialRecipeSerializer<>(EmptyBucketCauldronRecipe::new), "cauldron_empty_bucket");
    fillBucketSerializer = registry.register(new SpecialRecipeSerializer<>(FillBucketCauldronRecipe::new), "cauldron_fill_bucket");

    // add water as an override to fluids and potions
    CauldronWater water = CauldronContentTypes.WATER.get();
    CauldronContentTypes.FLUID.addOverride(Fluids.WATER, water);
    CauldronContentTypes.POTION.addOverride(Potions.WATER, water);

    // add all dyes as overrides into color
    for (DyeColor color : DyeColor.values()) {
      CauldronContentTypes.COLOR.addOverride(color.getColorValue(), CauldronContentTypes.DYE.of(color));
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
