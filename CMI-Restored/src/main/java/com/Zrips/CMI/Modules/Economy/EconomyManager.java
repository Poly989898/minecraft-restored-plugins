package com.Zrips.CMI.Modules.Economy;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMILeaderboard;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.utils.DateFormat;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;

public class EconomyManager {
  private CMI plugin;
  private boolean Enabled = false;
  private boolean CustomWorldsEnabled = false;
  private boolean Confirmation = false;
  private boolean OfflinePayments = false;
  private boolean LogEnabled = false;
  private List<String> logIgnoredUsers;
  private double MaxChequeValue;
  private boolean ChequePaper;
  private boolean ChequePermission;
  private boolean ChequeInCreative;
  private boolean ChequeAcceptNotEncoded;
  private boolean BalTopIncludeFakes;
  private long ExcludeInactive;
  private boolean BalTopDisplayWithShorts;
  private boolean BalTopLoadInAll = true;
  private List<String> BalTopExclude;
  private List<String> BalTopExcludeStartingWith;
  private HashMap<String, Double> shortAmountValues = new HashMap<>();
  private String townyTownPrefix = "town-";
  private String townyNationPrefix = "nation-";
  private String townyDebtPrefix = "[DEBT]-";
  private String townyClosed_economy = "towny-server";
  private boolean recalculatingTop = false;
  public static final String CMIDefaultWorld = "CMIGlobal";
  HashMap<String, EconomyManager.WorldGroup> groups = new HashMap<>();
  private EconomyManager.WorldGroup defaultGroup = null;
  private VaultManager vmanager = null;
  CMILeaderboard<Double> leaderboard =
      new CMILeaderboard<Double>(1200L, true) {
        protected void onRebuildComplete(Double amount) {
          EconomyManager.this.totalServerMoney = amount;
        }
      };
  private double totalServerMoney = 0.0;
  DecimalFormat defaultFormat;
  DecimalFormat shortDefaultFormat;
  File file = null;
  Writer writer = null;
  boolean checkedOldFile = false;
  private boolean recording = false;

  public EconomyManager(CMI cmi) {
    this.plugin = cmi;
    CMIScheduler.runTaskLater(cmi, this::recheckAll, 60L);
    if (this.plugin.isEnabled()) {
      if (this.recheckEconomyPlugin().equals(VaultManager.EconomySetupResponse.NoVaultEconomy)) {
        cmi.consoleMessage("&6Will attempt secondary economy check after server starts.");
        CMIScheduler.runTask(cmi, this::recheckEconomyPlugin);
      }
    }
  }

  public VaultManager.EconomySetupResponse recheckEconomyPlugin() {
    VaultManager.EconomySetupResponse economySetupResponse =
        VaultManager.EconomySetupResponse.NoVault;
    if (CMIHookType.Vault.isPresent()) {
      this.vmanager = new VaultManager(this.plugin);
      economySetupResponse = this.vmanager.setupVault();
    }

    CMIHookType.Towny.init();
    if (CMIHookType.Towny.isPresent()) {
      File townyDir = new File("plugins" + File.separator + "Towny" + File.separator + "settings");
      if (townyDir.isDirectory()) {
        File townyFile =
            new File(
                "plugins"
                    + File.separator
                    + "Towny"
                    + File.separator
                    + "settings"
                    + File.separator
                    + "config.yml");

        try {
          ConfigReader configReader = new ConfigReader(townyFile);
          this.townyTownPrefix = configReader.getC().getString("economy.town_prefix");
          this.townyNationPrefix = configReader.getC().getString("economy.nation_prefix");
          if (configReader.getC().isString("economy.closed_economy.server_account")) {
            this.townyClosed_economy =
                configReader.getC().getString("economy.closed_economy.server_account");
          }

          if (configReader.getC().isString("economy.debt_prefix")) {
            this.townyDebtPrefix = configReader.getC().getString("economy.debt_prefix");
          }
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    }

    switch (economySetupResponse) {
      case Vault:
      case CMI:
        this.plugin.consoleMessage(
            "&7Vault &3was found - Enabling capabilities. Economy: &5"
                + this.vmanager.getVaultEconomy().getName());
        break;
      case NoVault:
        this.plugin.consoleMessage("&6Vault was not found, you will have limited economy support");
        break;
      case NoVaultEconomy:
        this.plugin.consoleMessage(
            "&cVault was found but not economy plugin, you will have limited economy support");
    }

    try {
      new Economy(this.plugin);
    } catch (NoClassDefFoundError noClassDefFoundError) {
      this.Enabled = false;
      CMIMessages.sendMessage(
          Bukkit.getConsoleSender(),
          this.plugin.prefix + "Cant find Vault plugin for economy compatability");
    }

    return economySetupResponse;
  }

  public CMILeaderboard<Double> getLeaderboard() {
    return this.leaderboard;
  }

  @Deprecated
  public void setForBalTopRecalculation() {
    this.getLeaderboard().rebuild();
  }

  public String format(Double amount) {
    if (amount == null) {
      return "0";
    } else {
      return this.vmanager == null ? String.valueOf(amount) : this.vmanager.format(amount);
    }
  }

  public boolean isVaultEnabled() {
    return this.vmanager != null && this.vmanager.getVaultEconomy() != null;
  }

  @Deprecated
  public void removeFromUpdateCache(UUID uuid) {}

  private synchronized void recheckAll() {
    if (this.BalTopLoadInAll) {
      for (CMIUser cmiUser : this.plugin.getPlayerManager().getAllUsers().values()) {
        this.updateBalTop(cmiUser);
      }

      this.getLeaderboard().rebuild();
    }
  }

  public synchronized void updateBalTop(CMIUser cmiUser) {
    String string = cmiUser.getName(false);
    if (string != null) {
      if ((!cmiUser.isFakeAccount() || this.isBalTopIncludeFakes())
          && !this.startsWithExcluded(string)) {
        if (!this.getBalTopExclude().contains(string.toLowerCase())) {
          if (this.ExcludeInactive <= 0L
              || cmiUser.getLastLogin() + this.ExcludeInactive >= System.currentTimeMillis()) {
            this.getLeaderboard().setValue(cmiUser.getUniqueId(), cmiUser.getBalance());
          }
        }
      }
    }
  }

  @Deprecated
  private void forceUpdateBalTop(CMIUser cmiUser) {
    this.updateBalTop(cmiUser);
    this.getLeaderboard().rebuild();
  }

  @Deprecated
  public void recalculateBalTop() {
    this.getLeaderboard().rebuild();
  }

  private boolean startsWithExcluded(String string) {
    string = string.toLowerCase();

    for (String prefix : this.BalTopExcludeStartingWith) {
      if (string.startsWith(prefix)) {
        return true;
      }
    }

    return false;
  }

  @Deprecated
  public SortedMap<Double, UUID> getBalTopMap() {
    TreeMap<Double, UUID> treeMap = new TreeMap(Collections.reverseOrder());
    var arrayList = new ArrayList<>(this.getLeaderboard().getValues().entrySet());

    for (int value = 0; value < arrayList.size(); value++) {
      Entry entry = (Entry) arrayList.get(value);
      UUID uuid = (UUID) entry.getKey();
      Double balance = (Double) entry.getValue();

      for (int attemptsLeft = 10000;
          treeMap.containsKey(balance) && attemptsLeft > 0;
          attemptsLeft--) {
        balance = balance + 1.0E-7;
      }

      treeMap.put(balance, uuid);
    }

    return treeMap;
  }

  @Deprecated
  public int getBalTopPlace(UUID uuid) {
    int value = this.getLeaderboard().getRank(uuid);
    return value == -1 ? this.plugin.getPlayerManager().getTotalUserCount() : value;
  }

  public Set<EconomyManager.WorldGroup> getWorldGroups() {
    HashSet<com.Zrips.CMI.Modules.Economy.EconomyManager.WorldGroup> hashSet = new HashSet();

    for (var entry : this.groups.entrySet()) {
      if (!this.CustomWorldsEnabled || !((String) entry.getKey()).equals("CMIGlobal")) {
        hashSet.add((EconomyManager.WorldGroup) entry.getValue());
      }
    }

    return hashSet;
  }

  public Set<String> getWorlds(EconomyManager.WorldGroup worldGroup) {
    HashSet<String> hashSet = new HashSet();

    for (var entry : this.groups.entrySet()) {
      if (((EconomyManager.WorldGroup) entry.getValue()).getName().equals(worldGroup.getName())) {
        hashSet.add((String) entry.getKey());
      }
    }

    return hashSet;
  }

  public EconomyManager.WorldGroup getWorldGroup(String string) {
    if (string == null) {
      return this.groups.get("CMIGlobal");
    }

    if (this.groups.containsKey(string)) {
      return this.groups.get(string);
    }

    for (var entry : this.groups.entrySet()) {
      if (((EconomyManager.WorldGroup) entry.getValue()).getName().equals(string)) {
        return (EconomyManager.WorldGroup) entry.getValue();
      }
    }

    return this.groups.get("CMIGlobal");
  }

  public DecimalFormat getMoneyFormat() {
    return this.getMoneyFormat(null);
  }

  public Double translateMoney(String string) {
    if (string != null && !string.isEmpty()) {
      Double parsed = null;
      string = string.replace(this.getDefaultGroup().getCurrencySymbol(), "");
      if (string != null && !string.isEmpty()) {
        try {
          parsed = Double.valueOf(string);
        } catch (Exception exception) {
        }

        if (parsed == null) {
          String suffix = string.substring(string.length() - 1);
          Double multiplier = this.shortAmountValues.get(suffix.toLowerCase());

          try {
            if (multiplier == null) {
              Double.parseDouble(suffix);
            }
          } catch (Throwable throwable) {
            return parsed;
          }

          try {
            String dotText = string.replace(",", ".");
            parsed = Double.parseDouble(dotText.substring(0, dotText.length() - 1));
          } catch (Exception exception2) {
            try {
              String commaText = string.replace(".", ",");
              parsed = Double.parseDouble(commaText.substring(0, commaText.length() - 1));
            } catch (Exception exception3) {
            }
          }

          if (multiplier == null) {
            multiplier = 1.0;
          }

          if (parsed != null) {
            parsed = parsed * multiplier;
          }
        }

        return parsed;
      } else {
        return parsed;
      }
    } else {
      return null;
    }
  }

  public DecimalFormat getShortMoneyFormat() {
    return this.shortDefaultFormat;
  }

  public DecimalFormat getMoneyFormat(String string) {
    if (this.defaultFormat != null) {
      return this.defaultFormat;
    }

    EconomyManager.WorldGroup worldGroup = this.getDefaultGroup();

    try {
      if (worldGroup.isSwitchPlaces()) {
        this.defaultFormat =
            new DecimalFormat(
                worldGroup.getCurrencyFormat(), new DecimalFormatSymbols(Locale.GERMANY));
      } else {
        this.defaultFormat =
            new DecimalFormat(
                worldGroup.getCurrencyFormat(), new DecimalFormatSymbols(Locale.ENGLISH));
      }
    } catch (Exception exception) {
    }

    if (this.defaultFormat != null) {
      this.defaultFormat.setRoundingMode(RoundingMode.FLOOR);
      return this.defaultFormat;
    }

    try {
      this.defaultFormat = new DecimalFormat(worldGroup.getCurrencyFormat());
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      this.plugin.consoleMessage("&cIncorrect economy money format!");
      this.defaultFormat = new DecimalFormat("###,##0.00");
    }

    this.defaultFormat.setRoundingMode(RoundingMode.FLOOR);
    return this.defaultFormat;
  }

  public void loadConfig() {
    this.defaultFormat = null;
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Economy.Enabled",
        new String[] {
          "Enable or disable the CMI economy in general",
          "If CMIInjector is present, this will be set to true automatically",
          "Attention! For economy to work properly with other plugins you will need either an"
              + " injector or a recompiled Vault version.",
          "You can find both option at top of plugins page",
          "ATTENTION! If you disabled CMI economy while server was running, you will need to"
              + " perform a full server restart for this to take effect and avoid issues when"
              + " retrieving players' balances."
        });
    this.Enabled = configReader.get("Economy.Enabled", false);
    configReader.addComment(
        "Economy.Confirmation",
        new String[] {
          "Determines if player needs to confirm money payment by clicking on chat message"
        });
    this.Confirmation = configReader.get("Economy.Confirmation", false);
    configReader.addComment(
        "Economy.OfflinePayments",
        new String[] {
          "Should we allow offline payments. This is primarily for pay command and will not block"
              + " other payments like receiving money from other plugins"
        });
    this.OfflinePayments = configReader.get("Economy.OfflinePayments", true);
    configReader.addComment(
        "Economy.LogEnabled",
        new String[] {"Set to true if you want to log money transfers between players"});
    this.LogEnabled = configReader.get("Economy.LogEnabled", false);
    configReader.addComment(
        "Economy.Log." + EconomyManager.CMIMoneyLogType.Transfer.toString(),
        new String[] {
          "Pick what type you want to record.",
          "Keep in mind that Unknown type will be recorded every time there is money tranfer when"
              + " CMI Economy is in use",
          "While type as Transfer will only record /cmi pay command actions",
          "Some duplicates can accure while having Unknown enabled with other types, as unknown"
              + " record every balance change for the player without knowing who made that change"
        });
    EconomyManager.CMIMoneyLogType[] cmiMoneyLogTypes;
    int value = (cmiMoneyLogTypes = EconomyManager.CMIMoneyLogType.values()).length;

    for (int index = 0; index < value; index++) {
      EconomyManager.CMIMoneyLogType cmiMoneyLogType = cmiMoneyLogTypes[index];
      cmiMoneyLogType.setEnabled(
          configReader.get("Economy.log." + cmiMoneyLogType.toString(), true));
    }

    configReader.addComment(
        "Economy.Log.IgnoredUsers",
        new String[] {
          "Users we should ignore from recording into log file when money is being transfered to"
              + " them"
        });
    this.logIgnoredUsers = configReader.get("Economy.log.IgnoredUsers", Arrays.asList("Zrips"));
    CMIList.toLowerCase(this.logIgnoredUsers);
    configReader.addComment(
        "Economy.Cheque.MaxValue",
        new String[] {
          "Determines max amount of cheque player can create", "Set it to 0 to remove the limit"
        });
    this.MaxChequeValue =
        configReader.get(
            "Economy.Cheque.MaxValue",
            configReader.getC().getDouble("Economy.MaxChequeValue", 1.0E8));
    configReader.addComment(
        "Economy.Cheque.Paper",
        new String[] {
          "If set to true, the player will be required to hold a piece of paper to create a cheque."
        });
    this.ChequePaper = configReader.get("Economy.Cheque.Paper", true);
    configReader.addComment(
        "Economy.Cheque.Permission",
        new String[] {
          "When set to true, the player will be required to have "
              + PermissionsManager.CMIPerm.command_cheque_withdraw.getPermission()
              + " permission node to withdraw cheque"
        });
    this.ChequePermission = configReader.get("Economy.Cheque.Permission", false);
    configReader.addComment(
        "Economy.Cheque.InCreative",
        new String[] {
          "Should we allow cheque usage in creative mode?",
          "If you have creative server, keep this at false or players could start duplicating items"
        });
    this.ChequeInCreative = configReader.get("Economy.Cheque.InCreative", false);
    configReader.addComment(
        "Economy.Cheque.AcceptNotEncoded",
        new String[] {
          "Should we accept old cheque item stacks with non encoded data in them",
          "Keep it set to false for maximum security"
        });
    this.ChequeAcceptNotEncoded = configReader.get("Economy.Cheque.AcceptNotEncoded", false);
    configReader.addComment(
        "Economy.PaymentWithShorts.Allow",
        new String[] {
          "When set to true players will be able to make payments by using short amount formats"
              + " like 10k which results into 10000 and similar",
          "In addition 10.2k will result into 10200"
        });
    boolean flag = configReader.get("Economy.PaymentWithShorts.Allow", true);
    configReader.addComment(
        "Economy.PaymentWithShorts.values",
        new String[] {
          "List of suffixes we should accept and what amount they result in.",
          "Only one letter is acceptable when defining suffix"
        });
    var list =
        configReader.get(
            "Economy.PaymentWithShorts.values",
            Arrays.asList(
                "k-1000", "m-1000000", "b-1000000000", "t-1000000000000", "q-1000000000000000"));
    this.shortAmountValues.clear();
    if (flag) {
      for (String string : list) {
        if (string.contains("-")) {
          String[] strings = string.split("-");
          if (strings[0].length() <= 1) {
            try {
              this.shortAmountValues.put(strings[0].toLowerCase(), Double.parseDouble(strings[1]));
            } catch (Exception exception) {
            }
          }
        }
      }
    }

    configReader.addComment(
        "Economy.BalTop.LoadInAll",
        new String[] {
          "When enabled we will preload all players balances in baltop list on server startup",
          "If you have 3rd party economy plugin this might increase initial load times",
          "If set to false then baltop will only include newly modified players balances and not"
              + " all of them",
          "Keep it disabled if you are not using baltop from CMI"
        });
    this.BalTopLoadInAll = configReader.get("Economy.BalTop.LoadInAll", true);
    configReader.addComment(
        "Economy.BalTop.Exclude", new String[] {"List of players to exclude from baltop list"});
    this.BalTopExclude = configReader.get("Economy.BalTop.Exclude", Arrays.asList("Notch"));

    for (int balTopIndex = 0; balTopIndex < this.BalTopExclude.size(); balTopIndex++) {
      this.BalTopExclude.set(balTopIndex, this.BalTopExclude.get(balTopIndex).toLowerCase());
    }

    configReader.addComment(
        "Economy.BalTop.DisplayWithShorts",
        new String[] {
          "When enabled baltop list will show short money values, like 12.5M instead of 12501234"
        });
    this.BalTopDisplayWithShorts = configReader.get("Economy.BalTop.DisplayWithShorts", false);
    configReader.addComment(
        "Economy.BalTop.ExcludeStartingWith",
        new String[] {
          "List of names to exclude from baltop", "Can be used to filter out towny towns"
        });
    this.BalTopExcludeStartingWith =
        configReader.get(
            "Economy.BalTop.ExcludeStartingWith",
            Arrays.asList("town_", "town-", "towny_", "towny-", "debt-", "debt_"));

    for (int startingIndex = 0;
        startingIndex < this.BalTopExcludeStartingWith.size();
        startingIndex++) {
      this.BalTopExcludeStartingWith.set(
          startingIndex, this.BalTopExcludeStartingWith.get(startingIndex).toLowerCase());
    }

    configReader.addComment(
        "Economy.BalTop.IncludeFakes",
        new String[] {
          "When set to true some fake accounts will be included into baltop list",
          "This should include some previously marked accounts as fake which have been created for"
              + " town usage or similar things"
        });
    this.BalTopIncludeFakes = configReader.get("Economy.BalTop.IncludeFakes", false);
    configReader.addComment(
        "Economy.BalTop.ExcludeInactive",
        new String[] {
          "Set to 0 or lower to disable this feature",
          "Time in days of player being offline to exclude from being included in balance top list",
          "For full effect this will require server restart"
        });
    this.ExcludeInactive = configReader.get("Economy.BalTop.ExcludeInactive", 0);
    if (this.ExcludeInactive > 0L) {
      this.ExcludeInactive = this.ExcludeInactive * 24L * 60L * 60L * 1000L;
    } else {
      this.ExcludeInactive = 0L;
    }

    configReader.addComment(
        "Economy.Global.StartingAmount",
        new String[] {"Starting amount of money players will have"});
    Double startingAmount = configReader.get("Economy.Global.StartingAmount", 100.0);
    configReader.addComment(
        "Economy.Global.MinimalAmount",
        new String[] {"Minimal amount, can go into negative if needed"});
    Double minimalAmount = configReader.get("Economy.Global.MinimalAmount", 0.0);
    configReader.addComment(
        "Economy.Global.MaximumAmount",
        new String[] {"Maximal amount of money player can have. Set to -1 to disable this limit"});
    Double maximumAmount = configReader.get("Economy.Global.MaximumAmount", 1.0E8);
    configReader.addComment(
        "Economy.Global.MinimalPay",
        new String[] {
          "Defines amount until player can send to another player",
          "This can prevent annoying spam some players would want to create by sending tiny amounts"
              + " of money"
        });
    Double minimalPay = configReader.get("Economy.Global.MinimalPay", 0.5);
    configReader.addComment(
        "Economy.Global.Fractions",
        new String[] {
          "Defines if you want to allow payments with fractions",
          "if this is disabled then payments will be limited to whole numbers and in case player"
              + " provides number with fraction we will round number down"
        });
    boolean fractions = configReader.get("Economy.Global.Fractions", true);
    configReader.addComment(
        "Economy.Global.CurrencySymbol",
        new String[] {"Currency symbol to be used when showing balance or similar"});
    String currencySymbol = configReader.get("Economy.Global.CurrencySymbol", "€");
    configReader.addComment(
        "Economy.Global.MoneyFormat", new String[] {"Format used for displaying money"});
    String currencyName =
        configReader.get(
            "Economy.Global.MoneyFormat",
            configReader.getC().getString("Optimizations.MoneyFormat", "###,##0.00"));
    configReader.addComment(
        "Economy.Global.UseShortNumbers",
        new String[] {"Converts long numbers to short ones, like 12305122 to 12.3m"});
    boolean displayFlag =
        configReader.get(
            "Economy.Global.UseShortNumbers",
            configReader.getC().getBoolean("Economy.Global.UseShortNumners", false));
    configReader.addComment(
        "Economy.Global.shortNumbersFormat",
        new String[] {
          "This format is only for amounts which gets defines suffix like '1.23k' while numbers"
              + " lover than 1k will use MoneyFormat"
        });
    String shortNumbersFormat = configReader.get("Economy.Global.shortNumbersFormat", "###,##0.0");

    try {
      this.shortDefaultFormat =
          new DecimalFormat(shortNumbersFormat, new DecimalFormatSymbols(Locale.ENGLISH));
      this.shortDefaultFormat.setRoundingMode(RoundingMode.FLOOR);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    configReader.addComment(
        "Economy.Global.ShortNumbersSuffixes", new String[] {"Suffixes for short numbers"});
    var worldGroupsList =
        configReader.get(
            "Economy.Global.ShortNumbersSuffixes",
            Arrays.asList(
                "",
                "k-1000",
                "m-1000000",
                "b-1000000000",
                "t-1000000000000",
                "q-1000000000000000"));
    HashMap<Double, String> hashMap = new HashMap();
    Double defaultMultiplier = 1.0;

    for (String worldGroupName : worldGroupsList) {
      if (worldGroupName.contains("-")) {
        try {
          hashMap.put(
              Double.parseDouble(worldGroupName.split("-", 2)[1]), worldGroupName.split("-", 2)[0]);
        } catch (Exception exception2) {
        }
      } else {
        hashMap.put(defaultMultiplier, worldGroupName);
        defaultMultiplier = defaultMultiplier * 1000.0;
      }
    }

    configReader.addComment(
        "Economy.Global.SwitchPlaces",
        new String[] {
          "Replaces to western format where decimals are separated by , and thousands by . In"
              + " example 1,000,000.00 changes to 1.000.000,00"
        });
    Boolean enabledFlag =
        configReader.get(
            "Economy.Global.SwitchPlaces",
            configReader.getC().getBoolean("Optimizations.SwitchPlaces", false));
    configReader.addComment("Economy.Global.Placing", new String[] {"Placing of currency symbol"});
    String groupText =
        configReader.get(
            "Economy.Global.Placing",
            !this.plugin.getLM().containsKey("econ.currencyPlacing")
                ? "[money][symbol]"
                : this.plugin.getLM().getDefaultMessage("econ.currencyPlacing"));
    this.defaultGroup =
        this.groups.computeIfAbsent(
            "CMIGlobal", groupKey -> new EconomyManager.WorldGroup("CMIGlobal"));
    CMIEconomyTax cmiEconomyTax = this.defaultGroup.getTax();
    configReader.addComment(
        "Economy.Global.Tax",
        new String[] {
          "Taxing for /pay command usage",
          "This is only for this specific command at the moment and will not tax other actions"
        });
    configReader.addComment(
        "Economy.Global.Tax.Type",
        new String[] {
          "Type of taxing. None, Flat, Percentage",
          "Where flat type will deduct specific amount of money for each pay command usage",
          "Percentage type will deduct percentage of money for each pay command usage based on"
              + " transfer amount"
        });
    cmiEconomyTax.setType(
        CMIEconomyTaxType.get(configReader.get("Economy.Global.Tax.Type", "None")));
    configReader.addComment(
        "Economy.Global.Tax.Amount",
        new String[] {
          "Either flat amount or percentage on transaction amount",
          "Percentage is defined between 0 and 100"
        });
    cmiEconomyTax.setAmount(configReader.get("Economy.Global.Tax.Amount", 4.0));
    configReader.addComment(
        "Economy.Global.Tax.Min",
        new String[] {"Minimal amount to tax player, mostly used when taxing based on percentage"});
    cmiEconomyTax.setMin(configReader.get("Economy.Global.Tax.Min", 0.0));
    configReader.addComment(
        "Economy.Global.Tax.Max",
        new String[] {
          "Maximal amount to tax player, mostly used when taxing based on percentage",
          "Set to 0 if you don't want to have max limit"
        });
    cmiEconomyTax.setMax(configReader.get("Economy.Global.Tax.Max", 1000.0));
    configReader.addComment(
        "Economy.Global.Tax.DeductOnTop",
        new String[] {
          "When set to true tax will be added on top of transfered amount, so if player transfers"
              + " 100 and tax is 10 then receiving player receives 100 and sender player looses"
              + " 110",
          "If its set to false tax will be subtracted from transfered amount, so if player"
              + " transfers 100 and tax is 10 then receiving player receives 90 and sender player"
              + " pays 100",
          "Recommended to keep it at true just to avoid confusion why receiving player got less"
              + " money"
        });
    cmiEconomyTax.setDeductOnTop(configReader.get("Economy.Global.Tax.DeductOnTop", true));
    this.defaultGroup
        .setStartingAmount(startingAmount)
        .setMinimalAmount(minimalAmount)
        .setMaximumAmount(maximumAmount)
        .setCurrencyName(currencySymbol)
        .setCurrencySymbol(currencySymbol)
        .setCurrencyFormat(currencyName)
        .setSwitchPlaces(enabledFlag)
        .setFractions(fractions)
        .setPlacing(groupText)
        .setMinimalPay(minimalPay)
        .setUseShortNumbers(displayFlag)
        .setShortNumbersSuffixes(hashMap);
    this.groups.put("CMIGlobal", this.defaultGroup);
  }

  public boolean isCustomWorldsEnabled() {
    return this.CustomWorldsEnabled;
  }

  public boolean isEnabled() {
    return this.Enabled;
  }

  public void setEnabled(boolean flag) {
    this.Enabled = flag;
  }

  public VaultManager getVaultManager() {
    return this.vmanager;
  }

  public boolean isConfirmation() {
    return this.Confirmation;
  }

  public List<String> getBalTopExclude() {
    return this.BalTopExclude;
  }

  public void setBalTopExclude(List<String> list) {
    this.BalTopExclude = list;
  }

  public Boolean isRecalculatingTop() {
    return this.recalculatingTop;
  }

  public void setRecalculatingTop(boolean flag) {
    this.recalculatingTop = flag;
  }

  public Double getMaxChequeValue() {
    return this.MaxChequeValue;
  }

  public Double getTotalServerMoney() {
    return this.totalServerMoney;
  }

  public EconomyManager.WorldGroup getDefaultGroup() {
    return this.defaultGroup;
  }

  public void closeStream() {
    if (this.writer != null) {
      try {
        this.writer.flush();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }

      try {
        this.writer.close();
      } catch (IOException ioException2) {
        ioException2.printStackTrace();
      }
    }
  }

  public void moneyLog(
      CMIUser cmiUser,
      CMIUser otherUser,
      double number,
      EconomyManager.CMIMoneyLogType cmiMoneyLogType,
      String string) {
    if (this.LogEnabled) {
      if (this.logIgnoredUsers.isEmpty()
          || cmiUser == null
          || cmiUser.getName(false) == null
          || !this.logIgnoredUsers.contains(cmiUser.getName(false).toLowerCase())) {
        if (!this.checkedOldFile) {
          File logFolder = new File(CMI.getInstance().getDataFolder(), "moneyLog");
          if (!logFolder.isDirectory()) {
            logFolder.mkdir();
          }
        }

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd");
        String dateText = simpleDateFormat.format(date);
        boolean flag = false;
        if (this.file != null) {
          if (!this.file.getName().equalsIgnoreCase(dateText + ".yml")) {
            flag = true;
            if (this.writer != null) {
              try {
                this.writer.flush();
                this.writer.close();
              } catch (Throwable throwable) {
              }

              this.writer = null;
            }
          }
        } else {
          flag = true;
        }

        if (flag) {
          this.file =
              new File(
                  this.plugin.getDataFolder(), "moneyLog" + File.separator + dateText + ".yml");
          if (!this.file.exists()) {
            try {
              this.file.createNewFile();
            } catch (IOException ioException) {
              ioException.printStackTrace();
              return;
            }
          }
        }

        if (!this.checkedOldFile) {
          File logFile = new File(this.plugin.getDataFolder(), "moneyLog.yml");
          if (logFile.exists()) {
            File logFileOld =
                new File(
                    this.plugin.getDataFolder(), "moneyLog" + File.separator + "Old_moneyLog.yml");
            logFile.renameTo(logFileOld);
          }

          this.checkedOldFile = true;
        }

        try {
          if (cmiMoneyLogType == null) {
            return;
          }

          if (!cmiMoneyLogType.isEnabled()) {
            return;
          }

          if (this.file == null) {
            this.file =
                new File(
                    this.plugin.getDataFolder(), "moneyLog" + File.separator + dateText + ".yml");
          }

          if (!this.file.exists()) {
            try {
              this.file.createNewFile();
              if (this.writer != null) {
                this.writer.flush();
                this.writer.close();
              }

              this.writer = null;
            } catch (IOException ioException2) {
              ioException2.printStackTrace();
              return;
            }
          }

          if (this.writer == null) {
            try {
              this.writer =
                  new BufferedWriter(
                      new OutputStreamWriter(new FileOutputStream(this.file, true), "UTF-8"));
            } catch (Throwable throwable2) {
              throwable2.printStackTrace();
            }
          }

          try {
            if (!this.plugin.isFullyLoaded()) {
              return;
            }

            if (this.writer == null) {
              return;
            }

            String timeText = DateFormat.MiliToDate(System.currentTimeMillis(), "HH:mm:ss");
            label165:
            switch (cmiMoneyLogType) {
              case Unknown:
                if (cmiUser == null) {
                  return;
                }

                if (string != null) {
                  String logText = string;
                  switch (string.hashCode()) {
                    case -1660487654:
                      if (logText.equals("setBalance")) {
                        this.writer.append(
                            timeText
                                + " "
                                + cmiUser.getName()
                                + " money set to "
                                + this.format(number)
                                + System.lineSeparator());
                      }
                      break label165;
                    case -1075859842:
                      if (logText.equals("Deposit")) {
                        this.writer.append(
                            timeText
                                + " "
                                + cmiUser.getName()
                                + " got "
                                + this.format(number)
                                + " ("
                                + cmiUser.getFormatedBalance()
                                + ")"
                                + (otherUser != null ? " -> " + otherUser.getName() : "")
                                + System.lineSeparator());
                      }
                      break label165;
                    case -875598038:
                      if (logText.equals("Withdraw")) {
                        this.writer.append(
                            timeText
                                + " "
                                + cmiUser.getName()
                                + " lost "
                                + this.format(number)
                                + " ("
                                + cmiUser.getFormatedBalance()
                                + ")"
                                + (otherUser != null ? " -> " + otherUser.getName() : "")
                                + System.lineSeparator());
                      }
                  }
                }
                break;
              case Transfer:
                if (cmiUser == null || otherUser == null) {
                  return;
                }

                this.writer.append(
                    timeText
                        + " "
                        + cmiUser.getName()
                        + "("
                        + cmiUser.getFormatedBalance()
                        + ") got "
                        + this.format(number)
                        + " from "
                        + otherUser.getName()
                        + "("
                        + otherUser.getFormatedBalance()
                        + ")"
                        + System.lineSeparator());
            }

            if (this.writer != null) {
              this.writer.flush();
            }
          } catch (Throwable throwable3) {
            if (this.writer != null) {
              try {
                this.writer.flush();
                this.writer.close();
              } catch (Throwable throwable4) {
              }
            }

            this.writer = null;
          }
        } catch (Exception | Error value) {
          value.printStackTrace();
        }
      }
    }
  }

  public String getTownyTownPrefix() {
    return this.townyTownPrefix;
  }

  public void setTownyTownPrefix(String string) {
    this.townyTownPrefix = string;
  }

  public String getTownyNationPrefix() {
    return this.townyNationPrefix;
  }

  public void setTownyNationPrefix(String string) {
    this.townyNationPrefix = string;
  }

  public boolean isBalTopIncludeFakes() {
    return this.BalTopIncludeFakes;
  }

  public boolean isTownyAccount(String string) {
    return string == null
        ? false
        : CMIHookType.Towny.isPresent()
            && (string.startsWith(this.plugin.getEconomyManager().getTownyTownPrefix())
                || string.startsWith(this.plugin.getEconomyManager().getTownyNationPrefix())
                || string.startsWith(this.plugin.getEconomyManager().getTownyDebtPrefix())
                || string.equalsIgnoreCase(
                    this.plugin.getEconomyManager().getTownyClosedEconomyName()));
  }

  public String getTownyClosedEconomyName() {
    return this.townyClosed_economy;
  }

  public boolean isBalTopDisplayWithShorts() {
    return this.BalTopDisplayWithShorts;
  }

  public String getTownyDebtPrefix() {
    return this.townyDebtPrefix;
  }

  public void setTownyDebtPrefix(String string) {
    this.townyDebtPrefix = string;
  }

  public boolean isChequePaperRequired() {
    return this.ChequePaper;
  }

  public boolean isChequeRequiresPermission() {
    return this.ChequePermission;
  }

  public boolean isChequeInCreative() {
    return this.ChequeInCreative;
  }

  public boolean isChequeAcceptNotEncoded() {
    return this.ChequeAcceptNotEncoded;
  }

  public boolean isOfflinePayments() {
    return this.OfflinePayments;
  }

  public class CMIEconomyResponse {
    private double amount;
    private double balance;
    private EconomyManager.EconResponseType type;

    public CMIEconomyResponse(
        double number, double secondAmount, EconomyManager.EconResponseType econResponseType) {
      this.amount = number;
      this.balance = secondAmount;
      this.type = econResponseType;
    }

    public double getAmount() {
      return this.amount;
    }

    public double getBalance() {
      return this.balance;
    }

    public EconomyManager.EconResponseType getType() {
      return this.type;
    }
  }

  public enum CMIMoneyLogType {
    Unknown,
    Transfer;

    private boolean enabled;

    public boolean isEnabled() {
      return this.enabled;
    }

    public void setEnabled(boolean flag) {
      this.enabled = flag;
    }
  }

  public enum EconResponseType {
    SUCCESS,
    FAILURE,
    NOT_IMPLEMENTED;
  }

  public class WorldGroup {
    private double StartingAmount = 0.0;
    private double MinimalAmount = 0.0;
    private double MaximumAmount = 100.0;
    private double MinimalPay = 0.5;
    private String CurrencyName = "Euro";
    private String CurrencySymbol = "€";
    private String CurrencyFormat = "###,##0.00";
    private String Placing = "[money][symbol]";
    private boolean switchPlaces = false;
    private boolean UseShortNumbers = false;
    private boolean fractions = true;
    private HashMap<Double, String> ShortNumbersSuffixes = new HashMap<>();
    private String name;
    private CMIEconomyTax tax = new CMIEconomyTax(CMIEconomyTaxType.None, 0.0);

    public WorldGroup(String string) {
      this.name = string;
    }

    public Double getStartingAmount() {
      return this.StartingAmount;
    }

    public EconomyManager.WorldGroup setStartingAmount(Double amount) {
      this.StartingAmount = amount;
      return this;
    }

    public Double getMinimalAmount() {
      return this.MinimalAmount;
    }

    public EconomyManager.WorldGroup setMinimalAmount(Double amount) {
      this.MinimalAmount = amount;
      return this;
    }

    public Double getMaximumAmount() {
      return this.MaximumAmount;
    }

    public EconomyManager.WorldGroup setMaximumAmount(Double amount) {
      if (amount == -1.0) {
        this.MaximumAmount = Double.MAX_VALUE;
      } else {
        this.MaximumAmount = amount;
      }

      return this;
    }

    public String getCurrencyName() {
      return this.CurrencyName;
    }

    public EconomyManager.WorldGroup setCurrencyName(String string) {
      this.CurrencyName = string;
      return this;
    }

    public String getCurrencySymbol() {
      return this.CurrencySymbol;
    }

    public EconomyManager.WorldGroup setCurrencySymbol(String string) {
      this.CurrencySymbol = string;
      return this;
    }

    public String getName() {
      return this.name;
    }

    public void setName(String string) {
      this.name = string;
    }

    public String getCurrencyFormat() {
      return this.CurrencyFormat;
    }

    public EconomyManager.WorldGroup setCurrencyFormat(String string) {
      this.CurrencyFormat = string;
      return this;
    }

    public boolean isSwitchPlaces() {
      return this.switchPlaces;
    }

    public EconomyManager.WorldGroup setSwitchPlaces(boolean flag) {
      this.switchPlaces = flag;
      return this;
    }

    public String getPlacing() {
      return this.Placing;
    }

    public EconomyManager.WorldGroup setPlacing(String string) {
      this.Placing = string;
      return this;
    }

    public boolean isUseShortNumbers() {
      return this.UseShortNumbers;
    }

    public EconomyManager.WorldGroup setUseShortNumbers(boolean flag) {
      this.UseShortNumbers = flag;
      return this;
    }

    public HashMap<Double, String> getShortNumbersSuffixes() {
      return this.ShortNumbersSuffixes;
    }

    public EconomyManager.WorldGroup setShortNumbersSuffixes(HashMap<Double, String> hashMap) {
      this.ShortNumbersSuffixes = hashMap;
      return this;
    }

    public Double getMinimalPay() {
      return this.MinimalPay;
    }

    public EconomyManager.WorldGroup setMinimalPay(Double amount) {
      this.MinimalPay = amount;
      return this;
    }

    public boolean isFractions() {
      return this.fractions;
    }

    public EconomyManager.WorldGroup setFractions(boolean flag) {
      this.fractions = flag;
      return this;
    }

    public CMIEconomyTax getTax() {
      return this.tax;
    }

    public void setTax(CMIEconomyTax cmiEconomyTax) {
      this.tax = cmiEconomyTax;
    }
  }
}
