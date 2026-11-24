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
         ItemStack upgradeTemplateItem = new ItemStack(Material.OAK_SIGN);
         ItemMeta upgradeTemplateMeta = upgradeTemplateItem.getItemMeta();
         upgradeTemplateMeta.setDisplayName(ChatColor.GREEN + "Upgrade Template");
         ArrayList upgradeTemplateLore = new ArrayList();
         upgradeTemplateLore.add(ChatColor.GRAY + "This option dictates what stats are improved");
         upgradeTemplateLore.add(ChatColor.GRAY + "when your item is upgraded. More info on the wiki.");
         upgradeTemplateLore.add("");
         upgradeTemplateLore.add(ChatColor.GRAY + "Current Value: " + (upgradeTemplate == null ? ChatColor.RED + "No template" : ChatColor.GOLD + upgradeTemplate));
         upgradeTemplateLore.add("");
         upgradeTemplateLore.add(ChatColor.YELLOW + "►" + " Click to input the template.");
         upgradeTemplateLore.add(ChatColor.YELLOW + "►" + " Right click to reset.");
         upgradeTemplateMeta.setLore(upgradeTemplateLore);
         upgradeTemplateItem.setItemMeta(upgradeTemplateMeta);
         this.inventory.setItem(22, upgradeTemplateItem);
         int upgradeMax = this.getEditedSection().getInt("upgrade.max");
         ItemStack maxUpgradesItem = new ItemStack(Material.BARRIER);
         ItemMeta maxUpgradesMeta = maxUpgradesItem.getItemMeta();
         maxUpgradesMeta.setDisplayName(ChatColor.GREEN + "Max Upgrades");
         ArrayList maxUpgradesLore = new ArrayList();
         maxUpgradesLore.add(ChatColor.GRAY + "The maximum amount of upgrades your");
         maxUpgradesLore.add(ChatColor.GRAY + "item may receive (recipe or consumable).");
         maxUpgradesLore.add("");
         maxUpgradesLore.add(ChatColor.GRAY + "Current Value: " + (upgradeMax == 0 ? ChatColor.RED + "No limit" : ChatColor.GOLD + "" + upgradeMax));
         maxUpgradesLore.add("");
         maxUpgradesLore.add(ChatColor.YELLOW + "►" + " Click to chance this value.");
         maxUpgradesLore.add(ChatColor.YELLOW + "►" + " Right click to reset.");
         maxUpgradesMeta.setLore(maxUpgradesLore);
         maxUpgradesItem.setItemMeta(maxUpgradesMeta);
         this.inventory.setItem(40, maxUpgradesItem);
         int upgradeMin = this.getEditedSection().getInt("upgrade.min", 0);
         ItemStack minUpgradesItem = new ItemStack(Material.BARRIER);
         ItemMeta minUpgradesMeta = minUpgradesItem.getItemMeta();
         minUpgradesMeta.setDisplayName(ChatColor.GREEN + "Min Upgrades");
         ArrayList minUpgradesLore = new ArrayList();
         minUpgradesLore.add(ChatColor.GRAY + "The minimum level your item can be");
         minUpgradesLore.add(ChatColor.GRAY + "downgraded to (by dying or breaking).");
         minUpgradesLore.add("");
         minUpgradesLore.add(ChatColor.GRAY + "Current Value: " + (upgradeMin == 0 ? ChatColor.RED + "0" : ChatColor.GOLD + String.valueOf(upgradeMin)));
         minUpgradesLore.add("");
         minUpgradesLore.add(ChatColor.YELLOW + "►" + " Click to chance this value.");
         minUpgradesLore.add(ChatColor.YELLOW + "►" + " Right click to reset.");
         minUpgradesMeta.setLore(minUpgradesLore);
         minUpgradesItem.setItemMeta(minUpgradesMeta);
         this.inventory.setItem(41, minUpgradesItem);
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
         ItemStack destroyOnFailItem = new ItemStack(Material.FISHING_ROD);
         ItemMeta destroyOnFailMeta = destroyOnFailItem.getItemMeta();
         ((Damageable)destroyOnFailMeta).setDamage(30);
         destroyOnFailMeta.setDisplayName(ChatColor.GREEN + "Destroy on fail?");
         ArrayList destroyOnFailLore = new ArrayList();
         destroyOnFailLore.add(ChatColor.GRAY + "When toggled on, the item will be");
         destroyOnFailLore.add(ChatColor.GRAY + "destroyed when failing at upgrading it.");
         destroyOnFailLore.add("");
         destroyOnFailLore.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + this.getEditedSection().getBoolean("upgrade.destroy"));
         destroyOnFailLore.add("");
         destroyOnFailLore.add(ChatColor.YELLOW + "►" + " Click to change this value.");
         destroyOnFailMeta.setLore(destroyOnFailLore);
         destroyOnFailItem.setItemMeta(destroyOnFailMeta);
         this.inventory.setItem(42, destroyOnFailItem);
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
