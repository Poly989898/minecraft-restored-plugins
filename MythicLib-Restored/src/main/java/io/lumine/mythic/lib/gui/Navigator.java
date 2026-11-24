package io.lumine.mythic.lib.gui;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.util.Tasks;
import io.lumine.mythic.lib.util.lang3.Validate;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitTask;

/**
 * The stack of {@link PluginInventory} one player went through. It listens to that player's inventory
 * events (only while one of its GUIs is displayed) and forwards them to the GUI on top of the stack.
 */
public class Navigator implements Listener {
   private final Stack<PluginInventory> openedInventories = new Stack<>();
   private final MMOPlayerData playerData;
   private final Player player;
   private PluginInventory lastInvOpened;
   private Inventory lastBukkitOpened;
   public BukkitTask backgroundTask;
   private boolean canClose = true;
   private boolean closed = true;
   private boolean onHold = true;
   public boolean recycle;

   public Navigator(Player player) {
      this(MMOPlayerData.get(player));
   }

   public Navigator(MMOPlayerData playerData) {
      this.playerData = Objects.requireNonNull(playerData);
      this.player = playerData.getPlayer();
   }

   private void registerEvents() {
      Bukkit.getPluginManager().registerEvents(this, MythicLib.plugin);
   }

   public MMOPlayerData getMMOPlayerData() {
      return this.playerData;
   }

   /** While closing is blocked, the GUI shows up again after the player closed it. */
   public void blockClosing() {
      this.canClose = false;
   }

   public void unblockClosing() {
      this.canClose = true;
   }

   public PluginInventory push(PluginInventory inventory) {
      return this.openedInventories.push(inventory);
   }

   public PluginInventory pushOpen(PluginInventory inventory) {
      this.openedInventories.push(inventory);
      return this.openLast();
   }

   public boolean isClosed() {
      return this.closed;
   }

   public Inventory getLastBukkitOpened() {
      return this.lastBukkitOpened;
   }

   public PluginInventory peek() {
      return this.openedInventories.peek();
   }

   /** Displays the GUI on top of the stack, returns it (or null if the player is offline). */
   public PluginInventory openLast() {
      if (!this.playerData.isOnline()) {
         return null;
      }

      PluginInventory last = this.openedInventories.peek();
      last.onOpen();
      if (this.recycle) {
         this.recycle = false;
         return last;
      }

      if (this.lastInvOpened != null) {
         this.closeCurrentInventory();
      }

      this.lastInvOpened = last;
      Inventory inventory = last.getInventory();
      this.lastBukkitOpened = inventory;
      if (this.closed) {
         this.registerEvents();
         this.closed = false;
      }

      Tasks.runSync(MythicLib.plugin, () -> this.openToPlayer(inventory));
      this.startBackgroundTask(last);
      return last;
   }

   private void openToPlayer(Inventory inventory) {
      this.onHold = true;
      try {
         this.playerData.getPlayer().openInventory(inventory);
      } finally {
         this.onHold = false;
      }
   }

   /** Drops the GUI on top of the stack and displays the previous one, or closes the inventory if there is none. */
   public PluginInventory popOpen() {
      this.openedInventories.pop();
      if (this.openedInventories.isEmpty()) {
         this.player.closeInventory();
         return null;
      }

      return this.openLast();
   }

   private void close() {
      Validate.isTrue(!this.closed, "Already closed");
      this.closed = true;
      if (this.lastInvOpened != null) {
         this.closeCurrentInventory();
      }

      HandlerList.unregisterAll(this);
   }

   private void closeCurrentInventory() {
      Validate.notNull(this.lastInvOpened, "No inventory open");
      this.lastInvOpened.onClose();
      this.haltBackgroundTask();
      this.lastInvOpened = null;
   }

   private void startBackgroundTask(PluginInventory inventory) {
      Consumer<Inventory> runnable = inventory.getBackgroundRunnable();
      if (runnable == null) {
         return;
      }

      Validate.isTrue(this.backgroundTask == null, "Background task already running");
      this.backgroundTask = Bukkit.getScheduler().runTaskTimer(MythicLib.plugin, () -> {
         Inventory open = Objects.requireNonNull(VersionUtils.getOpen(this.player).getTopInventory());
         if (open != this.getLastBukkitOpened()) {
            this.haltBackgroundTask();
            throw new RuntimeException("Failed at keeping track of opened inventory");
         }

         runnable.accept(open);
      }, inventory.getBackgroundRunnablePeriod(), inventory.getBackgroundRunnablePeriod());
   }

   private void haltBackgroundTask() {
      if (this.backgroundTask == null) {
         return;
      }

      this.backgroundTask.cancel();
      this.backgroundTask = null;
   }

   @EventHandler
   public void onClose(InventoryCloseEvent event) {
      if (!event.getPlayer().equals(this.player) || this.onHold) {
         return;
      }

      if (this.canClose) {
         this.close();
      } else {
         this.onHold = true;
         PluginInventory top = this.openedInventories.peek();
         Bukkit.getScheduler().runTaskLater(MythicLib.plugin, () -> {
            if (this.closed || !this.playerData.isOnline() || this.lastBukkitOpened == null) {
               this.onHold = false;
               return;
            }

            this.openToPlayer(this.lastBukkitOpened);
         }, Math.max(1L, top.getCloseTimeOut()));
      }
   }

   /**
    * Whether the inventory the player is looking at is the one this navigator displayed. Some server versions do not fire
    * a close event when a GUI is replaced by another one, so a navigator whose GUI is gone shuts itself down here instead
    * of handling the clicks made in someone else's GUI.
    */
   private boolean isOwnView(InventoryView view) {
      if (view.getTopInventory().equals(this.lastBukkitOpened)) {
         return true;
      }

      if (this.canClose && !this.closed) {
         this.close();
      }
      return false;
   }

   @EventHandler
   public void onClick(InventoryClickEvent event) {
      if (!event.getWhoClicked().equals(this.player) || this.onHold || !this.isOwnView(event.getView())) {
         return;
      }

      this.openedInventories.peek().onClick(event);
   }

   @EventHandler
   public void onDrag(InventoryDragEvent event) {
      if (!event.getWhoClicked().equals(this.player) || this.onHold || !this.isOwnView(event.getView())) {
         return;
      }

      this.openedInventories.peek().onDrag(event);
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      if (!event.getPlayer().equals(this.player)) {
         return;
      }

      this.close();
   }
}
