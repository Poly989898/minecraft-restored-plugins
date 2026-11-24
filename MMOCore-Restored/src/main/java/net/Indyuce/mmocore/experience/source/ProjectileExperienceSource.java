package net.Indyuce.mmocore.experience.source;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.util.FlushableRegistry;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.type.SpecificExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ProjectileExperienceSource extends SpecificExperienceSource<Projectile> {
   private final ProjectileExperienceSource.ProjectileType projectileType;

   public ProjectileExperienceSource(ExperienceDispenser dispenser, MMOLineConfig config) {
      super(dispenser, config);
      if (!config.contains("type")) {
         this.projectileType = null;
      } else {
         String str = config.getString("type").toUpperCase().replace("-", "_");
         Validate.isTrue(Arrays.stream(ProjectileExperienceSource.ProjectileType.values()).map(Enum::toString).collect(Collectors.toList()).contains(str));
         this.projectileType = ProjectileExperienceSource.ProjectileType.valueOf(str);
      }
   }

   @Override
   public ExperienceSourceManager<ProjectileExperienceSource> newManager() {
      return new ProjectileExperienceSource.Manager();
   }

   public boolean matchesParameter(PlayerData player, Projectile projectile) {
      return this.projectileType == null ? true : this.projectileType.matches(projectile);
   }

   private static class Manager extends ExperienceSourceManager<ProjectileExperienceSource> {
      private final FlushableRegistry<Projectile, Location> projectiles = new FlushableRegistry<>((proj, loc) -> proj.isDead(), 1200L);

      @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
      public void onHit(ProjectileHitEvent e) {
         if (e.getHitBlock() != null) {
            this.projectiles.getRegistry().remove(e.getEntity());
         }
      }

      @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
      public void onDamage(EntityDamageByEntityEvent e) {
         if (e.getEntity() instanceof Projectile) {
            Projectile projectile = (Projectile)e.getEntity();
            Location loc = (Location)this.projectiles.getRegistry().get(projectile);
            if (loc == null) {
               return;
            }

            if (projectile.getShooter() instanceof Player && !((Player)projectile.getShooter()).hasMetadata("NPC")) {
               Player player = (Player)projectile.getShooter();
               PlayerData playerData = PlayerData.get(player);
               double distance = loc.distance(e.getEntity().getLocation());

               for (ProjectileExperienceSource source : this.getSources()) {
                  if (source.matchesParameter(playerData, projectile)) {
                     source.giveExperience(playerData, e.getFinalDamage() * distance, null);
                  }
               }
            }
         }
      }

      @EventHandler
      public void onLaunch(ProjectileLaunchEvent e) {
         if (e.getEntity().getShooter() instanceof Player && UtilityMethods.isRealPlayer((Player)e.getEntity().getShooter())) {
            this.projectiles.getRegistry().put(e.getEntity(), e.getLocation());
         }
      }
   }

   public enum ProjectileType {
      ARROW(p -> p instanceof Arrow),
      TRIDENT(p -> p instanceof Trident);

      private final Function<Projectile, Boolean> matching;

      ProjectileType(Function<Projectile, Boolean> matching) {
         this.matching = matching;
      }

      public boolean matches(Projectile projectile) {
         return this.matching.apply(projectile);
      }
   }
}
