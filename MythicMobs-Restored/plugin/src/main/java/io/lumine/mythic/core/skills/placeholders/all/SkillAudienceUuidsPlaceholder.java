package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "skill.audience-uuids",
   version = "5.7"
)
public class SkillAudienceUuidsPlaceholder extends GenericPlaceholder<String> implements StringPlaceholder {
   public SkillAudienceUuidsPlaceholder(GenericPlaceholder.GenericPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public String applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      return placeholderContext.meta() instanceof SkillMetadata data
         ? data.getExecutionAudience()
            .filter(audience -> !audience.isEmpty())
            .map(audience -> audience.stream().map(UUID::toString).collect(Collectors.joining(",")))
            .orElse("")
         : "";
   }
}
