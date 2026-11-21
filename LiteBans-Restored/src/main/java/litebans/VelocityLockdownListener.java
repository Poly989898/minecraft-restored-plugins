package litebans;

import org.jetbrains.annotations.NotNull;

@PlatformMarker(obtainByteValue = 2)
public final class VelocityLockdownListener extends ListenerModule {
   public VelocityLockdownListener(@NotNull Platform platform) {
      super(platform);
   }

   @Override
   public void disable() {
      this.platform.unregisterAllListeners();
   }

   @Override
   public boolean obtainFlag() {
      return this.platform.getPlatformId() == 2;
   }

   @Override
   public void enable() {
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      module.enable();
      new VelocityConnectListener(this.platform).obtainLiteVelocityListenerImpl3();
      if (module.getSettings().isMutesEnabled()) {
         new VelocityChatListener(this.platform).obtainLiteVelocityListenerImpl2();
      }
   }

   public VelocityListener computeVelocityListener(@NotNull Named named) {
      return Intrinsics.areEqual(named.getName(), "lockdown") ? new VelocityLockdownConnectListener(this.platform) : (VelocityListener)super.computePlatformListener(named);
   }

   @Override
   public PlatformListener computePlatformListener(Named named) {
      return this.computeVelocityListener(named);
   }
}
