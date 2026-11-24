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

   public ItemSet(ConfigurationSection section) {
      this.id = section.getName().toUpperCase().replace("-", "_");
      this.loreTag = section.getStringList("lore-tag");
      this.name = section.getString("name");
      Validate.isTrue(section.isConfigurationSection("bonuses"), "Could not find item set bonuses");

      for (int i = 2; i <= 10; i++) {
         if (section.getConfigurationSection("bonuses").contains(String.valueOf(i))) {
            String text = String.format("bonuses.%d", i);
            ItemSet.SetBonuses setBonuses = new ItemSet.SetBonuses();
            ConfigurationSection section2 = section.getConfigurationSection(text);
            Validate.notNull(section2, String.format("Item set '%s' is not a valid configuration section.", this.id));

            for (String text2 : section2.getStringList("granted-permissions")) {
               setBonuses.addPermission(text2);
            }

            for (String key : section2.getKeys(false)) {
               if (!key.equals("granted-permissions")) {
                  try {
                     String text2 = key.toUpperCase().replace("-", "_").replace(" ", "_");
                     if (key.startsWith("ability-")) {
                        ConfigurationSection section3 = section.getConfigurationSection(String.format("%s.%s", text, key));
                        Validate.notNull(section3, String.format("Ability '%s' is not a valid configuration section.", key));
                        setBonuses.addAbility(new AbilityData(section3));
                     } else if (key.startsWith("potion-")) {
                        PotionEffectType potionEffectType = PotionEffectType.getByName(text2.substring("potion-".length()));
                        Validate.notNull(potionEffectType, String.format("Could not load potion effect type from '%s'", text2));
                        setBonuses.addPotionEffect(
                           new PotionEffect(potionEffectType, MMOUtils.getEffectDuration(potionEffectType), section.getInt(String.format("%s.%s", text, key)) - 1, true, false)
                        );
                     } else if (key.startsWith("particle-")) {
                        ConfigurationSection section3 = section.getConfigurationSection(String.format("bonuses.%d.%s", i, key));
                        Validate.notNull(section3, String.format("Particle effect '%s' is not a valid configuration section.", key));
                        setBonuses.addParticle(new ParticleData(section3));
                     } else {
                        ItemStat itemStat = MMOItems.plugin.getStats().get(text2);
                        Validate.notNull(itemStat, String.format("Could not find stat called '%s'", text2));
                        setBonuses.addStat(itemStat, section.getDouble(String.format("bonuses.%d.%s", i, key)));
                     }
                  } catch (IllegalArgumentException exception) {
                     throw new IllegalArgumentException(String.format("Could not load set bonus '%s': %s", key, exception.getMessage()));
                  }
               }
            }

            this.bonuses.put(i, setBonuses);
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
   public ItemSet.SetBonuses getBonuses(int count) {
      ItemSet.SetBonuses setBonuses = new ItemSet.SetBonuses();

      for (int i = 2; i <= Math.min(count, 10); i++) {
         ItemSet.SetBonuses setBonuses2 = this.bonuses.get(i);
         if (setBonuses2 != null) {
            setBonuses.merge(setBonuses2);
         }
      }

      return setBonuses;
   }

   public List<String> getLoreTag() {
      return this.loreTag;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         ItemSet itemSet = (ItemSet)obj;
         return this.id.equals(itemSet.id);
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

      public void addStat(ItemStat<?, ?> itemStat, double value) {
         this.stats.put(itemStat, value);
      }

      public void addPotionEffect(PotionEffect potionEffect) {
         this.permEffects.put(potionEffect.getType(), potionEffect);
      }

      public void addAbility(AbilityData abilityData) {
         this.abilities.add(abilityData);
      }

      public void addParticle(ParticleData particleData) {
         this.particles.add(particleData);
      }

      public void addPermission(@NotNull String text) {
         this.permissions.add(text);
      }

      public boolean hasStat(ItemStat<?, ?> itemStat) {
         return this.stats.containsKey(itemStat);
      }

      public double getStat(ItemStat<?, ?> itemStat) {
         return this.stats.get(itemStat);
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

      public void merge(ItemSet.SetBonuses setBonuses) {
         setBonuses.getStats().forEach((var1x, value) -> this.stats.put((ItemStat<?, ?>)var1x, (this.stats.containsKey(var1x) ? this.stats.get(var1x) : 0.0) + value));

         for (PotionEffect potionEffect : setBonuses.getPotionEffects()) {
            if (!this.permEffects.containsKey(potionEffect.getType()) || this.permEffects.get(potionEffect.getType()).getAmplifier() < potionEffect.getAmplifier()) {
               this.permEffects.put(potionEffect.getType(), potionEffect);
            }
         }

         this.abilities.addAll(setBonuses.abilities);
         this.particles.addAll(setBonuses.particles);
         this.permissions.addAll(setBonuses.permissions);
      }
   }
}
