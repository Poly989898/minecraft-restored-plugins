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
   public Lute(Player player, NBTItem nbtItem) {
      super(player, nbtItem);
   }

   public Lute(PlayerData playerData, NBTItem nbtItem) {
      super(playerData, nbtItem);
   }

   @Override
   public boolean canAttack(boolean flag, EquipmentSlot equipmentSlot) {
      return flag;
   }

   @Override
   public void applyAttackEffect(final PlayerMetadata playerMetadata, EquipmentSlot equipmentSlot) {
      final double value = this.requireNonZero(playerMetadata.getStat("ATTACK_DAMAGE"), 7.0);
      final double range = this.requireNonZero(playerMetadata.getStat("RANGE"), MMOItems.plugin.getConfig().getDouble("default.range"));
      final Vector vector = new Vector(0.0, -0.003 * playerMetadata.getStat("NOTE_WEIGHT"), 0.0);
      LuteAttackEffectStat.LuteAttackEffect luteAttackEffect = LuteAttackEffectStat.LuteAttackEffect.get(this.getNBTItem());
      final SoundReader soundReader = new SoundReader(this.getNBTItem().getString("MMOITEMS_LUTE_ATTACK_SOUND"), VSound.BLOCK_NOTE_BLOCK_BELL.get());
      final ProjectileParticlesData projectileParticlesData = this.getNBTItem().hasTag("MMOITEMS_PROJECTILE_PARTICLES")
         ? new ProjectileParticlesData(this.getNBTItem().getString("MMOITEMS_PROJECTILE_PARTICLES"))
         : ProjectileParticlesData.DEFAULT;
      if (luteAttackEffect != null) {
         luteAttackEffect.getAttack().handle(playerMetadata, value, this.getNBTItem(), range, vector, soundReader, projectileParticlesData);
      } else {
         (new BukkitRunnable() {
            final Vector vec = Lute.this.getPlayer().getEyeLocation().getDirection().multiply(0.4);
            final Location loc = Lute.this.getPlayer().getEyeLocation();
            int ti = 0;

            public void run() {
               if (this.ti++ > range) {
                  this.cancel();
               }

               projectileParticlesData.shootParticle(this.loc);
               soundReader.play(this.loc, 2.0F, (float)(0.5 + this.ti / range));
               List<Entity> var1x = MMOUtils.getNearbyChunkEntities(this.loc);

               for (int var2x = 0; var2x < 3; var2x++) {
                  this.loc.add(this.vec.add(vector));
                  if (this.loc.getBlock().getType().isSolid()) {
                     this.cancel();
                     break;
                  }

                  for (Entity entity : var1x) {
                     if (UtilityMethods.canTarget(Lute.this.getPlayer(), this.loc, entity, InteractionType.OFFENSE_ACTION)) {
                        playerMetadata.attack((LivingEntity)entity, value, new DamageType[]{DamageType.WEAPON, DamageType.MAGIC, DamageType.PROJECTILE});
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
