package cursedflames.pale.fabric;

//? fabric {
import cursedflames.pale.Pale;
import cursedflames.pale.Platform;
import cursedflames.pale.StatusEffectPale;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class FabricPlatformImpl implements Platform {
    @Override
    public Holder<MobEffect> getPaleEffect() {
        if (Pale.PALE_EFFECT == null) {
            Pale.PALE_EFFECT = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, StatusEffectPale.ID, new StatusEffectPale(MobEffectCategory.HARMFUL, 0xffb340));
        }
        return Pale.PALE_EFFECT;
    }

    @Override
    public boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    @Override
    public String loader() {
        return "fabric";
    }

}
//?}