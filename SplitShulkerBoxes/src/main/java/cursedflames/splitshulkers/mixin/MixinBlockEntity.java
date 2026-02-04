package cursedflames.splitshulkers.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("CancellableInjectionUsage") // injected methods are overriden in MixinShulkerBoxBlockEntity and must be cancellable there
@Mixin(BlockEntity.class)
public class MixinBlockEntity {
	@Inject(method = "getUpdatePacket", at = @At("HEAD"), cancellable = true)
	protected void onGetUpdatePacket(CallbackInfoReturnable<ClientboundBlockEntityDataPacket> cir) {

	}

	@Inject(method = "getUpdateTag", at = @At("HEAD"), cancellable = true)
	protected void onGetUpdateTag(HolderLookup.Provider provider, CallbackInfoReturnable<CompoundTag> cir) {
	}
}
