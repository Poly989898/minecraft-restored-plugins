package com.nexomc.nexo.mechanics.furniture;

import com.nexomc.nexo.commands.DyeCommandKt;
import com.nexomc.nexo.utils.EnumUtils;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.QuaternionUtils;
import com.nexomc.nexo.utils.TriState;
import com.nexomc.nexo.utils.logs.Logs;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import kotlin.time.Duration;
import kotlin.time.DurationUnit;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b#\b\u0016\u0018\u00002\u00020\u0001B©\u0001\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0011\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u0013\u0012\b\b\u0002\u0010\u0014\u001a\u00020\u0013\u0012\b\b\u0002\u0010\u0015\u001a\u00020\u0016\u0012\b\b\u0002\u0010\u0017\u001a\u00020\u0018\u0012\b\b\u0002\u0010\u0019\u001a\u00020\u001a¢\u0006\u0004\b\u001b\u0010\u001cB\u0011\b\u0016\u0012\u0006\u0010\u001d\u001a\u00020\u001e¢\u0006\u0004\b\u001b\u0010\u001fR\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0015\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\n\n\u0002\u0010$\u001a\u0004\b\"\u0010#R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b%\u0010&R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b'\u0010(R\u0013\u0010\n\u001a\u0004\u0018\u00010\u000b¢\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u0015\u0010\f\u001a\u0004\u0018\u00010\u0005¢\u0006\n\n\u0002\u0010$\u001a\u0004\b+\u0010#R\u0015\u0010\r\u001a\u0004\u0018\u00010\u0005¢\u0006\n\n\u0002\u0010$\u001a\u0004\b,\u0010#R\u0011\u0010\u000e\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b-\u0010.R\u0011\u0010\u000f\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b/\u0010.R\u0011\u0010\u0010\u001a\u00020\u0011¢\u0006\b\n\u0000\u001a\u0004\b0\u00101R\u0011\u0010\u0012\u001a\u00020\u0013¢\u0006\b\n\u0000\u001a\u0004\b2\u00103R\u0011\u0010\u0014\u001a\u00020\u0013¢\u0006\b\n\u0000\u001a\u0004\b4\u00103R\u0013\u0010\u0015\u001a\u00020\u0016¢\u0006\n\n\u0002\u00107\u001a\u0004\b5\u00106R\u0011\u0010\u0017\u001a\u00020\u0018¢\u0006\b\n\u0000\u001a\u0004\b8\u00109R\u0011\u0010\u0019\u001a\u00020\u001a¢\u0006\b\n\u0000\u001a\u0004\b:\u0010;R\u001e\u0010=\u001a\u00020\u00112\u0006\u0010<\u001a\u00020\u0011@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b>\u00101R\u0011\u0010?\u001a\u00020\u00188F¢\u0006\u0006\u001a\u0004\b?\u00109R\u0011\u0010@\u001a\u00020\u00188F¢\u0006\u0006\u001a\u0004\b@\u00109¨\u0006A"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/FurnitureProperties;", "", "glowColor", "Lorg/bukkit/Color;", "viewRange", "", "brightness", "Lorg/bukkit/entity/Display$Brightness;", "displayTransform", "Lorg/bukkit/entity/ItemDisplay$ItemDisplayTransform;", "trackingRotation", "Lorg/bukkit/entity/Display$Billboard;", "shadowStrength", "shadowRadius", "displayWidth", "displayHeight", "translation", "Lorg/joml/Vector3f;", "leftRotation", "Lorg/joml/Quaternionf;", "rightRotation", "delay", "Lkotlin/time/Duration;", "offsetAgainstBlocks", "", "cullable", "Lcom/nexomc/nexo/utils/TriState;", "<init>", "(Lorg/bukkit/Color;Ljava/lang/Float;Lorg/bukkit/entity/Display$Brightness;Lorg/bukkit/entity/ItemDisplay$ItemDisplayTransform;Lorg/bukkit/entity/Display$Billboard;Ljava/lang/Float;Ljava/lang/Float;FFLorg/joml/Vector3f;Lorg/joml/Quaternionf;Lorg/joml/Quaternionf;JZLcom/nexomc/nexo/utils/TriState;Lkotlin/jvm/internal/DefaultConstructorMarker;)V", "configSection", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getGlowColor", "()Lorg/bukkit/Color;", "getViewRange", "()Ljava/lang/Float;", "Ljava/lang/Float;", "getBrightness", "()Lorg/bukkit/entity/Display$Brightness;", "getDisplayTransform", "()Lorg/bukkit/entity/ItemDisplay$ItemDisplayTransform;", "getTrackingRotation", "()Lorg/bukkit/entity/Display$Billboard;", "getShadowStrength", "getShadowRadius", "getDisplayWidth", "()F", "getDisplayHeight", "getTranslation", "()Lorg/joml/Vector3f;", "getLeftRotation", "()Lorg/joml/Quaternionf;", "getRightRotation", "getDelay-UwyO8pc", "()J", "J", "getOffsetAgainstBlocks", "()Z", "getCullable", "()Lcom/nexomc/nexo/utils/TriState;", "value", "scale", "getScale", "isFixedTransform", "isNoneTransform", "com.nexomc:core"}
)
public class FurnitureProperties {
   @Nullable
   private final Color glowColor;
   @Nullable
   private final Float viewRange;
   @Nullable
   private final Display.Brightness brightness;
   @NotNull
   private final ItemDisplay.ItemDisplayTransform displayTransform;
   @Nullable
   private final Display.Billboard trackingRotation;
   @Nullable
   private final Float shadowStrength;
   @Nullable
   private final Float shadowRadius;
   private final float displayWidth;
   private final float displayHeight;
   @NotNull
   private final Vector3f translation;
   @NotNull
   private final Quaternionf leftRotation;
   @NotNull
   private final Quaternionf rightRotation;
   private final long delay;
   private final boolean offsetAgainstBlocks;
   @NotNull
   private final TriState cullable;
   @NotNull
   private Vector3f scale;

   public FurnitureProperties(Color glowColor, Float viewRange, Display.Brightness brightness, ItemDisplay.ItemDisplayTransform displayTransform, Display.Billboard trackingRotation, Float shadowStrength, Float shadowRadius, float displayWidth, float displayHeight, Vector3f translation, Quaternionf leftRotation, Quaternionf rightRotation, long delay, boolean offsetAgainstBlocks, TriState cullable) {
      super();
      this.glowColor = glowColor;
      this.viewRange = viewRange;
      this.brightness = brightness;
      this.displayTransform = displayTransform;
      this.trackingRotation = trackingRotation;
      this.shadowStrength = shadowStrength;
      this.shadowRadius = shadowRadius;
      this.displayWidth = displayWidth;
      this.displayHeight = displayHeight;
      this.translation = translation;
      this.leftRotation = leftRotation;
      this.rightRotation = rightRotation;
      this.delay = delay;
      this.offsetAgainstBlocks = offsetAgainstBlocks;
      this.cullable = cullable;
      this.scale = new Vector3f(1.0F, 1.0F, 1.0F);
   }

   @Nullable
   public final Color getGlowColor() {
      return this.glowColor;
   }

   @Nullable
   public final Float getViewRange() {
      return this.viewRange;
   }

   @Nullable
   public final Display.Brightness getBrightness() {
      return this.brightness;
   }

   @NotNull
   public final ItemDisplay.ItemDisplayTransform getDisplayTransform() {
      return this.displayTransform;
   }

   @Nullable
   public final Display.Billboard getTrackingRotation() {
      return this.trackingRotation;
   }

   @Nullable
   public final Float getShadowStrength() {
      return this.shadowStrength;
   }

   @Nullable
   public final Float getShadowRadius() {
      return this.shadowRadius;
   }

   public final float getDisplayWidth() {
      return this.displayWidth;
   }

   public final float getDisplayHeight() {
      return this.displayHeight;
   }

   @NotNull
   public final Vector3f getTranslation() {
      return this.translation;
   }

   @NotNull
   public final Quaternionf getLeftRotation() {
      return this.leftRotation;
   }

   @NotNull
   public final Quaternionf getRightRotation() {
      return this.rightRotation;
   }

   public final long getDelay_UwyO8pc/* $FF was: getDelay-UwyO8pc*/() {
      return this.delay;
   }

   public final boolean getOffsetAgainstBlocks() {
      return this.offsetAgainstBlocks;
   }

   @NotNull
   public final TriState getCullable() {
      return this.cullable;
   }

   @NotNull
   public final Vector3f getScale() {
      return this.scale;
   }

   public FurnitureProperties(@NotNull ConfigurationSection configSection) {
      org.joml.Quaternionf quaternionf;
      org.joml.Quaternionf quaternionf2;
      org.bukkit.entity.Display.Billboard billboard;
      Color color;
      Float number;
      Float number2;
      Float number3;
      float value;
      float value2;
      ItemDisplay.ItemDisplayTransform itemDisplayTransform;
      block: {
         color = DyeCommandKt.toColor(NexoYamlKt.stringOrEmpty(configSection, "glow_color"));
         number = NexoYamlKt.floatOrNull(configSection, "view_range");
         number2 = NexoYamlKt.floatOrNull(configSection, "shadow_strength");
         number3 = NexoYamlKt.floatOrNull(configSection, "shadow_radius");
         value = NexoYamlKt.float_(configSection, "display_width", 0.0F);
         value2 = NexoYamlKt.float_(configSection, "display_height", 0.0F);
         String str = NexoYamlKt.stringOrNull(configSection, "display_transform");
         if (str != null) {
            EnumUtils enumUtils = EnumUtils.INSTANCE;
            String str3 = str;
            Object[] objs = ItemDisplayTransform.values();
            int tracking = 0;
            int value3 = objs.length;

            Enum enumValue2;
            while(true) {
               if (tracking >= value3) {
                  enumValue2 = null;
                  break;
               }

               Object element = objs[tracking];
               Enum enumValue6 = (Enum)element;
               int value4 = 0;
               if (StringsKt.equals(enumValue6.name(), str3, true)) {
                  enumValue2 = (Enum)element;
                  break;
               }

               ++tracking;
            }

            Enum enumValue3 = enumValue2;
            if (enumValue2 == null) {
               int value5 = 0;
               String itemID = NexoYamlKt.getRootId(configSection);
               Logs.logError("Use of illegal ItemDisplayTransform <i>" + str3 + "</i> in furniture <gold>" + itemID, false);
               Iterable iterable = (Iterable)FurnitureProperties.EntriesMappings.entries$0;
               Logs.logWarn("Allowed ones are: <gold>" + CollectionsKt.joinToString(iterable, (CharSequence)", ", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", FurnitureProperties::_init_$lambda$0$0), false);
               Logs.logWarn("Setting transform to <i>" + ItemDisplayTransform.NONE + "</i> for furniture: <gold>" + itemID, false);
               enumValue3 = (Enum)ItemDisplayTransform.NONE;
            }

            ItemDisplay.ItemDisplayTransform itemDisplayTransform2 = (ItemDisplay.ItemDisplayTransform)enumValue3;
            if (itemDisplayTransform2 != null) {
               itemDisplayTransform = itemDisplayTransform2;
               break block;
            }
         }

         itemDisplayTransform = ItemDisplayTransform.NONE;
      }

      ItemDisplay.ItemDisplayTransform itemDisplayTransform3;
      Vector3f vector3f;
      block: {
         itemDisplayTransform3 = itemDisplayTransform;
         vector3f = NexoYamlKt.vector3f(configSection, "translation");
         Float number4 = NexoYamlKt.floatOrNull(configSection, "left_rotation");
         if (number4 != null) {
            float it = ((Number)number4).floatValue();
            int value6 = 0;
            quaternionf2 = QuaternionUtils.INSTANCE.rotationFromYawPitch(it, 0.0F);
            if (quaternionf2 != null) {
               break block;
            }
         }

         quaternionf2 = NexoYamlKt.quaternionf(configSection, "left_rotation");
         if (quaternionf2 == null) {
            quaternionf2 = new Quaternionf();
         }
      }

      Quaternionf quaternionf3;
      block: {
         quaternionf3 = quaternionf2;
         Float number5 = NexoYamlKt.floatOrNull(configSection, "right_rotation");
         if (number5 != null) {
            float it = ((Number)number5).floatValue();
            int it_1 = 0;
            quaternionf = QuaternionUtils.INSTANCE.rotationFromYawPitch(0.0F, it_1);
            if (quaternionf != null) {
               break block;
            }
         }

         quaternionf = NexoYamlKt.quaternionf(configSection, "right_rotation");
         if (quaternionf == null) {
            quaternionf = new Quaternionf();
         }
      }

      Quaternionf quaternionf4;
      block: {
         quaternionf4 = quaternionf;
         String str2 = NexoYamlKt.stringOrNull(configSection, "tracking_rotation");
         if (str2 != null) {
            EnumUtils enumUtils2 = EnumUtils.INSTANCE;
            String str4 = str2;
            Object[] objs2 = Billboard.values();
            int value7 = 0;
            int transform = objs2.length;

            Enum enumValue4;
            while(true) {
               if (value7 >= transform) {
                  enumValue4 = null;
                  break;
               }

               Object obj = objs2[value7];
               Enum enumValue7 = (Enum)obj;
               int value8 = 0;
               if (StringsKt.equals(enumValue7.name(), str4, true)) {
                  enumValue4 = (Enum)obj;
                  break;
               }

               ++value7;
            }

            Enum enumValue5 = enumValue4;
            if (enumValue4 == null) {
               int value9 = 0;
               String itemID = NexoYamlKt.getRootId(configSection);
               Logs.logError("Use of illegal tracking-rotation " + str4 + " in " + itemID + " furniture.", false);
               Iterable iterable2 = (Iterable)FurnitureProperties.EntriesMappings.entries$1;
               Logs.logError("Allowed ones are: " + CollectionsKt.joinToString(iterable2, (CharSequence)", ", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", FurnitureProperties::_init_$lambda$3$0), false);
               Logs.logWarn("Set tracking-rotation to " + Billboard.FIXED + " for " + itemID, false);
               enumValue5 = (Enum)Billboard.FIXED;
            }

            Display.Billboard billboard2 = (Display.Billboard)enumValue5;
            if (billboard2 != null) {
               billboard = billboard2;
               break block;
            }
         }

         billboard = Billboard.FIXED;
      }

      Display.Billboard transform = billboard;
      ConfigurationSection configurationSection = NexoYamlKt.section(configSection, "brightness");
      Display.Brightness brightness2;
      if (configurationSection != null) {
         ConfigurationSection it = configurationSection;
         int value10 = 0;
         brightness2 = new Display.Brightness(it.getInt("block_light"), it.getInt("sky_light"));
      } else {
         brightness2 = null;
      }

      Display.Brightness brightness3 = brightness2;
      long unbox_impl = (RangesKt.coerceAtLeast(Duration.box_impl(NexoYamlKt.duration(configSection, "delay", (DurationUnit)null)), Duration.box_impl(Duration.Companion.getZERO_UwyO8pc()))).unbox_impl();
      boolean tracking = configSection.getBoolean("offset_against_blocks", true);
      TriState triState = NexoYamlKt.triState(configSection, "cullable");
      this(color, number, brightness3, itemDisplayTransform3, transform, number2, number3, value, value2, vector3f, quaternionf3, quaternionf4, unbox_impl, tracking, triState);
      this.scale = NexoYamlKt.vector3f(configSection, "scale", this.isFixedTransform() ? 0.5F : 1.0F);
   }

   public final boolean isFixedTransform() {
      return this.displayTransform == ItemDisplayTransform.FIXED;
   }

   public final boolean isNoneTransform() {
      return this.displayTransform == ItemDisplayTransform.NONE;
   }

   private static final CharSequence _init_$lambda$0$0(ItemDisplay.ItemDisplayTransform it) {
      return (CharSequence)it.name();
   }

   private static final CharSequence _init_$lambda$3$0(Display.Billboard it) {
      return (CharSequence)it.name();
   }

   public static final class EntriesMappings {
      public static final kotlin.enums.EnumEntries entries$0 = kotlin.enums.EnumEntriesKt.enumEntries(org.bukkit.entity.ItemDisplay.ItemDisplayTransform.values());
      public static final kotlin.enums.EnumEntries entries$1 = kotlin.enums.EnumEntriesKt.enumEntries(org.bukkit.entity.Display.Billboard.values());
   }
}
