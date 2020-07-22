package kdl.mixin;

import kdl.internal.client.ModelManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpriteAtlasTexture.class)
public class SpriteAtlasTextureMixin {

    @Inject(
            at = @At("HEAD"),
            method = "getTexturePath(Lnet/minecraft/util/Identifier;)Lnet/minecraft/util/Identifier;",
            cancellable = true
    )
    private void getTexturePath(Identifier identifier, CallbackInfoReturnable<Identifier> info) {
        Identifier custom = ModelManager.INSTANCE.getCustomTexturePath(identifier);
        if (custom != null) {
            info.setReturnValue(custom);
            info.cancel();
        }
    }
}
