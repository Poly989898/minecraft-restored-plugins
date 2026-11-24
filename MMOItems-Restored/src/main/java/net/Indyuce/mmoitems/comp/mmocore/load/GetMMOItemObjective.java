package net.Indyuce.mmoitems.comp.mmocore.load;

import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmocore.api.quest.ObjectiveProgress;
import net.Indyuce.mmocore.api.quest.QuestProgress;
import net.Indyuce.mmocore.api.quest.objective.Objective;
import net.Indyuce.mmocore.comp.citizens.CitizenInteractEvent;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GetMMOItemObjective extends Objective {
   private final Type type;
   private final String id;
   private final int required;
   private final int npcId;

   public GetMMOItemObjective(ConfigurationSection section, MMOLineConfig mmoLineConfig) {
      super(section);
      mmoLineConfig.validate(new String[]{"type", "id", "npc"});
      String text = mmoLineConfig.getString("type").toUpperCase().replace("-", "_").replace(" ", "_");
      Validate.isTrue(MMOItems.plugin.getTypes().has(text), "Could not find item type " + text);
      this.type = MMOItems.plugin.getTypes().get(text);
      this.id = mmoLineConfig.getString("id");
      this.required = mmoLineConfig.contains("amount") ? Math.max(mmoLineConfig.getInt("amount"), 1) : 1;
      this.npcId = mmoLineConfig.getInt("npc");
   }

   public ObjectiveProgress newProgress(QuestProgress questProgress) {
      return new GetMMOItemObjective.GotoProgress(questProgress, this);
   }

   public class GotoProgress extends ObjectiveProgress implements Listener {
      public GotoProgress(QuestProgress nullx, Objective nullxx) {
         super(nullx, nullxx);
      }

      @EventHandler
      public void a(CitizenInteractEvent event) {
         Player player = event.getPlayer();
         if (this.getQuestProgress().getPlayer().isOnline()) {
            if (player.equals(this.getQuestProgress().getPlayer().getPlayer())
               && event.getNPC().getId() == GetMMOItemObjective.this.npcId
               && player.getInventory().getItemInMainHand() != null) {
               NBTItem nbtItem = NBTItem.get(player.getInventory().getItemInMainHand());
               int count;
               if (nbtItem.getString("MMOITEMS_ITEM_TYPE").equals(GetMMOItemObjective.this.type.getId())
                  && nbtItem.getString("MMOITEMS_ITEM_ID").equals(GetMMOItemObjective.this.id)
                  && (count = player.getInventory().getItemInMainHand().getAmount()) >= GetMMOItemObjective.this.required) {
                  if (count <= GetMMOItemObjective.this.required) {
                     player.getInventory().setItemInMainHand(null);
                  } else {
                     player.getInventory().getItemInMainHand().setAmount(count - GetMMOItemObjective.this.required);
                  }

                  this.getQuestProgress().completeObjective();
               }
            }
         }
      }

      public String formatLore(String text) {
         return text;
      }
   }
}
