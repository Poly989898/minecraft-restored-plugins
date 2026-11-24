package net.Indyuce.mmoitems.api.edition;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.input.ChatEdition;
import net.Indyuce.mmoitems.gui.ItemBrowser;
import net.Indyuce.mmoitems.gui.PluginInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class NewItemEdition implements Edition {
   private final ItemBrowser inv;
   private boolean successful;

   public NewItemEdition(ItemBrowser itemBrowser) {
      this.inv = itemBrowser;
   }

   @Override
   public PluginInventory getInventory() {
      return this.inv;
   }

   @Override
   public void enable(String... texts) {
      this.inv.getPlayer().closeInventory();
      this.inv.getPlayer().sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
      this.inv.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Write in the chat the ID of the new item.");
      this.inv.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Type 'cancel' to abort.");
      new ChatEdition(this);
      this.inv.getPlayer().sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "Item Creation", "See chat.", 10, 40, 10);
   }

   @Override
   public boolean processInput(String text) {
      return this.successful = Bukkit.dispatchCommand(
         this.inv.getPlayer(), "mmoitems create " + this.inv.getType().getId() + " " + text.toUpperCase().replace(" ", "_").replace("-", "_")
      );
   }

   @Override
   public boolean shouldGoBack() {
      return !this.successful;
   }
}
