package litebans;

import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@PlatformMarker(AbstractPlatform = 3)
public final class FabricAltsCommands extends FabricLiteCommandManager {
   public FabricAltsCommands(@NotNull Platform var1) {
      super(var1);
   }

   @Override
   public boolean d_2() {
      return this.a_f.EntryQueueModule() == 3;
   }

   @Override
   public void AbstractPlatform(@NotNull LiteCommand var1, @NotNull LiteSender var2, @NotNull String[] var3) {
      this.AbstractPlatform(var2, var1.getName(), var3);
   }

   @Override
   public void AbstractPlatform(@NotNull LiteSender var1, @NotNull String var2, @NotNull String[] var3) {
      MutedCommandFilter var5 = AbstractCommand.d_f;
      boolean var6 = false;
      String[] var7 = var3;
      boolean var8 = false;
      int var9 = 0;
      int var10 = var7.length;

      boolean var10000;
      while (true) {
         if (var9 >= var10) {
            var10000 = true;
            break;
         }

         String var11 = var7[var9];
         String var12 = var11;
         boolean var13 = false;
         if (StringsKt.b_5(var12)) {
            var10000 = false;
            break;
         }

         var9++;
      }

      String[] var47;
      if (var10000) {
         var47 = var3;
      } else {
         String[] var21 = var3;
         boolean var25 = false;
         String[] var31 = var21;
         var var38 = new ArrayList();
         boolean var43 = false;
         int var45 = 0;

         for (int var46 = var31.length; var45 < var46; var45++) {
            String var14 = var31[var45];
            String var15 = var14;
            boolean var16 = false;
            if (!StringsKt.b_5(var15)) {
               var38.add(var14);
            }
         }

         var var22 = var38;
         boolean var26 = false;
         Collection var32 = var22;
         var47 = var32.toArray(new String[0]);
      }

      String[] var17 = var47;
      String var18 = var2;
      String var19 = StringsKt.AbstractPlatform(var18, "litebans:", null, 2, null);
      AbstractCommand var20 = (AbstractCommand)this.AbstractPlatform(var19);
      if (var20 != null) {
         String var23 = var20.getPermission();
         if (var23 != null) {
            LiteSender var27 = var1;
            boolean var33 = false;
            LiteSender var39 = var27;
            boolean var44 = false;
            if (!var39.e_2(var23)) {
               LiteSender var28 = var1;
               CharSequence var34 = Message.cZ_f;
               boolean var40 = false;
               CommandOutput.AbstractPlatform(CommandTask.a_f, var28, var34, null, 4, null);
               return;
            }
         }

         FabricAltsCommands var29 = this;
         boolean var35 = false;
         var29.a_f.PunishmentTypeSwitchMap2(new LiteRunnableImpl2(var1, var17, var20, var19));
      } else {
         String[] var24 = AltAccountPunisher.AbstractPlatform(AbstractCommand.d_f, var19, var17);
         FabricAltsCommands var30 = this;
         FabricAltsCommands var36 = this;
         boolean var41 = false;
         AbstractCommand var48 = (AbstractCommand)var36.AbstractPlatform("ban");
         if (var48 == null) {
            StandardKt.a_11();
            throw new CommandAbortException();
         }

         AbstractCommand var37 = var48;
         boolean var42 = false;
         var30.a_f.PunishmentTypeSwitchMap2(new LiteRunnableImpl2(var1, var24, var37, var19));
      }
   }

   @Override
   public void a_1() {
      ConfigModule var5 = (ConfigModule)this.a_f.AbstractPlatform(ConfigModule.class);
      var5.a_1();
      super.a_1();
      this.a_f.AbstractPlatform(this);
      FabricAltsCommands var2 = this;
      Intrinsics.a_15(var5);
      ConfigModule var3 = var5;
      boolean var4 = false;
      Platform var10000 = var2.a_f;
      Intrinsics.b_2(var2.a_f, "");
      ((FabricPlugin)var10000).g_3().AbstractPlatform(new FabricLoginFunction(var3, var2));
   }

   @Override
   public void AbstractPlatform(@NotNull BrigadierCommand var1, @NotNull CommandDispatcher var2) {
      ConfigModule var22 = (ConfigModule)this.a_f.AbstractPlatform(ConfigModule.class);
      if (var22.e_13(var1.getName())) {
         super.AbstractPlatform(var1, var2);
         if (!(var1.a_185() instanceof LiteAbstractCommandSub)) {
            String[][] var4;
            String[] var10002;
            label121: {
               var4 = new String[][]{var1.getAliases(), null};
               MutedCommandFilter var5 = AbstractCommand.d_f;
               String var6 = var1.getName();
               boolean var7 = false;
               String var8 = var6;
               switch (var8.hashCode()) {
                  case -1320563219:
                     if (var8.equals("dupeip")) {
                        String[] var9 = new String[]{"alts", "litebans:alts", "checkalts", "litebans:checkalts", "litebans:dupeip"};
                        var10002 = var9;
                        break label121;
                     }
                     break;
                  case 712910260:
                     if (var8.equals("staffhistory")) {
                        String[] var34 = new String[]{"staffhist", "litebans:staffhistory"};
                        var10002 = var34;
                        break label121;
                     }
                     break;
                  case 926934164:
                     if (var8.equals("history")) {
                        String[] var33 = new String[]{"hist", "litebans:history"};
                        var10002 = var33;
                        break label121;
                     }
               }

               String[] var35 = new String[]{"litebans:" + var6};
               var10002 = var35;
            }

            var4[1] = var10002;
            Iterable var23 = ArraysKt.AbstractPlatform(var4);
            boolean var25 = false;
            Iterable var27 = var23;
            var var29 = new ArrayList();
            boolean var31 = false;

            for (Object var10 : var27) {
               String var11 = (String)var10;
               boolean var12 = false;
               Intrinsics.a_15(var11);
               if (!StringsKt.UInt(var11, ':', false, 2, null)) {
                  var29.add(var10);
               }
            }

            var var24 = var29;
            boolean var26 = false;

            for (Object var30 : var24) {
               FabricAltsCommands var38;
               String var39;
               String var40;
               StringBuilder var10000;
               String var10001;
               label101: {
                  label100: {
                     label99: {
                        String var32 = (String)var30;
                        boolean var37 = false;
                        var38 = this;
                        Intrinsics.a_15(var32);
                        var39 = var32;
                        var40 = var1.getName();
                        boolean var13 = false;
                        var10000 = new StringBuilder().append("litebans.");
                        MutedCommandFilter var14 = AbstractCommand.d_f;
                        boolean var15 = false;
                        String var16 = var39;
                        switch (var16.hashCode()) {
                           case -1396405339:
                              if (var16.equals("ban-ip")) {
                                 break label99;
                              }
                              break;
                           case -1062781696:
                              if (var16.equals("muteip")) {
                                 break label100;
                              }
                              break;
                           case -840405966:
                              if (var16.equals("unmute")) {
                                 var10001 = "unmute.own";
                                 break label101;
                              }
                              break;
                           case -840127329:
                              if (var16.equals("unwarn")) {
                                 var10001 = "unwarn.own";
                                 break label101;
                              }
                              break;
                           case 93503862:
                              if (var16.equals("banip")) {
                                 break label99;
                              }
                              break;
                           case 111426262:
                              if (var16.equals("unban")) {
                                 var10001 = "unban.own";
                                 break label101;
                              }
                              break;
                           case 1231784084:
                              if (var16.equals("tempipmute")) {
                                 break label100;
                              }
                              break;
                           case 1351111124:
                              if (var16.equals("tempmuteip")) {
                                 break label100;
                              }
                              break;
                           case 1972486690:
                              if (var16.equals("tempbanip")) {
                                 break label99;
                              }
                              break;
                           case 1979386420:
                              if (var16.equals("tempipban")) {
                                 break label99;
                              }
                        }

                        var10001 = var39;
                        break label101;
                     }

                     var10001 = "ipban";
                     break label101;
                  }

                  var10001 = "ipmute";
               }

               LiteAbstractCommandSub var41;
               FabricAltsCommands var42;
               label81: {
                  String var17 = var10000.append(var10001).toString();
                  var41 = new LiteAbstractCommandSub(var39, var40, var1, var17, var38);
                  var42 = var38;
                  MutedCommandFilter var43 = AbstractCommand.d_f;
                  String var18 = var41.getName();
                  boolean var19 = false;
                  String var20 = var18;
                  switch (var20.hashCode()) {
                     case -1320563219:
                        if (var20.equals("dupeip")) {
                           String[] var21 = new String[]{"alts", "litebans:alts", "checkalts", "litebans:checkalts", "litebans:dupeip"};
                           var10001 = var21;
                           break label81;
                        }
                        break;
                     case 712910260:
                        if (var20.equals("staffhistory")) {
                           String[] var47 = new String[]{"staffhist", "litebans:staffhistory"};
                           var10001 = var47;
                           break label81;
                        }
                        break;
                     case 926934164:
                        if (var20.equals("history")) {
                           String[] var46 = new String[]{"hist", "litebans:history"};
                           var10001 = var46;
                           break label81;
                        }
                  }

                  String[] var48 = new String[]{"litebans:" + var18};
                  var10001 = var48;
               }

               String[] var44 = var10001;
               boolean var45 = false;
               var38.AbstractPlatform(new BrigadierCommand(var41, var41.getPermission(), var42.a_f, var44), var2);
               if (var1 != null) {
                  var38.AbstractPlatform(var39, var41);
               }
            }
         }
      }
   }

   @Override
   public List AbstractPlatform(@Nullable LiteCommand var1, @NotNull String var2, @NotNull LiteSender var3, @NotNull String[] var4) {
      if (var4.length != 0) {
         LiteSender var10 = var3;
         String var6 = "litebans.tabcomplete";
         boolean var7 = false;
         LiteSender var8 = var10;
         boolean var9 = false;
         if (var8.e_2(var6)) {
            return AbstractCommand.d_f.AbstractPlatform(var1, var2, var3, this.a_f, var4);
         }
      }

      return CollectionsKt.e_62();
   }
}
