package net.Indyuce.mmoitems.command.mmoitems.debug;

import io.lumine.mythic.lib.command.api.CommandTreeNode;
import io.lumine.mythic.lib.command.api.CommandTreeNode.CommandResult;
import io.lumine.mythic.lib.version.VPotionEffectType;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class HealCommandTreeNode extends CommandTreeNode {
   public HealCommandTreeNode(CommandTreeNode var1) {
      super(var1, "heal");
   }

   public CommandResult execute(CommandSender var1, String[] var2) {
      if (!(var1 instanceof Player)) {
         var1.sendMessage(ChatColor.RED + "This command is only for players.");
         return CommandResult.FAILURE;
      }

      Player var3 = (Player)var1;
      var3.setHealth(var3.getAttribute(Attribute.MAX_HEALTH).getValue());
      var3.setFoodLevel(20);
      var3.setFireTicks(0);
      var3.setSaturation(12.0F);

      for (PotionEffectType var7 : new PotionEffectType[]{
         PotionEffectType.POISON,
         PotionEffectType.BLINDNESS,
         VPotionEffectType.NAUSEA.get(),
         PotionEffectType.HUNGER,
         PotionEffectType.WEAKNESS,
         VPotionEffectType.SLOWNESS.get(),
         PotionEffectType.MINING_FATIGUE
      }) {
         var3.removePotionEffect(var7);
      }

      return CommandResult.SUCCESS;
   }
}
