package net.Indyuce.mmoitems.comp.rpg;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.api.event.SkillLevelUpEvent;
import com.archyx.aureliumskills.data.PlayerDataLoadEvent;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.stats.Stats;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.player.EmptyRPGPlayer;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.RequiredLevelStat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Deprecated
public class AureliumSkillsHook implements RPGHandler, Listener {
   private final AureliumSkills aSkills;
   private final Map<Stats, ItemStat> statExtra = new HashMap<>();
   private static final String MODIFIER_KEY_PREFIX = "mmoitems_";

   public AureliumSkillsHook() {
      this.aSkills = (AureliumSkills)Bukkit.getPluginManager().getPlugin("AureliumSkills");

      for (Stats var4 : Stats.values()) {
         String var5 = UtilityMethods.caseOnWords(var4.name().toLowerCase());
         DoubleStat var6 = new DoubleStat(
            "ADDITIONAL_" + var4.name(),
            Material.BOOK,
            "Additional " + var5,
            new String[]{"Additional " + var5 + " (AureliumSkills)"},
            new String[]{"!miscellaneous", "!block", "all"}
         );
         this.statExtra.put(var4, var6);
         MMOItems.plugin.getStats().register(var6);
      }

      for (Skills var10 : Skills.values()) {
         MMOItems.plugin.getStats().register(new AureliumSkillsHook.RequiredProfessionStat(var10));
      }
   }

   @EventHandler
   public void a(SkillLevelUpEvent var1) {
      Player var2 = var1.getPlayer();
      if (var2.isOnline()) {
         PlayerData.get(var2).getInventory().scheduleUpdate();
      }
   }

   @Override
   public void refreshStats(PlayerData var1) {
      this.statExtra
         .forEach(
            (var1x, var2) -> AureliumAPI.addStatModifier(var1.getPlayer(), "mmoitems_" + var1x.name(), var1x, var1.getStats().getStat((ItemStat<?, ?>)var2))
         );
   }

   @Override
   public RPGPlayer getInfo(PlayerData var1) {
      return new EmptyRPGPlayer(var1);
   }

   @EventHandler
   public void a(PlayerDataLoadEvent var1) {
      Player var2 = var1.getPlayerData().getPlayer();
      PlayerData var3 = PlayerData.get(var2);
      var3.setRPGPlayer(new AureliumSkillsHook.PlayerWrapper(var3, var1.getPlayerData()));
   }

   public class PlayerWrapper extends RPGPlayer {
      private final com.archyx.aureliumskills.data.PlayerData info;

      public PlayerWrapper(PlayerData nullx, com.archyx.aureliumskills.data.PlayerData nullxx) {
         super(nullx);
         this.info = nullxx;
      }

      public com.archyx.aureliumskills.data.PlayerData getAureliumSkillsPlayerData() {
         return this.info;
      }

      @Override
      public int getLevel() {
         return this.info.getPowerLevel();
      }

      @Override
      public String getClassName() {
         return "";
      }

      @Override
      public double getMana() {
         return this.info.getMana();
      }

      @Override
      public double getStamina() {
         return this.getPlayer().getFoodLevel();
      }

      @Override
      public void setMana(double var1) {
         this.info.setMana(var1);
      }

      @Override
      public void setStamina(double var1) {
         this.getPlayer().setFoodLevel((int)var1);
      }
   }

   public class RequiredProfessionStat extends RequiredLevelStat {
      private final Skill skill;

      public RequiredProfessionStat(Skills nullx) {
         super(
            nullx.name(),
            Material.EXPERIENCE_BOTTLE,
            nullx.getDisplayName(Locale.getDefault()),
            new String[]{"Amount of " + nullx.getDisplayName(Locale.getDefault()) + " levels the", "player needs to use the item.", "(AureliumSkills)"}
         );
         this.skill = AureliumSkillsHook.this.aSkills.getSkillRegistry().getSkill(nullx.name());
      }

      @Override
      public boolean canUse(RPGPlayer var1, NBTItem var2, boolean var3) {
         int var4 = var2.getInteger(this.getNBTPath());
         if (var4 <= 0) {
            return true;
         }

         int var5 = AureliumAPI.getSkillLevel(var1.getPlayer(), this.skill);
         if (var5 < var4 && !var1.getPlayer().hasPermission("mmoitems.bypass.level")) {
            if (var3) {
               Message.NOT_ENOUGH_PROFESSION.format(ChatColor.RED, "#profession#", this.skill.getDisplayName(Locale.getDefault())).send(var1.getPlayer());
               var1.getPlayer().playSound(var1.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
            }

            return false;
         } else {
            return true;
         }
      }
   }
}
