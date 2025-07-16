package cursedflames.pale.platform;

import cursedflames.pale.PaleNeoforge;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

public class NeoforgePlatformHelper implements PlatformHelper {
	@Override
	public Holder<MobEffect> getPaleEffect() {
		// Reference field from PaleNeoforge to trigger clinit and initialize the field
		return PaleNeoforge.getPaleEffect();
	}
}
