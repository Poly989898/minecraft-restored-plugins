package com.nexomc.nexo.utils;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import kotlin.Metadata;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\"\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u001c\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a4\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\b\b\u0000\u0010\u0002*\u00020\u0003*\b\u0012\u0004\u0012\u0002H\u00020\u00042\u0012\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u00070\u0006\u001a \u0010\b\u001a\b\u0012\u0004\u0012\u0002H\u00020\t\"\b\b\u0000\u0010\u0002*\u00020\u0003*\b\u0012\u0004\u0012\u0002H\u00020\u0004¨\u0006\n"},
   d2 = {"toKeySet", "Lio/papermc/paper/registry/set/RegistryKeySet;", "T", "Lorg/bukkit/Keyed;", "Lio/papermc/paper/registry/RegistryKey;", "values", "", "Lio/papermc/paper/registry/TypedKey;", "toRegistry", "Lorg/bukkit/Registry;", "com.nexomc:core"}
)
public final class RegistryUtilsKt {
   @NotNull
   public static final <T extends Keyed> RegistryKeySet<T> toKeySet(@NotNull RegistryKey<T> registryKey, @NotNull Iterable<? extends TypedKey<T>> values) {
      RegistryKeySet registryKeySet = RegistrySet.keySet(registryKey, (Iterable)values);
      return registryKeySet;
   }

   @NotNull
   public static final <T extends Keyed> Registry<T> toRegistry(@NotNull RegistryKey<T> registryKey) {
      Registry registry = RegistryAccess.registryAccess().getRegistry(registryKey);
      return registry;
   }
}
