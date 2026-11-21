package com.ticxo.modelengine.api.model.operation;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.BukkitEntity;
import com.ticxo.modelengine.api.entity.BukkitPlayer;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.PivotOverride;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.Generated;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ModelOperation {
   protected final Entity bukkitEntity;
   protected final BaseEntity<?> baseEntity;
   protected final String modelId;
   protected final boolean overrideHitbox;
   protected final boolean baseInvisible;
   protected final Boolean renderFire;
   protected final Boolean forceFire;
   protected final boolean doDamageTint;
   protected final String nametagBone;
   protected final boolean drivable;
   protected final boolean rideable;
   protected final boolean lockPitch;
   protected final boolean lockYaw;
   protected final Double stepHeight;
   protected final Integer viewRadius;
   protected final float viewRange;
   protected final double scale;
   protected final Double hitboxScale;
   protected final boolean useStateMachine;
   protected final boolean invisibleUpdate;
   protected final boolean initializeRenderer;
   protected final boolean showHitbox;
   protected final boolean showShadow;
   protected final boolean syncBodyYaw;
   protected final boolean shouldSave;
   protected final String rootBone;
   protected final Consumer<ModeledEntity> postModeledEntityInit;
   protected final Consumer<ActiveModel> postActiveModelInit;
   protected final boolean useBaseAsPivot;
   protected final boolean cameraPivot;
   protected final PivotOverride pivotOverride;
   protected final boolean viewSelf;
   protected final Predicate<Player> playerPredicate;

   protected BaseEntity<?> getBaseEntity() {
      if (this.baseEntity != null) {
         return this.baseEntity;
      } else if (this.bukkitEntity != null) {
         return new BukkitEntity(this.bukkitEntity);
      } else {
         throw new RuntimeException("You must supply a base entity for this operation.");
      }
   }

   protected boolean isDisguise() {
      return this.bukkitEntity instanceof Player || this.baseEntity instanceof BukkitPlayer;
   }

   protected ModeledEntity getOrCreateModeledEntity() {
      if (this.baseEntity != null) {
         return ModelEngineAPI.getOrCreateModeledEntity(this.baseEntity.getUUID(), () -> this.baseEntity);
      } else if (this.bukkitEntity != null) {
         return ModelEngineAPI.getOrCreateModeledEntity(this.bukkitEntity);
      } else {
         throw new RuntimeException("You must supply a base entity for this operation.");
      }
   }

   @Generated
   private static boolean $default$overrideHitbox() {
      return true;
   }

   @Generated
   private static float $default$viewRange() {
      return 4096.0F;
   }

   @Generated
   private static double $default$scale() {
      return 1.0;
   }

   @Generated
   private static boolean $default$initializeRenderer() {
      return true;
   }

   @Generated
   private static boolean $default$showHitbox() {
      return true;
   }

   @Generated
   private static boolean $default$showShadow() {
      return true;
   }

   @Generated
   private static boolean $default$syncBodyYaw() {
      return true;
   }

   @Generated
   private static boolean $default$shouldSave() {
      return true;
   }

   @Generated
   private static boolean $default$viewSelf() {
      return true;
   }

   @Generated
   ModelOperation(
      Entity bukkitEntity,
      BaseEntity<?> baseEntity,
      String modelId,
      boolean overrideHitbox,
      boolean baseInvisible,
      Boolean renderFire,
      Boolean forceFire,
      boolean doDamageTint,
      String nametagBone,
      boolean drivable,
      boolean rideable,
      boolean lockPitch,
      boolean lockYaw,
      Double stepHeight,
      Integer viewRadius,
      float viewRange,
      double scale,
      Double hitboxScale,
      boolean useStateMachine,
      boolean invisibleUpdate,
      boolean initializeRenderer,
      boolean showHitbox,
      boolean showShadow,
      boolean syncBodyYaw,
      boolean shouldSave,
      String rootBone,
      Consumer<ModeledEntity> postModeledEntityInit,
      Consumer<ActiveModel> postActiveModelInit,
      boolean useBaseAsPivot,
      boolean cameraPivot,
      PivotOverride pivotOverride,
      boolean viewSelf,
      Predicate<Player> playerPredicate
   ) {
      this.bukkitEntity = bukkitEntity;
      this.baseEntity = baseEntity;
      this.modelId = modelId;
      this.overrideHitbox = overrideHitbox;
      this.baseInvisible = baseInvisible;
      this.renderFire = renderFire;
      this.forceFire = forceFire;
      this.doDamageTint = doDamageTint;
      this.nametagBone = nametagBone;
      this.drivable = drivable;
      this.rideable = rideable;
      this.lockPitch = lockPitch;
      this.lockYaw = lockYaw;
      this.stepHeight = stepHeight;
      this.viewRadius = viewRadius;
      this.viewRange = viewRange;
      this.scale = scale;
      this.hitboxScale = hitboxScale;
      this.useStateMachine = useStateMachine;
      this.invisibleUpdate = invisibleUpdate;
      this.initializeRenderer = initializeRenderer;
      this.showHitbox = showHitbox;
      this.showShadow = showShadow;
      this.syncBodyYaw = syncBodyYaw;
      this.shouldSave = shouldSave;
      this.rootBone = rootBone;
      this.postModeledEntityInit = postModeledEntityInit;
      this.postActiveModelInit = postActiveModelInit;
      this.useBaseAsPivot = useBaseAsPivot;
      this.cameraPivot = cameraPivot;
      this.pivotOverride = pivotOverride;
      this.viewSelf = viewSelf;
      this.playerPredicate = playerPredicate;
   }

   @Generated
   public static ModelOperation.Builder builder() {
      return new ModelOperation.Builder();
   }

   public static class Builder {
      @Generated
      private Entity bukkitEntity;
      @Generated
      private BaseEntity<?> baseEntity;
      @Generated
      private String modelId;
      @Generated
      private boolean overrideHitbox$set;
      @Generated
      private boolean overrideHitbox$value;
      @Generated
      private boolean baseInvisible;
      @Generated
      private Boolean renderFire;
      @Generated
      private Boolean forceFire;
      @Generated
      private boolean doDamageTint;
      @Generated
      private String nametagBone;
      @Generated
      private boolean drivable;
      @Generated
      private boolean rideable;
      @Generated
      private boolean lockPitch;
      @Generated
      private boolean lockYaw;
      @Generated
      private Double stepHeight;
      @Generated
      private Integer viewRadius;
      @Generated
      private boolean viewRange$set;
      @Generated
      private float viewRange$value;
      @Generated
      private boolean scale$set;
      @Generated
      private double scale$value;
      @Generated
      private Double hitboxScale;
      @Generated
      private boolean useStateMachine;
      @Generated
      private boolean invisibleUpdate;
      @Generated
      private boolean initializeRenderer$set;
      @Generated
      private boolean initializeRenderer$value;
      @Generated
      private boolean showHitbox$set;
      @Generated
      private boolean showHitbox$value;
      @Generated
      private boolean showShadow$set;
      @Generated
      private boolean showShadow$value;
      @Generated
      private boolean syncBodyYaw$set;
      @Generated
      private boolean syncBodyYaw$value;
      @Generated
      private boolean shouldSave$set;
      @Generated
      private boolean shouldSave$value;
      @Generated
      private String rootBone;
      @Generated
      private Consumer<ModeledEntity> postModeledEntityInit;
      @Generated
      private Consumer<ActiveModel> postActiveModelInit;
      @Generated
      private boolean useBaseAsPivot;
      @Generated
      private boolean cameraPivot;
      @Generated
      private PivotOverride pivotOverride;
      @Generated
      private boolean viewSelf$set;
      @Generated
      private boolean viewSelf$value;
      @Generated
      private Predicate<Player> playerPredicate;

      public OperationResult operate(OperationType type) {
         return type.process(this.build());
      }

      @Generated
      Builder() {
      }

      @Generated
      public ModelOperation.Builder bukkitEntity(Entity bukkitEntity) {
         this.bukkitEntity = bukkitEntity;
         return this;
      }

      @Generated
      public ModelOperation.Builder baseEntity(BaseEntity<?> baseEntity) {
         this.baseEntity = baseEntity;
         return this;
      }

      @Generated
      public ModelOperation.Builder modelId(String modelId) {
         this.modelId = modelId;
         return this;
      }

      @Generated
      public ModelOperation.Builder overrideHitbox(boolean overrideHitbox) {
         this.overrideHitbox$value = overrideHitbox;
         this.overrideHitbox$set = true;
         return this;
      }

      @Generated
      public ModelOperation.Builder baseInvisible(boolean baseInvisible) {
         this.baseInvisible = baseInvisible;
         return this;
      }

      @Generated
      public ModelOperation.Builder renderFire(Boolean renderFire) {
         this.renderFire = renderFire;
         return this;
      }

      @Generated
      public ModelOperation.Builder forceFire(Boolean forceFire) {
         this.forceFire = forceFire;
         return this;
      }

      @Generated
      public ModelOperation.Builder doDamageTint(boolean doDamageTint) {
         this.doDamageTint = doDamageTint;
         return this;
      }

      @Generated
      public ModelOperation.Builder nametagBone(String nametagBone) {
         this.nametagBone = nametagBone;
         return this;
      }

      @Generated
      public ModelOperation.Builder drivable(boolean drivable) {
         this.drivable = drivable;
         return this;
      }

      @Generated
      public ModelOperation.Builder rideable(boolean rideable) {
         this.rideable = rideable;
         return this;
      }

      @Generated
      public ModelOperation.Builder lockPitch(boolean lockPitch) {
         this.lockPitch = lockPitch;
         return this;
      }

      @Generated
      public ModelOperation.Builder lockYaw(boolean lockYaw) {
         this.lockYaw = lockYaw;
         return this;
      }

      @Generated
      public ModelOperation.Builder stepHeight(Double stepHeight) {
         this.stepHeight = stepHeight;
         return this;
      }

      @Generated
      public ModelOperation.Builder viewRadius(Integer viewRadius) {
         this.viewRadius = viewRadius;
         return this;
      }

      @Generated
      public ModelOperation.Builder viewRange(float viewRange) {
         this.viewRange$value = viewRange;
         this.viewRange$set = true;
         return this;
      }

      @Generated
      public ModelOperation.Builder scale(double scale) {
         this.scale$value = scale;
         this.scale$set = true;
         return this;
      }

      @Generated
      public ModelOperation.Builder hitboxScale(Double hitboxScale) {
         this.hitboxScale = hitboxScale;
         return this;
      }

      @Generated
      public ModelOperation.Builder useStateMachine(boolean useStateMachine) {
         this.useStateMachine = useStateMachine;
         return this;
      }

      @Generated
      public ModelOperation.Builder invisibleUpdate(boolean invisibleUpdate) {
         this.invisibleUpdate = invisibleUpdate;
         return this;
      }

      @Generated
      public ModelOperation.Builder initializeRenderer(boolean initializeRenderer) {
         this.initializeRenderer$value = initializeRenderer;
         this.initializeRenderer$set = true;
         return this;
      }

      @Generated
      public ModelOperation.Builder showHitbox(boolean showHitbox) {
         this.showHitbox$value = showHitbox;
         this.showHitbox$set = true;
         return this;
      }

      @Generated
      public ModelOperation.Builder showShadow(boolean showShadow) {
         this.showShadow$value = showShadow;
         this.showShadow$set = true;
         return this;
      }

      @Generated
      public ModelOperation.Builder syncBodyYaw(boolean syncBodyYaw) {
         this.syncBodyYaw$value = syncBodyYaw;
         this.syncBodyYaw$set = true;
         return this;
      }

      @Generated
      public ModelOperation.Builder shouldSave(boolean shouldSave) {
         this.shouldSave$value = shouldSave;
         this.shouldSave$set = true;
         return this;
      }

      @Generated
      public ModelOperation.Builder rootBone(String rootBone) {
         this.rootBone = rootBone;
         return this;
      }

      @Generated
      public ModelOperation.Builder postModeledEntityInit(Consumer<ModeledEntity> postModeledEntityInit) {
         this.postModeledEntityInit = postModeledEntityInit;
         return this;
      }

      @Generated
      public ModelOperation.Builder postActiveModelInit(Consumer<ActiveModel> postActiveModelInit) {
         this.postActiveModelInit = postActiveModelInit;
         return this;
      }

      @Generated
      public ModelOperation.Builder useBaseAsPivot(boolean useBaseAsPivot) {
         this.useBaseAsPivot = useBaseAsPivot;
         return this;
      }

      @Generated
      public ModelOperation.Builder cameraPivot(boolean cameraPivot) {
         this.cameraPivot = cameraPivot;
         return this;
      }

      @Generated
      public ModelOperation.Builder pivotOverride(PivotOverride pivotOverride) {
         this.pivotOverride = pivotOverride;
         return this;
      }

      @Generated
      public ModelOperation.Builder viewSelf(boolean viewSelf) {
         this.viewSelf$value = viewSelf;
         this.viewSelf$set = true;
         return this;
      }

      @Generated
      public ModelOperation.Builder playerPredicate(Predicate<Player> playerPredicate) {
         this.playerPredicate = playerPredicate;
         return this;
      }

      @Generated
      public ModelOperation build() {
         boolean overrideHitbox$value = this.overrideHitbox$value;
         if (!this.overrideHitbox$set) {
            overrideHitbox$value = ModelOperation.$default$overrideHitbox();
         }

         float viewRange$value = this.viewRange$value;
         if (!this.viewRange$set) {
            viewRange$value = ModelOperation.$default$viewRange();
         }

         double scale$value = this.scale$value;
         if (!this.scale$set) {
            scale$value = ModelOperation.$default$scale();
         }

         boolean initializeRenderer$value = this.initializeRenderer$value;
         if (!this.initializeRenderer$set) {
            initializeRenderer$value = ModelOperation.$default$initializeRenderer();
         }

         boolean showHitbox$value = this.showHitbox$value;
         if (!this.showHitbox$set) {
            showHitbox$value = ModelOperation.$default$showHitbox();
         }

         boolean showShadow$value = this.showShadow$value;
         if (!this.showShadow$set) {
            showShadow$value = ModelOperation.$default$showShadow();
         }

         boolean syncBodyYaw$value = this.syncBodyYaw$value;
         if (!this.syncBodyYaw$set) {
            syncBodyYaw$value = ModelOperation.$default$syncBodyYaw();
         }

         boolean shouldSave$value = this.shouldSave$value;
         if (!this.shouldSave$set) {
            shouldSave$value = ModelOperation.$default$shouldSave();
         }

         boolean viewSelf$value = this.viewSelf$value;
         if (!this.viewSelf$set) {
            viewSelf$value = ModelOperation.$default$viewSelf();
         }

         return new ModelOperation(
            this.bukkitEntity,
            this.baseEntity,
            this.modelId,
            overrideHitbox$value,
            this.baseInvisible,
            this.renderFire,
            this.forceFire,
            this.doDamageTint,
            this.nametagBone,
            this.drivable,
            this.rideable,
            this.lockPitch,
            this.lockYaw,
            this.stepHeight,
            this.viewRadius,
            viewRange$value,
            scale$value,
            this.hitboxScale,
            this.useStateMachine,
            this.invisibleUpdate,
            initializeRenderer$value,
            showHitbox$value,
            showShadow$value,
            syncBodyYaw$value,
            shouldSave$value,
            this.rootBone,
            this.postModeledEntityInit,
            this.postActiveModelInit,
            this.useBaseAsPivot,
            this.cameraPivot,
            this.pivotOverride,
            viewSelf$value,
            this.playerPredicate
         );
      }

      @Generated
      @Override
      public String toString() {
         return "ModelOperation.Builder(bukkitEntity="
            + this.bukkitEntity
            + ", baseEntity="
            + this.baseEntity
            + ", modelId="
            + this.modelId
            + ", overrideHitbox$value="
            + this.overrideHitbox$value
            + ", baseInvisible="
            + this.baseInvisible
            + ", renderFire="
            + this.renderFire
            + ", forceFire="
            + this.forceFire
            + ", doDamageTint="
            + this.doDamageTint
            + ", nametagBone="
            + this.nametagBone
            + ", drivable="
            + this.drivable
            + ", rideable="
            + this.rideable
            + ", lockPitch="
            + this.lockPitch
            + ", lockYaw="
            + this.lockYaw
            + ", stepHeight="
            + this.stepHeight
            + ", viewRadius="
            + this.viewRadius
            + ", viewRange$value="
            + this.viewRange$value
            + ", scale$value="
            + this.scale$value
            + ", hitboxScale="
            + this.hitboxScale
            + ", useStateMachine="
            + this.useStateMachine
            + ", invisibleUpdate="
            + this.invisibleUpdate
            + ", initializeRenderer$value="
            + this.initializeRenderer$value
            + ", showHitbox$value="
            + this.showHitbox$value
            + ", showShadow$value="
            + this.showShadow$value
            + ", syncBodyYaw$value="
            + this.syncBodyYaw$value
            + ", shouldSave$value="
            + this.shouldSave$value
            + ", rootBone="
            + this.rootBone
            + ", postModeledEntityInit="
            + this.postModeledEntityInit
            + ", postActiveModelInit="
            + this.postActiveModelInit
            + ", useBaseAsPivot="
            + this.useBaseAsPivot
            + ", cameraPivot="
            + this.cameraPivot
            + ", pivotOverride="
            + this.pivotOverride
            + ", viewSelf$value="
            + this.viewSelf$value
            + ", playerPredicate="
            + this.playerPredicate
            + ")";
      }
   }
}
