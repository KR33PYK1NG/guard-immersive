package rmc.mixins.guard_immersive;

import org.bukkit.craftbukkit.v1_16_R3.event.CraftEventFactory;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.common.items.WirecutterItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Mixin(value = WirecutterItem.class)
public abstract class WirecutterItemMixin {

    @Inject(method = "Lblusunrize/immersiveengineering/common/items/WirecutterItem;onItemRightClick(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(value = "INVOKE",
                     target = "Lblusunrize/immersiveengineering/api/wires/GlobalWireNetwork;removeAndDropConnection(Lblusunrize/immersiveengineering/api/wires/Connection;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/World;)V"))
    private void onItemRightClickMixin(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult<ItemStack>> mixin, ItemStack stack, double reachDistance, Connection target) {
        PlayerInteractEvent eventA = CraftEventFactory.callPlayerInteractEvent(
            player,
            Action.RIGHT_CLICK_BLOCK,
            target.getEndA().getPosition(),
            Direction.DOWN,
            stack,
            hand
        );
        PlayerInteractEvent eventB = CraftEventFactory.callPlayerInteractEvent(
            player,
            Action.RIGHT_CLICK_BLOCK,
            target.getEndB().getPosition(),
            Direction.DOWN,
            stack,
            hand
        );
        if (eventA.useInteractedBlock() == Event.Result.DENY
         && eventB.useInteractedBlock() == Event.Result.DENY) {
            mixin.setReturnValue(new ActionResult<>(ActionResultType.FAIL, stack));
        }
    }

}