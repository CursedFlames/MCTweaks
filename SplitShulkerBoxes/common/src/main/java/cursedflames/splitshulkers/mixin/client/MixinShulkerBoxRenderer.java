package cursedflames.splitshulkers.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
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
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBoxRenderer.class)
public class MixinShulkerBoxRenderer {
	@Final @Shadow
	private ShulkerModel<?> model;

	@Inject(
		method = "render(Lnet/minecraft/world/level/block/entity/ShulkerBoxBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
		at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/resources/model/Material;buffer(Lnet/minecraft/client/renderer/MultiBufferSource;Ljava/util/function/Function;)Lcom/mojang/blaze3d/vertex/VertexConsumer;")
	)
	private void beforeRenderCall(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, CallbackInfo ci,
								  @Share("box") LocalRef<ShulkerBoxBlockEntity> box, @Share("buf") LocalRef<MultiBufferSource> buf) {
		box.set(shulkerBoxBlockEntity);
		buf.set(multiBufferSource);
	}

	@WrapWithCondition(
		method = "render(Lnet/minecraft/world/level/block/entity/ShulkerBoxBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ShulkerModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V")
	)
	private boolean onRenderCall(ShulkerModel model, PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j,
							  @Share("box") LocalRef<ShulkerBoxBlockEntity> box, @Share("buf") LocalRef<MultiBufferSource> buf) {
		var shulkerBox = box.get();
		var color1 = shulkerBox.getColor();
		var color2 = ((SplitShulkerBoxBlockEntity) shulkerBox).splitshulkers_getSecondaryColor();
		if (color1 == color2) {
			return true; // keep vanilla rendering if this shulker box is single-color
		}
		// Render base with new color
		Material material2 = color2 == null ? Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION : Sheets.SHULKER_TEXTURE_LOCATION.get(color2.ordinal());
		VertexConsumer vertexConsumer2 = material2.buffer(buf.get(), RenderType::entityCutoutNoCull);
		((ShulkerModelGetter) model).getBase().render(poseStack, vertexConsumer2, i, j);
		// Render lid normally
		model.getLid().render(poseStack, vertexConsumer, i, j);
		return false;
	}
}
