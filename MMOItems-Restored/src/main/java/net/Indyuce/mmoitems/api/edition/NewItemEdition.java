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

   public NewItemEdition(ItemBrowser var1) {
      this.inv = var1;
   }

   @Override
   public PluginInventory getInventory() {
      return this.inv;
   }

   @Override
   public void enable(String... var1) {
      this.inv.getPlayer().closeInventory();
      this.inv.getPlayer().sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "-----------------------------------------------------");
      this.inv.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Write in the chat the ID of the new item.");
      this.inv.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Type 'cancel' to abort.");
      new ChatEdition(this);
      this.inv.getPlayer().sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "Item Creation", "See chat.", 10, 40, 10);
   }

   @Override
   public boolean processInput(String var1) {
      return this.successful = Bukkit.dispatchCommand(
         this.inv.getPlayer(), "mmoitems create " + this.inv.getType().getId() + " " + var1.toUpperCase().replace(" ", "_").replace("-", "_")
      );
   }

   @Override
   public boolean shouldGoBack() {
      return !this.successful;
   }
}
