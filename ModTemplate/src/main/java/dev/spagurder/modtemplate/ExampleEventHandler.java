package dev.spagurder.modtemplate;

import net.minecraft.server.level.ServerPlayer;

public class ExampleEventHandler {

    public static void onPlayerHurt(ServerPlayer player) {
        // MinecraftServer.enforceWhitelist is private... only here to test ATs/AWs
        if (player.level().getServer().enforceWhitelist) {
            ModTemplate.LOG.info("{} took damage. Using whitelist.", player.getDisplayName().getString());
        } else {
            ModTemplate.LOG.info("{} took damage. Not using whitelist.", player.getDisplayName().getString());
        }
    }

}
