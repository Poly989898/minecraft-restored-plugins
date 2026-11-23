package com.nexomc.nexo;

import com.nexomc.nexo.configs.ConfigsManager;
import com.nexomc.nexo.configs.SoundManager;
import com.nexomc.nexo.converter.Converter;
import com.nexomc.nexo.fonts.FontManager;
import com.nexomc.nexo.utils.actions.ClickActionManager;
import com.nexomc.nexo.utils.breaker.ModernBreakerManager;
import com.nexomc.nexo.utils.inventories.InventoryManager;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.libs.folialib.FoliaLib;
import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.compatibilities.CompatibilitiesManager;
import com.nexomc.nexo.compatibilities.worldguard.NexoWorldguardFlags;
import com.nexomc.nexo.configs.ConfigsManager;
import com.nexomc.nexo.configs.Message;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.configs.SettingsUpdater;
import com.nexomc.nexo.configs.SoundManager;
import com.nexomc.nexo.converter.Converter;
import com.nexomc.nexo.converter.ItemsAdderConverter;
import com.nexomc.nexo.converter.OraxenConverter;
import com.nexomc.nexo.dialog.NexoDialogListener;
import com.nexomc.nexo.fonts.FontManager;
import com.nexomc.nexo.fonts.NexoTranslator;
import com.nexomc.nexo.items.ItemUpdater;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mods.CreativeInventoryListener;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.pack.PackGenerator;
import com.nexomc.nexo.pack.multipack.MultipackManager;
import com.nexomc.nexo.pack.server.EmptyServer;
import com.nexomc.nexo.pack.server.NexoPackServer;
import com.nexomc.nexo.proxy.ProxyHelpers;
import com.nexomc.nexo.recipes.RecipesManager;
import com.nexomc.nexo.utils.NexoDatapack;
import com.nexomc.nexo.utils.NexoMetrics;
import com.nexomc.nexo.utils.NoticeUtils;
import com.nexomc.nexo.utils.PluginUtils;
import com.nexomc.nexo.utils.Reloadable;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.nexo.utils.actions.ClickActionManager;
import com.nexomc.nexo.utils.breaker.BreakerManager;
import com.nexomc.nexo.utils.customarmor.CustomArmorListener;
import com.nexomc.nexo.utils.inventories.InventoryManager;
import com.nexomc.nexo.utils.inventories.NexoInventory;
import com.nexomc.protectionlib.ProtectionLib;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarFile;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0006\u0018\u0000 :2\u00020\u00012\u00020\u0002:\u0001:B\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\b\u00104\u001a\u000205H\u0016J\b\u00106\u001a\u000205H\u0016J\b\u00107\u001a\u000205H\u0016J\b\u00108\u001a\u000205H\u0016J\u0006\u0010\u0005\u001a\u00020\u0006J\u0006\u0010$\u001a\u00020%J\u0006\u0010\u000b\u001a\u00020\fJ\u0006\u0010\u0010\u001a\u00020\u0011J\u0006\u0010\u001f\u001a\u00020 J\u0006\u0010\u0015\u001a\u00020\u0016J\u0006\u0010+\u001a\u00020,J\u000e\u0010+\u001a\u0002052\u0006\u0010+\u001a\u00020,J\u0006\u0010)\u001a\u00020*J\u000e\u0010)\u001a\u0002052\u0006\u00109\u001a\u00020*J\u0006\u0010-\u001a\u00020.J\u0006\u0010\u001a\u001a\u00020\u001bR\u001b\u0010\u0005\u001a\u00020\u00068@X\u0080\u0084\u0002¢\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\bR\u001b\u0010\u000b\u001a\u00020\f8@X\u0080\u0084\u0002¢\u0006\f\n\u0004\b\u000f\u0010\n\u001a\u0004\b\r\u0010\u000eR\u001b\u0010\u0010\u001a\u00020\u00118@X\u0080\u0084\u0002¢\u0006\f\n\u0004\b\u0014\u0010\n\u001a\u0004\b\u0012\u0010\u0013R\u001b\u0010\u0015\u001a\u00020\u00168@X\u0080\u0084\u0002¢\u0006\f\n\u0004\b\u0019\u0010\n\u001a\u0004\b\u0017\u0010\u0018R\u001b\u0010\u001a\u001a\u00020\u001b8@X\u0080\u0084\u0002¢\u0006\f\n\u0004\b\u001e\u0010\n\u001a\u0004\b\u001c\u0010\u001dR\u001b\u0010\u001f\u001a\u00020 8@X\u0080\u0084\u0002¢\u0006\f\n\u0004\b#\u0010\n\u001a\u0004\b!\u0010\"R\u001b\u0010$\u001a\u00020%8@X\u0080\u0084\u0002¢\u0006\f\n\u0004\b(\u0010\n\u001a\u0004\b&\u0010'R\u000e\u0010)\u001a\u00020*X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010+\u001a\u00020,X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010-\u001a\u00020.X\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u00101\u001a\u0002002\u0006\u0010/\u001a\u000200@BX\u0086.¢\u0006\b\n\u0000\u001a\u0004\b2\u00103¨\u0006;"},
   d2 = {"Lcom/nexomc/nexo/NexoPlugin;", "Lorg/bukkit/plugin/java/JavaPlugin;", "Lcom/nexomc/nexo/utils/Reloadable;", "<init>", "()V", "configsManager", "Lcom/nexomc/nexo/configs/ConfigsManager;", "getConfigsManager$com_nexomc_core", "()Lcom/nexomc/nexo/configs/ConfigsManager;", "configsManager$delegate", "Lkotlin/Lazy;", "fontManager", "Lcom/nexomc/nexo/fonts/FontManager;", "getFontManager$com_nexomc_core", "()Lcom/nexomc/nexo/fonts/FontManager;", "fontManager$delegate", "soundManager", "Lcom/nexomc/nexo/configs/SoundManager;", "getSoundManager$com_nexomc_core", "()Lcom/nexomc/nexo/configs/SoundManager;", "soundManager$delegate", "invManager", "Lcom/nexomc/nexo/utils/inventories/InventoryManager;", "getInvManager$com_nexomc_core", "()Lcom/nexomc/nexo/utils/inventories/InventoryManager;", "invManager$delegate", "clickActionManager", "Lcom/nexomc/nexo/utils/actions/ClickActionManager;", "getClickActionManager$com_nexomc_core", "()Lcom/nexomc/nexo/utils/actions/ClickActionManager;", "clickActionManager$delegate", "breakerManager", "Lcom/nexomc/nexo/utils/breaker/BreakerManager;", "getBreakerManager$com_nexomc_core", "()Lcom/nexomc/nexo/utils/breaker/BreakerManager;", "breakerManager$delegate", "converter", "Lcom/nexomc/nexo/converter/Converter;", "getConverter$com_nexomc_core", "()Lcom/nexomc/nexo/converter/Converter;", "converter$delegate", "packServer", "Lcom/nexomc/nexo/pack/server/NexoPackServer;", "packGenerator", "Lcom/nexomc/nexo/pack/PackGenerator;", "multipackManager", "Lcom/nexomc/nexo/pack/multipack/MultipackManager;", "value", "Lcom/nexomc/libs/folialib/FoliaLib;", "foliaLib", "getFoliaLib", "()Lcom/nexomc/libs/folialib/FoliaLib;", "onLoad", "", "onEnable", "onDisable", "reload", "server", "Companion", "com.nexomc:core"}
)
public final class NexoPlugin extends JavaPlugin implements Reloadable {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Lazy configsManager$delegate;
   @NotNull
   private final Lazy fontManager$delegate;
   @NotNull
   private final Lazy soundManager$delegate;
   @NotNull
   private final Lazy invManager$delegate;
   @NotNull
   private final Lazy clickActionManager$delegate;
   @NotNull
   private final Lazy breakerManager$delegate;
   @NotNull
   private final Lazy converter$delegate;
   private NexoPackServer packServer;
   private PackGenerator packGenerator;
   @NotNull
   private final MultipackManager multipackManager;
   private FoliaLib foliaLib;
   private static NexoPlugin nexo;

   public NexoPlugin() {
      nexo = this;
      this.configsManager$delegate = LazyKt.lazy(() -> new ConfigsManager());
      this.fontManager$delegate = LazyKt.lazy(() -> new FontManager());
      this.soundManager$delegate = LazyKt.lazy(() -> new SoundManager());
      this.invManager$delegate = LazyKt.lazy(() -> new InventoryManager());
      this.clickActionManager$delegate = LazyKt.lazy(() -> new ClickActionManager());
      this.breakerManager$delegate = LazyKt.lazy(() -> new ModernBreakerManager());
      this.converter$delegate = LazyKt.lazy(() -> new Converter());
      this.multipackManager = new MultipackManager();
   }

   @NotNull
   public final ConfigsManager getConfigsManager$com_nexomc_core() {
      Lazy lazy = this.configsManager$delegate;
      return (ConfigsManager)lazy.getValue();
   }

   @NotNull
   public final FontManager getFontManager$com_nexomc_core() {
      Lazy lazy = this.fontManager$delegate;
      return (FontManager)lazy.getValue();
   }

   @NotNull
   public final SoundManager getSoundManager$com_nexomc_core() {
      Lazy lazy = this.soundManager$delegate;
      return (SoundManager)lazy.getValue();
   }

   @NotNull
   public final InventoryManager getInvManager$com_nexomc_core() {
      Lazy lazy = this.invManager$delegate;
      return (InventoryManager)lazy.getValue();
   }

   @NotNull
   public final ClickActionManager getClickActionManager$com_nexomc_core() {
      Lazy lazy = this.clickActionManager$delegate;
      return (ClickActionManager)lazy.getValue();
   }

   @NotNull
   public final BreakerManager getBreakerManager$com_nexomc_core() {
      Lazy lazy = this.breakerManager$delegate;
      return (BreakerManager)lazy.getValue();
   }

   @NotNull
   public final Converter getConverter$com_nexomc_core() {
      Lazy lazy = this.converter$delegate;
      return (Converter)lazy.getValue();
   }

   @NotNull
   public final FoliaLib getFoliaLib() {
      FoliaLib foliaLib2 = this.foliaLib;
      if (foliaLib2 != null) {
         return foliaLib2;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("foliaLib");
         return null;
      }
   }

   public void onLoad() {
      nexo = this;
      this.packServer = new EmptyServer();
      this.foliaLib = new FoliaLib((Plugin)this);
      this.getFoliaLib().getOptions().disableInvalidTickDebugMode();
      // WorldGuard is optional: its flag classes must not be touched when the plugin is absent
      try {
         Class.forName("com.sk89q.worldguard.protection.flags.Flag");
         NexoWorldguardFlags.INSTANCE.registerFlags();
      } catch (ClassNotFoundException | LinkageError ignored) {
         // WorldGuard not installed
      }
      NexoDatapack.Companion.clearOldDatapacks();
   }

   public void onEnable() {
      ProtectionLib.init(this);
      this.reload();
      ProtectionLib.debug = Settings.DEBUG.toBool();
      if (Settings.KEEP_UP_TO_DATE.toBool()) {
         (new SettingsUpdater()).handleSettingsUpdate();
      }

      this.packGenerator = new PackGenerator();
      Bukkit.getPluginManager().registerEvents(new CustomArmorListener(), (Plugin)this);
      Bukkit.getPluginManager().registerEvents(new ItemUpdater(), (Plugin)this);
      ProxyHelpers.INSTANCE.initialize();
      NMSHandlers.handler().creativeInventory().initialize();
      Bukkit.getPluginManager().registerEvents(new CreativeInventoryListener(), (Plugin)this);
      Bukkit.getPluginManager().registerEvents(new NexoDialogListener(), (Plugin)this);
      NexoMetrics.INSTANCE.initializeMetrics();
      MechanicsManager.INSTANCE.registerNativeMechanics(false);
      NexoPackServer.Companion.registerDefaultPackServers();
      this.getFoliaLib().getScheduler().runNextTick((a0_ -> NexoPlugin.onEnable$lambda$0(this, a0_)));
      CompatibilitiesManager.INSTANCE.enableCompatibilies();
   }

   public void onDisable() {
      NexoPlugin nexoPlugin = this;

      try {
         NexoPlugin nexoPlugin2 = nexoPlugin;
         int value = 0;
         NexoPackServer nexoPackServer = nexoPlugin2.packServer;
         if (nexoPackServer == null) {
            Intrinsics.throwUninitializedPropertyAccessException("packServer");
            nexoPackServer = null;
         }

         nexoPackServer.stop();
         NMSHandlers.handler().creativeInventory().uninitialize();
         HandlerList.unregisterAll((Plugin)nexoPlugin2);
         FurnitureFactory.Companion.unregisterEvolution();
         CompatibilitiesManager.INSTANCE.disableCompatibilities();
         Message.PLUGIN_UNLOADED.log();
         Object obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         Object obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

   }

   public void reload() {
      PluginUtils.INSTANCE.reload();
      this.getConfigsManager$com_nexomc_core().reload();
      NexoInventory.Companion.reload();
      this.getFontManager$com_nexomc_core().reload();
      this.getSoundManager$com_nexomc_core().reload();
      this.getConverter$com_nexomc_core().reload();
      this.getInvManager$com_nexomc_core().reload();
      this.multipackManager.reload();
      OraxenConverter.INSTANCE.convert();
      ItemsAdderConverter.INSTANCE.convert();
   }

   @NotNull
   public final ConfigsManager configsManager() {
      return this.getConfigsManager$com_nexomc_core();
   }

   @NotNull
   public final Converter converter() {
      return this.getConverter$com_nexomc_core();
   }

   @NotNull
   public final FontManager fontManager() {
      return this.getFontManager$com_nexomc_core();
   }

   @NotNull
   public final SoundManager soundManager() {
      return this.getSoundManager$com_nexomc_core();
   }

   @NotNull
   public final BreakerManager breakerManager() {
      return this.getBreakerManager$com_nexomc_core();
   }

   @NotNull
   public final InventoryManager invManager() {
      return this.getInvManager$com_nexomc_core();
   }

   @NotNull
   public final PackGenerator packGenerator() {
      PackGenerator packGenerator2 = this.packGenerator;
      if (packGenerator2 == null) {
         Intrinsics.throwUninitializedPropertyAccessException("packGenerator");
         packGenerator2 = null;
      }

      return packGenerator2;
   }

   public final void packGenerator(@NotNull PackGenerator packGenerator) {
      PackGenerator.Companion.stopPackGeneration();
      this.packGenerator = packGenerator;
   }

   @NotNull
   public final NexoPackServer packServer() {
      NexoPackServer nexoPackServer = this.packServer;
      if (nexoPackServer == null) {
         Intrinsics.throwUninitializedPropertyAccessException("packServer");
         nexoPackServer = null;
      }

      return nexoPackServer;
   }

   public final void packServer(@NotNull NexoPackServer server) {
      NexoPackServer nexoPackServer = this.packServer;
      if (nexoPackServer == null) {
         Intrinsics.throwUninitializedPropertyAccessException("packServer");
         nexoPackServer = null;
      }

      nexoPackServer.stop();
      this.packServer = server;
      nexoPackServer = this.packServer;
      if (nexoPackServer == null) {
         Intrinsics.throwUninitializedPropertyAccessException("packServer");
         nexoPackServer = null;
      }

      nexoPackServer.start();
   }

   @NotNull
   public final MultipackManager multipackManager() {
      return this.multipackManager;
   }

   @NotNull
   public final ClickActionManager clickActionManager() {
      return this.getClickActionManager$com_nexomc_core();
   }

   private static final void onEnable$lambda$0(NexoPlugin nexoPlugin, WrappedTask it) {
      NexoPackServer.Companion.initializeServer();
      NexoItems.loadItems();
      RecipesManager.INSTANCE.load();
      PackGenerator packGenerator2 = nexoPlugin.packGenerator;
      if (packGenerator2 == null) {
         Intrinsics.throwUninitializedPropertyAccessException("packGenerator");
         packGenerator2 = null;
      }

      packGenerator2.generatePack(true);
      NexoTranslator.Companion.registerTranslations();
   }

   @JvmStatic
   @NotNull
   public static final NexoPlugin instance() {
      return Companion.instance();
   }

   @JvmStatic
   @Nullable
   public static final JarFile openJarFile() {
      return Companion.openJarFile();
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\f\u0010\u0006\u001a\u00020\u0005H\u0007b\u0002\b\u0007J\u000e\u0010\b\u001a\u0004\u0018\u00010\tH\u0007b\u0002\b\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.¢\u0006\u0002\n\u0000R\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u001d\u0010\u000e\u001a\u0004\u0018\u00010\u000f8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0012\u0010\u0013\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u0014"},
      d2 = {"Lcom/nexomc/nexo/NexoPlugin$Companion;", "", "<init>", "()V", "nexo", "Lcom/nexomc/nexo/NexoPlugin;", "instance", "Lkotlin/jvm/JvmStatic;", "openJarFile", "Ljava/util/jar/JarFile;", "lFieldName", "", "getLFieldName", "()Ljava/lang/String;", "lField", "Ljava/lang/reflect/Field;", "getLField", "()Ljava/lang/reflect/Field;", "lField$delegate", "Lkotlin/Lazy;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @NotNull
      public final NexoPlugin instance() {
         NexoPlugin nexoPlugin = NexoPlugin.nexo;
         if (nexoPlugin == null) {
            Intrinsics.throwUninitializedPropertyAccessException("nexo");
            nexoPlugin = null;
         }

         return nexoPlugin;
      }

      @JvmStatic
      @Nullable
      public final JarFile openJarFile() {
         Companion companion = this;

         Object obj;
         try {
            Companion companion2 = companion;
            int value = 0;
            JarFile jarFile;
            NexoPlugin nexoPlugin = NexoPlugin.nexo;
            if (nexoPlugin == null) {
               Intrinsics.throwUninitializedPropertyAccessException("nexo");
               nexoPlugin = null;
            }

            jarFile = new JarFile(nexoPlugin.getFile());
            obj = ResultShim.ok(jarFile);
         } catch (Throwable e) {
            obj = ResultShim.ok(ResultKt.createFailure(e));
         }

         return (JarFile)(ResultShim.isFailure(obj) ? null : obj);
      }

   }
}
