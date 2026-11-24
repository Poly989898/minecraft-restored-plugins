package io.lumine.mythic.lib.api.condition;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.condition.type.LocationCondition;
import io.lumine.mythic.lib.api.condition.type.MMOCondition;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;

public class RegionCondition extends MMOCondition implements LocationCondition {
   private final String region;

   public RegionCondition(MMOLineConfig config) {
      super(config);
      config.validateKeys("name");
      this.region = config.getString("name");
   }

   @Override
   public boolean check(Location location) {
      List<String> regions = new ArrayList<>();
      WorldGuard.getInstance()
         .getPlatform()
         .getRegionContainer()
         .createQuery()
         .getApplicableRegions(BukkitAdapter.adapt(location))
         .getRegions()
         .forEach(region -> regions.add(region.getId()));
      return regions.contains(this.region);
   }
}
