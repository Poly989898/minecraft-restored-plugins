package net.Indyuce.mmocore.comp.mythicmobs.load;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.api.quest.ObjectiveProgress;
import net.Indyuce.mmocore.api.quest.QuestProgress;
import net.Indyuce.mmocore.api.quest.objective.Objective;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class KillMythicFactionObjective extends Objective {
   private final String factionName;
   private final int required;

   public KillMythicFactionObjective(ConfigurationSection section, MMOLineConfig config) {
      super(section);
      config.validate(new String[]{"amount", "name"});
      this.factionName = config.getString("name");
      this.required = config.getInt("amount");
   }

   @Override
   public ObjectiveProgress newProgress(QuestProgress questProgress) {
      return new KillMythicFactionObjective.KillFactionProgress(questProgress, this);
   }

   public class KillFactionProgress extends ObjectiveProgress implements Listener {
      private int count;

      public KillFactionProgress(QuestProgress questProgress, Objective objective) {
         super(questProgress, objective);
      }

      @EventHandler
      public void a(MythicMobDeathEvent event) {
         if (this.getQuestProgress().getPlayer().isOnline()) {
            if (event.getKiller() instanceof Player
               && event.getKiller().equals(this.getQuestProgress().getPlayer().getPlayer())
               && event.getMob().hasFaction()
               && event.getMob().getFaction().equals(KillMythicFactionObjective.this.factionName)) {
               this.count++;
               this.getQuestProgress().getPlayer().getQuestData().updateBossBar();
               if (this.count >= KillMythicFactionObjective.this.required) {
                  this.getQuestProgress().completeObjective();
               }
            }
         }
      }

      @Override
      public String formatLore(String lore) {
         return lore.replace("{left}", KillMythicFactionObjective.this.required - this.count + "");
      }
   }
}
