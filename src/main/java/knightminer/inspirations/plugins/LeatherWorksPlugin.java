package knightminer.inspirations.plugins;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.CauldronFluidRecipe;
import knightminer.inspirations.library.recipe.cauldron.CauldronFluidTransformRecipe;
import knightminer.inspirations.recipes.InspirationsRecipes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ObjectHolder;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;

@Pulse(
        id = LeatherWorksPlugin.pulseID,
        description = "Adds recipes for Leather Works items",
        modsRequired = LeatherWorksPlugin.requiredModID,
        pulsesRequired = InspirationsRecipes.pulseID
)
public class LeatherWorksPlugin extends PulseBase{
    public static final String pulseID = "LeatherWorks";

    public static final String requiredModID = "leatherworks";

    @ObjectHolder(LeatherWorksPlugin.requiredModID + ":tannin_ball")
    public static final Item tanninBall = null;
    @ObjectHolder(LeatherWorksPlugin.requiredModID + ":tannin_bottle")
    public static final Item tanninBottle = null;
    @ObjectHolder(LeatherWorksPlugin.requiredModID + ":crafting_leather_scraped")
    public static final Item preparedHide = null;
    @ObjectHolder(LeatherWorksPlugin.requiredModID + ":crafting_leather_soaked")
    public static final Item soakedHide = null;
    @ObjectHolder(LeatherWorksPlugin.requiredModID + ":crafting_leather_washed")
    public static final Item washedHide = null;

    @SubscribeEvent
    public void init(FMLCommonSetupEvent e){
        // we need cauldron fluids for this to work
        if(!Config.enableCauldronFluids()){
            return;
        }

        // get tannin fluid
        Fluid tannin =  FluidRegistry.getFluid("leatherworks:tannin");

        // register recipes
        if (tannin != null){
            // tannin creation in cauldron using tannin ball
            if (tanninBall != null){
                InspirationsRegistry.addCauldronRecipe(new CauldronFluidTransformRecipe(RecipeMatch.of(tanninBall),FluidRegistry.WATER,tannin,false));
            }

            // adds tannin bottle as fluid source
            if (tanninBottle != null){
                InspirationsRegistry.addCauldronFluidItem(new ItemStack(tanninBottle), new ItemStack(Items.GLASS_BOTTLE),tannin,1);
            }

            // add recipe for washed hide and soaked hide
            // washed hide = 1 bottle of water + prepared hide
            // soaked hide = 1 bottle of tannin + washed hide
            if (preparedHide != null && soakedHide != null && washedHide != null) {
                InspirationsRegistry.addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(preparedHide,1),FluidRegistry.WATER,new ItemStack(washedHide),false,1));
                InspirationsRegistry.addCauldronRecipe(new CauldronFluidRecipe(RecipeMatch.of(washedHide,1),tannin,new ItemStack(soakedHide),false,1));
            }
        }
    }
}
