package io.lumine.mythic.core.skills.stats.types;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.events.MythicStatChangeEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringListProp;
import io.lumine.mythic.bukkit.utils.config.properties.types.StringProp;
import io.lumine.mythic.core.config.Scope;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.stats.StatExecutor;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.utils.math.Functions;
import io.lumine.mythic.core.utils.math.Operators;
import java.io.File;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class CompositeStat extends StatType {
   private static final StringProp FORMULA = Property.String(Scope.STATS, "Formula", "0");
   private static final StringListProp PARENTS = Property.StringList(Scope.STATS, "ParentStats");
   private Expression expression;
   private final Collection<StatType> parentStats = Sets.newConcurrentHashSet();
   private final ReentrantLock lock = new ReentrantLock();

   public CompositeStat(File file, String key) {
      super(file, key);
   }

   @Override
   protected void load(StatExecutor manager) {
      for (String key : PARENTS.get(this)) {
         Optional<StatType> maybeStat = this.getManager().getStat(key);
         if (maybeStat.isEmpty()) {
            MythicLogger.error("Invalid parent stat {0}", key);
            return;
         }

         this.parentStats.add(maybeStat.get());
      }

      String formula = FORMULA.get(this);

      try {
         ExpressionBuilder builder = new ExpressionBuilder(formula).operator(Operators.operators).functions(Functions.functions);

         for (StatType parentStat : this.parentStats) {
            builder.variable(parentStat.getFormulaKey());
         }

         this.expression = builder.build();
      } catch (Exception e) {
         MythicLogger.error("Invalid stat formula defined: " + formula);
         e.printStackTrace();
         this.expression = new ExpressionBuilder("0").build();
         return;
      }

      Events.subscribe(MythicStatChangeEvent.class).handler(event -> {
         SkillCaster caster = event.getCaster();
         if (this.parentStats.contains(event.getStat())) {
            StatRegistry stats = caster.getStatRegistry();
            if (stats != null) {
               stats.putBaseValue(this, this.calculateBaseValue(stats));
            }
         }
      }).bindWith(this);
   }

   @Override
   public boolean isApplicable(AbstractEntity entity) {
      return entity.isPlayer();
   }

   public double calculateBaseValue(StatRegistry statRegistry) {
      this.lock.lock();

      double value;
      try {
         Expression expression = this.expression;

         for (StatType parentStat : this.parentStats) {
            expression.setVariable(parentStat.getFormulaKey(), statRegistry.get(parentStat));
         }

         double val = expression.evaluate();
         value = val;
      } finally {
         this.lock.unlock();
      }

      return value;
   }
}
