package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.SkillAdapter;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import lombok.Getter;

@MythicMechanic(
   author = "Ashijin",
   name = "shootfireball",
   aliases = {"fireball"},
   description = "Shoots a projectile at the target location"
)
public class ShootFireballMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   protected ShootFireballMechanic.FireballType type;
   protected PlaceholderFloat yield;
   protected PlaceholderFloat velocity;
   protected PlaceholderDuration fireticks;
   protected boolean incendiary;
   protected boolean playsound;
   protected boolean charged = false;
   protected AbstractItemStack itemType = null;
   @Getter
   protected Boolean fromOrigin;

   public ShootFireballMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.yield = mlc.getPlaceholderFloat(new String[]{"yield", "strength", "y", "s"}, 1.0F);
      this.velocity = mlc.getPlaceholderFloat(new String[]{"velocity", "v"}, 1.0F);
      this.fireticks = mlc.getPlaceholderDuration(new String[]{"fireticks", "ft"}, DurationUnit.TICKS, "0");
      this.incendiary = mlc.getBoolean(new String[]{"incendiary", "i"}, false);
      this.playsound = mlc.getBoolean(new String[]{"playsound", "ps"}, false);
      this.charged = mlc.getBoolean(new String[]{"charged", "c"}, false);
      this.fromOrigin = mlc.getBoolean(new String[]{"fromorigin", "fo"}, false);
      String strItem = mlc.getString(new String[]{"item", "material"}, "BLAZE_POWDER");
      this.itemType = this.getPlugin().getBootstrap().createItem(strItem);
      ShootFireballMechanic.FireballType def = ShootFireballMechanic.FireballType.NORMAL;
      if (mlc.getBoolean(new String[]{"smallfireball", "small", "sml"}, false)) {
         def = ShootFireballMechanic.FireballType.SMALL;
      }

      String strType = mlc.getString(new String[]{"type", "t"}, def.toString());

      try {
         this.type = ShootFireballMechanic.FireballType.valueOf(strType.toUpperCase());
      } catch (Error | Exception e) {
         MythicLogger.errorMechanicConfig(this, mlc, "Invalid Fireball type supplied");
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.castAtLocation(data, target.getLocation());
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      AbstractLocation origin;
      if (this.fromOrigin) {
         origin = data.getOrigin();
      } else {
         origin = null;
      }

      SkillAdapter.get()
         .shootFireball(
            data.getCaster(),
            this.type,
            origin,
            target,
            this.velocity.get(data),
            this.yield.get(data),
            this.incendiary,
            this.fireticks.get(data).getTicks(),
            this.playsound,
            this.charged,
            this.itemType
         );
      return SkillResult.SUCCESS;
   }

   public static enum FireballType {
      NORMAL,
      SMALL,
      LARGE,
      WITHER,
      DRAGON,
      ITEM;
   }
}
