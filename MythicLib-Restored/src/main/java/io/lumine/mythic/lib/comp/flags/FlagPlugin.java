package io.lumine.mythic.lib.comp.flags;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface FlagPlugin {
   boolean isPvpAllowed(Location location);

   boolean isFlagAllowed(Player player, CustomFlag customFlag);

   boolean isFlagAllowed(Location location, CustomFlag customFlag);
}
