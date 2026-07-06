package com.autorule.mixin;

import com.autorule.AutoRuleClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"), argsOnly = true, require = 0)
    private Text autorule$replaceSimpleMessage(Text message) {
        Text formattedMessage = AutoRuleClient.formatAutopetMessage(message);
        return formattedMessage == null ? message : formattedMessage;
    }

    @ModifyVariable(
            method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
            at = @At("HEAD"),
            argsOnly = true,
            require = 0
    )
    private Text autorule$replaceFullMessage(Text message) {
        Text formattedMessage = AutoRuleClient.formatAutopetMessage(message);
        return formattedMessage == null ? message : formattedMessage;
    }
}
