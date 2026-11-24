package net.Indyuce.mmocore.comp.profile;

import fr.phoenixdevt.profiles.ProfileDataModule;
import fr.phoenixdevt.profiles.ProfileProvider;
import fr.phoenixdevt.profiles.event.ProfileCreateEvent;
import fr.phoenixdevt.profiles.event.ProfileRemoveEvent;
import fr.phoenixdevt.profiles.event.ProfileSelectEvent;
import fr.phoenixdevt.profiles.event.ProfileUnloadEvent;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.SynchronizedDataLoadEvent;
import io.lumine.mythic.lib.comp.profile.ProfileMode;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.manager.InventoryManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class ForceClassProfileDataModule implements ProfileDataModule {
   public ForceClassProfileDataModule() {
      ProfileProvider<?> provider = (ProfileProvider<?>)Bukkit.getServicesManager().getRegistration(ProfileProvider.class).getProvider();
      provider.registerModule(this);
   }

   public JavaPlugin getOwningPlugin() {
      return MMOCore.plugin;
   }

   public String getIdentifier() {
      return "mmocore_force_class";
   }

   @EventHandler
   public void onProfileCreate(ProfileCreateEvent event) {
      if (MythicLib.plugin.getProfileMode() == ProfileMode.PROXY) {
         event.validate(this);
      } else {
         PlayerData playerData = PlayerData.get(event.getPlayerData().getPlayer());
         InventoryManager.CLASS_SELECT.newInventory(playerData, () -> event.validate(this)).open();
      }
   }

   @EventHandler
   public void onDataLoad(SynchronizedDataLoadEvent event) {
      if (event.getManager().getOwningPlugin().equals(MMOCore.plugin)) {
         PlayerData playerData = (PlayerData)event.getHolder();
         if (!event.hasProfileEvent()) {
            Validate.isTrue(
               MythicLib.plugin.getProfileMode() == ProfileMode.PROXY,
               "Listened to a data load event with no profile event attached but proxy-based profiles are disabled"
            );
            if (playerData.getProfess().equals(MMOCore.plugin.classManager.getDefaultClass())) {
               InventoryManager.CLASS_SELECT.newInventory(playerData, () -> {}).open();
            }

            return;
         }

         ProfileSelectEvent event1 = (ProfileSelectEvent)event.getProfileEvent();
         if (playerData.getProfess().equals(MMOCore.plugin.classManager.getDefaultClass())) {
            InventoryManager.CLASS_SELECT.newInventory(playerData, () -> event1.validate(this)).open();
         } else {
            event1.validate(this);
         }
      }
   }

   @EventHandler
   public void onProfileRemove(ProfileRemoveEvent event) {
      event.validate(this);
   }

   @EventHandler
   public void onProfileUnload(ProfileUnloadEvent event) {
      event.validate(this);
   }
}
