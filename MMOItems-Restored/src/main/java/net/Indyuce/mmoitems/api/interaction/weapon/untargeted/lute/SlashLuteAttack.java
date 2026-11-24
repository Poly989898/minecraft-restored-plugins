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
      final PlayerMetadata playerMetadata,
      double value,
      NBTItem nbtItem,
      final double duration,
      @NotNull Vector vector,
      @NotNull final SoundReader soundReader,
      @NotNull final ProjectileParticlesData projectileParticlesData
   ) {
      (new BukkitRunnable() {
         final Vector vec = playerMetadata.getPlayer().getEyeLocation().getDirection();
         final Location loc = playerMetadata.getPlayer().getLocation().add(0.0, 1.3, 0.0);
         double ti = 1.0;

         public void run() {
            if ((this.ti += 0.6) > 5.0) {
               this.cancel();
            }

            soundReader.play(this.loc, 2.0F, (float)(0.5 + this.ti / duration));

            for (byte angle = -30; angle < 30; angle += 3) {
               if (LuteAttackHandler.RANDOM.nextBoolean()) {
                  this.loc.setDirection(this.vec);
                  this.loc.setYaw(this.loc.getYaw() + angle);
                  this.loc.setPitch(playerMetadata.getPlayer().getEyeLocation().getPitch());
                  projectileParticlesData.shootParticle(this.loc.clone().add(this.loc.getDirection().multiply(1.5 * this.ti)));
               }
            }
         }
      }).runTaskTimer(MMOItems.plugin, 0L, 1L);

      for (Entity nearbyChunkEntity : MMOUtils.getNearbyChunkEntities(playerMetadata.getPlayer().getLocation())) {
         if (nearbyChunkEntity.getLocation().distanceSquared(playerMetadata.getPlayer().getLocation()) < 40.0
            && playerMetadata.getPlayer().getEyeLocation().getDirection().angle(nearbyChunkEntity.getLocation().toVector().subtract(playerMetadata.getPlayer().getLocation().toVector()))
               < Math.PI / 6
            && UtilityMethods.canTarget(playerMetadata.getPlayer(), nearbyChunkEntity, InteractionType.OFFENSE_ACTION)) {
            playerMetadata.attack((LivingEntity)nearbyChunkEntity, value, new DamageType[]{DamageType.WEAPON, DamageType.MAGIC, DamageType.PROJECTILE});
         }
      }
   }
}
