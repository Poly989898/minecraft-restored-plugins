package io.lumine.mythic.lib.comp.mythicmobs.condition;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

@MythicCondition(author = "Gunging", name = "mmodamagetype", aliases = {}, description = "If the skill is of this damage type. ")
public class HasDamageTypeCondition extends SkillCondition implements ISkillMetaCondition {
   protected final boolean exact;
   protected final Set<DamageType> types = new HashSet<>();

   public HasDamageTypeCondition(@NotNull MythicLineConfig mlc) {
      super(mlc.getLine());
      this.exact = mlc.getBoolean("exact", false);
      String typesString = mlc.getString(new String[]{"type", "t", "types"}, null, new String[0]);
      if (typesString != null && !typesString.isEmpty() && !typesString.equalsIgnoreCase("NONE")) {
         for (String str : typesString.replace("<&cm>", ",").split(",")) {
            this.types.add(DamageType.valueOf(UtilityMethods.enumName(str)));
         }
      }
   }

   public boolean check(SkillMetadata skillMetadata) {
      Set<DamageType> attackDamageTypes = new HashSet<>();

      for (AbstractEntity target : skillMetadata.getEntityTargets()) {
         AttackMetadata opt = MythicLib.plugin.getDamage().getRegisteredAttackMetadata(target.getBukkitEntity());
         if (opt != null) {
            attackDamageTypes.addAll(opt.getDamage().collectTypes());
         }
      }

      if (this.exact) {
         return attackDamageTypes.equals(this.types);
      }

      for (DamageType damageType : this.types) {
         if (!attackDamageTypes.contains(damageType)) {
            return false;
         }
      }

      return true;
   }
}
