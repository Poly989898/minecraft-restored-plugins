package io.lumine.mythic.core.skills.triggers.meta;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import lombok.Generated;

public class ThrownMetadata extends SkillTriggerMetadata {
   private final AbstractEntity thrower;
   private final AbstractVector throwVelocity;
   private final float throwStrength;
   private final float throwStrengthY;

   public ThrownMetadata(AbstractEntity thrower, AbstractVector throwVelocity, float throwStrength, float throwStrengthY) {
      this.thrower = thrower;
      this.throwVelocity = throwVelocity;
      this.throwStrength = throwStrength;
      this.throwStrengthY = throwStrengthY;
   }

   @Override
   public void applyToSkillMetadata(SkillMetadata skillMetadata) {
      skillMetadata.getVariables().putObject("thrower", this.thrower);
      skillMetadata.getVariables().putObject("throw-velocity", this.throwVelocity);
      skillMetadata.getVariables().putDouble("throw-strength", this.throwStrength);
      skillMetadata.getVariables().putDouble("throw-strength-y", this.throwStrengthY);
   }

   @Generated
   public AbstractEntity getThrower() {
      return this.thrower;
   }

   @Generated
   public AbstractVector getThrowVelocity() {
      return this.throwVelocity;
   }

   @Generated
   public float getThrowStrength() {
      return this.throwStrength;
   }

   @Generated
   public float getThrowStrengthY() {
      return this.throwStrengthY;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ThrownMetadata other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (Float.compare(this.getThrowStrength(), other.getThrowStrength()) != 0) {
         return false;
      } else if (Float.compare(this.getThrowStrengthY(), other.getThrowStrengthY()) != 0) {
         return false;
      } else {
         Object this$thrower = this.getThrower();
         Object other$thrower = other.getThrower();
         if (this$thrower == null ? other$thrower == null : this$thrower.equals(other$thrower)) {
            Object this$throwVelocity = this.getThrowVelocity();
            Object other$throwVelocity = other.getThrowVelocity();
            return this$throwVelocity == null ? other$throwVelocity == null : this$throwVelocity.equals(other$throwVelocity);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof ThrownMetadata;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + Float.floatToIntBits(this.getThrowStrength());
      result = result * 59 + Float.floatToIntBits(this.getThrowStrengthY());
      Object $thrower = this.getThrower();
      result = result * 59 + ($thrower == null ? 43 : $thrower.hashCode());
      Object $throwVelocity = this.getThrowVelocity();
      return result * 59 + ($throwVelocity == null ? 43 : $throwVelocity.hashCode());
   }
}
