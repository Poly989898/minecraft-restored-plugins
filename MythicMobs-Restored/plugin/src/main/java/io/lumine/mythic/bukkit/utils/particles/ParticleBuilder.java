package io.lumine.mythic.bukkit.utils.particles;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.utils.Players;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import java.awt.Color;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class ParticleBuilder {
   protected final Particle particle;
   protected Location location = null;
   protected Object particleData = null;
   protected int amount = 1;
   protected float speed = 0.0F;
   protected float offsetX = 0.0F;
   protected float offsetY = 0.0F;
   protected float offsetZ = 0.0F;
   protected boolean useExactOffsets;
   protected Vector towards = null;

   public static ParticleBuilder of(Particle particle) {
      return new ParticleBuilder(particle);
   }

   public ParticleBuilder(Particle particle2) {
      this.particle = particle2;
   }

   public ParticleBuilder withData(Object data) {
      if (data.getClass().isAssignableFrom(this.particle.toBukkitParticle().getDataType())) {
         this.particleData = data;
         return this;
      } else {
         if (data instanceof Material material) {
            if (this.particle.toBukkitParticle().getDataType().equals(ItemStack.class)) {
               try {
                  this.particleData = new ItemStack(material);
               } catch (Exception e) {
                  this.particleData = new ItemStack(Material.STONE);
               }
            } else if (this.particle.toBukkitParticle().getDataType().equals(BlockData.class)) {
               try {
                  this.particleData = Bukkit.getServer().createBlockData(material);
               } catch (Exception ex) {
                  this.particleData = Bukkit.getServer().createBlockData(Material.STONE);
               }
            } else if (this.particle.toBukkitParticle().getDataType().equals(MaterialData.class)) {
               try {
                  this.particleData = material.getData();
               } catch (Exception ex2) {
                  this.particleData = Material.STONE.getData();
               }
            }
         }

         if (this.particle.toBukkitParticle().getDataType().equals(DustOptions.class)) {
         }

         return this;
      }
   }

   public ParticleBuilder at(Location location) {
      this.location = location;
      return this;
   }

   public ParticleBuilder amount(int i) {
      this.amount = i;
      return this;
   }

   public ParticleBuilder speed(float i) {
      this.speed = i;
      return this;
   }

   public ParticleBuilder offset(float i) {
      this.offsetX = i;
      this.offsetY = i;
      this.offsetZ = i;
      return this;
   }

   public ParticleBuilder offset(float x, float y, float z) {
      this.offsetX = x;
      this.offsetY = y;
      this.offsetZ = z;
      return this;
   }

   public ParticleBuilder offsetX(float i) {
      this.offsetX = i;
      return this;
   }

   public ParticleBuilder offsetY(float i) {
      this.offsetY = i;
      return this;
   }

   public ParticleBuilder offsetZ(float i) {
      this.offsetZ = i;
      return this;
   }

   public ParticleBuilder towards(Vector v) {
      this.towards = v;
      return this;
   }

   public ParticleBuilder useExactOffsets(boolean b) {
      this.useExactOffsets = b;
      return this;
   }

   public ParticleBuilder withColor(String strColor) {
      return this.withColorAndSize(strColor, 1.0F);
   }

   public ParticleBuilder withColorAndSize(String strColor, float size) {
      Color color = Color.decode(strColor);
      int r = color.getRed();
      int g = color.getGreen();
      int b = color.getBlue();
      org.bukkit.Color c = org.bukkit.Color.fromRGB(r, g, b);
      this.particleData = new DustOptions(c, size);
      return this;
   }

   public void send() {
      this.send(this.location, Players.all());
   }

   public void send(Player to) {
      this.send(this.location, Lists.newArrayList(new Player[]{to}));
   }

   public void send(Player to, Location location) {
      this.send(location, Lists.newArrayList(new Player[]{to}));
   }

   public void send(Location location) {
      this.send(location, Players.all());
   }

   public void send(Collection<Player> to) {
      this.send(this.location, to);
   }

   public void send(Collection<Player> to, Location location) {
      this.send(location, to);
   }

   private void send(Location at, Collection<Player> to) {
      if (Bukkit.getServer().isPrimaryThread()) {
         Schedulers.async().run(() -> this.sendPackets(at, to));
      } else {
         this.sendPackets(at, to);
      }
   }

   private void sendPackets(Location loc, Collection<Player> to) {
      if (this.particleData != null && !this.particle.validateData(this.particleData)) {
         Log.error("Could not send particle: invalid particle data supplied.");
      } else {
         org.bukkit.Particle particle = this.particle.toBukkitParticle();
         if (this.towards != null) {
            for (int i = 0; i < this.amount; i++) {
               Location ln = loc.clone()
                  .add(
                     0.0F - this.offsetX + Numbers.randomDouble() * this.offsetX * 2.0,
                     this.offsetY - this.offsetY + Numbers.randomDouble() * this.offsetY * 2.0,
                     0.0F - this.offsetZ + Numbers.randomDouble() * this.offsetZ * 2.0
                  );

               for (Player player : to) {
                  if (player.getWorld().equals(loc.getWorld())) {
                     player.spawnParticle(particle, ln, 0, (float)this.towards.getX(), (float)this.towards.getY(), (float)this.towards.getZ(), this.speed);
                  }
               }
            }
         } else if (this.useExactOffsets) {
            for (int i = 0; i < this.amount; i++) {
               Location ln = loc.clone()
                  .add(
                     0.0F - this.offsetX + Numbers.randomDouble() * this.offsetX * 2.0,
                     this.offsetY - this.offsetY + Numbers.randomDouble() * this.offsetY * 2.0,
                     0.0F - this.offsetZ + Numbers.randomDouble() * this.offsetZ * 2.0
                  );

               for (Player playerx : to) {
                  if (playerx.getWorld().equals(loc.getWorld())) {
                     playerx.spawnParticle(particle, ln, 1, 0.0, 0.0, 0.0, this.speed);
                  }
               }
            }
         } else if (this.particleData != null) {
            for (Player playerxx : to) {
               if (playerxx.getWorld().equals(loc.getWorld())) {
                  playerxx.spawnParticle(particle, loc, this.amount, this.offsetX, this.offsetY, this.offsetZ, this.speed, this.particleData);
               }
            }
         } else {
            for (Player playerxxx : to) {
               if (playerxxx.getWorld().equals(loc.getWorld())) {
                  playerxxx.spawnParticle(particle, loc, this.amount, this.offsetX, this.offsetY, this.offsetZ, this.speed);
               }
            }
         }
      }
   }
}
