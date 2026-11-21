package litebans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;

public final class CacheModule extends PluginModule {
   public static final CacheModuleCompanion CACHE_MODULE_COMPANION = new CacheModuleCompanion(null);
   public static final boolean FLAG_2 = false;
   private static final Map return9 = new ConcurrentHashMap();

   public CacheModule(@Nullable Platform platform) {
      super(platform);
   }

   @Override
   public void enable() {
   }

   public static final Map obtainMap() {
      return return9;
   }
}
