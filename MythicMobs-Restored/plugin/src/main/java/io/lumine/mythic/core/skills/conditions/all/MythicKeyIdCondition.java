package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Phil",
   name = "MythicKeyId",
   aliases = {"keyid", "ariKeyId"},
   description = "Tests for the mythic key id that was pressed/released"
)
public class MythicKeyIdCondition extends SkillCondition implements ISkillMetaCondition {
   @MythicField(
      name = "mythickey",
      aliases = {"key", "keyid", "id"},
      description = "The MythicKey id to check for",
      defValue = "minecraft:attack"
   )
   private final PlaceholderString keyId;

   public MythicKeyIdCondition(String line, MythicLineConfig config) {
      super(line);
      this.keyId = config.getPlaceholderString(new String[]{"mythickey", "key", "keyid", "id", "arikey"}, "minecraft:attack");
   }

   @Override
   public boolean check(SkillMetadata data) {
      return data.getVariables().has("key-id") && data.getVariables().getString("key-id").equalsIgnoreCase(this.keyId.get(data, data.getCaster().getEntity()));
   }
}
