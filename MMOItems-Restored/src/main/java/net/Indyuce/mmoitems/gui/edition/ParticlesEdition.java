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

   public ParticlesEdition(Player var1, MMOItemTemplate var2) {
      super(var1, var2);
   }

   @Override
   public String getName() {
      return "Particles: " + this.template.getId();
   }

   @Override
   public void arrangeInventory() {
      int[] var1 = new int[]{37, 38, 39, 40, 41, 42, 43};
      int var2 = 0;
      ParticleType var3 = null;

      try {
         var3 = ParticleType.valueOf(this.getEditedSection().getString("item-particles.type"));
      } catch (Exception var18) {
      }

      ItemStack var4 = new ItemStack(Material.PINK_STAINED_GLASS);
      ItemMeta var5 = var4.getItemMeta();
      var5.setDisplayName(ChatColor.GREEN + "Particle Pattern");
      ArrayList var6 = new ArrayList();
      var6.add(ChatColor.GRAY + "The particle pattern defines how");
      var6.add(ChatColor.GRAY + "particles behave, what pattern they follow");
      var6.add(ChatColor.GRAY + "when displayed or what shape they form.");
      var6.add("");
      var6.add(ChatColor.GRAY + "Current Value: " + (var3 == null ? ChatColor.RED + "No type selected." : ChatColor.GOLD + var3.getDefaultName()));
      if (var3 != null) {
         var6.add("" + ChatColor.GRAY + ChatColor.ITALIC + var3.getDescription());
      }

      var6.add("");
      var6.add(ChatColor.YELLOW + "►" + " Click to change this value.");
      var6.add(ChatColor.YELLOW + "►" + " Right click to change this value.");
      var5.setLore(var6);
      var4.setItemMeta(var5);
      if (var3 != null) {
         ConfigurationSection var7 = this.getEditedSection().getConfigurationSection("item-particles");

         for (String var9 : var3.getModifiers()) {
            ItemStack var10 = new ItemStack(Material.GRAY_DYE);
            ItemMeta var11 = var10.getItemMeta();
            var11.setDisplayName(ChatColor.GREEN + UtilityMethods.caseOnWords(var9.toLowerCase().replace("-", " ")));
            ArrayList var12 = new ArrayList();
            var12.add("" + ChatColor.GRAY + ChatColor.ITALIC + "This is a pattern modifier.");
            var12.add("" + ChatColor.GRAY + ChatColor.ITALIC + "Changing this value will slightly");
            var12.add("" + ChatColor.GRAY + ChatColor.ITALIC + "customize the particle pattern.");
            var12.add("");
            var12.add(ChatColor.GRAY + "Current Value: " + ChatColor.GOLD + (var7.contains(var9) ? var7.getDouble(var9) : var3.getModifier(var9)));
            var11.setLore(var12);
            var11.getPersistentDataContainer().set(PATTERN_MODIFIED_KEY, PersistentDataType.STRING, var9);
            var10.setItemMeta(var11);
            this.inventory.setItem(var1[var2++], var10);
         }
      }

      Particle var19 = null;

      try {
         var19 = Particle.valueOf(this.getEditedSection().getString("item-particles.particle"));
      } catch (Exception var17) {
      }

      ItemStack var20 = new ItemStack(Material.BLAZE_POWDER);
      ItemMeta var21 = var20.getItemMeta();
      var21.setDisplayName(ChatColor.GREEN + "Particle");
      ArrayList var22 = new ArrayList();
      var22.add(ChatColor.GRAY + "Defines what particle is used");
      var22.add(ChatColor.GRAY + "in the particle effect.");
      var22.add("");
      var22.add(
         ChatColor.GRAY
            + "Current Value: "
            + (
               var19 == null
                  ? ChatColor.RED + "No particle selected."
                  : ChatColor.GOLD + UtilityMethods.caseOnWords(var19.name().toLowerCase().replace("_", " "))
            )
      );
      var22.add("");
      var22.add(ChatColor.YELLOW + "►" + " Click to change this value.");
      var22.add(ChatColor.YELLOW + "►" + " Right click to change this value.");
      var21.setLore(var22);
      var20.setItemMeta(var21);
      if (var19 != null && MMOUtils.isColorable(var19)) {
         int var23 = this.getEditedSection().getInt("item-particles.color.red");
         int var25 = this.getEditedSection().getInt("item-particles.color.green");
         int var13 = this.getEditedSection().getInt("item-particles.color.blue");
         ItemStack var14 = new ItemStack(Material.RED_DYE);
         ItemMeta var15 = var14.getItemMeta();
         var15.setDisplayName(ChatColor.GREEN + "Particle Color");
         ArrayList var16 = new ArrayList();
         var16.add(ChatColor.GRAY + "The RGB color of your particle.");
         var16.add("");
         var16.add(ChatColor.GRAY + "Current Value (R-G-B):");
         var16.add(
            ""
               + ChatColor.RED
               + ChatColor.BOLD
               + var23
               + ChatColor.GRAY
               + " - "
               + ChatColor.GREEN
               + ChatColor.BOLD
               + var25
               + ChatColor.GRAY
               + " - "
               + ChatColor.BLUE
               + ChatColor.BOLD
               + var13
         );
         var16.add("");
         var16.add(ChatColor.YELLOW + "►" + " Click to change this value.");
         var16.add(ChatColor.YELLOW + "►" + " Right click to change this value.");
         var15.setLore(var16);
         var14.setItemMeta(var15);
         this.inventory.setItem(25, var14);
      }

      ItemStack var24 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemMeta var26 = var24.getItemMeta();
      var26.setDisplayName(ChatColor.RED + "- No Modifier -");
      var24.setItemMeta(var26);

      while (var2 < var1.length) {
         this.inventory.setItem(var1[var2++], var24);
      }

      this.inventory.setItem(21, var4);
      this.inventory.setItem(23, var20);
   }

   @Override
   public void whenClicked(InventoryClickEvent var1) {
      ItemStack var2 = var1.getCurrentItem();
      var1.setCancelled(true);
      if (var1.getInventory() == var1.getClickedInventory() && MMOUtils.isMetaItem(var2, false)) {
         if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Particle")) {
            if (var1.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.ITEM_PARTICLES, "particle").enable("Write in the chat the particle you want.");
            }

            if (var1.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("item-particles.particle")) {
               this.getEditedSection().set("item-particles.particle", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the particle.");
            }
         }

         if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Particle Color")) {
            if (var1.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.ITEM_PARTICLES, "particle-color")
                  .enable("Write in the chat the RGB color you want.", ChatColor.AQUA + "Format: [RED] [GREEN] [BLUE]");
            }

            if (var1.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("item-particles.color")) {
               this.getEditedSection().set("item-particles.color", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the particle color.");
            }
         }

         if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Particle Pattern")) {
            if (var1.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.ITEM_PARTICLES, "particle-type").enable("Write in the chat the particle type you want.");
               this.player.sendMessage("");
               this.player.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "Available Particles Patterns");

               for (ParticleType var6 : ParticleType.values()) {
                  this.player.sendMessage("* " + ChatColor.GREEN + var6.name());
               }
            }

            if (var1.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("item-particles.type")) {
               this.getEditedSection().set("item-particles.type", null);

               for (String var9 : this.getEditedSection().getConfigurationSection("item-particles").getKeys(false)) {
                  if (!var9.equals("particle")) {
                     this.getEditedSection().set("item-particles." + var9, null);
                  }
               }

               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the particle pattern.");
            }
         }

         String var8 = (String)var2.getItemMeta().getPersistentDataContainer().get(PATTERN_MODIFIED_KEY, PersistentDataType.STRING);
         if (var8 != null && !var8.equals("")) {
            if (var1.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.ITEM_PARTICLES, var8).enable("Write in the chat the value you want.");
            }

            if (var1.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("item-particles." + var8)) {
               this.getEditedSection().set("item-particles." + var8, null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset " + ChatColor.GOLD + var8 + ChatColor.GRAY + ".");
            }
         }
      }
   }
}
