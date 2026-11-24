package net.Indyuce.mmocore.comp.mythicmobs.load;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.type.SpecificExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class KillMythicFactionExperienceSource extends SpecificExperienceSource<String> {
   private final String factionName;

   public KillMythicFactionExperienceSource(ExperienceDispenser dispenser, MMOLineConfig config) {
      super(dispenser, config);
      config.validate(new String[]{"name"});
      this.factionName = config.getString("name");
   }

   @Override
   public ExperienceSourceManager<KillMythicFactionExperienceSource> newManager() {
      return new ExperienceSourceManager<KillMythicFactionExperienceSource>() {
         @EventHandler
         public void a(MythicMobDeathEvent event) {
            Bukkit.getScheduler().runTaskLater(MMOCore.plugin, () -> {
               if (event.getEntity().isDead()) {
                  if (event.getMob().hasFaction()) {
                     if (event.getKiller() instanceof Player && !event.getKiller().hasMetadata("NPC")) {
                        PlayerData data = PlayerData.get((Player)event.getKiller());

                        for (KillMythicFactionExperienceSource source : this.getSources()) {
                           if (source.matches(data, event.getMob().getFaction())) {
                              source.giveExperience(data, 1.0, event.getEntity().getLocation());
                           }
                        }
                     }
                  }
               }
            }, 2L);
         }
      };
   }

   public boolean matchesParameter(PlayerData player, String name) {
      return name.equals(this.factionName);
   }
}
