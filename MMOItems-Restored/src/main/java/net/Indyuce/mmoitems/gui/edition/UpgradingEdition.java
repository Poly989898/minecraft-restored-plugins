package net.Indyuce.mmoitems.gui.edition;

import io.lumine.mythic.lib.api.util.ItemFactory;
import java.util.ArrayList;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class UpgradingEdition extends EditionInventory {
   private static final ItemStack notAvailable = ItemFactory.of(Material.RED_STAINED_GLASS_PANE).name("&cNot Available").build();

   public UpgradingEdition(Player player, MMOItemTemplate mmoItemTemplate) {
      super(player, mmoItemTemplate);
   }

   @Override
   public String getName() {
      return "Upgrade Setup: " + this.template.getId();
   }

   @Override
   public void arrangeInventory() {
      boolean upgradeWorkbench = this.getEditedSection().getBoolean("upgrade.workbench");
      if (!this.template.getType().corresponds(Type.CONSUMABLE)) {
         ItemStack item = new ItemStack(Material.CRAFTING_TABLE);
         ItemMeta itemMeta = item.getItemMeta();
         itemMeta.setDisplayName(ChatColor.GREEN + "Workbench Upgrade Only?");
         ArrayList list = new ArrayList();
         list.add(ChatColor.GRAY + "When toggled on, players must");
         list.add(ChatColor.GRAY + "use a crafting station recipe in");
         list.add(ChatColor.GRAY + "order to upgrade their weapon.");
         list.add("");
         list.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + upgradeWorkbench);
         list.add("");
         list.add(ChatColor.YELLOW + "►" + " Click to change this value.");
         itemMeta.setLore(list);
         item.setItemMeta(itemMeta);
         this.inventory.setItem(20, item);
         String upgradeTemplate = this.getEditedSection().getString("upgrade.template");
         ItemStack item2 = new ItemStack(Material.OAK_SIGN);
         ItemMeta itemMeta2 = item2.getItemMeta();
         itemMeta2.setDisplayName(ChatColor.GREEN + "Upgrade Template");
         ArrayList list2 = new ArrayList();
         list2.add(ChatColor.GRAY + "This option dictates what stats are improved");
         list2.add(ChatColor.GRAY + "when your item is upgraded. More info on the wiki.");
         list2.add("");
         list2.add(ChatColor.GRAY + "Current Value: " + (upgradeTemplate == null ? ChatColor.RED + "No template" : ChatColor.GOLD + upgradeTemplate));
         list2.add("");
         list2.add(ChatColor.YELLOW + "►" + " Click to input the template.");
         list2.add(ChatColor.YELLOW + "►" + " Right click to reset.");
         itemMeta2.setLore(list2);
         item2.setItemMeta(itemMeta2);
         this.inventory.setItem(22, item2);
         int upgradeMax = this.getEditedSection().getInt("upgrade.max");
         ItemStack item3 = new ItemStack(Material.BARRIER);
         ItemMeta itemMeta3 = item3.getItemMeta();
         itemMeta3.setDisplayName(ChatColor.GREEN + "Max Upgrades");
         ArrayList list3 = new ArrayList();
         list3.add(ChatColor.GRAY + "The maximum amount of upgrades your");
         list3.add(ChatColor.GRAY + "item may receive (recipe or consumable).");
         list3.add("");
         list3.add(ChatColor.GRAY + "Current Value: " + (upgradeMax == 0 ? ChatColor.RED + "No limit" : ChatColor.GOLD + "" + upgradeMax));
         list3.add("");
         list3.add(ChatColor.YELLOW + "►" + " Click to chance this value.");
         list3.add(ChatColor.YELLOW + "►" + " Right click to reset.");
         itemMeta3.setLore(list3);
         item3.setItemMeta(itemMeta3);
         this.inventory.setItem(40, item3);
         int upgradeMin = this.getEditedSection().getInt("upgrade.min", 0);
         ItemStack item4 = new ItemStack(Material.BARRIER);
         ItemMeta itemMeta4 = item4.getItemMeta();
         itemMeta4.setDisplayName(ChatColor.GREEN + "Min Upgrades");
         ArrayList list4 = new ArrayList();
         list4.add(ChatColor.GRAY + "The minimum level your item can be");
         list4.add(ChatColor.GRAY + "downgraded to (by dying or breaking).");
         list4.add("");
         list4.add(ChatColor.GRAY + "Current Value: " + (upgradeMin == 0 ? ChatColor.RED + "0" : ChatColor.GOLD + String.valueOf(upgradeMin)));
         list4.add("");
         list4.add(ChatColor.YELLOW + "►" + " Click to chance this value.");
         list4.add(ChatColor.YELLOW + "►" + " Right click to reset.");
         itemMeta4.setLore(list4);
         item4.setItemMeta(itemMeta4);
         this.inventory.setItem(41, item4);
      } else {
         this.inventory.setItem(20, notAvailable);
         this.inventory.setItem(22, notAvailable);
      }

      if (upgradeWorkbench && !this.template.getType().corresponds(Type.CONSUMABLE)) {
         this.inventory.setItem(38, notAvailable);
      } else {
         String upgradeReference = this.getEditedSection().getString("upgrade.reference");
         ItemStack item = new ItemStack(Material.PAPER);
         ItemMeta itemMeta = item.getItemMeta();
         itemMeta.setDisplayName(ChatColor.GREEN + "Upgrade Reference");
         ArrayList list = new ArrayList();
         list.add(ChatColor.GRAY + "This option dictates what consumables can");
         list.add(ChatColor.GRAY + "upgrade your item. " + ChatColor.AQUA + "The upgrade reference");
         list.add(ChatColor.AQUA + "of your consumable must match the reference");
         list.add(ChatColor.AQUA + "of the target item" + ChatColor.GRAY + ", otherwise it can't upgrade it.");
         list.add("");
         list.add(ChatColor.GRAY + "Current Value: " + (upgradeReference == null ? ChatColor.RED + "No reference" : ChatColor.GOLD + upgradeReference));
         list.add("");
         list.add(ChatColor.YELLOW + "►" + " Click to input the reference.");
         list.add(ChatColor.YELLOW + "►" + " Right click to reset.");
         itemMeta.setLore(list);
         item.setItemMeta(itemMeta);
         this.inventory.setItem(38, item);
      }

      double upgradeSuccess = this.getEditedSection().getDouble("upgrade.success");
      ItemStack item = new ItemStack(Material.EXPERIENCE_BOTTLE);
      ItemMeta itemMeta = item.getItemMeta();
      itemMeta.setDisplayName(ChatColor.GREEN + "Success Chance");
      ArrayList list = new ArrayList();
      list.add(ChatColor.GRAY + "The chance of successfully upgrading");
      list.add(ChatColor.GRAY + "when using a consumable or when using");
      list.add(ChatColor.GRAY + "a station upgrading recipe.");
      list.add("");
      list.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + (upgradeSuccess == 0.0 ? "100" : "" + upgradeSuccess) + "%");
      list.add("");
      list.add(ChatColor.YELLOW + "►" + " Left click to change this value.");
      list.add(ChatColor.YELLOW + "►" + " Right click to reset.");
      itemMeta.setLore(list);
      item.setItemMeta(itemMeta);
      this.inventory.setItem(24, item);
      if (upgradeSuccess > 0.0 && !this.template.getType().corresponds(Type.CONSUMABLE)) {
         ItemStack item2 = new ItemStack(Material.FISHING_ROD);
         ItemMeta itemMeta2 = item2.getItemMeta();
         ((Damageable)itemMeta2).setDamage(30);
         itemMeta2.setDisplayName(ChatColor.GREEN + "Destroy on fail?");
         ArrayList list2 = new ArrayList();
         list2.add(ChatColor.GRAY + "When toggled on, the item will be");
         list2.add(ChatColor.GRAY + "destroyed when failing at upgrading it.");
         list2.add("");
         list2.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + this.getEditedSection().getBoolean("upgrade.destroy"));
         list2.add("");
         list2.add(ChatColor.YELLOW + "►" + " Click to change this value.");
         itemMeta2.setLore(list2);
         item2.setItemMeta(itemMeta2);
         this.inventory.setItem(42, item2);
      }
   }

   @Override
   public void whenClicked(InventoryClickEvent event) {
      ItemStack currentItem = event.getCurrentItem();
      event.setCancelled(true);
      if (event.getInventory() == event.getClickedInventory() && MMOUtils.isMetaItem(currentItem, false)) {
         if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Success Chance")) {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.UPGRADE, "rate").enable("Write in the chat the success rate you want.");
            }

            if (event.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("upgrade.success")) {
               this.getEditedSection().set("upgrade.success", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset success chance.");
            }
         }

         if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Max Upgrades")) {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.UPGRADE, "max").enable("Write in the chat the number you want.");
            }

            if (event.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("upgrade.max")) {
               this.getEditedSection().set("upgrade.max", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the number of max upgrades.");
            }
         }

         if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Min Upgrades")) {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.UPGRADE, "min").enable("Write in the chat the number you want.");
            }

            if (event.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("upgrade.min")) {
               this.getEditedSection().set("upgrade.min", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the number of min level.");
            }
         }

         if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Upgrade Template")) {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.UPGRADE, "template").enable("Write in the chat the upgrade template ID you want.");
            }

            if (event.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("upgrade.template")) {
               this.getEditedSection().set("upgrade.template", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset upgrade template.");
            }
         }

         if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Upgrade Reference")) {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.UPGRADE, "ref").enable("Write in the chat the upgrade reference (text) you want.");
            }

            if (event.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("upgrade.reference")) {
               this.getEditedSection().set("upgrade.reference", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset upgrade reference.");
            }
         }

         if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Workbench Upgrade Only?")) {
            boolean flag = !this.getEditedSection().getBoolean("upgrade.workbench");
            this.getEditedSection().set("upgrade.workbench", flag);
            this.registerTemplateEdition();
            this.player
               .sendMessage(
                  MMOItems.plugin.getPrefix() + (flag ? "Your item must now be upgraded via recipes." : "Your item can now be upgraded using consumables.")
               );
         }

         if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Destroy on fail?")) {
            boolean flag = !this.getEditedSection().getBoolean("upgrade.destroy");
            this.getEditedSection().set("upgrade.destroy", flag);
            this.registerTemplateEdition();
            this.player
               .sendMessage(
                  MMOItems.plugin.getPrefix()
                     + (flag ? "Your item will be destroyed upon failing upgrade." : "Your item will not be destroyed upon failing upgrade.")
               );
         }
      }
   }
}
