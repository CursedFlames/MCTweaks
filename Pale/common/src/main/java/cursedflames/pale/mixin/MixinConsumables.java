package cursedflames.pale.mixin;

import cursedflames.pale.Pale;
import cursedflames.pale.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.consume_effects.PlaySoundConsumeEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Consumables.class)
public class MixinConsumables {
	@Redirect(method = "<clinit>",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/Consumable$Builder;onConsume(Lnet/minecraft/world/item/consume_effects/ConsumeEffect;)Lnet/minecraft/world/item/component/Consumable$Builder;"),
			slice = @Slice(
					from = @At(value = "FIELD", shift = At.Shift.BY, by = -4, target = "Lnet/minecraft/world/item/component/Consumables;ROTTEN_FLESH:Lnet/minecraft/world/item/component/Consumable;"),
					to = @At(value = "FIELD", target = "Lnet/minecraft/world/item/component/Consumables;ROTTEN_FLESH:Lnet/minecraft/world/item/component/Consumable;")
			),
			require = 1, allow = 1
			)
	private static Consumable.Builder onRottenFleshEffect(Consumable.Builder instance, ConsumeEffect consumeEffect) {
		// Skip the call to builder.effect by redirecting the call; add our own effect instead
		return instance
				.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(Services.PLATFORM.getPaleEffect(), 3*60*20, 0, false, false, true)))
				.onConsume(new PlaySoundConsumeEffect(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.ZOMBIE_AMBIENT)));
	}
}
