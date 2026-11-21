package io.lumine.mythic.core.drops.droppables;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.ILocationDrop;
import io.lumine.mythic.core.drops.Drop;

public class NothingDrop extends Drop implements ILocationDrop {
   public NothingDrop(String line, MythicLineConfig config) {
      super(line, config);
   }

   public NothingDrop(String line, MythicLineConfig config, double amount) {
      super(line, config, amount);
   }

   @Override
   public void drop(AbstractLocation target, DropMetadata metadata, double amount) {
   }
}
