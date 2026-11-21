package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.AbstractSkill;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.SegmentSource;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.IntegerPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "skill.*.charges",
   usedPlaceholderArguments = -1
)
public class SkillChargesPlaceholder extends EntityScopedPlaceholder<Integer> implements IntegerPlaceholder {
   private final ResolvedPlaceholderSegment<PlaceholderString> skill = this.getPlaceholderString(0, SegmentSource.WILDCARD_ARGS, new String[0]);

   public SkillChargesPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
   }

   @Nullable
   public Integer applyToScope(PlaceholderContext placeholderContext) {
      String skillName = this.skill.getOrDefault(placeholderContext, PlaceholderString::get, null);
      AbstractEntity entity = this.getEntity.get(placeholderContext);
      if (entity == null) {
         return null;
      } else {
         SkillCaster caster = this.manager.getPlugin().getSkillManager().getCaster(entity);
         if (skillName != null && caster != null) {
            Optional<Skill> maybeSkill = this.manager.getPlugin().getSkillManager().getSkill(skillName);
            if (!maybeSkill.isEmpty() && maybeSkill.get() instanceof AbstractSkill trackedSkill) {
               return trackedSkill.getAvailableCharges(caster);
            } else {
               MythicLogger.debug(
                  MythicLogger.DebugLevel.SKILL_INFO, "Placeholder " + this.instanceData.unparsedPlaceholder + " found no tracked skill " + skillName
               );
               return null;
            }
         } else {
            return null;
         }
      }
   }
}
