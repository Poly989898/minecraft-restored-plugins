package io.lumine.mythic.bukkit.utils;

import java.util.Optional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;

public class Entities {
   public static Optional<LivingEntity> getEntitySource(Entity source) {
      if (source instanceof LivingEntity le) {
         return Optional.of(le);
      } else {
         return source instanceof Projectile projectile && projectile.getShooter() != null && projectile.getShooter() instanceof LivingEntity le
            ? Optional.of(le)
            : Optional.empty();
      }
   }
}
