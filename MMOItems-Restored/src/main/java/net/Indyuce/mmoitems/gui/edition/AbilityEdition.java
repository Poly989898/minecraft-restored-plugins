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
      String normalizedAbility = text == null ? "" : text.toUpperCase().replace(" ", "_").replace("-", "_").replaceAll("[^A-Z_]", "");
      this.ability = MMOItems.plugin.getSkills().hasSkill(normalizedAbility) ? MMOItems.plugin.getSkills().getSkill(normalizedAbility) : null;
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
         String modeKey = this.getEditedSection().getString("ability." + this.configKey + ".mode");
         String normalizedMode = modeKey == null ? "" : modeKey.toUpperCase().replace(" ", "_").replace("-", "_").replaceAll("[^A-Z0-9_]", "");

         TriggerType triggerType;
         try {
            triggerType = TriggerType.valueOf(normalizedMode);
         } catch (RuntimeException exception) {
            triggerType = null;
         }

         ItemStack triggerItem = new ItemStack(Material.ARMOR_STAND);
         ItemMeta triggerMeta = triggerItem.getItemMeta();
         triggerMeta.setDisplayName(ChatColor.GREEN + "Trigger");
         ArrayList triggerLore = new ArrayList();
         triggerLore.add(ChatColor.GRAY + "Choose what action the player needs to");
         triggerLore.add(ChatColor.GRAY + "perform in order to cast your ability.");
         triggerLore.add("");
         triggerLore.add(ChatColor.GRAY + "Current Value: " + (triggerType == null ? ChatColor.RED + "No trigger selected." : ChatColor.GOLD + triggerType.getName()));
         triggerLore.add("");
         triggerLore.add(ChatColor.YELLOW + "►" + " Left click to select.");
         triggerLore.add(ChatColor.YELLOW + "►" + " Right click to reset.");
         triggerMeta.setLore(triggerLore);
         triggerItem.setItemMeta(triggerMeta);
         this.inventory.setItem(30, triggerItem);
      }

      if (this.ability != null) {
         ConfigurationSection section = this.getEditedSection().getConfigurationSection("ability." + this.configKey);

         for (String modifier : this.ability.getHandler().getModifiers()) {
            ItemStack grayDyeItem = new ItemStack(Material.GRAY_DYE);
            ItemMeta grayDyeMeta = grayDyeItem.getItemMeta();
            grayDyeMeta.setDisplayName(ChatColor.GREEN + UtilityMethods.caseOnWords(modifier.toLowerCase().replace("-", " ")));
            ArrayList grayDyeLore = new ArrayList();
            grayDyeLore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "This is an ability modifier. Changing this");
            grayDyeLore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "value will slightly customize the ability.");
            grayDyeLore.add("");

            try {
               grayDyeLore.add(
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
               grayDyeLore.add(ChatColor.GRAY + "Could not read value. Using default");
            }

            grayDyeLore.add(ChatColor.GRAY + "Default Value: " + ChatColor.GOLD + MODIFIER_FORMAT.format(this.ability.getDefaultModifier(modifier)));
            grayDyeLore.add("");
            grayDyeLore.add(ChatColor.YELLOW + "►" + " Click to change this value.");
            grayDyeLore.add(ChatColor.YELLOW + "►" + " Right click to reset.");
            grayDyeMeta.setLore(grayDyeLore);
            grayDyeMeta.getPersistentDataContainer().set(ABILITY_MOD_KEY, PersistentDataType.STRING, modifier);
            grayDyeItem.setItemMeta(grayDyeMeta);
            this.inventory.setItem(slots[count++], grayDyeItem);
         }
      }

      ItemStack noModifierItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemMeta noModifierMeta = noModifierItem.getItemMeta();
      noModifierMeta.setDisplayName(ChatColor.RED + "- No Modifier -");
      noModifierItem.setItemMeta(noModifierMeta);

      while (count < slots.length) {
         this.inventory.setItem(slots[count++], noModifierItem);
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
