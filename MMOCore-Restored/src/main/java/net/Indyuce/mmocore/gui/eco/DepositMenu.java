package net.Indyuce.mmocore.gui.eco;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.SmartGive;
import io.lumine.mythic.lib.gui.PluginInventory;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.util.item.SimpleItemBuilder;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class DepositMenu extends PluginInventory {
   private ItemStack depositItem;
   private int deposit;
   private Inventory lastBukkitInventory;
   private BukkitRunnable updateRunnable;

   public DepositMenu(Player player) {
      super(player);
   }

   public Inventory getInventory() {
      Inventory inv = Bukkit.createInventory(this, 27, "Deposit");
      this.updateDeposit(inv);
      return this.lastBukkitInventory = inv;
   }

   public void onClick(InventoryClickEvent event) {
      if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(this.depositItem)) {
         event.setCancelled(true);
         this.updateDeposit(event.getInventory());
         if (this.deposit > 0) {
            EconomyResponse response = MMOCore.plugin.economy.getEconomy().depositPlayer(this.player, this.deposit);
            if (response.transactionSuccess()) {
               event.getInventory().clear();
               this.player.closeInventory();
               this.player.playSound(this.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
               ConfigMessage.fromKey("deposit", "worth", String.valueOf(this.deposit)).send(this.player);
            }
         }
      } else {
         int worth = NBTItem.get(event.getCurrentItem()).getInteger("RpgWorth");
         if (worth < 1) {
            event.setCancelled(true);
         } else {
            this.scheduleUpdate(event.getInventory());
         }
      }
   }

   public void onClose() {
      if (this.updateRunnable != null) {
         this.updateRunnable.cancel();
      }

      SmartGive smart = new SmartGive(this.player);

      for (int j = 0; j < 26; j++) {
         ItemStack item = this.lastBukkitInventory.getItem(j);
         if (item != null) {
            smart.give(new ItemStack[]{item});
         }
      }
   }

   private void scheduleUpdate(final Inventory inv) {
      if (this.updateRunnable != null) {
         this.updateRunnable.cancel();
      }

      this.updateRunnable = new BukkitRunnable() {
         public void run() {
            DepositMenu.this.updateRunnable = null;
            DepositMenu.this.updateDeposit(inv);
         }
      };
      this.updateRunnable.runTaskLater(MMOCore.plugin, 10L);
   }

   private void updateDeposit(Inventory inv) {
      if (this.updateRunnable != null) {
         this.updateRunnable.cancel();
         this.updateRunnable = null;
      }

      this.deposit = MMOCoreUtils.getWorth(inv.getContents());
      inv.setItem(26, this.depositItem = new SimpleItemBuilder("DEPOSIT_ITEM").addPlaceholders("worth", String.valueOf(this.deposit)).build());
   }
}
