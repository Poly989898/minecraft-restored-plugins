package litebans;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.ArchiveNoneFormattingEventsEnum.File;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricPlugin implements DedicatedServerModInitializer, Platform {
   private static final Logger g_f = LoggerFactory.getLogger(FabricPlugin.class);
   private FabricMeta f_f;
   private java.util.logging.Logger b_f;
   private ModContainer e_f;
   private SchedulerHolder j_f;
   private final PluginLifecycle d_f = new PluginLifecycle(this);
   private FabricCommandSource h_f;
   private boolean c_f = false;

   @Override
   public void DateFormats() {
      try {
         this.j_f.a_34();
      } catch (Throwable var3) {
         throw SneakyThrow.<RuntimeException>rethrow(var3);
      }
   }

   @Override
   public ConfigFile CpioArchiveInputStream() {
      return this.d_f.o_f;
   }

   @Override
   public PluginModule[] ZipArchiveInputStream() {
      return this.d_f.j_5();
   }

   @Override
   public void YamlThreadLocal() {
      this.d_f.i_7();
   }

   @Override
   public boolean AbstractPlatform(PluginModule var1) {
      boolean var3 = var1.d_2();
      if (var3) {
         if (var1 instanceof FabricPlayerModule) {
            this.AbstractPlatform((PlayerModule)var1);
         }

         if (var1 instanceof FabricLiteCommandManager) {
            this.AbstractPlatform((LiteCommandManager)var1);
         }
      }

      return var3;
   }

   @Override
   public ClassUtil f_1() {
      return this.d_f.k_f;
   }

   @Override
   public LiteCommandManager UpdateModule() {
      return this.d_f.e_f;
   }

   @Override
   public void AbstractPlatform(LiteCommandManager var1) {
      this.d_f.e_f = var1;
   }

   @Override
   public PlayerModule PunishmentTypeSwitchMap2() {
      return this.d_f.f_f;
   }

   @Override
   public LiteSender JsonMessageModule() {
      return this.d_f.g_f;
   }

   @Override
   public PluginModule AbstractPlatform(Class var1) {
      return this.d_f.b_1(var1);
   }

   @Override
   public PluginModule PunishmentTypeSwitchMap2(Class var1) {
      return this.d_f.a_3(var1);
   }

   @Override
   public PluginModule[] PunishmentLookupModule() {
      return this.d_f.d_f;
   }

   @Override
   public String getName() {
      return this.f_f.d_59();
   }

   @Override
   public int a_2() {
      return this.d_f.l_4();
   }

   @Override
   public LiteSender[] ConfigModule() {
      return this.d_f.b_30();
   }

   @Override
   public Map DatabaseModule() {
      return this.d_f.b_f;
   }

   @Override
   public ThreadPoolExecutor CacheModule() {
      ThreadPoolExecutor var3 = this.d_f.h_f;
      if (var3 != null && !var3.isShutdown() || !this.d_f.j_f.get() && !this.d_f.m_f.get()) {
         return var3;
      }

      ThreadFactoryBuilder var2 = this.d_f.d_15();
      return this.d_f.AbstractPlatform(var2);
   }

   @Override
   public long g_1() {
      return this.d_f.h_7();
   }

   @Override
   public final byte EntryQueueModule() {
      return 3;
   }

   @Override
   public PlatformBridge DumpArchiveInputStream() {
      return new FabricBridge(this);
   }

   @Override
   public String e_1() {
      return this.f_f.b_117();
   }

   @Override
   public LiteSender ThrottleModule() {
      return this.h_f.AbstractPlatform(this.h_f.f_16(), this::f_2);
   }

   @Override
   public ConfigFile BroadcastModule() {
      ConfigFile var2 = this.AbstractPlatform(new File(this.getDataFolder(), "config.yml"));
      this.d_f.o_f = var2;
      return var2;
   }

   @Override
   public File getDataFolder() {
      return FabricLoader.getInstance().getConfigDir().resolve(this.f_f.c_87()).toFile();
   }

   public int b_4() {
      return this.h_f.b_38();
   }

   @Override
   public java.util.logging.Logger getLogger() {
      return this.b_f;
   }

   @Override
   public void TarArchiveInputStream() {
      this.j_f = new SchedulerHolder();
      this.d_f.k_4();
   }

   @Override
   public boolean CommandQueueModule() {
      return this.d_f.n_f.get();
   }

   @Override
   public boolean isEnabled() {
      return this.d_f.j_f.get();
   }

   @Override
   public boolean PluginModule() {
      return this.d_f.p_f;
   }

   @Override
   public ConfigFile AbstractPlatform(File var1) {
      try {
         return this.d_f.i_f.AbstractPlatform(this, var1);
      } catch (Throwable var4) {
         throw SneakyThrow.<RuntimeException>rethrow(var4);
      }
   }

   public ScheduledExecutorService c_1() {
      return this.j_f.AbstractPlatform(this.d_f);
   }

   @Override
   public boolean UInt() {
      return this.h_f.a_56();
   }

   @Override
   public boolean SyncModule() {
      return this.h_f.e_18();
   }

   @Override
   public boolean JarArchiveInputStream() {
      return false;
   }

   public void onInitializeServer() {
      try {
         this.f_f = new FabricMeta(ClassUtil.a_5(FabricMeta.class));
         String var2 = this.f_f.c_87();
         this.b_f = new PlatformLogger(LoggerFactory.getLogger(var2), var2);
         this.h_f = FabricCommandSourceFactory.PunishmentTypeSwitchMap2(
            this, ((ModContainer)FabricLoader.getInstance().getModContainer("fabric-api").get()).getMetadata().getVersion().getFriendlyString().split("\\+")[1]
         );
         ServerLifecycleEvents.SERVER_STARTING.register(this::AbstractPlatform);
         ServerLifecycleEvents.SERVER_STOPPING.register(this::PunishmentTypeSwitchMap2);
         this.d_f.c_22();
      } catch (Throwable var4) {
         throw SneakyThrow.<RuntimeException>rethrow(var4);
      }
   }

   private final void AbstractPlatform(MinecraftServer var1) {
      this.h_f.a_1(var1);
      this.e_f = (ModContainer)FabricLoader.getInstance().getModContainer(this.f_f.c_87()).orElse(null);

      try {
         Class.forName("me.lucko.fabric.api.permissions.v0.Permissions");
         this.c_f = true;
      } catch (ClassNotFoundException var4) {
      }

      this.TarArchiveInputStream();
   }

   private final void PunishmentTypeSwitchMap2(MinecraftServer var1) {
      this.YamlThreadLocal();
   }

   @Override
   public void PunishmentTypeSwitchMap2(@Nullable PlatformListener[] var1) {
      if (var1 != null) {
         for (PlatformListener var5 : var1) {
            if (var5 instanceof LitePlatformListenerSub) {
               LitePlatformListenerSub var6 = (LitePlatformListenerSub)var5;
               var6.a_7();
            }
         }
      }
   }

   @Override
   public boolean AbstractPlatform(String var1, Supplier var2) {
      return this.d_f.a_1(var1, var2);
   }

   @Override
   public void UInt(Runnable var1) {
      this.h_f.a_1(var1);
   }

   @Override
   public void PunishmentTypeSwitchMap2(Runnable var1, long var2) {
      this.AbstractPlatform(() -> this.YamlThreadLocal(var1), var2);
   }

   @Override
   public void AbstractPlatform(Runnable var1) {
      this.CacheModule().execute(var1);
   }

   @Override
   public void AbstractPlatform(Runnable var1, long var2) {
      if (var2 < 0L) {
         throw new IllegalArgumentException("Invalid delay " + var2);
      }

      try {
         this.c_1().schedule(var1, var2 * 50L, TimeUnit.MILLISECONDS);
      } catch (RejectedExecutionException var6) {
         this.a_1(var6);
      }
   }

   @Override
   public void PunishmentTypeSwitchMap2(Runnable var1) {
      this.AbstractPlatform(var1);
   }

   @Override
   public void AbstractPlatform(Runnable var1, long var2, long var4) {
      this.PunishmentTypeSwitchMap2(() -> this.ArchiveInputStream(var1), var2, var4);
   }

   @Override
   public void PunishmentTypeSwitchMap2(Runnable var1, long var2, long var4) {
      try {
         this.c_1().scheduleAtFixedRate(var1, var2 * 50L, var4 * 50L, TimeUnit.MILLISECONDS);
      } catch (RejectedExecutionException var8) {
         this.a_1(var8);
      }
   }

   private final void a_1(Exception var1) {
      try {
         if (this.d_f.m_f.get()) {
            this.getLogger().warning("Startup was cancelled");
         } else {
            throw var1;
         }
      } catch (Throwable var4) {
         throw SneakyThrow.<RuntimeException>rethrow(var4);
      }
   }

   @Override
   public void a_1(String var1) {
      ConfigFile.AbstractPlatform(this.getDataFolder(), var1, null, false);
   }

   @Override
   public void AbstractPlatform(Bootstrap var1) {
      this.d_f.i_f = var1;
   }

   @Override
   public void AbstractPlatform(PlayerModule var1) {
      this.d_f.f_f = var1;
   }

   @Override
   public void PeriodicTaskModule() {
   }

   @Override
   public void AbstractPlatform(@Nullable PlatformListener[] var1) {
   }

   @Override
   public LiteSender PunishmentTypeSwitchMap2(Object var1) {
      return this.h_f.c_2(var1);
   }

   @Override
   public LiteSender AbstractPlatform(Object var1) {
      return this.h_f.b_3(var1);
   }

   public ModContainer h_1() {
      return this.e_f;
   }

   public PluginLifecycle a_4() {
      return this.d_f;
   }

   public FabricCommandSource g_3() {
      return this.h_f;
   }

   public boolean d_3() {
      return this.c_f;
   }

   private final void ArchiveInputStream(Runnable var1) {
      this.UInt(var1);
   }

   private final void YamlThreadLocal(Runnable var1) {
      this.UInt(var1);
   }

   private final Object f_2() {
      return this.h_f.f_16();
   }
}
