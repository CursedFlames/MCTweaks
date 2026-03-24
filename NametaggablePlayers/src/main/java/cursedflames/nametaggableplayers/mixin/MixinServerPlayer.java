package cursedflames.nametaggableplayers.mixin;

import cursedflames.nametaggableplayers.config.NametaggablePlayersConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends Player {
	public MixinServerPlayer() {
		//?if < 1.21.6 {
		super(null, null, 0f, null);
		//?} else {
		//super(null, null);
 		//?}
	}

	@Inject(method = "restoreFrom", at = @At("HEAD"))
	private void onRestoreFrom(ServerPlayer serverPlayer, boolean flag, CallbackInfo ci) {
		// flag indicates a teleport rather than a death respawn
		if (flag || !NametaggablePlayersConfig.configInstance.resetNamesOnDeath) {
			this.setCustomName(serverPlayer.getCustomName());
		}
	}
}
