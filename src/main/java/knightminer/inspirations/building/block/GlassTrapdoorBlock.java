package knightminer.inspirations.building.block;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.IHidable;
import net.minecraft.block.TrapDoorBlock;

public class GlassTrapdoorBlock extends TrapDoorBlock implements IHidable {

  public GlassTrapdoorBlock(Properties props) {
    super(props);
  }

  @Override
  public boolean isEnabled() {
    return Config.enableGlassDoor.get();
  }
}
