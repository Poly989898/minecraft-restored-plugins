package com.nexomc.nexo.mechanics.furniture.evolution;

import com.nexomc.nexo.utils.NexoYamlKt;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.ranges.RangesKt;
import kotlin.time.Duration;
import kotlin.time.DurationUnit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0013\u0018\u00002\u00020\u0001BG\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\b\b\u0002\u0010\b\u001a\u00020\u0005\u0012\b\u0010\t\u001a\u0004\u0018\u00010\n\u0012\u0006\u0010\u000b\u001a\u00020\u0005¢\u0006\u0004\b\f\u0010\rB\u0011\b\u0016\u0012\u0006\u0010\u000e\u001a\u00020\u000f¢\u0006\u0004\b\f\u0010\u0010J\u0006\u0010!\u001a\u00020\u0005R\u0013\u0010\u0002\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\u0013\u001a\u0004\b\u0011\u0010\u0012R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001a\u0010\u0006\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0015\"\u0004\b\u0019\u0010\u0017R\u001a\u0010\u0007\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u0015\"\u0004\b\u001b\u0010\u0017R\u001a\u0010\b\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u0015\"\u0004\b\u001d\u0010\u0017R\u0013\u0010\t\u001a\u0004\u0018\u00010\n¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010\u000b\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b \u0010\u0015¨\u0006\""},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/evolution/EvolvingFurniture;", "", "delay", "Lkotlin/time/Duration;", "minimumLightLevel", "", "lightBoostTick", "rainBoostTick", "boneMealChance", "nextStage", "", "probability", "<init>", "(JIIIILjava/lang/String;ILkotlin/jvm/internal/DefaultConstructorMarker;)V", "plantSection", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getDelay-UwyO8pc", "()J", "J", "getMinimumLightLevel", "()I", "setMinimumLightLevel", "(I)V", "getLightBoostTick", "setLightBoostTick", "getRainBoostTick", "setRainBoostTick", "getBoneMealChance", "setBoneMealChance", "getNextStage", "()Ljava/lang/String;", "getProbability", "delayInMillis", "com.nexomc:core"}
)
public final class EvolvingFurniture {
   private final long delay;
   private int minimumLightLevel;
   private int lightBoostTick;
   private int rainBoostTick;
   private int boneMealChance;
   @Nullable
   private final String nextStage;
   private final int probability;

   private EvolvingFurniture(long delay, int minimumLightLevel, int lightBoostTick, int rainBoostTick, int boneMealChance, String nextStage, int probability) {
      this.delay = delay;
      this.minimumLightLevel = minimumLightLevel;
      this.lightBoostTick = lightBoostTick;
      this.rainBoostTick = rainBoostTick;
      this.boneMealChance = boneMealChance;
      this.nextStage = nextStage;
      this.probability = probability;
   }

   public final long getDelay_UwyO8pc/* $FF was: getDelay-UwyO8pc*/() {
      return this.delay;
   }

   public final int getMinimumLightLevel() {
      return this.minimumLightLevel;
   }

   public final void setMinimumLightLevel(int value) {
      this.minimumLightLevel = value;
   }

   public final int getLightBoostTick() {
      return this.lightBoostTick;
   }

   public final void setLightBoostTick(int value) {
      this.lightBoostTick = value;
   }

   public final int getRainBoostTick() {
      return this.rainBoostTick;
   }

   public final void setRainBoostTick(int value) {
      this.rainBoostTick = value;
   }

   public final int getBoneMealChance() {
      return this.boneMealChance;
   }

   public final void setBoneMealChance(int value) {
      this.boneMealChance = value;
   }

   @Nullable
   public final String getNextStage() {
      return this.nextStage;
   }

   public final int getProbability() {
      return this.probability;
   }

   public EvolvingFurniture(@NotNull ConfigurationSection plantSection) {
      long value = NexoYamlKt.duration(plantSection, "delay", (DurationUnit)null);
      int value2 = plantSection.getInt("rain_boost_tick");
      String string = plantSection.getString("next_stage");
      Object obj = plantSection.get("probability", 1);
      this(value, 0, 0, value2, 0, string, RangesKt.coerceAtLeast((int)((double)1.0F / (Double)obj), 0));
      ConfigurationSection configurationSection = NexoYamlKt.section(plantSection, "light_boost");
      if (configurationSection != null) {
         ConfigurationSection it = configurationSection;
         int value3 = 0;
         this.minimumLightLevel = it.getInt("minimum_light_level");
         this.lightBoostTick = it.getInt("boost_tick");
      }

      this.boneMealChance = RangesKt.coerceIn(plantSection.getInt("bone_meal_chance"), 0, 100);
   }

   public final int delayInMillis() {
      return (int)Duration.getInWholeMilliseconds_impl(this.delay);
   }
}
