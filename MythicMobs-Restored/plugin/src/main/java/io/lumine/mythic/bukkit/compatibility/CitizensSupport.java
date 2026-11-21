package io.lumine.mythic.bukkit.compatibility;

import io.lumine.mythic.api.adapters.AbstractEntity;

public class CitizensSupport {
   public boolean isCitizensNPC(AbstractEntity entity) {
      return entity.getBukkitEntity().hasMetadata("NPC");
   }
}
