package net.Indyuce.mmoitems.gui.edition;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.stat.type.InternalStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemEdition extends EditionInventory {
   private static final int[] slots = new int[]{19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
   private static final NamespacedKey STAT_ID_KEY = new NamespacedKey(MMOItems.plugin, "StatId");

   public ItemEdition(Player player, MMOItemTemplate mmoItemTemplate) {
      super(player, mmoItemTemplate);
   }

   @Override
   public String getName() {
      return "Item Edition: " + this.getEdited().getId();
   }

   @Override
   public void arrangeInventory() {
      int count = (this.page - 1) * slots.length;
      int count2 = this.page * slots.length;
      int count3 = 0;
      List list = new ArrayList<>(this.getEdited().getType().getAvailableStats())
         .stream()
         .filter(var1x -> var1x.hasValidMaterial(this.getCachedItem()) && !(var1x instanceof InternalStat))
         .collect(Collectors.toList());

      for (int i = count; i < Math.min(list.size(), count2); i++) {
         ItemStat itemStat = (ItemStat)list.get(i);
         ItemStack item = new ItemStack(itemStat.getDisplayMaterial());
         ItemMeta itemMeta = item.getItemMeta();
         itemMeta.addItemFlags(ItemFlag.values());
         VersionUtils.addEmptyAttributeModifier(itemMeta);
         itemMeta.setDisplayName(ChatColor.GREEN + itemStat.getName());
         List colors = MythicLib.plugin.parseColors(Arrays.stream(itemStat.getLore()).map(text -> ChatColor.GRAY + text).collect(Collectors.toList()));
         colors.add("");
         if (itemStat.getCategory() != null) {
            colors.add(0, "");
            colors.add(0, ChatColor.BLUE + itemStat.getCategory().getLoreTag());
         }

         itemStat.whenDisplayed(colors, this.getEventualStatData(itemStat));
         itemMeta.getPersistentDataContainer().set(STAT_ID_KEY, PersistentDataType.STRING, itemStat.getId());
         itemMeta.setLore(colors);
         item.setItemMeta(itemMeta);
         this.inventory.setItem(slots[count3++], item);
      }

      ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemMeta itemMeta = item.getItemMeta();
      itemMeta.setDisplayName(ChatColor.RED + "- No Item Stat -");
      item.setItemMeta(itemMeta);
      ItemStack item2 = new ItemStack(Material.ARROW);
      ItemMeta itemMeta2 = item2.getItemMeta();
      itemMeta2.setDisplayName(ChatColor.GREEN + "Next Page");
      item2.setItemMeta(itemMeta2);
      ItemStack item3 = new ItemStack(Material.ARROW);
      ItemMeta itemMeta3 = item3.getItemMeta();
      itemMeta3.setDisplayName(ChatColor.GREEN + "Previous Page");
      item3.setItemMeta(itemMeta3);

      while (count3 < slots.length) {
         this.inventory.setItem(slots[count3++], item);
      }

      this.inventory.setItem(27, this.page > 1 ? item3 : null);
      this.inventory.setItem(35, list.size() > count2 ? item2 : null);
   }

   @Override
   public void whenClicked(InventoryClickEvent event) {
      event.setCancelled(true);
      if (event.getInventory() == event.getClickedInventory()) {
         ItemStack currentItem = event.getCurrentItem();
         if (MMOUtils.isMetaItem(currentItem, false) && event.getInventory().getItem(4) != null) {
            if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Next Page")) {
               this.page++;
               this.refreshInventory();
            }

            if (currentItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Previous Page")) {
               this.page--;
               this.refreshInventory();
            }

            String text = (String)currentItem.getItemMeta().getPersistentDataContainer().get(STAT_ID_KEY, PersistentDataType.STRING);
            if (text != null && !text.isEmpty()) {
               ItemStat itemStat = MMOItems.plugin.getStats().get(text);
               if (MMOItems.plugin.hasPermissions()
                  && MMOItems.plugin.getLanguage().opStatsEnabled
                  && MMOItems.plugin.getLanguage().opStats.contains(itemStat.getId())
                  && !MMOItems.plugin.getVault().getPermissions().has((Player)event.getWhoClicked(), "mmoitems.edit.op")) {
                  event.getWhoClicked().sendMessage(ChatColor.RED + "You are lacking permission to edit this stat.");
               } else {
                  itemStat.whenClicked(this, event);
               }
            }
         }
      }
   }

   @Deprecated
   public ItemEdition onPage(int count) {
      this.page = count;
      return this;
   }
}
