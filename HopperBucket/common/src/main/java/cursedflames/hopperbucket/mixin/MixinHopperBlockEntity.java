package cursedflames.hopperbucket.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;

import static cursedflames.hopperbucket.Util.getFirstEmptySlot;
import static cursedflames.hopperbucket.Util.getFirstStackIndex;
import static cursedflames.hopperbucket.Util.getFirstStackOfSizeOneIndex;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {
	@Inject(method = "suckInItems", at = @At("HEAD"), cancellable = true)
	private static void onSuckInItems(Level level, Hopper hopper, CallbackInfoReturnable<Boolean> cir) {
		if (!hopper.hasAnyOf(Collections.singleton(Items.BUCKET))) return;
		var emptySlotIndex = getFirstEmptySlot(hopper);
		var bucketStackIndex = emptySlotIndex == -1 ? getFirstStackOfSizeOneIndex(hopper, Items.BUCKET) : getFirstStackIndex(hopper, Items.BUCKET);
		var bucketStack = hopper.getItem(bucketStackIndex);
		if (bucketStack.getCount() > 1 && emptySlotIndex == -1) return;

		var pos = BlockPos.containing(hopper.getLevelX(), hopper.getLevelY() + 1.0, hopper.getLevelZ());
		var blockState = level.getBlockState(pos);
		if (blockState.getBlock() instanceof BucketPickup bucketPickup) {
			var pickup = bucketPickup.pickupBlock(level, pos, blockState);
			if (pickup.isEmpty()) return;
			bucketPickup.getPickupSound().ifPresent(soundEvent -> level.playSound(null, pos, soundEvent, SoundSource.BLOCKS));
			level.gameEvent(hopper instanceof Entity ? (Entity) hopper : null, GameEvent.FLUID_PICKUP, pos);
			bucketStack.shrink(1);
			if (bucketStack.isEmpty()) {
				hopper.setItem(bucketStackIndex, pickup);
			} else {
				hopper.setItem(emptySlotIndex, pickup);
			}
			cir.setReturnValue(true);
		}
	}
}
