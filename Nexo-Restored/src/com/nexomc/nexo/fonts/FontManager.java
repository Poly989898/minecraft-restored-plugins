package com.nexomc.nexo.fonts;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.glyphs.Glyph;
import com.nexomc.nexo.glyphs.ReferenceGlyph;
import com.nexomc.nexo.proxy.ProxyHelpers;
import com.nexomc.nexo.utils.Reloadable;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\f\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u001e\n\u0000\n\u0002\u0010 \n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\u000e\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0015\u001a\u00020\u0007J\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00070\u0017J\u0014\u0010\u0018\u001a\u0010\u0012\f\u0012\n \u001a*\u0004\u0018\u00010\u00070\u00070\u0019J\u0010\u0010\u001b\u001a\u00020\u00072\b\u0010\u001c\u001a\u0004\u0018\u00010\u0006J\u0012\u0010\u001d\u001a\u0004\u0018\u00010\u00072\b\u0010\u001c\u001a\u0004\u0018\u00010\u0006J\u0012\u0010\u001e\u001a\u0004\u0018\u00010\u00072\b\u0010\u001f\u001a\u0004\u0018\u00010\u0006J\u000e\u0010 \u001a\u00020\u00132\u0006\u0010!\u001a\u00020\"R\u001a\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001d\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u001d\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\nR\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00060\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006#"},
   d2 = {"Lcom/nexomc/nexo/fonts/FontManager;", "Lcom/nexomc/nexo/utils/Reloadable;", "<init>", "()V", "glyphMap", "Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "", "Lcom/nexomc/nexo/glyphs/Glyph;", "placeholderGlyphMap", "getPlaceholderGlyphMap", "()Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "unicodeGlyphMap", "", "getUnicodeGlyphMap", "tabcompletions", "Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;", "getTabcompletions", "()Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;", "reload", "", "registerGlyph", "glyph", "glyphs", "", "emojis", "", "kotlin.jvm.PlatformType", "glyphFromName", "id", "glyphFromID", "glyphFromPlaceholder", "word", "sendGlyphTabCompletion", "player", "Lorg/bukkit/entity/Player;", "com.nexomc:core"}
)
public final class FontManager implements Reloadable {
   @NotNull
   private final Object2ObjectOpenHashMap<String, Glyph> glyphMap = new Object2ObjectOpenHashMap();
   @NotNull
   private final Object2ObjectOpenHashMap<String, Glyph> placeholderGlyphMap = new Object2ObjectOpenHashMap();
   @NotNull
   private final Object2ObjectOpenHashMap<Character, String> unicodeGlyphMap = new Object2ObjectOpenHashMap();
   @NotNull
   private final ObjectOpenHashSet<String> tabcompletions = new ObjectOpenHashSet();

   public FontManager() {
      Bukkit.getPluginManager().registerEvents(new FontListener(this), (Plugin)NexoPlugin.Companion.instance());
   }

   @NotNull
   public final Object2ObjectOpenHashMap<String, Glyph> getPlaceholderGlyphMap() {
      return this.placeholderGlyphMap;
   }

   @NotNull
   public final Object2ObjectOpenHashMap<Character, String> getUnicodeGlyphMap() {
      return this.unicodeGlyphMap;
   }

   @NotNull
   public final ObjectOpenHashSet<String> getTabcompletions() {
      return this.tabcompletions;
   }

   public void reload() {
      Iterable parseGlyphConfigs = (Iterable)NexoPlugin.Companion.instance().getConfigsManager$com_nexomc_core().parseGlyphConfigs();

      for(Object element : parseGlyphConfigs) {
         Glyph p0 = (Glyph)element;
         int value = 0;
         this.registerGlyph(p0);
      }

      NexoTranslator.Companion.registerTranslations();
      ProxyHelpers.INSTANCE.handleNexoProxyGlyphData();
      Collection onlinePlayers = Bukkit.getOnlinePlayers();
      if (!onlinePlayers.isEmpty()) {
         ProxyHelpers.INSTANCE.sendProxyGlyphData();
      }

   }

   public final void registerGlyph(@NotNull Glyph glyph) {
      ((Map)this.glyphMap).put(glyph.getId(), glyph);
      if (!(glyph instanceof ReferenceGlyph)) {
         for(String unicodes : glyph.getUnicodes()) {
            int value = 0;

            for(int i = unicodes.length(); value < i; ++value) {
               char ch = unicodes.charAt(value);
               ((Map)this.unicodeGlyphMap).put(ch, glyph.getId());
            }
         }

         for(String placeholder : glyph.getPlaceholders()) {
            ((Map)this.placeholderGlyphMap).put(placeholder, glyph);
         }

         if (glyph.getTabcomplete()) {
            if (Settings.UNICODE_COMPLETIONS.toBool()) {
               ((Collection)this.tabcompletions).add(glyph.getFormattedUnicodes());
            } else {
               CollectionsKt.addAll((Collection)this.tabcompletions, (Iterable)glyph.getPlaceholders());
            }
         }

      }
   }

   @NotNull
   public final Collection<Glyph> glyphs() {
      Collection collection = this.glyphMap.values();
      return collection;
   }

   @NotNull
   public final List<Glyph> emojis() {
      Collection collection = this.glyphMap.values();
      Iterable iterable = (Iterable)collection;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         Glyph p0 = (Glyph)element;
         int value = 0;
         if (p0.isEmoji()) {
            destination.add(element);
         }
      }

      iterable = (Iterable)((List)destination);
      return CollectionsKt.sortedWith(iterable, new FontManager$emojis$$inlined$sortedBy$1());
   }

   @NotNull
   public final Glyph glyphFromName(@Nullable String id) {
      Glyph glyph = this.glyphMap.get(id);
      if (glyph == null) {
         Object obj = this.glyphMap.get("required");
         glyph = (Glyph)obj;
      }

      return glyph;
   }

   @Nullable
   public final Glyph glyphFromID(@Nullable String id) {
      return this.glyphMap.get(id);
   }

   @Nullable
   public final Glyph glyphFromPlaceholder(@Nullable String word) {
      return this.placeholderGlyphMap.get(word);
   }

   public final void sendGlyphTabCompletion(@NotNull Player player) {
      player.removeCustomChatCompletions((Collection)this.tabcompletions);
      Collection collection = this.glyphMap.values();
      Iterable iterable = (Iterable)collection;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         Glyph it = (Glyph)element;
         int value = 0;
         if (it.getTabcomplete() && it.hasPermission(player)) {
            destination.add(element);
         }
      }

      iterable = (Iterable)((List)destination);
      destination = (Collection)(new ArrayList());

      for(Object obj : iterable) {
         Glyph it = (Glyph)obj;
         int value2 = 0;
         Iterable list = (Iterable)(Settings.UNICODE_COMPLETIONS.toBool() ? CollectionsKt.listOf(it.getFormattedUnicodes()) : it.getPlaceholders());
         CollectionsKt.addAll(destination, list);
      }

      List playerCompletions = (List)destination;
      player.addCustomChatCompletions((Collection)playerCompletions);
   }
}
