package litebans;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import java.util.function.KotlinFunction;
import java.util.function.Supplier;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FabricCommandSource {
   void a_1(Object var1, String var2);

   Object f_16();

   String a_11(Object var1);

   boolean a_56();

   boolean e_18();

   @Nullable List c_30();

   void a_1(Runnable var1);

   void b_1(Object var1, String var2);

   int b_38();

   Object d_1(Object var1);

   void UInt(@Nullable Object var1, String var2);

   void AbstractPlatform(Object var1, String var2, byte[] var3);

   LiteSender c_2(Object var1);

   LiteSender b_3(Object var1);

   LiteSender AbstractPlatform(Object var1, @Nullable Supplier var2);

   PlatformHook d_22();

   void a_1(MinecraftServer var1);

   void AbstractPlatform(KotlinFunction var1);

   void a_1(BrigadierCommand var1, CommandDispatcher var2);

   boolean YamlThreadLocal(@Nullable Object var1, @NotNull String var2);
}
