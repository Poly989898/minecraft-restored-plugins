package net.Indyuce.mmoitems.manager;

import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ConfigFile;
import net.Indyuce.mmoitems.api.crafting.ConditionalDisplay;
import net.Indyuce.mmoitems.api.crafting.CraftingStation;
import net.Indyuce.mmoitems.api.crafting.LoadedCraftingObject;
import net.Indyuce.mmoitems.api.crafting.condition.ClassCondition;
import net.Indyuce.mmoitems.api.crafting.condition.Condition;
import net.Indyuce.mmoitems.api.crafting.condition.FoodCondition;
import net.Indyuce.mmoitems.api.crafting.condition.LevelCondition;
import net.Indyuce.mmoitems.api.crafting.condition.ManaCondition;
import net.Indyuce.mmoitems.api.crafting.condition.PermissionCondition;
import net.Indyuce.mmoitems.api.crafting.condition.PlaceholderCondition;
import net.Indyuce.mmoitems.api.crafting.condition.StaminaCondition;
import net.Indyuce.mmoitems.api.crafting.ingredient.Ingredient;
import net.Indyuce.mmoitems.api.crafting.ingredient.IngredientType;
import net.Indyuce.mmoitems.api.crafting.ingredient.MMOItemIngredient;
import net.Indyuce.mmoitems.api.crafting.ingredient.VanillaIngredient;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.MMOItemPlayerIngredient;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.PlayerIngredient;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.VanillaPlayerIngredient;
import net.Indyuce.mmoitems.api.crafting.trigger.CommandTrigger;
import net.Indyuce.mmoitems.api.crafting.trigger.MMOItemTrigger;
import net.Indyuce.mmoitems.api.crafting.trigger.MessageTrigger;
import net.Indyuce.mmoitems.api.crafting.trigger.SoundTrigger;
import net.Indyuce.mmoitems.api.crafting.trigger.Trigger;
import net.Indyuce.mmoitems.api.crafting.trigger.VanillaTrigger;
import net.Indyuce.mmoitems.comp.mythicmobs.crafting.MythicMobsSkillTrigger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class CraftingManager implements Reloadable {
   private final List<IngredientType> ingredients = new ArrayList<>();
   private final Map<String, LoadedCraftingObject<Condition>> conditions = new HashMap<>();
   private final Map<String, LoadedCraftingObject<Trigger>> triggers = new HashMap<>();
   private final Map<String, CraftingStation> stations = new HashMap<>();

   public CraftingManager() {
      this.registerCondition("level", LevelCondition::new, new ConditionalDisplay("&a✔ Requires Level #level#", "&c✖ Requires Level #level#"));
      this.registerCondition("permission", PermissionCondition::new, new ConditionalDisplay("&a✔ #display#", "&c✖ #display#"));
      this.registerCondition("placeholder", PlaceholderCondition::new, new ConditionalDisplay("&a✔ #display#", "&c✖ #display#"));
      this.registerCondition("mana", ManaCondition::new, new ConditionalDisplay("&a✔ Requires #mana# Mana", "&c✖ Requires #mana# Mana"));
      this.registerCondition("stamina", StaminaCondition::new, new ConditionalDisplay("&a✔ Requires #stamina# Stamina", "&c✖ Requires #stamina# Stamina"));
      this.registerCondition("food", FoodCondition::new, new ConditionalDisplay("&a✔ Requires #food# Food", "&c✖ Requires #food# Food"));
      this.registerCondition("class", ClassCondition::new, new ConditionalDisplay("&a✔ Required Class: #class#", "&c✖ Required Class: #class#"));
      this.registerTrigger("command", CommandTrigger::new);
      this.registerTrigger("message", MessageTrigger::new);
      this.registerTrigger("sound", SoundTrigger::new);
      this.registerTrigger("vanilla", VanillaTrigger::new);
      this.registerTrigger("mmoitem", MMOItemTrigger::new);
      this.registerIngredient(
         "vanilla",
         VanillaIngredient::new,
         new ConditionalDisplay("&8✔ &7#amount# #item#", "&c✖ &7#amount# #item#"),
         nbtItem -> true,
         VanillaPlayerIngredient::new
      );
      this.registerIngredient(
         "mmoitem",
         MMOItemIngredient::new,
         new ConditionalDisplay("&8✔ &7#amount# #level##item#", "&c✖ &7#amount# #level##item#"),
         NBTItem::hasType,
         MMOItemPlayerIngredient::new
      );
      if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
         this.registerTrigger("mmskill", MythicMobsSkillTrigger::new);
      }
   }

   @Override
   public void reload() {
      this.stations.clear();
      ConfigFile configFile = new ConfigFile("/language", "crafting-stations");

      for (LoadedCraftingObject condition : this.getConditions()) {
         String text = "condition." + condition.getId();
         if (!configFile.getConfig().contains(text)) {
            configFile.getConfig().createSection(text);
            condition.getDisplay().setup(configFile.getConfig().getConfigurationSection(text));
         }

         condition.setDisplay(new ConditionalDisplay(configFile.getConfig().getConfigurationSection(text)));
      }

      for (IngredientType ingredient : this.getIngredients()) {
         String text = "ingredient." + ingredient.getId();
         if (!configFile.getConfig().contains(text)) {
            configFile.getConfig().createSection(text);
            ingredient.getDisplay().setup(configFile.getConfig().getConfigurationSection(text));
         }

         ingredient.setDisplay(new ConditionalDisplay(configFile.getConfig().getConfigurationSection(text)));
      }

      configFile.save();

      for (File listFile : new File(MMOItems.plugin.getDataFolder() + "/crafting-stations").listFiles()) {
         try {
            CraftingStation craftingStation = new CraftingStation(listFile.getName().substring(0, listFile.getName().length() - 4), YamlConfiguration.loadConfiguration(listFile));
            this.stations.put(craftingStation.getId(), craftingStation);
         } catch (IllegalArgumentException | NullPointerException exception) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not load station '" + listFile.getName() + "': " + exception.getMessage());
         }
      }

      for (CraftingStation craftingStation : this.stations.values()) {
         try {
            craftingStation.getPostLoadAction().performAction();
         } catch (IllegalArgumentException exception) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not post-load station '" + craftingStation.getId() + "': " + exception.getMessage());
         }
      }
   }

   public int countRecipes() {
      int count = 0;

      for (CraftingStation craftingStation : this.stations.values()) {
         count += craftingStation.getRecipes().size();
      }

      return count;
   }

   public boolean hasStation(String text) {
      return this.stations.containsKey(text);
   }

   public Collection<CraftingStation> getStations() {
      return this.stations.values();
   }

   public CraftingStation getStation(String text) {
      return this.stations.get(text);
   }

   @NotNull
   public Ingredient getIngredient(MMOLineConfig mmoLineConfig) {
      String key = mmoLineConfig.getKey();

      for (IngredientType ingredient : this.ingredients) {
         if (ingredient.getId().equals(key)) {
            return ingredient.load(mmoLineConfig);
         }
      }

      throw new IllegalArgumentException("Could not match ingredient");
   }

   @NotNull
   public Condition getCondition(MMOLineConfig mmoLineConfig) {
      return this.getConditionInfo(mmoLineConfig.getKey()).load(mmoLineConfig);
   }

   @NotNull
   public LoadedCraftingObject<Condition> getConditionInfo(String text) {
      return Objects.requireNonNull(this.conditions.get(text), "Could not match condition");
   }

   @NotNull
   public Trigger getTrigger(MMOLineConfig mmoLineConfig) {
      return this.getTriggerInfo(mmoLineConfig.getKey()).load(mmoLineConfig);
   }

   @NotNull
   public LoadedCraftingObject<Trigger> getTriggerInfo(String text) {
      return Objects.requireNonNull(this.triggers.get(text), "Could not match trigger");
   }

   public List<IngredientType> getIngredients() {
      return this.ingredients;
   }

   public Collection<LoadedCraftingObject<Condition>> getConditions() {
      return this.conditions.values();
   }

   public Collection<LoadedCraftingObject<Trigger>> getTriggers() {
      return this.triggers.values();
   }

   public void registerIngredient(
      String text, Function<MMOLineConfig, Ingredient> function, ConditionalDisplay conditionalDisplay, Predicate<NBTItem> predicate, Function<NBTItem, PlayerIngredient> function2
   ) {
      this.ingredients.add(0, new IngredientType(text, function, conditionalDisplay, predicate, function2));
   }

   public void registerCondition(String text, Function<MMOLineConfig, Condition> function, @NotNull ConditionalDisplay conditionalDisplay) {
      LoadedCraftingObject loadedCraftingObject = new LoadedCraftingObject(text, function, Objects.requireNonNull(conditionalDisplay, "Conditional display cannot be null"));
      this.conditions.put(loadedCraftingObject.getId(), loadedCraftingObject);
   }

   public void registerTrigger(String text, Function<MMOLineConfig, Trigger> function) {
      LoadedCraftingObject loadedCraftingObject = new LoadedCraftingObject(text, function, null);
      this.triggers.put(loadedCraftingObject.getId(), loadedCraftingObject);
   }

   public Collection<CraftingStation> getAll() {
      return this.stations.values();
   }
}
