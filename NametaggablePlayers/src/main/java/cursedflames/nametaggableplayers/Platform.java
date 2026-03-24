package cursedflames.nametaggableplayers;

//? fabric {
import cursedflames.nametaggableplayers.fabric.FabricPlatformImpl;
//?}
//? neoforge {
/*import cursedflames.nametaggableplayers.neoforge.NeoforgePlatformImpl;
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
