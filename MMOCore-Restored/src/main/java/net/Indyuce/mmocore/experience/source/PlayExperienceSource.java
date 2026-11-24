package net.Indyuce.mmocore.experience.source;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.Objects;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.type.SpecificExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayExperienceSource extends SpecificExperienceSource {
   private final World world;
   private final double x1;
   private final double x2;
   private final double z1;
   private final double z2;
   private final boolean inCombat;

   public PlayExperienceSource(ExperienceDispenser dispenser, MMOLineConfig config) {
      super(dispenser, config);
      this.inCombat = config.getBoolean("in-combat", false);
      this.world = config.contains("world")
         ? Objects.requireNonNull(Bukkit.getWorld(config.getString("world")), "Could not find world " + config.getString("world"))
         : null;
      if (config.contains("x1") && config.contains("x2")) {
         this.x1 = Math.min(config.getInt("x1"), config.getInt("x2"));
         this.x2 = Math.max(config.getInt("x1"), config.getInt("x2"));
      } else {
         this.x1 = Double.NEGATIVE_INFINITY;
         this.x2 = Double.POSITIVE_INFINITY;
      }

      if (config.contains("z1") && config.contains("z2")) {
         this.z1 = Math.min(config.getInt("z1"), config.getInt("z2"));
         this.z2 = Math.max(config.getInt("z1"), config.getInt("z2"));
      } else {
         this.z1 = Double.NEGATIVE_INFINITY;
         this.z2 = Double.POSITIVE_INFINITY;
      }
   }

   @Override
   public ExperienceSourceManager<PlayExperienceSource> newManager() {
      return new PlayExperienceSource.Manager();
   }

   @Override
   public boolean matchesParameter(PlayerData player, Object obj) {
      if (this.inCombat && !player.isInCombat()) {
         return false;
      }

      if (this.world == null) {
         return true;
      }

      Location location = player.getPlayer().getLocation();
      return location.getWorld().equals(this.world)
         && location.getX() > this.x1
         && location.getX() < this.x2
         && location.getZ() > this.z1
         && location.getZ() < this.z2;
   }

   private static class Manager extends ExperienceSourceManager<PlayExperienceSource> {
      public Manager() {
         (new BukkitRunnable() {
            public void run() {
               Bukkit.getOnlinePlayers().forEach(player -> {
                  if (UtilityMethods.isRealPlayer(player)) {
                     PlayerData playerData = PlayerData.get(player);

                     for (PlayExperienceSource source : Manager.this.getSources()) {
                        if (source.matchesParameter(playerData, null)) {
                           source.giveExperience(playerData, 1.0, null);
                        }
                     }
                  }
               });
            }
         }).runTaskTimer(MMOCore.plugin, 0L, 20L);
      }
   }
}
