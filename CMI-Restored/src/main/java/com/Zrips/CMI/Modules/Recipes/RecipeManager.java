package com.Zrips.CMI.Modules.Recipes;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIPaperItemSerializator;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.recipe;
import com.Zrips.CMI.utils.Util;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import net.Zrips.CMILib.Container.CMINamespacedKey;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.CmiInventoryType;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.GUI.GUIManager.InvType;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Recipes.CMIRecipe;
import net.Zrips.CMILib.Recipes.CMIRecipeChoice;
import net.Zrips.CMILib.Recipes.CMIRecipeCraftData;
import net.Zrips.CMILib.Recipes.CMIRecipeIngredient;
import net.Zrips.CMILib.Recipes.CMIRecipeType;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.TransmuteRecipe;
import org.bukkit.inventory.meta.PotionMeta;

public class RecipeManager {
  private CMI plugin;
  private boolean RecipesRequireBackwards;
  private boolean Recipes4Sized;
  private HashMap<String, Recipe> disabledRecipes = new HashMap<>();
  private Integer GUIRecipeResultSlot = 24;
  private HashMap<String, CMIRecipe> recipes = new HashMap<>();
  private HashMap<Recipe, CMIRecipe> recipesMap = new HashMap<>();
  public String fileName = "Recipes.yml";

  public RecipeManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Recipes.Condense.RequireBackwards",
        new String[] {
          "When enabled trying to condsense or uncondense we will check for backwards recipe and"
              + " will not allow to perform those actions if player is unable to revert his"
              + " actions",
          "While disabled we will ignore backwards recipe check",
          "Keep in mind that disabling might result in unpredicted behavior where 4 wood planks"
              + " gets converted into workbench"
        });
    this.RecipesRequireBackwards = configReader.get("Recipes.Condense.RequireBackwards", true);
    configReader.addComment(
        "Recipes.Condense.4Sized",
        new String[] {
          "Should we condense recipes with 4 same ingredients",
          "While this is enabled and RequireBackwards disabled then oak plants might be converted"
              + " into workbenches"
        });
    this.Recipes4Sized = configReader.get("Recipes.Condense.4Sized", true);
  }

  public boolean isCustom(Recipe recipe) {
    return this.recipesMap.containsKey(recipe);
  }

  public CMIRecipe getByKey(String string) {
    return string == null ? null : this.recipes.get(string);
  }

  public boolean removeRecipe(CMIRecipe cmiRecipe) {
    this.recipes.remove(cmiRecipe.getIdentificator());
    this.recipesMap.remove(cmiRecipe.getRecipe());
    this.saveRecipes();
    return true;
  }

  public Recipe addRecipe(
      CMIRecipeType cmiRecipeType,
      ItemStack itemStack,
      HashMap<Integer, CMIRecipeIngredient> hashMap) {
    return this.addRecipe(cmiRecipeType, itemStack, hashMap, null);
  }

  public Recipe addRecipe(
      CMIRecipeType cmiRecipeType,
      ItemStack itemStack,
      HashMap<Integer, CMIRecipeIngredient> hashMap,
      CMIRecipeCraftData cmiRecipeCraftData) {
    return this.addRecipe(cmiRecipeType, itemStack, hashMap, cmiRecipeCraftData, null);
  }

  public Recipe addRecipe(
      CMIRecipeType cmiRecipeType,
      ItemStack itemStack,
      HashMap<Integer, CMIRecipeIngredient> hashMap,
      CMIRecipeCraftData cmiRecipeCraftData,
      String string) {
    Recipe recipe =
        CMIRecipe.createRecipe(cmiRecipeType, itemStack, hashMap, cmiRecipeCraftData, string);
    if (recipe == null) {
      return null;
    }

    this.addRecipe(cmiRecipeType, recipe, cmiRecipeCraftData);
    return recipe;
  }

  @Deprecated
  public Recipe addRecipe(CMIRecipeType cmiRecipeType, Recipe recipe) {
    return this.addRecipe(cmiRecipeType, recipe, null);
  }

  public Recipe addRecipe(
      CMIRecipeType cmiRecipeType, Recipe recipe, CMIRecipeCraftData cmiRecipeCraftData) {
    return this.addRecipe(cmiRecipeType, recipe, cmiRecipeCraftData, null);
  }

  public Recipe addRecipe(
      CMIRecipeType cmiRecipeType,
      Recipe recipe,
      CMIRecipeCraftData cmiRecipeCraftData,
      String string) {
    if (recipe == null) {
      return null;
    } else {
      CMINamespacedKey cmiNamespacedKey = CMINamespacedKey.getKey(recipe);
      if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)
          && CMIRecipe.getRecipe(cmiNamespacedKey) != null) {
        CMIMessages.consoleMessage(
            "Tried to add recipe with "
                + cmiNamespacedKey.toString()
                + " key which already exists. Skipping");
        return recipe;
      } else {
        Bukkit.getServer().addRecipe(recipe);
        CMIRecipe cmiRecipe =
            new CMIRecipe(cmiNamespacedKey.getKey(), cmiRecipeType)
                .setRecipe(recipe)
                .setData(cmiRecipeCraftData);
        cmiRecipe.setCustomKey(string);
        this.recipes.put(cmiRecipe.getIdentificator(), cmiRecipe);
        this.recipesMap.put(cmiRecipe.getRecipe(), cmiRecipe);
        this.saveRecipes();
        return recipe;
      }
    }
  }

  public Recipe exist(Recipe recipe) {
    var iterator = Bukkit.recipeIterator();

    label81:
    while (iterator.hasNext()) {
      Recipe candidate = (Recipe) iterator.next();
      if (!candidate.getResult().isSimilar(recipe.getResult())) {
        if (candidate instanceof ShapelessRecipe && recipe instanceof ShapelessRecipe) {
          ShapelessRecipe shapelessRecipe = (ShapelessRecipe) candidate;
          ShapelessRecipe shapeless = (ShapelessRecipe) recipe;

          for (ItemStack itemStack : shapelessRecipe.getIngredientList()) {
            if (!shapeless.getIngredientList().contains(itemStack)) {
              continue label81;
            }
          }

          return candidate;
        } else if (candidate instanceof ShapedRecipe && recipe instanceof ShapedRecipe) {
          ShapedRecipe shapedRecipe = (ShapedRecipe) candidate;
          ShapedRecipe shaped = (ShapedRecipe) recipe;
          HashMap<Integer, ItemStack> hashMap = new HashMap();
          HashMap<Integer, ItemStack> ingredientMap = new HashMap();
          String[] strings = shapedRecipe.getShape();

          for (int value = 0; value < 9; value++) {
            ItemStack ingredient =
                (ItemStack) shapedRecipe.getIngredientMap().get(strings[value].charAt(0));
            if (ingredient != null) {
              hashMap.put(value, ingredient);
            }
          }

          strings = shaped.getShape();

          for (int slotIndex = 0; slotIndex < 9; slotIndex++) {
            ItemStack slotItem =
                (ItemStack) shaped.getIngredientMap().get(strings[slotIndex].charAt(0));
            if (slotItem != null) {
              ingredientMap.put(slotIndex, slotItem);
            }
          }

          for (Entry entry : hashMap.entrySet()) {
            ItemStack ingredientItem = (ItemStack) ingredientMap.get(entry.getKey());
            if (ingredientItem == null || !ingredientItem.isSimilar((ItemStack) entry.getValue())) {
              continue label81;
            }
          }

          return candidate;
        }
      }
    }

    return null;
  }

  public Recipe getRecipe(HashMap<Integer, ItemStack> hashMap) {
    ArrayList<ItemStack> arrayList = new ArrayList();

    for (var entry : hashMap.entrySet()) {
      arrayList.add((ItemStack) entry.getValue());
    }

    var iterator = Bukkit.recipeIterator();

    label74:
    while (iterator.hasNext()) {
      Recipe recipe = (Recipe) iterator.next();
      if (recipe instanceof ShapelessRecipe) {
        ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;

        for (ItemStack itemStack : shapelessRecipe.getIngredientList()) {
          if (!arrayList.contains(itemStack)) {
            continue label74;
          }
        }

        return recipe;
      } else if (recipe instanceof ShapedRecipe) {
        ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
        HashMap<Integer, ItemStack> slotMap = new HashMap();
        String[] strings = shapedRecipe.getShape();
        int value = 0;

        for (int rowIndex = 0; rowIndex < 3 && strings.length > rowIndex; rowIndex++) {
          for (int colIndex = 0; colIndex < 3; colIndex++) {
            value++;
            if (strings[rowIndex].length() > colIndex) {
              ItemStack ingredientItem =
                  (ItemStack)
                      shapedRecipe.getIngredientMap().get(strings[rowIndex].charAt(colIndex));
              if (ingredientItem != null) {
                slotMap.put(value, ingredientItem);
              }
              continue;
            }
          }
        }

        for (Entry slotEntry : slotMap.entrySet()) {
          ItemStack slotItem = (ItemStack) hashMap.get(slotEntry.getKey());
          if (slotItem == null || !slotItem.isSimilar((ItemStack) slotEntry.getValue())) {
            continue label74;
          }
        }

        return recipe;
      }
    }

    return null;
  }

  public void save() {
    this.saveRecipes();
  }

  private void saveRecipes() {
    if (this.plugin.isFullyLoaded()) {
      File file = new File(this.plugin.getDataFolder(), "Saves" + File.separator + this.fileName);
      if (!file.exists()) {
        try {
          file.createNewFile();
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }
      } else {
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        yamlConfiguration.set("CRecipes", null);
        yamlConfiguration.set("Disabled", null);

        for (Recipe recipe : this.disabledRecipes.values()) {
          CMINamespacedKey cmiNamespacedKey = CMINamespacedKey.getKey(recipe);
          yamlConfiguration.set(
              "Disabled." + cmiNamespacedKey.getKey(), cmiNamespacedKey.getNamespace());
        }

        for (CMIRecipe cmiRecipe : this.recipes.values()) {
          yamlConfiguration.set(
              "CRecipes." + cmiRecipe.getIdentificator() + ".Type", cmiRecipe.getType().name());
          CMIPaperItemSerializator.setTo(
              yamlConfiguration,
              "CRecipes." + cmiRecipe.getIdentificator() + ".Result",
              cmiRecipe.getResult());
          if (cmiRecipe.getCustomKey() != null) {
            yamlConfiguration.set(
                "CRecipes." + cmiRecipe.getIdentificator() + ".CustomKey",
                cmiRecipe.getCustomKey());
          }

          for (var entry : cmiRecipe.getIngredients().entrySet()) {
            CMIPaperItemSerializator.setTo(
                yamlConfiguration,
                "CRecipes." + cmiRecipe.getIdentificator() + ".Recipe." + entry.getKey() + ".item",
                ((CMIRecipeIngredient) entry.getValue()).getItem());
            if (((CMIRecipeIngredient) entry.getValue())
                .getChoice()
                .equals(CMIRecipeChoice.byItemStack)) {
              yamlConfiguration.set(
                  "CRecipes."
                      + cmiRecipe.getIdentificator()
                      + ".Recipe."
                      + entry.getKey()
                      + ".byItem",
                  true);
            }
          }

          if (cmiRecipe.getType().isTimeAndExp()
              && Version.isCurrentEqualOrHigher(Version.v1_13_R1)
              && cmiRecipe.getData() != null) {
            yamlConfiguration.set(
                "CRecipes." + cmiRecipe.getIdentificator() + ".Cook",
                cmiRecipe.getData().getCookingTime());
            yamlConfiguration.set(
                "CRecipes." + cmiRecipe.getIdentificator() + ".Exp", cmiRecipe.getData().getExp());
          }
        }

        try {
          yamlConfiguration.save(file);
        } catch (IOException ioException2) {
          ioException2.printStackTrace();
        }
      }
    }
  }

  private void loadCustomRecipes(YamlConfiguration yamlConfiguration) {
    long longValue = System.currentTimeMillis();
    if (yamlConfiguration.isConfigurationSection("CRecipes")) {
      var set = yamlConfiguration.getConfigurationSection("CRecipes").getKeys(false);
      if (!set.isEmpty()) {
        try {
          Integer.parseInt((String) set.iterator().next());
          ConfigReader configReader =
              new ConfigReader(this.plugin, "Saves" + File.separator + this.fileName);
          configReader.saveToBackup(false);
        } catch (Throwable throwable) {
        }
      }

      int value = 0;

      for (String string : set) {
        ConfigurationSection configurationSection =
            yamlConfiguration.getConfigurationSection("CRecipes." + string);
        String recipeKey = null;
        if (configurationSection.isString("CustomKey")) {
          recipeKey = configurationSection.getString("CustomKey");
        }

        CMIRecipeType cmiRecipeType =
            CMIRecipeType.getByName(configurationSection.getString("Type"));
        if (cmiRecipeType == null) {
          this.plugin.consoleMessage("&cCant determine recipe type for " + string);
        } else {
          ItemStack itemStack =
              CMIPaperItemSerializator.getItem(configurationSection.get("Result"));
          if (itemStack == null) {
            this.plugin.consoleMessage("&cCant determine recipe result type for " + string);
          } else if (!configurationSection.isConfigurationSection("Recipe")) {
            this.plugin.consoleMessage("&cCant determine recipe type for " + string);
          } else {
            var map = configurationSection.getConfigurationSection("Recipe").getValues(false);
            HashMap<Integer, CMIRecipeIngredient> hashMap = new HashMap();

            for (Entry entry : map.entrySet()) {
              try {
                if (entry.getValue() instanceof ItemStack) {
                  hashMap.put(
                      Integer.parseInt((String) entry.getKey()) - 1,
                      new CMIRecipeIngredient(CMIPaperItemSerializator.getItem(entry.getValue())));
                } else {
                  var recipeValues =
                      configurationSection
                          .getConfigurationSection("Recipe." + (String) entry.getKey())
                          .getValues(false);
                  CMIRecipeIngredient cmiRecipeIngredient =
                      new CMIRecipeIngredient(
                          CMIPaperItemSerializator.getItem(recipeValues.get("item")));
                  if (recipeValues.containsKey("byItem")) {
                    cmiRecipeIngredient.setChoice(
                        (Boolean) recipeValues.get("byItem")
                            ? CMIRecipeChoice.byItemStack
                            : CMIRecipeChoice.byMaterial);
                  }

                  hashMap.put(Integer.parseInt((String) entry.getKey()), cmiRecipeIngredient);
                }
              } catch (Exception exception) {
                exception.printStackTrace();
              }
            }

            if (hashMap.isEmpty()) {
              this.plugin.consoleMessage("&cCant determine recipe ingredients type for " + string);
            } else {
              CMIRecipeCraftData cmiRecipeCraftData = null;
              if (configurationSection.isDouble("Exp") && configurationSection.isInt("Cook")) {
                cmiRecipeCraftData =
                    new CMIRecipeCraftData(
                        (float) configurationSection.getDouble("Exp"),
                        configurationSection.getInt("Cook"));
              }

              value++;
              this.addRecipe(cmiRecipeType, itemStack, hashMap, cmiRecipeCraftData, recipeKey);
            }
          }
        }
      }

      if (value > 0) {
        this.plugin.loadMessage(value, "custom recipes", System.currentTimeMillis() - longValue);
      }
    }
  }

  private void loadDisabled(YamlConfiguration yamlConfiguration) {
    if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)
        && yamlConfiguration.isConfigurationSection("Disabled")) {
      var set = yamlConfiguration.getConfigurationSection("Disabled").getKeys(false);
      HashMap<String, String> hashMap = new HashMap();

      for (String string : set) {
        if (yamlConfiguration.isString("Disabled." + string)) {
          hashMap.put(string, yamlConfiguration.getString("Disabled." + string));
        }
      }

      CMIScheduler.runTaskLater(
          this.plugin,
          () -> {
            long longValue = System.currentTimeMillis();
            int value = 0;

            for (Entry entry : hashMap.entrySet()) {
              CMINamespacedKey cmiNamespacedKey =
                  new CMINamespacedKey((String) entry.getValue(), (String) entry.getKey());
              Recipe recipe = CMIRecipe.getRecipe(cmiNamespacedKey);
              if (recipe == null) {
                CMIMessages.consoleMessage(
                    "&eUnrecognized disabled recipe (" + (String) entry.getKey() + ")");
              } else {
                boolean flag =
                    Bukkit.getServer()
                        .removeRecipe(
                            new NamespacedKey(
                                cmiNamespacedKey.getNamespace(), cmiNamespacedKey.getKey()));
                if (!flag) {
                  CMIMessages.consoleMessage(
                      "&eFailed to disable recipe (" + (String) entry.getKey() + ")");
                } else if (!Version.isCurrentEqualOrLower(Version.v1_14_R2) && !flag) {
                  CMIMessages.consoleMessage(
                      "&eUnrecognized recipe which needs to be disabled ("
                          + (String) entry.getKey()
                          + ")");
                } else {
                  this.disabledRecipes.put(cmiNamespacedKey.getKey(), recipe);
                  value++;
                }
              }
            }

            if (value > 0) {
              this.plugin.loadMessage(
                  value, "disabled recipes", System.currentTimeMillis() - longValue);
            }
          },
          20L);
    }
  }

  public void load() {
    CMI.getInstance()
        .safeRenameFile(this.fileName.toLowerCase(), "Saves" + File.separator + this.fileName);
    File file = new File(this.plugin.getDataFolder(), "Saves" + File.separator + this.fileName);
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException ioException) {
      }
    } else {
      YamlConfiguration yamlConfiguration = null;

      try {
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
      } catch (Throwable throwable) {
        throwable.printStackTrace();

        try {
          ConfigReader configReader = new ConfigReader(file);
          configReader.saveToBackup();
        } catch (Exception exception) {
          exception.printStackTrace();
        }

        return;
      }

      this.loadCustomRecipes(yamlConfiguration);
      this.loadDisabled(yamlConfiguration);
    }
  }

  public Integer getGUIRecipeResultSlot() {
    return this.GUIRecipeResultSlot;
  }

  public HashMap<String, CMIRecipe> getCustomRecipes() {
    return this.recipes;
  }

  @Deprecated
  public List<CMIRecipe> getRecipes() {
    return new ArrayList<>(this.recipes.values());
  }

  @Deprecated
  public void setRecipes(List<CMIRecipe> list) {
    this.recipes = new HashMap<>();

    for (CMIRecipe cmiRecipe : list) {
      this.recipes.put(cmiRecipe.getIdentificator(), cmiRecipe);
      this.recipesMap.put(cmiRecipe.getRecipe(), cmiRecipe);
    }
  }

  public Boolean openRecipeUI(Player player, CMIItemStack cmiItemStack, int value) {
    return this.openRecipeUI(player, cmiItemStack, value, false);
  }

  public Boolean openOnlyCustomRecipeUI(Player player, int value) {
    ArrayList<Recipe> arrayList = new ArrayList();
    this.getCustomRecipes()
        .values()
        .forEach(
            cmiRecipe -> {
              if (!this.isDisabled(cmiRecipe.getRecipe())) {
                arrayList.add(cmiRecipe.getRecipe());
              }
            });
    return this.openRecipeUI(player, value, arrayList);
  }

  public Boolean openRecipeUI(Player player, CMIItemStack cmiItemStack, int value, boolean flag) {
    var list = cmiItemStack.getRecipesFor();
    list.addAll(cmiItemStack.getRecipesFrom());
    if (PermissionsManager.CMIPerm.command_recipe_admin.hasPermission(player)) {
      list.addAll(this.getDisabled(cmiItemStack.getItemStack()));
    }

    Integer integer = Util.getCustomModelData(cmiItemStack.getItemStack());
    LinkedHashMap<String, Recipe> linkedHashMap = new LinkedHashMap();
    Iterator iterator = list.iterator();

    while (true) {
      Recipe recipe;
      CMINamespacedKey cmiNamespacedKey;
      while (true) {
        if (!iterator.hasNext()) {
          list.clear();
          list.addAll(linkedHashMap.values());
          return this.openRecipeUI(player, value, list);
        }

        recipe = (Recipe) iterator.next();
        cmiNamespacedKey = CMINamespacedKey.getKey(recipe);
        if (!flag
            || cmiNamespacedKey == null
            || !cmiNamespacedKey.toString().startsWith("minecraft:")) {
          if (integer == null) {
            break;
          }

          boolean hasIngredients = false;

          for (ItemStack itemStack : CMIRecipe.getIngredientsList(recipe)) {
            if (getModelData(itemStack) == integer) {
              hasIngredients = true;
            }
          }

          if (getModelData(recipe.getResult()) == integer) {
            hasIngredients = true;
          }

          if (hasIngredients) {
            break;
          }
        }
      }

      if (cmiNamespacedKey != null) {
        linkedHashMap.put(cmiNamespacedKey.toString(), recipe);
      }
    }
  }

  private static int getModelData(ItemStack itemStack) {
    Integer integer = Util.getCustomModelData(itemStack);
    return integer == null ? 0 : integer;
  }

  @Deprecated
  public Boolean openRecipeUI(
      Player player, CMIItemStack cmiItemStack, int value, List<Recipe> list) {
    return this.openRecipeUI(player, value, list);
  }

  public Boolean openRecipeUI(final Player player, int value, final List<Recipe> list) {
    if (value > list.size()) {
      value = list.size();
    }

    if (list.isEmpty()) {
      CMILC.info(recipe.class.getSimpleName(), player, "noRecipes");
      return null;
    }

    PageInfo pageInfo = new PageInfo(1, list.size(), value);

    for (final Recipe recipe : list) {
      if (pageInfo.isEntryOk()) {
        if (pageInfo.isBreak()) {
          break;
        }

        CMIGui cmiGui =
            new CMIGui(player) {
              public void pageChange(int page) {
                RecipeManager.this.openRecipeUI(player, page, list);
              }

              public boolean click(int slot, GUIClickType guiClickType, ItemStack itemStack) {
                if (slot >= 54 && itemStack != null) {
                  RecipeManager.this.openRecipeUI(player, new CMIItemStack(itemStack), 1);
                  return true;
                } else {
                  return true;
                }
              }
            };
        cmiGui.addLock(InvType.Gui);
        cmiGui.addLock(InvType.Main);
        CMIRecipeType cmiRecipeType = CMIRecipeType.getByName(recipe.getClass().getSimpleName());
        String string =
            CMILC.getIM(
                recipe.class.getSimpleName(),
                "type",
                "[type]",
                CMILC.getIM(recipe.class.getSimpleName(), "types." + cmiRecipeType));
        if (list.size() > 1) {
          string =
              string
                  + CMILC.getIM(
                      recipe.class.getSimpleName(),
                      "page",
                      "[current]",
                      value,
                      "[total]",
                      list.size());
        }

        cmiGui.setTitle(string);
        cmiGui.setInvSize(GUIRows.r6);
        cmiGui.setCmiInventoryType(CmiInventoryType.Recipes);
        if (recipe instanceof ShapelessRecipe) {
          ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;
          int ingredientIndex = 0;

          for (final ItemStack ingredient : shapelessRecipe.getIngredientList()) {
            ItemStack ingredientCopy = ingredient.clone();
            if (CMIMaterial.isPotion(ingredientCopy.getType())
                || ingredientCopy.getType().name().contains("SPLASH_POTION")
                || ingredientCopy.getType().name().contains("LINGERING_POTION")) {
              PotionMeta potionMeta = (PotionMeta) ingredientCopy.getItemMeta();
              potionMeta.setDisplayName(CMIMaterial.get(ingredientCopy).getName());
              ingredientCopy.setItemMeta(potionMeta);
            }

            Integer integer = (Integer) cmiRecipeType.getIngredientSlots().get(ingredientIndex);
            CMIGuiButton cmiGuiButton =
                new CMIGuiButton(integer, ingredientCopy) {
                  public void click(GUIClickType clickType) {
                    RecipeManager.this.openRecipeUI(player, new CMIItemStack(ingredient), 1);
                  }
                };
            cmiGui.addButton(cmiGuiButton);
            ingredientIndex++;
          }
        } else if (recipe instanceof ShapedRecipe) {
          ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
          String[] strings = shapedRecipe.getShape();
          int slotCounter = 0;

          for (int rowIndex = 0; rowIndex < 3 && strings.length > rowIndex; rowIndex++) {
            String rowText = strings[rowIndex];

            for (int colIndex = 0; colIndex < 3; colIndex++) {
              if (rowText.length() <= colIndex) {
                slotCounter++;
              } else {
                char ch = rowText.charAt(colIndex);
                Integer ingredientSlot =
                    (Integer) cmiRecipeType.getIngredientSlots().get(slotCounter);
                ItemStack shapedIngredient = (ItemStack) shapedRecipe.getIngredientMap().get(ch);
                if (shapedIngredient == null) {
                  slotCounter++;
                } else {
                  final ItemStack ingredientStack = shapedIngredient.clone();
                  if (CMIMaterial.isPotion(ingredientStack.getType())
                      || ingredientStack.getType().name().contains("SPLASH_POTION")
                      || ingredientStack.getType().name().contains("LINGERING_POTION")) {
                    PotionMeta potionMeta = (PotionMeta) ingredientStack.getItemMeta();
                    potionMeta.setDisplayName(CMIMaterial.get(ingredientStack).getName());
                    ingredientStack.setItemMeta(potionMeta);
                  }

                  CMIGuiButton clearButton =
                      new CMIGuiButton(ingredientSlot, ingredientStack) {
                        public void click(GUIClickType clickType) {
                          RecipeManager.this.openRecipeUI(
                              player, new CMIItemStack(ingredientStack), 1);
                        }
                      };
                  cmiGui.addButton(clearButton);
                  slotCounter++;
                }
              }
            }
          }
        } else if (recipe instanceof FurnaceRecipe) {
          final FurnaceRecipe furnaceRecipe = (FurnaceRecipe) recipe;
          CMIGuiButton saveButton =
              new CMIGuiButton(
                  (Integer) cmiRecipeType.getIngredientSlots().get(0), furnaceRecipe.getInput()) {
                public void click(GUIClickType clickType) {
                  RecipeManager.this.openRecipeUI(
                      player, new CMIItemStack(furnaceRecipe.getInput()), 1);
                }
              };
          cmiGui.addButton(saveButton);
          saveButton =
              new CMIGuiButton(
                  (Integer) cmiRecipeType.getIngredientSlots().get(0) + 9, Material.LAVA_BUCKET);
          cmiGui.addButton(saveButton);
        } else {
          if (cmiRecipeType.toString().equalsIgnoreCase("Transmute")) {
            final TransmuteRecipe transmuteRecipe = (TransmuteRecipe) recipe;
            RecipeChoice recipeChoice = transmuteRecipe.getInput();
            List<ItemStack> resultItems = new ArrayList();
            if (recipeChoice instanceof ExactChoice) {
              resultItems = ((ExactChoice) recipeChoice).getChoices();
            } else if (recipeChoice instanceof MaterialChoice) {
              for (Material material : ((MaterialChoice) recipeChoice).getChoices()) {
                if (!material.equals(Material.AIR)) {
                  resultItems.add(new ItemStack(material));
                }
              }
            }

            if (resultItems.isEmpty()) {
              break;
            }

            final ItemStack resultItem = (ItemStack) resultItems.get(0);
            CMIGuiButton deleteButton =
                new CMIGuiButton((Integer) cmiRecipeType.getIngredientSlots().get(0), resultItem) {
                  public void click(GUIClickType clickType) {
                    RecipeManager.this.openRecipeUI(player, new CMIItemStack(resultItem), 1);
                  }
                };
            cmiGui.addButton(deleteButton);
            deleteButton =
                new CMIGuiButton(
                    (Integer) cmiRecipeType.getIngredientSlots().get(1),
                    transmuteRecipe.getMaterial().getItemStack()) {
                  public void click(GUIClickType clickType) {
                    RecipeManager.this.openRecipeUI(
                        player, new CMIItemStack(transmuteRecipe.getMaterial().getItemStack()), 1);
                  }
                };
            cmiGui.addButton(deleteButton);
          }

          switch (cmiRecipeType) {
            case Campfire:
              cmiGui.addButton(
                  new CMIGuiButton(
                      (Integer) cmiRecipeType.getIngredientSlots().get(0) + 9,
                      CMIMaterial.CAMPFIRE));
              break;
            case Cooking:
            case Merchant:
            case Stonecutting:
            default:
              final var ingredientList = CMIRecipe.getIngredientsList(recipe);
              if (!ingredientList.isEmpty()) {
                CMIGuiButton copyButton =
                    new CMIGuiButton(
                        (Integer) cmiRecipeType.getIngredientSlots().get(0),
                        (ItemStack) ingredientList.get(0)) {
                      public void click(GUIClickType clickType) {
                        RecipeManager.this.openRecipeUI(
                            player, new CMIItemStack((ItemStack) ingredientList.get(0)), 1);
                      }
                    };
                cmiGui.addButton(copyButton);
              }
              break;
            case Smoking:
              var ingredientsCopy = CMIRecipe.getIngredientsList(recipe);
              if (!ingredientsCopy.isEmpty()) {
                cmiGui.addButton(
                    new CMIGuiButton(
                        (Integer) cmiRecipeType.getIngredientSlots().get(0),
                        (ItemStack) ingredientsCopy.get(0)));
              }

              cmiGui.addButton(
                  new CMIGuiButton(
                      (Integer) cmiRecipeType.getIngredientSlots().get(0) + 9, CMIMaterial.COAL));
              break;
            case Smithing:
              final SmithingRecipe smithingRecipe = (SmithingRecipe) recipe;
              CMIGuiButton resetButton =
                  new CMIGuiButton(
                      (Integer) cmiRecipeType.getIngredientSlots().get(0),
                      smithingRecipe.getBase().getItemStack()) {
                    public void click(GUIClickType clickType) {
                      RecipeManager.this.openRecipeUI(
                          player, new CMIItemStack(smithingRecipe.getBase().getItemStack()), 1);
                    }
                  };
              cmiGui.addButton(resetButton);
              resetButton =
                  new CMIGuiButton(
                      (Integer) cmiRecipeType.getIngredientSlots().get(1),
                      smithingRecipe.getAddition().getItemStack()) {
                    public void click(GUIClickType clickType) {
                      RecipeManager.this.openRecipeUI(
                          player, new CMIItemStack(smithingRecipe.getAddition().getItemStack()), 1);
                    }
                  };
              cmiGui.addButton(resetButton);
          }
        }

        CMIGuiButton shapeButton =
            new CMIGuiButton(
                CMI.getInstance().getRecipeManager().getGUIRecipeResultSlot(), recipe.getResult());
        cmiGui.addButton(shapeButton);
        if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe) {
          for (int ingredientIndex = 0; ingredientIndex < 9; ingredientIndex++) {
            Integer slotNumber = (Integer) cmiRecipeType.getIngredientSlots().get(ingredientIndex);
            if (cmiGui.getButtons().get(slotNumber) == null) {
              shapeButton = new CMIGuiButton(slotNumber, CMIMaterial.LIGHT_GRAY_STAINED_GLASS_PANE);
              shapeButton.setName("&6");
              cmiGui.addButton(shapeButton);
            }
          }
        }

        if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)
            && PermissionsManager.CMIPerm.command_recipe_admin.hasPermission(player)) {
          final CMINamespacedKey cmiNamespacedKey = CMINamespacedKey.getKey(recipe);
          if (Bukkit.getServer()
                      .getRecipe(
                          new NamespacedKey(
                              cmiNamespacedKey.getNamespace(), cmiNamespacedKey.getKey()))
                  != null
              || this.disabledRecipes.containsKey(cmiNamespacedKey.getKey())) {
            CMIGuiButton backButton =
                new CMIGuiButton(8, CMIMaterial.GREEN_WOOL) {
                  public void click(GUIClickType clickType) {
                    if (RecipeManager.this.disabledRecipes.containsKey(cmiNamespacedKey.getKey())) {
                      Recipe currentRecipe =
                          RecipeManager.this.disabledRecipes.remove(cmiNamespacedKey.getKey());
                      Bukkit.getServer().addRecipe(currentRecipe);
                    } else {
                      RecipeManager.this.disabledRecipes.put(cmiNamespacedKey.getKey(), recipe);
                      Bukkit.getServer()
                          .removeRecipe(
                              new NamespacedKey(
                                  cmiNamespacedKey.getNamespace(), cmiNamespacedKey.getKey()));
                    }

                    this.updateLooks();
                    this.update();
                    RecipeManager.this.saveRecipes();
                  }

                  public void updateLooks() {
                    if (RecipeManager.this.disabledRecipes.containsKey(cmiNamespacedKey.getKey())) {
                      this.setItem(CMIMaterial.YELLOW_WOOL);
                      this.setName(LC.modify_disabled.getLocale(new Object[0]));
                    } else {
                      this.setItem(CMIMaterial.GREEN_WOOL);
                      this.setName(LC.modify_enabled.getLocale(new Object[0]));
                    }
                  }
                };
            cmiGui.addButton(backButton);
          }
        }

        cmiGui.addPagination(pageInfo);
        cmiGui.fillEmptyButtons();
        cmiGui.open();
        return true;
      }
    }

    return null;
  }

  private Set<Recipe> getDisabled(ItemStack itemStack) {
    HashSet<Recipe> hashSet = new HashSet();
    if (itemStack == null) {
      return hashSet;
    }

    itemStack = itemStack.clone();

    for (Recipe recipe : this.disabledRecipes.values()) {
      for (ItemStack ingredient : CMIRecipe.getIngredientsList(recipe)) {
        if (ingredient.isSimilar(itemStack)) {
          hashSet.add(recipe);
          break;
        }
      }

      if (recipe.getResult().isSimilar(itemStack)) {
        hashSet.add(recipe);
      }
    }

    return hashSet;
  }

  public boolean isDisabled(Recipe recipe) {
    CMINamespacedKey cmiNamespacedKey = CMINamespacedKey.getKey(recipe);
    return this.disabledRecipes.containsKey(cmiNamespacedKey.getKey());
  }

  public boolean isRecipesRequireBackwards() {
    return this.RecipesRequireBackwards;
  }

  public boolean isRecipes4Sized() {
    return this.Recipes4Sized;
  }
}
