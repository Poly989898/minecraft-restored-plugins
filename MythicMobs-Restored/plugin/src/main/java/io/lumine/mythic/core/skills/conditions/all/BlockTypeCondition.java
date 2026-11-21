package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractBlockMatcher;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.List;

@MythicCondition(
   author = "Ashijin",
   name = "blockType",
   aliases = {"inblock", "insideblock"},
   description = "Tests the material type present at the target location"
)
public class BlockTypeCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "types",
      aliases = {"block", "b", "type", "t", "material", "mat", "m"},
      description = "A list of materials to check"
   )
   private AbstractBlockMatcher matcher;

   public BlockTypeCondition(String line, MythicLineConfig mlc) {
      super(line);
      getPlugin().getClock().queuePostReload(() -> {
         List<String> list = mlc.getStringList(new String[]{"types", "type", "t", "material", "mat", "m", "block", "b"}, "DIRT", this.conditionVar);

         try {
            this.matcher = getPlugin().getBootstrap().getBlockMatcher(list);
         } catch (Throwable e) {
            MythicLogger.errorCondition(this, "Failed to initialize BlockMatcher");
            e.printStackTrace();
         }
      });
   }

   @Override
   public boolean check(AbstractLocation location) {
      if (this.matcher == null) {
         return false;
      } else if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(BukkitAdapter.adapt(location))) {
         Boolean result = Schedulers.of(BukkitAdapter.adapt(location)).supply(() -> this.matcher.matches(location)).join();
         return result != null ? result : false;
      } else {
         return this.matcher.matches(location);
      }
   }
}
