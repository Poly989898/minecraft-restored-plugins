package io.lumine.mythic.bukkit.utils.version.wrappers;

import io.lumine.mythic.bukkit.utils.version.wrappers.v1_20_R1.VolatileItemHandler;

public class VersionWrapper_v1_20_R1 extends VersionWrapper {
   public VersionItemHandler itemHandler = new VolatileItemHandler();

   @Override
   public VersionItemHandler getItemHandler() {
      return this.itemHandler;
   }
}
