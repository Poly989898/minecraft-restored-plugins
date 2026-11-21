package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "summoni",
   description = "Summons mobs at the target location with different defaults"
)
public class SummonIndependentMechanic extends SummonMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   public SummonIndependentMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.inheritFaction = mlc.getBoolean(new String[]{"inheritfaction", "if"}, false);
      this.summonerIsOwner = mlc.getBoolean(new String[]{"summonerisowner", "sio"}, false);
      this.summonerIsParent = mlc.getBoolean(new String[]{"summonerisparent", "sip"}, false);
      this.inheritDespawnOption = mlc.getBoolean(new String[]{"inheritdespawn", "inheritdespawnoption", "ido"}, false);
      this.useTargetYaw = mlc.getBoolean(new String[]{"usetargetyaw", "uty"}, true);
      this.useTargetPitch = mlc.getBoolean(new String[]{"usetargetpitch", "utp"}, true);
   }
}
