package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Location;
import org.bukkit.Sound;

@MythicMechanic(
   author = "Ashijin",
   name = "playBlockFallSound",
   aliases = {"blockFallSound"},
   description = "Plays the target block's fall sound. Requires Paper."
)
public class PlayBlockFallSoundMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   private float volume;
   private float pitch;

   public PlayBlockFallSoundMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.pitch = mlc.getFloat(new String[]{"pitch", "p"}, 1.0F);
      this.volume = mlc.getFloat(new String[]{"volume", "v"}, 1.0F);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      this.playEffect(target.getLocation());
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      this.playEffect(target);
      return SkillResult.SUCCESS;
   }

   public void playEffect(AbstractLocation location) {
      if (!ServerVersion.isPaper()) {
         MythicLogger.errorMechanicConfig(this, this.config, "Mechanic requires Paper");
      } else {
         Location l = BukkitAdapter.adapt(location);
         Sound sound = l.getBlock().getSoundGroup().getFallSound();
         l.getWorld().playSound(l, sound, this.volume, this.pitch);
      }
   }
}
