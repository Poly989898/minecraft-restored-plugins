package com.ticxo.modelengine.api.interaction;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Getter
@Setter
public class DynamicHitbox {
   @Getter
   private static final int pivotId = ModelEngineAPI.getEntityHandler().getNextEntityId();
   @Getter
   private static final UUID pivotUUID = UUID.randomUUID();
   @Getter
   private static final int hitboxId = ModelEngineAPI.getEntityHandler().getNextEntityId();
   @Getter
   private static final UUID hitboxUUID = UUID.randomUUID();
   private final Player player;
   private final DataTracker<Vector> positionTracker = new DataTracker<>();
   private int target;

   public DynamicHitbox(Player player, Vector position) {
      this.player = player;
      this.positionTracker.set(position);
      ModelEngineAPI.getEntityHandler().spawnDynamicHitbox(this);
      this.positionTracker.clearDirty();
   }

   public void update(Vector position) {
      this.positionTracker.set(position.setY(position.getY()));
      if (this.positionTracker.isDirty()) {
         ModelEngineAPI.getEntityHandler().updateDynamicHitbox(this);
         this.positionTracker.clearDirty();
      }
   }

   public void destroy() {
      ModelEngineAPI.getEntityHandler().destroyDynamicHitbox(this);
   }
}
