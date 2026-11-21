package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.IntegerPlaceholder;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "spawner.pir"
)
public class SpawnerPirPlaceholder extends GenericPlaceholder<Integer> implements IntegerPlaceholder {
   public SpawnerPirPlaceholder(GenericPlaceholder.GenericPlaceholderArguments arguments) {
      super(arguments);
   }

   @Nullable
   public Integer applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      MythicSpawner spawner = placeholderContext.spawner();
      if (spawner == null) {
         return null;
      } else {
         double radius = spawner.getScalingRange();
         return this.manager
            .getPlugin()
            .getVolatileCodeHandler()
            .getWorldHandler()
            .getEntitiesNearLocation(spawner.getLocation(), radius, AbstractEntity::isPlayer)
            .size();
      }
   }
}
