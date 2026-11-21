package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import java.util.Optional;

@MythicCondition(
   author = "Ashijin",
   name = "samefaction",
   aliases = {"factionsame"},
   version = "4.9",
   description = "Tests if the target is in the same faction as the caster"
)
public class FactionSameCondition extends SkillCondition implements IEntityComparisonCondition {
   public FactionSameCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity, AbstractEntity target) {
      if (entity.isPlayer() && !target.isPlayer()) {
         String f1 = this.getFaction(target);
         return getPlugin().getPlayerManager().getFactionProvider().isInFaction(entity.asPlayer(), f1);
      } else if (!entity.isPlayer() && target.isPlayer()) {
         String f1 = this.getFaction(entity);
         return getPlugin().getPlayerManager().getFactionProvider().isInFaction(target.asPlayer(), f1);
      } else {
         String f1 = this.getFaction(entity);
         String f2 = this.getFaction(target);
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "SameFaction {0} == {1}", f1, f2);
         return f1.equals(f2);
      }
   }

   private String getFaction(AbstractEntity entity) {
      String faction;
      if (entity.isPlayer()) {
         Optional<String> maybeFaction = getPlugin().getPlayerManager().getFactionProvider().getFaction(entity.asPlayer());
         if (maybeFaction.isPresent()) {
            faction = maybeFaction.get();
         } else {
            faction = "NONE";
         }
      } else {
         ActiveMob am = getPlugin().getMobManager().getMythicMobInstance(entity);
         if (am == null) {
            faction = "NONE";
         } else {
            faction = am.getFaction() == null ? "NONE" : am.getFaction();
         }
      }

      return faction;
   }
}
