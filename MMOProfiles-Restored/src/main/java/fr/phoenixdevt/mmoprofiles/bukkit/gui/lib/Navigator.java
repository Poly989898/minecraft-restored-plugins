package fr.phoenixdevt.mmoprofiles.bukkit.gui.lib;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import java.util.ArrayDeque;
import java.util.Deque;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/** Stack of the GUIs one player has opened; the top one is the one displayed. */
public class Navigator {
   private final MMOPlayerData playerData;
   private final Deque<GeneratedInventory> stack = new ArrayDeque<>();
   private Inventory lastOpened;
   private boolean closingBlocked;
   private boolean reopenScheduled;

   public Navigator(MMOPlayerData playerData) {
      this.playerData = playerData;
   }

   public MMOPlayerData getPlayerData() {
      return this.playerData;
   }

   public void blockClosing() {
      this.closingBlocked = true;
   }

   public void unblockClosing() {
      this.closingBlocked = false;
   }

   public boolean isClosed() {
      return this.lastOpened == null || this.stack.isEmpty();
   }

   public GeneratedInventory peek() {
      return this.stack.peek();
   }

   public Inventory getLastBukkitOpened() {
      return this.lastOpened;
   }

   void push(GeneratedInventory inv) {
      if (this.stack.peek() != inv) {
         this.stack.push(inv);
      }
   }

   void setLastOpened(Inventory inventory) {
      this.lastOpened = inventory;
   }

   /** Rebuilds and displays the GUI on top of the stack. */
   public void openLast() {
      GeneratedInventory top = this.stack.peek();
      if (top != null) {
         top.open();
      }
   }

   /** Drops the GUI on top of the stack and displays the previous one. */
   public void popOpen() {
      this.stack.poll();
      if (this.stack.isEmpty()) {
         Player player = this.playerData.getPlayer();
         this.lastOpened = null;
         if (player != null) {
            player.closeInventory();
         }
      } else {
         this.openLast();
      }
   }

   /** Called when the player's open GUI was closed by the player or the server. */
   void onClose(Inventory inventory) {
      if (inventory != this.lastOpened) {
         return;
      }

      this.lastOpened = null;
      GeneratedInventory top = this.stack.peek();
      if (!this.closingBlocked || top == null || this.reopenScheduled) {
         return;
      }

      this.reopenScheduled = true;
      Bukkit.getScheduler().runTaskLater(MMOProfiles.plugin, () -> {
         this.reopenScheduled = false;
         Player player = this.playerData.getPlayer();
         if (this.closingBlocked && this.lastOpened == null && player != null && player.isOnline() && this.stack.peek() == top) {
            top.open();
         }
      }, Math.max(1L, top.getCloseTimeOut()));
   }
}
