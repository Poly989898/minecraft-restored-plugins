package io.lumine.mythic.core.skills.stats;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.skills.stats.StatExecution;
import io.lumine.mythic.api.skills.stats.StatSnapshot;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicStatChangeEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.config.properties.PropertyHolder;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableConsumer;
import io.lumine.mythic.bukkit.utils.terminable.TerminableRegistry;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.config.Scope;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import io.lumine.mythic.core.utils.math.Functions;
import io.lumine.mythic.core.utils.math.Operators;
import java.io.File;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.Getter;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public abstract class StatType implements PropertyHolder, Terminable, TerminableConsumer {
   @Getter
   private StatExecutor manager;
   @Getter
   private Object scope;
   @Getter
   private final MythicConfig config;
   private final TerminableRegistry registry = TerminableRegistry.create();
   @Getter
   private boolean enabled;
   @Getter
   private String key;
   @Getter
   private String formulaKey;
   @Getter
   protected StatExecution executionPoint = StatExecution.NONE;
   @Getter
   private int priority = 0;
   @Getter
   private boolean alwaysActive;
   @Getter
   private final Collection<SkillTrigger> applicableTriggers = Sets.newConcurrentHashSet();
   private final Collection<SkillMechanic> skills = Lists.newArrayList();
   private double baseValue = 0.0;
   @Getter
   private double minValue = 0.0;
   @Getter
   private double maxValue = 0.0;
   @Getter
   private String display;
   private PlaceholderString formattingAdditive;
   private PlaceholderString formattingMultiply;
   private PlaceholderString formattingCompound;
   private PlaceholderString formattingSet;
   @Getter
   private int formattingRounding;
   @Getter
   private int formattingSortOrder;
   @Getter
   private Boolean showInItemLore = true;
   @Getter
   private Boolean showAdditiveInItemLore = true;
   @Getter
   private Boolean showMultiplyInItemLore = true;
   @Getter
   private Boolean showCompoundInItemLore = true;
   @Getter
   private Boolean showSetterInItemLore = true;
   private Expression expression;
   @Getter
   private final Collection<StatType> parentStats = Sets.newConcurrentHashSet();
   @Getter
   private boolean initialized = false;

   public StatType(String key) {
      this(Scope.STATS, key);
   }

   public StatType(Object scope, String key) {
      this.key = key;
      this.scope = scope;
      this.config = createConfigView(scope, key);
   }

   public void initialize(StatExecutor manager) {
      this.enabled = this.config.getBoolean("Enabled", false);
      if (!this.initialized) {
         this.display = this.config.getString("Display", "");
         this.formattingAdditive = PlaceholderString.of(this.config.getString("Formatting.Additive", "+<value> <display>").replace("<display>", this.display));
         this.formattingMultiply = PlaceholderString.of(this.config.getString("Formatting.Multiply", "+<value>% <display>").replace("<display>", this.display));
         this.formattingCompound = PlaceholderString.of(this.config.getString("Formatting.Compound", "x<value>% <display>").replace("<display>", this.display));
         this.formattingSet = PlaceholderString.of(this.config.getString("Formatting.Setter", "Force <value> <display>").replace("<display>", this.display));
         this.formattingRounding = this.config.getInteger("Formatting.Rounding", -1);
         this.formattingSortOrder = this.config.getInteger("Formatting.Sort", 0);
         this.showInItemLore = this.config.getBoolean("Formatting.ShowInItemLore", true);
         this.showAdditiveInItemLore = this.config.getBoolean("ShowInLore.Additive", this.showInItemLore);
         this.showMultiplyInItemLore = this.config.getBoolean("ShowInLore.Multiply", this.showInItemLore);
         this.showCompoundInItemLore = this.config.getBoolean("ShowInLore.Compound", this.showInItemLore);
         this.showSetterInItemLore = this.config.getBoolean("ShowInLore.Setter", this.showInItemLore);
         if (this.formattingRounding < 0) {
            this.formattingRounding = Scope.CONFIG_ITEMS.getConfig().getInteger("Configuration.ItemStats.Rounding", 2);
         }

         if (this.enabled) {
            this.manager = manager;
            this.minValue = this.config.getDouble("MinValue", 0.0);
            this.maxValue = this.config.getDouble("MaxValue", Double.MAX_VALUE);
            String formulaKey = this.config.getString("FormulaKey", null);
            if (formulaKey == null) {
               this.formulaKey = this.key;
            } else {
               this.formulaKey = formulaKey;
            }

            this.priority = this.config.getInteger("Priority", 0);
            this.alwaysActive = this.config.getBoolean("AlwaysActive", false);
            this.executionPoint = this.config.getEnum("ExecutionPoint", StatExecution.class, StatExecution.NONE);

            for (String in : this.config.getStringList("Triggers")) {
               SkillTrigger trigger = SkillTrigger.get(in);
               if (trigger != null) {
                  this.getApplicableTriggers().add(trigger);
               }
            }

            List<String> skills = this.config.getStringList("Skills");
            if (!skills.isEmpty()) {
               MythicBukkit.inst().getSkillManager().queueSecondPass(() -> {
                  for (String line : skills) {
                     line = MythicLineConfigImpl.unparseBlock(line);
                     SkillMechanic mechanic = MythicBukkit.inst().getSkillManager().getMechanic(line);
                     this.skills.add(mechanic);
                  }
               });
            }

            for (String parentKey : this.config.getStringList("ParentStats")) {
               Optional<StatType> maybeStat = this.getManager().getStat(parentKey);
               if (maybeStat.isEmpty()) {
                  MythicLogger.error("Invalid parent stat {0}", parentKey);
                  return;
               }

               this.parentStats.add(maybeStat.get());
            }

            if (!this.parentStats.isEmpty()) {
               for (StatType parentStat : this.parentStats) {
                  if (!parentStat.isInitialized()) {
                     parentStat.initialize(manager);
                  }
               }

               String formula = this.config.getString("Formula", null);
               if (formula != null) {
                  try {
                     ExpressionBuilder builder = new ExpressionBuilder(formula).operator(Operators.operators).functions(Functions.functions);

                     for (StatType parentStatx : this.parentStats) {
                        builder.variable(parentStatx.getFormulaKey());
                     }

                     this.expression = builder.build();
                  } catch (Exception e) {
                     MythicLogger.error("X Invalid stat formula defined: " + formula);
                     e.printStackTrace();
                     this.expression = new ExpressionBuilder("0").build();
                     return;
                  }
               }

               Events.subscribe(MythicStatChangeEvent.class).handler(event -> {
                  SkillCaster caster = event.getCaster();
                  if (this.parentStats.contains(event.getStat())) {
                     StatRegistry stats = caster.getStatRegistry();
                     if (stats != null) {
                        stats.putBaseValue(this, this.calculateFormulaicBaseValue(stats));
                     }
                  }
               }).bindWith(this);
            } else {
               this.baseValue = this.config.getDouble(new String[]{"PlayerBaseValue", "BaseValue"}, 0.0);
            }

            this.load(manager);
            this.initialized = true;
         }
      }
   }

   protected void load(StatExecutor manager) {
   }

   public boolean isApplicable(AbstractEntity entity) {
      return true;
   }

   public Optional<StatSnapshot> getCasterStats(SkillMetadata skillMetadata) {
      return skillMetadata.getCasterStats();
   }

   public StatRegistry getStatRegistry(AbstractEntity entity) {
      return MythicBukkit.inst().getStatManager().getStatRegistry(entity).orElse(null);
   }

   public double getStat(AbstractEntity entity) {
      Optional<StatRegistry> maybeRegistry = MythicBukkit.inst().getStatManager().getStatRegistry(entity);
      return maybeRegistry.isEmpty() ? 0.0 : maybeRegistry.get().get(this);
   }

   public String getFormattedValue(StatModifierType operation, double value) {
      return switch (operation) {
         case ADDITIVE -> this.formattingAdditive.get().replace("<value>", this.getFormattedNumber(operation, value));
         case ADDITIVE_MULTIPLIER -> this.formattingMultiply.get().replace("<value>", this.getFormattedNumber(operation, value));
         case COMPOUND_MULTIPLIER -> this.formattingCompound.get().replace("<value>", this.getFormattedNumber(operation, value));
         case SETTER -> this.formattingSet.get().replace("<value>", this.getFormattedNumber(operation, value));
      };
   }

   private String getFormattedNumber(StatModifierType operation, double value) {
      NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.US);
      percentFormat.setMaximumFractionDigits(this.formattingRounding);

      return switch (operation) {
         case ADDITIVE -> this instanceof PercentModifyer ? percentFormat.format(value) : this.formatRounded(value);
         case ADDITIVE_MULTIPLIER -> percentFormat.format(value);
         case COMPOUND_MULTIPLIER -> percentFormat.format(value);
         case SETTER -> this.formatRounded(value);
      };
   }

   private String formatRounded(double value) {
      return this.formattingRounding < 1 ? String.valueOf(Numbers.toInt(value)) : String.valueOf(Numbers.round(value, this.formattingRounding));
   }

   public boolean ShowInItemLore(StatModifierType operation) {
      return switch (operation) {
         case ADDITIVE -> this.showAdditiveInItemLore;
         case ADDITIVE_MULTIPLIER -> this.showMultiplyInItemLore;
         case COMPOUND_MULTIPLIER -> this.showCompoundInItemLore;
         case SETTER -> this.showSetterInItemLore;
      };
   }

   public String getTooltipLine(StatModifierType operation, double value) {
      return "";
   }

   public boolean isApplicable(SkillTrigger trigger) {
      return this.applicableTriggers.contains(trigger);
   }

   public void processTrigger(SkillMetadata skillMetadata, SkillTriggerMetadata triggerMetadata, StatSnapshot statRegistry, double value) {
   }

   protected void runProcSkills(SkillMetadata skillMetadata, SkillTriggerMetadata triggerMetadata) {
      for (SkillMechanic skill : this.skills) {
         if (skill.isUsableFromCaster(skillMetadata)) {
            skill.execute(skillMetadata);
         }
      }
   }

   @Override
   public String getPropertyNode() {
      return this.key;
   }

   @Override
   public <T extends AutoCloseable> T bind(T binding) {
      this.registry.accept((Terminable)binding);
      return binding;
   }

   @Override
   public void close() throws Exception {
      this.applicableTriggers.clear();
      this.parentStats.clear();
      this.skills.clear();
      this.registry.terminate();
      this.initialized = false;
   }

   public double getBaseValue() {
      return this.getBaseValue(null);
   }

   public double getBaseValue(StatRegistry statRegistry) {
      return this.parentStats.isEmpty() ? this.baseValue : this.calculateFormulaicBaseValue(statRegistry);
   }

   private double calculateFormulaicBaseValue(StatRegistry statRegistry) {
      if (this.expression == null) {
         if (statRegistry == null) {
            return 0.0;
         } else {
            double val = 0.0;

            for (StatType parentStat : this.parentStats) {
               val += statRegistry.get(parentStat);
            }

            return val;
         }
      } else {
         Expression expression = this.expression;
         if (statRegistry == null) {
            for (StatType parentStat : this.parentStats) {
               expression.setVariable(parentStat.getFormulaKey(), parentStat.getBaseValue());
            }
         } else {
            for (StatType parentStat : this.parentStats) {
               expression.setVariable(parentStat.getFormulaKey(), statRegistry.get(parentStat));
            }
         }

         return expression.evaluate();
      }
   }

   private static MythicConfig createConfigView(Object scope, String key) {
      if (scope instanceof Scope configScope) {
         return configScope.getConfig().getNestedConfig(key);
      } else if (scope instanceof File file) {
         return MythicConfig.of(file).getNestedConfig(key);
      } else {
         throw new IllegalArgumentException("Unsupported stat config scope: " + scope);
      }
   }

   protected void writeDefault(String key, Object value) {
      if (!this.getConfig().isSet(key)) {
         this.getConfig().set(key, value);
      }
   }
}
