package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.entity.Entity;

@MythicCondition(
   author = "Archontas",
   name = "uuid",
   aliases = {},
   version = "5.2",
   description = "Checks if target entity's UUID matches a given UUID"
)
public class UUIDCondition extends SkillCondition implements ISkillMetaComparisonCondition {
   @MythicField(
      name = "uuid",
      aliases = {"u"},
      description = "The UUID to compare against (supports placeholders)"
   )
   private final PlaceholderString uuidPlaceholder;

   public UUIDCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.uuidPlaceholder = mlc.getPlaceholderString(new String[]{"uuid", "u"}, null);
      if (this.uuidPlaceholder == null) {
         throw new IllegalArgumentException("UUIDCondition requires 'u' or 'uuid' parameter");
      }
   }

   @Override
   public boolean check(SkillMetadata data, AbstractEntity target) {
      String expectedUuidStr = this.uuidPlaceholder.get(data, target);
      if (expectedUuidStr != null && !expectedUuidStr.isEmpty()) {
         Entity bukkitTarget = BukkitAdapter.adapt(target);
         if (bukkitTarget == null) {
            return false;
         } else {
            String targetUuidStr = bukkitTarget.getUniqueId().toString();
            return this.normalizeUUID(expectedUuidStr).equalsIgnoreCase(this.normalizeUUID(targetUuidStr));
         }
      } else {
         return false;
      }
   }

   private String normalizeUUID(String uuid) {
      return uuid.replace("-", "").toLowerCase();
   }
}
