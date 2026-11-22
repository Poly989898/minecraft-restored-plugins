package com.Zrips.CMI.Modules.Scavenger;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Worth.WorthEnchantment;
import com.Zrips.CMI.commands.list.scavenge;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class ScavengeManager {
  private CMI plugin;
  public static double baseBreakPercentage = 0.0;
  public static double eachBreakPercentage = 0.0;
  public static double eachBreakPercentageLevel = 0.0;
  public static double baseEnchantFailPercentage = 0.0;
  public static double levelEnchantFailPercentage = 0.0;
  public static double levelEnchantFailMaxChance = 100.0;
  public static double LowerLevelChanceBase = 100.0;
  public static double LowerLevelForEachLevel = 100.0;
  public static double LowerLevelMaxChance = 100.0;
  public static double itemBreakMaxBreakChance = 100.0;
  public static double EnchantBaseCost = 0.0;
  public static double EnchantBaseCostExtra = 0.0;
  public static double IngredientReturnBase = 0.0;
  public static boolean ItemMaterialDurabilityCheck = true;
  public static boolean InvertBlackList = false;
  public static boolean AllowItemPickups = false;
  public static boolean ResetRepairCost = true;
  public static boolean WithoutIngredients = false;
  public static double ItemBreakDurabilityChange = 0.0;
  public static Set<CMIMaterial> blackList = new HashSet<>();
  private static final int slotOfItem = 13;

  public ScavengeManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void loadConfig() {
    ConfigReader configReader = null;

    try {
      configReader = new ConfigReader(this.plugin, "Settings" + File.separator + "Scavenge.yml");
    } catch (Exception exception) {
      exception.printStackTrace();
      return;
    }

    ConfigReader scavengeConfig = this.plugin.getConfigManager().getConfig();
    if (scavengeConfig.getC().isConfigurationSection("Scavenge")
        && !configReader.getC().isConfigurationSection("Scavenge")) {
      configReader.set("Scavenge", scavengeConfig.getC().getConfigurationSection("Scavenge"));
      configReader.save();

      try {
        configReader = new ConfigReader(this.plugin, "Settings" + File.separator + "Scavenge.yml");
      } catch (Exception exception2) {
        exception2.printStackTrace();
        return;
      }
    }

    configReader.addComment(
        "Scavenge.ItemBreak.Base",
        new String[] {
          "Defined percentage for item to break when salvaging it",
          "Value can be from 0 to 100, where 100 means that each time player extract enchant, item"
              + " breaks",
          "This can allow player to extract enchantments without breaking item itself",
          "Set it to 100 if you want to always break item",
          "Keep in mind that broken item will go throw ingredient return process",
          "Attention! Items without enchants will have 100% break chance"
        });
    baseBreakPercentage = configReader.get("Scavenge.ItemBreak.Base", 8.0);
    baseBreakPercentage =
        baseBreakPercentage < 0.0
            ? 0.0
            : (baseBreakPercentage > 100.0 ? 100.0 : baseBreakPercentage);
    configReader.addComment(
        "Scavenge.ItemBreak.ForEachEnchant",
        new String[] {
          "Adds extra chance to break item depending on how many enchants item has",
          "In example having base chance of 8% and having this set to 2 while having item with 3"
              + " enchants will result into 8+(2*3)=14% chance to break item"
        });
    eachBreakPercentage = configReader.get("Scavenge.ItemBreak.ForEachEnchant", 2.0);
    eachBreakPercentage =
        eachBreakPercentage < 0.0
            ? 0.0
            : (eachBreakPercentage > 100.0 ? 100.0 : eachBreakPercentage);
    configReader.addComment(
        "Scavenge.ItemBreak.ForEachEnchantLevel",
        new String[] {
          "Adds extra chance to break item depending on enchant level",
          "This will take into consideration enchantment max and current levels",
          "Having this set to 7.5 means, that enchantment at max level will have 7.5% extra chance"
              + " to to break item",
          "But if you have sharpness 2 which has max level of 5, then only 3% fail chance will be"
              + " added"
        });
    eachBreakPercentageLevel = configReader.get("Scavenge.ItemBreak.ForEachEnchantLevel", 2.0);
    eachBreakPercentageLevel =
        eachBreakPercentageLevel < 0.0
            ? 0.0
            : (eachBreakPercentageLevel > 100.0 ? 100.0 : eachBreakPercentageLevel);
    configReader.addComment(
        "Scavenge.ItemBreak.MaxBreakChance",
        new String[] {
          "Defines in percentage a max chance to break item when extracting enchants",
          "This can limit chance to particular one in case it gets to 100% and it would always"
              + " break"
        });
    itemBreakMaxBreakChance = configReader.get("Scavenge.ItemBreak.MaxBreakChance", 100.0);
    itemBreakMaxBreakChance =
        itemBreakMaxBreakChance < 0.0
            ? 0.0
            : (itemBreakMaxBreakChance > 100.0 ? 100.0 : itemBreakMaxBreakChance);
    configReader.addComment(
        "Scavenge.ItemBreak.BreakDurabilityCheck",
        new String[] {
          "Value between 0 and 100 which defines extra fail chance when items doesnt have max"
              + " durability",
          "Having this set to 50 will mean that item at 1 left durability will have fail chance"
              + " increase by 50%",
          "Items which are not damaged will not experience any fail chance increase"
        });
    ItemBreakDurabilityChange = configReader.get("Scavenge.ItemBreak.BreakDurabilityCheck", 50.0);
    ItemBreakDurabilityChange =
        ItemBreakDurabilityChange < 0.0
            ? 0.0
            : (ItemBreakDurabilityChange > 100.0 ? 100.0 : ItemBreakDurabilityChange);
    configReader.addComment(
        "Scavenge.ItemBreak.DurabilityCheck",
        new String[] {
          "When set to true, items durability will be taken into consideration when extracting"
              + " ingredients",
          "In example if item has 100 max durability and current is at 50, then only half of"
              + " ingredients will be considered for extraction",
          "This doesnt mean that player gets 50% of them, it only means that half of possible"
              + " ingredients will go throw IngredientReturn process"
        });
    ItemMaterialDurabilityCheck = configReader.get("Scavenge.ItemBreak.DurabilityCheck", true);
    configReader.addComment(
        "Scavenge.ItemBreak.IngredientReturn.Base",
        new String[] {
          "Defines in percentage a max chance to return ingredients of item if it fails extraction"
              + " process",
          "This will apply for each ingredient that item has",
          "Recipe to make that item should exist in database, or it will not return any ingredients"
        });
    IngredientReturnBase = configReader.get("Scavenge.ItemBreak.IngredientReturn.Base", 25.0);
    IngredientReturnBase =
        IngredientReturnBase < 0.0
            ? 0.0
            : (IngredientReturnBase > 100.0 ? 100.0 : IngredientReturnBase);
    configReader.addComment(
        "Scavenge.EnchantExtractionFail.Base",
        new String[] {
          "Adds base chance to fail enchantment extraction",
          "When enchantment fails, player will not get enchant book with appropriate enchantment"
        });
    baseEnchantFailPercentage = configReader.get("Scavenge.EnchantExtractionFail.Base", 10.0);
    baseEnchantFailPercentage =
        baseEnchantFailPercentage < 0.0
            ? 0.0
            : (baseEnchantFailPercentage > 100.0 ? 100.0 : baseEnchantFailPercentage);
    configReader.addComment(
        "Scavenge.EnchantExtractionFail.ForEachLevel",
        new String[] {
          "Adds extra chance to fail enchantment extraction depending on enchantment level",
          "This will take into consideration enchantment max and current levels",
          "Having this set to 75 means, that enchantment at max level will have 75% chance to fail"
              + " extraction process",
          "But if you have sharpness 2 which has max level of 5, then only 30% chance to fail will"
              + " get applied",
          "While enchants like Aquaaffinity will always have max fail chance as you can only have"
              + " it at level 1"
        });
    levelEnchantFailPercentage =
        configReader.get("Scavenge.EnchantExtractionFail.ForEachLevel", 10.0);
    levelEnchantFailPercentage =
        levelEnchantFailPercentage < 0.0
            ? 0.0
            : (levelEnchantFailPercentage > 100.0 ? 100.0 : levelEnchantFailPercentage);
    configReader.addComment(
        "Scavenge.EnchantExtractionFail.MaxFailChance",
        new String[] {
          "Defines in percentage a max chance to fail enchantment extraction",
          "This can limit chance to particular one in case it gets to 100% and it would always fail"
        });
    levelEnchantFailMaxChance =
        configReader.get("Scavenge.EnchantExtractionFail.MaxFailChance", 75.0);
    levelEnchantFailMaxChance =
        levelEnchantFailMaxChance < 0.0
            ? 0.0
            : (levelEnchantFailMaxChance > 100.0 ? 100.0 : levelEnchantFailMaxChance);
    configReader.addComment(
        "Scavenge.EnchantExtractionFail.LevelLower.Base",
        new String[] {
          "Defines a chance lowering enchant level if it fails extraction",
          "This is secondary step when extraction fails and will only apply when it does",
          "If Enchant is at level 1 already, then player will not get enchanted book at all",
          "If you want to avoid lowering level of enchant when it fails extraction, set this to"
              + " 100",
          "If you want to always lower level down when extracting enchantments, then set"
              + " EnchantExtractionFail.Base to 100 and set this to 100"
        });
    LowerLevelChanceBase = configReader.get("Scavenge.EnchantExtractionFail.LevelLower.Base", 50.0);
    LowerLevelChanceBase =
        LowerLevelChanceBase < 0.0
            ? 0.0
            : (LowerLevelChanceBase > 100.0 ? 100.0 : LowerLevelChanceBase);
    configReader.addComment(
        "Scavenge.EnchantExtractionFail.LevelLower.ForEachLevel",
        new String[] {
          "Will adjust level lowering chance depending on enchant level to defined max amount at"
              + " max level",
          "This will mean that higher levels will have higher chance to be lowered"
        });
    LowerLevelForEachLevel =
        configReader.get("Scavenge.EnchantExtractionFail.LevelLower.ForEachLevel", 5.0);
    LowerLevelForEachLevel =
        LowerLevelForEachLevel < 0.0
            ? 0.0
            : (LowerLevelForEachLevel > 100.0 ? 100.0 : LowerLevelForEachLevel);
    configReader.addComment(
        "Scavenge.EnchantExtractionFail.LevelLower.MaxChance",
        new String[] {
          "Will adjust level lower chance depending on enchant level to defined max amount at max"
              + " level"
        });
    LowerLevelMaxChance =
        configReader.get("Scavenge.EnchantExtractionFail.LevelLower.MaxChance", 75.0);
    LowerLevelMaxChance =
        LowerLevelMaxChance < 0.0
            ? 0.0
            : (LowerLevelMaxChance > 100.0 ? 100.0 : LowerLevelMaxChance);
    configReader.addComment(
        "Scavenge.Cost.Base",
        new String[] {"Defined base cost of extraction. Set to 0 if you want to make it free"});
    EnchantBaseCost = configReader.get("Scavenge.Cost.Base", 100.0);
    EnchantBaseCost = EnchantBaseCost < 0.0 ? 0.0 : EnchantBaseCost;
    configReader.addComment(
        "Scavenge.Cost.Extra",
        new String[] {
          "Extra cost which depends on enchantment worth which can be defined with /cmi"
              + " setenchantworth ",
          "This value is in percentage from worth value of that each enchantment and item",
          "So if you have base cost of 100, extra cost of 5% and you are trying to extract"
              + " sharpness 5 which worth is 1000 and item sell hand worth is 100, then you will"
              + " have to pay 155 for extraction process"
        });
    EnchantBaseCostExtra = configReader.get("Scavenge.Cost.Extra", 5.0);
    EnchantBaseCostExtra = EnchantBaseCostExtra < 0.0 ? 0.0 : EnchantBaseCostExtra;
    configReader.addComment(
        "Scavenge.WithoutIngredients",
        new String[] {
          "When enabled we will allow scavenging of items which doesn't have valid recipe for"
              + " ingredient return process",
          "This only applies in cases when we can return enchants to player, otherwise it doesn't"
              + " allow you to scavenge item"
        });
    WithoutIngredients = configReader.get("Scavenge.WithoutIngredients", false);
    configReader.addComment(
        "Scavenge.BlackList", new String[] {"List of materials to block from being scavenged"});
    var list =
        configReader.get(
            "Scavenge.BlackList",
            Arrays.asList(
                "diamond", "ironingot", "goldingot", "coal", "IRON_NUGGET", "GOLD_NUGGET"));
    blackList.clear();

    for (String string : list) {
      CMIMaterial cmiMaterial = CMIMaterial.get(string);
      if (cmiMaterial == null
          && !string.equalsIgnoreCase("Material1")
          && !string.equalsIgnoreCase("Material2")) {
        this.plugin.consoleMessage(
            "&cCan't recognize blacklisted scavenger material (" + string + ")");
      }

      if (cmiMaterial != null) {
        blackList.add(cmiMaterial);
      }
    }

    configReader.addComment(
        "Scavenge.BlackToWhiteList",
        new String[] {
          "When set to true, balck list becomes whitelist and will allow scavanging of items"
              + " defined in a blacklist only"
        });
    InvertBlackList = configReader.get("Scavenge.BlackToWhiteList", false);
    configReader.addComment(
        "Scavenge.AllowItemPickups",
        new String[] {
          "When set to false while player have open scavange UI they will not be able to pickup"
              + " item from ground"
        });
    AllowItemPickups = configReader.get("Scavenge.AllowItemPickups", false);
    configReader.addComment(
        "Scavenge.ResetRepairCost",
        new String[] {
          "When enabled items, likes swords, after being salvagted if they dint broke then their"
              + " repair cost will be reset so players can enchant it as like its a new one instead"
              + " of making it to dificult to be enchanted"
        });
    ResetRepairCost = configReader.get("Scavenge.ResetRepairCost", true);
    configReader.save();
  }

  public void openExtractor(final Player player) {
    if (player != null && player.isOnline()) {
      CMIGui cmiGui =
          new CMIGui(player) {
            public void onClose() {
              ItemStack itemStack = this.getInv().getItem(13);
              if (itemStack != null) {
                CMIUser cmiUser = ScavengeManager.this.plugin.getPlayerManager().getUser(player);
                cmiUser.getInventory().addItem(itemStack);
              }
            }

            public void outsideClick(GUIClickType guiClickType) {
              var outer = this;
              if (guiClickType == GUIClickType.LeftShift
                  || guiClickType == GUIClickType.RightShift) {
                CMIScheduler.runTaskLater(
                    ScavengeManager.this.plugin,
                    () -> ScavengeManager.this.updateExtractor(outer),
                    1L);
              }
            }

            public void onOpen() {
              var outer = this;
              CMIScheduler.runTaskLater(
                  ScavengeManager.this.plugin,
                  () -> ScavengeManager.this.updateExtractor(outer),
                  1L);
            }
          };
      cmiGui.setInvSize(GUIRows.r6);
      CMIGuiButton cmiGuiButton =
          new CMIGuiButton(13) {
            public void click(GUIClickType clickType) {
              var outerRef = this;
              CMIScheduler.runTaskLater(
                  ScavengeManager.this.plugin, () -> ScavengeManager.this.processClick(outerRef), 1L);
            }
          };
      cmiGui.setAllowShift(true);
      cmiGuiButton.unlockField();
      this.updateExtractor(cmiGui);
      cmiGui.addButton(cmiGuiButton);
      cmiGui.fillEmptyButtons();
      cmiGui.setAllowItemPickup(AllowItemPickups);
      cmiGui.open();
    }
  }

  private void processClick(CMIGuiButton cmiGuiButton) {
    this.updateExtractor(cmiGuiButton.getGui(), null);
  }

  private void processClick(CMIGuiButton cmiGuiButton, GUIClickType guiClickType) {
    this.updateExtractor(cmiGuiButton.getGui(), guiClickType);
  }

  private void updateExtractor(CMIGui cmiGui) {
    this.updateExtractor(cmiGui, null);
  }

  private void updateExtractor(final CMIGui cmiGui, GUIClickType guiClickType) {
    if (cmiGui.getPlayer() != null
        && cmiGui.getPlayer().getOpenInventory() != null
        && cmiGui.isOpened()) {
      ItemStack itemStack = cmiGui.getInv().getItem(13);
      final Player player = cmiGui.getPlayer();
      final CMIScavengeItem cmiScavengeItem = new CMIScavengeItem(itemStack);

      for (int value = 27; value < 45; value++) {
        cmiGui.getButtons().remove(value);
      }

      int enchantCount = cmiScavengeItem.getEnchants().size() / 2;
      int emptySlots = 30 - enchantCount;
      emptySlots = emptySlots < 26 ? 26 : emptySlots;
      if (cmiScavengeItem.canScavenge()) {
        HashMap<CMIMaterial, Integer> hashMap = new HashMap<>();
        cmiScavengeItem
            .getIngredients()
            .forEach(
                item ->
                    hashMap.compute(
                        CMIMaterial.get(item),
                        (key, currentAmount) ->
                            currentAmount == null
                                ? item.getAmount()
                                : currentAmount + item.getAmount()));

        for (var entry : cmiScavengeItem.getEnchants().entrySet()) {
          if (itemStack.containsEnchantment((Enchantment) entry.getKey())) {
            emptySlots++;
            ItemStack enchantedBook = CMIMaterial.ENCHANTED_BOOK.newItemStack();
            if (enchantedBook.getItemMeta() instanceof EnchantmentStorageMeta) {
              EnchantmentStorageMeta enchantmentStorageMeta =
                  (EnchantmentStorageMeta) enchantedBook.getItemMeta();
              enchantmentStorageMeta.addEnchant(
                  (Enchantment) entry.getKey(), (Integer) entry.getValue(), true);
              enchantedBook.setItemMeta(enchantmentStorageMeta);
            }

            emptySlots = emptySlots > 44 ? 44 : emptySlots;
            CMIGuiButton cmiGuiButton = new CMIGuiButton(emptySlots, enchantedBook);
            WorthEnchantment worthEnchantment =
                this.plugin
                    .getWorthManager()
                    .getEnchantWorth((Enchantment) entry.getKey(), (Integer) entry.getValue());
            if (worthEnchantment != null && worthEnchantment.getSellPrice() > 0.0) {
              cmiGuiButton.addLore(
                  CMILC.getIM(
                      scavenge.class.getSimpleName(),
                      "enchantWorth",
                      "[price]",
                      worthEnchantment.getSellPrice()));
            }

            double number =
                cmiScavengeItem.getEnchantExtractionFailChance((Enchantment) entry.getKey());
            if (number > 0.0) {
              cmiGuiButton.addLore(
                  CMILC.getIM(
                      scavenge.class.getSimpleName(), "enchantFailChance", "[chance]", number));
            }

            double chance =
                cmiScavengeItem.getEnchantLevelLowerChance((Enchantment) entry.getKey());
            if (chance > 0.0 && (Integer) entry.getValue() > 1) {
              cmiGuiButton.addLore(
                  CMILC.getIM(
                      scavenge.class.getSimpleName(), "enchantLevelChance", "[chance]", chance));
            }

            cmiGui.replaceButton(cmiGuiButton);
          }
        }
      }

      Double cost = cmiScavengeItem.getExtractionCost();
      if (itemStack != null && !CMIMaterial.isAir(itemStack.getType())) {
        CMIGuiButton extractButton =
            new CMIGuiButton(
                cmiScavengeItem.canScavenge()
                    ? CMIMaterial.LIME_STAINED_GLASS_PANE
                    : (cmiScavengeItem.getRecipeResultAmount() > itemStack.getAmount()
                        ? CMIMaterial.LIGHT_BLUE_STAINED_GLASS_PANE
                        : CMIMaterial.RED_STAINED_GLASS_PANE)) {
              public void click(GUIClickType clickType) {
                if (cmiScavengeItem.canScavenge()) {
                  CMIUser cmiUser = ScavengeManager.this.plugin.getPlayerManager().getUser(player);
                  ItemStack resultItem = cmiGui.getInv().getItem(13);
                  if (resultItem != null && !CMIMaterial.isAir(resultItem.getType())) {
                    double costValue = cmiScavengeItem.getExtractionCost();
                    boolean flag = false;
                    if (clickType != null && clickType == GUIClickType.LeftShift) {
                      flag = true;
                    }

                    if (flag) {
                      costValue *= resultItem.getAmount();
                    }

                    if (!cmiUser.hasMoney(costValue)) {
                      LC.econ_notEnoughMoney.sendMessage(
                          cmiUser.getPlayer(), new Object[] {"[amount]", costValue});
                    } else {
                      cmiUser.withdraw(costValue);
                      double breakChance = cmiScavengeItem.getItemBreakChance();
                      HashSet<Enchantment> hashSet = new HashSet();
                      if (resultItem.getAmount() == 1) {
                        for (var enchantEntry : resultItem.getEnchantments().entrySet()) {
                          int removedLevel =
                              resultItem.removeEnchantment((Enchantment) enchantEntry.getKey());
                          if (removedLevel == 0) {
                            hashSet.add((Enchantment) enchantEntry.getKey());
                          }
                        }
                      }

                      cmiGui.getInv().setItem(13, null);
                      if (!ScavengeManager.this.isFailed(breakChance)) {
                        if (ScavengeManager.ResetRepairCost) {
                          ItemMeta itemMeta = resultItem.getItemMeta();
                          if (itemMeta instanceof Repairable) {
                            Repairable repairable = (Repairable) itemMeta;
                            repairable.setRepairCost(1);
                            resultItem.setItemMeta(repairable);
                          }
                        }

                        for (Enchantment enchantment : resultItem.getEnchantments().keySet()) {
                          resultItem.removeEnchantment(enchantment);
                        }

                        cmiUser.getInventory().addItem(resultItem);
                      } else {
                        HashMap<CMIMaterial, Integer> materials = new HashMap();

                        for (ItemStack materialItem :
                            cmiScavengeItem.getIngredients(
                                ScavengeManager.ItemMaterialDurabilityCheck, true)) {
                          for (int index = 0;
                              index
                                  < (flag
                                      ? resultItem.getAmount()
                                          / cmiScavengeItem.getRecipeResultAmount()
                                      : 1);
                              index++) {
                            if (!ScavengeManager.this.isFailed(
                                100.0 - cmiScavengeItem.getIngredientReturnChance())) {
                              CMIMaterial cmiMaterial = CMIMaterial.get(materialItem);
                              ItemStack newItem = cmiMaterial.newItemStack();
                              newItem.setAmount(materialItem.getAmount());
                              if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)
                                  && newItem.getItemMeta() instanceof Damageable) {
                                Damageable damageable = (Damageable) newItem.getItemMeta();
                                damageable.setDamage(1);
                                newItem.setItemMeta(damageable);
                              }

                              cmiUser.getInventory().addItem(newItem);
                              Integer integer = (Integer) materials.get(cmiMaterial);
                              if (integer == null) {
                                integer = 0;
                              }

                              materials.put(cmiMaterial, integer + materialItem.getAmount());
                            }
                          }
                        }

                        if (resultItem.getAmount() > 1 && !flag) {
                          resultItem.setAmount(
                              resultItem.getAmount() - cmiScavengeItem.getRecipeResultAmount());
                          cmiGui.getInv().setItem(13, resultItem);
                        }

                        String string = "";

                        for (Entry materialEntry : materials.entrySet()) {
                          if (!string.isEmpty()) {
                            string = string + LC.info_ListSpliter.getLocale(new Object[0]);
                          }

                          string =
                              string
                                  + CMILC.getIM(
                                      scavenge.class.getSimpleName(),
                                      "gotIngredientsFormat",
                                      "[material]",
                                      ((CMIMaterial) materialEntry.getKey()).getName(),
                                      "[amount]",
                                      materialEntry.getValue());
                        }

                        if (!string.isEmpty()) {
                          cmiUser.sendMessage(
                              CMILC.getIM(
                                  scavenge.class.getSimpleName(),
                                  "gotIngredients",
                                  "[itemList]",
                                  string));
                        }

                        if (!cmiScavengeItem.getEnchants().isEmpty()) {
                          cmiUser.sendMessage(
                              CMILC.getIM(
                                  scavenge.class.getSimpleName(),
                                  "itemBroke",
                                  "[chance]",
                                  breakChance));
                        }
                      }

                      for (var enchantPair : cmiScavengeItem.getEnchants().entrySet()) {
                        if (!hashSet.contains(enchantPair.getKey())) {
                          double levelChance =
                              cmiScavengeItem.getEnchantExtractionFailChance(
                                  (Enchantment) enchantPair.getKey());

                          for (int slotIndex = 0;
                              slotIndex < (flag ? resultItem.getAmount() : 1);
                              slotIndex++) {
                            if (!ScavengeManager.this.isFailed(levelChance)) {
                              double chanceValue =
                                  cmiScavengeItem.getEnchantLevelLowerChance(
                                      (Enchantment) enchantPair.getKey());
                              if (ScavengeManager.this.isFailed(chanceValue)) {
                                if ((Integer) enchantPair.getValue() > 1) {
                                  cmiUser
                                      .getInventory()
                                      .addItem(
                                          cmiScavengeItem.createBook(
                                              (Enchantment) enchantPair.getKey(),
                                              (Integer) enchantPair.getValue() - 1));
                                }
                              } else {
                                cmiUser
                                    .getInventory()
                                    .addItem(
                                        cmiScavengeItem.createBook(
                                            (Enchantment) enchantPair.getKey()));
                              }
                            } else {
                              cmiUser.sendMessage(
                                  CMILC.getIM(
                                      scavenge.class.getSimpleName(),
                                      "enchantFailed",
                                      "[enchant]",
                                      CMIEnchantment.getName((Enchantment) enchantPair.getKey()),
                                      "[level]",
                                      enchantPair.getValue(),
                                      "[chance]",
                                      levelChance));
                            }
                          }
                        }
                      }

                      ScavengeManager.this.updateExtractor(cmiGui);
                    }
                  }
                }
              }
            };
        if (cmiScavengeItem.canScavenge()) {
          extractButton.setName(
              CMILC.getIM(
                  scavenge.class.getSimpleName(),
                  "confirm",
                  "[price]",
                  this.plugin.getEconomyManager().format(cost)));
          double breakRate = cmiScavengeItem.getItemBreakChance();
          if (breakRate > 0.0) {
            if (breakRate < 100.0) {
              extractButton.addLore(
                  CMILC.getIM(
                      scavenge.class.getSimpleName(), "itemBreakChance", "[chance]", breakRate));
              if (cmiScavengeItem.getBreakChanceByItemDurability() > 0.0) {
                extractButton.addLore(
                    CMILC.getIM(
                        scavenge.class.getSimpleName(),
                        "itemBreakChanceExtra",
                        "[durability]",
                        cmiScavengeItem.getLeftDurabilityPercentage(),
                        "[chance]",
                        cmiScavengeItem.getBreakChanceByItemDurability()));
              }
            } else {
              extractButton.addLore(CMILC.getIM(scavenge.class.getSimpleName(), "itemBreak"));
            }

            extractButton.addLore(
                CMILC.getIM(
                    scavenge.class.getSimpleName(),
                    "ingredientReturnChance",
                    "[chance]",
                    cmiScavengeItem.getIngredientReturnChance()));
          }
        } else {
          extractButton.setName(CMILC.getIM(scavenge.class.getSimpleName(), "cantScavenge"));
          if (itemStack.getAmount() < cmiScavengeItem.getRecipeResultAmount()) {
            extractButton.addLore(
                CMILC.getIML(
                    scavenge.class.getSimpleName(),
                    "moreLore",
                    "[amount]",
                    cmiScavengeItem.getRecipeResultAmount()));
          }
        }

        for (int slot = 45; slot < 54; slot++) {
          extractButton.setSlot(slot);
          cmiGui.replaceButton(extractButton);
        }
      } else {
        for (int bottomSlot = 45; bottomSlot < 54; bottomSlot++) {
          CMIGuiButton cancelButton =
              new CMIGuiButton(bottomSlot, CMIMaterial.YELLOW_STAINED_GLASS_PANE);
          cancelButton.setName(CMILC.getIM(scavenge.class.getSimpleName(), "insertItem"));
          cmiGui.replaceButton(cancelButton);
        }
      }

      if (cost > 0.0 && cmiScavengeItem.canScavenge()) {
        cmiGui.updateTitle(
            CMILC.getIM(
                scavenge.class.getSimpleName(),
                "scavengeCost",
                "[cost]",
                this.plugin.getEconomyManager().format(cost)));
      } else if (itemStack != null
          && itemStack.getAmount() < cmiScavengeItem.getRecipeResultAmount()) {
        cmiGui.updateTitle(
            CMILC.getIM(
                scavenge.class.getSimpleName(),
                "moreTitle",
                "[input]",
                itemStack.getAmount(),
                "[required]",
                cmiScavengeItem.getRecipeResultAmount()));
      } else {
        cmiGui.updateTitle(CMILC.getIM(scavenge.class.getSimpleName(), "title"));
      }

      cmiGui.fillEmptyButtons();
    }
  }

  private double format(double number) {
    return (int) (number * 100.0) / 100.0;
  }

  private boolean isFailed(double number) {
    if (number >= 100.0) {
      return true;
    }

    Random random = new Random();
    double roll = random.nextDouble() * 100.0;
    return roll < number;
  }
}
