package io.lumine.mythic.api.volatilecode.virtual;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.bukkit.utils.cache.DataTracker;
import io.lumine.mythic.bukkit.utils.text.Text;
import java.util.Collection;
import java.util.function.Function;
import lombok.Generated;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import org.bukkit.Color;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Getter
public class PacketTextDisplay<T extends PacketEntityRenderer & PacketTextDisplay.PacketTextDisplayEntityRenderer> extends PacketEntity {
   private DataTracker<Integer> interpolationDelay = new DataTracker<>(0);
   private DataTracker<Integer> interpolationDuration = new DataTracker<>(1);
   private DataTracker<Float> viewRange = new DataTracker<>(4096.0F);
   private DataTracker<Float> height = new DataTracker<>(5.0F);
   private DataTracker<Float> width = new DataTracker<>(5.0F);
   private DataTracker<Billboard> billboard = new DataTracker<>(Billboard.VERTICAL);
   private DataTracker<Integer> brightness = new DataTracker<>(0);
   private DataTracker<Vector3f> scale = new DataTracker<>(new Vector3f(1.0F, 1.0F, 1.0F));
   private DataTracker<Vector3f> translation = new DataTracker<>(new Vector3f(0.0F, 0.0F, 0.0F));
   private DataTracker<Quaternionf> rotationLeft = new DataTracker<>(new Quaternionf(), false);
   private DataTracker<Quaternionf> rotationRight = new DataTracker<>(new Quaternionf(), false);
   private DataTracker<Function<AbstractPlayer, Component>> text = new DataTracker<>(player -> Component.empty());
   private DataTracker<Integer> lineWidth = new DataTracker<>(null);
   private DataTracker<Color> backgroundColor = new DataTracker<>(Color.fromARGB(64, 0, 0, 0));
   private DataTracker<Byte> textOpacity = new DataTracker<>((byte)-1, false);
   private static final float DEG2RAD = (float) (Math.PI / 180.0);

   public static PacketTextDisplay.PacketTextDisplayBuilder create() {
      return new PacketTextDisplay.PacketTextDisplayBuilder();
   }

   public PacketTextDisplay(AbstractLocation location) {
      this(MythicProvider.get(), location);
   }

   public PacketTextDisplay(MythicPlugin plugin, AbstractLocation location) {
      super(location);
      this.setRenderer((T)plugin.getVolatileCodeHandler().getWorldHandler().createVirtualTextDisplayEntityRenderer(this));
   }

   public PacketTextDisplay(AbstractLocation location, PacketTextDisplay.PacketTextDisplayBuilder builder) {
      super(location);
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

      if (builder.yOffset != null) {
         this.translation.set(new Vector3f(0.0F, builder.yOffset, 0.0F));
      }

      if (builder.lineWidth != null) {
         this.lineWidth.set(builder.lineWidth);
      }

      if (builder.textFunction != null) {
         this.text.set(builder.textFunction);
      }

      if (builder.backgroundColor != null) {
         this.backgroundColor.set(builder.backgroundColor);
      }

      if (builder.rotation != null) {
         this.setRotation(builder.rotation.y(), builder.rotation.x(), builder.rotation.z());
      }

      this.setRenderer((T)MythicProvider.get().getVolatileCodeHandler().getWorldHandler().createVirtualTextDisplayEntityRenderer(this));
      if (builder.cullingDistance != null) {
         this.getRenderer().setCullingDistance(builder.cullingDistance);
      }
   }

   public T getRenderer() {
      return (T)super.getRenderer();
   }

   public void setText(String text) {
      this.setTextComponent(Text.parse(text));
   }

   public void setText(String[] text) {
      Collection<Component> collection = Lists.newArrayList();
      Builder mergedComponent = Component.text();
      int i = 0;
      int size = text.length;

      for (String line : text) {
         mergedComponent = mergedComponent.append(Text.parse(line));
         if (i < size - 1) {
            mergedComponent = mergedComponent.appendNewline();
         }

         i++;
      }

      this.setTextComponent(((net.kyori.adventure.text.ComponentBuilder)mergedComponent).build());
   }

   public void setText(Collection<String> text) {
      Collection<Component> collection = Lists.newArrayList();
      Builder mergedComponent = Component.text();
      int i = 0;
      int size = text.size();

      for (String line : text) {
         mergedComponent = mergedComponent.append(Text.parse(line));
         if (i < size - 1) {
            mergedComponent = mergedComponent.appendNewline();
         }

         i++;
      }

      this.setTextComponent(((net.kyori.adventure.text.ComponentBuilder)mergedComponent).build());
   }

   public void setTextComponent(Collection<Component> components) {
      Builder mergedComponent = Component.text();
      int i = 0;
      int size = components.size();

      for (Component component : components) {
         mergedComponent = mergedComponent.append(component);
         if (i < size - 1) {
            mergedComponent = mergedComponent.appendNewline();
         }

         i++;
      }

      this.setTextComponent(((net.kyori.adventure.text.ComponentBuilder)mergedComponent).build());
   }

   public void setTextComponent(Component text) {
      this.setTextComponent((Function<AbstractPlayer, Component>)(player -> text));
   }

   public void setTextComponent(Function<AbstractPlayer, Component> text) {
      if (this.text.set(text)) {
         this.setDirty();
      }
   }

   public void setTextOpacity(int opacity) {
      if (this.textOpacity.set((byte)opacity)) {
         this.setDirty();
      }
   }

   public void setInterpolationDuration(int duration) {
      if (this.interpolationDuration.set(duration)) {
         this.setDirty();
      }
   }

   public void restartInterpolation() {
      this.interpolationDelay.poke(0);
      this.interpolationDelay.markDirty();
      this.setDirty();
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

   public void setRotation(float yaw, float pitch) {
      if (this.rotationLeft.set(new Quaternionf().rotateY(-yaw * (float) (Math.PI / 180.0)).rotateX(-pitch * (float) (Math.PI / 180.0)))) {
         this.setDirty();
      }
   }

   public void setRotation(float yaw, float pitch, float roll) {
      if (this.rotationLeft
         .set(new Quaternionf().rotateY(yaw * (float) (Math.PI / 180.0)).rotateX(-pitch * (float) (Math.PI / 180.0)).rotateZ(-roll * (float) (Math.PI / 180.0)))
         )
       {
         this.setDirty();
      }
   }

   public static class PacketTextDisplayBuilder {
      private Integer cullingDistance;
      private Integer interpolationDelay;
      private Integer interpolationDuration;
      private Float height;
      private Float width;
      private Float yOffset;
      private Billboard billboard;
      private Integer brightness;
      private Vector3f scale;
      private Vector3f rotation;
      private Integer lineWidth;
      private Function<AbstractPlayer, Component> textFunction;
      private Color backgroundColor;

      public PacketTextDisplay.PacketTextDisplayBuilder brightness(Integer brightness) {
         this.brightness = brightness << 4 | brightness << 20;
         return this;
      }

      public PacketTextDisplay.PacketTextDisplayBuilder brightness(Integer blockLight, Integer skyLight) {
         this.brightness = blockLight << 4 | skyLight << 20;
         return this;
      }

      public PacketTextDisplay.PacketTextDisplayBuilder brightness(int brightness) {
         this.brightness = brightness << 4 | brightness << 20;
         return this;
      }

      public PacketTextDisplay.PacketTextDisplayBuilder brightness(int blockLight, int skyLight) {
         this.brightness = blockLight << 4 | skyLight << 20;
         return this;
      }

      public PacketTextDisplay.PacketTextDisplayBuilder brightnessBitshifted(int brightness) {
         this.brightness = brightness;
         return this;
      }

      public PacketTextDisplay.PacketTextDisplayBuilder text(String text) {
         return this.textComponent(Text.parse(text));
      }

      public PacketTextDisplay.PacketTextDisplayBuilder text(String[] text) {
         Collection<Component> collection = Lists.newArrayList();
         Builder mergedComponent = Component.text();
         int i = 0;
         int size = text.length;

         for (String line : text) {
            mergedComponent = mergedComponent.append(Text.parse(line));
            if (i < size - 1) {
               mergedComponent = mergedComponent.appendNewline();
            }

            i++;
         }

         return this.textComponent(((net.kyori.adventure.text.ComponentBuilder)mergedComponent).build());
      }

      public PacketTextDisplay.PacketTextDisplayBuilder text(Collection<String> text) {
         Collection<Component> collection = Lists.newArrayList();
         Builder mergedComponent = Component.text();
         int i = 0;
         int size = text.size();

         for (String line : text) {
            mergedComponent = mergedComponent.append(Text.parse(line));
            if (i < size - 1) {
               mergedComponent = mergedComponent.appendNewline();
            }

            i++;
         }

         return this.textComponent(((net.kyori.adventure.text.ComponentBuilder)mergedComponent).build());
      }

      public PacketTextDisplay.PacketTextDisplayBuilder textComponent(Collection<Component> components) {
         Builder mergedComponent = Component.text();
         int i = 0;
         int size = components.size();

         for (Component component : components) {
            mergedComponent = mergedComponent.append(component);
            if (i < size - 1) {
               mergedComponent = mergedComponent.appendNewline();
            }

            i++;
         }

         return this.textComponent(((net.kyori.adventure.text.ComponentBuilder)mergedComponent).build());
      }

      public PacketTextDisplay.PacketTextDisplayBuilder textComponent(Component text) {
         return this.textFunction(player -> text);
      }

      public PacketTextDisplay.PacketTextDisplayBuilder scale(AbstractVector scale) {
         this.scale = new Vector3f((float)scale.getX(), (float)scale.getY(), (float)scale.getZ());
         return this;
      }

      public PacketTextDisplay.PacketTextDisplayBuilder rotation(float yaw, float pitch, float roll) {
         this.rotation = new Vector3f(pitch, yaw, roll);
         return this;
      }

      public PacketTextDisplay build(AbstractLocation location) {
         return new PacketTextDisplay(location, this);
      }

      @Generated
      public PacketTextDisplay.PacketTextDisplayBuilder cullingDistance(Integer cullingDistance) {
         this.cullingDistance = cullingDistance;
         return this;
      }

      @Generated
      public PacketTextDisplay.PacketTextDisplayBuilder interpolationDelay(Integer interpolationDelay) {
         this.interpolationDelay = interpolationDelay;
         return this;
      }

      @Generated
      public PacketTextDisplay.PacketTextDisplayBuilder interpolationDuration(Integer interpolationDuration) {
         this.interpolationDuration = interpolationDuration;
         return this;
      }

      @Generated
      public PacketTextDisplay.PacketTextDisplayBuilder height(Float height) {
         this.height = height;
         return this;
      }

      @Generated
      public PacketTextDisplay.PacketTextDisplayBuilder width(Float width) {
         this.width = width;
         return this;
      }

      @Generated
      public PacketTextDisplay.PacketTextDisplayBuilder yOffset(Float yOffset) {
         this.yOffset = yOffset;
         return this;
      }

      @Generated
      public PacketTextDisplay.PacketTextDisplayBuilder billboard(Billboard billboard) {
         this.billboard = billboard;
         return this;
      }

      @Generated
      public PacketTextDisplay.PacketTextDisplayBuilder rotation(Vector3f rotation) {
         this.rotation = rotation;
         return this;
      }

      @Generated
      public PacketTextDisplay.PacketTextDisplayBuilder lineWidth(Integer lineWidth) {
         this.lineWidth = lineWidth;
         return this;
      }

      @Generated
      public PacketTextDisplay.PacketTextDisplayBuilder textFunction(Function<AbstractPlayer, Component> textFunction) {
         this.textFunction = textFunction;
         return this;
      }

      @Generated
      public PacketTextDisplay.PacketTextDisplayBuilder backgroundColor(Color backgroundColor) {
         this.backgroundColor = backgroundColor;
         return this;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof PacketTextDisplay.PacketTextDisplayBuilder other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$cullingDistance = this.cullingDistance();
            Object other$cullingDistance = other.cullingDistance();
            if (this$cullingDistance == null ? other$cullingDistance == null : this$cullingDistance.equals(other$cullingDistance)) {
               Object this$interpolationDelay = this.interpolationDelay();
               Object other$interpolationDelay = other.interpolationDelay();
               if (this$interpolationDelay == null ? other$interpolationDelay == null : this$interpolationDelay.equals(other$interpolationDelay)) {
                  Object this$interpolationDuration = this.interpolationDuration();
                  Object other$interpolationDuration = other.interpolationDuration();
                  if (this$interpolationDuration == null ? other$interpolationDuration == null : this$interpolationDuration.equals(other$interpolationDuration)
                     )
                   {
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
                                 Object this$lineWidth = this.lineWidth();
                                 Object other$lineWidth = other.lineWidth();
                                 if (this$lineWidth == null ? other$lineWidth == null : this$lineWidth.equals(other$lineWidth)) {
                                    Object this$billboard = this.billboard();
                                    Object other$billboard = other.billboard();
                                    if (this$billboard == null ? other$billboard == null : this$billboard.equals(other$billboard)) {
                                       Object this$scale = this.scale();
                                       Object other$scale = other.scale();
                                       if (this$scale == null ? other$scale == null : this$scale.equals(other$scale)) {
                                          Object this$rotation = this.rotation();
                                          Object other$rotation = other.rotation();
                                          if (this$rotation == null ? other$rotation == null : this$rotation.equals(other$rotation)) {
                                             Object this$textFunction = this.textFunction();
                                             Object other$textFunction = other.textFunction();
                                             if (this$textFunction == null ? other$textFunction == null : this$textFunction.equals(other$textFunction)) {
                                                Object this$backgroundColor = this.backgroundColor();
                                                Object other$backgroundColor = other.backgroundColor();
                                                return this$backgroundColor == null
                                                   ? other$backgroundColor == null
                                                   : this$backgroundColor.equals(other$backgroundColor);
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
         return other instanceof PacketTextDisplay.PacketTextDisplayBuilder;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $cullingDistance = this.cullingDistance();
         result = result * 59 + ($cullingDistance == null ? 43 : $cullingDistance.hashCode());
         Object $interpolationDelay = this.interpolationDelay();
         result = result * 59 + ($interpolationDelay == null ? 43 : $interpolationDelay.hashCode());
         Object $interpolationDuration = this.interpolationDuration();
         result = result * 59 + ($interpolationDuration == null ? 43 : $interpolationDuration.hashCode());
         Object $height = this.height();
         result = result * 59 + ($height == null ? 43 : $height.hashCode());
         Object $width = this.width();
         result = result * 59 + ($width == null ? 43 : $width.hashCode());
         Object $yOffset = this.yOffset();
         result = result * 59 + ($yOffset == null ? 43 : $yOffset.hashCode());
         Object $brightness = this.brightness();
         result = result * 59 + ($brightness == null ? 43 : $brightness.hashCode());
         Object $lineWidth = this.lineWidth();
         result = result * 59 + ($lineWidth == null ? 43 : $lineWidth.hashCode());
         Object $billboard = this.billboard();
         result = result * 59 + ($billboard == null ? 43 : $billboard.hashCode());
         Object $scale = this.scale();
         result = result * 59 + ($scale == null ? 43 : $scale.hashCode());
         Object $rotation = this.rotation();
         result = result * 59 + ($rotation == null ? 43 : $rotation.hashCode());
         Object $textFunction = this.textFunction();
         result = result * 59 + ($textFunction == null ? 43 : $textFunction.hashCode());
         Object $backgroundColor = this.backgroundColor();
         return result * 59 + ($backgroundColor == null ? 43 : $backgroundColor.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "PacketTextDisplay.PacketTextDisplayBuilder(cullingDistance="
            + this.cullingDistance()
            + ", interpolationDelay="
            + this.interpolationDelay()
            + ", interpolationDuration="
            + this.interpolationDuration()
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
            + ", rotation="
            + this.rotation()
            + ", lineWidth="
            + this.lineWidth()
            + ", textFunction="
            + this.textFunction()
            + ", backgroundColor="
            + this.backgroundColor()
            + ")";
      }

      @Generated
      public Integer cullingDistance() {
         return this.cullingDistance;
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
      public Vector3f rotation() {
         return this.rotation;
      }

      @Generated
      public Integer lineWidth() {
         return this.lineWidth;
      }

      @Generated
      public Function<AbstractPlayer, Component> textFunction() {
         return this.textFunction;
      }

      @Generated
      public Color backgroundColor() {
         return this.backgroundColor;
      }
   }

   public interface PacketTextDisplayEntityRenderer extends IPacketEntityRenderer {
   }
}
