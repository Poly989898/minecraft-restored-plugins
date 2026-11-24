package net.Indyuce.mmocore.manager;

import io.lumine.mythic.lib.util.FileUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.loot.droptable.DropTable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DropTableManager implements MMOCoreManager {
   private final Map<String, DropTable> map = new HashMap<>();

   public void register(DropTable table) {
      this.map.put(table.getId(), table);
   }

   @Nullable
   public DropTable get(String path) {
      return this.map.get(path);
   }

   public boolean has(String path) {
      return this.map.containsKey(path);
   }

   @NotNull
   public Collection<DropTable> getDropTables() {
      return this.map.values();
   }

   @NotNull
   public Set<String> getKeys() {
      return this.map.keySet();
   }

   @NotNull
   public DropTable loadDropTable(Object obj) throws IllegalArgumentException {
      if (obj instanceof String) {
         return this.get((String)obj);
      } else if (obj instanceof ConfigurationSection) {
         DropTable table = new DropTable((ConfigurationSection)obj);
         Bukkit.getScheduler().runTask(MMOCore.plugin, table.getPostLoadAction()::performAction);
         return table;
      } else {
         throw new IllegalArgumentException("Could not parse drop table.");
      }
   }

   @Override
   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         this.map.clear();
      }

      FileUtils.loadObjectsFromFolder(
         MMOCore.plugin, "drop-tables", false, (name, config) -> this.register(new DropTable(config)), "Could not load drop table '%s' from file '%s': %s"
      );
      Bukkit.getScheduler().runTask(MMOCore.plugin, () -> this.map.values().forEach(table -> table.getPostLoadAction().performAction()));
   }
}
