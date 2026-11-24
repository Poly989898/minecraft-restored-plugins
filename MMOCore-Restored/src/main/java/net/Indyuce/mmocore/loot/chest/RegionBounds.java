package net.Indyuce.mmocore.loot.chest;

import java.util.stream.Stream;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class RegionBounds {
   private final World world;
   private final int x1;
   private final int z1;
   private final int x2;
   private final int z2;

   public RegionBounds(ConfigurationSection config) {
      Validate.notNull(config, "Could not load config");
      String name = config.getString("world");
      Validate.notNull(name, "Could not find world name");
      Validate.notNull(this.world = Bukkit.getWorld(name), "Could not find world '" + config.getString("world") + "'");
      this.x1 = Math.min(config.getInt("x1"), config.getInt("x2"));
      this.x2 = Math.max(config.getInt("x1"), config.getInt("x2"));
      this.z1 = Math.min(config.getInt("z1"), config.getInt("z2"));
      this.z2 = Math.max(config.getInt("z1"), config.getInt("z2"));
   }

   public RegionBounds(Location loc1, Location loc2) {
      Validate.isTrue(loc1.getWorld().equals(loc2.getWorld()), "Locations must be in the same world");
      this.world = loc1.getWorld();
      this.x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
      this.x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
      this.z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
      this.z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
   }

   public Stream<PlayerData> getPlayers() {
      return this.world.getPlayers().stream().filter(this::isInRegion).map(PlayerData::get);
   }

   public boolean isInRegion(Player player) {
      int x = player.getLocation().getBlockX();
      int z = player.getLocation().getBlockZ();
      return player.getWorld().equals(this.world) && this.x1 <= x && this.x2 >= x && this.z1 <= z && this.z2 >= z;
   }
}
