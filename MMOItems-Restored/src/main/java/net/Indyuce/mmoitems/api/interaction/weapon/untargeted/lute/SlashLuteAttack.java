package net.Indyuce.mmoitems.api.interaction.weapon.untargeted.lute;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.comp.interaction.InteractionType;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.util.SoundReader;
import net.Indyuce.mmoitems.stat.data.ProjectileParticlesData;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class SlashLuteAttack implements LuteAttackHandler {
   @Override
   public void handle(
      final PlayerMetadata var1,
      double var2,
      NBTItem var4,
      final double var5,
      @NotNull Vector var7,
      @NotNull final SoundReader var8,
      @NotNull final ProjectileParticlesData var9
   ) {
      (new BukkitRunnable() {
         final Vector vec = var1.getPlayer().getEyeLocation().getDirection();
         final Location loc = var1.getPlayer().getLocation().add(0.0, 1.3, 0.0);
         double ti = 1.0;

         public void run() {
            if ((this.ti += 0.6) > 5.0) {
               this.cancel();
            }

            var8.play(this.loc, 2.0F, (float)(0.5 + this.ti / var5));

            for (byte var1x = -30; var1x < 30; var1x += 3) {
               if (LuteAttackHandler.RANDOM.nextBoolean()) {
                  this.loc.setDirection(this.vec);
                  this.loc.setYaw(this.loc.getYaw() + var1x);
                  this.loc.setPitch(var1.getPlayer().getEyeLocation().getPitch());
                  var9.shootParticle(this.loc.clone().add(this.loc.getDirection().multiply(1.5 * this.ti)));
               }
            }
         }
      }).runTaskTimer(MMOItems.plugin, 0L, 1L);

      for (Entity var11 : MMOUtils.getNearbyChunkEntities(var1.getPlayer().getLocation())) {
         if (var11.getLocation().distanceSquared(var1.getPlayer().getLocation()) < 40.0
            && var1.getPlayer().getEyeLocation().getDirection().angle(var11.getLocation().toVector().subtract(var1.getPlayer().getLocation().toVector()))
               < Math.PI / 6
            && UtilityMethods.canTarget(var1.getPlayer(), var11, InteractionType.OFFENSE_ACTION)) {
            var1.attack((LivingEntity)var11, var2, new DamageType[]{DamageType.WEAPON, DamageType.MAGIC, DamageType.PROJECTILE});
         }
      }
   }
}
