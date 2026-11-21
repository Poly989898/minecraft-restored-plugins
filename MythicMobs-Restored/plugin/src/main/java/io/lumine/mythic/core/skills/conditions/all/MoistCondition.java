package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Farmland;

@MythicCondition(
   author = "Joshinn",
   name = "ismoist",
   aliases = {"moist"},
   description = "Whether or not a location of farmland is moist."
)
public class MoistCondition extends SkillCondition implements ILocationCondition {
   public MoistCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractLocation l) {
      Location location = BukkitAdapter.adapt(l);
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(location)) {
         Boolean result = Schedulers.of(location).supply(() -> this.checkMoist(location)).join();
         return result != null ? result : false;
      } else {
         return this.checkMoist(location);
      }
   }

   private boolean checkMoist(Location location) {
      BlockData blockData = location.getBlock().getBlockData();
      return blockData instanceof Farmland ? ((Farmland)blockData).getMoisture() == 7 : false;
   }
}
