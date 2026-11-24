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
public class CircularLuteAttack implements LuteAttackHandler {
   @Override
   public void handle(
      final PlayerMetadata var1,
      final double var2,
      NBTItem var4,
      final double var5,
      @NotNull final Vector var7,
      @NotNull final SoundReader var8,
      @NotNull final ProjectileParticlesData var9
   ) {
      (new BukkitRunnable() {
         final Vector vec = var1.getPlayer().getEyeLocation().getDirection().multiply(0.4);
         final Location loc = var1.getPlayer().getEyeLocation();
         int ti = 0;

         public void run() {
            if (this.ti++ > var5) {
               this.cancel();
            }

            List<Entity> var1x = MMOUtils.getNearbyChunkEntities(this.loc);

            for (int var2x = 0; var2x < 3; var2x++) {
               this.loc.add(this.vec.add(var7));
               if (this.loc.getBlock().getType().isSolid()) {
                  this.cancel();
                  break;
               }

               double var3 = this.ti / 3.0;
               Vector var5x = MMOUtils.rotateFunc(new Vector(Math.cos(var3), Math.sin(var3), 0.0).multiply(0.3), this.loc);
               var9.shootParticle(this.loc.clone().add(var5x));
               var9.shootParticle(this.loc.clone().add(var5x.multiply(-1)));
               if (var2x == 0) {
                  var8.play(this.loc, 2.0F, (float)(0.5 + this.ti / var5));
               }

               for (Entity var7x : var1x) {
                  if (UtilityMethods.canTarget(var1.getPlayer(), this.loc, var7x, InteractionType.OFFENSE_ACTION)) {
                     var1.attack((LivingEntity)var7x, var2, new DamageType[]{DamageType.WEAPON, DamageType.MAGIC, DamageType.PROJECTILE});
                     this.cancel();
                     return;
                  }
               }
            }
         }
      }).runTaskTimer(MMOItems.plugin, 0L, 1L);
   }
}
