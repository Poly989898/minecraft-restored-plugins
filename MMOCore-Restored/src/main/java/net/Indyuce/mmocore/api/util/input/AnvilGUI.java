package net.Indyuce.mmocore.api.util.input;

import io.lumine.mythic.lib.MythicLib;
import net.Indyuce.mmocore.api.ConfigMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Consumer;

@Deprecated
public class AnvilGUI extends PlayerInput {
   private final int containerId;
   private final Inventory inventory;

   public AnvilGUI(Player player, PlayerInput.InputType type, Consumer<String> output) {
      super(player, output);
      ItemStack paper = new ItemStack(Material.PAPER);
      ItemMeta paperMeta = paper.getItemMeta();
      paperMeta.setDisplayName(ConfigMessage.fromKey("player-input.anvil." + type.getLowerCaseName()).asLine());
      paper.setItemMeta(paperMeta);
      MythicLib.plugin.getVersion().getWrapper().handleInventoryCloseEvent(player);
      MythicLib.plugin.getVersion().getWrapper().setActiveContainerDefault(player);
      Object container = MythicLib.plugin.getVersion().getWrapper().newContainerAnvil(player);
      this.inventory = MythicLib.plugin.getVersion().getWrapper().toBukkitInventory(container);
      this.inventory.setItem(0, paper);
      this.containerId = MythicLib.plugin.getVersion().getWrapper().getNextContainerId(player);
      MythicLib.plugin.getVersion().getWrapper().sendPacketOpenWindow(player, this.containerId);
      MythicLib.plugin.getVersion().getWrapper().setActiveContainer(player, container);
      MythicLib.plugin.getVersion().getWrapper().setActiveContainerId(container, this.containerId);
      MythicLib.plugin.getVersion().getWrapper().addActiveContainerSlotListener(container, player);
   }

   @Override
   public void close() {
      MythicLib.plugin.getVersion().getWrapper().handleInventoryCloseEvent(this.getPlayer());
      MythicLib.plugin.getVersion().getWrapper().setActiveContainerDefault(this.getPlayer());
      MythicLib.plugin.getVersion().getWrapper().sendPacketCloseWindow(this.getPlayer(), this.containerId);
      InventoryClickEvent.getHandlerList().unregister(this);
      InventoryCloseEvent.getHandlerList().unregister(this);
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void a(InventoryClickEvent event) {
      if (event.getInventory().equals(this.inventory)) {
         event.setCancelled(true);
         if (event.getRawSlot() == 2) {
            ItemStack clicked = this.inventory.getItem(event.getRawSlot());
            if (clicked != null && clicked.getType() != Material.AIR) {
               this.output(clicked.hasItemMeta() ? clicked.getItemMeta().getDisplayName() : clicked.getType().toString());
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void b(InventoryCloseEvent event) {
      if (event.getInventory().equals(this.inventory)) {
         this.close();
      }
   }
}
