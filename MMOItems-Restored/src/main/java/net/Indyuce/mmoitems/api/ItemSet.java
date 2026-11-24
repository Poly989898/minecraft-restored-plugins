package net.Indyuce.mmoitems.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import net.Indyuce.mmoitems.stat.data.ParticleData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class ItemSet {
   private final Map<Integer, ItemSet.SetBonuses> bonuses = new HashMap<>();
   private final List<String> loreTag;
   private final String name;
   private final String id;
   private static final int itemLimit = 10;

   public ItemSet(ConfigurationSection var1) {
      this.id = var1.getName().toUpperCase().replace("-", "_");
      this.loreTag = var1.getStringList("lore-tag");
      this.name = var1.getString("name");
      Validate.isTrue(var1.isConfigurationSection("bonuses"), "Could not find item set bonuses");

      for (int var2 = 2; var2 <= 10; var2++) {
         if (var1.getConfigurationSection("bonuses").contains(String.valueOf(var2))) {
            String var3 = String.format("bonuses.%d", var2);
            ItemSet.SetBonuses var4 = new ItemSet.SetBonuses();
            ConfigurationSection var5 = var1.getConfigurationSection(var3);
            Validate.notNull(var5, String.format("Item set '%s' is not a valid configuration section.", this.id));

            for (String var7 : var5.getStringList("granted-permissions")) {
               var4.addPermission(var7);
            }

            for (String var12 : var5.getKeys(false)) {
               if (!var12.equals("granted-permissions")) {
                  try {
                     String var8 = var12.toUpperCase().replace("-", "_").replace(" ", "_");
                     if (var12.startsWith("ability-")) {
                        ConfigurationSection var9 = var1.getConfigurationSection(String.format("%s.%s", var3, var12));
                        Validate.notNull(var9, String.format("Ability '%s' is not a valid configuration section.", var12));
                        var4.addAbility(new AbilityData(var9));
                     } else if (var12.startsWith("potion-")) {
                        PotionEffectType var13 = PotionEffectType.getByName(var8.substring("potion-".length()));
                        Validate.notNull(var13, String.format("Could not load potion effect type from '%s'", var8));
                        var4.addPotionEffect(
                           new PotionEffect(var13, MMOUtils.getEffectDuration(var13), var1.getInt(String.format("%s.%s", var3, var12)) - 1, true, false)
                        );
                     } else if (var12.startsWith("particle-")) {
                        ConfigurationSection var14 = var1.getConfigurationSection(String.format("bonuses.%d.%s", var2, var12));
                        Validate.notNull(var14, String.format("Particle effect '%s' is not a valid configuration section.", var12));
                        var4.addParticle(new ParticleData(var14));
                     } else {
                        ItemStat var15 = MMOItems.plugin.getStats().get(var8);
                        Validate.notNull(var15, String.format("Could not find stat called '%s'", var8));
                        var4.addStat(var15, var1.getDouble(String.format("bonuses.%d.%s", var2, var12)));
                     }
                  } catch (IllegalArgumentException var10) {
                     throw new IllegalArgumentException(String.format("Could not load set bonus '%s': %s", var12, var10.getMessage()));
                  }
               }
            }

            this.bonuses.put(var2, var4);
         }
      }
   }

   public String getName() {
      return this.name;
   }

   public String getId() {
      return this.id;
   }

   @NotNull
   public ItemSet.SetBonuses getBonuses(int var1) {
      ItemSet.SetBonuses var2 = new ItemSet.SetBonuses();

      for (int var3 = 2; var3 <= Math.min(var1, 10); var3++) {
         ItemSet.SetBonuses var4 = this.bonuses.get(var3);
         if (var4 != null) {
            var2.merge(var4);
         }
      }

      return var2;
   }

   public List<String> getLoreTag() {
      return this.loreTag;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ItemSet var2 = (ItemSet)var1;
         return this.id.equals(var2.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }

   public static class SetBonuses {
      private final Map<ItemStat<?, ?>, Double> stats = new HashMap<>();
      private final Map<PotionEffectType, PotionEffect> permEffects = new HashMap<>();
      private final List<AbilityData> abilities = new ArrayList<>();
      private final List<ParticleData> particles = new ArrayList<>();
      private final ArrayList<String> permissions = new ArrayList<>();

      public void addStat(ItemStat<?, ?> var1, double var2) {
         this.stats.put(var1, var2);
      }

      public void addPotionEffect(PotionEffect var1) {
         this.permEffects.put(var1.getType(), var1);
      }

      public void addAbility(AbilityData var1) {
         this.abilities.add(var1);
      }

      public void addParticle(ParticleData var1) {
         this.particles.add(var1);
      }

      public void addPermission(@NotNull String var1) {
         this.permissions.add(var1);
      }

      public boolean hasStat(ItemStat<?, ?> var1) {
         return this.stats.containsKey(var1);
      }

      public double getStat(ItemStat<?, ?> var1) {
         return this.stats.get(var1);
      }

      public Map<ItemStat<?, ?>, Double> getStats() {
         return this.stats;
      }

      public Collection<PotionEffect> getPotionEffects() {
         return this.permEffects.values();
      }

      public List<ParticleData> getParticles() {
         return this.particles;
      }

      public List<AbilityData> getAbilities() {
         return this.abilities;
      }

      @NotNull
      public ArrayList<String> getPermissions() {
         return this.permissions;
      }

      public void merge(ItemSet.SetBonuses var1) {
         var1.getStats().forEach((var1x, var2) -> this.stats.put((ItemStat<?, ?>)var1x, (this.stats.containsKey(var1x) ? this.stats.get(var1x) : 0.0) + var2));

         for (PotionEffect var3 : var1.getPotionEffects()) {
            if (!this.permEffects.containsKey(var3.getType()) || this.permEffects.get(var3.getType()).getAmplifier() < var3.getAmplifier()) {
               this.permEffects.put(var3.getType(), var3);
            }
         }

         this.abilities.addAll(var1.abilities);
         this.particles.addAll(var1.particles);
         this.permissions.addAll(var1.permissions);
      }
   }
}
