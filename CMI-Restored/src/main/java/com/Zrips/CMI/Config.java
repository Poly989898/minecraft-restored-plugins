package com.Zrips.CMI;

import com.Zrips.CMI.Containers.CMICompass;
import com.Zrips.CMI.Containers.CMICustomLogInOutMessages;
import com.Zrips.CMI.Containers.CMIMail;
import com.Zrips.CMI.Containers.CMISoundPreset;
import com.Zrips.CMI.Containers.CMIWorldLimits;
import com.Zrips.CMI.Containers.DamageControl;
import com.Zrips.CMI.Containers.RandomTeleport;
import com.Zrips.CMI.Containers.SleepStats;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Backup.CMIBackUpManager;
import com.Zrips.CMI.Modules.Chat.ChatManager;
import com.Zrips.CMI.Modules.ChatFilter.ChatFilterRule;
import com.Zrips.CMI.Modules.CustomText.CText;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayTime.CMIPlayTimeFormat;
import com.Zrips.CMI.Modules.Teleportations.TeleportManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.CMICommand;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.list.condense;
import com.Zrips.CMI.commands.list.reload;
import com.Zrips.CMI.commands.list.uncondense;
import com.Zrips.CMI.events.CMIConfigReloadEvent;
import com.Zrips.CMI.utils.CMIThread;
import com.Zrips.CMI.utils.ServerLinksManager;
import com.Zrips.CMI.utils.SpawnUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Config {
  public static String imageFolder =
      CMI.getInstance().getDataFolder().getPath() + File.separator + "Images";
  private boolean ShowNewVersion = true;
  public static boolean DyeBoundToPlayer = true;
  private List<String> signEditBlackList = new ArrayList<>();
  private HashMap<String, Integer> FlyAboveRoofLimitationsMap = new HashMap<>();
  public boolean VaultMoney = true;
  private boolean OverrideLoginMessage = true;
  private boolean BossBarHpBarEnabled = true;
  private List<EntityType> HpBarBlackList = new ArrayList<>();
  private String BooksDefaultAuthor;
  public static boolean BooksAddDate = false;
  public boolean VaultGroup = true;
  public boolean DynamicViewRangeEnabled = false;
  private int OptimizationsSimilarCommandChecker;
  private boolean OptimizationsSimilarCommandPrevention;
  public int lfixDefaultSpeed;
  public int SpawnMobMaxQuantity;
  public int SpawnMobMaxPassengers;
  public double lfixSoftCap;
  public boolean fixLightOngeneration;
  private List<String> fixWorldsToFix = new ArrayList<>();
  public boolean hungeroveride = false;
  public String Lang = "EN";
  public boolean LanguageDownload = true;
  public static boolean monochromeConsole = false;
  public static int ImmortalityOnJoin = 3;
  public boolean fileSaveAsync = false;
  private boolean PartialPlayerName = false;
  public static boolean PrioritizeOnlinePlayers = false;
  public boolean PreventDifferentCapitalizationNames = true;
  public boolean PlayTimeFromStats = false;
  public static boolean PlayTimeAutoUpdater = false;
  private List<Material> ItemLoreTypeBlackList = new ArrayList<>();
  private boolean ItemNameMarkChanged = false;
  private boolean ItemLoreMarkChanged = false;
  public static int ItemLoreMaxLength = 64;
  private boolean CMIPlayTimeTracking = false;
  public static List<String> checkAccountExlusions = new ArrayList<>();
  public static List<String> PlaytimeTopExclude = new ArrayList<>();
  public static long PlaytimeTopOffline = 0L;
  private boolean PerformCommandsOnNewName;
  private List<String> OptimizationsNameChangeCommands;
  private boolean OnLimitedItemUseInform = true;
  private boolean PreventEntityBoatEnterAnimals = true;
  private boolean PreventEntityBoatEnterMonsters = true;
  private boolean PreventEntityBoatEnterVillagers = true;
  private boolean PreventBedExplosionNether = true;
  private boolean PreventBedExplosionTheEnd = true;
  private boolean PreventPlayersOnNetherRoof = true;
  private int netherRoofHeight = 0;
  private boolean PreventPlayersBelowBedrock = true;
  private boolean PreventIronGolemRoses = false;
  public static boolean ShowMainHelpPage = true;
  private boolean PreventHook = false;
  public static int NearDefaultDistance = 200;
  public static boolean NearCommand = false;
  public static List<Integer> lastonlineTimers = new ArrayList<>();
  public static boolean NearHideInvisible = false;
  public static boolean NearHideObfuscate = false;
  public static boolean NearDirection = true;
  public static int NearCommandCount = 10;
  private boolean MulticraftDisableList = false;
  private boolean PreventExpPortals = false;
  public static boolean NoCommandsInBed = false;
  public static boolean LimitBooks = false;
  public static boolean BlockEndPortalItemTransfers = false;
  public static List<String> CommandsClearExclude = new ArrayList<>();
  public static boolean CommandsClearConfirmation = true;
  public static boolean CommandsListASCOrder = true;
  public static boolean CommandsGiveEnchantLimit = true;
  private boolean FlyAboveRoof = true;
  private boolean durabilityLossUse = true;
  private boolean armorDurabilityLossUse = true;
  private int durabilityLossPercentage = 10;
  private int armorDurabilityLossPercentage = 10;
  private String LongDateFormat;
  private String ShortDateFormat;
  private boolean SellLog;
  private boolean PermisionOnError;
  private boolean PermisionInConsole;
  private boolean OptimizationsCommandSorting;
  private boolean AllowRconCommands;
  private List<String> CustomCommandSenders;
  private boolean CleanRconCommands;
  private boolean OptimizationsCommandRemoveLabel;
  private HashMap<CMIItemStack, String> ItemRenamingPreventMap = new HashMap<>();
  public static boolean ItemRenamingGlobalDisable = false;
  public static boolean ItemRenamingCheckSource = false;
  public static boolean AddItalicByDefault = false;
  public static int ItemRenamingMaxLength = 64;
  private boolean PotionEffectsDeductWhileOffline = false;
  public int IPdelay;
  public static boolean IPRecord = true;
  public static boolean helpopfeedbackMessage = true;
  public boolean LoginDisabled = false;
  public boolean LogoutDisabled = false;
  public boolean LoginCustomUse = false;
  public boolean LogoutCustomUse = false;
  public boolean LogoutServerSwitch = false;
  public boolean LoginServerSwitch = false;
  public boolean FirstJoinMessageUse = false;
  public static boolean loginNameFilterUse = false;
  public static boolean logoutNameFilterUse = false;
  public static ChatFilterRule loginLogoutNameFilter = new ChatFilterRule();
  private int LogoutAutoHideFrom = -1;
  private int LoginAutoHideFrom = -1;
  private boolean NotesShowOnAlertEvent = true;
  public static long alertTimer = 1440L;
  public String TimeDay;
  public String TimeNight;
  public String TimeMorning;
  public String TimeDusk;
  public int AutoTimeInterval;
  private boolean AutoTimeSmooth;
  private int AutoTimeSmoothSpeed;
  private CText Motd = null;
  public boolean SearchPurge = false;
  public boolean SearchLogIntoFile = false;
  public static boolean MuteNoPm = true;
  public boolean CuffMute = true;
  public List<String> CuffAllowed = new ArrayList<>();
  private List<Material> GroundCleanWhiteList = new ArrayList<>();
  private List<EntityType> GroundCleanEntityWhiteList = new ArrayList<>();
  @Deprecated public boolean WorldLimits = false;
  public HashMap<String, GameMode> worldGameMode = new HashMap<>();
  public HashMap<String, Boolean> worldFlyMode = new HashMap<>();
  public HashMap<String, Boolean> worldGodMode = new HashMap<>();
  public HashMap<String, Boolean> worldElytraMode = new HashMap<>();
  public boolean hatIgnoreLored = false;
  public boolean hatBlockArmorItems = false;
  public boolean hatBlockNoneHatEnchanted = false;
  public boolean hatAllowMobHeads = true;
  public List<CMIMaterial> hatWhiteList = new ArrayList<>();
  public boolean RemoveNegative = false;
  public List<String> RemoveNegativeEffects = new ArrayList<>();
  private List<String> CommandSpyBlackListed = new ArrayList<>();
  private List<String> CommandSpyCommandList = new ArrayList<>();
  private int SpyDelayForTrigger = 1;
  private CMIEffect PointDefaultParticle = new CMIEffect(CMIParticle.DUST);
  private boolean ExploitPatcherRiptide = true;
  private boolean ExploitPatcherCheckItem = true;
  public int CounterRange = 10;
  private int MaxHp = 20;
  private boolean NetherPortalPreventCreation = false;
  public int NetherPortalMaxHeight = 23;
  public int NetherPortalMaxWidth = 23;
  public HashMap<DamageCause, ArrayList<DamageControl>> DamageControlMap = new HashMap<>();
  private CMIItemStack SelectionTool;
  private CMILocation firstSpawnPoint = null;
  private boolean InvDisableOffline = false;
  public static List<String> InvBlackList = new ArrayList<>();
  private long PlayerNotesExpiresIn = 30L;
  public int DisposeUILines = 4;
  public boolean DisposeCustomModelData = true;
  public boolean DisposeAttachedCommands = true;
  public List<CMIMaterial> DisposeMaterials = new ArrayList<>();
  private ConfigReader localeFile = null;
  private ConfigReader cfg = null;
  public static ChatFilterRule InteractiveCommandsSignRegex = new ChatFilterRule();
  public static boolean InteractiveCommandsSort = true;
  public static boolean UseFakeOperator = false;
  public static boolean InfiniteLoopDetection = true;
  public static boolean ShowSkullOwner = true;
  public static boolean ShowBeeHive = true;
  public static boolean ShowDecoratedPot = true;
  private List<String> ElevatorIndicator = new ArrayList<>();
  private String ElevatorStaticIndicator = "[*]";
  private boolean maintenance = false;
  public static boolean DisableTeamManagement = false;
  private boolean AutoDownloadGeoIp = true;
  private boolean AutoDownloadGeoLiteCity = true;
  private String maintenanceMessage = null;
  public static Boolean maintenanceBossbar = null;
  public static Boolean maintenanceAutoKick = null;
  private CMI plugin;
  List<String> CommentList =
      new ArrayList<>(
          Arrays.asList(
              "Full color code support and some variables",
              "Keep in mind that variables wont work for some lines, when it will for anothers :)",
              "Just keep them where there are now and everything will be ok :)",
              "Some lines can have global variables set. For player who will be effected. In"
                  + " example /heal Zrips then Zrips data will be used",
              "[serverName] to show server name",
              "[playerName] to show target player name",
              "[playerDisplayName] to show target player display name",
              "[lvl] to show target player level",
              "[exp] to show target player total exp",
              "[hp] to show target player health",
              "[maxHp] to show target player max health",
              "[hunger] to show target player hunger level",
              "[gameMode] to show target player gamemode",
              "[prefix] to show target player prefix if possible",
              "[suffix] to show target player suffix if possible",
              "Sender is console or player who performs command. In example Zrips performs /heal"
                  + " Zhax then Zrips data will be used",
              "[senderName] to show Sender player name",
              "[senderDisplayName] to show Sender player display name",
              "[senderLvl] to show Sender player level",
              "[senderExp] to show Sender player total exp",
              "[senderHp] to show Sender player health",
              "[senderMaxHp] to show Sender player max health",
              "[senderHunger] to show Sender player hunger level",
              "[senderGameMode] to show Sender player gamemode",
              "[senderPrefix] to show Sender player prefix if possible",
              "[senderSuffix] to show Sender player suffix if possible",
              "Source is player which is being used for extra info. In example Zrips performs /tp"
                  + " Zhax Zrips then Zhax data will be used as its location is being taken for new"
                  + " player location",
              "[sourceName] to show source player name",
              "[sourceDisplayName] to show source player display name",
              "[sourceLvl] to show source player level",
              "[sourceExp] to show source player total exp",
              "[sourceHp] to show source player health",
              "[sourceMaxHp] to show source player max health",
              "[sourceHunger] to show source player hunger level",
              "[sourceGameMode] to show source player gamemode",
              "[sourcePrefix] to show source player prefix if possible",
              "[sourceSuffix] to show source player suffix if possible",
              "***********************************************",
              "Some lines supports option to send them to custom places, like action bar, title,"
                  + " sub title or even create JSON/clickable messages",
              "If line starts with !toast! then player will get toast message (advancement popup,"
                  + " only 1.12 and up). Some extra variables can be used to define type and icon."
                  + " example: !toast! -t:goal -icon:paper Hello world!",
              "If line starts with !actionbar! then player will get action bar message defined"
                  + " after this variable",
              "If line starts with !actionbar:[seconds]! then player will get action bar message"
                  + " for defined amount of time",
              "If line starts with !broadcast! then everyone will receive message. You can add"
                  + " extra !toast! !actionbar! or !title! to send message for everyone to specific"
                  + " place, in example !broadcast!!title!",
              "If line starts with !customtext:[cTextName]! then custom text will be taken by name"
                  + " provided and shown for player. In case its used after !broadcast! then"
                  + " everyone who is online will get this custom text message",
              "If line starts with !title! then player will get title message defined after this"
                  + " variable, in addition it can contain !subtitle! which will add subtitle"
                  + " message. Optional time can be defined with !title:4.5! which will keep title"
                  + " message for 4.5 seconds",
              "If line starts with !bossbar:[name]-[timer]! then player will get bossbar message"
                  + " defined after this variable, in addition you can define how long this message"
                  + " will be visible. You need to define bossbar name which can be anything you"
                  + " want, but lines with same name will override each other to prevent stacking",
              "",
              "",
              "",
              "ATTENTION! As of 9.0.0.0 CMI version some of the locale lines got moved to CMILib"
                  + " file. This is to centralize some of most used ones across multiple plugins.",
              "After you update locale in CMILib file perform `/cmil reload` command for changes to"
                  + " take effect"));
  private static final Pattern PATTERN = Pattern.compile("\\|(\\d*)\\|([^\\s|]+)");

  public Config(CMI cmi) {
    this.plugin = cmi;
  }

  public void ChangeConfig(String string, Object object) {
    this.ChangeConfig(string, object, true);
  }

  public void ChangeConfig(String string, Object object, boolean flag) {
    File file = new File(this.plugin.getDataFolder(), "config.yml");

    try {
      new ConfigReader(file);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    yamlConfiguration.options().copyDefaults(true);
    yamlConfiguration.set(string, object);

    try {
      yamlConfiguration.save(file);
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }

    if (flag) {
      this.load();
    }
  }

  private static void newLn(StringBuilder stringBuilder) {
    stringBuilder.append(System.lineSeparator());
  }

  private static StringBuilder formStringBuilder(List<String> list) {
    StringBuilder stringBuilder = new StringBuilder();

    for (String string : list) {
      newLn(stringBuilder);
      stringBuilder.append(string);
    }

    return stringBuilder;
  }

  public boolean LoadLang(String string) {
    return this.LoadLang(string, false);
  }

  public boolean LoadLang(String string, boolean flag) {
    File file =
        new File(
            this.plugin.getDataFolder(),
            "Translations" + File.separator + "Locale_" + string + ".yml");
    if (!flag) {
      this.loadConfig(file, string, flag);
    } else {
      CompletableFuture.runAsync(
          () -> this.loadConfig(file, string, flag), CMIThread.getExecutor());
    }

    return true;
  }

  public CompletableFuture<Boolean> loadLangAsync(String string) {
    File file =
        new File(
            this.plugin.getDataFolder(),
            "Translations" + File.separator + "Locale_" + string + ".yml");
    return CompletableFuture.supplyAsync(
        () -> this.loadConfig(file, string, true), CMIThread.getExecutor());
  }

  private boolean loadConfig(File file, String string, boolean flag) {
    Long startTime = System.currentTimeMillis();

    try {
      this.localeFile = new ConfigReader(file);
    } catch (Exception exception) {
      if (flag) {
        this.plugin.consoleMessage(
            "&cSEVERE LOCALE FILE READ ERROR. Check your spelling! Changes will not be applied"
                + " until you fix it or perform server restart which can result in locale file"
                + " reset to default");
        return false;
      }
    }

    if (this.localeFile == null) {
      return false;
    }

    if (string.equalsIgnoreCase(this.Lang)) {
      this.localeFile.setRecordContents(true);
    }

    this.localeFile.options().copyDefaults(true);
    this.localeFile.options().header(formStringBuilder(this.CommentList).toString());
    CMILC[] cmilcs;
    int value = (cmilcs = CMILC.values()).length;

    for (int index = 0; index < value; index++) {
      CMILC cmilc = cmilcs[index];
      if (cmilc.getComments() != null && !cmilc.getComments().isEmpty()) {
        String[] strings = new String[cmilc.getComments().size()];

        for (int commentIndex = 0; commentIndex < cmilc.getComments().size(); commentIndex++) {
          strings[commentIndex] = cmilc.getComments().get(commentIndex);
        }

        this.localeFile.addComment(cmilc.name().replace("_", "."), strings);
      }

      this.localeFile.get(cmilc.name().replace("_", "."), cmilc.getText());
    }

    this.localeFile.get("command.help.output.usage", "{gcp}Usage: {gcs}%usage%");
    this.localeFile.get(
        "command.help.output.cmdInfoFormat", "{gcp}[command] {gcp}- {gcs}[description]");
    this.localeFile.get("command.help.output.cmdFormat", "{gcp}/[command]{gcn}[arguments]");
    this.localeFile.get("command.help.output.helpPageDescription", "{gcs}* [description]");
    this.localeFile.get("command.help.output.explanation", "{gcs} * [explanation]");
    this.localeFile.get(
        "command.help.output.title", "{gcs}------ ======= {gcp}Help {gcs}======= ------");
    this.localeFile.get("command.nocmd.help.info", "{gcp}Shows all available commands");
    this.localeFile.get("command.nocmd.help.args", "");

    for (var entry : this.plugin.getCommandManager().getCommands().entrySet()) {
      CMICommand cmiCommand = (CMICommand) entry.getValue();
      if ((!cmiCommand.getAnottation().hidden() || cmiCommand.getAnottation().indevelopment())
          && !cmiCommand.getAnottation().test()) {
        CAnnotation cAnnotation = cmiCommand.getAnottation();
        String info = cAnnotation.info();
        if (info != null) {
          this.localeFile.get("command." + cmiCommand.getName() + ".help.info", info);
        }

        String args = cAnnotation.args();
        if (args != null) {
          this.localeFile.get("command." + cmiCommand.getName() + ".help.args", args);
        }

        String[] tabArgs = cAnnotation.tab();
        boolean hasTab = false;
        if (tabArgs.length <= 0) {
          hasTab = true;
        } else {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append(cmiCommand.getName());

          for (Object object : Arrays.asList(tabArgs)) {
            if (!stringBuilder.toString().isEmpty()) {
              stringBuilder.append(" ");
            }

            StringBuilder tabBuilder = new StringBuilder();
            String[] typeParts;
            int typeCount = (typeParts = ((String) object).split("%%")).length;

            for (int typeIndex = 0; typeIndex < typeCount; typeIndex++) {
              String typePart = typeParts[typeIndex];
              if (!tabBuilder.toString().isEmpty()) {
                tabBuilder.append(",");
              }

              TabComplete.TabAction tabAction = null;
              TabComplete.TabAction[] tabActions;
              int tabActionCount = (tabActions = TabComplete.TabAction.values()).length;

              for (int tabActionIndex = 0; tabActionIndex < tabActionCount; tabActionIndex++) {
                TabComplete.TabAction candidateAction = tabActions[tabActionIndex];
                if (!candidateAction.equals(TabComplete.TabAction.na)
                    && candidateAction.name().equalsIgnoreCase(typePart)) {
                  tabAction = candidateAction;
                  break;
                }
              }

              if (tabAction != null) {
                tabBuilder.append("[" + typePart + "]");
              } else {
                tabBuilder.append(typePart);
              }
            }

            stringBuilder.append(tabBuilder.toString());
          }

          this.plugin.getTab().addTabComplete(stringBuilder.toString());
        }

        String[] multiTab = cAnnotation.multiTab();
        if (multiTab.length > 0) {
          for (String multiTabEntry : Arrays.asList(multiTab)) {
            StringBuilder multiBuilder = new StringBuilder();
            multiBuilder.append(cmiCommand.getName());
            multiBuilder.append(" ");
            multiBuilder.append(expand(multiTabEntry));
            this.plugin.getTab().addTabComplete(multiBuilder.toString());
          }
        } else if (hasTab) {
          this.plugin.getTab().addTabComplete(cmiCommand.getName());
        }

        var list = Arrays.asList(cAnnotation.explanation());
        if (list != null && !list.isEmpty()) {
          this.localeFile.get("command." + cmiCommand.getName() + ".help.explanation", list);
        }

        Cmd cmd = cmiCommand.getCmdClass();
        this.localeFile.setP(cmiCommand.getName());
        cmd.getExtra(this.localeFile);
        this.localeFile.resetP();
      }
    }

    this.localeFile.save();
    this.plugin.loadMessage(string, "language file", System.currentTimeMillis() - startTime);
    this.localeFile = null;
    return true;
  }

  public static String expand(String string) {
    Matcher matcher = PATTERN.matcher(string);
    StringBuilder stringBuilder = new StringBuilder();

    int value;
    for (value = 0; matcher.find(); value = matcher.end()) {
      stringBuilder.append(string, value, matcher.start());
      String first = matcher.group(1);
      String second = matcher.group(2).trim();
      if (first.isEmpty()) {
        for (int index = 0; index < 3; index++) {
          if (stringBuilder.length() > 0
              && stringBuilder.charAt(stringBuilder.length() - 1) != ' ') {
            stringBuilder.append(' ');
          }

          stringBuilder.append(second);
        }
      } else {
        int count = Integer.parseInt(first);

        for (int repeatIndex = 0; repeatIndex < count; repeatIndex++) {
          if (stringBuilder.length() > 0
              && stringBuilder.charAt(stringBuilder.length() - 1) != ' ') {
            stringBuilder.append(' ');
          }

          stringBuilder.append(second);
        }
      }
    }

    stringBuilder.append(string.substring(value));
    return stringBuilder.toString().replaceAll("\\s+", " ").trim();
  }

  public boolean load() {
    return this.load(false);
  }

  private static void exportChatSection(ConfigReader configReader) {
    if (configReader.getC().isConfigurationSection("Chat")) {
      try {
        File file =
            new File(
                CMI.getInstance().getDataFolder(),
                "Settings" + File.separator + ChatManager.fileName);
        if (!file.isFile()) {
          ConfigReader fileConfig = new ConfigReader(file);
          fileConfig.process("Chat", configReader.getC().get("Chat"));
          fileConfig.save();
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }

  public boolean reload() {
    return this.load(true);
  }

  public boolean load(boolean flag) {
    try {
      this.cfg = new ConfigReader(CMI.getInstance(), "config.yml");
    } catch (Exception exception) {
      if (flag) {
        this.plugin.consoleMessage(
            "&cSEVERE CONFIG FILE READ ERROR. Check your spelling! Changes will not be applied"
                + " until you fix it or perform server restart which can result in config file"
                + " reset");
        return false;
      }

      exception.printStackTrace();
    }

    exportChatSection(this.cfg);
    this.cfg.header(
        Arrays.asList(
            "",
            "********************************************",
            "** For more options check Settings folder **",
            "********************************************"));
    this.cfg.addComment("Language", new String[] {"Language file you want to use"});
    this.Lang = this.cfg.get("Language", "EN").toUpperCase();
    if (this.Lang.equalsIgnoreCase("False")) {
      this.cfg.set("Language", "NO");
      this.Lang = "NO";
    }

    this.cfg.addComment(
        "LanguageDownload",
        new String[] {
          "Defines if you want to auto download default locale files from github repository",
          "You can disable this if you are using EN or you already have your locale set up and you"
              + " don't need other languages to be downloaded."
        });
    this.LanguageDownload = this.cfg.get("LanguageDownload", true);
    Long startTime = System.currentTimeMillis();
    this.plugin.getDbManager().load();
    this.plugin.loadMessage(
        "-",
        this.plugin.getDbManager().getDbType().toString(),
        System.currentTimeMillis() - startTime);
    this.plugin.getEconomyManager().loadConfig();
    this.cfg.addComment(
        "FileSave.Async",
        new String[] {
          "Change this to true only if you have issues with drives I/O and you need to save players"
              + " files in async mode to lower waiting time for mc server"
        });
    this.fileSaveAsync = this.cfg.get("FileSave.Async", false);
    this.cfg.addComment(
        "InteractiveCommands.SignRegex",
        new String[] {
          "Regex used in recognizing public interactive commands used in signs",
          "Default format is [ic:[icName]] in example [ic:trash]",
          "If you want clean format use regex like: (\\[([a-zA-Z0-9]+)\\])"
        });
    InteractiveCommandsSignRegex.setPattern(
        Arrays.asList(this.cfg.get("InteractiveCommands.SignRegex", "(\\[ic:([a-zA-Z0-9]+)\\])")));
    this.cfg.addComment(
        "InteractiveCommands.SortByDistance",
        new String[] {"Sorting of IC list either by distance from you or alphabetically"});
    InteractiveCommandsSort = this.cfg.get("InteractiveCommands.SortByDistance", true);
    this.cfg.addComment(
        "Optimizations.UseFakeOperator",
        new String[] {
          "When disabled we wont try to deop fake operator on server starup or unload while at same"
              + " time disabling asFakeOp! specialized command usage"
        });
    UseFakeOperator = this.cfg.get("Optimizations.UseFakeOperator", false);
    this.cfg.addComment(
        "Optimizations.InfiniteLoopDetection",
        new String[] {
          "When disabled we will not check for possible infinite loops while performing custom"
              + " alias commands"
        });
    InfiniteLoopDetection = this.cfg.get("Optimizations.InfiniteLoopDetection", true);
    this.cfg.addComment(
        "Optimizations.MonochromeConsole",
        new String[] {
          "When this set to true any message sent to console will be strip out of any colors",
          "In case you have mohochrome console, keep this at true"
        });
    monochromeConsole = this.cfg.get("Optimizations.MonochromeConsole", false);
    this.cfg.addComment(
        "Optimizations.ImmortalityOnJoin",
        new String[] {
          "Time in seconds we want to give for playes who joins server a immortality state",
          "Set this to 0 to disable",
          "Max 60 seconds"
        });
    ImmortalityOnJoin = this.cfg.get("Optimizations.ImmortalityOnJoin", 3);
    if (ImmortalityOnJoin > 60) {
      ImmortalityOnJoin = 60;
    }

    this.cfg.addComment(
        "Optimizations.Maintenance", new String[] {"Enables or disables maintenance mode"});
    this.maintenance = this.cfg.get("Optimizations.Maintenance", false);
    this.cfg.addComment(
        "Optimizations.MaintenanceAutoKick",
        new String[] {
          "Automatically kicks everyone from server when enabling maintenance mode who doesnt have "
              + PermissionsManager.CMIPerm.command_maintenance_bypass.getPermission()
              + " permission node"
        });
    maintenanceAutoKick = this.cfg.get("Optimizations.MaintenanceAutoKick", true);
    this.cfg.addComment(
        "Optimizations.MaintenanceBossBar",
        new String[] {
          "When enabled will show bossbar message informing about current maintenance mode being"
              + " enabled"
        });
    maintenanceBossbar = this.cfg.get("Optimizations.MaintenanceBossBar", true);
    this.cfg.addComment(
        "Optimizations.DisableTeamManagement",
        new String[] {
          "When this is set to true features like glow color, player collision, name plates will"
              + " stop working and CMI will avoid doing anything related to player teams",
          "Enable this if you are experiencing issue with other plugins which utilizes players"
              + " teams for certain features"
        });
    DisableTeamManagement = this.cfg.get("Optimizations.DisableTeamManagement", false);
    this.plugin.getArmorStandManager().loadConfig(this.cfg);
    this.cfg.addComment(
        "Optimizations.AutoDownload.GeoIp",
        new String[] {
          "In case you don't have GeoIP.dat it will be downloaded automatically on server start."
              + " Restart can be needed for it to take effect"
        });
    this.AutoDownloadGeoIp = this.cfg.get("Optimizations.AutoDownload.GeoIp", false);
    this.cfg.addComment(
        "Optimizations.AutoDownload.GeoLiteCity",
        new String[] {
          "In case you don't have GeoLiteCity.dat it will be downloaded automatically on server"
              + " start. Restart can be needed for it to take effect"
        });
    this.AutoDownloadGeoLiteCity = this.cfg.get("Optimizations.AutoDownload.GeoLiteCity", false);
    this.cfg.addComment(
        "Optimizations.ShowSkullOwner",
        new String[] {
          "When enabled you will see skull owner name in action bar when right clicking it"
        });
    ShowSkullOwner = this.cfg.get("Optimizations.ShowSkullOwner", true);
    this.cfg.addComment(
        "Optimizations.ShowBeeHiveInformation",
        new String[] {
          "When enabled you will see beeHive information in action bar when right clicking it"
        });
    ShowBeeHive = this.cfg.get("Optimizations.ShowBeeHiveInformation", true);
    this.cfg.addComment(
        "Optimizations.ShowDecoratedPotInformation",
        new String[] {
          "When enabled you will see decorated pot contents information in action bar when right"
              + " clicking it"
        });
    ShowDecoratedPot = this.cfg.get("Optimizations.ShowDecoratedPotInformation", true);
    this.ElevatorIndicator.clear();
    this.cfg.addComment(
        "Optimizations.ElevatorIndicator",
        new String[] {"Sets indicator when creating elevator signs. Its case insensitive"});
    if (this.cfg.getC().isString("Optimizations.ElevatorIndicator")) {
      this.ElevatorIndicator.add(
          this.cfg.getC().getString("Optimizations.ElevatorIndicator", "[CMIElevator]"));
      this.cfg.getC().set("Optimizations.ElevatorIndicator", null);
    }

    this.ElevatorIndicator =
        this.cfg.get(
            "Optimizations.ElevatorIndicator",
            this.ElevatorIndicator.isEmpty()
                ? Arrays.asList("[CMIElevator]", "[Elevator]")
                : this.ElevatorIndicator);
    CMIList.toLowerCase(this.ElevatorIndicator);
    this.cfg.addComment(
        "Optimizations.SignEdit.BlackList",
        new String[] {
          "Defines sign top line by which player will not be able to edit sign with /cmi se command"
        });
    this.signEditBlackList =
        this.cfg.get(
            "Optimizations.SignEdit.BlackList",
            Arrays.asList(
                "[Private]",
                "[More Users]",
                "[Everyone]",
                "[AdminShop]",
                "[ChestShop]",
                "[CMIElevator]"));
    CMIList.toLowerCase(this.signEditBlackList);
    CMIList.removeColors(this.signEditBlackList);
    this.cfg.addComment(
        "Optimizations.ElevatorStaticIndicator",
        new String[] {
          "Defines elevator type. If this is used on second line of sing, then player will be"
              + " teleported directly in front of it instead of keeping original players x and z"
              + " coordinates"
        });
    this.ElevatorStaticIndicator = this.cfg.get("Optimizations.ElevatorStaticIndicator", "[*]");
    this.cfg.addComment(
        "Optimizations.LongDateFormat2",
        new String[] {
          "Used to show date in places like mail, checkban, infopage and similar locations"
        });
    this.LongDateFormat = this.cfg.get("Optimizations.LongDateFormat2", "dd/MM/yy HH:mm:ss");
    this.cfg.addComment(
        "Optimizations.ShortDateFormat",
        new String[] {"Used to show date in places like inv gui and similar locations"});
    this.ShortDateFormat = this.cfg.get("Optimizations.ShortDateFormat", "dd/MM/yy");
    this.cfg.addComment(
        "Optimizations.SellLog",
        new String[] {"Do you want to record sell hand actions into file"});
    this.SellLog = this.cfg.get("Optimizations.SellLog", true);
    if (!this.plugin.getServer().getOnlineMode()) {
      this.cfg.addComment(
          "Optimizations.PreventDifferentCapitalizationNames",
          new String[] {
            "When set to true players with same name but different capitalization will be denyied"
                + " from joining server to avoid possible issues. In example if on first day player"
                + " Zrips joined server and on second day player zRips tries to join server, he"
                + " will be rejected",
            "RECOMMENDED to keep this at true"
          });
      this.PreventDifferentCapitalizationNames =
          this.cfg.get("Optimizations.PreventDifferentCapitalizationNames", false);
    } else {
      this.PreventDifferentCapitalizationNames = false;
    }

    this.cfg.addComment(
        "Optimizations.PartialPlayerName",
        new String[] {
          "When this set to true by using any command, requiring players name, in case plugin cant"
              + " determine player by given full name, then partial matches from online players"
              + " will be used. In example: /cmi heal rips can heal player Zrips ",
          "Useful when you have players with complicated names"
        });
    this.PartialPlayerName = this.cfg.get("Optimizations.PartialPlayerName", false);
    this.cfg.addComment(
        "Optimizations.PrioritizeOnlinePlayers",
        new String[] {
          "If you want to prioritize online players when using partial player name enable this"
              + " setting",
          "Don't forget to enable PartialPlayerName for this to have full effect",
          "Keep in mind that this might block you from accessing offline player with same partial"
              + " name as some one currently being online",
          "For example offline Luuk might no longer be accessible while Luuk666 is online",
          "This can be overridden by adding -exa- suffix for a name, so luuk-exa- will return"
              + " offline player object"
        });
    PrioritizeOnlinePlayers = this.cfg.get("Optimizations.PrioritizeOnlinePlayers", false);
    this.cfg.addComment(
        "Optimizations.CommandSorting",
        new String[] {
          "When set to true, commands in help page will be sorted alphabeticaly",
          "If set to false, commands will be sorted by priority"
        });
    this.OptimizationsCommandSorting = this.cfg.get("Optimizations.CommandSorting", true);
    this.cfg.addComment(
        "Optimizations.AllowRconCommands",
        new String[] {
          "By default we will not allow Rcon type commands, if you have your server properly setup"
              + " to accept remote console commands safely and you have a need then you can enable"
              + " it"
        });
    this.AllowRconCommands = this.cfg.get("Optimizations.AllowRconCommands", false);
    this.cfg.addComment(
        "Optimizations.CustomCommandSenders",
        new String[] {"List of command senders to be recognized as valid"});
    this.CustomCommandSenders = this.cfg.get("Optimizations.CustomCommandSender", new ArrayList());
    CMIList.toLowerCase(this.CustomCommandSenders);
    this.cfg.addComment(
        "Optimizations.CleanRconCommands",
        new String[] {
          "When enabled Rcon feedback message will not include color codes for cleaner display"
        });
    this.CleanRconCommands = this.cfg.get("Optimizations.CleanRconCommands", false);
    this.cfg.addComment(
        "Optimizations.SimilarCommandChecker",
        new String[] {
          "Percentage value (1-100) to pick best command match if command cant be found",
          "Example: /cmi spawnmb will have 87.5% match with /cmi spawnmob",
          "Set to 0 to disable"
        });
    this.OptimizationsSimilarCommandChecker =
        this.cfg.get("Optimizations.SimilarCommandChecker", 75);
    this.cfg.addComment(
        "Optimizations.SimilarCommandPrevention",
        new String[] {
          "When set to true, if player enters incorrect command, then command will not gonna be"
              + " performed",
          "But message informing about incorrect usage and best match will be shown in any case"
        });
    this.OptimizationsSimilarCommandPrevention =
        this.cfg.get("Optimizations.SimilarCommandPrevention", false);
    this.cfg.addComment(
        "Optimizations.RemoveLabel",
        new String[] {
          "When set to true, commands in help page starting with /cmi will get shortened by hiding"
              + " base command. Example /cmi back becomes /back",
          "Keep in mind that this is automatic feature if alias or custom alias is set to that"
              + " command",
          "And keep in mind that this is only cosmetic change and will not impact command usage"
        });
    this.OptimizationsCommandRemoveLabel = this.cfg.get("Optimizations.RemoveLabel", false);
    this.cfg.addComment(
        "Optimizations.PermisionOnError",
        new String[] {
          "When set to true, all players can see missing permission node by hovering over error"
              + " message",
          "When set to false only players with cmi.permisiononerror permission node can see missing"
              + " permission node",
          "Keep in mind that by default players have acces to permission node, so negate it if you"
              + " want to hide missing permission nodes from them"
        });
    this.PermisionOnError = this.cfg.get("Optimizations.PermisionOnError", false);
    this.cfg.addComment(
        "Optimizations.PermisionInConsole",
        new String[] {
          "When set to true, each time player tries to use something he doesnthave permission,"
              + " message will be shown in console"
        });
    this.PermisionInConsole = this.cfg.get("Optimizations.PermisionInConsole", true);
    this.plugin.getTeleportManager().loadConfig();
    this.cfg.addComment(
        "Optimizations.Hat.Block.WithLore",
        new String[] {
          "When enabled we will not allow to place items into head slot if item has custom lore",
          "Doesn't apply to helmet items"
        });
    this.hatIgnoreLored =
        this.cfg.get(
            "Optimizations.Hat.Block.WithLore",
            this.cfg.getC().getBoolean("Optimizations.Hat.IgnoreLored", false));
    this.cfg.addComment(
        "Optimizations.Hat.Block.NoneHatItems",
        new String[] {"When enable we wont allow to equip items which are not hat type"});
    this.hatBlockArmorItems =
        this.cfg.get(
            "Optimizations.Hat.Block.NoneHatItems",
            this.cfg.getC().getBoolean("Optimizations.Hat.BlockArmorItems", true));
    this.cfg.addComment(
        "Optimizations.Hat.Block.NoneHatEnchanted",
        new String[] {"When enable we wont allow to equip none hat items with enchant on it"});
    this.hatBlockNoneHatEnchanted = this.cfg.get("Optimizations.Hat.Block.NoneHatEnchanted", true);
    this.cfg.addComment(
        "Optimizations.Hat.Allow.MobHeads",
        new String[] {
          "When enabled we will allow player to put on mob head without cmi.inventoryhat permission"
              + " node",
          "When disabled player will need to have cmi.inventoryhat to put on mob heads as hats"
        });
    this.hatAllowMobHeads =
        this.cfg.get(
            "Optimizations.Hat.Allow.MobHeads",
            this.cfg.getC().getBoolean("Optimizations.Hat.AllowMobHeads", true));
    this.cfg.addComment(
        "Optimizations.Hat.WhiteList",
        new String[] {
          "List of materials to be allowed on players head",
          "Keep it empty to allow all materials on player head"
        });
    List<String> list = this.cfg.get("Optimizations.Hat.WhiteList", new ArrayList<String>());
    this.hatWhiteList.clear();
    list.forEach(
        object -> {
          CMIMaterial cmiMaterial = CMIMaterial.get(object);
          if (!cmiMaterial.isValidItem()) {
            CMIMessages.consoleMessage(
                "Could not determine hat white list item material (" + object + ")");
          } else {
            this.hatWhiteList.add(cmiMaterial);
          }
        });
    this.cfg.addComment(
        "Optimizations.helpop.feedbackMessage",
        new String[] {
          "When set to true player will not see his sent message to helpop, but rather predefined"
              + " message informing that his question have been sent to online staff"
        });
    helpopfeedbackMessage = this.cfg.get("Optimizations.helpop.feedbackMessage", true);
    this.cfg.addComment(
        "Optimizations.IP.Record",
        new String[] {
          "When set to true, players ip will be recorded for future use, like recognizing players"
              + " other accounts",
          "Some commands will have limited usage when this is disabled",
          "Keep in mind that this could increase cached data size due to how much data this stores"
              + " for each player, especially if users have dynamic ip's"
        });
    IPRecord = this.cfg.get("Optimizations.IP.Record", false);
    this.cfg.addComment(
        "Optimizations.IP.delay",
        new String[] {
          "How long in second to wait until players ip is being recorded into data base",
          "This only applies for offline servers to allow for player first of all to login before"
              + " recording ip",
          "Try to keep this value higher than your login plugin's allowed login time so that we are"
              + " recording actual players IP who has permission to use this name"
        });
    this.IPdelay = this.cfg.get("Optimizations.IP.delay", 30);
    this.cfg.addComment(
        "Optimizations.MaxHp",
        new String[] {"Max amount of hp you can get when using /cmi maxhp command"});
    this.MaxHp = CMINumber.clamp(this.cfg.get("Optimizations.MaxHp", 1000), 0, Integer.MAX_VALUE);
    this.cfg.addComment(
        "Optimizations.PlayTimeFromStats",
        new String[] {
          "When set to true, player play time will be grabbed from user stats file instead of from"
              + " CMI user data file",
          "This can help to get more accurate play time if you have older server and using players"
              + " stats feature",
          "Recommended to keep this at true"
        });
    this.PlayTimeFromStats = this.cfg.get("Optimizations.PlayTimeFromStats", true);
    this.cfg.addComment(
        "Optimizations.PlayTimeAutoUpdater",
        new String[] {
          "Do you want to run auto top playtime updater",
          "This is responsible for providing players current top playtimes which gets updated on"
              + " consistent intervals, useful for placeholders"
        });
    PlayTimeAutoUpdater = this.cfg.get("Optimizations.PlayTimeAutoUpdater", true);
    this.cfg.addComment(
        "Optimizations.CMIPlayTimeTracking",
        new String[] {
          "Do you want to use CMI playtime tracking",
          "While this is enabled, player play time will be record for each hour he playied in"
              + " server",
          "This doesnt have effect on rankup or /playtime commands, it will only show more specific"
              + " playtime by hours when using /cplaytime command"
        });
    this.CMIPlayTimeTracking = this.cfg.get("Optimizations.CMIPlayTimeTracking", false);
    this.cfg.addComment(
        "Optimizations.PlaytimeTopExclude",
        new String[] {"Player names to be excluded from playtimetop list"});
    PlaytimeTopExclude =
        this.cfg.get("Optimizations.PlaytimeTopExclude", Arrays.asList("Some", "Names", "Here"));

    for (int value = 0; value < PlaytimeTopExclude.size(); value++) {
      PlaytimeTopExclude.set(value, PlaytimeTopExclude.get(value).toLowerCase());
    }

    this.cfg.addComment(
        "Optimizations.PlaytimeTopOffline",
        new String[] {
          "Time in days for player to be offline when he will be excluded from playtimetop list",
          "Set to 0 if you want to disable this"
        });
    PlaytimeTopOffline = this.cfg.get("Optimizations.PlaytimeTopOffline", 0);
    this.cfg.addComment(
        "Optimizations.ItemName.MarkChanged",
        new String[] {
          "For black listed materials check ItemRenaming.list section",
          "",
          "When set to true items which name got changed with /itemname command will be marked with"
              + " NBT data as 'ModifiedName' boolean type"
        });
    this.ItemNameMarkChanged = this.cfg.get("Optimizations.ItemName.MarkChanged", false);
    this.cfg.addComment(
        "Optimizations.ItemLore.MaxLength",
        new String[] {
          "Defines maximum length of item lore line excluding color codes for itemlore command",
          "Set it to 0 if you want to remove restrictions"
        });
    ItemLoreMaxLength = this.cfg.get("Optimizations.ItemLore.MaxLength", 64);
    this.cfg.addComment(
        "Optimizations.ItemLore.TypeBlackList",
        new String[] {"List of materials to block from itemName command"});

    for (String string :
        this.cfg.get("Optimizations.ItemLore.TypeBlackList", Arrays.asList("gold_nugget"))) {
      CMIMaterial material = CMIMaterial.get(string);
      if (material != null && material.getMaterial() != null) {
        this.ItemLoreTypeBlackList.add(material.getMaterial());
      }
    }

    this.cfg.addComment(
        "Optimizations.ItemLore.MarkChanged",
        new String[] {
          "When set to true items which lore got changed with itemname command will be marked with"
              + " NBT data as 'ModifiedLore' boolean type"
        });
    this.ItemLoreMarkChanged = this.cfg.get("Optimizations.ItemLore.MarkChanged", false);
    this.plugin.getPlayTimeRewardManager().loadConfig();
    this.cfg.addComment(
        "Optimizations.OnDurabilityLoss.Tools",
        new String[] {
          "Do you want to inform player when item durability gets lower than set threshold",
          "Player should have " + PermissionsManager.CMIPerm.informDurability.getPermission()
        });
    this.durabilityLossUse =
        this.cfg.get(
            "Optimizations.OnDurabilityLoss.Tools.Use",
            this.cfg.getC().getBoolean("Optimizations.OnDurabilityLoss.Use", true));
    this.durabilityLossPercentage =
        this.cfg.get(
            "Optimizations.OnDurabilityLoss.Tools.Percentage",
            this.cfg.getC().getInt("Optimizations.OnDurabilityLoss.Percentage", 10));
    this.armorDurabilityLossUse = this.cfg.get("Optimizations.OnDurabilityLoss.Armor.Use", true);
    this.armorDurabilityLossPercentage =
        this.cfg.get("Optimizations.OnDurabilityLoss.Armor.Percentage", 10);
    this.cfg.addComment(
        "Optimizations.OnLimitedItemUse.Inform", new String[] {"Informs about left uses of item"});
    this.OnLimitedItemUseInform = this.cfg.get("Optimizations.OnLimitedItemUse.Inform", true);
    this.cfg.addComment(
        "Optimizations.PreventEntityBoatEnter",
        new String[] {"Can prevent animals or monsters entering boats"});
    this.PreventEntityBoatEnterMonsters =
        this.cfg.get("Optimizations.PreventEntityBoatEnter.Monsters", false);
    this.PreventEntityBoatEnterAnimals =
        this.cfg.get("Optimizations.PreventEntityBoatEnter.Animals", false);
    this.PreventEntityBoatEnterVillagers =
        this.cfg.get("Optimizations.PreventEntityBoatEnter.Villagers", false);
    this.cfg.addComment(
        "Optimizations.PreventEntityBoatEnter",
        new String[] {"Can prevent animals or monsters entering boats"});
    this.PreventBedExplosionNether =
        this.cfg.get("Optimizations.PreventBedExplosion.Nether", false);
    this.PreventBedExplosionTheEnd =
        this.cfg.get("Optimizations.PreventBedExplosion.TheEnd", false);
    this.cfg.addComment(
        "Optimizations.PreventPlayersOnNetherRoof",
        new String[] {"Will teleport players down from nether 'roof'"});
    this.PreventPlayersOnNetherRoof =
        this.cfg.get("Optimizations.PreventPlayersOnNetherRoof", false);
    this.cfg.addComment(
        "Optimizations.netherRoofHeight",
        new String[] {
          "Will define nether roof height. Keeping it at 0 will use default nether height, which is"
              + " 128"
        });
    this.netherRoofHeight = this.cfg.get("Optimizations.netherRoofHeight", 0);
    this.cfg.addComment(
        "Optimizations.PreventPlayersBelowBedrock",
        new String[] {
          "This will teleport players up from beneath bedrock, if they ave flying or gliding with"
              + " the elytra on."
        });
    this.PreventPlayersBelowBedrock =
        this.cfg.get("Optimizations.PreventPlayersBelowBedrock", false);
    this.cfg.addComment(
        "Optimizations.PreventIronGolem.Roses",
        new String[] {"When set to true, iron golems will not drop roses on death"});
    this.PreventIronGolemRoses = this.cfg.get("Optimizations.PreventIronGolem.Roses", false);
    this.cfg.addComment(
        "Optimizations.PreventHook",
        new String[] {"When set to true, fishing rod will not move grabbed entity towards you"});
    this.PreventHook = this.cfg.get("Optimizations.PreventHook", false);
    this.cfg.addComment(
        "Optimizations.Commands.ShowMainHelpPage",
        new String[] {
          "When set to true player will see full list of possible commands when performing /cmi"
              + " command without defining specific command",
          "When set to false we will return 'No command found' feedback message"
        });
    ShowMainHelpPage = this.cfg.get("Optimizations.Commands.ShowMainHelpPage", true);
    this.cfg.addComment(
        "Optimizations.Commands.Near.Approximate",
        new String[] {
          "Use approximate distances instead of exact",
          "This will use separate locale line which includes direction to the player",
          "So instead of having something like 123m you will have ~100m"
        });
    NearCommand =
        this.cfg.get(
            "Optimizations.Commands.Near.Approximate",
            this.cfg.getC().getBoolean("Optimizations.NearCommand", true));
    this.cfg.addComment(
        "Optimizations.Commands.Near.Approximate",
        new String[] {
          "Use approximate distances instead of exact",
          "This will use separate locale line which includes direction to the player",
          "So instead of having something like 123m you will have ~100m"
        });
    NearCommand =
        this.cfg.get(
            "Optimizations.Commands.Near.Approximate",
            this.cfg.getC().getBoolean("Optimizations.NearCommand", true));
    this.cfg.addComment(
        "Optimizations.Commands.lastonline.Timers",
        new String[] {
          "Time in seconds to be used for lastonline command when showing aditional information"
              + " about amount of players who logged off in defined period"
        });
    lastonlineTimers.add(this.cfg.get("Optimizations.Commands.lastonline.Timers.Time1", 3600));
    lastonlineTimers.add(this.cfg.get("Optimizations.Commands.lastonline.Timers.Time2", 86400));
    lastonlineTimers.add(this.cfg.get("Optimizations.Commands.lastonline.Timers.Time3", 604800));
    lastonlineTimers.add(this.cfg.get("Optimizations.Commands.lastonline.Timers.Time4", 2592000));
    NearDefaultDistance = this.cfg.get("Optimizations.Commands.Near.DefaultDistance", 200);
    this.cfg.addComment(
        "Optimizations.Commands.CheckAccount.ExcludedUsers",
        new String[] {"List of users/ip's to be excluded from checkaccount command"});
    checkAccountExlusions =
        this.cfg.get("Optimizations.Commands.CheckAccount.ExcludedUsers", Arrays.asList("Zrips"));
    CMIList.toLowerCase(checkAccountExlusions);
    this.cfg.addComment(
        "Optimizations.Commands.Near.Invisibility.Hide",
        new String[] {
          "When enabled we will not show players who has invisibility potion effect on them in the"
              + " list"
        });
    NearHideInvisible = this.cfg.get("Optimizations.Commands.Near.Invisibility.Hide", false);
    this.cfg.addComment(
        "Optimizations.Commands.Near.Invisibility.Obfuscate",
        new String[] {
          "Only applies when previous Hide option is enabled",
          "Will obfuscate players name but stil shows his location in the list",
          "Actual players name wont be used, instead generic 8 character name with &k color format"
              + " gets used to prevent from people realizing who is near by the lenght of his name"
        });
    NearHideObfuscate = this.cfg.get("Optimizations.Commands.Near.Invisibility.Obfuscate", false);
    this.cfg.addComment(
        "Optimizations.Commands.Near.PointTo",
        new String[] {
          "When enabled, players can click on each player name and simple /cmi point [location]"
              + " will be performed which shows particles directing towards target player"
        });
    NearDirection = this.cfg.get("Optimizations.Commands.Near.PointTo", true);
    this.cfg.addComment(
        "Optimizations.Commands.Near.Count",
        new String[] {"How many lines you want to show when using /near command"});
    NearCommandCount = this.cfg.get("Optimizations.Commands.Near.Count", 10);
    this.cfg.addComment(
        "Optimizations.Commands.Inv.BlackList",
        new String[] {
          "List of players which you should not be able to open inventories",
          "This can still be bypassed if you are OP"
        });
    InvBlackList =
        this.cfg.get(
            "Optimizations.Commands.Inv.BlackList", new ArrayList<>(Arrays.asList("Zrips")));
    CMIList.toLowerCase(InvBlackList);
    this.cfg.addComment(
        "Optimizations.Commands.Inv.DisableOffline",
        new String[] {"Do you want to disable inv command on players who are offline"});
    this.InvDisableOffline = this.cfg.get("Optimizations.Commands.Inv.DisableOffline", false);
    this.cfg.addComment(
        "Optimizations.Multicraft.DisableList",
        new String[] {"When set to true, will prevent multicraft servers to spam in console."});
    this.MulticraftDisableList = this.cfg.get("Optimizations.Multicraft.DisableList", false);
    this.cfg.addComment(
        "Optimizations.Commands.Clear.Confirmation",
        new String[] {
          "When set to true, /cmi clear comamnd will require confirmation for it to be finalized"
        });
    CommandsClearConfirmation = this.cfg.get("Optimizations.Commands.Clear.Confirmation", true);
    this.cfg.addComment(
        "Optimizations.Commands.Clear.Exclude",
        new String[] {
          "List of NBT paths we should check before removing item from inventory. Each line should"
              + " start with NBT: this is for the future updates which will involve aditional"
              + " optional checks"
        });
    CommandsClearExclude =
        this.cfg.get(
            "Optimizations.Commands.Clear.Exclude",
            Arrays.asList("NBT:ItemJoin Name", "NBT:ItemJoin.Name"));
    this.cfg.addComment(
        "Optimizations.Commands.List.ASCOrder",
        new String[] {"Sorts players custom groups in Ascending or descending order"});
    CommandsListASCOrder = this.cfg.get("Optimizations.Commands.List.ASCOrder", true);
    this.cfg.addComment(
        "Optimizations.Commands.Give.LimitEnchants",
        new String[] {
          "When enabled items in give command will be limited to same limits as it is set for"
              + " enchants, same permission requirements will apply"
        });
    CommandsGiveEnchantLimit = this.cfg.get("Optimizations.Commands.Give.LimitEnchants", false);
    SleepStats.loadConfig();
    if (Version.isCurrentHigher(Version.v1_9_R1)) {
      CMICompass.loadConfig();
    }

    this.cfg.addComment(
        "ExploitPatcher.PreventExpPortals",
        new String[] {
          "When enabled this will prevent exp bootles being destroyed on portal edge and"
              + " duplicating them in result of that"
        });
    this.PreventExpPortals = this.cfg.get("ExploitPatcher.PreventExpPortals", true);
    this.cfg.addComment(
        "ExploitPatcher.NoCommandsInBed",
        new String[] {
          "When enabled this will prevent players from performing commands which could lead to some"
              + " bugs",
          "By default its disabled to keep vanilla behavior, but is recommended to enable it to"
              + " avoid issues"
        });
    NoCommandsInBed = this.cfg.get("ExploitPatcher.NoCommandsInBed", false);
    this.cfg.addComment(
        "ExploitPatcher.LimitBooks",
        new String[] {
          "Due to major exploit relating to oversized books this is recomended to be enabled",
          "While this is set to true players will be limited in how many pages they can create",
          "Limitation is determined by "
              + PermissionsManager.CMIPerm.book_pages_$1.getPermissionForShow()
              + " permission node and by default players can create 20 pages"
        });
    LimitBooks = this.cfg.get("ExploitPatcher.LimitBooks", true);
    this.cfg.addComment(
        "ExploitPatcher.BlockEndPortalItemTransfers",
        new String[] {
          "While enabled we will prevent items being teleported with end portal and end beacons"
        });
    BlockEndPortalItemTransfers = this.cfg.get("ExploitPatcher.BlockEndPortalItemTransfers", true);
    this.cfg.addComment(
        "ExploitPatcher.Riptide",
        new String[] {
          "When enabled we will not allow usage of riptide while player has pweather set to rain"
              + " while players world is in sunny state"
        });
    this.ExploitPatcherRiptide = this.cfg.get("ExploitPatcher.Riptide", true);
    this.cfg.addComment(
        "ExploitPatcher.Placeholders.blocked.checkItem",
        new String[] {
          "By default we are blocking PAPI %checkitem_...% placeholder to avoid potential serious"
              + " issues with it",
          "Only disable this if you have dedicated protection for it"
        });
    this.ExploitPatcherCheckItem =
        this.cfg.get("ExploitPatcher.Placeholders.blocked.checkItem", true);
    this.cfg.addComment(
        "Vault.Money",
        new String[] {
          "If your having issues with vault grabbing correct players' group or balance, consider to"
              + " turn this to false"
        });
    this.VaultMoney = this.cfg.get("Vault.Money", true);
    this.VaultGroup = this.cfg.get("Vault.Group", true);
    this.plugin.getWorthManager().loadConfig();
    this.cfg.addComment(
        "BossBar.HpBarEnabled",
        new String[] {
          "Enables or disbales bossbar hp bar on 1.9+ servers",
          "Only players with cmi.bossbar.hpbar permission node can see it",
          "Permission node is been rechecked no more often than every minute for efficiency"
        });
    this.BossBarHpBarEnabled = this.cfg.get("BossBar.HpBarEnabled", true);
    this.cfg.addComment(
        "BossBar.HpBarBlackList",
        new String[] {"List of mob types which will be excluded from hp boss bar"});
    var blackList = this.cfg.get("BossBar.HpBarBlackList", Arrays.asList("Ender_dragon"));
    this.HpBarBlackList.clear();

    for (String entityName : blackList) {
      CMIEntityType cmiEntityType = CMIEntityType.get(entityName);
      if (cmiEntityType != null) {
        this.HpBarBlackList.add(cmiEntityType.getType());
      }
    }

    this.cfg.addComment(
        "Ban.OverrideLoginMessage",
        new String[] {
          "When set to true players who are banned will get messages modified by CMI instead of"
              + " seeing vanilla type of message"
        });
    this.OverrideLoginMessage = this.cfg.get("Ban.OverrideLoginMessage", false);
    this.plugin.getHomeManager().loadConfig();
    this.plugin.getAfkManager().loadConfig();
    this.plugin.getHologramManager().loadConfig();
    this.plugin.getVotifierManager().loadConfig();
    this.plugin.getRankManager().loadConfig();
    this.plugin.getSignManager().loadConfig();
    this.plugin.getTabListManager().loadConfig();
    this.plugin.getSkinManager().loadConfig();
    this.cfg.addComment(
        "Alert.Timer",
        new String[] {
          "Time in minutes for how long we want to keep set allert on player when performing /cmi"
              + " alert command. Default is 24 hours"
        });
    alertTimer = this.cfg.get("Alert.Timer", 1440);
    this.cfg.addComment(
        "Notes.ShowOnAlertEvent",
        new String[] {
          "When enebled, when player logs in who has alert set on him, staff member will get"
              + " notification that this player have some notes attached to him"
        });
    this.NotesShowOnAlertEvent = this.cfg.get("Notes.ShowOnAlertEvent", true);
    this.GroundCleanWhiteList.clear();
    this.cfg.addComment(
        "GroundClean.WhiteList.Material",
        new String[] {"List of item types not to be removed on ground clean action"});

    for (String entryName :
        this.cfg.get(
            "GroundClean.WhiteList.Material",
            (List<String>)
                this.cfg.getC().getList("GroundClean.WhiteList", Arrays.asList("itemType")))) {
      if (!entryName.equalsIgnoreCase("itemType")) {
        CMIItemStack cmiItemStack = CMILib.getInstance().getItemManager().getItem(entryName);
        if (cmiItemStack == null) {
          this.plugin.consoleMessage(
              "&cCan't determine whitelisted groundclean item type by (" + entryName + ") name");
        } else {
          this.GroundCleanWhiteList.add(cmiItemStack.getType());
        }
      }
    }

    this.GroundCleanEntityWhiteList.clear();
    this.cfg.addComment(
        "GroundClean.WhiteList.EntityType",
        new String[] {"List of item types not to be removed on ground clean action"});

    for (String typeName :
        this.cfg.get("GroundClean.WhiteList.EntityType", Arrays.asList("entityType"))) {
      if (!typeName.equalsIgnoreCase("entityType")) {
        CMIEntityType entityType = CMIEntityType.get(typeName);
        if (entityType != null && entityType.getType() != null) {
          this.GroundCleanEntityWhiteList.add(entityType.getType());
        } else {
          this.plugin.consoleMessage(
              "&cCan't determine whitelisted groundclean entity type by (" + typeName + ") name");
        }
      }
    }

    try {
      if (this.cfg.getC().isConfigurationSection("Chat.ChatFilter")) {
        ConfigReader configReader = null;

        try {
          configReader = new ConfigReader(CMI.getInstance(), "ChatFilter.yml");
          configReader.process("ChatFilter", this.cfg.getC().get("Chat.ChatFilter"));
          configReader.save();
        } catch (Exception exception2) {
          exception2.printStackTrace();
        }
      }

      this.plugin.getChatFilterManager().load();
      this.plugin.getChatFilterManager().loadCommandFilters();
    } catch (Exception exception3) {
      CMIMessages.sendMessage(
          Bukkit.getConsoleSender(),
          ChatColor.RED + "Cant load chat filter section from config file! Error: ");
      exception3.printStackTrace();
    }

    if (!Bukkit.getOnlineMode()) {
      this.cfg.addComment(
          "Command.Spy.DelayForTrigger",
          new String[] {
            "Delay in seconds for commands spy to turn on after relog",
            "This will prevent some one from loging in with admin account and seeing commands"
                + " before loging in",
            "Keep it at same or higher than your login plugin delay for entering password",
            "This includes social spy too"
          });
      this.SpyDelayForTrigger = this.cfg.get("Command.Spy.DelayForTrigger", 60);
    }

    this.cfg.addComment(
        "Command.Spy.BlackListed",
        new String[] {
          "Commands in this list will not be shown when command spy is enabled for player for"
              + " security/privacy reasons",
          "This can be bypassed with "
              + PermissionsManager.CMIPerm.command_commandspy_bypass.getPermission()
              + " permission node"
        });
    this.CommandSpyBlackListed =
        this.cfg.get("Command.Spy.BlackListed", Arrays.asList("register", "login", "l"));
    this.cfg.addComment(
        "Command.Spy.CommandList",
        new String[] {
          "Players without cmi.security.admin will only see commands from this list with command"
              + " spy feature",
          "Keeping it empty will include all commands except the black listed ones"
        });
    this.CommandSpyCommandList = this.cfg.get("Command.Spy.CommandList", new ArrayList());
    this.cfg.addComment(
        "Command.Notes.ExpiresIn", new String[] {"For how long to keep players notes in days"});
    this.PlayerNotesExpiresIn =
        this.cfg.get("Command.Notes.ExpiresIn", this.cfg.getC().getInt("PlayerNotes.ExpiresIn", 30))
            * 24L
            * 60L
            * 60L
            * 1000L;
    CMIMail.loadConfig();
    this.plugin.getNickNameManager().load();
    this.cfg.addComment(
        "Dye.BoundToPlayer",
        new String[] {
          "When set to true colored lether armor will be bound to the player who receives that"
              + " coloration and will not work on others if he would give it to them",
          "Only applies for dynamic armor colors like biome, health and so on"
        });
    DyeBoundToPlayer = this.cfg.get("Dye.BoundToPlayer", false);
    this.cfg.addComment(
        "ShowNewVersion",
        new String[] {
          "Shows if there is an available new version on login with cmi.versioncheck permission"
              + " node"
        });
    this.ShowNewVersion = this.cfg.get("ShowNewVersion", true);
    this.plugin.getSpawnerChargesManager().loadConfig();
    this.cfg.addComment(
        "ItemRenaming.Prevent",
        new String[] {
          "When set to true, players will be denyied from renaming defined items in anvil or with"
              + " itemname command",
          "Option to define specific name by using regex format",
          "Can be bypassed with "
              + PermissionsManager.CMIPerm.anvil_itemrename_bypass.getPermission()
        });
    Boolean preventRenaming = this.cfg.get("ItemRenaming.Prevent", false);
    this.cfg.addComment(
        "ItemRenaming.GlobalDisable",
        new String[] {
          "When set to true item renaming will be disabled in general, with anvil or command"
              + " independent of renamed material",
          "This can be bypassed with previous permission node"
        });
    ItemRenamingGlobalDisable = this.cfg.get("ItemRenaming.GlobalDisable", false);
    this.cfg.addComment(
        "ItemRenaming.MaxLength",
        new String[] {
          "Defines maximum length of item name excluding color codes for itemname command",
          "Set it to 0 if you want to remove restrictions"
        });
    ItemRenamingMaxLength = this.cfg.get("ItemRenaming.MaxLength", 64);
    this.cfg.addComment(
        "ItemRenaming.Anvil.ItalicByDefault",
        new String[] {
          "When enabled and items name is being colorized, we will enforce it to be in Italic to"
              + " mimic vanilla item renaming"
        });
    AddItalicByDefault = this.cfg.get("ItemRenaming.Anvil.ItalicByDefault", true);
    if (this.cfg.getC().isList("Optimizations.ItemName.TypeBlackList")) {
      ArrayList<Material> arrayList = new ArrayList();

      for (String typeEntry :
          this.cfg.getC().getStringList("Optimizations.ItemName.TypeBlackList")) {
        CMIMaterial blackMaterial = CMIMaterial.get(typeEntry);
        if (blackMaterial != null && blackMaterial.getMaterial() != null) {
          arrayList.add(blackMaterial.getMaterial());
        }
      }

      if (!arrayList.isEmpty()) {
        ArrayList<String> blackListItems = new ArrayList();
        if (this.cfg.getC().isList("ItemRenaming.List")) {
          blackListItems.addAll(this.cfg.getC().getStringList("ItemRenaming.List"));
        }

        for (Material material : arrayList) {
          blackListItems.add(material.toString());
        }

        this.cfg.getC().set("ItemRenaming.List", blackListItems);
        this.cfg.get("ItemRenaming.List", blackListItems);
      }
    }

    this.cfg.addComment(
        "ItemRenaming.CheckSource",
        new String[] {
          "When enabled we will check source item in adition to final one",
          "This can prevent items with specific names being completely disabled from being renamed"
              + " and prevent other items being renamed into them"
        });
    ItemRenamingCheckSource = this.cfg.get("ItemRenaming.CheckSource", true);
    this.ItemRenamingPreventMap.clear();
    this.cfg.addComment(
        "ItemRenaming.List",
        new String[] {
          "List of materials followed with optional regex code which can prevent specific naming,"
              + " like renaming mob spawners into another type while still allowing renaming"
              + " spawner into anything else"
        });
    var loreLines =
        this.cfg.get("ItemRenaming.List", Arrays.asList("mobspawner:([A-z]+ (?i)\\w*spawner)"));
    if (preventRenaming) {
      for (String loreEntry : loreLines) {
        String loreText = loreEntry;
        String loreFiltered = "";
        if (loreEntry.contains(":")) {
          loreText = loreEntry.split(":")[0];
          loreFiltered = loreEntry.substring(loreText.length() + 1, loreEntry.length());
        }

        CMIItemStack loreItem = CMILib.getInstance().getItemManager().getItem(loreText);
        if (loreItem == null) {
          this.plugin.consoleMessage("Cant determine item type for: " + loreEntry);
        } else {
          this.ItemRenamingPreventMap.put(loreItem, loreFiltered);
        }
      }
    }

    this.cfg.addComment(
        "SpawnMob.MaxQuantity", new String[] {"Defines how many entities can be spawned at once"});
    this.SpawnMobMaxQuantity = this.cfg.get("SpawnMob.MaxQuantity", 25);
    this.cfg.addComment(
        "SpawnMob.MaxQuantity",
        new String[] {"Defines how many passengers entities can be spawned at once"});
    this.SpawnMobMaxPassengers = this.cfg.get("SpawnMob.MaxPassengers", 10);
    this.cfg.addComment(
        "Counter.Range", new String[] {"Default range to use when performing /counter start"});
    this.CounterRange = this.cfg.get("Counter.Range", 10);
    this.plugin.getMirrorManager().loadConfig();
    this.cfg.addComment(
        "NetherPortal.PreventCreation",
        new String[] {
          "Can prevent nether portal creation entirely. Option to bypass with "
              + PermissionsManager.CMIPerm.netherportalbypass.getPermission()
        });
    this.NetherPortalPreventCreation = this.cfg.get("NetherPortal.PreventCreation", false);
    this.cfg.addComment(
        "NetherPortal.MaxHeight",
        new String[] {"Maximum height nether portal can be created. Vanilla size is 23"});
    this.NetherPortalMaxHeight = this.cfg.get("NetherPortal.MaxHeight", 23);
    this.cfg.addComment(
        "NetherPortal.MaxWidth",
        new String[] {"Maximum width nether portal can be created. Vanilla size is 23"});
    this.NetherPortalMaxWidth = this.cfg.get("NetherPortal.MaxWidth", 23);
    this.plugin.getPortalManager().loadConfig();
    this.plugin.getAnimationManager().loadConfig();
    String damageText =
        Arrays.stream(DamageCause.values())
            .map(damageCause -> damageCause.name().toLowerCase())
            .collect(Collectors.joining(", "));
    this.cfg.addComment(
        "DamageControl",
        new String[] {
          "All possible damage causes: " + damageText,
          "Syntax should be [permissionNode]:[damageCause]:[multiplier]",
          "Example: nolavadamage:lava:0 will prevent lava damage with"
              + " cmi.damagecontrol.nolavadamage permission node",
          "Negative values will heal player instead of damaging him",
          "If player have more than one permission node for same damage cause, then last one in"
              + " list will be used to determine final damage"
        });
    var damageEntries =
        this.cfg.get(
            "DamageControl",
            Arrays.asList("nowalldamage:fly_into_wall:1", "lowermagmacubedamage:hot_floor:0.9"));
    this.plugin.getTotemManager().loadConfig();
    this.plugin.getElytraManager().loadConfig();
    this.DamageControlMap.clear();

    for (String damageEntry : damageEntries) {
      if (damageEntry.contains(":")) {
        String[] strings = damageEntry.split(":");
        if (strings.length == 3) {
          String damageCauseName = strings[0];
          DamageCause damageCause = getCause(strings[1]);
          if (damageCause != null) {
            Double damageValue = null;

            try {
              damageValue = Double.parseDouble(strings[2]);
            } catch (NumberFormatException numberFormatException) {
              continue;
            }

            if (damageCauseName != null) {
              DamageControl damageControl =
                  new DamageControl(damageCauseName, damageCause, damageValue);
              ArrayList<DamageControl> damageControls = new ArrayList();
              if (!this.DamageControlMap.containsKey(damageCause)) {
                damageControls.add(damageControl);
              } else {
                damageControls = this.DamageControlMap.get(damageCause);
                damageControls.add(damageControl);
              }

              this.DamageControlMap.put(damageCause, damageControls);
            }
          }
        }
      }
    }

    this.plugin.getFlightChargeManager().load();
    damageText =
        Arrays.stream(CMIParticle.values())
            .filter(CMIParticle::isParticle)
            .map(cmiParticle -> cmiParticle.name().toLowerCase())
            .collect(Collectors.joining(", "));
    this.cfg.addComment(
        "Point.DefaultParticle",
        new String[] {"Default particle for point command. Options: " + damageText});
    this.PointDefaultParticle =
        CMIEffect.deserialize(this.cfg.get("Point.DefaultParticle", CMIParticle.DUST.name()));
    if (this.PointDefaultParticle == null) {
      this.PointDefaultParticle = new CMIEffect(CMIParticle.DUST);
      this.plugin.consoleMessage(
          "Can't determine point command default particle, defaultin to "
              + CMIParticle.DUST.name());
    }

    CMICustomLogInOutMessages.loadConfig();
    this.cfg.addComment(
        "Books.DefaultAuthor",
        new String[] {"Defines default creator name for books when using getbook command"});
    this.BooksDefaultAuthor = this.cfg.get("Books.DefaultAuthor", "Server");
    this.cfg.addComment(
        "Books.AddDate", new String[] {"When set to true books write date will be added"});
    BooksAddDate = this.cfg.get("Books.AddDate", false);
    this.cfg.addComment(
        "Motd",
        new String[] {
          "Defines name of customtext on players login to server. To disable just set name to non"
              + " existing customText"
        });
    this.Motd = this.plugin.getCTextManager().getCText(this.cfg.get("Motd", "welcomeMessage"));
    this.plugin.getWarningManager().loadConfig();
    SpawnUtil.loadConfig();
    this.cfg.addComment(
        "Spawn.FirstSpawn",
        new String[] {
          "Defines players first spawn point when he logs into server for the first time"
        });
    boolean firstSpawnUse = this.cfg.get("Spawn.FirstSpawn.Use", true);
    if (this.cfg.getC().isString("Spawn.FirstSpawn.Location")) {
      this.firstSpawnPoint =
          CMILocation.fromString(this.cfg.get("Spawn.FirstSpawn.Location", "None"));
      if (!firstSpawnUse || this.firstSpawnPoint.getWorldName().equalsIgnoreCase("none")) {
        this.firstSpawnPoint = null;
      }
    } else {
      String firstSpawnWorld = this.cfg.get("Spawn.FirstSpawn.World", "None");
      Double firstSpawnX = this.cfg.get("Spawn.FirstSpawn.X", 0.0);
      Double firstSpawnY = this.cfg.get("Spawn.FirstSpawn.Y", 0.0);
      Double firstSpawnZ = this.cfg.get("Spawn.FirstSpawn.Z", 0.0);
      double number = this.cfg.get("Spawn.FirstSpawn.Pitch", 0.0);
      double firstSpawnYaw = this.cfg.get("Spawn.FirstSpawn.Yaw", 0.0);
      if (firstSpawnUse && !firstSpawnWorld.equalsIgnoreCase("none")) {
        this.firstSpawnPoint =
            new CMILocation(firstSpawnWorld, firstSpawnX, firstSpawnY, firstSpawnZ);
        this.firstSpawnPoint.setPitch((float) number);
        this.firstSpawnPoint.setYaw((float) firstSpawnYaw);
      }
    }

    this.plugin.getKitsManager().loadConfig(this.cfg);
    this.plugin.getWarpManager().loadConfig(this.cfg);
    this.cfg.addComment(
        "DynamicViewRange.Enabled",
        new String[] {
          "By setting to true will enable dynamic view range feature. Its still in beta stage and"
              + " can result in some CPU load increase.",
          "Don't enable if you are not using this feature on your server"
        });
    this.DynamicViewRangeEnabled = this.cfg.get("DynamicViewRange.Enabled", false);
    CMIWorldLimits.loadConfig();
    this.cfg.addComment(
        "CheckForNameChange", new String[] {"Checks if players name changed from last known"});
    this.cfg.addComment(
        "CheckForNameChange.PerformCommandsOnNewName",
        new String[] {"Do you want to perform commands"});
    this.PerformCommandsOnNewName =
        this.cfg.get("CheckForNameChange.PerformCommandsOnNewName", false);
    this.cfg.addComment(
        "CheckForNameChange.NameChangeCommands",
        new String[] {"Command list to be performed in case player logs in with new name"});
    this.OptimizationsNameChangeCommands =
        this.cfg.get(
            "CheckForNameChange.NameChangeCommands",
            Arrays.asList(
                "asConsole! cmi broadcast !&2[oldname] logged in with new name: [newname]"));
    this.plugin.getSavedInventoryManager().loadConfig();
    this.cfg.addComment(
        "hunger.overide", new String[] {"Do you want to give more than 20 hunger for players"});
    this.hungeroveride = this.cfg.get("hunger.overide", false);
    this.cfg.addComment(
        "heal.RemoveNegative.use",
        new String[] {"Do you want to remove negative potion effects from player on heal"});
    this.RemoveNegative = this.cfg.get("heal.RemoveNegative.use", true);
    this.RemoveNegativeEffects =
        this.cfg.get(
            "heal.RemoveNegative.List",
            Arrays.asList(
                "blindness",
                "confusion",
                "harm",
                "hunger",
                "poison",
                "slow",
                "slow_digging",
                "weakness",
                "wither"));
    if (!this.RemoveNegative) {
      this.RemoveNegativeEffects.clear();
    }

    this.cfg.addComment(
        "Cuff.Mute", new String[] {"When set to false will allow players to talk who is cuffed"});
    this.CuffMute = this.cfg.get("Cuff.Mute", true);
    this.CuffAllowed = this.cfg.get("Cuff.AllowedCommands", Arrays.asList("msg", "r", "tell"));
    this.cfg.addComment(
        "Mute.DenyPrivateMessages",
        new String[] {
          "When set to true, player will not be allowed to send private messages while he is muted"
        });
    MuteNoPm = this.cfg.get("Mute.DenyPrivateMessages", true);
    this.cfg.addComment("Dispose.UILines", new String[] {"Defines how big is dispose ui 1-6"});
    this.DisposeUILines = CMINumber.clamp(this.cfg.get("Dispose.UILines", 4), 1, 6);
    this.cfg.addComment(
        "Dispose.CustomModelData",
        new String[] {"Should we allow disposing items with custom model data"});
    this.DisposeCustomModelData = this.cfg.get("Dispose.CustomModelData", true);
    this.cfg.addComment(
        "Dispose.CustomModelData",
        new String[] {"Should we allow disposing items with attached commands in them"});
    this.DisposeAttachedCommands = this.cfg.get("Dispose.AttachedCommands", true);
    this.cfg.addComment(
        "Dispose.Materials",
        new String[] {"Black list of materials we should not allow to dispose"});
    this.DisposeMaterials =
        this.cfg.get("Dispose.Materials", new ArrayList<String>()).stream()
            .<CMIMaterial>map(CMIMaterial::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    this.plugin.getRepairManager().loadConfig();
    this.plugin.getCooldownManager().loadConfig();
    this.plugin.getPlayerCombatManager().loadConfig();
    this.plugin.getShulkerBoxManager().loadConfig();
    this.plugin.getVanishManager().loadConfig();
    this.plugin.getPlayerOptionsManager().loadConfig();
    this.plugin.getWarmUpManager().loadConfig();
    this.plugin.getJailManager().loadConfig();
    this.plugin.getScanManager().loadConfig();
    this.cfg.addComment(
        "search.LogIntoFile",
        new String[] {
          "When enabled search command output will be printed into file for option to go over it"
              + " later on"
        });
    this.SearchLogIntoFile = this.cfg.get("search.LogIntoFile", false);
    this.cfg.addComment(
        "search.EnablePurge",
        new String[] {
          "When this set to true, all found items in inventories will be deleted during search."
              + " e.g., /cmi search diamondaxe purge"
        });
    this.SearchPurge = this.cfg.get("search.EnablePurge", false);
    if (!Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
      this.cfg.addComment(
          "lfix.SoftCap", new String[] {"Tps cap from which to start adjusting light fix speed"});
      this.lfixSoftCap = CMINumber.clamp(this.cfg.get("lfix.SoftCap", 19.0), 1.0, 20.0);
      this.cfg.addComment(
          "lfix.DefaultSpeed",
          new String[] {"Starting speed when fixing light. Range from 1 to 100"});
      this.lfixDefaultSpeed = CMINumber.clamp(this.cfg.get("lfix.DefaultSpeed", 15), 1, 100);
    }

    this.plugin.getPurgeManager().loadConfig();
    this.cfg.addComment(
        "Selection.Tool", new String[] {"Tool material name to use for selection actions"});
    String configValue =
        this.cfg.get("Selection.Tool", CMIMaterial.WOODEN_SHOVEL.name().toLowerCase());
    this.SelectionTool = CMILib.getInstance().getItemManager().getItem(configValue);
    if (this.SelectionTool == null || this.SelectionTool.getType() == null) {
      this.plugin.consoleMessage(
          "&cCan't determine selection tool type. Defaulting to wooden shovel");
      this.SelectionTool =
          CMILib.getInstance().getItemManager().getItem(CMIMaterial.WOODEN_SHOVEL.newItemStack());
    }

    this.cfg.addComment("Time.Day", new String[] {"Defines preset time"});
    this.TimeDay = this.cfg.get("Time.Day", "12:00");
    this.TimeNight = this.cfg.get("Time.Night", "24:00");
    this.TimeMorning = this.cfg.get("Time.Morning", "06:00");
    this.TimeDusk = this.cfg.get("Time.Dusk", "18:00");
    this.cfg.addComment(
        "Time.AutoTime.Interval",
        new String[] {
          "Time in seconds time in game will be adjusted to match real",
          "Keep it at arround one minute"
        });
    this.AutoTimeInterval = this.cfg.get("Time.AutoTime.Interval", 60);
    this.cfg.addComment(
        "Time.AutoTime.Smooth",
        new String[] {
          "Enables by default smooth sun transition to new time",
          "You can always override this setting with -smooth variable in time command"
        });
    this.AutoTimeSmooth = this.cfg.get("Time.AutoTime.Smooth", true);
    this.cfg.addComment(
        "Time.AutoTime.SmoothSpeed",
        new String[] {
          "Speed of smooth transition",
          "100 will mean that sun moves 100 times faster than usual until it reaches target time"
        });
    this.AutoTimeSmoothSpeed = this.cfg.get("Time.AutoTime.SmoothSpeed", 100);
    this.plugin.getTimeManager().getWorlds().clear();
    this.cfg.addComment(
        "Time.AutoTime.Worlds", new String[] {"Worlds effected by autotime adjustment"});
    this.plugin
        .getTimeManager()
        .getWorlds()
        .addAll(this.cfg.get("Time.AutoTime.Worlds", Arrays.asList("")));
    this.plugin.getTimeManager().loadConfig();
    this.plugin.getEnchantManager().loadConfig();
    this.plugin.getScavengeManager().loadConfig();
    this.plugin.getBungeeCordManager().loadConfig();
    this.plugin.getRecipeManager().loadConfig();
    this.plugin.getBlueMapManager().loadConfig();
    this.cfg.addComment(
        "Sounds",
        new String[] {"If you want to disable particular sound entirely, set it to \"\" "});
    boolean soundsEnabled = this.cfg.get("Sounds.Enabled", true);
    CMISoundPreset[] cmiSoundPresets;
    int soundCount = (cmiSoundPresets = CMISoundPreset.values()).length;

    for (int soundIndex = 0; soundIndex < soundCount; soundIndex++) {
      CMISoundPreset cmiSoundPreset = cmiSoundPresets[soundIndex];
      String soundName =
          this.cfg.get("Sounds." + cmiSoundPreset.toString(), cmiSoundPreset.getSoundString());
      if (soundName != null && !soundName.isEmpty()) {
        cmiSoundPreset.setSoundString(soundName);
        cmiSoundPreset.setEnabled(soundsEnabled);
      } else {
        cmiSoundPreset.setEnabled(false);
      }
    }

    this.cfg.addComment(
        "PotionEffects.DeductWhileOffline",
        new String[] {
          "When set to true player poition effect will expire even if player is offline",
          "Keep in mind that player potion effect durability will be updated on players login event"
              + " so by checking players potions effect while he is offline can show incorrect"
              + " state"
        });
    this.PotionEffectsDeductWhileOffline = this.cfg.get("PotionEffects.DeductWhileOffline", false);
    this.cfg.save();
    this.cfg = null;
    return true;
  }

  private static DamageCause getCause(String string) {
    DamageCause[] damageCauses;
    int value = (damageCauses = DamageCause.values()).length;

    for (int index = 0; index < value; index++) {
      DamageCause damageCause = damageCauses[index];
      if (damageCause
          .name()
          .toLowerCase()
          .replace("_", "")
          .equalsIgnoreCase(string.replace("_", ""))) {
        return damageCause;
      }
    }

    return null;
  }

  public void reload(CommandSender commandSender) {
    Long startTime = System.currentTimeMillis();

    try {
      CMILib.getInstance().getConfigManager().reload(commandSender);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    CMIOnEnable.defaultLocaleDownloader();
    this.plugin.getCTextManager().load();
    this.plugin.getSignManager().load();
    boolean flag = this.load(true);
    var completableFuture = this.reloadLanguage();
    this.plugin.getParticleManager().loadConfig();
    this.plugin.getRandomTeleportationManager().loadConfig();
    this.plugin.getCommandCostManager().loadConfig(true);
    this.plugin.getChatManager().loadConfig(true);
    this.plugin.getKitsManager().onDisable();
    this.plugin.getKitsManager().load();
    this.plugin.getDialogManager().load();
    this.plugin.getSchedulerManager().load();
    this.plugin.getRankManager().load();
    this.plugin.getRankManager().stop();
    this.plugin.getRankManager().run();
    this.plugin.getRankManager().autoRecheck();
    this.plugin.getWarpManager().onDisable();
    this.plugin.getWarpManager().load();
    this.plugin.getAliasManager().fullLoad();
    this.plugin.getPortalManager().load();
    this.plugin.getInteractiveCommandManager().load();
    this.plugin.getWorthManager().load();
    this.plugin.getWorthManager().loadEnchtWorth();
    this.plugin.getChatFilterManager().load();
    this.plugin.getChatFormatManager().load();
    this.plugin.getPlayTimeRewardManager().load();
    this.plugin.getSavedItemManager().load();
    this.plugin.getSkinManager().load();
    this.plugin.getArmorStandManager().load();
    this.plugin.getEventActionManager().load();
    this.plugin.getDeathMessageManager().load();
    CMIBackUpManager.loadConfig();
    ServerLinksManager.load();
    condense.clearCache();
    uncondense.clearCache();
    CMIEntityType.cache.clear();
    completableFuture.thenAccept(
        object -> {
          if (flag && object) {
            CMILC.info(
                reload.class,
                commandSender,
                "feedback",
                "[ms]",
                System.currentTimeMillis() - startTime);
          }

          if (!flag) {
            CMILC.info(reload.class, commandSender, "failedConfig");
          }

          if (!object) {
            CMILC.info(reload.class, commandSender, "failedLocale");
          }

          if (commandSender != null) {
            CMIScheduler.runTask(
                this.plugin,
                () -> Bukkit.getPluginManager().callEvent(new CMIConfigReloadEvent(commandSender)));
          }
        });
    this.plugin.getHologramManager().reload();
  }

  public CompletableFuture<Boolean> reloadLanguage() {
    var completableFuture = this.loadLangAsync("EN");
    if (!this.Lang.equalsIgnoreCase("EN")) {
      completableFuture = completableFuture.thenCompose(object -> this.loadLangAsync(this.Lang));
    }

    return completableFuture.thenApply(
        item -> {
          if (item) {
            this.plugin.getLM().reload();
            CMIPlayTimeFormat.update();
          }

          return (Boolean) item;
        });
  }

  public Location getFirstSpawnPoint() {
    return this.firstSpawnPoint == null ? null : this.firstSpawnPoint.clone();
  }

  public ConfigReader getConfig() {
    if (this.cfg == null) {
      try {
        this.cfg = new ConfigReader(CMI.getInstance(), "config.yml");
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }

    return this.cfg;
  }

  public long getPlayerNotesExpiresIn() {
    return this.PlayerNotesExpiresIn;
  }

  @Deprecated
  public long getPlayerMailExpiresIn() {
    return CMIMail.getMailExpiresInMS();
  }

  @Deprecated
  public boolean isDurabilityLossUse() {
    return this.isToolDurabilityLossUse();
  }

  public boolean isToolDurabilityLossUse() {
    return this.durabilityLossUse;
  }

  @Deprecated
  public int getDurabilityLossPercentage() {
    return this.getToolDurabilityLossPercentage();
  }

  public int getToolDurabilityLossPercentage() {
    return this.durabilityLossPercentage;
  }

  public HashMap<CMIItemStack, String> getItemRenamingPreventMap() {
    return this.ItemRenamingPreventMap;
  }

  public boolean isOnLimitedItemUseInform() {
    return this.OnLimitedItemUseInform;
  }

  @Deprecated
  public boolean isRepairShareProtectNormalRepair() {
    return CMI.getInstance().getRepairManager().isRSProtectNormalRepair();
  }

  @Deprecated
  public int getRepairShareDurability() {
    return CMI.getInstance().getRepairManager().getRSDurability();
  }

  @Deprecated
  public boolean isRepairShareAddLore() {
    return CMI.getInstance().getRepairManager().isRSAddLore();
  }

  @Deprecated
  public boolean isRepairShareCancelEvent() {
    return CMI.getInstance().getRepairManager().isRSCancelEvent();
  }

  @Deprecated
  public boolean isRepairShareInformWithMessage() {
    return CMI.getInstance().getRepairManager().isRSInformWithMessage();
  }

  @Deprecated
  public boolean isRepairShareProtectCommandRepair() {
    return CMI.getInstance().getRepairManager().isRSProtectCommandRepair();
  }

  @Deprecated
  public boolean isRepairShareBypassWithPerm() {
    return CMI.getInstance().getRepairManager().isRSBypassWithPerm();
  }

  public int getMaxHp() {
    return this.MaxHp;
  }

  public boolean isNotesShowOnAlertEvent() {
    return this.NotesShowOnAlertEvent;
  }

  public boolean isPreventExpPortals() {
    return this.PreventExpPortals;
  }

  public ConfigReader getLocaleConfig() {
    if (this.localeFile == null) {
      File file =
          new File(
              this.plugin.getDataFolder(),
              "Translations" + File.separator + "Locale_" + this.Lang + ".yml");

      try {
        this.localeFile = new ConfigReader(file);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }

    return this.localeFile;
  }

  @Deprecated
  public boolean isDisableWorldChunkCheckInfo() {
    return this.plugin.getScanManager().isDisableWorldChunkCheckInfo();
  }

  public boolean isPreventEntityBoatEnterAnimals() {
    return this.PreventEntityBoatEnterAnimals;
  }

  public boolean isPreventEntityBoatEnterMonsters() {
    return this.PreventEntityBoatEnterMonsters;
  }

  public boolean isPreventBedExplosionNether() {
    return this.PreventBedExplosionNether;
  }

  public boolean isPreventIronGolemRoses() {
    return this.PreventIronGolemRoses;
  }

  public boolean isMulticraftDisableList() {
    return this.MulticraftDisableList;
  }

  public boolean isPreventBedExplosionTheEnd() {
    return this.PreventBedExplosionTheEnd;
  }

  public CMIItemStack getSelectionTool() {
    return this.SelectionTool;
  }

  public boolean isNetherPortalPreventCreation() {
    return this.NetherPortalPreventCreation;
  }

  public boolean isMaintenance() {
    return this.maintenance;
  }

  public void setMaintenance(boolean flag) {
    this.ChangeConfig("Optimizations.Maintenance", flag, false);
    this.maintenance = flag;
  }

  public String getMaintenanceMessage() {
    return this.maintenanceMessage;
  }

  public void setMaintenanceMessage(String string) {
    this.maintenanceMessage = string;
  }

  public boolean isOverrideLoginMessage() {
    return this.OverrideLoginMessage;
  }

  public CText getMotd() {
    return this.Motd;
  }

  public boolean isPermisionOnError() {
    return this.PermisionOnError;
  }

  public List<String> getFixWorldsToFix() {
    return this.fixWorldsToFix;
  }

  public List<String> getCommandSpyBlackListed() {
    return this.CommandSpyBlackListed;
  }

  public int getSpyDelayForTrigger() {
    return this.SpyDelayForTrigger;
  }

  public boolean isOptimizationsCommandSorting() {
    return this.OptimizationsCommandSorting;
  }

  public boolean isPreventPlayersOnNetherRoof() {
    return this.PreventPlayersOnNetherRoof;
  }

  public boolean isPreventPlayersBelowBedrock() {
    return this.PreventPlayersBelowBedrock;
  }

  public boolean isBossBarHpBarEnabled() {
    return this.BossBarHpBarEnabled;
  }

  public List<Material> getGroundCleanWhiteList() {
    return this.GroundCleanWhiteList;
  }

  public List<EntityType> getGroundCleanEntityWhiteList() {
    return this.GroundCleanEntityWhiteList;
  }

  public boolean isPartialPlayerName() {
    return this.PartialPlayerName;
  }

  public boolean isOptimizationsCommandRemoveLabel() {
    return this.OptimizationsCommandRemoveLabel;
  }

  @Deprecated
  public boolean isBlockedSpawnReason(String string, SpawnReason spawnReason) {
    return CMIWorldLimits.isBlockedSpawnReason(string, spawnReason);
  }

  public boolean isPreventHook() {
    return this.PreventHook;
  }

  @Deprecated
  public CMIParticle getPointDefaultParticle() {
    return this.PointDefaultParticle.getParticle();
  }

  public CMIEffect getPointDefaultEffect() {
    return this.PointDefaultParticle;
  }

  public boolean isPermisionInConsole() {
    return this.PermisionInConsole;
  }

  public boolean isSellLog() {
    return this.SellLog;
  }

  @Deprecated
  public boolean isSpawnersSupportDisabled() {
    return this.plugin.getSpawnerChargesManager().isSpawnersSupportDisabled();
  }

  public String getBooksDefaultAuthor() {
    return this.BooksDefaultAuthor;
  }

  @Deprecated
  public List<String> getCleanUpWhiteList() {
    return this.plugin.getChatManager().getCleanUpWhiteList();
  }

  @Deprecated
  public boolean isChatReplyToLastMessenger() {
    return this.plugin.getChatManager().isChatReplyToLastMessenger();
  }

  @Deprecated
  public int getLastMessengerTimeOut() {
    return this.plugin.getChatManager().getLastMessengerTimeOut();
  }

  @Deprecated
  public boolean isColorsMe() {
    return this.plugin.getChatManager().isColorsMe();
  }

  @Deprecated
  public boolean isChatDynMapChat() {
    return this.plugin.getChatManager().isChatDynMapChat();
  }

  @Deprecated
  public boolean isModifyChatFormat() {
    return this.plugin.getChatManager().isModifyChatFormat();
  }

  @Deprecated
  public boolean isChatClickHoverMessages() {
    return this.plugin.getChatManager().isChatClickHoverMessages();
  }

  @Deprecated
  public boolean isColorsPublicMessages() {
    return this.plugin.getChatManager().isColorsPublicMessages();
  }

  @Deprecated
  public boolean isColorsPrivateMessage() {
    return this.plugin.getChatManager().isColorsPrivateMessage();
  }

  @Deprecated
  public boolean isChatIgnorePublicMessage() {
    return this.plugin.getChatManager().isChatIgnorePublicMessage();
  }

  public List<String> getCommandSpyCommandList() {
    return this.CommandSpyCommandList;
  }

  public boolean isCMIPlayTimeTracking() {
    return this.CMIPlayTimeTracking;
  }

  public void setCMIPlayTimeTracking(boolean flag) {
    this.CMIPlayTimeTracking = flag;
  }

  @Deprecated
  public int getPlayerMailAllDays() {
    return (int) (CMIMail.getMailToAllExpirationMS() / 86400000L);
  }

  @Deprecated
  public String getElevatorIndicator() {
    return this.ElevatorIndicator.isEmpty() ? "[CMIElevator]" : this.ElevatorIndicator.get(0);
  }

  public List<String> getElevatorIndicators() {
    return this.ElevatorIndicator;
  }

  public String getElevatorStaticIndicator() {
    return this.ElevatorStaticIndicator;
  }

  @Deprecated
  public boolean isSpawnersXpDrop() {
    return this.plugin.getSpawnerChargesManager().isSpawnersXpDrop();
  }

  @Deprecated
  public int getLogoutAutoHideFrom() {
    return CMICustomLogInOutMessages.getLogoutAutoHideFrom();
  }

  @Deprecated
  public int getLoginAutoHideFrom() {
    return CMICustomLogInOutMessages.getLoginAutoHideFrom();
  }

  public boolean isAutoDownloadGeoIp() {
    return this.AutoDownloadGeoIp;
  }

  public boolean isAutoDownloadGeoLiteCity() {
    return this.AutoDownloadGeoLiteCity;
  }

  @Deprecated
  public boolean isPreloadTopPlaytime() {
    return false;
  }

  @Deprecated
  public boolean isElytraPreventSelfDamage() {
    return this.plugin.getElytraManager().isPreventSelfDamage();
  }

  @Deprecated
  public boolean isFlyAboveRoof() {
    return CMIWorldLimits.isFlyAboveRoof();
  }

  public int getOptimizationsSimilarCommandChecker() {
    return this.OptimizationsSimilarCommandChecker;
  }

  @Deprecated
  public HashMap<String, Integer> getFlyAboveRoofLimitationsMap() {
    return CMIWorldLimits.getFlyAboveRoofLimitationsMap();
  }

  public String getLongDateFormat() {
    return this.LongDateFormat;
  }

  public String getShortDateFormat() {
    return this.ShortDateFormat;
  }

  public boolean isAutoTimeSmooth() {
    return this.AutoTimeSmooth;
  }

  public int getAutoTimeSmoothSpeed() {
    return this.AutoTimeSmoothSpeed;
  }

  public List<EntityType> getHpBarBlackList() {
    return this.HpBarBlackList;
  }

  @Deprecated
  public RandomTeleport getRandomTeleport(World world) {
    return this.getRandomTeleport(world, false);
  }

  @Deprecated
  public RandomTeleport getRandomTeleport(World world, boolean flag) {
    return this.plugin.getRandomTeleportationManager().getRandomTeleport(world, flag);
  }

  @Deprecated
  public HashMap<String, RandomTeleport> getRandomTeleports() {
    return this.plugin.getRandomTeleportationManager().getRandomTeleports();
  }

  @Deprecated
  public int getRandomTeleportMaxTries() {
    return this.plugin.getRandomTeleportationManager().getRandomTeleportMaxTries();
  }

  @Deprecated
  public int getRandomTeleportCooldown() {
    return this.plugin.getRandomTeleportationManager().getRandomTeleportCooldown();
  }

  @Deprecated
  public List<String> getRandomTeleportExcludedBiomes() {
    return this.plugin.getRandomTeleportationManager().getRandomTeleportExcludedBiomes();
  }

  public List<Material> getItemLoreTypeBlackList() {
    return this.ItemLoreTypeBlackList;
  }

  @Deprecated
  public boolean isDisableRiptide() {
    return this.plugin.getElytraManager().isDisableRiptide();
  }

  public boolean isOptimizationsSimilarCommandPrevention() {
    return this.OptimizationsSimilarCommandPrevention;
  }

  @Deprecated
  public boolean isBossBarCompassEnabled() {
    return CMICompass.isBossBarCompassEnabled();
  }

  @Deprecated
  public boolean isCompassRequireCompass() {
    return CMICompass.isCompassRequireCompass();
  }

  @Deprecated
  public int getCompassUpdateInterval() {
    return CMICompass.getCompassUpdateInterval();
  }

  @Deprecated
  public String getCompassShape() {
    return CMICompass.getCompassShape();
  }

  @Deprecated
  public String getCompassColor() {
    return CMICompass.getCompassColor();
  }

  @Deprecated
  public String getCompassHomeIcon() {
    return CMICompass.getCompassHomeIcon();
  }

  @Deprecated
  public String getCompassSpawnIcon() {
    return CMICompass.getCompassSpawnIcon();
  }

  @Deprecated
  public String getCompassDeathIcon() {
    return CMICompass.getCompassDeathIcon();
  }

  @Deprecated
  public String getCompassTargetIcon() {
    return CMICompass.getCompassTargetIcon();
  }

  @Deprecated
  public boolean isRecoveryRequireCompass() {
    return CMICompass.isRecoveryRequireCompass();
  }

  @Deprecated
  public boolean isRecoveryAsRegularCompass() {
    return CMICompass.isRecoveryAsRegularCompass();
  }

  public boolean isPotionEffectsDeductWhileOffline() {
    return this.PotionEffectsDeductWhileOffline;
  }

  public boolean isPreventEntityBoatEnterVillagers() {
    return this.PreventEntityBoatEnterVillagers;
  }

  public int getArmorDurabilityLossPercentage() {
    return this.armorDurabilityLossPercentage;
  }

  public void setArmorDurabilityLossPercentage(int value) {
    this.armorDurabilityLossPercentage = value;
  }

  public boolean isArmorDurabilityLossUse() {
    return this.armorDurabilityLossUse;
  }

  public void setArmorDurabilityLossUse(boolean flag) {
    this.armorDurabilityLossUse = flag;
  }

  public boolean isItemNameMarkChanged() {
    return this.ItemNameMarkChanged;
  }

  public boolean isItemLoreMarkChanged() {
    return this.ItemLoreMarkChanged;
  }

  public boolean isShowNewVersion() {
    return this.ShowNewVersion;
  }

  public boolean isSignEditBlocked(String string) {
    return this.signEditBlackList.contains(CMIChatColor.stripColor(string).toLowerCase());
  }

  public int getNetherRoofHeight() {
    return this.netherRoofHeight;
  }

  public boolean isInvDisableOffline() {
    return this.InvDisableOffline;
  }

  @Deprecated
  public boolean isMirrorBreakDisabled() {
    return this.plugin.getMirrorManager().isBreakDisabled();
  }

  public boolean isExploitPatcherRiptide() {
    return this.ExploitPatcherRiptide;
  }

  @Deprecated
  public int getPlayerMailMax() {
    return CMIMail.getMaxMail();
  }

  @Deprecated
  public int getTeleportTpaMaxDistance() {
    return this.plugin.getTeleportManager().getTpaMaxDistance();
  }

  @Deprecated
  public int getTeleportTpaHereMaxDistance() {
    return this.plugin.getTeleportManager().getTpaHereMaxDistance();
  }

  @Deprecated
  public List<String> getBackBlackList() {
    return this.plugin.getTeleportManager().getBackBlackList();
  }

  @Deprecated
  public int getBackMinDistance() {
    return this.plugin.getTeleportManager().getBackMinDistance();
  }

  @Deprecated
  public boolean isSafeLocationDownThenUp() {
    return this.plugin.getTeleportManager().isSafeLocationDownThenUp();
  }

  @Deprecated
  public boolean isTpaCurrentLoc() {
    return this.plugin.getTeleportManager().isTpaCurrentLoc();
  }

  @Deprecated
  public boolean isTpahereCurrentLoc() {
    return this.plugin.getTeleportManager().isTpahereCurrentLoc();
  }

  @Deprecated
  public int getTeleportJumpDefault() {
    return this.plugin.getTeleportManager().getJumpDefaultDistance();
  }

  @Deprecated
  public HashMap<Material, Integer> getBlockedItems() {
    return this.plugin.getTeleportManager().getBlockedItems();
  }

  @Deprecated
  public Boolean isBlackListedItemsEnabledFor(TeleportManager.TpAction tpAction) {
    return this.plugin.getTeleportManager().isBlackListedItemsEnabledFor(tpAction);
  }

  @Deprecated
  public boolean isTeleportSwitchPlaces() {
    return this.plugin.getTeleportManager().isSwitchTeleportPlaces();
  }

  @Deprecated
  public int getTeleportTpaWarmup() {
    return this.plugin.getTeleportManager().getTpaWarmupTime();
  }

  @Deprecated
  public boolean isTeleportTpaMove() {
    return this.plugin.getTeleportManager().isTpaMove();
  }

  public boolean isAllowRconCommands() {
    return this.AllowRconCommands;
  }

  public boolean isCleanRconCommands() {
    return this.CleanRconCommands;
  }

  public boolean isPerformCommandsOnNewName() {
    return this.PerformCommandsOnNewName;
  }

  public List<String> getOptimizationsNameChangeCommands() {
    return this.OptimizationsNameChangeCommands;
  }

  public boolean isExploitPatcherCheckItem() {
    return this.ExploitPatcherCheckItem;
  }

  public List<String> getCustomCommandSenders() {
    return this.CustomCommandSenders;
  }
}
