package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.stat.type.StringStat;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ItemTierStat extends StringStat implements GemStoneStat {
   public ItemTierStat() {
      super(
         "TIER",
         Material.DIAMOND,
         "Item Tier",
         new String[]{
            "The tier defines how rare your item is",
            "and what item is dropped when your",
            "item is deconstructed.",
            "&9Tiers can be configured in the tiers.yml file"
         },
         new String[0]
      );
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull StringData var2) {
      String var3 = var2.toString().toUpperCase().replace("-", "_").replace(" ", "_");
      Validate.isTrue(MMOItems.plugin.getTiers().has(var3), "Could not find item tier with ID '" + var3 + "'");
      ItemTier var4 = MMOItems.plugin.getTiers().get(var3);
      var1.addItemTag(new ItemTag("MMOITEMS_TIER", var3));
      var1.getLore().insert(this.getPath(), this.getGeneralStatFormat().replace("{value}", var4.getName()));
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      String var4 = var2.toUpperCase().replace(" ", "_").replace("-", "_");
      Validate.isTrue(MMOItems.plugin.getTiers().has(var4), "Couldn't find the tier called '" + var4 + "'.");
      var1.getEditedSection().set(this.getPath(), var4);
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Tier successfully changed to " + var4 + ".");
   }
}
