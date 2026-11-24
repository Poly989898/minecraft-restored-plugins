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
   public Musket(Player var1, NBTItem var2) {
      super(var1, var2);
   }

   public Musket(PlayerData var1, NBTItem var2) {
      super(var1, var2);
   }

   @Override
   public boolean canAttack(boolean var1, EquipmentSlot var2) {
      return var1;
   }

   @Override
   public void applyAttackEffect(PlayerMetadata var1, EquipmentSlot var2) {
      double var3 = this.requireNonZero(var1.getStat("RANGE"), MMOItems.plugin.getConfig().getDouble("default.range"));
      double var5 = this.requireNonZero(var1.getStat("RECOIL"), MMOItems.plugin.getConfig().getDouble("default.recoil"));
      double var7 = var1.getStat("KNOCKBACK");
      if (var7 > 0.0) {
         this.getPlayer()
            .setVelocity(
               this.getPlayer().getVelocity().add(this.getPlayer().getEyeLocation().getDirection().setY(0).normalize().multiply(-1.0 * var7).setY(-0.2))
            );
      }

      double var9 = Math.toRadians(this.getPlayer().getEyeLocation().getYaw() + 90.0F + 45 * (var2 == EquipmentSlot.MAIN_HAND ? 1 : -1));
      Location var11 = this.getPlayer().getLocation().add(Math.cos(var9) * 0.5, 1.5, Math.sin(var9) * 0.5);
      var11.setPitch((float)(var11.getPitch() + (RANDOM.nextDouble() - 0.5) * 2.0 * var5));
      var11.setYaw((float)(var11.getYaw() + (RANDOM.nextDouble() - 0.5) * 2.0 * var5));
      RayTrace var12 = new RayTrace(
         var11, var11.getDirection(), var3, var1x -> UtilityMethods.canTarget(var1.getPlayer(), var1x, InteractionType.OFFENSE_ACTION)
      );
      if (var12.hasHit()) {
         var1.attack(var12.getHit(), var1.getStat("ATTACK_DAMAGE"), new DamageType[]{DamageType.WEAPON, DamageType.PHYSICAL, DamageType.PROJECTILE});
      }

      var12.draw(0.5, Color.BLACK);
      this.getPlayer().getWorld().playSound(this.getPlayer().getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 2.0F, 2.0F);
   }
}
