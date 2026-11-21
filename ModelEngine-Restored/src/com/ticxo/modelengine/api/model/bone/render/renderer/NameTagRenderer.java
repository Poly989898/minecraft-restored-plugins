package com.ticxo.modelengine.api.model.bone.render.renderer;

import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.utils.data.PositionCodec;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import java.util.UUID;
import org.bukkit.entity.Display.Billboard;
import org.joml.Vector3f;

public interface NameTagRenderer extends BehaviorRenderer, RenderQueues<NameTagRenderer.NameTag> {
   interface NameTag {
      int DEFAULT_BACKGROUND_COLOR = 1073741824;

      int getPivotId();

      UUID getPivotUuid();

      int getTagId();

      UUID getTagUuid();

      DataTracker<Vector3f> getPosition();

      DataTracker<String> getJsonString();

      DataTracker<Boolean> getVisibility();

      DataTracker<Integer> getBackgroundColor();

      DataTracker<Billboard> getBillboard();

      DataTracker<Byte> getTextOpacity();

      DataTracker<Integer> getLineWidth();

      DataTracker<Vector3f> getTranslate();

      DataTracker<Vector3f> getScale();

      DataTracker<Byte> getStyle();

      DataTracker<Integer> getBrightness();

      default PositionCodec getPositionCodec() {
         return null;
      }

      default boolean isDirty() {
         return this.getJsonString().isDirty()
            || this.getVisibility().isDirty()
            || this.getBillboard().isDirty()
            || this.getBackgroundColor().isDirty()
            || this.getTextOpacity().isDirty()
            || this.getLineWidth().isDirty()
            || this.getStyle().isDirty()
            || this.getTranslate().isDirty()
            || this.getScale().isDirty()
            || this.getBrightness().isDirty();
      }

      default void clearDirty() {
         this.getJsonString().clearDirty();
         this.getVisibility().clearDirty();
         this.getBillboard().clearDirty();
         this.getBackgroundColor().clearDirty();
         this.getTextOpacity().clearDirty();
         this.getLineWidth().clearDirty();
         this.getTranslate().clearDirty();
         this.getScale().clearDirty();
         this.getStyle().clearDirty();
         this.getBrightness().clearDirty();
      }
   }
}
