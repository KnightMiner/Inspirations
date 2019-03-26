package knightminer.inspirations.plugins.leatherworks;

import com.google.common.eventbus.Subscribe;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.CauldronFluidRecipe;
import knightminer.inspirations.library.recipe.cauldron.CauldronFluidTransformRecipe;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;

@Pulse(
        id = LeatherWorksPlugin.pulseID,
        description = "Adds leather works support",
        modsRequired = LeatherWorksPlugin.requiredModID,
        pulsesRequired = InspirationsRecipes.pulseID
)
public class LeatherWorksPlugin extends PulseBase
{
    public static final String pulseID = "LeatherWorks";

    public static final String requiredModID = "leatherworks";

    private static Logger log;

    @GameRegistry.ObjectHolder(LeatherWorksPlugin.requiredModID + ":tannin_ball")
    public static final Item tanninBall = null;

    @GameRegistry.ObjectHolder(LeatherWorksPlugin.requiredModID + ":crafting_leather_scraped")
    public static final Item preparedHide = null;

    @GameRegistry.ObjectHolder(LeatherWorksPlugin.requiredModID + ":crafting_leather_soaked")
    public static final Item soakedHide = null;

    @GameRegistry.ObjectHolder(LeatherWorksPlugin.requiredModID + ":crafting_leather_washed")
    public static final Item washedHide = null;

    static
    {
        log = LogManager.getLogger("LWPlugin - " + Inspirations.modID);
    }

    @Subscribe
    public void init(FMLInitializationEvent e)
    {
        // we need cauldron fluids for this to work
        if(!Config.enableExtendedCauldron) {
            return;
        }

        Fluid tannin =  FluidRegistry.getFluid("leatherworks:tannin");
        if (tannin != null)
        {
            InspirationsRegistry.addCauldronWater(tannin);

            if (tanninBall != null)
            {

                InspirationsRegistry.addCauldronRecipe(new CauldronFluidTransformRecipe(RecipeMatch.of(tanninBall),FluidRegistry.WATER,tannin,false));

            }

            if (preparedHide != null && soakedHide != null && washedHide != null)
            {

                InspirationsRegistry.addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(preparedHide,1),FluidRegistry.WATER,new ItemStack(washedHide),false,1));
                InspirationsRegistry.addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(washedHide,1),tannin,new ItemStack(soakedHide),false,1));

            }

        }
    }
}
