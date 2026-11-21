package litebans;

import org.jetbrains.annotations.Nullable;

public abstract class ListenerModule extends PluginModule {
   public ListenerModule(Platform platform) {
      super(platform);
   }

   public @Nullable PlatformListener computePlatformListener(Named named) {
      return null;
   }
}
