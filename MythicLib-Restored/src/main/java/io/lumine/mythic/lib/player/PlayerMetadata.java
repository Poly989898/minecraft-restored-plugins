package io.lumine.mythic.lib.player;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatInstance;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.api.stat.provider.PlayerStatProvider;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerMetadata implements PlayerStatProvider {
   private final Player player;
   private final MMOPlayerData playerData;
   private final Map<String, Double> playerStats;
   private final EquipmentSlot actionHand;

   public PlayerMetadata(PlayerMetadata parent) {
      Validate.notNull(parent, "Parent cannot be null");
      this.player = parent.player;
      this.playerData = parent.playerData;
      this.playerStats = parent.playerStats;
      this.actionHand = parent.actionHand;
   }

   public PlayerMetadata(StatMap statMap, @NotNull EquipmentSlot actionHand) {
      this.player = statMap.getData().getPlayer();
      this.playerData = statMap.getData();
      this.playerStats = new HashMap<>();
      this.actionHand = actionHand;
      Validate.isTrue(Objects.requireNonNull(actionHand).isHand(), "Equipment slot must be a hand");

      for (StatInstance ins : statMap.getInstances()) {
         this.playerStats.put(ins.getStat(), ins.getFilteredTotal(actionHand::isCompatible));
      }
   }

   @NotNull
   @Override
   public MMOPlayerData getData() {
      return this.playerData;
   }

   @NotNull
   public EquipmentSlot getActionHand() {
      return this.actionHand;
   }

   @Override
   public double getStat(String stat) {
      return this.playerStats.getOrDefault(stat, this.playerData.getStatMap().getInstance(stat).getBase());
   }

   public void setStat(String stat, double value) {
      this.playerStats.put(stat, value);
   }

   @NotNull
   public AttackMetadata attack(LivingEntity target, double damage, DamageType... types) {
      return this.attack(target, damage, true, types);
   }

   @NotNull
   public AttackMetadata attack(LivingEntity target, double damage, boolean knockback, DamageType... types) {
      AttackMetadata opt = MythicLib.plugin.getDamage().getRegisteredAttackMetadata(target);
      if (opt != null) {
         opt.getDamage().add(damage, types);
         return opt;
      } else {
         AttackMetadata attackMeta = new AttackMetadata(new DamageMetadata(damage, types), target, this);
         MythicLib.plugin.getDamage().registerAttack(attackMeta, knockback, false);
         return attackMeta;
      }
   }

   public void triggerSkills(TriggerType triggerType, @Nullable Entity entity, @Nullable AttackMetadata attack) {
      this.playerData.triggerSkills(new TriggerMetadata(this, triggerType, entity, attack));
   }

   @NotNull
   @Override
   public PlayerMetadata cache(@NotNull EquipmentSlot castHand) {
      return this;
   }
}
