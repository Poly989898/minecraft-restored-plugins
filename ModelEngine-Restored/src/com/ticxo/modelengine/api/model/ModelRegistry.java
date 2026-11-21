package com.ticxo.modelengine.api.model;

import com.google.common.collect.Ordering;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.utils.registry.TUnaryRegistry;
import java.util.Collection;
import java.util.PriorityQueue;

/** Registry of loaded model blueprints, keyed by blueprint name. */
public class ModelRegistry extends TUnaryRegistry<ModelBlueprint> {
   private final PriorityQueue<String> orderedId = new PriorityQueue<>(Ordering.natural());

   /** Registers the blueprint under its name and remembers the name in {@link #getOrderedId()}. */
   public void registerBlueprint(ModelBlueprint blueprint) {
      this.orderedId.add(blueprint.getName());
      this.register(blueprint.getName(), blueprint);
   }

   /** Removes all blueprints and forgets all ids. */
   public void clearRegistry() {
      this.orderedId.clear();
      this.registry.clear();
   }

   /**
    * Returns the registered blueprint names.
    *
    * This is a {@link java.util.PriorityQueue} with natural string ordering: {@code poll()} yields names in sorted order,
    * but iterating it does not guarantee sorted order (original behavior, kept as is).
    */
   public Collection<String> getOrderedId() {
      return this.orderedId;
   }
}
