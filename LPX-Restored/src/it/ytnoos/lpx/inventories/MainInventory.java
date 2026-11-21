package it.ytnoos.lpx.inventories;

import com.github.retrooper.packetevents.PacketEvents;
import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.inventories.items.ItemBuilder;
import it.ytnoos.lpx.inventories.smartinvs.ClickableItem;
import it.ytnoos.lpx.inventories.smartinvs.SmartInventory;
import it.ytnoos.lpx.inventories.smartinvs.content.InventoryContents;
import it.ytnoos.lpx.inventories.smartinvs.content.InventoryProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MainInventory implements InventoryProvider {
   private final LPXBukkit lpxBukkit;

   public MainInventory(LPXBukkit lpxBukkit2) {
      this.lpxBukkit = lpxBukkit2;
   }

   @Override
   public void init(Player player, InventoryContents inventoryContents) {
      String string = this.lpxBukkit.getConfigManager().getMessagesConfig().getMainColor();
      String string2 = this.lpxBukkit.getConfigManager().getMessagesConfig().getSecondColor();
      ItemBuilder itemBuilder = ItemBuilder.of(Material.WATER_BUCKET);
      String string3 = PacketEvents.getAPI().getServerManager().getVersion().getReleaseName();
      String string4 = string2;
      inventoryContents.set(0, 0, ClickableItem.empty(itemBuilder.name(string + "Protocol version: " + string4 + string3).toItemStack()));
      if (player.hasPermission("lpx.reload")) {
         inventoryContents.set(0, 4, ClickableItem.of(ItemBuilder.of(Material.NETHER_STAR).name(string + "Reload Config").toItemStack(), inventoryClickEvent -> {
            player.performCommand("lpx reload");
            player.closeInventory();
         }));
      } else {
         inventoryContents.set(
            0, 4, ClickableItem.empty(ItemBuilder.of(Material.BARRIER).name(string + "Reload Config").lore(new String[]{"<red><bold>NO PERMISSION"}).toItemStack())
         );
      }

      inventoryContents.set(
         0,
         8,
         ClickableItem.of(
            ItemBuilder.of(Material.BOOK).name(string + "Recent violations").toItemStack(), inventoryClickEvent2 -> this.lpxBukkit.getInventoryContainer().violationsInventory().open(player)
         )
      );
   }

   public SmartInventory build() {
      return SmartInventory.builder()
         .id("main")
         .provider(this)
         .manager(this.lpxBukkit.getInventoryManager())
         .size(1, 9)
         .title(
            this.lpxBukkit.getConfigManager().getMessagesConfig().getMainColor()
               + "<bold>LPX >><reset>"
               + this.lpxBukkit.getConfigManager().getMessagesConfig().getSecondColor()
               + " Main GUI"
         )
         .build();
   }

}
