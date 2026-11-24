package net.Indyuce.mmoitems.api.crafting.trigger;

import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.util.SmartGive;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MMOItemTrigger extends Trigger {
   private final MMOItemTemplate template;
   private final int amount;

   public MMOItemTrigger(MMOLineConfig var1) {
      super("mmoitem");
      var1.validate(new String[]{"type", "id"});
      Type var2 = MMOItems.plugin.getTypes().getOrThrow(var1.getString("type").toUpperCase().replace("-", "_").replace(" ", "_"));
      String var3 = var1.getString("id").replace("-", "_").toUpperCase();
      Validate.isTrue(MMOItems.plugin.getTemplates().hasTemplate(var2, var3), "Could not find MMOItem with ID '" + var3 + "'");
      this.template = MMOItems.plugin.getTemplates().getTemplate(var2, var3);
      this.amount = var1.contains("amount") ? Math.max(1, var1.getInt("amount")) : 1;
   }

   @Override
   public void whenCrafting(PlayerData var1) {
      if (var1.isOnline()) {
         ItemStack var2 = this.template.newBuilder(var1.getRPG()).build().newBuilder().build();
         if (var2 != null && var2.getType() != Material.AIR) {
            var2.setAmount(this.amount);
            if (var2 != null && var2.getType() != Material.AIR) {
               new SmartGive(var1.getPlayer()).give(new ItemStack[]{var2});
            }
         }
      }
   }
}
