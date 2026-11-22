package com.Zrips.CMI.Modules.SavedItems;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIPaperItemSerializator;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.utils.Util;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Messages.CMIMessages;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SavedItemManager {
  private LinkedHashMap<String, LinkedHashMap<String, SavedItem>> map = new LinkedHashMap<>();
  public static final String defaultCategory = "dc";
  private CMI plugin;
  public String fileName = "SavedItems.yml";

  public SavedItemManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void addSavedItem(SavedItem savedItem) {
    String string = savedItem.getCategory();
    if (string == null) {
      string = "dc";
    }

    string = string.toLowerCase();
    var linkedHashMap = this.map.getOrDefault(string, new LinkedHashMap<>());
    linkedHashMap.put(savedItem.getName().toLowerCase(), savedItem);
    this.map.put(string, linkedHashMap);
  }

  public boolean removeSavedItem(SavedItem savedItem) {
    return this.removeSavedItem(savedItem.getCategory(), savedItem.getName());
  }

  public boolean removeSavedItem(String string, String name) {
    if (string == null) {
      string = "dc";
    }

    string = string.toLowerCase();
    var linkedHashMap = this.map.get(string);
    if (linkedHashMap == null) {
      return false;
    }

    boolean flag = linkedHashMap.remove(name.toLowerCase()) != null;
    if (flag) {
      this.save();
    }

    return flag;
  }

  public SavedItem getSavedItem(String string, String name) {
    if (name == null) {
      return null;
    }

    if (string == null) {
      string = "dc";
    }

    string = string.toLowerCase();
    var linkedHashMap = this.map.get(string);
    return linkedHashMap == null ? null : (SavedItem) linkedHashMap.get(name.toLowerCase());
  }

  public LinkedHashMap<String, LinkedHashMap<String, SavedItem>> getSavedItemMap() {
    return this.map;
  }

  public Set<SavedItem> getSavedItems(String string) {
    LinkedHashSet<SavedItem> linkedHashSet = new LinkedHashSet();
    if (string == null) {
      string = "dc";
    }

    string = string.toLowerCase();
    if (string.equalsIgnoreCase("all")) {
      for (var linkedHashMap : this.map.values()) {
        for (SavedItem savedItem : linkedHashMap.values()) {
          linkedHashSet.add(savedItem);
        }
      }
    }

    var itemsByName = this.map.get(string);
    if (itemsByName == null) {
      return linkedHashSet;
    }

    for (SavedItem savedItem : itemsByName.values()) {
      linkedHashSet.add(savedItem);
    }

    return linkedHashSet;
  }

  public void load() {
    Long startTime = System.currentTimeMillis();
    CMI.getInstance().safeRenameFile(this.fileName, "Saves" + File.separator + this.fileName);
    Util.fixItemStackFile(
        new File(this.plugin.getDataFolder(), "Saves" + File.separator + this.fileName));
    File file = new File(this.plugin.getDataFolder(), "Saves" + File.separator + this.fileName);
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    } else {
      YamlConfiguration yamlConfiguration = null;

      try {
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
      } catch (Throwable throwable) {
        return;
      }

      this.map.clear();
      if (!yamlConfiguration.getKeys(false).isEmpty()) {
        ArrayList<String> arrayList = new ArrayList();

        for (var entry : this.map.entrySet()) {
          for (SavedItem savedItem :
              ((Iterable<SavedItem>) ((LinkedHashMap) entry.getValue()).values())) {
            arrayList.add(savedItem.getCategory() + "-" + savedItem.getName().toLowerCase());
          }
        }

        var itemValues = yamlConfiguration.getValues(false);
        boolean flag = false;
        Iterator iterator = itemValues.entrySet().iterator();
        if (iterator.hasNext()) {
          Entry entryItem = (Entry) iterator.next();

          try {
            var entryValues =
                yamlConfiguration
                    .getConfigurationSection((String) entryItem.getKey())
                    .getValues(false);
            if (entryValues.containsKey("Item")) {
              try {
                Util.fixHeadNameSection(
                    yamlConfiguration.getConfigurationSection(
                        (String) entryItem.getKey() + ".Item"));
                ItemStack.deserialize(
                    yamlConfiguration
                        .getConfigurationSection((String) entryItem.getKey() + ".Item")
                        .getValues(true));
                flag = true;
              } catch (Throwable throwable2) {
              }
            }
          } catch (Throwable throwable3) {
            throwable3.printStackTrace();
          }
        }

        if (flag) {
          iterator = itemValues.entrySet().iterator();

          label120:
          while (true) {
            SavedItem loadedItem;
            while (true) {
              if (!iterator.hasNext()) {
                break label120;
              }

              Entry secondEntry = (Entry) iterator.next();
              var secondValues =
                  yamlConfiguration
                      .getConfigurationSection((String) secondEntry.getKey())
                      .getValues(false);
              loadedItem = this.getSavedItem(null, (String) secondEntry.getKey());
              if (loadedItem == null) {
                loadedItem = new SavedItem((String) secondEntry.getKey());
              } else {
                loadedItem.reset();
              }

              try {
                if (secondValues.containsKey("Item")) {
                  Util.fixHeadNameSection(
                      yamlConfiguration.getConfigurationSection(
                          (String) secondEntry.getKey() + ".Item"));
                  ItemStack itemStack =
                      CMIPaperItemSerializator.getItem(
                          yamlConfiguration
                              .getConfigurationSection((String) secondEntry.getKey())
                              .get("Item"));
                  loadedItem.setItem(itemStack);
                }
                break;
              } catch (Throwable throwable4) {
                CMIMessages.sendMessage(
                    Bukkit.getConsoleSender(),
                    "Cant load saved item (" + (String) secondEntry.getKey() + ")");
                throwable4.printStackTrace();
              }
            }

            this.addSavedItem(loadedItem);
          }
        } else {
          label108:
          for (Entry categoryEntry : itemValues.entrySet()) {
            var categoryValues =
                yamlConfiguration
                    .getConfigurationSection((String) categoryEntry.getKey())
                    .getValues(false);
            Iterator categoryIterator = categoryValues.entrySet().iterator();

            while (true) {
              String string;
              SavedItem categoryItem;
              while (true) {
                if (!categoryIterator.hasNext()) {
                  continue label108;
                }

                Entry categoryPair = (Entry) categoryIterator.next();
                var categoryData =
                    yamlConfiguration
                        .getConfigurationSection(
                            (String) categoryEntry.getKey() + "." + (String) categoryPair.getKey())
                        .getValues(false);
                string = (String) categoryEntry.getKey();
                categoryItem = this.getSavedItem(string, (String) categoryPair.getKey());
                if (categoryItem == null) {
                  categoryItem = new SavedItem((String) categoryPair.getKey());
                } else {
                  categoryItem.reset();
                }

                categoryItem.setCategory(string);

                try {
                  if (categoryData.containsKey("Item")) {
                    ConfigurationSection configurationSection =
                        yamlConfiguration.getConfigurationSection(
                            (String) categoryEntry.getKey()
                                + "."
                                + (String) categoryPair.getKey()
                                + ".Item");
                    Util.fixHeadNameSection(configurationSection);
                    ItemStack savedStack =
                        CMIPaperItemSerializator.getItem(
                            yamlConfiguration
                                .getConfigurationSection(
                                    (String) categoryEntry.getKey()
                                        + "."
                                        + (String) categoryPair.getKey())
                                .get("Item"));
                    categoryItem.setItem(savedStack);
                  }
                  break;
                } catch (Throwable throwable5) {
                  CMIMessages.sendMessage(
                      Bukkit.getConsoleSender(),
                      "Cant load saved item (" + (String) categoryPair.getKey() + ")");
                  throwable5.printStackTrace();
                }
              }

              this.addSavedItem(categoryItem);
              arrayList.remove(string + "-" + categoryItem.getName().toLowerCase());
            }
          }
        }

        for (String category : arrayList) {
          String[] strings = category.split("-", 2);
          String categoryKey = null;
          if (!strings[0].equalsIgnoreCase("NULL")) {
            categoryKey = strings[0];
          }

          this.removeSavedItem(categoryKey, strings[1]);
        }

        if (!this.map.isEmpty()) {
          this.plugin.loadMessage(
              this.savedItemCount(), "saved items", System.currentTimeMillis() - startTime);
        }
      }
    }
  }

  public int savedItemCount() {
    int value = 0;

    for (var linkedHashMap : this.map.values()) {
      value += linkedHashMap.size();
    }

    return value;
  }

  public void save() {
    File file = new File(this.plugin.getDataFolder(), "Saves" + File.separator + this.fileName);
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    } else {
      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);

      for (String string : yamlConfiguration.getKeys(false)) {
        yamlConfiguration.set(string, null);
      }

      for (var entry : this.map.entrySet()) {
        for (SavedItem savedItem :
            ((Iterable<SavedItem>) ((LinkedHashMap) entry.getValue()).values())) {
          String itemName = savedItem.getName();
          if (yamlConfiguration.isConfigurationSection(itemName)) {
            yamlConfiguration.set(itemName, null);
          }

          String itemCategory = savedItem.getCategory();
          if (itemCategory == null) {
            itemCategory = "dc";
          }

          CMIPaperItemSerializator.setTo(
              yamlConfiguration, itemCategory + "." + itemName + ".Item", savedItem.getItem());
        }
      }

      try {
        yamlConfiguration.save(file);
      } catch (IOException ioException2) {
        ioException2.printStackTrace();
      }
    }
  }

  public ItemStack getUpatedItemClone(Player player, SavedItem savedItem) {
    ItemStack itemStack = savedItem.getItem().clone();
    CMIItemStack cmiItemStack = CMILib.getInstance().getItemManager().getItem(itemStack);
    if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
      String string = cmiItemStack.getDisplayName();
      String processedName = this.processText(string, player, savedItem);
      if (!string.equalsIgnoreCase(processedName)) {
        if (CMIChatColor.getColor(processedName) == null) {
          processedName = "&f" + processedName;
        }

        cmiItemStack.setDisplayName(processedName);
      }
    }

    var list = cmiItemStack.getLore();
    boolean flag = false;

    for (String loreLine : cmiItemStack.getLore()) {
      String processedLore = this.processText(loreLine, player, savedItem);
      if (!processedLore.equals(loreLine)) {
        flag = true;
      }

      list.add(processedLore);
    }

    if (flag) {
      cmiItemStack.clearLore();
      cmiItemStack.setLore(list);
    }

    return itemStack;
  }

  public String processText(String string, Player player, SavedItem savedItem) {
    string = CMIChatColor.translate(string);
    Snd snd = new Snd().setSender(player).setTarget(player);
    string = this.plugin.getLM().updateSnd(snd, string);
    if (player != null) {
      string = string.replaceAll(String.valueOf("(?i)(\\{username\\})"), player.getName());
      string =
          string.replaceAll(String.valueOf("(?i)(\\{displayname\\})"), player.getDisplayName());
      string =
          string.replaceAll(String.valueOf("(?i)(\\{worldname\\})"), player.getWorld().getName());
    }

    if (savedItem != null) {
      string = string.replaceAll(String.valueOf("(?i)(\\{saveditemname\\})"), savedItem.getName());
      Random random = new Random(System.currentTimeMillis());
      var list = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
      if (!list.isEmpty()) {
        int value = list.size();
        int randomIndex = random.nextInt(value) + 1;
        Player randomPlayer = (Player) list.get(randomIndex - 1);
        string =
            string.replaceAll(String.valueOf("(?i)(\\{randomplayer\\})"), randomPlayer.getName());
      }
    }

    return string;
  }
}
