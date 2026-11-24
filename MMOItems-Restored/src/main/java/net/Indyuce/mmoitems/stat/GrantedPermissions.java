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
   public StringListData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof List, "Must specify a string list");
      return new StringListData((List<String>)var1);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(var1, ItemStats.GRANTED_PERMISSIONS).enable("Write in the chat the permission you want to add.");
      }

      if (var2.getAction() == InventoryAction.PICKUP_HALF && var1.getEditedSection().contains(this.getPath())) {
         List var3 = var1.getEditedSection().getStringList(this.getPath());
         if (var3.isEmpty()) {
            return;
         }

         String var4 = (String)var3.get(var3.size() - 1);
         var3.remove(var4);
         var1.getEditedSection().set(this.getPath(), var3.isEmpty() ? null : var3);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed '" + MythicLib.plugin.parseColors(var4) + ChatColor.GRAY + "'.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      List var4 = var1.getEditedSection().contains(this.getPath()) ? var1.getEditedSection().getStringList(this.getPath()) : new ArrayList();
      var4.add(var2);
      var1.getEditedSection().set(this.getPath(), var4);
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Permission successfully added.");
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<StringListData> var2) {
      if (var2.isPresent()) {
         var1.add(ChatColor.GRAY + "Current Value:");
         StringListData var3 = (StringListData)var2.get();
         var3.getList().forEach(var1x -> var1.add(ChatColor.GRAY + var1x));
      } else {
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Click to add a permission.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove the last permission.");
   }
}
