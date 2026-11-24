package net.Indyuce.mmoitems.listener.reforging;

import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ReforgeOptions;
import net.Indyuce.mmoitems.api.event.MMOItemReforgeEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.util.MMOItemReforger;
import net.Indyuce.mmoitems.stat.data.EnchantListData;
import net.Indyuce.mmoitems.stat.data.GemSocketsData;
import net.Indyuce.mmoitems.stat.data.GemstoneData;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RFGKeepGems implements Listener {
   @EventHandler
   public void onReforge(MMOItemReforgeEvent var1) {
      if (var1.getOptions().shouldKeepGemStones()) {
         GemSocketsData var2 = (GemSocketsData)var1.getOldMMOItem().getData(ItemStats.GEM_SOCKETS);
         if (var2 != null) {
            GemSocketsData var3 = (GemSocketsData)var1.getNewMMOItem().getData(ItemStats.GEM_SOCKETS);
            ArrayList<GemstoneData> var4 = new ArrayList();
            if (var3 != null) {
               ArrayList<GemstoneData> var5 = new ArrayList();
               ArrayList<String> var6 = new ArrayList<>(var3.getEmptySlots());

               for (GemstoneData var9 : new ArrayList<>(var2.getGemstones())) {
                  if (var6.size() == 0) {
                     var4.add(var9);
                  } else {
                     String var10 = var9.getSocketColor();
                     if (var10 == null) {
                        var10 = GemSocketsData.getUncoloredGemSlot();
                     }

                     String var11 = null;

                     for (String var13 : var6) {
                        if (var13.equals(GemSocketsData.getUncoloredGemSlot()) || var10.equals(var13)) {
                           var11 = var13;
                        }
                     }

                     if (var11 == null) {
                        var4.add(var9);
                     } else {
                        MMOItem var28 = var1.getOldMMOItem().extractGemstone(var9);
                        if (var28 == null) {
                           var4.add(var9);
                        } else {
                           MMOItemReforger var29 = new MMOItemReforger(var28.newBuilder().build());
                           if (!var29.canReforge()) {
                              var4.add(var9);
                           } else {
                              var29.setPlayer(var1.getPlayer());
                              if (!var29.reforge(MMOItems.plugin.getLanguage().gemRevisionOptions)) {
                                 var4.add(var9);
                              } else {
                                 var1.getReforger().getReforgingOutput().addAll(var29.getReforgingOutput());
                                 LiveMMOItem var14 = new LiveMMOItem(var29.getResult());
                                 GemstoneData var15 = new GemstoneData(var14, var11, var9.getHistoricUUID());
                                 var15.setLevel(var9.getLevel());
                                 var6.remove(var11);
                                 var5.add(var15);

                                 for (ItemStat var17 : var14.getStats()) {
                                    if (!(var17 instanceof GemStoneStat)) {
                                       StatData var18 = var14.getData(var17);
                                       if (var18 instanceof Mergeable && (!(var18 instanceof EnchantListData) || !var18.isEmpty())) {
                                          StatHistory var19 = var1.getNewMMOItem().computeStatHistory(var17);
                                          var19.registerGemstoneData(var15.getHistoricUUID(), var18);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               GemSocketsData var23 = new GemSocketsData(var6);

               for (GemstoneData var27 : var5) {
                  if (var27 != null) {
                     var23.add(var27);
                  }
               }

               StatHistory var26 = var1.getNewMMOItem().computeStatHistory(ItemStats.GEM_SOCKETS);
               var26.setOriginalData(var23);
               var1.getNewMMOItem().setData(ItemStats.GEM_SOCKETS, var26.recalculate(var1.getNewMMOItem().getUpgradeLevel()));
            } else {
               var4.addAll(var2.getGemstones());
            }

            if (ReforgeOptions.dropRestoredGems) {
               for (GemstoneData var21 : var4) {
                  MMOItem var22 = var1.getOldMMOItem().extractGemstone(var21);
                  if (var22 == null) {
                     MMOItems.print(
                        null,
                        "$bGemstone $r{0} {1} $bno longer exists, it was$f deleted$b from $u{2}$b's {3}$b. ",
                        "RevID",
                        var21.getMMOItemType(),
                        var21.getMMOItemID(),
                        var1.getPlayer() == null ? "null" : var1.getPlayer().getName(),
                        SilentNumbers.getItemName(var1.getReforger().getStack(), false)
                     );
                  } else {
                     MMOItemReforger var24 = new MMOItemReforger(var22.newBuilder().build());
                     if (var24.canReforge()) {
                        var24.setPlayer(var1.getPlayer());
                        var24.reforge(MMOItems.plugin.getLanguage().gemRevisionOptions);
                        var1.getReforger().getReforgingOutput().addAll(var24.getReforgingOutput());
                        var1.getReforger().addReforgingOutput(var24.getResult());
                     } else {
                        var1.getReforger().addReforgingOutput(var22.newBuilder().build());
                     }
                  }
               }
            }
         }
      }
   }
}
