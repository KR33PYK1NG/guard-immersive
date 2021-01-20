package rmc.mixins.guard_immersive;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.authlib.GameProfile;

import blusunrize.immersiveengineering.common.util.IEExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

@Mixin(value = IEExplosion.class)
public abstract class IEExplosionMixin {

    private static final GameProfile FAKE_EXPLODER_PROFILE =
        new GameProfile(UUID.fromString("43fd36df-f2bc-44e3-833c-8db329f8faa6"), "__FAKE_EXPLODER_IE__");

    @Shadow
    private World world;

    @Inject(method = "Lblusunrize/immersiveengineering/common/util/IEExplosion;doExplosionA()V",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraftforge/event/ForgeEventFactory;onExplosionDetonate(Lnet/minecraft/world/World;Lnet/minecraft/world/Explosion;Ljava/util/List;D)V"))
    private void doExplosionAMixin(CallbackInfo mixin, Set<?> set, int i, float f3, int k1, int l1, int i2, int i1, int j2, int j1, List<Entity> list) {
        FakePlayer fake = FakePlayerFactory.get((ServerWorld) world, FAKE_EXPLODER_PROFILE);
        Iterator<Entity> it = list.iterator();
        while (it.hasNext()) {
            PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(
                fake.getBukkitEntity(),
                it.next().getBukkitEntity()
            );
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                it.remove();
            }
        }
    }

    @Inject(method = "Lblusunrize/immersiveengineering/common/util/IEExplosion;doExplosionA()V",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(value = "INVOKE",
                     target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z"))
    private void doExplosionAMixin(CallbackInfo mixin, Set<BlockPos> set) {
        FakePlayer fake = FakePlayerFactory.get((ServerWorld) world, FAKE_EXPLODER_PROFILE);
        Iterator<BlockPos> it = set.iterator();
        while (it.hasNext()) {
            BlockBreakEvent event = new BlockBreakEvent(
                CraftBlock.at(world, it.next()),
                fake.getBukkitEntity()
            );
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                it.remove();
            }
        }
    }

}