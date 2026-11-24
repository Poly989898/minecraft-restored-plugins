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

   public ItemTemplateDropItem(MMOLineConfig mmoLineConfig) {
      super(mmoLineConfig);
      mmoLineConfig.validate(new String[]{"type", "id"});
      String text = mmoLineConfig.getString("type").toUpperCase().replace("-", "_").replace(" ", "_");
      Validate.isTrue(MMOItems.plugin.getTypes().has(text), "Could not find item type with ID '" + text + "'");
      Type type = MMOItems.plugin.getTypes().get(text);
      String text2 = mmoLineConfig.getString("id").replace("-", "_").toUpperCase();
      Validate.isTrue(MMOItems.plugin.getTemplates().hasTemplate(type, text2), "Could not find MMOItem with ID '" + text2 + "'");
      this.template = MMOItems.plugin.getTemplates().getTemplate(type, text2);
   }

   public void collect(LootBuilder lootBuilder) {
      RPGPlayer rPG = PlayerData.get(lootBuilder.getEntity().getPlayer()).getRPG();
      MMOItem mmoItem = this.rollMMOItem(this.template, rPG);
      if (this.rollSoulbound()) {
         mmoItem.setData(ItemStats.SOULBOUND, new SoulboundData(rPG.getPlayer(), 1));
      }

      ItemStack item = this.rollUnidentification(mmoItem);
      item.setAmount(this.rollAmount());
      lootBuilder.addLoot(item);
   }
}
