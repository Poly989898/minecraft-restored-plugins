package net.Indyuce.mmoitems.api.crafting.trigger;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.Sound;

public class SoundTrigger extends Trigger {
   private final Sound sound;
   private final float vol;
   private final float pitch;

   public SoundTrigger(MMOLineConfig mmoLineConfig) {
      super("sound");
      mmoLineConfig.validate(new String[]{"sound"});
      this.sound = Sound.valueOf(mmoLineConfig.getString("sound").toUpperCase().replace("-", "_"));
      this.vol = (float)mmoLineConfig.getDouble("volume", 1.0);
      this.pitch = (float)mmoLineConfig.getDouble("pitch", 1.0);
   }

   @Override
   public void whenCrafting(PlayerData playerData) {
      if (playerData.isOnline()) {
         playerData.getPlayer().playSound(playerData.getPlayer().getLocation(), this.sound, this.vol, this.pitch);
      }
   }
}
