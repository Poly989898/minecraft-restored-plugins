package me.TechsCode.UltraPermissions.transfer;

import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.storage.Storable;
import me.TechsCode.UltraPermissions.base.storage.Storage;
import me.TechsCode.UltraPermissions.base.storage.StorageImplementation;

public class TransferStorage<S extends Storable> extends Storage<S> {
   private long lastWrite = System.currentTimeMillis();

   public TransferStorage(TechPlugin techPlugin, String text, Class<S> type, Class<? extends StorageImplementation> implementationType, boolean flag) {
      super(techPlugin, text, type, implementationType, flag);
   }

   @Override
   public void onMount(S value) {
      this.lastWrite = System.currentTimeMillis();
   }

   @Override
   public void onCreation(S value) {
      this.lastWrite = System.currentTimeMillis();
   }

   @Override
   public void onDestroy(S value) {
      this.lastWrite = System.currentTimeMillis();
   }

   @Override
   public void onChange(S value, S newValue) {
      this.lastWrite = System.currentTimeMillis();
   }

   public long getLastWrite() {
      return this.lastWrite;
   }

   @Override
   public void onDataSynchronization() {
   }
}
