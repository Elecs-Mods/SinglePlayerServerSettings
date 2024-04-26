package nl.elec332.minecraft.singleplayerserversettings.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

/**
 * Created by Elec332 on 23-04-2024
 */
@Mixin(ShareToLanScreen.class)
public abstract class ShareToLanScreenMixin extends Screen {

    protected ShareToLanScreenMixin(Component $$0) {
        super($$0);
        throw new RuntimeException();
    }

    @Shadow
    private int port;
    @Shadow
    private EditBox portEdit;
    @Unique
    private boolean onlineMode;
    @Unique
    private boolean pvpAllowed;
    @Unique
    private String motd;

    @Unique
    private static final Component OFFLINE_MODE = Component.literal("Offline Mode");
    @Unique
    private static final Component PVP_ALLOWED = Component.literal("PvP Allowed");
    @Unique
    private static final Component MOTD = Component.literal("MOTD");
    @Final @Shadow //TODO: Find out why @Shadow doesn't work, but it's midnight and I can't be fucked atm...
    private static final Component PORT_INFO_TEXT = Component.translatable("lanServer.port");


    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;setResponder(Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER))
    private void initHook(CallbackInfo ci) {
        IntegratedServer integratedServer = Objects.requireNonNull(Objects.requireNonNull(this.minecraft).getSingleplayerServer());
        this.port = 25565;
        this.motd = integratedServer.getMotd();
        this.onlineMode = integratedServer.usesAuthentication();
        this.pvpAllowed = integratedServer.isPvpAllowed();
        this.portEdit.setX(this.width / 2 + 5);
        this.portEdit.setY(170);
        this.addRenderableWidget(CycleButton.onOffBuilder(!this.onlineMode).create(this.width / 2 + 5, 130, 150, 20, OFFLINE_MODE, (cycleButton, b) -> this.onlineMode = !b));
        this.addRenderableWidget(CycleButton.onOffBuilder(this.pvpAllowed).create(this.width / 2 - 155, 130, 150, 20, PVP_ALLOWED, (cycleButton, b) -> this.pvpAllowed = b));
        EditBox motd = new EditBox(this.font, this.width / 2 - 155, 170, 150, 20, MOTD);
        motd.setValue(this.motd);
        motd.setResponder(s -> this.motd = s);
        this.addRenderableWidget(motd);
    }

    @Inject(method = {"lambda$init$2", "method_19851"}, at = @At(value = "TAIL"), remap = false)
    private void onServerStart(CallbackInfo ci) {
        IntegratedServer integratedServer = Objects.requireNonNull(Objects.requireNonNull(this.minecraft).getSingleplayerServer());
        integratedServer.setUsesAuthentication(onlineMode);
        integratedServer.setPvpAllowed(pvpAllowed);
        integratedServer.setMotd(this.motd);
        this.minecraft.gui.getChat().addMessage(Component.empty().append(OFFLINE_MODE).append(": " + !onlineMode + "    ").append(PVP_ALLOWED).append(": " + pvpAllowed));
        this.minecraft.gui.getChat().addMessage(Component.empty().append(MOTD).append(": " + this.motd));
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V", ordinal = 1, shift = At.Shift.AFTER), cancellable = true)
    private void render(GuiGraphics guiGraphics, int $$1, int $$2, float $$3, CallbackInfo ci) {
        guiGraphics.drawCenteredString(this.font, PORT_INFO_TEXT, this.width / 2 + 80, 157, 16777215);
        guiGraphics.drawCenteredString(this.font, MOTD, this.width / 2 - 80, 157, 16777215);
        ci.cancel();
    }

}
