package io.lumine.mythic.api.adapters.items.components;

import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import lombok.Generated;

public class AbstractItemKineticWeaponComponent implements AbstractItemComponent {
   private Integer contactCooldownTicks;
   private Integer delayTicks;
   private Float forwardMovement;
   private Float damageMultiplier;
   private String sound;
   private String hitSound;
   private AbstractItemKineticWeaponComponent.ConditionConfig damageConditions;
   private AbstractItemKineticWeaponComponent.ConditionConfig knockbackConditions;
   private AbstractItemKineticWeaponComponent.ConditionConfig dismountConditions;

   public AbstractItemKineticWeaponComponent(MythicItem mythicItem, MythicConfig config) {
      if (config.isSet("ContactCooldownTicks")) {
         this.contactCooldownTicks = config.getInteger("ContactCooldownTicks");
         if (this.contactCooldownTicks < 0) {
            MythicLogger.errorGenericConfig(config, "KineticWeapon.ContactCooldownTicks cannot be less than 0, using 0 instead");
            this.contactCooldownTicks = 0;
         }
      }

      if (config.isSet("DelayTicks")) {
         this.delayTicks = config.getInteger("DelayTicks");
         if (this.delayTicks < 0) {
            MythicLogger.errorGenericConfig(config, "KineticWeapon.DelayTicks cannot be less than 0, using 0 instead");
            this.delayTicks = 0;
         }
      }

      if (config.isSet("ForwardMovement")) {
         this.forwardMovement = config.getFloat("ForwardMovement");
      }

      if (config.isSet("DamageMultiplier")) {
         this.damageMultiplier = config.getFloat("DamageMultiplier");
      }

      if (config.isSet("Sound")) {
         this.sound = MythicConfig.normalizeNamespacedKey(config.getString("Sound"));
      }

      if (config.isSet("HitSound")) {
         this.hitSound = MythicConfig.normalizeNamespacedKey(config.getString("HitSound"));
      }

      if (config.isSet("DamageConditions")) {
         this.damageConditions = this.parseCondition(config.getNestedConfig("DamageConditions"));
      }

      if (config.isSet("KnockbackConditions")) {
         this.knockbackConditions = this.parseCondition(config.getNestedConfig("KnockbackConditions"));
      }

      if (config.isSet("DismountConditions")) {
         this.dismountConditions = this.parseCondition(config.getNestedConfig("DismountConditions"));
      }
   }

   @Override
   public void apply(MythicItem mythicItem, DropMetadata dropMetadata, AbstractItemStack itemStack) {
      itemStack.applyKineticWeaponComponent(dropMetadata, this);
   }

   private AbstractItemKineticWeaponComponent.ConditionConfig parseCondition(MythicConfig config) {
      Integer maxDurationTicks = config.isSet("MaxDurationTicks") ? config.getInteger("MaxDurationTicks") : null;
      Float minSpeed = config.isSet("MinSpeed") ? config.getFloat("MinSpeed") : null;
      Float minRelativeSpeed = config.isSet("MinRelativeSpeed") ? config.getFloat("MinRelativeSpeed") : null;
      if (maxDurationTicks != null && maxDurationTicks < 0) {
         MythicLogger.errorGenericConfig(config, "KineticWeapon condition MaxDurationTicks cannot be less than 0, using 0 instead");
         maxDurationTicks = 0;
      }

      return maxDurationTicks == null && minSpeed == null && minRelativeSpeed == null
         ? null
         : new AbstractItemKineticWeaponComponent.ConditionConfig(maxDurationTicks, minSpeed, minRelativeSpeed);
   }

   @Generated
   public Integer getContactCooldownTicks() {
      return this.contactCooldownTicks;
   }

   @Generated
   public Integer getDelayTicks() {
      return this.delayTicks;
   }

   @Generated
   public Float getForwardMovement() {
      return this.forwardMovement;
   }

   @Generated
   public Float getDamageMultiplier() {
      return this.damageMultiplier;
   }

   @Generated
   public String getSound() {
      return this.sound;
   }

   @Generated
   public String getHitSound() {
      return this.hitSound;
   }

   @Generated
   public AbstractItemKineticWeaponComponent.ConditionConfig getDamageConditions() {
      return this.damageConditions;
   }

   @Generated
   public AbstractItemKineticWeaponComponent.ConditionConfig getKnockbackConditions() {
      return this.knockbackConditions;
   }

   @Generated
   public AbstractItemKineticWeaponComponent.ConditionConfig getDismountConditions() {
      return this.dismountConditions;
   }

   @Generated
   public void setContactCooldownTicks(Integer contactCooldownTicks) {
      this.contactCooldownTicks = contactCooldownTicks;
   }

   @Generated
   public void setDelayTicks(Integer delayTicks) {
      this.delayTicks = delayTicks;
   }

   @Generated
   public void setForwardMovement(Float forwardMovement) {
      this.forwardMovement = forwardMovement;
   }

   @Generated
   public void setDamageMultiplier(Float damageMultiplier) {
      this.damageMultiplier = damageMultiplier;
   }

   @Generated
   public void setSound(String sound) {
      this.sound = sound;
   }

   @Generated
   public void setHitSound(String hitSound) {
      this.hitSound = hitSound;
   }

   @Generated
   public void setDamageConditions(AbstractItemKineticWeaponComponent.ConditionConfig damageConditions) {
      this.damageConditions = damageConditions;
   }

   @Generated
   public void setKnockbackConditions(AbstractItemKineticWeaponComponent.ConditionConfig knockbackConditions) {
      this.knockbackConditions = knockbackConditions;
   }

   @Generated
   public void setDismountConditions(AbstractItemKineticWeaponComponent.ConditionConfig dismountConditions) {
      this.dismountConditions = dismountConditions;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AbstractItemKineticWeaponComponent other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$contactCooldownTicks = this.getContactCooldownTicks();
         Object other$contactCooldownTicks = other.getContactCooldownTicks();
         if (this$contactCooldownTicks == null ? other$contactCooldownTicks == null : this$contactCooldownTicks.equals(other$contactCooldownTicks)) {
            Object this$delayTicks = this.getDelayTicks();
            Object other$delayTicks = other.getDelayTicks();
            if (this$delayTicks == null ? other$delayTicks == null : this$delayTicks.equals(other$delayTicks)) {
               Object this$forwardMovement = this.getForwardMovement();
               Object other$forwardMovement = other.getForwardMovement();
               if (this$forwardMovement == null ? other$forwardMovement == null : this$forwardMovement.equals(other$forwardMovement)) {
                  Object this$damageMultiplier = this.getDamageMultiplier();
                  Object other$damageMultiplier = other.getDamageMultiplier();
                  if (this$damageMultiplier == null ? other$damageMultiplier == null : this$damageMultiplier.equals(other$damageMultiplier)) {
                     Object this$sound = this.getSound();
                     Object other$sound = other.getSound();
                     if (this$sound == null ? other$sound == null : this$sound.equals(other$sound)) {
                        Object this$hitSound = this.getHitSound();
                        Object other$hitSound = other.getHitSound();
                        if (this$hitSound == null ? other$hitSound == null : this$hitSound.equals(other$hitSound)) {
                           Object this$damageConditions = this.getDamageConditions();
                           Object other$damageConditions = other.getDamageConditions();
                           if (this$damageConditions == null ? other$damageConditions == null : this$damageConditions.equals(other$damageConditions)) {
                              Object this$knockbackConditions = this.getKnockbackConditions();
                              Object other$knockbackConditions = other.getKnockbackConditions();
                              if (this$knockbackConditions == null
                                 ? other$knockbackConditions == null
                                 : this$knockbackConditions.equals(other$knockbackConditions)) {
                                 Object this$dismountConditions = this.getDismountConditions();
                                 Object other$dismountConditions = other.getDismountConditions();
                                 return this$dismountConditions == null
                                    ? other$dismountConditions == null
                                    : this$dismountConditions.equals(other$dismountConditions);
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
      return other instanceof AbstractItemKineticWeaponComponent;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $contactCooldownTicks = this.getContactCooldownTicks();
      result = result * 59 + ($contactCooldownTicks == null ? 43 : $contactCooldownTicks.hashCode());
      Object $delayTicks = this.getDelayTicks();
      result = result * 59 + ($delayTicks == null ? 43 : $delayTicks.hashCode());
      Object $forwardMovement = this.getForwardMovement();
      result = result * 59 + ($forwardMovement == null ? 43 : $forwardMovement.hashCode());
      Object $damageMultiplier = this.getDamageMultiplier();
      result = result * 59 + ($damageMultiplier == null ? 43 : $damageMultiplier.hashCode());
      Object $sound = this.getSound();
      result = result * 59 + ($sound == null ? 43 : $sound.hashCode());
      Object $hitSound = this.getHitSound();
      result = result * 59 + ($hitSound == null ? 43 : $hitSound.hashCode());
      Object $damageConditions = this.getDamageConditions();
      result = result * 59 + ($damageConditions == null ? 43 : $damageConditions.hashCode());
      Object $knockbackConditions = this.getKnockbackConditions();
      result = result * 59 + ($knockbackConditions == null ? 43 : $knockbackConditions.hashCode());
      Object $dismountConditions = this.getDismountConditions();
      return result * 59 + ($dismountConditions == null ? 43 : $dismountConditions.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "AbstractItemKineticWeaponComponent(contactCooldownTicks="
         + this.getContactCooldownTicks()
         + ", delayTicks="
         + this.getDelayTicks()
         + ", forwardMovement="
         + this.getForwardMovement()
         + ", damageMultiplier="
         + this.getDamageMultiplier()
         + ", sound="
         + this.getSound()
         + ", hitSound="
         + this.getHitSound()
         + ", damageConditions="
         + this.getDamageConditions()
         + ", knockbackConditions="
         + this.getKnockbackConditions()
         + ", dismountConditions="
         + this.getDismountConditions()
         + ")";
   }

   public static class ConditionConfig {
      private final Integer maxDurationTicks;
      private final Float minSpeed;
      private final Float minRelativeSpeed;

      @Generated
      public ConditionConfig(Integer maxDurationTicks, Float minSpeed, Float minRelativeSpeed) {
         this.maxDurationTicks = maxDurationTicks;
         this.minSpeed = minSpeed;
         this.minRelativeSpeed = minRelativeSpeed;
      }

      @Generated
      public Integer getMaxDurationTicks() {
         return this.maxDurationTicks;
      }

      @Generated
      public Float getMinSpeed() {
         return this.minSpeed;
      }

      @Generated
      public Float getMinRelativeSpeed() {
         return this.minRelativeSpeed;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof AbstractItemKineticWeaponComponent.ConditionConfig other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$maxDurationTicks = this.getMaxDurationTicks();
            Object other$maxDurationTicks = other.getMaxDurationTicks();
            if (this$maxDurationTicks == null ? other$maxDurationTicks == null : this$maxDurationTicks.equals(other$maxDurationTicks)) {
               Object this$minSpeed = this.getMinSpeed();
               Object other$minSpeed = other.getMinSpeed();
               if (this$minSpeed == null ? other$minSpeed == null : this$minSpeed.equals(other$minSpeed)) {
                  Object this$minRelativeSpeed = this.getMinRelativeSpeed();
                  Object other$minRelativeSpeed = other.getMinRelativeSpeed();
                  return this$minRelativeSpeed == null ? other$minRelativeSpeed == null : this$minRelativeSpeed.equals(other$minRelativeSpeed);
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
         return other instanceof AbstractItemKineticWeaponComponent.ConditionConfig;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $maxDurationTicks = this.getMaxDurationTicks();
         result = result * 59 + ($maxDurationTicks == null ? 43 : $maxDurationTicks.hashCode());
         Object $minSpeed = this.getMinSpeed();
         result = result * 59 + ($minSpeed == null ? 43 : $minSpeed.hashCode());
         Object $minRelativeSpeed = this.getMinRelativeSpeed();
         return result * 59 + ($minRelativeSpeed == null ? 43 : $minRelativeSpeed.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "AbstractItemKineticWeaponComponent.ConditionConfig(maxDurationTicks="
            + this.getMaxDurationTicks()
            + ", minSpeed="
            + this.getMinSpeed()
            + ", minRelativeSpeed="
            + this.getMinRelativeSpeed()
            + ")";
      }
   }
}
