package litebans;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class FabricHelperA {
   public static void a_1(Object var0, String var1, byte[] var2) {
      ServerPlayNetworking.send((ServerPlayer)var0, new FabricLoginPayload(var1, var2));
   }
}
