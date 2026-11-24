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
   private DamageType[] mapSkillTypes(Set<SkillType> skillTypes) {
      ArrayList<DamageType> damageTypes = new ArrayList<>();

      for (SkillType skillType : skillTypes) {
         DamageType damageType = this.damages.get(skillType);
         if (damageType != null) {
            damageTypes.add(damageType);
         }
      }

      damageTypes.add(DamageType.SKILL);
      return damageTypes.toArray(new DamageType[0]);
   }

   @Nullable
   public AttackMetadata getAttack(EntityDamageEvent event) {
      SkillUseInfo spellTargetInfo = Heroes.getInstance().getDamageManager().getSpellTargetInfo(event.getEntity());
      if (spellTargetInfo != null && spellTargetInfo.getCharacter().getEntity() instanceof Player) {
         Player entity = (Player)spellTargetInfo.getCharacter().getEntity();
         DamageMetadata damageMetadata = new DamageMetadata(event.getDamage(), this.mapSkillTypes(spellTargetInfo.getSkill().getTypes()));
         return new AttackMetadata(damageMetadata, (LivingEntity)event.getEntity(), MMOPlayerData.get(entity).getStatMap().cache(EquipmentSlot.MAIN_HAND));
      } else {
         return null;
      }
   }

   @Override
   public void refreshStats(PlayerData playerData) {
      Hero hero = Heroes.getInstance().getCharacterManager().getHero(playerData.getPlayer());
      hero.removeMaxMana("MMOItems");
      hero.addMaxMana("MMOItems", (int)playerData.getStats().getStat(ItemStats.MAX_MANA));
      hero.removeMaxStamina("MMOItems");
      hero.addMaxStamina("MMOItems", (int)playerData.getStats().getStat(MAX_STAMINA));
      hero.removeMaxHealth("MMOItems");
   }

   @Override
   public RPGPlayer getInfo(PlayerData playerData) {
      return new HeroesHook.PlayerWrapper(playerData);
   }

   @EventHandler
   public void a(HeroChangeLevelEvent event) {
      PlayerData.get(event.getHero().getPlayer()).getInventory().scheduleUpdate();
   }

   @EventHandler
   public void b(ClassChangeEvent event) {
      PlayerData.get(event.getHero().getPlayer()).getInventory().scheduleUpdate();
   }

   private static class PlayerWrapper extends RPGPlayer {
      private final Hero hero = Heroes.getInstance().getCharacterManager().getHero(this.getPlayer());

      public PlayerWrapper(PlayerData playerData) {
         super(playerData);
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
      public void setMana(double value) {
         this.hero.setMana((int)value);
      }

      @Override
      public void setStamina(double value) {
         this.hero.setStamina((int)value);
      }
   }

   private static class RequiredSecondaryLevel extends RequiredLevelStat implements ItemRestriction {
      public RequiredSecondaryLevel() {
         super("SECONDARY_HERO_LEVEL", Material.EXPERIENCE_BOTTLE, "Secondary Hero Level", new String[]{"Secondary hero level requirement for your item."});
      }

      @Override
      public boolean canUse(RPGPlayer rpgPlayer, NBTItem nbtItem, boolean flag) {
         int count = nbtItem.getInteger(this.getNBTPath());
         if (count <= 0) {
            return true;
         }

         Validate.isTrue(rpgPlayer instanceof HeroesHook.PlayerWrapper, "Cannot use secondary hero level requirement while Heroes is not the RPG provider");
         Hero hero = ((HeroesHook.PlayerWrapper)rpgPlayer).hero;
         HeroClass secondaryClass = hero.getSecondaryClass();
         int secondaryLevel = secondaryClass == null ? 1 : hero.getHeroLevel(hero.getSecondaryClass());
         if (secondaryLevel < count && !rpgPlayer.getPlayer().hasPermission("mmoitems.bypass.level")) {
            if (flag) {
               Message.NOT_ENOUGH_LEVELS.format(ChatColor.RED).send(rpgPlayer.getPlayer());
               rpgPlayer.getPlayer().playSound(rpgPlayer.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
            }

            return false;
         } else {
            return true;
         }
      }
   }
}
