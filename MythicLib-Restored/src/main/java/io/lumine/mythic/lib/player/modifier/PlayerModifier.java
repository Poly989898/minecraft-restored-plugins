package io.lumine.mythic.lib.player.modifier;

import io.lumine.mythic.lib.util.lang3.Validate;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.function.Function;
import java.util.HashMap;
import java.util.Map;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import java.util.Objects;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerModifier {
   private final UUID uniqueId;
   private final ModifierSource source;
   private final EquipmentSlot slot;
   private final String key;

   public PlayerModifier(@NotNull String key, @NotNull EquipmentSlot slot, @NotNull ModifierSource source) {
      this(UUID.randomUUID(), key, slot, source);
   }

   public PlayerModifier(@NotNull UUID uniqueId, @NotNull String key, @NotNull EquipmentSlot slot, @NotNull ModifierSource source) {
      this.uniqueId = uniqueId;
      this.key = key;
      this.slot = slot;
      this.source = source;
   }

   @NotNull
   public UUID getUniqueId() {
      return this.uniqueId;
   }

   @NotNull
   public String getKey() {
      return this.key;
   }

   @NotNull
   public EquipmentSlot getSlot() {
      return this.slot;
   }

   @NotNull
   public ModifierSource getSource() {
      return this.source;
   }

   public abstract void register(@NotNull MMOPlayerData var1);

   public abstract void unregister(@NotNull MMOPlayerData var1);

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         PlayerModifier that = (PlayerModifier)o;
         return this.uniqueId.equals(that.uniqueId);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.uniqueId);
   }

   private static final Map<String, Function<ConfigObject, PlayerModifier>> BY_KEY = new HashMap<>();

   /** The modifier map of the player holding the modifiers of this type; modifier types that support it override this. */
   @NotNull
   public ModifierMap<?> getMap(@NotNull MMOPlayerData playerData) {
      throw new UnsupportedOperationException("Modifier type " + this.getClass().getSimpleName() + " does not provide a modifier map");
   }

   /** Registers a modifier type which can then be read from configuration files by its key or one of its aliases. */
   public static void registerPlayerModifierType(@NotNull String key, @NotNull Function<ConfigObject, PlayerModifier> resolver, @NotNull String... aliases) {
      Validate.notNull(key, "Key cannot be null");
      Validate.notNull(resolver, "Resolver cannot be null");
      BY_KEY.put(key, resolver);
      for (String alias : aliases) {
         Validate.notNull(alias, "Alias cannot be null");
         BY_KEY.put(alias, resolver);
      }
   }

   /** Reads a modifier whose type is given by the "type" field of the configuration. */
   @NotNull
   public static PlayerModifier from(@NotNull ConfigObject config) {
      Validate.notNull(config, "Config cannot be null");
      String type = config.getKey();
      if (type == null) {
         type = config.getString("type");
      }

      Function<ConfigObject, PlayerModifier> resolver = BY_KEY.get(type);
      Validate.notNull(resolver, String.format("Could not match player modifier type to %s", type));
      return resolver.apply(config);
   }
}
