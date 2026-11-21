package it.ytnoos.lpx.check.checks.item;

import it.ytnoos.lpx.api.player.ProtocolPlayer;

public final class NbtSizeLimit {
   private static final int DEFAULT_LIMIT = 4096;
   private static final int DEFAULT_LEGACY_LIMIT = 2048;
   private final int limit;
   private final int legacyLimit;

   private NbtSizeLimit(int value, int value2) {
      this.limit = value;
      this.legacyLimit = value2;
   }

   static NbtSizeLimit defaults() {
      return new NbtSizeLimit(4096, 2048);
   }

   int getLimit(ProtocolPlayer protocolPlayer) {
      return protocolPlayer.isLegacyClientOnHashedServer() ? this.legacyLimit : this.limit;
   }

}
