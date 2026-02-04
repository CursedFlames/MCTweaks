package cursedflames.splitshulkers;

//? fabric {
import cursedflames.splitshulkers.fabric.FabricPlatformImpl;
//?}
//? neoforge {
/*import cursedflames.splitshulkers.neoforge.NeoforgePlatformImpl;
*///?}

public interface Platform {

    //? fabric {
    Platform INSTANCE = new FabricPlatformImpl();
    //?}
    //? neoforge {
    /*Platform INSTANCE = new NeoforgePlatformImpl();
    *///?}


    boolean isModLoaded(String modid);
    String loader();

}
