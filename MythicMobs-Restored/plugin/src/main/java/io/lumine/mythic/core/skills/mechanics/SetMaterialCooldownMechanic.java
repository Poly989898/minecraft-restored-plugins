package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@MythicMechanic(
   author = "Ash",
   name = "setMaterialCooldown",
   description = "Sets the given material to be on cooldown for the target player"
)
public class SetMaterialCooldownMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "material",
      aliases = {"mat", "m"},
      description = "The material to apply a cooldown to",
      defValue = "ENDER_PEARL"
   )
   private Material material;
   @MythicField(
      name = "duration",
      aliases = {"d"},
      description = "Cooldown duration in ticks",
      defValue = "100"
   )
   private PlaceholderDuration duration;

   public SetMaterialCooldownMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      String strMaterial = mlc.getString(new String[]{"material", "mat", "m"}, "ENDER_PEARL").toUpperCase();

      try {
         this.material = Material.matchMaterial(strMaterial);
      } catch (Error | Exception e) {
         MythicLogger.errorMechanicConfig(this, mlc, "Material '" + strMaterial + "' not found");
         this.material = Material.ENDER_PEARL;
      }

      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "100");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (BukkitAdapter.adapt(target) instanceof Player player) {
         player.setCooldown(this.material, this.duration.get(data, target).getTicks());
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
