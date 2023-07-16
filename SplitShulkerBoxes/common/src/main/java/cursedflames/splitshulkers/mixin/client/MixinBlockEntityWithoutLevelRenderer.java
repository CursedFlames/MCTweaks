package cursedflames.splitshulkers.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import cursedflames.splitshulkers.SplitShulkerBoxBlockEntity;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static cursedflames.splitshulkers.SplitShulkers.allColorPairs;
import static cursedflames.splitshulkers.SplitShulkers.secondaryColorFromTag;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class MixinBlockEntityWithoutLevelRenderer {
	@Shadow @Final private static ShulkerBoxBlockEntity[] SHULKER_BOXES;
	@Shadow @Final private static ShulkerBoxBlockEntity DEFAULT_SHULKER_BOX;
	private static final ShulkerBoxBlockEntity[] splitshulkers_AllShulkerBoxes;
	static {
		splitshulkers_AllShulkerBoxes = new ShulkerBoxBlockEntity[17*17];
		for (int i = 0; i < 17*17; i++) {
			var pair = allColorPairs.get(i);
			var color1 = pair.left();
			var color2 = pair.right();
			if (color2 == color1) {
				splitshulkers_AllShulkerBoxes[i] = color1 == null ? DEFAULT_SHULKER_BOX : SHULKER_BOXES[color1.getId()];
			} else {
				var shulker = new ShulkerBoxBlockEntity(color1, BlockPos.ZERO, Blocks.SHULKER_BOX.defaultBlockState());
				((SplitShulkerBoxBlockEntity) shulker).splitshulkers_setSecondaryColor(color2);
				splitshulkers_AllShulkerBoxes[i] = shulker;
			}
		}
	}

	@Shadow @Final private BlockEntityRenderDispatcher blockEntityRenderDispatcher;

	@Inject(method = "renderByItem", cancellable = true, at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/world/level/block/ShulkerBoxBlock;getColorFromItem(Lnet/minecraft/world/item/Item;)Lnet/minecraft/world/item/DyeColor;"))
	private void onRenderByItem(ItemStack stack, ItemTransforms.TransformType displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int i, int j, CallbackInfo ci) {
		var color1 = ShulkerBoxBlock.getColorFromItem(stack.getItem());
		var tag = BlockItem.getBlockEntityData(stack);
		var color2 = secondaryColorFromTag(tag, color1);
		var index = 17 * (color1 == null ? 0 : color1.getId()+1) + (color2 == null ? 0 : color2.getId()+1);
		this.blockEntityRenderDispatcher.renderItem(splitshulkers_AllShulkerBoxes[index], poseStack, bufferSource, i, j);
		ci.cancel();
	}
}
