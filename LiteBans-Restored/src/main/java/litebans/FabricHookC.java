package litebans;

import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.StrictJsonParser;

public class FabricHookC implements PlatformHook {
   @Override
   public Object AbstractPlatform(CharSequence var1) {
      return Component.literal(var1.toString());
   }

   @Override
   public Object PunishmentTypeSwitchMap2(CharSequence var1) {
      return this.a_17(var1);
   }

   private final Component a_17(CharSequence var1) {
      return (Component)ComponentSerialization.CODEC
         .parse(RegistryAccess.EMPTY.createSerializationContext(JsonOps.INSTANCE), StrictJsonParser.parse(var1.toString()))
         .getOrThrow(var0 -> new IllegalArgumentException(var0));
   }
}
