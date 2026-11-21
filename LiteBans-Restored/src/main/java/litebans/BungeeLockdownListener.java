package litebans;

import org.jetbrains.annotations.NotNull;

@PlatformMarker(obtainByteValue = 1)
public final class BungeeLockdownListener extends ListenerModule {
   public BungeeLockdownListener(@NotNull Platform platform) {
      super(platform);
   }

   @Override
   public void disable() {
      this.platform.unregisterAllListeners();
   }

   @Override
   public boolean obtainFlag() {
      return this.platform.getPlatformId() == 1;
   }

   @Override
   public void enable() {
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      module.enable();
      new BungeeLoginListener(this.platform).obtainLiteBungeeListenerImpl();
      if (module.getSettings().isMutesEnabled()) {
         new PoolBungeeListenerImpl(this.platform).obtainPoolBungeeListenerImpl();
      }
   }

   public BungeeListener computeBungeeListener(@NotNull Named named) {
      return Intrinsics.areEqual(named.getName(), "lockdown") ? new BungeeLockdownLoginListener(this.platform) : (BungeeListener)super.computePlatformListener(named);
   }

   @Override
   public PlatformListener computePlatformListener(Named named) {
      return this.computeBungeeListener(named);
   }
}
