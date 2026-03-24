package cursedflames.nametaggableplayers;

import cursedflames.nametaggableplayers.config.NametaggablePlayersConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NametaggablePlayers {
    public static final String MOD_ID = /*$ mod_id*/ "nametaggableplayers";
	public static final String MOD_VERSION = /*$ mod_version*/ "1.0.0";
	public static final String MOD_NAME = /*$ mod_name*/ "NametaggablePlayers";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
		NametaggablePlayersConfig.init();
    }

}
