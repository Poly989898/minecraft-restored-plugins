package io.lumine.mythic.core.skills.stats.types;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.stats.StatExecution;
import io.lumine.mythic.api.skills.stats.StatSnapshot;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringProp;
import io.lumine.mythic.core.config.Scope;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.stats.StatExecutor;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.skills.stats.TriggerModifyingStat;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.triggers.meta.EntityAttackMetadata;
import java.io.File;
import java.util.Collection;
import lombok.Getter;

@Getter
public class ElementalDefenseStat extends StatType implements TriggerModifyingStat {
   private static final StringProp ELEMENTS = Property.String(Scope.STATS, "Element", null);
   private final Collection<String> elements = Sets.newConcurrentHashSet();

   public ElementalDefenseStat(File file, String key) {
      super(file, key);
   }

   @Override
   public void initialize(StatExecutor manager) {
      super.initialize(manager);
      this.executionPoint = StatExecution.PRE;
      this.getApplicableTriggers().add(SkillTriggers.DAMAGED);
      String elements = ELEMENTS.get(this);
      if (elements != null) {
         for (String ele : elements.split(",")) {
            this.elements.add(ele.toUpperCase());
         }
      }
   }

   @Override
   public void processTrigger(SkillMetadata skillMetadata, SkillTriggerMetadata triggerMetadata, StatSnapshot statRegistry, double value) {
      if (triggerMetadata instanceof EntityAttackMetadata attackMetadata) {
         if (!this.elements.isEmpty() && !this.elements.contains(attackMetadata.getDamageCause().toUpperCase())) {
            return;
         }

         double damage = attackMetadata.getDamage();
         damage -= value;
         if (damage < 0.0) {
            damage = 0.0;
         }

         attackMetadata.setDamage(damage);
      }
   }
}
