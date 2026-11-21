package litebans;

import litebans.api.RandomID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LiteBansRandomId extends RandomID {
   private final Platform platform;

   public LiteBansRandomId(@NotNull Platform platform) {
      this.platform = platform;
   }

   @Override
   public String convert(long value) {
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      return module.obtainKotlinData2().computeText(value);
   }

   @Override
   public long reveal(@Nullable String text) {
      if (text == null) {
         return -1L;
      }

      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      return module.obtainKotlinData2().computeLongValue(text);
   }
}
