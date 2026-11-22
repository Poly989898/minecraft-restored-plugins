package litebans;

import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.jetbrains.annotations.NotNull;

public final class BukkitPreLoginListener implements BukkitListener {
   private final Platform platform;
   private final BroadcastModule broadcastModule;
   private final ConfigModule configModule;

   public BukkitPreLoginListener(@NotNull Platform platform) {
      this.platform = platform;
      this.broadcastModule = (BroadcastModule)this.platform.getModule(BroadcastModule.class);
      this.configModule = (ConfigModule)this.platform.getModule(ConfigModule.class);
   }

   public final BroadcastModule getBroadcastModule() {
      return this.broadcastModule;
   }

   public final ConfigModule getConfigModule() {
      return this.configModule;
   }

   @EventHandler(priority = EventPriority.LOW)
   public final void handleAsyncPlayerPreLoginEvent(@NotNull AsyncPlayerPreLoginEvent asyncPlayerPreLoginEvent) {
      if (this.configModule.isDebugAtLeast(1)) {
         long value = System.nanoTime();
         this.handleAsyncPlayerPreLoginEvent3(asyncPlayerPreLoginEvent);
         long value2 = System.nanoTime() - value;
         double number = value2 / 1000.0 / 1000.0;
         this.configModule.logDebug("Handled login event: " + NumberFormats.computeText(number) + " ms.");
      } else {
         this.handleAsyncPlayerPreLoginEvent3(asyncPlayerPreLoginEvent);
      }
   }

   private final void handleAsyncPlayerPreLoginEvent2(AsyncPlayerPreLoginEvent asyncPlayerPreLoginEvent) {
      long value = System.nanoTime();
      this.handleAsyncPlayerPreLoginEvent3(asyncPlayerPreLoginEvent);
      long value2 = System.nanoTime() - value;
      double number = value2 / 1000.0 / 1000.0;
      this.configModule.logDebug("Handled login event: " + NumberFormats.computeText(number) + " ms.");
   }

   private final void handleAsyncPlayerPreLoginEvent3(AsyncPlayerPreLoginEvent asyncPlayerPreLoginEvent) {
      if (asyncPlayerPreLoginEvent.getLoginResult() == Result.ALLOWED) {
         if (!this.platform.obtainFlag3()) {
            if (!this.platform.obtainFlag2()) {
               this.platform.getLogger().severe("Ignored " + asyncPlayerPreLoginEvent.getClass().getSimpleName() + "; plugin is not enabled yet!");
               if (FeatureDetection.obtainFlag()) {
                  this.platform
                     .getLogger()
                     .severe("Enable late-bind in spigot.yml to fix this problem, otherwise banned players can join before LiteBans has been loaded.");
               }
            } else {
               this.perform2();
               this.broadcastModule.getLoginHandler().handleObj(asyncPlayerPreLoginEvent);
            }
         }
      }
   }

   private final void perform2() {
      if (FeatureDetection.obtainFlag2()) {
         if (this.configModule.getObj18() != null) {
            Object obj = this.configModule.getObj18();
            Intrinsics.checkNotNullExpressionValue(obj, "");
            AtomicBoolean atomicBoolean = ((ServerListPlusBanProvider)obj).getAtomicBoolean();
            if (atomicBoolean.compareAndSet(false, true)) {
               obj = this.configModule.getObj18();
               Intrinsics.checkNotNullExpressionValue(obj, "");
               ((ServerListPlusBanProvider)obj).handleObj(this.configModule.getServerListPlus());
            }
         }
      }
   }

   public BukkitPreLoginListener obtainLiteBukkitListenerImpl4() {
      Platform platform = this.platform;
      PlatformListener[] platformListeners = new PlatformListener[]{this};
      platform.registerListeners(platformListeners);
      return this;
   }

   @Override
   public PlatformListener obtainPlatformListener() {
      return this.obtainLiteBukkitListenerImpl4();
   }
}
