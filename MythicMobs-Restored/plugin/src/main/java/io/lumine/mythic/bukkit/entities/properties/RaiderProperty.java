package io.lumine.mythic.bukkit.entities.properties;

import io.lumine.mythic.api.config.MythicConfig;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Raider;

@Getter
public class RaiderProperty implements EntityPropertySet {
   private boolean canJoinRaid = true;
   private boolean patrolLeader = false;
   private boolean patrolSpawnPoint = false;

   public RaiderProperty(MythicConfig mc) {
      if (mc.isSet("Options.CanJoinRaid")) {
         this.canJoinRaid = mc.getBoolean("Options.CanJoinRaid", true);
      }

      if (mc.isSet("Options.PatrolLeader")) {
         this.patrolLeader = mc.getBoolean("Options.PatrolLeader", false);
      }

      if (mc.isSet("Options.PatrolSpawnPoint")) {
         this.patrolSpawnPoint = mc.getBoolean("Options.PatrolSpawnPoint", false);
      }
   }

   @Override
   public Entity applyProperties(Entity entity) {
      Raider raider = (Raider)entity;
      raider.setCanJoinRaid(this.canJoinRaid);
      raider.setPatrolLeader(this.patrolLeader);
      raider.setPatrolTarget(entity.getLocation().getBlock());
      return raider;
   }
}
