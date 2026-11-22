package net.Zrips.CMILib;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.Zrips.CMILib.BossBar.BossBarListener;
import net.Zrips.CMILib.BossBar.BossBarManager;
import net.Zrips.CMILib.Chat.ChatEditorListener;
import net.Zrips.CMILib.Container.CMIPlayerConnection;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.GUI.GUIListener;
import net.Zrips.CMILib.GUI.GUIListener1_9;
import net.Zrips.CMILib.GUI.GUIManager;
import net.Zrips.CMILib.Items.ItemManager;
import net.Zrips.CMILib.Locale.Language;
import net.Zrips.CMILib.Logs.CMILogManager;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Placeholders.Placeholder;
import net.Zrips.CMILib.Placeholders.PlaceholderAPIHook;
import net.Zrips.CMILib.RawMessages.RawMessageListener;
import net.Zrips.CMILib.Shadow.ShadowCommandListener;
import net.Zrips.CMILib.Skins.SkinManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import net.Zrips.CMILib.Worlds.WorldsListener;
import net.Zrips.CMILib.commands.CommandsHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CMILib extends JavaPlugin {
  private static final UUID ServerUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
  private static final UUID emptyUserUUID = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
  private static final UUID fakeUserUUID = UUID.fromString("ffffffff-ffff-ffff-ffff-fffffffffff0");
  private static final String fakeUserName = "CMIFakeOperator";
  public static final String savesFolderName = "Saves";
  public static final String translationsFolderName = "Translations";
  public static final String itemsFolderName = "Items";
  private static CMILib instance;
  private boolean cmiPresent = false;
  private Reflections ref = null;
  private Language languageManager = null;
  private CMILibConfig config = null;
  private ItemManager itemManager = null;
  private TabComplete tab = null;
  private CommandsHandler cManager = null;
  private Placeholder Placeholder = null;
  private CMILogManager logManager;
  private GUIManager GUIManager;
  private BossBarManager BossBarManager;
  private SkinManager SkinManager;
  private boolean PlaceholderAPIEnabled = false;
  private int cmiLibResourceId = 87610;

  public static CMILib getInstance() {
    return instance;
  }

  public GUIManager getGUIManager() {
    if (this.GUIManager == null) {
      this.GUIManager = new GUIManager(this);
    }

    return this.GUIManager;
  }

  public BossBarManager getBossBarManager() {
    if (this.BossBarManager == null) {
      this.BossBarManager = new BossBarManager(this);
    }

    return this.BossBarManager;
  }

  public SkinManager getSkinManager() {
    if (this.SkinManager == null) {
      this.SkinManager = new SkinManager(this);
    }

    return this.SkinManager;
  }

  public CMILogManager getLogManager() {
    if (this.logManager == null) {
      this.logManager = new CMILogManager();
    }

    return this.logManager;
  }

  public Reflections getReflectionManager() {
    if (this.ref == null) {
      this.ref = new Reflections(this);
    }

    return this.ref;
  }

  public Language getLM() {
    if (this.languageManager == null) {
      this.languageManager = new Language(this, CMILibConfig.lang);
      this.languageManager.reload();
    }

    return this.languageManager;
  }

  public CMILibConfig getConfigManager() {
    if (this.config == null) {
      this.config = new CMILibConfig(this);
    }

    return this.config;
  }

  public TabComplete getTab() {
    if (this.tab == null) {
      this.tab = new TabComplete(this);
    }

    return this.tab;
  }

  public ItemManager getItemManager() {
    if (this.itemManager == null) {
      this.itemManager = new ItemManager(getInstance());
    }

    return this.itemManager;
  }

  public CommandsHandler getCommandManager() {
    if (this.cManager == null) {
      this.cManager = new CommandsHandler(this);
    }

    return this.cManager;
  }

  public void defaultLocaleDownloader() {
    try {
      List<String> lang =
          Arrays.asList(
              "BR", "CN", "CZ", "DE", "ES", "FR", "IT", "LT", "NO", "PL", "RU", "SK", "SL", "TR",
              "UA", "ZH");
      String lr = null;
      boolean download = true;

      try {
        File f = new File(this.getDataFolder(), "config.yml");
        if (f.isFile()) {
          YamlConfiguration conf = YamlConfiguration.loadConfiguration(f);
          download = conf.getBoolean("LanguageDownload", true);
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }

      if (!download) {
        return;
      }

      for (String one : lang) {
        File file =
            new File(
                this.getDataFolder() + File.separator + "Translations", "Locale_" + one + ".yml");
        if (!file.isFile()) {
          lr = one;
        }
      }

      final String flr = lr;

      for (final String one : lang) {
        File file =
            new File(
                this.getDataFolder() + File.separator + "Translations", "Locale_" + one + ".yml");
        if (!file.isFile()) {
          FileDownloader downloader =
              new FileDownloader() {
                @Override
                public void afterDownload() {
                  CMIMessages.consoleMessage("Downloaded Locale_" + one + ".yml file");
                  if (flr != null && one.equalsIgnoreCase(flr)) {
                    CMILib.this.getConfigManager().reloadLanguage();
                  }
                }
              };
          downloader.downloadUsingStream(
              "https://raw.githubusercontent.com/Zrips/CMILib/master/Translations/Locale_"
                  + one
                  + ".yml",
              file.getPath(),
              true);
        }
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  public void defaultItemLocaleDownloader() {
    try {
      List<String> lang = Arrays.asList("ES", "CN", "RU");
      String lr = null;

      for (String one : lang) {
        File file =
            new File(
                this.getDataFolder() + File.separator + "Translations" + File.separator + "Items",
                "items_" + one + ".yml");
        if (!file.isFile()) {
          lr = one;
        }
      }

      final String flr = lr;

      for (final String one : lang) {
        File file =
            new File(
                this.getDataFolder() + File.separator + "Translations" + File.separator + "Items",
                "items_" + one + ".yml");
        if (!file.isFile()) {
          FileDownloader downloader =
              new FileDownloader() {
                @Override
                public void afterDownload() {
                  CMIMessages.consoleMessage("Downloaded items_" + one + ".yml file");
                  if (flr != null && one.equalsIgnoreCase(flr)) {
                    CMILib.this.getConfigManager().reloadLanguage();
                  }
                }
              };
          downloader.downloadUsingStream(
              "https://raw.githubusercontent.com/Zrips/CMILib/master/Translations/Items/items_"
                  + one
                  + ".yml",
              file.getPath(),
              true);
        }
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  public void onEnable() {
    instance = this;
    File f = new File(this.getDataFolder(), "Saves");
    if (!f.isDirectory()) {
      f.mkdir();
    }

    this.getItemManager().load();
    this.getConfigManager().load();
    this.getItemManager().loadLocale();
    PluginManager pm = this.getServer().getPluginManager();
    this.defaultLocaleDownloader();
    this.defaultItemLocaleDownloader();
    new updateChecker(this, this.cmiLibResourceId)
        .getVersion(
            version -> {
              Integer cmilibVersion = Version.convertVersion(this.getDescription().getVersion());
              Integer cmilVersion = Version.convertVersion(version);
              if (cmilVersion > cmilibVersion) {
                if (!CMILibConfig.autoUpdate) {
                  CMIMessages.consoleMessage(
                      "&2New version of CMILib was detected. Please update it ("
                          + this.getDescription().getVersion()
                          + " -> "
                          + version
                          + ")");
                  return;
                }

                File folder = new File(this.getDataFolder().getParent(), "update");
                if (!folder.isDirectory()) {
                  folder.mkdir();
                }

                File file = new File(folder, "CMILib" + version + ".jar");
                if (!file.isFile()) {
                  FileDownloader downloader =
                      new FileDownloader() {
                        @Override
                        public void afterDownload() {
                          CMIMessages.consoleMessage("&2Downloaded CMILib" + version + " file");
                          File dir =
                              new File(CMILib.this.getDataFolder() + File.separator + "Libs");
                          if (dir.isDirectory()) {
                            for (File one : dir.listFiles()) {
                              if (one.getName().contains("CMILib")
                                  && one.getName().endsWith(".jar")) {
                                File dest =
                                    new File(
                                        CMILib.this.getDataFolder().getParent(), one.getName());
                                if (!dest.exists()) {
                                  one.renameTo(dest);
                                }
                                break;
                              }
                            }
                          }

                          CMIMessages.consoleMessage(
                              "&2Please restart server for this to take full effect");
                        }

                        @Override
                        public void failedDownload() {
                          CMIMessages.consoleMessage(
                              "&eThe attempt to automatically download CMILib has failed");
                          CMIMessages.consoleMessage(
                              "&eSome plugins might not work properly without the newest version");
                          CMIMessages.consoleMessage(
                              "&eYou can download it manually from:"
                                  + " https://www.zrips.net/CMILib/CMILib"
                                  + version
                                  + ".jar");
                        }
                      };
                  downloader.downloadUsingStream(
                      "https://www.zrips.net/CMILib/CMILib" + version + ".jar",
                      file.getPath(),
                      false);
                }
              }
            });
    this.setupCMIAPI();
    this.setupPlaceHolderAPI();
    this.getCommand(CommandsHandler.getLabel()).setExecutor(this.getCommandManager());
    this.getCommand(CommandsHandler.getLabel()).setTabCompleter(this.getTab());
    pm.registerEvents(new CMIPlayerConnection(), this);
    pm.registerEvents(new ChatEditorListener(this), this);
    pm.registerEvents(new BossBarListener(this), this);
    pm.registerEvents(new GUIListener(this), this);
    if (Version.isCurrentEqualOrHigher(Version.v1_9_0)) {
      pm.registerEvents(new GUIListener1_9(this), this);
    }

    pm.registerEvents(new RawMessageListener(), this);
    pm.registerEvents(new ShadowCommandListener(), this);
    pm.registerEvents(new WorldsListener(), this);
    this.getCommandManager().fillCommands();
    CMIEnchantment.initialize();
    CMIScheduler.runTask(this, CMIEnchantment::initialize);
    this.getConfigManager().LoadLang("EN");
    if (!CMILibConfig.lang.equalsIgnoreCase("EN")) {
      this.getConfigManager().LoadLang(CMILibConfig.lang);
    }
  }

  public void onDisable() {
    try {
      if (this.GUIManager != null) {
        this.getGUIManager().closeAll();
      }
    } catch (Throwable var2) {
    }
  }

  private boolean setupPlaceHolderAPI() {
    if (!this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      return false;
    }

    if (new PlaceholderAPIHook(this).register()) {
      CMIMessages.consoleMessage("PlaceholderAPI hooked.");
    }

    this.PlaceholderAPIEnabled = true;
    return true;
  }

  private boolean setupCMIAPI() {
    if (this.getServer().getPluginManager().getPlugin("CMI") == null) {
      return false;
    }

    CMIMessages.consoleMessage("CMI hooked.");
    this.cmiPresent = true;
    return true;
  }

  public boolean isPlaceholderAPIEnabled() {
    return this.PlaceholderAPIEnabled;
  }

  public Placeholder getPlaceholderAPIManager() {
    if (this.Placeholder == null) {
      this.Placeholder = new Placeholder(this);
    }

    return this.Placeholder;
  }

  public UUID getServerUUID() {
    return ServerUUID;
  }

  public static String getFakeUserName() {
    return "CMIFakeOperator";
  }

  public boolean isCmiPresent() {
    return this.cmiPresent;
  }

  public void info(Class<?> c, Player player, String path, Object... variables) {
    this.info(c.getSimpleName(), player, path, variables);
  }

  public void info(Object c, Player player, String path, Object... variables) {
    this.info(c.getClass().getSimpleName(), player, path, variables);
  }

  public void info(Object thi, CommandSender sender, String path, Object... variables) {
    this.info(thi.getClass().getSimpleName(), sender, path, variables);
  }

  public void info(String c, CommandSender sender, String path, Object... variables) {
    if (sender != null) {
      String msg = this.getLM().getMessage("command." + c + ".info." + path, variables);
      if (!msg.isEmpty()) {
        CMIMessages.sendMessage(sender, msg, false);
      }
    }
  }

  public void info(String c, Player player, String path, Object... variables) {
    if (player != null) {
      String msg = this.getLM().getMessage("command." + c + ".info." + path, variables);
      if (!msg.isEmpty()) {
        CMIMessages.sendMessage(player, msg, false);
      }
    }
  }

  public void loadMessage(Integer amount, String type, Long took) {
    if (this.isEnabled()) {
      CMIMessages.consoleMessage(
          "&3Loaded (&f" + amount + "&3) &7" + type + " &3into memory. &6Took &e" + took + "&6ms");
    }
  }
}
