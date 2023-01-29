package studio.coni.epsilon.management

import studio.coni.epsilon.common.AbstractModule
import studio.coni.epsilon.common.Category
import studio.coni.epsilon.common.interfaces.Helper
import studio.coni.epsilon.gui.def.DefaultHUDEditorScreen
import studio.coni.epsilon.hud.HUDModule
import studio.coni.epsilon.hud.combat.HoleOverlay
import studio.coni.epsilon.hud.combat.ObsidianWarning
import studio.coni.epsilon.hud.combat.TargetHud
import studio.coni.epsilon.hud.info.*
import studio.coni.epsilon.hud.spartan.EnergyShield
import studio.coni.epsilon.module.client.*
import studio.coni.epsilon.module.combat.*
import studio.coni.epsilon.module.misc.*
import studio.coni.epsilon.module.movement.*
import studio.coni.epsilon.module.player.*
import studio.coni.epsilon.module.render.*
import studio.coni.epsilon.module.setting.*
import studio.coni.epsilon.util.onRender2D

@Suppress("NOTHING_TO_INLINE")
object ModuleManager : Helper {

    val modules = mutableListOf<AbstractModule>()
    val hudModules = mutableListOf<HUDModule>()

    init {
        //Client
        HUDEditor.register()
        InfoHUD.register()
        NotificationRender.register()
        RootGUI.register()
        Test.register()
        //Combat
        AimAssist.register()
        AimBot.register()
        AntiAntiBurrow.register()
        AntiCev.register()
        AnvilCity.register()
        AutoBurrow.register()
        AutoCev.register()
        AutoCity.register()
        AutoClicker.register()
        ZealotCrystalTwo.register()
        AutoHoleFill.register()
        AutoLog.register()
        AutoMend.register()
        AutoOffhand.register()
        AutoTotem.register()
        AutoTrap.register()
        BedAura.register()
        Burrow.register()
        Critical.register()
        HoleSnap.register()
        KillAura.register()
        Surround.register()
        TargetStrafe.register()
        TotemPopCounter.register()
        ZealotCrystalPlus.register()

        //Misc
        AntiBot.register()
        AntiCrasher.register()
        AntiWeather.register()
        AutoFish.register()
//        AutoObsidian.register()
        AutoPorn.register()
        AutoReconnect.register()
        AutoRespawn.register()
        AutoTool.register()
        BowMcBomb.register()
        ClientSpoof.register()
        Crasher.register()
        FakePlayer.register()
        MiddleClick.register()
        MountBypass.register()
        NoRotate.register()
        PingSpoof.register()
        Refill.register()
        SkinBlinker.register()
        XCarry.register()

        //Movement
        AntiHunger.register()
//        AntiLevitation.register()
        AntiWeb.register()
        AutoCenter.register()
        AutoJump.register()
        AutoRemount.register()
        AutoWalk.register()
        ElytraFlight.register()
        ElytraReplace.register()
        EntitySpeed.register()
        FastSwim.register()
        Flight.register()
        InstantDrop.register()
        InventoryMove.register()
        Jesus.register()
        LongJump.register()
        NoFall.register()
        NoSlowDown.register()
        SafeWalk.register()
        Scaffold.register()
        Speed.register()
        Sprint.register()
        Step.register()
//        Strafe.register()
        Velocity.register()

        //Player
        AntiAim.register()
        AutoArmour.register()
        Freecam.register()
        Hitbox.register()
        LagBackCheck.register()
        LiquidInteract.register()
        NoVoid.register()
        PacketMine.register()
        Reach.register()
        WTap.register()

        //Render
        Animations.register()
        AntiOverlay.register()
        BreakESP.register()
        CameraClip.register()
        Chams.register()
        ChinaHat.register()
        CityESP.register()
        Crosshair.register()
        EntityESP.register()
        ESP2D.register()
        FullBright.register()
        HealthParticle.register()
        HoleESP.register()
        ItemESP.register()
        Nametags.register()
        NoRender.register()
        Skeleton.register()
        TextPopper.register()
        Tracers.register()
        Trajectories.register()
        ViewModel.register()
        WallHack.register()

        //CombatHUD
        HoleOverlay.register()
        ObsidianWarning.register()
        TargetHud.register()

        //InfoHUD
        CombatInfo.register()
        Compass.register()
        Inventory.register()
        Keystroke.register()
        LagNotification.register()
        Logo.register()
        Welcomer.register()

        //Spartan
        EnergyShield.register()


        modules.sortBy { it.name }
        hudModules.sortBy { it.name }

        onRender2D {
            DefaultHUDEditorScreen.hudList.reversed().forEach {
                if (!HUDEditor.isHUDEditor() && it.hudModule.isEnabled) it.hudModule.onRender()
            }
        }
    }

    fun getModulesByCategory(category: Category): List<AbstractModule> {
        return modules.asSequence().filter {
            it.category == category
        }.toList()
    }

    private inline fun AbstractModule.register() {
        registerNewModule(this)
    }

    private inline fun registerNewModule(abstractModule: AbstractModule) {
        modules.add(abstractModule)
        if (abstractModule.category.isHUD) {
            hudModules.add(abstractModule as HUDModule)
        }
    }

}