package com.nexomc.nexo.proxy;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.pack.server.NexoPackServer;
import com.nexomc.nexo.utils.logs.Logs;
import java.util.Collection;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import net.kyori.adventure.resource.ResourcePackInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u000e\u001a\u00020\u000fJ\u001e\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u0005J\u0006\u0010\u0015\u001a\u00020\u000fJ\u0006\u0010\u0016\u001a\u00020\u000fJ\u0006\u0010\u0017\u001a\u00020\u000fR\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0018"},
   d2 = {"Lcom/nexomc/nexo/proxy/ProxyHelpers;", "", "<init>", "()V", "PACK_HASH_CHANNEL", "", "GLYPH_INFO_CHANNEL", "HANDSHAKE_CHANNEL", "queuedPackInfo", "Lcom/nexomc/nexo/proxy/ProxyPackInfo;", "queuedGlyphInfo", "Lcom/nexomc/nexo/proxy/ProxyGlyphInfo;", "proxyHasPlugin", "", "initialize", "", "handleNexoProxyData", "packServer", "Lcom/nexomc/nexo/pack/server/NexoPackServer;", "unobfHash", "obfHash", "handleNexoProxyGlyphData", "sendProxyPackData", "sendProxyGlyphData", "com.nexomc:core"}
)
public final class ProxyHelpers {
   @NotNull
   public static final ProxyHelpers INSTANCE = new ProxyHelpers();
   @NotNull
   public static final String PACK_HASH_CHANNEL = "nexo:pack_hash";
   @NotNull
   public static final String GLYPH_INFO_CHANNEL = "nexo:glyph_info";
   @NotNull
   public static final String HANDSHAKE_CHANNEL = "nexo:proxy_handshake";
   @Nullable
   private static ProxyPackInfo queuedPackInfo;
   @Nullable
   private static ProxyGlyphInfo queuedGlyphInfo;
   private static boolean proxyHasPlugin;

   private ProxyHelpers() {
   }

   public final void initialize() {
      ProxyHelpers proxyHelpers = this;

      try {
         ProxyHelpers proxyHelpers2 = proxyHelpers;
         int value = 0;
         Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin)NexoPlugin.Companion.instance(), "nexo:pack_hash");
         Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin)NexoPlugin.Companion.instance(), "nexo:glyph_info");
         ResultShim.ok(Bukkit.getMessenger().registerIncomingPluginChannel((Plugin)NexoPlugin.Companion.instance(), "nexo:proxy_handshake", ((a0_, a1_, a2_) -> ProxyHelpers.initialize$lambda$0$0(this, a0_, a1_, a2_))));
      } catch (Throwable e) {
         Object obj = ResultShim.ok(ResultKt.createFailure(e));
      }

   }

   public final void handleNexoProxyData(@NotNull NexoPackServer packServer, @NotNull String unobfHash, @NotNull String obfHash) {
      ResourcePackInfo packInfo = packServer.packInfo();
      queuedPackInfo = new ProxyPackInfo(packInfo != null ? packInfo.id() : null, unobfHash, obfHash);
   }

   public final void handleNexoProxyGlyphData() {
      queuedGlyphInfo = new ProxyGlyphInfo();
   }

   public final void sendProxyPackData() {
      if (queuedPackInfo != null && proxyHasPlugin) {
         Collection onlinePlayers = Bukkit.getOnlinePlayers();
         Player player2 = (Player)CollectionsKt.firstOrNull((Iterable)onlinePlayers);
         if (player2 != null) {
            Player player = player2;
            Plugin plugin = (Plugin)NexoPlugin.Companion.instance();
            ProxyPackInfo proxyPackInfo = queuedPackInfo;
            player.sendPluginMessage(plugin, "nexo:pack_hash", proxyPackInfo.getAsBytes());
            Logs.INSTANCE.debug("Informed NexoProxy about ResourcePack-hashes");
         }
      }
   }

   public final void sendProxyGlyphData() {
      if (queuedGlyphInfo != null && proxyHasPlugin) {
         Collection onlinePlayers = Bukkit.getOnlinePlayers();
         Player player2 = (Player)CollectionsKt.firstOrNull((Iterable)onlinePlayers);
         if (player2 != null) {
            Player player = player2;
            Plugin plugin = (Plugin)NexoPlugin.Companion.instance();
            ProxyGlyphInfo proxyGlyphInfo = queuedGlyphInfo;
            player.sendPluginMessage(plugin, "nexo:glyph_info", proxyGlyphInfo.getAsBytes());
            Logs.INSTANCE.debug("Informed NexoProxy about Glyph-Info");
            queuedGlyphInfo = null;
         }
      }
   }

   private static final void initialize$lambda$0$0(ProxyHelpers proxyHelpers, String channel, Player player, byte[] bs) {
      proxyHasPlugin = true;
      proxyHelpers.sendProxyPackData();
      if (queuedGlyphInfo == null) {
         proxyHelpers.handleNexoProxyGlyphData();
         proxyHelpers.sendProxyGlyphData();
      }

   }
}
