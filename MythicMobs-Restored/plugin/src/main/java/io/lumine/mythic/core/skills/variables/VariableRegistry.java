package io.lumine.mythic.core.skills.variables;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.core.skills.variables.types.AbstractVariable;
import io.lumine.mythic.core.skills.variables.types.DoubleVariable;
import io.lumine.mythic.core.skills.variables.types.FloatVariable;
import io.lumine.mythic.core.skills.variables.types.IntegerVariable;
import io.lumine.mythic.core.skills.variables.types.LocationVariable;
import io.lumine.mythic.core.skills.variables.types.StringVariable;
import io.lumine.mythic.core.skills.variables.types.VectorVariable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VariableRegistry implements Cloneable {
   protected ConcurrentHashMap<String, Variable> entries = new ConcurrentHashMap<>();
   private transient DeferredVariableRegistry.WriteLog deferredWrites;

   public VariableRegistry() {
   }

   public VariableRegistry(Map<String, Variable> value) {
      Preconditions.checkNotNull(value);
      this.entries = new ConcurrentHashMap<>(value);
      this.clean();
   }

   public void put(String key, Variable value) {
      Preconditions.checkNotNull(key);
      Preconditions.checkNotNull(value);
      this.entries.put(key, value);
   }

   public void putAll(Map<String, ? extends Variable> value) {
      Preconditions.checkNotNull(value);

      for (Entry<String, ? extends Variable> entry : value.entrySet()) {
         this.put(entry.getKey(), entry.getValue());
      }
   }

   public void remove(String key) {
      Preconditions.checkNotNull(key);
      this.entries.remove(key);
   }

   public boolean has(String key) {
      Preconditions.checkNotNull(key);
      Variable var = this.entries.get(key);
      if (var == null) {
         return false;
      } else if (var.shouldExpire()) {
         this.entries.remove(key, var);
         return false;
      } else {
         return true;
      }
   }

   public Variable get(String key) {
      Preconditions.checkNotNull(key);
      Variable var = this.entries.get(key);
      if (var == null) {
         return null;
      } else if (var.shouldExpire()) {
         this.entries.remove(key, var);
         return null;
      } else {
         return var;
      }
   }

   public Variable getOrDefault(String key, Variable def) {
      Preconditions.checkNotNull(key);
      Variable var = this.entries.get(key);
      if (var == null) {
         return def;
      } else if (var.shouldExpire()) {
         this.entries.remove(key, var);
         return def;
      } else {
         return var;
      }
   }

   public void putInt(String key, int value) {
      this.put(key, new IntegerVariable(value));
   }

   public int getInt(String key) {
      int def = 0;
      int pipe = key.indexOf(124);
      if (pipe >= 0) {
         int pipe2 = key.indexOf(124, pipe + 1);
         String defStr = pipe2 < 0 ? key.substring(pipe + 1) : key.substring(pipe + 1, pipe2);
         key = key.substring(0, pipe);

         try {
            def = Integer.parseInt(defStr);
         } catch (IllegalArgumentException e) {
         }
      }

      Variable var = this.get(key);
      if (var == null) {
         return def;
      } else if (var instanceof IntegerVariable integerVariable) {
         return integerVariable.getValue();
      } else if (var instanceof FloatVariable floatVariable) {
         return (int)floatVariable.getValue();
      } else if (var instanceof DoubleVariable doubleVariable) {
         return (int)doubleVariable.getValue();
      } else {
         if (var instanceof StringVariable stringVariable) {
            try {
               String value = stringVariable.getValue();
               return Integer.parseInt(value);
            } catch (Exception ex) {
            }
         }

         return def;
      }
   }

   public void putString(String key, String value) {
      this.put(key, new StringVariable(value));
   }

   public String getString(String key) {
      String def = "UNDEFINED";
      int pipe = key.indexOf(124);
      if (pipe >= 0) {
         int pipe2 = key.indexOf(124, pipe + 1);
         def = pipe2 < 0 ? key.substring(pipe + 1) : key.substring(pipe + 1, pipe2);
         key = key.substring(0, pipe);
      }

      Variable var = this.get(key);
      return var == null ? def : var.toString();
   }

   @Nullable
   public String getString(@NotNull VariableIdentifier id) {
      Variable var = this.get(id.name());
      return var != null ? var.toString() : id.defaultValue();
   }

   public void putFloat(String key, float value) {
      this.put(key, new FloatVariable(value));
   }

   public float getFloat(String key) {
      float def = 0.0F;
      int pipe = key.indexOf(124);
      if (pipe >= 0) {
         int pipe2 = key.indexOf(124, pipe + 1);
         String defStr = pipe2 < 0 ? key.substring(pipe + 1) : key.substring(pipe + 1, pipe2);
         key = key.substring(0, pipe);

         try {
            def = Float.parseFloat(defStr);
         } catch (IllegalArgumentException e) {
         }
      }

      Variable var = this.get(key);
      if (var == null) {
         return def;
      } else if (var instanceof IntegerVariable integerVariable) {
         return integerVariable.getValue();
      } else if (var instanceof FloatVariable floatVariable) {
         return floatVariable.getValue();
      } else if (var instanceof DoubleVariable doubleVariable) {
         return (float)doubleVariable.getValue();
      } else {
         if (var instanceof StringVariable stringVariable) {
            try {
               String value = stringVariable.getValue();
               return Float.parseFloat(value);
            } catch (Exception ex) {
            }
         }

         return def;
      }
   }

   public void putDouble(String key, double value) {
      this.put(key, new DoubleVariable(value));
   }

   public double getDouble(String key) {
      double def = 0.0;
      int pipe = key.indexOf(124);
      if (pipe >= 0) {
         int pipe2 = key.indexOf(124, pipe + 1);
         String defStr = pipe2 < 0 ? key.substring(pipe + 1) : key.substring(pipe + 1, pipe2);
         key = key.substring(0, pipe);

         try {
            def = Double.parseDouble(defStr);
         } catch (IllegalArgumentException e) {
         }
      }

      Variable var = this.get(key);
      if (var == null) {
         return def;
      } else if (var instanceof IntegerVariable integerVariable) {
         return integerVariable.getValue();
      } else if (var instanceof FloatVariable floatVariable) {
         return floatVariable.getValue();
      } else if (var instanceof DoubleVariable doubleVariable) {
         return doubleVariable.getValue();
      } else {
         if (var instanceof StringVariable stringVariable) {
            try {
               String value = stringVariable.getValue();
               return Double.parseDouble(value);
            } catch (Exception ex) {
            }
         }

         return def;
      }
   }

   public void putLocation(String key, AbstractLocation value) {
      this.put(key, new LocationVariable(value));
   }

   public AbstractLocation getLocation(String key) {
      Variable var = this.get(key);
      if (var == null) {
         return null;
      } else {
         return var instanceof LocationVariable locationVariable ? locationVariable.getValue() : null;
      }
   }

   public void putVector(String key, AbstractVector value) {
      this.put(key, new VectorVariable(value));
   }

   public void putObject(String key, Object value) {
      this.put(key, new AbstractVariable(value));
   }

   public ImmutableMap<String, Variable> asMap() {
      return ImmutableMap.copyOf(this.entries);
   }

   public void clean() {
      this.entries.values().removeIf(o -> o.shouldExpire());
   }

   public void unload() {
      this.entries.values().removeIf(o -> !o.shouldSave());
   }

   synchronized DeferredVariableRegistry.WriteLog deferredWrites() {
      if (this.deferredWrites == null) {
         this.deferredWrites = new DeferredVariableRegistry.WriteLog();
      }

      return this.deferredWrites;
   }

   public VariableRegistry clone() throws CloneNotSupportedException {
      VariableRegistry clone = (VariableRegistry)super.clone();
      clone.entries = new ConcurrentHashMap<>();
      clone.deferredWrites = null;

      for (Entry<String, Variable> entry : this.entries.entrySet()) {
         Variable newVar = entry.getValue().clone();
         clone.entries.put(entry.getKey(), newVar);
      }

      return clone;
   }
}
