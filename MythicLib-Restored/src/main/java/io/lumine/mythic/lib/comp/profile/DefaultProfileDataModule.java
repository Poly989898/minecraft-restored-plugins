package io.lumine.mythic.lib.comp.profile;

import fr.phoenixdevt.profiles.ProfileDataModule;
import fr.phoenixdevt.profiles.event.ProfileCreateEvent;
import fr.phoenixdevt.profiles.event.ProfileRemoveEvent;
import io.lumine.mythic.lib.UtilityMethods;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class DefaultProfileDataModule implements ProfileDataModule {
   private final JavaPlugin plugin;
   private final String id;

   public DefaultProfileDataModule(@NotNull JavaPlugin plugin) {
      this.plugin = plugin;
      this.id = UtilityMethods.enumName(plugin.getName()).toLowerCase();
   }

   public JavaPlugin getOwningPlugin() {
      return this.plugin;
   }

   public String getIdentifier() {
      return this.id;
   }

   @EventHandler
   public void onProfileCreate(ProfileCreateEvent event) {
      event.validate(this);
   }

   @EventHandler
   public void onProfileDelete(ProfileRemoveEvent event) {
      event.validate(this);
   }
}
