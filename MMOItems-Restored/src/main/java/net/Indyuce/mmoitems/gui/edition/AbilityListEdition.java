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

   public AbilityListEdition(Player var1, MMOItemTemplate var2) {
      super(var1, var2);
   }

   @Override
   public String getName() {
      return "Ability List";
   }

   @Override
   public void arrangeInventory() {
      int var1 = 0;
      if (this.getEditedSection().contains("ability")) {
         for (String var3 : this.getEditedSection().getConfigurationSection("ability").getKeys(false)) {
            String var4 = this.getEditedSection().getString("ability." + var3 + ".type");
            String var18;
            RegisteredSkill var5 = var4 != null && MMOItems.plugin.getSkills().hasSkill(var18 = var4.toUpperCase().replace(" ", "_").replace("-", "_"))
               ? MMOItems.plugin.getSkills().getSkill(var18)
               : null;

            TriggerType var6;
            try {
               var6 = TriggerType.valueOf(UtilityMethods.enumName(this.getEditedSection().getString("ability." + var3 + ".mode")));
            } catch (RuntimeException var15) {
               var6 = null;
            }

            ItemStack var7 = new ItemStack(Material.BLAZE_POWDER);
            ItemMeta var8 = var7.getItemMeta();
            var8.setDisplayName(var5 != null ? ChatColor.GREEN + var5.getName() : ChatColor.RED + "! No Ability Selected !");
            ArrayList var9 = new ArrayList();
            var9.add("");
            var9.add(ChatColor.GRAY + "Cast Mode: " + (var6 != null ? ChatColor.GOLD + var6.getName() : ChatColor.RED + "Not Selected"));
            var9.add("");
            boolean var10 = false;
            if (var5 != null) {
               for (String var12 : this.getEditedSection().getConfigurationSection("ability." + var3).getKeys(false)) {
                  if (!var12.equals("type") && !var12.equals("mode") && var5.getHandler().getModifiers().contains(var12)) {
                     try {
                        var9.add(
                           ChatColor.GRAY
                              + "* "
                              + UtilityMethods.caseOnWords(var12.toLowerCase().replace("-", " "))
                              + ": "
                              + ChatColor.GOLD
                              + new NumericStatFormula(this.getEditedSection().get("ability." + var3 + "." + var12)).toString()
                        );
                        var10 = true;
                     } catch (IllegalArgumentException var14) {
                        var9.add(
                           ChatColor.GRAY + "* " + UtilityMethods.caseOnWords(var12.toLowerCase().replace("-", " ")) + ": " + ChatColor.GOLD + "Unreadable"
                        );
                     }
                  }
               }
            }

            if (var10) {
               var9.add("");
            }

            var9.add(ChatColor.YELLOW + "►" + " Left click to edit.");
            var9.add(ChatColor.YELLOW + "►" + " Right click to remove.");
            var8.setLore(var9);
            var8.getPersistentDataContainer().set(CONFIG_KEY, PersistentDataType.STRING, var3);
            var7.setItemMeta(var8);
            this.inventory.setItem(slots[var1++], var7);
         }
      }

      ItemStack var16 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemMeta var17 = var16.getItemMeta();
      var17.setDisplayName(ChatColor.RED + "- No Ability -");
      var16.setItemMeta(var17);
      ItemStack var19 = new ItemStack(Material.WRITABLE_BOOK);
      ItemMeta var20 = var19.getItemMeta();
      var20.setDisplayName(ChatColor.GREEN + "Add an ability...");
      var19.setItemMeta(var20);
      this.inventory.setItem(40, var19);

      while (var1 < slots.length) {
         this.inventory.setItem(slots[var1++], var16);
      }
   }

   @Override
   public void whenClicked(InventoryClickEvent var1) {
      ItemStack var2 = var1.getCurrentItem();
      var1.setCancelled(true);
      if (var1.getInventory() == var1.getClickedInventory() && MMOUtils.isMetaItem(var2, false)) {
         if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Add an ability...")) {
            if (!this.getEditedSection().contains("ability")) {
               this.getEditedSection().createSection("ability.ability1");
               this.registerTemplateEdition();
               return;
            }

            if (this.getEditedSection().getConfigurationSection("ability").getKeys(false).size() > 6) {
               this.player.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "You've hit the 7 abilities per item limit.");
               return;
            }

            for (int var3 = 1; var3 < 8; var3++) {
               if (!this.getEditedSection().getConfigurationSection("ability").contains("ability" + var3)) {
                  String var4 = "ability" + var3;
                  this.getEditedSection().set("ability." + var4 + ".type", "FIREBOLT");
                  this.getEditedSection().set("ability." + var4 + ".mode", "RIGHT_CLICK");
                  this.registerTemplateEdition();
                  return;
               }
            }
         }

         String var5 = (String)var2.getItemMeta().getPersistentDataContainer().get(CONFIG_KEY, PersistentDataType.STRING);
         if (var5 != null && !var5.equals("")) {
            if (var1.getAction() == InventoryAction.PICKUP_ALL) {
               new AbilityEdition(this.player, this.template, var5).open(this);
            }

            if (var1.getAction() == InventoryAction.PICKUP_HALF
               && this.getEditedSection().contains("ability")
               && this.getEditedSection().getConfigurationSection("ability").contains(var5)) {
               this.getEditedSection().set("ability." + var5, null);
               this.registerTemplateEdition();
               this.player
                  .sendMessage(
                     MMOItems.plugin.getPrefix()
                        + "Successfully removed "
                        + ChatColor.GOLD
                        + var5
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
