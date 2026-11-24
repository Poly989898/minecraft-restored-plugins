package io.lumine.mythic.lib.damage;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public interface AttackHandler {
   @Nullable
   default AttackMetadata getAttack(EntityDamageEvent event) {
      return this.getAttack(event.getEntity());
   }

   @Deprecated
   @Nullable
   default AttackMetadata getAttack(Entity entity) {
      throw new RuntimeException("Unsupported operation");
   }

   @Deprecated
   default boolean isAttacked(Entity entity) {
      throw new RuntimeException("Unsupported operation");
   }

   @Deprecated
   default boolean isFake(EntityDamageEvent event) {
      return false;
   }
}
