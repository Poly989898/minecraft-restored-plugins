package net.Zrips.CMILib;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Colors.CMIGlobalColor;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.Entities.CMIEntity;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Images.CMIImage;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Items.CMIPotionEffectType;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Version;
import net.Zrips.CMILib.commands.CAnnotation;
import net.Zrips.CMILib.commands.CMICommand;
import net.Zrips.CMILib.commands.Cmd;
import net.Zrips.CMILib.commands.CommandsHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class CMILibConfig {
  private CMILib plugin;
  public static String lang = "EN";
  public static boolean LanguageDownload = true;
  public static boolean autoUpdate = true;
  public static boolean permisionOnError = true;
  public static boolean isPermisionInConsole = true;
  public static boolean monochromeConsole = true;
  public static boolean showMainHelpPage = true;
  public static int SimilarCommandChecker = 75;
  public static boolean CommandSorting = true;
  public static boolean isSimilarCommandPrevention = false;
  public static boolean rmcConsoleLog = true;
  public static boolean OfficialHex = true;
  public static boolean QuirkyHex = true;
  public static List<String> mysterySpawners;
  public static boolean ExploitPatcherCheckItem = true;
  public static boolean legacyRawMessage = true;
  private CMIItemStack GUIEmptyField = null;
  private CMIItemStack GUIPreviousPage = null;
  private CMIItemStack GUINextPage = null;
  private CMIItemStack GUIMiddlePage = null;
  private CMIItemStack GUIClose = null;
  private CMIItemStack GUIInfo = null;
  private ConfigReader localeFile = null;
  private ConfigReader cfg = null;
  List<String> CommentList =
      new ArrayList<>(
          Arrays.asList(
              "The phrases support full color (hex) code, and some variables.",
              "Keep in mind that some variables will not work for certain lines.",
              "Just keep them where there are now and everything will be okay :)",
              "Some lines can have global variables set. For the player who will be affected. For"
                  + " example /heal Zrips then Zrips data will be used as variable",
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
              "Sender is console or player who performs the command. For example Zrips performs"
                  + " /heal Zhax then Zrips data will be used",
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
              "Source is player which is being used for extra info. For example Zrips performs /tp"
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
              "Some lines support the option to send them to custom places, like action bar, title,"
                  + " sub-title, or even create JSON/clickable messages",
              "If the line starts with !toast! then player will get toast message (advancement"
                  + " popup, only 1.12 and up). Some extra variables can be used to define type and"
                  + " icon. example: !toast! -t:goal -icon:paper Hello world!",
              "If the line starts with !actionbar! then player will get action bar message defined"
                  + " after this variable",
              "If the line starts with !actionbar:[seconds]! then player will get action bar"
                  + " message for a defined amount of time",
              "If the line starts with !broadcast! then everyone will receive message. You can add"
                  + " extra !toast! !actionbar! or !title! to send message for everyone to specific"
                  + " place, in example !broadcast!!title!",
              "If the line starts with !customtext:[cTextName]! then custom text will be taken by"
                  + " name provided and shown for player. In case it is used after !broadcast! then"
                  + " everyone who is online will get this custom text message",
              "If the line starts with !title! then player will get title message defined after"
                  + " this variable, in addition it can contain !subtitle! which will add subtitle"
                  + " message",
              "If the line starts with !bossbar:[name]-[timer]! then player will get bossbar"
                  + " message defined after this variable, in addition you can define how long this"
                  + " message will be visible. You need to define bossbar name which can be"
                  + " anything you want, but lines with same name will override each other to"
                  + " prevent stacking",
              "To include clickable messages: <T>Text</T><H>Hover"
                  + " text</H><C>command</C><SC>Suggested text</SC>",
              "<T> and </T> required, other is optional",
              "Use /n to break line",
              "To have more than one JSON message use <Next>",
              "<C> performs command as a player who clicked",
              "<CC> performs command from console once",
              "<CCI> performs command from console every time player clicks text",
              "<URL> includes url"));

  public CMILibConfig(CMILib plugin) {
    this.plugin = plugin;
  }

  private static StringBuilder formStringBuilder(List<String> list) {
    StringBuilder header = new StringBuilder();

    for (String one : list) {
      header.append(System.lineSeparator());
      header.append(one);
    }

    return header;
  }

  public boolean load() {
    return this.load(false);
  }

  public boolean load(boolean isReload) {
    try {
      this.cfg = new ConfigReader(this.plugin, "config.yml");
    } catch (Exception e) {
      if (isReload) {
        CMIMessages.consoleMessage(
            "&cSEVERE CONFIG FILE READ ERROR. Check your spelling! Changes will not be applied"
                + " until you fix it or perform server restart which can result in config file"
                + " reset");
        return false;
      }

      e.printStackTrace();
    }

    if (this.cfg == null) {
      return false;
    }

    String locale = "EN";
    File ff = new File("plugins" + File.separator + "CMI" + File.separator + "config.yml");
    if (ff.isFile()) {
      try {
        ConfigReader configReader = new ConfigReader(ff);
        locale = configReader.getC().getString("Language", "EN");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    this.cfg.addComment("Language", "Language file you want to use");
    lang = this.cfg.get("Language", locale).toUpperCase();
    this.cfg.addComment(
        "LanguageDownload",
        "Defines if you want to auto download default locale files from github repository",
        "You can disable this if you are using EN or you already have your locale setup and you"
            + " don't need to have other languages being downloaded");
    LanguageDownload = this.cfg.get("LanguageDownload", true);
    this.cfg.addComment(
        "AutoUpdate", "When enabled plugin will try to keep CMILib up to date automatically");
    autoUpdate = this.cfg.get("AutoUpdate", false);
    this.cfg.addComment(
        "ExploitPatcher.Placeholders.blocked.checkItem",
        "By default we are blocking PAPI %checkitem_...% placeholder to avoid potential serious"
            + " issues with it",
        "Only disable this if you have dedicated protection for it");
    ExploitPatcherCheckItem = this.cfg.get("ExploitPatcher.Placeholders.blocked.checkItem", true);
    this.cfg.addComment(
        "LinkRegex",
        "Responsible to automatically detect links inside text",
        "Default:"
            + " (?i)(?<![a-z0-9_&])(?:(?:https?|ftps?):\\/\\/[a-z0-9.-]+\\.[a-z]{2,}|[a-z0-9.-]+\\.[a-z]{2,3})(?::\\d+)?(?:\\/[^\\s]*)?(?=$|[\\s<>()])");
    String ChatLinkRegex =
        this.cfg.get(
            "LinkRegex",
            "(?i)(?<![a-z0-9_&])(?:(?:https?|ftps?):\\/\\/[a-z0-9.-]+\\.[a-z]{2,}|[a-z0-9.-]+\\.[a-z]{2,3})(?::\\d+)?(?:\\/[^\\s]*)?(?=$|[\\s<>()])");
    RawMessage.updateLinkRegex(ChatLinkRegex);
    if (Version.isCurrentEqualOrHigher(Version.v1_16_0) && Version.isPaperBranch()) {
      this.cfg.addComment(
          "LegacyRawMessage",
          "When enabled plugin will use legacy raw message system instead of new one",
          "Responsible for clikable messages, hover messages",
          "Recommended to keep it disabled if you are running Paper 1.16+ server");
      legacyRawMessage = this.cfg.get("LegacyRawMessage", false);
    }

    this.plugin.getSkinManager().loadConfig();
    this.cfg.addComment(
        "GlobalGui.EmptyField",
        "Defines item type in empty fields in GUI when its needed to be filled up");
    this.GUIEmptyField =
        CMILib.getInstance()
            .getItemManager()
            .getItem(this.cfg.get("GlobalGui.EmptyField", "BLACK_STAINED_GLASS_PANE"));
    if (this.GUIEmptyField == null) {
      this.GUIEmptyField = new CMIItemStack(CMIMaterial.BLACK_STAINED_GLASS_PANE.newItemStack());
    }

    this.GUIEmptyField.setDisplayName("&r ");
    this.cfg.addComment("GlobalGui.Pages.Previous", "Icon for UI previous page button");
    this.GUIPreviousPage =
        CMILib.getInstance()
            .getItemManager()
            .getItem(
                this.cfg.get(
                    "GlobalGui.Pages.Previous",
                    "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0="));
    if (this.GUIPreviousPage == null) {
      this.GUIPreviousPage = new CMIItemStack(CMIMaterial.WHITE_WOOL.newItemStack());
    }

    this.cfg.addComment("GlobalGui.Pages.Next", "Icon for UI next page button");
    this.GUINextPage =
        CMILib.getInstance()
            .getItemManager()
            .getItem(
                this.cfg.get(
                    "GlobalGui.Pages.Next",
                    "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19"));
    if (this.GUINextPage == null) {
      this.GUINextPage = new CMIItemStack(CMIMaterial.GRAY_WOOL.newItemStack());
    }

    this.cfg.addComment("GlobalGui.Pages.Middle", "Icon for UI information button");
    this.GUIMiddlePage =
        CMILib.getInstance()
            .getItemManager()
            .getItem(
                this.cfg.get(
                    "GlobalGui.Pages.Middle",
                    "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmEyYWZhN2JiMDYzYWMxZmYzYmJlMDhkMmM1NThhN2RmMmUyYmFjZGYxNWRhYzJhNjQ2NjJkYzQwZjhmZGJhZCJ9fX0="));
    if (this.GUIMiddlePage == null) {
      this.GUIMiddlePage = new CMIItemStack(CMIMaterial.LIGHT_GRAY_WOOL.newItemStack());
    }

    this.cfg.addComment("GlobalGui.Close", "Icon for UI close button");
    this.GUIClose =
        CMILib.getInstance()
            .getItemManager()
            .getItem(
                this.cfg.get(
                    "GlobalGui.Close",
                    "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzM4YWIxNDU3NDdiNGJkMDljZTAzNTQzNTQ5NDhjZTY5ZmY2ZjQxZDllMDk4YzY4NDhiODBlMTg3ZTkxOSJ9fX0="));
    if (this.GUIClose == null) {
      this.GUIClose = new CMIItemStack(CMIMaterial.LIGHT_GRAY_WOOL.newItemStack());
    }

    this.cfg.addComment("GlobalGui.Info", "Icon for UI info button");
    this.GUIInfo =
        CMILib.getInstance()
            .getItemManager()
            .getItem(
                this.cfg.get(
                    "GlobalGui.Info",
                    "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjcwNWZkOTRhMGM0MzE5MjdmYjRlNjM5YjBmY2ZiNDk3MTdlNDEyMjg1YTAyYjQzOWUwMTEyZGEyMmIyZTJlYyJ9fX0="));
    if (this.GUIInfo == null) {
      this.GUIInfo = new CMIItemStack(CMIMaterial.LIGHT_GRAY_WOOL.newItemStack());
    }

    this.cfg.addComment(
        "Spawners.mysterySpawners",
        "List of spawners to pick from while using spawner:random variable");
    mysterySpawners =
        this.cfg.get(
            "Spawners.mysterySpawners",
            Arrays.asList("skeleton", "zombie", "silverfish", "panda", "fox"));
    this.cfg.addComment(
        "RMCCommands.ConsoleLog",
        "When enabled we will (where possible) include which command was performed when using rmc"
            + " commands");
    rmcConsoleLog = this.cfg.get("RMCCommands.ConsoleLog", true);
    this.cfg.addComment(
        "Images.Filler",
        "Symbol to be used to create image fields",
        "Color codes are NOT supported here",
        "This will take full effect after server restart due to some images being already cached");
    CMIImage.imageFiller = CMIChatColor.stripColor(this.cfg.get("Images.Filler", "⬛"));
    this.cfg.addComment(
        "Images.EmptyFiller",
        "Symbol to be used to fill in empty image fields",
        "Color codes are supported here");
    CMIImage.imageEmptyFiller = CMIChatColor.translate(this.cfg.get("Images.EmptyFiller", "&7_|"));
    this.cfg.addComment(
        "Colors.OfficialHex",
        "When enabled plugin will try to detect simplified hex color codes like #f6f6f6 or #ff6 in"
            + " adition to {#f6f6f6} and {#red}",
        "Keep in mind that this adds extra checks and simplified format will not support gradients"
            + " or named colors so you will still need to use more complex format for those");
    OfficialHex = this.cfg.get("Colors.OfficialHex", true);
    this.cfg.addComment(
        "Colors.QuirkyHex",
        "When enabled plugin will try to detect quirky hex color codes like &#f6f6f6 or &#ff6 in"
            + " adition to {#f6f6f6} and {#red}",
        "Keep in mind that this adds extra checks and quirky format will not support gradients or"
            + " named colors so you will still need to use more complex format for those");
    QuirkyHex = this.cfg.get("Colors.QuirkyHex", true);
    this.cfg.addComment(
        "Colors.Global",
        "Defining global chat colors used in locale file",
        "This will allow you to modify multiple colors at once");

    for (CMIGlobalColor one : CMIGlobalColor.values()) {
      this.cfg.addComment(
          "Colors.Global." + one.toString(),
          "Use "
              + one.getColorId()
              + " to use this color. Default: "
              + CMIChatColor.deColorize(one.getColor()),
          one.getDescription());
      one.setColor(
          this.cfg.get("Colors.Global." + one.toString(), CMIChatColor.deColorize(one.getColor())));
    }

    this.cfg.save();
    return true;
  }

  public boolean LoadLang(String lang) {
    return this.LoadLang(lang, false);
  }

  public boolean reloadLanguage() {
    boolean langLoaded = this.LoadLang("EN", true);
    if (!lang.equalsIgnoreCase("EN") && !this.LoadLang(lang, true)) {
      langLoaded = false;
    }

    if (langLoaded) {
      this.plugin.getLM().setLang(lang);
      this.plugin.getLM().reload();
    }

    return langLoaded;
  }

  public boolean LoadLang(String lang, boolean isReload) {
    Long time = System.currentTimeMillis();
    File f =
        new File(
            this.plugin.getDataFolder(),
            "Translations" + File.separator + "Locale_" + lang + ".yml");

    try {
      this.localeFile = new ConfigReader(f);
    } catch (Exception e1) {
      if (isReload) {
        CMIMessages.consoleMessage(
            "&cSEVERE LOCALE FILE READ ERROR. Check your spelling! Changes will not be applied"
                + " until you fix it or perform server restart which can result in locale file"
                + " reset to default");
        return false;
      }
    }

    if (this.localeFile == null) {
      return false;
    }

    if (lang.equalsIgnoreCase(CMILibConfig.lang)) {
      this.localeFile.setRecordContents(true);
    }

    this.localeFile.options().copyDefaults(true);
    this.localeFile.options().header(formStringBuilder(this.CommentList).toString());

    for (LC one : LC.values()) {
      if (one.getComments() != null && !one.getComments().isEmpty()) {
        for (String oneC : one.getComments()) {
          this.localeFile.addComment(one.name().replace("_", "."), oneC);
        }
      }

      this.localeFile.get(one.name().replace("_", "."), one.getText());
    }

    HashMap<String, String> damageCauseList = new HashMap<>();
    damageCauseList.put("BLOCK_EXPLOSION", "Explosion");
    damageCauseList.put("CONTACT", "Block Damage");
    damageCauseList.put("CUSTOM", "Unknown");
    damageCauseList.put("DRAGON_BREATH", "Dragon breath");
    damageCauseList.put("DROWNING", "Drowning");
    damageCauseList.put("ENTITY_ATTACK", "Entity attack");
    damageCauseList.put("ENTITY_EXPLOSION", "Explosion");
    damageCauseList.put("FALL", "Fall");
    damageCauseList.put("FALLING_BLOCK", "Falling block");
    damageCauseList.put("FIRE", "Fire");
    damageCauseList.put("FIRE_TICK", "Fire");
    damageCauseList.put("FLY_INTO_WALL", "Fly into wall");
    damageCauseList.put("HOT_FLOOR", "Magma block");
    damageCauseList.put("LAVA", "Lava");
    damageCauseList.put("LIGHTNING", "Lightning");
    damageCauseList.put("MAGIC", "Magic");
    damageCauseList.put("MELTING", "Melting");
    damageCauseList.put("POISON", "Poison");
    damageCauseList.put("PROJECTILE", "Projectile");
    damageCauseList.put("STARVATION", "Starvation");
    damageCauseList.put("SUFFOCATION", "Suffocation");
    damageCauseList.put("SUICIDE", "Suicide");
    damageCauseList.put("THORNS", "Thorns");
    damageCauseList.put("VOID", "Void");
    damageCauseList.put("WITHER", "Wither");
    Arrays.stream(DamageCause.values())
        .map(Enum::name)
        .sorted()
        .filter(name -> name != null)
        .forEach(
            name -> {
              String path = "info.DamageCause." + name.toLowerCase();
              if (!damageCauseList.containsKey(name.toUpperCase())) {
                this.localeFile.get(path, name.toLowerCase().replace("_", " "));
              } else {
                this.localeFile.get(path, damageCauseList.get(name.toUpperCase()));
              }
            });
    CMIEntity.initialize();
    CMIEnchantment.updateLocale();
    CMIPotionEffectType.loadLocalization();
    CMIWorld.initialize();
    this.localeFile.get("command.help.output.usage", "&eUsage: &7%usage%");
    this.localeFile.get("command.help.output.cmdInfoFormat", "[command] &f- &e[description]");
    this.localeFile.get("command.help.output.cmdFormat", "&6/[command]&f[arguments]");
    this.localeFile.get("command.help.output.helpPageDescription", "&e* [description]");
    this.localeFile.get("command.help.output.explanation", "&e * [explanation]");
    this.localeFile.get("command.help.output.title", "&e------ ======= &6Help&e &e======= ------");
    this.localeFile.get("command.nocmd.help.info", "&eShows all available commands");
    this.localeFile.get("command.nocmd.help.args", "");

    for (Entry<String, CMICommand> one : CommandsHandler.getCommands().entrySet()) {
      CMICommand cmd = one.getValue();
      if (!cmd.getAnottation().hidden() && !cmd.getAnottation().test()) {
        CAnnotation cs = cmd.getAnottation();
        String info = cs.info();
        if (info != null) {
          this.localeFile.get("command." + cmd.getName() + ".help.info", info);
        }

        String args = cs.args();
        if (args != null) {
          this.localeFile.get("command." + cmd.getName() + ".help.args", args);
        }

        String[] multiTab = cs.multiTab();
        if (multiTab.length > 0) {
          for (Object oneT : Arrays.asList(multiTab)) {
            StringBuilder str = new StringBuilder();
            str.append(cmd.getName());
            str.append(" ");
            str.append(oneT);
            this.plugin.getTab().addTabComplete(str.toString());
          }
        } else {
          this.plugin.getTab().addTabComplete(cmd.getName());
        }

        List<String> explanation = Arrays.asList(cs.explanation());
        if (explanation != null && !explanation.isEmpty()) {
          this.localeFile.get("command." + cmd.getName() + ".help.explanation", explanation);
        }

        Cmd cm = cmd.getCmdClass();
        this.localeFile.setP(cmd.getName());
        cm.getExtra(this.localeFile);
        this.localeFile.resetP();
      }
    }

    this.localeFile.save();
    CMIMessages.consoleMessage(
        "Updated ("
            + lang
            + ") language file. Took &6"
            + (System.currentTimeMillis() - time)
            + "ms");
    this.localeFile = null;
    return true;
  }

  public void reload(CommandSender player) {
    Long time = System.currentTimeMillis();
    boolean configLoaded = this.load(true);
    boolean langLoaded = this.reloadLanguage();
    CMIWorld.onDisable();
    this.plugin.getItemManager().loadLocale();
    CMIEntityType.cache.clear();
    CMIChatColor.clearCache();
    if (configLoaded && langLoaded) {
      this.plugin.info("reload", player, "feedback", "[ms]", System.currentTimeMillis() - time);
    }

    if (!configLoaded) {
      this.plugin.info("reload", player, "failedConfig");
    }

    if (!langLoaded) {
      this.plugin.info("reload", player, "failedLocale");
    }
  }

  public ConfigReader getLocaleConfig() {
    if (this.localeFile == null) {
      File f =
          new File(
              this.plugin.getDataFolder(),
              "Translations" + File.separator + "Locale_" + lang + ".yml");

      try {
        this.localeFile = new ConfigReader(f);
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }

    return this.localeFile;
  }

  public CMIItemStack getGUIEmptyField() {
    return this.GUIEmptyField;
  }

  public CMIItemStack getGUIPreviousPage() {
    return this.GUIPreviousPage;
  }

  public CMIItemStack getGUIMiddlePage() {
    return this.GUIMiddlePage;
  }

  public CMIItemStack getGUINextPage() {
    return this.GUINextPage;
  }

  public ConfigReader getConfigFile() {
    return this.cfg;
  }

  public void setConfigFile(ConfigReader cfg) {
    this.cfg = cfg;
  }

  public CMIItemStack getGUIClose() {
    return this.GUIClose;
  }

  public CMIItemStack getGUIInfo() {
    return this.GUIInfo;
  }
}
