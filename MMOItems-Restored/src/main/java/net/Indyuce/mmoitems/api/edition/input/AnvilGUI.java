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
   public AnvilGUI(Edition var1) {
      super(var1);
      ItemStack var2 = new ItemStack(Material.PAPER);
      ItemMeta var3 = var2.getItemMeta();
      var3.setDisplayName("Input text..");
      var2.setItemMeta(var3);
      MythicLib.plugin.getVersion().getWrapper().handleInventoryCloseEvent(this.getPlayer());
      MythicLib.plugin.getVersion().getWrapper().setActiveContainerDefault(this.getPlayer());
      Bukkit.getPluginManager().registerEvents(this, MMOItems.plugin);
      Object var4 = MythicLib.plugin.getVersion().getWrapper().newContainerAnvil(this.getPlayer());
      this.inventory = MythicLib.plugin.getVersion().getWrapper().toBukkitInventory(var4);
      this.inventory.setItem(0, var2);
      this.containerId = MythicLib.plugin.getVersion().getWrapper().getNextContainerId(this.getPlayer());
      MythicLib.plugin.getVersion().getWrapper().sendPacketOpenWindow(this.getPlayer(), this.containerId);
      MythicLib.plugin.getVersion().getWrapper().setActiveContainer(this.getPlayer(), var4);
      MythicLib.plugin.getVersion().getWrapper().setActiveContainerId(var4, this.containerId);
      MythicLib.plugin.getVersion().getWrapper().addActiveContainerSlotListener(var4, this.getPlayer());
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
   public void a(InventoryClickEvent var1) {
      if (var1.getInventory().equals(this.inventory)) {
         var1.setCancelled(true);
         if (var1.getRawSlot() == 2) {
            ItemStack var2 = this.inventory.getItem(var1.getRawSlot());
            if (var2 != null && var2.getType() != Material.AIR) {
               this.registerInput(var2.hasItemMeta() ? var2.getItemMeta().getDisplayName() : var2.getType().toString());
            }
         }
      }
   }

   @EventHandler
   public void b(InventoryCloseEvent var1) {
      if (this.open && var1.getInventory().equals(this.inventory)) {
         this.close();
      }
   }
}
