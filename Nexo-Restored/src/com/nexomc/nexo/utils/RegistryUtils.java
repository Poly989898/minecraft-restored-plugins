package com.nexomc.nexo.utils;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.Tag;
import kotlin.Metadata;
import net.kyori.adventure.key.Key;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J$\u0010\u0004\u001a\b\u0012\u0004\u0012\u0002H\u00060\u0005\"\b\b\u0000\u0010\u0006*\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u0002H\u00060\tJ,\u0010\n\u001a\b\u0012\u0004\u0012\u0002H\u00060\u000b\"\b\b\u0000\u0010\u0006*\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u0002H\u00060\t2\u0006\u0010\f\u001a\u00020\r¨\u0006\u000e"},
   d2 = {"Lcom/nexomc/nexo/utils/RegistryUtils;", "", "<init>", "()V", "getRegistry", "Lorg/bukkit/Registry;", "T", "Lorg/bukkit/Keyed;", "registryKey", "Lio/papermc/paper/registry/RegistryKey;", "getTag", "Lio/papermc/paper/registry/tag/Tag;", "key", "Lnet/kyori/adventure/key/Key;", "com.nexomc:core"}
)
public final class RegistryUtils {
   @NotNull
   public static final RegistryUtils INSTANCE = new RegistryUtils();

   private RegistryUtils() {
   }

   @NotNull
   public final <T extends Keyed> Registry<T> getRegistry(@NotNull RegistryKey<T> registryKey) {
      Registry registry = RegistryAccess.registryAccess().getRegistry(registryKey);
      return registry;
   }

   @NotNull
   public final <T extends Keyed> Tag<T> getTag(@NotNull RegistryKey<T> registryKey, @NotNull Key key) {
      Tag tag = RegistryAccess.registryAccess().getRegistry(registryKey).getTag(registryKey.tagKey(key));
      return tag;
   }
}
