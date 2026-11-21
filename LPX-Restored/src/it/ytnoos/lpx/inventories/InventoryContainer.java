package it.ytnoos.lpx.inventories;

import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.inventories.smartinvs.SmartInventory;

public class InventoryContainer {
   private final LPXBukkit lpxBukkit;

   public InventoryContainer(LPXBukkit lpxBukkit2) {
      this.lpxBukkit = lpxBukkit2;
   }

   public SmartInventory violationsInventory() {
      return new ViolationsInventory(this.lpxBukkit).build();
   }

   public SmartInventory mainInventory() {
      return new MainInventory(this.lpxBukkit).build();
   }
}
