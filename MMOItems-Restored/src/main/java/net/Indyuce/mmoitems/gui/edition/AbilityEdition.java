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

   public AbilityEdition(Player var1, MMOItemTemplate var2, String var3) {
      super(var1, var2);
      this.configKey = var3;
   }

   @Override
   public String getName() {
      return "Ability Edition";
   }

   @Override
   public void arrangeInventory() {
      int var1 = 0;
      String var2 = this.getEditedSection().getString("ability." + this.configKey + ".type");
      String var3 = var2 == null ? "" : var2.toUpperCase().replace(" ", "_").replace("-", "_").replaceAll("[^A-Z_]", "");
      this.ability = MMOItems.plugin.getSkills().hasSkill(var3) ? MMOItems.plugin.getSkills().getSkill(var3) : null;
      ItemStack var4 = new ItemStack(Material.BLAZE_POWDER);
      ItemMeta var5 = var4.getItemMeta();
      var5.setDisplayName(ChatColor.GREEN + "Ability");
      ArrayList var6 = new ArrayList();
      var6.add(ChatColor.GRAY + "Choose what ability your weapon will cast.");
      var6.add("");
      var6.add(ChatColor.GRAY + "Current Value: " + (this.ability == null ? ChatColor.RED + "No ability selected." : ChatColor.GOLD + this.ability.getName()));
      var6.add("");
      var6.add(ChatColor.YELLOW + "►" + " Left click to select.");
      var6.add(ChatColor.YELLOW + "►" + " Right click to reset.");
      var5.setLore(var6);
      var4.setItemMeta(var5);
      if (this.ability != null) {
         String var7 = this.getEditedSection().getString("ability." + this.configKey + ".mode");
         String var8 = var7 == null ? "" : var7.toUpperCase().replace(" ", "_").replace("-", "_").replaceAll("[^A-Z0-9_]", "");

         TriggerType var9;
         try {
            var9 = TriggerType.valueOf(var8);
         } catch (RuntimeException var15) {
            var9 = null;
         }

         ItemStack var10 = new ItemStack(Material.ARMOR_STAND);
         ItemMeta var11 = var10.getItemMeta();
         var11.setDisplayName(ChatColor.GREEN + "Trigger");
         ArrayList var12 = new ArrayList();
         var12.add(ChatColor.GRAY + "Choose what action the player needs to");
         var12.add(ChatColor.GRAY + "perform in order to cast your ability.");
         var12.add("");
         var12.add(ChatColor.GRAY + "Current Value: " + (var9 == null ? ChatColor.RED + "No trigger selected." : ChatColor.GOLD + var9.getName()));
         var12.add("");
         var12.add(ChatColor.YELLOW + "►" + " Left click to select.");
         var12.add(ChatColor.YELLOW + "►" + " Right click to reset.");
         var11.setLore(var12);
         var10.setItemMeta(var11);
         this.inventory.setItem(30, var10);
      }

      if (this.ability != null) {
         ConfigurationSection var16 = this.getEditedSection().getConfigurationSection("ability." + this.configKey);

         for (String var20 : this.ability.getHandler().getModifiers()) {
            ItemStack var21 = new ItemStack(Material.GRAY_DYE);
            ItemMeta var22 = var21.getItemMeta();
            var22.setDisplayName(ChatColor.GREEN + UtilityMethods.caseOnWords(var20.toLowerCase().replace("-", " ")));
            ArrayList var23 = new ArrayList();
            var23.add("" + ChatColor.GRAY + ChatColor.ITALIC + "This is an ability modifier. Changing this");
            var23.add("" + ChatColor.GRAY + ChatColor.ITALIC + "value will slightly customize the ability.");
            var23.add("");

            try {
               var23.add(
                  ChatColor.GRAY
                     + "Current Value: "
                     + ChatColor.GOLD
                     + (
                        var16.contains(var20)
                           ? new NumericStatFormula(var16.get(var20)).toString()
                           : MODIFIER_FORMAT.format(this.ability.getDefaultModifier(var20))
                     )
               );
            } catch (IllegalArgumentException var14) {
               var23.add(ChatColor.GRAY + "Could not read value. Using default");
            }

            var23.add(ChatColor.GRAY + "Default Value: " + ChatColor.GOLD + MODIFIER_FORMAT.format(this.ability.getDefaultModifier(var20)));
            var23.add("");
            var23.add(ChatColor.YELLOW + "►" + " Click to change this value.");
            var23.add(ChatColor.YELLOW + "►" + " Right click to reset.");
            var22.setLore(var23);
            var22.getPersistentDataContainer().set(ABILITY_MOD_KEY, PersistentDataType.STRING, var20);
            var21.setItemMeta(var22);
            this.inventory.setItem(slots[var1++], var21);
         }
      }

      ItemStack var17 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemMeta var19 = var17.getItemMeta();
      var19.setDisplayName(ChatColor.RED + "- No Modifier -");
      var17.setItemMeta(var19);

      while (var1 < slots.length) {
         this.inventory.setItem(slots[var1++], var17);
      }

      this.addEditionItems();
      this.inventory.setItem(28, var4);
   }

   @Override
   public void whenClicked(InventoryClickEvent var1) {
      ItemStack var2 = var1.getCurrentItem();
      var1.setCancelled(true);
      if (var1.getInventory() == var1.getClickedInventory() && MMOUtils.isMetaItem(var2, false)) {
         if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "⇨" + " Back")) {
            new AbilityListEdition(this.player, this.template).open(this);
         } else if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Ability")) {
            if (var1.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.ABILITIES, this.configKey, "ability")
                  .enable("Write in the chat the ability you want.", "You can access the ability list by typing " + ChatColor.AQUA + "/mi list ability");
            }

            if (var1.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("ability." + this.configKey + ".type")) {
               this.getEditedSection().set("ability." + this.configKey, null);
               if (this.getEditedSection().contains("ability") && this.getEditedSection().getConfigurationSection("ability").getKeys(false).size() == 0) {
                  this.getEditedSection().set("ability", null);
               }

               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the ability.");
            }
         } else if (!var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Trigger")) {
            String var5 = (String)var2.getItemMeta().getPersistentDataContainer().get(ABILITY_MOD_KEY, PersistentDataType.STRING);
            if (var5 != null && !var5.equals("")) {
               if (var1.getAction() == InventoryAction.PICKUP_ALL) {
                  new StatEdition(this, ItemStats.ABILITIES, this.configKey, var5).enable("Write in the chat the value you want.");
               }

               if (var1.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("ability." + this.configKey + "." + var5)) {
                  this.getEditedSection().set("ability." + this.configKey + "." + var5, null);
                  this.registerTemplateEdition();
                  this.player
                     .sendMessage(
                        MMOItems.plugin.getPrefix()
                           + "Successfully reset "
                           + ChatColor.GOLD
                           + UtilityMethods.caseOnWords(var5.replace("-", " "))
                           + ChatColor.GRAY
                           + "."
                     );
               }
            }
         } else {
            if (var1.getAction() == InventoryAction.PICKUP_ALL) {
               new StatEdition(this, ItemStats.ABILITIES, this.configKey, "mode").enable();
               this.player.sendMessage("");
               this.player.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "Available Triggers");

               for (TriggerType var4 : TriggerType.values()) {
                  this.player.sendMessage("* " + ChatColor.GREEN + var4.name());
               }
            }

            if (var1.getAction() == InventoryAction.PICKUP_HALF && this.getEditedSection().contains("ability." + this.configKey + ".mode")) {
               this.getEditedSection().set("ability." + this.configKey + ".mode", null);
               this.registerTemplateEdition();
               this.player.sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the ability trigger.");
            }
         }
      }
   }
}
