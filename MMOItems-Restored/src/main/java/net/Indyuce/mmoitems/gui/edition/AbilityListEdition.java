package net.Indyuce.mmoitems.gui.edition;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import java.util.ArrayList;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.skill.RegisteredSkill;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class AbilityListEdition extends EditionInventory {
   private static final int[] slots = new int[]{19, 20, 21, 22, 23, 24, 25};
   private static final NamespacedKey CONFIG_KEY = new NamespacedKey(MMOItems.plugin, "ConfigKey");

   public AbilityListEdition(Player player, MMOItemTemplate mmoItemTemplate) {
      super(player, mmoItemTemplate);
   }

   @Override
   public String getName() {
      return "Ability List";
   }

   @Override
   public void arrangeInventory() {
      int count = 0;
      if (this.getEditedSection().contains("ability")) {
         for (String key : this.getEditedSection().getConfigurationSection("ability").getKeys(false)) {
            String text = this.getEditedSection().getString("ability." + key + ".type");
            String text2;
            RegisteredSkill registeredSkill = text != null && MMOItems.plugin.getSkills().hasSkill(text2 = text.toUpperCase().replace(" ", "_").replace("-", "_"))
               ? MMOItems.plugin.getSkills().getSkill(text2)
               : null;

            TriggerType triggerType;
            try {
               triggerType = TriggerType.valueOf(UtilityMethods.enumName(this.getEditedSection().getString("ability." + key + ".mode")));
            } catch (RuntimeException exception) {
               triggerType = null;
            }

            ItemStack item = new ItemStack(Material.BLAZE_POWDER);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(registeredSkill != null ? ChatColor.GREEN + registeredSkill.getName() : ChatColor.RED + "! No Ability Selected !");
            ArrayList list = new ArrayList();
            list.add("");
            list.add(ChatColor.GRAY + "Cast Mode: " + (triggerType != null ? ChatColor.GOLD + triggerType.getName() : ChatColor.RED + "Not Selected"));
            list.add("");
            boolean flag = false;
            if (registeredSkill != null) {
               for (String key2 : this.getEditedSection().getConfigurationSection("ability." + key).getKeys(false)) {
                  if (!key2.equals("type") && !key2.equals("mode") && registeredSkill.getHandler().getModifiers().contains(key2)) {
                     try {
                        list.add(
                           ChatColor.GRAY
                              + "* "
                              + UtilityMethods.caseOnWords(key2.toLowerCase().replace("-", " "))
                              + ": "
                              + ChatColor.GOLD
                              + new NumericStatFormula(this.getEditedSection().get("ability." + key + "." + key2)).toString()
                        );
                        flag = true;
                     } catch (IllegalArgumentException exception) {
                        list.add(
                           ChatColor.GRAY + "* " + UtilityMethods.caseOnWords(key2.toLowerCase().replace("-", " ")) + ": " + ChatColor.GOLD + "Unreadable"
                        );
                     }
                  }
               }
            }

            if (flag) {
               list.add("");
            }

            list.add(ChatColor.YELLOW + "►" + " Left click to edit.");
            list.add(ChatColor.YELLOW + "►" + " Right click to remove.");
            itemMeta.setLore(list);
            itemMeta.getPersistentDataContainer().set(CONFIG_KEY, PersistentDataType.STRING, key);
            item.setItemMeta(itemMeta);
            this.inventory.setItem(slots[count++], item);
         }
      }

      ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemMeta itemMeta = item.getItemMeta();
      itemMeta.setDisplayName(ChatColor.RED + "- No Ability -");
      item.setItemMeta(itemMeta);
      ItemStack item2 = new ItemStack(Material.WRITABLE_BOOK);
      ItemMeta itemMeta2 = item2.getItemMeta();
      itemMeta2.setDisplayName(ChatColor.GREEN + "Add an ability...");
      item2.setItemMeta(itemMeta2);
      this.inventory.setItem(40, item2);

      while (count < slots.length) {
         this.inventory.setItem(slots[count++], item);
      }
   }

   @Override
   public void whenClicked(InventoryClickEvent event) {
      ItemStack currentItem = event.getCurrentItem();
      event.setCancelled(true);
      if (event.getInventory() == event.getClickedInventory() && MMOUtils.isMetaItem(currentItem, false)) {
         if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Add an ability...")) {
            if (!this.getEditedSection().contains("ability")) {
               this.getEditedSection().createSection("ability.ability1");
               this.registerTemplateEdition();
               return;
            }

            if (this.getEditedSection().getConfigurationSection("ability").getKeys(false).size() > 6) {
               this.player.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "You've hit the 7 abilities per item limit.");
               return;
            }

            for (int i = 1; i < 8; i++) {
               if (!this.getEditedSection().getConfigurationSection("ability").contains("ability" + i)) {
                  String text = "ability" + i;
                  this.getEditedSection().set("ability." + text + ".type", "FIREBOLT");
                  this.getEditedSection().set("ability." + text + ".mode", "RIGHT_CLICK");
                  this.registerTemplateEdition();
                  return;
               }
            }
         }

         String text = (String)currentItem.getItemMeta().getPersistentDataContainer().get(CONFIG_KEY, PersistentDataType.STRING);
         if (text != null && !text.equals("")) {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
               new AbilityEdition(this.player, this.template, text).open(this);
            }

            if (event.getAction() == InventoryAction.PICKUP_HALF
               && this.getEditedSection().contains("ability")
               && this.getEditedSection().getConfigurationSection("ability").contains(text)) {
               this.getEditedSection().set("ability." + text, null);
               this.registerTemplateEdition();
               this.player
                  .sendMessage(
                     MMOItems.plugin.getPrefix()
                        + "Successfully removed "
                        + ChatColor.GOLD
                        + text
                        + ChatColor.DARK_GRAY
                        + " (Internal ID)"
                        + ChatColor.GRAY
                        + "."
                  );
            }
         }
      }
   }
}
