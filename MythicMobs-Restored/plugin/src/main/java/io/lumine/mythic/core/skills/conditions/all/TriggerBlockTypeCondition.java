package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractBlockMatcher;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.List;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.block.Block;

@MythicCondition(
   author = "Ashijin",
   name = "triggerBlockType",
   aliases = {"triggeringBlockType"},
   description = "Tests the material type that triggered the skill"
)
public class TriggerBlockTypeCondition extends SkillCondition implements ISkillMetaCondition {
   @MythicField(
      name = "types",
      aliases = {"types", "type", "t", "material", "mat", "m"},
      description = "A list of materials to check"
   )
   private AbstractBlockMatcher matcher;

   public TriggerBlockTypeCondition(String line, MythicLineConfig mlc) {
      super(line);
      getPlugin().getClock().queuePostReload(() -> {
         List<String> list = mlc.getStringList(new String[]{"types", "type", "t", "material", "mat", "m", "block", "b"}, "DIRT", this.conditionVar);
         this.matcher = getPlugin().getBootstrap().getBlockMatcher(list);
      });
   }

   @Override
   public boolean check(SkillMetadata meta) {
      Optional<Object> maybe = meta.getMetadata("block");
      if (maybe.isPresent()) {
         Block block = (Block)maybe.get();
         Location bukkitLocation = block.getLocation();
         AbstractLocation location = BukkitAdapter.adapt(bukkitLocation);
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(bukkitLocation)) {
            Boolean result = Schedulers.of(bukkitLocation).supply(() -> this.matcher.matches(location)).join();
            return result != null ? result : false;
         } else {
            return this.matcher.matches(location);
         }
      } else {
         return false;
      }
   }
}
