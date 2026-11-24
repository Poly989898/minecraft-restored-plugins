package net.Indyuce.mmocore.command.rpg.admin;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import io.lumine.mythic.lib.data.DataExport;
import io.lumine.mythic.lib.data.sql.SQLDataSource;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.manager.data.sql.SQLDataHandler;
import net.Indyuce.mmocore.manager.data.yaml.YAMLPlayerDataHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ExportDataTreeNode extends CommandTreeNode {
   public ExportDataTreeNode(CommandTreeNode parent) {
      super(parent, "exportdata");
   }

   @NotNull
   public CommandResult execute(CommandSender sender, String[] strings) {
      boolean result = new DataExport(MMOCore.plugin.playerDataManager, sender)
         .start(() -> new YAMLPlayerDataHandler(MMOCore.plugin), () -> new SQLDataHandler(new SQLDataSource(MMOCore.plugin)));
      return result ? CommandResult.SUCCESS : CommandResult.FAILURE;
   }
}
