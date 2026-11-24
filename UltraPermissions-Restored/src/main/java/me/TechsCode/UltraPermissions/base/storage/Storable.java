package me.TechsCode.UltraPermissions.base.storage;

import me.TechsCode.UltraPermissions.base.TechPlugin;
import com.google.gson.JsonObject;

public abstract class Storable {
   protected Storage<?> storage;
   private JsonObject lastSyncedState;

   public Storable() {
   }

   public void setStorage(Storage<?> storage) {
      this.storage = storage;
   }

   protected void sync() {
      if (this.storage != null) {
         JsonObject state = this.getState();
         if (this.lastSyncedState == null || !this.lastSyncedState.toString().equalsIgnoreCase(state.toString())) {
            this.storage.update(this, state);
            this.lastSyncedState = state;
         }
      }
   }

   public void setLastSyncedState(JsonObject jsonObject) {
      this.lastSyncedState = jsonObject;
   }

   public JsonObject getLastSyncedState() {
      return this.lastSyncedState;
   }

   protected void destroy() {
      if (this.storage != null) {
         this.storage.destroy(this);
         this.storage = null;
      }
   }

   public abstract String getKey();

   public abstract void setKey(String text);

   public abstract JsonObject getState();

   public abstract void setState(JsonObject jsonObject, TechPlugin<?> techPlugin);

   public boolean isLinkedToStorage() {
      return this.storage != null;
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof Storable storable) ? false : storable.getKey().equals(this.getKey());
   }

   @Override
   public int hashCode() {
      return this.getKey().hashCode();
   }

   public abstract void onMount(TechPlugin<?> techPlugin);
}
