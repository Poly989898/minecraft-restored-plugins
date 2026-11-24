package net.Indyuce.mmoitems.comp.rpg;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.SkillUseInfo;
import com.herocraftonline.heroes.api.events.ClassChangeEvent;
import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import com.herocraftonline.heroes.characters.skill.SkillType;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.damage.AttackHandler;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemRestriction;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.RequiredLevelStat;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HeroesHook implements RPGHandler, Listener, AttackHandler {
   private final Map<SkillType, DamageType> damages = new HashMap<>();
   public static final ItemStat MAX_STAMINA = new DoubleStat(
      "MAX_STAMINA", Material.EMERALD, "Max Stamina", new String[]{"Adds stamina to your max stamina bar"}
   );
   public static final ItemStat REQUIRED_SECONDARY_HERO_LEVEL = new HeroesHook.RequiredSecondaryLevel();

   public HeroesHook() {
      MythicLib.plugin.getDamage().registerHandler(this);
      this.damages.put(SkillType.ABILITY_PROPERTY_PHYSICAL, DamageType.PHYSICAL);
      this.damages.put(SkillType.ABILITY_PROPERTY_MAGICAL, DamageType.MAGIC);
      this.damages.put(SkillType.ABILITY_PROPERTY_PROJECTILE, DamageType.PROJECTILE);
   }

   @NotNull
   private DamageType[] mapSkillTypes(Set<SkillType> var1) {
      ArrayList<DamageType> var2 = new ArrayList<>();

      for (SkillType var4 : var1) {
         DamageType var5 = this.damages.get(var4);
         if (var5 != null) {
            var2.add(var5);
         }
      }

      var2.add(DamageType.SKILL);
      return var2.toArray(new DamageType[0]);
   }

   @Nullable
   public AttackMetadata getAttack(EntityDamageEvent var1) {
      SkillUseInfo var2 = Heroes.getInstance().getDamageManager().getSpellTargetInfo(var1.getEntity());
      if (var2 != null && var2.getCharacter().getEntity() instanceof Player) {
         Player var3 = (Player)var2.getCharacter().getEntity();
         DamageMetadata var4 = new DamageMetadata(var1.getDamage(), this.mapSkillTypes(var2.getSkill().getTypes()));
         return new AttackMetadata(var4, (LivingEntity)var1.getEntity(), MMOPlayerData.get(var3).getStatMap().cache(EquipmentSlot.MAIN_HAND));
      } else {
         return null;
      }
   }

   @Override
   public void refreshStats(PlayerData var1) {
      Hero var2 = Heroes.getInstance().getCharacterManager().getHero(var1.getPlayer());
      var2.removeMaxMana("MMOItems");
      var2.addMaxMana("MMOItems", (int)var1.getStats().getStat(ItemStats.MAX_MANA));
      var2.removeMaxStamina("MMOItems");
      var2.addMaxStamina("MMOItems", (int)var1.getStats().getStat(MAX_STAMINA));
      var2.removeMaxHealth("MMOItems");
   }

   @Override
   public RPGPlayer getInfo(PlayerData var1) {
      return new HeroesHook.PlayerWrapper(var1);
   }

   @EventHandler
   public void a(HeroChangeLevelEvent var1) {
      PlayerData.get(var1.getHero().getPlayer()).getInventory().scheduleUpdate();
   }

   @EventHandler
   public void b(ClassChangeEvent var1) {
      PlayerData.get(var1.getHero().getPlayer()).getInventory().scheduleUpdate();
   }

   private static class PlayerWrapper extends RPGPlayer {
      private final Hero hero = Heroes.getInstance().getCharacterManager().getHero(this.getPlayer());

      public PlayerWrapper(PlayerData var1) {
         super(var1);
      }

      @Override
      public int getLevel() {
         return this.hero.getHeroLevel(this.hero.getHeroClass());
      }

      @Override
      public String getClassName() {
         return this.hero.getHeroClass().getName();
      }

      @Override
      public double getMana() {
         return this.hero.getMana();
      }

      @Override
      public double getStamina() {
         return this.hero.getStamina();
      }

      @Override
      public void setMana(double var1) {
         this.hero.setMana((int)var1);
      }

      @Override
      public void setStamina(double var1) {
         this.hero.setStamina((int)var1);
      }
   }

   private static class RequiredSecondaryLevel extends RequiredLevelStat implements ItemRestriction {
      public RequiredSecondaryLevel() {
         super("SECONDARY_HERO_LEVEL", Material.EXPERIENCE_BOTTLE, "Secondary Hero Level", new String[]{"Secondary hero level requirement for your item."});
      }

      @Override
      public boolean canUse(RPGPlayer var1, NBTItem var2, boolean var3) {
         int var4 = var2.getInteger(this.getNBTPath());
         if (var4 <= 0) {
            return true;
         }

         Validate.isTrue(var1 instanceof HeroesHook.PlayerWrapper, "Cannot use secondary hero level requirement while Heroes is not the RPG provider");
         Hero var5 = ((HeroesHook.PlayerWrapper)var1).hero;
         HeroClass var6 = var5.getSecondaryClass();
         int var7 = var6 == null ? 1 : var5.getHeroLevel(var5.getSecondaryClass());
         if (var7 < var4 && !var1.getPlayer().hasPermission("mmoitems.bypass.level")) {
            if (var3) {
               Message.NOT_ENOUGH_LEVELS.format(ChatColor.RED).send(var1.getPlayer());
               var1.getPlayer().playSound(var1.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
            }

            return false;
         } else {
            return true;
         }
      }
   }
}
