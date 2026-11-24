package me.TechsCode.UltraPermissions.base.storage;

import me.TechsCode.UltraPermissions.base.TechPlugin;

public class SimpleStorage<S extends Storable> extends Storage<S> {
   public SimpleStorage(TechPlugin<?> techPlugin, String text, Class<S> type, Class<? extends StorageImplementation> implementationType, boolean flag) {
      super(techPlugin, text, type, implementationType, flag);
   }

   @Override
   public void onMount(S value) {
   }

   @Override
   public void onCreation(S value) {
   }

   @Override
   public void onChange(S value, S newValue) {
   }

   @Override
   public void onDestroy(S value) {
   }

   @Override
   public void onDataSynchronization() {
   }
}
