package net.Indyuce.mmoitems.comp.mmocore.stat;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.Profession;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.type.RequiredLevelStat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;

public class RequiredProfession extends RequiredLevelStat {
   private final Profession profession;

   public RequiredProfession(Profession profession) {
      super(
         true,
         "PROFESSION_" + UtilityMethods.enumName(profession.getId()),
         Material.PINK_DYE,
         profession.getName() + " Level (MMOCore)",
         new String[]{"Amount of " + profession.getName() + " levels the", "player needs to use the item."}
      );
      this.profession = profession;
   }

   @Override
   public boolean canUse(RPGPlayer rpgPlayer, NBTItem nbtItem, boolean flag) {
      int count = nbtItem.getInteger(this.getNBTPath());
      if (count <= 0) {
         return true;
      }

      PlayerData playerData = PlayerData.get(rpgPlayer.getPlayer());
      if (playerData.getCollectionSkills().getLevel(this.profession) >= count) {
         return true;
      }

      if (flag) {
         Message.NOT_ENOUGH_PROFESSION.format(ChatColor.RED, "#profession#", this.profession.getName()).send(rpgPlayer.getPlayer());
         rpgPlayer.getPlayer().playSound(rpgPlayer.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
      }

      return false;
   }
}
