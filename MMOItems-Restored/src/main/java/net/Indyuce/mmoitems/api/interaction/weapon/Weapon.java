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
   public Weapon(Player player, NBTItem nbtItem) {
      this(PlayerData.get(player), nbtItem);
   }

   public Weapon(PlayerData playerData, NBTItem nbtItem) {
      super(playerData, nbtItem);
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

   public boolean checkWeaponCosts(boolean flag) {
      if (flag && this.getPlayerData().getMMOPlayerData().getCooldownMap().isOnCooldown(this.mmoitem.getType())) {
         return false;
      } else {
         double stat = this.getNBTItem().getStat("MANA_COST");
         if (stat > 0.0 && this.playerData.getRPG().getMana() < stat) {
            Message.NOT_ENOUGH_MANA.format(ChatColor.RED).send(this.getPlayer());
            return false;
         } else {
            double staminaCost = this.getNBTItem().getStat("STAMINA_COST");
            if (staminaCost > 0.0 && this.playerData.getRPG().getStamina() < staminaCost) {
               Message.NOT_ENOUGH_STAMINA.format(ChatColor.RED).send(this.getPlayer());
               return false;
            } else {
               return true;
            }
         }
      }
   }

   public void applyWeaponCosts(@Nullable Double value) {
      double stat = this.getNBTItem().getStat("MANA_COST");
      if (stat > 0.0) {
         this.playerData.getRPG().giveMana(-stat);
      }

      double staminaCost = this.getNBTItem().getStat("STAMINA_COST");
      if (staminaCost > 0.0) {
         this.playerData.getRPG().giveStamina(-staminaCost);
      }

      if (value != null) {
         this.getPlayerData().getMMOPlayerData().getCooldownMap().applyCooldown(this.mmoitem.getType(), value);
      }
   }

   public boolean handleTargetedAttack(AttackMetadata attackMetadata, @NotNull PlayerMetadata playerMetadata, LivingEntity entity) {
      if (!this.checkAndApplyWeaponCosts()) {
         return false;
      }

      SkillHandler skillHandler = this.mmoitem.getType().onAttack();
      if (skillHandler != null && !this.getNBTItem().getBoolean("MMOITEMS_DISABLE_ATTACK_PASSIVE")) {
         new SimpleSkill(skillHandler).cast(new TriggerMetadata(playerMetadata, TriggerType.API, entity, attackMetadata));
      }

      return true;
   }

   @Nullable
   private SkillHandler findClickSkill(boolean flag) {
      String text = this.getNBTItem().getString((flag ? ItemStats.RIGHT_CLICK_SCRIPT : ItemStats.LEFT_CLICK_SCRIPT).getNBTPath());
      if (!flag && (text == null || text.isEmpty())) {
         text = this.getNBTItem().getString("MMOITEMS_STAFF_SPIRIT");
      }

      if (text != null && !text.isEmpty()) {
         return MythicLib.plugin.getSkills().getHandler(text);
      }

      Type type = this.mmoitem.getType();
      return flag ? type.onRightClick() : type.onLeftClick();
   }

   @Nullable
   public WeaponAttackResult handleUntargetedAttack(boolean flag, @NotNull EquipmentSlot equipmentSlot) {
      if (this instanceof LegacyWeapon) {
         return this.legacyHandleUntargetedAttack(flag, equipmentSlot);
      }

      SkillHandler clickSkill = this.findClickSkill(flag);
      if (clickSkill == null) {
         return WeaponAttackResult.NO_ATTACK;
      }

      SkillMetadata skillMetadata = new TriggerMetadata(this.getPlayerData().getMMOPlayerData(), TriggerType.API).toSkillMetadata(new SimpleSkill(clickSkill));
      SkillResult result = clickSkill.getResult(skillMetadata);
      if (!result.isSuccessful()) {
         return WeaponAttackResult.NO_ATTACK;
      }

      UntargetedDurabilityItem untargetedDurabilityItem = new UntargetedDurabilityItem(this.getPlayer(), this.getNBTItem(), equipmentSlot);
      if (untargetedDurabilityItem.isBroken()) {
         return WeaponAttackResult.DURABILITY;
      }

      PlayerMetadata temporary = this.getPlayerData().getStats().newTemporary(equipmentSlot);
      if (!this.checkWeaponCosts(true)) {
         return WeaponAttackResult.WEAPON_COSTS;
      }

      UntargetedWeaponUseEvent event = new UntargetedWeaponUseEvent(this.playerData, this);
      Bukkit.getPluginManager().callEvent(event);
      if (event.isCancelled()) {
         return WeaponAttackResult.BUKKIT_EVENT;
      }

      double value = 1.0 / this.requireNonZero(temporary.getStat("ATTACK_SPEED"), MMOItems.plugin.getConfig().getDouble("default.attack-speed"));
      this.applyWeaponCosts(value);
      clickSkill.whenCast(result, skillMetadata);
      if (untargetedDurabilityItem.isValid()) {
         untargetedDurabilityItem.decreaseDurability(1).inventoryUpdate();
      }

      return WeaponAttackResult.SUCCESS;
   }

   @Deprecated
   private WeaponAttackResult legacyHandleUntargetedAttack(boolean flag, @NotNull EquipmentSlot equipmentSlot) {
      if (!((LegacyWeapon)this).canAttack(flag, equipmentSlot)) {
         return WeaponAttackResult.NO_ATTACK;
      }

      UntargetedDurabilityItem untargetedDurabilityItem = new UntargetedDurabilityItem(this.getPlayer(), this.getNBTItem(), equipmentSlot);
      if (untargetedDurabilityItem.isBroken()) {
         return WeaponAttackResult.DURABILITY;
      }

      PlayerMetadata temporary = this.getPlayerData().getStats().newTemporary(equipmentSlot);
      if (!this.checkWeaponCosts(true)) {
         return WeaponAttackResult.WEAPON_COSTS;
      }

      UntargetedWeaponUseEvent event = new UntargetedWeaponUseEvent(this.playerData, this);
      Bukkit.getPluginManager().callEvent(event);
      if (event.isCancelled()) {
         return WeaponAttackResult.BUKKIT_EVENT;
      }

      double value = 1.0 / this.requireNonZero(temporary.getStat("ATTACK_SPEED"), MMOItems.plugin.getConfig().getDouble("default.attack-speed"));
      this.applyWeaponCosts(value);
      PlayerMetadata playerMetadata = this.playerData.getMMOPlayerData().getStatMap().cache(equipmentSlot);
      ((LegacyWeapon)this).applyAttackEffect(playerMetadata, equipmentSlot);
      if (untargetedDurabilityItem.isValid()) {
         untargetedDurabilityItem.decreaseDurability(1).inventoryUpdate();
      }

      return WeaponAttackResult.SUCCESS;
   }

   @Deprecated
   protected Location getGround(Location location) {
      for (int i = 0; i < 20; i++) {
         if (location.getBlock().getType().isSolid()) {
            return location;
         }

         location.add(0.0, -1.0, 0.0);
      }

      return location;
   }

   protected double requireNonZero(double value, double value2) {
      return value <= 0.0 ? value2 : value;
   }
}
