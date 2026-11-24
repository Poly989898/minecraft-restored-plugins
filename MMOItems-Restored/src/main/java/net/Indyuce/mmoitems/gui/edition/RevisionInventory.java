package net.Indyuce.mmoitems.gui.edition;

import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.Arrays;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class RevisionInventory extends EditionInventory {
   static ItemStack name;
   static ItemStack lore;
   static ItemStack enchantments;
   static ItemStack upgrades;
   static ItemStack gemstones;
   static ItemStack soulbind;
   static ItemStack external;
   static ItemStack revisionID;
   static final String REVISION = "§6Revision ID";

   public RevisionInventory(@NotNull Player var1, @NotNull MMOItemTemplate var2) {
      super(var1, var2);
      if (revisionID == null) {
         name = ItemFactory.of(Material.NAME_TAG)
            .name("§3Name")
            .lore(SilentNumbers.chop("The display name of the old item will be transferred to the new one", 40, "§7"))
            .build();
         lore = ItemFactory.of(Material.WRITABLE_BOOK)
            .name("§dLore")
            .lore(SilentNumbers.chop("Specifically keeps lore lines that begin with the color code §n&7", 40, "§7"))
            .build();
         enchantments = ItemFactory.of(Material.EXPERIENCE_BOTTLE)
            .name("§bEnchantments")
            .lore(
               SilentNumbers.chop(
                  "This keeps specifically enchantments that are not accounted for in upgrades nor gem stones (presumably added by the player).", 40, "§7"
               )
            )
            .build();
         upgrades = ItemFactory.of(Material.NETHER_STAR)
            .name("§aUpgrades")
            .lore(
               SilentNumbers.chop(
                  "Will this item retain the upgrade level after updating? Only the Upgrade Level is kept (as long as it does not exceed the new max).",
                  40,
                  "§7"
               )
            )
            .build();
         gemstones = ItemFactory.of(Material.EMERALD)
            .name("§eGem Stones")
            .lore(
               SilentNumbers.chop(
                  "Will the item retain its gem stones when updating? (Note that this allows gemstone overflow - will keep ALL old gemstones even if you reduced the gem sockets)",
                  40,
                  "§7"
               )
            )
            .build();
         soulbind = ItemFactory.of(Material.ENDER_EYE)
            .name("§cSoulbind")
            .lore(SilentNumbers.chop("If the old item is soulbound, updating will transfer the soulbind to the new item.", 40, "§7"))
            .build();
         external = ItemFactory.of(Material.SPRUCE_SIGN)
            .name("§9External SH")
            .lore(SilentNumbers.chop("Data registered onto the item's StatHistory by external plugins (like GemStones but not removable)", 40, "§7"))
            .build();
         revisionID = ItemFactory.of(Material.ITEM_FRAME)
            .name("§6Revision ID")
            .lore(
               SilentNumbers.chop(
                  "The updater is always active, increasing this number will update all instances of this MMOItem without further action.", 40, "§7"
               )
            )
            .build();
      }
   }

   @Override
   public String getName() {
      return "Revision Manager";
   }

   @Override
   public void arrangeInventory() {
      for (int var1 = 0; var1 < this.inventory.getSize(); var1++) {
         ItemStack var2 = null;
         Boolean var3 = null;
         Integer var4 = null;
         switch (var1) {
            case 19:
               var2 = name.clone();
               var3 = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepName();
               break;
            case 20:
               var2 = lore.clone();
               var3 = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepLore();
               break;
            case 21:
               var2 = enchantments.clone();
               var3 = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepEnchantments();
               break;
            case 22:
               var2 = external.clone();
               var3 = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepExternalSH();
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 31:
            case 32:
            default:
               break;
            case 28:
               var2 = upgrades.clone();
               var3 = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepUpgrades();
               break;
            case 29:
               var2 = gemstones.clone();
               var3 = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepGemStones();
               break;
            case 30:
               var2 = soulbind.clone();
               var3 = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepSoulBind();
               break;
            case 33:
               var4 = this.getEditedSection().getInt(ItemStats.REVISION_ID.getPath(), 1);
               var2 = revisionID.clone();
         }

         if (var2 != null) {
            if (var3 != null) {
               this.inventory.setItem(var1, this.addLore(var2, "", "§8Enabled (in config)? §6" + var3.toString()));
            } else if (var4 != null) {
               this.inventory.setItem(var1, this.addLore(var2, "", "§8Current Value: §6" + var4));
            } else {
               this.inventory.setItem(var1, var2);
            }
         }
      }
   }

   @Override
   public void whenClicked(InventoryClickEvent var1) {
      ItemStack var2 = var1.getCurrentItem();
      var1.setCancelled(true);
      if (var1.getInventory() == var1.getClickedInventory() && MMOUtils.isMetaItem(var2, false)) {
         if (var2.getItemMeta().getDisplayName().equals("§6Revision ID")) {
            int var3 = this.getEditedSection().getInt(ItemStats.REVISION_ID.getPath(), 1);
            if (var1.getAction() == InventoryAction.PICKUP_HALF) {
               var3 = Math.max(var3 - 1, 1);
            } else {
               var3 = Math.min(var3 + 1, Integer.MAX_VALUE);
            }

            this.getEditedSection().set(ItemStats.REVISION_ID.getPath(), var3);
            this.registerTemplateEdition();
            var1.setCurrentItem(this.addLore(revisionID.clone(), "", "§8Current Value: §6" + var3));
         }
      }
   }

   @NotNull
   ItemStack addLore(@NotNull ItemStack var1, String... var2) {
      ArrayList var3 = new ArrayList();
      ItemMeta var4 = var1.getItemMeta();
      if (var4 != null && var4.getLore() != null) {
         var3 = new ArrayList(var4.getLore());
      }

      var3.addAll(Arrays.asList(var2));
      var4.setLore(var3);
      var1.setItemMeta(var4);
      return var1;
   }
}
