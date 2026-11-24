package me.TechsCode.UltraPermissions.base.registry;

import com.google.gson.JsonObject;

public abstract class RegistryStorable {
   private final String key;
   private RegistryStorageEntry storageEntry;

   public RegistryStorable(String text) {
      this.key = text;
   }

   public void setStorageEntry(RegistryStorageEntry registryStorageEntry) {
      this.storageEntry = registryStorageEntry;
      this.storageEntry.setStorable(this);
   }

   public String getKey() {
      return this.key;
   }

   public abstract void setState(JsonObject jsonObject);

   public abstract JsonObject getState();

   public void sync() {
      this.storageEntry.setState(this.getState(), null);
      this.storageEntry.sync();
   }
}
