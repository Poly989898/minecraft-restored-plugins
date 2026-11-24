package io.lumine.mythic.lib.script.variable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public class SimpleVariableRegistry<D> implements VariableRegistry<Variable<D>> {
   private final Map<String, Function<D, Variable<?>>> registered = new HashMap<>();

   @NotNull
   public Variable<?> accessVariable(@NotNull Variable<D> d, @NotNull String name) {
      Function<D, ? extends Variable<?>> supplier = this.registered.get(name);
      Validate.notNull(supplier, "Cannot find subvariable '" + name + "' in variable type '" + d.getClass().getAnnotation(VariableMetadata.class).name() + "'");
      return (Variable<?>)supplier.apply(d.getStored());
   }

   @Override
   public boolean hasVariable(String name) {
      return this.registered.containsKey(name);
   }

   public <E extends D> void transferTo(@NotNull SimpleVariableRegistry<E> registry) {
      this.registered.forEach((key, getter) -> registry.registered.put(key, var -> (Variable<?>)getter.apply(var)));
   }

   public void registerVariable(@NotNull String name, @NotNull Function<D, Variable<?>> supplier, String... aliases) {
      Validate.isTrue(!this.registered.containsKey(name), "A subvariable with the name '" + name + "' already exists");
      Validate.notNull(supplier, "Supplier cannot be null");
      this.registered.put(name, supplier);

      for (String alias : aliases) {
         this.registerVariable(alias, supplier);
      }
   }
}
