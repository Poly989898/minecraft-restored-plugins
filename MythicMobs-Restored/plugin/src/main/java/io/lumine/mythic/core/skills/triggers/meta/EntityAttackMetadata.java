package io.lumine.mythic.core.skills.triggers.meta;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@Getter
@Setter
@ToString
public class EntityAttackMetadata extends SkillTriggerMetadata implements CancellableTriggerMetadata {
   private final EntityDamageByEntityEvent event;
   private final AbstractEntity damager;
   private final AbstractEntity damaged;
   private double damage;
   private double damageModifier = 1.0;
   private double damageCompound = 1.0;
   private Map<String, Double> bonusDamage;
   private Map<String, Double> bonusDamageModifiers;
   private String damageCause;
   private String element;
   private Function<Double, Double> finalizer;

   public EntityAttackMetadata(EntityDamageByEntityEvent event, DamageMetadata damageMetadata, AbstractEntity damager, AbstractEntity damaged) {
      super(damageMetadata);
      this.event = event;
      this.damager = damager;
      this.damaged = damaged;
      if (damageMetadata == null) {
         this.damage = event.getDamage();
         this.bonusDamage = Maps.newHashMap();
         this.damageCause = event.getCause().toString();
         this.element = "PHYSICAL";
      } else {
         this.damage = damageMetadata.getAmount();
         this.bonusDamage = damageMetadata.getBonusDamage();
         this.bonusDamageModifiers = damageMetadata.getBonusDamageModifiers();
         this.damageCause = "SKILL";
         this.element = damageMetadata.getElement();
         this.damageModifier = damageMetadata.getMultiplier();
      }
   }

   public double getFinalDamage() {
      double damage;
      if (this.bonusDamage.isEmpty()) {
         damage = this.damage;
      } else if (this.bonusDamageModifiers != null && !this.bonusDamageModifiers.isEmpty()) {
         damage = this.damage * this.bonusDamageModifiers.getOrDefault("PHYSICAL", 1.0);

         for (Entry<String, Double> bonusEntry : this.bonusDamage.entrySet()) {
            String bonusType = bonusEntry.getKey();
            damage += bonusEntry.getValue() * this.bonusDamageModifiers.getOrDefault(bonusType, 1.0);
         }
      } else {
         double bonus = 0.0;

         for (Double d : this.bonusDamage.values()) {
            bonus += d;
         }

         damage = this.damage + bonus;
      }

      double finalDamage = damage * this.damageModifier * this.damageCompound;
      return this.finalizer == null ? finalDamage : this.finalizer.apply(finalDamage);
   }

   public String getDamageType() {
      return this.element;
   }

   @Override
   public boolean isCancelled() {
      return this.event.isCancelled();
   }

   @Override
   public void applyToSkillMetadata(SkillMetadata skillMetadata) {
      AbstractEntity damaged = BukkitAdapter.adapt(this.event.getEntity());
      Optional<Object> maybeData = damaged.getMetadata("skill-damage");
      double preDamage = this.getFinalDamage();
      skillMetadata.getVariables().putDouble("damage-amount", preDamage);
      skillMetadata.getVariables().putDouble("damage-amount-post", this.resolvePostDamage(preDamage));
      if (maybeData.isPresent()) {
         DamageMetadata data = (DamageMetadata)maybeData.get();
         String damageTags = String.join(",", data.getTags());
         skillMetadata.getVariables().putObject("damage-metadata", data);
         skillMetadata.getVariables().putString("damage-cause", "SKILL");
         skillMetadata.getVariables().putString("real-damage-cause", this.event.getCause().toString());
         skillMetadata.getVariables().putString("damage-type", data.getElement() != null ? data.getElement() : (damageTags.isEmpty() ? "SKILL" : damageTags));
         skillMetadata.getVariables().putString("damage-tags", damageTags);
      } else {
         skillMetadata.getVariables().putString("damage-cause", this.event.getCause().toString());
      }
   }

   private double resolvePostDamage(double preDamage) {
      if (preDamage <= 0.0) {
         return preDamage;
      } else {
         double originalDamage = this.event.getDamage();

         double value;
         try {
            this.event.setDamage(preDamage);
            return this.event.getFinalDamage();
         } catch (IllegalArgumentException e) {
            value = preDamage;
         } finally {
            this.event.setDamage(originalDamage);
         }

         return value;
      }
   }

   public double getDamageMultiplier(String type, double def) {
      if (this.bonusDamageModifiers == null) {
         this.bonusDamageModifiers = Maps.newTreeMap();
      }

      return this.bonusDamageModifiers.getOrDefault(type, def);
   }

   public void setDamageMultiplier(String type, double amount) {
      if (this.bonusDamageModifiers == null) {
         this.bonusDamageModifiers = Maps.newTreeMap();
      }

      this.bonusDamageModifiers.put(type, amount);
   }

   public void addCompoundMultiplier(double multiplier) {
      this.damageCompound *= multiplier;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof EntityAttackMetadata other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (Double.compare(this.getDamage(), other.getDamage()) != 0) {
         return false;
      } else if (Double.compare(this.getDamageModifier(), other.getDamageModifier()) != 0) {
         return false;
      } else if (Double.compare(this.getDamageCompound(), other.getDamageCompound()) != 0) {
         return false;
      } else {
         Object this$event = this.getEvent();
         Object other$event = other.getEvent();
         if (this$event == null ? other$event == null : this$event.equals(other$event)) {
            Object this$damager = this.getDamager();
            Object other$damager = other.getDamager();
            if (this$damager == null ? other$damager == null : this$damager.equals(other$damager)) {
               Object this$damaged = this.getDamaged();
               Object other$damaged = other.getDamaged();
               if (this$damaged == null ? other$damaged == null : this$damaged.equals(other$damaged)) {
                  Object this$bonusDamage = this.getBonusDamage();
                  Object other$bonusDamage = other.getBonusDamage();
                  if (this$bonusDamage == null ? other$bonusDamage == null : this$bonusDamage.equals(other$bonusDamage)) {
                     Object this$bonusDamageModifiers = this.getBonusDamageModifiers();
                     Object other$bonusDamageModifiers = other.getBonusDamageModifiers();
                     if (this$bonusDamageModifiers == null ? other$bonusDamageModifiers == null : this$bonusDamageModifiers.equals(other$bonusDamageModifiers)) {
                        Object this$damageCause = this.getDamageCause();
                        Object other$damageCause = other.getDamageCause();
                        if (this$damageCause == null ? other$damageCause == null : this$damageCause.equals(other$damageCause)) {
                           Object this$element = this.getElement();
                           Object other$element = other.getElement();
                           if (this$element == null ? other$element == null : this$element.equals(other$element)) {
                              Object this$finalizer = this.getFinalizer();
                              Object other$finalizer = other.getFinalizer();
                              return this$finalizer == null ? other$finalizer == null : this$finalizer.equals(other$finalizer);
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof EntityAttackMetadata;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      long $damage = Double.doubleToLongBits(this.getDamage());
      result = result * 59 + (int)($damage >>> 32 ^ $damage);
      long $damageModifier = Double.doubleToLongBits(this.getDamageModifier());
      result = result * 59 + (int)($damageModifier >>> 32 ^ $damageModifier);
      long $damageCompound = Double.doubleToLongBits(this.getDamageCompound());
      result = result * 59 + (int)($damageCompound >>> 32 ^ $damageCompound);
      Object $event = this.getEvent();
      result = result * 59 + ($event == null ? 43 : $event.hashCode());
      Object $damager = this.getDamager();
      result = result * 59 + ($damager == null ? 43 : $damager.hashCode());
      Object $damaged = this.getDamaged();
      result = result * 59 + ($damaged == null ? 43 : $damaged.hashCode());
      Object $bonusDamage = this.getBonusDamage();
      result = result * 59 + ($bonusDamage == null ? 43 : $bonusDamage.hashCode());
      Object $bonusDamageModifiers = this.getBonusDamageModifiers();
      result = result * 59 + ($bonusDamageModifiers == null ? 43 : $bonusDamageModifiers.hashCode());
      Object $damageCause = this.getDamageCause();
      result = result * 59 + ($damageCause == null ? 43 : $damageCause.hashCode());
      Object $element = this.getElement();
      result = result * 59 + ($element == null ? 43 : $element.hashCode());
      Object $finalizer = this.getFinalizer();
      return result * 59 + ($finalizer == null ? 43 : $finalizer.hashCode());
   }
}
