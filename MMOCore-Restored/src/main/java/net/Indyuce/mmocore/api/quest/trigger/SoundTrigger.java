package net.Indyuce.mmocore.api.quest.trigger;

import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.version.Sounds;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Sound;

public class SoundTrigger extends Trigger {
   private final Sound sound;
   private final float vol;
   private final float pitch;

   public SoundTrigger(MMOLineConfig config) {
      super(config);
      config.validate(new String[]{"sound"});
      this.sound = Sounds.fromName(new String[]{config.getString("sound").toUpperCase().replace("-", "_")});
      this.vol = config.contains("volume") ? (float)config.getDouble("volume") : 1.0F;
      this.pitch = config.contains("pitch") ? (float)config.getDouble("pitch") : 1.0F;
   }

   @Override
   public void apply(PlayerData player) {
      if (player.isOnline()) {
         player.getPlayer().playSound(player.getPlayer().getLocation(), this.sound, this.vol, this.pitch);
      }
   }
}
