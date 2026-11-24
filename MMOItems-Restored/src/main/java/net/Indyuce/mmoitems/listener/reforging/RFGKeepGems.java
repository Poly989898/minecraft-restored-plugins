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
   public void onReforge(MMOItemReforgeEvent event) {
      if (event.getOptions().shouldKeepGemStones()) {
         GemSocketsData data = (GemSocketsData)event.getOldMMOItem().getData(ItemStats.GEM_SOCKETS);
         if (data != null) {
            GemSocketsData newSocketsData = (GemSocketsData)event.getNewMMOItem().getData(ItemStats.GEM_SOCKETS);
            ArrayList<GemstoneData> gemstoneDatas = new ArrayList();
            if (newSocketsData != null) {
               ArrayList<GemstoneData> newGemstones = new ArrayList();
               ArrayList<String> texts = new ArrayList<>(newSocketsData.getEmptySlots());

               for (GemstoneData gemstoneData : new ArrayList<>(data.getGemstones())) {
                  if (texts.size() == 0) {
                     gemstoneDatas.add(gemstoneData);
                  } else {
                     String socketColor = gemstoneData.getSocketColor();
                     if (socketColor == null) {
                        socketColor = GemSocketsData.getUncoloredGemSlot();
                     }

                     String text = null;

                     for (String gemLine : texts) {
                        if (gemLine.equals(GemSocketsData.getUncoloredGemSlot()) || socketColor.equals(gemLine)) {
                           text = gemLine;
                        }
                     }

                     if (text == null) {
                        gemstoneDatas.add(gemstoneData);
                     } else {
                        MMOItem mmoItem = event.getOldMMOItem().extractGemstone(gemstoneData);
                        if (mmoItem == null) {
                           gemstoneDatas.add(gemstoneData);
                        } else {
                           MMOItemReforger mmoItemReforger = new MMOItemReforger(mmoItem.newBuilder().build());
                           if (!mmoItemReforger.canReforge()) {
                              gemstoneDatas.add(gemstoneData);
                           } else {
                              mmoItemReforger.setPlayer(event.getPlayer());
                              if (!mmoItemReforger.reforge(MMOItems.plugin.getLanguage().gemRevisionOptions)) {
                                 gemstoneDatas.add(gemstoneData);
                              } else {
                                 event.getReforger().getReforgingOutput().addAll(mmoItemReforger.getReforgingOutput());
                                 LiveMMOItem liveMMOItem = new LiveMMOItem(mmoItemReforger.getResult());
                                 GemstoneData newGemstoneData = new GemstoneData(liveMMOItem, text, gemstoneData.getHistoricUUID());
                                 newGemstoneData.setLevel(gemstoneData.getLevel());
                                 texts.remove(text);
                                 newGemstones.add(newGemstoneData);

                                 for (ItemStat stat : liveMMOItem.getStats()) {
                                    if (!(stat instanceof GemStoneStat)) {
                                       StatData statData = liveMMOItem.getData(stat);
                                       if (statData instanceof Mergeable && (!(statData instanceof EnchantListData) || !statData.isEmpty())) {
                                          StatHistory statHistory = event.getNewMMOItem().computeStatHistory(stat);
                                          statHistory.registerGemstoneData(newGemstoneData.getHistoricUUID(), statData);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               GemSocketsData gemSocketsData = new GemSocketsData(texts);

               for (GemstoneData gemstoneData : newGemstones) {
                  if (gemstoneData != null) {
                     gemSocketsData.add(gemstoneData);
                  }
               }

               StatHistory statHistory = event.getNewMMOItem().computeStatHistory(ItemStats.GEM_SOCKETS);
               statHistory.setOriginalData(gemSocketsData);
               event.getNewMMOItem().setData(ItemStats.GEM_SOCKETS, statHistory.recalculate(event.getNewMMOItem().getUpgradeLevel()));
            } else {
               gemstoneDatas.addAll(data.getGemstones());
            }

            if (ReforgeOptions.dropRestoredGems) {
               for (GemstoneData gemstoneData : gemstoneDatas) {
                  MMOItem mmoItem = event.getOldMMOItem().extractGemstone(gemstoneData);
                  if (mmoItem == null) {
                     MMOItems.print(
                        null,
                        "$bGemstone $r{0} {1} $bno longer exists, it was$f deleted$b from $u{2}$b's {3}$b. ",
                        "RevID",
                        gemstoneData.getMMOItemType(),
                        gemstoneData.getMMOItemID(),
                        event.getPlayer() == null ? "null" : event.getPlayer().getName(),
                        SilentNumbers.getItemName(event.getReforger().getStack(), false)
                     );
                  } else {
                     MMOItemReforger mmoItemReforger = new MMOItemReforger(mmoItem.newBuilder().build());
                     if (mmoItemReforger.canReforge()) {
                        mmoItemReforger.setPlayer(event.getPlayer());
                        mmoItemReforger.reforge(MMOItems.plugin.getLanguage().gemRevisionOptions);
                        event.getReforger().getReforgingOutput().addAll(mmoItemReforger.getReforgingOutput());
                        event.getReforger().addReforgingOutput(mmoItemReforger.getResult());
                     } else {
                        event.getReforger().addReforgingOutput(mmoItem.newBuilder().build());
                     }
                  }
               }
            }
         }
      }
   }
}
