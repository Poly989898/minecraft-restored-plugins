package net.Indyuce.mmocore.api.quest.objective;

import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.event.PlayerKillEntityEvent;
import net.Indyuce.mmocore.api.quest.ObjectiveProgress;
import net.Indyuce.mmocore.api.quest.QuestProgress;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class KillMobObjective extends Objective {
   private final EntityType type;
   private final int required;

   public KillMobObjective(ConfigurationSection section, MMOLineConfig config) {
      super(section);
      config.validate(new String[]{"amount", "type"});
      this.type = EntityType.valueOf(config.getString("type"));
      this.required = config.getInt("amount");
   }

   @Override
   public ObjectiveProgress newProgress(QuestProgress questProgress) {
      return new KillMobObjective.KillMobProgress(questProgress, this);
   }

   public class KillMobProgress extends ObjectiveProgress implements Listener {
      private int count;

      public KillMobProgress(QuestProgress questProgress, Objective objective) {
         super(questProgress, objective);
      }

      @EventHandler
      public void a(PlayerKillEntityEvent event) {
         if (this.getPlayer().isOnline()) {
            if (event.getTarget().getType() == KillMobObjective.this.type && event.getPlayer().equals(this.getPlayer().getPlayer())) {
               this.count++;
               this.getQuestProgress().getPlayer().getQuestData().updateBossBar();
               if (this.count >= KillMobObjective.this.required) {
                  this.getQuestProgress().completeObjective();
               }
            }
         }
      }

      @Override
      public String formatLore(String lore) {
         return lore.replace("{left}", KillMobObjective.this.required - this.count + "");
      }
   }
}
