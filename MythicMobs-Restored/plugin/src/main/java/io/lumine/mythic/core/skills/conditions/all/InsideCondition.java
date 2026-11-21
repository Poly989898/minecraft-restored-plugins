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

@MythicCondition(
   author = "Ashijin",
   name = "inside",
   aliases = {},
   description = "Checks if the target has a block over their head"
)
public class InsideCondition extends SkillCondition implements ILocationCondition {
   public InsideCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractLocation target) {
      Location l = BukkitAdapter.adapt(target);
      if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(l)) {
         try {
            return Schedulers.of(l).supply(() -> l.getWorld().getHighestBlockYAt(l) > l.getY()).join();
         } catch (Exception e) {
            return false;
         }
      } else {
         return l.getWorld().getHighestBlockYAt(l) > l.getY();
      }
   }
}
