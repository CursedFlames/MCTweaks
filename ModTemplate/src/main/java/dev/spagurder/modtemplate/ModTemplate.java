package dev.spagurder.modtemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModTemplate {
    public static final String MOD_ID = /*$ mod_id*/ "modtemplate";
	public static final String MOD_VERSION = /*$ mod_version*/ "0.1.0";
	public static final String MOD_NAME = /*$ mod_name*/ "Mod Template";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOG.info("Initializing {} on {}", MOD_ID, Platform.INSTANCE.loader());
    }

}
