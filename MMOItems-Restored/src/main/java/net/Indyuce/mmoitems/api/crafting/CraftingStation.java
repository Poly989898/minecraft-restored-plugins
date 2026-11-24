package net.Indyuce.mmoitems.api.crafting;

import io.lumine.mythic.lib.util.PostLoadAction;
import io.lumine.mythic.lib.util.PreloadedObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.IngredientInventory;
import net.Indyuce.mmoitems.api.crafting.recipe.CheckedRecipe;
import net.Indyuce.mmoitems.api.crafting.recipe.CraftingRecipe;
import net.Indyuce.mmoitems.api.crafting.recipe.Recipe;
import net.Indyuce.mmoitems.api.crafting.recipe.UpgradingRecipe;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.apache.commons.lang.Validate;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftingStation implements PreloadedObject {
   private final String id;
   private final String name;
   private final Layout layout;
   private final Sound sound;
   private final StationItemOptions itemOptions;
   private final int maxQueueSize;
   private final Map<String, Recipe> recipes = new LinkedHashMap<>();
   private CraftingStation parent;
   private final PostLoadAction postLoadAction = new PostLoadAction(var1x -> {
      if (var1x.contains("parent")) {
         String var2x = var1x.getString("parent").toLowerCase().replace(" ", "-").replace("_", "-");
         Validate.isTrue(!var2x.equals(this.getId()), "Station cannot use itself as parent");
         Validate.isTrue(MMOItems.plugin.getCrafting().hasStation(var2x), "Could not find parent station with ID '" + var2x + "'");
         this.parent = MMOItems.plugin.getCrafting().getStation(var2x);
      }
   });

   public CraftingStation(String var1, FileConfiguration var2) {
      this.postLoadAction.cacheConfig(var2);
      this.id = var1.toLowerCase().replace("_", "-").replace(" ", "-");
      this.name = var2.getString("name", "A Station With No Name");
      this.layout = MMOItems.plugin.getLayouts().getLayout(var2.getString("layout", "default"));
      this.sound = Sound.valueOf(var2.getString("sound", "ENTITY_EXPERIENCE_ORB_PICKUP").toUpperCase());

      for (String var4 : var2.getConfigurationSection("recipes").getKeys(false)) {
         try {
            this.registerRecipe(this.loadRecipe(var2.getConfigurationSection("recipes." + var4)));
         } catch (IllegalArgumentException var6) {
            MMOItems.plugin
               .getLogger()
               .log(Level.SEVERE, "An issue occurred registering recipe '" + var4 + "' from crafting station '" + var1 + "': " + var6.getMessage());
         }
      }

      this.itemOptions = new StationItemOptions(var2.getConfigurationSection("items"));
      this.maxQueueSize = Math.max(1, Math.min(var2.getInt("max-queue-size"), 64));
   }

   public CraftingStation(String var1, String var2, Layout var3, Sound var4, StationItemOptions var5, int var6, CraftingStation var7) {
      Validate.notNull(var1, "Crafting station ID must not be null");
      Validate.notNull(var2, "Crafting station name must not be null");
      Validate.notNull(var4, "Crafting station sound must not be null");
      this.id = var1.toLowerCase().replace("_", "-").replace(" ", "-");
      this.name = var2;
      this.layout = var3;
      this.sound = var4;
      this.itemOptions = var5;
      this.maxQueueSize = var6;
      this.parent = var7;
   }

   @NotNull
   public PostLoadAction getPostLoadAction() {
      return this.postLoadAction;
   }

   public String getId() {
      return this.id;
   }

   @Deprecated
   public String getName() {
      return this.name;
   }

   public Layout getLayout() {
      return this.layout;
   }

   public Sound getSound() {
      return this.sound;
   }

   @Nullable
   public CraftingStation getParent() {
      return this.parent;
   }

   public Collection<Recipe> getRecipes() {
      if (this.parent == null) {
         return this.recipes.values();
      }

      ArrayList var1 = new ArrayList<>(this.recipes.values());

      for (CraftingStation var2 = this.parent; var2 != null; var2 = var2.parent) {
         var1.addAll(var2.recipes.values());
      }

      return var1;
   }

   public boolean hasRecipe(String var1) {
      return this.recipes.containsKey(var1) || this.parent != null && this.parent.hasRecipe(var1);
   }

   public Recipe getRecipe(String var1) {
      Recipe var2 = this.recipes.get(var1);
      return var2 == null && this.parent != null ? this.parent.getRecipe(var1) : var2;
   }

   public int getMaxQueueSize() {
      return this.maxQueueSize;
   }

   public List<CheckedRecipe> getAvailableRecipes(PlayerData var1, IngredientInventory var2) {
      ArrayList var3 = new ArrayList();

      for (Recipe var5 : this.getRecipes()) {
         CheckedRecipe var6 = var5.evaluateRecipe(var1, var2);
         if ((var6.areConditionsMet() || !var6.getRecipe().hasOption(Recipe.RecipeOption.HIDE_WHEN_LOCKED))
            && (var6.allIngredientsHad() || !var6.getRecipe().hasOption(Recipe.RecipeOption.HIDE_WHEN_NO_INGREDIENTS))) {
            var3.add(var6);
         }
      }

      return var3;
   }

   public StationItemOptions getItemOptions() {
      return this.itemOptions;
   }

   public void registerRecipe(Recipe var1) {
      this.recipes.put(var1.getId(), var1);
   }

   public int getMaxPage() {
      int var1 = this.getRecipes().size();
      return Math.max(1, (int)Math.ceil((double)var1 / this.getLayout().getRecipeSlots().size()));
   }

   private Recipe loadRecipe(ConfigurationSection var1) {
      return var1.contains("output") ? new CraftingRecipe(var1) : new UpgradingRecipe(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         CraftingStation var2 = (CraftingStation)var1;
         return Objects.equals(this.id, var2.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }
}
