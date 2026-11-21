package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.AbstractSkill;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.SegmentSource;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.DoublePlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.util.Arrays;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "skill.*.cooldown",
   usedPlaceholderArguments = -1
)
public class SkillCooldownPlaceholder extends EntityScopedPlaceholder<Double> implements DoublePlaceholder {
   private final ResolvedPlaceholderSegment<PlaceholderString> skill = this.getPlaceholderString(0, SegmentSource.WILDCARD_ARGS, new String[0]);
   private final SkillCooldownPlaceholder.Types type = this.<SkillCooldownPlaceholder.Types>getResolver()
      .mlcGetter(mythicLineConfig -> mythicLineConfig.getEnum(new String[]{"type"}, SkillCooldownPlaceholder.Types.class, null))
      .segIndex(0)
      .segValueParser(s -> SkillCooldownPlaceholder.Types.valueOf(s.toUpperCase()))
      .segValueChecker(s -> Arrays.stream(SkillCooldownPlaceholder.Types.values()).anyMatch(b -> b.name().equals(s.toUpperCase())))
      .defaultValue(SkillCooldownPlaceholder.Types.SECONDS)
      .build()
      .get()
      .value();

   public SkillCooldownPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
      this.initializeMetaKeywords();
   }

   @Nullable
   public Double applyToScope(PlaceholderContext placeholderContext) {
      if (this.type == null) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "Placeholder " + this.instanceData.unparsedPlaceholder + " is invalid");
         return null;
      } else {
         String skillName = this.skill.getOrDefault(placeholderContext, PlaceholderString::get, null);
         AbstractEntity entity = this.getEntity.get(placeholderContext);
         if (entity == null) {
            return null;
         } else {
            Optional<Skill> maybeSkill = this.manager.getPlugin().getSkillManager().getSkill(skillName);
            if (maybeSkill.isEmpty()) {
               MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "Placeholder " + this.instanceData.unparsedPlaceholder + " found no skill " + skillName);
               return null;
            } else if (maybeSkill.get() instanceof AbstractSkill skill) {
               SkillCaster caster = this.manager.getPlugin().getSkillManager().getCaster(entity);
               if (caster == null) {
                  return null;
               } else {
                  return switch (this.type) {
                     case MILLIS -> (double)skill.getCooldownMillis(caster);
                     default -> {
                        double cooldown = skill.getCooldown(caster);
                        yield cooldown == Double.MAX_VALUE ? cooldown : Numbers.round(cooldown, 2);
                     }
                  };
               }
            } else {
               MythicLogger.debug(
                  MythicLogger.DebugLevel.SKILL_INFO, "Placeholder " + this.instanceData.unparsedPlaceholder + " found no tracked skill " + skillName
               );
               return null;
            }
         }
      }
   }

   static enum Types {
      MILLIS,
      SECONDS;
   }
}
