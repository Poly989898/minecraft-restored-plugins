package io.lumine.mythic.api.mobs.entities;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicConfig;
import java.util.function.Consumer;
import org.bukkit.entity.Entity;

public abstract class MythicEntity {
   public void instantiate(MythicConfig mc) {
   }

   public abstract AbstractEntity spawn(AbstractLocation abstractLocation, SpawnReason spawnReason);

   public abstract AbstractEntity spawn(AbstractLocation abstractLocation, SpawnReason spawnReason, Consumer<Entity> consumer);

   public abstract AbstractEntity spawnBaseEntity(AbstractLocation abstractLocation, SpawnReason spawnReason);

   public int getHeight() {
      return 2;
   }

   public abstract String getHeadString();
}
