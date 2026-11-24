package net.Indyuce.inventory.manager.data.sql;

import io.lumine.mythic.lib.data.sql.SQLDataSource;
import io.lumine.mythic.lib.data.sql.SQLDataSynchronizer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.inventory.CustomInventoryHandler;
import net.Indyuce.inventory.slot.CustomSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;

public class MMOInventoryDataSynchronizer extends SQLDataSynchronizer<CustomInventoryHandler> {
   public MMOInventoryDataSynchronizer(SQLDataSource dataSource, CustomInventoryHandler data) {
      super("mmoinventory_inventories", "uuid", dataSource, data);
   }

   public void loadData(ResultSet result) throws SQLException, IOException, ClassNotFoundException {
      String columnValue = result.getString("inventory");
      if (columnValue != null && !columnValue.isEmpty()) {
         byte[] serializedObject = Base64.getDecoder().decode(columnValue);
         ByteArrayInputStream inputStream = new ByteArrayInputStream(serializedObject);
         BukkitObjectInputStream bukkitAdapter = new BukkitObjectInputStream(inputStream);
         int avail = bukkitAdapter.readInt();

         while (avail-- > 0) {
            int slot = bukkitAdapter.readInt();
            ItemStack stack = (ItemStack)bukkitAdapter.readObject();
            CustomSlot customSlot = MMOInventory.plugin.getSlotManager().get(slot);
            ((CustomInventoryHandler)this.getData()).setItem(customSlot, stack);
         }

         bukkitAdapter.close();
      }
   }

   public void loadEmptyData() {
   }
}
