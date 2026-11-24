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
   public StringListData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof List, "Must specify a string list");
      return new StringListData((List<String>)obj);
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull StringListData stringListData) {
      itemStackBuilder.getLore().insert("lore", stringListData.getList());
      itemStackBuilder.addItemTag(this.getAppliedNBT(stringListData));
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(editionInventory, ItemStats.LORE).enable("Write in the chat the lore line you want to add.");
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF && editionInventory.getEditedSection().contains("lore")) {
         List lore = editionInventory.getEditedSection().getStringList("lore");
         if (lore.isEmpty()) {
            return;
         }

         String text = (String)lore.get(lore.size() - 1);
         lore.remove(text);
         editionInventory.getEditedSection().set("lore", lore.isEmpty() ? null : lore);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed '" + MythicLib.plugin.parseColors(text) + ChatColor.GRAY + "'.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      List list = editionInventory.getEditedSection().contains("lore") ? editionInventory.getEditedSection().getStringList("lore") : new ArrayList();
      list.add(text);
      editionInventory.getEditedSection().set("lore", list);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Lore successfully added.");
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<StringListData> optional) {
      optional.ifPresentOrElse(stringListData -> {
         texts.add(ChatColor.GRAY + "Current Value:");
         texts.addAll(MythicLib.plugin.parseColors(stringListData.getList().stream().map(text -> ChatColor.GRAY + text).toList()));
      }, () -> texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None"));
      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Click to add a line.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove the last line.");
   }
}
