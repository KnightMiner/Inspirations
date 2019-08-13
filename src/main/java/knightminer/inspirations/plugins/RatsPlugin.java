package knightminer.inspirations.plugins;

import com.google.common.eventbus.Subscribe;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ObjectHolder;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(
        id = RatsPlugin.pulseID,
        description = "Supports the Rats milk cauldron as part of cauldron recipes. Note extended features such as recipes from dropping items in cauldrons will now work.",
        modsRequired = RatsPlugin.requiredModID,
        pulsesRequired = InspirationsRecipes.pulseID
)
public class RatsPlugin extends PulseBase{
    public static final String pulseID = "RatsPlugin";

    public static final String requiredModID = "rats";
    @ObjectHolder(RatsPlugin.requiredModID + ":cauldron_milk")
    public static final Block milkCauldron = null;


    @Subscribe
    public void init(FMLCommonSetupEvent event) {
        if (!Config.enableCauldronFluids()) {
            return;
        }
        final Fluid milk = FluidRegistry.getFluid("milk");
        if (milkCauldron != null && milk != null) {
            InspirationsRegistry.registerFullCauldron(milkCauldron.getDefaultState(), ICauldronRecipe.CauldronState.fluid(milk));
        } else if (milkCauldron == null) {
            Inspirations.log.error("Skipping registering Rats plugin, failed to find Rats milk cauldron");
        } else {
            Inspirations.log.info("Skipping registering Rats plugin, milk not registered");
        }
    }
}
