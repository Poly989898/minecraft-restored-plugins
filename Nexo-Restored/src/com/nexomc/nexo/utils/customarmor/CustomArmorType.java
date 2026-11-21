package com.nexomc.nexo.utils.customarmor;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.logs.Logs;
import java.util.Locale;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.enums.EnumEntries;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.text.Regex;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u0000 \u00072\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0007B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006¨\u0006\b"},
   d2 = {"Lcom/nexomc/nexo/utils/customarmor/CustomArmorType;", "", "<init>", "(Ljava/lang/String;I)V", "NONE", "TRIMS", "COMPONENT", "Companion", "com.nexomc:core"}
)
public enum CustomArmorType {
   NONE,
   TRIMS,
   COMPONENT;

   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private static final Regex itemIdRegex = new Regex("(.*)_(helmet|chestplate|leggings|boots|elytra|wolf_armor|llama_armor|horse_armor|camel_saddle|donkey_saddle|horse_saddle|mule_saddle|pig_saddle|skeleton_horse_saddle|strider_saddle|zombie_horse_saddle|harness)");
   @NotNull
   private static final Lazy<CustomArmorType> setting$delegate = LazyKt.lazy(CustomArmorType::setting_delegate$lambda$0);


   private static final CustomArmorType setting_delegate$lambda$0() {
      return Companion.fromString(Settings.CUSTOM_ARMOR_TYPE.toString());
   }

   @NotNull
   public static final CustomArmorType getSetting() {
      return Companion.getSetting();
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0010\u001a\u00020\t2\u0006\u0010\u0011\u001a\u00020\u0012R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R%\u0010\b\u001a\u00020\t8FX\u0087\u0084\u0002r\u0002\b\u000f¢\u0006\u0012\n\u0004\b\r\u0010\u000e\u0012\u0004\b\n\u0010\u0003\u001a\u0004\b\u000b\u0010\f¨\u0006\u0013"},
      d2 = {"Lcom/nexomc/nexo/utils/customarmor/CustomArmorType$Companion;", "", "<init>", "()V", "itemIdRegex", "Lkotlin/text/Regex;", "getItemIdRegex", "()Lkotlin/text/Regex;", "setting", "Lcom/nexomc/nexo/utils/customarmor/CustomArmorType;", "getSetting$annotations", "getSetting", "()Lcom/nexomc/nexo/utils/customarmor/CustomArmorType;", "setting$delegate", "Lkotlin/Lazy;", "Lkotlin/jvm/JvmStatic;", "fromString", "type", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final Regex getItemIdRegex() {
         return CustomArmorType.itemIdRegex;
      }

      @NotNull
      public final CustomArmorType getSetting() {
         Lazy lazy = CustomArmorType.setting$delegate;
         return (CustomArmorType)lazy.getValue();
      }

      @NotNull
      public final CustomArmorType fromString(@NotNull String type) {
         Companion companion = this;

         Object obj2;
         try {
            Companion companion2 = companion;
            int value = 0;
            Locale locale = Locale.getDefault();
            String str = type.toUpperCase(locale);
            obj2 = ResultShim.ok(CustomArmorType.valueOf(str));
         } catch (Throwable e) {
            obj2 = ResultShim.ok(ResultKt.createFailure(e));
         }

         Object obj;
         if (ResultShim.exceptionOrNull(obj2) == null) {
            obj = obj2;
         } else {
            int value2 = 0;
            Logs.logError("Invalid custom armor type: " + type, false);
            Logs.logError("Defaulting to NONE.", false);
            obj = CustomArmorType.NONE;
         }

         return (CustomArmorType)obj;
      }
   }
}
