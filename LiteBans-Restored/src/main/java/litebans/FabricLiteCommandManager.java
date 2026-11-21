package litebans;

import com.mojang.brigadier.CommandDispatcher;

@PlatformMarker(AbstractPlatform = 3)
public class FabricLiteCommandManager extends LiteCommandManager {
   public FabricLiteCommandManager(Platform var1) {
      super(var1);
   }

   @Override
   public boolean d_2() {
      return this.a_f.EntryQueueModule() == 3;
   }

   @Override
   public void e_2() {
   }

   @Override
   public void a_1() {
      super.a_1();
      ((FabricPlugin)this.a_f).g_3().AbstractPlatform(this::a_1);
   }

   public void AbstractPlatform(BrigadierCommand var1, CommandDispatcher var2) {
      ((FabricPlugin)this.a_f).g_3().a_1(var1, var2);
   }

   private final Object a_1(Object var1) {
      for (LiteCommand var3 : this.ArchiveInputStream()) {
         this.AbstractPlatform(new BrigadierCommand(var3, var3.getPermission(), this.a_f, var3.getAliases()), (CommandDispatcher)var1);
      }

      return void.class;
   }
}
