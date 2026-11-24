package net.Indyuce.mmoitems.api.crafting.trigger;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.Sound;

public class SoundTrigger extends Trigger {
   private final Sound sound;
   private final float vol;
   private final float pitch;

   public SoundTrigger(MMOLineConfig var1) {
      super("sound");
      var1.validate(new String[]{"sound"});
      this.sound = Sound.valueOf(var1.getString("sound").toUpperCase().replace("-", "_"));
      this.vol = (float)var1.getDouble("volume", 1.0);
      this.pitch = (float)var1.getDouble("pitch", 1.0);
   }

   @Override
   public void whenCrafting(PlayerData var1) {
      if (var1.isOnline()) {
         var1.getPlayer().playSound(var1.getPlayer().getLocation(), this.sound, this.vol, this.pitch);
      }
   }
}
