package net.Indyuce.inventory.manager.data.sql;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.data.DefaultOfflineDataHolder;
import io.lumine.mythic.lib.data.sql.SQLDataSource;
import io.lumine.mythic.lib.data.sql.SQLSynchronizedDataHandler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.inventory.CustomInventoryHandler;
import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.inventory.InventoryItem;
import net.Indyuce.inventory.inventory.InventoryLookupMode;
import net.Indyuce.inventory.manager.data.yaml.YAMLDataHandler;
import org.apache.commons.lang3.Validate;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class SQLDataHandler extends SQLSynchronizedDataHandler<InventoryHandler, DefaultOfflineDataHolder> {
   private static final String[] NEW_COLUMNS = new String[]{"is_saved", "TINYINT"};

   public SQLDataHandler(SQLDataSource dataSource) {
      super(dataSource);
   }

   public void setup() {
      this.getDataSource()
         .executeUpdate(
            "CREATE TABLE IF NOT EXISTS mmoinventory_inventories (uuid VARCHAR(36) NOT NULL,inventory LONGTEXT,is_saved TINYINT,PRIMARY KEY (uuid));"
         );
      this.getDataSource().executeUpdate("ALTER TABLE `mmoinventory_inventories` MODIFY `inventory` LONGTEXT;");

      for (int i = 0; i < NEW_COLUMNS.length; i += 2) {
         String columnName = NEW_COLUMNS[i];
         String dataType = NEW_COLUMNS[i + 1];
         this.getDataSource()
            .getResultAsync(
               "SELECT * FROM information_schema.COLUMNS WHERE TABLE_NAME = 'mmoinventory_inventories' AND COLUMN_NAME = '" + columnName + "'", result -> {
                  try {
                     if (!result.next()) {
                        this.getDataSource().executeUpdate("ALTER TABLE mmoinventory_inventories ADD COLUMN " + columnName + " " + dataType);
                     }
                  } catch (SQLException exception) {
                     exception.printStackTrace();
                  }
               }
            );
      }
   }

   public MMOInventoryDataSynchronizer newDataSynchronizer(InventoryHandler playerData) {
      Validate.isInstanceOf(CustomInventoryHandler.class, playerData, "Custom inventory is disabled", new Object[0]);
      return new MMOInventoryDataSynchronizer(this.getDataSource(), (CustomInventoryHandler)playerData);
   }

   public void saveData(InventoryHandler data, boolean autosave) {
      Validate.isInstanceOf(CustomInventoryHandler.class, data, "Custom inventory is disabled", new Object[0]);
      UUID effectiveId = data.getEffectiveId();

      try {
         Collection<InventoryItem> items = data.getItems(InventoryLookupMode.IGNORE_RESTRICTIONS);
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         BukkitObjectOutputStream bukkitAdapter = new BukkitObjectOutputStream(outputStream);
         bukkitAdapter.writeInt(items.size());

         for (InventoryItem entry : items) {
            int slot = entry.getSlot().getIndex();
            bukkitAdapter.writeInt(slot);
            bukkitAdapter.writeObject(entry.getItemStack());
         }

         bukkitAdapter.close();
         String base64 = new String(Base64.getEncoder().encode(outputStream.toByteArray()));

         try {
            Connection connection = this.getDataSource().getConnection();
            PreparedStatement prepared = connection.prepareStatement(
               "INSERT INTO mmoinventory_inventories (uuid, inventory, `is_saved`) VALUES(?,?,?) ON DUPLICATE KEY UPDATE inventory = VALUES(`inventory`), `is_saved` = VALUES(`is_saved`);"
            );

            try {
               UtilityMethods.debug(MMOInventory.plugin, "SQL", "Saving data of '" + effectiveId + "'");
               prepared.setString(1, effectiveId.toString());
               prepared.setString(2, base64);
               prepared.setInt(3, autosave ? 0 : 1);
               prepared.executeUpdate();
               UtilityMethods.debug(MMOInventory.plugin, "SQL", "Saved data of '" + effectiveId + "'");
            } catch (Throwable throwable) {
               MMOInventory.plugin
                  .getLogger()
                  .log(Level.WARNING, "Could not save player inventory of " + data.getPlayer().getName() + " (" + effectiveId + ")");
               throwable.printStackTrace();
            } finally {
               prepared.close();
               connection.close();
            }
         } catch (SQLException exception) {
            MMOInventory.plugin
               .getLogger()
               .log(Level.WARNING, "Could not save player inventory of " + data.getPlayer().getName() + " (" + effectiveId + "), saving in YAML instead");
            exception.printStackTrace();
            new YAMLDataHandler().saveData(data, autosave);
         }
      } catch (IOException exception) {
         MMOInventory.plugin.getLogger().log(Level.WARNING, "Could not save player inventory of " + data.getPlayer().getName() + " (" + effectiveId + ")");
         exception.printStackTrace();
      }
   }

   public DefaultOfflineDataHolder getOffline(UUID uuid) {
      return new DefaultOfflineDataHolder(uuid);
   }
}
