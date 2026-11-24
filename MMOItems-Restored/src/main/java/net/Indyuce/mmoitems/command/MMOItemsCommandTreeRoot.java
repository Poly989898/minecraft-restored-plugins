package net.Indyuce.mmoitems.command;

import io.lumine.mythic.lib.command.api.CommandTreeRoot;
import io.lumine.mythic.lib.command.api.Parameter;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.command.mmoitems.AbilityCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.AllItemsCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.BrowseCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.CopyCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.CreateCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.DeleteCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.DropCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.EditCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.GenerateCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.GiveAllCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.GiveCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.ItemListCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.ReloadCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.TakeCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.debug.DebugCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.item.ItemCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.list.ListCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.revid.RevisionIDCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.stations.StationsCommandTreeNode;
import net.Indyuce.mmoitems.command.mmoitems.update.UpdateCommandTreeNode;

public class MMOItemsCommandTreeRoot extends CommandTreeRoot {
   public static final Parameter TYPE = new Parameter("<type>", (var0, var1) -> MMOItems.plugin.getTypes().getAll().forEach(var1x -> var1.add(var1x.getId())));
   public static final Parameter ID_2 = new Parameter("<id>", (var0, var1) -> {
      try {
         Type var2 = Type.get(var0.getArguments()[1]);
         MMOItems.plugin.getTemplates().getTemplates(var2).forEach(var1x -> var1.add(var1x.getId()));
      } catch (Exception var3) {
      }
   });

   public MMOItemsCommandTreeRoot() {
      super("mmoitems", "mmoitems.admin");
      this.addChild(new CreateCommandTreeNode(this));
      this.addChild(new DeleteCommandTreeNode(this));
      this.addChild(new EditCommandTreeNode(this));
      this.addChild(new CopyCommandTreeNode(this));
      this.addChild(new GiveCommandTreeNode(this));
      this.addChild(new TakeCommandTreeNode(this));
      this.addChild(new GenerateCommandTreeNode(this));
      this.addChild(new BrowseCommandTreeNode(this));
      this.addChild(new UpdateCommandTreeNode(this));
      this.addChild(new DebugCommandTreeNode(this));
      this.addChild(new ReloadCommandTreeNode(this));
      this.addChild(new StationsCommandTreeNode(this));
      this.addChild(new AllItemsCommandTreeNode(this));
      this.addChild(new ListCommandTreeNode(this));
      this.addChild(new DropCommandTreeNode(this));
      this.addChild(new AbilityCommandTreeNode(this));
      this.addChild(new GiveAllCommandTreeNode(this));
      this.addChild(new ItemListCommandTreeNode(this));
      this.addChild(new RevisionIDCommandTreeNode(this));
      this.addChild(new ItemCommandTreeNode(this));
   }
}
