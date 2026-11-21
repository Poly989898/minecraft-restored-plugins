package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@MythicCondition(
   author = "Joshinn",
   name = "materialIsOnCooldown",
   aliases = {"materialCooldown", "matCooldown"},
   description = "Checks if the target's specific material is on cooldown."
)
public class MaterialCooldownCondition extends SkillCondition implements IEntityCondition {
   private PlaceholderString material;

   public MaterialCooldownCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.material = mlc.getPlaceholderString(new String[]{"material", "mat", "m"}, "ENDER_PEARL");
   }

   @Override
   public boolean check(AbstractEntity e) {
      if (e.isPlayer()) {
         Material varMaterial = Material.matchMaterial(this.material.get(e));
         return varMaterial == null ? false : ((Player)e.getBukkitEntity()).getCooldown(varMaterial) > 0;
      } else {
         return false;
      }
   }
}
