package com.ticxo.modelengine.api.mount.controller;

import com.ticxo.modelengine.api.model.bone.type.Mount;
import java.util.function.BiFunction;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Entity;

@AllArgsConstructor
public class MountControllerType implements MountControllerSupplier {
   private final BiFunction<Entity, Mount, MountController> controllerConstructor;

   @Override
   public MountController createController(Entity entity, Mount mount) {
      return this.controllerConstructor.apply(entity, mount);
   }
}
