package net.Indyuce.mmoitems.comp.mmocore.load;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.loot.LootBuilder;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.stat.data.SoulboundData;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

public class ItemTemplateDropItem extends ItemGenerationDropItem {
   private final MMOItemTemplate template;

   public ItemTemplateDropItem(MMOLineConfig var1) {
      super(var1);
      var1.validate(new String[]{"type", "id"});
      String var2 = var1.getString("type").toUpperCase().replace("-", "_").replace(" ", "_");
      Validate.isTrue(MMOItems.plugin.getTypes().has(var2), "Could not find item type with ID '" + var2 + "'");
      Type var3 = MMOItems.plugin.getTypes().get(var2);
      String var4 = var1.getString("id").replace("-", "_").toUpperCase();
      Validate.isTrue(MMOItems.plugin.getTemplates().hasTemplate(var3, var4), "Could not find MMOItem with ID '" + var4 + "'");
      this.template = MMOItems.plugin.getTemplates().getTemplate(var3, var4);
   }

   public void collect(LootBuilder var1) {
      RPGPlayer var2 = PlayerData.get(var1.getEntity().getPlayer()).getRPG();
      MMOItem var3 = this.rollMMOItem(this.template, var2);
      if (this.rollSoulbound()) {
         var3.setData(ItemStats.SOULBOUND, new SoulboundData(var2.getPlayer(), 1));
      }

      ItemStack var4 = this.rollUnidentification(var3);
      var4.setAmount(this.rollAmount());
      var1.addLoot(var4);
   }
}
