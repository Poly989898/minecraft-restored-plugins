package net.Indyuce.mmoitems.api.edition.input;

import io.lumine.mythic.lib.MythicLib;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.Edition;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Deprecated
public class AnvilGUI extends PlayerInputHandler implements Listener {
   private final int containerId;
   private final Inventory inventory;
   private boolean open;

   @Deprecated
   public AnvilGUI(Edition edition) {
      super(edition);
      ItemStack item = new ItemStack(Material.PAPER);
      ItemMeta itemMeta = item.getItemMeta();
      itemMeta.setDisplayName("Input text..");
      item.setItemMeta(itemMeta);
      MythicLib.plugin.getVersion().getWrapper().handleInventoryCloseEvent(this.getPlayer());
      MythicLib.plugin.getVersion().getWrapper().setActiveContainerDefault(this.getPlayer());
      Bukkit.getPluginManager().registerEvents(this, MMOItems.plugin);
      Object containerAnvil = MythicLib.plugin.getVersion().getWrapper().newContainerAnvil(this.getPlayer());
      this.inventory = MythicLib.plugin.getVersion().getWrapper().toBukkitInventory(containerAnvil);
      this.inventory.setItem(0, item);
      this.containerId = MythicLib.plugin.getVersion().getWrapper().getNextContainerId(this.getPlayer());
      MythicLib.plugin.getVersion().getWrapper().sendPacketOpenWindow(this.getPlayer(), this.containerId);
      MythicLib.plugin.getVersion().getWrapper().setActiveContainer(this.getPlayer(), containerAnvil);
      MythicLib.plugin.getVersion().getWrapper().setActiveContainerId(containerAnvil, this.containerId);
      MythicLib.plugin.getVersion().getWrapper().addActiveContainerSlotListener(containerAnvil, this.getPlayer());
      this.open = true;
   }

   public Inventory getInventory() {
      return this.inventory;
   }

   @Override
   public void close() {
      if (this.open) {
         this.open = false;
         MythicLib.plugin.getVersion().getWrapper().handleInventoryCloseEvent(this.getPlayer());
         MythicLib.plugin.getVersion().getWrapper().setActiveContainerDefault(this.getPlayer());
         MythicLib.plugin.getVersion().getWrapper().sendPacketCloseWindow(this.getPlayer(), this.containerId);
         HandlerList.unregisterAll(this);
      }
   }

   @EventHandler
   public void a(InventoryClickEvent event) {
      if (event.getInventory().equals(this.inventory)) {
         event.setCancelled(true);
         if (event.getRawSlot() == 2) {
            ItemStack item = this.inventory.getItem(event.getRawSlot());
            if (item != null && item.getType() != Material.AIR) {
               this.registerInput(item.hasItemMeta() ? item.getItemMeta().getDisplayName() : item.getType().toString());
            }
         }
      }
   }

   @EventHandler
   public void b(InventoryCloseEvent event) {
      if (this.open && event.getInventory().equals(this.inventory)) {
         this.close();
      }
   }
}
