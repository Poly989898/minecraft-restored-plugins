package io.lumine.mythic.core.mobs;

import com.google.common.collect.Maps;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MobRegistry implements Terminable {
   private transient Map<UUID, ActiveMob> activeMobs = Maps.newConcurrentMap();

   @Override
   public void close() {
      this.activeMobs.values().removeIf(mob -> mob.getType().getDespawnMode().getDespawnsOnUnload());
      this.activeMobs.values().forEach(mob -> mob.getVariables().unload());
   }

   public boolean isActiveMob(UUID uuid) {
      ActiveMob mob = this.activeMobs.get(uuid);
      if (mob == null) {
         return false;
      } else if (mob.getType() != null) {
         return true;
      } else {
         this.activeMobs.remove(uuid);
         return false;
      }
   }

   public Optional<ActiveMob> getActiveMob(UUID uuid) {
      ActiveMob mob = this.activeMobs.get(uuid);
      if (mob == null) {
         return Optional.empty();
      } else if (mob.getType() == null) {
         this.activeMobs.remove(uuid);
         return Optional.empty();
      } else {
         return Optional.of(mob);
      }
   }

   public Collection<ActiveMob> values() {
      return this.activeMobs.values();
   }

   public void put(UUID uuid, ActiveMob mob) {
      this.activeMobs.put(uuid, mob);
   }

   public void setInactive(UUID uuid) {
      this.activeMobs.remove(uuid);
   }

   public void removeMob(UUID uuid) {
      this.activeMobs.remove(uuid);
   }

   public ActiveMob get(UUID uniqueId) {
      ActiveMob mob = this.activeMobs.get(uniqueId);
      if (mob == null) {
         return null;
      } else if (mob.getType() == null) {
         this.activeMobs.remove(uniqueId);
         return null;
      } else {
         return mob;
      }
   }
}
