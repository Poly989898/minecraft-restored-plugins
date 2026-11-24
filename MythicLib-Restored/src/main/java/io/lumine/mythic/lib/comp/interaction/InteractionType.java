package io.lumine.mythic.lib.comp.interaction;

public enum InteractionType {
   OFFENSE_SKILL,
   SUPPORT_SKILL,
   OFFENSE_ACTION,
   SUPPORT_ACTION;

   public boolean isSkill() {
      return this == OFFENSE_SKILL || this == SUPPORT_SKILL;
   }

   public boolean isOffense() {
      return this == OFFENSE_ACTION || this == OFFENSE_SKILL;
   }
}
