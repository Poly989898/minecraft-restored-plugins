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
      ItemStack amountItem = new ItemStack(Material.GRAY_DYE);
      ItemMeta amountMeta = amountItem.getItemMeta();
      amountMeta.setDisplayName(ChatColor.GREEN + "Amount");
      ArrayList amountLore = new ArrayList();
      amountLore.add("");
      amountLore.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + this.getEditedSection().getInt("arrow-particles.amount"));
      amountLore.add("");
      amountLore.add(ChatColor.YELLOW + "►" + " Click to change this value.");
      amountLore.add(ChatColor.YELLOW + "►" + " Right click to reset.");
      amountMeta.setLore(amountLore);
      amountItem.setItemMeta(amountMeta);
      ItemStack offsetItem = new ItemStack(Material.GRAY_DYE);
      ItemMeta offsetMeta = offsetItem.getItemMeta();
      offsetMeta.setDisplayName(ChatColor.GREEN + "Offset");
      ArrayList offsetLore = new ArrayList();
      offsetLore.add("");
      offsetLore.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + this.getEditedSection().getDouble("arrow-particles.offset"));
      offsetLore.add("");
      offsetLore.add(ChatColor.YELLOW + "►" + " Click to change this value.");
      offsetLore.add(ChatColor.YELLOW + "►" + " Right click to reset.");
      offsetMeta.setLore(offsetLore);
      offsetItem.setItemMeta(offsetMeta);
      if (particle != null) {
         ConfigurationSection arrowParticles = this.getEditedSection().getConfigurationSection("arrow-particles");
         if (MMOUtils.isColorable(particle)) {
            int colorRed = arrowParticles.getInt("color.red");
            int colorGreen = arrowParticles.getInt("color.green");
            int colorBlue = arrowParticles.getInt("color.blue");
            ItemStack particleColorItem = new ItemStack(Material.GRAY_DYE);
            ItemMeta particleColorMeta = particleColorItem.getItemMeta();
            particleColorMeta.setDisplayName(ChatColor.GREEN + "Particle Color");
            ArrayList particleColorLore = new ArrayList();
            particleColorLore.add("");
            particleColorLore.add(ChatColor.GRAY + "Current Value (R-G-B):");
            particleColorLore.add(
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
            particleColorLore.add("");
            particleColorLore.add(ChatColor.YELLOW + "►" + " Click to change this value.");
            particleColorLore.add(ChatColor.YELLOW + "►" + " Right click to reset.");
            particleColorMeta.setLore(particleColorLore);
            particleColorItem.setItemMeta(particleColorMeta);
            this.inventory.setItem(41, particleColorItem);
         } else {
            ItemStack speedItem = new ItemStack(Material.GRAY_DYE);
            ItemMeta speedMeta = speedItem.getItemMeta();
            speedMeta.setDisplayName(ChatColor.GREEN + "Speed");
            ArrayList speedLore = new ArrayList();
            speedLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "The speed at which your particle");
            speedLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "flies off in random directions.");
            speedLore.add("");
            speedLore.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + arrowParticles.getDouble("speed"));
            speedLore.add("");
            speedLore.add(ChatColor.YELLOW + "►" + " Click to change this value.");
            speedLore.add(ChatColor.YELLOW + "►" + " Right click to reset.");
            speedMeta.setLore(speedLore);
            speedItem.setItemMeta(speedMeta);
            this.inventory.setItem(41, speedItem);
         }
      }

      this.inventory.setItem(30, item);
      this.inventory.setItem(23, amountItem);
      this.inventory.setItem(32, offsetItem);
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
