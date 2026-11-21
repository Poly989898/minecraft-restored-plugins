package litebans;

import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

@PlatformMarker(obtainByteValue = 0)
public final class BukkitLockdownListener extends ListenerModule {
   private boolean flag13;
   private Object[] objArray = new Object[0];

   public BukkitLockdownListener(@NotNull Platform platform) {
      super(platform);
   }

   @Override
   public void disable() {
      this.platform.unregisterAllListeners();
      Object[] objs = this.objArray;
      int value = 0;

      for (int i = objs.length; value < i; value++) {
         Object obj = objs[value];
         ProtocolLibHelper.handlePluginModule2(this, obj);
      }
   }

   @Override
   public boolean obtainFlag() {
      return this.platform.getPlatformId() == 0;
   }

   @Override
   public void enable() {
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      module.enable();
      Settings settings = module.getSettings();
      new BukkitPreLoginListener(this.platform).obtainLiteBukkitListenerImpl4();
      Platform platform = this.platform;
      boolean isCheckAfterJoin2 = settings.isCheckAfterJoin();
      if (isCheckAfterJoin2) {
         PlatformListener[] platformListeners = new PlatformListener[1];
         byte b = 0;
         PlatformListener liteBukkitListener = new BukkitJoinListener(this.platform);
         platformListeners[b] = liteBukkitListener;
         platform.registerListeners(platformListeners);
      }

      this.platform.runSync(() -> handleConfigModule(module, this));
      if (module.getSettings().isMutesEnabled()) {
         this.platform.runSync(() -> {
            try {
               this.flag13 = settings.isUseProtocollib() && this.platform.createBridge().hasPlugin("ProtocolLib");
               if (this.flag13 && LockdownState.LOCKDOWN_STATE.obtainFlag()) {
                  this.platform.getLogger().warning("ProtocolLib error 00 - manager is missing.");
               } else if (this.flag13) {
                  PluginModule pluginModule = this;
                  Object[] objs = new Object[]{new KotlinPacketAdapterSub2(this.platform), new KotlinPacketAdapterSub(this.platform)};
                  int value = 0;

                  for (int i = objs.length; value < i; value++) {
                     Object obj = objs[value];
                     ProtocolLibHelper.handlePluginModule(pluginModule, obj);
                  }

                  this.objArray = Arrays.copyOf(objs, objs.length);
               }
            } catch (Throwable e) {
               this.platform.getLogger().warning("Failed to add ProtocolLib packet listener.");
               if (!(e instanceof LinkageError) && !(e instanceof ReflectiveOperationException) || module.isVerbose()) {
                  module.handleConfigError(e);
               }
            }
         });
         new KotlinBukkitListenerImpl(this.platform).obtainKotlinBukkitListenerImpl();
      }
   }

   public BukkitListener computeBukkitListener(@NotNull Named named) {
      return Intrinsics.areEqual(named.getName(), "lockdown") ? new BukkitLoginListener(this.platform) : (BukkitListener)super.computePlatformListener(named);
   }

   private static final void handleConfigModule(ConfigModule configModule, BukkitLockdownListener bukkitLockdownListener) {
      if (configModule.isAuthMeSupported()) {
         try {
            bukkitLockdownListener.platform.checkText("fr.xephi.authme.events.LoginEvent", () -> {
               AuthMeLoginListener[] liteBukkitListenerImpls = new AuthMeLoginListener[]{new AuthMeLoginListener(bukkitLockdownListener.platform)};
               return liteBukkitListenerImpls;
            });
         } catch (Exception e) {
            PluginModule pluginModule = bukkitLockdownListener;
            String text = "Hooking into AuthMe failed.";
            pluginModule.platform.getLogger().warning(text);
            e.printStackTrace();
         }
      }
   }

   @Override
   public PlatformListener computePlatformListener(Named named) {
      return this.computeBukkitListener(named);
   }
}
