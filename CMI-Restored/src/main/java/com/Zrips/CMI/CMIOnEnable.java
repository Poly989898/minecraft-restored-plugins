package com.Zrips.CMI;

import com.Zrips.CMI.AllListeners.ArmorChangeListener;
import com.Zrips.CMI.AllListeners.ArmorChangeListener1_9;
import com.Zrips.CMI.AllListeners.GlicthListeners;
import com.Zrips.CMI.AllListeners.MobExpirationListener;
import com.Zrips.CMI.AllListeners.NameChangeListener;
import com.Zrips.CMI.AllListeners.PlayerCanvasEventListener;
import com.Zrips.CMI.AllListeners.PlayerFoliaEventListener;
import com.Zrips.CMI.AllListeners.PlayerListeners;
import com.Zrips.CMI.AllListeners.PlayerListeners1_8;
import com.Zrips.CMI.AllListeners.UserEvents;
import com.Zrips.CMI.Containers.CMIPlayerConnection;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.CustomEventTrigers.AnvilListener;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Alias.AliasTabCompListener_1_13_older;
import com.Zrips.CMI.Modules.Alias.AliasTabCompListener_1_14_never;
import com.Zrips.CMI.Modules.Alias.onPreprocessCommand;
import com.Zrips.CMI.Modules.Alias.onServerPreprocessCommand;
import com.Zrips.CMI.Modules.Animations.AnimationListener;
import com.Zrips.CMI.Modules.Animations.AnimationListener1_21;
import com.Zrips.CMI.Modules.Backup.CMIBackUpManager;
import com.Zrips.CMI.Modules.ChatFilter.ChatFilterListener;
import com.Zrips.CMI.Modules.ChatFormat.ChatFormatListener;
import com.Zrips.CMI.Modules.ChatHandling.CMIAsyncChatListener;
import com.Zrips.CMI.Modules.CustomText.CTextEditorListener;
import com.Zrips.CMI.Modules.DynMap.DynMapListener;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import com.Zrips.CMI.Modules.InvEndEditor.EnderChestListener;
import com.Zrips.CMI.Modules.InvRegEditor.RegChestListener;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModuleManager;
import com.Zrips.CMI.Modules.NickName.NickNameListener;
import com.Zrips.CMI.Modules.Packets.PacketInjector;
import com.Zrips.CMI.Modules.Packets.PacketPlayerListeners;
import com.Zrips.CMI.Modules.Repair.RepairListener;
import com.Zrips.CMI.Modules.SavedInv.SaveInvListener;
import com.Zrips.CMI.Modules.Teleportations.InvulnerabilityListener;
import com.Zrips.CMI.Modules.Worlds.CMIWorldListener;
import com.Zrips.CMI.Modules.Worth.WorthListener;
import com.Zrips.CMI.utils.CMIEncoder;
import com.Zrips.CMI.utils.CMIThread;
import com.Zrips.CMI.utils.FileDownloader;
import com.Zrips.CMI.utils.ServerLinksManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Locale.YmlMaker;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scoreboard.Team;

public class CMIOnEnable {
  static List<String> logo =
      new ArrayList<>(
          Arrays.asList(
              "&7_______________________________________________________",
              " ",
              "&7 ██████&8╗&f███&8╗   &f███&8╗&7██&8╗",
              "&7██&8╔════╝&f████&8╗ &f████&8║&7██&8║",
              "&7██&8║     &f██&8╔&f████&8╔&f██&8║&7██&8║",
              "&7██&8║     &f██&8║╚&f██&8╔╝&f██&8║&7██&8║",
              "&8╚&7██████&8╗&f██&8║ ╚═╝ &f██&8║&7██&8║",
              "&8 ╚═════╝╚═╝     ╚═╝╚═╝",
              "&7_______________________________________________________"));
  static List<String> SpigotLogo =
      new ArrayList<>(
          Arrays.asList(
              "_______________________________________________________",
              "   ______  ____    ____  _____  ",
              " .' ___  ||_   \\  /   _||_   _| ",
              "/ .'   \\_|  |   \\/   |    | |   ",
              "| |         | |\\  /| |    | |   ",
              "\\ `.___.'\\ _| |_\\/_| |_  _| |_  ",
              " `.____ .'|_____||_____||_____| ",
              "_______________________________________________________"));
  static List<String> FoliaError =
      new ArrayList<>(
          Arrays.asList(
              "_______________________________________________________",
              "_ &5Folia server detected.",
              "_______________________________________________________"));
  private static int requiredCMILibVersion = 1050203;
  private static final String slastCMILibVersion = "1.5.2.3";
  private static final String srequiredCMILibVersion = "1.5.2.3";
  public static int cmiLibId = 87610;

  public static void defaultLocaleDownloader() {
    CompletableFuture.runAsync(
        () -> {
          Long time = System.currentTimeMillis();

          try {
            var list =
                Arrays.asList(
                    "CN", "CZ", "DE", "ES", "FR", "IT", "LT", "NO", "PL", "RU", "SK", "SL", "TR",
                    "UA", "ZH", "KO", "JA", "BR");
            String string = null;
            boolean flag = true;

            try {
              File file = new File(CMI.getInstance().getDataFolder(), "config.yml");
              if (file.isFile()) {
                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
                flag = yamlConfiguration.getBoolean("LanguageDownload", true);
              }
            } catch (Throwable throwable) {
              throwable.printStackTrace();
            }

            if (!flag) {
              return;
            }

            for (String text : list) {
              File fileSecond =
                  new File(
                      CMI.getInstance().getDataFolder() + File.separator + "Translations",
                      "Locale_" + text + ".yml");
              if (!fileSecond.isFile()) {
                string = text;
              }
            }

            final String textSecond = string;

            for (final String textThird : list) {
              File fileThird =
                  new File(
                      CMI.getInstance().getDataFolder() + File.separator + "Translations",
                      "Locale_" + textThird + ".yml");
              if (!fileThird.isFile()) {
                FileDownloader fileDownloader =
                    new FileDownloader() {
                      @Override
                      public void afterDownload() {
                        CMI.getInstance()
                            .consoleMessage("Downloaded Locale_" + textThird + ".yml file");
                        if (textSecond != null && textThird.equalsIgnoreCase(textSecond)) {
                          CMI.getInstance().getConfigManager().reloadLanguage();
                        }
                      }
                    };
                fileDownloader.downloadUsingStream(
                    "https://raw.githubusercontent.com/Zrips/CMI/master/Translations/Locale_"
                        + textThird
                        + ".yml",
                    fileThird.getPath());
              }
            }
          } catch (Throwable throwable2) {
            throwable2.printStackTrace();
          }

          CMI.getInstance().loadMessage("-", "locale download", System.currentTimeMillis() - time);
        },
        CMIThread.getExecutor());
  }

  private static void libCheck() {
    Plugin plugin = Bukkit.getPluginManager().getPlugin("CMILib");
    if (plugin == null) {
      CMI.getInstance()
          .consoleMessage(
              "&cPlugin requires CMILib (1.5.2.3) version to work properly. We will try to download"
                  + " it automatically. If not successful please download it manually from"
                  + " https://www.zrips.net/CMILib/CMILib1.5.2.3.jar");
      CMI.getInstance().consoleMessage("&cAfter download please restart server");
    }
  }

  private static void libDownloader() {
    Plugin plugin = Bukkit.getPluginManager().getPlugin("CMILib");
    if (plugin == null) {
      File file = new File(CMI.getInstance().getDataFolder().getParent(), "CMILib1.5.2.3.jar");
      CMI.getInstance().consoleMessage("&aDownloading CMILib file.");
      FileDownloader fileDownloader =
          new FileDownloader() {
            @Override
            public void afterDownload() {
              CMI.getInstance().consoleMessage("&2Downloaded CMILib file.");
              CMI.getInstance()
                  .consoleMessage("&2Please restart server for this to take full effect");
            }

            @Override
            public void failedDownload() {
              CMI.getInstance()
                  .consoleMessage(
                      "&cPlugin will not work properly without CMILib plugin, please download it"
                          + " manually");
            }
          };
      fileDownloader.downloadUsingStream(
          "https://www.zrips.net/CMILib/CMILib1.5.2.3.jar", file.getPath());
    }
  }

  private static boolean makeDir(String string) {
    File file = new File(CMI.getInstance().getDataFolder(), string);
    return !file.isDirectory() ? file.mkdir() : false;
  }

  public static void init(CMI cmi) {
    CMI.instance = cmi;
    CMI.serverStartupTime = System.currentTimeMillis();
    Version.getCurrent();

    for (String string : Version.isPaper() ? logo : SpigotLogo) {
      CMIMessages.consoleMessage(CMIChatColor.translate(string));
    }

    if (Version.isFolia()) {
      for (String text : FoliaError) {
        CMIMessages.consoleMessage(CMIChatColor.translate(text));
      }
    }

    makeDir("Images");
    makeDir("Settings");
    makeDir("Saves");
    boolean flag = makeDir("Dialogs");
    makeDir("Logs");
    makeDir("Kits");
    makeDir("Translations");
    cmi.getDeathMessageManager().relocateDeathMessageLocaleFiles();
    if (Version.isPaper()) {
      try {
        cmi.consoleMessage("&3Integrating &5PaperSpigot &3async methods");
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }

    libCheck();
    libDownloader();
    defaultLocaleDownloader();

    try {
      for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
        if (team.getName().startsWith("CMINP")) {
          team.unregister();
        }
      }
    } catch (Throwable throwable2) {
      throwable2.printStackTrace();
    }

    CMIHookType.Vault.init();
    CMIHookType.Citizens.init();
    CMIHookType.NoCheatPlus.init();
    CMIHookType.AureliumSkills.init();
    CMIHookType.AuraSkills.init();
    CMIBackUpManager.loadConfig();
    CMIModuleManager.load();
    CMIHookType.Votifier.init();
    CMIHookType.BlueMap.init();

    try {
      cmi.getPortalManager().load();
    } catch (Throwable throwable3) {
      throwable3.printStackTrace();
    }

    try {
      cmi.getSignManager().load();
    } catch (Throwable throwable4) {
      throwable4.printStackTrace();
    }

    cmi.getPlayTimeManager();
    cmi.scoreboard = cmi.getServer().getScoreboardManager().getMainScoreboard();
    if (!new File(cmi.getDataFolder(), "kits.yml").isFile()
        && !new File(cmi.getDataFolder(), "Saves" + File.separator + "Kits.yml").isFile()
        && !new File(cmi.getDataFolder(), "Kits" + File.separator + "Kits.yml").isFile()) {
      new YmlMaker(cmi, "Kits" + File.separator + "Kits.yml").saveDefaultConfig();
    }

    if (flag) {
      new YmlMaker(cmi, "Dialogs" + File.separator + "ExampleFirst.yml").saveDefaultConfig();
      new YmlMaker(cmi, "Dialogs" + File.separator + "ExampleSecond.yml").saveDefaultConfig();
    }

    if (!new File(cmi.getDataFolder(), "ranks.yml").isFile()
        && !new File(cmi.getDataFolder(), "Saves" + File.separator + "Ranks.yml").isFile()) {
      new YmlMaker(cmi, "Settings" + File.separator + "Ranks.yml").saveDefaultConfig();
    }

    if (!new File(cmi.getDataFolder(), "schedules.yml").isFile()
        && !new File(cmi.getDataFolder(), "Saves" + File.separator + "Schedules.yml").isFile()) {
      new YmlMaker(cmi, "Settings" + File.separator + "Schedules.yml").saveDefaultConfig();
    }

    if (!new File(cmi.getDataFolder(), "PlayTimeRewards.yml").isFile()
        && !new File(cmi.getDataFolder(), "Saves" + File.separator + "PlayTimeRewards.yml")
            .isFile()) {
      new YmlMaker(cmi, "Settings" + File.separator + "PlayTimeRewards.yml").saveDefaultConfig();
    }

    if (!new File(cmi.getDataFolder(), "worth.yml").isFile()
        && !new File(cmi.getDataFolder(), "Saves" + File.separator + "Worth.yml").isFile()) {
      new YmlMaker(cmi, "Saves" + File.separator + "Worth.yml").saveDefaultConfig();
    }

    YmlMaker ymlMaker = new YmlMaker(cmi, "CustomText" + File.separator + "rules.txt");
    if (!new File(cmi.getDataFolder(), "CustomText").isDirectory()) {
      ymlMaker.saveDefaultConfig();
    }

    cmi.lagTask =
        CMIScheduler.scheduleSyncRepeatingTask(CMI.getInstance(), cmi.getLagMeter(), 100L, 20L);
    cmi.getPermissionsManager();
    cmi.getDbManager();
    cmi.setDBClassloader();
    cmi.getPurgeManager();
    cmi.getUtilManager();
    CMIEncoder.initialize();
    cmi.getCommandManager().fillCommands();
    cmi.getAliasManager().loadRegularAlias();
    cmi.getCTextManager().load();
    cmi.getParticleManager().loadConfig();
    cmi.getRandomTeleportationManager().loadConfig();
    cmi.getConfigManager().load(false);

    try {
      if (Config.UseFakeOperator) {
        Player player = cmi.getPlayerManager().getFakeOperator();
        if (player.isOp()) {
          player.setOp(false);
          player.saveData();
          CMIScheduler.runTask(cmi, () -> cmi.getNMS().unloadData(player));
        }
      }
    } catch (Throwable throwable5) {
    }

    cmi.getChatManager().loadConfig(false);
    cmi.getDeathMessageManager().load();
    cmi.getChatFormatManager().load();
    if (!cmi.getDbManager().isConnected()) {
      CMILC.sendMessage(
          Bukkit.getConsoleSender(),
          "&cCant establish connection with (&6"
              + cmi.getDbManager().getDbType().name()
              + "&c) data base! Check your config file!");
      try {
        cmi.getDbManager().switchDataBase();
      } catch (java.sql.SQLException sqlException) {
        sqlException.printStackTrace();
      }
      CMILC.sendMessage(
          Bukkit.getConsoleSender(),
          "&cSwitching to another database system!(&6"
              + cmi.getDbManager().getDbType().name()
              + "&c)");
      cmi.getConfigManager().ChangeConfig("storage.method", cmi.getDbManager().getDbType().name());
      cmi.getConfigManager().reload(null);
    }

    cmi.getBungeeCordManager().initialize();
    cmi.getCommandCostManager().loadConfig(false);

    try {
      cmi.getKitsManager().load();
    } catch (Throwable throwable6) {
      throwable6.printStackTrace();
      cmi.consoleMessage(
          "&cFailed to load kits.yml file. Report about this issue at github " + CMI.bugReportLink);
    }

    cmi.getDialogManager().load();
    cmi.getRankManager().load();
    cmi.getSavedItemManager().load();
    cmi.getPlayTimeRewardManager().load();
    cmi.getJailManager().load();
    cmi.getPlayerManager().loadData();
    cmi.getPlayTimeManager().updatePlayTimes();
    cmi.getAliasManager().load();
    cmi.getCommand("cmi").setExecutor(cmi.getCommandManager());
    cmi.getCommand("cmi").setTabCompleter(cmi.getTab());
    PluginDescriptionFile pluginDescriptionFile = cmi.getDescription();
    PluginManager pluginManager = cmi.getServer().getPluginManager();
    Long time = System.currentTimeMillis();
    pluginManager.registerEvents(new CMIPlayerConnection(), cmi);
    pluginManager.registerEvents(new NameChangeListener(cmi), cmi);
    pluginManager.registerEvents(new InvulnerabilityListener(cmi), cmi);
    pluginManager.registerEvents(new RegChestListener(cmi), cmi);
    pluginManager.registerEvents(new CMIWorldListener(cmi), cmi);
    pluginManager.registerEvents(new ArmorChangeListener(cmi), cmi);
    pluginManager.registerEvents(new MobExpirationListener(cmi), cmi);
    if (Version.isTestServer()) {
      CMIAsyncChatListener cmiAsyncChatListener = new CMIAsyncChatListener();
      cmiAsyncChatListener.init();
      pluginManager.registerEvents(cmiAsyncChatListener, cmi);
    }

    if (Version.isCurrentHigher(Version.v1_8_R3)) {
      pluginManager.registerEvents(new ArmorChangeListener1_9(cmi), cmi);
    }

    pluginManager.registerEvents(new EnderChestListener(cmi), cmi);
    pluginManager.registerEvents(new onPreprocessCommand(), cmi);
    if (Version.isCurrentHigher(Version.v1_7_R4)) {
      pluginManager.registerEvents(new onServerPreprocessCommand(), cmi);
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
      pluginManager.registerEvents(new AliasTabCompListener_1_14_never(), cmi);
    } else {
      pluginManager.registerEvents(new AliasTabCompListener_1_13_older(), cmi);
    }

    pluginManager.registerEvents(new SaveInvListener(cmi), cmi);
    pluginManager.registerEvents(new UserEvents(cmi), cmi);
    pluginManager.registerEvents(new PlayerListeners(cmi), cmi);
    if (Version.isCurrentEqualOrHigher(Version.v1_8_R1)) {
      pluginManager.registerEvents(new PlayerListeners1_8(cmi), cmi);
    }

    pluginManager.registerEvents(new NickNameListener(cmi), cmi);
    pluginManager.registerEvents(new CTextEditorListener(cmi), cmi);

    try {
      cmi.getInteractiveCommandManager().load();
    } catch (Throwable throwable7) {
      throwable7.printStackTrace();
    }

    try {
      if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
        pluginManager.registerEvents(new AnvilListener(cmi), cmi);
      }
    } catch (Throwable throwable8) {
      throwable8.printStackTrace();
    }

    pluginManager.registerEvents(new ChatFormatListener(cmi), cmi);

    try {
      pluginManager.registerEvents(new AnimationListener(cmi), cmi);
      if (Version.isCurrentEqualOrHigher(Version.v1_20_R3)) {
        pluginManager.registerEvents(new AnimationListener1_21(cmi), cmi);
      }
    } catch (Throwable throwable9) {
      cmi.consoleMessage("Failed to register AnimationListener");
    }

    if (Version.isFolia()) {
      PlayerCanvasEventListener.register(cmi);
    }

    if (Version.isFolia() && Version.isCurrentEqualOrHigher(Version.v1_21_11)) {
      pluginManager.registerEvents(new PlayerFoliaEventListener(), cmi);
    }

    CMIHookType.dynmap.init();
    cmi.getDynMapManager().init();
    if (CMIHookType.dynmap.isEnabled()) {
      pluginManager.registerEvents(new DynMapListener(cmi), cmi);
    }

    if (Config.NoCommandsInBed || Config.LimitBooks) {
      pluginManager.registerEvents(new GlicthListeners(cmi), cmi);
    }

    if (cmi.getRepairManager().isRSProtectCommandRepair()
        || cmi.getRepairManager().isRSProtectNormalRepair()) {
      pluginManager.registerEvents(new RepairListener(cmi), cmi);
    }

    pluginManager.registerEvents(new ChatFilterListener(cmi), cmi);
    pluginManager.registerEvents(new WorthListener(cmi), cmi);
    cmi.loadMessage("-", "Registered events", System.currentTimeMillis() - time);
    cmi.getPortalManager().handlePortalVisualizerUpdates();
    if (Version.isCurrentEqualOrHigher(Version.v1_8_R1)) {
      cmi.injector = new PacketInjector();
      pluginManager.registerEvents(new PacketPlayerListeners(cmi), cmi);
    }

    cmi.getEventActionManager().load();
    cmi.getConfigManager().LoadLang("EN");
    if (!cmi.getConfigManager().Lang.equalsIgnoreCase("EN")) {
      cmi.getConfigManager().LoadLang(cmi.getConfigManager().Lang);
    }

    cmi.getDeathMessageManager();
    cmi.getWorthManager().load();
    cmi.getWorthManager().loadEnchtWorth();
    cmi.initLM();
    cmi.getWarpManager().load();
    CMIHookType.VaultPermissions.init();
    CMIHookType.PlayerVaults.init();
    CMIHookType.PlaceholderAPI.init();
    CMIHookType.MVdWPlaceholderAPI.init();
    CMIHookType.LWC.init();
    CMIHookType.mcMMO.init();
    CMIHookType.Jobs.init();
    if (cmi.getPurgeManager().isCleanOnStart()) {
      CMI.getInstance().consoleMessage("Cleaning.");
      cmi.getPurgeManager().initialize();
    }

    cmi.getTimeManager().runTimer();
    cmi.getRegionManager();
    cmi.getSchedulerManager().load();
    cmi.getLookupService();
    cmi.getHologramManager().load();
    ServerLinksManager.load();
    cmi.getSkinManager().load();
    if (CMIModule.totemBehavior.isEnabled()) {
      CMIScheduler.scheduleSyncRepeatingTask(
          CMI.getInstance(),
          () -> CMI.getInstance().getTotemManager().checkAllForTotemCd(),
          0L,
          20L);
    }

    cmi.getRankManager().run();
    cmi.getRankManager().autoRecheck();
    cmi.getEnchantManager().loadDisabledEnchants();
    cmi.getEnchantManager().disableEnchantsOnLoad();

    for (Player playerSecond : Bukkit.getOnlinePlayers()) {
      CMIUser cmiUser = cmi.getPlayerManager().getUser(playerSecond);
      if (cmiUser != null) {
        if (CMIModule.vanish.isEnabled()) {
          cmi.getVanishManager().applyVanish(cmiUser);
        }

        cmiUser.reapplyNamePlate();
      }
    }

    cmi.getDiscordSRVManager();
    if (CMIModule.jail.isEnabled()) {
      cmi.getJailManager().recheckAllOnline();
    }

    if (!cmi.getStatsManager().isDataLoaded()) {
      CMILC.sendMessage(
          Bukkit.getConsoleSender(), cmi.prefix + "Preloading playtimetop statistics");
      cmi.getStatsManager();
    }

    try {
      cmi.getRecipeManager().load();
    } catch (Throwable throwable10) {
      throwable10.printStackTrace();
    }

    cmi.getArmorStandManager().load();
    CMIModuleManager.registerListeners();
    cmi.consoleMessage("Version &7" + pluginDescriptionFile.getVersion() + " &3has been enabled");
    CMILC.sendMessage(
        Bukkit.getConsoleSender(),
        CMIChatColor.translate("&7_______________________________________________________"));
    cmi.fullyLoaded = true;
    CMIScheduler.runTaskLater(CMI.getInstance(), () -> cmi.serverLoaded = true, 1L);
  }
}
