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
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull StringData stringData) {
      String text = stringData.toString().toUpperCase().replace("-", "_").replace(" ", "_");
      Validate.isTrue(MMOItems.plugin.getTiers().has(text), "Could not find item tier with ID '" + text + "'");
      ItemTier itemTier = MMOItems.plugin.getTiers().get(text);
      itemStackBuilder.addItemTag(new ItemTag("MMOITEMS_TIER", text));
      itemStackBuilder.getLore().insert(this.getPath(), this.getGeneralStatFormat().replace("{value}", itemTier.getName()));
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      String text2 = text.toUpperCase().replace(" ", "_").replace("-", "_");
      Validate.isTrue(MMOItems.plugin.getTiers().has(text2), "Couldn't find the tier called '" + text2 + "'.");
      editionInventory.getEditedSection().set(this.getPath(), text2);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Tier successfully changed to " + text2 + ".");
   }
}
