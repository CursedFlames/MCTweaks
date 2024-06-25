package cursedflames.splitshulkers.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import cursedflames.splitshulkers.SplitShulkerBoxBlockEntity;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static cursedflames.splitshulkers.SplitShulkers.allColorPairs;
import static cursedflames.splitshulkers.SplitShulkers.getItemBlockEntityTagUnsafe;
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

	@WrapOperation(method = "renderByItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;renderItem(Lnet/minecraft/world/level/block/entity/BlockEntity;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)Z"))
	private boolean onRenderByItem(BlockEntityRenderDispatcher instance, BlockEntity blockEntity, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, Operation<Boolean> original,
								   @Local(ordinal = 0) ItemStack stack, @Local(ordinal = 0) Item item, @Local(ordinal = 0) Block block) {
		if (block instanceof ShulkerBoxBlock) {
			DyeColor color1 = ShulkerBoxBlock.getColorFromItem(item);
			var tag = getItemBlockEntityTagUnsafe(stack);
			var color2 = secondaryColorFromTag(tag, color1);
			if (color1 != color2) {
				var index = 17 * (color1 == null ? 0 : color1.getId() + 1) + (color2 == null ? 0 : color2.getId() + 1);
				return original.call(instance, splitshulkers_AllShulkerBoxes[index], poseStack, multiBufferSource, i, j);
			}
		}
		return original.call(instance, blockEntity, poseStack, multiBufferSource, i, j);
	}
}
