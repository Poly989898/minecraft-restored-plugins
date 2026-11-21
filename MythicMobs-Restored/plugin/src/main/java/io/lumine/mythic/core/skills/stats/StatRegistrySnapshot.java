package io.lumine.mythic.core.skills.stats;

import io.lumine.mythic.api.skills.stats.StatSnapshot;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;

public class StatRegistrySnapshot implements StatSnapshot {
   @Getter
   private final StatRegistry baseRegistry;
   private final Map<StatType, Double> statsSnapshot;

   public StatRegistrySnapshot(StatRegistry registry) {
      this.baseRegistry = registry;
      Map<StatType, Double> snapshot = new HashMap<>(registry.getStatMaps().size());

      for (Entry<StatType, StatRegistry.StatMap> entry : registry.getStatMaps().entrySet()) {
         snapshot.put(entry.getKey(), entry.getValue().get());
      }

      this.statsSnapshot = snapshot;
   }

   @Override
   public double get(StatType type) {
      return this.statsSnapshot.getOrDefault(type, 0.0);
   }
}
