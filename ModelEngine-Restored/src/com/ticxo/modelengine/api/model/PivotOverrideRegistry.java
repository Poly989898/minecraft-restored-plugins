package com.ticxo.modelengine.api.model;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.entity.Entity;

public class PivotOverrideRegistry {
   private final Map<UUID, PivotOverride> overrides = Maps.newConcurrentMap();
   private final Map<Integer, UUID> entityIdLookup = Maps.newConcurrentMap();

   public Optional<PivotOverride> get(UUID uuid) {
      return Optional.ofNullable(uuid == null ? null : this.overrides.get(uuid));
   }

   public Optional<PivotOverride> get(int id) {
      return Optional.ofNullable(this.entityIdLookup.get(id)).map(this.overrides::get);
   }

   public void register(PivotOverride override) {
      this.overrides.put(override.getUuid(), override);
      this.entityIdLookup.put(override.getId(), override.getUuid());
   }

   public void remove(UUID uuid) {
      PivotOverride removed = this.overrides.remove(uuid);
      if (removed != null) {
         this.entityIdLookup.remove(removed.getId());
      }
   }

   public PivotOverride getOrCreate(Entity entity) {
      return this.overrides.computeIfAbsent(entity.getUniqueId(), uuid -> {
         this.entityIdLookup.put(entity.getEntityId(), uuid);
         return PivotOverride.create(entity);
      });
   }

   public Set<PivotOverride> collectDirty() {
      return this.overrides.values().stream().filter(override -> override.getPassengers().isDirty()).collect(Collectors.toSet());
   }

   public Collection<PivotOverride> getAll() {
      return this.overrides.values();
   }
}
