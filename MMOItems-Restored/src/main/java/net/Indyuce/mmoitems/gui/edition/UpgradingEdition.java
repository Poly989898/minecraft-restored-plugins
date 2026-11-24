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

   public UpgradingEdition(Player var1, MMOItemTemplate var2) {
      super(var1, var2);
   }

   @Override
   public String getName() {
      return "Upgrade Setup: " + this.template.getId();
   }

   @Override
   public void arrangeInventory() {
      boolean var1 = this.getEditedSection().getBoolean("upgrade.workbench");
      if (!this.template.getType().corresponds(Type.CONSUMABLE)) {
         ItemStack var2 = new ItemStack(Material.CRAFTING_TABLE);
         ItemMeta var3 = var2.getItemMeta();
         var3.setDisplayName(ChatColor.GREEN + "Workbench Upgrade Only?");
         ArrayList var4 = new ArrayList();
         var4.add(ChatColor.GRAY + "When toggled on, players must");
         var4.add(ChatColor.GRAY + "use a crafting station recipe in");
         var4.add(ChatColor.GRAY + "order to upgrade their weapon.");
         var4.add("");
         var4.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + var1);
         var4.add("");
         var4.add(ChatColor.YELLOW + "►" + " Click to change this value.");
         var3.setLore(var4);
         var2.setItemMeta(var3);
         this.inventory.setItem(20, var2);
         String var5 = this.getEditedSection().getString("upgrade.template");
         ItemStack var6 = new ItemStack(Material.OAK_SIGN);
         ItemMeta var7 = var6.getItemMeta();
         var7.setDisplayName(ChatColor.GREEN + "Upgrade Template");
         ArrayList var8 = new ArrayList();
         var8.add(ChatColor.GRAY + "This option dictates what stats are improved");
         var8.add(ChatColor.GRAY + "when your item is upgraded. More info on the wiki.");
         var8.add("");
         var8.add(ChatColor.GRAY + "Current Value: " + (var5 == null ? ChatColor.RED + "No template" : ChatColor.GOLD + var5));
         var8.add("");
         var8.add(ChatColor.YELLOW + "►" + " Click to input the template.");
         var8.add(ChatColor.YELLOW + "►" + " Right click to reset.");
         var7.setLore(var8);
         var6.setItemMeta(var7);
         this.inventory.setItem(22, var6);
         int var9 = this.getEditedSection().getInt("upgrade.max");
         ItemStack var10 = new ItemStack(Material.BARRIER);
         ItemMeta var11 = var10.getItemMeta();
         var11.setDisplayName(ChatColor.GREEN + "Max Upgrades");
         ArrayList var12 = new ArrayList();
         var12.add(ChatColor.GRAY + "The maximum amount of upgrades your");
         var12.add(ChatColor.GRAY + "item may receive (recipe or consumable).");
         var12.add("");
         var12.add(ChatColor.GRAY + "Current Value: " + (var9 == 0 ? ChatColor.RED + "No limit" : ChatColor.GOLD + "" + var9));
         var12.add("");
         var12.add(ChatColor.YELLOW + "►" + " Click to chance this value.");
         var12.add(ChatColor.YELLOW + "►" + " Right click to reset.");
         var11.setLore(var12);
         var10.setItemMeta(var11);
         this.inventory.setItem(40, var10);
         int var13 = this.getEditedSection().getInt("upgrade.min", 0);
         ItemStack var14 = new ItemStack(Material.BARRIER);
         ItemMeta var15 = var14.getItemMeta();
         var15.setDisplayName(ChatColor.GREEN + "Min Upgrades");
         ArrayList var16 = new ArrayList();
         var16.add(ChatColor.GRAY + "The minimum level your item can be");
         var16.add(ChatColor.GRAY + "downgraded to (by dying or breaking).");
         var16.add("");
         var16.add(ChatColor.GRAY + "Current Value: " + (var13 == 0 ? ChatColor.RED + "0" : ChatColor.GOLD + String.valueOf(var13)));
         var16.add("");
         var16.add(ChatColor.YELLOW + "►" + " Click to chance this value.");
         var16.add(ChatColor.YELLOW + "►" + " Right click to reset.");
         var15.setLore(var16);
         var14.setItemMeta(var15);
         this.inventory.setItem(41, var14);
      } else {
         this.inventory.setItem(20, notAvailable);
         this.inventory.setItem(22, notAvailable);
      }

      if (var1 && !this.template.getType().corresponds(Type.CONSUMABLE)) {
         this.inventory.setItem(38, notAvailable);
      } else {
         String var17 = this.getEditedSection().getString("upgrade.reference");
         ItemStack var19 = new ItemStack(Material.PAPER);
         ItemMeta var20 = var19.getItemMeta();
         var20.setDisplayName(ChatColor.GREEN + "Upgrade Reference");
         ArrayList var22 = new ArrayList();
         var22.add(ChatColor.GRAY + "This option dictates what consumables can");
         var22.add(ChatColor.GRAY + "upgrade your item. " + ChatColor.AQUA + "The upgrade reference");
         var22.add(ChatColor.AQUA + "of your consumable must match the reference");
         var22.add(ChatColor.AQUA + "of the target item" + ChatColor.GRAY + ", otherwise it can't upgrade it.");
         var22.add("");
         var22.add(ChatColor.GRAY + "Current Value: " + (var17 == null ? ChatColor.RED + "No reference" : ChatColor.GOLD + var17));
         var22.add("");
         var22.add(ChatColor.YELLOW + "►" + " Click to input the reference.");
         var22.add(ChatColor.YELLOW + "►" + " Right click to reset.");
         var20.setLore(var22);
         var19.setItemMeta(var20);
         this.inventory.setItem(38, var19);
      }

      double var18 = this.getEditedSection().getDouble("upgrade.success");
      ItemStack var21 = new ItemStack(Material.EXPERIENCE_BOTTLE);
      ItemMeta var23 = var21.getItemMeta();
      var23.setDisplayName(ChatColor.GREEN + "Success Chance");
      ArrayList var24 = new ArrayList();
      var24.add(ChatColor.GRAY + "The chance of successfully upgrading");
      var24.add(ChatColor.GRAY + "when using a consumable or when using");
      var24.add(ChatColor.GRAY + "a station upgrading recipe.");
      var24.add("");
      var24.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + (var18 == 0.0 ? "100" : "" + var18) + "%");
      var24.add("");
      var24.add(ChatColor.YELLOW + "►" + " Left click to change this value.");
      var24.add(ChatColor.YELLOW + "►" + " Right click to reset.");
      var23.setLore(var24);
      var21.setItemMeta(var23);
      this.inventory.setItem(24, var21);
      if (var18 > 0.0 && !this.template.getType().corresponds(Type.CONSUMABLE)) {
         ItemStack var25 = new ItemStack(Material.FISHING_ROD);
         ItemMeta var26 = var25.getItemMeta();
         ((Damageable)var26).setDamage(30);
         var26.setDisplayName(ChatColor.GREEN + "Destroy on fail?");
         ArrayList var27 = new ArrayList();
         var27.add(ChatColor.GRAY + "When toggled on, the item will be");
         var27.add(ChatColor.GRAY + "destroyed when failing at upgrading it.");
         var27.add("");
         var27.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + this.getEditedSection().getBoolean("upgrade.destroy"));
         var27.add("");
         var27.add(ChatColor.YELLOW + "►" + " Click to change this value.");
         var26.setLore(var27);
         var25.setItemMeta(var26);
         this.inventory.setItem(42, var25);
      }
   }

   @Override
   public void whenClicked(InventoryClickEvent var1) {
      ItemStack var2 = var1.getCurrentItem();
      var1.setCancelled(true);
      if (var1.getInventory() == var1.getClickedInventory() && MMOUtils.isMetaItem(var2, false)) {
         if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Success Chance")) {
            if (var1.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.UPGRADE, "rate").enable("Write in the chat the success rate you want.");
            }

            if (var1.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("upgrade.success")) {
               this.getEditedSection().set("upgrade.success", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset success chance.");
            }
         }

         if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Max Upgrades")) {
            if (var1.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.UPGRADE, "max").enable("Write in the chat the number you want.");
            }

            if (var1.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("upgrade.max")) {
               this.getEditedSection().set("upgrade.max", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the number of max upgrades.");
            }
         }

         if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Min Upgrades")) {
            if (var1.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.UPGRADE, "min").enable("Write in the chat the number you want.");
            }

            if (var1.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("upgrade.min")) {
               this.getEditedSection().set("upgrade.min", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the number of min level.");
            }
         }

         if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Upgrade Template")) {
            if (var1.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.UPGRADE, "template").enable("Write in the chat the upgrade template ID you want.");
            }

            if (var1.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("upgrade.template")) {
               this.getEditedSection().set("upgrade.template", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset upgrade template.");
            }
         }

         if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Upgrade Reference")) {
            if (var1.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.UPGRADE, "ref").enable("Write in the chat the upgrade reference (text) you want.");
            }

            if (var1.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("upgrade.reference")) {
               this.getEditedSection().set("upgrade.reference", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset upgrade reference.");
            }
         }

         if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Workbench Upgrade Only?")) {
            boolean var3 = !this.getEditedSection().getBoolean("upgrade.workbench");
            this.getEditedSection().set("upgrade.workbench", var3);
            this.registerTemplateEdition();
            this.player
               .sendMessage(
                  MMOItems.plugin.getPrefix() + (var3 ? "Your item must now be upgraded via recipes." : "Your item can now be upgraded using consumables.")
               );
         }

         if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Destroy on fail?")) {
            boolean var4 = !this.getEditedSection().getBoolean("upgrade.destroy");
            this.getEditedSection().set("upgrade.destroy", var4);
            this.registerTemplateEdition();
            this.player
               .sendMessage(
                  MMOItems.plugin.getPrefix()
                     + (var4 ? "Your item will be destroyed upon failing upgrade." : "Your item will not be destroyed upon failing upgrade.")
               );
         }
      }
   }
}
