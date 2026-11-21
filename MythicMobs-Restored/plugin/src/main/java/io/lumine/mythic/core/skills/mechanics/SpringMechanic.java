package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Material;
import org.bukkit.block.Block;

@MythicMechanic(
   author = "Ashijin",
   name = "spring",
   aliases = {"water"},
   description = "Creates a temporary spring of water at the target location"
)
public class SpringMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   private PlaceholderDuration tickDuration;
   private Material type;

   public SpringMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.tickDuration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "40");
      String strType = mlc.getString(new String[]{"type", "t"}, "water");
      if (strType.equalsIgnoreCase("lava")) {
         this.type = Material.LAVA;
      } else if (strType.equalsIgnoreCase("water")) {
         this.type = Material.WATER;
      } else {
         this.type = Material.WATER;
      }
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      this.playEffect(data, target);
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      this.playEffect(data, target.getLocation());
      return SkillResult.SUCCESS;
   }

   protected void playEffect(SkillMetadata data, AbstractLocation target) {
      Block block = BukkitAdapter.adapt(target).getBlock();
      if (block.getType() == Material.AIR) {
         block.setType(this.type, true);
         new SpringMechanic.Animator(data, target);
      }
   }

   private class Animator implements Runnable {
      private AbstractLocation location;

      public Animator(SkillMetadata data, AbstractLocation location) {
         this.location = location;
         Schedulers.of(BukkitAdapter.adapt(location)).runLater(this, Math.max(1, SpringMechanic.this.tickDuration.get(data).getTicks()));
      }

      @Override
      public void run() {
         try {
            BukkitAdapter.adapt(this.location).getBlock().setType(Material.AIR, true);
         } catch (Exception e) {
            MythicLogger.handleMinorError(e);
         }
      }
   }
}
