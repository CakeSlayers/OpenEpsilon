package com.client.epsilon.mixin.mixins;

import b.11;
import b.1p;
import b.2U;
import b.3A;
import b.3C;
import b.3D;
import b.3p;
import b.3z;
import b.4W;
import b.4X;
import b.4Z;
import b.6l;
import b.6n;
import b.6o;
import b.6p;
import b.6t;
import b.6u;
import b.IE;
import b.J8;
import b.es;
import b.fS;
import b.jG;
import com.client.epsilon.launch.InitManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Minecraft.class}, priority=2147483335)
public class MixinMinecraft {
    @Shadow
    public int displayWidth;
    @Shadow
    public int displayHeight;
    @Shadow
    public GuiScreen currentScreen;
    @Shadow
    public boolean skipRenderWorld;
    @Shadow
    private int leftClickCounter;

    @Inject(method={"run"}, at={@At(value="HEAD")})
    private void init(CallbackInfo callbackInfo) {
        if (this.displayWidth < 1067) {
            this.displayWidth = 1067;
        }
        if (this.displayHeight < 622) {
            this.displayHeight = 622;
        }
    }

    @Inject(method={"clickMouse"}, at={@At(value="HEAD")})
    private void clickMouse(CallbackInfo callbackInfo) {
        if (jG.07G.Md()) {
            this.leftClickCounter = 0;
        }
    }

    @Inject(method={"init"}, at={@At(value="HEAD")})
    private void onMinecraftInitHead(CallbackInfo callbackInfo) {
        IE.OS("Registering Spartan Mods");
        InitManager.onMinecraftInit();
    }

    @Inject(method={"init"}, at={@At(value="RETURN")})
    private void onMinecraftInitReturn(CallbackInfo callbackInfo) {
        IE.OS("Finishing loading Spartan Mods");
        InitManager.onFinishingInit();
    }

    @Inject(method={"loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V"}, at={@At(value="HEAD")})
    public void onUnload(WorldClient worldClient, String string, CallbackInfo callbackInfo) {
    }

    @Inject(method={"getLimitFramerate"}, at={@At(value="HEAD")}, cancellable=true)
    public void getLimitFramerate$Inject$HEAD(CallbackInfoReturnable<Integer> callbackInfoReturnable) {
        if (J8.zB().world == null && J8.zB().currentScreen != null) {
            callbackInfoReturnable.setReturnValue((Object)60);
        }
    }

    @Inject(method={"runGameLoop"}, at={@At(value="INVOKE", target="Lnet/minecraft/util/Timer;updateTimer()V", shift=At.Shift.BEFORE)})
    public void runGameLoop$Inject$INVOKE$updateTimer(CallbackInfo callbackInfo) {
        2U.Nk.Uz();
        J8.zB().profiler.startSection("epsilonRunGameLoop");
        6o.rA.yt();
        J8.zB().profiler.endSection();
    }

    @Inject(method={"runGameLoop"}, at={@At(value="INVOKE", target="Lnet/minecraft/profiler/Profiler;endSection()V", ordinal=0, shift=At.Shift.AFTER)})
    public void runGameLoop$INVOKE$endSection(CallbackInfo callbackInfo) {
        2U.Nk.Uz();
        J8.zB().profiler.startSection("epsilonRunGameLoop");
        6p.aL.yt();
        J8.zB().profiler.endSection();
    }

    @Inject(method={"runGameLoop"}, at={@At(value="INVOKE", target="Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V", ordinal=0, shift=At.Shift.BEFORE)})
    public void runGameLoop$Inject$INVOKE$endStartSection(CallbackInfo callbackInfo) {
        2U.Nk.Uz();
        J8.zB().profiler.endStartSection("epsilonRunGameLoop");
        6n.07r.yt();
    }

    @Inject(method={"runGameLoop"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/Minecraft;isFramerateLimitBelowMax()Z", shift=At.Shift.BEFORE)})
    public void runGameLoop$Inject$INVOKE$isFramerateLimitBelowMax(CallbackInfo callbackInfo) {
        2U.Nk.Uz();
        J8.zB().profiler.startSection("epsilonRunGameLoop");
        6l.Bv.yt();
        J8.zB().profiler.endSection();
    }

    @Inject(method={"runGameLoop"}, at={@At(value="HEAD")})
    private void onRunningGameLoopHead(CallbackInfo callbackInfo) {
        2U.Nk.Uz();
        11.t.e();
    }

    @Inject(method={"runGameLoop"}, at={@At(value="RETURN")})
    private void onRunningGameLoopReturn(CallbackInfo callbackInfo) {
        11.t.e();
    }

    @Inject(method={"runTick"}, at={@At(value="HEAD")})
    private void onRunTickPre(CallbackInfo callbackInfo) {
        if (Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().world != null) {
            6u.mY.yt();
        }
        11.t.e();
    }

    @Inject(method={"runTick"}, at={@At(value="RETURN")})
    private void onRunTick(CallbackInfo callbackInfo) {
        if (Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().world != null) {
            3p.03a.post();
            6t.35.yt();
        }
        11.t.e();
    }

    @Inject(method={"init"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/Minecraft;checkGLError(Ljava/lang/String;)V", ordinal=0, shift=At.Shift.BEFORE)})
    private void onPreInit(CallbackInfo callbackInfo) {
        InitManager.preInitHook();
    }

    @Inject(method={"init"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/Minecraft;checkGLError(Ljava/lang/String;)V", ordinal=2, shift=At.Shift.AFTER)})
    private void onPostInit(CallbackInfo callbackInfo) {
        InitManager.postInitHook();
    }

    @Inject(method={"displayGuiScreen"}, at={@At(value="FIELD", target="Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", shift=At.Shift.AFTER)})
    private void displayGuiScreen(CallbackInfo callbackInfo) {
        if (this.currentScreen instanceof GuiMainMenu || this.currentScreen != null && this.currentScreen.getClass().getName().startsWith("net.labymod") && this.currentScreen.getClass().getSimpleName().equals("ModGuiMainMenu")) {
            this.currentScreen = fS.Z8;
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            this.currentScreen.setWorldAndResolution(Minecraft.getMinecraft(), scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
            this.skipRenderWorld = false;
        }
    }

    @ModifyVariable(method={"displayGuiScreen"}, at=@At(value="HEAD"), ordinal=0, argsOnly=true)
    public GuiScreen displayGuiScreen$ModifyVariable$HEAD(GuiScreen guiScreen) {
        4W w;
        GuiScreen guiScreen2 = this.currentScreen;
        if (guiScreen2 != null) {
            w = new 4X(guiScreen2);
            w.yt();
        }
        w = new 4Z(guiScreen);
        w.yt();
        return ((4Z)w).qL();
    }

    @Inject(method={"shutdown"}, at={@At(value="HEAD")})
    public void shutdown(CallbackInfo callbackInfo) {
        1p.Ib.L3(true);
    }

    @Inject(method={"runTickKeyboard"}, at={@At(value="INVOKE_ASSIGN", target="org/lwjgl/input/Keyboard.getEventKeyState()Z", remap=false)})
    private void onKeyEvent(CallbackInfo callbackInfo) {
        if (this.currentScreen != null) {
            return;
        }
        boolean bl = Keyboard.getEventKeyState();
        int n = Keyboard.getEventKey();
        char c = Keyboard.getEventCharacter();
        if (n != 0) {
            if (bl) {
                3C.zu.post(new 3D(n, c));
                es.Aa(n);
            } else {
                3z.d1.post(new 3A(n, c));
            }
        }
    }
}
 