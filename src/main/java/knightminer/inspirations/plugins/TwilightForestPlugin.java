package knightminer.inspirations.plugins;

import com.google.common.eventbus.Subscribe;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.recipe.ArmorClearRecipe;
import knightminer.inspirations.recipes.recipe.ArmorDyeingCauldronRecipe;
import net.minecraft.item.ArmorMaterial;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(
    id = TwilightForestPlugin.pulseID,
    description = "Supports dyeing Twilight Forest armor in the Inspirations cauldron.",
    modsRequired = TwilightForestPlugin.requiredModID,
    pulsesRequired = InspirationsRecipes.pulseID
)
public class TwilightForestPlugin {
  public static final String pulseID = "TwilightForestPlugin";
  public static final String requiredModID = "twilightforest";

  @SubscribeEvent
  public void init(FMLCommonSetupEvent event) {
    if (!Config.enableCauldronRecipes()) {
      return;
    }
    try {
      ArmorMaterial material = ArmorMaterial.valueOf("ARCTIC");
      InspirationsRegistry.addCauldronRecipe(new ArmorClearRecipe(material));
      if (Config.enableCauldronDyeing()) {
        InspirationsRegistry.addCauldronRecipe(new ArmorDyeingCauldronRecipe(material));
      }
    } catch(IllegalArgumentException e) {
      Inspirations.log.error("Skipping registering Twilight Forest plugin, cannot find arctic armor material");
    }
  }
}
