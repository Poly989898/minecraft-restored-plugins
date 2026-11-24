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
         var0 -> true,
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
      ConfigFile var1 = new ConfigFile("/language", "crafting-stations");

      for (LoadedCraftingObject var3 : this.getConditions()) {
         String var4 = "condition." + var3.getId();
         if (!var1.getConfig().contains(var4)) {
            var1.getConfig().createSection(var4);
            var3.getDisplay().setup(var1.getConfig().getConfigurationSection(var4));
         }

         var3.setDisplay(new ConditionalDisplay(var1.getConfig().getConfigurationSection(var4)));
      }

      for (IngredientType var12 : this.getIngredients()) {
         String var15 = "ingredient." + var12.getId();
         if (!var1.getConfig().contains(var15)) {
            var1.getConfig().createSection(var15);
            var12.getDisplay().setup(var1.getConfig().getConfigurationSection(var15));
         }

         var12.setDisplay(new ConditionalDisplay(var1.getConfig().getConfigurationSection(var15)));
      }

      var1.save();

      for (File var5 : new File(MMOItems.plugin.getDataFolder() + "/crafting-stations").listFiles()) {
         try {
            CraftingStation var6 = new CraftingStation(var5.getName().substring(0, var5.getName().length() - 4), YamlConfiguration.loadConfiguration(var5));
            this.stations.put(var6.getId(), var6);
         } catch (IllegalArgumentException | NullPointerException var8) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not load station '" + var5.getName() + "': " + var8.getMessage());
         }
      }

      for (CraftingStation var14 : this.stations.values()) {
         try {
            var14.getPostLoadAction().performAction();
         } catch (IllegalArgumentException var7) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not post-load station '" + var14.getId() + "': " + var7.getMessage());
         }
      }
   }

   public int countRecipes() {
      int var1 = 0;

      for (CraftingStation var3 : this.stations.values()) {
         var1 += var3.getRecipes().size();
      }

      return var1;
   }

   public boolean hasStation(String var1) {
      return this.stations.containsKey(var1);
   }

   public Collection<CraftingStation> getStations() {
      return this.stations.values();
   }

   public CraftingStation getStation(String var1) {
      return this.stations.get(var1);
   }

   @NotNull
   public Ingredient getIngredient(MMOLineConfig var1) {
      String var2 = var1.getKey();

      for (IngredientType var4 : this.ingredients) {
         if (var4.getId().equals(var2)) {
            return var4.load(var1);
         }
      }

      throw new IllegalArgumentException("Could not match ingredient");
   }

   @NotNull
   public Condition getCondition(MMOLineConfig var1) {
      return this.getConditionInfo(var1.getKey()).load(var1);
   }

   @NotNull
   public LoadedCraftingObject<Condition> getConditionInfo(String var1) {
      return Objects.requireNonNull(this.conditions.get(var1), "Could not match condition");
   }

   @NotNull
   public Trigger getTrigger(MMOLineConfig var1) {
      return this.getTriggerInfo(var1.getKey()).load(var1);
   }

   @NotNull
   public LoadedCraftingObject<Trigger> getTriggerInfo(String var1) {
      return Objects.requireNonNull(this.triggers.get(var1), "Could not match trigger");
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
      String var1, Function<MMOLineConfig, Ingredient> var2, ConditionalDisplay var3, Predicate<NBTItem> var4, Function<NBTItem, PlayerIngredient> var5
   ) {
      this.ingredients.add(0, new IngredientType(var1, var2, var3, var4, var5));
   }

   public void registerCondition(String var1, Function<MMOLineConfig, Condition> var2, @NotNull ConditionalDisplay var3) {
      LoadedCraftingObject var4 = new LoadedCraftingObject(var1, var2, Objects.requireNonNull(var3, "Conditional display cannot be null"));
      this.conditions.put(var4.getId(), var4);
   }

   public void registerTrigger(String var1, Function<MMOLineConfig, Trigger> var2) {
      LoadedCraftingObject var3 = new LoadedCraftingObject(var1, var2, null);
      this.triggers.put(var3.getId(), var3);
   }

   public Collection<CraftingStation> getAll() {
      return this.stations.values();
   }
}
