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

public class KillMythicMobObjective extends Objective {
   private final String internalName;
   private final int required;

   public KillMythicMobObjective(ConfigurationSection section, MMOLineConfig config) {
      super(section);
      config.validate(new String[]{"amount", "name"});
      this.internalName = config.getString("name");
      this.required = config.getInt("amount");
   }

   @Override
   public ObjectiveProgress newProgress(QuestProgress questProgress) {
      return new KillMythicMobObjective.KillMobProgress(questProgress, this);
   }

   public class KillMobProgress extends ObjectiveProgress implements Listener {
      private int count;

      public KillMobProgress(QuestProgress questProgress, Objective objective) {
         super(questProgress, objective);
      }

      @EventHandler
      public void a(MythicMobDeathEvent event) {
         if (this.getQuestProgress().getPlayer().isOnline()) {
            if (event.getKiller() instanceof Player
               && event.getKiller().equals(this.getQuestProgress().getPlayer().getPlayer())
               && event.getMob().getType().getInternalName().equals(KillMythicMobObjective.this.internalName)) {
               this.count++;
               this.getQuestProgress().getPlayer().getQuestData().updateBossBar();
               if (this.count >= KillMythicMobObjective.this.required) {
                  this.getQuestProgress().completeObjective();
               }
            }
         }
      }

      @Override
      public String formatLore(String lore) {
         return lore.replace("{left}", KillMythicMobObjective.this.required - this.count + "");
      }
   }
}
