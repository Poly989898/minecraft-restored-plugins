package com.ticxo.modelengine.api.events;

import com.ticxo.modelengine.api.model.bone.ModelBone;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Getter
@AllArgsConstructor
public class BoneTransformReadEvent extends AbstractEvent {
   private static final HandlerList handlers = new HandlerList();
   private final ModelBone modelBone;
   private final Vector3f position;
   private final Quaternionf leftRotation;
   private final Vector3f scale;
   private final Quaternionf rightRotation;

   @NotNull
   public static HandlerList getHandlerList() {
      return handlers;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }
}
