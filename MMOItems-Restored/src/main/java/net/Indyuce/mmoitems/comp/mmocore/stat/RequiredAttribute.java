package net.Indyuce.mmoitems.comp.mmocore.stat;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.type.RequiredLevelStat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;

public class RequiredAttribute extends RequiredLevelStat {
   private final PlayerAttribute attribute;

   public RequiredAttribute(PlayerAttribute playerAttribute) {
      super(
         UtilityMethods.enumName(playerAttribute.getId()),
         Material.GRAY_DYE,
         playerAttribute.getName() + " (MMOCore)",
         new String[]{"Amount of " + playerAttribute.getName() + " points the", "player needs to use the item."}
      );
      this.attribute = playerAttribute;
   }

   @Override
   public boolean canUse(RPGPlayer rpgPlayer, NBTItem nbtItem, boolean flag) {
      int count = nbtItem.getInteger(this.getNBTPath());
      if (count <= 0) {
         return true;
      }

      PlayerData playerData = PlayerData.get(rpgPlayer.getPlayer());
      if (playerData.getAttributes().getAttribute(this.attribute) >= count) {
         return true;
      }

      if (flag) {
         Message.NOT_ENOUGH_ATTRIBUTE.format(ChatColor.RED, "#attribute#", this.attribute.getName()).send(rpgPlayer.getPlayer());
         rpgPlayer.getPlayer().playSound(rpgPlayer.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
      }

      return false;
   }
}
