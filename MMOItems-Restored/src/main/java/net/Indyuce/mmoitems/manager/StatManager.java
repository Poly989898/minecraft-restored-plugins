package net.Indyuce.mmoitems.manager;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.element.Element;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ConfigFile;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.stat.annotation.DeprecatedStat;
import net.Indyuce.mmoitems.stat.annotation.HasCategory;
import net.Indyuce.mmoitems.stat.category.StatCategory;
import net.Indyuce.mmoitems.stat.type.BooleanStat;
import net.Indyuce.mmoitems.stat.type.ConsumableItemInteraction;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.FakeElementalStat;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.stat.type.ItemRestriction;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.PlayerConsumable;
import net.Indyuce.mmoitems.stat.type.StringListStat;
import net.Indyuce.mmoitems.stat.type.StringStat;
import net.Indyuce.mmoitems.util.ElementStatType;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StatManager {
   private final Map<String, ItemStat<?, ?>> stats = new LinkedHashMap<>();
   private final Map<String, StatCategory> categories = new HashMap<>();
   private final Map<String, ItemStat<?, ?>> legacyAliases = new HashMap<>();
   private final List<DoubleStat> numericStats = new ArrayList<>();
   private final List<ItemRestriction> itemRestrictions = new ArrayList<>();
   private final List<ConsumableItemInteraction> consumableActions = new ArrayList<>();
   private final List<PlayerConsumable> playerConsumables = new ArrayList<>();

   public void loadBuiltins() {
      for (Field var4 : StatCategory.class.getFields()) {
         try {
            if (Modifier.isStatic(var4.getModifiers()) && Modifier.isFinal(var4.getModifiers()) && var4.get(null) instanceof StatCategory) {
               this.registerCategory((StatCategory)var4.get(null));
            }
         } catch (IllegalArgumentException | IllegalAccessException var7) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, String.format("Couldn't register category called '%s': %s", var4.getName(), var7.getMessage()));
         }
      }

      for (Field var11 : ItemStats.class.getFields()) {
         try {
            if (Modifier.isStatic(var11.getModifiers())
               && Modifier.isFinal(var11.getModifiers())
               && var11.get(null) instanceof ItemStat
               && var11.getAnnotation(DeprecatedStat.class) == null) {
               this.register((ItemStat<?, ?>)var11.get(null));
            }
         } catch (IllegalArgumentException | IllegalAccessException var6) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, String.format("Couldn't register stat called '%s': %s", var11.getName(), var6.getMessage()));
         }
      }

      this.loadCustomStats();
   }

   public void reload(boolean var1) {
      if (var1) {
         this.numericStats.removeIf(var0 -> var0 instanceof FakeElementalStat);
      }

      this.loadElements();
      FileConfiguration var2 = new ConfigFile("/language", "stats").getConfig();

      for (ItemStat var4 : this.getAll()) {
         try {
            Object var5 = var2.get(var4.getPath());
            if (var5 == null) {
               var5 = var2.get(var4.getLegacyTranslationPath());
            }

            var4.loadConfiguration(var2, var5 != null ? var5 : "<TranslationNotFound:" + var4.getPath() + ">");
         } catch (RuntimeException var6) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not load translation info for stat '" + var4.getId() + "': " + var6.getMessage());
         }
      }
   }

   private void loadCustomStats() {
      ConfigManager.DefaultFile.CUSTOM_STATS.checkFile();
      ConfigFile var1 = new ConfigFile("custom-stats");
      ConfigurationSection var2 = var1.getConfig().getConfigurationSection("custom-stats");
      Validate.notNull(var2, "Custom stats section is null");
      var2.getKeys(true)
         .stream()
         .filter(var2::isConfigurationSection)
         .<ConfigurationSection>map(var2::getConfigurationSection)
         .filter(Objects::nonNull)
         .forEach(this::registerCustomStat);
   }

   public void loadElements() {
      for (ElementStatType var4 : ElementStatType.values()) {
         for (Element var6 : MythicLib.plugin.getElements().getAll()) {
            this.numericStats.add(new FakeElementalStat(var6, var4));
         }
      }
   }

   public void registerCategory(@NotNull StatCategory var1) {
      this.categories.put(var1.getId(), var1);
   }

   @NotNull
   public StatCategory getCategory(@NotNull String var1) {
      return Objects.requireNonNull(this.categories.get(var1), "No stat category found with ID '" + var1 + "'");
   }

   @NotNull
   public Collection<ItemStat<?, ?>> getAll() {
      return this.stats.values();
   }

   @NotNull
   public List<DoubleStat> getNumericStats() {
      return this.numericStats;
   }

   @NotNull
   public List<ItemRestriction> getItemRestrictionStats() {
      return this.itemRestrictions;
   }

   @NotNull
   public List<ConsumableItemInteraction> getConsumableActions() {
      return this.consumableActions;
   }

   @NotNull
   public List<PlayerConsumable> getPlayerConsumables() {
      return this.playerConsumables;
   }

   public boolean has(String var1) {
      return this.stats.containsKey(var1);
   }

   @Nullable
   public ItemStat<?, ?> get(String var1) {
      ItemStat var2 = this.stats.get(var1);
      if (var2 != null) {
         return var2;
      }

      var2 = this.numericStats.stream().filter(var1x -> var1x.getId().equals(var1)).findFirst().orElse(null);
      if (var2 != null) {
         return var2;
      }

      var2 = this.legacyAliases.get(var1);
      return var2 != null ? var2 : null;
   }

   public void register(@NotNull ItemStat<?, ?> var1) {
      if (var1.isEnabled()) {
         this.stats.compute(var1.getId(), (var1x, var2) -> {
            Validate.isTrue(var2 == null, "A stat with ID '" + var1x + "' already exists");
            return var1;
         });

         for (String var5 : var1.getAliases()) {
            this.legacyAliases.put(var5, var1);
         }

         if (var1 instanceof DoubleStat && !(var1 instanceof GemStoneStat) && var1.isCompatible(Type.GEM_STONE)) {
            this.numericStats.add((DoubleStat)var1);
         }

         if (var1 instanceof ItemRestriction) {
            this.itemRestrictions.add((ItemRestriction)var1);
         }

         if (var1 instanceof ConsumableItemInteraction) {
            this.consumableActions.add((ConsumableItemInteraction)var1);
         }

         if (var1 instanceof PlayerConsumable) {
            this.playerConsumables.add((PlayerConsumable)var1);
         }

         HasCategory var6 = var1.getClass().getAnnotation(HasCategory.class);
         if (var6 != null) {
            var1.setCategory(this.getCategory(UtilityMethods.enumName(var6.cat())));
         }

         if (MMOItems.plugin.getTypes() != null) {
            MMOItems.plugin.getTypes().getAll().stream().filter(var1::isCompatible).forEach(var1x -> var1x.getAvailableStats().add(var1));
         }
      }
   }

   private void registerCustomStat(@NotNull ConfigurationSection var1) {
      String var2 = var1.getString("name");
      String var3 = var1.getString("type");
      Validate.notNull(var1, "Cannot register a custom stat from a null section");
      Validate.notNull(var2, "Cannot register a custom stat without a name");
      Validate.notNull(var3, "Cannot register a custom stat without a type");
      Class<? extends ItemStat> var4;
      switch (var3.toLowerCase()) {
         case "double":
            var4 = DoubleStat.class;
            break;
         case "boolean":
            var4 = BooleanStat.class;
            break;
         case "text":
            var4 = StringStat.class;
            break;
         case "text-list":
            var4 = StringListStat.class;
            break;
         default:
            throw new RuntimeException("Cannot register a custom stat of type " + var3);
      }

      String var9 = String.format("custom_%s", var2.replace(" ", "_")).toUpperCase();
      String[] var10 = new String[0];
      if (var1.isList("lore")) {
         var10 = var1.getStringList("lore").toArray(new String[0]);
      } else if (var1.isString("lore")) {
         var10 = new String[]{var1.getString("lore")};
      }

      try {
         ItemStat var7 = var4.getConstructor(String.class, Material.class, String.class, String[].class, String[].class, Material[].class)
            .newInstance(var9, Material.PAPER, var2, var10, new String[]{"!miscellaneous", "!block", "all"}, new Material[0]);
         this.register(var7);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException var8) {
         throw new RuntimeException("Unable to create a custom stat of type " + var3, var8);
      }
   }

   @Deprecated
   public void register(@Nullable String var1, @NotNull ItemStat<?, ?> var2) {
      this.register(var2);
   }

   @Deprecated
   public void loadInternalStats() {
      this.loadBuiltins();
   }
}
