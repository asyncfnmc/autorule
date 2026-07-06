package com.autorule.mixin;

import com.autorule.AutoRuleClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageType;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MessageHandler.class)
public abstract class MessageHandlerMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    private void autorule$replaceAutopetGameMessage(Text message, boolean overlay, CallbackInfo ci) {
        Text formattedMessage = AutoRuleClient.formatAutopetMessage(message);
        if (formattedMessage == null) {
            return;
        }

        ci.cancel();
        if (!overlay && client.inGameHud != null) {
            client.inGameHud.getChatHud().addMessage(formattedMessage);
        }
    }

    @Inject(method = "onProfilelessMessage", at = @At("HEAD"), cancellable = true)
    private void autorule$replaceAutopetProfilelessMessage(Text message, MessageType.Parameters parameters, CallbackInfo ci) {
        Text formattedMessage = AutoRuleClient.formatAutopetMessage(message);
        if (formattedMessage == null) {
            return;
        }

        ci.cancel();
        if (client.inGameHud != null) {
            client.inGameHud.getChatHud().addMessage(formattedMessage);
        }
    }
}
