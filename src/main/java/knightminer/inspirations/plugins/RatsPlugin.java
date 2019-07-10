package knightminer.inspirations.plugins;

import com.google.common.eventbus.Subscribe;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
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
    @GameRegistry.ObjectHolder(RatsPlugin.requiredModID + ":cauldron_milk")
    public static final Block milkCauldron = null;


    @Subscribe
    public void init(FMLInitializationEvent event) {
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
