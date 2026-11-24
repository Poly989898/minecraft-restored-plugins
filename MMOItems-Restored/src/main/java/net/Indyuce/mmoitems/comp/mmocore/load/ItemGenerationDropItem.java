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

   public ItemGenerationDropItem(MMOLineConfig mmoLineConfig) {
      super(mmoLineConfig);
      this.level = mmoLineConfig.getInt("level", 0);
      if (mmoLineConfig.contains("tier")) {
         String text = mmoLineConfig.getString("tier").toUpperCase().replace("-", "_").replace(" ", "_");
         Validate.isTrue(MMOItems.plugin.getTiers().has(text), "Could not find item tier with ID '" + text + "'");
         this.tier = MMOItems.plugin.getTiers().get(text);
      } else {
         this.tier = null;
      }

      this.unidentified = mmoLineConfig.getDouble("unidentified", 0.0);
      this.soulbound = mmoLineConfig.getDouble("soulbound", 0.0);
   }

   @NotNull
   public MMOItem rollMMOItem(MMOItemTemplate mmoItemTemplate, RPGPlayer rpgPlayer) {
      int count = this.level > 0
         ? this.level
         : (mmoItemTemplate.hasOption(MMOItemTemplate.TemplateOption.LEVEL_ITEM) ? MMOItems.plugin.getTemplates().rollLevel(rpgPlayer.getLevel()) : 0);
      ItemTier itemTier = this.tier != null
         ? this.tier
         : (mmoItemTemplate.hasOption(MMOItemTemplate.TemplateOption.TIERED) ? MMOItems.plugin.getTemplates().rollTier() : null);
      return new MMOItemBuilder(mmoItemTemplate, count, itemTier).build();
   }

   @NotNull
   public ItemStack rollUnidentification(MMOItem mmoItem) {
      ItemStack item = mmoItem.newBuilder().build();
      if (random.nextDouble() < this.unidentified) {
         item = mmoItem.getType().getUnidentifiedTemplate().newBuilder(NBTItem.get(item)).build();
      }

      return item;
   }

   public boolean rollSoulbound() {
      return random.nextDouble() < this.soulbound;
   }
}
