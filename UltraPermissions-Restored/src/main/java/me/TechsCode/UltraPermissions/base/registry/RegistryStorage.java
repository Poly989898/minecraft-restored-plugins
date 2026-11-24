package me.TechsCode.UltraPermissions.base.registry;

import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.storage.SimpleStorage;
import me.TechsCode.UltraPermissions.base.storage.StorageImplementation;

public class RegistryStorage extends SimpleStorage<RegistryStorageEntry> {
   public RegistryStorage(TechPlugin<?> plugin, Class<? extends StorageImplementation> clazz) {
      super(plugin, "Registry", RegistryStorageEntry.class, clazz, true);
   }

   public RegistryStorageEntry retrieve(RegistryStorageEntry registryStorageEntry) {
      for (RegistryStorageEntry existing : this.get()) {
         if (existing.getKey().equalsIgnoreCase(registryStorageEntry.getKey())) {
            return existing;
         }
      }

      return this.create(registryStorageEntry);
   }
}
