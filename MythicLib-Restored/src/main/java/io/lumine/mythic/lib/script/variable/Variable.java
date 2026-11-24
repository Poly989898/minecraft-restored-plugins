package io.lumine.mythic.lib.script.variable;

import javax.annotation.Nullable;
import org.apache.commons.lang.Validate;

public abstract class Variable<D> implements VariableContainer {
   private final String name;
   private D stored;

   public Variable(String name, D stored) {
      this.name = name;
      this.stored = stored;
      Validate.isTrue(this.getClass().isAnnotationPresent(VariableMetadata.class), "Variable type with no VariableMetadata annotation");
   }

   public String getName() {
      return this.name;
   }

   public D getStored() {
      return this.stored;
   }

   public void setStored(D d) {
      this.stored = d;
   }

   public abstract VariableRegistry<Variable<D>> getVariableRegistry();

   @Nullable
   @Override
   public Variable<?> getVariable(String name) {
      return this.getVariableRegistry().accessVariable(this, name);
   }

   @Override
   public String toString() {
      return this.stored == null ? "None" : this.stored.toString();
   }
}
