package net.Indyuce.mmocore.command.rpg.admin;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.resource.PlayerResource;
import org.bukkit.command.CommandSender;

public class AdminCommandTreeNode extends CommandTreeNode {
   public AdminCommandTreeNode(CommandTreeNode parent) {
      super(parent, "admin");
      this.addChild(new HideActionBarCommandTreeNode(this));
      this.addChild(new NoCooldownCommandTreeNode(this));
      this.addChild(new ResetCommandTreeNode(this));
      this.addChild(new InfoCommandTreeNode(this));
      this.addChild(new ClassCommandTreeNode(this));
      this.addChild(new ForceClassCommandTreeNode(this));
      this.addChild(new ExportDataTreeNode(this));
      this.addChild(new ExperienceCommandTreeNode(this));
      this.addChild(new LevelCommandTreeNode(this));
      this.addChild(new AttributeCommandTreeNode(this));
      this.addChild(new SkillCommandTreeNode(this));
      this.addChild(new SaveDataTreeNode(this));
      this.addChild(new SlotCommandTreeNode(this));
      this.addChild(new PointsCommandTreeNode("skill", this, PlayerData::setSkillPoints, PlayerData::giveSkillPoints, PlayerData::getSkillPoints));
      this.addChild(new PointsCommandTreeNode("class", this, PlayerData::setClassPoints, PlayerData::giveClassPoints, PlayerData::getClassPoints));
      this.addChild(
         new PointsCommandTreeNode("attribute", this, PlayerData::setAttributePoints, PlayerData::giveAttributePoints, PlayerData::getAttributePoints)
      );
      this.addChild(
         new PointsCommandTreeNode(
            "attr-realloc",
            this,
            PlayerData::setAttributeReallocationPoints,
            PlayerData::giveAttributeReallocationPoints,
            PlayerData::getAttributeReallocationPoints
         )
      );
      this.addChild(
         new PointsCommandTreeNode(
            "skill-realloc", this, PlayerData::setSkillReallocationPoints, PlayerData::giveSkillReallocationPoints, PlayerData::getSkillReallocationPoints
         )
      );
      this.addChild(
         new PointsCommandTreeNode(
            "skill-tree-realloc",
            this,
            PlayerData::setSkillTreeReallocationPoints,
            PlayerData::giveSkillTreeReallocationPoints,
            PlayerData::getSkillTreeReallocationPoints
         )
      );
      this.addChild(
         new SkillTreePointsCommandTreeNode(
            this,
            (playerData, integer, s) -> playerData.setSkillTreePoints(s, integer),
            (playerData, integer, s) -> playerData.giveSkillTreePoints(s, integer),
            PlayerData::getSkillTreePoints
         )
      );

      for (PlayerResource res : PlayerResource.values()) {
         this.addChild(new ResourceCommandTreeNode(res.name().toLowerCase(), this, res));
      }
   }

   public CommandResult execute(CommandSender sender, String[] args) {
      return CommandResult.THROW_USAGE;
   }
}
