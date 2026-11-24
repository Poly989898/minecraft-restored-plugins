package fr.phoenixdevt.mmoprofiles.bukkit.command;

import io.lumine.mythic.lib.command.api.CommandTreeRoot;

public class MMOProfilesCommandTreeRoot extends CommandTreeRoot {
   public MMOProfilesCommandTreeRoot() {
      super("profiles", "mmoprofiles.command");
      this.addChild(new ReloadCommandNode(this));
      this.addChild(new TransferCommandNode(this));
      this.addChild(new OpenCommandNode(this));
      this.addChild(new TestCommandNode(this));
      this.addChild(new ExportDataCommandNode(this));
   }
}
