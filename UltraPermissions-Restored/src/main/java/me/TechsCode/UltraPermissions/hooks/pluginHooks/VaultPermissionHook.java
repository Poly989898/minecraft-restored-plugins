package me.TechsCode.UltraPermissions.hooks.pluginHooks;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import me.TechsCode.UltraPermissions.CachedPlayerPermissionProvider;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.networking.NServer;
import me.TechsCode.UltraPermissions.internal.lookup.LookupCheck;
import me.TechsCode.UltraPermissions.internal.lookup.PermissionLookup;
import me.TechsCode.UltraPermissions.internal.lookup.checks.BlankCheck;
import me.TechsCode.UltraPermissions.internal.lookup.checks.DefaultPermissionCheck;
import me.TechsCode.UltraPermissions.internal.lookup.checks.DefinedPermissionCheck;
import me.TechsCode.UltraPermissions.internal.lookup.checks.OperatorLookupCheck;
import me.TechsCode.UltraPermissions.storage.collection.PermissionList;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.Holder;
import me.TechsCode.UltraPermissions.storage.objects.PermissionHolder;
import me.TechsCode.UltraPermissions.storage.objects.User;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VaultPermissionHook extends Permission {
   private final UltraPermissions plugin;
   private final CachedPlayerPermissionProvider permissionProvider;

   public VaultPermissionHook(UltraPermissions ultraPermissions) {
      this.plugin = ultraPermissions;
      this.permissionProvider = new CachedPlayerPermissionProvider(ultraPermissions);
   }

   public void clearCache() {
      this.permissionProvider.clearCache();
   }

   public String getName() {
      return "UltraPermissions";
   }

   public boolean isEnabled() {
      return true;
   }

   public boolean hasSuperPermsCompat() {
      return true;
   }

   public boolean playerHas(String text, final String playerName, final String permission) {
      if (playerName == null) {
         return false;
      }

      if (text == null) {
         text = UUID.randomUUID().toString();
      }

      final String worldName = text;
      PermissionLookup permissionLookup = new PermissionLookup(permission) {
         @Override
         public LookupCheck[] getChecks() {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            return new LookupCheck[]{
               offlinePlayer != null ? new OperatorLookupCheck(VaultPermissionHook.this.plugin, offlinePlayer) : new BlankCheck(),
               VaultPermissionHook.this.plugin.isDefaultPermissionsEnabled() && offlinePlayer != null ? new DefaultPermissionCheck(offlinePlayer) : new BlankCheck(),
               new DefinedPermissionCheck() {
                  @Override
                  public List<me.TechsCode.UltraPermissions.storage.objects.Permission> getDefinedPermissions() {
                     Optional optional = VaultPermissionHook.this.user(playerName);
                     if (!optional.isPresent()) {
                        return Collections.emptyList();
                     }

                     User user = (User)optional.get();
                     PermissionList worldPermissions = VaultPermissionHook.this.plugin
                        .getPermissions()
                        .holder(Collections.singletonList(Holder.fromUser(user)))
                        .servers(true, VaultPermissionHook.this.plugin.getThisServer().map(NServer::getName).orElse(null))
                        .worlds(true, worldName);
                     Optional worldFirst = worldPermissions.name(permission).stream().findFirst();
                     if (worldFirst.isPresent()) {
                        return Collections.singletonList((me.TechsCode.UltraPermissions.storage.objects.Permission)worldFirst.get());
                     }

                     for (Group toValue : user.getActiveGroups()
                        .servers(true, VaultPermissionHook.this.plugin.getThisServer().map(NServer::getName).orElse(null))
                        .worlds(true, worldName != null && !worldName.isEmpty() ? worldName : "world")
                        .stream()
                        .sorted(Comparator.comparingInt(Group::getPriority))
                        .toList()) {
                        PermissionList list = VaultPermissionHook.this.plugin
                           .getPermissions()
                           .holder(Collections.singletonList(Holder.fromGroup(toValue)))
                           .servers(true, VaultPermissionHook.this.plugin.getThisServer().map(NServer::getName).orElse(null))
                           .worlds(true, worldName);
                        Optional first = list.name(permission).stream().findFirst();
                        if (first.isPresent()) {
                           return Collections.singletonList((me.TechsCode.UltraPermissions.storage.objects.Permission)first.get());
                        }
                     }

                     return Collections.emptyList();
                  }
               }
            };
         }
      };
      return permissionLookup.perform().getResult();
   }

   public boolean playerHas(String text, OfflinePlayer offlinePlayer, String permission) {
      return this.playerHas(text, offlinePlayer.getName(), permission);
   }

   public boolean playerHas(Player player, String text) {
      return player.hasPermission(text);
   }

   public boolean playerAdd(String text, String playerName, String permission) {
      return this.add(text, this.user(playerName).orElse(null), permission);
   }

   public boolean playerAdd(String text, OfflinePlayer offlinePlayer, String permission) {
      return this.playerAdd(text, offlinePlayer.getName(), permission);
   }

   public boolean playerAdd(Player player, String text) {
      return this.playerAdd(player.getWorld().getName(), player.getName(), text);
   }

   public boolean playerRemove(String text, String playerName, String permission) {
      return this.remove(text, this.user(playerName).orElse(null), permission);
   }

   public boolean playerRemove(String text, OfflinePlayer offlinePlayer, String permission) {
      return this.playerRemove(text, offlinePlayer.getName(), permission);
   }

   public boolean playerRemove(Player player, String text) {
      return this.playerRemove(player.getWorld().getName(), player.getName(), text);
   }

   public boolean groupHas(String text, String groupName, String permission) {
      Preconditions.checkArgument(groupName != null, "Group Name has to be defined");
      final Optional optional = this.group(text, groupName);
      PermissionLookup permissionLookup = new PermissionLookup(permission) {
         @Override
         public LookupCheck[] getChecks() {
            return new LookupCheck[]{optional.isPresent() ? new DefinedPermissionCheck() {
               @Override
               public List<me.TechsCode.UltraPermissions.storage.objects.Permission> getDefinedPermissions() {
                  PermissionList permissions = ((Group)optional.get()).getPermissions();
                  permissions.addAll(((Group)optional.get()).getInheritedPermissions());
                  return permissions;
               }
            } : new BlankCheck()};
         }
      };
      return permissionLookup.getOutcome() == null ? false : permissionLookup.getOutcome().getResult();
   }

   public boolean groupHas(World world, String text, String permission) {
      return this.groupHas(world.getName(), text, permission);
   }

   public boolean groupAdd(String text, String groupName, String permission) {
      return this.add(text, this.group(text, groupName).orElse(null), permission);
   }

   public boolean groupAdd(World world, String text, String permission) {
      return this.groupAdd(world.getName(), text, permission);
   }

   public boolean groupRemove(String text, String groupName, String permission) {
      return this.remove(text, this.group(text, groupName).orElse(null), permission);
   }

   public boolean groupRemove(World world, String text, String permission) {
      return this.groupRemove(world.getName(), text, permission);
   }

   public boolean playerInGroup(String text, String playerName, String groupName) {
      Optional optional = this.user(playerName);
      Optional groupOptional = this.group(text, groupName);
      return optional.isPresent() && groupOptional.isPresent() ? ((User)optional.get()).getGroups().contains(((Group)groupOptional.get()).toStored()) : false;
   }

   public boolean playerInGroup(World world, String text, String groupName) {
      return this.playerInGroup(world.getName(), text, groupName);
   }

   public boolean playerInGroup(String text, OfflinePlayer offlinePlayer, String groupName) {
      return this.playerInGroup(text, offlinePlayer.getName(), groupName);
   }

   public boolean playerInGroup(Player player, String text) {
      return this.playerInGroup(player.getWorld().getName(), player.getName(), text);
   }

   public boolean playerAddGroup(String text, String playerName, String groupName) {
      Optional optional = this.user(playerName);
      Optional groupOptional = this.group(text, groupName);
      if (optional.isPresent() && groupOptional.isPresent()) {
         ((User)optional.get()).addGroup((Group)groupOptional.get());
         return true;
      } else {
         return false;
      }
   }

   public boolean playerAddGroup(String text, OfflinePlayer offlinePlayer, String groupName) {
      return this.playerAddGroup(text, offlinePlayer.getName(), groupName);
   }

   public boolean playerAddGroup(Player player, String text) {
      return this.playerAddGroup(player.getWorld().getName(), player.getName(), text);
   }

   public boolean playerRemoveGroup(String text, String playerName, String groupName) {
      Optional optional = this.user(playerName);
      Optional groupOptional = this.group(text, groupName);
      if (optional.isPresent() && groupOptional.isPresent()) {
         if (!((User)optional.get()).getGroups().contains(((Group)groupOptional.get()).toStored())) {
            return false;
         }

         ((User)optional.get()).removeGroup((Group)groupOptional.get());
         return true;
      } else {
         return false;
      }
   }

   public boolean playerRemoveGroup(String text, OfflinePlayer offlinePlayer, String groupName) {
      return this.playerRemoveGroup(text, offlinePlayer.getName(), groupName);
   }

   public boolean playerRemoveGroup(Player player, String text) {
      return this.playerRemoveGroup(player.getWorld().getName(), player.getName(), text);
   }

   public String[] getPlayerGroups(String text, String playerName) {
      Optional<User> optional = this.user(playerName);
      return optional.<String[]>map(
            user -> user.getActiveGroups()
               .servers(true, this.plugin.getThisServer().map(NServer::getName).orElse(null))
               .worlds(true, text != null && !text.isEmpty() ? text : "world")
               .stream()
               .map(Group::getName)
               .toArray(String[]::new)
         )
         .orElseGet(() -> new String[0]);
   }

   public String[] getPlayerGroups(String text, OfflinePlayer offlinePlayer) {
      return this.getPlayerGroups(text, offlinePlayer.getName());
   }

   public String[] getPlayerGroups(Player player) {
      return this.getPlayerGroups(player.getWorld().getName(), player.getName());
   }

   public String getPrimaryGroup(String text, String playerName) {
      String[] playerGroups = this.getPlayerGroups(text, playerName);
      return playerGroups.length == 0 ? null : playerGroups[0];
   }

   public String getPrimaryGroup(String text, OfflinePlayer offlinePlayer) {
      return this.getPrimaryGroup(text, offlinePlayer.getName());
   }

   public String getPrimaryGroup(Player player) {
      return this.getPrimaryGroup(player.getWorld().getName(), player.getName());
   }

   public String[] getGroups() {
      return this.plugin
         .getGroups()
         .servers(true, this.plugin.getThisServer().map(NServer::getName).orElse(null))
         .stream()
         .map(Group::getName)
         .toArray(String[]::new);
   }

   public boolean hasGroupSupport() {
      return true;
   }

   public boolean has(Player player, String text) {
      return player.hasPermission(text);
   }

   public boolean has(CommandSender sender, String text) {
      return sender.hasPermission(text);
   }

   public Optional<User> user(String text) {
      return this.plugin.getUsers().name(text);
   }

   public Optional<Group> group(String text, String groupName) {
      return this.plugin.getGroups().servers(true, this.plugin.getThisServer().map(NServer::getName).orElse(null)).worlds(true, text).name(groupName);
   }

   public boolean add(String text, PermissionHolder permissionHolder, String permission) {
      if (permissionHolder == null) {
         return false;
      }

      permissionHolder.newPermission(permission)
         .setWorld(text != null && !text.isEmpty() ? text : "world")
         .setServer(this.plugin.getThisServer().map(NServer::getName).orElse(null))
         .create();
      return true;
   }

   public boolean remove(String text, PermissionHolder permissionHolder, String permission) {
      if (permissionHolder == null) {
         return false;
      }

      permissionHolder.getPermissions()
         .name(permission)
         .servers(true, this.plugin.getThisServer().map(NServer::getName).orElse(null))
         .worlds(true, text != null && !text.isEmpty() ? text : "world")
         .forEach(me.TechsCode.UltraPermissions.storage.objects.Permission::remove);
      return true;
   }
}
