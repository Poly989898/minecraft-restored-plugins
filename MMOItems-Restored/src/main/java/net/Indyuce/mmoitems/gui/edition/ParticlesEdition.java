package net.Indyuce.mmoitems.gui.edition;

import io.lumine.mythic.lib.UtilityMethods;
import java.util.ArrayList;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.particle.api.ParticleType;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ParticlesEdition extends EditionInventory {
   private static final NamespacedKey PATTERN_MODIFIED_KEY = new NamespacedKey(MMOItems.plugin, "PatternModifierId");

   public ParticlesEdition(Player player, MMOItemTemplate mmoItemTemplate) {
      super(player, mmoItemTemplate);
   }

   @Override
   public String getName() {
      return "Particles: " + this.template.getId();
   }

   @Override
   public void arrangeInventory() {
      int[] counts = new int[]{37, 38, 39, 40, 41, 42, 43};
      int count = 0;
      ParticleType particleType = null;

      try {
         particleType = ParticleType.valueOf(this.getEditedSection().getString("item-particles.type"));
      } catch (Exception exception) {
      }

      ItemStack item = new ItemStack(Material.PINK_STAINED_GLASS);
      ItemMeta itemMeta = item.getItemMeta();
      itemMeta.setDisplayName(ChatColor.GREEN + "Particle Pattern");
      ArrayList list = new ArrayList();
      list.add(ChatColor.GRAY + "The particle pattern defines how");
      list.add(ChatColor.GRAY + "particles behave, what pattern they follow");
      list.add(ChatColor.GRAY + "when displayed or what shape they form.");
      list.add("");
      list.add(ChatColor.GRAY + "Current Value: " + (particleType == null ? ChatColor.RED + "No type selected." : ChatColor.GOLD + particleType.getDefaultName()));
      if (particleType != null) {
         list.add("" + ChatColor.GRAY + ChatColor.ITALIC + particleType.getDescription());
      }

      list.add("");
      list.add(ChatColor.YELLOW + "►" + " Click to change this value.");
      list.add(ChatColor.YELLOW + "►" + " Right click to change this value.");
      itemMeta.setLore(list);
      item.setItemMeta(itemMeta);
      if (particleType != null) {
         ConfigurationSection itemParticles = this.getEditedSection().getConfigurationSection("item-particles");

         for (String modifier : particleType.getModifiers()) {
            ItemStack grayDyeItem = new ItemStack(Material.GRAY_DYE);
            ItemMeta grayDyeMeta = grayDyeItem.getItemMeta();
            grayDyeMeta.setDisplayName(ChatColor.GREEN + UtilityMethods.caseOnWords(modifier.toLowerCase().replace("-", " ")));
            ArrayList grayDyeLore = new ArrayList();
            grayDyeLore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "This is a pattern modifier.");
            grayDyeLore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "Changing this value will slightly");
            grayDyeLore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "customize the particle pattern.");
            grayDyeLore.add("");
            grayDyeLore.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + (itemParticles.contains(modifier) ? itemParticles.getDouble(modifier) : particleType.getModifier(modifier)));
            grayDyeMeta.setLore(grayDyeLore);
            grayDyeMeta.getPersistentDataContainer().set(PATTERN_MODIFIED_KEY, PersistentDataType.STRING, modifier);
            grayDyeItem.setItemMeta(grayDyeMeta);
            this.inventory.setItem(counts[count++], grayDyeItem);
         }
      }

      Particle particle = null;

      try {
         particle = Particle.valueOf(this.getEditedSection().getString("item-particles.particle"));
      } catch (Exception exception) {
      }

      ItemStack particleItem = new ItemStack(Material.BLAZE_POWDER);
      ItemMeta particleMeta = particleItem.getItemMeta();
      particleMeta.setDisplayName(ChatColor.GREEN + "Particle");
      ArrayList particleLore = new ArrayList();
      particleLore.add(ChatColor.GRAY + "Defines what particle is used");
      particleLore.add(ChatColor.GRAY + "in the particle effect.");
      particleLore.add("");
      particleLore.add(
         ChatColor.GRAY
            + "Current Value: "
            + (
               particle == null
                  ? ChatColor.RED + "No particle selected."
                  : ChatColor.GOLD + UtilityMethods.caseOnWords(particle.name().toLowerCase().replace("_", " "))
            )
      );
      particleLore.add("");
      particleLore.add(ChatColor.YELLOW + "►" + " Click to change this value.");
      particleLore.add(ChatColor.YELLOW + "►" + " Right click to change this value.");
      particleMeta.setLore(particleLore);
      particleItem.setItemMeta(particleMeta);
      if (particle != null && MMOUtils.isColorable(particle)) {
         int red = this.getEditedSection().getInt("item-particles.color.red");
         int green = this.getEditedSection().getInt("item-particles.color.green");
         int blue = this.getEditedSection().getInt("item-particles.color.blue");
         ItemStack particleColorItem = new ItemStack(Material.RED_DYE);
         ItemMeta particleColorMeta = particleColorItem.getItemMeta();
         particleColorMeta.setDisplayName(ChatColor.GREEN + "Particle Color");
         ArrayList particleColorLore = new ArrayList();
         particleColorLore.add(ChatColor.GRAY + "The RGB color of your particle.");
         particleColorLore.add("");
         particleColorLore.add(ChatColor.GRAY + "Current Value (R-G-B):");
         particleColorLore.add(
            ""
               + ChatColor.RED
               + ChatColor.BOLD
               + red
               + ChatColor.GRAY
               + " - "
               + ChatColor.GREEN
               + ChatColor.BOLD
               + green
               + ChatColor.GRAY
               + " - "
               + ChatColor.BLUE
               + ChatColor.BOLD
               + blue
         );
         particleColorLore.add("");
         particleColorLore.add(ChatColor.YELLOW + "►" + " Click to change this value.");
         particleColorLore.add(ChatColor.YELLOW + "►" + " Right click to change this value.");
         particleColorMeta.setLore(particleColorLore);
         particleColorItem.setItemMeta(particleColorMeta);
         this.inventory.setItem(25, particleColorItem);
      }

      ItemStack noModifierItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemMeta noModifierMeta = noModifierItem.getItemMeta();
      noModifierMeta.setDisplayName(ChatColor.RED + "- No Modifier -");
      noModifierItem.setItemMeta(noModifierMeta);

      while (count < counts.length) {
         this.inventory.setItem(counts[count++], noModifierItem);
      }

      this.inventory.setItem(21, item);
      this.inventory.setItem(23, particleItem);
   }

   @Override
   public void whenClicked(InventoryClickEvent event) {
      ItemStack currentItem = event.getCurrentItem();
      event.setCancelled(true);
      if (event.getInventory() == event.getClickedInventory() && MMOUtils.isMetaItem(currentItem, false)) {
         if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Particle")) {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.ITEM_PARTICLES, "particle").enable("Write in the chat the particle you want.");
            }

            if (event.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("item-particles.particle")) {
               this.getEditedSection().set("item-particles.particle", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the particle.");
            }
         }

         if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Particle Color")) {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.ITEM_PARTICLES, "particle-color")
                  .enable("Write in the chat the RGB color you want.", ChatColor.AQUA + "Format: [RED] [GREEN] [BLUE]");
            }

            if (event.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("item-particles.color")) {
               this.getEditedSection().set("item-particles.color", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the particle color.");
            }
         }

         if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Particle Pattern")) {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.ITEM_PARTICLES, "particle-type").enable("Write in the chat the particle type you want.");
               this.player.sendMessage("");
               this.player.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "Available Particles Patterns");

               for (ParticleType particleType : ParticleType.values()) {
                  this.player.sendMessage("* " + ChatColor.GREEN + particleType.name());
               }
            }

            if (event.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("item-particles.type")) {
               this.getEditedSection().set("item-particles.type", null);

               for (String key : this.getEditedSection().getConfigurationSection("item-particles").getKeys(false)) {
                  if (!key.equals("particle")) {
                     this.getEditedSection().set("item-particles." + key, null);
                  }
               }

               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the particle pattern.");
            }
         }

         String text = (String)currentItem.getItemMeta().getPersistentDataContainer().get(PATTERN_MODIFIED_KEY, PersistentDataType.STRING);
         if (text != null && !text.equals("")) {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.ITEM_PARTICLES, text).enable("Write in the chat the value you want.");
            }

            if (event.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("item-particles." + text)) {
               this.getEditedSection().set("item-particles." + text, null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset " + ChatColor.GOLD + text + ChatColor.GRAY + ".");
            }
         }
      }
   }
}
