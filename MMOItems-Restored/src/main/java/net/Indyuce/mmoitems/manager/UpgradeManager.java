package net.Indyuce.mmoitems.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.Indyuce.mmoitems.api.ConfigFile;
import net.Indyuce.mmoitems.api.UpgradeTemplate;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpgradeManager implements Reloadable {
   private final Map<String, UpgradeTemplate> templates = new HashMap<>();

   public UpgradeManager() {
      this.reload();
   }

   @Override
   public void reload() {
      this.templates.clear();
      FileConfiguration var1 = new ConfigFile("upgrade-templates").getConfig();

      for (String var3 : var1.getKeys(false)) {
         this.registerTemplate(new UpgradeTemplate(var1.getConfigurationSection(var3)));
      }
   }

   public Collection<UpgradeTemplate> getAll() {
      return this.templates.values();
   }

   @Nullable
   public UpgradeTemplate getTemplate(@NotNull String var1) {
      return this.templates.get(var1);
   }

   public boolean hasTemplate(String var1) {
      return this.templates.containsKey(var1);
   }

   public void registerTemplate(UpgradeTemplate var1) {
      this.templates.put(var1.getId(), var1);
   }
}
