package io.lumine.mythic.lib.listener.option;

import fr.phoenixdevt.profiles.event.PlayerIdDispatchEvent;
import fr.phoenixdevt.profiles.event.ProfileSelectedEvent;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class FixAttributeModifiers implements Listener {
   private final List<Attribute> attributes = new ArrayList<>();
   private final int targetRevId;
   private static final String PROFILELESS_KEY = "Profileless";
   private static final NamespacedKey TAG_PATH = NamespacedKey.fromString("attr_mods_reset", MythicLib.plugin);

   public FixAttributeModifiers(ConfigurationSection config) {
      this.targetRevId = config.getInt("rev_id");
      Validate.isTrue(this.targetRevId >= 0, "Revision ID must be positive");
      List<String> _attributes = config.getStringList("attributes");
      if (_attributes.isEmpty()) {
         this.attributes.addAll(Arrays.asList(Attribute.values()));
      } else {
         for (String str : _attributes) {
            this.attributes.add(Attribute.valueOf(UtilityMethods.enumName(str)));
         }
      }

      try {
         Class.forName("fr.phoenixdevt.profiles.event.ProfileSelectedEvent");
         Bukkit.getPluginManager().registerEvents(new FixAttributeModifiers.LegacyProfiles(), MythicLib.plugin);
      } catch (Exception exception) {
         Bukkit.getPluginManager().registerEvents(this, MythicLib.plugin);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void a(PlayerJoinEvent event) {
      this.checkForRevid(event.getPlayer(), "Profileless");
   }

   private void checkForRevid(@NotNull Player player, @NotNull String key) {
      if (!player.hasPlayedBefore()) {
         JsonObject json = new JsonObject();
         json.addProperty(key, this.targetRevId);
         player.getPersistentDataContainer().set(TAG_PATH, PersistentDataType.STRING, json.toString());
      } else {
         String jsonFormat = (String)player.getPersistentDataContainer().get(TAG_PATH, PersistentDataType.STRING);
         JsonObject json = jsonFormat == null ? new JsonObject() : JsonParser.parseString(jsonFormat).getAsJsonObject();
         int revid = json.has(key) ? json.get(key).getAsInt() : 0;
         if (revid < this.targetRevId) {
            json.addProperty(key, this.targetRevId);
            player.getPersistentDataContainer().set(TAG_PATH, PersistentDataType.STRING, json.toString());
            this.resetAttributeModifiers(player);
         }
      }
   }

   private void resetAttributeModifiers(@NotNull Player player) {
      for (Attribute attribute : this.attributes) {
         AttributeInstance instance = player.getAttribute(attribute);
         if (instance != null) {
            for (AttributeModifier modifier : new ArrayList<>(instance.getModifiers())) {
               instance.removeModifier(modifier);
            }
         }
      }
   }

   class LegacyProfiles implements Listener {
      @EventHandler(priority = EventPriority.MONITOR)
      public void onProfileSelect(ProfileSelectedEvent event) {
         FixAttributeModifiers.this.checkForRevid(event.getPlayer(), event.getProfile().getUniqueId().toString());
      }

      @EventHandler(priority = EventPriority.MONITOR)
      public void onIdDispatch(PlayerIdDispatchEvent event) {
         FixAttributeModifiers.this.checkForRevid(event.getPlayer(), event.getFakeId() == null ? "Profileless" : event.getFakeId().toString());
      }
   }
}
