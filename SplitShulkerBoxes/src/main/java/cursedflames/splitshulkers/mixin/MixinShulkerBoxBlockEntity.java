package cursedflames.splitshulkers.mixin;

import cursedflames.splitshulkers.SplitShulkerBoxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static cursedflames.splitshulkers.SplitShulkerBoxes.secondaryColorFromTag;
import static cursedflames.splitshulkers.SplitShulkerBoxes.secondaryColorToTag;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class MixinShulkerBoxBlockEntity extends MixinBlockEntity implements SplitShulkerBoxBlockEntity {
	@Shadow @Final @Nullable
	private DyeColor color;
	@Nullable private DyeColor splitshulkers_secondaryColor;

	@Inject(method = "<init>(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At("RETURN"))
	private void onInit(BlockPos $$0, BlockState $$1, CallbackInfo ci) {
		this.splitshulkers_secondaryColor = this.color;
	}
	@Inject(method = "<init>(Lnet/minecraft/world/item/DyeColor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At("RETURN"))
	private void onInit(DyeColor $$0, BlockPos $$1, BlockState $$2, CallbackInfo ci) {
		this.splitshulkers_secondaryColor = this.color;
	}

	@Accessor
	public abstract DyeColor getColor();

	@Nullable
	@Override
	public DyeColor splitshulkers_getSecondaryColor() {
		return splitshulkers_secondaryColor;
	}

	@Override
	public void splitshulkers_setSecondaryColor(@Nullable DyeColor color) {
		splitshulkers_secondaryColor = color;
	}

	@Inject(method = "saveAdditional", at = @At(value = "HEAD"))
	public void onSaveAdditional(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
		if (this.getColor() != splitshulkers_secondaryColor) {
			secondaryColorToTag(splitshulkers_secondaryColor, tag);
		}
	}

	@Inject(method = "loadFromTag", at = @At("HEAD"))
	public void onLoadFromTag(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
		splitshulkers_secondaryColor = secondaryColorFromTag(tag, this.getColor());
	}

	@Override
	protected void onGetUpdatePacket(CallbackInfoReturnable<ClientboundBlockEntityDataPacket> cir) {
		cir.setReturnValue(ClientboundBlockEntityDataPacket.create((BlockEntity) (Object) this));
	}

	@Override
	protected void onGetUpdateTag(HolderLookup.Provider provider, CallbackInfoReturnable<CompoundTag> cir) {
		var color = this.getColor();
		var color2 = this.splitshulkers_secondaryColor;
		if (color != color2) {
			var tag = new CompoundTag();
			secondaryColorToTag(color2, tag);
			cir.setReturnValue(tag);
		}
	}
}
