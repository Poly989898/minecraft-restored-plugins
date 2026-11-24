package me.TechsCode.UltraPermissions.base.registry;

import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.storage.implementations.LocalFile;
import me.TechsCode.UltraPermissions.base.storage.implementations.MySQL;

public class Registry {
   private TechPlugin<?> plugin;
   private RegistryStorage local;
   private RegistryStorage global;

   public Registry(TechPlugin<?> techPlugin) {
      this.plugin = techPlugin;
      this.local = new RegistryStorage(techPlugin, LocalFile.class);
   }

   private void startRemoteRegistry() {
      if (this.global == null) {
         this.global = this.plugin.getMySQLManager() != null && this.plugin.getMySQLManager().isEnabled()
            ? new RegistryStorage(this.plugin, MySQL.class)
            : null;
      }
   }

   public <T extends RegistryStorable> BiRegistry<T> register(Class<? extends T> type) {
      try {
         RegistryStorable instance = (RegistryStorable)type.newInstance();
         return this.register((T)instance);
      } catch (InstantiationException | IllegalAccessException exception) {
         exception.printStackTrace();
         return null;
      }
   }

   public <T extends RegistryStorable> BiRegistry<T> register(T storable) {
      RegistryStorable local = this.register(storable, RegistrationChoice.LOCAL);
      RegistryStorable global = this.register(storable, RegistrationChoice.GLOBAL);
      return new BiRegistry<>((T)local, (T)global);
   }

   public <T extends RegistryStorable> T register(Class<? extends T> type, RegistrationChoice registrationChoice) {
      try {
         RegistryStorable instance = (RegistryStorable)type.newInstance();
         return this.register((T)instance, registrationChoice);
      } catch (InstantiationException | IllegalAccessException exception) {
         exception.printStackTrace();
         return null;
      }
   }

   public <T extends RegistryStorable> T register(T storable, RegistrationChoice registrationChoice) {
      this.startRemoteRegistry();
      Registration registration = null;
      switch (registrationChoice) {
         case LOCAL:
            registration = Registration.LOCAL;
            break;
         case GLOBAL:
            registration = this.global != null ? Registration.GLOBAL : null;
            break;
         case GLOBAL_IF_AVAILABLE:
            registration = this.global != null ? Registration.GLOBAL : Registration.LOCAL;
      }

      if (registration == null) {
         return null;
      }

      RegistryStorage registryStorage = registration == Registration.LOCAL ? this.local : this.global;
      RegistryStorageEntry registryStorageEntry = registryStorage.retrieve(new RegistryStorageEntry(storable.getKey(), storable.getState()));
      storable.setStorageEntry(registryStorageEntry);
      storable.setState(registryStorageEntry.getState());
      return (T)storable;
   }
}
