package net.Indyuce.mmoitems.gui.edition;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import java.text.DecimalFormat;
import java.util.ArrayList;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.skill.RegisteredSkill;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class AbilityEdition extends EditionInventory {
   private final String configKey;
   private RegisteredSkill ability;
   private static final DecimalFormat MODIFIER_FORMAT = new DecimalFormat("0.###");
   private static final int[] slots = new int[]{23, 24, 25, 32, 33, 34, 41, 42, 43, 50, 51, 52};
   private static final NamespacedKey ABILITY_MOD_KEY = new NamespacedKey(MMOItems.plugin, "AbilityModifier");

   public AbilityEdition(Player player, MMOItemTemplate mmoItemTemplate, String text) {
      super(player, mmoItemTemplate);
      this.configKey = text;
   }

   @Override
   public String getName() {
      return "Ability Edition";
   }

   @Override
   public void arrangeInventory() {
      int count = 0;
      String text = this.getEditedSection().getString("ability." + this.configKey + ".type");
      String text2 = text == null ? "" : text.toUpperCase().replace(" ", "_").replace("-", "_").replaceAll("[^A-Z_]", "");
      this.ability = MMOItems.plugin.getSkills().hasSkill(text2) ? MMOItems.plugin.getSkills().getSkill(text2) : null;
      ItemStack item = new ItemStack(Material.BLAZE_POWDER);
      ItemMeta itemMeta = item.getItemMeta();
      itemMeta.setDisplayName(ChatColor.GREEN + "Ability");
      ArrayList list = new ArrayList();
      list.add(ChatColor.GRAY + "Choose what ability your weapon will cast.");
      list.add("");
      list.add(ChatColor.GRAY + "Current Value: " + (this.ability == null ? ChatColor.RED + "No ability selected." : ChatColor.GOLD + this.ability.getName()));
      list.add("");
      list.add(ChatColor.YELLOW + "►" + " Left click to select.");
      list.add(ChatColor.YELLOW + "►" + " Right click to reset.");
      itemMeta.setLore(list);
      item.setItemMeta(itemMeta);
      if (this.ability != null) {
         String text3 = this.getEditedSection().getString("ability." + this.configKey + ".mode");
         String text4 = text3 == null ? "" : text3.toUpperCase().replace(" ", "_").replace("-", "_").replaceAll("[^A-Z0-9_]", "");

         TriggerType triggerType;
         try {
            triggerType = TriggerType.valueOf(text4);
         } catch (RuntimeException exception) {
            triggerType = null;
         }

         ItemStack item2 = new ItemStack(Material.ARMOR_STAND);
         ItemMeta itemMeta2 = item2.getItemMeta();
         itemMeta2.setDisplayName(ChatColor.GREEN + "Trigger");
         ArrayList list2 = new ArrayList();
         list2.add(ChatColor.GRAY + "Choose what action the player needs to");
         list2.add(ChatColor.GRAY + "perform in order to cast your ability.");
         list2.add("");
         list2.add(ChatColor.GRAY + "Current Value: " + (triggerType == null ? ChatColor.RED + "No trigger selected." : ChatColor.GOLD + triggerType.getName()));
         list2.add("");
         list2.add(ChatColor.YELLOW + "►" + " Left click to select.");
         list2.add(ChatColor.YELLOW + "►" + " Right click to reset.");
         itemMeta2.setLore(list2);
         item2.setItemMeta(itemMeta2);
         this.inventory.setItem(30, item2);
      }

      if (this.ability != null) {
         ConfigurationSection section = this.getEditedSection().getConfigurationSection("ability." + this.configKey);

         for (String modifier : this.ability.getHandler().getModifiers()) {
            ItemStack item2 = new ItemStack(Material.GRAY_DYE);
            ItemMeta itemMeta2 = item2.getItemMeta();
            itemMeta2.setDisplayName(ChatColor.GREEN + UtilityMethods.caseOnWords(modifier.toLowerCase().replace("-", " ")));
            ArrayList list2 = new ArrayList();
            list2.add("" + ChatColor.GRAY + ChatColor.ITALIC + "This is an ability modifier. Changing this");
            list2.add("" + ChatColor.GRAY + ChatColor.ITALIC + "value will slightly customize the ability.");
            list2.add("");

            try {
               list2.add(
                  ChatColor.GRAY
                     + "Current Value: "
                     + ChatColor.GOLD
                     + (
                        section.contains(modifier)
                           ? new NumericStatFormula(section.get(modifier)).toString()
                           : MODIFIER_FORMAT.format(this.ability.getDefaultModifier(modifier))
                     )
               );
            } catch (IllegalArgumentException exception) {
               list2.add(ChatColor.GRAY + "Could not read value. Using default");
            }

            list2.add(ChatColor.GRAY + "Default Value: " + ChatColor.GOLD + MODIFIER_FORMAT.format(this.ability.getDefaultModifier(modifier)));
            list2.add("");
            list2.add(ChatColor.YELLOW + "►" + " Click to change this value.");
            list2.add(ChatColor.YELLOW + "►" + " Right click to reset.");
            itemMeta2.setLore(list2);
            itemMeta2.getPersistentDataContainer().set(ABILITY_MOD_KEY, PersistentDataType.STRING, modifier);
            item2.setItemMeta(itemMeta2);
            this.inventory.setItem(slots[count++], item2);
         }
      }

      ItemStack item2 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemMeta itemMeta2 = item2.getItemMeta();
      itemMeta2.setDisplayName(ChatColor.RED + "- No Modifier -");
      item2.setItemMeta(itemMeta2);

      while (count < slots.length) {
         this.inventory.setItem(slots[count++], item2);
      }

      this.addEditionItems();
      this.inventory.setItem(28, item);
   }

   @Override
   public void whenClicked(InventoryClickEvent event) {
      ItemStack currentItem = event.getCurrentItem();
      event.setCancelled(true);
      if (event.getInventory() == event.getClickedInventory() && MMOUtils.isMetaItem(currentItem, false)) {
         if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "⇨" + " Back")) {
            new AbilityListEdition(this.player, this.template).open(this);
         } else if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Ability")) {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.ABILITIES, this.configKey, "ability")
                  .enable("Write in the chat the ability you want.", "You can access the ability list by typing " + ChatColor.AQUA + "/mi list ability");
            }

            if (event.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("ability." + this.configKey + ".type")) {
               this.getEditedSection().set("ability." + this.configKey, null);
               if (this.getEditedSection().contains("ability") && this.getEditedSection().getConfigurationSection("ability").getKeys(false).size() == 0) {
                  this.getEditedSection().set("ability", null);
               }

               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the ability.");
            }
         } else if (!currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Trigger")) {
            String text = (String)currentItem.getItemMeta().getPersistentDataContainer().get(ABILITY_MOD_KEY, PersistentDataType.STRING);
            if (text != null && !text.equals("")) {
               if (event.getAction() == InventoryAction.PICKUP_ALL) {
                  new StatEdition(this, ItemStats.ABILITIES, this.configKey, text).enable("Write in the chat the value you want.");
               }

               if (event.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("ability." + this.configKey + "." + text)) {
                  this.getEditedSection().set("ability." + this.configKey + "." + text, null);
                  this.registerTemplateEdition();
                  this.player
                     .sendMessage(
                        MMOItems.plugin.getPrefix()
                           + "Successfully reset "
                           + ChatColor.GOLD
                           + UtilityMethods.caseOnWords(text.replace("-", " "))
                           + ChatColor.GRAY
                           + "."
                     );
               }
            }
         } else {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.ABILITIES, this.configKey, "mode").enable();
               this.player.sendMessage("");
               this.player.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "Available Triggers");

               for (TriggerType triggerType : TriggerType.values()) {
                  this.player.sendMessage("* " + ChatColor.GREEN + triggerType.name());
               }
            }

            if (event.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("ability." + this.configKey + ".mode")) {
               this.getEditedSection().set("ability." + this.configKey + ".mode", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the ability trigger.");
            }
         }
      }
   }
}
