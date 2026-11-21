package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.IntegerPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "skill.targets",
   version = "5.6"
)
public class SkillTargetsPlaceholder extends GenericPlaceholder<Integer> implements IntegerPlaceholder {
   public SkillTargetsPlaceholder(GenericPlaceholder.GenericPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public Integer applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      if (placeholderContext.meta() instanceof SkillMetadata data) {
         if (data.getEntityTargets() != null && !data.getEntityTargets().isEmpty()) {
            return data.getEntityTargets().size();
         } else {
            return data.getLocationTargets() != null && !data.getLocationTargets().isEmpty() ? data.getLocationTargets().size() : 0;
         }
      } else {
         return 0;
      }
   }
}
