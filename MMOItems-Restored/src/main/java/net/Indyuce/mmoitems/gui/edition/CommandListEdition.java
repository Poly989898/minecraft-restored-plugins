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

   public CommandListEdition(Player player, MMOItemTemplate mmoItemTemplate) {
      super(player, mmoItemTemplate);
   }

   @Override
   public String getName() {
      return "Command List";
   }

   @Override
   public void arrangeInventory() {
      int count = 0;
      if (this.getEditedSection().contains("commands")) {
         for (String key : this.getEditedSection().getConfigurationSection("commands").getKeys(false)) {
            String text = this.getEditedSection().getString("commands." + key + ".format");
            double value = this.getEditedSection().getDouble("commands." + key + ".delay");
            boolean flag = this.getEditedSection().getBoolean("commands." + key + ".console");
            boolean flag2 = this.getEditedSection().getBoolean("commands." + key + ".op");
            ItemStack item = new ItemStack(Material.COMPARATOR);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(text != null && !text.equals("") ? ChatColor.GREEN + text : ChatColor.RED + "No Format");
            ArrayList list = new ArrayList();
            list.add("");
            list.add(ChatColor.GRAY + "Command Delay: " + ChatColor.RED + value);
            list.add(ChatColor.GRAY + "Sent by Console: " + ChatColor.RED + flag);
            list.add(ChatColor.GRAY + "Sent w/ OP perms: " + ChatColor.RED + flag2);
            list.add("");
            list.add(ChatColor.YELLOW + "►" + " Right click to remove.");
            itemMeta.setLore(list);
            itemMeta.getPersistentDataContainer().set(CONFIG_KEY, PersistentDataType.STRING, key);
            item.setItemMeta(itemMeta);
            this.inventory.setItem(slots[count++], item);
         }
      }

      ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemMeta itemMeta = item.getItemMeta();
      itemMeta.setDisplayName(ChatColor.RED + "- No Command -");
      item.setItemMeta(itemMeta);
      ItemStack item2 = new ItemStack(Material.WRITABLE_BOOK);
      ItemMeta itemMeta2 = item2.getItemMeta();
      itemMeta2.setDisplayName(ChatColor.GREEN + "Register a command...");
      item2.setItemMeta(itemMeta2);
      this.inventory.setItem(40, item2);

      while (count < slots.length) {
         this.inventory.setItem(slots[count++], item);
      }
   }

   @Override
   public void whenClicked(InventoryClickEvent event) {
      ItemStack currentItem = event.getCurrentItem();
      event.setCancelled(true);
      if (event.getInventory() == event.getClickedInventory() && MMOUtils.isMetaItem(currentItem, false)) {
         if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Register a command...")) {
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
            String text = (String)currentItem.getItemMeta().getPersistentDataContainer().get(CONFIG_KEY, PersistentDataType.STRING);
            if (text != null && !text.equals("")) {
               if (event.getAction() == InventoryAction.PICKUP_HALF
                  && this.getEditedSection().contains("commands")
                  && this.getEditedSection().getConfigurationSection("commands").contains(text)) {
                  this.getEditedSection().set("commands." + text, null);
                  this.registerTemplateEdition();
                  this.player
                     .sendMessage(
                        MMOItems.plugin.getPrefix()
                           + "Successfully removed "
                           + ChatColor.GOLD
                           + text
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
