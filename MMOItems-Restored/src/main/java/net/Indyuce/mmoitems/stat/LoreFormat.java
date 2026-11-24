package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.stat.type.StringStat;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class LoreFormat extends StringStat implements GemStoneStat {
   public LoreFormat() {
      super(
         "LORE_FORMAT",
         Material.MAP,
         "Lore Format",
         new String[]{"The lore format decides", "where each stat goes.", "&9Formats can be configured in", "&9the lore-formats folder"},
         new String[0]
      );
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull StringData stringData) {
      String text = stringData.toString();
      Validate.isTrue(MMOItems.plugin.getLore().hasFormat(text), "Could not find lore format with ID '" + text + "'");
      itemStackBuilder.addItemTag(new ItemTag(this.getNBTPath(), text));
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      Validate.isTrue(MMOItems.plugin.getLore().hasFormat(text), "Couldn't find lore format with ID '" + text + "'.");
      editionInventory.getEditedSection().set(this.getPath(), text);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Lore Format successfully changed to " + text + ".");
   }
}
