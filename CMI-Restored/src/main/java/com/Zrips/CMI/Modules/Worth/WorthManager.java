package com.Zrips.CMI.Modules.Worth;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.commands.list.setworth;
import com.Zrips.CMI.commands.list.worthlist;
import com.Zrips.CMI.utils.DateFormat;
import com.Zrips.CMI.utils.Util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Recipes.CMIRecipe;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class WorthManager {
  private CMI plugin;
  private LinkedHashMap<CMIMaterial, List<WorthItem>> map = new LinkedHashMap<>();
  private HashMap<Enchantment, HashMap<Integer, WorthEnchantment>> enchantMap = new HashMap<>();
  public static String fileName = "Worth.yml";
  File file = null;
  Writer writer = null;
  boolean checkedOldFile = false;
  CompletableFuture<Void> saveTask = null;
  List<String> badLore = new ArrayList<>();
  private boolean loreBlock = false;
  private boolean requireFullDurability = false;
  private boolean devalueByDurability = false;
  private int autoGeneratePriceIncrease = 0;
  private boolean customNameBlocking = false;

  public WorthManager(CMI cmi) {
    this.plugin = cmi;
  }

  public Double getEnchantSellPrice(Enchantment enchantment, int value) {
    var hashMap = this.enchantMap.get(enchantment);
    if (hashMap == null) {
      return 0.0;
    }

    WorthEnchantment worthEnchantment = (WorthEnchantment) hashMap.get(value);
    if (worthEnchantment == null) {
      worthEnchantment = (WorthEnchantment) hashMap.get(0);
    }

    return worthEnchantment == null ? 0.0 : worthEnchantment.getSellPrice();
  }

  public WorthEnchantment getEnchantWorth(Enchantment enchantment, int value) {
    var hashMap = this.enchantMap.get(enchantment);
    return hashMap == null ? null : (WorthEnchantment) hashMap.get(value);
  }

  public void addEnchantWorth(WorthEnchantment worthEnchantment) {
    var hashMap = this.enchantMap.get(worthEnchantment.getEnchant());
    if (hashMap == null) {
      hashMap = new HashMap();
      this.enchantMap.put(worthEnchantment.getEnchant(), hashMap);
    }

    hashMap.put(worthEnchantment.getLevel(), worthEnchantment);
  }

  public void load() {
    Long startTime = System.currentTimeMillis();

    try {
      CMI.getInstance().safeRenameFile(fileName.toLowerCase(), "Saves" + File.separator + fileName);
      File saveFile = new File(this.plugin.getDataFolder(), "Saves" + File.separator + fileName);
      if (!saveFile.exists()) {
        try {
          saveFile.createNewFile();
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }

        return;
      }

      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(saveFile);
      if (yamlConfiguration.getKeys(false).isEmpty()) {
        return;
      }

      this.map.clear();
      if (!yamlConfiguration.isConfigurationSection("worth")) {
        return;
      }

      ConfigurationSection configurationSection =
          yamlConfiguration.getConfigurationSection("worth");
      var worthValues = configurationSection.getValues(false);

      for (Entry entry : worthValues.entrySet()) {
        String string = (String) entry.getKey();

        try {
          string = string.replace(">", ":");
          Integer integer = null;
          if (string.contains(";cm{")) {
            try {
              integer =
                  Integer.parseInt(string.split(Pattern.quote(";cm{"), 2)[1].split("}", 2)[0]);
            } catch (Exception exception) {
            }

            string = string.split(Pattern.quote(";cm{"), 2)[0];
          }

          CMIItemStack cmiItemStack =
              CMILib.getInstance().getItemManager().getItem(string.replace(">", ":"));
          if (cmiItemStack != null && cmiItemStack.getItemStack() != null) {
            if (configurationSection.isConfigurationSection((String) entry.getKey())) {
              ConfigurationSection itemSection =
                  configurationSection.getConfigurationSection((String) entry.getKey());
              var subValues = itemSection.getValues(false);

              for (Entry valueEntry : subValues.entrySet()) {
                if (((String) valueEntry.getKey()).equalsIgnoreCase("*")) {
                  try {
                    Double wildcardSellPrice = null;
                    Double wildcardBuyPrice = null;
                    if (valueEntry.getValue().toString().contains(";")) {
                      wildcardSellPrice =
                          Double.parseDouble(valueEntry.getValue().toString().split(";")[0]);
                      wildcardBuyPrice =
                          Double.parseDouble(valueEntry.getValue().toString().split(";")[1]);
                    } else {
                      wildcardSellPrice = Double.parseDouble(valueEntry.getValue().toString());
                    }

                    WorthItem worthItem =
                        new WorthItem(cmiItemStack.getItemStack(), wildcardSellPrice);
                    if (wildcardBuyPrice != null) {
                      worthItem.setBuyPrice(wildcardBuyPrice);
                    }

                    worthItem.setModelData(integer);
                    CMIMaterial cmiMaterial = cmiItemStack.getCMIType();
                    var list = this.map.get(cmiMaterial);
                    if (list == null) {
                      list = new ArrayList();
                      this.map.put(cmiMaterial, list);
                    }

                    list.add(worthItem);
                    this.map.put(cmiMaterial, list);
                  } catch (Exception exception2) {
                    this.plugin.consoleMessage(
                        "&cCan't load worth value for "
                            + string
                            + ":"
                            + (String) valueEntry.getKey());
                  }
                } else {
                  try {
                    int value = Integer.parseInt((String) valueEntry.getKey());
                    Double numericSellPrice = null;
                    Double numericBuyPrice = null;
                    if (valueEntry.getValue().toString().contains(";")) {
                      numericSellPrice =
                          Double.parseDouble(valueEntry.getValue().toString().split(";")[0]);
                      numericBuyPrice =
                          Double.parseDouble(valueEntry.getValue().toString().split(";")[1]);
                    } else {
                      numericSellPrice = Double.parseDouble(valueEntry.getValue().toString());
                    }

                    WorthItem numericItem =
                        new WorthItem(cmiItemStack.getItemStack(), value, numericSellPrice);
                    if (numericBuyPrice != null) {
                      numericItem.setBuyPrice(numericBuyPrice);
                    }

                    numericItem.setModelData(integer);
                    CMIMaterial numericMaterial = cmiItemStack.getCMIType();
                    var numericList = this.map.get(numericMaterial);
                    if (numericList == null) {
                      numericList = new ArrayList();
                      this.map.put(numericMaterial, numericList);
                    }

                    numericList.add(numericItem);
                    this.map.put(numericMaterial, numericList);
                  } catch (Exception exception3) {
                    this.plugin.consoleMessage(
                        "&cCan't load worth value for "
                            + string
                            + ":"
                            + (String) valueEntry.getKey());
                  }
                }
              }
            } else if (entry.getValue() instanceof Double
                || entry.getValue() instanceof Integer
                || entry.getValue() instanceof String) {
              Double plainSellPrice = null;
              Double plainBuyPrice = null;
              if (!entry.getValue().toString().isEmpty()) {
                if (entry.getValue().toString().contains(";")) {
                  plainSellPrice = Double.parseDouble(entry.getValue().toString().split(";")[0]);
                  plainBuyPrice = Double.parseDouble(entry.getValue().toString().split(";")[1]);
                } else {
                  plainSellPrice = Double.parseDouble(entry.getValue().toString());
                }

                WorthItem plainItem =
                    new WorthItem(cmiItemStack.getItemStack(), null, plainSellPrice);
                if (plainBuyPrice != null) {
                  plainItem.setBuyPrice(plainBuyPrice);
                }

                plainItem.setModelData(integer);
                CMIMaterial plainMaterial = cmiItemStack.getCMIType();
                var plainList = this.map.get(plainMaterial);
                if (plainList == null) {
                  plainList = new ArrayList();
                  this.map.put(plainMaterial, plainList);
                }

                plainList.add(plainItem);
              }
            }
          } else {
            this.plugin.consoleMessage("&cCan't load worth value for " + string);
          }
        } catch (Exception exception4) {
          exception4.printStackTrace();
          this.plugin.consoleMessage("&cFailed to load worth for: " + string);
        }
      }

      this.plugin.loadMessage(
          this.map.size(), "worth values", System.currentTimeMillis() - startTime);
    } catch (Exception exception5) {
      exception5.printStackTrace();
      this.plugin.consoleMessage("&cFailed to load worth file");
    }
  }

  public void loadEnchtWorth() {
    Long startTime = System.currentTimeMillis();

    try {
      CMI.getInstance().safeRenameFile(fileName.toLowerCase(), "Saves" + File.separator + fileName);
      File saveFile = new File(this.plugin.getDataFolder(), "Saves" + File.separator + fileName);
      if (!saveFile.exists()) {
        try {
          saveFile.createNewFile();
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }

        return;
      }

      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(saveFile);
      if (yamlConfiguration.getKeys(false).isEmpty()) {
        return;
      }

      this.enchantMap.clear();
      ConfigurationSection configurationSection =
          yamlConfiguration.getConfigurationSection("enchantWorth");
      if (configurationSection != null) {
        var enchantValues = configurationSection.getValues(false);
        int value = 0;

        for (Entry entry : enchantValues.entrySet()) {
          String string = (String) entry.getKey();

          try {
            Enchantment enchantment = CMIEnchantment.getByName(string);
            if (enchantment == null) {
              this.plugin.consoleMessage("&cCan't load emnchant worth value for " + string);
            } else {
              for (String enchantEntry : (List<String>) entry.getValue()) {
                Integer integer = null;
                Double sellPrice = null;
                Double buyPrice = null;
                if (enchantEntry.contains("-")) {
                  integer = Integer.parseInt(enchantEntry.split("-")[0]);
                  if (enchantEntry.contains(";")) {
                    sellPrice = Double.parseDouble(enchantEntry.split("-")[1].split(";")[0]);
                    buyPrice = Double.parseDouble(enchantEntry.split("-")[1].split(";")[1]);
                  } else {
                    sellPrice = Double.parseDouble(enchantEntry.split("-")[1]);
                  }

                  WorthEnchantment worthEnchantment =
                      new WorthEnchantment(enchantment, integer, sellPrice, buyPrice);
                  value++;
                  this.addEnchantWorth(worthEnchantment);
                }
              }
            }
          } catch (Exception exception) {
            this.plugin.consoleMessage("&cFailed to load enchant worth for: " + string);
          }
        }

        this.plugin.loadMessage(value, "enchant worth", System.currentTimeMillis() - startTime);
      }
    } catch (Exception | Error value2) {
      value2.printStackTrace();
      this.plugin.consoleMessage("&cFailed to load enchant worth file");
    }
  }

  private void addSellPriceButton(
      CMIGui cmiGui,
      CMIItemStack cmiItemStack,
      final WorthItem worthItem,
      int value,
      CMIMaterial cmiMaterial,
      final double number) {
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(value, cmiMaterial) {
          public void click(GUIClickType guiClickType) {
            Long sellCents = Math.round(worthItem.getSellPrice() * 100.0);
            Long stepCents = Math.round(number * 100.0);
            switch (guiClickType) {
              case Left:
                worthItem.setSellPrice((sellCents + stepCents) / 100.0);
                break;
              case LeftShift:
                worthItem.setSellPrice(
                    (sellCents.longValue() + stepCents.longValue() * 10.0) / 100.0);
                break;
              case Right:
                worthItem.setSellPrice((sellCents - stepCents) / 100.0);
                break;
              case RightShift:
                worthItem.setSellPrice(
                    (sellCents.longValue() - stepCents.longValue() * 10.0) / 100.0);
            }

            if (worthItem.isBuyPriceSet() && worthItem.getSellPrice() > worthItem.getBuyPrice()) {
              worthItem.setSellPrice(worthItem.getBuyPrice());
            }

            this.updateLooks();
          }

          public void updateLooks() {
            this.clearLore();
            WorthManager.this.priceLore(this, worthItem);
          }
        };
    cmiGuiButton.setName(CMILC.getIM(setworth.class, "setSell", "[amount]", number));
    cmiGuiButton.setGui(cmiGui);
    cmiGui.addButton(cmiGuiButton);
  }

  private void addBuyPriceButton(
      final CMIGui cmiGui,
      final CMIItemStack cmiItemStack,
      final WorthItem worthItem,
      int value,
      CMIMaterial cmiMaterial,
      final double number) {
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(value, cmiMaterial) {
          public void click(GUIClickType guiClickType) {
            Long buyCents =
                worthItem.getBuyPrice() == null ? 0L : Math.round(worthItem.getBuyPrice() * 100.0);
            Long stepCents = Math.round(number * 100.0);
            switch (guiClickType) {
              case Left:
                worthItem.setBuyPrice((buyCents - stepCents) / 100.0);
                break;
              case LeftShift:
                worthItem.setBuyPrice(
                    (buyCents.longValue() - stepCents.longValue() * 10.0) / 100.0);
                break;
              case Right:
                worthItem.setBuyPrice((buyCents + stepCents) / 100.0);
                break;
              case RightShift:
                worthItem.setBuyPrice(
                    (buyCents.longValue() + stepCents.longValue() * 10.0) / 100.0);
            }

            if (worthItem.getSellPrice() > worthItem.getBuyPrice()) {
              worthItem.setBuyPrice(worthItem.getSellPrice());
            }

            WorthManager.this.openWorthGui(cmiGui.getPlayer(), cmiItemStack);
          }

          public void updateLooks() {
            this.clearLore();
            WorthManager.this.priceLore(this, worthItem);
          }
        };
    cmiGuiButton.setName(CMILC.getIM(setworth.class, "setBuy", "[amount]", number));
    cmiGuiButton.setGui(cmiGui);
    cmiGui.addButton(cmiGuiButton);
  }

  public void openWorthGui(final Player player, CMIItemStack cmiItemStack) {
    cmiItemStack = cmiItemStack.clone();
    cmiItemStack.getItemStack().setAmount(1);
    WorthItem worthItem =
        CMI.getInstance().getWorthManager().getWorth(cmiItemStack.getItemStack(), true);
    Integer integer = Util.getCustomModelData(cmiItemStack.getItemStack());
    if (integer != null && worthItem != null && worthItem.getModelData() == null) {
      worthItem = null;
    }

    if (worthItem == null) {
      worthItem = new WorthItem(cmiItemStack.getItemStack(), 0.0);
      worthItem.setModelData(integer);
      CMIMaterial cmiMaterial = cmiItemStack.getCMIType();
      var list = this.map.get(cmiMaterial);
      if (list == null) {
        list = new ArrayList();
        this.map.put(cmiMaterial, list);
      }

      list.add(worthItem);
      this.map.put(cmiMaterial, list);
    }

    final WorthItem finalWorthItem = worthItem;
    finalWorthItem.setItem(cmiItemStack.getItemStack());
    CMIGui cmiGui =
        new CMIGui(player) {
          public void onClose() {
            CMI.getInstance().getWorthManager().updatePriceInFile(finalWorthItem);
            finalWorthItem.resetRecipes();
          }
        };
    String string = "";
    if (finalWorthItem.getModelData() != null) {
      string = " (" + finalWorthItem.getModelData() + ")";
    }

    cmiGui.setTitle(cmiItemStack.getRealName() + string);
    cmiGui.setInvSize(GUIRows.r6);
    finalWorthItem.updateExploitRecipe();
    this.addSellPriceButton(cmiGui, cmiItemStack, finalWorthItem, 9, CMIMaterial.YELLOW_WOOL, 0.01);
    this.addSellPriceButton(cmiGui, cmiItemStack, finalWorthItem, 10, CMIMaterial.YELLOW_WOOL, 1.0);
    this.addSellPriceButton(
        cmiGui, cmiItemStack, finalWorthItem, 11, CMIMaterial.YELLOW_WOOL, 100.0);
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(13, cmiItemStack.getItemStack().clone()) {
          public void click(GUIClickType guiClickType) {
            WorthManager.this.plugin.performCommand(
                player,
                CommandsHandler.getLabel() + " " + worthlist.class.getSimpleName() + " -missing");
          }
        };
    this.priceLore(cmiGuiButton, finalWorthItem);
    cmiGui.addButton(cmiGuiButton);
    this.addBuyPriceButton(cmiGui, cmiItemStack, finalWorthItem, 15, CMIMaterial.GREEN_WOOL, 0.01);
    this.addBuyPriceButton(cmiGui, cmiItemStack, finalWorthItem, 16, CMIMaterial.GREEN_WOOL, 1.0);
    this.addBuyPriceButton(cmiGui, cmiItemStack, finalWorthItem, 17, CMIMaterial.GREEN_WOOL, 100.0);
    Recipe recipe = finalWorthItem.getRecipe();
    if (recipe != null) {
      int value = 0;
      int startSlot = 30;
      var ingredients = CMIRecipe.getIngredientsList(recipe);
      startSlot -= ingredients.size() / 2;

      for (final ItemStack itemStack : CMIRecipe.getIngredientsList(recipe)) {
        value++;
        ItemStack ingredientCopy = itemStack.clone();
        final WorthItem ingredientWorth = this.getWorth(ingredientCopy, true);
        cmiGuiButton =
            new CMIGuiButton(startSlot + value, ingredientCopy) {
              public void click(GUIClickType clickType) {
                CMI.getInstance().getWorthManager().updatePriceInFile(finalWorthItem);
                finalWorthItem.resetRecipes();
                WorthManager.this.openWorthGui(player, new CMIItemStack(itemStack));
              }

              public void updateLooks() {
                if (ingredientWorth != null && ingredientWorth.isBuyPriceSet()) {
                  this.setName(
                      CMILC.getIM(
                          setworth.class,
                          "buyPrice",
                          "[amount]",
                          WorthManager.this
                              .plugin
                              .getEconomyManager()
                              .format(ingredientWorth.getBuyPrice())));
                } else {
                  this.setName(CMILC.getIM(setworth.class, "notSetBuy"));
                }
              }
            };
        if (ingredientWorth == null || !ingredientWorth.isBuyPriceSet()) {
          cmiGuiButton.setGlowing();
        }

        cmiGui.addButton(cmiGuiButton);
      }
    }

    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }

  private void priceLore(CMIGuiButton cmiGuiButton, WorthItem worthItem) {
    if (worthItem != null) {
      Double sellPrice = worthItem.getSellPrice();
      Double buyPrice = worthItem.getBuyPrice();
      cmiGuiButton.addLore(
          CMILC.getIM(
              setworth.class,
              "sellPrice",
              "[amount]",
              sellPrice == null ? "-" : this.plugin.getEconomyManager().format(sellPrice)));
      cmiGuiButton.addLore(
          CMILC.getIM(
              setworth.class,
              "buyPrice",
              "[amount]",
              worthItem.isBuyPriceSet() && buyPrice != null
                  ? this.plugin.getEconomyManager().format(buyPrice)
                  : "-"));
      boolean flag = true;
      Recipe recipe = worthItem.getExploitRecipe();
      if (recipe == null) {
        flag = false;
        recipe = worthItem.getRecipe();
      }

      HashMap<String, ItemStack> hashMap = new HashMap();
      WorthItem resultWorth = null;
      CMIItemStack cmiItemStack = new CMIItemStack(worthItem.getItem());
      if (recipe != null) {
        cmiItemStack = CMILib.getInstance().getItemManager().getItem(recipe.getResult());
        resultWorth = this.getWorth(recipe.getResult(), true);

        for (ItemStack itemStack : CMIRecipe.getIngredientsList(recipe)) {
          CMIItemStack ingredientItem = CMILib.getInstance().getItemManager().getItem(itemStack);
          short shortValue = ingredientItem.getData();
          shortValue = shortValue < 0 ? 0 : shortValue;
          String string = ingredientItem.getType() + ":" + shortValue;
          ItemStack existingIngredient = (ItemStack) hashMap.get(string);
          if (existingIngredient == null) {
            hashMap.put(string, itemStack.clone());
          } else {
            existingIngredient.setAmount(existingIngredient.getAmount() + itemStack.getAmount());
          }
        }
      }

      cmiGuiButton.addLore(" ");
      if (flag) {
        cmiGuiButton.addLore(CMILC.getIM(setworth.class, "exploit"));
      }

      cmiGuiButton.addLore(CMILC.getIM(setworth.class, "ingredient"));
      Double ingredientsTotal = 0.0;
      double number = 0.0;

      for (Entry entry : hashMap.entrySet()) {
        WorthItem ingredientWorth =
            CMI.getInstance().getWorthManager().getWorth((ItemStack) entry.getValue(), true);
        CMIItemStack ingredientStack =
            CMILib.getInstance().getItemManager().getItem((ItemStack) entry.getValue());
        if (ingredientWorth != null && ingredientWorth.getSellPrice() != null) {
          number += ((ItemStack) entry.getValue()).getAmount() * ingredientWorth.getSellPrice();
        }

        if (ingredientWorth != null && ingredientWorth.isBuyPriceSet()) {
          cmiGuiButton.addLore(
              CMILC.getIM(
                  setworth.class,
                  "ingredients",
                  "[price]",
                  this.plugin
                      .getEconomyManager()
                      .format(
                          ((ItemStack) entry.getValue()).getAmount()
                              * ingredientWorth.getBuyPrice()),
                  "[ingridient]",
                  ingredientStack.getRealName(),
                  "[ingredient]",
                  ingredientStack.getRealName(),
                  "[amount]",
                  ((ItemStack) entry.getValue()).getAmount()));
          ingredientsTotal =
              ingredientsTotal
                  + ((ItemStack) entry.getValue()).getAmount() * ingredientWorth.getBuyPrice();
        } else {
          cmiGuiButton.addLore(
              CMILC.getIM(
                  setworth.class,
                  "ingredients",
                  "[price]",
                  CMILC.getIM(setworth.class, "notSet"),
                  "[ingridient]",
                  ingredientStack.getRealName(),
                  "[ingredient]",
                  ingredientStack.getRealName(),
                  "[amount]",
                  ((ItemStack) entry.getValue()).getAmount()));
        }
      }

      cmiGuiButton.addLore(
          CMILC.getIM(
              setworth.class,
              "result",
              "[result]",
              cmiItemStack.getRealName(),
              "[amount]",
              cmiItemStack.getAmount()));
      cmiGuiButton.addLore(
          CMILC.getIM(
              setworth.class,
              "ingredientsPrice",
              "[price]",
              this.plugin.getEconomyManager().format(ingredientsTotal)));
      if (resultWorth != null && resultWorth.getSellPrice() != null) {
        cmiGuiButton.addLore(
            CMILC.getIM(
                setworth.class,
                "resultPrice",
                "[price]",
                this.plugin
                    .getEconomyManager()
                    .format(resultWorth.getSellPrice() * cmiItemStack.getAmount())));
      }

      cmiGuiButton.addLore(CMILC.getIM(setworth.class, "explanation"));
      cmiGuiButton.update();
    }
  }

  public void closeStream() {
    if (this.writer != null) {
      try {
        this.writer.close();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }
  }

  public void sellLog(Player player, HashMap<WorthItem, Integer> hashMap) {
    if (!this.checkedOldFile) {
      File logDir = new File(CMI.getInstance().getDataFolder(), "sellLogs");
      if (!logDir.isDirectory()) {
        logDir.mkdir();
      }
    }

    Date date = new Date();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM");
    String string = simpleDateFormat.format(date);
    boolean flag = false;
    if (this.file != null) {
      if (!this.file.getName().equalsIgnoreCase(string + ".yml")) {
        flag = true;
        if (this.writer != null) {
          try {
            this.writer.flush();
            this.writer.close();
          } catch (IOException ioException) {
            ioException.printStackTrace();
          }

          this.writer = null;
        }
      }
    } else {
      flag = true;
    }

    if (flag) {
      this.file =
          new File(this.plugin.getDataFolder(), "sellLogs" + File.separator + string + ".yml");
      if (!this.file.exists()) {
        try {
          this.file.createNewFile();
        } catch (IOException ioException2) {
          ioException2.printStackTrace();
          return;
        }
      }
    }

    if (!this.checkedOldFile) {
      File oldLog = new File(this.plugin.getDataFolder(), "sellLog.yml");
      if (oldLog.exists()) {
        new File(this.plugin.getDataFolder(), "sellLogs" + File.separator + "Old_sellLog.yml");
      }

      this.checkedOldFile = true;
    }

    try {
      if (this.writer == null) {
        this.writer =
            new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(this.file, true), "UTF-8"));
      }

      String day = DateFormat.MiliToDate(System.currentTimeMillis(), "dd");
      String locationText =
          this.plugin
              .getPlayerManager()
              .convertLocToStringShort(new CMILocation(player.getLocation()));
      String logLine = day + " " + player.getName() + " " + locationText;
      if (hashMap.size() > 1) {
        this.writer.append(logLine + System.lineSeparator());

        for (var entry : hashMap.entrySet()) {
          CMIItemStack cmiItemStack =
              CMILib.getInstance().getItemManager().getItem(((WorthItem) entry.getKey()).getItem());
          this.writer.append(
              " sold "
                  + cmiItemStack.getRealName()
                  + " x "
                  + entry.getValue()
                  + " for "
                  + ((WorthItem) entry.getKey()).getSellPrice()
                      * ((Integer) entry.getValue()).intValue()
                  + System.lineSeparator());
        }
      } else {
        for (var entry : hashMap.entrySet()) {
          CMIItemStack soldStack =
              CMILib.getInstance().getItemManager().getItem(((WorthItem) entry.getKey()).getItem());
          this.writer.append(
              logLine
                  + " sold "
                  + soldStack.getRealName()
                  + " x "
                  + entry.getValue()
                  + " for "
                  + ((WorthItem) entry.getKey()).getSellPrice()
                      * ((Integer) entry.getValue()).intValue()
                  + System.lineSeparator());
        }
      }

      this.writer.flush();
    } catch (IOException ioException3) {
      ioException3.printStackTrace();
    }
  }

  public void updatePriceInFile() {
    this.updateWorthInFile(null, null);
  }

  public void updatePriceInFile(WorthItem worthItem) {
    this.updatePriceInFile(worthItem, null);
  }

  public void updatePriceInFile(WorthEnchantment worthEnchantment) {
    this.updatePriceInFile(null, worthEnchantment);
  }

  public void updatePriceInFile(WorthItem worthItem, WorthEnchantment worthEnchantment) {
    HashSet<WorthItem> hashSet = new HashSet();
    if (worthItem != null) {
      hashSet.add(worthItem);
    }

    HashSet<WorthEnchantment> enchantSet = new HashSet();
    if (worthEnchantment != null) {
      enchantSet.add(worthEnchantment);
    }

    this.updateWorthInFile(hashSet, enchantSet);
  }

  private void updateWorthInFile(Set<WorthItem> set, Set<WorthEnchantment> enchantSet) {
    if (set != null) {
      for (WorthItem worthItem : set) {
        CMIMaterial cmiMaterial = CMIMaterial.get(worthItem.getItem());
        var list = CMI.getInstance().getWorthManager().getMap().get(cmiMaterial);
        if (list == null) {
          list = new ArrayList();
          CMI.getInstance().getWorthManager().getMap().put(cmiMaterial, list);
        }

        if (!list.contains(worthItem)) {
          list.add(worthItem);
        }
      }
    }

    if (enchantSet != null) {
      for (WorthEnchantment worthEnchantment : enchantSet) {
        var hashMap = this.enchantMap.get(worthEnchantment.getEnchant());
        if (hashMap == null) {
          hashMap = new HashMap();
          this.enchantMap.put(worthEnchantment.getEnchant(), hashMap);
        }

        hashMap.put(worthEnchantment.getLevel(), worthEnchantment);
      }
    }

    if (this.saveTask == null) {
      this.saveTask =
          CMIScheduler.runTaskAsynchronously(
              this.plugin,
              () -> {
                try {
                  ConfigReader configReader =
                      new ConfigReader(CMI.getInstance(), "Saves" + File.separator + fileName);
                  configReader.addComment(
                      "worth", new String[] {"Use ingame editor /cmi setworth to change prices"});
                  TreeMap<String, List> treeMap = new TreeMap();

                  for (var entry :
                      new HashMap<>(CMI.getInstance().getWorthManager().getMap()).entrySet()) {
                    treeMap.put(((CMIMaterial) entry.getKey()).toString(), (List) entry.getValue());
                  }

                  for (Entry materialEntry : treeMap.entrySet()) {
                    for (WorthItem worthItemForSave : (List<WorthItem>) materialEntry.getValue()) {
                      String string =
                          worthItemForSave.getSellPrice()
                              + (worthItemForSave.isBuyPriceSet()
                                  ? ";" + worthItemForSave.getBuyPrice()
                                  : "");
                      String itemKey =
                          new CMIItemStack(worthItemForSave.getItem())
                              .toOneLiner()
                              .replace(":", ">");
                      if (worthItemForSave.getModelData() != null) {
                        itemKey = itemKey + ";cm{" + worthItemForSave.getModelData() + "}";
                      }

                      configReader.set("worth." + itemKey, string);
                    }
                  }

                  configReader.addComment(
                      "enchantWorth",
                      new String[] {"Use ingame editor /cmi setenchantworth to change prices"});
                  var enchantSnapshot = new HashMap<>(this.enchantMap);

                  for (Entry enchantEntry : enchantSnapshot.entrySet()) {
                    ArrayList<String> arrayList = new ArrayList();

                    for (Entry levelEntry :
                        ((Iterable<Entry>) ((HashMap) enchantEntry.getValue()).entrySet())) {
                      WorthEnchantment enchantWorth = (WorthEnchantment) levelEntry.getValue();
                      String enchantValue =
                          (int) (enchantWorth.getSellPrice() * 100.0) / 100.0
                              + (enchantWorth.isBuyPriceSet()
                                  ? ";" + enchantWorth.getBuyPrice()
                                  : "");
                      arrayList.add(levelEntry.getKey() + "-" + enchantValue);
                    }

                    configReader.set(
                        "enchantWorth."
                            + CMIEnchantment.getName((Enchantment) enchantEntry.getKey()),
                        arrayList);
                  }

                  configReader.save();
                } catch (Exception exception) {
                  exception.printStackTrace();
                }

                this.saveTask = null;
              });
    }
  }

  public WorthItem getWorth(ItemStack itemStack) {
    return this.getWorth(itemStack, false);
  }

  public boolean containsBlockedLore(CMIItemStack cmiItemStack) {
    for (String string : cmiItemStack.getLore()) {
      String plainLore = CMIChatColor.stripColor(string).toLowerCase();

      for (String blockedLore : this.badLore) {
        if (plainLore.contains(blockedLore)) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean isRepairBeforeSell(ItemStack itemStack) {
    CMIItemStack cmiItemStack = CMILib.getInstance().getItemManager().getItem(itemStack);
    return this.requireFullDurability
        && cmiItemStack.getMaxDurability() > 2
        && cmiItemStack.getDurability() > 0;
  }

  public double damagePercentage(ItemStack itemStack) {
    CMIItemStack cmiItemStack = CMILib.getInstance().getItemManager().getItem(itemStack);
    if (cmiItemStack.getMaxDurability() < 2) {
      return 0.0;
    }

    double number =
        100.0
            - (double) ((cmiItemStack.getMaxDurability() - cmiItemStack.getDurability()) * 100)
                / cmiItemStack.getMaxDurability();
    return (int) (number * 100.0) / 100.0;
  }

  public double getWorthByDurability(ItemStack itemStack, double number) {
    if (!this.devalueByDurability) {
      return number;
    }

    CMIItemStack cmiItemStack = CMILib.getInstance().getItemManager().getItem(itemStack);
    if (cmiItemStack.getMaxDurability() < 2) {
      return number;
    }

    double durabilityFactor =
        (double) (cmiItemStack.getMaxDurability() - cmiItemStack.getDurability())
            / cmiItemStack.getMaxDurability();
    return (int) (number * durabilityFactor * 10000.0) / 10000.0;
  }

  public WorthItem getWorth(ItemStack itemStack, boolean flag) {
    if (itemStack == null) {
      return null;
    }

    CMIItemStack cmiItemStack = new CMIItemStack(itemStack);
    if (this.isRepairBeforeSell(itemStack) || this.containsBlockedLore(cmiItemStack)) {
      return null;
    }

    if (this.customNameBlocking
        && itemStack.hasItemMeta()
        && itemStack.getItemMeta().hasDisplayName()
        && !CMIMaterial.isPlayerHead(itemStack.getType())) {
      return null;
    }

    if (this.loreBlock && itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
      return null;
    }

    var list = this.map.get(cmiItemStack.getCMIType());
    if (list != null && !list.isEmpty()) {
      Integer integer = Util.getCustomModelData(itemStack);
      if (integer != null) {
        for (WorthItem worthItem : list) {
          if (worthItem != null
              && worthItem.getModelData() != null
              && worthItem.getModelData().equals(integer)
              && cmiItemStack.isSimilar(worthItem.getItem())) {
            return worthItem;
          }
        }
      }

      for (WorthItem candidate : list) {
        if (candidate != null
            && cmiItemStack.isSimilar(candidate.getItem())
            && (candidate.getSellPrice() != 0.0 || flag)) {
          return candidate;
        }
      }

      return null;
    } else {
      return null;
    }
  }

  public List<ItemStack> getItems(Player player, WorthManager.worthType type) {
    return this.getItems(player, type, null);
  }

  public List<ItemStack> getItems(
      Player player, WorthManager.worthType type, CMIMaterial cmiMaterial) {
    return this.getItems(player, type, cmiMaterial, 0);
  }

  public List<ItemStack> getItems(
      Player player, WorthManager.worthType type, CMIMaterial cmiMaterial, int value) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    ArrayList<ItemStack> arrayList = new ArrayList();
    switch (type) {
      case all:
        arrayList.addAll(
            cmiUser.getInventory().getItems(CMIPlayerInventory.CMIInventorySlot.MainInventory));
        break;
      case hand:
        ItemStack itemStack = cmiUser.getItemInHand();
        if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
          return arrayList;
        }

        arrayList.add(itemStack);
        break;
      case blocks:
        for (ItemStack invItem :
            cmiUser.getInventory().getItems(CMIPlayerInventory.CMIInventorySlot.MainInventory)) {
          if (invItem != null && invItem.getType().isBlock()) {
            arrayList.add(invItem);
          }
        }
        break;
      case same:
        ItemStack heldItem = cmiUser.getItemInHand();
        if (heldItem == null || heldItem.getType().equals(Material.AIR)) {
          return arrayList;
        }

        ItemStack[] itemStacks;
        int slotCount = (itemStacks = player.getInventory().getContents()).length;

        for (int slotIndex = 0; slotIndex < slotCount; slotIndex++) {
          ItemStack sameItem = itemStacks[slotIndex];
          if (sameItem != null && sameItem.isSimilar(heldItem)) {
            arrayList.add(sameItem);
          }
        }
        break;
      case material:
        int materialAmount = 0;

        for (ItemStack materialItem :
            cmiUser.getInventory().getItems(CMIPlayerInventory.CMIInventorySlot.MainInventory)) {
          if (materialItem != null && CMIMaterial.get(materialItem).equals(cmiMaterial)) {
            if (value > 0 && materialAmount > value) {
              break;
            }

            arrayList.add(materialItem);
            materialAmount += materialItem.getAmount();
          }
        }
    }

    return arrayList;
  }

  public boolean isShulkerWithItems(ItemStack itemStack) {
    if (!CMIMaterial.isShulkerBox(itemStack.getType())) {
      return false;
    }

    var list = this.plugin.getShulkerBoxManager().getShulkerBoxContents(itemStack);
    return !list.isEmpty();
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Worth.BadLore",
        new String[] {
          "Defines lore that will prevent an item from being sold using /cmi sell.",
          "Color codes and capitalization are being ignored"
        });
    this.badLore = configReader.get("Worth.BadLore", Arrays.asList("Creative item by Gasha"));

    for (int value = 0; value < this.badLore.size(); value++) {
      this.badLore.set(value, CMIChatColor.stripColor(this.badLore.get(value)).toLowerCase());
    }

    configReader.addComment(
        "Worth.CustomNameBlocking",
        new String[] {
          "If enabled any item with custom name will not be allowed to be sold with /cmi sell"
              + " command"
        });
    this.customNameBlocking = configReader.get("Worth.CustomNameBlocking", false);
    configReader.addComment(
        "Worth.LoreBlocking",
        new String[] {
          "If enabled any item with custom lore will not be allowed to be sold with /cmi sell"
              + " command"
        });
    this.loreBlock = configReader.get("Worth.LoreBlocking", false);
    configReader.addComment(
        "Worth.RequireFullDurability",
        new String[] {"When enabled we will not allow to sell items which are not fully repaired"});
    this.requireFullDurability = configReader.get("Worth.RequireFullDurability", false);
    configReader.addComment(
        "Worth.DevalueByDurability",
        new String[] {
          "When enabled items which doesnt have full durability will be worth less. 30% left"
              + " durability will result in items worth going down by 70%",
          "This only works while RequireFullDurability is disabled"
        });
    this.devalueByDurability = configReader.get("Worth.DevalueByDurability", false);
    configReader.addComment(
        "Worth.AutoGenerate.PriceIncrease",
        new String[] {
          "Value in percentages in how much more we should add to end product while auto"
              + " calculating items price based on its ingredient worth",
          "For example one stick is worth 0.1 and diamond 44 then end result as diamond_hoe will be"
              + " worth 88.2 and with extra 2% this will be changed to 89.96",
          "Value can be negative"
        });
    this.autoGeneratePriceIncrease = configReader.get("Worth.AutoGenerate.PriceIncrease", 0);
  }

  public HashMap<CMIMaterial, List<WorthItem>> getMap() {
    return this.map;
  }

  public WorthItem addWorth(WorthItem worthItem) {
    CMIMaterial cmiMaterial = CMIMaterial.get(worthItem.getItem());
    var list = this.map.get(cmiMaterial);
    if (list == null) {
      list = new ArrayList();
      this.map.put(cmiMaterial, list);
    }

    list.add(worthItem);
    this.map.put(cmiMaterial, list);
    return worthItem;
  }

  @Deprecated
  public void openWorthListGui(Player player, int value) {
    this.openWorthListGui(player, value, false);
  }

  public void openWorthListGui(final Player player, int value, final boolean flag) {
    CMIGui cmiGui =
        new CMIGui(player) {
          public void pageChange(int page) {
            WorthManager.this.openWorthListGui(player, page, flag);
          }
        };
    cmiGui.setTitle(CMILC.getIM(worthlist.class, "title"));
    int totalCount = 0;
    LinkedHashMap<CMIMaterial, ArrayList> linkedHashMap = new LinkedHashMap();
    if (!flag) {
      linkedHashMap = (LinkedHashMap) new LinkedHashMap(this.plugin.getWorthManager().getMap());

      for (Entry entry : linkedHashMap.entrySet()) {
        totalCount += ((List) entry.getValue()).size();
      }
    } else {
      CMIMaterial[] cmiMaterials;
      int materialCount = (cmiMaterials = CMIMaterial.values()).length;

      for (int materialIndex = 0; materialIndex < materialCount; materialIndex++) {
        CMIMaterial cmiMaterial = cmiMaterials[materialIndex];
        if (!cmiMaterial.isLegacy() && cmiMaterial.isValidAsItemStack()) {
          CMIItemStack cmiItemStack = cmiMaterial.newCMIItemStack();
          WorthItem worthItem = this.getWorth(cmiItemStack.getItemStack());
          if (worthItem == null) {
            linkedHashMap.put(cmiMaterial, new ArrayList());
            totalCount++;
          }
        }
      }
    }

    PageInfo pageInfo = new PageInfo(45, totalCount, value);

    label59:
    for (final Entry pageEntry : linkedHashMap.entrySet()) {
      if (((List) pageEntry.getValue()).isEmpty()) {
        if (!pageInfo.isContinue()) {
          if (pageInfo.isBreak()) {
            break;
          }

          CMIGuiButton cmiGuiButton =
              new CMIGuiButton(((CMIMaterial) pageEntry.getKey()).newItemStack()) {
                public void click(GUIClickType guiClickType) {
                  WorthManager.this.plugin.performCommand(
                      player,
                      CommandsHandler.getLabel()
                          + " "
                          + setworth.class.getSimpleName()
                          + " "
                          + pageEntry.getKey());
                }
              };
          CMIItemStack typeStack = new CMIItemStack((CMIMaterial) pageEntry.getKey());
          cmiGuiButton.setName(
              CMILC.getIM(
                  worthlist.class,
                  "type",
                  "[type]",
                  typeStack.getItemStack() != null
                      ? typeStack.getDisplayName()
                      : typeStack.getRealName()));
          cmiGui.addButton(cmiGuiButton);
        }
      } else {
        for (WorthItem pageItem : (List<WorthItem>) pageEntry.getValue()) {
          if (!pageInfo.isContinue()) {
            if (pageInfo.isBreak()) {
              break label59;
            }

            CMIGuiButton itemButton = new CMIGuiButton(pageItem.getItem());
            itemButton.setName(
                CMILC.getIM(
                    worthlist.class,
                    "type",
                    "[type]",
                    new CMIItemStack(pageItem.getItem()).getDisplayName()));
            itemButton.addLore(
                CMILC.getIM(
                    worthlist.class,
                    "worth",
                    "[price]",
                    this.plugin.getEconomyManager().format(pageItem.getSellPrice())));
            cmiGui.addButton(itemButton);
          }
        }
      }
    }

    cmiGui.addPagination(pageInfo);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }

  public int getAutoGeneratePriceIncrease() {
    return this.autoGeneratePriceIncrease;
  }

  public enum worthType {
    all,
    hand,
    blocks,
    same,
    material,
    gui;

    public static WorthManager.worthType getByName(String string) {
      WorthManager.worthType[] worthTypes;
      int value = (worthTypes = values()).length;

      for (int index = 0; index < value; index++) {
        WorthManager.worthType candidate = worthTypes[index];
        if (candidate.name().equalsIgnoreCase(string)) {
          return candidate;
        }
      }

      return null;
    }
  }
}
