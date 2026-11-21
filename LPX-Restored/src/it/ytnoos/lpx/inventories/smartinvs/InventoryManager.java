package it.ytnoos.lpx.inventories.smartinvs;

import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.InventoryClickListener;
import it.ytnoos.lpx.inventories.smartinvs.content.InventoryContents;
import it.ytnoos.lpx.inventories.smartinvs.opener.ChestInventoryOpener;
import it.ytnoos.lpx.inventories.smartinvs.opener.InventoryOpener;
import it.ytnoos.lpx.inventories.smartinvs.opener.SpecialInventoryOpener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class InventoryManager {
   public final Plugin plugin;
   public final LPXBukkit lpxBukkit;
   private final PluginManager pluginManager;
   public final Map<Player, SmartInventory> inventories;
   public final Map<Player, InventoryContents> contents;
   private final List<InventoryOpener> defaultOpeners;
   private final List<InventoryOpener> openers;

   public InventoryManager(LPXBukkit lpxBukkit2) {
      this.plugin = lpxBukkit2.getPlugin();
      this.lpxBukkit = lpxBukkit2;
      this.pluginManager = Bukkit.getPluginManager();
      this.inventories = new HashMap<>();
      this.contents = new HashMap<>();
      this.defaultOpeners = Arrays.asList(new ChestInventoryOpener(), new SpecialInventoryOpener());
      this.openers = new ArrayList<>();
   }

   public void init() {
      this.pluginManager.registerEvents(new InventoryClickListener(this), this.plugin);
   }

   public Optional findOpener(InventoryType inventoryType) {
      Optional optional = this.openers.stream().filter(inventoryOpener -> inventoryOpener.supports(inventoryType)).findAny();
      if (!optional.isPresent()) {
         optional = this.defaultOpeners.stream().filter(inventoryOpener2 -> inventoryOpener2.supports(inventoryType)).findAny();
      }

      return optional;
   }

   public void registerOpeners(InventoryOpener[] inventoryOpeners) {
      this.openers.addAll(Arrays.asList(inventoryOpeners));
   }

   public List getOpenedPlayers(SmartInventory smartInventory) {
      ArrayList arrayList = new ArrayList();
      this.inventories.forEach((player, smartInventory2) -> {
         if (smartInventory.equals(smartInventory2)) {
            arrayList.add(player);
         }
      });
      return arrayList;
   }

   public Optional getInventory(Object object) {
      return Optional.ofNullable(this.inventories.get(object));
   }

   protected void setInventory(Player player, SmartInventory smartInventory) {
      if (smartInventory == null) {
         this.inventories.remove(player);
      } else {
         this.inventories.put(player, smartInventory);
      }
   }

   public Optional getContents(Object object) {
      return Optional.ofNullable(this.contents.get(object));
   }

   protected void setContents(Player player, InventoryContents inventoryContents) {
      if (inventoryContents == null) {
         this.contents.remove(player);
      } else {
         this.contents.put(player, inventoryContents);
      }
   }

}
