package io.lumine.mythic.core.skills.stats.types;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.events.MythicStatChangeEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.stats.StatExecutor;
import io.lumine.mythic.core.skills.stats.StatModifierType;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.stats.StatSource;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.utils.math.Functions;
import io.lumine.mythic.core.utils.math.Operators;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MutatorStat extends StatType {
   private static final String[] TARGET_KEYS = new String[]{"Stat", "TargetStat", "Target"};
   private static final String[] OPERATION_KEYS = new String[]{"Operation", "ModifierType", "Modifier", "Type"};
   private final List<MutatorStat.MutatorComponent> components = new ArrayList<>();

   public MutatorStat(File file, String key) {
      super(file, key);
   }

   @Override
   protected void load(StatExecutor manager) {
      this.loadComponents(manager::getStat);
      if (!this.components.isEmpty()) {
         Events.subscribe(MythicStatChangeEvent.class).handler(event -> {
            if (event.getStat() == this) {
               SkillCaster caster = event.getCaster();
               if (caster != null) {
                  StatRegistry stats = event.getStatRegistry();
                  if (stats == null) {
                     stats = caster.getStatRegistry();
                  }

                  if (stats != null) {
                     this.applyMutations(stats, event.getNewValue());
                  }
               }
            }
         }).bindWith(this);
      }
   }

   int loadComponents(Function<String, Optional<StatType>> statLookup) {
      this.components.clear();
      int index = 0;

      for (MythicConfig componentConfig : this.getConfig().getConfigurationList("Components")) {
         MutatorStat.MutatorComponent component = this.createComponent(statLookup, componentConfig, index);
         if (component != null) {
            this.components.add(component);
         }

         index++;
      }

      return this.components.size();
   }

   private MutatorStat.MutatorComponent createComponent(Function<String, Optional<StatType>> statLookup, MythicConfig componentConfig, int index) {
      String targetKey = componentConfig.getString(TARGET_KEYS, null);
      if (targetKey != null && !targetKey.isBlank()) {
         targetKey = targetKey.trim();
         Optional<StatType> maybeTarget = statLookup.apply(targetKey);
         if (maybeTarget.isEmpty()) {
            MythicLogger.error("Mutator stat {0} references invalid target stat {1}.", this.getKey(), targetKey);
            return null;
         } else {
            StatType target = maybeTarget.get();
            if (target.getKey().equalsIgnoreCase(this.getKey())) {
               MythicLogger.error("Mutator stat {0} cannot target itself.", this.getKey());
               return null;
            } else {
               String operationInput = componentConfig.getString(OPERATION_KEYS, StatModifierType.ADDITIVE.name());
               StatModifierType operation = StatModifierType.get(operationInput.trim().toUpperCase(Locale.ROOT));
               if (operation == null) {
                  MythicLogger.error("Mutator stat {0} has invalid operation {1}.", this.getKey(), operationInput);
                  return null;
               } else {
                  String formula = componentConfig.getString("Formula", null);
                  if (formula == null) {
                     formula = "v";
                  } else if (formula.isBlank()) {
                     MythicLogger.error("Mutator stat {0} has a blank formula for target stat {1}.", this.getKey(), targetKey);
                     return null;
                  }

                  try {
                     Expression expression = new ExpressionBuilder(formula).operator(Operators.operators).functions(Functions.functions).variable("v").build();
                     MutatorStat.MutatorStatSource source = new MutatorStat.MutatorStatSource(this.getKey(), index, target.getKey(), operation);
                     return new MutatorStat.MutatorComponent(target, operation, formula, expression, source, new ReentrantLock());
                  } catch (Exception e) {
                     MythicLogger.error("Invalid mutator stat formula defined for {0}: {1}", this.getKey(), formula);
                     e.printStackTrace();
                     return null;
                  }
               }
            }
         }
      } else {
         MythicLogger.error("Mutator stat {0} has a component without a target stat.", this.getKey());
         return null;
      }
   }

   void applyMutations(StatRegistry statRegistry, double value) {
      HashSet<StatType> touchedStats = new HashSet<>();

      for (MutatorStat.MutatorComponent component : this.components) {
         try {
            double modifierValue = component.evaluate(value);
            if (!Double.isFinite(modifierValue)) {
               throw new ArithmeticException("Formula returned a non-finite value: " + modifierValue);
            }

            statRegistry.putValueSilently(component.target(), component.source(), component.operation(), modifierValue);
            touchedStats.add(component.target());
         } catch (Exception e) {
            MythicLogger.error("Failed to apply mutator stat {0} formula {1}", this.getKey(), component.formula());
            e.printStackTrace();
            statRegistry.removeValueSilently(component.target(), component.source());
            touchedStats.add(component.target());
         }
      }

      for (StatType touchedStat : touchedStats) {
         statRegistry.getStatData(touchedStat).ifPresent(StatRegistry.StatMap::updateIfDirty);
      }
   }

   private record MutatorComponent(StatType target, StatModifierType operation, String formula, Expression expression, StatSource source, ReentrantLock lock) {
      private double evaluate(double value) {
         this.lock.lock();

         double value2;
         try {
            value2 = this.expression.setVariable("v", value).evaluate();
         } finally {
            this.lock.unlock();
         }

         return value2;
      }
   }

   private record MutatorStatSource(String mutatorKey, int componentIndex, String targetKey, StatModifierType operation) implements StatSource {
   }
}
