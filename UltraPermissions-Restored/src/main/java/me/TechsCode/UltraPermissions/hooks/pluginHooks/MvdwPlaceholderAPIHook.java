package me.TechsCode.UltraPermissions.hooks.pluginHooks;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import java.util.Optional;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.networking.NServer;
import me.TechsCode.UltraPermissions.hooks.UpermsPlaceholder;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class MvdwPlaceholderAPIHook {
   public MvdwPlaceholderAPIHook(UltraPermissions ultraPermissions) {
      Optional thisServer = ultraPermissions.getThisServer();

      for (UpermsPlaceholder placeholder : UltraPermissions.placeholders) {
         PlaceholderAPI.registerPlaceholder((Plugin)ultraPermissions.getBootstrap(), "uperms_" + placeholder.getName(), event -> {
            Optional optional = ultraPermissions.getUsers().uuid(event.getOfflinePlayer().getUniqueId());
            return optional.isPresent() ? placeholder.get((User)optional.get(), (NServer)thisServer.orElse(null)) : "";
         });
      }
   }

   public String replace(Player player, String text) {
      return PlaceholderAPI.replacePlaceholders(player, text);
   }
}
