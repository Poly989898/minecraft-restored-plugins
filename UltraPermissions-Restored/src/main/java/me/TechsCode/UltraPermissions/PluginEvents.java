package me.TechsCode.UltraPermissions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import me.TechsCode.UltraPermissions.base.legacy.task.UpdateEvent;
import me.TechsCode.UltraPermissions.base.legacy.task.UpdateTime;
import me.TechsCode.UltraPermissions.internal.ModifiedPermissible;
import me.TechsCode.UltraPermissions.internal.PermissibleInjector;
import me.TechsCode.UltraPermissions.permissionlogger.PermissionLogger;
import me.TechsCode.UltraPermissions.storage.collection.GroupList;
import me.TechsCode.UltraPermissions.storage.collection.PermissionList;
import me.TechsCode.UltraPermissions.storage.collection.UserList;
import me.TechsCode.UltraPermissions.storage.objects.Permission;
import me.TechsCode.UltraPermissions.storage.objects.User;
import me.TechsCode.UltraPermissions.storage.objects.UserRankup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.Plugin;

public class PluginEvents implements Listener {
   private final UltraPermissions plugin;
   private final PermissionLogger permissionLogger;
   private final HashMap<Player, ModifiedPermissible> permissibleHashMap;

   public PluginEvents(UltraPermissions ultraPermissions, PermissionLogger permissionLogger) {
      this.plugin = ultraPermissions;
      this.permissionLogger = permissionLogger;
      this.permissibleHashMap = new HashMap<>();
      Bukkit.getOnlinePlayers().forEach(this::login);
      Bukkit.getPluginManager().registerEvents(this, (Plugin)ultraPermissions.getBootstrap());
   }

   public void login(Player player) {
      Optional optional = this.plugin.getUsers().uuid(player.getUniqueId());
      if (optional.isPresent()) {
         if (!((User)optional.get()).getName().equals(player.getName())) {
            ((User)optional.get()).setPlayerName(player.getName());
         }
      } else {
         this.plugin.registerUser(player.getUniqueId(), player.getName(), this.plugin.getDefaultGroupAssignOption() == DefaultGroupAssignOption.FIRST_JOIN);
      }

      try {
         ModifiedPermissible modifiedPermissible = new ModifiedPermissible(player, this.plugin, this.permissionLogger);
         this.permissibleHashMap.put(player, modifiedPermissible);
         PermissibleInjector.inject(player, modifiedPermissible);
      } catch (IllegalAccessException exception) {
         exception.printStackTrace();
      }
   }

   public Collection<ModifiedPermissible> getPermissibles() {
      return this.permissibleHashMap.values();
   }

   @EventHandler(priority = EventPriority.LOW)
   public void login(PlayerLoginEvent event) {
      if (event.getResult() != Result.KICK_WHITELIST) {
         this.login(event.getPlayer());
      }
   }

   @EventHandler
   public void quit(PlayerQuitEvent event) {
      this.permissibleHashMap.remove(event.getPlayer());
   }

   @EventHandler
   public void updateData(UpdateEvent event) {
      if (event.getUpdateTime() == UpdateTime.SEC) {
         this.plugin.getScheduler().runAsync(() -> {
            GroupList groups = this.plugin.getGroups();
            UserList users = this.plugin.getUsers();
            PermissionList permissions = this.plugin.getPermissions();
            GroupList list = groups.defaults(true);

            for (User user : users) {
               DefaultGroupAssignOption defaultGroupAssignOption = this.plugin.getDefaultGroupAssignOption();
               boolean flag = user.getGroups().isEmpty() && defaultGroupAssignOption == DefaultGroupAssignOption.NO_GROUP;
               boolean alwaysAssign = defaultGroupAssignOption == DefaultGroupAssignOption.ALWAYS_ASSIGN;
               if (flag || alwaysAssign) {
                  list.stream().filter(group -> !user.getGroups().contains(group.toStored())).forEach(user::addGroup);
               }

               user.getRankups().stream().filter(UserRankup::isExpired).forEach(UserRankup::remove);
            }

            groups.forEach(group -> group.getActiveInheritedGroups().forEach(inheritedGroup -> {
               boolean flag = !group.getServer().equals(inheritedGroup.getServer());
               boolean worldDiffers = !group.getWorld().equals(inheritedGroup.getWorld());
               if (flag || worldDiffers) {
                  group.removeInheritance(inheritedGroup);
               }
            }));
            permissions.expired().forEach(Permission::remove);
         });
      }
   }
}
