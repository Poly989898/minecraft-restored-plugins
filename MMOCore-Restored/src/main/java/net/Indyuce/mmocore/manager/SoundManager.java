package net.Indyuce.mmocore.manager;

import io.lumine.mythic.lib.util.SoundObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigFile;
import net.Indyuce.mmocore.api.SoundEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class SoundManager implements MMOCoreManager {
   private final Map<SoundEvent, SoundObject> sounds = new HashMap<>();

   @NotNull
   public SoundObject getSound(SoundEvent event) {
      return Objects.requireNonNull(this.sounds.get(event), "Could not find sound for " + event.name());
   }

   @Override
   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         this.sounds.clear();
      }

      FileConfiguration config = new ConfigFile("sounds").getConfig();

      for (SoundEvent sound : SoundEvent.values()) {
         try {
            this.sounds.put(sound, new SoundObject(config.getString(sound.name().replace("_", "-").toLowerCase())));
         } catch (RuntimeException exception) {
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load sound for '" + sound.name() + "': " + exception.getMessage());
         }
      }
   }
}
