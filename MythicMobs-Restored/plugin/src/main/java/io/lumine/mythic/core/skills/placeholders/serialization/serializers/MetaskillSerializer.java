package io.lumine.mythic.core.skills.placeholders.serialization.serializers;

import io.lumine.mythic.core.skills.MetaSkill;
import io.lumine.mythic.core.skills.placeholders.serialization.MythicPlaceholderClassSerializer;
import io.lumine.mythic.core.skills.placeholders.serialization.PlaceholderClassSerializer;
import io.lumine.mythic.core.skills.variables.types.MetaSkillVariable;
import org.jetbrains.annotations.NotNull;

@MythicPlaceholderClassSerializer(
   name = "metaskill"
)
public class MetaskillSerializer extends PlaceholderClassSerializer<MetaSkill> {
   @NotNull
   public String serialize(@NotNull MetaSkill object) {
      return MetaSkillVariable.toString(object);
   }

   @NotNull
   public MetaSkill deserialize(@NotNull String object) {
      return MetaSkillVariable.fromString(object);
   }
}
