package com.ticxo.modelengine.core.animation.handler;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.AnimationPropertyRegistry;
import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.ModelState;
import com.ticxo.modelengine.api.animation.Timeline;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.handler.IStateMachineHandler;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeType;
import com.ticxo.modelengine.api.animation.keyframe.KeyframeTypeRegistry;
import com.ticxo.modelengine.api.animation.keyframe.type.AbstractKeyframe;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.animation.property.SimpleProperty;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.events.AnimationEndEvent;
import com.ticxo.modelengine.api.events.AnimationPlayEvent;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.data.io.DataIO;
import com.ticxo.modelengine.api.utils.data.io.SavedData;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.api.utils.state.StateMachine;
import com.ticxo.modelengine.api.utils.state.StateNode;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

public class StateMachineHandler implements IStateMachineHandler {
   private static int DEFAULT_PRIORITY;
   @Getter
   private final ActiveModel activeModel;
   @Getter
   private final ModelBlueprint blueprint;
   private final Map<ModelState, AnimationHandler.DefaultProperty> defaultProperties = Maps.newConcurrentMap();
   @Getter
   private final TreeMap<Integer, StateMachineHandler.AnimationStateMachine> stateMachines = new TreeMap<>();
   private final Queue<Runnable> actionQueue = new ConcurrentLinkedQueue<>();
   private boolean firstSpawn = true;

   public StateMachineHandler(ActiveModel activeModel) {
      this.activeModel = activeModel;
      this.blueprint = activeModel.getBlueprint();
      this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.IDLE, 0.25, 0.0, 1.0));
      this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.WALK, 0.25, 0.0, 1.0));
      this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.STRAFE, 0.25, 0.0, 1.0));
      this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.JUMP_START, 0.0, 0.0, 1.0));
      this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.JUMP, 0.0, 0.0, 1.0));
      this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.JUMP_END, 0.0, 0.0, 1.0));
      this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.HOVER, 0.25, 0.0, 1.0));
      this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.FLY, 0.25, 0.0, 1.0));
      this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.SPAWN, 0.0, 0.0, 1.0));
      this.setDefaultProperty(new AnimationHandler.DefaultProperty(ModelState.DEATH, 0.0, 0.0, 1.0));
      this.configureAnimation();
   }

   private void configureAnimation() {
      StateMachineHandler.AnimationStateMachine stateMachine = new StateMachineHandler.AnimationStateMachine(false);
      StateNode<BaseEntity<?>> root = stateMachine.getRootNode();
      StateNode<BaseEntity<?>> spawn = stateMachine.createAnimationNode(() -> this.createStateProperty(ModelState.SPAWN));
      StateNode<BaseEntity<?>> idle = stateMachine.createAnimationNode(() -> this.createStateProperty(ModelState.IDLE));
      StateNode<BaseEntity<?>> walk = stateMachine.createAnimationNode(() -> this.createStateProperty(ModelState.WALK));
      StateNode<BaseEntity<?>> strafe = stateMachine.createAnimationNode(() -> this.createStateProperty(ModelState.STRAFE));
      StateNode<BaseEntity<?>> jumpStart = stateMachine.createAnimationNode(() -> this.createStateProperty(ModelState.JUMP_START));
      StateNode<BaseEntity<?>> jumpLoop = stateMachine.createAnimationNode(() -> this.createStateProperty(ModelState.JUMP));
      StateNode<BaseEntity<?>> jumpEnd = stateMachine.createAnimationNode(() -> this.createStateProperty(ModelState.JUMP_END));
      StateNode<BaseEntity<?>> hover = stateMachine.createAnimationNode(() -> this.createStateProperty(ModelState.HOVER));
      StateNode<BaseEntity<?>> fly = stateMachine.createAnimationNode(() -> this.createStateProperty(ModelState.FLY));
      StateNode<BaseEntity<?>> death = stateMachine.createAnimationNode(() -> {
         this.forceStopAllAnimations();
         return this.createStateProperty(ModelState.DEATH);
      });
      root.addConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
      root.addConnectedNode(baseEntity -> this.hasAnimation(ModelState.SPAWN), spawn);
      root.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
      root.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
      root.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_START), jumpStart);
      root.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP), jumpLoop);
      root.addConnectedNode(baseEntity -> baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
      root.addConnectedNode(baseEntity -> this.isWalking(this.activeModel), walk);
      root.addConnectedNode(baseEntity -> !this.isWalking(this.activeModel), idle);
      spawn.addConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
      spawn.setCommonPredicate(baseEntity -> stateMachine.hasFinishedPlaying(ModelState.SPAWN));
      spawn.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
      spawn.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
      spawn.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_START), jumpStart);
      spawn.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP), jumpLoop);
      spawn.addConnectedNode(baseEntity -> baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
      spawn.addConnectedNode(baseEntity -> this.isWalking(this.activeModel), walk);
      spawn.addConnectedNode(baseEntity -> !this.isWalking(this.activeModel), idle);
      idle.addConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
      idle.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
      idle.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
      idle.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_START), jumpStart);
      idle.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP), jumpLoop);
      idle.addConnectedNode(baseEntity -> baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
      idle.addConnectedNode(baseEntity -> this.isWalking(this.activeModel), walk);
      walk.addConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
      walk.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
      walk.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
      walk.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_START), jumpStart);
      walk.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP), jumpLoop);
      walk.addConnectedNode(baseEntity -> baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
      walk.addConnectedNode(baseEntity -> !this.isWalking(this.activeModel), idle);
      strafe.addConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
      strafe.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
      strafe.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
      strafe.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_START), jumpStart);
      strafe.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP), jumpLoop);
      strafe.addConnectedNode(baseEntity -> this.isWalking(this.activeModel), walk);
      strafe.addConnectedNode(baseEntity -> !this.isWalking(this.activeModel), idle);
      jumpStart.addForceConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
      jumpStart.setCommonPredicate(baseEntity -> stateMachine.hasFinishedPlaying(ModelState.JUMP_START));
      jumpStart.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
      jumpStart.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
      jumpStart.addConnectedNode(baseEntity -> !baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_END), jumpEnd);
      jumpStart.addConnectedNode(baseEntity -> !baseEntity.isJumping() && this.isWalking(this.activeModel), walk);
      jumpStart.addConnectedNode(baseEntity -> !baseEntity.isJumping() && !this.isWalking(this.activeModel), idle);
      jumpLoop.addConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
      jumpLoop.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
      jumpLoop.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
      jumpLoop.addConnectedNode(baseEntity -> !baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_END), jumpEnd);
      jumpLoop.addConnectedNode(baseEntity -> !baseEntity.isJumping() && this.isWalking(this.activeModel), walk);
      jumpLoop.addConnectedNode(baseEntity -> !baseEntity.isJumping() && !this.isWalking(this.activeModel), idle);
      jumpEnd.addForceConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
      jumpEnd.setCommonPredicate(baseEntity -> stateMachine.hasFinishedPlaying(ModelState.JUMP_END));
      jumpEnd.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
      jumpEnd.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.hasAnimation(ModelState.HOVER), hover);
      jumpEnd.addConnectedNode(baseEntity -> baseEntity.isJumping() && this.hasAnimation(ModelState.JUMP_START), jumpStart);
      jumpEnd.addConnectedNode(baseEntity -> baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
      jumpEnd.addConnectedNode(baseEntity -> this.isWalking(this.activeModel), walk);
      jumpEnd.addConnectedNode(baseEntity -> !this.isWalking(this.activeModel), idle);
      hover.addConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
      hover.addConnectedNode(baseEntity -> baseEntity.isFlying() && this.isWalking(this.activeModel) && this.hasAnimation(ModelState.FLY), fly);
      hover.addConnectedNode(baseEntity -> !baseEntity.isFlying() && baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
      hover.addConnectedNode(baseEntity -> !baseEntity.isFlying() && this.isWalking(this.activeModel), walk);
      hover.addConnectedNode(baseEntity -> !baseEntity.isFlying() && !this.isWalking(this.activeModel), idle);
      fly.addConnectedNode(baseEntity -> !baseEntity.isAlive(), death);
      fly.addConnectedNode(baseEntity -> baseEntity.isFlying() && !this.isWalking(this.activeModel) && this.hasAnimation(ModelState.HOVER), hover);
      fly.addConnectedNode(baseEntity -> !baseEntity.isFlying() && baseEntity.isStrafing() && this.hasAnimation(ModelState.STRAFE), strafe);
      fly.addConnectedNode(baseEntity -> !baseEntity.isFlying() && this.isWalking(this.activeModel), walk);
      fly.addConnectedNode(baseEntity -> !baseEntity.isFlying() && !this.isWalking(this.activeModel), idle);
      this.stateMachines.put(DEFAULT_PRIORITY, stateMachine);
   }

   @Override
   public void prepare() {
      while (!this.actionQueue.isEmpty()) {
         this.actionQueue.poll().run();
      }

      synchronized (this.stateMachines) {
         this.stateMachines.values().forEach(machine -> {
            machine.materializeQueued();
            machine.execute(this.activeModel.getModeledEntity().getBase());
            machine.queuedAnimation = null;
         });
      }

      this.firstSpawn = false;
   }

   @Override
   public void updateBone(ModelBone bone) {
      this.updateBone(bone, null);
   }

   @Override
   public void updateBone(ModelBone bone, @Nullable UnaryOperator<IAnimationProperty> propertyMapper) {
      bone.setHasGlobalRotation(false);
      KeyframeTypeRegistry registry = ModelEngineAPI.getAPI().getKeyframeTypeRegistry();
      ArrayDeque<StateMachineHandler.AnimationStateMachine> stack = new ArrayDeque<>();

      for (String typeName : registry.getKeys()) {
         KeyframeType<? extends AbstractKeyframe<?>, ?> type = (KeyframeType<? extends AbstractKeyframe<?>, ?>)registry.get(typeName);
         if (!type.isGlobal()) {
            this.fillUpdateStack(stack, type, bone);

            while (!stack.isEmpty()) {
               StateMachineHandler.AnimationStateMachine stateMachine = stack.pop();
               IAnimationProperty currentProperty = stateMachine.currentAnimation;
               IAnimationProperty lastProperty = stateMachine.lastAnimation;
               bone.setHasGlobalRotation(this.isGlobalRotation(currentProperty, bone) || this.isGlobalRotation(lastProperty, bone));
               if (propertyMapper != null) {
                  currentProperty = propertyMapper.apply(currentProperty);
                  lastProperty = propertyMapper.apply(lastProperty);
               }

               type.updateBone(IStateMachineHandler.class, this, bone, currentProperty, lastProperty);
            }
         }
      }
   }

   private boolean isGlobalRotation(IAnimationProperty property, ModelBone bone) {
      if (property == null) {
         return false;
      }

      BlueprintAnimation animation = property.getBlueprintAnimation();
      if (animation == null) {
         return false;
      }

      Map<UUID, Timeline> timelines = animation.getTimelines();
      if (timelines == null) {
         return false;
      }

      Timeline timeline = timelines.get(bone.getBlueprintBone().getUuid());
      return timeline == null ? false : timeline.isGlobalRotation();
   }

   @Override
   public boolean hasFinishedAllAnimations() {
      synchronized (this.stateMachines) {
         for (StateMachineHandler.AnimationStateMachine machine : this.stateMachines.values()) {
            if (machine.currentAnimation != null && !machine.currentAnimation.isFinished() && !machine.currentAnimation.isIgnoreDeath()) {
               return false;
            }
         }

         return true;
      }
   }

   @Override
   public void setDefaultProperty(AnimationHandler.DefaultProperty defaultProperty) {
      this.defaultProperties.put(defaultProperty.getState(), defaultProperty);
   }

   @Override
   public AnimationHandler.DefaultProperty getDefaultProperty(ModelState state) {
      return this.defaultProperties.get(state);
   }

   @Override
   public void tickGlobal() {
      synchronized (this.stateMachines) {
         for (StateMachineHandler.AnimationStateMachine machine : this.stateMachines.values()) {
            if (machine.currentAnimation != null) {
               KeyframeTypeRegistry registry = ModelEngineAPI.getAPI().getKeyframeTypeRegistry();

               for (String typeName : registry.getKeys()) {
                  KeyframeType<? extends AbstractKeyframe<?>, ?> type = (KeyframeType<? extends AbstractKeyframe<?>, ?>)registry.get(typeName);
                  if (type.isGlobal()) {
                     type.updateModel(IStateMachineHandler.class, this, machine.currentAnimation, machine.lastAnimation);
                  }
               }
            }
         }
      }
   }

   @Nullable
   @Override
   public IAnimationProperty playAnimation(String animation, double lerpIn, double lerpOut, double speed, boolean force) {
      return this.playAnimation(1, animation, lerpIn, lerpOut, speed, force);
   }

   @Override
   public boolean playAnimation(IAnimationProperty property, boolean force) {
      return this.playAnimation(1, property, force);
   }

   private void fillUpdateStack(ArrayDeque<StateMachineHandler.AnimationStateMachine> stack, KeyframeType<?, ?> type, ModelBone bone) {
      if (!this.stateMachines.isEmpty()) {
         UUID boneUuid = bone.getBlueprintBone().getUuid();

         for (Entry<Integer, StateMachineHandler.AnimationStateMachine> entry = this.stateMachines.lastEntry();
            entry != null;
            entry = this.stateMachines.lowerEntry(entry.getKey())
         ) {
            StateMachineHandler.AnimationStateMachine machine = entry.getValue();
            IAnimationProperty currentProperty = machine.currentAnimation;
            IAnimationProperty lastProperty = machine.lastAnimation;
            if (currentProperty != null) {
               stack.push(machine);
               if (this.isLastProperty(currentProperty, type, boneUuid) && (lastProperty == null || this.isLastProperty(lastProperty, type, boneUuid))) {
                  break;
               }
            }
         }
      }
   }

   private boolean isLastProperty(IAnimationProperty property, KeyframeType<?, ?> type, UUID boneUuid) {
      return property.isOverride() && property.containsKeyframe(type, boneUuid) && property.getPhase() == IAnimationProperty.Phase.PLAY;
   }

   @Nullable
   @Override
   public IAnimationProperty getAnimation(String animation) {
      synchronized (this.stateMachines) {
         for (StateMachineHandler.AnimationStateMachine machine : this.stateMachines.values()) {
            if (machine.isQueued(this.blueprint, animation)) {
               return machine.queuedAnimation;
            }

            if (machine.isPlaying(animation)) {
               return machine.currentAnimation;
            }
         }

         return null;
      }
   }

   @Override
   public Map<String, IAnimationProperty> getAnimations() {
      HashMap<String, IAnimationProperty> map = new HashMap<>();

      for (Entry<Integer, StateMachineHandler.AnimationStateMachine> entry : this.stateMachines.entrySet()) {
         int id = entry.getKey();
         StateMachineHandler.AnimationStateMachine stateMachine = entry.getValue();
         IAnimationProperty animation = stateMachine.getEffectiveAnimation();
         if (animation != null) {
            map.put(id + ":" + animation.getName(), animation);
         }
      }

      return map;
   }

   @Nullable
   @Override
   public IAnimationProperty getAnimation(int priority, ModelBlueprint source, String animation) {
      StateMachineHandler.AnimationStateMachine machine = this.stateMachines.get(priority);
      if (machine == null) {
         return null;
      } else if (machine.isQueued(source, animation)) {
         return machine.queuedAnimation;
      } else {
         return machine.isPlaying(source, animation) ? machine.currentAnimation : null;
      }
   }

   @Nullable
   @Override
   public IAnimationProperty playAnimation(int priority, ModelBlueprint source, String animation, double lerpIn, double lerpOut, double speed, boolean force) {
      BlueprintAnimation blueprintAnimation = source.getAnimations().get(animation);
      if (blueprintAnimation == null) {
         return null;
      }

      SimpleProperty property = new SimpleProperty(this.activeModel, blueprintAnimation, lerpIn, lerpOut, speed);
      return this.playAnimation(priority, property, force) ? property : null;
   }

   @Override
   public boolean playAnimation(int priority, IAnimationProperty property, boolean force) {
      AnimationPlayEvent event = new AnimationPlayEvent(this.activeModel, property);
      ModelEngineAPI.callEvent(event);
      if (event.isCancelled()) {
         return false;
      }

      synchronized (this.stateMachines) {
         StateMachineHandler.AnimationStateMachine machine = this.stateMachines
            .computeIfAbsent(priority, id -> new StateMachineHandler.AnimationStateMachine(true));
         if (force || machine.queuedAnimation == null && !machine.isPlaying(property.getName())) {
            machine.queuedAnimation = property;
            machine.queuedForce = force;
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public void refreshState(AnimationHandler.DefaultProperty property) {
      StateMachineHandler.AnimationStateMachine machine = this.stateMachines.get(DEFAULT_PRIORITY);
      if (machine != null && machine.isPlaying(property.getAnimation())) {
         machine.forceReentry(this.activeModel.getModeledEntity().getBase());
      }
   }

   @Override
   public boolean isPlayingAnimation(String animation) {
      return this.getAnimation(animation) != null;
   }

   @Override
   public boolean isPlayingAnimation(int priority, ModelBlueprint source, String animation) {
      return this.getAnimation(priority, source, animation) != null;
   }

   @Override
   public void stopAnimation(String animation) {
      this.stateMachines.keySet().forEach(integer -> this.stopAnimation(integer, animation));
   }

   @Override
   public void stopAnimation(int priority, ModelBlueprint source, String animation) {
      StateMachineHandler.AnimationStateMachine machine = this.stateMachines.get(priority);
      if (machine != null) {
         if (machine.isQueued(source, animation)) {
            machine.queuedAnimation = null;
         } else {
            if (machine.isPlaying(source, animation)) {
               if (machine.currentAnimation.getLerpOut() > 1.0E-5F) {
                  machine.currentAnimation.stop();
               } else {
                  machine.getCurrentNode().addForceConnectedNode(baseEntity -> true, machine.getRootNode());
               }
            }
         }
      }
   }

   @Override
   public void forceStopAnimation(String animation) {
      this.stateMachines.keySet().forEach(integer -> this.forceStopAnimation(integer, animation));
   }

   @Override
   public void forceStopAnimation(int priority, ModelBlueprint source, String animation) {
      StateMachineHandler.AnimationStateMachine machine = this.stateMachines.get(priority);
      if (machine != null) {
         if (machine.isQueued(source, animation)) {
            machine.queuedAnimation = null;
         } else {
            if (machine.isPlaying(source, animation)) {
               machine.getCurrentNode().addForceConnectedNode(baseEntity -> true, machine.getRootNode());
            }
         }
      }
   }

   @Override
   public void forceStopAllAnimations() {
      this.actionQueue.add(() -> this.stateMachines.keySet().removeIf(key -> {
         if (key == DEFAULT_PRIORITY) {
            return false;
         }

         StateMachineHandler.AnimationStateMachine machine = this.stateMachines.get(key);
         return machine.getCurrentAnimation() == null || !machine.getCurrentAnimation().isIgnoreDeath();
      }));
   }

   private boolean hasAnimation(ModelState state) {
      return this.blueprint.getAnimations().containsKey(state.getString());
   }

   @Nullable
   private IAnimationProperty createStateProperty(ModelState state) {
      AnimationHandler.DefaultProperty defProperty = this.getDefaultProperty(state);
      IAnimationProperty property = this.firstSpawn
         ? defProperty.buildRef(this.activeModel, 0.0, defProperty.getLerpOut(), defProperty.getSpeed())
         : defProperty.buildRef(this.activeModel);
      if (property != null) {
         property.setForceLoopMode(state.getLoopMode());
         property.setForceOverride(state.getOverride());
      }

      return property;
   }

   @Override
   public void save(SavedData data) {
      IStateMachineHandler.super.save(data);
      SavedData stateMachinesData = new SavedData();
      this.stateMachines.forEach((id, asm) -> asm.save().ifPresent(smd -> stateMachinesData.putData(Integer.toString(id), smd)));
      data.putData("state_machines", stateMachinesData);
   }

   @Override
   public void load(SavedData data) {
      IStateMachineHandler.super.load(data);
      data.getData("state_machines").ifPresent(stateMachinesData -> {
         for (String key : stateMachinesData.keySet()) {
            int id = Integer.parseInt(key);
            stateMachinesData.getData(key).ifPresent(smd -> {
               StateMachineHandler.AnimationStateMachine asm = new StateMachineHandler.AnimationStateMachine(true);
               asm.load(smd);
               this.stateMachines.put(id, asm);
            });
         }
      });
   }

   public static StateMachineHandler create(ActiveModel model, SavedData data) {
      StateMachineHandler handler = new StateMachineHandler(model);
      handler.load(data);
      return handler;
   }

   static {
      ModelEngineAPI.getAPI().getConfigManager().registerReferenceUpdate(() -> DEFAULT_PRIORITY = ConfigProperty.DEFAULT_PRIORITY.getInt());
   }

   @RequiredArgsConstructor
   public class AnimationStateMachine extends StateMachine<BaseEntity<?>> implements DataIO {
      protected final boolean saved;
      protected StateNode<BaseEntity<?>> rootNode;
      @Getter @Setter
      @Nullable
      protected IAnimationProperty lastAnimation;
      @Getter @Setter
      @Nullable
      protected IAnimationProperty currentAnimation;
      @Nullable
      protected IAnimationProperty queuedAnimation;
      protected boolean queuedForce;

      @Override
      public StateNode<BaseEntity<?>> getCurrentNode() {
         return this.currentNode == null ? this.getRootNode() : this.currentNode;
      }

      public StateNode<BaseEntity<?>> getRootNode() {
         if (this.rootNode == null) {
            this.rootNode = new StateNode<>(this);
            this.rootNode.setEntryAction(baseEntity -> this.currentAnimation = null);
            this.rootNode.setExitAction(baseEntity -> {
               this.rootNode.clearForceConnectedNodes();
               this.rootNode.clearConnectedNodes();
               this.lastAnimation = this.currentAnimation;
            });
            this.setEntryNode(this.rootNode);
         }

         return this.rootNode;
      }

      public StateNode<BaseEntity<?>> createAnimationNode(Supplier<IAnimationProperty> propertySupplier) {
         StateNode<BaseEntity<?>> node = new StateNode<>(this);
         node.setEntryAction(baseEntity -> this.currentAnimation = propertySupplier.get());
         node.setAction(baseEntity -> {
            if (this.currentAnimation != null) {
               this.currentAnimation.update();
            }

            if (this.lastAnimation != null && this.lastAnimation != this.currentAnimation) {
               this.lastAnimation.update();
            }
         });
         node.setExitAction(baseEntity -> {
            AnimationEndEvent event = new AnimationEndEvent(StateMachineHandler.this.activeModel, this.currentAnimation);
            ModelEngineAPI.callEvent(event);
            if (this.currentAnimation instanceof SimpleProperty simpleProperty && simpleProperty.getOnEndTask() != null) {
               simpleProperty.getOnEndTask().run();
            }

            this.lastAnimation = this.currentAnimation;
         });
         return node;
      }

      private void materializeQueued() {
         if (this.queuedAnimation != null) {
            IAnimationProperty property = this.queuedAnimation;
            StateNode<BaseEntity<?>> currentNode = this.getCurrentNode();
            StateNode<BaseEntity<?>> node = this.createAnimationNode(() -> property);
            if (this.queuedForce) {
               currentNode.clearConnectedNodes();
               currentNode.clearForceConnectedNodes();
            }

            node.addConnectedNode(baseEntity -> property.isEnded(), this.getRootNode());
            currentNode.addForceConnectedNode(baseEntity -> true, node);
         }
      }

      public boolean hasFinishedPlaying(ModelState modelState) {
         return !this.isPlaying(StateMachineHandler.this.getDefaultProperty(modelState).getAnimation());
      }

      public boolean isPlaying(String animation) {
         return this.isPlaying(StateMachineHandler.this.blueprint, animation);
      }

      public boolean isPlaying(ModelBlueprint source, String animation) {
         if (this.currentAnimation != null
            && this.currentAnimation.getBlueprintAnimation().getModelBlueprint() == source
            && this.currentAnimation.getBlueprintAnimation().getName().equals(animation)
            && this.currentAnimation.getPhase() != IAnimationProperty.Phase.LERPOUT) {
            switch (this.currentAnimation.getLoopMode()) {
               case ONCE:
                  if (this.currentAnimation.getTime() < this.currentAnimation.getBlueprintAnimation().getLength()) {
                     return true;
                  }
                  break;
               case LOOP:
                  if (this.currentAnimation.getTime() < this.currentAnimation.getBlueprintAnimation().getLength() + 0.05) {
                     return true;
                  }
                  break;
               default:
                  return true;
            }
         }

         return false;
      }

      public boolean isQueued(ModelBlueprint source, String animation) {
         return this.queuedAnimation != null
            && this.queuedAnimation.getBlueprintAnimation().getModelBlueprint() == source
            && this.queuedAnimation.getBlueprintAnimation().getName().equals(animation);
      }

      @Nullable
      public IAnimationProperty getEffectiveAnimation() {
         return this.queuedAnimation != null ? this.queuedAnimation : this.currentAnimation;
      }

      public boolean isPlayingWithLog(ModelBlueprint source, String animation) {
         if (this.currentAnimation == null) {
            TLogger.log("Current Animation is null");
            return false;
         }

         if (this.currentAnimation.getBlueprintAnimation().getModelBlueprint() != source) {
            TLogger.log("Current Animation is not from source");
            return false;
         }

         if (!this.currentAnimation.getBlueprintAnimation().getName().equals(animation)) {
            TLogger.log("Current Animation name is not equal to animation");
            return false;
         }

         if (this.currentAnimation.getPhase() == IAnimationProperty.Phase.LERPOUT) {
            TLogger.log("Current Animation is in LERPOUT phase");
            return false;
         }

         return switch (this.currentAnimation.getLoopMode()) {
            case ONCE -> {
               TLogger.log("Once: " + (this.currentAnimation.getTime() < this.currentAnimation.getBlueprintAnimation().getLength()));
               yield this.currentAnimation.getTime() < this.currentAnimation.getBlueprintAnimation().getLength();
            }
            case LOOP -> {
               TLogger.log("Loop: " + (this.currentAnimation.getTime() < this.currentAnimation.getBlueprintAnimation().getLength() + 0.05));
               yield this.currentAnimation.getTime() < this.currentAnimation.getBlueprintAnimation().getLength() + 0.05;
            }
            default -> true;
         };
      }

      public boolean isPlayingOrEnding(String animation) {
         return this.isPlayingOrEnding(StateMachineHandler.this.blueprint, animation);
      }

      public boolean isPlayingOrEnding(ModelBlueprint source, String animation) {
         return this.currentAnimation != null
            && this.currentAnimation.getBlueprintAnimation().getModelBlueprint() == source
            && this.currentAnimation.getName().equals(animation)
            && !this.currentAnimation.isEnded();
      }

      public void forceReentry(BaseEntity<?> base) {
         this.getCurrentNode().acceptEntry(base);
      }

      @Override
      public void save(SavedData data) {
         if (this.saved) {
            if (this.lastAnimation != null) {
               this.lastAnimation.save().ifPresent(data1 -> data.putData("last_animation", data1));
            }

            if (this.currentAnimation != null) {
               this.currentAnimation.save().ifPresent(data1 -> data.putData("current_animation", data1));
            }
         }
      }

      @Override
      public void load(SavedData data) {
         AnimationPropertyRegistry registry = ModelEngineAPI.getAnimationPropertyRegistry();
         data.getData("last_animation").ifPresent(property -> this.lastAnimation = registry.createAnimationProperty(StateMachineHandler.this, property));
         data.getData("current_animation").ifPresent(property -> this.currentAnimation = registry.createAnimationProperty(StateMachineHandler.this, property));
         StateNode<BaseEntity<?>> currentNode = this.getCurrentNode();
         if (this.currentAnimation != null) {
            StateNode<BaseEntity<?>> node = this.createAnimationNode(() -> this.currentAnimation);
            node.addConnectedNode(baseEntity -> this.currentAnimation.isEnded(), this.getRootNode());
            currentNode.addForceConnectedNode(baseEntity -> true, node);
         }
      }
   }
}
