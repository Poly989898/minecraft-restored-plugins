package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.data.RequiredLevelData;
import net.Indyuce.mmoitems.stat.type.RequiredLevelStat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public class RequiredLevel extends RequiredLevelStat {
   public RequiredLevel() {
      super("LEVEL", Material.EXPERIENCE_BOTTLE, "Level", new String[]{"The level your item needs", "in order to be used."});
   }

   @Override
   public boolean canUse(RPGPlayer rpgPlayer, NBTItem nbtItem, boolean flag) {
      int count = nbtItem.getInteger(this.getNBTPath());
      if (count <= 0) {
         return true;
      }

      if (rpgPlayer.getLevel() < count && !rpgPlayer.getPlayer().hasPermission("mmoitems.bypass.level")) {
         if (flag) {
            Message.NOT_ENOUGH_LEVELS.format(ChatColor.RED).send(rpgPlayer.getPlayer());
            rpgPlayer.getPlayer().playSound(rpgPlayer.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
         }

         return false;
      } else {
         return true;
      }
   }

   @NotNull
   @Override
   public RequiredLevelData getClearStatData() {
      return new RequiredLevelData(0.0);
   }
}
