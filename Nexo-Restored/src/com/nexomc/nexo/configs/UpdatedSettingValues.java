package com.nexomc.nexo.configs;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.text.StringsKt;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u000b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B7\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0014\b\u0002\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007¢\u0006\u0004\b\n\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u001d\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011j\u0002\b\u0012j\u0002\b\u0013¨\u0006\u0014"},
   d2 = {"Lcom/nexomc/nexo/configs/UpdatedSettingValues;", "", "path", "", "oldValue", "newValue", "condition", "Lkotlin/Function1;", "Lorg/bukkit/configuration/file/YamlConfiguration;", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/jvm/functions/Function1;)V", "getPath", "()Ljava/lang/String;", "getOldValue", "getNewValue", "getCondition", "()Lkotlin/jvm/functions/Function1;", "NEXO_SERVER", "HOSTED_POLYMATH", "com.nexomc:core"}
)
public enum UpdatedSettingValues {
   NEXO_SERVER("Pack.server.type", "NEXO", "HERMES", UpdatedSettingValues::_init_$lambda$0),
   HOSTED_POLYMATH("Pack.server.type", "POLYMATH", "HERMES", UpdatedSettingValues::_init_$lambda$1);

   @NotNull
   private final String path;
   @NotNull
   private final String oldValue;
   @NotNull
   private final String newValue;
   @NotNull
   private final Function1<? super YamlConfiguration, Boolean> condition;

   private UpdatedSettingValues(String path, String oldValue, String newValue, Function1<? super YamlConfiguration, Boolean> condition) {
      this.path = path;
      this.oldValue = oldValue;
      this.newValue = newValue;
      this.condition = (Function1)(Object)condition;
   }

   @NotNull
   public final String getPath() {
      return this.path;
   }

   @NotNull
   public final String getOldValue() {
      return this.oldValue;
   }

   @NotNull
   public final String getNewValue() {
      return this.newValue;
   }

   @NotNull
   public final Function1<YamlConfiguration, Boolean> getCondition() {
      return (Function1<YamlConfiguration, Boolean>)(Function1)this.condition;
   }


   private static final boolean _init_$lambda$0(YamlConfiguration it) {
      return true;
   }

   private static final boolean _init_$lambda$1(YamlConfiguration it) {
      String string = it.getString("Pack.server.polymath.server");
      if (string == null) {
         string = "atlas.nexomc.com";
      }

      return StringsKt.equals(string, "atlas.nexomc.com", true);
   }
}
