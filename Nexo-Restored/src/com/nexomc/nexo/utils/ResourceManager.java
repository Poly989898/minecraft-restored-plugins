package com.nexomc.nexo.utils;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import java.io.Closeable;
import java.io.File;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u0016\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\u0007¨\u0006\f"},
   d2 = {"Lcom/nexomc/nexo/utils/ResourceManager;", "", "<init>", "()V", "extractConfiguration", "Ljava/io/File;", "fileName", "", "extractConfigsInFolder", "", "folder", "fileExtension", "com.nexomc:core"}
)
public final class ResourceManager {
   @NotNull
   public static final ResourceManager INSTANCE = new ResourceManager();

   private ResourceManager() {
   }

   @NotNull
   public final File extractConfiguration(@NotNull String fileName) {
      File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
      File it = FilesKt.resolve(dataFolder, fileName);
      int value = 0;
      if (!it.exists()) {
         ResourceManager resourceManager = INSTANCE;

         Object obj;
         try {
            int value2 = 0;
            NexoPlugin.Companion.instance().saveResource(fileName, false);
            obj = ResultShim.ok(Unit.INSTANCE);
         } catch (Throwable e) {
            obj = ResultShim.ok(ResultKt.createFailure(e));
         }

         UtilsKt.printOnFailure(obj, false);
      }

      return it;
   }

   public final void extractConfigsInFolder(@NotNull String folder, @NotNull String fileExtension) {
      JarFile openJarFile = NexoPlugin.Companion.openJarFile();
      if (openJarFile != null) {
         Closeable closeable = (Closeable)openJarFile;
         Throwable throwable = null;

         try {
            JarFile jarFile = (JarFile)closeable;
            int value = 0;
            Enumeration entries = jarFile.entries();
            Sequence sequence = SequencesKt.asSequence(CollectionsKt.iterator(entries));

            for(Object element : kotlin.sequences.SequencesKt.asIterable(sequence)) {
               JarEntry entry = (JarEntry)element;
               int value2 = 0;
               if (!entry.isDirectory()) {
                  String name = entry.getName();
                  if (StringsKt.startsWith(name, folder + "/", false)) {
                     name = entry.getName();
                     if (StringsKt.endsWith(name, "." + fileExtension, false)) {
                        NexoPlugin.Companion.instance().saveResource(entry.getName(), true);
                     }
                  }
               }
            }

            Unit unit = Unit.INSTANCE;
         } catch (Throwable e) {
            throwable = e;
            throw e;
         } finally {
            CloseableKt.closeFinally(closeable, throwable);
         }
      }

   }
}
