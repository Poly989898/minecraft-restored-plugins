package io.lumine.mythic.lib.data.yaml;

import io.lumine.mythic.lib.data.OfflineDataHolder;
import io.lumine.mythic.lib.data.SynchronizedDataHandler;
import io.lumine.mythic.lib.data.SynchronizedDataHolder;
import io.lumine.mythic.lib.util.ConfigFile;
import java.util.Objects;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public abstract class YAMLSynchronizedDataHandler<H extends SynchronizedDataHolder, O extends OfflineDataHolder> implements SynchronizedDataHandler<H, O> {
   private final Plugin owning;

   public YAMLSynchronizedDataHandler(Plugin owning) {
      this.owning = Objects.requireNonNull(owning, "Plugin cannot be null");
   }

   @Deprecated
   public YAMLSynchronizedDataHandler(Plugin owning, boolean profilePlugin) {
      this(owning);
   }

   @Override
   public void saveData(@NotNull H playerData, boolean autosave) {
      ConfigFile configFile = this.getUserFile(playerData);
      this.saveInSection(playerData, configFile.getConfig());
      configFile.save();
   }

   public abstract void saveInSection(@NotNull H var1, @NotNull ConfigurationSection var2);

   @Override
   public boolean loadData(@NotNull H playerData) {
      this.loadFromSection(playerData, this.getUserFile(playerData).getConfig());
      return true;
   }

   public abstract void loadFromSection(@NotNull H var1, @NotNull ConfigurationSection var2);

   private ConfigFile getUserFile(@NotNull H playerData) {
      return new ConfigFile(this.owning, "/userdata", playerData.getEffectiveId().toString());
   }
}
