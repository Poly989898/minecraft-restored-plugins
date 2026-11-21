package litebans;

import java.io.InputStream;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class ArchiveHelper5 implements ArchiveAInterface {
   private static final int VALUE_512 = 512;
   private static final int VALUE_32 = 32;
   private static final int VALUE_12 = 12;
   private static final ArchiveHelper5 defaultInstance = new ArchiveHelper5();
   public static final String TEXT = "ar";
   public static final String TEXT_2 = "arj";
   public static final String TEXT_3 = "cpio";
   public static final String TEXT_4 = "dump";
   public static final String TEXT_5 = "jar";
   public static final String TEXT_6 = "tar";
   public static final String TEXT_7 = "zip";
   public static final String TEXT_8 = "7z";
   private final String text3;
   private volatile String text4;
   private SortedMap sortedMap;

   private static final ArrayList obtainArrayList() {
      return ArchiveUtil5.computeArrayList(createProviderIterator());
   }

   static final void handleSet(Set set, ArchiveAInterface archiveAInterface, TreeMap treeMap) {
      for (String text : (Iterable<String>) set) {
         treeMap.put(access$toUpperCase(text), archiveAInterface);
      }
   }

   private static final Iterator createProviderIterator() {
      return new ServiceLoaderIterator(ArchiveAInterface.class);
   }

   private static final String access$toUpperCase(String text) {
      return text.toUpperCase(Locale.ROOT);
   }

   public static final SortedMap obtainSortedMap() {
      return (java.util.SortedMap) AccessController.doPrivileged(new ArchivePrivilegedActionImpl());
   }

   public ArchiveHelper5() {
      this(null);
   }

   public ArchiveHelper5(String text) {
      this.text3 = text;
      this.text4 = text;
   }

   public ArchiveInputStream createArchiveInputStream(String text, InputStream inputStream) {
      return this.createArchiveInputStream(text, inputStream, this.text4);
   }

   @Override
   public ArchiveInputStream createArchiveInputStream(String text, InputStream inputStream, String text2) {
      try {
         if (text == null) {
            throw new IllegalArgumentException("Archivername must not be null.");
         } else if (inputStream == null) {
            throw new IllegalArgumentException("InputStream must not be null.");
         } else if ("tar".equalsIgnoreCase(text)) {
            return text2 != null ? new TarArchiveInputStream(inputStream, text2) : new TarArchiveInputStream(inputStream);
         } else if ("ar".equalsIgnoreCase(text) || "arj".equalsIgnoreCase(text) || "zip".equalsIgnoreCase(text)
               || "jar".equalsIgnoreCase(text) || "cpio".equalsIgnoreCase(text) || "dump".equalsIgnoreCase(text)) {
            // Only tar is needed (GeoIP download). The other readers had no getNextEntry() in the original jar.
            throw new ArchiveExceptionSub("Archiver: " + text + " is not supported.");
         } else if ("7z".equalsIgnoreCase(text)) {
            throw new StreamingNotSupportedException("7z");
         } else {
            ArchiveAInterface archiveAInterface = (ArchiveAInterface)this.obtainSortedMap2().get(access$toUpperCase(text));
            if (archiveAInterface != null) {
               return archiveAInterface.createArchiveInputStream(text, inputStream, text2);
            } else {
               throw new ArchiveExceptionSub("Archiver: " + text + " not found.");
            }
         }
      } catch (ArchiveExceptionSub e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public SortedMap obtainSortedMap2() {
      if (this.sortedMap == null) {
         this.sortedMap = Collections.unmodifiableSortedMap(obtainSortedMap());
      }

      return this.sortedMap;
   }

   @Override
   public Set getInputStreamArchiveNames() {
      return Sets.computeHashSet(new String[]{"ar", "arj", "zip", "tar", "jar", "cpio", "dump", "7z"});
   }

   static final ArchiveHelper5 getDefault() {
      return defaultInstance;
   }

   static final ArrayList obtainArrayList2() {
      return obtainArrayList();
   }
}
