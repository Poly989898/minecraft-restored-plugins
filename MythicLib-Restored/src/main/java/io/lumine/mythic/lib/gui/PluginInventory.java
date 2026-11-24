package io.lumine.mythic.lib.gui;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.util.lang3.Validate;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * A GUI opened to one player. Every instance is registered in a {@link Navigator} which keeps the
 * stack of the GUIs the player went through and routes the inventory events to the top one.
 */
public abstract class PluginInventory implements InventoryHolder {
   protected final Player player;
   protected final MMOPlayerData playerData;
   protected final Navigator navigator;
   public int page = 1;
   private Consumer<Inventory> backgroundRunnable;
   private long backgroundRunnablePeriod;

   public PluginInventory(MMOPlayerData playerData) {
      this(new Navigator(playerData));
   }

   public PluginInventory(Player player) {
      this(retrievePlayerData(player));
   }

   public PluginInventory(Navigator navigator) {
      this.playerData = navigator.getMMOPlayerData();
      this.player = this.playerData.getPlayer();
      this.navigator = navigator;
      navigator.push(this);
   }

   public MMOPlayerData getMMOPlayerData() {
      return this.playerData;
   }

   public Player getPlayer() {
      return this.player;
   }

   public Navigator getNavigator() {
      return this.navigator;
   }

   /** Ticks to wait before the GUI is shown again after the player closed a GUI whose closing is blocked. */
   public long getCloseTimeOut() {
      return 20L;
   }

   /** Runs the task with the open inventory every {@code period} ticks while this GUI is displayed. */
   public void registerRepeatingTask(Consumer<Inventory> runnable, long period) {
      Validate.isTrue(this.backgroundRunnable == null, "A runnable already exists");
      this.backgroundRunnablePeriod = period;
      this.backgroundRunnable = runnable;
   }

   public Consumer<Inventory> getBackgroundRunnable() {
      return this.backgroundRunnable;
   }

   public long getBackgroundRunnablePeriod() {
      return this.backgroundRunnablePeriod;
   }

   /** Displays this GUI, which must be the last one of its navigator. */
   public void open() {
      Validate.isTrue(this.getNavigator().openLast() == this, "Opened an inventory that is not the last");
   }

   @Override
   public abstract Inventory getInventory();

   /**
    * Handles a click in this GUI. The default implementation forwards to {@link #whenClicked(InventoryClickEvent)}
    * which older code overrides instead.
    */
   public void onClick(InventoryClickEvent event) {
      this.whenClicked(event);
   }

   /** Legacy click handler, only called by the default {@link #onClick(InventoryClickEvent)}. */
   public void whenClicked(InventoryClickEvent event) {
   }

   public void onDrag(InventoryDragEvent event) {
   }

   /** Called when this GUI stops being the one displayed to the player. */
   public void onClose() {
   }

   /** Called right before this GUI is displayed. */
   public void onOpen() {
   }

   private static MMOPlayerData retrievePlayerData(Player player) {
      InventoryHolder holder = UtilityMethods.getHolder(VersionUtils.getOpen(player).getTopInventory());
      return holder instanceof PluginInventory ? ((PluginInventory)holder).playerData : MMOPlayerData.get(player);
   }
}
