package kdl.mixin;

import kdl.internal.block.blockentity.KDLBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(
            at = @At("TAIL"),
            method = "onBlockEntityUpdate(Lnet/minecraft/network/packet/s2c/play/BlockEntityUpdateS2CPacket;)V",
            cancellable = true
    )
    public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet, CallbackInfo ci) {
        if (packet.getBlockEntityType() == 0) {
            BlockEntity blockEntity = MinecraftClient.getInstance().world.getBlockEntity(packet.getPos());

            if (blockEntity instanceof KDLBlockEntity) {
                ((KDLBlockEntity) blockEntity).receiveUpdatePacket(packet);
            }
        }
    }
}
