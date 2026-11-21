package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractBlockMatcher;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.List;

@MythicCondition(
   author = "Ashijin",
   name = "onBlock",
   aliases = {},
   description = "Matches the block the target entity is standing on"
)
public class OnBlockCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "types",
      aliases = {"block", "b", "type", "t", "material", "mat", "m"},
      description = "A list of materials to check"
   )
   private AbstractBlockMatcher matcher;

   public OnBlockCondition(String line, MythicLineConfig mlc) {
      super(line);
      getPlugin().getClock().queuePostReload(() -> {
         List<String> list = mlc.getStringList(new String[]{"types", "type", "t", "material", "mat", "m", "block", "b"}, "DIRT", this.conditionVar);
         this.matcher = getPlugin().getBootstrap().getBlockMatcher(list);
      });
   }

   @Override
   public boolean check(AbstractLocation target) {
      if (this.matcher == null) {
         return false;
      } else {
         AbstractLocation location = target.clone().subtract(0.0, 0.6, 0.0);
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(BukkitAdapter.adapt(location))) {
            Boolean result = Schedulers.of(BukkitAdapter.adapt(location)).supply(() -> this.matcher.matches(location)).join();
            return result != null ? result : false;
         } else {
            return this.matcher.matches(location);
         }
      }
   }
}
