package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Player;

@MythicMechanic(
   author = "Ashijin",
   version = "4.10",
   name = "recoil",
   aliases = {"effect:recoil", "e:recoil"},
   description = "Causes the player's screen to recoil"
)
public class RecoilEffect extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "duration",
      aliases = {"d"},
      version = "4.8",
      description = "How long the effect should last"
   )
   private PlaceholderFloat recoil;
   private PlaceholderFloat pitchMod;
   private PlaceholderFloat yawMod;
   private PlaceholderFloat sneakingMod;
   private PlaceholderFloat sprintingMod;

   public RecoilEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.recoil = mlc.getPlaceholderFloat(new String[]{"recoil", "r"}, 1.0F);
      this.pitchMod = mlc.getPlaceholderFloat(new String[]{"pitch", "p"}, "-10to10");
      this.yawMod = mlc.getPlaceholderFloat(new String[]{"yaw", "y"}, "-0.25to0.25");
      this.sneakingMod = mlc.getPlaceholderFloat(new String[]{"sneaking", "sn"}, 0.75F);
      this.sprintingMod = mlc.getPlaceholderFloat(new String[]{"sprinting", "sp"}, 1.25F);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         Player player = (Player)target.getBukkitEntity();
         float currentPitch = target.getLocation().getPitch();
         float currentYaw = target.getLocation().getYaw();
         float basePitchDelta = this.pitchMod.get(data);
         float baseYawDelta = this.yawMod.get(data);
         float recoilValue = this.recoil.get(data);
         float sneakingMod = this.sneakingMod.get(data);
         float sprintingMod = this.sprintingMod.get(data);
         Schedulers.runOnRegion(player, () -> {
            float pitchDelta = basePitchDelta;
            if (player.isSneaking()) {
               pitchDelta = basePitchDelta * sneakingMod;
            } else if (player.isSprinting()) {
               pitchDelta = basePitchDelta * sprintingMod;
            }

            pitchDelta *= recoilValue;
            float yawDelta = baseYawDelta * recoilValue;
            this.getPlugin().getVolatileCodeHandler().getEntityHandler().setPlayerRotation(target.asPlayer(), yawDelta, pitchDelta, true);
         });
         return SkillResult.SUCCESS;
      }
   }
}
