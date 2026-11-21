package team.unnamed.creative.serialize.minecraft.fs;

import java.util.zip.ZipEntry;

public interface ZipEntryLifecycleHandler {
   ZipEntryLifecycleHandler DEFAULT = new ZipEntryLifecycleHandler() {
      public ZipEntry create(String path) {
         ZipEntry entry = ZipEntryLifecycleHandler.super.create(path);
         entry.setTime(0L);
         return entry;
      }
   };

   default ZipEntry create(String path) {
      return new ZipEntry(path);
   }

   default void onClose(ZipEntry entry) {
   }
}
