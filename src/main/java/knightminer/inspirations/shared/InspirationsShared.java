package knightminer.inspirations.shared;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.ModuleBase;
import knightminer.inspirations.common.data.ConfigEnabledCondition;
import knightminer.inspirations.common.data.FillTexturedBlockLootFunction;
import knightminer.inspirations.library.recipe.ModItemList;
import knightminer.inspirations.library.recipe.ShapelessNoContainerRecipe;
import knightminer.inspirations.library.recipe.TextureRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Base module for common code between the modules
 */
public class InspirationsShared extends ModuleBase {
	public static LootConditionType lootConfig;
	public static LootFunctionType textureFunction;


	@SuppressWarnings("Convert2MethodRef")
	public static Object proxy = DistExecutor.callWhenOn(Dist.CLIENT, ()->()->new SharedClientProxy());

	@SubscribeEvent
	void setup(FMLCommonSetupEvent event) {
		MinecraftForge.EVENT_BUS.addListener(SharedEvents::updateMilkCooldown);
	}

	@SubscribeEvent
	void registerRecipeTypes(RegistryEvent.Register<IRecipeSerializer<?>> event) {
		IForgeRegistry<IRecipeSerializer<?>> r = event.getRegistry();
		r.register(ShapelessNoContainerRecipe.SERIALIZER);
		r.register(TextureRecipe.SERIALIZER);

		// no event registries
		// config condition
		ConfigEnabledCondition.Serializer confEnabled = new ConfigEnabledCondition.Serializer();
		CraftingHelper.register(confEnabled);
		lootConfig = register(Registry.LOOT_CONDITION_TYPE, "config", new LootConditionType(confEnabled));

		// recipe ingredient type
		CraftingHelper.register(Inspirations.getResource("mod_item_list"), ModItemList.SERIALIZER);

		// texture block function
		textureFunction = register(Registry.LOOT_FUNCTION_TYPE, "fill_textured_block", new LootFunctionType(new FillTexturedBlockLootFunction.Serializer()));
	}
}
