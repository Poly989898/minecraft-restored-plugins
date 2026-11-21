package com.ticxo.modelengine.api.animation.property;

import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeType;
import com.ticxo.modelengine.api.animation.keyframe.type.ScriptKeyframe;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.math.Easing;
import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class SubTickProperty implements IAnimationProperty {
   private final IAnimationProperty base;
   private final double alpha;
   private final double time;
   private final double lastTime;

   @Nullable
   public static SubTickProperty at(@Nullable IAnimationProperty base, double alpha) {
      return base == null ? null : new SubTickProperty(base, alpha);
   }

   public SubTickProperty(IAnimationProperty base, double alpha) {
      this.base = base;
      this.alpha = alpha;
      double curr = base.getTime();
      double prev = base.getLastTime();
      double delta = base.getSpeed() * 0.05;
      if (prev < 0.0 || curr < 0.0) {
         this.time = Math.max(curr, 0.0);
      } else if (curr >= prev) {
         this.time = prev + alpha * (curr - prev);
      } else if (delta > 0.0) {
         double modulus = prev + delta - curr;
         double shifted = prev + alpha * delta;
         this.time = shifted >= modulus ? shifted - modulus : shifted;
      } else {
         this.time = prev + alpha * (curr - prev);
      }

      this.lastTime = Math.max(prev, 0.0);
   }

   public IAnimationProperty getBase() {
      return this.base;
   }

   public double getAlpha() {
      return this.alpha;
   }

   @Override
   public double getTime() {
      return this.time;
   }

   @Override
   public double getLastTime() {
      return this.lastTime;
   }

   @Override
   public double getLerpInRatio() {
      double lerpIn = this.base.getLerpIn();
      if (lerpIn <= 0.0) {
         return this.base.getLerpInRatio();
      }

      double ratio = TMath.clamp((this.base.getLerpInTime() - (1.0 - this.alpha) * this.base.getSpeed() * 0.05) / lerpIn, 0.0, 1.0);
      Easing curve = this.base.getLerpCurveIn();
      return curve == Easing.LINEAR ? ratio : curve.apply(ratio);
   }

   @Override
   public double getLerpOutRatio() {
      double lerpOut = this.base.getLerpOut();
      if (lerpOut <= 0.0) {
         return this.base.getLerpOutRatio();
      }

      double ratio = TMath.clamp((this.base.getLerpOutTime() - (1.0 - this.alpha) * this.base.getSpeed() * 0.05) / lerpOut, 0.0, 1.0);
      Easing curve = this.base.getLerpCurveOut();
      return curve == Easing.LINEAR ? ratio : curve.apply(ratio);
   }

   @Override
   public Vector3f getPositionFrame(ModelBone bone, BlueprintBone blueprintBone) {
      return this.base.getBlueprintAnimation().getPosition(bone, blueprintBone, this);
   }

   @Override
   public Vector3f getVelocityFrame(ModelBone bone, BlueprintBone blueprintBone) {
      return this.base.getBlueprintAnimation().getVelocity(bone, blueprintBone, this);
   }

   @Override
   public Vector3f getRotationFrame(ModelBone bone, BlueprintBone blueprintBone) {
      return this.base.getBlueprintAnimation().getRotation(bone, blueprintBone, this);
   }

   @Override
   public Vector3f getScaleFrame(ModelBone bone, BlueprintBone blueprintBone) {
      return this.base.getBlueprintAnimation().getScale(bone, blueprintBone, this);
   }

   @Override
   public List<ScriptKeyframe.Script> getScriptFrame() {
      return this.base.getBlueprintAnimation().getScript(this);
   }

   @Override
   public BlueprintAnimation getBlueprintAnimation() {
      return this.base.getBlueprintAnimation();
   }

   @Override
   public boolean update() {
      return this.base.update();
   }

   @Override
   public void stop() {
      this.base.stop();
   }

   @Override
   public boolean isEnded() {
      return this.base.isEnded();
   }

   @Override
   public boolean canReplace() {
      return this.base.canReplace();
   }

   @Override
   public String getName() {
      return this.base.getName();
   }

   @Override
   public boolean containsKeyframe(KeyframeType<?, ?> type, UUID bone) {
      return this.base.containsKeyframe(type, bone);
   }

   @Override
   public boolean isFinished() {
      return this.base.isFinished();
   }

   @Override
   public ActiveModel getModel() {
      return this.base.getModel();
   }

   @Override
   public double getLerpIn() {
      return this.base.getLerpIn();
   }

   @Override
   public void setLerpIn(double lerpIn) {
      this.base.setLerpIn(lerpIn);
   }

   @Override
   public double getLerpOut() {
      return this.base.getLerpOut();
   }

   @Override
   public void setLerpOut(double lerpOut) {
      this.base.setLerpOut(lerpOut);
   }

   @NotNull
   @Override
   public Easing getLerpCurveIn() {
      return this.base.getLerpCurveIn();
   }

   @Override
   public void setLerpCurveIn(@Nullable Easing lerpCurveIn) {
      this.base.setLerpCurveIn(lerpCurveIn);
   }

   @NotNull
   @Override
   public Easing getLerpCurveOut() {
      return this.base.getLerpCurveOut();
   }

   @Override
   public void setLerpCurveOut(@Nullable Easing lerpCurveOut) {
      this.base.setLerpCurveOut(lerpCurveOut);
   }

   @Override
   public double getLerpInTime() {
      return this.base.getLerpInTime();
   }

   @Override
   public void setLerpInTime(double lerpInTime) {
      this.base.setLerpInTime(lerpInTime);
   }

   @Override
   public double getLerpOutTime() {
      return this.base.getLerpOutTime();
   }

   @Override
   public void setLerpOutTime(double lerpOutTime) {
      this.base.setLerpOutTime(lerpOutTime);
   }

   @Override
   public double getSpeed() {
      return this.base.getSpeed();
   }

   @Override
   public void setSpeed(double speed) {
      this.base.setSpeed(speed);
   }

   @NotNull
   @Override
   public IAnimationProperty.Phase getPhase() {
      return this.base.getPhase();
   }

   @Override
   public BlueprintAnimation.LoopMode getForceLoopMode() {
      return this.base.getForceLoopMode();
   }

   @Override
   public void setForceLoopMode(BlueprintAnimation.LoopMode forceLoopMode) {
      this.base.setForceLoopMode(forceLoopMode);
   }

   @Override
   public BlueprintAnimation.LoopMode getLoopMode() {
      return this.base.getLoopMode();
   }

   @Override
   public boolean isOverride() {
      return this.base.isOverride();
   }

   @Override
   public BlueprintAnimation.OverrideMode getForceOverride() {
      return this.base.getForceOverride();
   }

   @Override
   public void setForceOverride(BlueprintAnimation.OverrideMode mode) {
      this.base.setForceOverride(mode);
   }

   @Override
   public boolean isEmptyZero() {
      return this.base.isEmptyZero();
   }

   @Override
   public void setEmptyZero(boolean flag) {
      this.base.setEmptyZero(flag);
   }

   @Override
   public boolean isSkipLastFrame() {
      return this.base.isSkipLastFrame();
   }

   @Override
   public void setSkipLastFrame(boolean skipLastFrame) {
      this.base.setSkipLastFrame(skipLastFrame);
   }

   @Override
   public boolean isMergeLerp() {
      return this.base.isMergeLerp();
   }

   @Override
   public void setMergeLerp(boolean mergeLerp) {
      this.base.setMergeLerp(mergeLerp);
   }

   @Override
   public boolean isIgnoreDeath() {
      return this.base.isIgnoreDeath();
   }

   @Override
   public void setIgnoreDeath(boolean ignoreDeath) {
      this.base.setIgnoreDeath(ignoreDeath);
   }

   @Override
   public void save(SavedData data) {
      this.base.save(data);
   }

   @Override
   public void load(SavedData data) {
      this.base.load(data);
   }
}
