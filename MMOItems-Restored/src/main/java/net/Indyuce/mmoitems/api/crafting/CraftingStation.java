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

   public CraftingStation(String text, FileConfiguration config) {
      this.postLoadAction.cacheConfig(config);
      this.id = text.toLowerCase().replace("_", "-").replace(" ", "-");
      this.name = config.getString("name", "A Station With No Name");
      this.layout = MMOItems.plugin.getLayouts().getLayout(config.getString("layout", "default"));
      this.sound = Sound.valueOf(config.getString("sound", "ENTITY_EXPERIENCE_ORB_PICKUP").toUpperCase());

      for (String key : config.getConfigurationSection("recipes").getKeys(false)) {
         try {
            this.registerRecipe(this.loadRecipe(config.getConfigurationSection("recipes." + key)));
         } catch (IllegalArgumentException exception) {
            MMOItems.plugin
               .getLogger()
               .log(Level.SEVERE, "An issue occurred registering recipe '" + key + "' from crafting station '" + text + "': " + exception.getMessage());
         }
      }

      this.itemOptions = new StationItemOptions(config.getConfigurationSection("items"));
      this.maxQueueSize = Math.max(1, Math.min(config.getInt("max-queue-size"), 64));
   }

   public CraftingStation(String text, String text2, Layout layout, Sound sound, StationItemOptions stationItemOptions, int count, CraftingStation craftingStation) {
      Validate.notNull(text, "Crafting station ID must not be null");
      Validate.notNull(text2, "Crafting station name must not be null");
      Validate.notNull(sound, "Crafting station sound must not be null");
      this.id = text.toLowerCase().replace("_", "-").replace(" ", "-");
      this.name = text2;
      this.layout = layout;
      this.sound = sound;
      this.itemOptions = stationItemOptions;
      this.maxQueueSize = count;
      this.parent = craftingStation;
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

      ArrayList list = new ArrayList<>(this.recipes.values());

      for (CraftingStation parent = this.parent; parent != null; parent = parent.parent) {
         list.addAll(parent.recipes.values());
      }

      return list;
   }

   public boolean hasRecipe(String text) {
      return this.recipes.containsKey(text) || this.parent != null && this.parent.hasRecipe(text);
   }

   public Recipe getRecipe(String text) {
      Recipe recipe = this.recipes.get(text);
      return recipe == null && this.parent != null ? this.parent.getRecipe(text) : recipe;
   }

   public int getMaxQueueSize() {
      return this.maxQueueSize;
   }

   public List<CheckedRecipe> getAvailableRecipes(PlayerData playerData, IngredientInventory ingredientInventory) {
      ArrayList list = new ArrayList();

      for (Recipe recipe : this.getRecipes()) {
         CheckedRecipe checkedRecipe = recipe.evaluateRecipe(playerData, ingredientInventory);
         if ((checkedRecipe.areConditionsMet() || !checkedRecipe.getRecipe().hasOption(Recipe.RecipeOption.HIDE_WHEN_LOCKED))
            && (checkedRecipe.allIngredientsHad() || !checkedRecipe.getRecipe().hasOption(Recipe.RecipeOption.HIDE_WHEN_NO_INGREDIENTS))) {
            list.add(checkedRecipe);
         }
      }

      return list;
   }

   public StationItemOptions getItemOptions() {
      return this.itemOptions;
   }

   public void registerRecipe(Recipe recipe) {
      this.recipes.put(recipe.getId(), recipe);
   }

   public int getMaxPage() {
      int size = this.getRecipes().size();
      return Math.max(1, (int)Math.ceil((double)size / this.getLayout().getRecipeSlots().size()));
   }

   private Recipe loadRecipe(ConfigurationSection section) {
      return section.contains("output") ? new CraftingRecipe(section) : new UpgradingRecipe(section);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         CraftingStation craftingStation = (CraftingStation)obj;
         return Objects.equals(this.id, craftingStation.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }
}
