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
      FileConfiguration config = new ConfigFile("upgrade-templates").getConfig();

      for (String key : config.getKeys(false)) {
         this.registerTemplate(new UpgradeTemplate(config.getConfigurationSection(key)));
      }
   }

   public Collection<UpgradeTemplate> getAll() {
      return this.templates.values();
   }

   @Nullable
   public UpgradeTemplate getTemplate(@NotNull String text) {
      return this.templates.get(text);
   }

   public boolean hasTemplate(String text) {
      return this.templates.containsKey(text);
   }

   public void registerTemplate(UpgradeTemplate upgradeTemplate) {
      this.templates.put(upgradeTemplate.getId(), upgradeTemplate);
   }
}
