package io.lumine.mythic.bukkit.compatibility;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.bukkit.utils.serialize.Orient;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.core.mobs.model.MobModel;
import java.util.UUID;
import lombok.Generated;
import org.bukkit.Material;
import org.bukkit.util.BoundingBox;

public abstract class AbstractModelEngineSupport extends ReloadableModule<MythicBukkit> {
   public AbstractModelEngineSupport(MythicBukkit plugin) {
      super(plugin);
   }

   public abstract boolean isSubHitbox(UUID uuid);

   public abstract boolean isBoundToSubHitbox(UUID uuid, UUID uuid2);

   public String getSubHitboxBoneId(UUID entity) {
      return null;
   }

   public abstract UUID getParentUUID(UUID uuid);

   public abstract AbstractEntity getParent(AbstractEntity abstractEntity);

   public abstract boolean overlapsOOBB(BoundingBox boundingBox, AbstractEntity abstractEntity);

   public abstract AbstractModelEngineSupport.ModelConfig getBoneModel(String str, String str2) throws IllegalArgumentException;

   public abstract MobModel createMobModel(MythicMob mythicMob, MythicConfig mythicConfig);

   public MobModel createMobModel(AbstractEntity entity, String modelId) {
      return null;
   }

   public AbstractModelEngineSupport.MEGProjectile createMEGBullet(AbstractModelEngineSupport.MEGProjectileData modelData, AbstractLocation location) {
      Log.error("MEG Bullets unsupported, update ModelEngine");
      return null;
   }

   public AbstractModelEngineSupport.MEGAttachment createMEGAttachment(AbstractModelEngineSupport.MEGAttachmentData model, AbstractEntity entity) {
      Log.error("MEG Aura Attachments unsupported, update ModelEngine");
      return null;
   }

   public abstract void queuePostModelRegistration(Runnable runnable);

   public double distanceSquaredToSubHitbox(AbstractLocation origin, AbstractEntity entity) {
      BoundingBox box = entity.getBukkitEntity().getBoundingBox();
      double x = Math.max(Math.abs(origin.getX() - box.getCenterX()) - box.getWidthX() * 0.5, 0.0);
      double y = Math.max(Math.abs(origin.getY() - box.getCenterY()) - box.getHeight() * 0.5, 0.0);
      double z = Math.max(Math.abs(origin.getZ() - box.getCenterZ()) - box.getWidthZ() * 0.5, 0.0);
      return x * x + y * y + z * z;
   }

   public interface MEGAttachment extends Terminable {
      UUID getEntityId();

      default void disableCulling() {
      }

      default void setRenderRadius(int radius) {
      }

      default void modelState(String state, boolean remove) {
      }
   }

   public static enum MEGAttachmentAnchor {
      HEAD,
      BODY;
   }

   public static class MEGAttachmentData {
      private String modelId;
      private Orient offset;
      private double scale;
      private String color;
      private boolean enchanted;
      private boolean glowing;
      private String glowColor;
      private String state = null;
      private AbstractModelEngineSupport.MEGAttachmentAnchor anchor = AbstractModelEngineSupport.MEGAttachmentAnchor.BODY;
      private Integer brightness;
      private boolean useALOD;

      @Generated
      public String getModelId() {
         return this.modelId;
      }

      @Generated
      public Orient getOffset() {
         return this.offset;
      }

      @Generated
      public double getScale() {
         return this.scale;
      }

      @Generated
      public String getColor() {
         return this.color;
      }

      @Generated
      public boolean isEnchanted() {
         return this.enchanted;
      }

      @Generated
      public boolean isGlowing() {
         return this.glowing;
      }

      @Generated
      public String getGlowColor() {
         return this.glowColor;
      }

      @Generated
      public String getState() {
         return this.state;
      }

      @Generated
      public AbstractModelEngineSupport.MEGAttachmentAnchor getAnchor() {
         return this.anchor;
      }

      @Generated
      public Integer getBrightness() {
         return this.brightness;
      }

      @Generated
      public boolean isUseALOD() {
         return this.useALOD;
      }

      @Generated
      public void setModelId(String modelId) {
         this.modelId = modelId;
      }

      @Generated
      public void setOffset(Orient offset) {
         this.offset = offset;
      }

      @Generated
      public void setScale(double scale) {
         this.scale = scale;
      }

      @Generated
      public void setColor(String color) {
         this.color = color;
      }

      @Generated
      public void setEnchanted(boolean enchanted) {
         this.enchanted = enchanted;
      }

      @Generated
      public void setGlowing(boolean glowing) {
         this.glowing = glowing;
      }

      @Generated
      public void setGlowColor(String glowColor) {
         this.glowColor = glowColor;
      }

      @Generated
      public void setState(String state) {
         this.state = state;
      }

      @Generated
      public void setAnchor(AbstractModelEngineSupport.MEGAttachmentAnchor anchor) {
         this.anchor = anchor;
      }

      @Generated
      public void setBrightness(Integer brightness) {
         this.brightness = brightness;
      }

      @Generated
      public void setUseALOD(boolean useALOD) {
         this.useALOD = useALOD;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof AbstractModelEngineSupport.MEGAttachmentData other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (Double.compare(this.getScale(), other.getScale()) != 0) {
            return false;
         } else if (this.isEnchanted() != other.isEnchanted()) {
            return false;
         } else if (this.isGlowing() != other.isGlowing()) {
            return false;
         } else if (this.isUseALOD() != other.isUseALOD()) {
            return false;
         } else {
            Object this$brightness = this.getBrightness();
            Object other$brightness = other.getBrightness();
            if (this$brightness == null ? other$brightness == null : this$brightness.equals(other$brightness)) {
               Object this$modelId = this.getModelId();
               Object other$modelId = other.getModelId();
               if (this$modelId == null ? other$modelId == null : this$modelId.equals(other$modelId)) {
                  Object this$offset = this.getOffset();
                  Object other$offset = other.getOffset();
                  if (this$offset == null ? other$offset == null : this$offset.equals(other$offset)) {
                     Object this$color = this.getColor();
                     Object other$color = other.getColor();
                     if (this$color == null ? other$color == null : this$color.equals(other$color)) {
                        Object this$glowColor = this.getGlowColor();
                        Object other$glowColor = other.getGlowColor();
                        if (this$glowColor == null ? other$glowColor == null : this$glowColor.equals(other$glowColor)) {
                           Object this$state = this.getState();
                           Object other$state = other.getState();
                           if (this$state == null ? other$state == null : this$state.equals(other$state)) {
                              Object this$anchor = this.getAnchor();
                              Object other$anchor = other.getAnchor();
                              return this$anchor == null ? other$anchor == null : this$anchor.equals(other$anchor);
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof AbstractModelEngineSupport.MEGAttachmentData;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         long $scale = Double.doubleToLongBits(this.getScale());
         result = result * 59 + (int)($scale >>> 32 ^ $scale);
         result = result * 59 + (this.isEnchanted() ? 79 : 97);
         result = result * 59 + (this.isGlowing() ? 79 : 97);
         result = result * 59 + (this.isUseALOD() ? 79 : 97);
         Object $brightness = this.getBrightness();
         result = result * 59 + ($brightness == null ? 43 : $brightness.hashCode());
         Object $modelId = this.getModelId();
         result = result * 59 + ($modelId == null ? 43 : $modelId.hashCode());
         Object $offset = this.getOffset();
         result = result * 59 + ($offset == null ? 43 : $offset.hashCode());
         Object $color = this.getColor();
         result = result * 59 + ($color == null ? 43 : $color.hashCode());
         Object $glowColor = this.getGlowColor();
         result = result * 59 + ($glowColor == null ? 43 : $glowColor.hashCode());
         Object $state = this.getState();
         result = result * 59 + ($state == null ? 43 : $state.hashCode());
         Object $anchor = this.getAnchor();
         return result * 59 + ($anchor == null ? 43 : $anchor.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "AbstractModelEngineSupport.MEGAttachmentData(modelId="
            + this.getModelId()
            + ", offset="
            + this.getOffset()
            + ", scale="
            + this.getScale()
            + ", color="
            + this.getColor()
            + ", enchanted="
            + this.isEnchanted()
            + ", glowing="
            + this.isGlowing()
            + ", glowColor="
            + this.getGlowColor()
            + ", state="
            + this.getState()
            + ", anchor="
            + this.getAnchor()
            + ", brightness="
            + this.getBrightness()
            + ", useALOD="
            + this.isUseALOD()
            + ")";
      }

      @Generated
      public MEGAttachmentData(
         String modelId,
         Orient offset,
         double scale,
         String color,
         boolean enchanted,
         boolean glowing,
         String glowColor,
         String state,
         AbstractModelEngineSupport.MEGAttachmentAnchor anchor,
         Integer brightness,
         boolean useALOD
      ) {
         this.modelId = modelId;
         this.offset = offset;
         this.scale = scale;
         this.color = color;
         this.enchanted = enchanted;
         this.glowing = glowing;
         this.glowColor = glowColor;
         this.state = state;
         this.anchor = anchor;
         this.brightness = brightness;
         this.useALOD = useALOD;
      }
   }

   public interface MEGProjectile extends Terminable {
      UUID getEntityId();

      void setLocation(AbstractLocation abstractLocation);

      void setDirection(AbstractVector abstractVector);

      default void disableCulling() {
      }

      default void setRenderRadius(int radius) {
      }

      default void modelState(String state, boolean remove) {
      }
   }

   public static class MEGProjectileData {
      private String modelId;
      private double scale;
      private String color;
      private boolean enchanted;
      private boolean glowing;
      private String glowColor;
      private String state = null;
      private Integer brightness;
      private boolean useALOD;

      @Generated
      public String getModelId() {
         return this.modelId;
      }

      @Generated
      public double getScale() {
         return this.scale;
      }

      @Generated
      public String getColor() {
         return this.color;
      }

      @Generated
      public boolean isEnchanted() {
         return this.enchanted;
      }

      @Generated
      public boolean isGlowing() {
         return this.glowing;
      }

      @Generated
      public String getGlowColor() {
         return this.glowColor;
      }

      @Generated
      public String getState() {
         return this.state;
      }

      @Generated
      public Integer getBrightness() {
         return this.brightness;
      }

      @Generated
      public boolean isUseALOD() {
         return this.useALOD;
      }

      @Generated
      public void setModelId(String modelId) {
         this.modelId = modelId;
      }

      @Generated
      public void setScale(double scale) {
         this.scale = scale;
      }

      @Generated
      public void setColor(String color) {
         this.color = color;
      }

      @Generated
      public void setEnchanted(boolean enchanted) {
         this.enchanted = enchanted;
      }

      @Generated
      public void setGlowing(boolean glowing) {
         this.glowing = glowing;
      }

      @Generated
      public void setGlowColor(String glowColor) {
         this.glowColor = glowColor;
      }

      @Generated
      public void setState(String state) {
         this.state = state;
      }

      @Generated
      public void setBrightness(Integer brightness) {
         this.brightness = brightness;
      }

      @Generated
      public void setUseALOD(boolean useALOD) {
         this.useALOD = useALOD;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof AbstractModelEngineSupport.MEGProjectileData other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (Double.compare(this.getScale(), other.getScale()) != 0) {
            return false;
         } else if (this.isEnchanted() != other.isEnchanted()) {
            return false;
         } else if (this.isGlowing() != other.isGlowing()) {
            return false;
         } else if (this.isUseALOD() != other.isUseALOD()) {
            return false;
         } else {
            Object this$brightness = this.getBrightness();
            Object other$brightness = other.getBrightness();
            if (this$brightness == null ? other$brightness == null : this$brightness.equals(other$brightness)) {
               Object this$modelId = this.getModelId();
               Object other$modelId = other.getModelId();
               if (this$modelId == null ? other$modelId == null : this$modelId.equals(other$modelId)) {
                  Object this$color = this.getColor();
                  Object other$color = other.getColor();
                  if (this$color == null ? other$color == null : this$color.equals(other$color)) {
                     Object this$glowColor = this.getGlowColor();
                     Object other$glowColor = other.getGlowColor();
                     if (this$glowColor == null ? other$glowColor == null : this$glowColor.equals(other$glowColor)) {
                        Object this$state = this.getState();
                        Object other$state = other.getState();
                        return this$state == null ? other$state == null : this$state.equals(other$state);
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof AbstractModelEngineSupport.MEGProjectileData;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         long $scale = Double.doubleToLongBits(this.getScale());
         result = result * 59 + (int)($scale >>> 32 ^ $scale);
         result = result * 59 + (this.isEnchanted() ? 79 : 97);
         result = result * 59 + (this.isGlowing() ? 79 : 97);
         result = result * 59 + (this.isUseALOD() ? 79 : 97);
         Object $brightness = this.getBrightness();
         result = result * 59 + ($brightness == null ? 43 : $brightness.hashCode());
         Object $modelId = this.getModelId();
         result = result * 59 + ($modelId == null ? 43 : $modelId.hashCode());
         Object $color = this.getColor();
         result = result * 59 + ($color == null ? 43 : $color.hashCode());
         Object $glowColor = this.getGlowColor();
         result = result * 59 + ($glowColor == null ? 43 : $glowColor.hashCode());
         Object $state = this.getState();
         return result * 59 + ($state == null ? 43 : $state.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "AbstractModelEngineSupport.MEGProjectileData(modelId="
            + this.getModelId()
            + ", scale="
            + this.getScale()
            + ", color="
            + this.getColor()
            + ", enchanted="
            + this.isEnchanted()
            + ", glowing="
            + this.isGlowing()
            + ", glowColor="
            + this.getGlowColor()
            + ", state="
            + this.getState()
            + ", brightness="
            + this.getBrightness()
            + ", useALOD="
            + this.isUseALOD()
            + ")";
      }

      @Generated
      public MEGProjectileData(
         String modelId, double scale, String color, boolean enchanted, boolean glowing, String glowColor, String state, Integer brightness, boolean useALOD
      ) {
         this.modelId = modelId;
         this.scale = scale;
         this.color = color;
         this.enchanted = enchanted;
         this.glowing = glowing;
         this.glowColor = glowColor;
         this.state = state;
         this.brightness = brightness;
         this.useALOD = useALOD;
      }
   }

   public static class ModelConfig {
      private final int id;
      private final Material material;
      private final boolean isSmall;

      @Generated
      public ModelConfig(int id, Material material, boolean isSmall) {
         this.id = id;
         this.material = material;
         this.isSmall = isSmall;
      }

      @Generated
      public int getId() {
         return this.id;
      }

      @Generated
      public Material getMaterial() {
         return this.material;
      }

      @Generated
      public boolean isSmall() {
         return this.isSmall;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof AbstractModelEngineSupport.ModelConfig other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else if (this.getId() != other.getId()) {
            return false;
         } else if (this.isSmall() != other.isSmall()) {
            return false;
         } else {
            Object this$material = this.getMaterial();
            Object other$material = other.getMaterial();
            return this$material == null ? other$material == null : this$material.equals(other$material);
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof AbstractModelEngineSupport.ModelConfig;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + this.getId();
         result = result * 59 + (this.isSmall() ? 79 : 97);
         Object $material = this.getMaterial();
         return result * 59 + ($material == null ? 43 : $material.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "AbstractModelEngineSupport.ModelConfig(id=" + this.getId() + ", material=" + this.getMaterial() + ", isSmall=" + this.isSmall() + ")";
      }
   }
}
