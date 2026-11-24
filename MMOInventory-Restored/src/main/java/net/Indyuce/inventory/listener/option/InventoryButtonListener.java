package net.Indyuce.inventory.listener.option;

import java.util.ArrayList;
import java.util.List;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.gui.PlayerInventoryView;
import net.Indyuce.inventory.util.InventoryButton;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryButtonListener implements Listener {
   private final ItemStack icon;
   private final int slot;

   private InventoryButtonListener(ConfigurationSection config) {
      this.slot = config.getInt("slot");
      Validate.isTrue(this.slot >= 0, "Slot must be positive");
      this.icon = new InventoryButton(config.getConfigurationSection("item")).getItem();
   }

   @NotNull
   public static Iterable<Listener> getListeners(@NotNull ConfigurationSection config) {
      List<Listener> listeners = new ArrayList<>();
      InventoryButtonListener main = new InventoryButtonListener(config);
      listeners.add(main);
      if (main.slot < 9) {
         listeners.add(new InventoryButtonListener.HotbarListener());
      }

      return listeners;
   }

   @EventHandler
   public void giveItemsOnJoin(PlayerJoinEvent event) {
      event.getPlayer().getInventory().setItem(this.slot, this.icon);
   }

   @EventHandler
   public void giveItemsOnRespawn(PlayerRespawnEvent event) {
      event.getPlayer().getInventory().setItem(this.slot, this.icon);
   }

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void blockInteractions(InventoryClickEvent event) {
      boolean cancel = event.getAction() == InventoryAction.HOTBAR_SWAP
         ? event.getHotbarButton() == this.slot || event.getClickedInventory().getType() == InventoryType.PLAYER && event.getSlot() == this.slot
         : isButton(event.getCurrentItem()) || isButton(event.getCursor());
      if (cancel) {
         event.setCancelled(true);
         Bukkit.getScheduler().runTask(MMOInventory.plugin, () -> openInventory((Player)event.getWhoClicked()));
      }
   }

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void deathDrop(PlayerDeathEvent event) {
      if (!event.getKeepInventory()) {
         event.getDrops().removeIf(InventoryButtonListener::isButton);
      }
   }

   static boolean isButton(@Nullable ItemStack item) {
      return item != null && item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(InventoryButton.NAMESPACED_KEY, PersistentDataType.BYTE);
   }

   static void openInventory(@NotNull Player player) {
      new PlayerInventoryView(player).open();
   }

   static class HotbarListener implements Listener {
      @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
      public void interact(PlayerInteractEvent event) {
         if (InventoryButtonListener.isButton(event.getItem())) {
            event.setCancelled(true);
            InventoryButtonListener.openInventory(event.getPlayer());
         }
      }

      @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
      public void hotbarDrop(PlayerDropItemEvent event) {
         if (InventoryButtonListener.isButton(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            InventoryButtonListener.openInventory(event.getPlayer());
         }
      }

      @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
      public void hotbarSwap(PlayerSwapHandItemsEvent event) {
         if (InventoryButtonListener.isButton(event.getOffHandItem())) {
            event.setCancelled(true);
            InventoryButtonListener.openInventory(event.getPlayer());
         }
      }
   }
}
