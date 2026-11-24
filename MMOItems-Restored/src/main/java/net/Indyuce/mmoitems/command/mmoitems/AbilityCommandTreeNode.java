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
   public AbilityCommandTreeNode(CommandTreeNode var1) {
      super(var1, "ability");
      this.addParameter(
         new Parameter("<ability>", (var0, var1x) -> MMOItems.plugin.getSkills().getAll().forEach(var1xx -> var1x.add(var1xx.getHandler().getId())))
      );
      this.addParameter(Parameter.PLAYER_OPTIONAL);

      for (int var2 = 0; var2 < 10; var2++) {
         this.addParameter(new Parameter("<modifier>", (var0, var1x) -> {
            try {
               RegisteredSkill var2x = MMOItems.plugin.getSkills().getSkillOrThrow(var0.getArguments()[1].toUpperCase().replace("-", "_"));
               var1x.addAll(var2x.getHandler().getModifiers());
            } catch (Exception var3) {
            }
         }));
         this.addParameter(new Parameter("<value>", (var0, var1x) -> var1x.add("0")));
      }
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (var2.length < 2) {
         return CommandResult.THROW_USAGE;
      }

      if (var2.length < 3 && !(var1 instanceof Player)) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "Please specify a player to use this command.");
         return CommandResult.FAILURE;
      }

      Player var3 = var2.length > 2 ? Bukkit.getPlayer(var2[2]) : (Player)var1;
      if (var3 == null) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "Couldn't find player called " + var2[2] + ".");
         return CommandResult.FAILURE;
      }

      String var4 = var2[1].toUpperCase().replace("-", "_");
      if (!MMOItems.plugin.getSkills().hasSkill(var4)) {
         var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "Couldn't find ability " + var4 + ".");
         return CommandResult.FAILURE;
      }

      AbilityData var5 = new AbilityData(MMOItems.plugin.getSkills().getSkill(var4), TriggerType.CAST);

      for (byte var6 = 3; var6 < var2.length - 1; var6 += 2) {
         String var7 = var2[var6];
         String var8 = var2[var6 + 1];

         try {
            var5.setModifier(var7, Double.parseDouble(var8));
         } catch (Exception var10) {
            var1.sendMessage(MMOItems.plugin.getPrefix() + ChatColor.RED + "Wrong format: {" + var7 + " " + var8 + "}");
            return CommandResult.FAILURE;
         }
      }

      PlayerMetadata var11 = MMOPlayerData.get(var3).getStatMap().cache(EquipmentSlot.MAIN_HAND);
      var5.cast(new TriggerMetadata(var11, null, null));
      return CommandResult.SUCCESS;
   }
}
