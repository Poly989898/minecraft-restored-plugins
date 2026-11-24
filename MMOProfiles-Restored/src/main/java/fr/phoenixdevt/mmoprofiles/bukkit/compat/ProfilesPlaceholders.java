package fr.phoenixdevt.mmoprofiles.bukkit.compat;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.ProfileListImpl;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang3.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ProfilesPlaceholders extends PlaceholderExpansion {
   private static final String ERROR_PLACEHOLDER = " ";

   public boolean persist() {
      return true;
   }

   public boolean canRegister() {
      return true;
   }

   public String getAuthor() {
      return "Indyuce";
   }

   public String getIdentifier() {
      return "mmoprofiles";
   }

   public String getVersion() {
      return MMOProfiles.plugin.getDescription().getVersion();
   }

   public String onRequest(OfflinePlayer player, String identifier) {
      if (!MMOProfiles.plugin.playerDataManager.isLoaded(player.getUniqueId())) {
         return null;
      }

      ProfileListImpl profileList = ProfileListImpl.get((Player)player);
      if (identifier.equals("real_uuid")) {
         return profileList.getEffectiveId().toString();
      }

      if (identifier.equals("current_profile_uuid")) {
         return profileList.getCurrent() == null ? " " : profileList.getCurrent().getUniqueId().toString();
      }

      if (identifier.equals("current_profile_name")) {
         return profileList.getCurrent() == null ? " " : profileList.getCurrent().getName();
      }

      if (identifier.equals("current_profile_slot")) {
         try {
            return String.valueOf(profileList.getCurrentProfileIndex() + 1);
         } catch (Exception exception) {
            return " ";
         }
      } else {
         if (identifier.equals("amount_profiles")) {
            return String.valueOf(profileList.getProfiles().size());
         }

         if (identifier.equals("profile_slots")) {
            return String.valueOf(profileList.getProfileSlots());
         }

         if (identifier.equals("remaining_profile_slots")) {
            return String.valueOf(profileList.getProfileSlots() - profileList.getProfiles().size());
         }

         if (!identifier.startsWith("profile_name_")) {
            return " ";
         }

         try {
            int index = Integer.parseInt(identifier.substring(13)) - 1;
            Validate.isTrue(index >= 0 && index < profileList.getProfiles().size());
            return profileList.getProfiles().get(index).getName();
         } catch (Exception exception) {
            return " ";
         }
      }
   }
}
