package net.Indyuce.mmoitems.comp.rpg;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.api.exceptions.McMMOPlayerNotFoundException;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelDownEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.stat.type.DisableStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class McMMOHook implements RPGHandler, Listener {
   public static final ItemStat disableMcMMORepair = new DisableStat(
      "MCMMO_REPAIR", Material.IRON_BLOCK, "Disable McMMO Repair", "Players can't repair this with McMMO."
   );

   @EventHandler(ignoreCancelled = true)
   public void a(McMMOPlayerLevelUpEvent event) {
      PlayerData.get(event.getPlayer()).getInventory().scheduleUpdate();
   }

   @EventHandler(ignoreCancelled = true)
   public void b(McMMOPlayerLevelDownEvent event) {
      PlayerData.get(event.getPlayer()).getInventory().scheduleUpdate();
   }

   @Override
   public RPGPlayer getInfo(PlayerData playerData) {
      return new McMMOHook.McMMOPlayer(playerData);
   }

   @Override
   public void refreshStats(PlayerData playerData) {
   }

   public static class McMMOPlayer extends RPGPlayer {
      public McMMOPlayer(PlayerData playerData) {
         super(playerData);
      }

      @Override
      public int getLevel() {
         try {
            return ExperienceAPI.getPowerLevel(this.getPlayer());
         } catch (McMMOPlayerNotFoundException exception) {
            return 0;
         }
      }

      @Override
      public String getClassName() {
         return "";
      }

      @Override
      public double getMana() {
         return this.getPlayer().getFoodLevel();
      }

      @Override
      public double getStamina() {
         return 0.0;
      }

      @Override
      public void setMana(double value) {
         this.getPlayer().setFoodLevel((int)value);
      }

      @Override
      public void setStamina(double value) {
      }
   }
}
