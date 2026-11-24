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

   public MMOItemTrigger(MMOLineConfig mmoLineConfig) {
      super(mmoLineConfig);
      mmoLineConfig.validate(new String[]{"type", "id"});
      String text = mmoLineConfig.getString("type").toUpperCase().replace("-", "_").replace(" ", "_");
      Validate.isTrue(MMOItems.plugin.getTypes().has(text), "Could not find item type with ID '" + text + "'");
      Type type = MMOItems.plugin.getTypes().get(text);
      String text2 = mmoLineConfig.getString("id").replace("-", "_").toUpperCase();
      Validate.isTrue(MMOItems.plugin.getTemplates().hasTemplate(type, text2), "Could not find MMOItem with ID '" + text2 + "'");
      this.template = MMOItems.plugin.getTemplates().getTemplate(type, text2);
      this.amount = Math.max(1, mmoLineConfig.getInt("amount", 1));
   }

   public void apply(PlayerData playerData) {
      ItemStack item = this.template.newBuilder(net.Indyuce.mmoitems.api.player.PlayerData.get(playerData.getPlayer()).getRPG()).build().newBuilder().build();
      item.setAmount(this.amount);
      new SmartGive(playerData.getPlayer()).give(new ItemStack[]{item});
   }
}
