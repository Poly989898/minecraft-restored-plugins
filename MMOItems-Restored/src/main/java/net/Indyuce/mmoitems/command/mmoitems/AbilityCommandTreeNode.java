package net.Indyuce.mmoitems.command.mmoitems;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.Parameter;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.skill.RegisteredSkill;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AbilityCommandTreeNode extends CommandTreeNode {
   public AbilityCommandTreeNode(CommandTreeNode commandTreeNode) {
      super(commandTreeNode, "ability");
      this.addParameter(
         new Parameter("<ability>", (commandTreeExplorer, var1x) -> MMOItems.plugin.getSkills().getAll().forEach(var1xx -> var1x.add(var1xx.getHandler().getId())))
      );
      this.addParameter(Parameter.PLAYER_OPTIONAL);

      for (int i = 0; i < 10; i++) {
         this.addParameter(new Parameter("<modifier>", (commandTreeExplorer, var1x) -> {
            try {
               RegisteredSkill var2x = MMOItems.plugin.getSkills().getSkillOrThrow(commandTreeExplorer.getArguments()[1].toUpperCase().replace("-", "_"));
               var1x.addAll(var2x.getHandler().getModifiers());
            } catch (Exception exception) {
            }
         }));
         this.addParameter(new Parameter("<value>", (commandTreeExplorer, var1x) -> var1x.add("0")));
      }
   }

   public CommandResult execute(CommandSender sender, String[] texts) {
      if (texts.length < 2) {
         return CommandResult.THROW_USAGE;
      }

      if (texts.length < 3 && !(sender instanceof Player)) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "Please specify a player to use this command.");
         return CommandResult.FAILURE;
      }

      Player player = texts.length > 2 ? Bukkit.getPlayer(texts[2]) : (Player)sender;
      if (player == null) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "Couldn't find player called " + texts[2] + ".");
         return CommandResult.FAILURE;
      }

      String text = texts[1].toUpperCase().replace("-", "_");
      if (!MMOItems.plugin.getSkills().hasSkill(text)) {
         sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "Couldn't find ability " + text + ".");
         return CommandResult.FAILURE;
      }

      AbilityData abilityData = new AbilityData(MMOItems.plugin.getSkills().getSkill(text), TriggerType.CAST);

      for (byte b = 3; b < texts.length - 1; b += 2) {
         String text2 = texts[b];
         String text3 = texts[b + 1];

         try {
            abilityData.setModifier(text2, Double.parseDouble(text3));
         } catch (Exception exception) {
            sender.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "Wrong format: {" + text2 + " " + text3 + "}");
            return CommandResult.FAILURE;
         }
      }

      PlayerMetadata playerMetadata = MMOPlayerData.get(player).getStatMap().cache(EquipmentSlot.MAIN_HAND);
      abilityData.cast(new TriggerMetadata(playerMetadata, null, null));
      return CommandResult.SUCCESS;
   }
}
