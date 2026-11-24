package net.Indyuce.mmoitems.comp.mmocore.load;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.Optional;
import net.Indyuce.mmocore.loot.LootBuilder;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.item.template.explorer.ClassFilter;
import net.Indyuce.mmoitems.api.item.template.explorer.TemplateExplorer;
import net.Indyuce.mmoitems.api.item.template.explorer.TierFilter;
import net.Indyuce.mmoitems.api.item.template.explorer.TypeFilter;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.stat.data.SoulboundData;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

public class RandomItemDropItem extends ItemGenerationDropItem {
   private final boolean matchClass;
   private final String profess;
   private final Type type;
   private final ItemTier tier;

   public RandomItemDropItem(MMOLineConfig mmoLineConfig) {
      super(mmoLineConfig);
      this.matchClass = mmoLineConfig.getBoolean("match-class", false);
      this.profess = mmoLineConfig.getString("class", "");
      if (mmoLineConfig.contains("type")) {
         String text = mmoLineConfig.getString("type").toUpperCase().replace("-", "_").replace(" ", "_");
         Validate.isTrue(MMOItems.plugin.getTypes().has(text), "Could not find item type with ID '" + text + "'");
         this.type = MMOItems.plugin.getTypes().get(text);
      } else {
         this.type = null;
      }

      if (mmoLineConfig.contains("tierset")) {
         String text = UtilityMethods.enumName(mmoLineConfig.getString("tierset"));
         Validate.isTrue(MMOItems.plugin.getTiers().has(text), "Could not find item tier");
         this.tier = MMOItems.plugin.getTiers().get(text);
      } else {
         this.tier = null;
      }
   }

   public void collect(LootBuilder lootBuilder) {
      RPGPlayer rPG = PlayerData.get(lootBuilder.getEntity().getPlayer()).getRPG();
      TemplateExplorer templateExplorer = new TemplateExplorer();
      if (this.matchClass) {
         templateExplorer.applyFilter(new ClassFilter(rPG));
      } else if (!this.profess.isEmpty()) {
         templateExplorer.applyFilter(new ClassFilter(this.profess));
      }

      if (this.type != null) {
         templateExplorer.applyFilter(new TypeFilter(this.type));
      }

      if (this.tier != null) {
         templateExplorer.applyFilter(new TierFilter(this.tier.getId()));
      }

      Optional optional = templateExplorer.rollLoot();
      if (optional.isPresent()) {
         MMOItem mmoItem = this.rollMMOItem((MMOItemTemplate)optional.get(), rPG);
         if (this.rollSoulbound()) {
            mmoItem.setData(ItemStats.SOULBOUND, new SoulboundData(rPG.getPlayer(), 1));
         }

         ItemStack item = this.rollUnidentification(mmoItem);
         item.setAmount(this.rollAmount());
         lootBuilder.addLoot(item);
      }
   }
}
