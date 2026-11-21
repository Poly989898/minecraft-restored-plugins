package com.ticxo.modelengine.api.model.bone.render.renderer;

import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.UUID;
import org.joml.Vector3f;

public interface MountRenderer extends BehaviorRenderer, RenderQueues<MountRenderer.Mount> {
   interface Mount {
      int getPivotId();

      UUID getPivotUuid();

      int getMountId();

      UUID getMountUuid();

      DataTracker<Vector3f> getPosition();

      DataTracker<Byte> getYaw();

      DataTracker<Float> getMaxHealth();

      DataTracker<Float> getHealth();

      CollectionDataTracker<Integer> getPassengers();
   }
}
