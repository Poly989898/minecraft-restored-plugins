package com.nexomc.nexo.configs;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.UtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import java.io.File;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0004\u001a\u00020\u0005J$\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000b0\nH\u0002J\u001e\u0010\f\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u0002J*\u0010\u0010\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\u0018\u0010\u0011\u001a\u0014\u0012\u0004\u0012\u00020\u000b\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\u00120\nH\u0002¨\u0006\u0013"},
   d2 = {"Lcom/nexomc/nexo/configs/SettingsUpdater;", "", "<init>", "()V", "handleSettingsUpdate", "", "updateKeys", "Lorg/bukkit/configuration/file/YamlConfiguration;", "settings", "newKeyPaths", "", "", "updateValues", "updates", "", "Lcom/nexomc/nexo/configs/UpdatedSettingValues;", "removeKeys", "keys", "", "com.nexomc:core"}
)
public final class SettingsUpdater {
   public final void handleSettingsUpdate() {
      YamlConfiguration settings = Settings.Companion.settings();
      String saveToString = settings.saveToString();
      String oldSettings = saveToString;
      settings = this.updateKeys(settings, UpdatedSettings.Companion.toStringMap());
      settings = this.updateValues(settings, (List)kotlin.enums.EnumEntriesKt.enumEntries(UpdatedSettingValues.values()));
      settings = this.removeKeys(settings, RemovedSettings.Companion.toMap());
      if (!Intrinsics.areEqual(settings.saveToString(), oldSettings)) {
         SettingsUpdater settingsUpdater = this;

         Object obj;
         try {
            SettingsUpdater settingsUpdater2 = settingsUpdater;
            int value = 0;
            File absoluteFile = NexoPlugin.Companion.instance().getDataFolder().getAbsoluteFile();
            settings.save(FilesKt.resolve(absoluteFile, "settings.yml"));
            Logs.logSuccess("Successfully updated settings.yml", true);
            obj = ResultShim.ok(Unit.INSTANCE);
         } catch (Throwable e) {
            obj = ResultShim.ok(ResultKt.createFailure(e));
         }

         UtilsKt.printOnFailure(obj, false);
      }
   }

   private final YamlConfiguration updateKeys(YamlConfiguration settings, Map<String, String> newKeyPaths) {
      Iterable entrySet = (Iterable)newKeyPaths.entrySet();

      for(Object element : entrySet) {
         Map.Entry entry = (Map.Entry)element;
         int value2 = 0;
         String key = (String)entry.getKey();
         String value = (String)entry.getValue();
         if (settings.contains(key)) {
            Logs.logWarn("Found outdated setting-path " + key + ". This will be updated.", false);
            settings.set(value, settings.get(key));
            settings.set(key, (Object)null);
         }
      }

      return settings;
   }

   private final YamlConfiguration updateValues(YamlConfiguration settings, List<? extends UpdatedSettingValues> updates) {
      Iterable iterable = (Iterable)updates;

      for(Object element : iterable) {
         UpdatedSettingValues update = (UpdatedSettingValues)element;
         int value = 0;
         if (StringsKt.equals(settings.getString(update.getPath()), update.getOldValue(), true) && update.getCondition().invoke(settings)) {
            String oldValue = update.getOldValue();
            Logs.logWarn("Found outdated setting-value " + oldValue + " at " + update.getPath() + ". This will be updated to " + update.getNewValue() + ".", false);
            settings.set(update.getPath(), update.getNewValue());
         }
      }

      return settings;
   }

   private final YamlConfiguration removeKeys(YamlConfiguration settings, Map<String, String[]> keys) {

      for(Map.Entry element : keys.entrySet()) {
         int value = 0;
         String key = (String)element.getKey();
         String[] subKeys = (String[])element.getValue();
         if (settings.contains(key)) {
            Logs.logWarn("Found outdated setting " + key + ". This will be removed.", false);
            Object[] objs = subKeys;
            int value2 = 0;

            for(int i = subKeys.length; value2 < i; ++value2) {
               Object obj = objs[value2];
               int value3 = 0;
               NexoYamlKt.remove((ConfigurationSection)settings, (String)obj);
            }
         }

         NexoYamlKt.remove((ConfigurationSection)settings, key);
         ConfigurationSection parent = NexoYamlKt.section((ConfigurationSection)settings, StringsKt.substringBeforeLast(key, ".", key));
         if (parent != null && parent.getKeys(false).isEmpty()) {
            String currentPath = parent.getCurrentPath();
            settings.set(currentPath, (Object)null);
         }
      }

      return settings;
   }
}
