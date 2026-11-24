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

public class DemoteCommand extends Command {
   private final UltraPermissions plugin;
   private Optional<Group> toBeDemotedGroup;
   private static final Phrase NOT_VALID = Phrase.create("DemoteCommand.notValid", "Invalid User", Colors.GRAY);
   private static final Phrase PROMOTED = Phrase.create(
      "DemoteCommand.demoted.success",
      "Successfully demoted **%user%** from **%oldGroup%** to **%newGroup%** (**Group Path: %groupPathName%**)",
      Colors.GRAY,
      Colors.AQUA,
      Colors.GREEN,
      Colors.GREEN,
      Colors.WHITE
   );
   private static final Phrase FAILED_NOT_IN_PATH = Phrase.create(
      "DemoteCommand.demoted.failed.notInPath",
      "User **%user%** does not own a group of group path **%groupPathName%**",
      Colors.GRAY,
      Colors.AQUA,
      Colors.YELLOW
   );
   private static final Phrase FAILED_ALREADY_IN_HIGHEST = Phrase.create(
      "DemoteCommand.demoted.failed.alreadyInLowest",
      "User **%user%** is already in the lowest group of the group path **%groupPathName%**",
      Colors.GRAY,
      Colors.AQUA,
      Colors.YELLOW
   );

   public DemoteCommand(UltraPermissions ultraPermissions) {
      super(ultraPermissions, "demote", "ultrapermissions.commands.demote");
      this.plugin = ultraPermissions;
      this.toBeDemotedGroup = Optional.empty();
   }

   @Override
   public void define(CommandSender sender, CommandNode<String, SpecificArgument> commandNode) {
      commandNode.require(new PermissionRequirement(this.plugin, "ultrapermissions.commands.demote"));
      commandNode.newNode(
         new UserArgument(this.plugin, EmptyReason.NO_MATCH),
         userNode -> userNode.newNode(
            new GroupPathArgument(this.plugin, EmptyReason.NO_MATCH),
            pathNode -> pathNode.action(
               (commandSender, player, list) -> {
                  ArgumentValue argumentValue = userNode.getValue(list);
                  ArgumentValue pathValue = pathNode.getValue(list);
                  if (argumentValue.isValid() && pathValue.isValid()) {
                     ((User)argumentValue.get()).getGroups().forEach(stored -> {
                        if (stored.get().isPresent() && ((GroupPath)pathValue.get()).getGroupPath().contains(stored.get().get())) {
                           this.toBeDemotedGroup = stored.get();
                        }
                     });
                     ((GroupPath)pathValue.get()).getGroupPath().forEach(group -> ((User)argumentValue.get()).getActiveGroups().forEach(userGroup -> {
                        if (userGroup.getKey().equals(group.getKey())) {
                           this.toBeDemotedGroup = Optional.of(group);
                        }
                     }));
                     if (!this.toBeDemotedGroup.isPresent()) {
                        commandSender.sendMessage(
                           this.plugin.getPrefix()
                              + FAILED_NOT_IN_PATH.get()
                                 .replace("%user%", ((User)argumentValue.get()).getName())
                                 .replace("%groupPathName%", ((GroupPath)pathValue.get()).getName())
                        );
                     } else if (((GroupPath)pathValue.get()).getGroupPath().indexOf(this.toBeDemotedGroup.get()) - 1 < 0) {
                        commandSender.sendMessage(
                           this.plugin.getPrefix()
                              + FAILED_ALREADY_IN_HIGHEST.get()
                                 .replace("%user%", ((User)argumentValue.get()).getName())
                                 .replace("%groupPathName%", ((GroupPath)pathValue.get()).getName())
                        );
                     } else {
                        Optional optional = Optional.ofNullable(
                           ((GroupPath)pathValue.get()).getGroupPath().get(((GroupPath)pathValue.get()).getGroupPath().indexOf(this.toBeDemotedGroup.get()) - 1)
                        );
                        if (optional.isPresent()) {
                           ((User)argumentValue.get()).addGroup((Group)optional.get(), ((User)argumentValue.get()).getGroupExpiry(this.toBeDemotedGroup.get().toStored()));
                           ((User)argumentValue.get()).removeGroup(this.toBeDemotedGroup.get());
                           commandSender.sendMessage(
                              this.plugin.getPrefix()
                                 + PROMOTED.get()
                                    .replace("%user%", ((User)argumentValue.get()).getName())
                                    .replace("%oldGroup%", this.toBeDemotedGroup.get().getName())
                                    .replace("%newGroup%", ((Group)optional.get()).getName())
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
