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
      for (Field field : StatCategory.class.getFields()) {
         try {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && field.get(null) instanceof StatCategory) {
               this.registerCategory((StatCategory)field.get(null));
            }
         } catch (IllegalArgumentException | IllegalAccessException exception) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, String.format("Couldn't register category called '%s': %s", field.getName(), exception.getMessage()));
         }
      }

      for (Field field : ItemStats.class.getFields()) {
         try {
            if (Modifier.isStatic(field.getModifiers())
               && Modifier.isFinal(field.getModifiers())
               && field.get(null) instanceof ItemStat
               && field.getAnnotation(DeprecatedStat.class) == null) {
               this.register((ItemStat<?, ?>)field.get(null));
            }
         } catch (IllegalArgumentException | IllegalAccessException exception) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, String.format("Couldn't register stat called '%s': %s", field.getName(), exception.getMessage()));
         }
      }

      this.loadCustomStats();
   }

   public void reload(boolean flag) {
      if (flag) {
         this.numericStats.removeIf(doubleStat -> doubleStat instanceof FakeElementalStat);
      }

      this.loadElements();
      FileConfiguration config = new ConfigFile("/language", "stats").getConfig();

      for (ItemStat itemStat : this.getAll()) {
         try {
            Object obj = config.get(itemStat.getPath());
            if (obj == null) {
               obj = config.get(itemStat.getLegacyTranslationPath());
            }

            itemStat.loadConfiguration(config, obj != null ? obj : "<TranslationNotFound:" + itemStat.getPath() + ">");
         } catch (RuntimeException exception) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not load translation info for stat '" + itemStat.getId() + "': " + exception.getMessage());
         }
      }
   }

   private void loadCustomStats() {
      ConfigManager.DefaultFile.CUSTOM_STATS.checkFile();
      ConfigFile configFile = new ConfigFile("custom-stats");
      ConfigurationSection customStats = configFile.getConfig().getConfigurationSection("custom-stats");
      Validate.notNull(customStats, "Custom stats section is null");
      customStats.getKeys(true)
         .stream()
         .filter(customStats::isConfigurationSection)
         .<ConfigurationSection>map(customStats::getConfigurationSection)
         .filter(Objects::nonNull)
         .forEach(this::registerCustomStat);
   }

   public void loadElements() {
      for (ElementStatType elementStatType : ElementStatType.values()) {
         for (Element element : MythicLib.plugin.getElements().getAll()) {
            this.numericStats.add(new FakeElementalStat(element, elementStatType));
         }
      }
   }

   public void registerCategory(@NotNull StatCategory statCategory) {
      this.categories.put(statCategory.getId(), statCategory);
   }

   @NotNull
   public StatCategory getCategory(@NotNull String text) {
      return Objects.requireNonNull(this.categories.get(text), "No stat category found with ID '" + text + "'");
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

   public boolean has(String text) {
      return this.stats.containsKey(text);
   }

   @Nullable
   public ItemStat<?, ?> get(String text) {
      ItemStat itemStat = this.stats.get(text);
      if (itemStat != null) {
         return itemStat;
      }

      itemStat = this.numericStats.stream().filter(var1x -> var1x.getId().equals(text)).findFirst().orElse(null);
      if (itemStat != null) {
         return itemStat;
      }

      itemStat = this.legacyAliases.get(text);
      return itemStat != null ? itemStat : null;
   }

   public void register(@NotNull ItemStat<?, ?> itemStat) {
      if (itemStat.isEnabled()) {
         this.stats.compute(itemStat.getId(), (var1x, itemStat2) -> {
            Validate.isTrue(itemStat2 == null, "A stat with ID '" + var1x + "' already exists");
            return itemStat;
         });

         for (String aliase : itemStat.getAliases()) {
            this.legacyAliases.put(aliase, itemStat);
         }

         if (itemStat instanceof DoubleStat && !(itemStat instanceof GemStoneStat) && itemStat.isCompatible(Type.GEM_STONE)) {
            this.numericStats.add((DoubleStat)itemStat);
         }

         if (itemStat instanceof ItemRestriction) {
            this.itemRestrictions.add((ItemRestriction)itemStat);
         }

         if (itemStat instanceof ConsumableItemInteraction) {
            this.consumableActions.add((ConsumableItemInteraction)itemStat);
         }

         if (itemStat instanceof PlayerConsumable) {
            this.playerConsumables.add((PlayerConsumable)itemStat);
         }

         HasCategory annotation = itemStat.getClass().getAnnotation(HasCategory.class);
         if (annotation != null) {
            itemStat.setCategory(this.getCategory(UtilityMethods.enumName(annotation.cat())));
         }

         if (MMOItems.plugin.getTypes() != null) {
            MMOItems.plugin.getTypes().getAll().stream().filter(itemStat::isCompatible).forEach(var1x -> var1x.getAvailableStats().add(itemStat));
         }
      }
   }

   private void registerCustomStat(@NotNull ConfigurationSection section) {
      String name = section.getString("name");
      String type = section.getString("type");
      Validate.notNull(section, "Cannot register a custom stat from a null section");
      Validate.notNull(name, "Cannot register a custom stat without a name");
      Validate.notNull(type, "Cannot register a custom stat without a type");
      Class<? extends ItemStat> type2;
      switch (type.toLowerCase()) {
         case "double":
            type2 = DoubleStat.class;
            break;
         case "boolean":
            type2 = BooleanStat.class;
            break;
         case "text":
            type2 = StringStat.class;
            break;
         case "text-list":
            type2 = StringListStat.class;
            break;
         default:
            throw new RuntimeException("Cannot register a custom stat of type " + type);
      }

      String text = String.format("custom_%s", name.replace(" ", "_")).toUpperCase();
      String[] texts = new String[0];
      if (section.isList("lore")) {
         texts = section.getStringList("lore").toArray(new String[0]);
      } else if (section.isString("lore")) {
         texts = new String[]{section.getString("lore")};
      }

      try {
         ItemStat instance = type2.getConstructor(String.class, Material.class, String.class, String[].class, String[].class, Material[].class)
            .newInstance(text, Material.PAPER, name, texts, new String[]{"!miscellaneous", "!block", "all"}, new Material[0]);
         this.register(instance);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
         throw new RuntimeException("Unable to create a custom stat of type " + type, exception);
      }
   }

   @Deprecated
   public void register(@Nullable String text, @NotNull ItemStat<?, ?> itemStat) {
      this.register(itemStat);
   }

   @Deprecated
   public void loadInternalStats() {
      this.loadBuiltins();
   }
}
