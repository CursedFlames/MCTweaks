package cursedflames.hopperbucket.mixin.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

import static cursedflames.hopperbucket.Util.getFirstMatching;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {
	// We would target `ejectItems` like on fabric, but forge changes the signature which seems to affect mixin targeting
	@Inject(method = "tryMoveItems", at = @At(value = "INVOKE", shift= At.Shift.BEFORE, target="Lnet/minecraft/world/level/block/entity/HopperBlockEntity;ejectItems(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/HopperBlockEntity;)Z"))
	private static void onTryMoveItems(Level level, BlockPos pos, BlockState blockState, HopperBlockEntity hopper, BooleanSupplier _unused, CallbackInfoReturnable<Boolean> cir) {
		var bucketStackIndex = getFirstMatching(hopper, itemStack -> itemStack.getItem() instanceof BucketItem && itemStack.getItem() != Items.BUCKET);
		if (bucketStackIndex == -1) return;
		var bucketStack = hopper.getItem(bucketStackIndex);
		if (bucketStack.getItem() instanceof DispensibleContainerItem dispensible) {
			Direction direction = blockState.getValue(HopperBlock.FACING);
			var outputPos = pos.relative(direction);
			if (dispensible.emptyContents(null, level, outputPos, null)) {
				dispensible.checkExtraContent(null, level, bucketStack, outputPos);
				hopper.setItem(bucketStackIndex, new ItemStack(Items.BUCKET));
			}
		}
	}
}
