package cursedflames.hopperbucket.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.function.BooleanSupplier;

import static cursedflames.hopperbucket.Util.getFirstEmptySlot;
import static cursedflames.hopperbucket.Util.getFirstMatching;
import static cursedflames.hopperbucket.Util.getFirstStackIndex;
import static cursedflames.hopperbucket.Util.getFirstStackOfSizeOneIndex;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {
	@Inject(method = "tryMoveItems", cancellable = true, at = @At(value = "INVOKE", target = "Ljava/util/function/BooleanSupplier;getAsBoolean()Z"))
	private static void onSuckInItems(Level level, BlockPos unusedPos, BlockState unusedBlockState, HopperBlockEntity hopper, BooleanSupplier booleanSupplier, CallbackInfoReturnable<Boolean> cir) {
		if (!hopper.hasAnyOf(Collections.singleton(Items.BUCKET))) return;
		var emptySlotIndex = getFirstEmptySlot(hopper);
		var bucketStackIndex = emptySlotIndex == -1 ? getFirstStackOfSizeOneIndex(hopper, Items.BUCKET) : getFirstStackIndex(hopper, Items.BUCKET);
		if (bucketStackIndex == -1) return;
		var bucketStack = hopper.getItem(bucketStackIndex);

		var pos = BlockPos.containing(hopper.getLevelX(), hopper.getLevelY() + 1.0, hopper.getLevelZ());
		var blockState = level.getBlockState(pos);
		if (blockState.getBlock() instanceof BucketPickup bucketPickup) {
			var pickup = bucketPickup.pickupBlock(null, level, pos, blockState);
			if (pickup.isEmpty()) return;
			//? neoforge {
			/*bucketPickup.getPickupSound(blockState).ifPresent(soundEvent -> level.playSound(null, pos, soundEvent, SoundSource.BLOCKS));
			*///?} else {
			bucketPickup.getPickupSound().ifPresent(soundEvent -> level.playSound(null, pos, soundEvent, SoundSource.BLOCKS));
			//?}
			level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
			bucketStack.shrink(1);
			if (bucketStack.isEmpty()) {
				hopper.setItem(bucketStackIndex, pickup);
			} else {
				hopper.setItem(emptySlotIndex, pickup);
			}
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "tryMoveItems", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;ejectItems(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/HopperBlockEntity;)Z"))
	private static void onEjectItems(Level level, BlockPos pos, BlockState blockState, HopperBlockEntity hopper, BooleanSupplier booleanSupplier, CallbackInfoReturnable<Boolean> cir) {
		var bucketStackIndex = getFirstMatching(hopper, itemStack -> itemStack.getItem() instanceof BucketItem && itemStack.getItem() != Items.BUCKET);
		if (bucketStackIndex == -1) return;
		var bucketStack = hopper.getItem(bucketStackIndex);
		if (bucketStack.getItem() instanceof DispensibleContainerItem dispensible) {
			Direction direction = blockState.getValue(HopperBlock.FACING);
			var outputPos = pos.relative(direction);
			//? neoforge {
			/*if (dispensible.emptyContents(null, level, outputPos, null, bucketStack)) {
			*///?} else {
			if (dispensible.emptyContents(null, level, outputPos, null)) {
			 //?}
				dispensible.checkExtraContent(null, level, bucketStack, outputPos);
				hopper.setItem(bucketStackIndex, new ItemStack(Items.BUCKET));
				cir.setReturnValue(true);
			}
		}
	}
}
