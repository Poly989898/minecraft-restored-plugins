package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.MythicLib;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
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

public class GrantedPermissions extends StringListStat implements GemStoneStat {
   public GrantedPermissions() {
      super(
         "GRANTED_PERMISSIONS",
         Material.NAME_TAG,
         "Granted Permissions",
         new String[]{"A list of permissions that will,", "be granted by the item."},
         new String[0]
      );
   }

   @Override
   public StringListData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof List, "Must specify a string list");
      return new StringListData((List<String>)obj);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(editionInventory, ItemStats.GRANTED_PERMISSIONS).enable("Write in the chat the permission you want to add.");
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF && editionInventory.getEditedSection().contains(this.getPath())) {
         List list = editionInventory.getEditedSection().getStringList(this.getPath());
         if (list.isEmpty()) {
            return;
         }

         String text = (String)list.get(list.size() - 1);
         list.remove(text);
         editionInventory.getEditedSection().set(this.getPath(), list.isEmpty() ? null : list);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed '" + MythicLib.plugin.parseColors(text) + ChatColor.GRAY + "'.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      List list = editionInventory.getEditedSection().contains(this.getPath()) ? editionInventory.getEditedSection().getStringList(this.getPath()) : new ArrayList();
      list.add(text);
      editionInventory.getEditedSection().set(this.getPath(), list);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Permission successfully added.");
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<StringListData> optional) {
      if (optional.isPresent()) {
         texts.add(ChatColor.GRAY + "Current Value:");
         StringListData stringListData = (StringListData)optional.get();
         stringListData.getList().forEach(var1x -> texts.add(ChatColor.GRAY + var1x));
      } else {
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Click to add a permission.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove the last permission.");
   }
}
