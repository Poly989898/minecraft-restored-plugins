package com.nexomc.nexo.mechanics.furniture.states;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0014J\u0010\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0014R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\u000f"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/states/ModelEngineAnimState;", "Lcom/nexomc/nexo/mechanics/furniture/states/ModelEngineState;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "animationState", "Lcom/nexomc/nexo/mechanics/furniture/states/AnimationState;", "getAnimationState", "()Lcom/nexomc/nexo/mechanics/furniture/states/AnimationState;", "doSetState", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "doUnsetState", "com.nexomc:core"}
)
public final class ModelEngineAnimState extends ModelEngineState {
   @NotNull
   private final AnimationState animationState;

   public ModelEngineAnimState(@NotNull ConfigurationSection section) {
      super(section);
      this.animationState = new AnimationState(section);
   }

   @NotNull
   public final AnimationState getAnimationState() {
      return this.animationState;
   }

   protected void doSetState(@NotNull ItemDisplay baseEntity) {
      super.doSetState(baseEntity);
      this.animationState.setState(baseEntity);
   }

   protected void doUnsetState(@NotNull ItemDisplay baseEntity) {
      super.doUnsetState(baseEntity);
      this.animationState.unsetState(baseEntity);
   }
}
