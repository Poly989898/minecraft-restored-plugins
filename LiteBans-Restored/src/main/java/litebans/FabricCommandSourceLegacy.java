package litebans;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_2540;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3222;

public class FabricCommandSourceLegacy extends FabricCommandSourceV121 {
   protected final PlatformHook e_f = new FabricHookB(this);

   public FabricCommandSourceLegacy(Platform var1) {
      super(var1);
   }

   @Override
   public Object d_1(Object var1) {
      return var1 instanceof class_3222 ? ((class_3222)var1).method_5671() : var1;
   }

   @Override
   public PlatformHook d_22() {
      return this.e_f;
   }

   @Override
   public void a_1(Object var1, String var2) {
      int var5 = this.b_38();
      GameVersion var4 = var5 >= 770 ? GameVersion.V1_21_5 : GameVersion.V1_16;
      ((class_3222)var1).field_13987.method_14367((class_2561)this.e_f.PunishmentTypeSwitchMap2(LiteHelper6.AbstractPlatform(var2, var4)));
   }

   @Override
   public void AbstractPlatform(Object var1, String var2, byte[] var3) {
      class_2540 var5 = PacketByteBufs.create();
      var5.writeBytes(var3);
      ((class_3222)var1).field_13987.method_14364(ServerPlayNetworking.createS2CPacket(class_2960.method_12829(var2), var5));
   }
}
