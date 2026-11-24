package net.Indyuce.mmoitems.gui.edition;

import java.util.ArrayList;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CommandListEdition extends EditionInventory {
   private static final int[] slots = new int[]{19, 20, 21, 22, 23, 24, 25, 28, 29, 33, 34, 37, 38, 42, 43};
   private static final NamespacedKey CONFIG_KEY = new NamespacedKey(MMOItems.plugin, "ConfigKey");

   public CommandListEdition(Player var1, MMOItemTemplate var2) {
      super(var1, var2);
   }

   @Override
   public String getName() {
      return "Command List";
   }

   @Override
   public void arrangeInventory() {
      int var1 = 0;
      if (this.getEditedSection().contains("commands")) {
         for (String var3 : this.getEditedSection().getConfigurationSection("commands").getKeys(false)) {
            String var4 = this.getEditedSection().getString("commands." + var3 + ".format");
            double var5 = this.getEditedSection().getDouble("commands." + var3 + ".delay");
            boolean var7 = this.getEditedSection().getBoolean("commands." + var3 + ".console");
            boolean var8 = this.getEditedSection().getBoolean("commands." + var3 + ".op");
            ItemStack var9 = new ItemStack(Material.COMPARATOR);
            ItemMeta var10 = var9.getItemMeta();
            var10.setDisplayName(var4 != null && !var4.equals("") ? ChatColor.GREEN + var4 : ChatColor.RED + "No Format");
            ArrayList var11 = new ArrayList();
            var11.add("");
            var11.add(ChatColor.GRAY + "Command Delay: " + ChatColor.RED + var5);
            var11.add(ChatColor.GRAY + "Sent by Console: " + ChatColor.RED + var7);
            var11.add(ChatColor.GRAY + "Sent w/ OP perms: " + ChatColor.RED + var8);
            var11.add("");
            var11.add(ChatColor.YELLOW + "►" + " Right click to remove.");
            var10.setLore(var11);
            var10.getPersistentDataContainer().set(CONFIG_KEY, PersistentDataType.STRING, var3);
            var9.setItemMeta(var10);
            this.inventory.setItem(slots[var1++], var9);
         }
      }

      ItemStack var12 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemMeta var13 = var12.getItemMeta();
      var13.setDisplayName(ChatColor.RED + "- No Command -");
      var12.setItemMeta(var13);
      ItemStack var14 = new ItemStack(Material.WRITABLE_BOOK);
      ItemMeta var15 = var14.getItemMeta();
      var15.setDisplayName(ChatColor.GREEN + "Register a command...");
      var14.setItemMeta(var15);
      this.inventory.setItem(40, var14);

      while (var1 < slots.length) {
         this.inventory.setItem(slots[var1++], var12);
      }
   }

   @Override
   public void whenClicked(InventoryClickEvent var1) {
      ItemStack var2 = var1.getCurrentItem();
      var1.setCancelled(true);
      if (var1.getInventory() == var1.getClickedInventory() && MMOUtils.isMetaItem(var2, false)) {
         if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Register a command...")) {
            new StatEdition(this, ItemStats.COMMANDS)
               .enable(
                  "Write in the chat the command you want to add.",
                  "",
                  "To add a delay, use " + ChatColor.RED + "-d:<delay>",
                  "To make the command cast itself w/ console, use " + ChatColor.RED + "-c",
                  "To make the command cast w/ OP perms, use " + ChatColor.RED + "-op",
                  "",
                  ChatColor.YELLOW + "Ex: -d:10.3 -op bc Hello, this is a test command."
               );
         } else {
            String var3 = (String)var2.getItemMeta().getPersistentDataContainer().get(CONFIG_KEY, PersistentDataType.STRING);
            if (var3 != null && !var3.equals("")) {
               if (var1.getAction() == InventoryAction.PICKUP_HALF
                  && this.getEditedSection().contains("commands")
                  && this.getEditedSection().getConfigurationSection("commands").contains(var3)) {
                  this.getEditedSection().set("commands." + var3, null);
                  this.registerTemplateEdition();
                  this.player
                     .sendMessage(
                        MMOItems.plugin.getPrefix()
                           + "Successfully removed "
                           + ChatColor.GOLD
                           + var3
                           + ChatColor.DARK_GRAY
                           + " (Internal ID)"
                           + ChatColor.GRAY
                           + "."
                     );
               }
            }
         }
      }
   }
}
