package knightminer.inspirations.plugins.top;

import javax.annotation.Nullable;

import com.google.common.base.Function;

import mcjty.theoneprobe.api.ITheOneProbe;

public class TheOneProbeRegistrar implements Function<ITheOneProbe, Void> {

	@Nullable
	@Override
	public Void apply(ITheOneProbe probe) {
		probe.registerProvider(new CauldronInfoProvider());
		return null;
	}
}
