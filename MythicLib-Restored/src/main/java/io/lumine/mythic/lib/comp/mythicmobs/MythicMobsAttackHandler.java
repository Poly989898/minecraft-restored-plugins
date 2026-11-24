package io.lumine.mythic.lib.comp.mythicmobs;

import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.stat.provider.StatProvider;
import io.lumine.mythic.lib.damage.AttackHandler;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamagePacket;
import io.lumine.mythic.lib.element.Element;
import java.util.Optional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public class MythicMobsAttackHandler implements AttackHandler {
   @Nullable
   @Override
   public AttackMetadata getAttack(EntityDamageEvent event) {
      Optional<Object> opt = BukkitAdapter.adapt(event.getEntity()).getMetadata("skill-damage");
      if (!opt.isPresent()) {
         return null;
      }

      DamageMetadata mythic = (DamageMetadata)opt.get();
      Entity damagerBukkit = mythic.getDamager().getEntity().getBukkitEntity();
      StatProvider damager = damagerBukkit instanceof LivingEntity ? StatProvider.get((LivingEntity)damagerBukkit, EquipmentSlot.MAIN_HAND, true) : null;
      io.lumine.mythic.lib.damage.DamageMetadata damageMeta = new io.lumine.mythic.lib.damage.DamageMetadata(
         mythic.getAmount(), MythicLib.plugin.getDamage().getVanillaDamageTypes(mythic.getDamageCause())
      );
      if (mythic.getElement() != null) {
         try {
            Element element = MythicLib.plugin.getElements().get(UtilityMethods.enumName(mythic.getElement()));

            for (DamagePacket packet : damageMeta.getPackets()) {
               packet.setElement(element);
            }
         } catch (Exception var10) {
         }
      }

      return new AttackMetadata(damageMeta, (LivingEntity)event.getEntity(), damager);
   }
}
