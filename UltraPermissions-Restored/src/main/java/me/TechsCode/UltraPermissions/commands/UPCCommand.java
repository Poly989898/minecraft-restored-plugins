package me.TechsCode.UltraPermissions.commands;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.CachedOnlineChecker;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.command.ArgumentValue;
import me.TechsCode.UltraPermissions.base.command.Command;
import me.TechsCode.UltraPermissions.base.command.CommandNode;
import me.TechsCode.UltraPermissions.base.command.EmptyReason;
import me.TechsCode.UltraPermissions.base.command.arguments.AnyArgument;
import me.TechsCode.UltraPermissions.base.command.arguments.AnyIntegerArgument;
import me.TechsCode.UltraPermissions.base.command.arguments.SpecificArgument;
import me.TechsCode.UltraPermissions.base.command.arguments.SpecificArguments;
import me.TechsCode.UltraPermissions.base.legacy.Tools;
import me.TechsCode.UltraPermissions.base.networking.NServer;
import me.TechsCode.UltraPermissions.base.storage.Stored;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.commands.arguments.BooleanArgument;
import me.TechsCode.UltraPermissions.commands.arguments.GroupArgument;
import me.TechsCode.UltraPermissions.commands.arguments.PermissionArgument;
import me.TechsCode.UltraPermissions.commands.arguments.ServerArgument;
import me.TechsCode.UltraPermissions.commands.arguments.StringArgument;
import me.TechsCode.UltraPermissions.commands.arguments.UserArgument;
import me.TechsCode.UltraPermissions.commands.arguments.WorldArgument;
import me.TechsCode.UltraPermissions.commands.requirements.GrantGroupsRequirement;
import me.TechsCode.UltraPermissions.commands.requirements.MySQLEnabledRequirement;
import me.TechsCode.UltraPermissions.commands.requirements.SuperadminRequirement;
import me.TechsCode.UltraPermissions.storage.collection.GroupList;
import me.TechsCode.UltraPermissions.storage.collection.PermissionList;
import me.TechsCode.UltraPermissions.storage.collection.UserList;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.Permission;
import me.TechsCode.UltraPermissions.storage.objects.PermissionHolder;
import me.TechsCode.UltraPermissions.storage.objects.User;
import me.TechsCode.UltraPermissions.storage.objects.UserRankup;
import me.TechsCode.UltraPermissions.transfer.TransferAssistant;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class UPCCommand extends Command {
   private final UltraPermissions plugin;
   private CachedOnlineChecker cachedOnlineChecker;
   private static final Phrase LIST_PLAYER_PERMISSIONS = Phrase.create(
      "command.listPlayerPermissions", "Permissions of User **%user%**", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase LIST_GROUP_PERMISSIONS = Phrase.create(
      "command.listGroupPermissions", "Permissions of Group **%group%**", Colors.GRAY, Colors.GREEN
   );
   private static final Phrase CREATE_GROUP_NO_SPACES = Phrase.create(
      "command.createGroup.noSpaces", "The Group Name **should not** contain spaces", Colors.GRAY, Colors.RED
   );
   private static final Phrase CREATE_GROUP_NO_COLORS = Phrase.create(
      "command.createGroup.noColors", "The Group Name **should not** contain Color Codes", Colors.GRAY, Colors.RED
   );
   private static final Phrase CREATE_GROUP_NAME_TAKEN = Phrase.create("command.createGroup.nameTaken", "This Group Name is already in use", Colors.GRAY);
   private static final Phrase CREATE_GROUP_SUCCESS = Phrase.create(
      "command.createGroup.success", "Successfully create a new Group (**%groupName%**)", Colors.GRAY, Colors.GREEN
   );
   private static final Phrase CREATE_GROUP_SUCCESS_BUNGEE = Phrase.create(
      "command.createGroup.success.bungee", "Successfully create a new Group (**%groupName%**) for **%server%**", Colors.GRAY, Colors.GREEN, Colors.YELLOW
   );
   private static final Phrase COPY_GROUP_SUCCESSFUL = Phrase.create(
      "command.copyGroup.successful", "Created a copy of Group **%group%** called **%copyGroup%**", Colors.GRAY, Colors.YELLOW, Colors.GREEN
   );
   private static final Phrase COPY_GROUP_UNSUCCESSFUL = Phrase.create(
      "command.copyGroup.unsuccessful", "A Group with the name **%group%** already exists", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase PLAYER_ADD_GROUP = Phrase.create(
      "command.playerAddGroup", "Added Group **%group%** to **%player%**", Colors.GRAY, Colors.YELLOW, Colors.AQUA
   );
   private static final Phrase PLAYER_ADD_GROUP_TIMED = Phrase.create(
      "command.playerAddGroupTimed", "Added Group **%group%** to **%player%** for **%operator% %time%**", Colors.GRAY, Colors.YELLOW, Colors.AQUA, Colors.RED
   );
   private static final Phrase PLAYER_ADD_GROUP_NOT_TIME_ALLOWED = Phrase.create(
      "command.playerAddGroup.notTimeAllowed",
      "You can't grant Group **%group%** to users for a duration of **%duration%**",
      Colors.GRAY,
      Colors.GREEN,
      Colors.YELLOW
   );
   private static final Phrase PLAYER_ADD_GROUP_WORD_PERMANENT = Phrase.create("command.playerAddGroup.word.permanent", "Permanent", Colors.RED);
   private static final Phrase GROUP_ALREADY_HAS_PERMISSION = Phrase.create(
      "command.groupAlreadyHasPermission", "The group **%group%** already has the permission **%permission%**", Colors.GRAY, Colors.GREEN, Colors.YELLOW
   );
   private static final Phrase GROUP_ADD_PERMISSION = Phrase.create(
      "command.groupAddPermission", "Added permission **%permission%** to **%group%**", Colors.GRAY, Colors.GREEN, Colors.AQUA
   );
   private static final Phrase GROUP_ADD_PERMISSION_BUNGEE = Phrase.create(
      "command.groupAddPermission.bungee",
      "Added permission **%permission%** to **%group%** for **%server%**",
      Colors.GRAY,
      Colors.GREEN,
      Colors.AQUA,
      Colors.YELLOW
   );
   private static final Phrase PLAYER_ALREADY_HAS_PERMISSION = Phrase.create(
      "command.playerAlreadyHasPermission", "The player **%player%** already has the permission **%permission%**", Colors.GRAY, Colors.GREEN, Colors.YELLOW
   );
   private static final Phrase PLAYER_ADD_PERMISSION = Phrase.create(
      "command.playerAddPermission", "Added permission **%permission%** to **%player%**", Colors.GRAY, Colors.GREEN, Colors.AQUA
   );
   private static final Phrase PLAYER_ADD_PERMISSION_BUNGEE = Phrase.create(
      "command.playerAddPermission.bungee",
      "Added permission **%permission%** to **%player%** for **%server%**",
      Colors.GRAY,
      Colors.GREEN,
      Colors.AQUA,
      Colors.YELLOW
   );
   private static final Phrase PLAYER_ALREADY_SUPER_ADMIN = Phrase.create(
      "command.playerAlreadySuperAdmin", "**%player%** is already a superadmin", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase PLAYER_ADD_SUPER_ADMIN = Phrase.create(
      "command.playerAddSuperAdmin", "Successfully added **%player%** as a superadmin", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase REGISTER_USER_ALREADY_EXISTING = Phrase.create(
      "command.registerUser.failed.alreadyExisting", "The UUID **%uuid%** is already registered", Colors.GRAY, Colors.WHITE
   );
   private static final Phrase REGISTER_USER_INVALID_UUID = Phrase.create("command.registerUser.failed.invalidUUID", "Invalid UUID", Colors.GRAY);
   private static final Phrase REGISTER_USER_SUCCESS = Phrase.create(
      "command.registerUser.success", "Successfully registered the user **%playerName%** (**%uuid%**)", Colors.GRAY, Colors.YELLOW, Colors.WHITE
   );
   private static final Phrase PLAYER_REMOVE_GROUP = Phrase.create(
      "command.playerRemoveGroup", "The player **%player%** is not in the group **%group%**", Colors.GRAY, Colors.AQUA, Colors.YELLOW
   );
   private static final Phrase PLAYER_NOT_IN_GROUP = Phrase.create(
      "command.playerNotInGroup", "The player **%player%** is not in the group **%group%**", Colors.GRAY, Colors.AQUA, Colors.YELLOW
   );
   private static final Phrase DELETE_USER_SUCCESSFUL = Phrase.create(
      "command.deleteUser.successful", "Successfully removed the user **%user%**", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase DELETE_USER_UNSUCCESSFUL = Phrase.create(
      "command.deleteUser.unsuccessful", "The user **%user%** needs to be offline", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase REMOVE_PERMISSION_UNSUCCESSFUL = Phrase.create(
      "command.groupRemovePermission.unsuccessful", "Did not find permission **%permission%** for **%target%**", Colors.GRAY, Colors.YELLOW, Colors.AQUA
   );
   private static final Phrase REMOVE_PERMISSION_SUCCESSFUL = Phrase.create(
      "command.groupRemovePermission.successful", "Removed permission **%permission%** from **%target%**", Colors.GRAY, Colors.YELLOW, Colors.AQUA
   );
   private static final Phrase REMOVE_PERMISSION_SUCCESSFUL_MULTI = Phrase.create(
      "command.groupRemovePermission.successfulMultiple",
      "Removed **count** copies of **%permission%** from **%target%**",
      Colors.GRAY,
      Colors.YELLOW,
      Colors.YELLOW,
      Colors.AQUA
   );
   private static final Phrase PLAYER_NOT_SUPER_ADMIN = Phrase.create(
      "command.playerNotSuperAdmin", "**%player%** is not a superadmin", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase PLAYER_REMOVE_SUPER_ADMIN = Phrase.create(
      "command.playerRemoveSuperAdmin", "Successfully removed **%player%** from the superadmins", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase GROUP_RESTRICT_TO_WORLD_ALL_WORLDS = Phrase.create(
      "command.restrictGroupToWorld.allWorlds",
      "Changed settings of **%group%** to allow this Group on **all Worlds**",
      Colors.GRAY,
      Colors.YELLOW,
      Colors.WHITE
   );
   private static final Phrase GROUP_RESTRICT_TO_WORLD_SPECIFIC_WORLD = Phrase.create(
      "command.restrictGroupToWorld.specificWorld",
      "Changed settings of **%group%** to restrict this Group to **%world%**",
      Colors.GRAY,
      Colors.YELLOW,
      Colors.WHITE
   );
   private static final Phrase GROUP_RESTRICT_TO_WORLD_INVALID = Phrase.create("command.restrictGroupToWorld.invalid", "Invalid world", Colors.GRAY);
   private static final Phrase DELETE_GROUP_CONFIRM = Phrase.create(
      "command.deleteGroup.confirm", "Successfully **deleted** group **%group%**", Colors.GRAY, Colors.RED, Colors.YELLOW
   );
   private static final Phrase PURGE_SUPER_ADMINS_CONFIRM = Phrase.create(
      "command.purgeSuperAdmins.confirm", "Successfully **purged** all **Super Admins**", Colors.GRAY, Colors.RED, Colors.GREEN
   );
   private static final Phrase PURGE_GROUP_CONFIRM = Phrase.create(
      "command.purgeGroup.confirm", "Successfully **purged** all Players from **%group%**", Colors.GRAY, Colors.RED, Colors.YELLOW
   );
   private static final Phrase PURGE_GROUPPERMISSION_CONFIRM = Phrase.create(
      "command.purgeGroupPermission.confirm", "Successfully **purged** **%permission%** from all Groups", Colors.GRAY, Colors.RED, Colors.GREEN
   );
   private static final Phrase PURGE_PLAYERPERMISSION_CONFIRM = Phrase.create(
      "command.purgePlayerPermission.confirm", "Successfully **purged** **%permission%** from all Players", Colors.GRAY, Colors.RED, Colors.GREEN
   );
   private static final Phrase PURGE_ALL_PLAYERPERMISSIONS_CONFIRM = Phrase.create(
      "command.purgeAllPlayerPermissions.confirm", "Successfully **purged** all Permissions from **%user%**", Colors.GRAY, Colors.RED, Colors.AQUA
   );
   private static final Phrase PURGE_PLAYER_GROUPS_CONFIRM = Phrase.create(
      "command.purgePlayerGroups.confirm", "Successfully **purged** all groups from **%player%**", Colors.GRAY, Colors.RED, Colors.GREEN
   );
   private static final Phrase PLAYER_CLEARED_GROUPS = Phrase.create(
      "command.playerClearedGroups", "Cleared all groups of **%player%**", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase PLAYER_SET_GROUPS = Phrase.create(
      "command.playerSetGroups", "Set the groups **%groups%** for **%player%**", Colors.GRAY, Colors.YELLOW, Colors.AQUA
   );
   private static final Phrase CHANGED_GROUP_PRIORITY = Phrase.create(
      "command.setGroupPriority", "Changed the priority of **%group%** to **%priority%**", Colors.GRAY, Colors.YELLOW, Colors.Green
   );
   private static final Phrase GROUP_SET_PREFIX_SET = Phrase.create(
      "command.groupSetPrefix.set", "Set the prefix of **%group%** to **%prefix%**", Colors.GRAY, Colors.YELLOW, Colors.WHITE
   );
   private static final Phrase GROUP_SET_PREFIX_CLEAR = Phrase.create(
      "command.groupSetPrefix.clear", "Cleared the prefix of **%group%**", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase GROUP_SET_SUFFIX_SET = Phrase.create(
      "command.groupSetSuffix.set", "Set the suffix of **%group%** to **%suffix%**", Colors.GRAY, Colors.YELLOW, Colors.WHITE
   );
   private static final Phrase GROUP_SET_SUFFIX_CLEAR = Phrase.create(
      "command.groupSetSuffix.clear", "Cleared the suffix of **%group%**", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase PLAYER_SET_PREFIX_CLEAR_ALL = Phrase.create(
      "command.playerSetPrefix.clearAll", "Successfully cleared **%count% prefixes**", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase PLAYER_SET_PREFIX_CLEAR = Phrase.create(
      "command.playerSetPrefix.clear", "Cleared the prefix of **%user%**", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase PLAYER_SET_PREFIX_SET = Phrase.create(
      "command.playerSetPrefix.set", "Set the prefix of **%user%** to **%prefix%**", Colors.GRAY, Colors.AQUA, Colors.WHITE
   );
   private static final Phrase PLAYER_SET_SUFFIX_CLEAR_ALL = Phrase.create(
      "command.playerSetSuffix.clearAll", "Successfully cleared **%count% suffixes**", Colors.GRAY, Colors.YELLOW
   );
   private static final Phrase PLAYER_SET_SUFFIX_CLEAR = Phrase.create(
      "command.playerSetSuffix.clear", "Cleared the suffix of **%user%**", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase PLAYER_SET_SUFFIX_SET = Phrase.create(
      "command.playerSetSuffix.set", "Set the suffix of **%user%** to **%suffix%**", Colors.GRAY, Colors.AQUA, Colors.WHITE
   );
   private static final Phrase TRANSFER_ACCOUNT_SUCCESSFUL_GROUPS = Phrase.create(
      "command.transferAccount.successful.groups",
      "Successfully transferred all **groups** from **%oldTarget%** to **%newTarget%**",
      Colors.GRAY,
      Colors.YELLOW,
      Colors.AQUA,
      Colors.GREEN
   );
   private static final Phrase TRANSFER_ACCOUNT_SUCCESSFUL_PERMISSIONS = Phrase.create(
      "command.transferAccount.successful.permissions",
      "Successfully transferred all **permissions** from **%oldTarget%** to **%newTarget%**",
      Colors.GRAY,
      Colors.YELLOW,
      Colors.AQUA,
      Colors.GREEN
   );
   private static final Phrase TRANSFER_ACCOUNT_SUCCESSFUL_ALL_DATA = Phrase.create(
      "command.transferAccount.successful.allData",
      "Successfully transferred all **groups** and **permissions** from **%oldTarget%** to **%newTarget%**",
      Colors.GRAY,
      Colors.YELLOW,
      Colors.YELLOW,
      Colors.AQUA,
      Colors.GREEN
   );
   private static final Phrase TRANSFER_SOURCES_EQUAL = Phrase.create("command.transfer.sourcesEqual", "From and To should not be equal!", Colors.GRAY);
   private static final Phrase NO_VALID_USER = Phrase.create("command.noValidUser", "Could not find the user **%user%**", Colors.GRAY, Colors.YELLOW);
   private static final Phrase NO_VALID_GROUP = Phrase.create("command.noValidGroup", "Could not find the group **%group%**", Colors.GRAY, Colors.YELLOW);

   public UPCCommand(UltraPermissions ultraPermissions) {
      super(ultraPermissions, "upc", "");
      this.plugin = ultraPermissions;
   }

   @Override
   public void define(CommandSender sender, CommandNode<String, SpecificArgument> commandNode) {
      commandNode.require(new GrantGroupsRequirement(this.plugin));
      this.AddGroupCommand(commandNode);
      this.AddGroupPermission(commandNode);
      this.AddPlayerPermission(commandNode);
      this.AddSuperAdmin(commandNode);
      this.copyGroupCommand(commandNode);
      this.createGroupCommand(commandNode);
      this.DeleteGroup(commandNode);
      this.DeleteUser(commandNode);
      this.ListGroupPermissions(commandNode);
      this.ListPlayerPermissions(commandNode);
      this.PurgeSuperAdmins(commandNode);
      this.PurgeGroup(commandNode);
      this.PurgeGroupPermission(commandNode);
      this.PurgeAllPlayerPermission(commandNode);
      this.PurgePlayerPermission(commandNode);
      this.PurgePlayerGroups(commandNode);
      this.RegisterUser(commandNode);
      this.RemoveGroup(commandNode);
      this.RemoveGroupPermission(commandNode);
      this.RemovePlayerPermission(commandNode);
      this.RemoveSuperAdmin(commandNode);
      this.RestrictGroupToWorld(commandNode);
      this.SetGroups(commandNode);
      this.SetGroupPriority(commandNode);
      this.SetGroupPrefix(commandNode);
      this.SetGroupSuffix(commandNode);
      this.SetPlayerPrefix(commandNode);
      this.SetPlayerSuffix(commandNode);
      this.TransferAccount(commandNode);
      this.Transfer(commandNode);
   }

   private void ListPlayerPermissions(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("listPlayerPermissions"),
         listPlayerPermissionsNode -> listPlayerPermissionsNode.newNode(
            new UserArgument(this.plugin, EmptyReason.NO_MATCH),
            userNode -> userNode.action(
               (sender, player, list) -> {
                  ArgumentValue userValue = userNode.getValue(list);
                  if (userValue.isValid()) {
                     if (!((User)userValue.get()).getPermissions().isEmpty()) {
                        sender.sendMessage(this.plugin.getPrefix() + LIST_PLAYER_PERMISSIONS.get().replace("%user%", ((User)userValue.get()).getName()));
                        ((User)userValue.get())
                           .getPermissions()
                           .stream()
                           .map(Permission::getDisplayName)
                           .sorted(Comparator.naturalOrder())
                           .forEach(text -> sender.sendMessage(Colors.GRAY + "- " + (text.startsWith("-") ? Colors.RED : Colors.GREEN) + text));
                     }
                  }
               }
            )
         )
      );
   }

   private void ListGroupPermissions(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("listGroupPermissions"),
         listGroupPermissionsNode -> listGroupPermissionsNode.newNode(
            new GroupArgument(this.plugin, EmptyReason.NO_MATCH),
            groupNode -> groupNode.action(
               (sender, player, list) -> {
                  ArgumentValue groupValue = groupNode.getValue(list);
                  if (groupValue.isValid()) {
                     if (!((Group)groupValue.get()).getPermissions().isEmpty()) {
                        sender.sendMessage(this.plugin.getPrefix() + LIST_GROUP_PERMISSIONS.get().replace("%group%", ((Group)groupValue.get()).getName()));
                        ((Group)groupValue.get())
                           .getPermissions()
                           .stream()
                           .map(Permission::getDisplayName)
                           .sorted(Comparator.naturalOrder())
                           .forEach(text -> sender.sendMessage(Colors.GRAY + "- " + (text.startsWith("-") ? Colors.RED : Colors.GREEN) + text));
                     }
                  }
               }
            )
         )
      );
   }

   private void createGroupCommand(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("createGroup"),
         createGroupNode -> createGroupNode.newNode(
            new StringArgument(this.plugin, "groupName"),
            groupNameNode -> {
               groupNameNode.require(new SuperadminRequirement(this.plugin));
               if (this.plugin.getThisServer().isPresent()) {
                  groupNameNode.newNode(
                     new ServerArgument(this.plugin),
                     serverNode -> serverNode.action(
                        (sender, player, list) -> {
                           ArgumentValue groupNameValue = groupNameNode.getValue(list);
                           ArgumentValue serverValue = serverNode.getValue(list);
                           if (serverValue.isValid()) {
                              String serverName = serverValue.get() != null ? ((NServer)serverValue.get()).getName() : "All Servers";
                              if (((String)groupNameValue.get()).contains(" ")) {
                                 sender.sendMessage(this.plugin.getPrefix() + CREATE_GROUP_NO_SPACES);
                              } else if (((String)groupNameValue.get()).contains("&")) {
                                 sender.sendMessage(this.plugin.getPrefix() + CREATE_GROUP_NO_COLORS);
                              } else {
                                 boolean isPresent = this.plugin.getGroups().name((String)groupNameValue.get()).isPresent();
                                 if (isPresent) {
                                    sender.sendMessage(this.plugin.getPrefix() + CREATE_GROUP_NAME_TAKEN);
                                 } else {
                                    this.plugin
                                       .newGroup((String)groupNameValue.get())
                                       .setPrefix(Colors.GREEN + (String)groupNameValue.get())
                                       .setServer(serverValue.get() != null ? ((NServer)serverValue.get()).getName() : null)
                                       .create();
                                    sender.sendMessage(
                                       this.plugin.getPrefix()
                                          + CREATE_GROUP_SUCCESS_BUNGEE.get().replace("%groupName%", (CharSequence)groupNameValue.get()).replace("%server%", serverName)
                                    );
                                 }
                              }
                           }
                        }
                     )
                  );
               } else {
                  groupNameNode.require(new SuperadminRequirement(this.plugin));
                  groupNameNode.action((sender, player, list) -> {
                     ArgumentValue groupNameValue = groupNameNode.getValue(list);
                     if (((String)groupNameValue.get()).contains(" ")) {
                        sender.sendMessage(this.plugin.getPrefix() + CREATE_GROUP_NO_SPACES);
                     } else if (((String)groupNameValue.get()).contains("&")) {
                        sender.sendMessage(this.plugin.getPrefix() + CREATE_GROUP_NO_COLORS);
                     } else {
                        boolean isPresent = this.plugin.getGroups().name((String)groupNameValue.get()).isPresent();
                        if (isPresent) {
                           sender.sendMessage(this.plugin.getPrefix() + CREATE_GROUP_NAME_TAKEN);
                        } else {
                           this.plugin.newGroup((String)groupNameValue.get()).setPrefix(Colors.GREEN + (String)groupNameValue.get()).create();
                           sender.sendMessage(this.plugin.getPrefix() + CREATE_GROUP_SUCCESS.get().replace("%groupName%", (CharSequence)groupNameValue.get()));
                        }
                     }
                  });
               }
            }
         )
      );
   }

   private void copyGroupCommand(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("copyGroup"),
         copyGroupNode -> copyGroupNode.newNode(
            new GroupArgument(this.plugin, EmptyReason.INVALID),
            sourceGroupNode -> sourceGroupNode.newNode(
               new StringArgument(this.plugin, "Name of Copy"),
               copyNameNode -> {
                  copyNameNode.require(new SuperadminRequirement(this.plugin));
                  copyNameNode.action(
                     (sender, player, list) -> {
                        ArgumentValue sourceGroupValue = sourceGroupNode.getValue(list);
                        ArgumentValue copyNameValue = copyNameNode.getValue(list);
                        if (this.plugin.getGroups().name((String)copyNameValue.get()).isPresent()) {
                           sender.sendMessage(this.plugin.getPrefix() + COPY_GROUP_UNSUCCESSFUL.get().replace("%group%", (CharSequence)copyNameValue.get()));
                        } else {
                           this.plugin
                              .newGroup((String)copyNameValue.get())
                              .setIcon(((Group)sourceGroupValue.get()).getIcon())
                              .setPrefix(((Group)sourceGroupValue.get()).getPrefix().orElse(null))
                              .setSuffix(((Group)sourceGroupValue.get()).getSuffix().orElse(null))
                              .setServer(((Group)sourceGroupValue.get()).getServer().orElse(null))
                              .setWorld(((Group)sourceGroupValue.get()).getWorld().orElse(null))
                              .create();
                           Optional name = this.plugin.getGroups().name((String)copyNameValue.get());

                           for (Permission permission : ((Group)sourceGroupValue.get()).getPermissions()) {
                              ((Group)name.get())
                                 .newPermission(permission.getName())
                                 .setExpiration(permission.getExpiration())
                                 .setServer(permission.getServer().orElse(null))
                                 .setWorld(permission.getWorld().orElse(null))
                                 .setPositive(permission.isPositive())
                                 .create();
                           }

                           for (Stored inheritedGroup : ((Group)sourceGroupValue.get()).getInheritedGroups()) {
                              ((Group)name.get()).getInheritedGroups().add(inheritedGroup);
                           }

                           sender.sendMessage(
                              this.plugin.getPrefix()
                                 + COPY_GROUP_SUCCESSFUL.get()
                                    .replace("%group%", ((Group)sourceGroupValue.get()).getName())
                                    .replace("%copyGroup%", ((Group)name.get()).getName())
                           );
                        }
                     }
                  );
               }
            )
         )
      );
   }

   private void AddGroupCommand(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("AddGroup"),
         addGroupNode -> addGroupNode.newNode(
            new UserArgument(this.plugin, EmptyReason.INVALID),
            userNode -> userNode.newNode(
               new GroupArgument(this.plugin, EmptyReason.INVALID),
               groupNode -> groupNode.action(
                  (sender, player, list) -> {
                     ArgumentValue userValue = userNode.getValue(list);
                     ArgumentValue groupValue = groupNode.getValue(list);
                     boolean canGrantGroup = false;
                     boolean canGrantDuration = false;
                     long timeSecondsFromString = Tools.getTimeSecondsFromString(String.join(" ", list));
                     long value = timeSecondsFromString == 0L ? 0L : System.currentTimeMillis() + 1000L * timeSecondsFromString;
                     Optional optional = null;
                     if (player != null) {
                        optional = this.plugin.getUsers().uuid(player.getUniqueId());
                     }

                     if (this.isValidUser(sender, userValue) && this.isValidGroup(sender, groupValue)) {
                        if (player != null && optional.isPresent()) {
                           canGrantGroup = ((User)optional.get())
                              .getGroups()
                              .stream()
                              .anyMatch(
                                 stored -> stored.isPresent() && stored.get().get().getGrantGroups().containsKey(((Group)groupValue.get()).toStored())
                              );
                           canGrantDuration = ((User)optional.get())
                              .getGroups()
                              .stream()
                              .anyMatch(
                                 stored -> stored.isPresent() && stored.get().get().getGrantGroups().containsKey(((Group)groupValue.get()).toStored())
                                    ? stored.get()
                                       .get()
                                       .getGrantGroups()
                                       .entrySet()
                                       .stream()
                                       .anyMatch(
                                          entry -> entry.getKey().equals(((Group)groupValue.get()).toStored())
                                             && (entry.getValue() >= timeSecondsFromString && timeSecondsFromString != 0L || entry.getValue() == 0L)
                                       )
                                    : false
                              );
                        }

                        if (!canGrantDuration && player != null) {
                           optional = this.plugin.getUsers().uuid(player.getUniqueId());
                           if (optional.isPresent() && !((User)optional.get()).isSuperadmin()) {
                              player.sendMessage(
                                 this.plugin.getPrefix()
                                    + PLAYER_ADD_GROUP_NOT_TIME_ALLOWED.get()
                                       .replace("%group%", ((Group)groupValue.get()).getName())
                                       .replace("%duration%", value != 0L ? Tools.getTimeString(timeSecondsFromString) : PLAYER_ADD_GROUP_WORD_PERMANENT.get())
                              );
                              return;
                           }
                        }

                        if (canGrantGroup && canGrantDuration || player == null || optional.isPresent() && ((User)optional.get()).isSuperadmin()) {
                           boolean hadGroup = false;
                           String groupName = ((Group)groupValue.get()).getName();
                           String userName = ((User)userValue.get()).getName();
                           String timeText = value != 0L ? Tools.getTimeString(timeSecondsFromString) : "none";
                           String operator = "";
                           if (list.contains("+")) {
                              operator = "+";
                           }

                           if (list.contains("-")) {
                              operator = "-";
                           }

                           Phrase phrase = value != 0L ? PLAYER_ADD_GROUP_TIMED : PLAYER_ADD_GROUP;
                           sender.sendMessage(
                              this.plugin.getPrefix()
                                 + phrase.get().replace("%group%", groupName).replace("%player%", userName).replace("%time%", timeText).replace("%operator%", operator)
                           );
                           if (((User)userValue.get()).getActiveGroups().name(groupName).isPresent()) {
                              hadGroup = true;
                           }

                           if (list.contains("+") && hadGroup) {
                              long groupExpiry = ((User)userValue.get()).getGroupExpiry(((Group)groupValue.get()).toStored());
                              ((User)userValue.get()).addGroup((Group)groupValue.get(), groupExpiry + 1000L * timeSecondsFromString);
                           } else if (list.contains("-") && hadGroup) {
                              long groupExpiry = ((User)userValue.get()).getGroupExpiry(((Group)groupValue.get()).toStored());
                              ((User)userValue.get()).addGroup((Group)groupValue.get(), groupExpiry - 1000L * timeSecondsFromString);
                           } else {
                              ((User)userValue.get()).addGroup((Group)groupValue.get(), value);
                           }
                        }
                     }
                  }
               )
            )
         )
      );
   }

   private void AddGroupPermission(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("AddGroupPermission"),
         addGroupPermissionNode -> addGroupPermissionNode.newNode(
            new GroupArgument(this.plugin, EmptyReason.INVALID),
            groupNode -> groupNode.newNode(
               new PermissionArgument(this.plugin),
               permissionNode -> {
                  if (this.plugin.getThisServer().isPresent()) {
                     permissionNode.newNode(
                        new ServerArgument(this.plugin),
                        serverNode -> {
                           serverNode.newNode(
                              new WorldArgument(this.plugin, EmptyReason.INVALID),
                              worldNode -> {
                                 worldNode.require(new SuperadminRequirement(this.plugin));
                                 worldNode.action(
                                    (sender, player, list) -> {
                                       ArgumentValue groupValue = groupNode.getValue(list);
                                       ArgumentValue permissionValue = permissionNode.getValue(list);
                                       ArgumentValue serverValue = serverNode.getValue(list);
                                       ArgumentValue worldValue = worldNode.getValue(list);
                                       if (serverValue.isValid()) {
                                          String serverName = serverValue.get() != null ? ((NServer)serverValue.get()).getName() : "All Servers";
                                          if (this.isValidGroup(sender, groupValue)) {
                                             CommandOptions commandOptions = new CommandOptions(this.plugin, permissionNode.getSpareArguments(list));
                                             boolean flag = ((Group)groupValue.get())
                                                .getPermissions()
                                                .name((String)permissionValue.get())
                                                .servers(commandOptions.getServer() == null, commandOptions.getServer())
                                                .worlds(worldValue.get() == null, commandOptions.getWorld())
                                                .stream()
                                                .anyMatch(permission -> permission.getExpiration() == commandOptions.getExpiration());
                                             if (flag) {
                                                String message = GROUP_ALREADY_HAS_PERMISSION.get()
                                                   .replace("%group%", ((Group)groupValue.get()).getName())
                                                   .replace("%permission%", (CharSequence)permissionValue.get());
                                                sender.sendMessage(this.plugin.getPrefix() + message);
                                                return;
                                             }

                                             ((Group)groupValue.get())
                                                .newPermission((String)permissionValue.get())
                                                .setServer(serverValue.get() != null ? ((NServer)serverValue.get()).getName() : null)
                                                .setWorld(commandOptions.getWorld())
                                                .setExpiration(commandOptions.getExpiration())
                                                .create();
                                             String message = GROUP_ADD_PERMISSION_BUNGEE.get()
                                                .replace("%permission%", (CharSequence)permissionValue.get())
                                                .replace("%group%", ((Group)groupValue.get()).getName())
                                                .replace("%server%", serverName);
                                             sender.sendMessage(this.plugin.getPrefix() + message + commandOptions.getCombinedInfo());
                                          }
                                       }
                                    }
                                 );
                              }
                           );
                           serverNode.require(new SuperadminRequirement(this.plugin));
                           serverNode.action(
                              (sender, player, list) -> {
                                 ArgumentValue groupValue = groupNode.getValue(list);
                                 ArgumentValue permissionValue = permissionNode.getValue(list);
                                 ArgumentValue serverValue = serverNode.getValue(list);
                                 if (serverValue.isValid()) {
                                    String serverName = serverValue.get() != null ? ((NServer)serverValue.get()).getName() : "All Servers";
                                    if (this.isValidGroup(sender, groupValue)) {
                                       CommandOptions commandOptions = new CommandOptions(this.plugin, permissionNode.getSpareArguments(list));
                                       boolean flag = ((Group)groupValue.get())
                                          .getPermissions()
                                          .name((String)permissionValue.get())
                                          .servers(commandOptions.getServer() == null, commandOptions.getServer())
                                          .worlds(true)
                                          .stream()
                                          .anyMatch(permission -> permission.getExpiration() == commandOptions.getExpiration());
                                       if (flag) {
                                          String message = GROUP_ALREADY_HAS_PERMISSION.get()
                                             .replace("%group%", ((Group)groupValue.get()).getName())
                                             .replace("%permission%", (CharSequence)permissionValue.get());
                                          sender.sendMessage(this.plugin.getPrefix() + message);
                                          return;
                                       }

                                       ((Group)groupValue.get())
                                          .newPermission((String)permissionValue.get())
                                          .setServer(serverValue.get() != null ? ((NServer)serverValue.get()).getName() : null)
                                          .setWorld(commandOptions.getWorld())
                                          .setExpiration(commandOptions.getExpiration())
                                          .create();
                                       String message = GROUP_ADD_PERMISSION_BUNGEE.get()
                                          .replace("%permission%", (CharSequence)permissionValue.get())
                                          .replace("%group%", ((Group)groupValue.get()).getName())
                                          .replace("%server%", serverName);
                                       sender.sendMessage(this.plugin.getPrefix() + message + commandOptions.getCombinedInfo());
                                    }
                                 }
                              }
                           );
                        }
                     );
                  } else {
                     permissionNode.newNode(
                        new WorldArgument(this.plugin, EmptyReason.INVALID),
                        worldNode -> {
                           worldNode.require(new SuperadminRequirement(this.plugin));
                           worldNode.action(
                              (sender, player, list) -> {
                                 ArgumentValue groupValue = groupNode.getValue(list);
                                 ArgumentValue permissionValue = permissionNode.getValue(list);
                                 ArgumentValue worldValue = worldNode.getValue(list);
                                 if (this.isValidGroup(sender, groupValue)) {
                                    CommandOptions commandOptions = new CommandOptions(this.plugin, permissionNode.getSpareArguments(list));
                                    boolean flag = ((Group)groupValue.get())
                                       .getPermissions()
                                       .name((String)permissionValue.get())
                                       .servers(commandOptions.getServer() == null, commandOptions.getServer())
                                       .worlds(worldValue.get() == null, commandOptions.getWorld())
                                       .stream()
                                       .anyMatch(permission -> permission.getExpiration() == commandOptions.getExpiration());
                                    if (flag) {
                                       String text = GROUP_ALREADY_HAS_PERMISSION.get()
                                          .replace("%group%", ((Group)groupValue.get()).getName())
                                          .replace("%permission%", (CharSequence)permissionValue.get());
                                       sender.sendMessage(this.plugin.getPrefix() + text);
                                       return;
                                    }

                                    ((Group)groupValue.get())
                                       .newPermission((String)permissionValue.get())
                                       .setServer(commandOptions.getServer())
                                       .setWorld(commandOptions.getWorld())
                                       .setExpiration(commandOptions.getExpiration())
                                       .create();
                                    String text = GROUP_ADD_PERMISSION.get()
                                       .replace("%permission%", (CharSequence)permissionValue.get())
                                       .replace("%group%", ((Group)groupValue.get()).getName());
                                    sender.sendMessage(this.plugin.getPrefix() + text + commandOptions.getCombinedInfo());
                                 }
                              }
                           );
                        }
                     );
                     permissionNode.require(new SuperadminRequirement(this.plugin));
                     permissionNode.action(
                        (sender, player, list) -> {
                           ArgumentValue groupValue = groupNode.getValue(list);
                           ArgumentValue permissionValue = permissionNode.getValue(list);
                           if (this.isValidGroup(sender, groupValue)) {
                              CommandOptions commandOptions = new CommandOptions(this.plugin, permissionNode.getSpareArguments(list));
                              boolean flag = ((Group)groupValue.get())
                                 .getPermissions()
                                 .name((String)permissionValue.get())
                                 .servers(commandOptions.getServer() == null, commandOptions.getServer())
                                 .worlds(true)
                                 .stream()
                                 .anyMatch(permission -> permission.getExpiration() == commandOptions.getExpiration());
                              if (flag) {
                                 String text = GROUP_ALREADY_HAS_PERMISSION.get()
                                    .replace("%group%", ((Group)groupValue.get()).getName())
                                    .replace("%permission%", (CharSequence)permissionValue.get());
                                 sender.sendMessage(this.plugin.getPrefix() + text);
                                 return;
                              }

                              ((Group)groupValue.get())
                                 .newPermission((String)permissionValue.get())
                                 .setServer(commandOptions.getServer())
                                 .setWorld(commandOptions.getWorld())
                                 .setExpiration(commandOptions.getExpiration())
                                 .create();
                              String text = GROUP_ADD_PERMISSION.get()
                                 .replace("%permission%", (CharSequence)permissionValue.get())
                                 .replace("%group%", ((Group)groupValue.get()).getName());
                              sender.sendMessage(this.plugin.getPrefix() + text + commandOptions.getCombinedInfo());
                           }
                        }
                     );
                  }
               }
            )
         )
      );
   }

   private void AddPlayerPermission(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("AddPlayerPermission"),
         addPlayerPermissionNode -> addPlayerPermissionNode.newNode(
            new UserArgument(this.plugin, EmptyReason.INVALID),
            userNode -> userNode.newNode(
               new PermissionArgument(this.plugin),
               permissionNode -> {
                  if (this.plugin.getThisServer().isPresent()) {
                     permissionNode.newNode(
                        new ServerArgument(this.plugin),
                        serverNode -> serverNode.newNode(
                           new WorldArgument(this.plugin, EmptyReason.INVALID),
                           worldNode -> {
                              worldNode.require(new SuperadminRequirement(this.plugin));
                              worldNode.action(
                                 (sender, player, list) -> {
                                    ArgumentValue userValue = userNode.getValue(list);
                                    ArgumentValue permissionValue = permissionNode.getValue(list);
                                    ArgumentValue serverValue = serverNode.getValue(list);
                                    ArgumentValue worldValue = worldNode.getValue(list);
                                    if (serverValue.isValid()) {
                                       String serverName = serverValue.get() != null ? ((NServer)serverValue.get()).getName() : "All Servers";
                                       if (this.isValidUser(sender, userValue)) {
                                          CommandOptions commandOptions = new CommandOptions(this.plugin, serverNode.getSpareArguments(list));
                                          long expiration = commandOptions.getExpiration();
                                          commandOptions.setOperator(list.contains("+") ? "+" : (list.contains("-") ? "-" : ""));
                                          Optional first = ((User)userValue.get())
                                             .getPermissions()
                                             .name((String)permissionValue.get())
                                             .servers(commandOptions.getServer() == null, commandOptions.getServer())
                                             .worlds(worldValue.get() == null, commandOptions.getWorld())
                                             .stream()
                                             .findFirst();
                                          if (commandOptions.getOperator().equalsIgnoreCase("+") && first.isPresent()) {
                                             long currentExpiration = ((Permission)first.get()).getExpiration();
                                             expiration = currentExpiration + commandOptions.getSecs() * 1000L;
                                             ((Permission)first.get()).remove();
                                          } else if (commandOptions.getOperator().equalsIgnoreCase("-") && first.isPresent()) {
                                             long currentExpiration = ((Permission)first.get()).getExpiration();
                                             expiration = currentExpiration - commandOptions.getSecs() * 1000L;
                                             ((Permission)first.get()).remove();
                                          } else if (first.isPresent()) {
                                             String message = PLAYER_ALREADY_HAS_PERMISSION.get()
                                                .replace("%player%", ((User)userValue.get()).getName())
                                                .replace("%permission%", (CharSequence)permissionValue.get());
                                             sender.sendMessage(this.plugin.getPrefix() + message);
                                             return;
                                          }

                                          ((User)userValue.get())
                                             .newPermission((String)permissionValue.get())
                                             .setServer(serverValue.get() != null ? ((NServer)serverValue.get()).getName() : null)
                                             .setWorld(commandOptions.getWorld())
                                             .setExpiration(expiration)
                                             .create();
                                          String message = PLAYER_ADD_PERMISSION_BUNGEE.get()
                                             .replace("%permission%", (CharSequence)permissionValue.get())
                                             .replace("%player%", ((User)userValue.get()).getName())
                                             .replace("%server%", serverName);
                                          sender.sendMessage(this.plugin.getPrefix() + message + commandOptions.getCombinedInfo());
                                       }
                                    }
                                 }
                              );
                              serverNode.require(new SuperadminRequirement(this.plugin));
                              serverNode.action(
                                 (sender, player, list) -> {
                                    ArgumentValue userValue = userNode.getValue(list);
                                    ArgumentValue permissionValue = permissionNode.getValue(list);
                                    ArgumentValue serverValue = serverNode.getValue(list);
                                    if (serverValue.isValid()) {
                                       String serverName = serverValue.get() != null ? ((NServer)serverValue.get()).getName() : "All Servers";
                                       if (this.isValidUser(sender, userValue)) {
                                          CommandOptions commandOptions = new CommandOptions(this.plugin, serverNode.getSpareArguments(list));
                                          long expiration = commandOptions.getExpiration();
                                          commandOptions.setOperator(list.contains("+") ? "+" : (list.contains("-") ? "-" : ""));
                                          Optional first = ((User)userValue.get())
                                             .getPermissions()
                                             .name((String)permissionValue.get())
                                             .worlds(true)
                                             .servers(commandOptions.getServer() == null, commandOptions.getServer())
                                             .stream()
                                             .findFirst();
                                          if (commandOptions.getOperator().equalsIgnoreCase("+") && first.isPresent()) {
                                             long currentExpiration = ((Permission)first.get()).getExpiration();
                                             expiration = currentExpiration + commandOptions.getSecs() * 1000L;
                                             ((Permission)first.get()).remove();
                                          } else if (commandOptions.getOperator().equalsIgnoreCase("-") && first.isPresent()) {
                                             long currentExpiration = ((Permission)first.get()).getExpiration();
                                             expiration = currentExpiration - commandOptions.getSecs() * 1000L;
                                             ((Permission)first.get()).remove();
                                          } else if (first.isPresent()) {
                                             String message = PLAYER_ALREADY_HAS_PERMISSION.get()
                                                .replace("%player%", ((User)userValue.get()).getName())
                                                .replace("%permission%", (CharSequence)permissionValue.get());
                                             sender.sendMessage(this.plugin.getPrefix() + message);
                                             return;
                                          }

                                          ((User)userValue.get())
                                             .newPermission((String)permissionValue.get())
                                             .setServer(serverValue.get() != null ? ((NServer)serverValue.get()).getName() : null)
                                             .setExpiration(expiration)
                                             .create();
                                          String message = PLAYER_ADD_PERMISSION_BUNGEE.get()
                                             .replace("%permission%", (CharSequence)permissionValue.get())
                                             .replace("%player%", ((User)userValue.get()).getName())
                                             .replace("%server%", serverName);
                                          sender.sendMessage(this.plugin.getPrefix() + message + commandOptions.getCombinedInfo());
                                       }
                                    }
                                 }
                              );
                           }
                        )
                     );
                  } else {
                     permissionNode.newNode(
                        new WorldArgument(this.plugin, EmptyReason.INVALID),
                        worldNode -> {
                           worldNode.require(new SuperadminRequirement(this.plugin));
                           worldNode.action(
                              (sender, player, list) -> {
                                 ArgumentValue userValue = userNode.getValue(list);
                                 ArgumentValue permissionValue = permissionNode.getValue(list);
                                 ArgumentValue worldValue = worldNode.getValue(list);
                                 if (this.isValidUser(sender, userValue)) {
                                    CommandOptions commandOptions = new CommandOptions(this.plugin, permissionNode.getSpareArguments(list));
                                    long expiration = commandOptions.getExpiration();
                                    commandOptions.setOperator(list.contains("+") ? "+" : (list.contains("-") ? "-" : ""));
                                    Optional first = ((User)userValue.get())
                                       .getPermissions()
                                       .name((String)permissionValue.get())
                                       .servers(commandOptions.getServer() == null, commandOptions.getServer())
                                       .worlds(worldValue.get() == null, commandOptions.getWorld())
                                       .stream()
                                       .findFirst();
                                    if (commandOptions.getOperator().equalsIgnoreCase("+") && first.isPresent()) {
                                       long currentExpiration = ((Permission)first.get()).getExpiration();
                                       expiration = currentExpiration + commandOptions.getSecs() * 1000L;
                                       ((Permission)first.get()).remove();
                                    } else if (commandOptions.getOperator().equalsIgnoreCase("-") && first.isPresent()) {
                                       long currentExpiration = ((Permission)first.get()).getExpiration();
                                       expiration = currentExpiration - commandOptions.getSecs() * 1000L;
                                       ((Permission)first.get()).remove();
                                    } else if (first.isPresent()) {
                                       String text = PLAYER_ALREADY_HAS_PERMISSION.get()
                                          .replace("%player%", ((User)userValue.get()).getName())
                                          .replace("%permission%", (CharSequence)permissionValue.get());
                                       sender.sendMessage(this.plugin.getPrefix() + text);
                                       return;
                                    }

                                    ((User)userValue.get())
                                       .newPermission((String)permissionValue.get())
                                       .setServer(commandOptions.getServer())
                                       .setWorld(commandOptions.getWorld())
                                       .setExpiration(expiration)
                                       .create();
                                    String text = PLAYER_ADD_PERMISSION.get()
                                       .replace("%permission%", (CharSequence)permissionValue.get())
                                       .replace("%player%", ((User)userValue.get()).getName());
                                    sender.sendMessage(this.plugin.getPrefix() + text + commandOptions.getCombinedInfo());
                                 }
                              }
                           );
                        }
                     );
                     permissionNode.require(new SuperadminRequirement(this.plugin));
                     permissionNode.action(
                        (sender, player, list) -> {
                           ArgumentValue userValue = userNode.getValue(list);
                           ArgumentValue permissionValue = permissionNode.getValue(list);
                           if (this.isValidUser(sender, userValue)) {
                              CommandOptions commandOptions = new CommandOptions(this.plugin, permissionNode.getSpareArguments(list));
                              long expiration = commandOptions.getExpiration();
                              commandOptions.setOperator(list.contains("+") ? "+" : (list.contains("-") ? "-" : ""));
                              Optional first = ((User)userValue.get())
                                 .getPermissions()
                                 .name((String)permissionValue.get())
                                 .servers(commandOptions.getServer() == null, commandOptions.getServer())
                                 .stream()
                                 .findFirst();
                              if (commandOptions.getOperator().equalsIgnoreCase("+") && first.isPresent()) {
                                 long currentExpiration = ((Permission)first.get()).getExpiration();
                                 expiration = currentExpiration + commandOptions.getSecs() * 1000L;
                                 ((Permission)first.get()).remove();
                              } else if (commandOptions.getOperator().equalsIgnoreCase("-") && first.isPresent()) {
                                 long currentExpiration = ((Permission)first.get()).getExpiration();
                                 expiration = currentExpiration - commandOptions.getSecs() * 1000L;
                                 ((Permission)first.get()).remove();
                              } else if (first.isPresent()) {
                                 String text = PLAYER_ALREADY_HAS_PERMISSION.get()
                                    .replace("%player%", ((User)userValue.get()).getName())
                                    .replace("%permission%", (CharSequence)permissionValue.get());
                                 sender.sendMessage(this.plugin.getPrefix() + text);
                                 return;
                              }

                              ((User)userValue.get()).newPermission((String)permissionValue.get()).setServer(commandOptions.getServer()).setExpiration(expiration).create();
                              String text = PLAYER_ADD_PERMISSION.get()
                                 .replace("%permission%", (CharSequence)permissionValue.get())
                                 .replace("%player%", ((User)userValue.get()).getName());
                              sender.sendMessage(this.plugin.getPrefix() + text + commandOptions.getCombinedInfo());
                           }
                        }
                     );
                  }
               }
            )
         )
      );
   }

   private void AddSuperAdmin(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(new SpecificArgument("AddSuperAdmin"), addSuperAdminNode -> addSuperAdminNode.newNode(new UserArgument(this.plugin, EmptyReason.INVALID), userNode -> {
         userNode.require(new SuperadminRequirement(this.plugin));
         userNode.action((sender, player, list) -> {
            ArgumentValue userValue = userNode.getValue(list);
            if (this.isValidUser(sender, userValue)) {
               if (((User)userValue.get()).isSuperadmin()) {
                  sender.sendMessage(this.plugin.getPrefix() + PLAYER_ALREADY_SUPER_ADMIN.get().replace("%player%", ((User)userValue.get()).getName()));
               } else {
                  ((User)userValue.get()).setSuperadmin(true);
                  sender.sendMessage(this.plugin.getPrefix() + PLAYER_ADD_SUPER_ADMIN.get().replace("%player%", ((User)userValue.get()).getName()));
               }
            }
         });
      }));
   }

   private void RegisterUser(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("RegisterUser"),
         registerUserNode -> registerUserNode.newNode(
            new AnyArgument("<UUID>"),
            uuidNode -> uuidNode.newNode(
               new AnyArgument("<PlayerName>"),
               playerNameNode -> playerNameNode.newNode(
                  new BooleanArgument(this.plugin, EmptyReason.INVALID),
                  assignDefaultNode -> assignDefaultNode.action(
                     (sender, player, list) -> {
                        ArgumentValue uuidValue = uuidNode.getValue(list);
                        ArgumentValue playerNameValue = playerNameNode.getValue(list);
                        ArgumentValue assignDefaultValue = assignDefaultNode.getValue(list);

                        try {
                           UUID uuid = UUID.fromString((String)uuidValue.get());
                           boolean isPresent = this.plugin.getUsers().uuid(uuid).isPresent();
                           if (isPresent) {
                              player.sendMessage(this.plugin.getPrefix() + REGISTER_USER_ALREADY_EXISTING.get().replace("%uuid%", uuid.toString()));
                           } else {
                              this.plugin.registerUser(uuid, playerNameValue.get() == null ? "NOT DEFINED" : (String)playerNameValue.get(), (Boolean)assignDefaultValue.get());
                              player.sendMessage(
                                 this.plugin.getPrefix()
                                    + REGISTER_USER_SUCCESS.get().replace("%playerName%", (CharSequence)playerNameValue.get()).replace("%uuid%", uuid.toString())
                              );
                           }
                        } catch (IllegalArgumentException exception) {
                           player.sendMessage(this.plugin.getPrefix() + REGISTER_USER_INVALID_UUID.get());
                        }
                     }
                  )
               )
            )
         )
      );
   }

   private void RemoveGroup(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("RemoveGroup"),
         removeGroupNode -> removeGroupNode.newNode(
            new UserArgument(this.plugin, EmptyReason.INVALID), userNode -> userNode.newNode(new GroupArgument(this.plugin, EmptyReason.INVALID), groupNode -> {
               groupNode.require(new SuperadminRequirement(this.plugin));
               groupNode.action((sender, player, list) -> {
                  ArgumentValue userValue = userNode.getValue(list);
                  ArgumentValue groupValue = groupNode.getValue(list);
                  if (this.isValidUser(sender, userValue) && this.isValidGroup(sender, groupValue)) {
                     Phrase phrase;
                     if (((User)userValue.get()).getGroups().contains(((Group)groupValue.get()).toStored())) {
                        ((User)userValue.get()).removeGroup((Group)groupValue.get());
                        phrase = PLAYER_REMOVE_GROUP;
                     } else {
                        phrase = PLAYER_NOT_IN_GROUP;
                     }

                     String text = phrase.get().replace("%player%", ((User)userValue.get()).getName()).replace("%group%", ((Group)groupValue.get()).getName());
                     sender.sendMessage(this.plugin.getPrefix() + text);
                  }
               });
            })
         )
      );
   }

   private void DeleteUser(CommandNode<String, SpecificArgument> commandNode) {
      this.cachedOnlineChecker = CachedOnlineChecker.scan();
      commandNode.newNode(new SpecificArgument("deleteUser"), deleteUserNode -> deleteUserNode.newNode(new UserArgument(this.plugin, EmptyReason.INVALID), userNode -> {
         userNode.require(new SuperadminRequirement(this.plugin));
         userNode.action((sender, player, list) -> {
            ArgumentValue userValue = userNode.getValue(list);
            if (this.cachedOnlineChecker.isOnline(((User)userValue.get()).getUuid())) {
               sender.sendMessage(this.plugin.getPrefix() + DELETE_USER_UNSUCCESSFUL.get().replace("%user%", ((User)userValue.get()).getName()));
            }

            if (!this.cachedOnlineChecker.isOnline(((User)userValue.get()).getUuid())) {
               ((User)userValue.get()).remove();
               sender.sendMessage(this.plugin.getPrefix() + DELETE_USER_SUCCESSFUL.get().replace("%user%", ((User)userValue.get()).getName()));
            }
         });
      }));
   }

   private void RemoveGroupPermission(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("RemoveGroupPermission"),
         removeGroupPermissionNode -> removeGroupPermissionNode.newNode(
            new GroupArgument(this.plugin, EmptyReason.INVALID),
            groupNode -> groupNode.newNode(
               new PermissionArgument(this.plugin),
               permissionNode -> {
                  permissionNode.require(new SuperadminRequirement(this.plugin));
                  permissionNode.action(
                     (sender, player, list) -> {
                        ArgumentValue groupValue = groupNode.getValue(list);
                        ArgumentValue permissionValue = permissionNode.getValue(list);
                        if (this.isValidGroup(sender, groupValue)) {
                           PermissionList name = ((Group)groupValue.get()).getPermissions().name((String)permissionValue.get());
                           name.forEach(Permission::remove);
                           if (name.size() == 0) {
                              String text = REMOVE_PERMISSION_UNSUCCESSFUL.get()
                                 .replace("%permission%", (CharSequence)permissionValue.get())
                                 .replace("%target%", ((Group)groupValue.get()).getName());
                              sender.sendMessage(this.plugin.getPrefix() + text);
                              return;
                           }

                           if (name.size() == 1) {
                              String text = REMOVE_PERMISSION_SUCCESSFUL.get()
                                 .replace("%permission%", (CharSequence)permissionValue.get())
                                 .replace("%target%", ((Group)groupValue.get()).getName());
                              sender.sendMessage(this.plugin.getPrefix() + text);
                              return;
                           }

                           String text = REMOVE_PERMISSION_SUCCESSFUL_MULTI.get()
                              .replace("%permission%", (CharSequence)permissionValue.get())
                              .replace("%target%", ((Group)groupValue.get()).getName())
                              .replace("%count%", Integer.toString(name.size()));
                           sender.sendMessage(this.plugin.getPrefix() + text);
                        }
                     }
                  );
               }
            )
         )
      );
   }

   private void RemovePlayerPermission(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("RemovePlayerPermission"),
         removePlayerPermissionNode -> removePlayerPermissionNode.newNode(
            new UserArgument(this.plugin, EmptyReason.INVALID),
            userNode -> userNode.newNode(
               new PermissionArgument(this.plugin),
               permissionNode -> {
                  permissionNode.require(new SuperadminRequirement(this.plugin));
                  permissionNode.action(
                     (sender, player, list) -> {
                        ArgumentValue userValue = userNode.getValue(list);
                        ArgumentValue permissionValue = permissionNode.getValue(list);
                        if (this.isValidUser(sender, userValue)) {
                           PermissionList name = ((User)userValue.get()).getPermissions().name((String)permissionValue.get());
                           name.forEach(Permission::remove);
                           if (name.size() == 0) {
                              String text = REMOVE_PERMISSION_UNSUCCESSFUL.get()
                                 .replace("%permission%", (CharSequence)permissionValue.get())
                                 .replace("%target%", ((User)userValue.get()).getName());
                              sender.sendMessage(this.plugin.getPrefix() + text);
                              return;
                           }

                           if (name.size() == 1) {
                              String text = REMOVE_PERMISSION_SUCCESSFUL.get()
                                 .replace("%permission%", (CharSequence)permissionValue.get())
                                 .replace("%target%", ((User)userValue.get()).getName());
                              sender.sendMessage(this.plugin.getPrefix() + text);
                              return;
                           }

                           String text = REMOVE_PERMISSION_SUCCESSFUL_MULTI.get()
                              .replace("%permission%", (CharSequence)permissionValue.get())
                              .replace("%target%", ((User)userValue.get()).getName())
                              .replace("%count%", Integer.toString(name.size()));
                           sender.sendMessage(this.plugin.getPrefix() + text);
                        }
                     }
                  );
               }
            )
         )
      );
   }

   private void RemoveSuperAdmin(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(new SpecificArgument("RemoveSuperAdmin"), removeSuperAdminNode -> removeSuperAdminNode.newNode(new UserArgument(this.plugin, EmptyReason.INVALID), userNode -> {
         userNode.require(new SuperadminRequirement(this.plugin));
         userNode.action((sender, player, list) -> {
            ArgumentValue userValue = userNode.getValue(list);
            if (this.isValidUser(sender, userValue)) {
               if (((User)userValue.get()).isSuperadmin()) {
                  ((User)userValue.get()).setSuperadmin(false);
                  sender.sendMessage(this.plugin.getPrefix() + PLAYER_REMOVE_SUPER_ADMIN.get().replace("%player%", ((User)userValue.get()).getName()));
               } else {
                  sender.sendMessage(this.plugin.getPrefix() + PLAYER_NOT_SUPER_ADMIN.get().replace("%player%", ((User)userValue.get()).getName()));
               }
            }
         });
      }));
   }

   private void RestrictGroupToWorld(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("RestrictGroupToWorld"),
         restrictGroupToWorldNode -> restrictGroupToWorldNode.newNode(
            new GroupArgument(this.plugin, EmptyReason.INVALID),
            groupNode -> groupNode.newNode(
               new WorldArgument(this.plugin, EmptyReason.INVALID),
               worldNode -> {
                  worldNode.require(new SuperadminRequirement(this.plugin));
                  worldNode.action(
                     (sender, player, list) -> {
                        ArgumentValue groupValue = groupNode.getValue(list);
                        ArgumentValue worldValue = worldNode.getValue(list);
                        if (worldValue.get() == null) {
                           ((Group)groupValue.get()).setWorld(null);
                           player.sendMessage(
                              this.plugin.getPrefix() + GROUP_RESTRICT_TO_WORLD_ALL_WORLDS.get().replace("%group%", ((Group)groupValue.get()).getName())
                           );
                        } else if (!(worldValue.get() instanceof World)) {
                           player.sendMessage(this.plugin.getPrefix() + GROUP_RESTRICT_TO_WORLD_INVALID.get());
                        } else {
                           ((Group)groupValue.get()).setWorld(((World)worldValue.get()).getName());
                           player.sendMessage(
                              this.plugin.getPrefix()
                                 + GROUP_RESTRICT_TO_WORLD_SPECIFIC_WORLD.get()
                                    .replace("%group%", ((Group)groupValue.get()).getName())
                                    .replace("%world%", ((World)worldValue.get()).getName())
                           );
                        }
                     }
                  );
               }
            )
         )
      );
   }

   private void DeleteGroup(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(new SpecificArgument("DeleteGroup"), deleteGroupNode -> deleteGroupNode.newNode(new GroupArgument(this.plugin, EmptyReason.INVALID), groupNode -> {
         groupNode.require(new SuperadminRequirement(this.plugin));
         groupNode.action((sender, player, list) -> {
            ArgumentValue groupValue = groupNode.getValue(list);
            ((Group)groupValue.get()).remove();
            player.sendMessage(this.plugin.getPrefix() + DELETE_GROUP_CONFIRM.get());
         });
      }));
   }

   private void PurgeSuperAdmins(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(new SpecificArgument("PurgeSuperAdmins"), purgeSuperAdminsNode -> {
         purgeSuperAdminsNode.require(new SuperadminRequirement(this.plugin));
         purgeSuperAdminsNode.action((sender, player, list) -> {
            for (User user : this.plugin.getUsers()) {
               user.setSuperadmin(false);
            }

            sender.sendMessage(this.plugin.getPrefix() + PURGE_SUPER_ADMINS_CONFIRM.get());
         });
      });
   }

   private void PurgeGroup(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(new SpecificArgument("PurgeGroup"), purgeGroupNode -> purgeGroupNode.newNode(new GroupArgument(this.plugin, EmptyReason.INVALID), groupNode -> {
         groupNode.require(new SuperadminRequirement(this.plugin));
         groupNode.action((sender, player, list) -> {
            ArgumentValue groupValue = groupNode.getValue(list);
            if (this.isValidGroup(sender, groupValue)) {
               for (User user : this.plugin.getUsers()) {
                  user.removeGroup((Group)groupValue.get());
               }

               sender.sendMessage(this.plugin.getPrefix() + PURGE_GROUP_CONFIRM.get().replace("%group%", ((Group)groupValue.get()).getName()));
            }
         });
      }));
   }

   private void PurgeGroupPermission(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(new SpecificArgument("PurgeGroupPermission"), purgeGroupPermissionNode -> purgeGroupPermissionNode.newNode(new PermissionArgument(this.plugin), permissionNode -> {
         permissionNode.require(new SuperadminRequirement(this.plugin));
         permissionNode.action((sender, player, list) -> {
            ArgumentValue permissionValue = permissionNode.getValue(list);

            for (Group group : this.plugin.getGroups()) {
               PermissionList name = group.getPermissions().name((String)permissionValue.get());
               name.forEach(Permission::remove);
            }

            sender.sendMessage(this.plugin.getPrefix() + PURGE_GROUPPERMISSION_CONFIRM.get().replace("%permission%", (CharSequence)permissionValue.get()));
         });
      }));
   }

   private void PurgePlayerPermission(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(new SpecificArgument("PurgePlayerPermission"), purgePlayerPermissionNode -> {
         purgePlayerPermissionNode.require(new SuperadminRequirement(this.plugin));
         purgePlayerPermissionNode.newNode(new PermissionArgument(this.plugin), permissionNode -> permissionNode.action((sender, player, list) -> {
            ArgumentValue permissionValue = permissionNode.getValue(list);

            for (User user : this.plugin.getUsers()) {
               PermissionList name = user.getPermissions().name((String)permissionValue.get());
               name.forEach(Permission::remove);
            }

            sender.sendMessage(this.plugin.getPrefix() + PURGE_PLAYERPERMISSION_CONFIRM.get().replace("%permission%", (CharSequence)permissionValue.get()));
         }));
      });
   }

   private void PurgeAllPlayerPermission(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(new SpecificArgument("PurgeAllPlayerPermission"), purgeAllPlayerPermissionNode -> {
         purgeAllPlayerPermissionNode.require(new SuperadminRequirement(this.plugin));
         purgeAllPlayerPermissionNode.newNode(new UserArgument(this.plugin, EmptyReason.INVALID), userNode -> userNode.action((sender, player, list) -> {
            ArgumentValue userValue = userNode.getValue(list);
            if (userValue.isValid()) {
               for (Permission permission : ((User)userValue.get()).getPermissions()) {
                  permission.remove();
               }

               sender.sendMessage(this.plugin.getPrefix() + PURGE_ALL_PLAYERPERMISSIONS_CONFIRM.get().replace("%user%", ((User)userValue.get()).getName()));
            }
         }));
      });
   }

   private void PurgePlayerGroups(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(new SpecificArgument("PurgePlayerGroups"), purgePlayerGroupsNode -> purgePlayerGroupsNode.newNode(new UserArgument(this.plugin, EmptyReason.INVALID), userNode -> {
         userNode.require(new SuperadminRequirement(this.plugin));
         userNode.action((sender, player, list) -> {
            ArgumentValue userValue = userNode.getValue(list);

            for (Group activeGroup : ((User)userValue.get()).getActiveGroups()) {
               ((User)userValue.get()).removeGroup(activeGroup);
            }

            sender.sendMessage(this.plugin.getPrefix() + PURGE_PLAYER_GROUPS_CONFIRM.get().replace("%player%", ((User)userValue.get()).getName()));
         });
      }));
   }

   private void SetGroups(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("SetGroups"),
         setGroupsNode -> setGroupsNode.newNode(
            new UserArgument(this.plugin, EmptyReason.INVALID),
            userNode -> {
               userNode.require(new SuperadminRequirement(this.plugin));
               userNode.action(
                  (sender, player, list) -> {
                     ArgumentValue userValue = userNode.getValue(list);
                     if (this.isValidUser(sender, userValue)) {
                        ((User)userValue.get()).getRankups().forEach(UserRankup::remove);
                        GroupList groups = userNode.getSpareArguments(list)
                           .stream()
                           .map(text -> this.plugin.getGroups().name(text).orElse(null))
                           .filter(Objects::nonNull)
                           .collect(Collectors.toCollection(GroupList::new));

                        for (Group group : groups) {
                           ((User)userValue.get()).addGroup(group);
                        }

                        if (groups.size() == 0) {
                           sender.sendMessage(this.plugin.getPrefix() + PLAYER_CLEARED_GROUPS.get().replace("%player%", ((User)userValue.get()).getName()));
                        } else {
                           String groupNames = groups.stream().map(Group::getName).collect(Collectors.joining(", "));
                           String message = PLAYER_SET_GROUPS.get().replace("%player%", ((User)userValue.get()).getName()).replace("%groups%", groupNames);
                           sender.sendMessage(this.plugin.getPrefix() + message);
                        }
                     }
                  }
               );
            }
         )
      );
   }

   private void SetGroupPriority(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("SetGroupPriority"),
         setGroupPriorityNode -> setGroupPriorityNode.newNode(
            new GroupArgument(this.plugin, EmptyReason.INVALID),
            groupNode -> groupNode.newNode(
               new AnyIntegerArgument("Priority", EmptyReason.INVALID),
               priorityNode -> {
                  priorityNode.require(new SuperadminRequirement(this.plugin));
                  priorityNode.action(
                     (sender, player, list) -> {
                        ArgumentValue groupValue = groupNode.getValue(list);
                        ArgumentValue priorityValue = priorityNode.getValue(list);
                        int count = 1;

                        try {
                           for (Group group : this.plugin.getGroups().bestToWorst()) {
                              group.setPriority(count);
                              count++;
                           }

                           int priority = ((Group)groupValue.get()).getPriority();
                           Optional first = this.plugin.getGroups().stream().filter(group -> group.getPriority() == (Integer)priorityValue.get()).findFirst();
                           ((Group)groupValue.get()).setPriority((Integer)priorityValue.get());
                           ((Group)first.get()).setPriority(priority);
                           sender.sendMessage(
                              this.plugin.getPrefix()
                                 + CHANGED_GROUP_PRIORITY.get()
                                    .replace("%group%", ((Group)groupValue.get()).getName())
                                    .replace("%priority%", ((Integer)priorityValue.get()).toString())
                           );
                        } catch (NullPointerException | NoSuchElementException exception) {
                           player.sendMessage(this.plugin.getPrefix() + Colors.GRAY + "Invalid Arguments");
                        }
                     }
                  );
               }
            )
         )
      );
   }

   private void SetGroupPrefix(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("SetGroupPrefix"),
         setGroupPrefixNode -> setGroupPrefixNode.newNode(
            new GroupArgument(this.plugin, EmptyReason.INVALID),
            groupNode -> {
               groupNode.newNode(new SpecificArgument("none"), noneNode -> {
                  noneNode.require(new SuperadminRequirement(this.plugin));
                  noneNode.action((sender, player, list) -> {
                     ArgumentValue groupValue = groupNode.getValue(list);
                     if (this.isValidGroup(sender, groupValue)) {
                        ((Group)groupValue.get()).setPrefix(null);
                        sender.sendMessage(this.plugin.getPrefix() + GROUP_SET_PREFIX_CLEAR.get().replace("%group%", ((Group)groupValue.get()).getName()));
                     }
                  });
               });
               groupNode.newNode(
                  new AnyArgument("<Prefix>"),
                  prefixNode -> {
                     prefixNode.require(new SuperadminRequirement(this.plugin));
                     prefixNode.action(
                        (sender, player, list) -> {
                           ArgumentValue groupValue = groupNode.getValue(list);
                           ArgumentValue prefixValue = prefixNode.getValue(list);
                           String extraWords = String.join(" ", prefixNode.getSpareArguments(list));
                           if (extraWords.isEmpty()) {
                              extraWords = null;
                           }

                           if (this.isValidGroup(sender, groupValue)) {
                              ((Group)groupValue.get()).setPrefix((String)prefixValue.get() + (extraWords == null ? "" : " " + extraWords));
                              String message = GROUP_SET_PREFIX_SET.get()
                                 .replace("%group%", ((Group)groupValue.get()).getName())
                                 .replace("%prefix%", (String)prefixValue.get() + (extraWords == null ? "" : " " + extraWords));
                              sender.sendMessage(this.plugin.getPrefix() + message);
                           }
                        }
                     );
                  }
               );
            }
         )
      );
   }

   private void SetGroupSuffix(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("SetGroupSuffix"),
         setGroupSuffixNode -> setGroupSuffixNode.newNode(
            new GroupArgument(this.plugin, EmptyReason.INVALID),
            groupNode -> {
               groupNode.newNode(new SpecificArgument("none"), noneNode -> {
                  noneNode.require(new SuperadminRequirement(this.plugin));
                  noneNode.action((sender, player, list) -> {
                     ArgumentValue groupValue = groupNode.getValue(list);
                     if (this.isValidGroup(sender, groupValue)) {
                        ((Group)groupValue.get()).setSuffix(null);
                        sender.sendMessage(this.plugin.getPrefix() + GROUP_SET_SUFFIX_CLEAR.get().replace("%group%", ((Group)groupValue.get()).getName()));
                     }
                  });
               });
               groupNode.newNode(
                  new AnyArgument("<Suffix>"),
                  suffixNode -> suffixNode.action(
                     (sender, player, list) -> {
                        suffixNode.require(new SuperadminRequirement(this.plugin));
                        ArgumentValue groupValue = groupNode.getValue(list);
                        ArgumentValue suffixValue = suffixNode.getValue(list);
                        String extraWords = String.join(" ", suffixNode.getSpareArguments(list));
                        if (extraWords.isEmpty()) {
                           extraWords = null;
                        }

                        if (this.isValidGroup(sender, groupValue)) {
                           ((Group)groupValue.get()).setSuffix((String)suffixValue.get() + (extraWords == null ? "" : " " + extraWords));
                           String message = GROUP_SET_SUFFIX_SET.get()
                              .replace("%group%", ((Group)groupValue.get()).getName())
                              .replace("%suffix%", (String)suffixValue.get() + (extraWords == null ? "" : " " + extraWords));
                           sender.sendMessage(this.plugin.getPrefix() + message);
                        }
                     }
                  )
               );
            }
         )
      );
   }

   private void SetPlayerPrefix(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("SetPlayerPrefix"),
         setPlayerPrefixNode -> {
            setPlayerPrefixNode.newNode(new SpecificArgument("clearAll"), clearAllNode -> {
               clearAllNode.require(new SuperadminRequirement(this.plugin));
               clearAllNode.action((sender, player, list) -> {
                  UserList usersWithPrefix = this.plugin.getUsers().stream().filter(User::hasPrefix).collect(Collectors.toCollection(UserList::new));
                  usersWithPrefix.forEach(user -> user.setPrefix(null));
                  sender.sendMessage(this.plugin.getPrefix() + PLAYER_SET_PREFIX_CLEAR_ALL.get().replace("%count%", Integer.toString(usersWithPrefix.size())));
               });
            });
            setPlayerPrefixNode.newNode(
               new UserArgument(this.plugin, EmptyReason.INVALID),
               userNode -> {
                  userNode.newNode(new SpecificArgument("none"), noneNode -> {
                     noneNode.require(new SuperadminRequirement(this.plugin));
                     noneNode.action((sender, player, list) -> {
                        ArgumentValue userValue = userNode.getValue(list);
                        if (this.isValidUser(sender, userValue)) {
                           ((User)userValue.get()).setPrefix(null);
                           sender.sendMessage(this.plugin.getPrefix() + PLAYER_SET_PREFIX_CLEAR.get().replace("%user%", ((User)userValue.get()).getName()));
                        }
                     });
                  });
                  userNode.newNode(
                     new AnyArgument("<Prefix>"),
                     prefixNode -> {
                        prefixNode.require(new SuperadminRequirement(this.plugin));
                        prefixNode.action(
                           (sender, player, list) -> {
                              ArgumentValue userValue = userNode.getValue(list);
                              ArgumentValue prefixValue = prefixNode.getValue(list);
                              String extraWords = String.join(" ", prefixNode.getSpareArguments(list));
                              if (extraWords.isEmpty()) {
                                 extraWords = null;
                              }

                              if (this.isValidUser(sender, userValue)) {
                                 ((User)userValue.get()).setPrefix((String)prefixValue.get() + (extraWords == null ? "" : " " + extraWords));
                                 String message = PLAYER_SET_PREFIX_SET.get()
                                    .replace("%user%", ((User)userValue.get()).getName())
                                    .replace("%prefix%", (String)prefixValue.get() + (extraWords == null ? "" : " " + extraWords));
                                 sender.sendMessage(this.plugin.getPrefix() + message);
                              }
                           }
                        );
                     }
                  );
               }
            );
         }
      );
   }

   private void SetPlayerSuffix(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("SetPlayerSuffix"),
         setPlayerSuffixNode -> {
            setPlayerSuffixNode.newNode(new SpecificArgument("clearAll"), clearAllNode -> {
               clearAllNode.require(new SuperadminRequirement(this.plugin));
               clearAllNode.action((sender, player, list) -> {
                  UserList usersWithSuffix = this.plugin.getUsers().stream().filter(User::hasSuffix).collect(Collectors.toCollection(UserList::new));
                  usersWithSuffix.forEach(user -> user.setSuffix(null));
                  sender.sendMessage(this.plugin.getPrefix() + PLAYER_SET_SUFFIX_CLEAR_ALL.get().replace("%count%", Integer.toString(usersWithSuffix.size())));
               });
            });
            setPlayerSuffixNode.newNode(
               new UserArgument(this.plugin, EmptyReason.INVALID),
               userNode -> {
                  userNode.newNode(new SpecificArgument("none"), noneNode -> {
                     noneNode.require(new SuperadminRequirement(this.plugin));
                     noneNode.action((sender, player, list) -> {
                        ArgumentValue userValue = userNode.getValue(list);
                        if (this.isValidUser(sender, userValue)) {
                           ((User)userValue.get()).setSuffix(null);
                           sender.sendMessage(this.plugin.getPrefix() + PLAYER_SET_SUFFIX_CLEAR.get().replace("%user%", ((User)userValue.get()).getName()));
                        }
                     });
                  });
                  userNode.newNode(
                     new AnyArgument("<Suffix>"),
                     suffixNode -> suffixNode.action(
                        (sender, player, list) -> {
                           ArgumentValue userValue = userNode.getValue(list);
                           ArgumentValue suffixValue = suffixNode.getValue(list);
                           String extraWords = String.join(" ", suffixNode.getSpareArguments(list));
                           if (extraWords.isEmpty()) {
                              extraWords = null;
                           }

                           if (this.isValidUser(sender, userValue)) {
                              ((User)userValue.get()).setSuffix((String)suffixValue.get() + (extraWords == null ? "" : " " + extraWords));
                              String message = PLAYER_SET_SUFFIX_SET.get()
                                 .replace("%user%", ((User)userValue.get()).getName())
                                 .replace("%suffix%", (String)suffixValue.get() + (extraWords == null ? "" : " " + extraWords));
                              sender.sendMessage(this.plugin.getPrefix() + message);
                           }
                        }
                     )
                  );
               }
            );
         }
      );
   }

   private void TransferAccount(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("TransferAccount"),
         transferAccountNode -> transferAccountNode.newNode(
            new SpecificArguments(new String[]{"Groups", "Permissions", "All"}),
            typeNode -> typeNode.newNode(
               new SpecificArguments(new String[]{"Overwrite", "Append"}),
               modeNode -> modeNode.newNode(
                  new UserArgument(this.plugin, EmptyReason.INVALID),
                  oldUserNode -> oldUserNode.newNode(
                     new UserArgument(this.plugin, EmptyReason.INVALID),
                     newUserNode -> {
                        newUserNode.require(new SuperadminRequirement(this.plugin));
                        newUserNode.action(
                           (sender, player, list) -> {
                              ArgumentValue typeValue = typeNode.getValue(list);
                              ArgumentValue modeValue = modeNode.getValue(list);
                              ArgumentValue oldUserValue = oldUserNode.getValue(list);
                              ArgumentValue newUserValue = newUserNode.getValue(list);
                              if (this.isValidUser(sender, oldUserValue) && this.isValidUser(sender, newUserValue)) {
                                 if (oldUserValue.get() == newUserValue.get()) {
                                    sender.sendMessage(this.plugin.getPrefix() + TRANSFER_SOURCES_EQUAL.get());
                                 }

                                 if (((String)modeValue.get()).equalsIgnoreCase("Overwrite")) {
                                    if (((String)typeValue.get()).equalsIgnoreCase("Groups") || ((String)typeValue.get()).equalsIgnoreCase("All")) {
                                       for (Stored group : ((User)newUserValue.get()).getGroups()) {
                                          ((User)newUserValue.get()).removeGroup((Group)group.get().get());
                                       }
                                    }

                                    if (((String)typeValue.get()).equalsIgnoreCase("Permissions") || ((String)typeValue.get()).equalsIgnoreCase("All")) {
                                       ((User)newUserValue.get()).getPermissions().stream().forEach(Permission::remove);
                                    }
                                 }

                                 if (((String)typeValue.get()).equalsIgnoreCase("Groups")) {
                                    for (Stored group : ((User)oldUserValue.get()).getGroups()) {
                                       ((User)newUserValue.get()).addGroup((Group)group.get().get());
                                       ((User)oldUserValue.get()).removeGroup((Group)group.get().get());
                                    }

                                    sender.sendMessage(
                                       this.plugin.getPrefix()
                                          + TRANSFER_ACCOUNT_SUCCESSFUL_GROUPS.get()
                                             .replace("%oldTarget%", ((User)oldUserValue.get()).getName())
                                             .replace("%newTarget%", ((User)newUserValue.get()).getName())
                                    );
                                 }

                                 if (((String)typeValue.get()).equalsIgnoreCase("Permissions")) {
                                    ((User)oldUserValue.get()).getPermissions().stream().forEach(permission -> {
                                       permission.copyTo((PermissionHolder)newUserValue.get());
                                       permission.remove();
                                    });
                                    sender.sendMessage(
                                       this.plugin.getPrefix()
                                          + TRANSFER_ACCOUNT_SUCCESSFUL_PERMISSIONS.get()
                                             .replace("%oldTarget%", ((User)oldUserValue.get()).getName())
                                             .replace("%newTarget%", ((User)newUserValue.get()).getName())
                                    );
                                 }

                                 if (((String)typeValue.get()).equalsIgnoreCase("All")) {
                                    for (Stored group : ((User)oldUserValue.get()).getGroups()) {
                                       ((User)newUserValue.get()).addGroup((Group)group.get().get());
                                       ((User)oldUserValue.get()).removeGroup((Group)group.get().get());
                                    }

                                    ((User)oldUserValue.get()).getPermissions().stream().forEach(permission -> {
                                       permission.copyTo((PermissionHolder)newUserValue.get());
                                       permission.remove();
                                    });
                                    sender.sendMessage(
                                       this.plugin.getPrefix()
                                          + TRANSFER_ACCOUNT_SUCCESSFUL_ALL_DATA.get()
                                             .replace("%oldTarget%", ((User)oldUserValue.get()).getName())
                                             .replace("%newTarget%", ((User)newUserValue.get()).getName())
                                    );
                                 }
                              }
                           }
                        );
                     }
                  )
               )
            )
         )
      );
   }

   private void Transfer(CommandNode<String, SpecificArgument> commandNode) {
      commandNode.newNode(
         new SpecificArgument("Transfer"),
         transferNode -> {
            transferNode.require(new SuperadminRequirement(this.plugin));
            transferNode.require(new MySQLEnabledRequirement(this.plugin));
            transferNode.newNode(
               new SpecificArguments(new String[]{"File", "MySQL"}),
               sourceNode -> sourceNode.newNode(new SpecificArguments(new String[]{"File", "MySQL"}), targetNode -> targetNode.action((sender, player, list) -> {
                  String sourceType = sourceNode.getValue(list).get();
                  String targetType = targetNode.getValue(list).get();
                  if (sourceType.equalsIgnoreCase(targetType)) {
                     sender.sendMessage(this.plugin.getPrefix() + TRANSFER_SOURCES_EQUAL);
                  }

                  if (sourceType.equals("File") && targetType.equals("MySQL")) {
                     new TransferAssistant(this.plugin, true);
                  }

                  if (sourceType.equals("MySQL") && targetType.equals("File")) {
                     new TransferAssistant(this.plugin, false);
                  }
               }))
            );
         }
      );
   }

   @Override
   public boolean isRegistered() {
      return true;
   }

   private boolean isValidUser(CommandSender sender, ArgumentValue<User> argumentValue) {
      if (!argumentValue.isValid()) {
         sender.sendMessage(this.plugin.getPrefix() + NO_VALID_USER.get().replace("%user%", argumentValue.getRaw()));
      }

      return argumentValue.isValid();
   }

   private boolean isValidGroup(CommandSender sender, ArgumentValue<Group> argumentValue) {
      if (!argumentValue.isValid()) {
         sender.sendMessage(this.plugin.getPrefix() + NO_VALID_GROUP.get().replace("%group%", argumentValue.getRaw()));
      }

      return argumentValue.isValid();
   }
}
