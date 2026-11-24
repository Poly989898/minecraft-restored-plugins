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

   public MMOItemTrigger(MMOLineConfig mmoLineConfig) {
      super("mmoitem");
      mmoLineConfig.validate(new String[]{"type", "id"});
      Type type = MMOItems.plugin.getTypes().getOrThrow(mmoLineConfig.getString("type").toUpperCase().replace("-", "_").replace(" ", "_"));
      String text = mmoLineConfig.getString("id").replace("-", "_").toUpperCase();
      Validate.isTrue(MMOItems.plugin.getTemplates().hasTemplate(type, text), "Could not find MMOItem with ID '" + text + "'");
      this.template = MMOItems.plugin.getTemplates().getTemplate(type, text);
      this.amount = mmoLineConfig.contains("amount") ? Math.max(1, mmoLineConfig.getInt("amount")) : 1;
   }

   @Override
   public void whenCrafting(PlayerData playerData) {
      if (playerData.isOnline()) {
         ItemStack item = this.template.newBuilder(playerData.getRPG()).build().newBuilder().build();
         if (item != null && item.getType() != Material.AIR) {
            item.setAmount(this.amount);
            if (item != null && item.getType() != Material.AIR) {
               new SmartGive(playerData.getPlayer()).give(new ItemStack[]{item});
            }
         }
      }
   }
}
