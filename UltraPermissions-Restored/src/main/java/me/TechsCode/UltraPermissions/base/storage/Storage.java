package me.TechsCode.UltraPermissions.base.storage;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.reflection.ClassInstanceCreator;
import me.TechsCode.UltraPermissions.base.storage.syncing.SyncingAgent;
import org.apache.commons.lang3.function.TriConsumer;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Storage<STORABLE extends Storable> {
   protected TechPlugin<?> plugin;
   private final Class<STORABLE> storable;
   private StorageImplementation implementation;
   private SyncingAgent syncingAgent;
   protected Map<String, STORABLE> instances;
   protected Map<String, STORABLE> instancesView;
   protected List<TriConsumer<@NotNull StorageChangeType, String, @Nullable STORABLE>> observers;

   public Storage(final TechPlugin<?> techPlugin, String text, final Class<STORABLE> type, Class<? extends StorageImplementation> implementationType, boolean flag) {
      this.plugin = techPlugin;
      this.storable = type;

      try {
         this.implementation = (StorageImplementation)implementationType.getConstructor(TechPlugin.class, String.class).newInstance(techPlugin, text);
      } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
         exception.printStackTrace();
         return;
      }

      this.syncingAgent = this.implementation.hasMultiServerSupport() && flag ? techPlugin.getSyncingAgent() : new SyncingAgent.EmptyAgent();
      this.syncingAgent.register(this);
      this.observers = new ArrayList<>();
      this.instances = new HashMap<>();
      this.instancesView = Collections.unmodifiableMap(this.instances);
      this.implementation.read("*", new ReadCallback() {
         @Override
         public void onSuccess(HashMap<String, JsonObject> map) {
            Storage.this.createInstances(map);
         }

         @Override
         public void onFailure(Exception exception) {
            techPlugin.log("Could not retrieve data from Storage '" + type.getName() + "' :");
            techPlugin.log(exception.getMessage());
         }
      });
   }

   protected void instancesChanged(StorageChangeType storageChangeType, String text, @Nullable STORABLE value) {
      for (TriConsumer observer : this.observers) {
         observer.accept(storageChangeType, text, value);
      }
   }

   private void createInstances(Map<String, JsonObject> map) {
      for (Entry entry : map.entrySet()) {
         Storable storable = ClassInstanceCreator.create(this.storable);

         try {
            storable.onMount(this.plugin);
            storable.setKey((String)entry.getKey());
            storable.setState((JsonObject)entry.getValue(), this.plugin);
            storable.setLastSyncedState((JsonObject)entry.getValue());
            storable.setStorage(this);
            this.onMount((STORABLE)storable);
            String key = (String)entry.getKey();
            this.instances.put(key, (STORABLE)storable);
            this.instancesChanged(StorageChangeType.ADDED, key, (STORABLE)storable);
         } catch (Exception exception) {
            this.plugin.log(((JsonObject)entry.getValue()).toString());
            this.plugin.log("Error with Storage " + this.getModelName() + " with key " + (String)entry.getKey() + ":");
            throw exception;
         }
      }
   }

   public void syncFromDatabase(String text) {
      this.plugin
         .getScheduler()
         .runAsync(
            () -> this.implementation
               .read(
                  text,
                  new ReadCallback() {
                     @Override
                     public void onSuccess(HashMap<String, JsonObject> remoteStates) {
                        if (text.equalsIgnoreCase("*")) {
                           Map newEntries = remoteStates.entrySet()
                              .stream()
                              .filter(entry -> !Storage.this.instancesView.containsKey(entry.getKey()))
                              .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
                           Storage.this.createInstances(newEntries);
                           Map<String, JsonObject> existingEntries = remoteStates.entrySet()
                              .stream()
                              .filter(entry -> Storage.this.instancesView.containsKey(entry.getKey()))
                              .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

                           for (Entry<String, JsonObject> entry : existingEntries.entrySet()) {
                              Storable storable = Storage.this.instancesView.get(entry.getKey());
                              if (!storable.getState().toString().equalsIgnoreCase(((JsonObject)entry.getValue()).toString())) {
                                 storable.setState((JsonObject)entry.getValue(), Storage.this.plugin);
                                 storable.setLastSyncedState((JsonObject)entry.getValue());
                              }
                           }

                           Map<String, STORABLE> removedEntries = Storage.this.instancesView
                              .entrySet()
                              .stream()
                              .filter(entry -> !remoteStates.containsKey(entry.getKey()))
                              .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
                           removedEntries.forEach((removedKey, value) -> {
                              Storage.this.instances.remove(removedKey);
                              Storage.this.instancesChanged(StorageChangeType.REMOVED, text, value);
                           });
                        } else if (!remoteStates.isEmpty() && !Storage.this.instancesView.containsKey(text)) {
                           Storage.this.createInstances(remoteStates);
                        } else if (!remoteStates.isEmpty() && Storage.this.instancesView.containsKey(text)) {
                           Storage.this.instancesView.get(text).setState((JsonObject)remoteStates.get(text), Storage.this.plugin);
                           Storage.this.instancesView.get(text).setLastSyncedState((JsonObject)remoteStates.get(text));
                        } else if (remoteStates.isEmpty()) {
                           STORABLE value = Storage.this.instances.remove(text);
                           Storage.this.instancesChanged(StorageChangeType.REMOVED, text, value);
                        }

                        Storage.this.onDataSynchronization();
                     }

                     @Override
                     public void onFailure(Exception exception) {
                        Storage.this.plugin.log("Could not update Storage Instances of Storage '" + Storage.this.storable.getName() + "' :");
                        Storage.this.plugin.log(exception.getMessage());
                     }
                  }
               )
         );
   }

   public Collection<STORABLE> get() {
      return this.instances.values();
   }

   void update(final Storable storable, JsonObject jsonObject) {
      JsonObject lastSyncedState = storable.getLastSyncedState();
      if (lastSyncedState != null) {
         Storable previousStorable = ClassInstanceCreator.create(this.storable);
         previousStorable.setState(lastSyncedState, this.plugin);
         previousStorable.setKey(storable.getKey());
         previousStorable.onMount(this.plugin);
         this.onChange((STORABLE)previousStorable, (STORABLE)storable);
      }

      this.implementation.update(storable.getKey(), jsonObject, new WriteCallback() {
         @Override
         public void onSuccess() {
            Storage.this.syncingAgent.notifyNewDataChanges(Storage.this, storable.getKey());
         }

         @Override
         public void onFailure(Exception exception) {
            Storage.this.plugin.log("Could not sync '" + storable.getKey() + "' of '" + Storage.this.storable.getName() + "' :");
            Storage.this.plugin.log(exception.getMessage() + "");
            Storage.this.plugin.log("Json Data:");
            Storage.this.plugin.log(storable.getState().toString());
            Storage.this.plugin.log("It is strongly advised to restart the Server!");
         }
      });
   }

   void destroy(final Storable storable) {
      this.instances.remove(storable.getKey());
      this.instancesChanged(StorageChangeType.REMOVED, storable.getKey(), null);
      this.onDestroy((STORABLE)storable);
      this.implementation.destroy(storable.getKey(), new WriteCallback() {
         @Override
         public void onSuccess() {
            Storage.this.syncingAgent.notifyNewDataChanges(Storage.this, storable.getKey());
         }

         @Override
         public void onFailure(Exception exception) {
            Storage.this.plugin.log("Could not destroy '" + storable.getKey() + "' of '" + Storage.this.storable.getName() + "' :");
            Storage.this.plugin.log(exception.getMessage() + "");
            Storage.this.plugin.log("Json Data:");
            Storage.this.plugin.log(storable.getState().toString());
            Storage.this.plugin.log("It is strongly advised to restart the Server!");
         }
      });
   }

   public STORABLE create(final STORABLE value) {
      value.onMount(this.plugin);
      value.setStorage(this);
      this.onMount((STORABLE)value);
      final String key = value.getKey();
      this.instances.put(key, (STORABLE)value);
      this.instancesChanged(StorageChangeType.ADDED, key, (STORABLE)value);
      this.onCreation((STORABLE)value);
      this.implementation.create(key, value.getState(), new WriteCallback() {
         @Override
         public void onSuccess() {
            Storage.this.syncingAgent.notifyNewDataChanges(Storage.this, key);
         }

         @Override
         public void onFailure(Exception exception) {
            Storage.this.plugin.log("Could not create '" + key + "' of '" + Storage.this.storable.getName() + "' :");
            Storage.this.plugin.log(exception.getMessage() + "");
            Storage.this.plugin.log("Json Data:");
            Storage.this.plugin.log(value.getState().toString());
            Storage.this.plugin.log("It is strongly advised to restart the Server!");
         }
      });
      return (STORABLE)value;
   }

   public int getNextNumericId() {
      return this.instancesView.keySet().stream().mapToInt(Integer::parseInt).max().orElse(0) + 1;
   }

   public Class<STORABLE> getModel() {
      return this.storable;
   }

   public String getModelName() {
      return this.getModel().getName();
   }

   public abstract void onMount(STORABLE value);

   public abstract void onCreation(STORABLE value);

   public abstract void onChange(STORABLE previous, STORABLE current);

   public abstract void onDestroy(STORABLE value);

   public abstract void onDataSynchronization();
}
