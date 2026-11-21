package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractBlock;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitBlock;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;
import org.bukkit.Material;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:geyser",
   aliases = {"geyser", "e:geyser"},
   description = "Creates a geyser at the target location"
)
public class GeyserEffect extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   private static final AbstractBlock AIR_BLOCK = new BukkitBlock(Material.AIR);
   protected SkillAudience audience;
   @MythicField(
      name = "height",
      aliases = {"h"},
      description = "The height of the geyser effect",
      defValue = "3"
   )
   private PlaceholderInt height;
   @MythicField(
      name = "interval",
      aliases = {"i", "speed", "s"},
      description = "The speed in ticks of the geyser animation",
      defValue = "10"
   )
   private final PlaceholderDuration tickInterval;
   @MythicField(
      name = "type",
      aliases = {"t"},
      description = "The type of liquid to use. Can be \"water\" or \"lava\"",
      defValue = "water"
   )
   private final AbstractBlock type;

   public GeyserEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.audience = mlc.getAudience("audience", "nearby");
      this.height = mlc.getPlaceholderInteger(new String[]{"height", "h"}, 3);
      this.tickInterval = mlc.getPlaceholderDuration(new String[]{"interval", "i", "speed", "s"}, DurationUnit.TICKS, "10");
      String strType = mlc.getString(new String[]{"type", "t"}, "water");
      if (strType.equalsIgnoreCase("lava")) {
         this.type = new BukkitBlock(Material.LAVA);
      } else if (strType.equalsIgnoreCase("water")) {
         this.type = new BukkitBlock(Material.WATER);
      } else {
         this.type = new BukkitBlock(Material.WATER);
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      new GeyserEffect.Animator(data, target);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new GeyserEffect.Animator(data, target.getLocation());
      return SkillResult.SUCCESS;
   }

   private class Animator implements Runnable {
      private final SkillMetadata data;
      private Task task;
      private final int tickInterval;
      private final int height;
      private int tick = 0;
      private AbstractLocation start;
      private Collection<AbstractPlayer> nearby;

      public Animator(SkillMetadata data, AbstractLocation start) {
         this.data = data;
         this.start = start;
         this.height = Math.max(1, GeyserEffect.this.height.get(data));
         this.tickInterval = Math.max(1, GeyserEffect.this.tickInterval.get(data).getTicks());
         this.nearby = SkillAudience.getEffective(GeyserEffect.this.audience, data, data.getCaster().getEntity());
         this.task = Schedulers.of(BukkitAdapter.adapt(start)).runRepeating(this, 0L, (long)this.tickInterval);
      }

      @Override
      public void run() {
         try {
            if (this.tick > this.height * 2) {
               this.task.terminate();
            } else if (this.tick < this.height) {
               AbstractLocation target = this.start.clone().add(0.0, this.tick, 0.0);
               if (target.getBlock().isAir()) {
                  GeyserEffect.this.getPlugin().getVolatileCodeHandler().getBlockHandler().sendBlockChange(this.nearby, target, GeyserEffect.this.type);
               }
            } else {
               int n = this.height - (this.tick - this.height) - 1;
               AbstractLocation target = this.start.clone().add(0.0, n, 0.0);
               GeyserEffect.this.getPlugin().getVolatileCodeHandler().getBlockHandler().sendBlockChange(this.nearby, target, null);
            }

            this.tick++;
         } catch (Exception e) {
            MythicLogger.handleMinorError(e);
            this.task.terminate();
         }
      }
   }
}
