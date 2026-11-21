package com.ticxo.modelengine.api.model.bone.render.renderer;

import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.render.DisplayBone;
import com.ticxo.modelengine.api.utils.data.PositionCodec;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.UUID;
import org.joml.Vector3f;

public interface SegmentRenderer extends BehaviorRenderer, RenderQueues<SegmentRenderer.Pivot> {
   interface Pivot extends RenderQueues<SegmentRenderer.SegmentDisplayBone> {
      int getId();

      UUID getUuid();

      void clearDirty();

      DataTracker<Vector3f> getPosition();

      DataTracker<Float> getYaw();

      CollectionDataTracker<Integer> getPassengers();

      default PositionCodec getPositionCodec() {
         return null;
      }
   }

   interface SegmentDisplayBone extends DisplayBone {
      SegmentRenderer.Pivot getPivot();
   }
}
