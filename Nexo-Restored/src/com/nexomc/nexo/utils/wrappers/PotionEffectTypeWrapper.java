package com.nexomc.nexo.utils.wrappers;

import com.nexomc.nexo.decomp.ResultShim;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.JvmField;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\n\u001a\u0004\u0018\u00010\u00052\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0002R\u0015\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004\u0092\u0002\u0002\b\u0006¢\u0006\u0002\n\u0000R\u0015\u0010\u0007\u001a\u00020\u00058\u0006X\u0087\u0004\u0092\u0002\u0002\b\u0006¢\u0006\u0002\n\u0000R\u0015\u0010\b\u001a\u00020\u00058\u0006X\u0087\u0004\u0092\u0002\u0002\b\u0006¢\u0006\u0002\n\u0000R\u0017\u0010\t\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004\u0092\u0002\u0002\b\u0006¢\u0006\u0002\n\u0000¨\u0006\r"},
   d2 = {"Lcom/nexomc/nexo/utils/wrappers/PotionEffectTypeWrapper;", "", "<init>", "()V", "HASTE", "Lorg/bukkit/potion/PotionEffectType;", "Lkotlin/jvm/JvmField;", "CONDUIT_POWER", "MINING_FATIGUE", "WIND_CHARGED", "getEffectType", "effect", "", "com.nexomc:core"}
)
public final class PotionEffectTypeWrapper {
   @NotNull
   public static final PotionEffectTypeWrapper INSTANCE = new PotionEffectTypeWrapper();
   @JvmField
   @NotNull
   public static final PotionEffectType HASTE;
   @JvmField
   @NotNull
   public static final PotionEffectType CONDUIT_POWER;
   @JvmField
   @NotNull
   public static final PotionEffectType MINING_FATIGUE;
   @JvmField
   @Nullable
   public static final PotionEffectType WIND_CHARGED;

   private PotionEffectTypeWrapper() {
   }

   private final PotionEffectType getEffectType(String effect) {
      CharSequence charSequence = (CharSequence)effect;
      if (charSequence == null || charSequence.length() == 0) {
         return null;
      } else {
         PotionEffectTypeWrapper potionEffectTypeWrapper = this;

         Object obj;
         try {
            PotionEffectTypeWrapper potionEffectTypeWrapper2 = potionEffectTypeWrapper;
            int value = 0;
            obj = ResultShim.ok((PotionEffectType)Registry.POTION_EFFECT_TYPE.get(NamespacedKey.minecraft(effect)));
         } catch (Throwable e) {
            obj = ResultShim.ok(ResultKt.createFailure(e));
         }

         PotionEffectType potionEffectType = (PotionEffectType)(ResultShim.isFailure(obj) ? null : obj);
         if (potionEffectType == null) {
            potionEffectType = PotionEffectType.getByName(effect);
            if (potionEffectType == null) {
               potionEffectType = PotionEffectType.getByKey(NamespacedKey.minecraft(effect));
            }
         }

         return potionEffectType;
      }
   }

   static {
      PotionEffectType effectType = INSTANCE.getEffectType("haste");
      HASTE = effectType;
      effectType = INSTANCE.getEffectType("conduit_power");
      CONDUIT_POWER = effectType;
      effectType = INSTANCE.getEffectType("mining_fatigue");
      MINING_FATIGUE = effectType;
      WIND_CHARGED = INSTANCE.getEffectType("wind_charged");
   }
}
