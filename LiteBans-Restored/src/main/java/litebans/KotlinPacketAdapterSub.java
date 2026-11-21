package litebans;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Login.Server;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import java.util.Arrays;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class KotlinPacketAdapterSub extends PacketAdapter {
   public static final KotlinPacketAdapterSubCompanion KOTLIN_PACKET_ADAPTER_SUB_COMPANION = new KotlinPacketAdapterSubCompanion(null);
   private final Platform platform;
   private final ConfigModule configModule4;
   private static final Lazy return2 = KotlinStub5.computeLazy(() -> {
      PacketType packetType;
      try {
         packetType = Server.DISCONNECT;
      } catch (Throwable e) {
         packetType = null;
      }

      return packetType;
   });

   public KotlinPacketAdapterSub(@NotNull Platform platform) {
      PlatformBridge bridge = platform.createBridge();
      Intrinsics.checkNotNullExpressionValue(bridge, "");
      Plugin plugin = BukkitBridge.asPluginWithDefaults((BukkitBridge)bridge, null, 1, null);
      PacketType[] packetTypes = new PacketType[1];
      Object obj = KotlinPacketAdapterSubCompanion.computeObj(KOTLIN_PACKET_ADAPTER_SUB_COMPANION);
      Intrinsics.checkNotNullExpressionValue(obj, "");
      packetTypes[0] = (PacketType)obj;
      super(PacketAdapter.params(plugin, packetTypes).optionAsync());
      this.platform = platform;
      this.configModule4 = (ConfigModule)this.platform.getModule(ConfigModule.class);
   }

   public void onPacketSending(@NotNull PacketEvent packetEvent) {
      try {
         if (ProtocolLibHelper.access$getClass(packetEvent.getPlayer())) {
            return;
         }

         PacketContainer packet = packetEvent.getPacket();
         WrappedChatComponent read2 = (WrappedChatComponent)packet.getChatComponents().read(0);
         ConfigModule configModule = this.configModule4;
         if (configModule.isVerbose()) {
            configModule.logDebug("1 " + (read2 != null ? read2.getJson() : null));
         }

         ChatBaseComponent[] chatBaseComponents = TextComponentDeserializer.computeChatBaseComponentArray(read2.getJson());
         String text = ChatTextComponent.computeText(Arrays.copyOf(chatBaseComponents, chatBaseComponents.length));
         ConfigModule configModule2 = this.configModule4;
         if (configModule2.isVerbose()) {
            configModule2.logDebug("2 " + text);
         }

         Intrinsics.checkNotNull(text);
         if (StringsKt.containsWithDefaults((CharSequence)text, (CharSequence)LiteColor.MAGIC, false, 2, null)) {
            ChatBaseComponent[] fromLegacyText2 = ChatTextComponent.fromLegacyText(text);
            WrappedChatComponent fromJson2 = WrappedChatComponent.fromJson(TextComponentDeserializer.computeText(Arrays.copyOf(fromLegacyText2, fromLegacyText2.length)));
            ConfigModule configModule3 = this.configModule4;
            if (configModule3.isVerbose()) {
               configModule3.logDebug("3 " + fromJson2.getJson());
            }

            packet.getChatComponents().write(0, fromJson2);
         }
      } catch (Throwable e) {
         ConfigModule.handleConfigModule(this.configModule4, e, 0, 2, null);
         ProtocolLibHelper.handlePluginModule2(this.configModule4, this);
      }
   }

   public static final Lazy obtainLazy() {
      return return2;
   }
}
