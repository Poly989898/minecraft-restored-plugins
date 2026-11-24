package net.Indyuce.mmoitems.api.interaction.weapon.untargeted.lute;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.comp.interaction.InteractionType;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import java.util.List;
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
public class WaveLuteAttack implements LuteAttackHandler {
   @Override
   public void handle(
      final PlayerMetadata playerMetadata,
      final double value,
      NBTItem nbtItem,
      final double value2,
      @NotNull final Vector vector,
      @NotNull final SoundReader soundReader,
      @NotNull final ProjectileParticlesData projectileParticlesData
   ) {
      (new BukkitRunnable() {
         final Vector vec = playerMetadata.getPlayer().getEyeLocation().getDirection().multiply(0.4);
         final Location loc = playerMetadata.getPlayer().getEyeLocation();
         int ti = 0;

         public void run() {
            if (this.ti++ > value2) {
               this.cancel();
            }

            List<Entity> var1x = MMOUtils.getNearbyChunkEntities(this.loc);

            for (int var2x = 0; var2x < 3; var2x++) {
               this.loc.add(this.vec.add(vector));
               if (this.loc.getBlock().getType().isSolid()) {
                  this.cancel();
                  break;
               }

               Vector vector2 = MMOUtils.rotateFunc(new Vector(0.5, 0.0, 0.0), this.loc);
               projectileParticlesData.shootParticle(this.loc.clone().add(vector2.clone().multiply(Math.sin(this.ti / 2.0))));
               projectileParticlesData.shootParticle(this.loc.clone().add(vector2.clone().multiply(-1)));
               if (var2x == 0) {
                  soundReader.play(this.loc, 2.0F, (float)(0.5 + this.ti / value2));
               }

               for (Entity var5x : var1x) {
                  if (UtilityMethods.canTarget(playerMetadata.getPlayer(), this.loc, var5x, InteractionType.OFFENSE_ACTION)) {
                     playerMetadata.attack((LivingEntity)var5x, value, new DamageType[]{DamageType.WEAPON, DamageType.MAGIC, DamageType.PROJECTILE});
                     this.cancel();
                     return;
                  }
               }
            }
         }
      }).runTaskTimer(MMOItems.plugin, 0L, 1L);
   }
}
