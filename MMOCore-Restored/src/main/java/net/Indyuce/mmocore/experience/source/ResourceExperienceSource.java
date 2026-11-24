package net.Indyuce.mmocore.experience.source;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.api.event.PlayerResourceUpdateEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.resource.PlayerResource;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.type.SpecificExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;
import org.apache.commons.lang.Validate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class ResourceExperienceSource extends SpecificExperienceSource<PlayerResource> {
   private final PlayerResource resource;

   public ResourceExperienceSource(ExperienceDispenser dispenser, MMOLineConfig config) {
      super(dispenser, config);
      if (!config.contains("type")) {
         this.resource = null;
      } else {
         String str = config.getString("type").toUpperCase().replace("-", "_");
         Validate.isTrue(
            str.equals("MANA") || str.equals("STELLIUM") || str.equals("STAMINA"),
            "ResourceExperienceSource problem: The resource can only be mana, stamina or STELLIUM"
         );
         this.resource = PlayerResource.valueOf(str);
      }
   }

   @Override
   public ExperienceSourceManager<ResourceExperienceSource> newManager() {
      return new ResourceExperienceSource.Manager();
   }

   public boolean matchesParameter(PlayerData player, PlayerResource obj) {
      return this.resource == null ? !obj.equals(PlayerResource.HEALTH) : this.resource.equals(obj);
   }

   private static class Manager extends ExperienceSourceManager<ResourceExperienceSource> {
      @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
      public void onResource(PlayerResourceUpdateEvent event) {
         if (UtilityMethods.isRealPlayer(event.getPlayer())) {
            PlayerData playerData = PlayerData.get(event.getPlayer());
            if (!(event.getAmount() >= 0.0)) {
               for (ResourceExperienceSource source : this.getSources()) {
                  if (source.matchesParameter(playerData, event.getResource())) {
                     source.giveExperience(playerData, -event.getAmount(), null);
                  }
               }
            }
         }
      }
   }
}
