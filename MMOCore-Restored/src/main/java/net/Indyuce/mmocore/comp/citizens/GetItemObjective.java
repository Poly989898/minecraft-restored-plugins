package net.Indyuce.mmocore.comp.citizens;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.api.quest.ObjectiveProgress;
import net.Indyuce.mmocore.api.quest.QuestProgress;
import net.Indyuce.mmocore.api.quest.objective.Objective;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class GetItemObjective extends Objective {
   private final Material material;
   private final int required;
   private final int npcId;

   public GetItemObjective(ConfigurationSection section, MMOLineConfig config) {
      super(section);
      config.validate(new String[]{"type", "amount", "npc"});
      this.material = Material.valueOf(config.getString("type").toUpperCase().replace("-", "_"));
      this.required = config.getInt("amount");
      this.npcId = config.getInt("npc");
   }

   @Override
   public ObjectiveProgress newProgress(QuestProgress questProgress) {
      return new GetItemObjective.GotoProgress(questProgress, this);
   }

   public class GotoProgress extends ObjectiveProgress implements Listener {
      public GotoProgress(QuestProgress questProgress, Objective objective) {
         super(questProgress, objective);
      }

      @EventHandler
      public void a(CitizenInteractEvent event) {
         Player player = event.getPlayer();
         if (this.getQuestProgress().getPlayer().isOnline()) {
            if (player.equals(this.getQuestProgress().getPlayer().getPlayer())
               && event.getNPC().getId() == GetItemObjective.this.npcId
               && player.getInventory().getItemInMainHand() != null) {
               ItemStack item = player.getInventory().getItemInMainHand();
               if (item.getType() == GetItemObjective.this.material && item.getAmount() >= GetItemObjective.this.required) {
                  item.setAmount(item.getAmount() - GetItemObjective.this.required);
                  this.getQuestProgress().completeObjective();
               }
            }
         }
      }

      @Override
      public String formatLore(String lore) {
         return lore;
      }
   }
}
