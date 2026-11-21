package litebans;

import litebans.api.PlayerProvider;
import org.jetbrains.annotations.NotNull;

public final class LiteBansPlayerProvider extends PlayerProvider {
   private final Platform platform;
   private final ConfigModule configModule;

   public LiteBansPlayerProvider(@NotNull Platform platform) {
      this.platform = platform;
      this.configModule = (ConfigModule)this.platform.getModule(ConfigModule.class);
   }

   @Override
   public String provide(@NotNull String text) {
      return text;
   }

   @Override
   public String toString() {
      return "PlayerProvider";
   }
}
