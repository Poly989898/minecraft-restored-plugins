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

      for (File var4 : new File(MMOItems.plugin.getDataFolder() + "/layouts").listFiles()) {
         try {
            Layout var5 = new Layout(var4.getName().substring(0, var4.getName().length() - 4), YamlConfiguration.loadConfiguration(var4));
            this.layouts.put(var5.getId(), var5);
         } catch (IllegalArgumentException var6) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not load layout '" + var4.getName() + "': " + var6.getMessage());
         }
      }
   }

   public boolean hasLayout(String var1) {
      return this.layouts.containsKey(var1);
   }

   public Collection<Layout> getLayouts() {
      return this.layouts.values();
   }

   public Layout getLayout(String var1) {
      return this.layouts.getOrDefault(var1, this.layouts.get("default"));
   }
}
