package net.Indyuce.mmoitems.comp.mmocore.load;

import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.util.SmartGive;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.quest.trigger.Trigger;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

public class MMOItemTrigger extends Trigger {
   private final MMOItemTemplate template;
   private final int amount;

   public MMOItemTrigger(MMOLineConfig var1) {
      super(var1);
      var1.validate(new String[]{"type", "id"});
      String var2 = var1.getString("type").toUpperCase().replace("-", "_").replace(" ", "_");
      Validate.isTrue(MMOItems.plugin.getTypes().has(var2), "Could not find item type with ID '" + var2 + "'");
      Type var3 = MMOItems.plugin.getTypes().get(var2);
      String var4 = var1.getString("id").replace("-", "_").toUpperCase();
      Validate.isTrue(MMOItems.plugin.getTemplates().hasTemplate(var3, var4), "Could not find MMOItem with ID '" + var4 + "'");
      this.template = MMOItems.plugin.getTemplates().getTemplate(var3, var4);
      this.amount = Math.max(1, var1.getInt("amount", 1));
   }

   public void apply(PlayerData var1) {
      ItemStack var2 = this.template.newBuilder(net.Indyuce.mmoitems.api.player.PlayerData.get(var1.getPlayer()).getRPG()).build().newBuilder().build();
      var2.setAmount(this.amount);
      new SmartGive(var1.getPlayer()).give(new ItemStack[]{var2});
   }
}
