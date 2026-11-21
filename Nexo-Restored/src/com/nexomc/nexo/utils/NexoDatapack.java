package com.nexomc.nexo.utils;
import com.nexomc.nexo.utils.FileUtilsKt;
import com.nexomc.nexo.utils.JsonBuilder;
import com.nexomc.nexo.utils.SchedulerUtils;
import kotlin.jvm.functions.Function0;

import com.google.gson.JsonObject;
import com.nexomc.nexo.NexoBootstrap;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.recipes.RecipesManager;
import io.papermc.paper.datapack.Datapack;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\b\u0016\u0018\u0000  2\u00020\u0001:\u0001 B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u0006\u0010\u001a\u001a\u00020\u001bJ\u001f\u0010\u001c\u001a\u00020\u001b2\u0006\u0010\u001d\u001a\u00020\u00132\b\b\u0002\u0010\u001e\u001a\u00020\u0013H\u0000¢\u0006\u0002\b\u001fR\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u000b\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0012\u001a\u00020\u00138F¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0014R\u0011\u0010\u0015\u001a\u00020\u00138F¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u0014R\u0012\u0010\u0017\u001a\u00060\u0018j\u0002`\u0019X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006!"},
   d2 = {"Lcom/nexomc/nexo/utils/NexoDatapack;", "", "key", "", "description", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", "defaultWorld", "Lorg/bukkit/World;", "getDefaultWorld", "()Lorg/bukkit/World;", "datapackName", "getDatapackName", "()Ljava/lang/String;", "datapackFile", "Ljava/io/File;", "getDatapackFile", "()Ljava/io/File;", "isFirstInstall", "", "()Z", "datapackEnabled", "getDatapackEnabled", "datapackMeta", "Lcom/google/gson/JsonObject;", "Lcom/nexomc/nexo/utils/JsonObject;", "writeMCMeta", "", "enableDatapack", "enabled", "reload", "enableDatapack$com_nexomc_core", "Companion", "com.nexomc:core"}
)
public class NexoDatapack {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final World defaultWorld;
   @NotNull
   private final String datapackName;
   @NotNull
   private final File datapackFile;
   @NotNull
   private final JsonObject datapackMeta;
   @NotNull
   private static final List<String> removedDatapacks;

   public NexoDatapack(@NotNull String key, @NotNull String description) {
      super();
      List worlds = Bukkit.getWorlds();
      Object obj = CollectionsKt.first(worlds);
      this.defaultWorld = (World)obj;
      this.datapackName = "file/" + key;
      File worldFolder = this.defaultWorld.getWorldFolder();
      String[] strs = new String[]{"datapacks/" + key};
      this.datapackFile = FileUtilsKt.resolve(worldFolder, strs);
      this.datapackMeta = JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonObject(), "pack", JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonObject(), "description", description), "pack_format", NMSHandlers.handler().datapackFormat()));
      File file = this.datapackFile;
      strs = new String[]{"data"};
      FilesKt.deleteRecursively(FileUtilsKt.resolve(file, strs));
   }

   @NotNull
   public final World getDefaultWorld() {
      return this.defaultWorld;
   }

   @NotNull
   public final String getDatapackName() {
      return this.datapackName;
   }

   @NotNull
   public final File getDatapackFile() {
      return this.datapackFile;
   }

   public final boolean isFirstInstall() {
      Iterable packs = (Iterable)Bukkit.getDatapackManager().getPacks();
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(packs, 10)));

      for(Object item : packs) {
         Datapack it = (Datapack)item;
         int value = 0;
         destination.add(it.getName());
      }

      packs = (Iterable)((List)destination);
      String str = this.datapackName;
      boolean flag;
      if (packs instanceof Collection && ((Collection)packs).isEmpty()) {
         flag = true;
      } else {
         Iterator iterator = packs.iterator();

         while(true) {
            if (!iterator.hasNext()) {
               flag = true;
               break;
            }

            Object element = iterator.next();
            int value2 = 0;
            if (str.equals(element)) {
               flag = false;
               break;
            }
         }
      }

      return flag;
   }

   public final boolean getDatapackEnabled() {
      Iterable packs = (Iterable)Bukkit.getDatapackManager().getPacks();
      Iterator iterator = packs.iterator();

      Object obj;
      while(true) {
         if (iterator.hasNext()) {
            Object obj2 = iterator.next();
            Datapack it = (Datapack)obj2;
            int value = 0;
            if (!Intrinsics.areEqual(it.getName(), this.datapackName)) {
               continue;
            }

            obj = obj2;
            break;
         }

         obj = null;
         break;
      }

      return (Datapack)obj != null ? ((Datapack)obj).isEnabled() : false;
   }

   public final void writeMCMeta() {
        Object object = new String[]{"pack.mcmeta"};
        Object obj = object = FileUtilsKt.resolve(this.datapackFile, (String[])object);
        boolean bl = false;
        ((File)obj).getParentFile().mkdirs();
        String string = this.datapackMeta.toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, "toString(...)");
        FilesKt.writeText((File)object, string, kotlin.text.Charsets.UTF_8);
    }

   public final void enableDatapack$com_nexomc_core(boolean enabled, boolean reload) {
      SchedulerUtils.INSTANCE.callSyncMethod((() -> NexoDatapack.enableDatapack$lambda$0(this, enabled, reload)));
   }

   private static final Unit enableDatapack$lambda$0(NexoDatapack nexoDatapack, boolean enabled, boolean reload) {
      Datapack pack = Bukkit.getDatapackManager().getPack(nexoDatapack.datapackName);
      if (pack != null) {
         Datapack it = pack;
         int value = 0;
         pack = it.isEnabled() != enabled ? it : null;
         if (pack != null) {
            it = pack;
            value = 0;
            it.setEnabled(enabled);
            if (reload) {
               RecipesManager.reload();
            }
         }
      }

      return Unit.INSTANCE;
   }

   static {
      String[] strs = new String[]{NexoBootstrap.Companion.getBootsStrung() ? "nexo_custom_blocks" : null, NexoBootstrap.Companion.getBootsStrung() ? "nexo_music_discs" : null};
      removedDatapacks = CollectionsKt.listOfNotNull(strs);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0007\u001a\u00020\bR\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\t"},
      d2 = {"Lcom/nexomc/nexo/utils/NexoDatapack$Companion;", "", "<init>", "()V", "removedDatapacks", "", "", "clearOldDatapacks", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      public final void clearOldDatapacks() {
         File[] files = Bukkit.getServer().getWorldContainer().listFiles((java.io.FileFilter)(f -> clearOldDatapacks$lambda$0(f)));
         Iterable iterable = (Iterable)FileUtilsKt.resolve(ArraysKt.toList(files), "datapacks");
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            File it = (File)element;
            int value = 0;
            files = it.listFiles((java.io.FileFilter)(f -> clearOldDatapacks$lambda$1$0(f)));
            Iterable list = (Iterable)ArraysKt.toList(files);
            CollectionsKt.addAll(destination, list);
         }

         iterable = (Iterable)((List)destination);

         for(Object obj : iterable) {
            File it = (File)obj;
            int value2 = 0;
            FilesKt.deleteRecursively(it);
         }

      }

      private static final boolean clearOldDatapacks$lambda$0(File it) {
         return it.isDirectory();
      }

      private static final boolean clearOldDatapacks$lambda$1$0(File it) {
         return it.isDirectory() && NexoDatapack.removedDatapacks.contains(it.getName());
      }
   }
}
