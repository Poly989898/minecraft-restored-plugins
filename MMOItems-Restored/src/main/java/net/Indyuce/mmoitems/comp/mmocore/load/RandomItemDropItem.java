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

   public RandomItemDropItem(MMOLineConfig var1) {
      super(var1);
      this.matchClass = var1.getBoolean("match-class", false);
      this.profess = var1.getString("class", "");
      if (var1.contains("type")) {
         String var2 = var1.getString("type").toUpperCase().replace("-", "_").replace(" ", "_");
         Validate.isTrue(MMOItems.plugin.getTypes().has(var2), "Could not find item type with ID '" + var2 + "'");
         this.type = MMOItems.plugin.getTypes().get(var2);
      } else {
         this.type = null;
      }

      if (var1.contains("tierset")) {
         String var3 = UtilityMethods.enumName(var1.getString("tierset"));
         Validate.isTrue(MMOItems.plugin.getTiers().has(var3), "Could not find item tier");
         this.tier = MMOItems.plugin.getTiers().get(var3);
      } else {
         this.tier = null;
      }
   }

   public void collect(LootBuilder var1) {
      RPGPlayer var2 = PlayerData.get(var1.getEntity().getPlayer()).getRPG();
      TemplateExplorer var3 = new TemplateExplorer();
      if (this.matchClass) {
         var3.applyFilter(new ClassFilter(var2));
      } else if (!this.profess.isEmpty()) {
         var3.applyFilter(new ClassFilter(this.profess));
      }

      if (this.type != null) {
         var3.applyFilter(new TypeFilter(this.type));
      }

      if (this.tier != null) {
         var3.applyFilter(new TierFilter(this.tier.getId()));
      }

      Optional var4 = var3.rollLoot();
      if (var4.isPresent()) {
         MMOItem var5 = this.rollMMOItem((MMOItemTemplate)var4.get(), var2);
         if (this.rollSoulbound()) {
            var5.setData(ItemStats.SOULBOUND, new SoulboundData(var2.getPlayer(), 1));
         }

         ItemStack var6 = this.rollUnidentification(var5);
         var6.setAmount(this.rollAmount());
         var1.addLoot(var6);
      }
   }
}
