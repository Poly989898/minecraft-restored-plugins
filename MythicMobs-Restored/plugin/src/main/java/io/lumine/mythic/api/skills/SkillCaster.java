package io.lumine.mythic.api.skills;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.core.skills.AbstractSkill;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.auras.AuraRegistry;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import java.util.Collection;

public interface SkillCaster {
   AbstractEntity getEntity();

   default AbstractLocation getLocation() {
      return this.getEntity().getLocation();
   }

   void setUsingDamageSkill(boolean flag);

   boolean isUsingDamageSkill();

   default double getLevel() {
      return 1.0;
   }

   default float getPower() {
      return 1.0F;
   }

   default int getGlobalCooldown() {
      return 0;
   }

   default void setGlobalCooldown(int ticks) {
   }

   default void setSkillCooldownMillis(AbstractSkill skill, double millis) {
      MythicProvider.get().getSkillManager().setSkillCooldown(this, skill, millis);
   }

   default void setSkillCooldownSeconds(AbstractSkill skill, double seconds) {
      this.setSkillCooldownMillis(skill, seconds * 1000.0);
   }

   default void setSkillCooldownTicks(AbstractSkill skill, int ticks) {
      this.setSkillCooldownMillis(skill, ticks * 50.0);
   }

   default boolean isSkillOnCooldown(AbstractSkill skill) {
      return MythicProvider.get().getSkillManager().isSkillOnCooldown(this, skill);
   }

   default double getSkillCooldownRemainingMillis(AbstractSkill skill) {
      return MythicProvider.get().getSkillManager().getSkillCooldownRemainingMillis(this, skill);
   }

   default double getSkillCooldownRemainingSeconds(AbstractSkill skill) {
      double remaining = this.getSkillCooldownRemainingMillis(skill);
      if (remaining == 9.223372E18F) {
         return Double.MAX_VALUE;
      } else {
         return remaining > 0.0 ? remaining / 1000.0 : 0.0;
      }
   }

   default AuraRegistry getAuraRegistry() {
      return MythicProvider.get().getSkillManager().getAuraManager().getAuraRegistry(this.getEntity());
   }

   default void registerAura(String buffName, Aura.AuraTracker buff) {
      if (this.getAuraRegistry() != null) {
         this.getAuraRegistry().registerAura(buffName, buff);
      }
   }

   default void unregisterAura(String buffName, Aura.AuraTracker buff) {
      if (this.getAuraRegistry() != null) {
         this.getAuraRegistry().unregisterAura(buffName, buff);
      }
   }

   default boolean hasAura(String auraName) {
      return MythicProvider.get().getSkillManager().getAuraManager().getHasAura(this.getEntity(), auraName);
   }

   default int getAuraStacks(String auraName) {
      return MythicProvider.get().getSkillManager().getAuraManager().getAuraStacks(this.getEntity(), auraName);
   }

   default void addChild(AbstractEntity entity) {
   }

   default void removeChild(AbstractEntity entity) {
   }

   default Collection<AbstractEntity> getChildren() {
      return Sets.newConcurrentHashSet();
   }

   default String getName() {
      return this.getEntity() == null ? "Unknown" : this.getEntity().getName();
   }

   default StatRegistry getStatRegistry() {
      return null;
   }
}
