package com.autorule.mixin;

import com.autorule.AutoRuleClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    private void autorule$replaceAutopetGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        Text formattedMessage = AutoRuleClient.formatAutopetMessage(packet.content());
        if (formattedMessage == null) {
            return;
        }

        ci.cancel();

        if (!packet.overlay()) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.execute(() -> {
                if (client.inGameHud != null) {
                    client.inGameHud.getChatHud().addMessage(formattedMessage);
                }
            });
        }
    }
}
