package net.Indyuce.mmocore.command;

import io.lumine.mythic.lib.command.api.CommandTreeRoot;
import io.lumine.mythic.lib.command.api.Parameter;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.command.rpg.CastCommandTreeNode;
import net.Indyuce.mmocore.command.rpg.CoinsCommandTreeNode;
import net.Indyuce.mmocore.command.rpg.NoteCommandTreeNode;
import net.Indyuce.mmocore.command.rpg.ReloadCommandTreeNode;
import net.Indyuce.mmocore.command.rpg.admin.AdminCommandTreeNode;
import net.Indyuce.mmocore.command.rpg.booster.BoosterCommandTreeNode;
import net.Indyuce.mmocore.command.rpg.debug.DebugCommandTreeNode;
import net.Indyuce.mmocore.command.rpg.quest.QuestCommandTreeNode;
import net.Indyuce.mmocore.command.rpg.waypoint.WaypointsCommandTreeNode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public class MMOCoreCommandTreeRoot extends CommandTreeRoot implements CommandExecutor, TabCompleter {
   public static final Parameter PROFESSION = new Parameter("<profession/main>", (explorer, list) -> {
      MMOCore.plugin.professionManager.getAll().forEach(profession -> list.add(profession.getId()));
      list.add("main");
   });
   public static final Parameter QUEST = new Parameter(
      "<quest>", (explorer, list) -> MMOCore.plugin.questManager.getAll().forEach(quest -> list.add(quest.getId()))
   );

   public MMOCoreCommandTreeRoot() {
      super("mmocore", "mmocore.admin");
      this.addChild(new ReloadCommandTreeNode(this));
      this.addChild(new CastCommandTreeNode(this));
      this.addChild(new CoinsCommandTreeNode(this));
      this.addChild(new NoteCommandTreeNode(this));
      this.addChild(new AdminCommandTreeNode(this));
      this.addChild(new DebugCommandTreeNode(this));
      this.addChild(new BoosterCommandTreeNode(this));
      this.addChild(new WaypointsCommandTreeNode(this));
      this.addChild(new QuestCommandTreeNode(this));
   }
}
