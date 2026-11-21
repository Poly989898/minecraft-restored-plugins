package it.ytnoos.lpx.inventories.smartinvs;

import it.ytnoos.lpx.inventories.smartinvs.content.InventoryContents;
import it.ytnoos.lpx.inventories.smartinvs.content.InventoryProvider;
import it.ytnoos.lpx.inventories.smartinvs.opener.InventoryOpener;
import it.ytnoos.lpx.utils.string.MessageFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class SmartInventory {
   private final InventoryManager inventoryManager;
   private String id;
   private String title;
   private InventoryType type;
   private int rows;
   private int columns;
   private boolean closeable;
   private InventoryProvider inventoryProvider;
   private SmartInventory smartInventory;
   private List<InventoryListener<? extends Event>> listeners;
   private static final String b = "No opener found for the inventory type ";

   private SmartInventory(InventoryManager inventoryManager2) {
      this.inventoryManager = inventoryManager2;
   }

   public static SmartInventory.Builder builder() {
      return new SmartInventory.Builder();
   }

   public Inventory open(Player player) {
      return this.open(player, 0);
   }

   public Inventory open(Player player, int value) {
      Optional<SmartInventory> optional = this.inventoryManager.getInventory(player);
      optional.ifPresent(
         smartInventory2 -> {
            smartInventory2.getListeners()
               .stream()
               .filter(inventoryListener -> inventoryListener.getType() == InventoryCloseEvent.class)
               .forEach(inventoryListener2 -> inventoryListener2.accept(new InventoryCloseEvent(player.getOpenInventory())));
            this.inventoryManager.setInventory(player, null);
         }
      );
      InventoryContents.Impl impl = new InventoryContents.Impl(this, player);
      impl.getPagination().page(value);
      this.inventoryManager.setContents(player, impl);
      this.inventoryProvider.init(player, impl);
      InventoryOpener inventoryOpener = (InventoryOpener)((java.util.Optional<?>)this.inventoryManager.findOpener(this.type)).orElseThrow((java.util.function.Supplier<IllegalStateException>)() -> new IllegalStateException(b + this.type.name()));
      Inventory inventory = inventoryOpener.open(this, player);
      this.inventoryManager.setInventory(player, this);
      return inventory;
   }

   public void close(Player player) {
      this.listeners
         .stream()
         .filter(inventoryListener -> inventoryListener.getType() == InventoryCloseEvent.class)
         .forEach(inventoryListener2 -> inventoryListener2.accept(new InventoryCloseEvent(player.getOpenInventory())));
      this.inventoryManager.setInventory(player, null);
      player.closeInventory();
      this.inventoryManager.setContents(player, null);
   }

   public String getId() {
      return this.id;
   }

   public String getTitle() {
      return this.title;
   }

   public InventoryType getType() {
      return this.type;
   }

   public int getRows() {
      return this.rows;
   }

   public int getColumns() {
      return this.columns;
   }

   public boolean isCloseable() {
      return this.closeable;
   }

   public void setCloseable(boolean flag) {
      this.closeable = flag;
   }

   public InventoryProvider getInventoryProvider() {
      return this.inventoryProvider;
   }

   public Optional getParent() {
      return Optional.ofNullable(this.smartInventory);
   }

   public InventoryManager getInventoryManager() {
      return this.inventoryManager;
   }

   public List<InventoryListener<? extends Event>> getListeners() {
      return this.listeners;
   }

   public static final class Builder {
      private final List<InventoryListener<? extends Event>> listeners = new ArrayList<>();
      private String id = "unknown";
      private String title = "";
      private InventoryType type = InventoryType.CHEST;
      private int rows = 6;
      private int columns = 9;
      private boolean closeable = true;
      private InventoryManager inventoryManager;
      private InventoryProvider inventoryProvider;
      private SmartInventory smartInventory;

      private Builder() {
      }

      public SmartInventory.Builder id(String string) {
         this.id = string;
         return this;
      }

      public SmartInventory.Builder title(String string) {
         this.title = MessageFormatter.toLegacy(string);
         return this;
      }

      public SmartInventory.Builder type(InventoryType inventoryType) {
         this.type = inventoryType;
         return this;
      }

      public SmartInventory.Builder size(int value, int value2) {
         this.rows = value;
         this.columns = value2;
         return this;
      }

      public SmartInventory.Builder closeable(boolean flag) {
         this.closeable = flag;
         return this;
      }

      public SmartInventory.Builder provider(InventoryProvider inventoryProvider2) {
         this.inventoryProvider = inventoryProvider2;
         return this;
      }

      public SmartInventory.Builder parent(SmartInventory smartInventory2) {
         this.smartInventory = smartInventory2;
         return this;
      }

      public SmartInventory.Builder listener(Object object) {
         this.listeners.add((InventoryListener<? extends Event>)object);
         return this;
      }

      public SmartInventory.Builder manager(InventoryManager inventoryManager2) {
         this.inventoryManager = inventoryManager2;
         return this;
      }

      public SmartInventory build() {
         if (this.inventoryProvider == null) {
            throw new IllegalStateException("The provider of the SmartInventory.Builder must be set.");
         }

         InventoryManager inventoryManager2 = this.inventoryManager;
         if (inventoryManager2 == null) {
            throw new IllegalStateException("The manager of the SmartInventory.Builder must be set, or the SmartInvs should be loaded as a plugin.");
         }

         SmartInventory smartInventory2 = new SmartInventory(inventoryManager2);
         smartInventory2.id = this.id;
         smartInventory2.title = this.title;
         smartInventory2.type = this.type;
         smartInventory2.rows = this.rows;
         smartInventory2.columns = this.columns;
         smartInventory2.closeable = this.closeable;
         smartInventory2.inventoryProvider = this.inventoryProvider;
         smartInventory2.smartInventory = this.smartInventory;
         smartInventory2.listeners = this.listeners;
         return smartInventory2;
      }
   }
}
