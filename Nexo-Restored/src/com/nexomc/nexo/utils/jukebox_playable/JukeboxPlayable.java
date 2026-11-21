package com.nexomc.nexo.utils.jukebox_playable;

import com.google.gson.JsonObject;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.DurationUtilsKt;
import com.nexomc.nexo.utils.JsonBuilder;
import com.nexomc.nexo.utils.NexoYamlKt;
import java.util.Map;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import kotlin.time.Duration;
import kotlin.time.DurationKt;
import kotlin.time.DurationUnit;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B1\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\f\u0010\rB\u0019\b\u0016\u0012\u0006\u0010\u000e\u001a\u00020\u000f\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\f\u0010\u0010B%\b\u0016\u0012\u0012\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00010\u0012\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\f\u0010\u0014J\t\u0010(\u001a\u00020\u0003HÆ\u0003J\u0010\u0010)\u001a\u0004\u0018\u00010\u0005HÆ\u0003¢\u0006\u0002\u0010\u0018J\u0010\u0010*\u001a\u00020\u0007HÆ\u0003¢\u0006\u0004\b+\u0010\u001bJ\t\u0010,\u001a\u00020\tHÆ\u0003J\t\u0010-\u001a\u00020\u000bHÆ\u0003JD\u0010.\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000bHÆ\u0001¢\u0006\u0004\b/\u00100J\u0014\u00101\u001a\u0002022\b\u00103\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u00104\u001a\u00020\u0003HÖ\u0081\u0004J\n\u00105\u001a\u00020\u0013HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0015\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\n\n\u0002\u0010\u0019\u001a\u0004\b\u0017\u0010\u0018R\u0013\u0010\u0006\u001a\u00020\u0007¢\u0006\n\n\u0002\u0010\u001c\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u001f\u0010!\u001a\u00060\"j\u0002`#8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b&\u0010'\u001a\u0004\b$\u0010%¨\u00066"},
   d2 = {"Lcom/nexomc/nexo/utils/jukebox_playable/JukeboxPlayable;", "", "comparatorOutput", "", "range", "", "duration", "Lkotlin/time/Duration;", "description", "Lnet/kyori/adventure/text/Component;", "soundId", "Lnet/kyori/adventure/key/Key;", "<init>", "(ILjava/lang/Float;JLnet/kyori/adventure/text/Component;Lnet/kyori/adventure/key/Key;Lkotlin/jvm/internal/DefaultConstructorMarker;)V", "jukeboxSection", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;Lnet/kyori/adventure/key/Key;)V", "jukeboxMap", "", "", "(Ljava/util/Map;Lnet/kyori/adventure/key/Key;)V", "getComparatorOutput", "()I", "getRange", "()Ljava/lang/Float;", "Ljava/lang/Float;", "getDuration-UwyO8pc", "()J", "J", "getDescription", "()Lnet/kyori/adventure/text/Component;", "getSoundId", "()Lnet/kyori/adventure/key/Key;", "jukeboxJson", "Lcom/google/gson/JsonObject;", "Lcom/nexomc/nexo/utils/JsonObject;", "getJukeboxJson", "()Lcom/google/gson/JsonObject;", "jukeboxJson$delegate", "Lkotlin/Lazy;", "component1", "component2", "component3", "component3-UwyO8pc", "component4", "component5", "copy", "copy-1Y68eR8", "(ILjava/lang/Float;JLnet/kyori/adventure/text/Component;Lnet/kyori/adventure/key/Key;)Lcom/nexomc/nexo/utils/jukebox_playable/JukeboxPlayable;", "equals", "", "other", "hashCode", "toString", "com.nexomc:core"}
)
public final class JukeboxPlayable {
   private final int comparatorOutput;
   @Nullable
   private final Float range;
   private final long duration;
   @NotNull
   private final Component description;
   @NotNull
   private final Key soundId;
   @NotNull
   private final Lazy jukeboxJson$delegate;

   private JukeboxPlayable(int comparatorOutput, Float range, long duration, Component description, Key soundId) {
      super();
      this.comparatorOutput = comparatorOutput;
      this.range = range;
      this.duration = duration;
      this.description = description;
      this.soundId = soundId;
      this.jukeboxJson$delegate = LazyKt.lazy((() -> JukeboxPlayable.jukeboxJson_delegate$lambda$0(this)));
   }

   public final int getComparatorOutput() {
      return this.comparatorOutput;
   }

   @Nullable
   public final Float getRange() {
      return this.range;
   }

   public final long getDuration_UwyO8pc/* $FF was: getDuration-UwyO8pc*/() {
      return this.duration;
   }

   @NotNull
   public final Component getDescription() {
      return this.description;
   }

   @NotNull
   public final Key getSoundId() {
      return this.soundId;
   }

   public JukeboxPlayable(@NotNull ConfigurationSection jukeboxSection, @NotNull Key soundId) {
      int value = RangesKt.coerceIn(jukeboxSection.getInt("comparator_output", 15), 0, 15);
      Float number = NexoYamlKt.floatOrNull(jukeboxSection, "range");
      Duration duration2 = Duration.box_impl(NexoYamlKt.duration(jukeboxSection, "duration", (DurationUnit)null));
      Duration.Companion companion = Duration.Companion;
      long unbox_impl = (RangesKt.coerceAtLeast(duration2, Duration.box_impl(DurationKt.toDuration(1, DurationUnit.SECONDS)))).unbox_impl();
      Component richMessage = jukeboxSection.getRichMessage("description");
      if (richMessage == null) {
         TextComponent textComponent = Component.text(soundId.value());
         richMessage = (Component)textComponent;
      }

      this(value, number, unbox_impl, richMessage, soundId);
   }

   public JukeboxPlayable(@NotNull Map<String, ? extends Object> jukeboxMap, @NotNull Key soundId) {
      int value;
      JukeboxPlayable jukeboxPlayable;
      block: {
         /* this-alias removed */
         Object obj = jukeboxMap.get("comparator_output");
         if (obj != null) {
            String str = obj.toString();
            if (str != null) {
               Integer number = StringsKt.toIntOrNull(str);
               if (number != null) {
                  value = RangesKt.coerceIn((int)(number), 0, 15);
                  break block;
               }
            }
         }

         value = 15;
      }

      Float number2;
      block: {
         Object obj2 = jukeboxMap.get("range");
         if (obj2 != null) {
            String str2 = obj2.toString();
            if (str2 != null) {
               Float number3 = StringsKt.toFloatOrNull(str2);
               if (number3 != null) {
                  Float number4 = number3;
                  float it = ((Number)number4).floatValue();
                  int value2 = value;
                  int value3 = 0;
                  boolean flag = it > 0.0F;
                  /* this-alias removed */
                  value = value2;
                  number2 = flag ? number4 : null;
                  break block;
               }
            }
         }

         number2 = null;
      }

      long value4;
      block: {
         Object obj3 = jukeboxMap.get("duration");
         if (obj3 != null) {
            String str3 = obj3.toString();
            if (str3 != null) {
               Duration duration2 = DurationUtilsKt.toDuration(str3, DurationUnit.SECONDS);
               if (duration2 != null) {
                  Comparable comparable = (Comparable)duration2;
                  Duration.Companion companion = Duration.Companion;
                  value4 = (RangesKt.<Duration>coerceAtLeast((Duration)comparable, Duration.box_impl(DurationKt.toDuration(1, DurationUnit.SECONDS)))).unbox_impl();
                  break block;
               }
            }
         }

         Duration.Companion companion2 = Duration.Companion;
         value4 = DurationKt.toDuration(1, DurationUnit.SECONDS);
      }

      Component component = AdventureUtils.INSTANCE.getMINI_MESSAGE().deserialize(String.valueOf(jukeboxMap.get("description")));
      this(value, number2, value4, component, soundId);
   }

   @NotNull
   public final JsonObject getJukeboxJson() {
      Lazy lazy = this.jukeboxJson$delegate;
      return (JsonObject)lazy.getValue();
   }

   public final int component1() {
      return this.comparatorOutput;
   }

   @Nullable
   public final Float component2() {
      return this.range;
   }

   public final long component3_UwyO8pc/* $FF was: component3-UwyO8pc*/() {
      return this.duration;
   }

   @NotNull
   public final Component component4() {
      return this.description;
   }

   @NotNull
   public final Key component5() {
      return this.soundId;
   }

   @NotNull
   public final JukeboxPlayable copy_1Y68eR8/* $FF was: copy-1Y68eR8*/(int comparatorOutput, @Nullable Float range, long duration, @NotNull Component description, @NotNull Key soundId) {
      return new JukeboxPlayable(comparatorOutput, range, duration, description, soundId);
   }

   @NotNull
   public String toString() {
      int value = this.comparatorOutput;
      return "JukeboxPlayable(comparatorOutput=" + value + ", range=" + this.range + ", duration=" + Duration.toString_impl(this.duration) + ", description=" + this.description + ", soundId=" + this.soundId + ")";
   }

   public int hashCode() {
      int result = Integer.hashCode(this.comparatorOutput);
      result = result * 31 + (this.range == null ? 0 : this.range.hashCode());
      result = result * 31 + Duration.hashCode_impl(this.duration);
      result = result * 31 + this.description.hashCode();
      result = result * 31 + this.soundId.hashCode();
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof JukeboxPlayable)) {
         return false;
      } else {
         JukeboxPlayable jukeboxPlayable = (JukeboxPlayable)other;
         if (this.comparatorOutput != jukeboxPlayable.comparatorOutput) {
            return false;
         } else if (!Intrinsics.areEqual(this.range, jukeboxPlayable.range)) {
            return false;
         } else if (!Duration.equals_impl0(this.duration, jukeboxPlayable.duration)) {
            return false;
         } else if (!Intrinsics.areEqual(this.description, jukeboxPlayable.description)) {
            return false;
         } else {
            return Intrinsics.areEqual(this.soundId, jukeboxPlayable.soundId);
         }
      }
   }

   private static final JsonObject jukeboxJson_delegate$lambda$0(JukeboxPlayable jukeboxPlayable) {
      return JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonObject(), "sound_event", JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonObject(), "sound_id", jukeboxPlayable.soundId.asString()), "range", jukeboxPlayable.range)), "description", JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonObject(), "text", AdventureUtils.LEGACY_SERIALIZER.serialize(jukeboxPlayable.description))), "length_in_seconds", DurationUtilsKt.toFloat_VtjQ1oo(jukeboxPlayable.duration, DurationUnit.SECONDS)), "comparator_output", RangesKt.coerceIn(jukeboxPlayable.comparatorOutput, 0, 15));
   }
}
