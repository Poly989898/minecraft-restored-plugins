package me.TechsCode.UltraPermissions.commands;

import java.util.Optional;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.command.ArgumentValue;
import me.TechsCode.UltraPermissions.base.command.Command;
import me.TechsCode.UltraPermissions.base.command.CommandNode;
import me.TechsCode.UltraPermissions.base.command.EmptyReason;
import me.TechsCode.UltraPermissions.base.command.arguments.SpecificArgument;
import me.TechsCode.UltraPermissions.base.command.requirements.PermissionRequirement;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.commands.arguments.GroupPathArgument;
import me.TechsCode.UltraPermissions.commands.arguments.UserArgument;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.GroupPath;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.command.CommandSender;

public class PromoteCommand extends Command {
   private final UltraPermissions plugin;
   private Optional<Group> toBePromotedGroup;
   private static final Phrase NOT_VALID = Phrase.create("PromoteCommand.notValid", "Invalid User", Colors.GRAY);
   private static final Phrase PROMOTED = Phrase.create(
      "PromoteCommand.promoted.success",
      "Successfully promoted **%user%** from **%oldGroup%** to **%newGroup%** (**Group Path: %groupPathName%**)",
      Colors.GRAY,
      Colors.AQUA,
      Colors.GREEN,
      Colors.GREEN,
      Colors.WHITE
   );
   private static final Phrase FAILED_NOT_IN_PATH = Phrase.create(
      "PromoteCommand.promoted.failed.notInPath",
      "User **%user%** does not own a group of group path **%groupPathName%**",
      Colors.GRAY,
      Colors.AQUA,
      Colors.YELLOW
   );
   private static final Phrase FAILED_ALREADY_IN_HIGHEST = Phrase.create(
      "PromoteCommand.promoted.failed.alreadyInHighest",
      "User **%user%** is already in the highest group of the group path **%groupPathName%**",
      Colors.GRAY,
      Colors.AQUA,
      Colors.YELLOW
   );

   public PromoteCommand(UltraPermissions ultraPermissions) {
      super(ultraPermissions, "promote", "ultrapermissions.commands.promote");
      this.plugin = ultraPermissions;
      this.toBePromotedGroup = Optional.empty();
   }

   @Override
   public void define(CommandSender sender, CommandNode<String, SpecificArgument> node) {
      node.require(new PermissionRequirement(this.plugin, "ultrapermissions.commands.promote"));
      node.newNode(
         new UserArgument(this.plugin, EmptyReason.NO_MATCH),
         userNode -> userNode.newNode(
            new GroupPathArgument(this.plugin, EmptyReason.NO_MATCH),
            pathNode -> pathNode.action(
               (commandSender, player, arguments) -> {
                  ArgumentValue userValue = userNode.getValue(arguments);
                  ArgumentValue pathValue = pathNode.getValue(arguments);
                  if (userValue.isValid() && pathValue.isValid()) {
                     ((User)userValue.get()).getGroups().forEach(groupStored -> {
                        if (groupStored.get().isPresent() && ((GroupPath)pathValue.get()).getGroupPath().contains(groupStored.get().get())) {
                           this.toBePromotedGroup = groupStored.get();
                        }
                     });
                     ((GroupPath)pathValue.get()).getGroupPath().forEach(pathGroup -> ((User)userValue.get()).getActiveGroups().forEach(activeGroup -> {
                        if (activeGroup.getKey().equals(pathGroup.getKey())) {
                           this.toBePromotedGroup = Optional.of(pathGroup);
                        }
                     }));
                     if (!this.toBePromotedGroup.isPresent()) {
                        commandSender.sendMessage(
                           this.plugin.getPrefix()
                              + FAILED_NOT_IN_PATH.get()
                                 .replace("%user%", ((User)userValue.get()).getName())
                                 .replace("%groupPathName%", ((GroupPath)pathValue.get()).getName())
                        );
                     } else if (((GroupPath)pathValue.get()).getGroupPath().size()
                        <= ((GroupPath)pathValue.get()).getGroupPath().indexOf(this.toBePromotedGroup.get()) + 1) {
                        commandSender.sendMessage(
                           this.plugin.getPrefix()
                              + FAILED_ALREADY_IN_HIGHEST.get()
                                 .replace("%user%", ((User)userValue.get()).getName())
                                 .replace("%groupPathName%", ((GroupPath)pathValue.get()).getName())
                        );
                     } else {
                        Optional nextGroup = Optional.ofNullable(
                           ((GroupPath)pathValue.get()).getGroupPath().get(((GroupPath)pathValue.get()).getGroupPath().indexOf(this.toBePromotedGroup.get()) + 1)
                        );
                        if (nextGroup.isPresent()) {
                           ((User)userValue.get()).addGroup((Group)nextGroup.get(), ((User)userValue.get()).getGroupExpiry(this.toBePromotedGroup.get().toStored()));
                           ((User)userValue.get()).removeGroup(this.toBePromotedGroup.get());
                           commandSender.sendMessage(
                              this.plugin.getPrefix()
                                 + PROMOTED.get()
                                    .replace("%user%", ((User)userValue.get()).getName())
                                    .replace("%oldGroup%", this.toBePromotedGroup.get().getName())
                                    .replace("%newGroup%", ((Group)nextGroup.get()).getName())
                                    .replace("%groupPathName%", ((GroupPath)pathValue.get()).getName())
                           );
                        }
                     }
                  } else {
                     commandSender.sendMessage(NOT_VALID.get());
                  }
               }
            )
         )
      );
   }

   @Override
   public boolean isRegistered() {
      return true;
   }
}
