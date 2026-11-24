package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.MythicLib;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StringListData;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.stat.type.StringListStat;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class Lore extends StringListStat implements GemStoneStat {
   public Lore() {
      super("LORE", Material.WRITABLE_BOOK, "Lore", new String[]{"The item lore."}, new String[0]);
   }

   @Override
   public StringListData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof List, "Must specify a string list");
      return new StringListData((List<String>)var1);
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull StringListData var2) {
      var1.getLore().insert("lore", var2.getList());
      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(var1, ItemStats.LORE).enable("Write in the chat the lore line you want to add.");
      }

      if (var2.getAction() == InventoryAction.PICKUP_HALF && var1.getEditedSection().contains("lore")) {
         List var3 = var1.getEditedSection().getStringList("lore");
         if (var3.isEmpty()) {
            return;
         }

         String var4 = (String)var3.get(var3.size() - 1);
         var3.remove(var4);
         var1.getEditedSection().set("lore", var3.isEmpty() ? null : var3);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed '" + MythicLib.plugin.parseColors(var4) + ChatColor.GRAY + "'.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      List var4 = var1.getEditedSection().contains("lore") ? var1.getEditedSection().getStringList("lore") : new ArrayList();
      var4.add(var2);
      var1.getEditedSection().set("lore", var4);
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Lore successfully added.");
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<StringListData> var2) {
      var2.ifPresentOrElse(var1x -> {
         var1.add(ChatColor.GRAY + "Current Value:");
         var1.addAll(MythicLib.plugin.parseColors(var1x.getList().stream().map(var0x -> ChatColor.GRAY + var0x).toList()));
      }, () -> var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None"));
      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Click to add a line.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove the last line.");
   }
}
