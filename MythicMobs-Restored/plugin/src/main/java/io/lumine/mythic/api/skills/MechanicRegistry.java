package io.lumine.mythic.api.skills;

import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

public final class MechanicRegistry {
   private static final MechanicProvider[] EMPTY = new MechanicProvider[0];
   private static final CopyOnWriteArrayList<MechanicProvider> PROVIDERS = new CopyOnWriteArrayList<>();

   private MechanicRegistry() {
   }

   public static void registerProvider(MechanicProvider provider) {
      PROVIDERS.addIfAbsent(provider);
      PROVIDERS.sort(Comparator.comparingInt(MechanicProvider::getPriority).reversed());
   }

   public static void unregisterProvider(MechanicProvider provider) {
      PROVIDERS.remove(provider);
   }

   public static MechanicProvider[] snapshot() {
      return PROVIDERS.toArray(EMPTY);
   }
}
