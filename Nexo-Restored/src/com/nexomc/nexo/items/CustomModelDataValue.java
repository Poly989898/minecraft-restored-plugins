package com.nexomc.nexo.items;

import com.nexomc.nexo.commands.DyeCommandKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u0000 \r2\u00020\u0001:\u0005\t\n\u000b\f\rB\t\b\u0004¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005J\u0006\u0010\u0007\u001a\u00020\b\u0082\u0001\u0004\u000e\u000f\u0010\u0011¨\u0006\u0012"},
   d2 = {"Lcom/nexomc/nexo/items/CustomModelDataValue;", "", "<init>", "()V", "appendTo", "Lio/papermc/paper/datacomponent/item/CustomModelData$Builder;", "builder", "toCustomModelData", "Lio/papermc/paper/datacomponent/item/CustomModelData;", "FloatValue", "FlagValue", "StringValue", "ColorValue", "Companion", "Lcom/nexomc/nexo/items/CustomModelDataValue$ColorValue;", "Lcom/nexomc/nexo/items/CustomModelDataValue$FlagValue;", "Lcom/nexomc/nexo/items/CustomModelDataValue$FloatValue;", "Lcom/nexomc/nexo/items/CustomModelDataValue$StringValue;", "com.nexomc:core"}
)
public abstract sealed class CustomModelDataValue {
   @NotNull
   public static final Companion Companion = new Companion();

   private CustomModelDataValue() {
   }

   @NotNull
   public final io.papermc.paper.datacomponent.item.CustomModelData.Builder appendTo(@NotNull io.papermc.paper.datacomponent.item.CustomModelData.Builder builder) {
      io.papermc.paper.datacomponent.item.CustomModelData.Builder builder2;
      if (this instanceof FloatValue) {
         builder2 = builder.addFloat(((FloatValue)this).getValue());
      } else if (this instanceof FlagValue) {
         builder2 = builder.addFlag(((FlagValue)this).getValue());
      } else if (this instanceof StringValue) {
         builder2 = builder.addString(((StringValue)this).getValue());
      } else {
         if (!(this instanceof ColorValue)) {
            throw new NoWhenBranchMatchedException();
         }

         builder2 = builder.addColor(((ColorValue)this).getValue());
      }

      return builder2;
   }

   @NotNull
   public final io.papermc.paper.datacomponent.item.CustomModelData toCustomModelData() {
      io.papermc.paper.datacomponent.item.CustomModelData.Builder customModelData = io.papermc.paper.datacomponent.item.CustomModelData.customModelData();
      Object obj = this.appendTo(customModelData).build();
      return (io.papermc.paper.datacomponent.item.CustomModelData)obj;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\b\u001a\u00020\tJ\u0006\u0010\n\u001a\u00020\u0003J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0014\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010HÖ\u0083\u0004J\n\u0010\u0011\u001a\u00020\tHÖ\u0081\u0004J\n\u0010\u0012\u001a\u00020\u0013HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0014"},
      d2 = {"Lcom/nexomc/nexo/items/CustomModelDataValue$FloatValue;", "Lcom/nexomc/nexo/items/CustomModelDataValue;", "value", "", "<init>", "(F)V", "getValue", "()F", "toInt", "", "toFloat", "component1", "copy", "equals", "", "other", "", "hashCode", "toString", "", "com.nexomc:core"}
   )
   public static final class FloatValue extends CustomModelDataValue {
      private final float value;

      public FloatValue(float value) {
         super();
         this.value = value;
      }

      public final float getValue() {
         return this.value;
      }

      public final int toInt() {
         return (int)this.value;
      }

      public final float toFloat() {
         return this.value;
      }

      public final float component1() {
         return this.value;
      }

      @NotNull
      public final FloatValue copy(float value) {
         return new FloatValue(value);
      }

      @NotNull
      public String toString() {
         return "FloatValue(value=" + this.value + ")";
      }

      public int hashCode() {
         return Float.hashCode(this.value);
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof FloatValue)) {
            return false;
         } else {
            FloatValue floatValue = (FloatValue)other;
            return Float.compare(this.value, floatValue.value) == 0;
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u0086\u0002J\u0006\u0010\t\u001a\u00020\u0003J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\u000b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0014\u0010\f\u001a\u00020\u00032\b\u0010\r\u001a\u0004\u0018\u00010\u000eHÖ\u0083\u0004J\n\u0010\u000f\u001a\u00020\u0010HÖ\u0081\u0004J\n\u0010\u0011\u001a\u00020\u0012HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0013"},
      d2 = {"Lcom/nexomc/nexo/items/CustomModelDataValue$FlagValue;", "Lcom/nexomc/nexo/items/CustomModelDataValue;", "value", "", "<init>", "(Z)V", "getValue", "()Z", "not", "toBoolean", "component1", "copy", "equals", "other", "", "hashCode", "", "toString", "", "com.nexomc:core"}
   )
   public static final class FlagValue extends CustomModelDataValue {
      private final boolean value;

      public FlagValue(boolean value) {
         super();
         this.value = value;
      }

      public final boolean getValue() {
         return this.value;
      }

      public final boolean not() {
         return !this.value;
      }

      public final boolean toBoolean() {
         return this.value;
      }

      public final boolean component1() {
         return this.value;
      }

      @NotNull
      public final FlagValue copy(boolean value) {
         return new FlagValue(value);
      }

      @NotNull
      public String toString() {
         return "FlagValue(value=" + this.value + ")";
      }

      public int hashCode() {
         return Boolean.hashCode(this.value);
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof FlagValue)) {
            return false;
         } else {
            FlagValue flagValue = (FlagValue)other;
            return this.value == flagValue.value;
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0014\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rHÖ\u0083\u0004J\n\u0010\u000e\u001a\u00020\u000fHÖ\u0081\u0004J\n\u0010\u0010\u001a\u00020\u0003HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0011"},
      d2 = {"Lcom/nexomc/nexo/items/CustomModelDataValue$StringValue;", "Lcom/nexomc/nexo/items/CustomModelDataValue;", "value", "", "<init>", "(Ljava/lang/String;)V", "getValue", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "com.nexomc:core"}
   )
   public static final class StringValue extends CustomModelDataValue {
      @NotNull
      private final String value;

      public StringValue(@NotNull String value) {
         super();
         this.value = value;
      }

      @NotNull
      public final String getValue() {
         return this.value;
      }

      @NotNull
      public final String component1() {
         return this.value;
      }

      @NotNull
      public final StringValue copy(@NotNull String value) {
         return new StringValue(value);
      }

      @NotNull
      public String toString() {
         return "StringValue(value=" + this.value + ")";
      }

      public int hashCode() {
         return this.value.hashCode();
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof StringValue)) {
            return false;
         } else {
            StringValue stringValue = (StringValue)other;
            return Intrinsics.areEqual(this.value, stringValue.value);
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0014\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rHÖ\u0083\u0004J\n\u0010\u000e\u001a\u00020\u000fHÖ\u0081\u0004J\n\u0010\u0010\u001a\u00020\u0011HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0012"},
      d2 = {"Lcom/nexomc/nexo/items/CustomModelDataValue$ColorValue;", "Lcom/nexomc/nexo/items/CustomModelDataValue;", "value", "Lorg/bukkit/Color;", "<init>", "(Lorg/bukkit/Color;)V", "getValue", "()Lorg/bukkit/Color;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "com.nexomc:core"}
   )
   public static final class ColorValue extends CustomModelDataValue {
      @NotNull
      private final Color value;

      public ColorValue(@NotNull Color value) {
         super();
         this.value = value;
      }

      @NotNull
      public final Color getValue() {
         return this.value;
      }

      @NotNull
      public final Color component1() {
         return this.value;
      }

      @NotNull
      public final ColorValue copy(@NotNull Color value) {
         return new ColorValue(value);
      }

      @NotNull
      public String toString() {
         return "ColorValue(value=" + this.value + ")";
      }

      public int hashCode() {
         return this.value.hashCode();
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof ColorValue)) {
            return false;
         } else {
            ColorValue colorValue = (ColorValue)other;
            return Intrinsics.areEqual(this.value, colorValue.value);
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/items/CustomModelDataValue$Companion;", "", "<init>", "()V", "parseOrThrow", "Lcom/nexomc/nexo/items/CustomModelDataValue;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final CustomModelDataValue parseOrThrow(@NotNull ConfigurationSection section) {
         Object value = section.get("value");
         CustomModelDataValue customModelDataValue;
         if (value == null) {
            customModelDataValue = null;
         } else if (value instanceof Boolean) {
            customModelDataValue = new FlagValue((Boolean)value);
         } else if (value instanceof Number) {
            customModelDataValue = new FloatValue(((Number)value).floatValue());
         } else if (value instanceof Color) {
            customModelDataValue = new ColorValue((Color)value);
         } else if (value instanceof String) {
            if (((CharSequence)value).length() == 0) {
               customModelDataValue = null;
            } else if (StringsKt.toBooleanStrictOrNull((String)value) != null) {
               customModelDataValue = new FlagValue(StringsKt.toBooleanStrict((String)value));
            } else if (StringsKt.toFloatOrNull((String)value) != null) {
               customModelDataValue = new FloatValue(Float.parseFloat((String)value));
            } else if (DyeCommandKt.toColor((String)value) != null) {
               Color color = DyeCommandKt.toColor((String)value);
               customModelDataValue = new ColorValue(color);
            } else {
               customModelDataValue = new StringValue((String)value);
            }
         } else {
            customModelDataValue = null;
         }

         if (customModelDataValue == null) {
            String name = section.getName();
            throw new IllegalStateException(("Invalid 'current' value for CUSTOM_MODEL_DATA state '" + name + "': " + section.get("value")).toString());
         } else {
            return customModelDataValue;
         }
      }
   }
}
