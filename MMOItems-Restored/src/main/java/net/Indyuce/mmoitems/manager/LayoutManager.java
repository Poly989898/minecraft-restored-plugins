package net.Indyuce.mmoitems.manager;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.crafting.Layout;
import org.bukkit.configuration.file.YamlConfiguration;

public class LayoutManager implements Reloadable {
   private final Map<String, Layout> layouts = new HashMap<>();

   @Override
   public void reload() {
      this.layouts.clear();

      for (File listFile : new File(MMOItems.plugin.getDataFolder() + "/layouts").listFiles()) {
         try {
            Layout layout = new Layout(listFile.getName().substring(0, listFile.getName().length() - 4), YamlConfiguration.loadConfiguration(listFile));
            this.layouts.put(layout.getId(), layout);
         } catch (IllegalArgumentException exception) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not load layout '" + listFile.getName() + "': " + exception.getMessage());
         }
      }
   }

   public boolean hasLayout(String text) {
      return this.layouts.containsKey(text);
   }

   public Collection<Layout> getLayouts() {
      return this.layouts.values();
   }

   public Layout getLayout(String text) {
      return this.layouts.getOrDefault(text, this.layouts.get("default"));
   }
}
