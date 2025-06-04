package knightminer.inspirations.common.datagen;

import knightminer.inspirations.Inspirations;
import knightminer.inspirations.cauldrons.InspirationsCaudrons;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/** Creates damage types and damage type tags */
public class InspirationsDamageTypeProvider extends DamageTypeTagsProvider {
  public InspirationsDamageTypeProvider(PackOutput pOutput, CompletableFuture<Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
    super(pOutput, pLookupProvider, Inspirations.modID, existingFileHelper);
  }

  /** Registers this provider with the registry set builder */
  public static void registerGenerator(RegistrySetBuilder builder) {
    builder.add(Registries.DAMAGE_TYPE, InspirationsDamageTypeProvider::generateDamageTypes);
  }

  /** Creates all damage sources */
  private static void generateDamageTypes(BootstapContext<DamageType> context) {
    context.register(InspirationsCaudrons.DAMAGE_BOIL, new DamageType(Inspirations.prefix("boiling"), 0.1f, DamageEffects.BURNING));
  }

  @Override
  protected void addTags(Provider provider) {
    tag(DamageTypeTags.BYPASSES_ARMOR).add(InspirationsCaudrons.DAMAGE_BOIL);
  }
}
