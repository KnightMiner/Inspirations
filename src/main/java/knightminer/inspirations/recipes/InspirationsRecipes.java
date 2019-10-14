package knightminer.inspirations.recipes;

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
import knightminer.inspirations.library.util.ReflectionUtil;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import knightminer.inspirations.recipes.block.BlockSmashingAnvil;
import knightminer.inspirations.recipes.dispenser.DispenseCauldronRecipe;
import knightminer.inspirations.recipes.item.ItemMixedDyedWaterBottle;
import knightminer.inspirations.recipes.item.ItemSimpleDyedWaterBottle;
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
import knightminer.inspirations.shared.InspirationsShared;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;

import java.util.Map;

@Pulse(id = InspirationsRecipes.pulseID, description = "Adds additional recipe types, including cauldrons and anvil smashing")
public class InspirationsRecipes extends PulseBase {
	public static final String pulseID = "InspirationsRecipes";

	public static CommonProxy proxy = DistExecutor.runForDist(
			() -> () -> new RecipesClientProxy(),
			() -> () -> new CommonProxy()
			);

	// blocks
	public static Block fullAnvil;
	public static Block chippedAnvil;
	public static Block damagedAnvil;

	public static BlockEnhancedCauldron cauldron;

	// items
	public static Map<DyeColor, ItemSimpleDyedWaterBottle> simpleDyedWaterBottle;
	public static ItemMixedDyedWaterBottle mixedDyedWaterBottle;

	// fluids
	public static Fluid mushroomStew;
	public static Fluid beetrootSoup;
	public static Fluid rabbitStew;
	public static Fluid milk;


	@SubscribeEvent
	public void preInit(FMLCommonSetupEvent event) {
		proxy.preInit();

		if(Config.enableCauldronFluids()) {
			mushroomStew = registerColoredFluid("mushroom_stew", 0xFFCD8C6F);
			beetrootSoup = registerColoredFluid("beetroot_soup", 0xFFB82A30);
			rabbitStew = registerColoredFluid("rabbit_stew", 0xFF984A2C);
			if(Config.enableMilk()) {
				milk = registerFluid(new Fluid("milk", Util.getResource("blocks/milk"), Util.getResource("blocks/milk_flow")));
			}
		}
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		if(Config.enableAnvilSmashing.get()) {
			fullAnvil = new BlockSmashingAnvil(Blocks.ANVIL);
			chippedAnvil = new BlockSmashingAnvil(Blocks.CHIPPED_ANVIL);
			damagedAnvil = new BlockSmashingAnvil(Blocks.DAMAGED_ANVIL);
			r.registerAll(fullAnvil, chippedAnvil, damagedAnvil);
		}
		if(Config.enableExtendedCauldron()) {
			cauldron = register(r, new BlockEnhancedCauldron(), Blocks.CAULDRON.getRegistryName());
		}
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		for(DyeColor color: DyeColor.values()) {
			simpleDyedWaterBottle.put(color, registerItem(r,
					new ItemSimpleDyedWaterBottle(color),
					color.getName() + "_dyed_bottle"
			));
		}
		mixedDyedWaterBottle = registerItem(r, new ItemMixedDyedWaterBottle(), "mixed_dyed_bottle");
	}

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
	}

	@SubscribeEvent
	public void init(FMLCommonSetupEvent event) {
		proxy.init();

		InspirationsRegistry.registerAnvilBreaking(Material.GLASS);
		if(Config.enableCauldronRecipes()) {
			registerCauldronRecipes();
		}
		registerDispenserBehavior();
	}

	@SubscribeEvent
	public void postInit(InterModProcessEvent event) {
		proxy.postInit();
		MinecraftForge.EVENT_BUS.register(RecipesEvents.class);
		registerPostCauldronRecipes();
	}

	private void registerCauldronRecipes() {
		InspirationsRegistry.registerDefaultCauldron();
		InspirationsRegistry.addCauldronRecipe(new FillCauldronRecipe(RecipeMatch.of(Blocks.ICE), FluidRegistry.WATER, InspirationsRegistry.getCauldronMax(), ItemStack.EMPTY, true, SoundEvents.ITEM_BUCKET_EMPTY_LAVA));
		if(Config.canSpongeEmptyCauldron()) {
			InspirationsRegistry.addCauldronRecipe(SpongeEmptyCauldron.INSTANCE);
		}
		if(Config.cauldronObsidian.get()) {
			ICauldronRecipe recipe;
			// minor detail: if the cauldron can hold fluids, show the lava in the cauldron in JEI
			// else show water in the cauldron as lava is not allowed
			// in either case both are supported
			if(Config.enableCauldronFluids()) {
				recipe = new CauldronMixRecipe(FluidRegistry.LAVA, FluidRegistry.WATER, new ItemStack(Blocks.OBSIDIAN));
			} else {
				recipe = new CauldronMixRecipe(FluidRegistry.WATER, FluidRegistry.LAVA, new ItemStack(Blocks.OBSIDIAN));
			}
			InspirationsRegistry.addCauldronRecipe(recipe);
		}

		if(!Config.enableExtendedCauldron()) {
			return;
		}

		// reimplemented vanilla recipes
		InspirationsRegistry.addCauldronRecipe(new ArmorClearRecipe(ItemArmor.ArmorMaterial.LEATHER));
		InspirationsRegistry.addCauldronRecipe(BannerClearRecipe.INSTANCE);
		// fill from water bottle, does not use the shortcut as we need NBT matching
		ItemStack waterBottle = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
		InspirationsRegistry.addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(Items.GLASS_BOTTLE), FluidRegistry.WATER, waterBottle, null, SoundEvents.ITEM_BOTTLE_FILL));
		InspirationsRegistry.addCauldronRecipe(new FillCauldronRecipe(RecipeMatch.ofNBT(waterBottle), FluidRegistry.WATER, 1, new ItemStack(Items.GLASS_BOTTLE)));

		if(Config.enableCauldronDyeing()) {
			InspirationsRegistry.addCauldronRecipe(FillDyedBottleFromCauldron.INSTANCE);
			InspirationsRegistry.addCauldronRecipe(FillCauldronFromDyedBottle.INSTANCE);
			InspirationsRegistry.addCauldronRecipe(new ArmorDyeingCauldronRecipe(ItemArmor.ArmorMaterial.LEATHER));

			for(DyeColor color : DyeColor.values()) {
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
				for(DyeColor color : DyeColor.values()) {
					InspirationsRegistry.addCauldronRecipe(new CauldronDyeRecipe(
							anyTrapdoor, color,
							new ItemStack(InspirationsUtility.carpetedTrapdoors[color.getId()])
							));
				}
			}
		}

		if(Config.enableCauldronPotions()) {
			addPotionBottle(Items.POTION, new ItemStack(Items.GLASS_BOTTLE), "bottles/normal");
			addPotionBottle(Items.SPLASH_POTION, new ItemStack(InspirationsShared.splashBottle), "bottles/splash");
			addPotionBottle(Items.LINGERING_POTION, new ItemStack(InspirationsShared.lingeringBottle), "bottles/lingering");
			if (Config.cauldronTipArrows()) {
				InspirationsRegistry.addCauldronRecipe(TippedArrowCauldronRecipe.INSTANCE);
			}
		}

		if(Config.enableCauldronFluids()) {
			InspirationsRegistry.addCauldronRecipe(FillFluidContainerFromCauldron.INSTANCE);

			addStewRecipes(new ItemStack(Items.BEETROOT_SOUP), beetrootSoup, new ItemStack(Items.BEETROOT, 6));
			addStewRecipes(new ItemStack(Items.MUSHROOM_STEW), mushroomStew, new ItemStack(InspirationsShared.mushrooms));
			addStewRecipes(new ItemStack(Items.RABBIT_STEW), rabbitStew, new ItemStack(InspirationsShared.rabbitStewMix));
		} else {
			// above relied on for bucket filling cauldron
			InspirationsRegistry.addCauldronFluidItem(new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.BUCKET), FluidRegistry.WATER, 3);
		}
	}

	/**
	 * These recipes need to be registered later to prevent from conflicts or missing recipes
	 */
	private void registerPostCauldronRecipes() {
		if(Config.enableCauldronBrewing()) {
			for(Object recipe : PotionUtils.POTION_TYPE_CONVERSIONS) {
				Potion input = ReflectionUtil.getMixPredicateInput(recipe);
				Ingredient reagent = ReflectionUtil.getMixPredicateReagent(recipe);
				Potion output = ReflectionUtil.getMixPredicateOutput(recipe);
				if(input != null && reagent != null && output != null) {
					InspirationsRegistry.addCauldronRecipe(new CauldronBrewingRecipe(input, reagent, output));
				}
			}
			findRecipesFromBrewingRegistry();
		}
		if(Config.enableCauldronFluids()) {
			InspirationsRegistry.addCauldronRecipe(FillCauldronFromFluidContainer.INSTANCE);
		}
	}

	private static void addPotionBottle(Item potion, ItemStack bottle, String bottleTag) {
		InspirationsRegistry.addCauldronRecipe(new FillCauldronFromPotion(potion, bottle));
		InspirationsRegistry.addCauldronRecipe(new FillPotionFromCauldron(potion,
				new ItemTags.Wrapper(new ResourceLocation("forge", bottleTag))
		));
	}

	private static void addStewRecipes(ItemStack stew, Fluid fluid, ItemStack ingredient) {
		InspirationsRegistry.addCauldronScaledTransformRecipe(ingredient, FluidRegistry.WATER, fluid, true);
		// filling and emptying bowls
		InspirationsRegistry.addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(Items.BOWL), fluid, stew, null, SoundEvents.ITEM_BOTTLE_FILL));
		InspirationsRegistry.addCauldronRecipe(new FillCauldronRecipe(RecipeMatch.of(stew), fluid, 1, new ItemStack(Items.BOWL)));
	}

	private void findRecipesFromBrewingRegistry() {
		for(IBrewingRecipe irecipe : BrewingRecipeRegistry.getRecipes()) {
			if(irecipe instanceof BrewingRecipe) {

				BrewingRecipe recipe = (BrewingRecipe) irecipe;
				Ingredient inputIngredient = recipe.getInput();
				ItemStack outputStack = recipe.getOutput();
				Ingredient ingredient = recipe.getIngredient();

				// null checks because some dumb mod is returning null for the input or output
				if (ingredient == null || inputIngredient == null || outputStack == null){
					continue;
				}

				ItemStack inputStack = ItemStack.EMPTY;
				for (ItemStack validInput: inputIngredient.getMatchingStacks()) {
					if (validInput.getItem() == Items.POTION) {
						inputStack = validInput;
						break;
					}
				}

				if(!inputStack.isEmpty() && outputStack.getItem() == Items.POTION) {
					Potion input = PotionUtils.getPotionFromItem(inputStack);
					Potion output = PotionUtils.getPotionFromItem(outputStack);
					if(input != Potions.EMPTY && output != Potions.EMPTY) {
						InspirationsRegistry.addCauldronRecipe(new CauldronBrewingRecipe(input, ingredient, output));
					}
				}
			}
		}
	}

	private void registerDispenserBehavior() {
		if(Config.enableCauldronDispenser()) {
			for(Item item : InspirationsRegistry.TAG_DISP_FLUID_TANKS.getAllElements()) {
				registerDispenserBehavior(
						item,
						new DispenseCauldronRecipe(DispenserBlock.DISPENSE_BEHAVIOR_REGISTRY.get(item))
				);
			}
		}
	}
}
