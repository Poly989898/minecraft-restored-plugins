package io.lumine.mythic.lib.script.mechanic.projectile;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.entity.ProjectileMetadata;
import io.lumine.mythic.lib.entity.ProjectileType;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.skill.PassiveSkill;
import io.lumine.mythic.lib.script.Script;
import io.lumine.mythic.lib.script.mechanic.type.DirectionMechanic;
import io.lumine.mythic.lib.skill.SimpleSkill;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.MythicLibSkillHandler;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShootArrowMechanic extends DirectionMechanic {
   private final boolean fromItem;
   private final boolean playerAttackDamage;
   private final DoubleFormula velocity;
   @Nullable
   private final Script onHit;
   @Nullable
   private final Script onLand;
   @Nullable
   private final Script onTick;
   private static final String PASSIVE_SKILL_KEY = "api";

   public ShootArrowMechanic(ConfigObject config) {
      super(config);
      this.fromItem = config.getBoolean("from_item", false);
      this.playerAttackDamage = config.getBoolean("player_attack_damage", false);
      this.onHit = config.getScriptOrNull("hit");
      this.onLand = config.getScriptOrNull("land");
      this.onTick = config.getScriptOrNull("tick");
      this.velocity = config.getDoubleFormula("velocity", DoubleFormula.constant(1.0));
   }

   @Override
   public void cast(SkillMetadata meta, Location source, Vector dir) {
      Arrow arrow = (Arrow)meta.getCaster().getPlayer().launchProjectile(Arrow.class);
      arrow.setVelocity(dir.multiply(this.velocity.evaluate(meta)));
      meta.getCaster().triggerSkills(TriggerType.SHOOT_BOW, arrow, null);
      ProjectileMetadata proj = ProjectileMetadata.create(meta.getCaster(), ProjectileType.ARROW, arrow);
      if (this.fromItem) {
         proj.setSourceItem(NBTItem.get(meta.getCaster().getPlayer().getInventory().getItem(meta.getCaster().getActionHand().toBukkit())));
      }

      if (this.playerAttackDamage) {
         proj.setCustomDamage(true);
      }

      if (this.onHit != null) {
         proj.getEffectiveSkills().add(this.skill(this.onHit, TriggerType.ARROW_HIT));
      }

      if (this.onLand != null) {
         proj.getEffectiveSkills().add(this.skill(this.onLand, TriggerType.ARROW_LAND));
      }

      if (this.onTick != null) {
         proj.getEffectiveSkills().add(this.skill(this.onTick, TriggerType.ARROW_TICK));
      }
   }

   @NotNull
   private PassiveSkill skill(Script script, TriggerType triggerType) {
      return new PassiveSkill("api", new SimpleSkill(triggerType, new MythicLibSkillHandler(script)), EquipmentSlot.OTHER, ModifierSource.OTHER);
   }
}
