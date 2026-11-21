package com.nexomc.nexo.mechanics.furniture.text_entity;

import com.nexomc.nexo.mechanics.furniture.FurnitureProperties;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Vector3f;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\u0007\n\u0002\b\u000e\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001Bc\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0012¢\u0006\u0004\b\u0013\u0010\u0014B\u0011\b\u0016\u0012\u0006\u0010\u0015\u001a\u00020\u0016¢\u0006\u0004\b\u0013\u0010\u0017J\u000e\u0010\u0002\u001a\u00020\u00032\u0006\u0010(\u001a\u00020)J\t\u0010*\u001a\u00020\u0003HÆ\u0003J\u000f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J\t\u0010,\u001a\u00020\bHÆ\u0003J\t\u0010-\u001a\u00020\bHÆ\u0003J\t\u0010.\u001a\u00020\u000bHÆ\u0003J\t\u0010/\u001a\u00020\rHÆ\u0003J\t\u00100\u001a\u00020\u000fHÆ\u0003J\t\u00101\u001a\u00020\u000fHÆ\u0003J\t\u00102\u001a\u00020\u0012HÆ\u0003Ji\u00103\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u000f2\b\b\u0002\u0010\u0011\u001a\u00020\u0012HÆ\u0001J\u0014\u00104\u001a\u00020\u000f2\b\u00105\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u00106\u001a\u00020\bHÖ\u0081\u0004J\n\u00107\u001a\u000208HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\t\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001dR\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010\f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u0011\u0010\u000e\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b#\u0010$R\u0011\u0010\u0010\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b%\u0010$R\u0011\u0010\u0011\u001a\u00020\u0012¢\u0006\b\n\u0000\u001a\u0004\b&\u0010'¨\u00069"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/text_entity/TextEntityMechanic;", "", "offset", "Lorg/joml/Vector3f;", "text", "", "Lnet/kyori/adventure/text/Component;", "textOpacity", "", "lineWidth", "backgroundColor", "Lorg/bukkit/Color;", "alignment", "Lorg/bukkit/entity/TextDisplay$TextAlignment;", "seeThrough", "", "shadow", "properties", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureProperties;", "<init>", "(Lorg/joml/Vector3f;Ljava/util/List;IILorg/bukkit/Color;Lorg/bukkit/entity/TextDisplay$TextAlignment;ZZLcom/nexomc/nexo/mechanics/furniture/FurnitureProperties;)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getOffset", "()Lorg/joml/Vector3f;", "getText", "()Ljava/util/List;", "getTextOpacity", "()I", "getLineWidth", "getBackgroundColor", "()Lorg/bukkit/Color;", "getAlignment", "()Lorg/bukkit/entity/TextDisplay$TextAlignment;", "getSeeThrough", "()Z", "getShadow", "getProperties", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureProperties;", "angle", "", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "", "com.nexomc:core"}
)
public final class TextEntityMechanic {
   @NotNull
   private final Vector3f offset;
   @NotNull
   private final List<Component> text;
   private final int textOpacity;
   private final int lineWidth;
   @NotNull
   private final Color backgroundColor;
   @NotNull
   private final TextDisplay.TextAlignment alignment;
   private final boolean seeThrough;
   private final boolean shadow;
   @NotNull
   private final FurnitureProperties properties;

   public TextEntityMechanic(@NotNull Vector3f offset, @NotNull List<Component> text, int textOpacity, int lineWidth, @NotNull Color backgroundColor, @NotNull TextDisplay.TextAlignment alignment, boolean seeThrough, boolean shadow, @NotNull FurnitureProperties properties) {
      super();
      this.offset = offset;
      this.text = text;
      this.textOpacity = textOpacity;
      this.lineWidth = lineWidth;
      this.backgroundColor = backgroundColor;
      this.alignment = alignment;
      this.seeThrough = seeThrough;
      this.shadow = shadow;
      this.properties = properties;
   }

   @NotNull
   public final Vector3f getOffset() {
      return this.offset;
   }

   @NotNull
   public final List<Component> getText() {
      return this.text;
   }

   public final int getTextOpacity() {
      return this.textOpacity;
   }

   public final int getLineWidth() {
      return this.lineWidth;
   }

   @NotNull
   public final Color getBackgroundColor() {
      return this.backgroundColor;
   }

   @NotNull
   public final TextDisplay.TextAlignment getAlignment() {
      return this.alignment;
   }

   public final boolean getSeeThrough() {
      return this.seeThrough;
   }

   public final boolean getShadow() {
      return this.shadow;
   }

   @NotNull
   public final FurnitureProperties getProperties() {
      return this.properties;
   }

   public TextEntityMechanic(@NotNull ConfigurationSection section) {
      Vector3f vector3f = NexoYamlKt.vector3f(section, "offset");
      Iterable iterable = (Iterable)NexoYamlKt.stringListOrSingle(section, "text", (String)null);
      Vector3f vector3f2 = vector3f;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         String it = (String)item;
         int value = 0;
         destination.add(AdventureUtilsKt.deserialize(it));
      }

      List list = (List)destination;
      Integer number = NexoYamlKt.intOrNull(section, "text_opacity");
      int value2 = number != null ? number : -1;
      Integer number2 = NexoYamlKt.intOrNull(section, "line_width");
      int value3 = number2 != null ? number2 : 200;
      Color color = NexoYamlKt.color(section, "background_color");
      if (color == null) {
         color = Color.fromARGB(0, 0, 0, 0);
      }

      this(vector3f2, list, value2, value3, color, TextDisplay.TextAlignment.CENTER, section.getBoolean("see_through"), false, new FurnitureProperties(section));
   }

   @NotNull
   public final Vector3f offset(float angle) {
      float angle_1 = angle;
      if (angle_1 < 0.0F) {
         angle_1 = angle_1 + 360.0F;
      }

      float radians = Math.toRadians(angle_1);
      float x = this.offset.x * (float)java.lang.Math.cos((double)radians) - -this.offset.z * (float)java.lang.Math.sin((double)radians);
      float z = this.offset.x * (float)java.lang.Math.sin((double)radians) + -this.offset.z * (float)java.lang.Math.cos((double)radians);
      return new Vector3f(x, this.offset.y, z);
   }

   @NotNull
   public final Vector3f component1() {
      return this.offset;
   }

   @NotNull
   public final List<Component> component2() {
      return this.text;
   }

   public final int component3() {
      return this.textOpacity;
   }

   public final int component4() {
      return this.lineWidth;
   }

   @NotNull
   public final Color component5() {
      return this.backgroundColor;
   }

   @NotNull
   public final TextDisplay.TextAlignment component6() {
      return this.alignment;
   }

   public final boolean component7() {
      return this.seeThrough;
   }

   public final boolean component8() {
      return this.shadow;
   }

   @NotNull
   public final FurnitureProperties component9() {
      return this.properties;
   }

   @NotNull
   public final TextEntityMechanic copy(@NotNull Vector3f offset, @NotNull List<Component> text, int textOpacity, int lineWidth, @NotNull Color backgroundColor, @NotNull TextDisplay.TextAlignment alignment, boolean seeThrough, boolean shadow, @NotNull FurnitureProperties properties) {
      return new TextEntityMechanic(offset, text, textOpacity, lineWidth, backgroundColor, alignment, seeThrough, shadow, properties);
   }

   @NotNull
   public String toString() {
      return "TextEntityMechanic(offset=" + this.offset + ", text=" + this.text + ", textOpacity=" + this.textOpacity + ", lineWidth=" + this.lineWidth + ", backgroundColor=" + this.backgroundColor + ", alignment=" + this.alignment + ", seeThrough=" + this.seeThrough + ", shadow=" + this.shadow + ", properties=" + this.properties + ")";
   }

   public int hashCode() {
      int result = this.offset.hashCode();
      result = result * 31 + this.text.hashCode();
      result = result * 31 + Integer.hashCode(this.textOpacity);
      result = result * 31 + Integer.hashCode(this.lineWidth);
      result = result * 31 + this.backgroundColor.hashCode();
      result = result * 31 + this.alignment.hashCode();
      result = result * 31 + Boolean.hashCode(this.seeThrough);
      result = result * 31 + Boolean.hashCode(this.shadow);
      result = result * 31 + this.properties.hashCode();
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof TextEntityMechanic)) {
         return false;
      } else {
         TextEntityMechanic textEntityMechanic = (TextEntityMechanic)other;
         if (!Intrinsics.areEqual(this.offset, textEntityMechanic.offset)) {
            return false;
         } else if (!Intrinsics.areEqual(this.text, textEntityMechanic.text)) {
            return false;
         } else if (this.textOpacity != textEntityMechanic.textOpacity) {
            return false;
         } else if (this.lineWidth != textEntityMechanic.lineWidth) {
            return false;
         } else if (!Intrinsics.areEqual(this.backgroundColor, textEntityMechanic.backgroundColor)) {
            return false;
         } else if (this.alignment != textEntityMechanic.alignment) {
            return false;
         } else if (this.seeThrough != textEntityMechanic.seeThrough) {
            return false;
         } else if (this.shadow != textEntityMechanic.shadow) {
            return false;
         } else {
            return Intrinsics.areEqual(this.properties, textEntityMechanic.properties);
         }
      }
   }
}
