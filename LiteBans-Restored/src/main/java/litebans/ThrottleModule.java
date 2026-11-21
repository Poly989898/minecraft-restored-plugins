package litebans;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ThrottleModule extends PluginModule {
   private final Lazy configModuleLazy = KotlinStub5.computeLazy(() -> (ConfigModule)this.platform.getModule(ConfigModule.class));
   private final Set set2 = Collections.newSetFromMap(new ConcurrentHashMap());

   public ThrottleModule(@NotNull Platform platform) {
      super(platform);
   }

   public final ConfigModule obtainConfigModule() {
      Lazy lazy = this.configModuleLazy;
      return (ConfigModule)lazy.getValue();
   }

   @Override
   public void enable() {
      this.obtainConfigModule().enable();
      this.set2.clear();
      long notificationThrottleTicks = this.obtainConfigModule().getSettings().getNotificationThrottleTicks();
      if (notificationThrottleTicks > 0L) {
         this.platform.runAsyncTimer(this.set2::clear, notificationThrottleTicks, notificationThrottleTicks);
      }
   }

   @Override
   public void reloadConfig() {
      this.enable();
   }

   public final boolean checkByteValue(byte b, @Nullable String text) {
      if (text == null) {
         return false;
      }

      if (this.obtainConfigModule().getSettings().getNotificationThrottleTicks() <= 0) {
         return false;
      }

      boolean flag = !this.set2.add("" + b + ':' + text);
      ConfigModule configModule = this.obtainConfigModule();
      byte b2 = 10;
      if (configModule.isDebugAtLeast(b2)) {
         configModule.logDebug("Throttled! " + text);
      }

      return flag;
   }

   public final void handleByteValue(byte b, @Nullable String text) {
      if (text != null) {
         if (this.obtainConfigModule().getSettings().getNotificationThrottleTicks() > 0) {
            this.set2.remove("" + b + ':' + text);
         }
      }
   }

}
