package io.lumine.mythic.bukkit.utils.serialization;

import java.io.File;

public class WrappedJsonFile<T> {
   private final SerializingModule parent;
   private final File file;
   private T value;

   public WrappedJsonFile(SerializingModule parent, File file, T value) {
      this.parent = parent;
      this.file = file;
      this.value = value;
   }

   public void set(T value) {
      this.value = value;
   }

   public T get() {
      return this.value;
   }

   public void save() {
      if (this.value != null) {
         this.parent.saveJson(this.file, this.value);
      }
   }
}
