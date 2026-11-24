package net.Indyuce.mmoitems.api.interaction.weapon.untargeted;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.comp.interaction.InteractionType;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.version.VSound;
import java.util.List;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.interaction.weapon.Weapon;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.SoundReader;
import net.Indyuce.mmoitems.stat.LuteAttackEffectStat;
import net.Indyuce.mmoitems.stat.data.ProjectileParticlesData;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@Deprecated
public class Lute extends Weapon implements LegacyWeapon {
   @Deprecated
   public Lute(Player var1, NBTItem var2) {
      super(var1, var2);
   }

   public Lute(PlayerData var1, NBTItem var2) {
      super(var1, var2);
   }

   @Override
   public boolean canAttack(boolean var1, EquipmentSlot var2) {
      return var1;
   }

   @Override
   public void applyAttackEffect(final PlayerMetadata var1, EquipmentSlot var2) {
      final double var3 = this.requireNonZero(var1.getStat("ATTACK_DAMAGE"), 7.0);
      final double var5 = this.requireNonZero(var1.getStat("RANGE"), MMOItems.plugin.getConfig().getDouble("default.range"));
      final Vector var7 = new Vector(0.0, -0.003 * var1.getStat("NOTE_WEIGHT"), 0.0);
      LuteAttackEffectStat.LuteAttackEffect var8 = LuteAttackEffectStat.LuteAttackEffect.get(this.getNBTItem());
      final SoundReader var9 = new SoundReader(this.getNBTItem().getString("MMOITEMS_LUTE_ATTACK_SOUND"), VSound.BLOCK_NOTE_BLOCK_BELL.get());
      final ProjectileParticlesData var10 = this.getNBTItem().hasTag("MMOITEMS_PROJECTILE_PARTICLES")
         ? new ProjectileParticlesData(this.getNBTItem().getString("MMOITEMS_PROJECTILE_PARTICLES"))
         : ProjectileParticlesData.DEFAULT;
      if (var8 != null) {
         var8.getAttack().handle(var1, var3, this.getNBTItem(), var5, var7, var9, var10);
      } else {
         (new BukkitRunnable() {
            final Vector vec = Lute.this.getPlayer().getEyeLocation().getDirection().multiply(0.4);
            final Location loc = Lute.this.getPlayer().getEyeLocation();
            int ti = 0;

            public void run() {
               if (this.ti++ > var5) {
                  this.cancel();
               }

               var10.shootParticle(this.loc);
               var9.play(this.loc, 2.0F, (float)(0.5 + this.ti / var5));
               List<Entity> var1x = MMOUtils.getNearbyChunkEntities(this.loc);

               for (int var2x = 0; var2x < 3; var2x++) {
                  this.loc.add(this.vec.add(var7));
                  if (this.loc.getBlock().getType().isSolid()) {
                     this.cancel();
                     break;
                  }

                  for (Entity var4 : var1x) {
                     if (UtilityMethods.canTarget(Lute.this.getPlayer(), this.loc, var4, InteractionType.OFFENSE_ACTION)) {
                        var1.attack((LivingEntity)var4, var3, new DamageType[]{DamageType.WEAPON, DamageType.MAGIC, DamageType.PROJECTILE});
                        this.cancel();
                        return;
                     }
                  }
               }
            }
         }).runTaskTimer(MMOItems.plugin, 0L, 1L);
      }
   }
}
