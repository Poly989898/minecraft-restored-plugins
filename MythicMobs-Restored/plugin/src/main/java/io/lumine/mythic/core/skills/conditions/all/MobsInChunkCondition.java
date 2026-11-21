package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.numbers.RangedDouble;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.Chunk;
import org.bukkit.Location;

@MythicCondition(
   author = "Ashijin",
   name = "mobsinchunk",
   aliases = {},
   description = "Matches a range to how many mobs are in the target location's chunk"
)
public class MobsInChunkCondition extends SkillCondition implements ILocationCondition {
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The number range to match"
   )
   private final RangedDouble amount;

   public MobsInChunkCondition(String line, MythicLineConfig mlc) {
      super(line);
      String k = mlc.getString(new String[]{"amount", "a"}, "0", this.conditionVar);
      this.amount = new RangedDouble(k);
   }

   @Override
   public boolean check(AbstractLocation l) {
      if (ServerVersion.isFolia()) {
         Location loc = BukkitAdapter.adapt(l);
         if (!FoliaHelper.isOwnedByCurrentRegion(loc)) {
            Integer count = Schedulers.of(loc).supply(() -> {
               Chunk chunk = loc.getChunk();
               return chunk.getEntities().length;
            }).join();
            return count != null && this.amount.equals(count);
         }
      }

      return this.amount.equals(BukkitAdapter.adapt(l).getChunk().getEntities().length);
   }
}
