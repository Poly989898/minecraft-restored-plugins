package com.nexomc.nexo.utils.wrappers;

import kotlin.Metadata;
import kotlin.jvm.JvmField;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u001a\u0010\u0004\u001a\u00070\u0005¢\u0006\u0002\b\u00068\u0006X\u0087\u0004\u0092\u0002\u0002\b\u0007¢\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\u00070\u0005¢\u0006\u0002\b\u0006¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0016\u0010\u000b\u001a\u00070\u0005¢\u0006\u0002\b\u0006¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\n¨\u0006\r"},
   d2 = {"Lcom/nexomc/nexo/utils/wrappers/ParticleWrapper;", "", "<init>", "()V", "DUST", "Lorg/bukkit/Particle;", "Lorg/jspecify/annotations/Nullable;", "Lkotlin/jvm/JvmField;", "SPLASH", "getSPLASH", "()Lorg/bukkit/Particle;", "HAPPY_VILLAGER", "getHAPPY_VILLAGER", "com.nexomc:core"}
)
public final class ParticleWrapper {
   @NotNull
   public static final ParticleWrapper INSTANCE = new ParticleWrapper();
   @JvmField
   @NotNull
   public static final Particle DUST;
   @NotNull
   private static final Particle SPLASH;
   @NotNull
   private static final Particle HAPPY_VILLAGER;

   private ParticleWrapper() {
   }

   @NotNull
   public final Particle getSPLASH() {
      return SPLASH;
   }

   @NotNull
   public final Particle getHAPPY_VILLAGER() {
      return HAPPY_VILLAGER;
   }

   static {
      Keyed keyed = Registry.PARTICLE_TYPE.get(NamespacedKey.minecraft("dust"));
      DUST = (Particle)keyed;
      keyed = Registry.PARTICLE_TYPE.get(NamespacedKey.minecraft("splash"));
      SPLASH = (Particle)keyed;
      keyed = Registry.PARTICLE_TYPE.get(NamespacedKey.minecraft("happy_villager"));
      HAPPY_VILLAGER = (Particle)keyed;
   }
}
