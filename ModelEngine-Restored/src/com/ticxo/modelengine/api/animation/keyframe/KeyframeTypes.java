package com.ticxo.modelengine.api.animation.keyframe;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.handler.IPriorityHandler;
import com.ticxo.modelengine.api.animation.handler.IStateMachineHandler;
import com.ticxo.modelengine.api.animation.interpolator.KeyframeInterpolator;
import com.ticxo.modelengine.api.animation.interpolator.PrePostInterpolator;
import com.ticxo.modelengine.api.animation.interpolator.ScriptInterpolator;
import com.ticxo.modelengine.api.animation.keyframe.type.ScriptKeyframe;
import com.ticxo.modelengine.api.animation.keyframe.type.VectorKeyframe;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.animation.script.ScriptReader;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.utils.StepFlag;
import com.ticxo.modelengine.api.utils.math.SafeTransform;
import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public final class KeyframeTypes {
   public static final KeyframeType<VectorKeyframe, Vector3f> POSITION = KeyframeType.Builder.<VectorKeyframe, Vector3f>of("position", VectorKeyframe::new)
      .interpolator(
         timeline -> new PrePostInterpolator<>(
            (ctx, prev, next, ratio) -> standard(ctx, prev, next, ratio, StepFlag.POSITION),
            (ctx, vectorKeyframe) -> markStep(ctx, vectorKeyframe, StepFlag.POSITION),
            KeyframeTypes::derive
         )
      )
      .registerBoneUpdater(IPriorityHandler.class, (handler, bone, data) -> {
         IAnimationProperty property = (IAnimationProperty)data[0];
         SafeTransform transform = bone.getLocalTransform();
         Vector3f output = transform.getPosition();
         if (bone.isRootBone()) {
            if (property.getPhase() == IAnimationProperty.Phase.PLAY) {
               Vector3f vel = property.getVelocityFrame(bone);
               if (vel == null && property.isEmptyZero()) {
                  vel = new Vector3f();
               }

               if (vel != null) {
                  if (property.isOverride()) {
                     output.set(bone.getBlueprintBone().getLocalPosition());
                  }

                  output.add(vel);
               }

               transform.setPosition(output);
               Vector3f val = property.getPositionFrame(bone);
               bone.setBoneOnGround(val == null || TMath.isSimilar(val.y, 0.0F));
            }
         } else {
            Vector3f val = property.getPositionFrame(bone);
            if (!property.isOverride()) {
               if (val == null) {
                  val = new Vector3f();
               }

               switch (property.getPhase()) {
                  case LERPOUT:
                     val = TMath.lerp(val, new Vector3f(), property.getLerpOutRatio());
                     break;
                  case LERPIN:
                     val = TMath.lerp(new Vector3f(), val, property.getLerpInRatio());
               }

               output.add(val);
            } else {
               if (val == null && property.isEmptyZero()) {
                  val = new Vector3f();
               }

               if (val != null) {
                  Vector3f local = bone.getBlueprintBone().getLocalPosition();
                  switch (property.getPhase()) {
                     case PLAY:
                        output.set(local).add(val);
                        break;
                     case LERPOUT:
                        output.set(TMath.lerp(val.add(local), output, property.getLerpOutRatio()));
                        break;
                     case LERPIN:
                        output.set(TMath.lerp(output, val.add(local), property.getLerpInRatio()));
                  }
               }
            }

            transform.setPosition(output);
         }
      })
      .registerBoneUpdater(IStateMachineHandler.class, (handler, bone, data) -> {
         IAnimationProperty currProperty = (IAnimationProperty)data[0];
         if (currProperty != null) {
            SafeTransform transform = bone.getLocalTransform();
            if (bone.isRootBone()) {
               Vector3f output = transform.getPosition();
               if (currProperty.getPhase() == IAnimationProperty.Phase.PLAY) {
                  Vector3f vel = currProperty.getVelocityFrame(bone);
                  if (vel == null && currProperty.isEmptyZero()) {
                     vel = new Vector3f();
                  }

                  if (vel != null) {
                     if (currProperty.isOverride()) {
                        output.set(bone.getBlueprintBone().getLocalPosition());
                     }

                     output.add(vel);
                  }

                  transform.setPosition(output);
                  Vector3f val = currProperty.getPositionFrame(bone);
                  bone.setBoneOnGround(val == null || TMath.isSimilar(val.y, 0.0F));
               }
            } else {
               Vector3f currVal = new Vector3f(transform.getPosition());
               Vector3f val = currProperty.getPositionFrame(bone);
               if (val == null && currProperty.isEmptyZero()) {
                  val = new Vector3f();
               }

               if (val != null) {
                  if (currProperty.isOverride()) {
                     currVal.set(bone.getBlueprintBone().getLocalPosition());
                  }

                  currVal.add(val);
               }

               switch (currProperty.getPhase()) {
                  case PLAY:
                     transform.setPosition(currVal);
                     return;
                  case LERPOUT:
                     transform.setPosition(TMath.lerp(currVal, transform.getPosition(), currProperty.getLerpOutRatio()));
                     return;
                  default:
                     IAnimationProperty lastProperty = (IAnimationProperty)data[1];
                     if (lastProperty == null) {
                        transform.setPosition(TMath.lerp(transform.getPosition(), currVal, currProperty.getLerpInRatio()));
                        return;
                     }

                     Vector3f lastVal = new Vector3f(transform.getPosition());
                     Vector3f positionFrame = lastProperty.getPositionFrame(bone);
                     if (positionFrame == null && lastProperty.isEmptyZero()) {
                        positionFrame = new Vector3f();
                     }

                     if (positionFrame != null) {
                        if (lastProperty.isOverride()) {
                           lastVal.set(bone.getBlueprintBone().getLocalPosition());
                        }

                        lastVal.add(positionFrame);
                     }

                     transform.setPosition(TMath.lerp(lastVal, currVal, currProperty.getLerpInRatio()));
               }
            }
         }
      })
      .build();
   public static final KeyframeType<VectorKeyframe, Vector3f> ROTATION = KeyframeType.Builder.<VectorKeyframe, Vector3f>of("rotation", VectorKeyframe::new)
      .interpolator(
         timeline -> new PrePostInterpolator<>(
            (ctx, prev, next, ratio) -> standard(ctx, prev, next, ratio, StepFlag.ROTATION),
            (ctx, vectorKeyframe) -> markStep(ctx, vectorKeyframe, StepFlag.ROTATION),
            KeyframeTypes::derive
         )
      )
      .registerBoneUpdater(IPriorityHandler.class, (handler, bone, data) -> {
         IAnimationProperty property = (IAnimationProperty)data[0];
         SafeTransform transform = bone.getLocalTransform();
         Vector3f output = transform.getLeftEuler();
         Vector3f val = property.getRotationFrame(bone);
         if (!property.isOverride()) {
            if (val == null) {
               val = new Vector3f();
            }

            switch (property.getPhase()) {
               case LERPOUT:
                  val = TMath.slerp(val, new Vector3f(), property.getLerpOutRatio());
                  break;
               case LERPIN:
                  val = TMath.slerp(new Vector3f(), val, property.getLerpInRatio());
            }

            output.add(val);
         } else {
            if (val == null && property.isEmptyZero()) {
               val = new Vector3f();
            }

            if (val != null) {
               Vector3f local = bone.getBlueprintBone().getLocalRotation();
               switch (property.getPhase()) {
                  case PLAY:
                     output.set(local).add(val);
                     break;
                  case LERPOUT:
                     output.set(TMath.slerp(val.add(local), output, property.getLerpOutRatio()));
                     break;
                  case LERPIN:
                     output.set(TMath.slerp(output, val.add(local), property.getLerpInRatio()));
               }
            }
         }

         transform.setLeftEuler(output);
      })
      .registerBoneUpdater(IStateMachineHandler.class, (handler, bone, data) -> {
         IAnimationProperty currProperty = (IAnimationProperty)data[0];
         if (currProperty != null) {
            SafeTransform transform = bone.getLocalTransform();
            Vector3f currVal = new Vector3f(transform.getLeftEuler());
            Vector3f val = currProperty.getRotationFrame(bone);
            if (val == null && currProperty.isEmptyZero()) {
               val = new Vector3f();
            }

            if (val != null) {
               if (currProperty.isOverride()) {
                  currVal.set(bone.getBlueprintBone().getLocalRotation());
               }

               currVal.add(val);
            }

            switch (currProperty.getPhase()) {
               case PLAY:
                  transform.setLeftEuler(currVal);
                  return;
               case LERPOUT:
                  transform.setLeftEuler(TMath.slerp(currVal, transform.getLeftEuler(), currProperty.getLerpOutRatio()));
                  return;
               default:
                  IAnimationProperty lastProperty = (IAnimationProperty)data[1];
                  if (lastProperty == null) {
                     transform.setLeftEuler(TMath.slerp(transform.getLeftEuler(), currVal, currProperty.getLerpInRatio()));
                  } else {
                     Vector3f lastVal = new Vector3f(transform.getLeftEuler());
                     Vector3f rotationFrame = lastProperty.getRotationFrame(bone);
                     if (rotationFrame == null && lastProperty.isEmptyZero()) {
                        rotationFrame = new Vector3f();
                     }

                     if (rotationFrame != null) {
                        if (lastProperty.isOverride()) {
                           lastVal.set(bone.getBlueprintBone().getLocalRotation());
                        }

                        lastVal.add(rotationFrame);
                     }

                     transform.setLeftEuler(TMath.slerp(lastVal, currVal, currProperty.getLerpInRatio()));
                  }
            }
         }
      })
      .build();
   public static final KeyframeType<VectorKeyframe, Vector3f> SCALE = KeyframeType.Builder.<VectorKeyframe, Vector3f>of("scale", VectorKeyframe::new)
      .interpolator(
         timeline -> new PrePostInterpolator<>(
            (ctx, prev, next, ratio) -> standard(ctx, prev, next, ratio, StepFlag.SCALE),
            (ctx, vectorKeyframe) -> markStep(ctx, vectorKeyframe, StepFlag.SCALE),
            KeyframeTypes::derive
         )
      )
      .registerBoneUpdater(IPriorityHandler.class, (handler, bone, data) -> {
         IAnimationProperty property = (IAnimationProperty)data[0];
         SafeTransform transform = bone.getLocalTransform();
         Vector3f output = transform.getScale();
         Vector3f val = property.getScaleFrame(bone);
         if (!property.isOverride()) {
            if (val == null) {
               val = new Vector3f(1.0F);
            }

            switch (property.getPhase()) {
               case LERPOUT:
                  val = TMath.lerp(val, new Vector3f(1.0F), property.getLerpOutRatio());
                  break;
               case LERPIN:
                  val = TMath.lerp(new Vector3f(1.0F), val, property.getLerpInRatio());
            }

            output.mul(val);
         } else {
            if (val == null && property.isEmptyZero()) {
               val = new Vector3f(1.0F);
            }

            if (val != null) {
               switch (property.getPhase()) {
                  case PLAY:
                     output.set(val);
                     break;
                  case LERPOUT:
                     output.set(TMath.lerp(val, output, property.getLerpOutRatio()));
                     break;
                  case LERPIN:
                     output.set(TMath.lerp(output, val, property.getLerpInRatio()));
               }
            }
         }

         transform.setScale(output);
      })
      .registerBoneUpdater(IStateMachineHandler.class, (handler, bone, data) -> {
         IAnimationProperty currProperty = (IAnimationProperty)data[0];
         if (currProperty != null) {
            SafeTransform transform = bone.getLocalTransform();
            Vector3f currVal = new Vector3f(transform.getScale());
            Vector3f val = currProperty.getScaleFrame(bone);
            if (val == null && currProperty.isEmptyZero()) {
               val = new Vector3f(1.0F);
            }

            if (val != null) {
               if (currProperty.isOverride()) {
                  currVal.set(1.0F);
               }

               currVal.mul(val);
            }

            switch (currProperty.getPhase()) {
               case PLAY:
                  transform.setScale(currVal);
                  return;
               case LERPOUT:
                  transform.setScale(TMath.lerp(currVal, transform.getScale(), currProperty.getLerpOutRatio()));
                  return;
               default:
                  IAnimationProperty lastProperty = (IAnimationProperty)data[1];
                  if (lastProperty == null) {
                     transform.setScale(TMath.lerp(transform.getScale(), currVal, currProperty.getLerpInRatio()));
                  } else {
                     Vector3f lastVal = new Vector3f(transform.getScale());
                     Vector3f scaleFrame = lastProperty.getScaleFrame(bone);
                     if (scaleFrame == null && lastProperty.isEmptyZero()) {
                        scaleFrame = new Vector3f(1.0F);
                     }

                     if (scaleFrame != null) {
                        if (lastProperty.isOverride()) {
                           lastVal.set(1.0F);
                        }

                        lastVal.mul(scaleFrame);
                     }

                     transform.setScale(TMath.lerp(lastVal, currVal, currProperty.getLerpInRatio()));
                  }
            }
         }
      })
      .build();
   public static final KeyframeType<ScriptKeyframe, List<ScriptKeyframe.Script>> SCRIPT = KeyframeType.Builder.<ScriptKeyframe, List<ScriptKeyframe.Script>>of(
         "script", ScriptKeyframe::new
      )
      .interpolator(blueprintAnimation -> new ScriptInterpolator<>(ArrayList::new, List::addAll))
      .registerModelUpdater(IPriorityHandler.class, KeyframeTypes::standardScript)
      .registerModelUpdater(IStateMachineHandler.class, KeyframeTypes::standardScript)
      .global()
      .build();

   private static Vector3f standard(KeyframeInterpolator.Context<VectorKeyframe, Vector3f> ctx, Vector3f prev, Vector3f next, float ratio, StepFlag stepFlag) {
      String animation = ctx.property.getBlueprintAnimation().getName();
      String prevMode = ctx.prevFrame.getInterpolation();
      if (prevMode.equals("step")) {
         ctx.bone.markStep(stepFlag, animation, ctx.prevKey);
         return prev;
      }

      VectorKeyframe nextFrame = ctx.nextFrame;
      if (nextFrame.isDiscontinuous() && ctx.property.getTime() + ctx.property.getSpeed() * 0.05 > ctx.nextKey) {
         ctx.bone.markStep(stepFlag, animation, ctx.nextKey);
      }

      String nextMode = nextFrame.getInterpolation();
      if (prevMode.equals("catmullrom") || nextMode.equals("catmullrom")) {
         Entry<Float, VectorKeyframe> nextControlEntry = ctx.interpolator.higherEntry(ctx.nextKey);
         if (nextControlEntry == null) {
            nextControlEntry = ctx.interpolator.lastEntry();
         }

         Entry<Float, VectorKeyframe> lastControlEntry = ctx.interpolator.lowerEntry(ctx.prevKey);
         if (lastControlEntry == null) {
            lastControlEntry = ctx.interpolator.firstEntry();
         }

         VectorKeyframe nextControlVector = nextControlEntry.getValue();
         VectorKeyframe lastControlVector = lastControlEntry.getValue();
         return TMath.smoothLerp(lastControlVector.getValue(0, ctx.property), prev, next, nextControlVector.getValue(0, ctx.property), ratio);
      } else {
         return !ctx.prevFrame.isBezier() && !nextFrame.isBezier()
            ? prev.lerp(next, ratio, new Vector3f())
            : bezierLerp(ctx, ctx.prevFrame, nextFrame, prev, next, ratio);
      }
   }

   private static Vector3f bezierLerp(
      KeyframeInterpolator.Context<VectorKeyframe, Vector3f> ctx, VectorKeyframe prevFrame, VectorKeyframe nextFrame, Vector3f prev, Vector3f next, float ratio
   ) {
      float gap = ctx.nextKey - ctx.prevKey;
      return new Vector3f(
         bezierAxis(ratio, gap, prevFrame, nextFrame, prev.x, next.x, 0),
         bezierAxis(ratio, gap, prevFrame, nextFrame, prev.y, next.y, 1),
         bezierAxis(ratio, gap, prevFrame, nextFrame, prev.z, next.z, 2)
      );
   }

   private static float bezierAxis(float t, float gap, VectorKeyframe prevFrame, VectorKeyframe nextFrame, float prev, float next, int axis) {
      float prevOffset = prevFrame.getRightValue()[axis] * axisFactor(prevFrame, axis);
      float nextOffset = nextFrame.getLeftValue()[axis] * axisFactor(nextFrame, axis);
      if (prevOffset == 0.0F && nextOffset == 0.0F) {
         return TMath.lerp(prev, next, t);
      }

      float u1 = TMath.clamp(prevFrame.isBezier() ? prevFrame.getRightTime()[axis] : 0.1F, 0.0F, gap) / gap;
      float u2 = 1.0F + TMath.clamp(nextFrame.isBezier() ? nextFrame.getLeftTime()[axis] : -0.1F, -gap, 0.0F) / gap;
      float s = TMath.cubicBezierSolve(u1, u2, t);
      return TMath.cubicBezierValue(prev, prev + prevOffset, next + nextOffset, next, s);
   }

   private static float axisFactor(VectorKeyframe frame, int axis) {
      return switch (axis) {
         case 0 -> frame.getXFactor();
         case 1 -> frame.getYFactor();
         default -> frame.getZFactor();
      };
   }

   private static void markStep(KeyframeInterpolator.Context<VectorKeyframe, Vector3f> ctx, VectorKeyframe frame, StepFlag stepFlag) {
      String animation = ctx.property.getBlueprintAnimation().getName();
      if ("step".equals(frame.getInterpolation()) || frame.isDiscontinuous()) {
         ctx.bone.markStep(stepFlag, animation, ctx.nextKey);
      }
   }

   private static Vector3f derive(@Nullable Vector3f prev, @Nullable Vector3f curr) {
      return prev != null && curr != null ? curr.sub(prev, new Vector3f()) : new Vector3f();
   }

   private static void standardScript(AnimationHandler handler, ActiveModel model, Object... data) {
      IAnimationProperty property = (IAnimationProperty)data[0];
      List<ScriptKeyframe.Script> scripts = property.getScriptFrame();
      if (scripts != null && !scripts.isEmpty()) {
         for (ScriptKeyframe.Script script : scripts) {
            ScriptReader reader = ModelEngineAPI.getAPI().getScriptReaderRegistry().get(script.reader());
            if (reader != null) {
               reader.read(property, script.script());
            }
         }
      }
   }
}
