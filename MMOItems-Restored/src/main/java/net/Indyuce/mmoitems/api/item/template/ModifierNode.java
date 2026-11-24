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

   private void loadChildrenAndStats(ConfigurationSection var1x) {
      ConfigurationSection var2x = var1x.getConfigurationSection("modifiers");
      if (var2x != null) {
         for (String var4x : var2x.getKeys(false)) {
            try {
               ModifierNode var5 = fromConfig(var4x, var1x.get("modifiers." + var4x));
               var5.getPostLoadAction().performAction();
               this.children.add(var5);
            } catch (RuntimeException var9) {
               MMOItems.plugin
                  .getLogger()
                  .log(Level.SEVERE, "Could not load parent modifier node '" + var4x + "' of modifier group '" + this.getId() + "': " + var9.getMessage());
            }
         }
      }

      Validate.notNull(this.data, "Internal error");
      ConfigurationSection var10 = var1x.getConfigurationSection("stats");
      if (var10 != null) {
         for (String var12 : var10.getKeys(false)) {
            try {
               String var6x = UtilityMethods.enumName(var12);
               ItemStat var7x = MMOItems.plugin.getStats().get(var6x);
               Validate.notNull(var7x, "Could not find stat with ID '" + var6x + "'");
               this.data.put(var7x, var7x.whenInitialized(var10.get(var12)));
            } catch (IllegalArgumentException var8x) {
               MMOItems.plugin
                  .getLogger()
                  .log(Level.SEVERE, "An error occurred while trying to load modifier node " + this.getId() + ": " + var8x.getMessage());
            }
         }
      }
   }

   public ModifierNode(@NotNull String var1, @NotNull Object var2) {
      this.id = var1;
      if (var2 instanceof Number) {
         this.referenceNode = findReferenceNode(this.id);
         this.chance = this.referenceNode.chance;
         this.weight = ((Number)var2).doubleValue();
         this.nameModifier = this.referenceNode.nameModifier;
      } else if (var2 instanceof String) {
         this.referenceNode = findReferenceNode(var2.toString());
         this.chance = this.referenceNode.chance;
         this.weight = this.referenceNode.weight;
         this.nameModifier = this.referenceNode.nameModifier;
      } else {
         if (!(var2 instanceof ConfigurationSection)) {
            throw new IllegalArgumentException("Must be either a string, number config section");
         }

         ConfigurationSection var3 = (ConfigurationSection)var2;
         ModifierNode var4 = null;

         try {
            var4 = findReferenceNode(this.id);
         } catch (Throwable var6) {
         }

         this.referenceNode = var4;
         this.chance = var3.getDouble("chance", var4 != null ? var4.getChance() : 1.0);
         this.weight = var3.getDouble("weight", var4 != null ? var4.getWeight() : 0.0);
         this.nameModifier = var3.contains("suffix")
            ? new NameModifier(NameModifier.ModifierType.SUFFIX, var3.get("suffix"))
            : (var3.contains("prefix") ? new NameModifier(NameModifier.ModifierType.PREFIX, var3.get("prefix")) : (var4 != null ? var4.nameModifier : null));
         Validate.isTrue(this.chance > 0.0, "Chance must be strictly positive");
      }

      if (this.referenceNode != null) {
         this.children = this.referenceNode.children;
         ConfigurationSection var7 = var2 instanceof ConfigurationSection ? (ConfigurationSection)var2 : null;
         this.min = var7 != null && var7.contains("min") ? var7.getInt("min") : this.referenceNode.min;
         this.max = var7 != null && var7.contains("max") ? var7.getInt("max") : this.referenceNode.max;
         this.data = this.referenceNode.data;
      } else {
         Validate.isTrue(var2 instanceof ConfigurationSection, "Must provide a config section when not using a reference node");
         ConfigurationSection var8 = (ConfigurationSection)var2;
         this.min = var8.getInt("min");
         this.max = var8.getInt("max", -1);
         this.children = new ArrayList<>();
         this.postLoadAction.cacheConfig(var8);
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

   public boolean collect(@NotNull MMOItemBuilder var1) {
      if (!this.rollChance()) {
         return false;
      }

      if (this.weight > var1.getCapacity()) {
         return false;
      }

      UUID var2 = UUID.randomUUID();
      var1.reduceCapacity(this.weight);
      if (this.hasNameModifier()) {
         var1.addModifier(this.getNameModifier(), var2);
      }

      this.whenCollected(var1, var2);
      return true;
   }

   public void whenCollected(@NotNull MMOItemBuilder var1, @NotNull UUID var2) {
      this.data.forEach((var2x, var3x) -> var1.addModifierData(var2x, var3x.randomize(var1), var2));
      ArrayList var3 = new ArrayList<>(this.children);
      if (var1.getTemplate().hasOption(MMOItemTemplate.TemplateOption.ROLL_MODIFIER_CHECK_ORDER)) {
         Collections.shuffle(var3);
      }

      int var4 = this.max <= 0 ? var3.size() : Math.min(this.max, var3.size());
      int var5 = 0;

      while (this.children.size() - var3.size() < var4 && var5 < var3.size()) {
         ModifierNode var6 = (ModifierNode)var3.get(var5);
         if (var6.collect(var1)) {
            var3.remove(var5);
         } else {
            var5++;
         }
      }

      while (this.children.size() - var3.size() < this.min) {
         ModifierNode var7 = (ModifierNode)var3.remove(this.rollModifier(var3));
         var7.collect(var1);
      }
   }

   @NotNull
   public static ModifierNode fromConfig(@NotNull String var0, @NotNull Object var1) {
      if (var1 instanceof Number || var1 instanceof String) {
         Validate.notNull(findReferenceNode(var0), "Could not find reference node called '" + var0 + "'");
      }

      return new ModifierNode(var0, var1);
   }

   @NotNull
   private static ModifierNode findReferenceNode(@NotNull String var0) {
      ModifierNode var1 = MMOItems.plugin.getTemplates().getModifierNode(var0);
      Validate.notNull(var1, "Could not find public modifier with ID '" + var0 + "'");
      return var1;
   }

   @NotNull
   private int rollModifier(@NotNull List<ModifierNode> var1) {
      double[] var2 = new double[var1.size()];
      double var3 = 0.0;

      for (int var5 = 0; var5 < var1.size(); var5++) {
         var3 += ((ModifierNode)var1.get(var5)).getChance();
         var2[var5] = var3;
      }

      double var8 = RANDOM.nextDouble() * var3;

      for (int var7 = 0; var7 < var1.size(); var7++) {
         if (var8 <= var2[var7]) {
            return var7;
         }
      }

      throw new IllegalArgumentException("Could not roll new modifier from group '" + this.getId() + "'");
   }
}
