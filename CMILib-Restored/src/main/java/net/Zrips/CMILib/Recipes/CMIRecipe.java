package net.Zrips.CMILib.Recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CMINamespacedKey;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.inventory.TransmuteRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class CMIRecipe {
  private Recipe recipe = null;
  private String identificator = null;
  private String customKey = null;
  private CMIRecipeType type = null;
  private boolean discoverable = true;
  private CMIRecipeCraftData data = null;

  public CMIRecipe(String identificator, CMIRecipeType type) {
    this.identificator = identificator;
    this.type = type;
  }

  public String getIdentificator() {
    return this.identificator;
  }

  public CMIRecipe setIdentificator(String identificator) {
    this.identificator = identificator;
    return this;
  }

  public Recipe getRecipe() {
    return this.recipe;
  }

  public CMIRecipe setRecipe(Recipe recipe) {
    this.recipe = recipe;
    return this;
  }

  public CMIRecipeType getType() {
    return this.type;
  }

  public CMIRecipe setType(CMIRecipeType type) {
    this.type = type;
    return this;
  }

  public ItemStack getResult() {
    return this.recipe.getResult();
  }

  public HashMap<Integer, ItemStack> getIngridients() {
    HashMap<Integer, ItemStack> map = new HashMap<>();
    switch (this.type) {
      case Shaped:
        ShapedRecipe src = (ShapedRecipe) this.recipe;
        String[] shape = src.getShape();
        List<String> chars = new ArrayList<>();

        for (String one : shape) {
          chars.addAll(Arrays.asList(one.split("(?<=\\G.)")));
        }

        for (int i = 0; i < 9 && chars.size() > i; i++) {
          String ch = chars.get(i);
          ItemStack item = (ItemStack) src.getIngredientMap().get(ch.charAt(0));
          if (item != null) {
            map.put(i + 1, item);
          }
        }

        return map;
      case Shapeless:
        ShapelessRecipe rc = (ShapelessRecipe) this.recipe;
        int i = 0;

        for (ItemStack one : rc.getIngredientList()) {
          i++;
          if (one != null) {
            map.put(i, one);
          }
        }

        return map;
      case Furnace:
        FurnaceRecipe fc = (FurnaceRecipe) this.recipe;
        map.put(1, fc.getInput());
        return map;
      case Transmute:
        TransmuteRecipe tc = (TransmuteRecipe) this.recipe;
        CMIRecipeIngredient base = getRecipeChoice(tc.getMaterial());
        CMIRecipeIngredient ingredient = getRecipeChoice(tc.getInput());
        if (base != null && ingredient != null) {
          map.put(1, base.getItem());
          map.put(2, ingredient.getItem());
          return map;
        }

        return map;
      default:
        return null;
    }
  }

  private static CMIRecipeIngredient getRecipeChoice(RecipeChoice choice) {
    if (choice instanceof MaterialChoice) {
      Iterator choiceIterator = ((MaterialChoice) choice).getChoices().iterator();
      if (choiceIterator.hasNext()) {
        Material material = (Material) choiceIterator.next();
        return new CMIRecipeIngredient(new ItemStack(material))
            .setChoice(CMIRecipeChoice.byMaterial);
      } else {
        return null;
      }
    } else {
      return choice instanceof ExactChoice
          ? new CMIRecipeIngredient((ItemStack) ((ExactChoice) choice).getChoices().get(0))
              .setChoice(CMIRecipeChoice.byItemStack)
          : null;
    }
  }

  public static Recipe makeShapedRecipe(
      ItemStack result, HashMap<Integer, CMIRecipeIngredient> Recipe) {
    return makeShapedRecipe(result, Recipe, null);
  }

  public static Recipe makeShapedRecipe(
      ItemStack result, HashMap<Integer, CMIRecipeIngredient> Recipe, String customKey) {
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      ShapedRecipe NewShapedRecipe =
          new ShapedRecipe(
              new NamespacedKey(
                  CMILib.getInstance(),
                  customKey == null
                      ? getRecipeIdentificator(CMIRecipeType.Shaped, result, Recipe)
                      : customKey),
              result);
      LinkedHashMap<String, CMIRecipeIngredient> itemsused = new LinkedHashMap<>();
      StringBuilder ss = new StringBuilder();
      int y = 0;

      for (int i = 0; i < 9; i++) {
        CMIRecipeIngredient ritem = Recipe.get(i);
        if (ritem == null) {
          ss.append(" ");
        } else {
          int e = 1;
          ItemStack item = ritem.getItem().clone();
          String ident = item.toString();
          if (!itemsused.containsKey(ident)) {
            itemsused.put(ident, ritem.setItem(item));
            e = ++y;
          } else {
            e = getIndex(itemsused, ident) + 1;
          }

          ss.append(String.valueOf(e));
        }
      }

      NewShapedRecipe.shape(ss.toString().split("(?<=\\G...)"));
      char[] charId = new char[] {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
      int i = 0;

      for (Entry<String, CMIRecipeIngredient> one : itemsused.entrySet()) {
        char ch = charId[i];
        ItemStack item = one.getValue().getItem();
        if (item != null) {
          i++;
          NewShapedRecipe.setIngredient(ch, (RecipeChoice) one.getValue().generateChoice());
        }
      }

      return NewShapedRecipe;
    } else {
      ShapedRecipe NewShapedRecipe = null;
      if (Version.isCurrentHigher(Version.v1_11_R1)) {
        NewShapedRecipe =
            new ShapedRecipe(
                new NamespacedKey(
                    CMILib.getInstance(),
                    customKey == null
                        ? getRecipeIdentificator(CMIRecipeType.Shaped, result, Recipe)
                        : customKey),
                result);
      } else {
        NewShapedRecipe = new ShapedRecipe(result);
      }

      LinkedHashMap<String, CMIRecipeIngredient> itemsused = new LinkedHashMap<>();
      String ss = "";
      int y = 0;

      for (int i = 0; i < 9; i++) {
        CMIRecipeIngredient ritem = Recipe.get(i);
        if (ritem == null) {
          ss = ss + " ";
        } else {
          int e = 1;
          ItemStack item =
              new ItemStack(ritem.getItem().getType(), 1, ritem.getItem().getData().getData());
          if (!itemsused.containsKey(item.getType() + ":" + ritem.getItem().getData().getData())) {
            itemsused.put(
                item.getType() + ":" + ritem.getItem().getData().getData(), ritem.setItem(item));
            e = ++y;
          } else {
            e = getIndex(itemsused, item.getType() + ":" + ritem.getItem().getData().getData()) + 1;
          }

          ss = ss + String.valueOf(e);
        }
      }

      NewShapedRecipe.shape(ss.split("(?<=\\G...)"));
      char[] charId = new char[] {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
      int i = 0;

      for (Entry<String, CMIRecipeIngredient> one : itemsused.entrySet()) {
        char ch = charId[i];
        ItemStack item = one.getValue().getItem();
        if (item != null) {
          i++;
          NewShapedRecipe.setIngredient(ch, item.getData());
        }
      }

      return NewShapedRecipe;
    }
  }

  public static int getIndex(LinkedHashMap<String, CMIRecipeIngredient> itemsused, Object value) {
    int result = 0;

    for (Entry<String, CMIRecipeIngredient> entry : itemsused.entrySet()) {
      if (entry.getKey().equals(value)) {
        return result;
      }

      result++;
    }

    return -1;
  }

  public static Recipe makeShaplessRecipe(
      ItemStack result, HashMap<Integer, CMIRecipeIngredient> recipe) {
    return makeShaplessRecipe(result, recipe, null, null);
  }

  public static Recipe makeShaplessRecipe(
      ItemStack result, HashMap<Integer, CMIRecipeIngredient> recipe, String customKey) {
    return makeShaplessRecipe(result, recipe, null, customKey);
  }

  public static Recipe makeShaplessRecipe(
      ItemStack result, HashMap<Integer, CMIRecipeIngredient> recipe, CMINamespacedKey key) {
    return makeShaplessRecipe(result, recipe, key, null);
  }

  public static Recipe makeShaplessRecipe(
      ItemStack result,
      HashMap<Integer, CMIRecipeIngredient> recipe,
      CMINamespacedKey key,
      String customKey) {
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      ShapelessRecipe NewShapelessRecipe =
          new ShapelessRecipe(
              key == null
                  ? new NamespacedKey(
                      CMILib.getInstance(),
                      customKey == null
                          ? getRecipeIdentificator(CMIRecipeType.Shapeless, result, recipe)
                          : customKey)
                  : new NamespacedKey(key.getNamespace(), key.getKey()),
              result);

      for (CMIRecipeIngredient item : recipe.values()) {
        if (item != null) {
          NewShapelessRecipe.addIngredient((RecipeChoice) item.generateChoice());
        }
      }

      return NewShapelessRecipe;
    } else {
      ShapelessRecipe NewShapelessRecipe = null;
      if (Version.isCurrentHigher(Version.v1_11_R1)) {
        NewShapelessRecipe =
            new ShapelessRecipe(
                key == null
                    ? new NamespacedKey(
                        CMILib.getInstance(),
                        customKey == null
                            ? getRecipeIdentificator(CMIRecipeType.Shapeless, result, recipe)
                            : customKey)
                    : new NamespacedKey(key.getNamespace(), key.getKey()),
                result);
      } else {
        NewShapelessRecipe = new ShapelessRecipe(result);
      }

      for (CMIRecipeIngredient item : recipe.values()) {
        if (item != null) {
          NewShapelessRecipe.addIngredient(1, item.getItem().getData());
        }
      }

      return NewShapelessRecipe;
    }
  }

  public static Recipe makeFurnaceRecipe(ItemStack result, CMIRecipeIngredient Recipe) {
    return makeFurnaceRecipe(result, Recipe, null, null);
  }

  public static Recipe makeFurnaceRecipe(
      ItemStack result, CMIRecipeIngredient Recipe, CMIRecipeCraftData temp) {
    return makeFurnaceRecipe(result, Recipe, temp, null);
  }

  public static Recipe makeFurnaceRecipe(
      ItemStack result, CMIRecipeIngredient Recipe, CMIRecipeCraftData temp, String customKey) {
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      float exp = 0.1F;
      int duration = 60;
      if (temp != null) {
        exp = temp.getExp();
        duration = temp.getCookingTime();
      }

      return new FurnaceRecipe(
          new NamespacedKey(
              CMILib.getInstance(),
              customKey == null
                  ? getRecipeIdentificator(CMIRecipeType.Furnace, result, Recipe)
                  : customKey),
          result,
          (RecipeChoice) Recipe.generateChoice(),
          exp,
          duration);
    } else {
      return new FurnaceRecipe(result, Recipe.getItem().getData());
    }
  }

  public static Recipe makeSmithingRecipe(
      ItemStack result, CMIRecipeIngredient ingredient, CMIRecipeIngredient secondIngredient) {
    return makeSmithingRecipe(result, ingredient, secondIngredient, null);
  }

  public static Recipe makeSmithingRecipe(
      ItemStack result,
      CMIRecipeIngredient ingredient,
      CMIRecipeIngredient secondIngredient,
      String customKey) {
    return Version.isCurrentEqualOrLower(Version.v1_13_R1)
        ? null
        : new SmithingRecipe(
            new NamespacedKey(
                CMILib.getInstance(),
                customKey == null
                    ? getRecipeIdentificator(
                        CMIRecipeType.Smithing, result, ingredient, secondIngredient)
                    : customKey),
            result,
            (RecipeChoice) ingredient.generateChoice(),
            (RecipeChoice) secondIngredient.generateChoice());
  }

  public static Recipe makeStonecuttingRecipe(ItemStack result, CMIRecipeIngredient ingredient) {
    return makeStonecuttingRecipe(result, ingredient, null);
  }

  public static Recipe makeStonecuttingRecipe(
      ItemStack result, CMIRecipeIngredient ingredient, String customKey) {
    return Version.isCurrentEqualOrLower(Version.v1_13_R1)
        ? null
        : new StonecuttingRecipe(
            new NamespacedKey(
                CMILib.getInstance(),
                customKey == null
                    ? getRecipeIdentificator(CMIRecipeType.Stonecutting, result, ingredient)
                    : customKey),
            result,
            (RecipeChoice) ingredient.generateChoice());
  }

  public static Recipe makeCampfireRecipe(
      ItemStack result, CMIRecipeIngredient ingredient, CMIRecipeCraftData temp) {
    return makeCampfireRecipe(result, ingredient, temp, null);
  }

  public static Recipe makeCampfireRecipe(
      ItemStack result, CMIRecipeIngredient ingredient, CMIRecipeCraftData temp, String customKey) {
    if (Version.isCurrentEqualOrLower(Version.v1_13_R1)) {
      return null;
    }

    float exp = 0.1F;
    int duration = 60;
    if (temp != null) {
      exp = temp.getExp();
      duration = temp.getCookingTime();
    }

    return new CampfireRecipe(
        new NamespacedKey(
            CMILib.getInstance(),
            customKey == null
                ? getRecipeIdentificator(CMIRecipeType.Campfire, result, ingredient)
                : customKey),
        result,
        (RecipeChoice) ingredient.generateChoice(),
        exp,
        duration);
  }

  public static Recipe makeSmokingRecipe(
      ItemStack result, CMIRecipeIngredient ingredient, CMIRecipeCraftData temp) {
    return makeSmokingRecipe(result, ingredient, temp, null);
  }

  public static Recipe makeSmokingRecipe(
      ItemStack result, CMIRecipeIngredient ingredient, CMIRecipeCraftData temp, String customKey) {
    if (Version.isCurrentEqualOrLower(Version.v1_13_R1)) {
      return null;
    }

    float exp = 0.1F;
    int duration = 60;
    if (temp != null) {
      exp = temp.getExp();
      duration = temp.getCookingTime();
    }

    return new SmokingRecipe(
        new NamespacedKey(
            CMILib.getInstance(),
            customKey == null
                ? getRecipeIdentificator(CMIRecipeType.Smoking, result, ingredient)
                : customKey),
        result,
        (RecipeChoice) ingredient.generateChoice(),
        exp,
        duration);
  }

  public static Recipe makeTransmuteRecipe(
      ItemStack result, CMIRecipeIngredient ingredient, String customKey) {
    return Version.isCurrentEqualOrLower(Version.v1_21_R1)
        ? null
        : new TransmuteRecipe(
            new NamespacedKey(
                CMILib.getInstance(),
                customKey == null
                    ? getRecipeIdentificator(CMIRecipeType.Smoking, result, ingredient)
                    : customKey),
            result.getType(),
            (RecipeChoice) ingredient.generateChoice(),
            (RecipeChoice) ingredient.generateChoice());
  }

  public static Recipe makeBlastingRecipe(
      ItemStack result, CMIRecipeIngredient ingredient, CMIRecipeCraftData temp) {
    return makeBlastingRecipe(result, ingredient, temp, null);
  }

  public static Recipe makeBlastingRecipe(
      ItemStack result, CMIRecipeIngredient ingredient, CMIRecipeCraftData temp, String customKey) {
    if (Version.isCurrentEqualOrLower(Version.v1_13_R1)) {
      return null;
    }

    float exp = 0.1F;
    int duration = 60;
    if (temp != null) {
      exp = temp.getExp();
      duration = temp.getCookingTime();
    }

    return new BlastingRecipe(
        new NamespacedKey(
            CMILib.getInstance(),
            customKey == null
                ? getRecipeIdentificator(CMIRecipeType.Blasting, result, ingredient)
                : customKey),
        result,
        (RecipeChoice) ingredient.generateChoice(),
        exp,
        duration);
  }

  public static Recipe createRecipe(
      CMIRecipeType type, ItemStack result, HashMap<Integer, CMIRecipeIngredient> Recipe) {
    return createRecipe(type, result, Recipe, null);
  }

  public static Recipe createRecipe(
      CMIRecipeType type,
      ItemStack result,
      HashMap<Integer, CMIRecipeIngredient> Recipe,
      CMIRecipeCraftData temp) {
    return createRecipe(type, result, Recipe, temp, null);
  }

  public static Recipe createRecipe(
      CMIRecipeType type,
      ItemStack result,
      HashMap<Integer, CMIRecipeIngredient> Recipe,
      CMIRecipeCraftData temp,
      String customKey) {
    Recipe recipe = null;
    switch (type) {
      case Shaped:
        recipe = makeShapedRecipe(result, Recipe, customKey);
        break;
      case Shapeless:
        recipe = makeShaplessRecipe(result, Recipe, customKey);
        break;
      case Furnace:
        recipe = makeFurnaceRecipe(result, Recipe.get(0), temp, customKey);
      case Transmute:
      default:
        break;
      case Smithing:
        recipe = makeSmithingRecipe(result, Recipe.get(0), Recipe.get(1), customKey);
        break;
      case Stonecutting:
        recipe = makeStonecuttingRecipe(result, Recipe.get(0), customKey);
        break;
      case Campfire:
        recipe = makeCampfireRecipe(result, Recipe.get(0), temp, customKey);
        break;
      case Smoking:
        recipe = makeSmokingRecipe(result, Recipe.get(0), temp, customKey);
        break;
      case Blasting:
        recipe = makeBlastingRecipe(result, Recipe.get(0), temp, customKey);
    }

    return recipe;
  }

  public static List<ItemStack> getIngredientsList(Recipe recipe) {
    List<ItemStack> list = new ArrayList<>();

    for (CMIRecipeIngredient one : getIngredientsMap(recipe).values()) {
      if (one.getItem() != null) {
        list.add(one.getItem());
      }
    }

    return list;
  }

  public HashMap<Integer, CMIRecipeIngredient> getIngredients() {
    return getIngredientsMap(this.recipe);
  }

  public static HashMap<Integer, CMIRecipeIngredient> getIngredientsMap(Recipe recipe) {
    HashMap<Integer, CMIRecipeIngredient> map = new HashMap<>();
    if (recipe == null) {
      return map;
    }

    if (recipe instanceof ShapelessRecipe) {
      ShapelessRecipe rc = (ShapelessRecipe) recipe;

      for (int i = 0; i < rc.getIngredientList().size(); i++) {
        CMIRecipeIngredient CMIri =
            new CMIRecipeIngredient((ItemStack) rc.getIngredientList().get(i));
        if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
          setChoice((RecipeChoice) rc.getChoiceList().get(i), CMIri);
        }

        map.put(i, CMIri);
      }
    } else if (recipe instanceof ShapedRecipe) {
      ShapedRecipe rc = (ShapedRecipe) recipe;
      String[] shape = rc.getShape();
      int i = -1;

      for (int x = 0; x < 3 && shape.length > x; x++) {
        String line = shape[x];

        for (int z = 0; z < 3; z++) {
          i++;
          if (line.length() > z) {
            char oneS = line.charAt(z);
            ItemStack item = (ItemStack) rc.getIngredientMap().get(oneS);
            if (item != null) {
              CMIRecipeIngredient shapedIngredient = new CMIRecipeIngredient(item);
              if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
                setChoice((RecipeChoice) rc.getChoiceMap().get(oneS), shapedIngredient);
              }

              map.put(i, shapedIngredient);
            }
          }
        }
      }
    } else if (recipe instanceof FurnaceRecipe) {
      FurnaceRecipe rc = (FurnaceRecipe) recipe;
      CMIRecipeIngredient furnaceIngredient = new CMIRecipeIngredient(rc.getInput());
      if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
        setChoice(rc.getInputChoice(), furnaceIngredient);
      }

      map.put(0, furnaceIngredient);
    } else {
      CMIRecipeType recipeType = CMIRecipeType.getByName(recipe.getClass().getSimpleName());
      switch (recipeType) {
        case Transmute:
          TransmuteRecipe tc = (TransmuteRecipe) recipe;
          CMIRecipeIngredient base = getRecipeChoice(tc.getMaterial());
          CMIRecipeIngredient ingredient = getRecipeChoice(tc.getInput());
          if (base == null || ingredient == null) {
            return map;
          }

          map.put(1, base);
          map.put(2, ingredient);
          break;
        case Smithing:
          SmithingRecipe smtr = (SmithingRecipe) recipe;
          CMIRecipeIngredient smithingBaseIngredient =
              new CMIRecipeIngredient(smtr.getBase().getItemStack());
          if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
            setChoice(smtr.getBase(), smithingBaseIngredient);
          }

          map.put(0, smithingBaseIngredient);
          CMIRecipeIngredient smithingAdditionIngredient =
              new CMIRecipeIngredient(smtr.getAddition().getItemStack());
          if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
            setChoice(smtr.getAddition(), smithingAdditionIngredient);
          }

          map.put(1, smithingAdditionIngredient);
          break;
        case Stonecutting:
          StonecuttingRecipe str = (StonecuttingRecipe) recipe;
          CMIRecipeIngredient stonecutterIngredient = new CMIRecipeIngredient(str.getInput());
          if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
            setChoice(str.getInputChoice(), stonecutterIngredient);
          }

          map.put(0, stonecutterIngredient);
          break;
        case Campfire:
          CampfireRecipe cfr = (CampfireRecipe) recipe;
          CMIRecipeIngredient campfireIngredient = new CMIRecipeIngredient(cfr.getInput());
          if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
            setChoice(cfr.getInputChoice(), campfireIngredient);
          }

          map.put(0, campfireIngredient);
          break;
        case Smoking:
          SmokingRecipe smr = (SmokingRecipe) recipe;
          CMIRecipeIngredient smokerIngredient = new CMIRecipeIngredient(smr.getInput());
          if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
            setChoice(smr.getInputChoice(), smokerIngredient);
          }

          map.put(0, smokerIngredient);
          break;
        case Blasting:
          BlastingRecipe brc = (BlastingRecipe) recipe;
          CMIRecipeIngredient blastingIngredient = new CMIRecipeIngredient(brc.getInput());
          if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
            setChoice(brc.getInputChoice(), blastingIngredient);
          }

          map.put(0, blastingIngredient);
          break;
        case Cooking:
          CookingRecipe cor = (CookingRecipe) recipe;
          CMIRecipeIngredient cookingIngredient = new CMIRecipeIngredient(cor.getInput());
          if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
            setChoice(cor.getInputChoice(), cookingIngredient);
          }

          map.put(0, cookingIngredient);
          break;
        case Merchant:
          MerchantRecipe mer = (MerchantRecipe) recipe;

          for (int i = 0; i < mer.getIngredients().size(); i++) {
            CMIRecipeIngredient merchantIngredient =
                new CMIRecipeIngredient((ItemStack) mer.getIngredients().get(i));
            map.put(i, merchantIngredient);
          }
      }
    }

    return map;
  }

  private static void setChoice(RecipeChoice choice, CMIRecipeIngredient CMIri) {
    try {
      if (choice instanceof ExactChoice) {
        CMIri.setChoice(CMIRecipeChoice.byItemStack);
      } else {
        CMIri.setChoice(CMIRecipeChoice.byMaterial);
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  public static String getRecipeIdentificator(CMIRecipeType type, Recipe recipe) {
    return getRecipeIdentificator(type, recipe.getResult(), getIngredientsMap(recipe));
  }

  private static String getRecipeIdentificator(
      CMIRecipeType type,
      ItemStack result,
      CMIRecipeIngredient ingredient,
      CMIRecipeIngredient secondIngredient) {
    HashMap<Integer, CMIRecipeIngredient> temp = new HashMap<>();
    temp.put(1, ingredient);
    temp.put(2, secondIngredient);
    return getRecipeIdentificator(type, result, temp);
  }

  private static String getRecipeIdentificator(
      CMIRecipeType type, ItemStack result, CMIRecipeIngredient ingredient) {
    HashMap<Integer, CMIRecipeIngredient> temp = new HashMap<>();
    temp.put(1, ingredient);
    return getRecipeIdentificator(type, result, temp);
  }

  private static String getRecipeIdentificator(
      CMIRecipeType type, ItemStack result, HashMap<Integer, CMIRecipeIngredient> Recipe) {
    StringBuilder str = new StringBuilder();
    str.append(type.toString());
    str.append(result.getType() + ":" + result.getAmount());

    for (Entry<Integer, CMIRecipeIngredient> one : Recipe.entrySet()) {
      if (one.getValue().getItem() != null) {
        if (one.getValue().getChoice().equals(CMIRecipeChoice.byItemStack)) {
          if (one.getValue().getItem().hasItemMeta()) {
            ItemMeta meta = one.getValue().getItem().getItemMeta();
            if (meta.hasDisplayName()) {
              str.append(":" + meta.getDisplayName());
            }

            if (meta.hasLore()) {
              str.append(CMIList.listToString(meta.getLore()));
            }
          }

          Integer value = CMIItemStack.getCustomModelData(one.getValue().getItem());
          if (value != null) {
            str.append(":" + value);
          }
        }

        str.append(
            one.getValue().getItem().getType()
                + ":"
                + one.getValue().getItem().getAmount()
                + ":"
                + (one.getValue().getChoice().equals(CMIRecipeChoice.byItemStack) ? "1:" : "")
                + one.getKey());
      }
    }

    return UUID.nameUUIDFromBytes(str.toString().getBytes()).toString();
  }

  public static Recipe getRecipe(CMINamespacedKey key) {
    if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
      return Bukkit.getServer().getRecipe(new NamespacedKey(key.getNamespace(), key.getKey()));
    }

    Iterator<Recipe> iter = Bukkit.getServer().recipeIterator();

    while (iter.hasNext()) {
      Recipe recipe = iter.next();
      CMINamespacedKey subKey = CMINamespacedKey.getKey(recipe);
      if (subKey != null && subKey.toString().equals(key.toString())) {
        return recipe;
      }
    }

    return null;
  }

  public CMIRecipeCraftData getData() {
    return this.data;
  }

  public CMIRecipe setData(CMIRecipeCraftData data) {
    this.data = data;
    return this;
  }

  public String getCustomKey() {
    return this.customKey;
  }

  public CMIRecipe setCustomKey(String customKey) {
    this.customKey = customKey;
    return this;
  }

  public boolean isDiscoverable() {
    return this.discoverable;
  }

  public void setDiscoverable(boolean discoverable) {
    this.discoverable = discoverable;
  }
}
