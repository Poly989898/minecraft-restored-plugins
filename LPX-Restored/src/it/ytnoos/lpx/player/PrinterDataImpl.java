package it.ytnoos.lpx.player;

import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.alert.AlertManager;
import it.ytnoos.lpx.api.player.PrinterData;
import it.ytnoos.lpx.api.player.PrinterStatus;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.utils.TimeUtils;
import it.ytnoos.lpx.config.PrinterConfig;

public class PrinterDataImpl implements PrinterData {
   private final LPXBukkit lpxBukkit;
   private final ProtocolPlayer player;
   private PrinterStatus printerStatus = PrinterStatus.NONE;
   private long lastPlace = TimeUtils.current();
   private long lastUpdate = TimeUtils.current();
   private int blocksPlaced;
   private static final String MODE_PLACEHOLDER = "%mode%";

   public PrinterDataImpl(LPXBukkit lpxBukkit2, ProtocolPlayer protocolPlayer) {
      this.lpxBukkit = lpxBukkit2;
      this.player = protocolPlayer;
   }

   @Override
   public PrinterStatus getPrinterStatus() {
      return this.printerStatus;
   }

   @Override
   public boolean isUsingPrinter() {
      return this.printerStatus != PrinterStatus.NONE;
   }

   @Override
   public void setPrinterMode(boolean flag) {
      this.setPrinterMode(flag ? PrinterStatus.API : PrinterStatus.NONE);
   }

   @Override
   public void setPrinterMode(PrinterStatus printerStatus2) {
      this.printerStatus = printerStatus2;
      PrinterConfig printerConfig = this.lpxBukkit.getConfigManager().getOptionsConfig().getPrinterConfig();
      if (printerConfig.isAlerts()) {
         String string = printerConfig.getPermission();
         AlertManager alertManager = this.lpxBukkit.getAlertManager();
         if (printerStatus2 != PrinterStatus.NONE) {
            alertManager.sendPermissionAlert(alertManager.replacePlayer(printerConfig.getJoin(), this.player).replace(MODE_PLACEHOLDER, printerStatus2.name()), string);
         } else {
            alertManager.sendPermissionAlert(alertManager.replacePlayer(printerConfig.getLeave(), this.player), string);
         }
      }
   }

   @Override
   public void updateLastPlace() {
      this.lastPlace = TimeUtils.current();
   }

   @Override
   public long getLastPlace() {
      return this.lastPlace;
   }

   @Override
   public int increaseBlocksPlaced() {
      if (TimeUtils.difference(this.lastUpdate) > 1000L) {
         this.lastUpdate = TimeUtils.current();
         this.blocksPlaced = 0;
      }

      return ++this.blocksPlaced;
   }

   @Override
   public int getBlocksPlaced() {
      return this.blocksPlaced;
   }

}
