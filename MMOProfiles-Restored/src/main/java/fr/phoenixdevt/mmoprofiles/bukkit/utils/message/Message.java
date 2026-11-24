package fr.phoenixdevt.mmoprofiles.bukkit.utils.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.ConfigurationSection;

public enum Message {
   TOO_MUCH_PROFILES("&cYou reached the profile number limit."),
   PROFILE_WITH_SAME_NAME("&cYou already have a profile with the same name."),
   SET_PROFILE_NAME_ASK("&ePlease type your desired profile name. Type 'cancel' to abort.");

   private List<String> message;
   private SoundReader sound;

   Message(String... message) {
      this(null, message);
   }

   Message(SoundReader sound, String... message) {
      this.message = Arrays.asList(message);
      this.sound = sound;
   }

   public String getPath() {
      return this.name().toLowerCase().replace("_", "-");
   }

   public List<String> getCached() {
      return new ArrayList<>(this.message);
   }

   public SoundReader getSound() {
      return this.sound;
   }

   public boolean hasSound() {
      return this.sound != null;
   }

   public PlayerMessage format(Object... placeholders) {
      return new PlayerMessage(this).format(placeholders);
   }

   public void update(ConfigurationSection config) {
      List<String> format = config.getStringList("format");
      Validate.notNull(this.message = format, "Could not read message format");
      this.sound = config.contains("sound") ? new SoundReader(config.getConfigurationSection("sound")) : null;
   }
}
