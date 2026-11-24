package net.Indyuce.mmoitems.gui.edition;

import io.lumine.mythic.lib.UtilityMethods;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.CustomSound;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SoundsEdition extends EditionInventory {
   public static final Map<Integer, String> CORRESPONDING_SLOT = new HashMap<>();

   public SoundsEdition(Player var1, MMOItemTemplate var2) {
      super(var1, var2);
   }

   @Override
   public String getName() {
      return "Custom Sounds: " + this.template.getId();
   }

   @Override
   public void arrangeInventory() {
      for (CustomSound var4 : CustomSound.values()) {
         ItemStack var5 = var4.getItem().clone();
         ItemMeta var6 = var5.getItemMeta();
         var6.addItemFlags(ItemFlag.values());
         var6.setDisplayName(ChatColor.GREEN + var4.getName());
         ArrayList var7 = new ArrayList();

         for (String var11 : var4.getLore()) {
            var7.add(ChatColor.GRAY + var11);
         }

         var7.add("");
         String var12 = var4.getName().replace(" ", "-").toLowerCase();
         String var13 = this.getEditedSection().getString("sounds." + var12 + ".sound");
         if (var13 != null) {
            var7.add(ChatColor.GRAY + "Current Values:");
            var7.add(
               ChatColor.GRAY + " - Sound Name: '" + ChatColor.GREEN + this.getEditedSection().getString("sounds." + var12 + ".sound") + ChatColor.GRAY + "'"
            );
            var7.add(ChatColor.GRAY + " - Volume: " + ChatColor.GREEN + this.getEditedSection().getDouble("sounds." + var12 + ".volume"));
            var7.add(ChatColor.GRAY + " - Pitch: " + ChatColor.GREEN + this.getEditedSection().getDouble("sounds." + var12 + ".pitch"));
         } else {
            var7.add(ChatColor.GRAY + "Current Values: " + ChatColor.RED + "None");
         }

         var7.add("");
         var7.add(ChatColor.YELLOW + "►" + " Click to change this value.");
         var7.add(ChatColor.YELLOW + "►" + " Right click to remove this value.");
         var6.setLore(var7);
         var5.setItemMeta(var6);
         this.inventory.setItem(var4.getSlot(), var5);
      }
   }

   @Override
   public void whenClicked(InventoryClickEvent var1) {
      ItemStack var2 = var1.getCurrentItem();
      var1.setCancelled(true);
      if (var1.getInventory() == var1.getClickedInventory() && MMOUtils.isMetaItem(var2, false)) {
         if (CORRESPONDING_SLOT.containsKey(var1.getSlot())) {
            if (var1.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.CUSTOM_SOUNDS, CORRESPONDING_SLOT.get(var1.getSlot()))
                  .enable(
                     "Write in the chat the custom sound you want to add.",
                     ChatColor.AQUA + "Format: [SOUND NAME] [VOLUME] [PITCH]",
                     ChatColor.AQUA + "Example: entity.generic.drink 1 1"
                  );
            }

            if (var1.getAction() == InventoryAction.PICKUP_HALF) {
               String var3 = CORRESPONDING_SLOT.get(var1.getSlot());
               this.getEditedSection().set("sounds." + var3, null);
               if (this.getEditedSection().contains("sounds." + var3)
                  && this.getEditedSection().getConfigurationSection("sounds." + var3).getKeys(false).isEmpty()) {
                  this.getEditedSection().set("sounds." + var3, null);
                  if (this.getEditedSection().getConfigurationSection("sounds").getKeys(false).isEmpty()) {
                     this.getEditedSection().set("sounds", null);
                  }
               }

               this.registerTemplateEdition();
               this.player
                  .sendMessage(
                     MMOItems.plugin.getPrefix()
                        + ChatColor.RED
                        + UtilityMethods.caseOnWords(var3.replace("-", " "))
                        + " Sound"
                        + ChatColor.GRAY
                        + " successfully removed."
                  );
            }
         }
      }
   }

   static {
      for (CustomSound var3 : CustomSound.values()) {
         CORRESPONDING_SLOT.put(var3.getSlot(), var3.name().replace("_", "-").toLowerCase());
      }
   }
}
