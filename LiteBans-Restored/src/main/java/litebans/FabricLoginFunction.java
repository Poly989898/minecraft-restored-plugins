package litebans;

import com.mojang.brigadier.CommandDispatcher;
import java.util.function.KotlinFunction;

public final class FabricLoginFunction implements KotlinFunction {
   final ConfigModule b_f;
   final FabricAltsCommands c_f;

   public FabricLoginFunction(ConfigModule var1, FabricAltsCommands var2) {
      this.b_f = var1;
      this.c_f = var2;
   }

   public final void a_12(Object var1) {
      String[] var22 = AbstractCommand.d_f.AbstractPlatform(this.b_f);
      FabricAltsCommands var3 = this.c_f;
      boolean var4 = false;
      int var5 = 0;

      for (int var6 = var22.length; var5 < var6; var5++) {
         String var8;
         FabricAltsCommands var10;
         Object var11;
         CommandDispatcher var12;
         StringBuilder var10000;
         String var10001;
         label73: {
            label72: {
               label71: {
                  String var7 = var22[var5];
                  var8 = var7;
                  boolean var9 = false;
                  var10 = var3;
                  var11 = null;
                  Intrinsics.b_2(var1, "");
                  var12 = (CommandDispatcher)var1;
                  boolean var13 = false;
                  var10000 = new StringBuilder().append("litebans.");
                  MutedCommandFilter var14 = AbstractCommand.d_f;
                  boolean var15 = false;
                  String var16 = var8;
                  switch (var16.hashCode()) {
                     case -1396405339:
                        if (var16.equals("ban-ip")) {
                           break label71;
                        }
                        break;
                     case -1062781696:
                        if (var16.equals("muteip")) {
                           break label72;
                        }
                        break;
                     case -840405966:
                        if (var16.equals("unmute")) {
                           var10001 = "unmute.own";
                           break label73;
                        }
                        break;
                     case -840127329:
                        if (var16.equals("unwarn")) {
                           var10001 = "unwarn.own";
                           break label73;
                        }
                        break;
                     case 93503862:
                        if (var16.equals("banip")) {
                           break label71;
                        }
                        break;
                     case 111426262:
                        if (var16.equals("unban")) {
                           var10001 = "unban.own";
                           break label73;
                        }
                        break;
                     case 1231784084:
                        if (var16.equals("tempipmute")) {
                           break label72;
                        }
                        break;
                     case 1351111124:
                        if (var16.equals("tempmuteip")) {
                           break label72;
                        }
                        break;
                     case 1972486690:
                        if (var16.equals("tempbanip")) {
                           break label71;
                        }
                        break;
                     case 1979386420:
                        if (var16.equals("tempipban")) {
                           break label71;
                        }
                  }

                  var10001 = var8;
                  break label73;
               }

               var10001 = "ipban";
               break label73;
            }

            var10001 = "ipmute";
         }

         LiteAbstractCommandSub var23;
         FabricAltsCommands var24;
         label54: {
            String var17 = var10000.append(var10001).toString();
            var23 = new LiteAbstractCommandSub(var8, var8, (BrigadierCommand)var11, var17, var10);
            var24 = var10;
            MutedCommandFilter var25 = AbstractCommand.d_f;
            String var18 = var23.getName();
            boolean var19 = false;
            String var20 = var18;
            switch (var20.hashCode()) {
               case -1320563219:
                  if (var20.equals("dupeip")) {
                     String[] var21 = new String[]{"alts", "litebans:alts", "checkalts", "litebans:checkalts", "litebans:dupeip"};
                     var10001 = var21;
                     break label54;
                  }
                  break;
               case 712910260:
                  if (var20.equals("staffhistory")) {
                     String[] var29 = new String[]{"staffhist", "litebans:staffhistory"};
                     var10001 = var29;
                     break label54;
                  }
                  break;
               case 926934164:
                  if (var20.equals("history")) {
                     String[] var28 = new String[]{"hist", "litebans:history"};
                     var10001 = var28;
                     break label54;
                  }
            }

            String[] var30 = new String[]{"litebans:" + var18};
            var10001 = var30;
         }

         String[] var26 = var10001;
         boolean var27 = false;
         var10.AbstractPlatform(new BrigadierCommand(var23, var23.getPermission(), var24.a_f, var26), var12);
      }
   }

   @Override
   public Object apply(Object var1) {
      this.a_12(var1);
      return Unit.a_f;
   }
}
