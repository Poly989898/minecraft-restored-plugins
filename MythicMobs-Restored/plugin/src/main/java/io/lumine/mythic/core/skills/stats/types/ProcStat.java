package io.lumine.mythic.core.skills.stats.types;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.stats.StatSnapshot;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringListProp;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.config.Scope;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.conditions.InvalidCondition;
import io.lumine.mythic.core.skills.stats.PercentModifyer;
import io.lumine.mythic.core.skills.stats.StatExecutor;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.skills.stats.TriggerModifyingStat;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import java.io.File;
import java.util.List;

public class ProcStat extends StatType implements PercentModifyer, TriggerModifyingStat {
   private static final StringListProp CONDITIONS = Property.StringList(Scope.STATS, "Conditions");
   private List<SkillCondition> conditions = Lists.newArrayList();

   public ProcStat(File file, String key) {
      super(file, key);
   }

   @Override
   public void initialize(StatExecutor manager) {
      super.initialize(manager);

      for (String cond : CONDITIONS.fget(this.getScope(), this)) {
         SkillCondition c = MythicBukkit.inst().getSkillManager().getCondition(cond);
         if (!(c instanceof InvalidCondition)) {
            this.conditions.add(c);
         }
      }
   }

   @Override
   public void processTrigger(SkillMetadata skillMetadata, SkillTriggerMetadata triggerMetadata, StatSnapshot statRegistry, double value) {
      double roll = Numbers.randomDouble();
      if (value >= roll) {
         for (SkillCondition condition : this.conditions) {
            if (!condition.evaluateCaster(skillMetadata)) {
               return;
            }
         }

         this.runProcSkills(skillMetadata, triggerMetadata);
      }
   }
}
