package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.SegmentSource;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "skill.*",
   version = "5.6"
)
public class SkillParameterPlaceholder extends GenericPlaceholder<String> implements StringPlaceholder {
   private final ResolvedPlaceholderSegment<PlaceholderString> parameter = this.getPlaceholderString(0, SegmentSource.WILDCARD_ARGS, new String[0]);

   public SkillParameterPlaceholder(GenericPlaceholder.GenericPlaceholderArguments metaContext) {
      super(metaContext);
   }

   @Nullable
   public String applyWithMetaKeywords(PlaceholderContext context) {
      String parameterName = this.parameter.getOrDefault(context, PlaceholderString::get, "[Invalid]");
      if (context.meta() instanceof SkillMetadata data) {
         String def = null;
         if (parameterName.contains("|")) {
            String[] split = parameterName.split("\\|");
            parameterName = split[0];

            try {
               def = split[1];
            } catch (IllegalArgumentException e) {
            }
         } else if (super.defaultValue != null) {
            def = this.defaultValue.get(context);
         }

         String parameter = data.getParameters().get(parameterName.toLowerCase());
         return parameter == null ? def : parameter;
      } else {
         return null;
      }
   }
}
