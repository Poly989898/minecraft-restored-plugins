package io.lumine.mythic.api.skills;

import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ConditionRegistry {
   private static final ConditionProvider[] EMPTY = new ConditionProvider[0];
   private static final CopyOnWriteArrayList<ConditionProvider> PROVIDERS = new CopyOnWriteArrayList<>();

   private ConditionRegistry() {
   }

   public static void registerProvider(ConditionProvider provider) {
      PROVIDERS.addIfAbsent(provider);
      PROVIDERS.sort(Comparator.comparingInt(ConditionProvider::getPriority).reversed());
   }

   public static void unregisterProvider(ConditionProvider provider) {
      PROVIDERS.remove(provider);
   }

   public static ConditionProvider[] snapshot() {
      return PROVIDERS.toArray(EMPTY);
   }
}
