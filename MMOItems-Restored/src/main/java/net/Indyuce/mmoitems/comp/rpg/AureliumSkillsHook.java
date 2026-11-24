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

      for (Stats stats : Stats.values()) {
         String text = UtilityMethods.caseOnWords(stats.name().toLowerCase());
         DoubleStat doubleStat = new DoubleStat(
            "ADDITIONAL_" + stats.name(),
            Material.BOOK,
            "Additional " + text,
            new String[]{"Additional " + text + " (AureliumSkills)"},
            new String[]{"!miscellaneous", "!block", "all"}
         );
         this.statExtra.put(stats, doubleStat);
         MMOItems.plugin.getStats().register(doubleStat);
      }

      for (Skills skills : Skills.values()) {
         MMOItems.plugin.getStats().register(new AureliumSkillsHook.RequiredProfessionStat(skills));
      }
   }

   @EventHandler
   public void a(SkillLevelUpEvent event) {
      Player player = event.getPlayer();
      if (player.isOnline()) {
         PlayerData.get(player).getInventory().scheduleUpdate();
      }
   }

   @Override
   public void refreshStats(PlayerData playerData) {
      this.statExtra
         .forEach(
            (var1x, itemStat) -> AureliumAPI.addStatModifier(playerData.getPlayer(), "mmoitems_" + var1x.name(), var1x, playerData.getStats().getStat((ItemStat<?, ?>)itemStat))
         );
   }

   @Override
   public RPGPlayer getInfo(PlayerData playerData) {
      return new EmptyRPGPlayer(playerData);
   }

   @EventHandler
   public void a(PlayerDataLoadEvent event) {
      Player player = event.getPlayerData().getPlayer();
      PlayerData playerData = PlayerData.get(player);
      playerData.setRPGPlayer(new AureliumSkillsHook.PlayerWrapper(playerData, event.getPlayerData()));
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
      public void setMana(double value) {
         this.info.setMana(value);
      }

      @Override
      public void setStamina(double value) {
         this.getPlayer().setFoodLevel((int)value);
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
      public boolean canUse(RPGPlayer rpgPlayer, NBTItem nbtItem, boolean flag) {
         int count = nbtItem.getInteger(this.getNBTPath());
         if (count <= 0) {
            return true;
         }

         int skillLevel = AureliumAPI.getSkillLevel(rpgPlayer.getPlayer(), this.skill);
         if (skillLevel < count && !rpgPlayer.getPlayer().hasPermission("mmoitems.bypass.level")) {
            if (flag) {
               Message.NOT_ENOUGH_PROFESSION.format(ChatColor.RED, "#profession#", this.skill.getDisplayName(Locale.getDefault())).send(rpgPlayer.getPlayer());
               rpgPlayer.getPlayer().playSound(rpgPlayer.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
            }

            return false;
         } else {
            return true;
         }
      }
   }
}
