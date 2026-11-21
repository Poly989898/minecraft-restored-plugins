package litebans;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftVersion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class KotlinPacketAdapterSub2 extends PacketAdapter {
   public static final KotlinPacketAdapterSub2Companion KOTLIN_PACKET_ADAPTER_SUB2_COMPANION = new KotlinPacketAdapterSub2Companion(null);
   private final Platform platform;
   private final PunishmentLookupModule punishmentLookupModule;
   private final BroadcastModule broadcastModule;
   private final ConfigModule configModule;
   private final DatabaseModule databaseModule;
   private static final Lazy return2 = KotlinStub5.computeLazy(() -> {
      PacketType packetType;
      try {
         packetType = Client.CHAT;
      } catch (Throwable e) {
         packetType = null;
      }

      return packetType;
   });
   private static final Lazy return3 = KotlinStub5.computeLazy(() -> {
      PacketType packetType;
      try {
         packetType = MinecraftVersion.WILD_UPDATE.atOrAbove() ? Client.CHAT_COMMAND : null;
      } catch (Throwable e) {
         packetType = null;
      }

      return packetType;
   });

   public KotlinPacketAdapterSub2(@NotNull Platform platform) {
      PlatformBridge bridge = platform.createBridge();
      Intrinsics.checkNotNullExpressionValue(bridge, "");
      Plugin plugin = BukkitBridge.asPluginWithDefaults((BukkitBridge)bridge, null, 1, null);
      Object[] objs = new Object[]{KotlinPacketAdapterSub2Companion.computeObj(KOTLIN_PACKET_ADAPTER_SUB2_COMPANION), KotlinPacketAdapterSub2Companion.computeObj2(KOTLIN_PACKET_ADAPTER_SUB2_COMPANION)};
      var arrayList = new ArrayList();
      int value = 0;

      for (int i = objs.length; value < i; value++) {
         Object obj = objs[value];
         PacketType packetType = obj instanceof PacketType ? (PacketType)obj : null;
         if ((obj instanceof PacketType ? (PacketType)obj : null) != null) {
            arrayList.add(packetType);
         }
      }

      Collection collection = arrayList;
      PacketType[] array = (PacketType[])collection.toArray(new PacketType[0]);
      super(PacketAdapter.params(plugin, Arrays.copyOf(array, array.length)).optionAsync());
      this.platform = platform;
      this.punishmentLookupModule = (PunishmentLookupModule)this.platform.getModule(PunishmentLookupModule.class);
      this.broadcastModule = (BroadcastModule)this.platform.getModule(BroadcastModule.class);
      this.configModule = (ConfigModule)this.platform.getModule(ConfigModule.class);
      this.databaseModule = (DatabaseModule)this.platform.getModule(DatabaseModule.class);
   }

   public void onPacketReceiving(@NotNull PacketEvent packetEvent) {
      PacketType packetType = packetEvent.getPacketType();
      if (!packetEvent.isCancelled() && !ProtocolLibHelper.checkPlayer(packetEvent.getPlayer()) && (ProtocolLibHelper.computeIntValue(packetEvent.getPlayer()) < 759 || Intrinsics.areEqual(packetType, KotlinPacketAdapterSub2Companion.computeObj2(KOTLIN_PACKET_ADAPTER_SUB2_COMPANION)))) {
         String text = (Intrinsics.areEqual(packetType, KotlinPacketAdapterSub2Companion.computeObj2(KOTLIN_PACKET_ADAPTER_SUB2_COMPANION)) ? "/" : "") + this.computeText(packetEvent);
         boolean flag = KotlinPacketAdapterSub2Companion.computeObj2(KOTLIN_PACKET_ADAPTER_SUB2_COMPANION) != null && !Intrinsics.areEqual(packetType, KotlinPacketAdapterSub2Companion.computeObj2(KOTLIN_PACKET_ADAPTER_SUB2_COMPANION));
         this.punishmentLookupModule.getMuteChatTask().handleObj(packetEvent, text, this.platform.wrapSender(packetEvent.getPlayer()), flag);
      }
   }

   public final String computeText(@NotNull PacketEvent packetEvent) {
      String safely = (String)packetEvent.getPacket().getStrings().readSafely(0);
      if (safely == null) {
         safely = "";
      }

      return safely;
   }

   public static final Lazy obtainLazy() {
      return return2;
   }

   public static final Lazy obtainLazy2() {
      return return3;
   }
}
