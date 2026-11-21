package com.nexomc.nexo.mechanics.furniture.states;

import kotlin.time.Duration;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.utils.DurationUtilsKt;
import com.nexomc.nexo.utils.PluginUtils;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import java.util.Optional;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.optionals.OptionalsKt;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001BA\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f¢\u0006\u0004\b\r\u0010\u000eJ\u0010\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0014J\u0010\u0010\u001d\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0014R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0013\u0010\u0006\u001a\u00020\u0007¢\u0006\n\n\u0002\u0010\u0013\u001a\u0004\b\u0011\u0010\u0012R\u0013\u0010\b\u001a\u00020\u0007¢\u0006\n\n\u0002\u0010\u0013\u001a\u0004\b\u0014\u0010\u0012R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018¨\u0006\u001e"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/states/AnimationState;", "Lcom/nexomc/nexo/mechanics/furniture/states/FurnitureState;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "animation", "", "lerpIn", "Lkotlin/time/Duration;", "lerpOut", "speed", "", "force", "", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;Ljava/lang/String;JJDZLkotlin/jvm/internal/DefaultConstructorMarker;)V", "getAnimation", "()Ljava/lang/String;", "getLerpIn-UwyO8pc", "()J", "J", "getLerpOut-UwyO8pc", "getSpeed", "()D", "getForce", "()Z", "doUnsetState", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "doSetState", "com.nexomc:core"}
)
public final class AnimationState extends FurnitureState {
   @NotNull
   private final String animation;
   private final long lerpIn;
   private final long lerpOut;
   private final double speed;
   private final boolean force;

   public AnimationState(@NotNull ConfigurationSection section) {
      this(section, NexoYamlKt.stringOrEmpty(section, "animation"), durationOrZero(section, "lerp_in"), durationOrZero(section, "lerp_out"), section.getDouble("speed", 1.0), section.getBoolean("force", true));
   }

   private static long durationOrZero(ConfigurationSection section, String key) {
      Duration duration = NexoYamlKt.durationOrNull(section, key, null);
      return duration != null ? duration.unbox_impl() : Duration.Companion.getZERO_UwyO8pc();
   }

   private AnimationState(ConfigurationSection section, String animation, long lerpIn, long lerpOut, double speed, boolean force) {
      super(section);
      this.animation = animation;
      this.lerpIn = lerpIn;
      this.lerpOut = lerpOut;
      this.speed = speed;
      this.force = force;
      if (!PluginUtils.INSTANCE.isModelEngineEnabled()) {
         int value = 0;
         String str = this.getType() + " is only allowed when ModelEngine is on the server";
         throw new IllegalArgumentException(str.toString());
      }
   }

   @NotNull
   public final String getAnimation() {
      return this.animation;
   }

   public final long getLerpIn_UwyO8pc/* $FF was: getLerpIn-UwyO8pc*/() {
      return this.lerpIn;
   }

   public final long getLerpOut_UwyO8pc/* $FF was: getLerpOut-UwyO8pc*/() {
      return this.lerpOut;
   }

   public final double getSpeed() {
      return this.speed;
   }

   public final boolean getForce() {
      return this.force;
   }

   protected void doUnsetState(@NotNull ItemDisplay baseEntity) {
      FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
      if (furnitureMechanic != null) {
         FurnitureMechanic it = furnitureMechanic;
         int value = 0;
         furnitureMechanic = it.isModelEngine() ? it : null;
         if (furnitureMechanic != null) {
            FurnitureMechanic mechanic = furnitureMechanic;
            ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity((Entity)baseEntity);
            if (modeledEntity == null) {
               return;
            }

            ModeledEntity modelEntity = modeledEntity;
            ActiveModel activeModel2 = (ActiveModel)CollectionsKt.singleOrNull((Iterable)modelEntity.getModels().values());
            if (activeModel2 == null) {
               String modelEngineID = mechanic.getModelEngineID();
               Optional model = modelEntity.getModel(modelEngineID);
               activeModel2 = model != null ? (ActiveModel)OptionalsKt.getOrNull(model) : null;
               if (activeModel2 == null) {
                  return;
               }
            }

            ActiveModel activeModel = activeModel2;
            activeModel.getAnimationHandler().stopAnimation(this.animation);
            return;
         }
      }

   }

   protected void doSetState(@NotNull ItemDisplay baseEntity) {
      FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic((Entity)baseEntity);
      if (furnitureMechanic != null) {
         FurnitureMechanic it = furnitureMechanic;
         int value = 0;
         furnitureMechanic = it.isModelEngine() ? it : null;
         if (furnitureMechanic != null) {
            FurnitureMechanic mechanic = furnitureMechanic;
            ModeledEntity modeledEntity = ModelEngineAPI.getModeledEntity((Entity)baseEntity);
            if (modeledEntity == null) {
               return;
            }

            ModeledEntity modelEntity = modeledEntity;
            ActiveModel activeModel2 = (ActiveModel)CollectionsKt.singleOrNull((Iterable)modelEntity.getModels().values());
            if (activeModel2 == null) {
               String modelEngineID = mechanic.getModelEngineID();
               Optional model = modelEntity.getModel(modelEngineID);
               activeModel2 = model != null ? (ActiveModel)OptionalsKt.getOrNull(model) : null;
               if (activeModel2 == null) {
                  return;
               }
            }

            ActiveModel activeModel = activeModel2;
            activeModel.getAnimationHandler().playAnimation(this.animation, (double)DurationUtilsKt.getTicks_LRDsOJo(this.lerpIn) * (double)20.0F, (double)DurationUtilsKt.getTicks_LRDsOJo(this.lerpOut) * (double)20.0F, this.speed, this.force);
            return;
         }
      }

   }
}
