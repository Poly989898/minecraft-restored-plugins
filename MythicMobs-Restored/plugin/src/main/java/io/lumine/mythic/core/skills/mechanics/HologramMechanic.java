package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.holograms.IHologram;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Joshinn",
   name = "hologram",
   aliases = {"summonhologram", "holo"},
   description = "Spawns a hologram at a target location."
)
public class HologramMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "text",
      aliases = {"t"},
      description = "The text of the hologram",
      defValue = "Hologram Text Missing"
   )
   protected PlaceholderString holoText;
   @MythicField(
      name = "stayTime",
      aliases = {"time", "stay"},
      description = "The duration of the hologram",
      defValue = "100"
   )
   protected PlaceholderDuration stayTime;

   public HologramMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.holoText = mlc.getPlaceholderString(new String[]{"text", "t"}, "Hologram Text Missing");
      this.stayTime = mlc.getPlaceholderDuration(new String[]{"staytime", "stay", "time"}, DurationUnit.TICKS, "100");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      this.playEffect(data, target, target.getLocation());
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      this.playEffect(data, null, target);
      return SkillResult.SUCCESS;
   }

   protected void playEffect(SkillMetadata data, AbstractEntity target, AbstractLocation l) {
      String text = target == null ? this.holoText.get(data) : this.holoText.get(data, target);
      IHologram h = MythicBukkit.inst().getHologramManager().createHologram("#MythicHologram", l, text);
      if (h != null) {
         if (this.stayTime != null) {
            int stayTime = target == null ? this.stayTime.get(data).getTicks() : this.stayTime.get(data, target).getTicks();
            Schedulers.sync().runLater(h::terminate, stayTime);
         }
      }
   }
}
