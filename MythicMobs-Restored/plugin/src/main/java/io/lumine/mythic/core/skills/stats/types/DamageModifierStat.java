package io.lumine.mythic.core.skills.stats.types;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.stats.StatSnapshot;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringListProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringProp;
import io.lumine.mythic.core.config.Scope;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.conditions.InvalidCondition;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMetadataKey;
import io.lumine.mythic.core.skills.stats.StatExecutor;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.skills.stats.TriggerModifyingStat;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.skills.triggers.meta.DamagedMetadata;
import io.lumine.mythic.core.skills.triggers.meta.EntityAttackMetadata;
import io.lumine.mythic.core.utils.math.Functions;
import io.lumine.mythic.core.utils.math.Operators;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class DamageModifierStat extends StatType implements TriggerModifyingStat {
   private static final StringProp TYPE = Property.String(Scope.STATS, "DamageType", null);
   private static final StringProp FORMULA = Property.String(Scope.STATS, "DamageFormula", null);
   private static final StringListProp CONDITIONS = Property.StringList(Scope.STATS, "Conditions");
   private static final StringListProp PARENTS = Property.StringList(Scope.STATS, "ParentStats");
   private static final StringListProp TRIGGER_STATS = Property.StringList(Scope.STATS, "TriggerStats");
   private String damageType;
   private boolean allDamage = false;
   private Expression expression;
   private List<SkillCondition> conditions = Lists.newArrayList();
   private final Collection<StatType> parentStats = Sets.newConcurrentHashSet();
   private final Map<StatType, String> triggerStats = Maps.newConcurrentMap();

   public DamageModifierStat(Object scope, String key) {
      super(scope, key);
   }

   @Override
   public void initialize(StatExecutor manager) {
      super.initialize(manager);
      this.damageType = TYPE.fget(this.getScope(), this);
      if (this.damageType != null && this.damageType.equalsIgnoreCase("ALL")) {
         this.allDamage = true;
      }

      for (String key : PARENTS.fget(this.getScope(), this)) {
         Optional<StatType> maybeStat = this.getManager().getStat(key);
         if (maybeStat.isEmpty()) {
            MythicLogger.error("Invalid parent stat {0}", key);
            return;
         }

         this.parentStats.add(maybeStat.get());
      }

      for (String key : TRIGGER_STATS.fget(this.getScope(), this)) {
         try {
            String[] split = key.split(" ");
            String statKey = split[0];
            String formulaKey = split[1];
            Optional<StatType> maybeStat = this.getManager().getStat(statKey);
            if (maybeStat.isEmpty()) {
               MythicLogger.error("Invalid trigger stat {0}", key);
               return;
            }

            this.triggerStats.put(maybeStat.get(), formulaKey);
         } catch (Throwable e) {
            MythicLogger.error("Invalid Trigger Stat {0}", key);
         }
      }

      for (String cond : CONDITIONS.fget(this.getScope(), this)) {
         SkillCondition c = MythicBukkit.inst().getSkillManager().getCondition(cond);
         if (!(c instanceof InvalidCondition)) {
            this.conditions.add(c);
         }
      }

      String formula = FORMULA.fget(this.getScope(), this);

      try {
         ExpressionBuilder builder = new ExpressionBuilder(formula).operator(Operators.operators).functions(Functions.functions).variables("d", "v");

         for (StatType parentStat : this.parentStats) {
            builder.variable(parentStat.getFormulaKey());
         }

         for (String stat : this.triggerStats.values()) {
            builder.variable(stat);
         }

         this.expression = builder.build();
      } catch (Exception ex) {
         MythicLogger.error("X Invalid DamageModifier formula defined: " + formula);
         ex.printStackTrace();
         this.expression = new ExpressionBuilder("0").build();
      }
   }

   @Override
   public void processTrigger(SkillMetadata skillMetadata, SkillTriggerMetadata triggerMetadata, StatSnapshot statRegistry, double value) {
      if (this.triggerStats.isEmpty() || skillMetadata.getTrigger() != null) {
         if (triggerMetadata instanceof EntityAttackMetadata attackMetadata) {
            for (SkillCondition condition : this.conditions) {
               if (!condition.evaluateCaster(skillMetadata)) {
                  return;
               }
            }

            if (this.damageType == null) {
               double damage = attackMetadata.getDamage();
               double newDamage = this.createNewDamage(skillMetadata, statRegistry, value, damage);
               attackMetadata.setDamage(newDamage);
            } else if (this.allDamage) {
               double damage = attackMetadata.getDamageModifier();
               double newDamage = this.createNewDamage(skillMetadata, statRegistry, value, damage);
               attackMetadata.setDamageModifier(newDamage);
            } else {
               if (!attackMetadata.getBonusDamage().containsKey(this.damageType)) {
                  return;
               }

               Double damage = attackMetadata.getBonusDamage().computeIfAbsent(this.damageType, d -> value);
               double newDamage = this.createNewDamage(skillMetadata, statRegistry, value, damage);
               attackMetadata.getBonusDamage().put(this.damageType, newDamage);
            }
         } else if (triggerMetadata instanceof DamagedMetadata damagedMetadata) {
            for (SkillCondition conditionx : this.conditions) {
               if (!conditionx.evaluateCaster(skillMetadata)) {
                  return;
               }
            }

            if (this.damageType == null) {
               double damage = damagedMetadata.getDamage();
               double newDamage = this.createNewDamage(skillMetadata, statRegistry, value, damage);
               damagedMetadata.setDamage(newDamage);
            } else {
               if (!damagedMetadata.getBonusDamage().containsKey(this.damageType)) {
                  return;
               }

               Double damage = damagedMetadata.getBonusDamage().computeIfAbsent(this.damageType, d -> value);
               double newDamage = this.createNewDamage(skillMetadata, statRegistry, value, damage);
               damagedMetadata.getBonusDamage().put(this.damageType, newDamage);
            }
         }
      }
   }

   private double createNewDamage(SkillMetadata skillMetadata, StatSnapshot statRegistry, double value, double damage) {
      Expression expression = this.expression;

      for (StatType parentStat : this.parentStats) {
         expression.setVariable(parentStat.getFormulaKey(), statRegistry.get(parentStat));
      }

      if (!this.triggerStats.isEmpty()) {
         StatRegistry triggerStatRegistry = this.getStatRegistry(skillMetadata.getTrigger());

         for (Entry<StatType, String> entry : this.triggerStats.entrySet()) {
            expression.setVariable(entry.getValue(), triggerStatRegistry.get(entry.getKey()));
         }
      }

      double newDamage = expression.setVariable("d", damage).setVariable("v", value).evaluate();
      double damageModifier = newDamage / damage;
      if (newDamage > 0.0) {
         skillMetadata.setMetadata(PlaceholderMetadataKey.STAT_DAMAGE_MODIFIER, damageModifier);
         this.runProcSkills(skillMetadata, null);
      }

      return newDamage;
   }
}
