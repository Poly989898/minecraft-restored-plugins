package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.UUID;

@MythicTargeter(
   author = "Phil",
   name = "UniqueIdentifier",
   aliases = {"uuid"},
   description = "Targets entities with the configured UUID or UUID list"
)
public class UniqueIdentifierTargeter extends IEntitySelector {
   private final PlaceholderString uuid;

   public UniqueIdentifierTargeter(SkillExecutor manager, MythicLineConfig config) {
      super(manager, config);
      this.uuid = config.getPlaceholderString(new String[]{"uuid", "u"}, "0");
      this.targetAll = true;
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      String parsedUuid = this.uuid.get(data).trim();
      if (!parsedUuid.contains(",")) {
         try {
            AbstractEntity entity = this.getPlugin().getBootstrap().getEntity(UUID.fromString(parsedUuid));
            return entity == null ? Collections.emptyList() : Collections.singletonList(entity);
         } catch (IllegalArgumentException e) {
            return Collections.emptyList();
         }
      } else {
         Collection<UUID> identifiers = new LinkedHashSet<>();

         for (String entry : parsedUuid.split(",")) {
            try {
               identifiers.add(UUID.fromString(entry.trim()));
            } catch (IllegalArgumentException ex) {
            }
         }

         return this.getPlugin().getBootstrap().getEntities(identifiers);
      }
   }
}
