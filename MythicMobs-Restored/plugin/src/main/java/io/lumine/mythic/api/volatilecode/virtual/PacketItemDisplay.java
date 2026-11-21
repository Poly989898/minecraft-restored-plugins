package io.lumine.mythic.api.volatilecode.virtual;

import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.bukkit.utils.cache.DataTracker;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import lombok.Generated;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Getter
public class PacketItemDisplay<T extends PacketEntityRenderer & PacketItemDisplay.PacketItemDisplayEntityRenderer> extends PacketEntity {
   private static final boolean IS_AFTER_1_19_4 = ServerVersion.isAfter(MinecraftVersions.v1_19_4);
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
   private DataTracker<ItemStack> item = DataTracker.of(new ItemStack(Material.AIR));
   private DataTracker<ItemDisplayTransform> displayTransform = DataTracker.of(ItemDisplayTransform.HEAD);
   private static final float DEG2RAD = (float) (Math.PI / 180.0);

   public static PacketItemDisplay.PacketItemDisplayBuilder create() {
      return new PacketItemDisplay.PacketItemDisplayBuilder();
   }

   public PacketItemDisplay(AbstractLocation location) {
      this(MythicProvider.get(), location);
   }

   public PacketItemDisplay(AbstractLocation location, PacketItemDisplay.PacketItemDisplayBuilder builder) {
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

      if (builder.displayTransform != null) {
         this.displayTransform.set(builder.displayTransform);
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

      if (builder.item != null) {
         this.item.set(builder.item);
      }
   }

   public PacketItemDisplay(MythicPlugin plugin, AbstractLocation location) {
      super(location);
      this.setRenderer((T)plugin.getVolatileCodeHandler().getWorldHandler().createVirtualDisplayItemEntityRenderer(this));
   }

   public void setItem(ItemStack item) {
      if (this.item.set(item)) {
         this.setDirty();
      }
   }

   public void setRotation(float yaw, float pitch) {
      Quaternionf q = new Quaternionf().rotateY(-yaw * (float) (Math.PI / 180.0)).rotateX(-pitch * (float) (Math.PI / 180.0));
      if (IS_AFTER_1_19_4) {
         q.rotateY((float) Math.PI);
      }

      if (this.rotationLeft.set(q)) {
         this.interpolationDelay.markDirty();
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

   public void setInterpolationDelay(int interpolationDelay) {
      if (this.interpolationDelay.set(interpolationDelay)) {
         this.setDirty();
      }
   }

   public void setInterpolationDelayForced(int interpolationDelay) {
      this.interpolationDelay.set(interpolationDelay);
      this.setDirty();
   }

   public void setInterpolationDuration(int interpolationDuration) {
      if (this.interpolationDuration.set(interpolationDuration)) {
         this.setDirty();
      }
   }

   public void setBrightness(Brightness brightness) {
      int brightnessBlock = brightness.getBlockLight();
      int brightnessSky = brightness.getSkyLight();
      this.brightness.set(brightnessBlock << 4 | brightnessSky << 20);
      this.setDirty();
   }

   public static class PacketItemDisplayBuilder {
      private Integer interpolationDelay;
      private Integer interpolationDuration;
      private Float viewRange;
      private Float height;
      private Float width;
      private Float yOffset;
      private Billboard billboard;
      private Integer brightness;
      private ItemDisplayTransform displayTransform;
      private Vector3f scale;
      private Vector3f translation;
      private Quaternionf rotationLeft;
      private Quaternionf rotationRight;
      private ItemStack item;

      public PacketItemDisplay.PacketItemDisplayBuilder scale(Vector3f scale) {
         this.scale = scale;
         return this;
      }

      public PacketItemDisplay.PacketItemDisplayBuilder scale(AbstractVector scale) {
         this.scale = new Vector3f((float)scale.getX(), (float)scale.getY(), (float)scale.getZ());
         return this;
      }

      public PacketItemDisplay.PacketItemDisplayBuilder brightness(Integer brightness) {
         this.brightness = brightness;
         return this;
      }

      public PacketItemDisplay.PacketItemDisplayBuilder brightness(int brightness) {
         this.brightness = brightness;
         return this;
      }

      public PacketItemDisplay.PacketItemDisplayBuilder brightness(int blockLight, int skyLight) {
         this.brightness = blockLight << 4 | skyLight << 20;
         return this;
      }

      public PacketItemDisplay.PacketItemDisplayBuilder billboard(Billboard billboard) {
         this.billboard = billboard;
         return this;
      }

      public PacketItemDisplay.PacketItemDisplayBuilder billboard(String billboarding) {
         this.billboard = Billboard.valueOf(billboarding.toUpperCase());
         return this;
      }

      public PacketItemDisplay.PacketItemDisplayBuilder displayTransform(ItemDisplayTransform transform) {
         this.displayTransform = transform;
         return this;
      }

      public PacketItemDisplay build(AbstractLocation location) {
         return new PacketItemDisplay(location, this);
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
      public ItemDisplayTransform displayTransform() {
         return this.displayTransform;
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
      public ItemStack item() {
         return this.item;
      }

      @Generated
      public PacketItemDisplay.PacketItemDisplayBuilder interpolationDelay(Integer interpolationDelay) {
         this.interpolationDelay = interpolationDelay;
         return this;
      }

      @Generated
      public PacketItemDisplay.PacketItemDisplayBuilder interpolationDuration(Integer interpolationDuration) {
         this.interpolationDuration = interpolationDuration;
         return this;
      }

      @Generated
      public PacketItemDisplay.PacketItemDisplayBuilder viewRange(Float viewRange) {
         this.viewRange = viewRange;
         return this;
      }

      @Generated
      public PacketItemDisplay.PacketItemDisplayBuilder height(Float height) {
         this.height = height;
         return this;
      }

      @Generated
      public PacketItemDisplay.PacketItemDisplayBuilder width(Float width) {
         this.width = width;
         return this;
      }

      @Generated
      public PacketItemDisplay.PacketItemDisplayBuilder yOffset(Float yOffset) {
         this.yOffset = yOffset;
         return this;
      }

      @Generated
      public PacketItemDisplay.PacketItemDisplayBuilder translation(Vector3f translation) {
         this.translation = translation;
         return this;
      }

      @Generated
      public PacketItemDisplay.PacketItemDisplayBuilder rotationLeft(Quaternionf rotationLeft) {
         this.rotationLeft = rotationLeft;
         return this;
      }

      @Generated
      public PacketItemDisplay.PacketItemDisplayBuilder rotationRight(Quaternionf rotationRight) {
         this.rotationRight = rotationRight;
         return this;
      }

      @Generated
      public PacketItemDisplay.PacketItemDisplayBuilder item(ItemStack item) {
         this.item = item;
         return this;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof PacketItemDisplay.PacketItemDisplayBuilder other)) {
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
                                    Object this$displayTransform = this.displayTransform();
                                    Object other$displayTransform = other.displayTransform();
                                    if (this$displayTransform == null ? other$displayTransform == null : this$displayTransform.equals(other$displayTransform)) {
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
                                                   Object this$item = this.item();
                                                   Object other$item = other.item();
                                                   return this$item == null ? other$item == null : this$item.equals(other$item);
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
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof PacketItemDisplay.PacketItemDisplayBuilder;
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
         Object $displayTransform = this.displayTransform();
         result = result * 59 + ($displayTransform == null ? 43 : $displayTransform.hashCode());
         Object $scale = this.scale();
         result = result * 59 + ($scale == null ? 43 : $scale.hashCode());
         Object $translation = this.translation();
         result = result * 59 + ($translation == null ? 43 : $translation.hashCode());
         Object $rotationLeft = this.rotationLeft();
         result = result * 59 + ($rotationLeft == null ? 43 : $rotationLeft.hashCode());
         Object $rotationRight = this.rotationRight();
         result = result * 59 + ($rotationRight == null ? 43 : $rotationRight.hashCode());
         Object $item = this.item();
         return result * 59 + ($item == null ? 43 : $item.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "PacketItemDisplay.PacketItemDisplayBuilder(interpolationDelay="
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
            + ", displayTransform="
            + this.displayTransform()
            + ", scale="
            + this.scale()
            + ", translation="
            + this.translation()
            + ", rotationLeft="
            + this.rotationLeft()
            + ", rotationRight="
            + this.rotationRight()
            + ", item="
            + this.item()
            + ")";
      }
   }

   public interface PacketItemDisplayEntityRenderer extends IPacketEntityRenderer {
   }
}
