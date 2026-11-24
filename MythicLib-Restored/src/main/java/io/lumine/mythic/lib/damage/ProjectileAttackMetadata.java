package io.lumine.mythic.lib.damage;

import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.entity.ProjectileMetadata;
import io.lumine.mythic.lib.player.PlayerMetadata;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProjectileAttackMetadata extends AttackMetadata {
   private final Projectile projectile;
   @Nullable
   private final ProjectileMetadata projectileMetadata;

   @Deprecated
   public ProjectileAttackMetadata(DamageMetadata damage, PlayerMetadata attacker, Projectile projectile) {
      this(damage, null, attacker, projectile, null);
   }

   public ProjectileAttackMetadata(DamageMetadata damage, LivingEntity target, StatProvider attacker, Projectile projectile) {
      this(damage, target, attacker, projectile, null);
   }

   public ProjectileAttackMetadata(
      @NotNull DamageMetadata damage,
      @NotNull LivingEntity target,
      @NotNull StatProvider attacker,
      @NotNull Projectile projectile,
      @Nullable ProjectileMetadata projectileMetadata
   ) {
      super(damage, target, attacker);
      this.projectile = projectile;
      this.projectileMetadata = projectileMetadata;
   }

   public Projectile getProjectile() {
      return this.projectile;
   }

   @Nullable
   public ProjectileMetadata getProjectileMetadata() {
      return this.projectileMetadata;
   }
}
