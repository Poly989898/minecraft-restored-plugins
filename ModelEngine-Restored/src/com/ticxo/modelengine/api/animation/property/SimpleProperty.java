package com.ticxo.modelengine.api.animation.property;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.Timeline;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeType;
import com.ticxo.modelengine.api.animation.keyframe.type.ScriptKeyframe;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.math.Easing;
import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Generated;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Getter
public class SimpleProperty implements IAnimationProperty {
   private final ActiveModel model;
   private final BlueprintAnimation blueprintAnimation;
   private final String name;
   private double lerpIn;
   private double lerpOut;
   private double lerpInTime = 0.0;
   private double lerpOutTime = 0.0;
   private double lastTime = -1.0;
   private double time = -1.0;
   private double speed;
   @NotNull
   private IAnimationProperty.Phase phase = IAnimationProperty.Phase.LERPIN;
   private BlueprintAnimation.LoopMode forceLoopMode = null;
   private BlueprintAnimation.OverrideMode forceOverride = null;
   @NotNull
   private Easing lerpCurveIn = Easing.LINEAR;
   @NotNull
   private Easing lerpCurveOut = Easing.LINEAR;
   private boolean emptyZero = ModelEngineAPI.getConfigCache().isEmptyZero();
   private boolean skipLastFrame;
   private boolean mergeLerp = false;
   private boolean ignoreDeath = false;
   private boolean stopping;
   private boolean ended;
   private Runnable onEndTask = null;

   public SimpleProperty(ActiveModel model, BlueprintAnimation blueprintAnimation) {
      this(model, blueprintAnimation, 0.0, 0.0, 1.0);
   }

   public SimpleProperty(ActiveModel model, BlueprintAnimation blueprintAnimation, double lerpIn, double lerpOut, double speed) {
      if (blueprintAnimation == null) {
         throw new IllegalArgumentException("BlueprintAnimation cannot be null");
      }

      this.model = model;
      this.blueprintAnimation = blueprintAnimation;
      if (blueprintAnimation.getModelBlueprint() != model.getBlueprint()) {
         this.name = "ref[" + blueprintAnimation.getModelBlueprint().getName() + "." + blueprintAnimation.getName() + "]";
      } else {
         this.name = blueprintAnimation.getName();
      }

      this.lerpIn = lerpIn;
      this.lerpOut = lerpOut;
      this.speed = speed;
   }

   @Override
   public boolean update() {
      this.lastTime = this.time;
      boolean flag;
      if (this.mergeLerp) {
         flag = this.mergeUpdate();
      } else {
         switch (this.phase) {
            case LERPIN:
               flag = this.updateLerpIn();
               break;
            case PLAY:
               flag = this.updateTime();
               break;
            case LERPOUT:
               flag = this.updateLerpOut();
               break;
            default:
               throw new IncompatibleClassChangeError();
         }
      }

      return flag;
   }

   private boolean mergeUpdate() {
      this.time = Math.max(this.time, 0.0);
      BlueprintAnimation.LoopMode mode = this.getLoopMode();
      double delta = this.speed * 0.05;
      double length = this.blueprintAnimation.getLength();
      boolean infiniteLoop = length <= 0.0;
      if (infiniteLoop) {
         this.time += delta;
      } else {
         switch (mode) {
            case ONCE:
            case HOLD:
               this.time = Math.min(this.time + delta, length);
               break;
            case LOOP:
               this.time = (this.time + delta) % (length + (this.skipLastFrame ? 0.0 : delta));
         }
      }

      boolean reprocess = true;

      while (reprocess) {
         reprocess = false;
         switch (this.phase) {
            case LERPIN:
               this.lerpInTime += delta;
               if (this.lerpInTime >= this.lerpIn - 1.0E-5F) {
                  this.phase = this.stopping ? IAnimationProperty.Phase.LERPOUT : IAnimationProperty.Phase.PLAY;
                  reprocess = true;
               }
               break;
            case PLAY:
               if (this.stopping || !infiniteLoop && mode == BlueprintAnimation.LoopMode.ONCE && length - this.time <= this.lerpOut) {
                  this.phase = IAnimationProperty.Phase.LERPOUT;
                  reprocess = true;
               }
               break;
            case LERPOUT:
               this.lerpOutTime += delta;
               if (this.lerpOutTime >= this.lerpOut - 1.0E-5F) {
                  this.ended = true;
               }
         }
      }

      return !this.ended;
   }

   private boolean updateLerpIn() {
      if (this.lerpInTime >= this.lerpIn - 1.0E-5F) {
         this.time = 0.0;
         return this.stopping ? this.updateLerpOut() : this.updateTime();
      } else {
         this.lerpInTime = this.lerpInTime + this.speed * 0.05;
         return this.playingOrLerpOut();
      }
   }

   private boolean updateTime() {
      if (this.phase == IAnimationProperty.Phase.LERPIN) {
         this.phase = IAnimationProperty.Phase.PLAY;
         return this.playingOrLerpOut();
      }

      double delta = this.speed * 0.05;
      double length = this.blueprintAnimation.getLength();
      boolean infiniteLoop = length <= 0.0;
      if (infiniteLoop) {
         this.time += delta;
         return this.playingOrLerpOut();
      }

      BlueprintAnimation.LoopMode mode = this.getLoopMode();
      switch (mode) {
         case ONCE:
            if (this.time < length) {
               this.time = Math.min(this.time + delta, length);
               return this.playingOrLerpOut();
            }

            return this.updateLerpOut();
         case HOLD:
            this.time = Math.min(this.time + delta, length);
            return this.playingOrLerpOut();
         case LOOP:
            this.time = (this.time + delta) % (this.skipLastFrame ? length : length + delta);
            return this.playingOrLerpOut();
         default:
            return false;
      }
   }

   private boolean updateLerpOut() {
      if (this.phase != IAnimationProperty.Phase.LERPOUT && this.lerpOut > 1.0E-5F) {
         this.phase = IAnimationProperty.Phase.LERPOUT;
         return true;
      } else if (this.lerpOutTime >= this.lerpOut - 1.0E-5F) {
         this.ended = true;
         return false;
      } else {
         this.lerpOutTime = this.lerpOutTime + this.speed * 0.05;
         return true;
      }
   }

   private boolean playingOrLerpOut() {
      return !this.stopping || this.updateLerpOut();
   }

   @Override
   public void stop() {
      this.stopping = true;
   }

   @Override
   public boolean canReplace() {
      return this.stopping || this.phase == IAnimationProperty.Phase.LERPOUT || this.ended;
   }

   @Override
   public boolean containsKeyframe(KeyframeType<?, ?> type, UUID boneUuid) {
      Timeline timeline = this.blueprintAnimation.getTimelines().get(boneUuid);
      return timeline == null ? false : timeline.hasInterpolator(type) && !timeline.getInterpolator(type).isEmpty();
   }

   @Override
   public Vector3f getPositionFrame(ModelBone bone, BlueprintBone blueprintBone) {
      return this.blueprintAnimation.getPosition(bone, blueprintBone, this);
   }

   @Override
   public Vector3f getVelocityFrame(ModelBone bone, BlueprintBone blueprintBone) {
      return this.blueprintAnimation.getVelocity(bone, blueprintBone, this);
   }

   @Override
   public Vector3f getRotationFrame(ModelBone bone, BlueprintBone blueprintBone) {
      return this.blueprintAnimation.getRotation(bone, blueprintBone, this);
   }

   @Override
   public Vector3f getScaleFrame(ModelBone bone, BlueprintBone blueprintBone) {
      return this.blueprintAnimation.getScale(bone, blueprintBone, this);
   }

   @Override
   public List<ScriptKeyframe.Script> getScriptFrame() {
      return this.blueprintAnimation.getScript(this);
   }

   @Override
   public double getLerpInRatio() {
      double ratio = TMath.clamp(this.lerpInTime / this.lerpIn, 0.0, 1.0);
      return this.lerpCurveIn == Easing.LINEAR ? ratio : this.lerpCurveIn.apply(ratio);
   }

   @Override
   public double getLerpOutRatio() {
      double ratio = TMath.clamp(this.lerpOutTime / this.lerpOut, 0.0, 1.0);
      return this.lerpCurveOut == Easing.LINEAR ? ratio : this.lerpCurveOut.apply(ratio);
   }

   @Override
   public void setLerpCurveIn(@Nullable Easing lerpCurveIn) {
      this.lerpCurveIn = lerpCurveIn == null ? Easing.LINEAR : lerpCurveIn;
   }

   @Override
   public void setLerpCurveOut(@Nullable Easing lerpCurveOut) {
      this.lerpCurveOut = lerpCurveOut == null ? Easing.LINEAR : lerpCurveOut;
   }

   @Override
   public boolean isFinished() {
      return this.phase == IAnimationProperty.Phase.LERPOUT || this.time >= this.blueprintAnimation.getLength();
   }

   @Override
   public BlueprintAnimation.LoopMode getLoopMode() {
      return this.forceLoopMode == null ? this.blueprintAnimation.getLoopMode() : this.forceLoopMode;
   }

   @Override
   public boolean isOverride() {
      return this.forceOverride == null && this.blueprintAnimation.isOverride() || this.forceOverride == BlueprintAnimation.OverrideMode.OVERRIDE;
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.lerpIn, this.lerpOut, this.time, this.speed, this.phase, this.forceLoopMode, this.forceOverride);
   }

   @Override
   public void save(SavedData data) {
      data.putString("id", "simple");
      data.putString("name", this.getName());
      data.putDouble("lerp_in", this.lerpIn);
      data.putDouble("lerp_out", this.lerpOut);
      data.putDouble("lerp_in_time", this.lerpInTime);
      data.putDouble("lerp_out_time", this.lerpOutTime);
      data.putDouble("last_time", this.lastTime);
      data.putDouble("time", this.time);
      data.putDouble("speed", this.speed);
      data.putString("phase", this.phase.name());
      if (this.forceLoopMode != null) {
         data.putString("force_loop_mode", this.forceLoopMode.name());
      }

      if (this.forceOverride != null) {
         data.putString("force_override", this.forceOverride.name());
      }

      if (this.lerpCurveIn != Easing.LINEAR) {
         data.putString("lerp_curve_in", this.lerpCurveIn.name());
      }

      if (this.lerpCurveOut != Easing.LINEAR) {
         data.putString("lerp_curve_out", this.lerpCurveOut.name());
      }
   }

   @Override
   public void load(SavedData data) {
      this.lerpInTime = data.getDouble("lerp_in_time");
      this.lerpOutTime = data.getDouble("lerp_out_time");
      this.lastTime = data.getDouble("last_time");
      this.time = data.getDouble("time");
      this.phase = IAnimationProperty.Phase.valueOf(data.getString("phase"));
      data.loadIfExist("force_loop_mode", SavedData::getString, val -> this.forceLoopMode = BlueprintAnimation.LoopMode.getOrNull(val));
      data.loadIfExist("force_override", SavedData::getString, val -> {
         if (Boolean.parseBoolean(val)) {
            this.forceOverride = BlueprintAnimation.OverrideMode.OVERRIDE;
         }

         this.forceOverride = BlueprintAnimation.OverrideMode.getOrNull(val);
      });
      data.loadIfExist("lerp_curve_in", SavedData::getString, val -> this.setLerpCurveIn(Easing.getOrNull(val)));
      data.loadIfExist("lerp_curve_out", SavedData::getString, val -> this.setLerpCurveOut(Easing.getOrNull(val)));
   }

   public static SimpleProperty create(AnimationHandler handler, SavedData data) {
      ActiveModel model = handler.getActiveModel();
      ModelBlueprint blueprint = model.getBlueprint();
      BlueprintAnimation animation = blueprint.getAnimationOrRef(data.getString("name"));
      SimpleProperty property = new SimpleProperty(
         model, animation, data.getDouble("lerp_in", 0.0), data.getDouble("lerp_out", 0.0), data.getDouble("speed", 1.0)
      );
      property.load(data);
      return property;
   }

   @Generated
   @Override
   public String toString() {
      return "SimpleProperty(model="
         + this.getModel()
         + ", blueprintAnimation="
         + this.getBlueprintAnimation()
         + ", name="
         + this.getName()
         + ", lerpIn="
         + this.getLerpIn()
         + ", lerpOut="
         + this.getLerpOut()
         + ", lerpInTime="
         + this.getLerpInTime()
         + ", lerpOutTime="
         + this.getLerpOutTime()
         + ", lastTime="
         + this.getLastTime()
         + ", time="
         + this.getTime()
         + ", speed="
         + this.getSpeed()
         + ", phase="
         + this.getPhase()
         + ", forceLoopMode="
         + this.getForceLoopMode()
         + ", forceOverride="
         + this.getForceOverride()
         + ", lerpCurveIn="
         + this.getLerpCurveIn()
         + ", lerpCurveOut="
         + this.getLerpCurveOut()
         + ", emptyZero="
         + this.isEmptyZero()
         + ", skipLastFrame="
         + this.isSkipLastFrame()
         + ", mergeLerp="
         + this.isMergeLerp()
         + ", ignoreDeath="
         + this.isIgnoreDeath()
         + ", stopping="
         + this.isStopping()
         + ", ended="
         + this.isEnded()
         + ", onEndTask="
         + this.getOnEndTask()
         + ")";
   }

   @Generated
   @Override
   public void setLerpIn(double lerpIn) {
      this.lerpIn = lerpIn;
   }

   @Generated
   @Override
   public void setLerpOut(double lerpOut) {
      this.lerpOut = lerpOut;
   }

   @Generated
   @Override
   public void setLerpInTime(double lerpInTime) {
      this.lerpInTime = lerpInTime;
   }

   @Generated
   @Override
   public void setLerpOutTime(double lerpOutTime) {
      this.lerpOutTime = lerpOutTime;
   }

   @Generated
   @Override
   public void setSpeed(double speed) {
      this.speed = speed;
   }

   @Generated
   public void setPhase(@NotNull IAnimationProperty.Phase phase) {
      if (phase == null) {
         throw new NullPointerException("phase is marked non-null but is null");
      }

      this.phase = phase;
   }

   @Generated
   @Override
   public void setForceLoopMode(BlueprintAnimation.LoopMode forceLoopMode) {
      this.forceLoopMode = forceLoopMode;
   }

   @Generated
   @Override
   public void setForceOverride(BlueprintAnimation.OverrideMode forceOverride) {
      this.forceOverride = forceOverride;
   }

   @Generated
   @Override
   public void setEmptyZero(boolean emptyZero) {
      this.emptyZero = emptyZero;
   }

   @Generated
   @Override
   public void setSkipLastFrame(boolean skipLastFrame) {
      this.skipLastFrame = skipLastFrame;
   }

   @Generated
   @Override
   public void setMergeLerp(boolean mergeLerp) {
      this.mergeLerp = mergeLerp;
   }

   @Generated
   @Override
   public void setIgnoreDeath(boolean ignoreDeath) {
      this.ignoreDeath = ignoreDeath;
   }

   @Generated
   public void setOnEndTask(Runnable onEndTask) {
      this.onEndTask = onEndTask;
   }
}
