package me.TechsCode.UltraPermissions.commands;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.command.ArgumentValue;
import me.TechsCode.UltraPermissions.base.command.Command;
import me.TechsCode.UltraPermissions.base.command.CommandNode;
import me.TechsCode.UltraPermissions.base.command.EmptyReason;
import me.TechsCode.UltraPermissions.base.command.arguments.SpecificArgument;
import me.TechsCode.UltraPermissions.base.command.requirements.PlayerRequirement;
import me.TechsCode.UltraPermissions.commands.arguments.GroupArgument;
import me.TechsCode.UltraPermissions.commands.arguments.UserArgument;
import me.TechsCode.UltraPermissions.commands.requirements.SuperadminRequirement;
import me.TechsCode.UltraPermissions.gui.GroupView;
import me.TechsCode.UltraPermissions.gui.MigrationView;
import me.TechsCode.UltraPermissions.gui.Overview;
import me.TechsCode.UltraPermissions.gui.UserView;
import me.TechsCode.UltraPermissions.migration.MigrationAssistant;
import me.TechsCode.UltraPermissions.migration.Migrations;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.command.CommandSender;

public class MainCommand extends Command {
   private final UltraPermissions plugin;

   public MainCommand(UltraPermissions plugin) {
      super(plugin, "uperms", "");
      this.plugin = plugin;
   }

   @Override
   public void define(CommandSender commandSender, CommandNode<String, SpecificArgument> node) {
      node.require(new PlayerRequirement(this.plugin));
      node.require(new SuperadminRequirement(this.plugin));
      node.newNode(new UserArgument(this.plugin, EmptyReason.NO_MATCH), userNode -> userNode.action((sender, player, arguments) -> {
         ArgumentValue argumentValue = userNode.getValue(arguments);
         if (argumentValue.isValid()) {
            new UserView(player, this.plugin, (User)argumentValue.get()) {
               @Override
               public void onBack() {
               }

               @Override
               public boolean hasBackButton() {
                  return false;
               }
            };
         }
      }));
      node.newNode(new GroupArgument(this.plugin, EmptyReason.NO_MATCH), groupNode -> groupNode.action((sender, player, arguments) -> {
         ArgumentValue argumentValue = groupNode.getValue(arguments);
         if (argumentValue.isValid()) {
            new GroupView(player, this.plugin, (Group)argumentValue.get()) {
               @Override
               public void onBack() {
               }

               @Override
               public boolean hasBackButton() {
                  return false;
               }
            };
         }
      }));
      node.action((sender, player, arguments) -> {
         if (Migrations.isAvailable()) {
            MigrationAssistant migrationAssistant = Migrations.getMigrationAssistant();
            new MigrationView(player, this.plugin, migrationAssistant);
         } else {
            new Overview(player, this.plugin).reopen();
         }
      });
   }

   @Override
   public boolean isRegistered() {
      return true;
   }
}
