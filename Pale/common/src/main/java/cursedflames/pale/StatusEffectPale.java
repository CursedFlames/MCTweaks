package cursedflames.pale;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class StatusEffectPale extends MobEffect {
	public static final ResourceLocation ID = new ResourceLocation("pale", "pale");
	public static final StatusEffectPale PALE =
			new StatusEffectPale(MobEffectCategory.HARMFUL, 0xffb340);

	public StatusEffectPale(MobEffectCategory type, int color) {
		super(type, color);
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		if (entity.getLevel().isDay() && !entity.fireImmune() && !entity.isInWaterRainOrBubble() && !entity.isInPowderSnow) {
			BlockPos pos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
			if (entity.getLevel().canSeeSky(pos)) {
				var biome = entity.getLevel().getBiome(pos);
				float damage = (biome.isBound() && !biome.value().hasPrecipitation()) ? 2 : 1;
				entity.hurt(entity.damageSources().onFire(), damage);
			}
		}
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return (duration % (40 * Math.max(1, 4-amplifier))) == 0;
	}
}
