package cursedflames.splitshulkers.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import cursedflames.splitshulkers.SplitShulkerBoxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

import static cursedflames.splitshulkers.SplitShulkerBoxes.getItemBlockEntityTagUnsafe;
import static cursedflames.splitshulkers.SplitShulkerBoxes.secondaryColorFromTag;
import static cursedflames.splitshulkers.SplitShulkerBoxes.secondaryColorToTag;

@Mixin(ShulkerBoxBlock.class)
public abstract class MixinShulkerBoxBlock extends BaseEntityBlock {
	protected MixinShulkerBoxBlock(Properties p) {
		super(p);
	}

	@Inject(method = "appendHoverText", at = @At("HEAD"))
	private void onAppendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> components, TooltipFlag tooltipFlag, CallbackInfo ci) {
		var color1 = ShulkerBoxBlock.getColorFromItem(stack.getItem());
		var tag = getItemBlockEntityTagUnsafe(stack);
		var color2 = secondaryColorFromTag(tag, color1);
		if (color1 == color2) return;
		components.add(1, Component.translatable("splitshulkers.secondarycolor").append(color2 == null ? Component.translatable("gui.none") : Component.translatable("color.minecraft." + color2.getName())));
	}

	// Does not apply when in Creative
	@Redirect(method = "getDrops", require = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BaseEntityBlock;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/storage/loot/LootParams$Builder;)Ljava/util/List;"))
	private List onGetDrops(BaseEntityBlock instance, BlockState blockState, LootParams.Builder builder) {
		var drops = super.getDrops(blockState, builder);
		var blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockEntity instanceof ShulkerBoxBlockEntity shulker) {
			drops.forEach(stack -> {
				var data = getItemBlockEntityTagUnsafe(stack);
				if (data == null) data = new CompoundTag();
				var color2 = ((SplitShulkerBoxBlockEntity) shulker).splitshulkers_getSecondaryColor();
				secondaryColorToTag(color2, data);
				BlockItem.setBlockEntityData(stack, BlockEntityType.SHULKER_BOX, data);
			});
		}
		return drops;
	}

	// Applies when player is in Creative
	@Inject(method = "playerWillDestroy", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;applyComponents(Lnet/minecraft/core/component/DataComponentMap;)V"))
	private void onPlayerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player, CallbackInfoReturnable<BlockState> cir, @Local ShulkerBoxBlockEntity shulker, @Local ItemStack stack) {
		var data = getItemBlockEntityTagUnsafe(stack);
		if (data == null) data = new CompoundTag();
		var color2 = ((SplitShulkerBoxBlockEntity) shulker).splitshulkers_getSecondaryColor();
		secondaryColorToTag(color2, data);
		BlockItem.setBlockEntityData(stack, BlockEntityType.SHULKER_BOX, data);
	}
}
