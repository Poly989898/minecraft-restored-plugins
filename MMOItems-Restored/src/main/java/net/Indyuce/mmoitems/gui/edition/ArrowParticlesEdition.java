package net.Indyuce.mmoitems.gui.edition;

import io.lumine.mythic.lib.UtilityMethods;
import java.util.ArrayList;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ArrowParticlesEdition extends EditionInventory {
   public ArrowParticlesEdition(Player var1, MMOItemTemplate var2) {
      super(var1, var2);
   }

   @Override
   public String getName() {
      return "Arrow Particles: " + this.template.getId();
   }

   @Override
   public void arrangeInventory() {
      Particle var1 = null;

      try {
         var1 = Particle.valueOf(this.getEditedSection().getString("arrow-particles.particle"));
      } catch (Exception var18) {
      }

      ItemStack var2 = new ItemStack(Material.BLAZE_POWDER);
      ItemMeta var3 = var2.getItemMeta();
      var3.setDisplayName(ChatColor.GREEN + "Particle");
      ArrayList var4 = new ArrayList();
      var4.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "The particle which is displayed around the");
      var4.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "arrow. Fades away when the arrow lands.");
      var4.add("");
      var4.add(
         ChatColor.GRAY
            + "Current Value: "
            + (
               var1 == null
                  ? ChatColor.RED + "No particle selected."
                  : ChatColor.GOLD + UtilityMethods.caseOnWords(var1.name().toLowerCase().replace("_", " "))
            )
      );
      var4.add("");
      var4.add(ChatColor.YELLOW + "►" + " Click to change this value.");
      var4.add(ChatColor.YELLOW + "►" + " Right click to reset.");
      var3.setLore(var4);
      var2.setItemMeta(var3);
      ItemStack var5 = new ItemStack(Material.GRAY_DYE);
      ItemMeta var6 = var5.getItemMeta();
      var6.setDisplayName(ChatColor.GREEN + "Amount");
      ArrayList var7 = new ArrayList();
      var7.add("");
      var7.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + this.getEditedSection().getInt("arrow-particles.amount"));
      var7.add("");
      var7.add(ChatColor.YELLOW + "►" + " Click to change this value.");
      var7.add(ChatColor.YELLOW + "►" + " Right click to reset.");
      var6.setLore(var7);
      var5.setItemMeta(var6);
      ItemStack var8 = new ItemStack(Material.GRAY_DYE);
      ItemMeta var9 = var8.getItemMeta();
      var9.setDisplayName(ChatColor.GREEN + "Offset");
      ArrayList var10 = new ArrayList();
      var10.add("");
      var10.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + this.getEditedSection().getDouble("arrow-particles.offset"));
      var10.add("");
      var10.add(ChatColor.YELLOW + "►" + " Click to change this value.");
      var10.add(ChatColor.YELLOW + "►" + " Right click to reset.");
      var9.setLore(var10);
      var8.setItemMeta(var9);
      if (var1 != null) {
         ConfigurationSection var11 = this.getEditedSection().getConfigurationSection("arrow-particles");
         if (MMOUtils.isColorable(var1)) {
            int var12 = var11.getInt("color.red");
            int var13 = var11.getInt("color.green");
            int var14 = var11.getInt("color.blue");
            ItemStack var15 = new ItemStack(Material.GRAY_DYE);
            ItemMeta var16 = var15.getItemMeta();
            var16.setDisplayName(ChatColor.GREEN + "Particle Color");
            ArrayList var17 = new ArrayList();
            var17.add("");
            var17.add(ChatColor.GRAY + "Current Value (R-G-B):");
            var17.add(
               ""
                  + ChatColor.RED
                  + ChatColor.BOLD
                  + var12
                  + ChatColor.GRAY
                  + " - "
                  + ChatColor.GREEN
                  + ChatColor.BOLD
                  + var13
                  + ChatColor.GRAY
                  + " - "
                  + ChatColor.BLUE
                  + ChatColor.BOLD
                  + var14
            );
            var17.add("");
            var17.add(ChatColor.YELLOW + "►" + " Click to change this value.");
            var17.add(ChatColor.YELLOW + "►" + " Right click to reset.");
            var16.setLore(var17);
            var15.setItemMeta(var16);
            this.inventory.setItem(41, var15);
         } else {
            ItemStack var19 = new ItemStack(Material.GRAY_DYE);
            ItemMeta var20 = var19.getItemMeta();
            var20.setDisplayName(ChatColor.GREEN + "Speed");
            ArrayList var21 = new ArrayList();
            var21.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "The speed at which your particle");
            var21.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "flies off in random directions.");
            var21.add("");
            var21.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + var11.getDouble("speed"));
            var21.add("");
            var21.add(ChatColor.YELLOW + "►" + " Click to change this value.");
            var21.add(ChatColor.YELLOW + "►" + " Right click to reset.");
            var20.setLore(var21);
            var19.setItemMeta(var20);
            this.inventory.setItem(41, var19);
         }
      }

      this.inventory.setItem(30, var2);
      this.inventory.setItem(23, var5);
      this.inventory.setItem(32, var8);
   }

   @Override
   public void whenClicked(InventoryClickEvent var1) {
      ItemStack var2 = var1.getCurrentItem();
      var1.setCancelled(true);
      if (var1.getInventory() == var1.getClickedInventory() && MMOUtils.isMetaItem(var2, false)) {
         if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Particle")) {
            if (var1.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.ARROW_PARTICLES, "particle").enable("Write in the chat the particle you want.");
            }

            if (var1.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("arrow-particles.particle")) {
               this.getEditedSection().set("arrow-particles", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the particle.");
            }
         }

         if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Particle Color")) {
            if (var1.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.ARROW_PARTICLES, "color")
                  .enable("Write in the chat the RGB color you want.", ChatColor.AQUA + "Format: [RED] [GREEN] [BLUE]");
            }

            if (var1.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("arrow-particles.color")) {
               this.getEditedSection().set("arrow-particles.color", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the particle color.");
            }
         }

         for (String var6 : new String[]{"amount", "offset", "speed"}) {
            if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + UtilityMethods.caseOnWords(var6))) {
               if (var1.getAction() == InventoryAction.PICKUP_ALL) {
                  new StatEdition(this, ItemStats.ARROW_PARTICLES, var6).enable("Write in the chat the " + var6 + " you want.");
               }

               if (var1.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("arrow-particles." + var6)) {
                  this.getEditedSection().set("arrow-particles." + var6, null);
                  this.registerTemplateEdition();
                  this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the " + var6 + ".");
               }
            }
         }
      }
   }
}
