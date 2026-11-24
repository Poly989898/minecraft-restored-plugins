package io.lumine.mythic.lib.player.particle.type;

import io.lumine.mythic.lib.player.particle.ParticleEffect;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.bukkit.entity.Player;

public class OffsetParticleEffect extends ParticleEffect {
   private final double speed;
   private final double h_offset;
   private final double v_offset;
   private final double height;
   private final int amount;

   public OffsetParticleEffect(ConfigObject obj) {
      super(obj);
      this.speed = obj.getDouble("speed");
      this.height = obj.getDouble("height");
      this.h_offset = obj.getDouble("horizontal-offset");
      this.v_offset = obj.getDouble("vertical-offset");
      this.amount = obj.getInt("amount");
   }

   @Override
   public void tick(Player player) {
      this.getParticle()
         .display(player.getPlayer().getLocation().add(0.0, this.height, 0.0), this.amount, this.h_offset, this.v_offset, this.h_offset, this.speed);
   }
}
