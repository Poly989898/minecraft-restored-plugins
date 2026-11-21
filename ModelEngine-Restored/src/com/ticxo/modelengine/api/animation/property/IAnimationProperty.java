package com.ticxo.modelengine.api.animation.property;

import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeType;
import com.ticxo.modelengine.api.animation.keyframe.type.ScriptKeyframe;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.math.Easing;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public interface IAnimationProperty extends DataIO {
   BlueprintAnimation getBlueprintAnimation();

   boolean update();

   void stop();

   boolean isEnded();

   boolean canReplace();

   String getName();

   boolean containsKeyframe(KeyframeType<?, ?> type, UUID bone);

   default Vector3f getPositionFrame(ModelBone bone) {
      return this.getPositionFrame(bone, this.getBlueprintBone(bone));
   }

   Vector3f getPositionFrame(ModelBone bone, BlueprintBone blueprintBone);

   default Vector3f getVelocityFrame(ModelBone bone) {
      return this.getVelocityFrame(bone, this.getBlueprintBone(bone));
   }

   Vector3f getVelocityFrame(ModelBone bone, BlueprintBone blueprintBone);

   default Vector3f getRotationFrame(ModelBone bone) {
      return this.getRotationFrame(bone, this.getBlueprintBone(bone));
   }

   Vector3f getRotationFrame(ModelBone bone, BlueprintBone blueprintBone);

   default Vector3f getScaleFrame(ModelBone bone) {
      return this.getScaleFrame(bone, this.getBlueprintBone(bone));
   }

   Vector3f getScaleFrame(ModelBone bone, BlueprintBone blueprintBone);

   List<ScriptKeyframe.Script> getScriptFrame();

   double getLerpInRatio();

   double getLerpOutRatio();

   boolean isFinished();

   ActiveModel getModel();

   double getLerpIn();

   default void setLerpIn(double lerpIn) {
   }

   double getLerpOut();

   default void setLerpOut(double lerpOut) {
   }

   @NotNull
   default Easing getLerpCurveIn() {
      return Easing.LINEAR;
   }

   default void setLerpCurveIn(@Nullable Easing lerpCurveIn) {
   }

   @NotNull
   default Easing getLerpCurveOut() {
      return Easing.LINEAR;
   }

   default void setLerpCurveOut(@Nullable Easing lerpCurveOut) {
   }

   double getLerpInTime();

   void setLerpInTime(double lerpInTime);

   double getLerpOutTime();

   void setLerpOutTime(double lerpOutTime);

   double getLastTime();

   double getTime();

   double getSpeed();

   void setSpeed(double speed);

   @NotNull
   IAnimationProperty.Phase getPhase();

   BlueprintAnimation.LoopMode getForceLoopMode();

   void setForceLoopMode(BlueprintAnimation.LoopMode forceLoopMode);

   BlueprintAnimation.LoopMode getLoopMode();

   boolean isOverride();

   BlueprintAnimation.OverrideMode getForceOverride();

   void setForceOverride(BlueprintAnimation.OverrideMode forceOverride);

   boolean isEmptyZero();

   void setEmptyZero(boolean emptyZero);

   boolean isSkipLastFrame();

   void setSkipLastFrame(boolean skipLastFrame);

   boolean isMergeLerp();

   void setMergeLerp(boolean mergeLerp);

   boolean isIgnoreDeath();

   void setIgnoreDeath(boolean ignoreDeath);

   default BlueprintBone getBlueprintBone(ModelBone bone) {
      BlueprintBone bBone = bone.getBlueprintBone();
      BlueprintBone bBone2 = this.getBlueprintAnimation().getModelBlueprint().getFlatMap().get(bBone.getName());
      return bBone2 == null ? bBone : bBone2;
   }

   enum Phase {
      LERPIN,
      PLAY,
      LERPOUT;
   }
}
