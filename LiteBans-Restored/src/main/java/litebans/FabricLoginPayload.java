package litebans;

import net.minecraft.class_2540;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;

public class FabricLoginPayload extends LiteHelper14 implements CustomPacketPayload {
   public FabricLoginPayload(String var1, byte[] var2) {
      super(var1, var2);
   }

   private FabricLoginPayload(class_2540 var1) {
      super("", new byte[var1.readableBytes()]);
      var1.readBytes(this.a_f);
   }

   private final void AbstractPlatform(class_2540 var1) {
      var1.writeBytes(this.a_f);
   }

   public Type type() {
      return new Type(Identifier.parse(this.b_f));
   }
}
