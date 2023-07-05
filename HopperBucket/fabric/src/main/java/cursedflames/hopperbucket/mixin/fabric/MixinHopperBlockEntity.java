package cursedflames.hopperbucket.mixin.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
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

import static cursedflames.hopperbucket.Util.getFirstMatching;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {
	@Inject(method = "ejectItems", at = @At("HEAD"), cancellable = true)
	private static void onEjectItems(Level level, BlockPos pos, BlockState blockState, Container hopper, CallbackInfoReturnable<Boolean> cir) {
		var bucketStackIndex = getFirstMatching(hopper, itemStack -> itemStack.getItem() instanceof BucketItem && itemStack.getItem() != Items.BUCKET);
		if (bucketStackIndex == -1) return;
		var bucketStack = hopper.getItem(bucketStackIndex);
		if (bucketStack.getItem() instanceof DispensibleContainerItem dispensible) {
			Direction direction = blockState.getValue(HopperBlock.FACING);
			var outputPos = pos.relative(direction);
			if (dispensible.emptyContents(null, level, outputPos, null)) {
				dispensible.checkExtraContent(null, level, bucketStack, outputPos);
				hopper.setItem(bucketStackIndex, new ItemStack(Items.BUCKET));
				cir.setReturnValue(true);
			}
		}
	}
}
