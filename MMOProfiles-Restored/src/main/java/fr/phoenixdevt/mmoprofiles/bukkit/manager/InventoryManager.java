package fr.phoenixdevt.mmoprofiles.bukkit.manager;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.ConfirmDeletionViewer;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.ProfileListViewer;
import fr.phoenixdevt.mmoprofiles.bukkit.utils.ConfigFile;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.EditableInventory;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class InventoryManager {
   public static final ProfileListViewer PROFILE_LIST = new ProfileListViewer();
   public static final ConfirmDeletionViewer CONFIRM_DELETION = new ConfirmDeletionViewer();
   public static final List<EditableInventory> LIST = Arrays.asList(PROFILE_LIST, CONFIRM_DELETION);

   public static void reload() {
      LIST.forEach(inv -> {
         MMOProfiles.plugin.configManager.loadDefaultFile("language/gui", inv.getId() + ".yml");

         try {
            inv.reload(MMOProfiles.plugin, new ConfigFile("/language/gui", inv.getId()).getConfig());
         } catch (IllegalArgumentException exception) {
            MMOProfiles.plugin.getLogger().log(Level.WARNING, "Could not load inventory " + inv.getId() + ": " + exception.getMessage());
         }
      });
   }
}
