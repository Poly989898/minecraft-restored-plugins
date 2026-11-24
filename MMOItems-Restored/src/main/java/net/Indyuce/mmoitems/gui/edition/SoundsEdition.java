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

   public SoundsEdition(Player player, MMOItemTemplate mmoItemTemplate) {
      super(player, mmoItemTemplate);
   }

   @Override
   public String getName() {
      return "Custom Sounds: " + this.template.getId();
   }

   @Override
   public void arrangeInventory() {
      for (CustomSound customSound : CustomSound.values()) {
         ItemStack item = customSound.getItem().clone();
         ItemMeta itemMeta = item.getItemMeta();
         itemMeta.addItemFlags(ItemFlag.values());
         itemMeta.setDisplayName(ChatColor.GREEN + customSound.getName());
         ArrayList list = new ArrayList();

         for (String text : customSound.getLore()) {
            list.add(ChatColor.GRAY + text);
         }

         list.add("");
         String text = customSound.getName().replace(" ", "-").toLowerCase();
         String text2 = this.getEditedSection().getString("sounds." + text + ".sound");
         if (text2 != null) {
            list.add(ChatColor.GRAY + "Current Values:");
            list.add(
               ChatColor.GRAY + " - Sound Name: '" + ChatColor.GREEN + this.getEditedSection().getString("sounds." + text + ".sound") + ChatColor.GRAY + "'"
            );
            list.add(ChatColor.GRAY + " - Volume: " + ChatColor.GREEN + this.getEditedSection().getDouble("sounds." + text + ".volume"));
            list.add(ChatColor.GRAY + " - Pitch: " + ChatColor.GREEN + this.getEditedSection().getDouble("sounds." + text + ".pitch"));
         } else {
            list.add(ChatColor.GRAY + "Current Values: " + ChatColor.RED + "None");
         }

         list.add("");
         list.add(ChatColor.YELLOW + "►" + " Click to change this value.");
         list.add(ChatColor.YELLOW + "►" + " Right click to remove this value.");
         itemMeta.setLore(list);
         item.setItemMeta(itemMeta);
         this.inventory.setItem(customSound.getSlot(), item);
      }
   }

   @Override
   public void whenClicked(InventoryClickEvent event) {
      ItemStack currentItem = event.getCurrentItem();
      event.setCancelled(true);
      if (event.getInventory() == event.getClickedInventory() && MMOUtils.isMetaItem(currentItem, false)) {
         if (CORRESPONDING_SLOT.containsKey(event.getSlot())) {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.CUSTOM_SOUNDS, CORRESPONDING_SLOT.get(event.getSlot()))
                  .enable(
                     "Write in the chat the custom sound you want to add.",
                     ChatColor.AQUA + "Format: [SOUND NAME] [VOLUME] [PITCH]",
                     ChatColor.AQUA + "Example: entity.generic.drink 1 1"
                  );
            }

            if (event.getAction() == InventoryAction.PICKUP_HALF) {
               String text = CORRESPONDING_SLOT.get(event.getSlot());
               this.getEditedSection().set("sounds." + text, null);
               if (this.getEditedSection().contains("sounds." + text)
                  && this.getEditedSection().getConfigurationSection("sounds." + text).getKeys(false).isEmpty()) {
                  this.getEditedSection().set("sounds." + text, null);
                  if (this.getEditedSection().getConfigurationSection("sounds").getKeys(false).isEmpty()) {
                     this.getEditedSection().set("sounds", null);
                  }
               }

               this.registerTemplateEdition();
               this.player
                  .sendMessage(
                     MMOItems.plugin.getPrefix()
                        + ChatColor.RED
                        + UtilityMethods.caseOnWords(text.replace("-", " "))
                        + " Sound"
                        + ChatColor.GRAY
                        + " successfully removed."
                  );
            }
         }
      }
   }

   static {
      for (CustomSound customSound : CustomSound.values()) {
         CORRESPONDING_SLOT.put(customSound.getSlot(), customSound.name().replace("_", "-").toLowerCase());
      }
   }
}
