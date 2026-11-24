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
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull StringData var2) {
      String var3 = var2.toString();
      Validate.isTrue(MMOItems.plugin.getLore().hasFormat(var3), "Could not find lore format with ID '" + var3 + "'");
      var1.addItemTag(new ItemTag(this.getNBTPath(), var3));
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      Validate.isTrue(MMOItems.plugin.getLore().hasFormat(var2), "Couldn't find lore format with ID '" + var2 + "'.");
      var1.getEditedSection().set(this.getPath(), var2);
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Lore Format successfully changed to " + var2 + ".");
   }
}
