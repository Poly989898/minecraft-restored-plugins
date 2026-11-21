package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.VariableCondition;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableType;
import io.lumine.mythic.core.skills.variables.interfaces.ContainingVariable;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

@MythicCondition(
   author = "Lxlp",
   name = "variableContains",
   aliases = {"variableContain", "varContains", "varContain", "varCont"},
   version = "5.10",
   description = "Checks if the specified variable contains a given value. This can either be done between strings or sets"
)
public class VariableContainsCondition
   extends VariableCondition
   implements IEntityCondition,
   ILocationCondition,
   ISkillMetaCondition,
   ISkillMetaComparisonCondition {
   @MythicField(
      name = "value",
      aliases = {"val", "v"},
      description = "The value to match"
   )
   private PolymorphicPlaceholder value;
   private PlaceholderString compareType;
   private static final Map<String, VariableContainsCondition.CompareType> COMPARE_TYPE_MAP = new HashMap<>();

   public VariableContainsCondition(String line, MythicLineConfig mlc) {
      super(line, mlc);

      try {
         this.value = PolymorphicPlaceholder.of(mlc.getString(new String[]{"value", "val", "v"}, null));
         this.value.cache(VariableType.STRING);
         this.compareType = PlaceholderString.of(mlc.getString(new String[]{"compareType", "compare", "comp"}, "ALL"));
      } catch (Exception e) {
         MythicLogger.errorConditionConfig(this, mlc, "Variable value must be set.");
      }
   }

   private void handleException(Exception ex) {
      MythicLogger.error("Failed to process VariableContains condition.");
      MythicLogger.handleMinorError(ex);
   }

   private boolean checkContainsPlain(String value, String target, @NotNull VariableContainsCondition.CompareType compareType) {
      MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "VariableContains: checking {0} \u2208 {1}", target, value);
      if (compareType == VariableContainsCondition.CompareType.EXACT_ENTRY) {
         String targetEntry = target.trim();

         for (String entry : value.split(",")) {
            if (entry.trim().equals(targetEntry)) {
               return true;
            }
         }

         return false;
      } else if (value.contains(",")) {
         Set<String> valueSet = new HashSet<>(Arrays.asList(value.split(",")));
         if (target.contains(",")) {
            String[] targets = target.split(",");
            if (Objects.requireNonNull(compareType) == VariableContainsCondition.CompareType.ANY) {
               for (String t : targets) {
                  if (valueSet.contains(t)) {
                     return true;
                  }
               }

               return false;
            } else {
               for (String tx : targets) {
                  if (!valueSet.contains(tx)) {
                     return false;
                  }
               }

               return true;
            }
         } else {
            return valueSet.contains(target);
         }
      } else {
         return switch (compareType) {
            case STARTS_WITH -> value.startsWith(target);
            case ENDS_WITH -> value.endsWith(target);
            default -> value.contains(target);
         };
      }
   }

   private boolean checkWithContext(VariableRegistry registry, PlaceholderContext context) {
      VariableContainsCondition.CompareType compareType = VariableContainsCondition.CompareType.getOperator(this.compareType.get(context));
      if (compareType == VariableContainsCondition.CompareType.ALL && registry.get(this.key.get(context)) instanceof ContainingVariable containingVariable) {
         return containingVariable.checkContainsAll(this.value, context);
      } else {
         String value = this.value.getString(context);

         try {
            String variableValue = registry.getString(this.key.get(context));
            return this.checkContainsPlain(variableValue, value, compareType);
         } catch (Exception e) {
            this.handleException(e);
            return false;
         }
      }
   }

   @Override
   public boolean check(AbstractEntity target) {
      PlaceholderContext context = PlaceholderContext.builder().entity(target).build();
      VariableRegistry registry = getPlugin().getVariableManager().getRegistry(this.scope, target);
      return this.checkWithContext(registry, context);
   }

   @Override
   public boolean check(AbstractLocation target) {
      PlaceholderContext context = PlaceholderContext.builder().location(target).build();
      VariableRegistry registry = getPlugin().getVariableManager().getRegistry(this.scope, target);
      return this.checkWithContext(registry, context);
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity target) {
      PlaceholderContext context = PlaceholderContext.builder().meta(meta).entity(target).build();
      VariableRegistry registry = getPlugin().getVariableManager().getRegistry(this.scope, meta, target);
      return this.checkWithContext(registry, context);
   }

   @Override
   public boolean check(SkillMetadata meta) {
      PlaceholderContext context = PlaceholderContext.builder().meta(meta).entity(meta.getCaster().getEntity()).build();
      VariableRegistry registry = getPlugin().getVariableManager().getRegistry(this.scope, meta, meta.getCaster().getEntity());
      return this.checkWithContext(registry, context);
   }

   private static enum CompareType {
      ALL("ALL"),
      ANY("ANY"),
      EXACT_ENTRY("EXACT_ENTRY", "EXACTENTRY", "EE"),
      STARTS_WITH("STARTS_WITH", "SW"),
      ENDS_WITH("ENDS_WITH", "EW");

      private CompareType(String... aliases) {
         for (String alias : aliases) {
            VariableContainsCondition.COMPARE_TYPE_MAP.put(alias, this);
         }
      }

      public static VariableContainsCondition.CompareType getOperator(String op) {
         return VariableContainsCondition.COMPARE_TYPE_MAP.getOrDefault(op.toUpperCase(), ALL);
      }
   }
}
