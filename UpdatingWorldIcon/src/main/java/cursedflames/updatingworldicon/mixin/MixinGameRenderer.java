package cursedflames.updatingworldicon.mixin;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.server.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Optional;

@Mixin(GameRenderer.class)
class MixinGameRenderer {
	private boolean updatingWorldIcon_hasWorldScreenshot = false;

	@Redirect(method = "tryTakeScreenshotIfNeeded", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/GameRenderer;hasWorldScreenshot:Z"))
	private boolean on_tryTakeScreenshotIfNeeded_hasWorldScreenshot(GameRenderer instance) {
		return updatingWorldIcon_hasWorldScreenshot;
	}

	@Redirect(method = "tryTakeScreenshotIfNeeded", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;getWorldScreenshotFile()Ljava/util/Optional;"))
	private Optional on_tryTakeScreenshotIfNeeded_isRegularFile(IntegratedServer instance) {
		instance.getWorldScreenshotFile().ifPresent(this::takeAutoScreenshot);
		return Optional.empty();
	}

	@Shadow
	private void takeAutoScreenshot(Path path) {}

	// This isn't entirely reliable, but we can't target inside the lambda after the new screenshot is saved since `this` isn't captured
	@Inject(method = "takeAutoScreenshot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Screenshot;takeScreenshot(Lcom/mojang/blaze3d/pipeline/RenderTarget;)Lcom/mojang/blaze3d/platform/NativeImage;"))
	private void on_takeAutoScreenshot(Path $$0, CallbackInfo ci) {
		this.updatingWorldIcon_hasWorldScreenshot = true;
	}

	@Inject(method = "resetData", at = @At(value = "HEAD"))
	private void on_resetData(CallbackInfo ci) {
		updatingWorldIcon_hasWorldScreenshot = false;
	}
}
