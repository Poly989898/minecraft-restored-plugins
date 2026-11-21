package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.SkillAdapter;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "pushbutton",
   aliases = {"buttonpush"},
   description = "Pushes a button at the target location"
)
public class PushButtonMechanic extends SkillMechanic implements ITargetedLocationSkill, INoTargetSkill {
   protected int x;
   protected int y;
   protected int z;

   public PushButtonMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      String coords = mlc.getString("location");
      coords = mlc.getString("loc", coords);
      coords = mlc.getString("l", coords);
      if (coords != null) {
         String[] split = coords.split(",");

         try {
            this.x = Integer.parseInt(split[0]);
            this.y = Integer.parseInt(split[1]);
            this.z = Integer.parseInt(split[2]);
         } catch (Exception e) {
            MythicLogger.error("PushButton skill coordinates are configured incorrectly in skill string: " + skill);
            return;
         }
      } else {
         this.x = mlc.getInteger("x", 0);
         this.y = mlc.getInteger("y", 0);
         this.z = mlc.getInteger("z", 0);
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      SkillAdapter.get()
         .pushButton(
            data.getCaster(), new AbstractLocation(data.getCaster().getEntity().getLocation().getWorld(), (double)this.x, (double)this.y, (double)this.z)
         );
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      SkillAdapter.get().pushButton(data.getCaster(), target);
      return SkillResult.SUCCESS;
   }
}
