package net.Zrips.CMILib.Container;

import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Recipes.CMIRecipe;
import net.Zrips.CMILib.Recipes.CMIRecipeType;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ComplexRecipe;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.plugin.Plugin;

public class CMINamespacedKey {
  private String namespace;
  private String key;

  public CMINamespacedKey(NamespacedKey key) {
    this.namespace = key.getNamespace();
    this.key = key.getKey();
  }

  public CMINamespacedKey(Plugin plugin, String key) {
    this.namespace = plugin.getName().toLowerCase();
    this.key = key.toLowerCase();
  }

  public CMINamespacedKey(String plugin, String key) {
    this.namespace = plugin.toLowerCase();
    this.key = key.toLowerCase();
  }

  public String getNamespace() {
    return this.namespace;
  }

  public String getKey() {
    return this.key;
  }

  @Override
  public String toString() {
    return this.namespace + ":" + this.key;
  }

  public static CMINamespacedKey getKey(Recipe recipe) {
    if (Version.isCurrentEqualOrLower(Version.v1_11_R1)) {
      return new CMINamespacedKey(
          "bukkit",
          CMIRecipe.getRecipeIdentificator(
              CMIRecipeType.getByName(recipe.getClass().getSimpleName()), recipe));
    }

    CMIRecipeType type = CMIRecipeType.getByName(recipe.getClass().getSimpleName());
    switch (type) {
      case Shapeless:
        return new CMINamespacedKey(((ShapelessRecipe) recipe).getKey());
      case Shaped:
        return new CMINamespacedKey(((ShapedRecipe) recipe).getKey());
      case Blasting:
        return new CMINamespacedKey(((BlastingRecipe) recipe).getKey());
      case Campfire:
        return new CMINamespacedKey(((CampfireRecipe) recipe).getKey());
      case Cooking:
        return new CMINamespacedKey(((CookingRecipe) recipe).getKey());
      case Smoking:
        return new CMINamespacedKey(((SmokingRecipe) recipe).getKey());
      case Stonecutting:
        return new CMINamespacedKey(((StonecuttingRecipe) recipe).getKey());
      case Smithing:
        return new CMINamespacedKey(((SmithingRecipe) recipe).getKey());
      case Furnace:
        if (Version.isCurrentEqualOrLower(Version.v1_12_R1)) {
          return new CMINamespacedKey(
              new NamespacedKey(
                  CMILib.getInstance(),
                  CMIRecipe.getRecipeIdentificator(CMIRecipeType.Furnace, recipe)));
        }

        return new CMINamespacedKey(((FurnaceRecipe) recipe).getKey());
      case Transmute:
        if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
          return new CMINamespacedKey(
              new NamespacedKey(
                  CMILib.getInstance(),
                  CMIRecipe.getRecipeIdentificator(CMIRecipeType.Transmute, recipe)));
        }

        return null;
      case Complex:
        return new CMINamespacedKey(((ComplexRecipe) recipe).getKey());
      default:
        return null;
    }
  }

  public static CMINamespacedKey randomKey() {
    return new CMINamespacedKey("bukkit", UUID.randomUUID().toString());
  }
}
