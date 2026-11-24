package me.TechsCode.UltraPermissions.base.registry;

import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.storage.Storable;
import com.google.gson.JsonObject;

public class RegistryStorageEntry extends Storable {
   private String key;
   private JsonObject value;
   private RegistryStorable storable;

   public RegistryStorageEntry(String text, JsonObject jsonObject) {
      this.key = text;
      this.value = jsonObject;
   }

   public void setStorable(RegistryStorable registryStorable) {
      this.storable = registryStorable;
   }

   @Override
   public String getKey() {
      return this.key;
   }

   @Override
   public void setKey(String text) {
      this.key = text;
   }

   @Override
   public JsonObject getState() {
      return this.value;
   }

   @Override
   public void setState(JsonObject jsonObject, TechPlugin techPlugin) {
      this.value = jsonObject;
      if (this.storable != null) {
         this.storable.setState(jsonObject);
      }
   }

   @Override
   public void onMount(TechPlugin techPlugin) {
   }

   @Override
   protected void sync() {
      super.sync();
   }
}
