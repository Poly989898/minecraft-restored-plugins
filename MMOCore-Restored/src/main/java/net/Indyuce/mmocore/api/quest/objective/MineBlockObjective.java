package net.Indyuce.mmocore.api.quest.objective;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.api.event.CustomBlockMineEvent;
import net.Indyuce.mmocore.api.quest.ObjectiveProgress;
import net.Indyuce.mmocore.api.quest.QuestProgress;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class MineBlockObjective extends Objective {
   private final Material block;
   private final int required;
   private final boolean playerPlaced;

   public MineBlockObjective(ConfigurationSection section, MMOLineConfig config) {
      super(section);
      config.validate(new String[]{"type", "amount"});
      this.block = Material.valueOf(config.getString("type").replace("-", "_").toUpperCase());
      this.required = config.getInt("amount");
      this.playerPlaced = config.getBoolean("player-placed", false);
   }

   @Override
   public ObjectiveProgress newProgress(QuestProgress questProgress) {
      return new MineBlockObjective.MineBlockProgress(questProgress, this);
   }

   public class MineBlockProgress extends ObjectiveProgress implements Listener {
      private int count;

      public MineBlockProgress(QuestProgress questProgress, Objective objective) {
         super(questProgress, objective);
      }

      @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
      public void a(BlockBreakEvent event) {
         if (this.getQuestProgress().getPlayer().isOnline()) {
            if (!UtilityMethods.isFake(event)) {
               if (MineBlockObjective.this.playerPlaced || !event.getBlock().hasMetadata("player_placed")) {
                  if (event.getPlayer().equals(this.getQuestProgress().getPlayer().getPlayer()) && event.getBlock().getType() == MineBlockObjective.this.block) {
                     this.count++;
                     this.getQuestProgress().getPlayer().getQuestData().updateBossBar();
                     if (this.count >= MineBlockObjective.this.required) {
                        this.getQuestProgress().completeObjective();
                     }
                  }
               }
            }
         }
      }

      @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
      public void b(CustomBlockMineEvent event) {
         if (this.getQuestProgress().getPlayer().isOnline()) {
            if (event.getPlayer().equals(this.getQuestProgress().getPlayer().getPlayer()) && event.getBlock().getType() == MineBlockObjective.this.block) {
               this.count++;
               this.getQuestProgress().getPlayer().getQuestData().updateBossBar();
               if (this.count >= MineBlockObjective.this.required) {
                  this.getQuestProgress().completeObjective();
               }
            }
         }
      }

      @Override
      public String formatLore(String lore) {
         return lore.replace("{left}", MineBlockObjective.this.required - this.count + "");
      }
   }
}
