package io.lumine.mythic.core.skills.triggers.meta;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import java.util.Map;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.event.entity.EntityDamageEvent;

@Getter
@Setter
@ToString
public class DamagedMetadata extends SkillTriggerMetadata implements CancellableTriggerMetadata {
   private final EntityDamageEvent event;
   private final AbstractEntity damaged;
   private final DamageMetadata damageMetadata;
   private double damage;
   private Map<String, Double> bonusDamage;
   private String damageCause;
   private String element;

   public DamagedMetadata(EntityDamageEvent event, AbstractEntity damaged) {
      this.event = event;
      this.damaged = damaged;
      this.damageMetadata = null;
      this.damage = event.getDamage();
      this.bonusDamage = Maps.newHashMap();
      this.damageCause = event.getCause().toString();
      this.element = this.damageCause;
   }

   public DamagedMetadata(EntityDamageEvent event, DamageMetadata damageMetadata, AbstractEntity damaged) {
      super(damageMetadata);
      this.event = event;
      this.damaged = damaged;
      this.damageMetadata = damageMetadata;
      this.damage = damageMetadata.getAmount();
      this.bonusDamage = damageMetadata.getBonusDamage();
      this.damageCause = "SKILL";
      this.element = damageMetadata.getElement();
   }

   public double getFinalDamage() {
      return this.damage;
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
      skillMetadata.getVariables().putDouble("damage-amount", this.damage);
      skillMetadata.getVariables().putDouble("damage-amount-post", this.resolvePostDamage(this.damage));
      if (this.damageMetadata == null) {
         skillMetadata.getVariables().putString("damage-cause", this.event.getCause().toString());
         skillMetadata.getVariables().putString("damage-type", this.element);
      } else {
         String damageTags = String.join(",", this.damageMetadata.getTags());
         skillMetadata.getVariables().putObject("damage-metadata", this.damageMetadata);
         skillMetadata.getVariables().putString("damage-cause", "SKILL");
         skillMetadata.getVariables().putString("real-damage-cause", this.event.getCause().toString());
         skillMetadata.getVariables()
            .putString(
               "damage-type", this.damageMetadata.getElement() != null ? this.damageMetadata.getElement() : (damageTags.isEmpty() ? "SKILL" : damageTags)
            );
         skillMetadata.getVariables().putString("damage-tags", damageTags);
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

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof DamagedMetadata other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (Double.compare(this.getDamage(), other.getDamage()) != 0) {
         return false;
      } else {
         Object this$event = this.getEvent();
         Object other$event = other.getEvent();
         if (this$event == null ? other$event == null : this$event.equals(other$event)) {
            Object this$damaged = this.getDamaged();
            Object other$damaged = other.getDamaged();
            if (this$damaged == null ? other$damaged == null : this$damaged.equals(other$damaged)) {
               Object this$damageMetadata = this.getDamageMetadata();
               Object other$damageMetadata = other.getDamageMetadata();
               if (this$damageMetadata == null ? other$damageMetadata == null : this$damageMetadata.equals(other$damageMetadata)) {
                  Object this$bonusDamage = this.getBonusDamage();
                  Object other$bonusDamage = other.getBonusDamage();
                  if (this$bonusDamage == null ? other$bonusDamage == null : this$bonusDamage.equals(other$bonusDamage)) {
                     Object this$damageCause = this.getDamageCause();
                     Object other$damageCause = other.getDamageCause();
                     if (this$damageCause == null ? other$damageCause == null : this$damageCause.equals(other$damageCause)) {
                        Object this$element = this.getElement();
                        Object other$element = other.getElement();
                        return this$element == null ? other$element == null : this$element.equals(other$element);
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
      return other instanceof DamagedMetadata;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      long $damage = Double.doubleToLongBits(this.getDamage());
      result = result * 59 + (int)($damage >>> 32 ^ $damage);
      Object $event = this.getEvent();
      result = result * 59 + ($event == null ? 43 : $event.hashCode());
      Object $damaged = this.getDamaged();
      result = result * 59 + ($damaged == null ? 43 : $damaged.hashCode());
      Object $damageMetadata = this.getDamageMetadata();
      result = result * 59 + ($damageMetadata == null ? 43 : $damageMetadata.hashCode());
      Object $bonusDamage = this.getBonusDamage();
      result = result * 59 + ($bonusDamage == null ? 43 : $bonusDamage.hashCode());
      Object $damageCause = this.getDamageCause();
      result = result * 59 + ($damageCause == null ? 43 : $damageCause.hashCode());
      Object $element = this.getElement();
      return result * 59 + ($element == null ? 43 : $element.hashCode());
   }
}
