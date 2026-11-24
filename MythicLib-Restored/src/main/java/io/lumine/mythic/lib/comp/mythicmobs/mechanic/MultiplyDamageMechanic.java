package io.lumine.mythic.lib.comp.mythicmobs.mechanic;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageType;

@MythicMechanic(
   author = "Indyuce",
   name = "multiplydamage",
   aliases = {"multdamage", "multdmg"},
   description = "Increases damage of current attack by a certain factor"
)
public class MultiplyDamageMechanic implements INoTargetSkill {
   protected final PlaceholderDouble amount;
   protected final DamageType type;
   protected final boolean additive;

   public MultiplyDamageMechanic(MythicLineConfig config) {
      this.amount = PlaceholderDouble.of(config.getString(new String[]{"amount", "a"}, "1", new String[0]));
      String typeFormat = config.getString(new String[]{"type", "t"}, "", new String[0]);
      this.type = typeFormat.isEmpty() ? null : DamageType.valueOf(typeFormat.toUpperCase().replace(" ", "_").replace("-", "_"));
      this.additive = config.getBoolean("additive", false);
   }

   public SkillResult cast(SkillMetadata skillMetadata) {
      for (AbstractEntity target : skillMetadata.getEntityTargets()) {
         AttackMetadata attackMeta = MythicLib.plugin.getDamage().getRegisteredAttackMetadata(target.getBukkitEntity());
         if (attackMeta != null) {
            double a = this.amount.get(skillMetadata);
            if (this.additive) {
               if (this.type == null) {
                  attackMeta.getDamage().additiveModifier(a);
               } else {
                  attackMeta.getDamage().additiveModifier(a, this.type);
               }
            } else if (this.type == null) {
               attackMeta.getDamage().multiplicativeModifier(a);
            } else {
               attackMeta.getDamage().multiplicativeModifier(a, this.type);
            }
         }
      }

      return SkillResult.SUCCESS;
   }
}
