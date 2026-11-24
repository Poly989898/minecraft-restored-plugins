package net.Indyuce.mmoitems.api.interaction.weapon;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.comp.flags.CustomFlag;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.skill.SimpleSkill;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.result.SkillResult;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import javax.annotation.Nullable;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.event.item.UntargetedWeaponUseEvent;
import net.Indyuce.mmoitems.api.interaction.UseItem;
import net.Indyuce.mmoitems.api.interaction.WeaponAttackResult;
import net.Indyuce.mmoitems.api.interaction.util.UntargetedDurabilityItem;
import net.Indyuce.mmoitems.api.interaction.weapon.untargeted.LegacyWeapon;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Weapon extends UseItem {
   @Deprecated
   public Weapon(Player var1, NBTItem var2) {
      this(PlayerData.get(var1), var2);
   }

   public Weapon(PlayerData var1, NBTItem var2) {
      super(var1, var2);
   }

   @Override
   public boolean checkItemRequirements() {
      if (this.playerData.isEncumbered()) {
         Message.HANDS_TOO_CHARGED.format(ChatColor.RED).send(this.getPlayer());
         return false;
      } else {
         return this.playerData.getRPG().canUse(this.getNBTItem(), true) && MythicLib.plugin.getFlags().isFlagAllowed(this.getPlayer(), this.getUseFlag());
      }
   }

   public CustomFlag getUseFlag() {
      return CustomFlag.MI_WEAPONS;
   }

   public boolean checkAndApplyWeaponCosts() {
      if (!this.checkWeaponCosts(false)) {
         return false;
      }

      this.applyWeaponCosts(null);
      return true;
   }

   public boolean checkWeaponCosts(boolean var1) {
      if (var1 && this.getPlayerData().getMMOPlayerData().getCooldownMap().isOnCooldown(this.mmoitem.getType())) {
         return false;
      } else {
         double var2 = this.getNBTItem().getStat("MANA_COST");
         if (var2 > 0.0 && this.playerData.getRPG().getMana() < var2) {
            Message.NOT_ENOUGH_MANA.format(ChatColor.RED).send(this.getPlayer());
            return false;
         } else {
            double var4 = this.getNBTItem().getStat("STAMINA_COST");
            if (var4 > 0.0 && this.playerData.getRPG().getStamina() < var4) {
               Message.NOT_ENOUGH_STAMINA.format(ChatColor.RED).send(this.getPlayer());
               return false;
            } else {
               return true;
            }
         }
      }
   }

   public void applyWeaponCosts(@Nullable Double var1) {
      double var2 = this.getNBTItem().getStat("MANA_COST");
      if (var2 > 0.0) {
         this.playerData.getRPG().giveMana(-var2);
      }

      double var4 = this.getNBTItem().getStat("STAMINA_COST");
      if (var4 > 0.0) {
         this.playerData.getRPG().giveStamina(-var4);
      }

      if (var1 != null) {
         this.getPlayerData().getMMOPlayerData().getCooldownMap().applyCooldown(this.mmoitem.getType(), var1);
      }
   }

   public boolean handleTargetedAttack(AttackMetadata var1, @NotNull PlayerMetadata var2, LivingEntity var3) {
      if (!this.checkAndApplyWeaponCosts()) {
         return false;
      }

      SkillHandler var4 = this.mmoitem.getType().onAttack();
      if (var4 != null && !this.getNBTItem().getBoolean("MMOITEMS_DISABLE_ATTACK_PASSIVE")) {
         new SimpleSkill(var4).cast(new TriggerMetadata(var2, TriggerType.API, var3, var1));
      }

      return true;
   }

   @Nullable
   private SkillHandler findClickSkill(boolean var1) {
      String var2 = this.getNBTItem().getString((var1 ? ItemStats.RIGHT_CLICK_SCRIPT : ItemStats.LEFT_CLICK_SCRIPT).getNBTPath());
      if (!var1 && (var2 == null || var2.isEmpty())) {
         var2 = this.getNBTItem().getString("MMOITEMS_STAFF_SPIRIT");
      }

      if (var2 != null && !var2.isEmpty()) {
         return MythicLib.plugin.getSkills().getHandler(var2);
      }

      Type var3 = this.mmoitem.getType();
      return var1 ? var3.onRightClick() : var3.onLeftClick();
   }

   @Nullable
   public WeaponAttackResult handleUntargetedAttack(boolean var1, @NotNull EquipmentSlot var2) {
      if (this instanceof LegacyWeapon) {
         return this.legacyHandleUntargetedAttack(var1, var2);
      }

      SkillHandler var3 = this.findClickSkill(var1);
      if (var3 == null) {
         return WeaponAttackResult.NO_ATTACK;
      }

      SkillMetadata var4 = new TriggerMetadata(this.getPlayerData().getMMOPlayerData(), TriggerType.API).toSkillMetadata(new SimpleSkill(var3));
      SkillResult var5 = var3.getResult(var4);
      if (!var5.isSuccessful()) {
         return WeaponAttackResult.NO_ATTACK;
      }

      UntargetedDurabilityItem var6 = new UntargetedDurabilityItem(this.getPlayer(), this.getNBTItem(), var2);
      if (var6.isBroken()) {
         return WeaponAttackResult.DURABILITY;
      }

      PlayerMetadata var7 = this.getPlayerData().getStats().newTemporary(var2);
      if (!this.checkWeaponCosts(true)) {
         return WeaponAttackResult.WEAPON_COSTS;
      }

      UntargetedWeaponUseEvent var8 = new UntargetedWeaponUseEvent(this.playerData, this);
      Bukkit.getPluginManager().callEvent(var8);
      if (var8.isCancelled()) {
         return WeaponAttackResult.BUKKIT_EVENT;
      }

      double var9 = 1.0 / this.requireNonZero(var7.getStat("ATTACK_SPEED"), MMOItems.plugin.getConfig().getDouble("default.attack-speed"));
      this.applyWeaponCosts(var9);
      var3.whenCast(var5, var4);
      if (var6.isValid()) {
         var6.decreaseDurability(1).inventoryUpdate();
      }

      return WeaponAttackResult.SUCCESS;
   }

   @Deprecated
   private WeaponAttackResult legacyHandleUntargetedAttack(boolean var1, @NotNull EquipmentSlot var2) {
      if (!((LegacyWeapon)this).canAttack(var1, var2)) {
         return WeaponAttackResult.NO_ATTACK;
      }

      UntargetedDurabilityItem var3 = new UntargetedDurabilityItem(this.getPlayer(), this.getNBTItem(), var2);
      if (var3.isBroken()) {
         return WeaponAttackResult.DURABILITY;
      }

      PlayerMetadata var4 = this.getPlayerData().getStats().newTemporary(var2);
      if (!this.checkWeaponCosts(true)) {
         return WeaponAttackResult.WEAPON_COSTS;
      }

      UntargetedWeaponUseEvent var5 = new UntargetedWeaponUseEvent(this.playerData, this);
      Bukkit.getPluginManager().callEvent(var5);
      if (var5.isCancelled()) {
         return WeaponAttackResult.BUKKIT_EVENT;
      }

      double var6 = 1.0 / this.requireNonZero(var4.getStat("ATTACK_SPEED"), MMOItems.plugin.getConfig().getDouble("default.attack-speed"));
      this.applyWeaponCosts(var6);
      PlayerMetadata var8 = this.playerData.getMMOPlayerData().getStatMap().cache(var2);
      ((LegacyWeapon)this).applyAttackEffect(var8, var2);
      if (var3.isValid()) {
         var3.decreaseDurability(1).inventoryUpdate();
      }

      return WeaponAttackResult.SUCCESS;
   }

   @Deprecated
   protected Location getGround(Location var1) {
      for (int var2 = 0; var2 < 20; var2++) {
         if (var1.getBlock().getType().isSolid()) {
            return var1;
         }

         var1.add(0.0, -1.0, 0.0);
      }

      return var1;
   }

   protected double requireNonZero(double var1, double var3) {
      return var1 <= 0.0 ? var3 : var1;
   }
}
