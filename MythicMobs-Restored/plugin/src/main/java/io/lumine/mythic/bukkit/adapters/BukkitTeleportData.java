package io.lumine.mythic.bukkit.adapters;

import io.lumine.mythic.api.adapters.AbstractTeleportData;
import io.lumine.mythic.api.config.MythicLineConfig;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class BukkitTeleportData extends AbstractTeleportData {
   public BukkitTeleportData(MythicLineConfig config) {
   }

   public void teleport(Entity entity, Location location) {
      entity.teleport(location);
   }
}
