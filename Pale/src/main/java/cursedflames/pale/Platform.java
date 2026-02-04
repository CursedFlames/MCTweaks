package cursedflames.pale;

//? fabric {
import cursedflames.pale.fabric.FabricPlatformImpl;
//?}
//? neoforge {
/*import cursedflames.pale.neoforge.NeoforgePlatformImpl;
*///?}
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

public interface Platform {

    //? fabric {
    Platform INSTANCE = new FabricPlatformImpl();
    //?}
    //? neoforge {
    /*Platform INSTANCE = new NeoforgePlatformImpl();
    *///?}


    boolean isModLoaded(String modid);
    String loader();

    Holder<MobEffect> getPaleEffect();

}
