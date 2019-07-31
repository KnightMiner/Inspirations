package knightminer.inspirations.recipes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.recipe.cauldron.CauldronBrewingRecipe;
import knightminer.inspirations.library.recipe.cauldron.CauldronDyeRecipe;
import knightminer.inspirations.library.recipe.cauldron.CauldronFluidRecipe;
import knightminer.inspirations.library.recipe.cauldron.CauldronMixRecipe;
import knightminer.inspirations.library.recipe.cauldron.FillCauldronRecipe;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.library.util.RecipeUtil;
import knightminer.inspirations.library.util.ReflectionUtil;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import knightminer.inspirations.recipes.block.BlockSmashingAnvil;
import knightminer.inspirations.recipes.dispenser.DispenseCauldronRecipe;
import knightminer.inspirations.recipes.item.ItemDyedWaterBottle;
import knightminer.inspirations.recipes.recipe.ArmorClearRecipe;
import knightminer.inspirations.recipes.recipe.ArmorDyeingCauldronRecipe;
import knightminer.inspirations.recipes.recipe.BannerClearRecipe;
import knightminer.inspirations.recipes.recipe.DyeCauldronWater;
import knightminer.inspirations.recipes.recipe.DyeIngredientWrapper;
import knightminer.inspirations.recipes.recipe.FillCauldronFromDyedBottle;
import knightminer.inspirations.recipes.recipe.FillCauldronFromFluidContainer;
import knightminer.inspirations.recipes.recipe.FillCauldronFromPotion;
import knightminer.inspirations.recipes.recipe.FillDyedBottleFromCauldron;
import knightminer.inspirations.recipes.recipe.FillFluidContainerFromCauldron;
import knightminer.inspirations.recipes.recipe.FillPotionFromCauldron;
import knightminer.inspirations.recipes.recipe.SpongeEmptyCauldron;
import knightminer.inspirations.recipes.recipe.TippedArrowCauldronRecipe;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.AbstractBrewingRecipe;
import net.minecraftforge.common.brewing.BrewingOreRecipe;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;

import java.util.Collection;
import java.util.Map;

@Pulse(id = InspirationsRecipes.pulseID, description = "Adds additional recipe types, including cauldrons and anvil smashing")
public class InspirationsRecipes extends PulseBase {
	public static final String pulseID = "InspirationsRecipes";

	@SidedProxy(clientSide = "knightminer.inspirations.recipes.RecipesClientProxy", serverSide = "knightminer.inspirations.common.CommonProxy")
	public static CommonProxy proxy;

	// blocks
	public static Block anvil;
	public static BlockEnhancedCauldron cauldron;

	// items
	public static ItemDyedWaterBottle dyedWaterBottle;
	public static ItemSoup potatoSoupItem;

	// fluids
	public static Fluid mushroomStew;
	public static Fluid potatoSoup;
	public static Fluid beetrootSoup;
	public static Fluid rabbitStew;
	public static Fluid milk;


	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();

		if(Config.cauldronStew) {
			mushroomStew = registerColoredFluid("mushroom_stew", 0xFFCD8C6F);
			potatoSoup = registerColoredFluid("potato_soup", 0xFFFFE7A8);
			beetrootSoup = registerColoredFluid("beetroot_soup", 0xFFB82A30);
			rabbitStew = registerColoredFluid("rabbit_stew", 0xFF984A2C);
		}
		if(Config.enableMilk) {
			milk = registerFluid(new Fluid("milk", Util.getResource("blocks/milk"), Util.getResource("blocks/milk_flow")));
		}
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		if(Config.enableAnvilSmashing) {
			anvil = register(r, new BlockSmashingAnvil(), new ResourceLocation("anvil"));
		}
		if(Config.enableExtendedCauldron) {
			cauldron = register(r, new BlockEnhancedCauldron(), new ResourceLocation("cauldron"));
			registerTE(TileCauldron.class, "cauldron");
		}
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		if(Config.enableCauldronDyeing) {
			dyedWaterBottle = registerItem(r, new ItemDyedWaterBottle(), "dyed_bottle");
		}
		if(Config.cauldronStew) {
			potatoSoupItem = registerItem(r, new ItemSoup(8), "potato_soup");
		}
	}

	@SubscribeEvent
	public void registerRecipes(Register<IRecipe> event) {
		if(!Config.patchVanillaDyeRecipes) {
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
			if(irecipe instanceof ShapelessRecipes) {
				// simply find all current ingredients and wrap them in my class which removes bottles
				ShapelessRecipes recipe = (ShapelessRecipes) irecipe;
				NonNullList<Ingredient> newIngredients = NonNullList.create();
				recipe.recipeItems.forEach(i->newIngredients.add(new DyeIngredientWrapper(i)));
				recipe.recipeItems.clear();
				recipe.recipeItems.addAll(newIngredients);
			} else {
				// another mod modified or removed recipe
				String error = irecipe == null ? "recipe removed" : "recipe unexpected class " + irecipe.getClass();
				Inspirations.log.warn("Error modifying dye recipe '{}', {}", recipeName, error);
			}
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();

		InspirationsRegistry.registerAnvilBreaking(Material.GLASS);
		if(Config.enableCauldronRecipes) {
			registerCauldronRecipes();
		}
		registerDispenserBehavior();
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
		MinecraftForge.EVENT_BUS.register(RecipesEvents.class);
		registerPostCauldronRecipes();
	}

	private void registerCauldronRecipes() {
		InspirationsRegistry.registerDefaultCauldron();
		InspirationsRegistry.addCauldronRecipe(new FillCauldronRecipe(RecipeMatch.of(Blocks.ICE), FluidRegistry.WATER, InspirationsRegistry.getCauldronMax(), ItemStack.EMPTY, true, SoundEvents.ITEM_BUCKET_EMPTY_LAVA));
		if(Config.spongeEmptyCauldron) {
			InspirationsRegistry.addCauldronRecipe(SpongeEmptyCauldron.INSTANCE);
		}
		if(Config.cauldronObsidian) {
			ICauldronRecipe recipe;
			// minor detail: if the cauldron can hold fluids, show the lava in the cauldron in JEI
			// else show water in the cauldron as lava is not allowed
			// in either case both are supported
			if(Config.enableCauldronFluids) {
				recipe = new CauldronMixRecipe(FluidRegistry.LAVA, FluidRegistry.WATER, new ItemStack(Blocks.OBSIDIAN));
			} else {
				recipe = new CauldronMixRecipe(FluidRegistry.WATER, FluidRegistry.LAVA, new ItemStack(Blocks.OBSIDIAN));
			}
			InspirationsRegistry.addCauldronRecipe(recipe);
		}

		if(!Config.enableExtendedCauldron) {
			return;
		}

		// reimplemented vanilla recipes
		InspirationsRegistry.addCauldronRecipe(new ArmorClearRecipe(ItemArmor.ArmorMaterial.LEATHER));
		InspirationsRegistry.addCauldronRecipe(BannerClearRecipe.INSTANCE);
		// fill from water bottle, does not use the shortcut as we need NBT matching
		ItemStack waterBottle = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
		InspirationsRegistry.addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(Items.GLASS_BOTTLE), FluidRegistry.WATER, waterBottle, null, SoundEvents.ITEM_BOTTLE_FILL));
		InspirationsRegistry.addCauldronRecipe(new FillCauldronRecipe(RecipeMatch.ofNBT(waterBottle), FluidRegistry.WATER, 1, new ItemStack(Items.GLASS_BOTTLE)));

		if(Config.enableCauldronDyeing) {
			InspirationsRegistry.addCauldronRecipe(FillDyedBottleFromCauldron.INSTANCE);
			InspirationsRegistry.addCauldronRecipe(FillCauldronFromDyedBottle.INSTANCE);
			InspirationsRegistry.addCauldronRecipe(new ArmorDyeingCauldronRecipe(ItemArmor.ArmorMaterial.LEATHER));

			for(EnumDyeColor color : EnumDyeColor.values()) {
				InspirationsRegistry.addCauldronRecipe(new DyeCauldronWater(color));
				InspirationsRegistry.addCauldronRecipe(new CauldronDyeRecipe(
						new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE),
						color,
						new ItemStack(Blocks.WOOL, 1, color.getMetadata())
						));

				InspirationsRegistry.addCauldronRecipe(new CauldronDyeRecipe(
						new ItemStack(Blocks.CARPET, 1, OreDictionary.WILDCARD_VALUE),
						color,
						new ItemStack(Blocks.CARPET, 1, color.getMetadata())
						));

				InspirationsRegistry.addCauldronRecipe(new CauldronDyeRecipe(
						new ItemStack(Items.BED, 1, OreDictionary.WILDCARD_VALUE),
						color,
						new ItemStack(Items.BED, 1, color.getMetadata())
						));
			}
			if(InspirationsUtility.carpetedTrapdoors != null) {
				RecipeMatch anyTrapdoor = RecipeMatch.of("trapdoorCarpeted");
				for(EnumDyeColor color : EnumDyeColor.values()) {
					InspirationsRegistry.addCauldronRecipe(new CauldronDyeRecipe(
							anyTrapdoor, color,
							new ItemStack(InspirationsUtility.carpetedTrapdoors[color.getMetadata()])
							));
				}
			}
		}

		if(Config.enableCauldronPotions) {
			addPotionBottle(Items.POTIONITEM, new ItemStack(Items.GLASS_BOTTLE), null);
			addPotionBottle(Items.SPLASH_POTION, InspirationsShared.splashBottle, "bottleSplash");
			addPotionBottle(Items.LINGERING_POTION, InspirationsShared.lingeringBottle, "bottleLingering");
			if (Config.cauldronTipArrows) {
				InspirationsRegistry.addCauldronRecipe(TippedArrowCauldronRecipe.INSTANCE);
			}
		}

		if(Config.enableCauldronFluids) {
			InspirationsRegistry.addCauldronRecipe(FillFluidContainerFromCauldron.INSTANCE);

			if (Config.cauldronStew) {
				addStewRecipes(new ItemStack(Items.BEETROOT_SOUP), beetrootSoup, new ItemStack(Items.BEETROOT, 6), FluidRegistry.WATER);
				addStewRecipes(new ItemStack(Items.MUSHROOM_STEW), mushroomStew, "mushroomAny", 2, FluidRegistry.WATER);
				addStewRecipes(new ItemStack(potatoSoupItem), potatoSoup, new ItemStack(Items.BAKED_POTATO, 2), mushroomStew);
				addStewRecipes(new ItemStack(Items.RABBIT_STEW), rabbitStew, new ItemStack(Items.COOKED_RABBIT), potatoSoup);

				// legacy
				addStewRecipes(new ItemStack(Items.MUSHROOM_STEW), mushroomStew, InspirationsShared.mushrooms.copy(), FluidRegistry.WATER);
				addStewRecipes(new ItemStack(Items.RABBIT_STEW), rabbitStew, InspirationsShared.rabbitStewMix.copy(), FluidRegistry.WATER);
			}
		} else {
			// above relied on for bucket filling cauldron
			InspirationsRegistry.addCauldronFluidItem(new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.BUCKET), FluidRegistry.WATER, 3);
		}
	}

	/**
	 * These recipes need to be registered later to prevent from conflicts or missing recipes
	 */
	private void registerPostCauldronRecipes() {
		if(Config.enableCauldronBrewing) {
			for(Object recipe : PotionHelper.POTION_TYPE_CONVERSIONS) {
				PotionType input = ReflectionUtil.getMixPredicateInput(recipe);
				Ingredient reagent = ReflectionUtil.getMixPredicateReagent(recipe);
				PotionType output = ReflectionUtil.getMixPredicateOutput(recipe);
				if(input != null && reagent != null && output != null) {
					InspirationsRegistry.addCauldronRecipe(new CauldronBrewingRecipe(input, reagent, output));
				}
			}
			findRecipesFromBrewingRegistry();
		}
		if(Config.enableCauldronFluids) {
			InspirationsRegistry.addCauldronRecipe(FillCauldronFromFluidContainer.INSTANCE);
		}
	}

	private static void addPotionBottle(Item potion, ItemStack bottle, String bottleOre) {
		InspirationsRegistry.addCauldronRecipe(new FillCauldronFromPotion(potion, bottle));
		if(bottleOre != null) {
			InspirationsRegistry.addCauldronRecipe(new FillPotionFromCauldron(potion, bottleOre));
		} else {
			InspirationsRegistry.addCauldronRecipe(new FillPotionFromCauldron(potion, bottle));
		}
	}

	private static void addStewRecipes(ItemStack stew, Fluid fluid, ItemStack ingredient, Fluid base) {
		InspirationsRegistry.addCauldronScaledTransformRecipe(ingredient, base, fluid, true);
		// filling and emptying bowls
		InspirationsRegistry.addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(Items.BOWL), fluid, stew, null, SoundEvents.ITEM_BOTTLE_FILL));
		InspirationsRegistry.addCauldronRecipe(new FillCauldronRecipe(RecipeMatch.of(stew), fluid, 1, new ItemStack(Items.BOWL)));
	}

	private static void addStewRecipes(ItemStack stew, Fluid fluid, String ingredient, int count, Fluid base) {
		InspirationsRegistry.addCauldronScaledTransformRecipe(ingredient, count, base, fluid, true);
		// filling and emptying bowls
		InspirationsRegistry.addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(Items.BOWL), fluid, stew, null, SoundEvents.ITEM_BOTTLE_FILL));
		InspirationsRegistry.addCauldronRecipe(new FillCauldronRecipe(RecipeMatch.of(stew), fluid, 1, new ItemStack(Items.BOWL)));
	}

	private void findRecipesFromBrewingRegistry() {
		for(IBrewingRecipe irecipe : BrewingRecipeRegistry.getRecipes()) {
			if(irecipe instanceof AbstractBrewingRecipe) {

				AbstractBrewingRecipe recipe = (AbstractBrewingRecipe) irecipe;
				ItemStack inputStack = recipe.getInput();
				ItemStack outputStack = recipe.getOutput();
				Ingredient ingredient = null;
				if (recipe instanceof BrewingRecipe) {
					ingredient = Ingredient.fromStacks(((BrewingRecipe)recipe).getIngredient());
				} else if (recipe instanceof BrewingOreRecipe) {
					ingredient = Ingredient.fromStacks(((BrewingOreRecipe)recipe).getIngredient().stream().toArray(ItemStack[]::new));
				}

				// null checks because some dumb mod is returning null for the input or output
				if(ingredient != null && inputStack != null && inputStack.getItem() == Items.POTIONITEM
						&& outputStack != null && outputStack.getItem() == Items.POTIONITEM) {
					PotionType input = PotionUtils.getPotionFromItem(inputStack);
					PotionType output = PotionUtils.getPotionFromItem(outputStack);
					if(input != PotionTypes.EMPTY && output != PotionTypes.EMPTY) {
						InspirationsRegistry.addCauldronRecipe(new CauldronBrewingRecipe(input, ingredient, output));
					}
				}
			}
		}
	}

	private void registerDispenserBehavior() {
		if(Config.enableCauldronDispenser) {
			Multimap<Item,Integer> map = HashMultimap.create();
			for(String line : Config.cauldronDispenserRecipes) {
				if (!StringUtils.isNullOrEmpty(line)) {
					ItemStack stack = RecipeUtil.getItemStackFromString(line, true);
					map.put(stack.getItem(), stack.getMetadata());
				}
			}
			for(Map.Entry<Item,Collection<Integer>> entry : map.asMap().entrySet()) {
				registerDispenseCauldronLogic(entry.getKey(), toArray(entry.getValue()));
			}
		}
	}

	private static int[] toArray(Collection<Integer> list) {
		// if there is a wildcard, return an empty list to signify all meta
		if(list.contains(OreDictionary.WILDCARD_VALUE)) {
			return new int[0];
		}
		return list.stream().mapToInt(i->i).toArray();
	}

	private static void registerDispenseCauldronLogic(Item item, int[] meta) {
		registerDispenserBehavior(item, new DispenseCauldronRecipe(BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(item), meta));
	}
}
