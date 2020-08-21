package knightminer.inspirations.recipes;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.ModuleBase;
import knightminer.inspirations.common.item.HidableItem;
import knightminer.inspirations.library.recipe.cauldron.CauldronContentTypes;
import knightminer.inspirations.library.recipe.cauldron.recipe.CauldronRecipe;
import knightminer.inspirations.recipes.block.EnhancedCauldronBlock;
import knightminer.inspirations.recipes.item.MixedDyedBottleItem;
import knightminer.inspirations.recipes.item.SimpleDyedBottleItem;
import knightminer.inspirations.recipes.recipe.cauldron.contents.CauldronWater;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.potion.Potions;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.mantle.registration.adapter.BlockRegistryAdapter;
import slimeknights.mantle.registration.adapter.ItemRegistryAdapter;
import slimeknights.mantle.registration.adapter.RegistryAdapter;
import slimeknights.mantle.registration.object.EnumObject;

@SuppressWarnings({"WeakerAccess", "unused"})
public class InspirationsRecipes extends ModuleBase {
  public static final String pulseID = "InspirationsRecipes";

  // blocks
  public static Block fullAnvil;
  public static Block chippedAnvil;
  public static Block damagedAnvil;

  public static EnhancedCauldronBlock cauldron;

  // items
  public static Item splashBottle;
  public static Item lingeringBottle;
  public static EnumObject<DyeColor,SimpleDyedBottleItem> simpleDyedWaterBottle;
  public static MixedDyedBottleItem mixedDyedWaterBottle;

  // fluids
  public static Fluid mushroomStew;
  public static Fluid beetrootSoup;
  public static Fluid rabbitStew;
  public static Fluid milk;

  // cauldron serializers
  public static CauldronRecipe.Serializer cauldronSerializer;


  @SubscribeEvent
  void preInit(FMLCommonSetupEvent event) {
    //TODO: reimplement
    //if(Config.enableCauldronFluids()) {
    //mushroomStew = registerColoredFluid("mushroom_stew", 0xFFCD8C6F);
    //beetrootSoup = registerColoredFluid("beetroot_soup", 0xFFB82A30);
    //rabbitStew = registerColoredFluid("rabbit_stew", 0xFF984A2C);
    //if (Config.enableMilk()) {
    ///milk = registerFluid(new Fluid("milk", Util.getResource("blocks/milk"), Util.getResource("blocks/milk_flow")));
    //}
    //}
  }

  @SubscribeEvent
  void registerBlocks(Register<Block> event) {
    BlockRegistryAdapter registry = new BlockRegistryAdapter(event.getRegistry());

    /*
    if (Config.enableAnvilSmashing.get()) {
      registry.registerOverride(SmashingAnvilBlock::new, Blocks.ANVIL);
      registry.registerOverride(SmashingAnvilBlock::new, Blocks.CHIPPED_ANVIL);
      registry.registerOverride(SmashingAnvilBlock::new, Blocks.DAMAGED_ANVIL);
    }
    */
    if (Config.enableExtendedCauldron()) {
      cauldron = registry.registerOverride(EnhancedCauldronBlock::new, Blocks.CAULDRON);
    }
  }

  @SubscribeEvent
  void registerItems(Register<Item> event) {
    ItemRegistryAdapter registry = new ItemRegistryAdapter(event.getRegistry());
    Item.Properties brewingProps = new Item.Properties().group(ItemGroup.BREWING);

    splashBottle = registry.register(new HidableItem(brewingProps, Config::enableCauldronPotions), "splash_bottle");
    lingeringBottle = registry.register(new HidableItem(brewingProps, Config::enableCauldronPotions), "lingering_bottle");

    simpleDyedWaterBottle = registry.registerEnum(SimpleDyedBottleItem::new, DyeColor.values(), "dyed_bottle");
    mixedDyedWaterBottle = registry.register(new MixedDyedBottleItem(), "mixed_dyed_bottle");
  }

  @SubscribeEvent
  void registerSerializers(Register<IRecipeSerializer<?>> event) {
    RegistryAdapter<IRecipeSerializer<?>> registry = new RegistryAdapter<>(event.getRegistry());
    cauldronSerializer = registry.register(new CauldronRecipe.Serializer(), "cauldron");

    // add water as an override to fluids and potions
    CauldronWater water = CauldronContentTypes.WATER.get();
    CauldronContentTypes.FLUID.addOverride(Fluids.WATER, water);
    CauldronContentTypes.POTION.addOverride(Potions.WATER, water);

    // add all dyes as overrides into color
    for (DyeColor color : DyeColor.values()) {
      CauldronContentTypes.COLOR.addOverride(color.getColorValue(), CauldronContentTypes.DYE.of(color));
    }
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
