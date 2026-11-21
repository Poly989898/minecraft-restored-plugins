package io.lumine.mythic.bukkit.utils;

import java.util.Optional;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class Worlds {
   @Nullable
   public static World getNullable(String name) {
      return Bukkit.getWorld(name);
   }

   public static Optional<World> get(String name) {
      return Optional.ofNullable(getNullable(name));
   }
}
