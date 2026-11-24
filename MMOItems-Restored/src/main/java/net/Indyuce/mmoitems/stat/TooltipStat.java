package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.stat.type.StringStat;
import net.Indyuce.mmoitems.tooltip.TooltipTexture;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class TooltipStat extends StringStat implements GemStoneStat {
   public TooltipStat() {
      super(
         "TOOLTIP",
         Material.BIRCH_SIGN,
         "Tooltip",
         new String[]{
            "The identifier of the custom tooltip texture", "you'd like to use. Check the wiki for usage!", "&9Tooltips are setup in the tooltips.yml file"
         },
         new String[0]
      );
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull StringData var2) {
      String var3 = UtilityMethods.enumName(var2.toString());
      TooltipTexture var4 = MMOItems.plugin.getLore().getTooltip(var3);
      Validate.notNull(var4, "Could not find tooltip with ID '" + var3 + "'");
      var1.addItemTag(new ItemTag("MMOITEMS_TOOLTIP", var4.getId()));
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      String var4 = UtilityMethods.enumName(var2);
      Validate.isTrue(MMOItems.plugin.getLore().hasTooltip(var4), "Couldn't find tooltip with ID '" + var4 + "'");
      var1.getEditedSection().set(this.getPath(), var4);
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Tier successfully changed to " + var4 + ".");
   }
}
