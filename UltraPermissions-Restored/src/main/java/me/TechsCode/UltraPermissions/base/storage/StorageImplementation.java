package me.TechsCode.UltraPermissions.base.storage;

import me.TechsCode.UltraPermissions.base.TechPlugin;
import com.google.gson.JsonObject;

public abstract class StorageImplementation {
   protected TechPlugin plugin;
   protected String name;
   private boolean multiServerSupport;

   public StorageImplementation(TechPlugin techPlugin, String text, boolean flag) {
      this.plugin = techPlugin;
      this.name = text;
      this.multiServerSupport = flag;
   }

   public abstract void destroy(String text, WriteCallback writeCallback);

   public abstract void create(String text, JsonObject jsonObject, WriteCallback writeCallback);

   public abstract void update(String text, JsonObject jsonObject, WriteCallback writeCallback);

   public abstract void read(String text, ReadCallback readCallback);

   public boolean hasMultiServerSupport() {
      return this.multiServerSupport;
   }
}
