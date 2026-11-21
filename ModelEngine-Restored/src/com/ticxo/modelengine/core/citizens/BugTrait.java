package com.ticxo.modelengine.core.citizens;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

@TraitName("bug")
public class BugTrait extends Trait {
   private int tick;

   public BugTrait() {
      super("bug");
   }

   public void run() {
      Entity entity = this.npc.getEntity();
      if (entity != null) {
         EntityHandler handler = ModelEngineAPI.getEntityHandler();
         Location location = entity.getLocation();
         World world = location.getWorld();
         Vector yRot = new Vector(0.0, 0.0, 0.5).rotateAroundY(-handler.getYRot(entity) * (float) (Math.PI / 180.0));
         Vector yBodyRot = new Vector(0, 0, 1).rotateAroundY(-handler.getYBodyRot(entity) * (float) (Math.PI / 180.0));
         Vector yHeadRot = new Vector(0.0, 0.0, 1.5).rotateAroundY(-handler.getYHeadRot(entity) * (float) (Math.PI / 180.0));
         world.spawnParticle(Particle.DUST, location.clone().add(yRot), 1, new DustOptions(Color.RED, 0.25F));
         world.spawnParticle(Particle.DUST, location.clone().add(yBodyRot), 1, new DustOptions(Color.GREEN, 0.25F));
         world.spawnParticle(Particle.DUST, location.clone().add(yHeadRot), 1, new DustOptions(Color.BLUE, 0.25F));
      }
   }
}
