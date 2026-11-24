package net.Indyuce.mmoitems.api.item.template;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.util.PostLoadAction;
import io.lumine.mythic.lib.util.PreloadedObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModifierNode implements PreloadedObject {
   private final String id;
   private final double chance;
   private final double weight;
   private final int min;
   private final int max;
   @Nullable
   private final NameModifier nameModifier;
   @Nullable
   private final List<ModifierNode> children;
   @Nullable
   private final Map<ItemStat, RandomStatData> data;
   private static final Random RANDOM = new Random();
   private final ModifierNode referenceNode;
   private final PostLoadAction postLoadAction = new PostLoadAction(this::loadChildrenAndStats);

   private void loadChildrenAndStats(ConfigurationSection config) {
      ConfigurationSection modifiers = config.getConfigurationSection("modifiers");
      if (modifiers != null) {
         for (String key : modifiers.getKeys(false)) {
            try {
               ModifierNode modifierNode = fromConfig(key, config.get("modifiers." + key));
               modifierNode.getPostLoadAction().performAction();
               this.children.add(modifierNode);
            } catch (RuntimeException exception) {
               MMOItems.plugin
                  .getLogger()
                  .log(Level.SEVERE, "Could not load parent modifier node '" + key + "' of modifier group '" + this.getId() + "': " + exception.getMessage());
            }
         }
      }

      Validate.notNull(this.data, "Internal error");
      ConfigurationSection stats = config.getConfigurationSection("stats");
      if (stats != null) {
         for (String key : stats.getKeys(false)) {
            try {
               String statId = UtilityMethods.enumName(key);
               ItemStat stat = MMOItems.plugin.getStats().get(statId);
               Validate.notNull(stat, "Could not find stat with ID '" + statId + "'");
               this.data.put(stat, stat.whenInitialized(stats.get(key)));
            } catch (IllegalArgumentException exception) {
               MMOItems.plugin
                  .getLogger()
                  .log(Level.SEVERE, "An error occurred while trying to load modifier node " + this.getId() + ": " + exception.getMessage());
            }
         }
      }
   }

   public ModifierNode(@NotNull String text, @NotNull Object obj) {
      this.id = text;
      if (obj instanceof Number) {
         this.referenceNode = findReferenceNode(this.id);
         this.chance = this.referenceNode.chance;
         this.weight = ((Number)obj).doubleValue();
         this.nameModifier = this.referenceNode.nameModifier;
      } else if (obj instanceof String) {
         this.referenceNode = findReferenceNode(obj.toString());
         this.chance = this.referenceNode.chance;
         this.weight = this.referenceNode.weight;
         this.nameModifier = this.referenceNode.nameModifier;
      } else {
         if (!(obj instanceof ConfigurationSection)) {
            throw new IllegalArgumentException("Must be either a string, number config section");
         }

         ConfigurationSection section = (ConfigurationSection)obj;
         ModifierNode modifierNode = null;

         try {
            modifierNode = findReferenceNode(this.id);
         } catch (Throwable exception) {
         }

         this.referenceNode = modifierNode;
         this.chance = section.getDouble("chance", modifierNode != null ? modifierNode.getChance() : 1.0);
         this.weight = section.getDouble("weight", modifierNode != null ? modifierNode.getWeight() : 0.0);
         this.nameModifier = section.contains("suffix")
            ? new NameModifier(NameModifier.ModifierType.SUFFIX, section.get("suffix"))
            : (section.contains("prefix") ? new NameModifier(NameModifier.ModifierType.PREFIX, section.get("prefix")) : (modifierNode != null ? modifierNode.nameModifier : null));
         Validate.isTrue(this.chance > 0.0, "Chance must be strictly positive");
      }

      if (this.referenceNode != null) {
         this.children = this.referenceNode.children;
         ConfigurationSection section = obj instanceof ConfigurationSection ? (ConfigurationSection)obj : null;
         this.min = section != null && section.contains("min") ? section.getInt("min") : this.referenceNode.min;
         this.max = section != null && section.contains("max") ? section.getInt("max") : this.referenceNode.max;
         this.data = this.referenceNode.data;
      } else {
         Validate.isTrue(obj instanceof ConfigurationSection, "Must provide a config section when not using a reference node");
         ConfigurationSection section = (ConfigurationSection)obj;
         this.min = section.getInt("min");
         this.max = section.getInt("max", -1);
         this.children = new ArrayList<>();
         this.postLoadAction.cacheConfig(section);
         this.data = new HashMap<>();
      }
   }

   @NotNull
   public String getId() {
      return this.id;
   }

   public double getWeight() {
      return this.weight;
   }

   public double getChance() {
      return this.chance;
   }

   @NotNull
   public Map<ItemStat, RandomStatData> getItemData() {
      return this.data;
   }

   @Nullable
   public ModifierNode getReferenceNode() {
      return this.referenceNode;
   }

   @Nullable
   public NameModifier getNameModifier() {
      return this.nameModifier;
   }

   public boolean hasNameModifier() {
      return this.nameModifier != null;
   }

   public boolean rollChance() {
      return RANDOM.nextDouble() < this.chance;
   }

   public PostLoadAction getPostLoadAction() {
      return this.postLoadAction;
   }

   public List<ModifierNode> getChildren() {
      return this.children;
   }

   public boolean collect(@NotNull MMOItemBuilder mmoItemBuilder) {
      if (!this.rollChance()) {
         return false;
      }

      if (this.weight > mmoItemBuilder.getCapacity()) {
         return false;
      }

      UUID uuid = UUID.randomUUID();
      mmoItemBuilder.reduceCapacity(this.weight);
      if (this.hasNameModifier()) {
         mmoItemBuilder.addModifier(this.getNameModifier(), uuid);
      }

      this.whenCollected(mmoItemBuilder, uuid);
      return true;
   }

   public void whenCollected(@NotNull MMOItemBuilder mmoItemBuilder, @NotNull UUID uuid) {
      this.data.forEach((stat, data) -> mmoItemBuilder.addModifierData(stat, data.randomize(mmoItemBuilder), uuid));
      ArrayList list = new ArrayList<>(this.children);
      if (mmoItemBuilder.getTemplate().hasOption(MMOItemTemplate.TemplateOption.ROLL_MODIFIER_CHECK_ORDER)) {
         Collections.shuffle(list);
      }

      int count = this.max <= 0 ? list.size() : Math.min(this.max, list.size());
      int index = 0;

      while (this.children.size() - list.size() < count && index < list.size()) {
         ModifierNode modifierNode = (ModifierNode)list.get(index);
         if (modifierNode.collect(mmoItemBuilder)) {
            list.remove(index);
         } else {
            index++;
         }
      }

      while (this.children.size() - list.size() < this.min) {
         ModifierNode modifierNode = (ModifierNode)list.remove(this.rollModifier(list));
         modifierNode.collect(mmoItemBuilder);
      }
   }

   @NotNull
   public static ModifierNode fromConfig(@NotNull String text, @NotNull Object obj) {
      if (obj instanceof Number || obj instanceof String) {
         Validate.notNull(findReferenceNode(text), "Could not find reference node called '" + text + "'");
      }

      return new ModifierNode(text, obj);
   }

   @NotNull
   private static ModifierNode findReferenceNode(@NotNull String text) {
      ModifierNode modifierNode = MMOItems.plugin.getTemplates().getModifierNode(text);
      Validate.notNull(modifierNode, "Could not find public modifier with ID '" + text + "'");
      return modifierNode;
   }

   @NotNull
   private int rollModifier(@NotNull List<ModifierNode> modifierNodes) {
      double[] values = new double[modifierNodes.size()];
      double value = 0.0;

      for (int i = 0; i < modifierNodes.size(); i++) {
         value += ((ModifierNode)modifierNodes.get(i)).getChance();
         values[i] = value;
      }

      double randomValue = RANDOM.nextDouble() * value;

      for (int i = 0; i < modifierNodes.size(); i++) {
         if (randomValue <= values[i]) {
            return i;
         }
      }

      throw new IllegalArgumentException("Could not roll new modifier from group '" + this.getId() + "'");
   }
}
