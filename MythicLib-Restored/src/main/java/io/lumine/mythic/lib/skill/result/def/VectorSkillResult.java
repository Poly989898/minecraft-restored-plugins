package io.lumine.mythic.lib.skill.result.def;

import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.result.SkillResult;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VectorSkillResult implements SkillResult {
   private final Vector target;

   public VectorSkillResult(SkillMetadata skillMeta) {
      this.target = this.getTargetDirection(skillMeta.getCaster().getPlayer(), skillMeta.getTargetEntityOrNull());
   }

   public VectorSkillResult(@Nullable Vector vec) {
      this.target = vec;
   }

   @Nullable
   public Vector getTarget() {
      return this.target;
   }

   @NotNull
   public Vector getTargetDirection(Player player, Entity target) {
      return target != null && target.getWorld().equals(player.getWorld())
         ? target.getLocation().add(0.0, target.getHeight() / 2.0, 0.0).subtract(player.getLocation().add(0.0, 1.3, 0.0)).toVector().normalize()
         : player.getEyeLocation().getDirection();
   }

   @Override
   public boolean isSuccessful() {
      return this.target != null;
   }
}
