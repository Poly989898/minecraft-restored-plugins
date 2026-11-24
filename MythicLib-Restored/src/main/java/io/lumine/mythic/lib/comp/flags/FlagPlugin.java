package io.lumine.mythic.lib.comp.flags;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface FlagPlugin {
   boolean isPvpAllowed(Location var1);

   boolean isFlagAllowed(Player var1, CustomFlag var2);

   boolean isFlagAllowed(Location var1, CustomFlag var2);
}
