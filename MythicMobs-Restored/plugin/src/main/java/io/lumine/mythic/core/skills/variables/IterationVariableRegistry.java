package io.lumine.mythic.core.skills.variables;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import io.lumine.mythic.core.skills.variables.types.IntegerVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class IterationVariableRegistry extends VariableRegistry {
   private static final Set<String> ITERATION_KEYS = Set.of("interval", "itr");
   private final VariableRegistry parent;
   private final Set<String> keys;
   private final ConcurrentHashMap<String, Variable> pinned;

   private IterationVariableRegistry(VariableRegistry parent, Set<String> keys, Map<String, Variable> values) {
      this.parent = parent;
      this.entries = parent.entries;
      this.keys = keys;
      this.pinned = new ConcurrentHashMap<>(values);
   }

   public static IterationVariableRegistry ofIteration(VariableRegistry enclosing, int iteration) {
      return of(enclosing, ITERATION_KEYS, Map.of("interval", new IntegerVariable(iteration), "itr", new IntegerVariable(iteration)));
   }

   public static IterationVariableRegistry of(VariableRegistry enclosing, String key, Variable value) {
      return of(enclosing, Set.of(key), Map.of(key, value));
   }

   private static IterationVariableRegistry of(VariableRegistry enclosing, Set<String> keys, Map<String, Variable> values) {
      for (Entry<String, Variable> entry : values.entrySet()) {
         enclosing.entries.put(entry.getKey(), entry.getValue().clone());
      }

      return new IterationVariableRegistry(enclosing, keys, values);
   }

   @Override
   DeferredVariableRegistry.WriteLog deferredWrites() {
      return this.parent.deferredWrites();
   }

   @Override
   public void put(String key, Variable value) {
      if (!this.keys.contains(key)) {
         this.parent.put(key, value);
      } else {
         Preconditions.checkNotNull(value);
         this.pinned.put(key, value);
      }
   }

   @Override
   public void remove(String key) {
      if (this.keys.contains(key)) {
         this.pinned.remove(key);
      } else {
         this.parent.remove(key);
      }
   }

   @Override
   public boolean has(String key) {
      return this.keys.contains(key) ? this.livePinned(key) != null : this.parent.has(key);
   }

   @Override
   public Variable get(String key) {
      return this.keys.contains(key) ? this.livePinned(key) : this.parent.get(key);
   }

   @Override
   public Variable getOrDefault(String key, Variable def) {
      if (!this.keys.contains(key)) {
         return this.parent.getOrDefault(key, def);
      } else {
         Variable var = this.livePinned(key);
         return var == null ? def : var;
      }
   }

   @Override
   public ImmutableMap<String, Variable> asMap() {
      HashMap<String, Variable> merged = new HashMap<>(this.parent.asMap());
      merged.keySet().removeAll(this.keys);
      merged.putAll(this.pinned);
      return ImmutableMap.copyOf(merged);
   }

   @Override
   public VariableRegistry clone() throws CloneNotSupportedException {
      VariableRegistry clone = new VariableRegistry();
      UnmodifiableIterator unmodifiableIterator = this.asMap().entrySet().iterator();

      while (unmodifiableIterator.hasNext()) {
         Entry<String, Variable> entry = (Entry<String, Variable>)unmodifiableIterator.next();
         clone.entries.put(entry.getKey(), entry.getValue().clone());
      }

      return clone;
   }

   private Variable livePinned(String key) {
      Variable var = this.pinned.get(key);
      if (var != null && var.shouldExpire()) {
         this.pinned.remove(key, var);
         return null;
      } else {
         return var;
      }
   }
}
