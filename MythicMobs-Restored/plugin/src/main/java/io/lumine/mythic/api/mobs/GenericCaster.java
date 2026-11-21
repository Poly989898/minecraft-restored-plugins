package io.lumine.mythic.api.mobs;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.SkillCaster;

public class GenericCaster implements SkillCaster {
   private AbstractEntity entity;
   private boolean doingDamage = false;
   private int gcd = 0;

   public GenericCaster(AbstractEntity entity) {
      this.entity = entity;
   }

   @Override
   public AbstractEntity getEntity() {
      return this.entity;
   }

   @Override
   public AbstractLocation getLocation() {
      return this.entity.getLocation();
   }

   @Override
   public void setUsingDamageSkill(boolean b) {
      this.doingDamage = b;
   }

   @Override
   public boolean isUsingDamageSkill() {
      return this.doingDamage;
   }

   @Override
   public double getLevel() {
      return 0.0;
   }

   @Override
   public float getPower() {
      return 1.0F;
   }

   @Override
   public int getGlobalCooldown() {
      return this.gcd;
   }

   @Override
   public void setGlobalCooldown(int ticks) {
      this.gcd = ticks;
   }
}
