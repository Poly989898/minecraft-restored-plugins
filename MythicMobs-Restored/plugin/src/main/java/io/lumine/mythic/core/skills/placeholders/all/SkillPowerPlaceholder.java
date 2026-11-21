package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.FloatPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "skill.power",
   version = "5.11"
)
public class SkillPowerPlaceholder extends GenericPlaceholder<Float> implements FloatPlaceholder {
   public SkillPowerPlaceholder(GenericPlaceholder.GenericPlaceholderArguments metaContext) {
      super(metaContext);
   }

   @Nullable
   public Float applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      return placeholderContext.getSkillMetadata().map(SkillMetadata::getPower).orElse(0.0F);
   }
}
