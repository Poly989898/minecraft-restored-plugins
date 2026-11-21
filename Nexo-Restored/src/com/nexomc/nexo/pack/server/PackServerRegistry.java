package com.nexomc.nexo.pack.server;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u00062\u0010\u0010\r\u001a\f\u0012\u0004\u0012\u00020\b0\u0007j\u0002`\tH\u0007b\u0002\b\u000eJ\u0016\u0010\u000f\u001a\u0004\u0018\u00010\b2\u0006\u0010\f\u001a\u00020\u0006H\u0007b\u0002\b\u000eR$\u0010\u0004\u001a\u0018\u0012\u0004\u0012\u00020\u0006\u0012\u000e\u0012\f\u0012\u0004\u0012\u00020\b0\u0007j\u0002`\t0\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00060\u0011¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013¨\u0006\u0014"},
   d2 = {"Lcom/nexomc/nexo/pack/server/PackServerRegistry;", "", "<init>", "()V", "registry", "", "", "Lkotlin/Function0;", "Lcom/nexomc/nexo/pack/server/NexoPackServer;", "Lcom/nexomc/nexo/pack/server/PackServerFactory;", "register", "", "type", "factory", "Lkotlin/jvm/JvmStatic;", "create", "allRegisteredTypes", "", "getAllRegisteredTypes", "()Ljava/util/Set;", "com.nexomc:core"}
)
public final class PackServerRegistry {
   @NotNull
   public static final PackServerRegistry INSTANCE = new PackServerRegistry();
   @NotNull
   private static final Map<String, Function0<NexoPackServer>> registry = (Map)(new LinkedHashMap());
   @NotNull
   private static final Set<String> allRegisteredTypes;

   private PackServerRegistry() {
   }

   @JvmStatic
   public static final void register(@NotNull String type, @NotNull Function0<? extends NexoPackServer> factory) {
      Map map = registry;
      String str = type.toUpperCase(Locale.ROOT);
      map.put(str, factory);
   }

   @JvmStatic
   @Nullable
   public static final NexoPackServer create(@NotNull String type) {
      Map map = registry;
      String str = type.toUpperCase(Locale.ROOT);
      Function0 function0 = (Function0)map.get(str);
      return function0 != null ? (NexoPackServer)function0.invoke() : null;
   }

   @NotNull
   public final Set<String> getAllRegisteredTypes() {
      return allRegisteredTypes;
   }

   static {
      allRegisteredTypes = registry.keySet();
   }
}
