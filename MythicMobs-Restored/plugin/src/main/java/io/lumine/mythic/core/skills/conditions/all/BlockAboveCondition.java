package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractBlockMatcher;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
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
   name = "blockAbove",
   aliases = {"aboveBlock"},
   description = "Tests the material type present above a location"
)
public class BlockAboveCondition extends SkillCondition implements ILocationCondition, ISkillMetaCondition {
   @MythicField(
      name = "types",
      aliases = {"block", "b", "type", "t", "material", "mat", "m"},
      description = "A list of materials to check"
   )
   private AbstractBlockMatcher matcher;
   private final List<String> materialList;
   @MythicField(
      name = "y",
      aliases = {"offset", "o", "amount", "a"},
      description = "Amount above the evaluated location to check"
   )
   private final PlaceholderInt yOffset;

   public BlockAboveCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.yOffset = mlc.getPlaceholderInteger(new String[]{"y", "offset", "o", "amount", "a"}, 1, this.conditionVar);
      this.materialList = mlc.getStringList(new String[]{"types", "type", "t", "material", "mat", "m", "block", "b"}, "DIRT", this.conditionVar);
      getPlugin().getClock().queuePostReload(() -> this.matcher = getPlugin().getBootstrap().getBlockMatcher(this.materialList));
   }

   @Override
   public boolean check(AbstractLocation location) {
      return this.checkLocation(location, this.yOffset.get());
   }

   @Override
   public boolean check(SkillMetadata meta) {
      AbstractLocation origin = meta.getOrigin() != null ? meta.getOrigin() : meta.getCaster().getLocation();
      return this.checkLocation(origin, this.yOffset.get(meta));
   }

   private boolean checkLocation(AbstractLocation baseLocation, int y) {
      if (baseLocation == null) {
         return false;
      } else {
         if (this.matcher == null) {
            try {
               this.matcher = getPlugin().getBootstrap().getBlockMatcher(this.materialList);
            } catch (Throwable e) {
               MythicLogger.errorCondition(this, "Failed to initialize BlockMatcher");
               return false;
            }
         }

         if (this.matcher == null) {
            return false;
         } else {
            AbstractLocation checkLocation = baseLocation.clone().add(0.0, y, 0.0);
            if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(BukkitAdapter.adapt(checkLocation))) {
               Boolean result = Schedulers.of(BukkitAdapter.adapt(checkLocation)).supply(() -> this.matcher.matches(checkLocation)).join();
               return result != null ? result : false;
            } else {
               return this.matcher.matches(checkLocation);
            }
         }
      }
   }
}
