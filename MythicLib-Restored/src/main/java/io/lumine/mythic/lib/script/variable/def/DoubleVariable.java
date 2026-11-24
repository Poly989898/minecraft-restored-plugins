package io.lumine.mythic.lib.script.variable.def;

import io.lumine.mythic.lib.script.variable.SimpleVariableRegistry;
import io.lumine.mythic.lib.script.variable.Variable;
import io.lumine.mythic.lib.script.variable.VariableMetadata;
import io.lumine.mythic.lib.script.variable.VariableRegistry;

@VariableMetadata(name = "double")
public class DoubleVariable extends Variable<Double> {
   public static final SimpleVariableRegistry<Double> VARIABLE_REGISTRY = new SimpleVariableRegistry<>();

   public DoubleVariable(String name, double value) {
      super(name, value);
   }

   @Override
   public VariableRegistry<Variable<Double>> getVariableRegistry() {
      return VARIABLE_REGISTRY;
   }

   static {
      VARIABLE_REGISTRY.registerVariable("int", var -> new IntegerVariable("temp", (int)var.doubleValue()));
   }
}
