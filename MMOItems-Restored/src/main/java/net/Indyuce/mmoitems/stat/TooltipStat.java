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
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull StringData stringData) {
      String text = UtilityMethods.enumName(stringData.toString());
      TooltipTexture tooltip = MMOItems.plugin.getLore().getTooltip(text);
      Validate.notNull(tooltip, "Could not find tooltip with ID '" + text + "'");
      itemStackBuilder.addItemTag(new ItemTag("MMOITEMS_TOOLTIP", tooltip.getId()));
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      String tooltipName = UtilityMethods.enumName(text);
      Validate.isTrue(MMOItems.plugin.getLore().hasTooltip(tooltipName), "Couldn't find tooltip with ID '" + tooltipName + "'");
      editionInventory.getEditedSection().set(this.getPath(), tooltipName);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Tier successfully changed to " + tooltipName + ".");
   }
}
