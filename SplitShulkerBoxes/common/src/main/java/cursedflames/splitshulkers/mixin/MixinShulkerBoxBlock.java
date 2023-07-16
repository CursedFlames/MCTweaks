package cursedflames.splitshulkers.mixin;

import cursedflames.splitshulkers.SplitShulkerBoxBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static cursedflames.splitshulkers.SplitShulkers.secondaryColorFromTag;
import static cursedflames.splitshulkers.SplitShulkers.secondaryColorToTag;

@Mixin(ShulkerBoxBlock.class)
public abstract class MixinShulkerBoxBlock extends BaseEntityBlock {
	protected MixinShulkerBoxBlock(Properties p) {
		super(p);
	}

	@Inject(method = "appendHoverText", at = @At("HEAD"))
	private void onAppendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> components, TooltipFlag tooltipFlag, CallbackInfo ci) {
		var color1 = ShulkerBoxBlock.getColorFromItem(stack.getItem());
		var tag = BlockItem.getBlockEntityData(stack);
		var color2 = secondaryColorFromTag(tag, color1);
		if (color1 == color2) return;
		components.add(1, new TranslatableComponent("splitshulkers.secondarycolor").append(color2 == null ? new TranslatableComponent("gui.none") : new TranslatableComponent("color.minecraft." + color2.getName())));
	}

	@Redirect(method = "getDrops", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BaseEntityBlock;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/storage/loot/LootContext$Builder;)Ljava/util/List;"))
	private List onGetDrops(BaseEntityBlock instance, BlockState blockState, LootContext.Builder builder) {
		var drops = super.getDrops(blockState, builder);
		var blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockEntity instanceof ShulkerBoxBlockEntity shulker) {
			drops.forEach(stack -> {
				var data = BlockItem.getBlockEntityData(stack);
				if (data == null) data = new CompoundTag();
				var color2 = ((SplitShulkerBoxBlockEntity) shulker).splitshulkers_getSecondaryColor();
				secondaryColorToTag(color2, data);
				BlockItem.setBlockEntityData(stack, BlockEntityType.SHULKER_BOX, data);
			});
		}
		return drops;
	}
}
