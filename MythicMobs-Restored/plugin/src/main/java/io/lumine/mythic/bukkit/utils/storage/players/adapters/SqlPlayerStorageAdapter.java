package io.lumine.mythic.bukkit.utils.storage.players.adapters;

import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.plugin.PluginModule;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.bukkit.utils.storage.players.PlayerStorageAdapter;
import io.lumine.mythic.bukkit.utils.storage.players.Profile;
import io.lumine.mythic.bukkit.utils.storage.sql.SqlConnector;
import java.util.Optional;
import java.util.UUID;

public abstract class SqlPlayerStorageAdapter<T extends LuminePlugin, P extends Profile> extends PluginModule<T> implements PlayerStorageAdapter<P> {
   private SqlConnector connector;

   public SqlPlayerStorageAdapter(T plugin, SqlConnector connector) {
      super(plugin, false);
      this.connector = connector;
   }

   @Override
   public abstract Promise<Optional<P>> load(UUID uuid, int value);

   @Override
   public abstract Promise<Boolean> save(UUID uuid, P p);

   @Override
   public abstract boolean saveSync(UUID uuid, P p);

   @Override
   public abstract Promise<Optional<P>> loadByName(String str, int value);

   public SqlConnector getConnector() {
      return this.connector;
   }
}
