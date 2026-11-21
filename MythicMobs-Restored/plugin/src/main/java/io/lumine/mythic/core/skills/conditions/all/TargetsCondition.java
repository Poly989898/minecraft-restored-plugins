package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.bukkit.utils.numbers.RangedInt;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Optional;

@MythicCondition(
   author = "Ashijin",
   name = "targets",
   aliases = {},
   description = "Tests if the number of inherited targets from the parent skilltree matches the given range."
)
public class TargetsCondition extends SkillCondition implements ISkillMetaCondition {
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "Range of how many targets to check for",
      defValue = ">0"
   )
   private RangedInt amount;
   @MythicField(
      name = "location",
      aliases = {"target", "to", "loc", "l", "coordinates", "c"},
      version = "?",
      description = "The coordinates to teleport to in format x,y,z."
   )
   private Optional<SkillTargeter> targeter = Optional.empty();

   public TargetsCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.amount = new RangedInt(mlc.getString(new String[]{"amount", "a"}, this.conditionVar, ">0"));
      String tar = mlc.getString("targeter", null);
      if (tar != null) {
         this.targeter = Optional.ofNullable(getPlugin().getSkillManager().getTargeter(tar));
      }
   }

   @Override
   public boolean check(SkillMetadata meta) {
      if (this.targeter.isPresent()) {
         SkillTargeter targeter = this.targeter.get();
         meta = getPlugin().getSkillManager().processTargets(meta.deepClone(), targeter);
      }

      if (meta.getEntityTargets() != null) {
         return this.amount.equals(meta.getEntityTargets().size());
      } else {
         return meta.getLocationTargets() != null ? this.amount.equals(meta.getLocationTargets().size()) : false;
      }
   }
}
