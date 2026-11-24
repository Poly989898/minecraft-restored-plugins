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

   public GetMMOItemObjective(ConfigurationSection var1, MMOLineConfig var2) {
      super(var1);
      var2.validate(new String[]{"type", "id", "npc"});
      String var3 = var2.getString("type").toUpperCase().replace("-", "_").replace(" ", "_");
      Validate.isTrue(MMOItems.plugin.getTypes().has(var3), "Could not find item type " + var3);
      this.type = MMOItems.plugin.getTypes().get(var3);
      this.id = var2.getString("id");
      this.required = var2.contains("amount") ? Math.max(var2.getInt("amount"), 1) : 1;
      this.npcId = var2.getInt("npc");
   }

   public ObjectiveProgress newProgress(QuestProgress var1) {
      return new GetMMOItemObjective.GotoProgress(var1, this);
   }

   public class GotoProgress extends ObjectiveProgress implements Listener {
      public GotoProgress(QuestProgress nullx, Objective nullxx) {
         super(nullx, nullxx);
      }

      @EventHandler
      public void a(CitizenInteractEvent var1) {
         Player var2 = var1.getPlayer();
         if (this.getQuestProgress().getPlayer().isOnline()) {
            if (var2.equals(this.getQuestProgress().getPlayer().getPlayer())
               && var1.getNPC().getId() == GetMMOItemObjective.this.npcId
               && var2.getInventory().getItemInMainHand() != null) {
               NBTItem var3 = NBTItem.get(var2.getInventory().getItemInMainHand());
               int var4;
               if (var3.getString("MMOITEMS_ITEM_TYPE").equals(GetMMOItemObjective.this.type.getId())
                  && var3.getString("MMOITEMS_ITEM_ID").equals(GetMMOItemObjective.this.id)
                  && (var4 = var2.getInventory().getItemInMainHand().getAmount()) >= GetMMOItemObjective.this.required) {
                  if (var4 <= GetMMOItemObjective.this.required) {
                     var2.getInventory().setItemInMainHand(null);
                  } else {
                     var2.getInventory().getItemInMainHand().setAmount(var4 - GetMMOItemObjective.this.required);
                  }

                  this.getQuestProgress().completeObjective();
               }
            }
         }
      }

      public String formatLore(String var1) {
         return var1;
      }
   }
}
