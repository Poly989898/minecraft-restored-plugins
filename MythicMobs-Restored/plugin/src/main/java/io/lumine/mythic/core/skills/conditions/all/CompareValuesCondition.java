package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.variables.VariableType;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.math.MathExpressions;
import java.util.HashMap;
import java.util.Map;

@MythicCondition(
   author = "Lxlp",
   name = "CompareValues",
   aliases = {"comparevalue"},
   version = "5.9.1",
   description = "Compares two values based on a specified operation"
)
public class CompareValuesCondition extends SkillCondition implements IEntityCondition, ILocationCondition, ISkillMetaComparisonCondition {
   private PlaceholderString value1;
   private PlaceholderString value2;
   private final PlaceholderString operator;
   private VariableType type;
   private final MathExpressions.NoVarMemo mathMemo1 = new MathExpressions.NoVarMemo();
   private final MathExpressions.NoVarMemo mathMemo2 = new MathExpressions.NoVarMemo();
   private static final Map<String, CompareValuesCondition.Operator> OPERATOR_MAP = new HashMap<>();

   public CompareValuesCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.operator = PlaceholderString.of(mlc.getString(new String[]{"operator", "op"}, "EQUALS"));
      String strType = mlc.getString(new String[]{"type", "t"}, null);
      if (strType != null) {
         try {
            this.type = VariableType.valueOf(strType.toUpperCase());
         } catch (Exception e) {
            this.type = null;
         }
      } else {
         this.type = null;
      }

      try {
         this.value1 = PlaceholderString.of(mlc.getString(new String[]{"value1", "val1", "v1"}, null));
      } catch (Exception ex) {
         MythicLogger.errorConditionConfig(this, mlc, "Value1 attribute must be set.");
         return;
      }

      try {
         this.value2 = PlaceholderString.of(mlc.getString(new String[]{"value2", "val2", "v2"}, null));
      } catch (Exception ex2) {
         MythicLogger.errorConditionConfig(this, mlc, "Value2 attribute must be set.");
      }
   }

   private double parseMath(MathExpressions.NoVarMemo memo, String eq) {
      return memo.evaluate(eq);
   }

   private boolean compareLogic(String value1, String value2, String operatorString) {
      if (value1 != null && value2 != null) {
         CompareValuesCondition.Operator op = CompareValuesCondition.Operator.getOperator(operatorString);
         if (this.type != null) {
            try {
               return switch (this.type) {
                  case INTEGER -> this.compareValues((long)this.parseMath(this.mathMemo1, value1), (long)this.parseMath(this.mathMemo2, value2), op);
                  case FLOAT -> this.compareValues((float)this.parseMath(this.mathMemo1, value1), (float)this.parseMath(this.mathMemo2, value2), op);
                  case DOUBLE -> this.compareValues(this.parseMath(this.mathMemo1, value1), this.parseMath(this.mathMemo2, value2), op);
                  default -> this.compareValues(value1, value2, op);
               };
            } catch (Exception e) {
               return false;
            }
         } else {
            try {
               return this.compareValues(this.parseMath(this.mathMemo1, value1), this.parseMath(this.mathMemo2, value2), op);
            } catch (Exception ex) {
               return this.compareValues(value1, value2, op);
            }
         }
      } else {
         return false;
      }
   }

   private <T extends Comparable<T>> boolean compareValues(T compare1, T compare2, CompareValuesCondition.Operator op) {
      int comparison = compare1.compareTo(compare2);

      return switch (op) {
         case EQUALS -> comparison == 0;
         case NOT_EQUALS -> comparison != 0;
         case GREATER_THAN -> comparison > 0;
         case LESS_THAN -> comparison < 0;
         case GREATER_THAN_OR_EQUALS -> comparison >= 0;
         case LESS_THAN_OR_EQUALS -> comparison <= 0;
      };
   }

   @Override
   public boolean check(AbstractEntity e) {
      String value1 = this.value1.get(e);
      String value2 = this.value2.get(e);
      String operator = this.operator.get(e);
      return this.compareLogic(value1, value2, operator);
   }

   @Override
   public boolean check(AbstractLocation l) {
      String value1 = this.value1.get();
      String value2 = this.value2.get();
      String operator = this.operator.get();
      return this.compareLogic(value1, value2, operator);
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity target) {
      String value1 = this.value1.get(meta, target);
      String value2 = this.value2.get(meta, target);
      String operator = this.operator.get(meta, target);
      return this.compareLogic(value1, value2, operator);
   }

   private static enum Operator {
      EQUALS("==", "EQUALS", "EQUAL", "EQ", "EQL"),
      NOT_EQUALS("!=", "NOT_EQUALS", "NOTEQUALS", "NOTEQUAL", "NE", "NEQ", "NEQL"),
      GREATER_THAN(">", "GT", "GREATERTHAN", "GTR", "GREATER_THAN"),
      LESS_THAN("<", "LT", "LESSTHAN", "LTR", "LESS_THAN"),
      GREATER_THAN_OR_EQUALS(">=", "GTE", "GREATERTHANOREQUALS", "GTEO", "GREATER_THAN_OR_EQUALS"),
      LESS_THAN_OR_EQUALS("<=", "LTE", "LESSTHANOREQUALS", "LTEO", "LESS_THAN_OR_EQUALS");

      private Operator(String... aliases) {
         for (String alias : aliases) {
            CompareValuesCondition.OPERATOR_MAP.put(alias, this);
         }
      }

      public static CompareValuesCondition.Operator getOperator(String op) {
         return CompareValuesCondition.OPERATOR_MAP.getOrDefault(op.toUpperCase(), EQUALS);
      }
   }
}
