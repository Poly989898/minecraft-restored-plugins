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
   public ArrowParticlesEdition(Player player, MMOItemTemplate mmoItemTemplate) {
      super(player, mmoItemTemplate);
   }

   @Override
   public String getName() {
      return "Arrow Particles: " + this.template.getId();
   }

   @Override
   public void arrangeInventory() {
      Particle particle = null;

      try {
         particle = Particle.valueOf(this.getEditedSection().getString("arrow-particles.particle"));
      } catch (Exception exception) {
      }

      ItemStack item = new ItemStack(Material.BLAZE_POWDER);
      ItemMeta itemMeta = item.getItemMeta();
      itemMeta.setDisplayName(ChatColor.GREEN + "Particle");
      ArrayList list = new ArrayList();
      list.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "The particle which is displayed around the");
      list.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "arrow. Fades away when the arrow lands.");
      list.add("");
      list.add(
         ChatColor.GRAY
            + "Current Value: "
            + (
               particle == null
                  ? ChatColor.RED + "No particle selected."
                  : ChatColor.GOLD + UtilityMethods.caseOnWords(particle.name().toLowerCase().replace("_", " "))
            )
      );
      list.add("");
      list.add(ChatColor.YELLOW + "►" + " Click to change this value.");
      list.add(ChatColor.YELLOW + "►" + " Right click to reset.");
      itemMeta.setLore(list);
      item.setItemMeta(itemMeta);
      ItemStack item2 = new ItemStack(Material.GRAY_DYE);
      ItemMeta itemMeta2 = item2.getItemMeta();
      itemMeta2.setDisplayName(ChatColor.GREEN + "Amount");
      ArrayList list2 = new ArrayList();
      list2.add("");
      list2.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + this.getEditedSection().getInt("arrow-particles.amount"));
      list2.add("");
      list2.add(ChatColor.YELLOW + "►" + " Click to change this value.");
      list2.add(ChatColor.YELLOW + "►" + " Right click to reset.");
      itemMeta2.setLore(list2);
      item2.setItemMeta(itemMeta2);
      ItemStack item3 = new ItemStack(Material.GRAY_DYE);
      ItemMeta itemMeta3 = item3.getItemMeta();
      itemMeta3.setDisplayName(ChatColor.GREEN + "Offset");
      ArrayList list3 = new ArrayList();
      list3.add("");
      list3.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + this.getEditedSection().getDouble("arrow-particles.offset"));
      list3.add("");
      list3.add(ChatColor.YELLOW + "►" + " Click to change this value.");
      list3.add(ChatColor.YELLOW + "►" + " Right click to reset.");
      itemMeta3.setLore(list3);
      item3.setItemMeta(itemMeta3);
      if (particle != null) {
         ConfigurationSection arrowParticles = this.getEditedSection().getConfigurationSection("arrow-particles");
         if (MMOUtils.isColorable(particle)) {
            int colorRed = arrowParticles.getInt("color.red");
            int colorGreen = arrowParticles.getInt("color.green");
            int colorBlue = arrowParticles.getInt("color.blue");
            ItemStack item4 = new ItemStack(Material.GRAY_DYE);
            ItemMeta itemMeta4 = item4.getItemMeta();
            itemMeta4.setDisplayName(ChatColor.GREEN + "Particle Color");
            ArrayList list4 = new ArrayList();
            list4.add("");
            list4.add(ChatColor.GRAY + "Current Value (R-G-B):");
            list4.add(
               ""
                  + ChatColor.RED
                  + ChatColor.BOLD
                  + colorRed
                  + ChatColor.GRAY
                  + " - "
                  + ChatColor.GREEN
                  + ChatColor.BOLD
                  + colorGreen
                  + ChatColor.GRAY
                  + " - "
                  + ChatColor.BLUE
                  + ChatColor.BOLD
                  + colorBlue
            );
            list4.add("");
            list4.add(ChatColor.YELLOW + "►" + " Click to change this value.");
            list4.add(ChatColor.YELLOW + "►" + " Right click to reset.");
            itemMeta4.setLore(list4);
            item4.setItemMeta(itemMeta4);
            this.inventory.setItem(41, item4);
         } else {
            ItemStack item4 = new ItemStack(Material.GRAY_DYE);
            ItemMeta itemMeta4 = item4.getItemMeta();
            itemMeta4.setDisplayName(ChatColor.GREEN + "Speed");
            ArrayList list4 = new ArrayList();
            list4.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "The speed at which your particle");
            list4.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "flies off in random directions.");
            list4.add("");
            list4.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + arrowParticles.getDouble("speed"));
            list4.add("");
            list4.add(ChatColor.YELLOW + "►" + " Click to change this value.");
            list4.add(ChatColor.YELLOW + "►" + " Right click to reset.");
            itemMeta4.setLore(list4);
            item4.setItemMeta(itemMeta4);
            this.inventory.setItem(41, item4);
         }
      }

      this.inventory.setItem(30, item);
      this.inventory.setItem(23, item2);
      this.inventory.setItem(32, item3);
   }

   @Override
   public void whenClicked(InventoryClickEvent event) {
      ItemStack currentItem = event.getCurrentItem();
      event.setCancelled(true);
      if (event.getInventory() == event.getClickedInventory() && MMOUtils.isMetaItem(currentItem, false)) {
         if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Particle")) {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.ARROW_PARTICLES, "particle").enable("Write in the chat the particle you want.");
            }

            if (event.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("arrow-particles.particle")) {
               this.getEditedSection().set("arrow-particles", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the particle.");
            }
         }

         if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Particle Color")) {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.ARROW_PARTICLES, "color")
                  .enable("Write in the chat the RGB color you want.", ChatColor.AQUA + "Format: [RED] [GREEN] [BLUE]");
            }

            if (event.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("arrow-particles.color")) {
               this.getEditedSection().set("arrow-particles.color", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the particle color.");
            }
         }

         for (String text : new String[]{"amount", "offset", "speed"}) {
            if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + UtilityMethods.caseOnWords(text))) {
               if (event.getAction() == InventoryAction.PICKUP_ALL) {
                  new StatEdition(this, ItemStats.ARROW_PARTICLES, text).enable("Write in the chat the " + text + " you want.");
               }

               if (event.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("arrow-particles." + text)) {
                  this.getEditedSection().set("arrow-particles." + text, null);
                  this.registerTemplateEdition();
                  this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the " + text + ".");
               }
            }
         }
      }
   }
}
