package com.nexomc.nexo.utils;

import java.util.Locale;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0010\u0010\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003JA\u0010\u0004\u001a\u0002H\u0005\"\u000e\b\u0000\u0010\u0005*\b\u0012\u0004\u0012\u0002H\u00050\u0006*\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u0002H\u00050\t2\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u0002H\u00050\u000b¢\u0006\u0002\u0010\fJ;\u0010\u0004\u001a\u0002H\r\"\u0010\b\u0000\u0010\r\u0018\u0001*\b\u0012\u0004\u0012\u0002H\r0\u0006*\u00020\u00072\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u0002H\r0\u000bH\u0086\bø\u0001\u0000¢\u0006\u0002\u0010\u000e\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\u000f"},
   d2 = {"Lcom/nexomc/nexo/utils/EnumUtils;", "", "<init>", "()V", "toEnumOrElse", "E", "", "", "enumClass", "Ljava/lang/Class;", "fallback", "Lkotlin/Function1;", "(Ljava/lang/String;Ljava/lang/Class;Lkotlin/jvm/functions/Function1;)Ljava/lang/Enum;", "T", "(Ljava/lang/String;Lkotlin/jvm/functions/Function1;)Ljava/lang/Enum;", "com.nexomc:core"}
)
public final class EnumUtils {
   @NotNull
   public static final EnumUtils INSTANCE = new EnumUtils();

   private EnumUtils() {
   }

   @NotNull
   public final <E extends Enum<E>> E toEnumOrElse(@NotNull String str2, @NotNull Class<E> enumClass, @NotNull Function1<? super String, ? extends E> fallback) {
      String str = str2.toUpperCase(Locale.ROOT);
      Enum enumValue2 = org.apache.commons.lang3.EnumUtils.getEnum(enumClass, str);
      if (enumValue2 == null) {
         enumValue2 = (Enum)fallback.invoke(str2);
      }

      return (E)enumValue2;
   }
}
