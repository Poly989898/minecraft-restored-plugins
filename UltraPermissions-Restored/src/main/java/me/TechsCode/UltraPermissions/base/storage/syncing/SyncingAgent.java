package me.TechsCode.UltraPermissions.base.storage.syncing;

import java.util.ArrayList;
import java.util.List;
import me.TechsCode.UltraPermissions.base.storage.Storage;

public abstract class SyncingAgent {
   private List<Storage> storages = new ArrayList<>();

   public SyncingAgent() {
   }

   public void executeLocalSynchronization(String text, String syncData) {
      this.storages.forEach(storage -> {
         if (storage.getModelName().equals(text)) {
            storage.syncFromDatabase(syncData);
         }
      });
   }

   public void register(Storage storage) {
      this.storages.add(storage);
   }

   public abstract void notifyNewDataChanges(Storage storage, String text);

   public static class EmptyAgent extends SyncingAgent {
      public EmptyAgent() {
      }

      @Override
      public void notifyNewDataChanges(Storage storage, String text) {
      }
   }
}
