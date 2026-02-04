package cursedflames.pale.neoforge;

//? neoforge {
/*import cursedflames.pale.Pale;
import cursedflames.pale.Platform;
import cursedflames.pale.StatusEffectPale;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;

public class NeoforgePlatformImpl implements Platform {
	@Override
	public Holder<MobEffect> getPaleEffect() {
		if (Pale.PALE_EFFECT == null) {
			Pale.PALE_EFFECT = DeferredHolder.create(Registries.MOB_EFFECT, StatusEffectPale.ID);
		}
		return Pale.PALE_EFFECT;
	}

    @Override
    public boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }

    @Override
    public String loader() {
        return "neoforge";
    }

}
*///?}