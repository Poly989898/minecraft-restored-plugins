package com.nexomc.nexo.compatibilities.placeholderapi;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.glyphs.Glyph;
import com.nexomc.nexo.glyphs.RequiredGlyph;
import com.nexomc.nexo.glyphs.Shift;
import com.nexomc.nexo.pack.multipack.PlayerPackData;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.key.Key;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.BuiltResourcePack;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u0006\u001a\u00020\u0007H\u0016J\b\u0010\b\u001a\u00020\u0007H\u0016J\b\u0010\t\u001a\u00020\u0007H\u0016J\b\u0010\n\u001a\u00020\u000bH\u0016J\u001c\u0010\f\u001a\u0004\u0018\u00010\u00072\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u000f\u001a\u00020\u0007H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\u0010\u001a\u00070\u0011¢\u0006\u0002\b\u0012X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0013"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/placeholderapi/NexoExpansion;", "Lme/clip/placeholderapi/expansion/PlaceholderExpansion;", "plugin", "Lcom/nexomc/nexo/NexoPlugin;", "<init>", "(Lcom/nexomc/nexo/NexoPlugin;)V", "getAuthor", "", "getIdentifier", "getVersion", "persist", "", "onRequest", "player", "Lorg/bukkit/OfflinePlayer;", "params", "DEFAULT_FONT", "Lnet/kyori/adventure/key/Key;", "Lorg/jetbrains/annotations/NotNull;", "com.nexomc:core"}
)
public final class NexoExpansion extends PlaceholderExpansion {
   @NotNull
   private final NexoPlugin plugin;
   @NotNull
   private final Key DEFAULT_FONT;

   public NexoExpansion(@NotNull NexoPlugin plugin) {
      super();
      this.plugin = plugin;
      Key key = Key.key("default");
      this.DEFAULT_FONT = key;
   }

   @NotNull
   public String getAuthor() {
      return "boy0000";
   }

   @NotNull
   public String getIdentifier() {
      return "nexo";
   }

   @NotNull
   public String getVersion() {
      String version = this.plugin.getPluginMeta().getVersion();
      return version;
   }

   public boolean persist() {
      return true;
   }

   @Nullable
   public String onRequest(@Nullable OfflinePlayer player, @NotNull String params) {
      Glyph glyph = this.plugin.fontManager().glyphFromName(params);
      String str;
      if (!(glyph instanceof RequiredGlyph)) {
         str = Intrinsics.areEqual(glyph.getFont(), this.DEFAULT_FONT) ? glyph.getFormattedUnicodes() : glyph.getGlyphTag();
      } else if (StringsKt.startsWith(params, "shift_", false)) {
         Shift shift = Shift.INSTANCE;
         Integer number = StringsKt.toIntOrNull(StringsKt.substringAfter(params, "shift_", params));
         if (number == null) {
            return null;
         }

         str = shift.of(number);
      } else if (Intrinsics.areEqual(params, "pack_hash")) {
         BuiltResourcePack builtPack = this.plugin.packGenerator().builtPack();
         str = builtPack != null ? builtPack.hash() : null;
      } else if (StringsKt.startsWith(params, "multipack_", false)) {
         PlayerPackData playerPackData = PlayerPackData.INSTANCE;
         if (player == null) {
            return "";
         }

         Player player2 = player.getPlayer();
         if (player2 == null) {
            return "";
         }

         str = String.valueOf(playerPackData.enabledTemplates((OfflinePlayer)player2).contains(StringsKt.substringAfter(params, "multipack_", params)));
      } else {
         str = "";
      }

      return str;
   }
}
