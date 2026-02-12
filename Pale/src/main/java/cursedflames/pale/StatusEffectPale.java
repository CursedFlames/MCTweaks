package cursedflames.pale;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class StatusEffectPale extends MobEffect {
	public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("pale", "pale");

	public StatusEffectPale(MobEffectCategory type, int color) {
		super(type, color);
	}

	@Override
	public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
		//? <1.21.5 {
		if (level.isDay() && !entity.fireImmune() && !entity.isInWaterRainOrBubble() && !entity.isInPowderSnow) {
		//?} else {
		/*if (level.isBrightOutside() && !entity.fireImmune() && !entity.isInWaterOrRain() && !entity.isInPowderSnow && !entity.wasInPowderSnow) {
		*///?}
			BlockPos pos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
			if (level.canSeeSky(pos)) {
				var biome = level.getBiome(pos);
				float damage = (biome.isBound() && !biome.value().hasPrecipitation()) ? 2 : 1;
				entity.hurtServer(level, entity.damageSources().onFire(), damage);
			}
		}
		return true;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return (duration % (20*5)) == 0;
	}
}
