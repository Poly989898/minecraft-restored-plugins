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

   public RevisionInventory(@NotNull Player player, @NotNull MMOItemTemplate mmoItemTemplate) {
      super(player, mmoItemTemplate);
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
      for (int i = 0; i < this.inventory.getSize(); i++) {
         ItemStack item = null;
         Boolean flag = null;
         Integer value = null;
         switch (i) {
            case 19:
               item = name.clone();
               flag = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepName();
               break;
            case 20:
               item = lore.clone();
               flag = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepLore();
               break;
            case 21:
               item = enchantments.clone();
               flag = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepEnchantments();
               break;
            case 22:
               item = external.clone();
               flag = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepExternalSH();
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
               item = upgrades.clone();
               flag = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepUpgrades();
               break;
            case 29:
               item = gemstones.clone();
               flag = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepGemStones();
               break;
            case 30:
               item = soulbind.clone();
               flag = MMOItems.plugin.getLanguage().revisionOptions.shouldKeepSoulBind();
               break;
            case 33:
               value = this.getEditedSection().getInt(ItemStats.REVISION_ID.getPath(), 1);
               item = revisionID.clone();
         }

         if (item != null) {
            if (flag != null) {
               this.inventory.setItem(i, this.addLore(item, "", "§8Enabled (in config)? §6" + flag.toString()));
            } else if (value != null) {
               this.inventory.setItem(i, this.addLore(item, "", "§8Current Value: §6" + value));
            } else {
               this.inventory.setItem(i, item);
            }
         }
      }
   }

   @Override
   public void whenClicked(InventoryClickEvent event) {
      ItemStack currentItem = event.getCurrentItem();
      event.setCancelled(true);
      if (event.getInventory() == event.getClickedInventory() && MMOUtils.isMetaItem(currentItem, false)) {
         if (currentItem.getItemMeta().getDisplayName().equals("§6Revision ID")) {
            int count = this.getEditedSection().getInt(ItemStats.REVISION_ID.getPath(), 1);
            if (event.getAction() == InventoryAction.PICKUP_HALF) {
               count = Math.max(count - 1, 1);
            } else {
               count = Math.min(count + 1, Integer.MAX_VALUE);
            }

            this.getEditedSection().set(ItemStats.REVISION_ID.getPath(), count);
            this.registerTemplateEdition();
            event.setCurrentItem(this.addLore(revisionID.clone(), "", "§8Current Value: §6" + count));
         }
      }
   }

   @NotNull
   ItemStack addLore(@NotNull ItemStack item, String... texts) {
      ArrayList list = new ArrayList();
      ItemMeta itemMeta = item.getItemMeta();
      if (itemMeta != null && itemMeta.getLore() != null) {
         list = new ArrayList(itemMeta.getLore());
      }

      list.addAll(Arrays.asList(texts));
      itemMeta.setLore(list);
      item.setItemMeta(itemMeta);
      return item;
   }
}
