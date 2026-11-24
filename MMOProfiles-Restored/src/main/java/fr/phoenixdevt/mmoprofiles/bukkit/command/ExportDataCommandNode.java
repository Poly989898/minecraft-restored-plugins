package fr.phoenixdevt.mmoprofiles.bukkit.command;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.manager.data.json.JSONDataHandler;
import fr.phoenixdevt.mmoprofiles.bukkit.manager.data.sql.SQLDataHandler;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import io.lumine.mythic.lib.data.DataExport;
import io.lumine.mythic.lib.data.sql.SQLDataSource;
import org.bukkit.command.CommandSender;

public class ExportDataCommandNode extends CommandTreeNode {
   public ExportDataCommandNode(CommandTreeNode parent) {
      super(parent, "export-data");
   }

   public CommandResult execute(CommandSender sender, String[] args) {
      boolean result = new DataExport(MMOProfiles.plugin.playerDataManager, sender)
         .start(JSONDataHandler::new, () -> new SQLDataHandler(new SQLDataSource(MMOProfiles.plugin)));
      return result ? CommandResult.SUCCESS : CommandResult.FAILURE;
   }
}
