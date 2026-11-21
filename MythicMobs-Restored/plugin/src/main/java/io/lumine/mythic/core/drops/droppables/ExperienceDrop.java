package io.lumine.mythic.core.drops.droppables;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.ILocationDrop;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.drops.Drop;
import org.bukkit.Location;
import org.bukkit.entity.ExperienceOrb;

public class ExperienceDrop extends Drop implements ILocationDrop {
   public ExperienceDrop(String line, MythicLineConfig config) {
      super(line, config);
   }

   public ExperienceDrop(String line, MythicLineConfig config, double amount) {
      super(line, config, amount);
   }

   @Override
   public void drop(AbstractLocation target, DropMetadata metadata, double amount) {
      Location loc = BukkitAdapter.adapt(target);
      ((ExperienceOrb)loc.getWorld().spawn(loc, ExperienceOrb.class)).setExperience((int)amount);
   }
}
