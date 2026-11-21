package io.lumine.mythic.bukkit.utils.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.lumine.mythic.bukkit.utils.gson.GsonSerializable;
import io.lumine.mythic.bukkit.utils.gson.JsonBuilder;
import io.lumine.mythic.bukkit.utils.logging.Log;
import javax.annotation.Nonnull;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class Sound implements GsonSerializable {
   private final String sound;
   private final float volume;
   private final float pitch;
   private final Sound.Category category;

   public static Sound deserialize(String element) {
      Preconditions.checkNotNull(element);
      return new Sound(element);
   }

   public static Sound deserialize(JsonElement element) {
      Preconditions.checkArgument(element.isJsonObject());
      JsonObject object = element.getAsJsonObject();
      Preconditions.checkArgument(object.has("sound"));
      Preconditions.checkArgument(object.has("volume"));
      Preconditions.checkArgument(object.has("pitch"));
      Preconditions.checkArgument(object.has("category"));
      String sound = object.get("sound").getAsString();
      float volume = object.get("volume").getAsFloat();
      float pitch = object.get("pitch").getAsFloat();
      String source = object.get("category").getAsString();
      return of(sound, volume, pitch);
   }

   public static Sound of(String input) {
      return new Sound(input);
   }

   public static Sound of(String sound, float volume, float pitch) {
      return new Sound(sound, volume, pitch, Sound.Category.MASTER);
   }

   public static Sound of(String sound, float volume, float pitch, Sound.Category category) {
      return new Sound(sound, volume, pitch, category);
   }

   private Sound(String sound, float volume, float pitch, Sound.Category category) {
      this.sound = sound;
      this.volume = volume;
      this.pitch = pitch;
      this.category = category;
   }

   private Sound(String input) {
      if (input == null || input.isEmpty()) {
         input = "block.note_block.didgeridoo";
         Log.error("Sound cannot be null or blank");
      }

      String[] parts = input.split(" ");
      this.sound = parts[0];
      this.volume = parts.length > 1 ? Float.parseFloat(parts[1]) : 1.0F;
      this.pitch = parts.length > 2 ? Float.parseFloat(parts[2]) : 1.0F;
      this.category = parts.length > 3 ? Sound.Category.get(parts[2]) : Sound.Category.MASTER;
   }

   public void play(Player player) {
      play(player, this.sound, this.category.getSoundCategory(), this.volume, this.pitch);
   }

   public void play(Player player, Location location) {
      play(player, location, this.sound, this.category.getSoundCategory(), this.volume, this.pitch);
   }

   public void play(Player player, Position location) {
      play(player, location.toLocation(), this.sound, this.category.getSoundCategory(), this.volume, this.pitch);
   }

   public void play(Player player, Locus location) {
      play(player, location.toLocation(player.getWorld()), this.sound, this.category.getSoundCategory(), this.volume, this.pitch);
   }

   public void play(Location location) {
      play(location, this.sound, this.category, this.volume, this.pitch);
   }

   @Nonnull
   public JsonObject serialize() {
      return JsonBuilder.object().add("sound", this.sound).add("v", this.volume).add("p", this.pitch).add("category", this.category.toString()).build();
   }

   public String serializeShortForm() {
      return this.sound + " " + this.volume + " " + this.pitch + " " + this.category.toString();
   }

   public static void play(Player player, String sound) {
      play(player, sound, Sound.Category.MASTER, 1.0F, 1.0F);
   }

   public static void play(Player player, String sound, float volume, float pitch) {
      play(player, sound, Sound.Category.MASTER, volume, pitch);
   }

   public static void play(Player player, String sound, Sound.Category category, float volume, float pitch) {
      player.playSound(player.getLocation(), sound, category.getSoundCategory(), volume, pitch);
   }

   public static void play(Player player, String sound, SoundCategory category, float volume, float pitch) {
      player.playSound(player.getLocation(), sound, category, volume, pitch);
   }

   public static void play(Player player, Location location, String sound) {
      play(player, location, sound, Sound.Category.MASTER, 1.0F, 1.0F);
   }

   public static void play(Player player, Location location, String sound, float volume, float pitch) {
      play(player, location, sound, Sound.Category.MASTER, volume, pitch);
   }

   public static void play(Player player, Location location, String sound, Sound.Category category, float volume, float pitch) {
      player.playSound(location, sound, category.getSoundCategory(), volume, pitch);
   }

   public static void play(Player player, Location location, String sound, SoundCategory category, float volume, float pitch) {
      player.playSound(location, sound, category, volume, pitch);
   }

   public static void play(Location location, String sound) {
      play(location, sound, 1.0F, 1.0F);
   }

   public static void play(Location location, String sound, float volume, float pitch) {
      for (Player player : location.getWorld().getPlayers()) {
         player.playSound(location, sound, volume, pitch);
      }
   }

   public static void play(Location location, String sound, Sound.Category category, float volume, float pitch) {
      for (Player player : location.getWorld().getPlayers()) {
         player.playSound(location, sound, category.getSoundCategory(), volume, pitch);
      }
   }

   public static enum Category {
      MASTER("master", SoundCategory.MASTER),
      MUSIC("music", SoundCategory.MUSIC),
      RECORD("record", SoundCategory.RECORDS),
      WEATHER("weather", SoundCategory.WEATHER),
      BLOCK("block", SoundCategory.BLOCKS),
      HOSTILE("hostile", SoundCategory.HOSTILE),
      NEUTRAL("neutral", SoundCategory.NEUTRAL),
      PLAYER("player", SoundCategory.PLAYERS),
      AMBIENT("ambient", SoundCategory.AMBIENT),
      VOICE("voice", SoundCategory.VOICE);

      private final String name;
      private final SoundCategory soundCategory;

      private Category(final String name, SoundCategory soundCategory) {
         this.name = name;
         this.soundCategory = soundCategory;
      }

      public static Sound.Category get(String name) {
         try {
            return valueOf(name);
         } catch (Throwable e) {
            return MASTER;
         }
      }

      public String getName() {
         return this.name;
      }

      public SoundCategory getSoundCategory() {
         return this.soundCategory;
      }
   }
}
