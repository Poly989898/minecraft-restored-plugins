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
import org.bukkit.block.data.Waterlogged;

@MythicCondition(
   author = "Ashijin",
   name = "inLiquid",
   aliases = {"insideliquid", "isinliquid"},
   description = "Tests if the target location is a liquid or waterlogged block"
)
public class InLiquidCondition extends SkillCondition implements ILocationCondition {
   public InLiquidCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractLocation location) {
      Location bukkitLocation = BukkitAdapter.adapt(location);
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(bukkitLocation)) {
         Boolean result = Schedulers.of(bukkitLocation).supply(() -> this.checkLiquid(location)).join();
         return result != null ? result : false;
      } else {
         return this.checkLiquid(location);
      }
   }

   private boolean checkLiquid(AbstractLocation location) {
      if (location.getBlock().isLiquid()) {
         return true;
      } else {
         return location.getBlock().getBlockData() instanceof Waterlogged waterlogged ? waterlogged.isWaterlogged() : false;
      }
   }
}
