package io.lumine.mythic.core.skills.variables;

import io.lumine.mythic.core.skills.variables.types.AbstractVariable;
import io.lumine.mythic.core.skills.variables.types.BooleanVariable;
import io.lumine.mythic.core.skills.variables.types.DoubleVariable;
import io.lumine.mythic.core.skills.variables.types.FloatVariable;
import io.lumine.mythic.core.skills.variables.types.IntegerVariable;
import io.lumine.mythic.core.skills.variables.types.ItemVariable;
import io.lumine.mythic.core.skills.variables.types.ListVariable;
import io.lumine.mythic.core.skills.variables.types.LocationVariable;
import io.lumine.mythic.core.skills.variables.types.LongVariable;
import io.lumine.mythic.core.skills.variables.types.MapVariable;
import io.lumine.mythic.core.skills.variables.types.MetaSkillVariable;
import io.lumine.mythic.core.skills.variables.types.SetVariable;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import io.lumine.mythic.core.skills.variables.types.TimeVariable;
import io.lumine.mythic.core.skills.variables.types.VectorVariable;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VariableInfoRegistry {
   public static final Map<String, VariableInfo<?>> typeNameInfoRegistry = new ConcurrentHashMap<>();
   public static final Map<VariableType, VariableInfo<?>> typeInfoRegistry = new EnumMap<>(VariableType.class);
   public static final Map<Class<? extends Variable>, VariableInfo<?>> classInfoRegistry = new ConcurrentHashMap<>();
   public static final Map<Class<?>, VariableInfo<?>> valueClassInfoRegistry = new ConcurrentHashMap<>();
   public static final Map<String, VariableInfo<?>> defaultVariablesPrefixes = new ConcurrentHashMap<>();

   public static void register(VariableInfo<?> variableInfo) {
      typeNameInfoRegistry.put(variableInfo.typeName(), variableInfo);
      typeInfoRegistry.put(variableInfo.type(), variableInfo);
      classInfoRegistry.put(variableInfo.clazz(), variableInfo);
      valueClassInfoRegistry.put(variableInfo.valueClass(), variableInfo);
      defaultVariablesPrefixes.put(variableInfo.typeName().toLowerCase() + "/", variableInfo);
   }

   public static VariableInfo<?> get(String typeName) {
      return typeNameInfoRegistry.get(typeName);
   }

   public static VariableInfo<?> get(VariableType variableType) {
      return typeInfoRegistry.get(variableType);
   }

   public static VariableInfo<?> get(Class<? extends Variable> variableClass) {
      return classInfoRegistry.get(variableClass);
   }

   public static VariableInfo<?> getByValueClass(Class<?> variableClass) {
      return valueClassInfoRegistry.get(variableClass);
   }

   static {
      register(AbstractVariable.variableInfo);
      register(BooleanVariable.variableInfo);
      register(DoubleVariable.variableInfo);
      register(FloatVariable.variableInfo);
      register(IntegerVariable.variableInfo);
      register(LongVariable.variableInfo);
      register(ItemVariable.variableInfo);
      register(ListVariable.variableInfo);
      register(LocationVariable.variableInfo);
      register(MapVariable.variableInfo);
      register(MetaSkillVariable.variableInfo);
      register(SetVariable.variableInfo);
      register(StringVariable.variableInfo);
      register(TimeVariable.variableInfo);
      register(VectorVariable.variableInfo);
   }
}
