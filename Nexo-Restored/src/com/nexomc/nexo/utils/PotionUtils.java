package com.nexomc.nexo.utils;

import com.nexomc.nexo.decomp.ResultShim;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¨\u0006\b"},
   d2 = {"Lcom/nexomc/nexo/utils/PotionUtils;", "", "<init>", "()V", "getEffectType", "Lorg/bukkit/potion/PotionEffectType;", "effect", "", "com.nexomc:core"}
)
public final class PotionUtils {
   @NotNull
   public static final PotionUtils INSTANCE = new PotionUtils();

   private PotionUtils() {
   }

   @Nullable
   public final PotionEffectType getEffectType(@Nullable String effect) {
      CharSequence charSequence = (CharSequence)effect;
      if (charSequence == null || charSequence.length() == 0) {
         return null;
      } else {
         PotionUtils potionUtils = this;

         Object obj;
         try {
            PotionUtils potionUtils2 = potionUtils;
            int value = 0;
            Registry registry = Registry.POTION_EFFECT_TYPE;
            NamespacedKey namespacedKey = NamespacedKey.fromString(effect);
            obj = ResultShim.ok((PotionEffectType)registry.get(namespacedKey));
         } catch (Throwable e) {
            obj = ResultShim.ok(ResultKt.createFailure(e));
         }

         PotionEffectType potionEffectType = (PotionEffectType)(ResultShim.isFailure(obj) ? null : obj);
         if (potionEffectType == null) {
            potionEffectType = PotionEffectType.getByName(effect);
            if (potionEffectType == null) {
               potionEffectType = PotionEffectType.getByKey(NamespacedKey.fromString(effect));
            }
         }

         return potionEffectType;
      }
   }
}
