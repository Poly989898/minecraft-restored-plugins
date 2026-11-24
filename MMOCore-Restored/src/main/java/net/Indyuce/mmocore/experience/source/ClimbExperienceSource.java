package net.Indyuce.mmocore.experience.source;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.type.SpecificExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

public class ClimbExperienceSource extends SpecificExperienceSource<Material> {
   private final Material type;

   public ClimbExperienceSource(ExperienceDispenser dispenser, MMOLineConfig config) {
      super(dispenser, config);
      if (!config.contains("type")) {
         this.type = null;
      } else {
         String str = config.getString("type").toUpperCase().replace("-", "_");
         Validate.isTrue(
            str.equals("LADDER") || str.equals("VINE") || str.equals("TWISTING_VINES") || str.equals("WEEPING_VINES"),
            "ClimbExperienceSource problem: The type must be ladder, vine, twisting-vines or weeping-vines"
         );
         this.type = Material.valueOf(str);
      }
   }

   @Override
   public ExperienceSourceManager<ClimbExperienceSource> newManager() {
      return new ClimbExperienceSource.Manager();
   }

   public boolean matchesParameter(PlayerData player, Material material) {
      if (this.type == null) {
         return material.equals(Material.LADDER)
            || material.equals(Material.VINE)
            || material.equals(Material.WEEPING_VINES)
            || material.equals(Material.TWISTING_VINES)
            || material.equals(Material.WEEPING_VINES_PLANT)
            || material.equals(Material.TWISTING_VINES_PLANT);
      } else if (this.type.equals(Material.WEEPING_VINES)) {
         return material.equals(Material.WEEPING_VINES) || material.equals(Material.WEEPING_VINES_PLANT);
      } else {
         return !this.type.equals(Material.TWISTING_VINES)
            ? material.equals(this.type)
            : material.equals(Material.TWISTING_VINES) || material.equals(Material.TWISTING_VINES_PLANT);
      }
   }

   private static class Manager extends ExperienceSourceManager<ClimbExperienceSource> {
      @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
      public void onClimb(PlayerMoveEvent e) {
         double delta = e.getTo().getBlockY() - e.getFrom().getBlockY();
         if (delta > 0.0) {
            if (e.getPlayer().hasMetadata("NPC")) {
               return;
            }

            PlayerData playerData = PlayerData.get(e.getPlayer());

            for (ClimbExperienceSource source : this.getSources()) {
               if (source.matchesParameter(playerData, e.getFrom().getBlock().getType())) {
                  source.giveExperience(playerData, delta, null);
               }
            }
         }
      }
   }
}
