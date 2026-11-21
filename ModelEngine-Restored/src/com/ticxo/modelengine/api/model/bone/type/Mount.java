package com.ticxo.modelengine.api.model.bone.type;

import java.util.Collection;
import java.util.Set;
import org.bukkit.entity.Entity;
import org.joml.Vector3f;

public interface Mount {
   boolean isDriver();

   Vector3f getLocation();

   Vector3f getGlobalLocation();

   boolean addPassenger(Entity entity);

   boolean addPassengers(Collection<Entity> entities);

   void removePassenger(Entity entity);

   void removePassengers(Collection<Entity> entities);

   Set<Entity> clearPassengers();

   Set<Entity> getPassengers();

   boolean canMountMore();
}
