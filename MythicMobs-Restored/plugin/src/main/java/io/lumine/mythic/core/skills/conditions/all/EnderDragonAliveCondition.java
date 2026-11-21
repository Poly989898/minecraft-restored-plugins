package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;

@MythicCondition(
   author = "Ashijin",
   name = "enderdragonalive",
   aliases = {"isdragonalive", "dragonalive"},
   description = "Tests if an EnderDragon is alive on this map"
)
public class EnderDragonAliveCondition extends SkillCondition implements ILocationCondition {
   public EnderDragonAliveCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractLocation target) {
      return this.isDragonAlive(target);
   }

   private boolean isDragonAlive(AbstractLocation location) {
      World world = BukkitAdapter.adapt(location).getWorld();
      if (Bukkit.isPrimaryThread()) {
         return world.getEntitiesByClass(EnderDragon.class).size() > 0;
      } else {
         try {
            return Promise.<Boolean>supplyingSync(() -> world.getEntitiesByClass(EnderDragon.class).size() > 0).get();
         } catch (Throwable e) {
            e.printStackTrace();
            return false;
         }
      }
   }
}
