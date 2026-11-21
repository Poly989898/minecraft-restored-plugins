package io.lumine.mythic.api.volatilecode.virtual;

import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractBlock;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.bukkit.utils.cache.DataTracker;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Getter
public class PacketBlockDisplay<T extends PacketEntityRenderer & PacketBlockDisplay.PacketBlockDisplayEntityRenderer> extends PacketEntity {
   private DataTracker<Integer> interpolationDelay = DataTracker.of(0);
   private DataTracker<Integer> interpolationDuration = DataTracker.of(1);
   private DataTracker<Float> viewRange = DataTracker.of(4096.0F);
   private DataTracker<Float> height = DataTracker.of(4096.0F);
   private DataTracker<Float> width = DataTracker.of(4096.0F);
   private DataTracker<Billboard> billboard = DataTracker.of(Billboard.FIXED);
   private DataTracker<Integer> brightness = DataTracker.of(-1, false);
   private DataTracker<Vector3f> scale = DataTracker.of(new Vector3f(1.0F, 1.0F, 1.0F));
   private DataTracker<Vector3f> translation = DataTracker.of(new Vector3f(0.0F, 0.0F, 0.0F));
   private DataTracker<Quaternionf> rotationLeft = DataTracker.of(new Quaternionf(), false);
   private DataTracker<Quaternionf> rotationRight = DataTracker.of(new Quaternionf(), false);
   private DataTracker<BlockData> blockData = DataTracker.of(Material.STONE.createBlockData());
   private static final float DEG2RAD = (float) (Math.PI / 180.0);

   public static PacketBlockDisplay.PacketBlockDisplayBuilder create() {
      return new PacketBlockDisplay.PacketBlockDisplayBuilder();
   }

   public PacketBlockDisplay(AbstractLocation location) {
      this(MythicProvider.get(), location);
   }

   public PacketBlockDisplay(AbstractLocation location, PacketBlockDisplay.PacketBlockDisplayBuilder builder) {
      this(MythicProvider.get(), location);
      if (builder.interpolationDelay != null) {
         this.interpolationDelay.set(builder.interpolationDelay);
      }

      if (builder.interpolationDuration != null) {
         this.interpolationDuration.set(builder.interpolationDuration);
      }

      if (builder.height != null) {
         this.height.set(builder.height);
      }

      if (builder.width != null) {
         this.width.set(builder.width);
      }

      if (builder.billboard != null) {
         this.billboard.set(builder.billboard);
      }

      if (builder.brightness != null) {
         this.brightness.set(builder.brightness);
      }

      if (builder.scale != null) {
         this.scale.set(builder.scale);
      }

      if (builder.translation != null) {
         this.translation.set(builder.translation);
      } else if (builder.yOffset != null) {
         this.translation.set(new Vector3f(0.0F, builder.yOffset, 0.0F));
      }

      if (builder.rotationLeft != null) {
         this.rotationLeft.set(builder.rotationLeft);
      }

      if (builder.rotationRight != null) {
         this.rotationRight.set(builder.rotationRight);
      }

      if (builder.blockData != null) {
         this.blockData.set(builder.blockData);
      }
   }

   public PacketBlockDisplay(MythicPlugin plugin, AbstractLocation location) {
      super(location);
      this.setRenderer((T)plugin.getVolatileCodeHandler().getWorldHandler().createVirtualDisplayBlockEntityRenderer(this));
   }

   public void setBlock(Material material) {
      if (material.isBlock()) {
         if (this.blockData.set(material.createBlockData())) {
            this.setDirty();
         }
      }
   }

   public void setBlock(AbstractBlock abstractBlock) {
      if (this.blockData.set(abstractBlock.getBlockData())) {
         this.setDirty();
      }
   }

   public void setBlock(BlockState block) {
      if (this.blockData.set(block.getBlockData())) {
         this.setDirty();
      }
   }

   public void setRotation(float yaw, float pitch) {
      Quaternionf q = new Quaternionf().rotateY(-yaw * (float) (Math.PI / 180.0)).rotateX(-pitch * (float) (Math.PI / 180.0));
      if (ServerVersion.isAfter(MinecraftVersions.v1_19_4)) {
         q.rotateY((float) Math.PI);
      }

      if (this.rotationLeft.set(q)) {
         this.setDirty();
      }
   }

   public void setRotationTransformation(float x, float y, float z) {
      if (this.rotationRight.set(new Quaternionf().rotationXYZ(x, y, z))) {
         this.setDirty();
      }
   }

   public void setTranslationTransformation(float x, float y, float z) {
      if (this.translation.set(new Vector3f(x, y, z))) {
         this.setDirty();
      }
   }

   public void setScale(AbstractVector scale) {
      if (this.scale.set(scale.toVector3f())) {
         this.setDirty();
      }
   }

   public void setBrightness(Brightness brightness) {
      int brightnessBlock = brightness.getBlockLight();
      int brightnessSky = brightness.getSkyLight();
      this.brightness.set(brightnessBlock << 4 | brightnessSky << 20);
      this.setDirty();
   }

   public static class PacketBlockDisplayBuilder {
      private Integer interpolationDelay;
      private Integer interpolationDuration;
      private Float viewRange;
      private Float height;
      private Float width;
      private Float yOffset;
      private Billboard billboard;
      private Integer brightness;
      private Vector3f scale;
      private Vector3f translation;
      private Quaternionf rotationLeft;
      private Quaternionf rotationRight;
      private BlockData blockData;

      public PacketBlockDisplay.PacketBlockDisplayBuilder scale(Vector3f scale) {
         this.scale = scale;
         return this;
      }

      public PacketBlockDisplay.PacketBlockDisplayBuilder scale(AbstractVector scale) {
         this.scale = new Vector3f((float)scale.getX(), (float)scale.getY(), (float)scale.getZ());
         return this;
      }

      public PacketBlockDisplay.PacketBlockDisplayBuilder brightness(Integer brightness) {
         this.brightness = brightness;
         return this;
      }

      public PacketBlockDisplay.PacketBlockDisplayBuilder brightness(int brightness) {
         this.brightness = brightness;
         return this;
      }

      public PacketBlockDisplay.PacketBlockDisplayBuilder brightness(int blockLight, int skyLight) {
         this.brightness = blockLight << 4 | skyLight << 20;
         return this;
      }

      public PacketBlockDisplay.PacketBlockDisplayBuilder billboard(Billboard billboard) {
         this.billboard = billboard;
         return this;
      }

      public PacketBlockDisplay.PacketBlockDisplayBuilder billboard(String billboarding) {
         this.billboard = Billboard.valueOf(billboarding.toUpperCase());
         return this;
      }

      public PacketBlockDisplay build(AbstractLocation location) {
         return new PacketBlockDisplay(location, this);
      }

      @Generated
      public Integer interpolationDelay() {
         return this.interpolationDelay;
      }

      @Generated
      public Integer interpolationDuration() {
         return this.interpolationDuration;
      }

      @Generated
      public Float viewRange() {
         return this.viewRange;
      }

      @Generated
      public Float height() {
         return this.height;
      }

      @Generated
      public Float width() {
         return this.width;
      }

      @Generated
      public Float yOffset() {
         return this.yOffset;
      }

      @Generated
      public Billboard billboard() {
         return this.billboard;
      }

      @Generated
      public Integer brightness() {
         return this.brightness;
      }

      @Generated
      public Vector3f scale() {
         return this.scale;
      }

      @Generated
      public Vector3f translation() {
         return this.translation;
      }

      @Generated
      public Quaternionf rotationLeft() {
         return this.rotationLeft;
      }

      @Generated
      public Quaternionf rotationRight() {
         return this.rotationRight;
      }

      @Generated
      public BlockData blockData() {
         return this.blockData;
      }

      @Generated
      public PacketBlockDisplay.PacketBlockDisplayBuilder interpolationDelay(Integer interpolationDelay) {
         this.interpolationDelay = interpolationDelay;
         return this;
      }

      @Generated
      public PacketBlockDisplay.PacketBlockDisplayBuilder interpolationDuration(Integer interpolationDuration) {
         this.interpolationDuration = interpolationDuration;
         return this;
      }

      @Generated
      public PacketBlockDisplay.PacketBlockDisplayBuilder viewRange(Float viewRange) {
         this.viewRange = viewRange;
         return this;
      }

      @Generated
      public PacketBlockDisplay.PacketBlockDisplayBuilder height(Float height) {
         this.height = height;
         return this;
      }

      @Generated
      public PacketBlockDisplay.PacketBlockDisplayBuilder width(Float width) {
         this.width = width;
         return this;
      }

      @Generated
      public PacketBlockDisplay.PacketBlockDisplayBuilder yOffset(Float yOffset) {
         this.yOffset = yOffset;
         return this;
      }

      @Generated
      public PacketBlockDisplay.PacketBlockDisplayBuilder translation(Vector3f translation) {
         this.translation = translation;
         return this;
      }

      @Generated
      public PacketBlockDisplay.PacketBlockDisplayBuilder rotationLeft(Quaternionf rotationLeft) {
         this.rotationLeft = rotationLeft;
         return this;
      }

      @Generated
      public PacketBlockDisplay.PacketBlockDisplayBuilder rotationRight(Quaternionf rotationRight) {
         this.rotationRight = rotationRight;
         return this;
      }

      @Generated
      public PacketBlockDisplay.PacketBlockDisplayBuilder blockData(BlockData blockData) {
         this.blockData = blockData;
         return this;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof PacketBlockDisplay.PacketBlockDisplayBuilder other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$interpolationDelay = this.interpolationDelay();
            Object other$interpolationDelay = other.interpolationDelay();
            if (this$interpolationDelay == null ? other$interpolationDelay == null : this$interpolationDelay.equals(other$interpolationDelay)) {
               Object this$interpolationDuration = this.interpolationDuration();
               Object other$interpolationDuration = other.interpolationDuration();
               if (this$interpolationDuration == null ? other$interpolationDuration == null : this$interpolationDuration.equals(other$interpolationDuration)) {
                  Object this$viewRange = this.viewRange();
                  Object other$viewRange = other.viewRange();
                  if (this$viewRange == null ? other$viewRange == null : this$viewRange.equals(other$viewRange)) {
                     Object this$height = this.height();
                     Object other$height = other.height();
                     if (this$height == null ? other$height == null : this$height.equals(other$height)) {
                        Object this$width = this.width();
                        Object other$width = other.width();
                        if (this$width == null ? other$width == null : this$width.equals(other$width)) {
                           Object this$yOffset = this.yOffset();
                           Object other$yOffset = other.yOffset();
                           if (this$yOffset == null ? other$yOffset == null : this$yOffset.equals(other$yOffset)) {
                              Object this$brightness = this.brightness();
                              Object other$brightness = other.brightness();
                              if (this$brightness == null ? other$brightness == null : this$brightness.equals(other$brightness)) {
                                 Object this$billboard = this.billboard();
                                 Object other$billboard = other.billboard();
                                 if (this$billboard == null ? other$billboard == null : this$billboard.equals(other$billboard)) {
                                    Object this$scale = this.scale();
                                    Object other$scale = other.scale();
                                    if (this$scale == null ? other$scale == null : this$scale.equals(other$scale)) {
                                       Object this$translation = this.translation();
                                       Object other$translation = other.translation();
                                       if (this$translation == null ? other$translation == null : this$translation.equals(other$translation)) {
                                          Object this$rotationLeft = this.rotationLeft();
                                          Object other$rotationLeft = other.rotationLeft();
                                          if (this$rotationLeft == null ? other$rotationLeft == null : this$rotationLeft.equals(other$rotationLeft)) {
                                             Object this$rotationRight = this.rotationRight();
                                             Object other$rotationRight = other.rotationRight();
                                             if (this$rotationRight == null ? other$rotationRight == null : this$rotationRight.equals(other$rotationRight)) {
                                                Object this$blockData = this.blockData();
                                                Object other$blockData = other.blockData();
                                                return this$blockData == null ? other$blockData == null : this$blockData.equals(other$blockData);
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
         return other instanceof PacketBlockDisplay.PacketBlockDisplayBuilder;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $interpolationDelay = this.interpolationDelay();
         result = result * 59 + ($interpolationDelay == null ? 43 : $interpolationDelay.hashCode());
         Object $interpolationDuration = this.interpolationDuration();
         result = result * 59 + ($interpolationDuration == null ? 43 : $interpolationDuration.hashCode());
         Object $viewRange = this.viewRange();
         result = result * 59 + ($viewRange == null ? 43 : $viewRange.hashCode());
         Object $height = this.height();
         result = result * 59 + ($height == null ? 43 : $height.hashCode());
         Object $width = this.width();
         result = result * 59 + ($width == null ? 43 : $width.hashCode());
         Object $yOffset = this.yOffset();
         result = result * 59 + ($yOffset == null ? 43 : $yOffset.hashCode());
         Object $brightness = this.brightness();
         result = result * 59 + ($brightness == null ? 43 : $brightness.hashCode());
         Object $billboard = this.billboard();
         result = result * 59 + ($billboard == null ? 43 : $billboard.hashCode());
         Object $scale = this.scale();
         result = result * 59 + ($scale == null ? 43 : $scale.hashCode());
         Object $translation = this.translation();
         result = result * 59 + ($translation == null ? 43 : $translation.hashCode());
         Object $rotationLeft = this.rotationLeft();
         result = result * 59 + ($rotationLeft == null ? 43 : $rotationLeft.hashCode());
         Object $rotationRight = this.rotationRight();
         result = result * 59 + ($rotationRight == null ? 43 : $rotationRight.hashCode());
         Object $blockData = this.blockData();
         return result * 59 + ($blockData == null ? 43 : $blockData.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "PacketBlockDisplay.PacketBlockDisplayBuilder(interpolationDelay="
            + this.interpolationDelay()
            + ", interpolationDuration="
            + this.interpolationDuration()
            + ", viewRange="
            + this.viewRange()
            + ", height="
            + this.height()
            + ", width="
            + this.width()
            + ", yOffset="
            + this.yOffset()
            + ", billboard="
            + this.billboard()
            + ", brightness="
            + this.brightness()
            + ", scale="
            + this.scale()
            + ", translation="
            + this.translation()
            + ", rotationLeft="
            + this.rotationLeft()
            + ", rotationRight="
            + this.rotationRight()
            + ", blockData="
            + this.blockData()
            + ")";
      }
   }

   public interface PacketBlockDisplayEntityRenderer extends IPacketEntityRenderer {
   }
}
