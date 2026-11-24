package io.lumine.mythic.lib.script.mechanic.buff.stat;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import io.lumine.mythic.lib.api.stat.modifier.TemporaryStatModifier;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.script.mechanic.type.TargetMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AddStatModifierMechanic extends TargetMechanic {
   private final DoubleFormula amount;
   private final DoubleFormula lifetime;
   private final String stat;
   private final String key;
   private final boolean relative;

   @MechanicMetadata
   public AddStatModifierMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("stat", "amount", "key");
      this.stat = config.getString("stat");
      this.key = config.getString("key");
      this.lifetime = config.contains("time") ? new DoubleFormula(config.getString("time")) : DoubleFormula.ZERO;
      this.relative = config.getBoolean("relative", false);
      this.amount = new DoubleFormula(config.getString("amount"));
   }

   @Override
   public void cast(SkillMetadata meta, Entity target) {
      Validate.isTrue(target instanceof Player, "Can only give temporary stats to players");
      MMOPlayerData playerData = MMOPlayerData.get((OfflinePlayer)target);
      long lifetime = Math.max(0L, (long)this.lifetime.evaluate(meta));
      if (lifetime > 0L) {
         new TemporaryStatModifier(
               this.key,
               this.stat,
               this.amount.evaluate(meta),
               this.relative ? ModifierType.RELATIVE : ModifierType.FLAT,
               EquipmentSlot.OTHER,
               ModifierSource.OTHER
            )
            .register(playerData, lifetime);
      } else {
         new StatModifier(
               this.key,
               this.stat,
               this.amount.evaluate(meta),
               this.relative ? ModifierType.RELATIVE : ModifierType.FLAT,
               EquipmentSlot.OTHER,
               ModifierSource.OTHER
            )
            .register(playerData);
      }
   }
}
