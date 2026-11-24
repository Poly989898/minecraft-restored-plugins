package net.Indyuce.mmoitems.api.interaction.weapon.untargeted;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.comp.interaction.InteractionType;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.util.RayTrace;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.interaction.weapon.Weapon;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Deprecated
public class Musket extends Weapon implements LegacyWeapon {
   @Deprecated
   public Musket(Player player, NBTItem nbtItem) {
      super(player, nbtItem);
   }

   public Musket(PlayerData playerData, NBTItem nbtItem) {
      super(playerData, nbtItem);
   }

   @Override
   public boolean canAttack(boolean flag, EquipmentSlot equipmentSlot) {
      return flag;
   }

   @Override
   public void applyAttackEffect(PlayerMetadata playerMetadata, EquipmentSlot equipmentSlot) {
      double value = this.requireNonZero(playerMetadata.getStat("RANGE"), MMOItems.plugin.getConfig().getDouble("default.range"));
      double value2 = this.requireNonZero(playerMetadata.getStat("RECOIL"), MMOItems.plugin.getConfig().getDouble("default.recoil"));
      double stat = playerMetadata.getStat("KNOCKBACK");
      if (stat > 0.0) {
         this.getPlayer()
            .setVelocity(
               this.getPlayer().getVelocity().add(this.getPlayer().getEyeLocation().getDirection().setY(0).normalize().multiply(-1.0 * stat).setY(-0.2))
            );
      }

      double value3 = Math.toRadians(this.getPlayer().getEyeLocation().getYaw() + 90.0F + 45 * (equipmentSlot == EquipmentSlot.MAIN_HAND ? 1 : -1));
      Location location = this.getPlayer().getLocation().add(Math.cos(value3) * 0.5, 1.5, Math.sin(value3) * 0.5);
      location.setPitch((float)(location.getPitch() + (RANDOM.nextDouble() - 0.5) * 2.0 * value2));
      location.setYaw((float)(location.getYaw() + (RANDOM.nextDouble() - 0.5) * 2.0 * value2));
      RayTrace rayTrace = new RayTrace(
         location, location.getDirection(), value, var1x -> UtilityMethods.canTarget(playerMetadata.getPlayer(), var1x, InteractionType.OFFENSE_ACTION)
      );
      if (rayTrace.hasHit()) {
         playerMetadata.attack(rayTrace.getHit(), playerMetadata.getStat("ATTACK_DAMAGE"), new DamageType[]{DamageType.WEAPON, DamageType.PHYSICAL, DamageType.PROJECTILE});
      }

      rayTrace.draw(0.5, Color.BLACK);
      this.getPlayer().getWorld().playSound(this.getPlayer().getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 2.0F, 2.0F);
   }
}
