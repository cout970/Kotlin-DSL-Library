package kdl.mixin;

import kdl.internal.client.TranslationManager;
import net.minecraft.client.resource.language.TranslationStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(TranslationStorage.class)
public class TranslationStorageMixin {

    @Shadow
    protected Map<String, String> translations;

    @Inject(
            at = @At("HEAD"),
            method = "get(Ljava/lang/String;)Ljava/lang/String;",
            cancellable = true
    )
    private void getTranslation(String key, CallbackInfoReturnable<String> callback) {
        if (!translations.containsKey(key)) {
            String translation = TranslationManager.INSTANCE.getDefaultTranslation(key);

            if (translation != null) {
                callback.setReturnValue(translation);
                callback.cancel();
            }
        }
    }
}
