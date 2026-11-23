package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import org.apache.commons.lang3.text.WordUtils;

@Deprecated(
   forRemoval = true
)
public class EntityNamePlaceholder {
   protected String getName(AbstractEntity entity) {
      if (entity == null) {
         return "Unknown";
      } else if (entity.isPlayer()) {
         return entity.asPlayer().getName();
      } else {
         return entity.getName() != null ? entity.getName() : WordUtils.capitalize(entity.getBukkitEntity().getType().getName());
      }
   }
}
