package io.lumine.mythic.api.skills.damage;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@Getter
@Setter
public class DamageMetadata {
   private final SkillCaster damager;
   private final SkillMetadata metadata;
   private final AbstractItemStack damagerItem;
   private double amount;
   private Map<String, Double> bonusDamage;
   private Map<String, Double> bonusDamageModifiers;
   @Deprecated
   private String element;
   private double multiplier = 1.0;
   private final Map<String, Object> data = new ConcurrentHashMap<>();
   private final Collection<String> tags = Sets.newHashSet();
   protected Boolean ignoresArmor;
   protected Boolean preventsImmunity;
   protected Boolean preventsKnockback;
   protected Boolean ignoreEnchantments;
   protected final DamageCause damageCause;

   public DamageMetadata(
      SkillCaster damager,
      SkillMetadata metadata,
      AbstractItemStack damagerItem,
      double amount,
      Map<String, Double> bonusDamage,
      Map<String, Double> bonusDamageModifiers,
      String element,
      double multiplier,
      Boolean ignoresArmor,
      Boolean preventsImmunity,
      Boolean preventsKnockback,
      Boolean ignoreEnchantments,
      DamageCause damageCause
   ) {
      this.damager = damager;
      this.metadata = metadata;
      this.damagerItem = damagerItem;
      this.amount = amount;
      this.bonusDamage = bonusDamage;
      this.bonusDamageModifiers = bonusDamageModifiers;
      this.element = element;
      this.multiplier = multiplier;
      this.ignoresArmor = ignoresArmor;
      this.preventsImmunity = preventsImmunity;
      this.preventsKnockback = preventsKnockback;
      this.ignoreEnchantments = ignoreEnchantments;
      this.damageCause = damageCause;
   }

   public void putObject(String key, Object value) {
      this.data.put(key, value);
   }

   public Object getValue(String key) {
      return this.data.get(key);
   }

   public Object getOrDefault(String key, Object def) {
      return this.data.getOrDefault(key, def);
   }

   public void putAll(Map<String, Object> data) {
      this.data.putAll(data);
   }

   public void putString(String key, String value) {
      this.data.put(key, value);
   }

   public void putBoolean(String key, boolean value) {
      this.data.put(key, value);
   }

   public void putTag(String key) {
      this.data.put(key, true);
      this.tags.add(key);
   }

   public void putFloat(String key, float value) {
      this.data.put(key, value);
   }

   public void putDouble(String key, double value) {
      this.data.put(key, value);
   }

   public String getString(String key) {
      return this.data.get(key).toString();
   }

   public boolean getBoolean(String key) {
      return this.getValue(key) instanceof Boolean bool ? bool : false;
   }

   public float getFloat(String key) {
      if (this.getValue(key) instanceof Float f) {
         return f;
      } else {
         throw new NumberFormatException(this.getString(key) + " is not a decimal value");
      }
   }

   public double getDouble(String key) {
      return this.getValue(key) instanceof Double f ? f : this.getFloat(key);
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("DamageMetadata(damager=")
         .append(this.getDamager().getName())
         .append(", amount=")
         .append(this.getAmount())
         .append(", element=")
         .append(this.getElement())
         .append(", ignoresArmor=")
         .append(this.getIgnoresArmor())
         .append(", preventsImmunity=")
         .append(this.getPreventsImmunity())
         .append(", preventsKnockback=")
         .append(this.getPreventsKnockback())
         .append(", ignoreEnchantments=")
         .append(this.getIgnoreEnchantments())
         .append(", damageCause=")
         .append(this.getDamageCause());
      this.data.forEach((key, value) -> sb.append(", ").append(key).append("=").append(value));
      sb.append(")");
      return sb.toString();
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof DamageMetadata other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (Double.compare(this.getAmount(), other.getAmount()) != 0) {
         return false;
      } else if (Double.compare(this.getMultiplier(), other.getMultiplier()) != 0) {
         return false;
      } else {
         Object this$ignoresArmor = this.getIgnoresArmor();
         Object other$ignoresArmor = other.getIgnoresArmor();
         if (this$ignoresArmor == null ? other$ignoresArmor == null : this$ignoresArmor.equals(other$ignoresArmor)) {
            Object this$preventsImmunity = this.getPreventsImmunity();
            Object other$preventsImmunity = other.getPreventsImmunity();
            if (this$preventsImmunity == null ? other$preventsImmunity == null : this$preventsImmunity.equals(other$preventsImmunity)) {
               Object this$preventsKnockback = this.getPreventsKnockback();
               Object other$preventsKnockback = other.getPreventsKnockback();
               if (this$preventsKnockback == null ? other$preventsKnockback == null : this$preventsKnockback.equals(other$preventsKnockback)) {
                  Object this$ignoreEnchantments = this.getIgnoreEnchantments();
                  Object other$ignoreEnchantments = other.getIgnoreEnchantments();
                  if (this$ignoreEnchantments == null ? other$ignoreEnchantments == null : this$ignoreEnchantments.equals(other$ignoreEnchantments)) {
                     Object this$damager = this.getDamager();
                     Object other$damager = other.getDamager();
                     if (this$damager == null ? other$damager == null : this$damager.equals(other$damager)) {
                        Object this$metadata = this.getMetadata();
                        Object other$metadata = other.getMetadata();
                        if (this$metadata == null ? other$metadata == null : this$metadata.equals(other$metadata)) {
                           Object this$damagerItem = this.getDamagerItem();
                           Object other$damagerItem = other.getDamagerItem();
                           if (this$damagerItem == null ? other$damagerItem == null : this$damagerItem.equals(other$damagerItem)) {
                              Object this$bonusDamage = this.getBonusDamage();
                              Object other$bonusDamage = other.getBonusDamage();
                              if (this$bonusDamage == null ? other$bonusDamage == null : this$bonusDamage.equals(other$bonusDamage)) {
                                 Object this$bonusDamageModifiers = this.getBonusDamageModifiers();
                                 Object other$bonusDamageModifiers = other.getBonusDamageModifiers();
                                 if (this$bonusDamageModifiers == null
                                    ? other$bonusDamageModifiers == null
                                    : this$bonusDamageModifiers.equals(other$bonusDamageModifiers)) {
                                    Object this$element = this.getElement();
                                    Object other$element = other.getElement();
                                    if (this$element == null ? other$element == null : this$element.equals(other$element)) {
                                       Object this$data = this.getData();
                                       Object other$data = other.getData();
                                       if (this$data == null ? other$data == null : this$data.equals(other$data)) {
                                          Object this$tags = this.getTags();
                                          Object other$tags = other.getTags();
                                          if (this$tags == null ? other$tags == null : this$tags.equals(other$tags)) {
                                             Object this$damageCause = this.getDamageCause();
                                             Object other$damageCause = other.getDamageCause();
                                             return this$damageCause == null ? other$damageCause == null : this$damageCause.equals(other$damageCause);
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
      return other instanceof DamageMetadata;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      long $amount = Double.doubleToLongBits(this.getAmount());
      result = result * 59 + (int)($amount >>> 32 ^ $amount);
      long $multiplier = Double.doubleToLongBits(this.getMultiplier());
      result = result * 59 + (int)($multiplier >>> 32 ^ $multiplier);
      Object $ignoresArmor = this.getIgnoresArmor();
      result = result * 59 + ($ignoresArmor == null ? 43 : $ignoresArmor.hashCode());
      Object $preventsImmunity = this.getPreventsImmunity();
      result = result * 59 + ($preventsImmunity == null ? 43 : $preventsImmunity.hashCode());
      Object $preventsKnockback = this.getPreventsKnockback();
      result = result * 59 + ($preventsKnockback == null ? 43 : $preventsKnockback.hashCode());
      Object $ignoreEnchantments = this.getIgnoreEnchantments();
      result = result * 59 + ($ignoreEnchantments == null ? 43 : $ignoreEnchantments.hashCode());
      Object $damager = this.getDamager();
      result = result * 59 + ($damager == null ? 43 : $damager.hashCode());
      Object $metadata = this.getMetadata();
      result = result * 59 + ($metadata == null ? 43 : $metadata.hashCode());
      Object $damagerItem = this.getDamagerItem();
      result = result * 59 + ($damagerItem == null ? 43 : $damagerItem.hashCode());
      Object $bonusDamage = this.getBonusDamage();
      result = result * 59 + ($bonusDamage == null ? 43 : $bonusDamage.hashCode());
      Object $bonusDamageModifiers = this.getBonusDamageModifiers();
      result = result * 59 + ($bonusDamageModifiers == null ? 43 : $bonusDamageModifiers.hashCode());
      Object $element = this.getElement();
      result = result * 59 + ($element == null ? 43 : $element.hashCode());
      Object $data = this.getData();
      result = result * 59 + ($data == null ? 43 : $data.hashCode());
      Object $tags = this.getTags();
      result = result * 59 + ($tags == null ? 43 : $tags.hashCode());
      Object $damageCause = this.getDamageCause();
      return result * 59 + ($damageCause == null ? 43 : $damageCause.hashCode());
   }
}
