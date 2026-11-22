package com.Zrips.CMI.Modules.ModuleHandling;

import com.Zrips.CMI.AllListeners.BossBarCompassListener;
import com.Zrips.CMI.AllListeners.CustomMessagesListener;
import com.Zrips.CMI.AllListeners.DamageControlListener;
import com.Zrips.CMI.AllListeners.DecoratedPotListener;
import com.Zrips.CMI.AllListeners.ElevatorListener;
import com.Zrips.CMI.AllListeners.FirstJoinListener;
import com.Zrips.CMI.AllListeners.HatListeners;
import com.Zrips.CMI.AllListeners.LaunchPadListener;
import com.Zrips.CMI.AllListeners.LaunchPadListener1_9;
import com.Zrips.CMI.AllListeners.NightSpeedUpListener;
import com.Zrips.CMI.AllListeners.NoTargetListener;
import com.Zrips.CMI.AllListeners.PlayerItemDamageListener;
import com.Zrips.CMI.AllListeners.PortalCreationListener;
import com.Zrips.CMI.AllListeners.TeleportWithListener;
import com.Zrips.CMI.AllListeners.UserCuffedEvents;
import com.Zrips.CMI.AllListeners.WorldLimits;
import com.Zrips.CMI.AllListeners.WorldLimitsElytra;
import com.Zrips.CMI.AllListeners.versionCheck;
import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.AStand.ArmorStandListener;
import com.Zrips.CMI.Modules.AStand.ArmorStandListener1_8;
import com.Zrips.CMI.Modules.Afk.AfkListener;
import com.Zrips.CMI.Modules.Afk.AfkListener1_8;
import com.Zrips.CMI.Modules.Afk.AfkListener1_9;
import com.Zrips.CMI.Modules.Animations.AnimationColoredArmorListener;
import com.Zrips.CMI.Modules.Animations.AnimationRideListener;
import com.Zrips.CMI.Modules.Animations.AnimationSitListener;
import com.Zrips.CMI.Modules.Anvil.AnvilColorListener;
import com.Zrips.CMI.Modules.Anvil.AnvilRenameListener;
import com.Zrips.CMI.Modules.ArmorEffects.ArmorEffectListener;
import com.Zrips.CMI.Modules.AttachedCommands.CustomNBTListener;
import com.Zrips.CMI.Modules.AttachedCommands.CustomNBTListener1_8;
import com.Zrips.CMI.Modules.Chat.ChatBubbleListener;
import com.Zrips.CMI.Modules.Chat.ChatBubblePaperListener;
import com.Zrips.CMI.Modules.Chat.ChatBubbleSpigotListener;
import com.Zrips.CMI.Modules.ChatTag.TagListener;
import com.Zrips.CMI.Modules.CmdCooldown.CooldownListener;
import com.Zrips.CMI.Modules.CmdWarmUp.WarmUpListener;
import com.Zrips.CMI.Modules.DeathMessages.DeathMessageListener;
import com.Zrips.CMI.Modules.Dialogs.DialogsListener;
import com.Zrips.CMI.Modules.DynamicSigns.SignListener;
import com.Zrips.CMI.Modules.Elytra.ElytraBoostListener;
import com.Zrips.CMI.Modules.Elytra.ElytraExploitListener;
import com.Zrips.CMI.Modules.Elytra.ElytraExploitListener1_13;
import com.Zrips.CMI.Modules.Elytra.ElytraLaunchListener;
import com.Zrips.CMI.Modules.Enchants.EnchantListener;
import com.Zrips.CMI.Modules.EventActions.EventActionListener;
import com.Zrips.CMI.Modules.EventActions.EventActionListener1_12;
import com.Zrips.CMI.Modules.EventActions.EventActionListener1_9;
import com.Zrips.CMI.Modules.FlightCharge.FlightListener;
import com.Zrips.CMI.Modules.Holograms.HologramListener;
import com.Zrips.CMI.Modules.Homes.HomeListener;
import com.Zrips.CMI.Modules.Homes.HomeListener1_11;
import com.Zrips.CMI.Modules.HpBar.HpBarListener;
import com.Zrips.CMI.Modules.InteractiveCommand.InteractiveCommandListener;
import com.Zrips.CMI.Modules.InteractiveCommand.InteractiveCommandListener1_8;
import com.Zrips.CMI.Modules.Jail.JailListener;
import com.Zrips.CMI.Modules.Kits.KitListener;
import com.Zrips.CMI.Modules.Mirror.CMIMirrorListener;
import com.Zrips.CMI.Modules.MoneyCheque.MoneyChequeListener;
import com.Zrips.CMI.Modules.Painting.PaintingListener;
import com.Zrips.CMI.Modules.PlayTime.PlayTimeListener;
import com.Zrips.CMI.Modules.PlayerCombat.HeadContainerListener;
import com.Zrips.CMI.Modules.PlayerCombat.HeadDropListener;
import com.Zrips.CMI.Modules.PlayerCombat.PlayerCombatListener;
import com.Zrips.CMI.Modules.Portals.PortalListener;
import com.Zrips.CMI.Modules.Selection.SelectionListener;
import com.Zrips.CMI.Modules.ShulkerBoxInventory.ShulkerBoxListener;
import com.Zrips.CMI.Modules.Signs.PlayerSignEdit1_20Listeners;
import com.Zrips.CMI.Modules.Signs.PlayerSignEditListeners;
import com.Zrips.CMI.Modules.SilentChest.silentChestListener;
import com.Zrips.CMI.Modules.Skin.SkinListener;
import com.Zrips.CMI.Modules.SpawnerCharge.SpawnerActionsListener;
import com.Zrips.CMI.Modules.SpawnerCharge.SpawnerChargeListener;
import com.Zrips.CMI.Modules.SpawnerCharge.SpawnerProximityListener;
import com.Zrips.CMI.Modules.SpeedTracker.CMISpeedListener;
import com.Zrips.CMI.Modules.SpeedTracker.CMISpeedListener1_9;
import com.Zrips.CMI.Modules.TabList.TabListListener;
import com.Zrips.CMI.Modules.TimedCommands.TimedCommandListener;
import com.Zrips.CMI.Modules.Totems.ResurectionListener;
import com.Zrips.CMI.Modules.Vanish.VanishListener;
import com.Zrips.CMI.Modules.Vanish.VanishListener1_12;
import com.Zrips.CMI.Modules.Vanish.VanishListener1_19;
import com.Zrips.CMI.Modules.Vanish.VanishListener1_21;
import com.Zrips.CMI.Modules.Votifier.VotifierListener;
import java.lang.reflect.Constructor;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public enum CMIModule {
  silentChest("Handles silent chest opening actions", silentChestListener.class),
  versionCheck("Version check when player logs in", versionCheck.class),
  worldLimits("World limitations: fly, game mode and god mode", WorldLimits.class),
  worldLimitsElytra("World limitations: elytra mode", WorldLimitsElytra.class, Version.v1_9_R1),
  customMessages("Login, logout and death messages", CustomMessagesListener.class),
  firstJoinMessages("First join messages", FirstJoinListener.class),
  cuffed("Handling of cuffed players", UserCuffedEvents.class),
  elevator("Handling of elevators", ElevatorListener.class),
  nightSpeedup("Handling of night speedup when players sleep in beds", NightSpeedUpListener.class),
  damageControl("Handling of damage control", DamageControlListener.class),
  portalCreation("Handling of portal creation", PortalCreationListener.class),
  teleportWith("Handling of portal creation", TeleportWithListener.class),
  durabilityLoss(
      "Handling of messages appearing when item durability gets low",
      PlayerItemDamageListener.class),
  mirror(
      "Handling of mirror feature, which checks block place and break actions",
      new CMIModuleClass(CMIMirrorListener.class)),
  spawners("Spawner handling, like drop, place, egg interactions", SpawnerActionsListener.class),
  spawnerProximity("Checking spawner place proximity", SpawnerProximityListener.class),
  spawnerCharge("Checking spawner charges", SpawnerChargeListener.class),
  warmup("Handling of command warmups", WarmUpListener.class),
  cooldown("Handling of command cooldowns", CooldownListener.class),
  interactiveCommands(
      "Handling of interactive commands",
      new CMIModuleClass(InteractiveCommandListener.class),
      new CMIModuleClass(InteractiveCommandListener1_8.class, Version.v1_8_R1)),
  attachedCommands(
      "Handling of attached commands",
      new CMIModuleClass(CustomNBTListener.class),
      new CMIModuleClass(CustomNBTListener1_8.class, Version.v1_8_R1)),
  armorEffects("Handling of armor effects", ArmorEffectListener.class),
  playerChatTag("Handling of player tagging in chat and tabcomplete with @", TagListener.class),
  chatBubble(
      "Handling of chat bubbles",
      new CMIModuleClass(ChatBubbleListener.class, Version.v1_19_R3),
      new CMIModuleClass(
          ChatBubbleSpigotListener.class, Version.v1_19_R3, CMIModuleServerType.SpigotOnly),
      new CMIModuleClass(
          ChatBubblePaperListener.class, Version.v1_20_R1, CMIModuleServerType.PaperOnly)),
  tablist("Handling of tablist", TabListListener.class),
  paintingEditor("Handling of painting editing with shift+right click", PaintingListener.class),
  vanish(
      "Handles player vanish mode",
      new CMIModuleClass(VanishListener.class),
      new CMIModuleClass(VanishListener1_19.class, Version.v1_19_R1),
      new CMIModuleClass(VanishListener1_21.class, Version.v1_21_R1),
      new CMIModuleClass(
          VanishListener1_12.class, Version.v1_16_R1, CMIModuleServerType.PaperOnly)),
  moneyCheque("Handles money cheques", MoneyChequeListener.class),
  armorstand(
      "Handles armor stand editor",
      new CMIModuleClass(ArmorStandListener.class),
      new CMIModuleClass(ArmorStandListener1_8.class, Version.v1_8_R1)),
  cmiPlaytime("Handles dedicated CMI playtime tracker", PlayTimeListener.class),
  holograms("Handles hologram updates", HologramListener.class),
  portals("Handles portals", PortalListener.class),
  homeInteractions(
      "Handles home bed interactions and bed destroy events. Disabling this homes will still work",
      HomeListener.class,
      HomeListener1_11.class),
  dynamicSigns("Handles dynamic signs", SignListener.class),
  signEdit(
      "Handles sign editing",
      new CMIModuleClass(PlayerSignEditListeners.class),
      new CMIModuleClass(PlayerSignEdit1_20Listeners.class, Version.v1_20_R1)),
  coloredArmor(
      "Handles leather armor which has dynamic colors", AnimationColoredArmorListener.class),
  noTarget("Handles entity target prevention on player /cmi notarget", NoTargetListener.class),
  ranks("Handles rankup system"),
  votifier("Handles votifier system", VotifierListener.class),
  playerCombat("Handles player combat system", PlayerCombatListener.class),
  headDrop(
      "Handles head drops system",
      new CMIModuleClass(HeadDropListener.class),
      new CMIModuleClass(HeadContainerListener.class, Version.v1_16_R3)),
  kits("Handles kit listener, mainly to give out new player kit", KitListener.class),
  schedule("Handles schedule system"),
  namePlates("Handles player name plates"),
  jail("Handles jail system", JailListener.class),
  flightCharge("Handles cmi flight charge system", FlightListener.class),
  shulkerBackpack("Handles shulker box as backpacks", ShulkerBoxListener.class, Version.v1_11_R1),
  decoratedpot(
      "Handles decorated pot content display", DecoratedPotListener.class, Version.v1_20_R1),
  skin("Handles player skins", SkinListener.class, Version.v1_8_R1),
  selection(
      "Handles selection areas. While this is disabled, command can still be used",
      new CMIModuleClass(SelectionListener.class)),
  hpBossBar(
      "Handles mob and player hp bars as boss bar",
      new CMIModuleClass(HpBarListener.class, Version.v1_9_R1)),
  afk(
      "Handling of afk feature. This will disable any checks related to afk and players will never"
          + " get into afk mode",
      new CMIModuleClass(AfkListener.class),
      new CMIModuleClass(AfkListener1_8.class, Version.v1_8_R1),
      new CMIModuleClass(AfkListener1_9.class, Version.v1_9_R1)),
  timedCommands("Handling timed commands like tfly and tgod", TimedCommandListener.class),
  eventCommands(
      "Handling of eventCommands.yml feature ",
      new CMIModuleClass(EventActionListener.class),
      new CMIModuleClass(EventActionListener1_9.class, Version.v1_9_R1),
      new CMIModuleClass(EventActionListener1_12.class, Version.v1_12_R1)),
  disabledEnchants("Handling of disabled enchants in inventory", EnchantListener.class),
  sitAnimation("Handling of siting on blocks", AnimationSitListener.class, Version.v1_8_R1),
  rideAnimation("Handling of ride suffocation detection", AnimationRideListener.class),
  bossBarCompass("Handling of boss bar compass", BossBarCompassListener.class, Version.v1_9_R1),
  elytraBoost("Elytra boost", ElytraBoostListener.class, Version.v1_9_R1),
  elytraLaunch("Elytra launch", ElytraLaunchListener.class, Version.v1_9_R1),
  elytraExploit(
      "Exploit prevention, like using trident for infinite flight or self damage with arrow",
      new CMIModuleClass(ElytraExploitListener.class, Version.v1_9_R1),
      new CMIModuleClass(ElytraExploitListener1_13.class, Version.v1_15_R1)),
  totemBehavior("Handling of custom totem behavior", ResurectionListener.class, Version.v1_11_R1),
  anvilColor("Handling of anvil item colorization", AnvilColorListener.class, Version.v1_9_R1),
  anvilRename("Handling of anvil item renaming", AnvilRenameListener.class, Version.v1_9_R1),
  launchPad(
      "Handling of launch command falldamage",
      new CMIModuleClass(LaunchPadListener.class),
      new CMIModuleClass(LaunchPadListener1_9.class, Version.v1_9_R1)),
  deathMessages("Handling of custom death messages", DeathMessageListener.class),
  serverLinks("Handling of server links"),
  hat("Handling of hats", HatListeners.class),
  dialogs("Handling of some dialogs UI aspects", DialogsListener.class, Version.v1_21_R5),
  speedtracking(
      "Handling of speed tracking",
      new CMIModuleClass(CMISpeedListener.class),
      new CMIModuleClass(CMISpeedListener1_9.class, Version.v1_9_R1));

  private CMIModuleClass[] listeners;
  private boolean enabled = true;
  private String desc;
  private Version from = null;

  CMIModule(String string, Class<?> moduleClass) {
    this(string, Version.v1_7_R4, new CMIModuleClass(moduleClass));
  }

  CMIModule(String string, Class<?> moduleClass, Class<?> secondModuleClass) {
    this(
        string,
        Version.v1_7_R4,
        new CMIModuleClass(moduleClass),
        new CMIModuleClass(secondModuleClass));
  }

  CMIModule(String string, Class<?> moduleClass, Version version) {
    this(string, version, new CMIModuleClass(moduleClass, version));
  }

  CMIModule(String string, CMIModuleClass... cmiModuleClasses) {
    this(string, Version.v1_7_R4, cmiModuleClasses);
  }

  CMIModule(String string, Version version, CMIModuleClass... cmiModuleClasses) {
    this.desc = string;
    this.from = version;
    this.listeners = cmiModuleClasses;
  }

  public static CMIModule getByName(String string) {
    CMIModule[] cmiModules;
    int value = (cmiModules = values()).length;

    for (int index = 0; index < value; index++) {
      CMIModule cmiModule = cmiModules[index];
      if (cmiModule.toString().equalsIgnoreCase(string)) {
        return cmiModule;
      }
    }

    return null;
  }

  public static boolean isEnabled(String string) {
    CMIModule[] cmiModules;
    int value = (cmiModules = values()).length;

    for (int index = 0; index < value; index++) {
      CMIModule cmiModule = cmiModules[index];
      if (cmiModule.toString().equalsIgnoreCase(string)) {
        return cmiModule.enabled;
      }
    }

    return true;
  }

  public Version getFromVersion() {
    if (this.listeners != null) {
      CMIModuleClass[] cmiModuleClasses = this.listeners;
      int value = this.listeners.length;

      for (int index = 0; index < value; index++) {
        CMIModuleClass cmiModuleClass = cmiModuleClasses[index];
        if (cmiModuleClass.getFrom() != null && cmiModuleClass.getFrom().isHigher(this.from)) {
          this.from = cmiModuleClass.getFrom();
        }
      }
    }

    return this.from;
  }

  public Version getToVersion() {
    Version version = null;
    if (this.listeners != null) {
      CMIModuleClass[] cmiModuleClasses = this.listeners;
      int value = this.listeners.length;

      for (int index = 0; index < value; index++) {
        CMIModuleClass cmiModuleClass = cmiModuleClasses[index];
        if (cmiModuleClass.getTo() != null) {
          version = cmiModuleClass.getTo();
        }
      }
    }

    return version;
  }

  public void setEnabled(boolean flag) {
    this.enabled = flag;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void registerListener() {
    PluginManager pluginManager = CMI.getInstance().getServer().getPluginManager();
    if (this.listeners != null) {
      CMIModuleClass[] cmiModuleClasses = this.listeners;
      int value = this.listeners.length;

      for (int index = 0; index < value; index++) {
        CMIModuleClass cmiModuleClass = cmiModuleClasses[index];
        if ((cmiModuleClass.getFrom() == null || !Version.isCurrentLower(cmiModuleClass.getFrom()))
            && (cmiModuleClass.getTo() == null || !Version.isCurrentHigher(cmiModuleClass.getTo()))
            && (cmiModuleClass.getType() == CMIModuleServerType.Any
                || (cmiModuleClass.getType() != CMIModuleServerType.SpigotOnly
                        || !Version.isPaperBranch())
                    && (cmiModuleClass.getType() != CMIModuleServerType.PaperOnly
                        || Version.isPaperBranch()))) {
          var listener = cmiModuleClass.getListener();

          try {
            Constructor constructor = listener.getConstructor(CMI.class);
            Object object = constructor.newInstance(CMI.getInstance());
            pluginManager.registerEvents((Listener) object, CMI.getInstance());
          } catch (Exception | Error value3) {
            value3.printStackTrace();

            try {
              Constructor fallbackConstructor = listener.getConstructor();
              Object listenerInstance = fallbackConstructor.newInstance(listener);
              pluginManager.registerEvents((Listener) listenerInstance, CMI.getInstance());
            } catch (Exception | Error value4) {
              value4.printStackTrace();
            }
          }
        }
      }
    }
  }

  public String getDesc() {
    return this.desc;
  }

  public void setDesc(String string) {
    this.desc = string;
  }

  public CMIModuleClass[] getListeners() {
    return this.listeners;
  }
}
