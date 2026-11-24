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

   public ItemEdition(Player var1, MMOItemTemplate var2) {
      super(var1, var2);
   }

   @Override
   public String getName() {
      return "Item Edition: " + this.getEdited().getId();
   }

   @Override
   public void arrangeInventory() {
      int var1 = (this.page - 1) * slots.length;
      int var2 = this.page * slots.length;
      int var3 = 0;
      List var4 = new ArrayList<>(this.getEdited().getType().getAvailableStats())
         .stream()
         .filter(var1x -> var1x.hasValidMaterial(this.getCachedItem()) && !(var1x instanceof InternalStat))
         .collect(Collectors.toList());

      for (int var5 = var1; var5 < Math.min(var4.size(), var2); var5++) {
         ItemStat var6 = (ItemStat)var4.get(var5);
         ItemStack var7 = new ItemStack(var6.getDisplayMaterial());
         ItemMeta var8 = var7.getItemMeta();
         var8.addItemFlags(ItemFlag.values());
         VersionUtils.addEmptyAttributeModifier(var8);
         var8.setDisplayName(ChatColor.GREEN + var6.getName());
         List var9 = MythicLib.plugin.parseColors(Arrays.stream(var6.getLore()).map(var0 -> ChatColor.GRAY + var0).collect(Collectors.toList()));
         var9.add("");
         if (var6.getCategory() != null) {
            var9.add(0, "");
            var9.add(0, ChatColor.BLUE + var6.getCategory().getLoreTag());
         }

         var6.whenDisplayed(var9, this.getEventualStatData(var6));
         var8.getPersistentDataContainer().set(STAT_ID_KEY, PersistentDataType.STRING, var6.getId());
         var8.setLore(var9);
         var7.setItemMeta(var8);
         this.inventory.setItem(slots[var3++], var7);
      }

      ItemStack var11 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemMeta var12 = var11.getItemMeta();
      var12.setDisplayName(ChatColor.RED + "- No Item Stat -");
      var11.setItemMeta(var12);
      ItemStack var13 = new ItemStack(Material.ARROW);
      ItemMeta var14 = var13.getItemMeta();
      var14.setDisplayName(ChatColor.GREEN + "Next Page");
      var13.setItemMeta(var14);
      ItemStack var15 = new ItemStack(Material.ARROW);
      ItemMeta var10 = var15.getItemMeta();
      var10.setDisplayName(ChatColor.GREEN + "Previous Page");
      var15.setItemMeta(var10);

      while (var3 < slots.length) {
         this.inventory.setItem(slots[var3++], var11);
      }

      this.inventory.setItem(27, this.page > 1 ? var15 : null);
      this.inventory.setItem(35, var4.size() > var2 ? var13 : null);
   }

   @Override
   public void whenClicked(InventoryClickEvent var1) {
      var1.setCancelled(true);
      if (var1.getInventory() == var1.getClickedInventory()) {
         ItemStack var2 = var1.getCurrentItem();
         if (MMOUtils.isMetaItem(var2, false) && var1.getInventory().getItem(4) != null) {
            if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Next Page")) {
               this.page++;
               this.refreshInventory();
            }

            if (var2.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Previous Page")) {
               this.page--;
               this.refreshInventory();
            }

            String var3 = (String)var2.getItemMeta().getPersistentDataContainer().get(STAT_ID_KEY, PersistentDataType.STRING);
            if (var3 != null && !var3.isEmpty()) {
               ItemStat var4 = MMOItems.plugin.getStats().get(var3);
               if (MMOItems.plugin.hasPermissions()
                  && MMOItems.plugin.getLanguage().opStatsEnabled
                  && MMOItems.plugin.getLanguage().opStats.contains(var4.getId())
                  && !MMOItems.plugin.getVault().getPermissions().has((Player)var1.getWhoClicked(), "mmoitems.edit.op")) {
                  var1.getWhoClicked().sendMessage(ChatColor.RED + "You are lacking permission to edit this stat.");
               } else {
                  var4.whenClicked(this, var1);
               }
            }
         }
      }
   }

   @Deprecated
   public ItemEdition onPage(int var1) {
      this.page = var1;
      return this;
   }
}
