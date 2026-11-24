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

   public RequiredProfession(Profession var1) {
      super(
         true,
         "PROFESSION_" + UtilityMethods.enumName(var1.getId()),
         Material.PINK_DYE,
         var1.getName() + " Level (MMOCore)",
         new String[]{"Amount of " + var1.getName() + " levels the", "player needs to use the item."}
      );
      this.profession = var1;
   }

   @Override
   public boolean canUse(RPGPlayer var1, NBTItem var2, boolean var3) {
      int var4 = var2.getInteger(this.getNBTPath());
      if (var4 <= 0) {
         return true;
      }

      PlayerData var5 = PlayerData.get(var1.getPlayer());
      if (var5.getCollectionSkills().getLevel(this.profession) >= var4) {
         return true;
      }

      if (var3) {
         Message.NOT_ENOUGH_PROFESSION.format(ChatColor.RED, "#profession#", this.profession.getName()).send(var1.getPlayer());
         var1.getPlayer().playSound(var1.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
      }

      return false;
   }
}
