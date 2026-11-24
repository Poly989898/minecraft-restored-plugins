package net.Indyuce.mmoitems.comp.mmocore.load;

import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmocore.loot.droptable.dropitem.DropItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ItemGenerationDropItem extends DropItem {
   protected final int level;
   protected final ItemTier tier;
   private final double unidentified;
   private final double soulbound;

   public ItemGenerationDropItem(MMOLineConfig var1) {
      super(var1);
      this.level = var1.getInt("level", 0);
      if (var1.contains("tier")) {
         String var2 = var1.getString("tier").toUpperCase().replace("-", "_").replace(" ", "_");
         Validate.isTrue(MMOItems.plugin.getTiers().has(var2), "Could not find item tier with ID '" + var2 + "'");
         this.tier = MMOItems.plugin.getTiers().get(var2);
      } else {
         this.tier = null;
      }

      this.unidentified = var1.getDouble("unidentified", 0.0);
      this.soulbound = var1.getDouble("soulbound", 0.0);
   }

   @NotNull
   public MMOItem rollMMOItem(MMOItemTemplate var1, RPGPlayer var2) {
      int var3 = this.level > 0
         ? this.level
         : (var1.hasOption(MMOItemTemplate.TemplateOption.LEVEL_ITEM) ? MMOItems.plugin.getTemplates().rollLevel(var2.getLevel()) : 0);
      ItemTier var4 = this.tier != null
         ? this.tier
         : (var1.hasOption(MMOItemTemplate.TemplateOption.TIERED) ? MMOItems.plugin.getTemplates().rollTier() : null);
      return new MMOItemBuilder(var1, var3, var4).build();
   }

   @NotNull
   public ItemStack rollUnidentification(MMOItem var1) {
      ItemStack var2 = var1.newBuilder().build();
      if (random.nextDouble() < this.unidentified) {
         var2 = var1.getType().getUnidentifiedTemplate().newBuilder(NBTItem.get(var2)).build();
      }

      return var2;
   }

   public boolean rollSoulbound() {
      return random.nextDouble() < this.soulbound;
   }
}
