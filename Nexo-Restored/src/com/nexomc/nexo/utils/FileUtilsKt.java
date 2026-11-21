package com.nexomc.nexo.utils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000L\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a#\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\u0012\u0010\u0002\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u0003\"\u00020\u0004¢\u0006\u0002\u0010\u0005\u001a#\u0010\u0000\u001a\u00020\u0006*\u00020\u00062\u0012\u0010\u0002\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u0003\"\u00020\u0004¢\u0006\u0002\u0010\u0007\u001a\u001a\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\t*\u00020\u00012\b\b\u0002\u0010\n\u001a\u00020\u000b\u001a\u001a\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\t*\u00020\u00012\b\b\u0002\u0010\n\u001a\u00020\u000b\u001a&\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\t*\u00020\u00012\u0014\b\u0002\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u000b0\u000f\u001a\u001e\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00010\t*\b\u0012\u0004\u0012\u00020\u00010\t2\u0006\u0010\u0010\u001a\u00020\u0004\u001a\u001a\u0010\u0011\u001a\u00020\u0012*\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u0004\u001a\u001e\u0010\u0011\u001a\u00020\u0012*\u00020\u00012\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0016\u001a;\u0010\u0011\u001a\u00020\u0012*\u00020\u00012*\u0010\u0015\u001a\u0016\u0012\u0012\b\u0001\u0012\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u00170\u0003\"\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0017¢\u0006\u0002\u0010\u0018\u001a.\u0010\u0019\u001a\u00020\u0001*\u00020\u00012\u0012\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00120\u000fH\u0007b\f\b\u001b\u0012\b\b\u001c\u0012\u0004\b\b(\u001d¨\u0006\u001e"},
   d2 = {"resolve", "Ljava/io/File;", "path", "", "", "(Ljava/io/File;[Ljava/lang/String;)Ljava/io/File;", "Ljava/nio/file/Path;", "(Ljava/nio/file/Path;[Ljava/lang/String;)Ljava/nio/file/Path;", "listYamlFiles", "", "deep", "", "listJsonFiles", "listDirectories", "predicate", "Lkotlin/Function1;", "string", "replaceText", "", "target", "replacement", "replacements", "", "Lkotlin/Pair;", "(Ljava/io/File;[Lkotlin/Pair;)V", "ifNotExists", "action", "Lorg/jetbrains/annotations/Contract;", "value", "_ -> this", "com.nexomc:core"}
)
public final class FileUtilsKt {
   @NotNull
   public static final File resolve(@NotNull File file, @NotNull String... path) {
      String[] strs = path;
      Object accumulator = file;
      int value = 0;

      for(int i = path.length; value < i; ++value) {
         Object element = strs[value];
         int value2 = 0;
         accumulator = new File((File)accumulator, (String)element);
      }

      return (File)accumulator;
   }

   @NotNull
   public static final Path resolve(@NotNull Path path3, @NotNull String... path) {
      String[] strs = path;
      Object accumulator = path3;
      int value = 0;

      for(int i = path.length; value < i; ++value) {
         Object element = strs[value];
         int value2 = 0;
         Path path2 = ((Path)accumulator).resolve((String)element);
         accumulator = path2;
      }

      return (Path)accumulator;
   }

   @NotNull
   public static final List<File> listYamlFiles(@NotNull File file, boolean deep) {
      List list;
      if (deep) {
         list = (List)CollectionUtilsKt.toFastList(SequencesKt.filter((Sequence)FilesKt.walkBottomUp(file), FileUtilsKt::listYamlFiles$lambda$0));
      } else {
         File[] files = file.listFiles(FileUtilsKt::listYamlFiles$lambda$1);
         if (files != null) {
            list = ArraysKt.toList(files);
            if (list != null) {
               return list;
            }
         }

         list = CollectionsKt.emptyList();
      }

      return list;
   }

   @NotNull
   public static final List<File> listJsonFiles(@NotNull File file, boolean deep) {
      List list;
      if (deep) {
         list = (List)CollectionUtilsKt.toFastList(SequencesKt.filter((Sequence)FilesKt.walkBottomUp(file), FileUtilsKt::listJsonFiles$lambda$0));
      } else {
         File[] files = file.listFiles(FileUtilsKt::listJsonFiles$lambda$1);
         if (files != null) {
            list = ArraysKt.toList(files);
            if (list != null) {
               return list;
            }
         }

         list = CollectionsKt.emptyList();
      }

      return list;
   }

   @NotNull
   public static final List<File> listDirectories(@NotNull File file, @NotNull Function1<? super File, Boolean> predicate) {
      return (List)CollectionUtilsKt.<File>toFastList(SequencesKt.filter((Sequence)FilesKt.walkBottomUp(file), (a0_ -> FileUtilsKt.listDirectories$lambda$1(predicate, (File)a0_))));
   }

   @NotNull
   public static final List<File> resolve(@NotNull List<? extends File> list, @NotNull String string) {
      Iterable iterable = (Iterable)list;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         int value = 0;
         File it = (File)element;
         int value2 = 0;
         String[] it_2 = new String[]{string};
         File it_1 = resolve(it, it_2);
         int value3 = 0;
         File file = it_1.exists() ? it_1 : null;
         if (file != null) {
            Object obj = file;
            int value4 = 0;
            destination.add(obj);
         }
      }

      return (List)destination;
   }

   public static final void replaceText(@NotNull File file, @NotNull String target, @NotNull String replacement) {
      FilesKt.writeText(file, StringsKt.replace(FilesKt.readText(file, kotlin.text.Charsets.UTF_8), target, replacement, false), kotlin.text.Charsets.UTF_8);
   }

   public static final void replaceText(@NotNull File file, @NotNull Map<String, String> replacements) {
      Iterable entrySet = (Iterable)replacements.entrySet();
      String initial = FilesKt.readText(file, kotlin.text.Charsets.UTF_8);
      Object accumulator = initial;

      for(Object element : entrySet) {
         Map.Entry entry = (Map.Entry)element;
         int value = 0;
         String b = (String)entry.getKey();
         String c = (String)entry.getValue();
         accumulator = StringsKt.replace((String)accumulator, b, c, false);
      }

      FilesKt.writeText(file, (String)accumulator, kotlin.text.Charsets.UTF_8);
   }

   public static final void replaceText(@NotNull File file, @NotNull Pair<String, String>... replacements) {
      Pair[] pairs = replacements;
      String initial = FilesKt.readText(file, kotlin.text.Charsets.UTF_8);
      Object accumulator = initial;
      int value = 0;

      for(int i = replacements.length; value < i; ++value) {
         Object element = pairs[value];
         int value2 = 0;
         String b = (String)((Pair)element).component1();
         String c = (String)((Pair)element).component2();
         accumulator = StringsKt.replace((String)accumulator, b, c, false);
      }

      FilesKt.writeText(file, (String)accumulator, kotlin.text.Charsets.UTF_8);
   }

   @Contract("_ -> this")
   @NotNull
   public static final File ifNotExists(@NotNull File file, @NotNull Function1<? super File, Unit> action) {
      if (!file.exists()) {
         action.invoke(file);
      }

      return file;
   }

   private static final boolean listYamlFiles$lambda$0(File file) {
      return file.isFile() && Intrinsics.areEqual(FilesKt.getExtension(file), "yml") && file.length() != 0L;
   }

   private static final boolean listYamlFiles$lambda$1(File file) {
      boolean flag;
      if (file.isFile()) {
         if (Intrinsics.areEqual(FilesKt.getExtension(file), "yml") && file.length() != 0L) {
            flag = true;
            return flag;
         }
      }

      flag = false;
      return flag;
   }

   private static final boolean listJsonFiles$lambda$0(File file) {
      return file.isFile() && Intrinsics.areEqual(FilesKt.getExtension(file), "yml") && file.length() != 0L;
   }

   private static final boolean listJsonFiles$lambda$1(File file) {
      boolean flag;
      if (file.isFile()) {
         if (Intrinsics.areEqual(FilesKt.getExtension(file), "json") && file.length() != 0L) {
            flag = true;
            return flag;
         }
      }

      flag = false;
      return flag;
   }

   private static final boolean listDirectories$lambda$1(Function1 predicate, File it) {
      return it.isDirectory() && (Boolean)predicate.invoke(it);
   }

   private static final boolean listDirectories$lambda$0(File it) {
      return true;
   }
}
