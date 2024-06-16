package cursedflames.splitshulkers.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import cursedflames.splitshulkers.SplitShulkerBoxBlockEntity;
import cursedflames.splitshulkers.client.ShulkerModelGetter;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ShulkerBoxRenderer.class)
public class MixinShulkerBoxRenderer {
	@Final @Shadow
	private ShulkerModel<?> model;

	// A local is added by forge patches
	@Inject(
		method = "render(Lnet/minecraft/world/level/block/entity/ShulkerBoxBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
		at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/resources/model/Material;buffer(Lnet/minecraft/client/renderer/MultiBufferSource;Ljava/util/function/Function;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"),
		cancellable = true,
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void onRender(ShulkerBoxBlockEntity shulkerBox, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, CallbackInfo ci, Direction direction, Material material) {
//		if (false) return; // TODO check for non-split shulkers
		ci.cancel();
		var color2 = ((SplitShulkerBoxBlockEntity) shulkerBox).splitshulkers_getSecondaryColor();
		var model = (ShulkerModelGetter) this.model;
		// Render base with new color
		Material material2 = color2 == null ? Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION : Sheets.SHULKER_TEXTURE_LOCATION.get(color2.ordinal());
		VertexConsumer vertexConsumer2 = material2.buffer(multiBufferSource, RenderType::entityCutoutNoCull);
		model.getBase().render(poseStack, vertexConsumer2, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
		// Render lid normally
		VertexConsumer vertexConsumer = material.buffer(multiBufferSource, RenderType::entityCutoutNoCull);
		model.getLid().render(poseStack, vertexConsumer, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
		poseStack.popPose();
	}
}
