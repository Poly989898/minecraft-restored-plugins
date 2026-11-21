package com.nexomc.nexo.pack.multipack;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.pack.creative.NexoPackReader;
import com.nexomc.nexo.pack.creative.NexoPackWriter;
import com.nexomc.nexo.pack.multipack.gui.MultipackGui;
import com.nexomc.nexo.pack.multipack.gui.MultipackMenuConfig;
import com.nexomc.nexo.pack.server.NexoPackServer;
import com.nexomc.nexo.utils.FileUtilsKt;
import com.nexomc.nexo.utils.NexoYaml;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.Reloadable;
import com.nexomc.nexo.utils.UtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.text.StringsKt;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.BuiltResourcePack;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.metadata.pack.PackFormat;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\b\u0003\u0018\u0000 +2\u00020\u0001:\u0001+B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u001b\u001a\u00020\u001cH\u0016J\u0006\u0010\u001d\u001a\u00020\u001cJ\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020 0\u001fJ\u000e\u0010!\u001a\u00020\u001c2\u0006\u0010\"\u001a\u00020#J\u0014\u0010$\u001a\b\u0012\u0004\u0012\u00020%0\u000f2\u0006\u0010\"\u001a\u00020&J\u001a\u0010'\u001a\b\u0012\u0004\u0012\u00020%0\u000f2\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00060)J\f\u0010*\u001a\b\u0012\u0004\u0012\u00020%0\u000fR\u001f\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005¢\u0006\n\n\u0002\u0010\n\u001a\u0004\b\b\u0010\tR\u001f\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\f0\u0005¢\u0006\n\n\u0002\u0010\n\u001a\u0004\b\r\u0010\tR\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\"\u0010\u0015\u001a\u0004\u0018\u00010\u00142\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0018\u001a\u00020\u00198F¢\u0006\u0006\u001a\u0004\b\u0018\u0010\u001a¨\u0006,"},
   d2 = {"Lcom/nexomc/nexo/pack/multipack/MultipackManager;", "Lcom/nexomc/nexo/utils/Reloadable;", "<init>", "()V", "builtPacks", "", "", "Lteam/unnamed/creative/BuiltResourcePack;", "getBuiltPacks", "()Ljava/util/Map;", "Ljava/util/concurrent/ConcurrentHashMap;", "packArrays", "", "getPackArrays", "templates", "", "Lcom/nexomc/nexo/pack/multipack/PackTemplate;", "getTemplates", "()Ljava/util/List;", "value", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackMenuConfig;", "menuConfig", "getMenuConfig", "()Lcom/nexomc/nexo/pack/multipack/gui/MultipackMenuConfig;", "isEnabled", "", "()Z", "reload", "", "loadMultipackFile", "buildTemplatePacks", "Ljava/util/concurrent/CompletableFuture;", "Ljava/lang/Void;", "openGui", "player", "Lorg/bukkit/entity/Player;", "templatePackInfos", "Lnet/kyori/adventure/resource/ResourcePackInfo;", "Lorg/bukkit/OfflinePlayer;", "templatePackInfosForIds", "enabledIds", "", "requiredTemplatePackInfos", "Companion", "com.nexomc:core"}
)
public final class MultipackManager implements Reloadable {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final ConcurrentHashMap<String, BuiltResourcePack> builtPacks = new ConcurrentHashMap();
   @NotNull
   private final ConcurrentHashMap<String, byte[]> packArrays = new ConcurrentHashMap();
   @NotNull
   private final List<PackTemplate> templates = (List)(new ArrayList());
   @Nullable
   private MultipackMenuConfig menuConfig;

   @NotNull
   public final Map<String, BuiltResourcePack> getBuiltPacks() {
      return (Map)this.builtPacks;
   }

   @NotNull
   public final Map<String, byte[]> getPackArrays() {
      return (Map)this.packArrays;
   }

   @NotNull
   public final List<PackTemplate> getTemplates() {
      return this.templates;
   }

   @Nullable
   public final MultipackMenuConfig getMenuConfig() {
      return this.menuConfig;
   }

   public final boolean isEnabled() {
      return !((Collection)this.templates).isEmpty();
   }

   public void reload() {
      this.loadMultipackFile();
      this.buildTemplatePacks().join();
   }

   public final void loadMultipackFile() {
      this.templates.clear();
      this.menuConfig = null;
      File file = FileUtilsKt.ifNotExists(Companion.getMultipackFile(), (a0_ -> MultipackManager.loadMultipackFile$lambda$0(this, a0_)));
      if (file.exists()) {
         YamlConfiguration multipack;
         Collection collection;
         List list;
         block: {
            multipack = NexoYaml.Companion.loadConfiguration(file);
            collection = (Collection)this.templates;
            ConfigurationSection configurationSection = NexoYamlKt.section((ConfigurationSection)multipack, "templates");
            if (configurationSection != null) {
               Map map = NexoYamlKt.childSections(configurationSection);
               if (map != null) {
                  Collection collection2 = map.values();
                  if (collection2 != null) {
                     Iterable iterable2 = (Iterable)collection2;
                     Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

                     for(Object item : iterable2) {
                        ConfigurationSection p0 = (ConfigurationSection)item;
                        int value = 0;
                        destination.add(new PackTemplate(p0));
                     }

                     list = (List)destination;
                     break block;
                  }
               }
            }

            list = null;
         }

         if (list == null) {
            list = CollectionsKt.emptyList();
         }

         Iterable iterable = (Iterable)list;
         CollectionsKt.addAll(collection, iterable);
         this.menuConfig = MultipackMenuConfig.Companion.fromSection(NexoYamlKt.section((ConfigurationSection)multipack, "menu"));
         if (!((Collection)this.templates).isEmpty()) {
            int value2 = this.templates.size();
            Logs.logInfo("Loaded " + value2 + " multipack template(s): <aqua>" + CollectionsKt.joinToString((Iterable)this.templates, (CharSequence)", ", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", MultipackManager::loadMultipackFile$lambda$2));
         }

      }
   }

   @NotNull
   public final CompletableFuture<Void> buildTemplatePacks() {
      if (!this.isEnabled()) {
         CompletableFuture completableFuture = CompletableFuture.completedFuture((Object)null);
         return completableFuture;
      } else {
         Logs.logInfo("Building TemplatePacks...");
         CompletableFuture completableFuture2 = CompletableFuture.runAsync((() -> MultipackManager.buildTemplatePacks$lambda$0(this)));
         return completableFuture2;
      }
   }

   public final void openGui(@NotNull Player player) {
      MultipackMenuConfig multipackMenuConfig = this.menuConfig;
      if (multipackMenuConfig == null) {
         player.sendRichMessage("<red>Multipack GUI is not configured. Add a <aqua>menu:</aqua> section to <aqua>multipack.yml</aqua>.");
      } else {
         MultipackMenuConfig config = multipackMenuConfig;
         (new MultipackGui(config, player)).open();
      }
   }

   @NotNull
   public final List<ResourcePackInfo> templatePackInfos(@NotNull OfflinePlayer player) {
      if (!this.isEnabled()) {
         return CollectionsKt.emptyList();
      } else {
         Set enabledIds = PlayerPackData.INSTANCE.enabledTemplates(player);
         return this.templatePackInfosForIds(enabledIds);
      }
   }

   @NotNull
   public final List<ResourcePackInfo> templatePackInfosForIds(@NotNull Set<String> enabledIds) {
      if (!this.isEnabled()) {
         return CollectionsKt.emptyList();
      } else {
         NexoPackServer packServer = NexoPlugin.Companion.instance().packServer();
         Iterable iterable = (Iterable)this.templates;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            PackTemplate it = (PackTemplate)element;
            if (enabledIds.contains(it.getId())) {
               destination.add(element);
            }
         }

         iterable = (Iterable)((List)destination);
         destination = (Collection)(new ArrayList());

         for(Object obj : iterable) {
            int value = 0;
            PackTemplate template = (PackTemplate)obj;
            int value2 = 0;
            BuiltResourcePack builtResourcePack = this.builtPacks.get(template.getId());
            ResourcePackInfo resourcePackInfo;
            if (builtResourcePack == null) {
               resourcePackInfo = null;
            } else {
               BuiltResourcePack built = builtResourcePack;
               String str = built.hash();
               String hash = str;
               UUID packUUID = UUID.nameUUIDFromBytes(NexoPackServer.Companion.hashArray(hash));
               str = packServer.packUrl();
               String str2 = packServer.hash();
               String packUrl = StringsKt.replace(str, str2, hash, false);
               resourcePackInfo = ResourcePackInfo.resourcePackInfo(packUUID, URI.create(packUrl), hash);
            }

            if (resourcePackInfo != null) {
               Object obj2 = resourcePackInfo;
               int value3 = 0;
               destination.add(obj2);
            }
         }

         return (List)destination;
      }
   }

   @NotNull
   public final List<ResourcePackInfo> requiredTemplatePackInfos() {
      if (!this.isEnabled()) {
         return CollectionsKt.emptyList();
      } else {
         NexoPackServer packServer = NexoPlugin.Companion.instance().packServer();
         String baseAddress = StringsKt.substringBeforeLast(packServer.packUrl(), "/", packServer.packUrl());
         Iterable iterable = (Iterable)this.templates;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            PackTemplate it = (PackTemplate)element;
            if (it.getRequired()) {
               destination.add(element);
            }
         }

         iterable = (Iterable)((List)destination);
         destination = (Collection)(new ArrayList());

         for(Object obj : iterable) {
            int value = 0;
            PackTemplate template = (PackTemplate)obj;
            int value2 = 0;
            BuiltResourcePack builtResourcePack = this.builtPacks.get(template.getId());
            ResourcePackInfo resourcePackInfo;
            if (builtResourcePack == null) {
               resourcePackInfo = null;
            } else {
               BuiltResourcePack built = builtResourcePack;
               String str = built.hash();
               String hash = str;
               UUID packUUID = UUID.nameUUIDFromBytes(NexoPackServer.Companion.hashArray(hash));
               URI packUrl = URI.create(baseAddress + "/" + hash + ".zip");
               resourcePackInfo = ResourcePackInfo.resourcePackInfo(packUUID, packUrl, hash);
            }

            if (resourcePackInfo != null) {
               Object obj2 = resourcePackInfo;
               int value3 = 0;
               destination.add(obj2);
            }
         }

         return (List)destination;
      }
   }

   private static final Unit loadMultipackFile$lambda$0(MultipackManager multipackManager, File it) {

      Object obj;
      try {
         int value = 0;
         NexoPlugin.Companion.instance().saveResource("multipack.yml", false);
         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      UtilsKt.printOnFailure(obj, false);
      return Unit.INSTANCE;
   }

   private static final CharSequence loadMultipackFile$lambda$2(PackTemplate it) {
      return (CharSequence)it.getId();
   }

   private static final void buildTemplatePacks$lambda$0(MultipackManager multipackManager) {
      multipackManager.builtPacks.clear();
      multipackManager.packArrays.clear();

      for(PackTemplate template : multipackManager.templates) {
         MultipackManager multipackManager2 = multipackManager;

         Object obj;
         try {
            int value = 0;
            if (!template.getEmpty()) {
               File file = template.getPath();
               if (!file.exists()) {
                  Logs.logWarn("Multipack template <aqua>" + template.getId() + "</aqua> path does not exist: " + file.getPath(), false);
               } else {
                  ResourcePack resourcePack = NexoPackReader.INSTANCE.readFile(file, (PackFormat)null);
                  if (resourcePack.packMeta() == null) {
                     PackFormat packFormat2 = PackFormat.format(NMSHandlers.handler().resourcepackFormat());
                     PackFormat packFormat = packFormat2;
                     resourcePack.packMeta(packFormat, (Component)Component.text("Nexo Multipack: " + template.getId()));
                  }

                  BuiltResourcePack built = NexoPackWriter.INSTANCE.build(resourcePack);
                  ((Map)multipackManager2.builtPacks).put(template.getId(), built);
                  ((Map)multipackManager2.packArrays).put(built.hash(), built.data().toByteArray());
               }
            }

            obj = ResultShim.ok(Unit.INSTANCE);
         } catch (Throwable e) {
            obj = ResultShim.ok(ResultKt.createFailure(e));
         }

         Throwable throwable = ResultShim.exceptionOrNull(obj);
         if (throwable != null) {
            Throwable it = throwable;
            int value2 = 0;
            Logs.logError("Failed to build multipack template " + template.getId(), false);
            if (Settings.DEBUG.toBool()) {
               Logs.logError(ExceptionsKt.stackTraceToString(it), false);
            } else {
               String message = it.getMessage();
               if (message == null) {
                  message = "Unknown error";
               }

               Logs.logError(message, false);
            }
         }
      }

   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\b\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\t\u0010\u0007¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/pack/multipack/MultipackManager$Companion;", "", "<init>", "()V", "folder", "Ljava/io/File;", "getFolder", "()Ljava/io/File;", "multipackFile", "getMultipackFile", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final File getFolder() {
         File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
         File file = FilesKt.resolve(dataFolder, "pack/template_packs");
         int value = 0;
         file.mkdirs();
         return file;
      }

      @NotNull
      public final File getMultipackFile() {
         File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
         return FilesKt.resolve(dataFolder, "multipack.yml");
      }
   }
}
