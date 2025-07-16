package cursedflames.pale.platform;

import cursedflames.pale.PaleFabric;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

public class FabricPlatformHelper implements PlatformHelper {
	@Override
	public Holder<MobEffect> getPaleEffect() {
		// Reference field from PaleFabric to trigger clinit and initialize the field
		return PaleFabric.getPaleEffect();
	}
}
