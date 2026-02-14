package nl.elec332.minecraft.singleplayerserversettings.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;
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

    @Unique
    private CycleButton<Boolean> offlineMode;
    @Unique
    private CycleButton<Boolean> pvpAllowed;
    @Unique
    private EditBox motd;

    @Unique
    private static final Component OFFLINE_MODE = Component.literal("Offline Mode");
    @Unique
    private static final Component PVP_ALLOWED = Component.literal("PvP Allowed");
    @Unique
    private static final Component MOTD = Component.literal("MOTD");
//    @Final @Shadow //TODO: Find out why @Shadow doesn't work, but it's midnight and I can't be fucked atm...
    @Unique
    private static final Component PORT_INFO_TEXT_ = Component.translatable("lanServer.port");

    @Accessor("portEdit")
    protected abstract EditBox getBox();

    @Accessor("port")
    protected abstract void setPort(int i);

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;setResponder(Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER))
    private void initHook(CallbackInfo ci) {
        IntegratedServer integratedServer = Objects.requireNonNull(Objects.requireNonNull(this.minecraft).getSingleplayerServer());
        this.setPort(25565);
        this.pvpAllowed = CycleButton.onOffBuilder(true).create(this.width / 2 - 155, 130, 150, 20, PVP_ALLOWED);
        try {
            this.pvpAllowed.setValue(integratedServer.isPvpAllowed());
        } catch (NoSuchMethodError e) {
            this.pvpAllowed.active = false;
            this.pvpAllowed.setTooltip(Tooltip.create(Component.literal("Setting pvp is not supported in this Minecraft version.")));
        }
        this.offlineMode = CycleButton.onOffBuilder(!integratedServer.usesAuthentication()).create(this.width / 2 + 5, 130, 150, 20, OFFLINE_MODE);

        this.getBox().setX(this.width / 2 + 5);
        this.getBox().setY(170);
        this.addRenderableWidget(this.offlineMode);
        this.addRenderableWidget(this.pvpAllowed);
        this.motd = new EditBox(this.font, this.width / 2 - 155, 170, 150, 20, MOTD);
        this.motd.setValue(integratedServer.getMotd());
        this.addRenderableWidget(this.motd);
    }

    @Inject(method = {"lambda$init$2", "method_19851", "m_279789_"}, at = @At(value = "TAIL"), remap = false)
    private void onServerStart(CallbackInfo ci) {
        IntegratedServer integratedServer = Objects.requireNonNull(Objects.requireNonNull(this.minecraft).getSingleplayerServer());
        integratedServer.setUsesAuthentication(!this.offlineMode.getValue());
        try {
            integratedServer.setPvpAllowed(this.pvpAllowed.getValue());
        } catch (NoSuchMethodError e) {
            // Later 1.21.x versions do this using per-world GameRules
        }
        integratedServer.setMotd(this.motd.getValue());
        this.minecraft.gui.getChat().addMessage(Component.empty().append(OFFLINE_MODE).append(": " + this.offlineMode.getValue() + "    ").append(PVP_ALLOWED).append(": " + (this.pvpAllowed.active ? this.pvpAllowed.getValue() : "default")));
        this.minecraft.gui.getChat().addMessage(Component.empty().append(MOTD).append(": " + this.motd.getValue()));
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V", ordinal = 1, shift = At.Shift.AFTER), cancellable = true)
    private void render(GuiGraphics guiGraphics, int $$1, int $$2, float $$3, CallbackInfo ci) {
        guiGraphics.drawCenteredString(this.font, PORT_INFO_TEXT_, this.width / 2 + 80, 157, 16777215);
        guiGraphics.drawCenteredString(this.font, MOTD, this.width / 2 - 80, 157, 16777215);
        ci.cancel();
    }

}
