package com.Zrips.CMI.Modules.Kits;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIKitUsage;
import com.Zrips.CMI.Containers.CMIPaperItemSerializator;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIShulkerBox;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.commands.list.homes;
import com.Zrips.CMI.commands.list.kit;
import com.Zrips.CMI.commands.list.kiteditor;
import com.Zrips.CMI.events.CMIUserKitAcquireEvent;
import com.Zrips.CMI.utils.Util;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Chat.ChatMessageEdit;
import net.Zrips.CMILib.Chat.ChatMessageListEdit;
import net.Zrips.CMILib.Chat.ChatMessageListEdit.ChatEditType;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIButtonLocation;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.GUI.GUIManager.InvType;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMC;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.RawMessages.RawMessageCommand;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class KitsManager {
  private LinkedHashMap<String, Kit> map = new LinkedHashMap<>();
  private static HashMap<UUID, Clickery> clickeryMap = new HashMap<>();
  private static HashMap<UUID, List<ItemStack>> cloneCache = new HashMap<>();
  private String NewbieKitName;
  private Kit NewbieKit;
  private CMI plugin;
  private boolean KitsGUI = true;
  private boolean KitsGUIFillEmptyFields = true;
  private boolean allowKitPreview = true;
  private String dateFormat = "yyyy-MM-dd HH:mm:ss";
  private boolean guiCloseButton;
  private int guiCloseButtonSlot;
  private CMIItemStack guiCloseButtonItem;
  private List<String> guiCloseButtonCommands;
  private boolean guiInfoButton;
  private int guiInfoButtonSlot;
  private CMIItemStack guiInfoButtonItem;
  private List<String> guiInfoButtonCommands;
  private static ConcurrentHashMap<UUID, LinkedHashMap<String, CMIKitUsage>> kitsData =
      new ConcurrentHashMap<>();
  private String fileName = "Kits.yml";
  private CMITask saveTask = null;
  private static int min = 36;

  public void clearCache(UUID uuid) {
    clickeryMap.remove(uuid);
    cloneCache.remove(uuid);
  }

  public void onDisable() {
    if (this.saveTask != null) {
      this.saveTask.cancel();
      this.saveTask = null;
      this.save();
    }
  }

  public KitsManager(CMI cmi) {
    this.plugin = cmi;
  }

  public static LinkedHashMap<String, CMIKitUsage> getUserKits(UUID uuid) {
    return kitsData.computeIfAbsent(uuid, playerUuid -> new LinkedHashMap<>());
  }

  @Nullable
  public static HashMap<String, Object> getPlayersKitsDataForSave(@Nonnull UUID uuid) {
    if (kitsData.get(uuid) == null) {
      return null;
    }

    var linkedHashMap = getUserKits(uuid);
    HashMap<String, Object> hashMap = new HashMap<>();

    for (CMIKitUsage cmiKitUsage : linkedHashMap.values()) {
      HashMap<String, Long> usageData = new HashMap();
      usageData.put("t", cmiKitUsage.getLastUsage());
      if (cmiKitUsage.getUsedTimes() > 0) {
        usageData.put("u", (long) cmiKitUsage.getUsedTimes());
      }

      hashMap.put(cmiKitUsage.getKitName(), usageData);
    }

    return hashMap;
  }

  public void loadConfig(ConfigReader configReader) {
    configReader.addComment(
        "Newbie.Kit", new String[] {"Kit name to give for new players joining server"});
    this.NewbieKitName = configReader.get("Newbie.Kit", "Newbie");
    configReader.addComment(
        "Kits.GUI",
        new String[] {"When set to true, kit list will be shown in GUI instead of chat list"});
    this.KitsGUI = configReader.get("Kits.GUI", true);
    configReader.addComment(
        "Kits.FillEmptyFields",
        new String[] {
          "When set to true, kit selection gui empty fields will get filled with definet item"
        });
    this.KitsGUIFillEmptyFields = configReader.get("Kits.FillEmptyFields", true);
    configReader.addComment(
        "Kits.KitPreview",
        new String[] {
          "When enabled players can preview kits they have access to or have"
              + " cmi.kit.[kitName].preview or cmi.kit.[kitName].* permission node"
        });
    this.allowKitPreview = configReader.get("Kits.KitPreview", true);
    configReader.addComment(
        "Kits.DateFormat", new String[] {"Defines how {DATE} value will be formatted"});
    this.dateFormat = configReader.get("Kits.DateFormat", "yyyy-MM-dd HH:mm:ss");
    configReader.addComment(
        "Kits.Buttons.Cooldown",
        new String[] {
          "Icons for kit preview gui", "Setting icon to invalid item will hide button"
        });
    KitsManager.CMIKitGUILayout.Delay.setIcon(
        CMILib.getInstance()
            .getItemManager()
            .getItem(configReader.get("Kits.Buttons.Cooldown", "Watch")));
    KitsManager.CMIKitGUILayout.Usages.setIcon(
        CMILib.getInstance()
            .getItemManager()
            .getItem(configReader.get("Kits.Buttons.Usages", "STONE_PLATE")));
    KitsManager.CMIKitGUILayout.Money.setIcon(
        CMILib.getInstance()
            .getItemManager()
            .getItem(configReader.get("Kits.Buttons.Money", "GOLD_INGOT")));
    KitsManager.CMIKitGUILayout.Exp.setIcon(
        CMILib.getInstance()
            .getItemManager()
            .getItem(configReader.get("Kits.Buttons.Exp", "EXP_BOTTLE")));
    KitsManager.CMIKitGUILayout.Description.setIcon(
        CMILib.getInstance()
            .getItemManager()
            .getItem(configReader.get("Kits.Buttons.Desc", "GreenWool")));
    KitsManager.CMIKitGUILayout.Back.setIcon(
        CMILib.getInstance()
            .getItemManager()
            .getItem(configReader.get("Kits.Buttons.Back", "Fence")));
    String string = "Kits.Complex.CloseButton.";
    this.guiCloseButton = configReader.get(string + "Use", true);
    this.guiCloseButtonSlot = configReader.get(string + "Slot", 9) - 1;
    this.guiCloseButtonItem =
        CMILib.getInstance()
            .getItemManager()
            .getItem(
                configReader.get(
                    string + "Material",
                    "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNjYmY5ODgzZGQzNTlmZGYyMzg1YzkwYTQ1OWQ3Mzc3NjUzODJlYzQxMTdiMDQ4OTVhYzRkYzRiNjBmYyJ9fX0="));
    this.guiCloseButtonCommands = configReader.get(string + "Commands", Arrays.asList("closeinv!"));
    string = "Kits.Complex.InfoButton.";
    configReader.addComment(
        string + "Use",
        new String[] {
          "Extra button to be used in case you want to provide any aditional information when"
              + " clicking on it"
        });
    this.guiInfoButton = configReader.get(string + "Use", false);
    this.guiInfoButtonSlot = configReader.get(string + "Slot", 1) - 1;
    this.guiInfoButtonItem =
        CMILib.getInstance()
            .getItemManager()
            .getItem(
                configReader.get(
                    string + "Material",
                    "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDZiYTYzMzQ0ZjQ5ZGQxYzRmNTQ4OGU5MjZiZjNkOWUyYjI5OTE2YTZjNTBkNjEwYmI0MGE1MjczZGM4YzgyIn19fQ=="));
    this.guiInfoButtonCommands = configReader.get(string + "Commands", Arrays.asList("closeinv!"));
  }

  public void addKit(Kit kit) {
    this.map.put(kit.getConfigName().toLowerCase(), kit);
  }

  public boolean removeKit(String string) {
    return this.map.remove(string.toLowerCase()) != null;
  }

  public void renameKitConfigName(Kit kit, String string) {
    this.map.remove(kit.getConfigName().toLowerCase());
    kit.setName(string);
    this.map.put(kit.getConfigName().toLowerCase(), kit);
    this.save();
  }

  public void renameKitCommandName(Kit kit, String string) {
    kit.setCommandName(string);
    this.save();
  }

  public HashMap<String, List<Kit>> getValidKitsForPlayer(Player player, boolean flag) {
    HashMap<String, List<Kit>> hashMap = new HashMap<>();
    HashMap<String, ArrayList> kitsByGroup = new HashMap();
    boolean previewAll = PermissionsManager.CMIPerm.kit_$1_preview.hasPermission(player, "*");
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    boolean bypassOneTimeUse =
        PermissionsManager.CMIPerm.kit_bypass_onetimeuse.hasPermission(player);

    for (var entry : this.getKitMap().entrySet()) {
      if (((Kit) entry.getValue()).isEnabled()) {
        List<Kit> list = new ArrayList();
        if (hashMap.containsKey(((Kit) entry.getValue()).getGroup())) {
          list = (List) hashMap.get(((Kit) entry.getValue()).getGroup());
        }

        if ((cmiUser.getLeftKitUseTimes((Kit) entry.getValue()) != 0
                || bypassOneTimeUse
                || ((Kit) entry.getValue()).isShowDespiteUsage())
            && (PermissionsManager.CMIPerm.kit_$1.hasPermission(
                    player, ((Kit) entry.getValue()).getConfigName().toLowerCase())
                || flag
                    && (!flag
                        || PermissionsManager.CMIPerm.kit_$1_preview.hasPermission(
                            player, ((Kit) entry.getValue()).getConfigName().toLowerCase()))
                || previewAll)) {
          list.add((Kit) entry.getValue());
          hashMap.put(((Kit) entry.getValue()).getGroup(), list);
        }
      }
    }

    for (Entry groupEntry : hashMap.entrySet()) {
      List<Kit> groupKits = (List<Kit>) groupEntry.getValue();
      int value = 0;

      for (Kit kit : groupKits) {
        if (value < kit.getWeight()) {
          value = kit.getWeight();
        }
      }

      ArrayList<Kit> arrayList = new ArrayList();

      for (Kit groupKit : groupKits) {
        if (groupKit.getWeight() == value
            || groupKit.isShowDespiteWeight()
            || groupKit.getGroup() == null) {
          arrayList.add(groupKit);
        }
      }

      kitsByGroup.put((String) groupEntry.getKey(), arrayList);
    }

    hashMap.clear();

    for (Entry sortedEntry : kitsByGroup.entrySet()) {
      TreeMap<Double, Kit> treeMap = new TreeMap();

      for (Kit weightedKit : (List<Kit>) sortedEntry.getValue()) {
        Double weightKey = (double) weightedKit.getWeight();

        while (treeMap.containsKey(weightKey)) {
          weightKey = weightKey + 1.0E-4;
        }

        treeMap.put(weightKey, weightedKit);
      }

      ArrayList<Kit> sortedKits = new ArrayList();

      for (Entry treeEntry : treeMap.entrySet()) {
        sortedKits.add((Kit) treeEntry.getValue());
      }

      hashMap.put((String) sortedEntry.getKey(), sortedKits);
    }

    return hashMap;
  }

  public Kit getKit(Player player, String string) {
    return this.getKit(player, string, false);
  }

  public Kit getKit(Player player, String string, boolean flag) {
    return this.getKit(player, string, flag, false);
  }

  public Kit getKit(Player player, String string, boolean flag, boolean allowPreview) {
    Kit kit = null;

    for (var entry : this.map.entrySet()) {
      if (((Kit) entry.getValue()).getCommandName().equalsIgnoreCase(string)) {
        kit = (Kit) entry.getValue();
        break;
      }
    }

    if (kit == null) {
      for (var kitEntry : this.map.entrySet()) {
        if (((Kit) kitEntry.getValue()).getConfigName().equalsIgnoreCase(string)) {
          kit = (Kit) kitEntry.getValue();
          break;
        }
      }

      if (kit == null) {
        return null;
      }
    }

    var list = this.getKitsByCommandName(kit);
    kit = null;
    boolean previewAll = PermissionsManager.CMIPerm.kit_$1_preview.hasPermission(player, "*");

    for (Kit candidateKit : list) {
      if ((flag
              || PermissionsManager.CMIPerm.kit_$1.hasPermission(
                  player, candidateKit.getConfigName().toLowerCase())
              || allowPreview
                  && (PermissionsManager.CMIPerm.kit_$1_preview.hasPermission(
                          player, candidateKit.getConfigName().toLowerCase())
                      || previewAll))
          && (kit == null || !allowPreview && candidateKit.getWeight() > kit.getWeight())) {
        kit = candidateKit;
      }
    }

    for (Kit groupedKit : this.getGroupedKits(kit)) {
      if (!allowPreview
          && kit != null
          && groupedKit.getWeight() > kit.getWeight()
          && PermissionsManager.CMIPerm.kit_$1.hasPermission(
              player, groupedKit.getConfigName().toLowerCase())) {
        kit = groupedKit;
      }
    }

    return kit != null
            && !kit.getCommandName().equalsIgnoreCase(string)
            && !kit.getConfigName().equalsIgnoreCase(string)
        ? null
        : kit;
  }

  public Kit getKit(String string) {
    return this.getKit(string, true);
  }

  public Kit getKit(String string, boolean flag) {
    if (string == null) {
      return null;
    } else {
      Kit kit = this.map.get(string.toLowerCase());
      if (kit == null || kit.isEnabled()) {
        return kit;
      } else {
        return flag ? kit : null;
      }
    }
  }

  public List<Kit> getKitsByCommandName(Kit kit) {
    ArrayList<Kit> arrayList = new ArrayList();

    for (var entry : this.map.entrySet()) {
      if (((Kit) entry.getValue()).getCommandName().equalsIgnoreCase(kit.getCommandName())) {
        arrayList.add((Kit) entry.getValue());
      }
    }

    return arrayList;
  }

  public List<Kit> getGroupedKits(String string) {
    return this.getGroupedKits(this.getKit(string));
  }

  public List<Kit> getGroupedKits(Kit kit) {
    ArrayList<Kit> arrayList = new ArrayList();
    if (kit != null && kit.getGroup() != null) {
      for (var entry : this.map.entrySet()) {
        if (((Kit) entry.getValue()).getGroup() != null
            && ((Kit) entry.getValue()).getGroup().equalsIgnoreCase(kit.getGroup())) {
          arrayList.add((Kit) entry.getValue());
        }
      }

      return arrayList;
    } else {
      return arrayList;
    }
  }

  public LinkedHashMap<String, Kit> getKitMap() {
    return this.map;
  }

  public void setKits(LinkedHashMap<String, Kit> linkedHashMap) {
    this.map = linkedHashMap;
  }

  public void load() {
    Long startTime = System.currentTimeMillis();
    this.plugin.safeRenameFile("kits.yml", "Kits" + File.separator + this.fileName);
    this.plugin.safeRenameFile(
        "Saves" + File.separator + this.fileName, "Kits" + File.separator + this.fileName);
    File file = new File(CMI.getInstance().getDataFolder(), "Kits");
    ArrayList<String> arrayList = new ArrayList();

    for (var entry : this.map.entrySet()) {
      arrayList.add((String) entry.getKey());
    }

    int value = 0;
    if (file.isDirectory()) {
      File[] files;
      int fileCount = (files = file.listFiles()).length;

      for (int fileIndex = 0; fileIndex < fileCount; fileIndex++) {
        File kitFile = files[fileIndex];
        if (kitFile.exists() && kitFile.getName().endsWith(".yml")) {
          Util.fixItemStackFile(kitFile);
        }
      }

      fileCount = (files = file.listFiles()).length;

      continueSecond:
      for (int fileIndex = 0; fileIndex < fileCount; fileIndex++) {
        File kitFile = files[fileIndex];
        if (!kitFile.exists()) {
          try {
            kitFile.createNewFile();
          } catch (IOException ioException) {
            ioException.printStackTrace();
          }

          return;
        }

        if (kitFile.getName().endsWith(".yml")) {
          YamlConfiguration yamlConfiguration = null;

          try {
            yamlConfiguration = YamlConfiguration.loadConfiguration(kitFile);
          } catch (Exception exception) {
            CMIMessages.consoleMessage("Could not load kits file " + kitFile.getName());
            continue;
          }

          if (!yamlConfiguration.getKeys(false).isEmpty()) {
            var kitRootValues = yamlConfiguration.getValues(false);
            Iterator iterator = kitRootValues.entrySet().iterator();

            while (true) {
              Kit kit;
              while (true) {
                if (!iterator.hasNext()) {
                  value++;
                  continue continueSecond;
                }

                Entry kitEntry = (Entry) iterator.next();
                kit = this.getKit((String) kitEntry.getKey(), true);
                if (kit == null) {
                  kit = new Kit((String) kitEntry.getKey());
                } else {
                  kit.reset();
                }

                try {
                  var kitValues =
                      yamlConfiguration
                          .getConfigurationSection((String) kitEntry.getKey())
                          .getValues(false);
                  if (kitValues.containsKey("CommandName")) {
                    kit.setCommandName((String) kitValues.get("CommandName"));
                  }

                  if (kitValues.containsKey("DisplayName")) {
                    kit.setDisplayName((String) kitValues.get("DisplayName"));
                  }

                  if (kitValues.containsKey("Weight")) {
                    kit.setWeight((Integer) kitValues.get("Weight"));
                  }

                  if (kitValues.containsKey("Delay")) {
                    kit.setDelay(
                        kitValues.get("Delay") instanceof Integer
                            ? ((Integer) kitValues.get("Delay")).intValue()
                            : (Long) kitValues.get("Delay"));
                  }

                  if (kitValues.containsKey("Enabled")) {
                    kit.setEnabled((Boolean) kitValues.get("Enabled"));
                  }

                  if (kitValues.containsKey("DropOnGround")) {
                    kit.setDropItems((Boolean) kitValues.get("DropOnGround"));
                  }

                  if (kitValues.containsKey("ShowDespiteWeight")) {
                    kit.setShowDespiteWeight((Boolean) kitValues.get("ShowDespiteWeight"));
                  }

                  if (kitValues.containsKey("MoneyCost")) {
                    if (kitValues.get("MoneyCost") instanceof Double) {
                      kit.setCost((Double) kitValues.get("MoneyCost"));
                    } else {
                      kit.setCost(((Integer) kitValues.get("MoneyCost")).intValue());
                    }
                  }

                  if (kitValues.containsKey("ExpCost")) {
                    kit.setExpCost((Integer) kitValues.get("ExpCost"));
                  }

                  if (kitValues.containsKey("MaxUsages")) {
                    kit.setMaxUsages((Integer) kitValues.get("MaxUsages"));
                  }

                  if (kitValues.containsKey("ShowWithoutUsages")) {
                    kit.setShowDespiteUsage((Boolean) kitValues.get("ShowWithoutUsages"));
                  }

                  if (kit.getDelay() < 0L && kit.getMaxUsages() == 1) {
                    kit.setDelay(0L);
                  }

                  if (kitValues.containsKey("Slots")) {
                    kit.setSlot((Integer) kitValues.get("Slots"));
                  }

                  if (kitValues.containsKey("Page")) {
                    kit.setPage((Integer) kitValues.get("Page"));
                  }

                  if (kitValues.containsKey("Icon")) {
                    kit.setIcon(CMIPaperItemSerializator.getItem(kitValues.get("Icon")));
                  }

                  if (kitValues.containsKey("IconOff")) {
                    kit.setIconOff(CMIPaperItemSerializator.getItem(kitValues.get("IconOff")));
                  }

                  if (kitValues.containsKey("Commands")) {
                    kit.setCommands((List<String>) kitValues.get("Commands"));
                  }

                  if (kitValues.containsKey("Conditions")) {
                    kit.setConditions((List<String>) kitValues.get("Conditions"));
                  }

                  if (kitValues.containsKey("Description")) {
                    kit.setDescription((List<String>) kitValues.get("Description"));
                  }

                  if (kitValues.containsKey("Group")) {
                    kit.setGroup((String) kitValues.get("Group"));
                  }

                  if (kitValues.containsKey("Items")) {
                    parseItems(
                        yamlConfiguration.getConfigurationSection(
                            (String) kitEntry.getKey() + ".Items"),
                        kit);
                  }
                  break;
                } catch (Exception exception2) {
                  CMIMessages.sendMessage(
                      Bukkit.getConsoleSender(),
                      "Cant load kit (" + (String) kitEntry.getKey() + ")");
                  exception2.printStackTrace();
                }
              }

              kit.setFileName(kitFile.getName().split("\\.", 2)[0]);
              this.map.put(kit.getConfigName().toLowerCase(), kit);
              arrayList.remove(kit.getConfigName().toLowerCase());
            }
          }
        }
      }

      for (String string : arrayList) {
        this.map.remove(string);
      }

      this.plugin.loadMessage(this.map.size(), "kits", System.currentTimeMillis() - startTime);
    }
  }

  private static void parseItems(ConfigurationSection configurationSection, Kit kit) {
    for (String string : configurationSection.getKeys(false)) {
      Object object = configurationSection.get(string);
      ItemStack itemStack = CMIPaperItemSerializator.getItem(object);
      if (itemStack == null) {
        itemStack = configurationSection.getItemStack(string);
      }

      try {
        String slotName = string;
        switch (string.hashCode()) {
          case -2137067379:
            if (slotName.equals("Helmet")) {
              kit.setExtraItem(CMIPlayerInventory.CMIInventorySlot.Helmet, itemStack);
              continue;
            }
            break;
          case 2364485:
            if (slotName.equals("Legs")) {
              kit.setExtraItem(CMIPlayerInventory.CMIInventorySlot.Pants, itemStack);
              continue;
            }
            break;
          case 64369569:
            if (slotName.equals("Boots")) {
              kit.setExtraItem(CMIPlayerInventory.CMIInventorySlot.Boots, itemStack);
              continue;
            }
            break;
          case 65074913:
            if (slotName.equals("Chest")) {
              kit.setExtraItem(CMIPlayerInventory.CMIInventorySlot.ChestPlate, itemStack);
              continue;
            }
            break;
          case 114960990:
            if (slotName.equals("OffHand")) {
              kit.setExtraItem(CMIPlayerInventory.CMIInventorySlot.OffHand, itemStack);
              continue;
            }
        }

        int value = -1;

        try {
          value = Integer.parseInt(string) - 1;
        } catch (Exception exception) {
        }

        if (value != -1) {
          kit.setItem(value, itemStack);
        } else if (itemStack != null) {
          kit.addItem(itemStack);
        }
      } catch (Exception exception2) {
        exception2.printStackTrace();
      }
    }
  }

  public void safeSave() {
    if (this.saveTask == null) {
      this.saveTask =
          CMIScheduler.runLaterAsync(
              this.plugin,
              () -> {
                this.save();
                this.saveTask = null;
              },
              20L);
    }
  }

  public void save() {
    if (this.saveTask != null) {
      this.saveTask.cancel();
      this.saveTask = null;
    }

    var set = this.map.values().stream().map(Kit::getFileName).collect(Collectors.toSet());
    File file = new File(CMI.getInstance().getDataFolder(), "Kits");
    File[] files;
    int value = (files = file.listFiles()).length;

    for (int fileIndex = 0; fileIndex < value; fileIndex++) {
      File staleFile = files[fileIndex];
      if (staleFile.getName().endsWith(".yml")
          && !set.contains(staleFile.getName().split("\\.")[0])) {
        try {
          new ConfigReader(this.plugin, "Kits" + File.separator + staleFile.getName())
              .saveToBackup(false);
        } catch (Exception exception) {
          exception.printStackTrace();
          continue;
        }

        staleFile.delete();
      }
    }

    for (String string : set) {
      File kitFile =
          new File(this.plugin.getDataFolder(), "Kits" + File.separator + string + ".yml");
      if (!kitFile.exists()) {
        try {
          kitFile.createNewFile();
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }
      }

      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(kitFile);

      for (String kitKey : yamlConfiguration.getKeys(false)) {
        yamlConfiguration.set(kitKey, null);
      }

      for (var entry : this.map.entrySet()) {
        if (((Kit) entry.getValue()).getFileName().equals(string)) {
          String configKey = ((Kit) entry.getValue()).getConfigName().replace(".", "_");
          Kit kit = (Kit) entry.getValue();
          if (yamlConfiguration.isConfigurationSection(configKey)) {
            yamlConfiguration.set(configKey, null);
          }

          if (!kit.getDescription().isEmpty()) {
            yamlConfiguration.set(configKey + ".Description", kit.getDescription());
          } else {
            yamlConfiguration.set(configKey + ".Description", null);
          }

          yamlConfiguration.set(configKey + ".CommandName", kit.getCommandName());
          if (!kit.getDisplayName().equals(kit.getCommandName())) {
            yamlConfiguration.set(configKey + ".DisplayName", kit.getDisplayName());
          }

          yamlConfiguration.set(
              configKey + ".Weight", kit.getWeight() > 0 ? kit.getWeight() : null);
          if (!kit.isEnabled()) {
            yamlConfiguration.set(configKey + ".Enabled", kit.isEnabled());
          }

          if (!kit.isDropItems()) {
            yamlConfiguration.set(configKey + ".DropOnGround", kit.isDropItems());
          }

          if (kit.isShowDespiteWeight()) {
            yamlConfiguration.set(configKey + ".ShowDespiteWeight", kit.isShowDespiteWeight());
          }

          if (kit.getDelay() > 0L) {
            yamlConfiguration.set(configKey + ".Delay", kit.getDelay());
          }

          if (kit.getMaxUsages() > 0) {
            yamlConfiguration.set(configKey + ".MaxUsages", kit.getMaxUsages());
          }

          if (kit.isShowDespiteUsage()) {
            yamlConfiguration.set(configKey + ".ShowWithoutUsages", kit.isShowDespiteUsage());
          }

          if (kit.getIcon() != null) {
            CMIPaperItemSerializator.setTo(yamlConfiguration, configKey + ".Icon", kit.getIcon());
          }

          if (kit.getIconOff() != null) {
            CMIPaperItemSerializator.setTo(
                yamlConfiguration, configKey + ".IconOff", kit.getIconOff());
          }

          if (kit.getCost() > 0.0) {
            yamlConfiguration.set(configKey + ".MoneyCost", kit.getCost());
          } else {
            yamlConfiguration.set(configKey + ".MoneyCost", null);
          }

          if (kit.getExpCost() > 0.0) {
            yamlConfiguration.set(configKey + ".ExpCost", kit.getExpCost());
          } else {
            yamlConfiguration.set(configKey + ".ExpCost", null);
          }

          if (kit.getSlot() != null) {
            yamlConfiguration.set(configKey + ".Slots", kit.getSlot());
          } else {
            yamlConfiguration.set(configKey + ".Slots", null);
          }

          if (kit.getPage() != null) {
            yamlConfiguration.set(configKey + ".Page", kit.getPage());
          } else {
            yamlConfiguration.set(configKey + ".Page", null);
          }

          if (!kit.getCommands().isEmpty()) {
            yamlConfiguration.set(configKey + ".Commands", kit.getCommands());
          } else {
            yamlConfiguration.set(configKey + ".Commands", null);
          }

          if (!kit.getConditions().isEmpty()) {
            yamlConfiguration.set(configKey + ".Conditions", kit.getConditions());
          } else {
            yamlConfiguration.set(configKey + ".Conditions", null);
          }

          if (kit.getGroup() != null) {
            yamlConfiguration.set(configKey + ".Group", kit.getGroup());
          } else {
            yamlConfiguration.set(configKey + ".Group", null);
          }

          int itemSlot = 0;
          yamlConfiguration.set(configKey + ".Items", null);

          for (ItemStack itemStack : kit.getItems()) {
            itemSlot++;
            if (itemStack != null) {
              CMIPaperItemSerializator.setTo(
                  yamlConfiguration, configKey + ".Items." + itemSlot, itemStack);
            }
          }

          if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Helmet) != null) {
            CMIPaperItemSerializator.setTo(
                yamlConfiguration,
                configKey + ".Items.Helmet",
                kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Helmet));
          }

          if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.ChestPlate) != null) {
            CMIPaperItemSerializator.setTo(
                yamlConfiguration,
                configKey + ".Items.Chest",
                kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.ChestPlate));
          }

          if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Pants) != null) {
            CMIPaperItemSerializator.setTo(
                yamlConfiguration,
                configKey + ".Items.Legs",
                kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Pants));
          }

          if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Boots) != null) {
            CMIPaperItemSerializator.setTo(
                yamlConfiguration,
                configKey + ".Items.Boots",
                kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Boots));
          }

          if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.OffHand) != null) {
            CMIPaperItemSerializator.setTo(
                yamlConfiguration,
                configKey + ".Items.OffHand",
                kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.OffHand));
          }
        }
      }

      try {
        yamlConfiguration.save(kitFile);
      } catch (IOException ioException2) {
        ioException2.printStackTrace();
      }
    }
  }

  private static Material getMaterial(String string) {
    try {
      int value = -1;
      if (string.contains(":")) {
        value = Integer.parseInt(string.split(":")[0]);
      } else {
        value = Integer.parseInt(string);
      }

      return CMIMaterial.get(value).getMaterial();
    } catch (NumberFormatException numberFormatException) {
      if (string.contains(":")) {
        string = string.split(":")[0].replace("_", "");
        Material[] materials;
        int materialCount = (materials = Material.values()).length;

        for (int materialIndex = 0; materialIndex < materialCount; materialIndex++) {
          Material material = materials[materialIndex];
          if (material.name().replace("_", "").equalsIgnoreCase(string)) {
            return material;
          }
        }
      } else {
        string = string.replace("_", "");
        Material[] materialValues;
        int materialCount = (materialValues = Material.values()).length;

        for (int materialIndex = 0; materialIndex < materialCount; materialIndex++) {
          Material material = materialValues[materialIndex];
          if (material.name().replace("_", "").equalsIgnoreCase(string)) {
            return material;
          }
        }
      }

      return null;
    }
  }

  private static Integer getData(String string) {
    if (!string.contains(":")) {
      return 0;
    }

    String dataText = string.split(":")[1].replace("_", "");

    try {
      return Integer.parseInt(dataText);
    } catch (NumberFormatException numberFormatException) {
      if (CMIMaterial.SPAWNER.equals(getMaterial(string))) {
        EntityType[] entityTypes;
        int value = (entityTypes = EntityType.values()).length;

        for (int typeIndex = 0; typeIndex < value; typeIndex++) {
          EntityType entityType = entityTypes[typeIndex];
          if (entityType.name().replace("_", "").equalsIgnoreCase(dataText)) {
            return Integer.valueOf(entityType.getTypeId());
          }
        }
      }

      return 0;
    }
  }

  public ItemStack updateItemStackLore(ItemStack itemStack, Player player, Kit kit) {
    if (itemStack == null) {
      return itemStack;
    }

    try {
      CMIMaterial cmiMaterial = CMIMaterial.get(itemStack);
      if (cmiMaterial.containsCriteria(CMIMC.SHULKERBOX)) {
        Inventory inventory = CMIShulkerBox.getShulkerInv(itemStack);
        ItemStack[] itemStacks = inventory.getContents();

        for (int value = 0; value < itemStacks.length; value++) {
          itemStacks[value] = this.updateItemStackLore(itemStacks[value], player, kit);
        }

        CMIShulkerBox.setShulkerInv(itemStack, itemStacks);
      } else if (cmiMaterial.containsCriteria(CMIMC.BUNDLE)) {
        BundleMeta bundleMeta = (BundleMeta) itemStack.getItemMeta();
        ArrayList arrayList = new ArrayList(bundleMeta.getItems());

        for (int bundleIndex = 0; bundleIndex < arrayList.size(); bundleIndex++) {
          arrayList.set(
              bundleIndex,
              this.updateItemStackLore((ItemStack) arrayList.get(bundleIndex), player, kit));
        }

        bundleMeta.setItems(arrayList);
        itemStack.setItemMeta(bundleMeta);
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta.hasDisplayName()) {
      itemMeta.setDisplayName(this.processText(itemMeta.getDisplayName(), player, kit));
    }

    if (itemMeta.hasLore()) {
      ArrayList<String> loreLines = new ArrayList();

      for (String string : itemMeta.getLore()) {
        loreLines.add(this.processText(string, player, kit));
      }

      itemMeta.setLore(loreLines);
    }

    itemStack.setItemMeta(itemMeta);
    return itemStack;
  }

  public String processText(String string, Player player, Kit kit) {
    string = CMIChatColor.translate(string);
    Snd snd = new Snd().setSender(player).setTarget(player);
    string = this.plugin.getLM().updateSnd(snd, string);
    if (player != null) {
      string = string.replaceAll("(?i)\\{USERNAME\\}", player.getName());
      string = string.replaceAll("(?i)\\{DISPLAYNAME\\}", player.getDisplayName());
      if (player.isOnline()) {
        string = string.replaceAll("(?i)\\{WORLDNAME\\}", player.getWorld().getName());
      }
    }

    if (kit != null) {
      if (string.toLowerCase().contains("{date}")) {
        string =
            string.replaceAll(
                "(?i)\\{DATE\\}",
                new SimpleDateFormat(this.dateFormat).format(Calendar.getInstance().getTime()));
      }

      string = string.replaceAll("(?i)\\{KITNAME\\}", kit.getCommandName());
      Random random = new Random(System.currentTimeMillis());
      var list = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
      int value = list.size();
      if (value > 0) {
        int randomIndex = random.nextInt(value) + 1;
        Player randomPlayer = (Player) list.get(randomIndex - 1);
        string = string.replaceAll("(?i)\\{RANDOMPLAYER\\}", randomPlayer.getName());
      } else {
        string = string.replaceAll("(?i)\\{RANDOMPLAYER\\}", "-");
      }
    }

    return CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(player, string);
  }

  public void listPlayersKits(Player player) {
    this.listPlayersKits(null, player, 1);
  }

  public void listPlayersKits(CommandSender commandSender, Player player, int value) {
    if (player == null) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("&eKits: ");
      int kitCount = 0;

      for (var entry : this.plugin.getKitsManager().getKitMap().entrySet()) {
        kitCount++;
        stringBuilder.append(((Kit) entry.getValue()).getConfigName());
        if (kitCount < this.plugin.getKitsManager().getKitMap().size()) {
          stringBuilder.append(LC.info_ListSpliter.getLocale(new Object[0]));
        }
      }

      if (!(commandSender instanceof Player)) {
        commandSender.sendMessage(CMIChatColor.translate(stringBuilder.toString()));
      }
    }

    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    var hashMap = this.getValidKitsForPlayer(player, true);
    int totalKits = 0;

    for (Entry groupEntry : hashMap.entrySet()) {
      totalKits += ((List) groupEntry.getValue()).size();
    }

    if (totalKits == 0) {
      CMILC.info(kit.class, commandSender, "noFoundKit");
    } else {
      PageInfo pageInfo = new PageInfo(10, totalKits, value);
      int kitNumber = 0;
      RawMessage rawMessage = new RawMessage();

      breakSecond:
      for (Entry groupEntry : hashMap.entrySet()) {
        List list = (List) groupEntry.getValue();
        Iterator iterator = list.iterator();

        while (true) {
          if (iterator.hasNext()) {
            Kit kit = (Kit) iterator.next();
            kitNumber++;
            String string = kit.getDisplayName();
            String kitDescription =
                CMIList.listToString(
                    kit.getDescription(), LC.info_ListSpliter.getLocale(new Object[0]));
            if (pageInfo.isContinue()) {
              continue;
            }

            if (!pageInfo.isBreak()) {
              if (cmiUser.getLeftKitUseTimes(kit) == 0
                  && !PermissionsManager.CMIPerm.kit_bypass_onetimeuse.hasPermission(player)) {
                continue;
              }

              Long lastUse = cmiUser.getKitTime(kit);
              String statusText = "";
              if (lastUse != null && lastUse < 0L) {
                int usesLeft = cmiUser.getLeftKitUseTimes(kit);
                if (usesLeft > 0) {
                  statusText =
                      CMILC.getIM(
                          kit.class, "limitedTimeUse", "[times]", cmiUser.getLeftKitUseTimes(kit));
                } else {
                  statusText = CMILC.getIM(kit.class, "limitedTimeAllUsed");
                }
              } else {
                if (lastUse != null) {
                  long longValue = lastUse + kit.getDelay() * 1000L;
                  if (longValue > System.currentTimeMillis()) {
                    statusText =
                        this.plugin
                            .getLM()
                            .getMessage(
                                "command.kit.info.canUseIn",
                                "[time]",
                                CMITimeManager.to24hourShort(
                                    longValue - System.currentTimeMillis()));
                  }
                }

                if (lastUse != null && lastUse + kit.getDelay() * 1000L < System.currentTimeMillis()
                    || lastUse == null) {
                  statusText =
                      this.plugin
                          .getLM()
                          .getMessage(
                              "command.kit.info.cooldown",
                              "[time]",
                              CMITimeManager.to24hourShort(kit.getDelay() * 1000L));
                }
              }

              if (!kit.getDescription().isEmpty()) {
                for (String descriptionLine : kit.getDescription(player)) {
                  statusText = statusText + " \n" + descriptionLine;
                }
              }

              kitDescription =
                  CMILC.getIM(kit.class, cmiUser.canUseKit(kit) ? "use" : "cantuse")
                      + kitDescription;
              if (kit.getGroup() != null && list.size() > 1) {
                if (kitNumber == 1) {
                  string = "(" + string;
                }

                if (kitNumber == list.size()) {
                  string = string + ") ";
                } else {
                  string = string + " - ";
                }
              }

              RawMessage entryMessage = new RawMessage();
              entryMessage
                  .addText(
                      CMILC.getIM(
                          kit.class,
                          "list",
                          "[number]",
                          kitNumber,
                          "[kitName]",
                          string,
                          "[description]",
                          kitDescription))
                  .addHover(statusText)
                  .addCommand("cmi kit " + kit.getCommandName());
              if (this.allowKitPreview) {
                entryMessage
                    .addText(CMILC.getIM(kit.class, "eye"))
                    .addHover(LC.info_Preview.getLocale(new Object[0]))
                    .addCommand("cmi kit " + kit.getCommandName() + " -preview");
              }

              rawMessage.addRM(entryMessage);
              continue;
            }
          }

          if (pageInfo.isBreak()) {
            break breakSecond;
          }
          break;
        }
      }

      rawMessage.show(player);
      pageInfo.autoPagination(
          commandSender, CommandsHandler.getLabel() + " " + this.getClass().getSimpleName(), "-p:");
    }
  }

  private List<String> getLoreForButton(CMIUser cmiUser, Kit kit) {
    Long lastUse = cmiUser.getKitTime(kit);
    ArrayList<String> arrayList = new ArrayList();
    if (!kit.getDescription().isEmpty()) {
      for (String string : kit.getDescription(cmiUser.getPlayer())) {
        arrayList.add(string);
      }
    }

    arrayList.add("");
    boolean flag =
        this.plugin.getKitsManager().getKit(cmiUser.getPlayer(), kit.getCommandName(), false)
            == null;
    if (flag) {
      arrayList.add(CMILC.getIM(kit.class, "noKitAccess"));
    }

    if (kit.isLimitedUse()) {
      int value = cmiUser.getLeftKitUseTimes(kit);
      if (value > 0) {
        arrayList.add(
            CMILC.getIM(
                kit.class,
                "limitedTimeUse",
                "[times]",
                cmiUser.getLeftKitUseTimes(kit),
                "[max]",
                kit.getMaxUsages()));
      } else {
        arrayList.add(CMILC.getIM(kit.class, "limitedTimeAllUsed"));
      }
    }

    if (lastUse != null) {
      long longValue = lastUse + kit.getDelay() * 1000L;
      if (longValue > System.currentTimeMillis()) {
        arrayList.add(
            this.plugin
                .getLM()
                .getMessage(
                    "command.kit.info.canUseIn",
                    "[time]",
                    CMITimeManager.to24hourShort(longValue - System.currentTimeMillis())));
      }
    }

    if (lastUse != null && lastUse + kit.getDelay() * 1000L < System.currentTimeMillis()
        || lastUse == null) {
      arrayList.add(
          this.plugin
              .getLM()
              .getMessage(
                  "command.kit.info.cooldown",
                  "[time]",
                  CMITimeManager.to24hourShort(kit.getDelay() * 1000L)));
    }

    return arrayList;
  }

  private int slotToGUIrelativeSlot(int value) {
    return 9 + value / 7 * 2 + 1 + value;
  }

  public void listPlayersKitsInComplexGUI(final Player player, int value) {
    if (player != null) {
      value = value < 1 ? 1 : value;
      final CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      final CMIGui cmiGui =
          new CMIGui(player) {
            public void pageChange(int page) {
              KitsManager.this.listPlayersKitsInComplexGUI(player, page);
            }
          };
      cmiGui.addLock(InvType.Gui);
      var list = CMILC.getIML("kit", "titlesByPage");
      String string = CMILC.getIM("kit", "title");
      if (list.size() >= value) {
        string = (String) list.get(value - 1);
      }

      cmiGui.setTitle(string);
      cmiGui.setInvSize(GUIRows.r6);

      try {
        cmiGui.setAllowClone(true);
      } catch (Exception exception) {
      }

      CMIGuiButton cmiGuiButton =
          new CMIGuiButton(this.guiCloseButtonSlot, this.guiCloseButtonItem) {
            public void click(GUIClickType guiClickType) {
              Snd snd = new Snd();
              snd.setTarget(player);
              snd.setSender(player);
              snd.setSource(player);
              KitsManager.this
                  .plugin
                  .getSpecializedCommandManager()
                  .processCmds(
                      KitsManager.this
                          .plugin
                          .getLM()
                          .updateSnd(snd, new ArrayList<>(KitsManager.this.guiCloseButtonCommands)),
                      player);
            }
          };
      cmiGuiButton.setName(CMILC.getIM(homes.class, "closeButton"));
      if (this.guiCloseButton) {
        cmiGui.addButton(cmiGuiButton);
      }

      cmiGuiButton =
          new CMIGuiButton(this.guiInfoButtonSlot, this.guiInfoButtonItem) {
            public void click(GUIClickType clickType) {
              Snd clickSound = new Snd();
              clickSound.setTarget(player);
              clickSound.setSender(player);
              clickSound.setSource(player);
              KitsManager.this
                  .plugin
                  .getSpecializedCommandManager()
                  .processCmds(
                      KitsManager.this
                          .plugin
                          .getLM()
                          .updateSnd(
                              clickSound, new ArrayList<>(KitsManager.this.guiInfoButtonCommands)),
                      player);
            }
          };
      cmiGuiButton.setName(CMILC.getIM(homes.class, "infoButton"));
      if (this.guiInfoButton) {
        cmiGui.addButton(cmiGuiButton);
      }

      final boolean flag = PermissionsManager.CMIPerm.command_$1.hasPermission(player, "kiteditor");
      int totalKits = 0;
      int kitNumber = 1;
      var hashMap = this.getValidKitsForPlayer(player, true);

      for (Entry entry : hashMap.entrySet()) {
        totalKits += ((List) entry.getValue()).size();

        for (Kit kit : (List<Kit>) entry.getValue()) {
          if (kit.getPage() != null && kit.getPage() > kitNumber) {
            kitNumber = kit.getPage();
          }
        }
      }

      if (totalKits / 28.0 > kitNumber) {
        kitNumber = (int) Math.ceil(totalKits / 28.0);
      }

      PageInfo pageInfo = new PageInfo(1, kitNumber, value);
      cmiGui.addPagination(pageInfo);
      cmiGui.fillInEdges();
      pageInfo = new PageInfo(28, totalKits, value);

      for (Entry groupEntry : hashMap.entrySet()) {
        if (pageInfo.isBreak()) {
          break;
        }

        for (final Kit pageKit : (List<Kit>) groupEntry.getValue()) {
          if ((!pageInfo.isContinue() || pageKit.getPage() != null && pageKit.getPage() == value)
              && (pageKit.getPage() == null || pageKit.getPage() == value)) {
            boolean canUse = cmiUser.canUseKit(pageKit);
            boolean noAccess =
                this.plugin.getKitsManager().getKit(player, pageKit.getCommandName(), false)
                    == null;
            final String kitNameText =
                CMILC.getIM(
                    "kit",
                    !noAccess && canUse ? "nameCanUse" : "nameCantUse",
                    "[kitName]",
                    pageKit.getDisplayName());
            ItemStack itemStack =
                !noAccess && canUse ? pageKit.getIcon() : pageKit.getSafeIconOff();
            if (itemStack == null) {
              itemStack = pageKit.getFirstNotNullItem();
              if (itemStack != null) {
                itemStack = itemStack.clone();
                itemStack.setAmount(1);
              }
            }

            if (itemStack == null) {
              itemStack = CMIMaterial.getRandom(CMIMaterial.BLACK_WOOL).newItemStack();
            }

            itemStack = itemStack.clone();
            itemStack = this.updateItemStackLore(itemStack, player, pageKit);
            Integer integer =
                pageKit.getSlot() == null ? null : this.slotToGUIrelativeSlot(pageKit.getSlot());
            if (integer != null && cmiGui.getButtons().get(integer) != null) {
              integer = null;
            }

            cmiGuiButton =
                new CMIGuiButton(integer, itemStack) {
                  public void click(GUIClickType clickType) {
                    switch (clickType) {
                      case Left:
                      case LeftShift:
                        CMI.getInstance()
                            .getCommandManager()
                            .performCMICommand(player, kit.class, pageKit.getCommandName());
                        CMIGui openGui = CMILib.getInstance().getGUIManager().getGui(player);
                        if (openGui != null && openGui.equals(cmiGui)) {
                          this.updateLooks();
                          this.update(cmiGui);
                        }
                        break;
                      case Right:
                      case RightShift:
                        if (KitsManager.this.allowKitPreview) {
                          KitsManager.this.openGuiEditorMain(pageKit, player, false);
                        }
                        break;
                      case MiddleMouse:
                      case Q:
                        if (flag
                            || PermissionsManager.CMIPerm.command_$1.hasPermission(
                                player, kiteditor.class.getSimpleName())) {
                          KitsManager.this.openGuiEditorMain(pageKit, player, true);
                        }
                      case QControl:
                    }
                  }

                  public void updateLooks() {
                    this.setName(kitNameText);
                    this.clearLore();
                    this.addLore(KitsManager.this.getLoreForButton(cmiUser, pageKit));
                    if (pageKit.getGroup() != null) {
                      this.addLore(CMILC.getIM("kit", "group", "[group]", pageKit.getGroup()));
                    }

                    this.addLore(CMILC.getIM("kit", "left"));
                    if (KitsManager.this.allowKitPreview) {
                      this.addLore(CMILC.getIM("kit", "right"));
                    }

                    if (flag) {
                      this.addLore(" ");
                      this.addLore(LC.modify_middlemouse.getLocale(new Object[0]));
                    }

                    boolean canUseHere = cmiUser.canUseKit(pageKit);
                    boolean hasNoAccess =
                        KitsManager.this
                                .plugin
                                .getKitsManager()
                                .getKit(player, pageKit.getCommandName(), false)
                            == null;
                    if (!hasNoAccess && canUseHere) {
                      this.setGlowing();
                    } else {
                      this.removeGlowing();
                    }
                  }
                };
            cmiGuiButton.hideItemFlags();
            cmiGuiButton.startAutoUpdate(20);
            cmiGui.addButton(cmiGuiButton);
          }
        }
      }

      cmiGui.open();
    }
  }

  public void listPlayersKitsInGUI(Player player) {
    this.listPlayersKitsInGUI(player, 1);
  }

  public void listPlayersKitsInGUI(Player player, int value) {
    this.listPlayersKitsInComplexGUI(player, value);
  }

  public void listPlayersKitsForEditing(final Player player, Integer integer) {
    integer = integer == null ? 1 : integer;
    integer = integer < 1 ? 1 : integer;
    RawMessage rawMessage = new RawMessage();
    rawMessage.addText(CMILC.getIM("kiteditor", "kits"));
    LinkedHashMap<String, List> linkedHashMap = new LinkedHashMap();
    int value = 0;
    var iterator = new LinkedList<>(this.getKitMap().keySet()).descendingIterator();

    while (iterator.hasNext()) {
      String string = (String) iterator.next();
      Kit kit = this.getKitMap().get(string);
      List<Kit> list = new ArrayList();
      if (linkedHashMap.containsKey(kit.getGroup())) {
        list = (List) linkedHashMap.get(kit.getGroup());
      }

      list.add(kit);
      linkedHashMap.put(kit.getGroup(), list);
    }

    for (Entry entry : linkedHashMap.entrySet()) {
      value += ((List) entry.getValue()).size();
    }

    PageInfo pageInfo = new PageInfo(20, value, integer);
    boolean flag = false;

    for (Entry groupEntry : linkedHashMap.entrySet()) {
      if (flag) {
        break;
      }

      List<Kit> groupKits = (List<Kit>) groupEntry.getValue();
      int kitCount = 0;

      for (final Kit pageKit : groupKits) {
        if (!pageInfo.isContinue()) {
          if (pageInfo.isBreak()) {
            flag = true;
            break;
          }

          kitCount++;
          String configName = pageKit.getConfigName();
          if (pageKit.isEnabled()) {
            if (pageKit.getGroup() != null && groupKits.size() > 1) {
              configName = "&e" + configName + "&r";
            } else {
              configName = "&6" + configName + "&r";
            }
          } else {
            configName = "&7" + configName + "&r";
          }

          if (pageKit.getGroup() != null && groupKits.size() > 1) {
            if (kitCount == 1) {
              rawMessage.add("(");
            }

            if (kitCount == groupKits.size()) {
              configName = configName + ") ";
            } else {
              configName = configName + "-";
            }
          } else {
            configName = configName + " ";
          }

          RawMessageCommand rawMessageCommand =
              new RawMessageCommand() {
                public void run(CommandSender commandSender) {
                  KitsManager.this.openGuiEditorMain(pageKit, player, true);
                }
              };
          rawMessageCommand.setKeep(true);
          rawMessage.add(
              configName,
              LC.modify_editSymbolHover.getLocale(new Object[] {"[text]", configName}),
              rawMessageCommand);
        }
      }
    }

    RawMessageCommand addCallback =
        new RawMessageCommand() {
          public void run(CommandSender sender) {
            if (sender instanceof Player) {
              KitsManager.this.startChatKitAdd((Player) sender, null);
            }
          }
        };
    addCallback.setKeep(true);
    rawMessage.add(
        CMILC.getIM("kiteditor", "ListAdd"), CMILC.getIM("kiteditor", "ListAddHover"), addCallback);
    rawMessage.show(player);
    pageInfo.autoPagination(
        player, CommandsHandler.getLabel() + " " + kiteditor.class.getSimpleName(), "-p:");
  }

  public boolean startChatKitAdd(final Player player, String string) {
    ChatMessageEdit chatMessageEdit =
        new ChatMessageEdit(player) {
          public void run(String input) {
            input = CMIChatColor.stripColor(CMIText.getFirstVariable(input));
            if (CMIText.isValidString(input)) {
              if (CMI.getInstance().getKitsManager().getKit(input, true) != null) {
                CMILC.info(kiteditor.class, player, "kitExists");
              } else {
                Kit kit = new Kit(input);
                kit.setDelay(10L);
                kit.setEnabled(false);
                KitsManager.this.addKit(kit);
                KitsManager.this.openGuiEditorMain(kit, player, true);
                KitsManager.this.listPlayersKitsForEditing(player, 1);
              }
            }
          }

          public void onCancel() {
            KitsManager.this.listPlayersKitsForEditing(player, 1);
          }
        };
    chatMessageEdit.setCheckForCancel(true);
    if (string != null) {
      chatMessageEdit.run(string);
      chatMessageEdit.delete();
    } else {
      CMILC.info(kiteditor.class, player, "addNewInfo");
    }

    return true;
  }

  public boolean startChatKitRename(final Player player, final Kit kit) {
    if (kit == null) {
      CMILC.info(kiteditor.class, player, "noKit");
      return false;
    } else {
      ChatMessageEdit chatMessageEdit =
          new ChatMessageEdit(player) {
            public void run(String string) {
              string = CMIChatColor.stripColor(CMIText.getFirstVariable(string));
              if (CMIText.isValidString(string)) {
                if (CMI.getInstance().getKitsManager().getKit(string, true) != null) {
                  CMILC.info(kiteditor.class, player, "kitExists");
                } else {
                  String oldName = kit.getConfigName();
                  KitsManager.this
                      .plugin
                      .getKitsManager()
                      .renameKitConfigName(kit, CMIChatColor.stripColor(string));
                  CMILC.info(
                      kiteditor.class,
                      player,
                      "renamed",
                      "[kitname]",
                      CMIChatColor.stripColor(oldName),
                      "[newkitname]",
                      CMIChatColor.stripColor(kit.getConfigName()));
                }
              }
            }

            public void onDisable() {
              KitsManager.this.openGuiEditorSettings(kit, player, true);
            }
          };
      chatMessageEdit.setCheckForCancel(true);
      CMILC.info(kiteditor.class, player, "renameConfigInfo");
      return true;
    }
  }

  public boolean startChatKitCommandRename(final Player player, final Kit kit) {
    if (kit == null) {
      CMILC.info(kiteditor.class, player, "noKit");
      return false;
    } else {
      ChatMessageEdit chatMessageEdit =
          new ChatMessageEdit(player) {
            public void run(String string) {
              string = CMIChatColor.stripColor(CMIText.getFirstVariable(string));
              if (CMIText.isValidString(string)) {
                String oldCommandName = kit.getCommandName();
                KitsManager.this
                    .plugin
                    .getKitsManager()
                    .renameKitCommandName(kit, CMIChatColor.stripColor(string));
                CMILC.info(
                    kiteditor.class,
                    player,
                    "renamed",
                    "[kitname]",
                    oldCommandName,
                    "[newkitname]",
                    CMIChatColor.stripColor(string));
              }
            }

            public void onDisable() {
              KitsManager.this.openGuiEditorSettings(kit, player, true);
            }
          };
      chatMessageEdit.setCheckForCancel(true);
      CMILC.info(kiteditor.class, player, "renameCommandInfo");
      return true;
    }
  }

  public boolean startChatKitGroupRename(final Player player, final Kit kit) {
    if (kit == null) {
      CMILC.info(kiteditor.class, player, "noKit");
      return false;
    } else {
      ChatMessageEdit chatMessageEdit =
          new ChatMessageEdit(player) {
            public void run(String string) {
              string = CMIChatColor.stripColor(CMIText.getFirstVariable(string));
              if (CMIText.isValidString(string)) {
                kit.setGroup(CMIChatColor.stripColor(string));
                CMILC.info(
                    kiteditor.class,
                    player,
                    "groupSet",
                    "[groupname]",
                    string,
                    "[kitname]",
                    kit.getConfigName());
              }
            }

            public void onDisable() {
              KitsManager.this.openGuiEditorSettings(kit, player, true);
            }
          };
      chatMessageEdit.setCheckForCancel(true);
      CMILC.info(kiteditor.class, player, "groupInfo");
      return true;
    }
  }

  public void startChatKitRemove(final Player player, final Kit kit, boolean flag) {
    if (kit == null) {
      CMILC.info(kiteditor.class, player, "noKit");
    } else if (!flag) {
      RawMessageCommand rawMessageCommand =
          new RawMessageCommand() {
            public void run(CommandSender commandSender) {
              KitsManager.this.startChatKitRemove(player, kit, true);
            }
          };
      RawMessage rawMessage = new RawMessage();
      rawMessage.add(
          CMILC.getIM(kiteditor.class, "deleteConfirm", "[kitname]", kit.getConfigName()),
          CMILC.getIM(kiteditor.class, "deleteConfirmFinal"),
          rawMessageCommand.getCommand());
      rawMessage.show(player);
    } else {
      if (this.plugin.getKitsManager().removeKit(kit.getConfigName())) {
        CMILC.info(kiteditor.class, player, "kitRemoved", "[kitname]", kit.getConfigName());
        this.listPlayersKitsForEditing(player, 1);
        this.plugin.getKitsManager().save();
      } else {
        CMILC.info(kiteditor.class, player, "kitNotRemoved", "[kitname]", kit.getConfigName());
      }
    }
  }

  public void giveKit(Player player, Kit kit) {
    this.giveKit(player, kit, true);
  }

  public void giveKit(Player player, Kit kit, boolean flag) {
    CMIUserKitAcquireEvent cmiUserKitAcquireEvent =
        new CMIUserKitAcquireEvent(CMIUser.getUser(player), kit, flag);
    CMI.getInstance().getServer().getPluginManager().callEvent(cmiUserKitAcquireEvent);
    if (!cmiUserKitAcquireEvent.isCancelled()) {
      flag = cmiUserKitAcquireEvent.isGiveItems();
      if (flag) {
        var list = kit.getItems(player);
        ArrayList<ItemStack> arrayList = new ArrayList();
        CMIUser cmiUser = CMIUser.getUser(player);

        for (int value = 27; value <= 35 && list.size() >= value; value++) {
          ItemStack itemStack = (ItemStack) list.get(value);
          if (itemStack != null) {
            if (player.getInventory().getItem(value - 27) != null
                && player.getInventory().getItem(value - 27).getType() != Material.AIR) {
              arrayList.add(itemStack);
            } else {
              player.getInventory().setItem(value - 27, itemStack);
            }
          }
        }

        for (int slotIndex = 0; slotIndex <= 26 && list.size() >= slotIndex; slotIndex++) {
          ItemStack kitItem = (ItemStack) list.get(slotIndex);
          if (kitItem != null) {
            if (player.getInventory().getItem(slotIndex + 9) != null
                && player.getInventory().getItem(slotIndex + 9).getType() != Material.AIR) {
              arrayList.add(kitItem);
            } else {
              player.getInventory().setItem(slotIndex + 9, kitItem);
            }
          }
        }

        if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Helmet) != null) {
          ItemStack helmetItem = player.getInventory().getHelmet();
          if (helmetItem != null && helmetItem.getType() != Material.AIR) {
            arrayList.add(kit.getExtraItem(player, CMIPlayerInventory.CMIInventorySlot.Helmet));
          } else {
            player
                .getInventory()
                .setHelmet(kit.getExtraItem(player, CMIPlayerInventory.CMIInventorySlot.Helmet));
          }
        }

        if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.ChestPlate) != null) {
          ItemStack chestItem = player.getInventory().getChestplate();
          if (chestItem != null && chestItem.getType() != Material.AIR) {
            arrayList.add(kit.getExtraItem(player, CMIPlayerInventory.CMIInventorySlot.ChestPlate));
          } else {
            player
                .getInventory()
                .setChestplate(
                    kit.getExtraItem(player, CMIPlayerInventory.CMIInventorySlot.ChestPlate));
          }
        }

        if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Pants) != null) {
          ItemStack legsItem = player.getInventory().getLeggings();
          if (legsItem != null && legsItem.getType() != Material.AIR) {
            arrayList.add(kit.getExtraItem(player, CMIPlayerInventory.CMIInventorySlot.Pants));
          } else {
            player
                .getInventory()
                .setLeggings(kit.getExtraItem(player, CMIPlayerInventory.CMIInventorySlot.Pants));
          }
        }

        if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Boots) != null) {
          ItemStack bootsItem = player.getInventory().getBoots();
          if (bootsItem != null && bootsItem.getType() != Material.AIR) {
            arrayList.add(kit.getExtraItem(player, CMIPlayerInventory.CMIInventorySlot.Boots));
          } else {
            player
                .getInventory()
                .setBoots(kit.getExtraItem(player, CMIPlayerInventory.CMIInventorySlot.Boots));
          }
        }

        if (Version.isCurrentHigher(Version.v1_8_R3)
            && kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.OffHand) != null) {
          ItemStack offHandItem = player.getInventory().getItemInOffHand();
          if (offHandItem != null && offHandItem.getType() != Material.AIR) {
            arrayList.add(kit.getExtraItem(player, CMIPlayerInventory.CMIInventorySlot.OffHand));
          } else {
            player
                .getInventory()
                .setItemInOffHand(
                    kit.getExtraItem(player, CMIPlayerInventory.CMIInventorySlot.OffHand));
          }
        }

        boolean droppedAny = false;

        for (ItemStack leftoverItem : arrayList) {
          if (leftoverItem != null) {
            if (player.getInventory().firstEmpty() == -1) {
              if (kit.isDropItems()) {
                droppedAny = true;
                cmiUser.dropItemNearPlayer(leftoverItem);
              } else {
                player.getInventory().addItem(new ItemStack[] {leftoverItem});
              }
            } else {
              player.getInventory().addItem(new ItemStack[] {leftoverItem});
            }
          }
        }

        if (droppedAny) {
          CMIMessages.sendMessage(player, LC.info_Inventory_FullDrop, new Object[0]);
        }
      }

      var commands = kit.getCommands(player);
      CMIScheduler.runTask(
          this.plugin,
          () -> this.plugin.getSpecializedCommandManager().processCmds(commands, player));
      this.plugin.save(player);
    }
  }

  public Kit getNewbieKit() {
    if (this.NewbieKit == null) {
      this.NewbieKit = this.getKit(this.NewbieKitName, true);
    }

    return this.NewbieKit;
  }

  public void openGuiEditorMain(final Kit kit, final Player player, final boolean flag) {
    if (kit != null && (flag || this.allowKitPreview)) {
      CMIGui cmiGui =
          new CMIGui(player) {
            public void onClose() {
              KitsManager.this.safeSave();
            }
          };
      cmiGui.setTitle(
          CMILC.getIM(
              kit.class,
              "kitGuiTitle",
              "[kitName]",
              kit.getCommandName(),
              "[kitDisplayName]",
              kit.getDisplayName()));
      cmiGui.setInvSize(GUIRows.r6);
      if (flag) {
        CMIGuiButton cmiGuiButton =
            new CMIGuiButton(
                KitsManager.CMIKitGUILayout.Clone.getSlot(),
                !cloneCache.containsKey(player.getUniqueId())
                    ? new ItemStack(Material.BOOK)
                    : new ItemStack(Material.ENCHANTED_BOOK)) {
              public void click(GUIClickType guiClickType) {
                ArrayList<ItemStack> arrayList = new ArrayList();
                if (!KitsManager.cloneCache.containsKey(player.getUniqueId())) {
                  PlayerInventory playerInventory = player.getInventory();
                  ItemStack[] itemStacks = playerInventory.getContents();
                  Inventory inventory = CMIPlayerInventory.getTopInventory(player);
                  ArrayList<ItemStack> clonedItems = new ArrayList();

                  for (int value = 9; value < KitsManager.min; value++) {
                    clonedItems.add(itemStacks[value]);
                    if (inventory != null) {
                      arrayList.add(inventory.getContents()[value + 9]);
                    }
                  }

                  for (int slotIndex = 0; slotIndex < 9; slotIndex++) {
                    clonedItems.add(itemStacks[slotIndex]);
                    if (inventory != null) {
                      arrayList.add(inventory.getContents()[slotIndex + 45]);
                    }
                  }

                  kit.setItem(clonedItems);
                  if (guiClickType.equals(GUIClickType.Right)) {
                    arrayList.add(kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Helmet));
                    kit.setExtraItem(
                        CMIPlayerInventory.CMIInventorySlot.Helmet, playerInventory.getHelmet());
                    arrayList.add(kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.ChestPlate));
                    kit.setExtraItem(
                        CMIPlayerInventory.CMIInventorySlot.ChestPlate,
                        playerInventory.getChestplate());
                    arrayList.add(kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Pants));
                    kit.setExtraItem(
                        CMIPlayerInventory.CMIInventorySlot.Pants, playerInventory.getLeggings());
                    arrayList.add(kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Boots));
                    kit.setExtraItem(
                        CMIPlayerInventory.CMIInventorySlot.Boots, playerInventory.getBoots());
                    if (Version.isCurrentHigher(Version.v1_8_R3)) {
                      arrayList.add(kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.OffHand));
                      kit.setExtraItem(
                          CMIPlayerInventory.CMIInventorySlot.OffHand,
                          playerInventory.getItemInOffHand());
                    }
                  }

                  KitsManager.cloneCache.put(player.getUniqueId(), arrayList);
                  CMI.getInstance().getKitsManager().openGuiEditorMain(kit, player, true);
                } else {
                  var list = KitsManager.cloneCache.get(player.getUniqueId());
                  ArrayList<ItemStack> restoredItems = new ArrayList();

                  for (int restoreIndex = 0; restoreIndex < KitsManager.min; restoreIndex++) {
                    restoredItems.add((ItemStack) list.get(restoreIndex));
                  }

                  kit.setItem(restoredItems);
                  if (list.size() > KitsManager.min) {
                    kit.setExtraItem(
                        CMIPlayerInventory.CMIInventorySlot.Helmet,
                        (ItemStack) list.get(KitsManager.min));
                    kit.setExtraItem(
                        CMIPlayerInventory.CMIInventorySlot.ChestPlate,
                        (ItemStack) list.get(KitsManager.min + 1));
                    kit.setExtraItem(
                        CMIPlayerInventory.CMIInventorySlot.Pants,
                        (ItemStack) list.get(KitsManager.min + 2));
                    kit.setExtraItem(
                        CMIPlayerInventory.CMIInventorySlot.Boots,
                        (ItemStack) list.get(KitsManager.min + 3));
                    if (Version.isCurrentHigher(Version.v1_8_R3)) {
                      kit.setExtraItem(
                          CMIPlayerInventory.CMIInventorySlot.OffHand,
                          (ItemStack) list.get(KitsManager.min + 4));
                    }
                  }

                  KitsManager.cloneCache.remove(player.getUniqueId());
                  CMI.getInstance().getKitsManager().openGuiEditorMain(kit, player, true);
                }
              }
            };
        cmiGuiButton.setName(CMILC.getIM("kiteditor", "Clone"));
        cmiGuiButton.addLore(CMILC.getIML("kiteditor", "CloneExtra"));
        cmiGui.addButton(cmiGuiButton);
      }

      if (!Version.isCurrentLower(Version.v1_9_R1)) {
        ItemStack itemStack = new ItemStack(Material.SHIELD);
        ItemMeta itemMeta = itemStack.getItemMeta();

        try {
          BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
          Banner banner = (Banner) blockStateMeta.getBlockState();
          banner.setBaseColor(DyeColor.WHITE);
          banner.update();
          blockStateMeta.setBlockState(banner);
        } catch (Exception exception) {
        }

        itemStack.setItemMeta(itemMeta);
        CMIGuiButton offHandButton =
            new CMIGuiButton(
                KitsManager.CMIKitGUILayout.OffHand.getSlot(), flag ? itemStack : null) {
              public void click(GUIClickType clickType) {
                if (flag) {
                  ItemStack cursorItem = player.getItemOnCursor();
                  if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.OffHand) != null) {
                    player.setItemOnCursor(
                        kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.OffHand));
                    kit.setExtraItem(CMIPlayerInventory.CMIInventorySlot.OffHand, null);
                  } else {
                    player.setItemOnCursor(null);
                  }

                  if (cursorItem != null && cursorItem.getType() != Material.AIR) {
                    kit.setExtraItem(
                        CMIPlayerInventory.CMIInventorySlot.OffHand, cursorItem.clone());
                  }

                  CMI.getInstance().getKitsManager().openGuiEditorMain(kit, player, true);
                }
              }
            };
        if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.OffHand) == null
            || kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.OffHand).getType()
                == Material.AIR) {
          offHandButton.setName(CMILC.getIM("kiteditor", "OffHand"));
          offHandButton.hideItemFlags();
        } else if (flag) {
          offHandButton =
              offHandButton.setItem(kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.OffHand));
        } else {
          offHandButton =
              offHandButton.setItem(
                  kit.getExtraItem(player, CMIPlayerInventory.CMIInventorySlot.OffHand));
        }

        if (flag
            || offHandButton.getItem() != null
                && !offHandButton.getItem().getType().equals(Material.AIR)) {
          cmiGui.addButton(offHandButton);
        }
      }

      if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Helmet) != null || flag) {
        CMIGuiButton helmetButton =
            new CMIGuiButton(
                KitsManager.CMIKitGUILayout.Helmet.getSlot(),
                flag ? new ItemStack(Material.CHAINMAIL_HELMET) : null) {
              public void click(GUIClickType clickType) {
                if (flag) {
                  ItemStack cursorItem = player.getItemOnCursor();
                  if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Helmet) != null) {
                    player.setItemOnCursor(
                        kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Helmet));
                    kit.setExtraItem(CMIPlayerInventory.CMIInventorySlot.Helmet, null);
                  } else {
                    player.setItemOnCursor(null);
                  }

                  if (cursorItem != null && cursorItem.getType() != Material.AIR) {
                    kit.setExtraItem(
                        CMIPlayerInventory.CMIInventorySlot.Helmet, cursorItem.clone());
                  }

                  CMI.getInstance().getKitsManager().openGuiEditorMain(kit, player, true);
                }
              }
            };
        if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Helmet) == null
            || kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Helmet).getType()
                == Material.AIR) {
          helmetButton.setName(CMILC.getIM("kiteditor", "Helmet"));
          helmetButton.hideItemFlags();
        } else if (flag) {
          helmetButton =
              helmetButton.setItem(kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Helmet));
        } else {
          helmetButton =
              helmetButton.setItem(
                  kit.getExtraItem(player, CMIPlayerInventory.CMIInventorySlot.Helmet));
        }

        cmiGui.addButton(helmetButton);
      }

      if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.ChestPlate) != null || flag) {
        CMIGuiButton chestButton =
            new CMIGuiButton(
                KitsManager.CMIKitGUILayout.Chest.getSlot(),
                flag ? new ItemStack(Material.CHAINMAIL_CHESTPLATE) : null) {
              public void click(GUIClickType clickType) {
                if (flag) {
                  ItemStack cursorItem = player.getItemOnCursor();
                  if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.ChestPlate) != null) {
                    player.setItemOnCursor(
                        kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.ChestPlate));
                    kit.setExtraItem(CMIPlayerInventory.CMIInventorySlot.ChestPlate, null);
                  } else {
                    player.setItemOnCursor(null);
                  }

                  if (cursorItem != null && cursorItem.getType() != Material.AIR) {
                    kit.setExtraItem(
                        CMIPlayerInventory.CMIInventorySlot.ChestPlate, cursorItem.clone());
                  }

                  CMI.getInstance().getKitsManager().openGuiEditorMain(kit, player, true);
                }
              }
            };
        if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.ChestPlate) == null
            || kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.ChestPlate).getType()
                == Material.AIR) {
          chestButton.setName(CMILC.getIM("kiteditor", "Chest"));
          chestButton.hideItemFlags();
        } else if (flag) {
          chestButton =
              chestButton.setItem(kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.ChestPlate));
        } else {
          chestButton =
              chestButton.setItem(
                  kit.getExtraItem(player, CMIPlayerInventory.CMIInventorySlot.ChestPlate));
        }

        cmiGui.addButton(chestButton);
      }

      if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Pants) != null || flag) {
        CMIGuiButton legsButton =
            new CMIGuiButton(
                KitsManager.CMIKitGUILayout.Legs.getSlot(),
                flag ? new ItemStack(Material.CHAINMAIL_LEGGINGS) : null) {
              public void click(GUIClickType clickType) {
                if (flag) {
                  ItemStack cursorItem = player.getItemOnCursor();
                  if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Pants) != null) {
                    player.setItemOnCursor(
                        kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Pants));
                    kit.setExtraItem(CMIPlayerInventory.CMIInventorySlot.Pants, null);
                  } else {
                    player.setItemOnCursor(null);
                  }

                  if (cursorItem != null && cursorItem.getType() != Material.AIR) {
                    kit.setExtraItem(CMIPlayerInventory.CMIInventorySlot.Pants, cursorItem.clone());
                  }

                  CMI.getInstance().getKitsManager().openGuiEditorMain(kit, player, true);
                }
              }
            };
        if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Pants) == null
            || kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Pants).getType()
                == Material.AIR) {
          legsButton.setName(CMILC.getIM("kiteditor", "Legs"));
          legsButton.hideItemFlags();
        } else if (flag) {
          legsButton =
              legsButton.setItem(kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Pants));
        } else {
          legsButton =
              legsButton.setItem(
                  kit.getExtraItem(player, CMIPlayerInventory.CMIInventorySlot.Pants));
        }

        cmiGui.addButton(legsButton);
      }

      if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Boots) != null || flag) {
        CMIGuiButton bootsButton =
            new CMIGuiButton(
                KitsManager.CMIKitGUILayout.Boots.getSlot(),
                flag ? new ItemStack(Material.CHAINMAIL_BOOTS) : null) {
              public void click(GUIClickType clickType) {
                if (flag) {
                  ItemStack cursorItem = player.getItemOnCursor();
                  if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Boots) != null) {
                    player.setItemOnCursor(
                        kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Boots));
                    kit.setExtraItem(CMIPlayerInventory.CMIInventorySlot.Boots, null);
                  } else {
                    player.setItemOnCursor(null);
                  }

                  if (cursorItem != null && cursorItem.getType() != Material.AIR) {
                    kit.setExtraItem(CMIPlayerInventory.CMIInventorySlot.Boots, cursorItem.clone());
                  }

                  CMI.getInstance().getKitsManager().openGuiEditorMain(kit, player, true);
                }
              }
            };
        if (kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Boots) == null
            || kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Boots).getType()
                == Material.AIR) {
          bootsButton.setName(CMILC.getIM("kiteditor", "Boots"));
          bootsButton.hideItemFlags();
        } else if (flag) {
          bootsButton =
              bootsButton.setItem(kit.getExtraItem(CMIPlayerInventory.CMIInventorySlot.Boots));
        } else {
          bootsButton =
              bootsButton.setItem(
                  kit.getExtraItem(player, CMIPlayerInventory.CMIInventorySlot.Boots));
        }

        cmiGui.addButton(bootsButton);
      }

      if (flag) {
        CMIGuiButton enabledButton =
            new CMIGuiButton(
                KitsManager.CMIKitGUILayout.Enabled.getSlot(),
                kit.isEnabled()
                    ? CMIMaterial.GREEN_STAINED_GLASS_PANE.newItemStack()
                    : CMIMaterial.RED_STAINED_GLASS_PANE.newItemStack()) {
              public void click(GUIClickType clickType) {
                if (flag) {
                  KitsManager.this.openGuiEditorSettings(kit, player, flag);
                }
              }
            };
        enabledButton.setName(
            kit.isEnabled()
                ? CMILC.getIM("kiteditor", "enabled")
                : CMILC.getIM("kiteditor", "disabled"));
        cmiGui.addButton(enabledButton);
      }

      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (!flag) {
        if (KitsManager.CMIKitGUILayout.Delay.getIcon() != null) {
          CMIGuiButton delayButton =
              new CMIGuiButton(13, KitsManager.CMIKitGUILayout.Delay.getIcon());
          delayButton.setName(
              kit.getDelay() == -1L
                  ? CMILC.getIM("kit", "limitedTimeUse", "[times]", cmiUser.getLeftKitUseTimes(kit))
                  : CMILC.getIM(
                      "kiteditor",
                      "timeDelay",
                      "[time]",
                      CMITimeManager.to24hourShort(kit.getDelay() * 1000L)));
          if (kit.getDelay() > -1L) {
            delayButton.addLore(
                CMILC.getIM("kiteditor", "timeDelaySeconds", "[time]", kit.getDelay()));
          }

          cmiGui.addButton(delayButton);
        }

        if (kit.getMaxUsages() > 0 && KitsManager.CMIKitGUILayout.Usages.getIcon() != null) {
          CMIGuiButton usagesButton =
              new CMIGuiButton(4, KitsManager.CMIKitGUILayout.Usages.getIcon());
          usagesButton.setName(CMILC.getIM("kiteditor", "usages", "[times]", kit.getMaxUsages()));
          cmiGui.addButton(usagesButton);
        }

        if (kit.getCost() != 0.0 && KitsManager.CMIKitGUILayout.Money.getIcon() != null) {
          CMIGuiButton moneyButton =
              new CMIGuiButton(14, KitsManager.CMIKitGUILayout.Money.getIcon());
          moneyButton.setName(CMILC.getIM("kiteditor", "moneyCost", "[cost]", kit.getCost()));
          cmiGui.addButton(moneyButton);
        }

        if (kit.getExpCost() != 0 && KitsManager.CMIKitGUILayout.Exp.getIcon() != null) {
          CMIGuiButton expButton = new CMIGuiButton(15, KitsManager.CMIKitGUILayout.Exp.getIcon());
          expButton.setName(CMILC.getIM("kiteditor", "expCost", "[cost]", kit.getExpCost()));
          cmiGui.addButton(expButton);
        }

        if (!kit.getDescription().isEmpty()
            && KitsManager.CMIKitGUILayout.Description.getIcon() != null) {
          CMIGuiButton descriptionButton =
              new CMIGuiButton(16, KitsManager.CMIKitGUILayout.Description.getIcon());
          descriptionButton.setName(CMILC.getIM("kiteditor", "desc"));

          for (String string : kit.getDescription()) {
            descriptionButton.addLore(CMIChatColor.translate(string));
          }

          cmiGui.addButton(descriptionButton);
        }

        if (KitsManager.CMIKitGUILayout.Back.getIcon() != null) {
          CMIGuiButton backButton = new CMIGuiButton(8, KitsManager.CMIKitGUILayout.Back.getIcon());
          backButton.setName(LC.info_Back.getLocale(new Object[0]));
          backButton.addCommand("cmi kit");
          cmiGui.addButton(backButton);
        }
      }

      if (flag) {
        CMIGuiButton settingsButton =
            new CMIGuiButton(
                KitsManager.CMIKitGUILayout.Settings.getSlot(),
                CMIMaterial.CRAFTING_TABLE.newItemStack()) {
              public void click(GUIClickType clickType) {
                if (flag) {
                  KitsManager.this.openGuiEditorSettings(kit, player, flag);
                }
              }
            };
        settingsButton.setName(CMILC.getIM("kiteditor", "settings"));
        cmiGui.addButton(settingsButton);
      }

      var varSecond = kit.getItems();
      if (!flag) {
        varSecond = kit.getItems(player);
      }

      for (int slotIndex = 0; slotIndex < 36; slotIndex++) {
        if (slotIndex >= varSecond.size()) {
          CMIGuiButton emptySlotButton =
              new CMIGuiButton(slotIndex + 18) {
                public void click(GUIClickType clickType) {
                  if (flag) {
                    KitsManager.ItemClickInGui(player, kit);
                  }
                }
              };
          if (!flag) {
            emptySlotButton.lockField();
          } else {
            emptySlotButton.unlockField();
          }

          cmiGui.addButton(emptySlotButton);
        } else {
          CMIGuiButton itemSlotButton =
              new CMIGuiButton(slotIndex + 18, (ItemStack) varSecond.get(slotIndex)) {
                public void click(GUIClickType clickType) {
                  if (flag) {
                    KitsManager.ItemClickInGui(player, kit);
                  }
                }
              };
          if (!flag) {
            itemSlotButton.lockField();
          } else {
            itemSlotButton.unlockField();
          }

          cmiGui.addButton(itemSlotButton);
        }
      }

      cmiGui.fillEmptyButtons();
      cmiGui.open();
    }
  }

  public static void saveOnClose() {
    CMI.getInstance().getKitsManager().save();
  }

  public static void ItemClickInGui(Player player, Kit kit) {
    CMIScheduler.runTaskLater(
        CMI.getInstance(),
        () -> {
          ItemStack[] itemStacks = CMIPlayerInventory.getTopInventoryContents(player);
          ArrayList<ItemStack> arrayList = new ArrayList<>();

          for (int value = 18; value < (itemStacks.length < 54 ? itemStacks.length : 54); value++) {
            ItemStack itemStack = itemStacks[value];
            arrayList.add(itemStack == null ? null : itemStack.clone());
          }

          kit.setItem(arrayList);
        },
        1L);
  }

  public static void openGuiEditorSettingsPreview(Player player, Kit kit) {
    CMI.getInstance().getKitsManager().openGuiEditorSettings(kit, player, false);
  }

  public void openGuiEditorSettings(final Kit kit, final Player player, final boolean flag) {
    final CMIGui cmiGui =
        new CMIGui(player) {
          public void onClose() {
            KitsManager.this.safeSave();
          }
        };
    cmiGui.setTitle(
        CMILC.getIM(
            kit.class,
            "kitGuiTitle",
            "[kitName]",
            kit.getCommandName(),
            "[kitDisplayName]",
            kit.getDisplayName()));
    cmiGui.setInvSize(6);
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(
            cmiGui.getSlot(GUIButtonLocation.bottomRight), CMIMaterial.BARRIER.newItemStack()) {
          public void click(GUIClickType guiClickType) {
            KitsManager.this.startChatKitRemove(player, kit, false);
            player.closeInventory();
          }
        };
    cmiGuiButton.setName(CMILC.getIM("kiteditor", "delete"));
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(
            KitsManager.CMIKitGUILayout.MainMenu.getSlot(),
            CMIMaterial.CRAFTING_TABLE.newItemStack()) {
          public void click(GUIClickType clickType) {
            KitsManager.this.openGuiEditorMain(kit, player, flag);
          }
        };
    cmiGuiButton.setName(CMILC.getIM("kiteditor", "mainMenu"));
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(
            KitsManager.CMIKitGUILayout.Delay.getSlot(), CMIMaterial.CLOCK.newItemStack()) {
          public void click(GUIClickType clickType) {
            if (flag) {
              int value = KitsManager.getSpeed(player.getUniqueId());
              switch (clickType) {
                case Left:
                  kit.setDelay(kit.getDelay() + 1 * value);
                  break;
                case LeftShift:
                  kit.setDelay(kit.getDelay() + 10 * value);
                  break;
                case Right:
                  kit.setDelay(kit.getDelay() - 1 * value);
                  if (kit.getDelay() < -1L) {
                    kit.setDelay(-1L);
                  }
                  break;
                case RightShift:
                  kit.setDelay(kit.getDelay() - 10 * value);
                  if (kit.getDelay() < -1L) {
                    kit.setDelay(-1L);
                  }
              }

              this.updateLooks();
              this.update(cmiGui);
            }
          }

          public void updateLooks() {
            this.setName(
                CMILC.getIM(
                    "kiteditor",
                    "timeDelay",
                    "[time]",
                    CMITimeManager.to24hourShort(kit.getDelay() * 1000L)));
            this.clearLore();
            if (kit.getDelay() > -1L) {
              this.addLore(CMILC.getIM("kiteditor", "timeDelaySeconds", "[time]", kit.getDelay()));
            }

            if (flag) {
              this.addLore(CMILC.getIML("kiteditor", "guiClickery"));
            }
          }
        };
    cmiGui.addButton(cmiGuiButton);
    if (flag) {
      cmiGuiButton =
          new CMIGuiButton(
              KitsManager.CMIKitGUILayout.ShowWithoutUsage.getSlot(),
              CMIMaterial.TORCH.newItemStack()) {
            public void click(GUIClickType clickType) {
              if (flag) {
                kit.setShowDespiteUsage(!kit.isShowDespiteUsage());
                CMI.getInstance().getKitsManager().openGuiEditorSettings(kit, player, true);
              }
            }
          };
      cmiGuiButton.setName(
          CMILC.getIM("kiteditor", "withoutUsage", "[state]", kit.isShowDespiteUsage()));
      cmiGui.addButton(cmiGuiButton);
    }

    cmiGuiButton =
        new CMIGuiButton(
            KitsManager.CMIKitGUILayout.Usages.getSlot(),
            CMIMaterial.REDSTONE_TORCH.newItemStack()) {
          public void click(GUIClickType clickType) {
            if (flag) {
              switch (clickType) {
                case Left:
                  if (kit.getMaxUsages() == -1) {
                    kit.setMaxUsages(0);
                  }

                  kit.setMaxUsages(kit.getMaxUsages() + 1);
                  break;
                case LeftShift:
                  if (kit.getMaxUsages() == -1) {
                    kit.setMaxUsages(0);
                  }

                  kit.setMaxUsages(kit.getMaxUsages() + 10);
                  break;
                case Right:
                  kit.setMaxUsages(kit.getMaxUsages() - 1);
                  if (kit.getMaxUsages() < 1) {
                    kit.setMaxUsages(-1);
                  }
                  break;
                case RightShift:
                  kit.setMaxUsages(kit.getMaxUsages() - 10);
                  if (kit.getMaxUsages() < 1) {
                    kit.setMaxUsages(-1);
                  }
              }

              CMI.getInstance().getKitsManager().openGuiEditorSettings(kit, player, true);
            }
          }
        };
    cmiGuiButton.setName(
        CMILC.getIM(
            "kiteditor",
            "usages",
            "[times]",
            kit.getMaxUsages() == -1
                ? CMILC.getIM("kiteditor", "usagesUnlimited")
                : kit.getMaxUsages()));
    if (flag) {
      cmiGuiButton.addLore(CMILC.getIML("kiteditor", "guiClickery"));
    }

    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(
            KitsManager.CMIKitGUILayout.Money.getSlot(), new ItemStack(Material.GOLD_INGOT)) {
          public void click(GUIClickType clickType) {
            if (flag) {
              int speed = KitsManager.getSpeed(player.getUniqueId());
              switch (clickType) {
                case Left:
                  kit.setCost(kit.getCost() + 1 * speed);
                  break;
                case LeftShift:
                  kit.setCost(kit.getCost() + 10 * speed);
                  break;
                case Right:
                  kit.setCost(kit.getCost() - 1 * speed);
                  if (kit.getCost() < 0.0) {
                    kit.setCost(0.0);
                  }
                  break;
                case RightShift:
                  kit.setCost(kit.getCost() - 10 * speed);
                  if (kit.getCost() < 0.0) {
                    kit.setCost(0.0);
                  }
              }

              CMI.getInstance().getKitsManager().openGuiEditorSettings(kit, player, true);
            }
          }
        };
    cmiGuiButton.setName(CMILC.getIM("kiteditor", "moneyCost", "[cost]", kit.getCost()));
    if (flag) {
      cmiGuiButton.addLore(CMILC.getIML("kiteditor", "guiClickery"));
    }

    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(
            KitsManager.CMIKitGUILayout.Exp.getSlot(),
            CMIMaterial.EXPERIENCE_BOTTLE.newItemStack()) {
          public void click(GUIClickType clickType) {
            if (flag) {
              int speed = KitsManager.getSpeed(player.getUniqueId());
              switch (clickType) {
                case Left:
                  kit.setExpCost(kit.getExpCost() + 1 * speed);
                  break;
                case LeftShift:
                  kit.setExpCost(kit.getExpCost() + 10 * speed);
                  break;
                case Right:
                  kit.setExpCost(kit.getExpCost() - 1 * speed);
                  if (kit.getExpCost() < 0) {
                    kit.setExpCost(0);
                  }
                  break;
                case RightShift:
                  kit.setExpCost(kit.getExpCost() - 10 * speed);
                  if (kit.getExpCost() < 0) {
                    kit.setExpCost(0);
                  }
              }

              CMI.getInstance().getKitsManager().openGuiEditorSettings(kit, player, true);
            }
          }
        };
    cmiGuiButton.setName(CMILC.getIM("kiteditor", "expCost", "[cost]", kit.getExpCost()));
    if (flag) {
      cmiGuiButton.addLore(CMILC.getIML("kiteditor", "guiClickery"));
    }

    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(
            KitsManager.CMIKitGUILayout.Enabled.getSlot(),
            kit.isEnabled()
                ? CMIMaterial.GREEN_STAINED_GLASS_PANE.newItemStack()
                : CMIMaterial.RED_STAINED_GLASS_PANE.newItemStack()) {
          public void click(GUIClickType clickType) {
            if (flag) {
              kit.setEnabled(!kit.isEnabled());
              CMI.getInstance().getKitsManager().openGuiEditorSettings(kit, player, true);
            }
          }
        };
    cmiGuiButton.setName(
        kit.isEnabled()
            ? CMILC.getIM("kiteditor", "enabled")
            : CMILC.getIM("kiteditor", "disabled"));
    cmiGuiButton.addLore(CMILC.getIM("kiteditor", "clickToSwitch"));
    cmiGui.addButton(cmiGuiButton);
    if (flag) {
      cmiGuiButton =
          new CMIGuiButton(
              KitsManager.CMIKitGUILayout.commandName.getSlot(),
              CMIMaterial.CYAN_WOOL.newItemStack()) {
            public void click(GUIClickType clickType) {
              KitsManager.this.startChatKitCommandRename(player, kit);
            }
          };
      cmiGuiButton.setName(CMIChatColor.GOLD + kit.getCommandName());
      cmiGuiButton.addLore(CMILC.getIML("kiteditor", "editCommandName"));
      cmiGuiButton.setCloseInv(true);
      cmiGui.addButton(cmiGuiButton);
    }

    if (flag) {
      cmiGuiButton =
          new CMIGuiButton(
              KitsManager.CMIKitGUILayout.configName.getSlot(),
              CMIMaterial.LIGHT_GRAY_WOOL.newItemStack()) {
            public void click(GUIClickType clickType) {
              KitsManager.this.startChatKitRename(player, kit);
            }
          };
      cmiGuiButton.setName(CMIChatColor.GOLD + kit.getConfigName());
      cmiGuiButton.addLore(CMILC.getIML("kiteditor", "editConfigName"));
      cmiGuiButton.setCloseInv(true);
      cmiGui.addButton(cmiGuiButton);
    }

    if (flag) {
      cmiGuiButton =
          new CMIGuiButton(
              KitsManager.CMIKitGUILayout.displayName.getSlot(), CMIMaterial.NAME_TAG) {
            public void click() {
              player.closeInventory();
              ChatMessageEdit chatMessageEdit =
                  new ChatMessageEdit(player, kit.getDisplayName()) {
                    public void run(String string) {
                      if (CMIText.isValidString(string)) {
                        kit.setDisplayName(string);
                        KitsManager.this.openGuiEditorSettings(kit, player, flag);
                      }
                    }

                    public void onCancel() {
                      KitsManager.this.openGuiEditorSettings(kit, player, flag);
                    }
                  };
              chatMessageEdit.setCheckForCancel(true);
              chatMessageEdit.printMessage();
            }
          };
      cmiGuiButton.setName(
          CMILC.getIM(
              kiteditor.class.getSimpleName(), "displayName", "[name]", kit.getDisplayName()));
      cmiGuiButton.addLore(CMILC.getIML(kiteditor.class.getSimpleName(), "editDisplayName"));
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(KitsManager.CMIKitGUILayout.fileName.getSlot(), CMIMaterial.BOOK) {
            public void click() {
              player.closeInventory();
              ChatMessageEdit chatEdit =
                  new ChatMessageEdit(player, kit.getFileName()) {
                    public void run(String input) {
                      if (CMIText.isValidString(input)) {
                        kit.setFileName(input.split("//.")[0]);
                        KitsManager.this.openGuiEditorSettings(kit, player, flag);
                      }
                    }

                    public void onCancel() {
                      KitsManager.this.openGuiEditorSettings(kit, player, flag);
                    }
                  };
              chatEdit.setCheckForCancel(true);
              chatEdit.printMessage();
            }
          };
      cmiGuiButton.setName(
          CMILC.getIM(kiteditor.class.getSimpleName(), "fileName", "[name]", kit.getFileName()));
      cmiGuiButton.addLore(CMILC.getIML(kiteditor.class.getSimpleName(), "editFileName"));
      cmiGui.addButton(cmiGuiButton);
    }

    cmiGuiButton =
        new CMIGuiButton(
            KitsManager.CMIKitGUILayout.ShowDespiteWeight.getSlot(),
            kit.isShowDespiteWeight()
                ? CMIMaterial.GREEN_WOOL.newItemStack()
                : CMIMaterial.RED_WOOL.newItemStack()) {
          public void click(GUIClickType clickType) {
            if (flag) {
              kit.setShowDespiteWeight(!kit.isShowDespiteWeight());
              CMI.getInstance().getKitsManager().openGuiEditorSettings(kit, player, true);
            }
          }
        };
    cmiGuiButton.setName(
        CMILC.getIM("kiteditor", "ignoreWeight", "[state]", kit.isShowDespiteWeight()));
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(
            KitsManager.CMIKitGUILayout.Weight.getSlot(), new ItemStack(Material.ANVIL)) {
          public void click(GUIClickType clickType) {
            if (flag) {
              switch (clickType) {
                case Left:
                  kit.setWeight(kit.getWeight() + 1);
                  break;
                case LeftShift:
                  kit.setWeight(kit.getWeight() + 10);
                  break;
                case Right:
                  kit.setWeight(kit.getWeight() - 1);
                  if (kit.getWeight() < 0) {
                    kit.setWeight(0);
                  }
                  break;
                case RightShift:
                  kit.setWeight(kit.getWeight() - 10);
                  if (kit.getWeight() < 0) {
                    kit.setWeight(0);
                  }
              }

              CMI.getInstance().getKitsManager().openGuiEditorSettings(kit, player, true);
            }
          }

          public void updateLooks() {
            this.setName(CMILC.getIM("kiteditor", "Weight", "[Weight]", kit.getWeight()));
          }
        };
    cmiGuiButton.addLore(CMILC.getIML("kiteditor", "WeightExtra"));
    cmiGui.addButton(cmiGuiButton);
    if (flag) {
      cmiGuiButton =
          new CMIGuiButton(
              KitsManager.CMIKitGUILayout.Slot.getSlot(), new ItemStack(Material.ANVIL)) {
            public void click(GUIClickType clickType) {
              if (flag) {
                Integer integer = kit.getSlot() == null ? -1 : kit.getSlot();
                switch (clickType) {
                  case Left:
                    kit.setSlot(integer + 1);
                    break;
                  case LeftShift:
                    kit.setSlot(integer + 10);
                    break;
                  case Right:
                    kit.setSlot(integer - 1);
                    break;
                  case RightShift:
                    kit.setSlot(integer - 10);
                }

                this.updateLooks();
                this.update(cmiGui);
              }
            }

            public void updateLooks() {
              this.setName(
                  CMILC.getIM(
                      "kiteditor",
                      "Slot",
                      "[slot]",
                      kit.getSlot() == null
                          ? CMILC.getIM("kiteditor", "SlotAuto")
                          : kit.getSlot() + 1));
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(
              KitsManager.CMIKitGUILayout.Page.getSlot(), new ItemStack(Material.PAPER)) {
            public void click(GUIClickType clickType) {
              if (flag) {
                Integer page = kit.getPage() == null ? 0 : kit.getPage();
                switch (clickType) {
                  case Left:
                    kit.setPage(page + 1);
                  case LeftShift:
                  default:
                    break;
                  case Right:
                    kit.setPage(page - 1);
                }

                this.updateLooks();
                this.update(cmiGui);
              }
            }

            public void updateLooks() {
              this.setName(
                  CMILC.getIM(
                      "kiteditor",
                      "Page",
                      "[page]",
                      kit.getPage() == null
                          ? CMILC.getIM("kiteditor", "SlotAuto")
                          : kit.getPage()));
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(KitsManager.CMIKitGUILayout.DropItems.getSlot(), CMIMaterial.INK_SAC) {
            public void click(GUIClickType clickType) {
              kit.setDropItems(!kit.isDropItems());
              this.updateLooks();
              this.update(cmiGui);
            }

            public void updateLooks() {
              this.setName(CMILC.getIM("kiteditor", "dropItems", "[state]", kit.isDropItems()));
            }
          };
      cmiGui.addButton(cmiGuiButton);
    }

    cmiGuiButton =
        new CMIGuiButton(
            KitsManager.CMIKitGUILayout.Group.getSlot(), new ItemStack(Material.ANVIL)) {
          public void click(GUIClickType clickType) {
            if (flag) {
              KitsManager.this.startChatKitGroupRename(player, kit);
            }
          }
        };
    cmiGuiButton.setName(CMILC.getIM("kiteditor", "kitGroup", "[group]", kit.getGroup()));
    cmiGuiButton.addLore(CMILC.getIM("kiteditor", "clickToEdit"));
    if (flag) {
      cmiGuiButton.setCloseInv(true);
    }

    cmiGui.addButton(cmiGuiButton);
    if (PermissionsManager.CMIPerm.command_kiteditor_admin.hasPermission(player)) {
      cmiGuiButton =
          new CMIGuiButton(
              KitsManager.CMIKitGUILayout.Commands.getSlot(),
              CMIMaterial.COMMAND_BLOCK.newItemStack()) {
            public void click(GUIClickType clickType) {
              if (flag) {
                ChatMessageListEdit chatMessageListEdit =
                    new ChatMessageListEdit(player, kit.getCommands(), ChatEditType.String) {
                      public void onUpdate() {
                        KitsManager.this.plugin.getKitsManager().safeSave();
                      }
                    };
                final Kit kitForSettings = kit;
                RawMessage rawMessage = new RawMessage();
                RawMessageCommand rawMessageCommand =
                    new RawMessageCommand() {
                      public void run(CommandSender commandSender) {
                        KitsManager.this
                            .plugin
                            .getKitsManager()
                            .openGuiEditorSettings(kitForSettings, player, true);
                      }
                    };
                rawMessageCommand.setKeep(true);
                rawMessage
                    .addText(CMILC.getIM(kiteditor.class, "commandsListTitle"))
                    .addHover(CMILC.getIM(kiteditor.class, "ListTitleHover"))
                    .addCommand(rawMessageCommand.getCommand());
                chatMessageListEdit.setTopLine(rawMessage);
                chatMessageListEdit.print();
              }
            }
          };
      cmiGuiButton.setName(CMILC.getIM("kiteditor", "commands"));

      for (String command : kit.getCommands()) {
        cmiGuiButton.addLore(CMIChatColor.translate(command));
      }

      if (flag) {
        cmiGuiButton.addLore(CMILC.getIM("kiteditor", "clickToEdit"));
        cmiGuiButton.setCloseInv(true);
      }

      cmiGui.addButton(cmiGuiButton);
    }

    cmiGuiButton =
        new CMIGuiButton(
            KitsManager.CMIKitGUILayout.Conditions.getSlot(),
            CMIMaterial.BROWN_WOOL.newItemStack()) {
          public void click(GUIClickType clickType) {
            if (flag) {
              ChatMessageListEdit commandsEdit =
                  new ChatMessageListEdit(player, kit.getConditions(), ChatEditType.String) {
                    public void onUpdate() {
                      KitsManager.this.plugin.getKitsManager().safeSave();
                    }
                  };
              final Kit kitForCommands = kit;
              RawMessage commandMessage = new RawMessage();
              RawMessageCommand commandAction =
                  new RawMessageCommand() {
                    public void run(CommandSender sender) {
                      KitsManager.this
                          .plugin
                          .getKitsManager()
                          .openGuiEditorSettings(kitForCommands, player, true);
                    }
                  };
              commandAction.setKeep(true);
              commandMessage
                  .addText(CMILC.getIM(kiteditor.class, "conditionsListTitle"))
                  .addHover(CMILC.getIM(kiteditor.class, "ListTitleHover"))
                  .addCommand(commandAction.getCommand());
              commandsEdit.setTopLine(commandMessage);
              commandsEdit.print();
            }
          }
        };
    cmiGuiButton.setName(CMILC.getIM("kiteditor", "conditions"));

    for (String condition : kit.getConditions()) {
      cmiGuiButton.addLore(CMIChatColor.translate(condition));
    }

    if (flag) {
      cmiGuiButton.addLore(CMILC.getIM("kiteditor", "clickToEdit"));
      cmiGuiButton.setCloseInv(true);
    }

    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(
            KitsManager.CMIKitGUILayout.Description.getSlot(),
            CMIMaterial.GREEN_WOOL.newItemStack()) {
          public void click(GUIClickType clickType) {
            if (flag) {
              ChatMessageListEdit conditionsEdit =
                  new ChatMessageListEdit(player, kit.getDescription(), ChatEditType.String) {
                    public void onUpdate() {
                      KitsManager.this.plugin.getKitsManager().safeSave();
                    }
                  };
              final Kit kitForConditions = kit;
              RawMessage conditionMessage = new RawMessage();
              RawMessageCommand conditionAction =
                  new RawMessageCommand() {
                    public void run(CommandSender sender) {
                      KitsManager.this
                          .plugin
                          .getKitsManager()
                          .openGuiEditorSettings(kitForConditions, player, true);
                    }
                  };
              conditionAction.setKeep(true);
              conditionMessage.add(
                  CMILC.getIM(kiteditor.class, "descListTitle"),
                  CMILC.getIM(kiteditor.class, "ListTitleHover"),
                  conditionAction.getCommand());
              conditionsEdit.setTopLine(conditionMessage);
              conditionsEdit.print();
            }
          }
        };
    cmiGuiButton.setName(CMILC.getIM("kiteditor", "desc"));

    for (String descriptionLine : kit.getDescription()) {
      cmiGuiButton.addLore(CMIChatColor.translate(descriptionLine));
    }

    if (flag) {
      cmiGuiButton.addLore(CMILC.getIM(kiteditor.class, "clickToEdit"));
      cmiGuiButton.setCloseInv(true);
    }

    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(KitsManager.CMIKitGUILayout.Icon.getSlot(), kit.getSafeIcon()) {
          public void click(GUIClickType clickType) {
            if (flag) {
              ItemStack itemStack = player.getItemOnCursor();
              if (kit.getIcon() != null) {
                kit.setIcon(null);
              }

              if (itemStack != null && itemStack.getType() != Material.AIR) {
                kit.setIcon(itemStack.clone());
              }

              this.setItem(kit.getSafeIcon());
              this.updateLooks();
              this.update(cmiGui);
            }
          }

          public void updateLooks() {
            this.setName(CMILC.getIM("kiteditor", "icon"));
          }
        };
    if (flag) {
      cmiGuiButton.addLore(LC.modify_newItem.getLocale(new Object[0]));
    }

    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(KitsManager.CMIKitGUILayout.IconCD.getSlot(), kit.getSafeIconOff()) {
          public void click(GUIClickType clickType) {
            if (flag) {
              ItemStack cursorItem = player.getItemOnCursor();
              if (kit.getSafeIconOff() != null) {
                kit.setIconOff(null);
              }

              if (cursorItem != null && cursorItem.getType() != Material.AIR) {
                kit.setIconOff(cursorItem.clone());
              }

              this.setItem(kit.getSafeIconOff());
              this.updateLooks();
              this.update(cmiGui);
            }
          }

          public void updateLooks() {
            this.setName(CMILC.getIM("kiteditor", "iconCd"));
          }
        };
    if (flag) {
      cmiGuiButton.addLore(LC.modify_newItem.getLocale(new Object[0]));
    }

    cmiGui.addButton(cmiGuiButton);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }

  private static int getSpeed(UUID uuid) {
    Clickery clickery = clickeryMap.get(uuid);
    int value = 1;
    if (clickery == null) {
      clickeryMap.put(uuid, new Clickery());
    } else {
      value = clickery.getSpeed();
    }

    return value;
  }

  @Deprecated
  public CMIItemStack getKitPreviewIcon(KitsManager.CMIKitGUILayout cmiKitGUILayout) {
    return cmiKitGUILayout.getIcon();
  }

  public boolean isKitsGUI() {
    return this.KitsGUI;
  }

  public boolean isKitsGUIFillEmptyFields() {
    return this.KitsGUIFillEmptyFields;
  }

  public enum CMIKitGUILayout {
    Separator(-1),
    OffHand(11),
    Helmet(0),
    Chest(9),
    Legs(1),
    Boots(10),
    Clone(4),
    configName(39),
    commandName(40),
    fileName(48),
    displayName(49),
    ShowDespiteWeight(36),
    Weight(37),
    Group(28),
    Slot(15),
    Page(6),
    DropItems(35),
    Icon(12),
    IconCD(13),
    Delay(10),
    Money(21),
    Exp(22),
    Back(9),
    Commands(33),
    Conditions(42),
    ShowWithoutUsage(18),
    Usages(19),
    Description(24),
    Enabled(8),
    Settings(17),
    MainMenu(17);

    private int slots;
    private CMIItemStack icon = null;

    CMIKitGUILayout(Integer integer) {
      this.slots = integer;
    }

    public Integer getSlot() {
      return this.slots;
    }

    public static KitsManager.CMIKitGUILayout getButton(int value) {
      KitsManager.CMIKitGUILayout[] cmiKitGUILayouts;
      int layoutCount = (cmiKitGUILayouts = values()).length;

      for (int layoutIndex = 0; layoutIndex < layoutCount; layoutIndex++) {
        KitsManager.CMIKitGUILayout cmiKitGUILayout = cmiKitGUILayouts[layoutIndex];
        if (cmiKitGUILayout.getSlot() == value) {
          return cmiKitGUILayout;
        }
      }

      return null;
    }

    public CMIItemStack getIcon() {
      return this.icon;
    }

    public void setIcon(CMIItemStack cmiItemStack) {
      this.icon = cmiItemStack;
    }
  }
}
