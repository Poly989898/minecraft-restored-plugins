package com.nexomc.nexo.mechanics.furniture.jukebox;

import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.NexoPlugin;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.JukeboxPlayable;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.text.StringsKt;
import org.bukkit.JukeboxSong;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u00182\u00020\u0001:\u0001\u0018B%\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bB\u0011\b\u0016\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u0007\u0010\u000bJ\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0012\u001a\u00020\u0013J\u0010\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000f¨\u0006\u0019"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/jukebox/JukeboxBlock;", "", "permission", "", "volume", "", "pitch", "<init>", "(Ljava/lang/String;FF)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getPermission", "()Ljava/lang/String;", "getVolume", "()F", "getPitch", "playingSong", "baseEntity", "Lorg/bukkit/entity/Entity;", "hasPermission", "", "player", "Lorg/bukkit/entity/Player;", "Companion", "com.nexomc:core"}
)
public final class JukeboxBlock {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final String permission;
   private final float volume;
   private final float pitch;
   @NotNull
   private static final NamespacedKey MUSIC_DISC_KEY;

   public JukeboxBlock(@NotNull String permission, float volume, float pitch) {
      super();
      this.permission = permission;
      this.volume = volume;
      this.pitch = pitch;
   }

   @NotNull
   public final String getPermission() {
      return this.permission;
   }

   public final float getVolume() {
      return this.volume;
   }

   public final float getPitch() {
      return this.pitch;
   }

   public JukeboxBlock(@NotNull ConfigurationSection section) {
      String string = section.getString("permission");
      if (string == null) {
         string = "";
      }

      this(string, (float)section.getDouble("volume", (double)1.0F), (float)section.getDouble("pitch", (double)1.0F));
   }

   @Nullable
   public final String playingSong(@NotNull Entity baseEntity) {
      ItemStack itemStack = baseEntity.getPersistentDataContainer().get(MUSIC_DISC_KEY, DataType.ITEM_STACK);
      if (itemStack == null) {
         return null;
      } else {
         ItemStack disc = itemStack;
         String str;
         if (disc.getType().isRecord()) {
            str = disc.getType().name().toLowerCase(Locale.ROOT);
            str = StringsKt.replace(str, "music_disc_", "minecraft:music_disc.", false);
         } else {
            JukeboxPlayable data = disc.getData(DataComponentTypes.JUKEBOX_PLAYABLE);
            if (data != null) {
               JukeboxSong jukeboxSong = data.jukeboxSong();
               if (jukeboxSong != null) {
                  NamespacedKey key = jukeboxSong.getKey();
                  if (key != null) {
                     str = key.asString();
                     return str;
                  }
               }
            }

            str = null;
         }

         return str;
      }
   }

   public final boolean hasPermission(@Nullable Player player) {
      return player == null || StringsKt.isBlank((CharSequence)this.permission) || player.hasPermission(this.permission);
   }

   public JukeboxBlock() {
      this("", 1.0F, 1.0F);
   }

   static {
      MUSIC_DISC_KEY = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "music_disc");
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/jukebox/JukeboxBlock$Companion;", "", "<init>", "()V", "MUSIC_DISC_KEY", "Lorg/bukkit/NamespacedKey;", "getMUSIC_DISC_KEY", "()Lorg/bukkit/NamespacedKey;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final NamespacedKey getMUSIC_DISC_KEY() {
         return JukeboxBlock.MUSIC_DISC_KEY;
      }
   }
}
