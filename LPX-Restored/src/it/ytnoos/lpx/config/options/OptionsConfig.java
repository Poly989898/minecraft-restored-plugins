package it.ytnoos.lpx.config.options;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.settings.PacketEventsSettings;
import it.ytnoos.lpx.api.LPXPlugin;
import it.ytnoos.lpx.config.ConfigSection;
import it.ytnoos.lpx.config.ItemStripConfig;
import it.ytnoos.lpx.config.PrinterConfig;
import it.ytnoos.lpx.config.messages.MessagesConfig;

public class OptionsConfig extends ConfigSection {
   private static final String SECTION = "options";
   private static final String NETTY_B_OPTIONS = "checks.netty.b.options";
   private final boolean silentFailures = this.getBoolean("silent-failures");
   private final boolean alertsOnJoin;
   private final boolean kickOnException;
   private final boolean hiddenCommand;
   private final boolean bypassPermission;
   private final boolean geyser;
   private final boolean axiom;
   private final boolean checkUpdates;
   private final boolean bStatsEnabled;
   private final boolean clearTaskEnabled;
   private final int clearTaskDelay;
   private final PrinterConfig printerConfig;
   private final ItemStripConfig itemStripConfig;
   private final int netherPortalDelay;
   private final boolean netherPortalDelayEnabled;
   private final int maxArrowVelocity;
   private final boolean maxArrowVelocityEnabled;
   private final int shearsCooldown;
   private final boolean shearsCooldownEnabled;
   private final boolean breakCloseInventory;
   private final boolean trapdoorRailRedstone;
   private final boolean interactContainerDelayEnabled;
   private final long interactContainerDelay;
   private final boolean alertsStore;
   private final int alertsDays;
   private final String server;
   private final int punishDelay;
   private final boolean debug;
   private final boolean logCancelledPackets;

   public OptionsConfig(MessagesConfig messagesConfig, LPXPlugin lpxPlugin) {
      super(lpxPlugin, "options", messagesConfig);
      PacketEvents.getAPI().getSettings().ignoreFailures(this.silentFailures);
      this.alertsOnJoin = this.getBoolean("alerts-on-join");
      this.kickOnException = this.getBoolean("kick-on-exception");
      PacketEvents.getAPI().getSettings().kickOnPacketException(this.kickOnException);
      this.applyPacketEventsSettings();
      this.hiddenCommand = this.getBoolean("hidden-command");
      this.bypassPermission = this.getBoolean("bypass-permission");
      this.geyser = this.getBoolean("geyser");
      this.axiom = this.getBoolean("axiom");
      if (this.axiom) {
         PacketEvents.getAPI().getSettings().maxPayloadMultiplier(15);
      }

      this.checkUpdates = this.getBoolean("check-updates");
      this.bStatsEnabled = this.getConfig().getBoolean(this.key + ".bstats", true);
      this.clearTaskEnabled = this.getBoolean("clear-task.enabled");
      this.clearTaskDelay = this.getInt("clear-task.delay");
      this.printerConfig = new PrinterConfig(lpxPlugin, "options", messagesConfig);
      this.itemStripConfig = new ItemStripConfig(lpxPlugin, "options", messagesConfig);
      this.netherPortalDelay = this.getInt("mechanics.nether-portal-delay");
      this.netherPortalDelayEnabled = this.netherPortalDelay != -1;
      this.maxArrowVelocity = this.getInt("mechanics.max-arrow-velocity");
      this.maxArrowVelocityEnabled = this.maxArrowVelocity != -1;
      this.shearsCooldown = this.getInt("mechanics.shears-cooldown");
      this.shearsCooldownEnabled = this.shearsCooldown != -1;
      this.breakCloseInventory = this.getBoolean("mechanics.break-close-inventory");
      this.trapdoorRailRedstone = this.getBoolean("mechanics.trapdoor-rail-redstone");
      this.interactContainerDelay = this.getInt("mechanics.interact-container-delay");
      this.interactContainerDelayEnabled = this.interactContainerDelay != -1L;
      this.alertsStore = this.getBoolean("alerts.store");
      this.alertsDays = this.getInt("alerts.days");
      this.server = this.getMessage("server");
      this.punishDelay = this.getInt("punish-delay");
      this.debug = this.getBoolean("debug");
      this.logCancelledPackets = this.getConfig().getBoolean(this.key + ".log-cancelled-packets", true);
   }

   private void applyPacketEventsSettings() {
      PacketEventsSettings packetEventsSettings = PacketEvents.getAPI().getSettings();
      packetEventsSettings.maxItemDepth(this.getNettyOption("max-item-depth", 4));
      packetEventsSettings.maxItemsPerPacket(this.getNettyOption("max-items-per-packet", 1458));
      packetEventsSettings.maxReceivedNbtElementsPerPacket(this.getNettyOption("max-nbt-elements-per-packet", 2500));
      packetEventsSettings.maxReceivedNbtDepth(this.getNettyOption("max-nbt-depth", 64));
   }

   private int getNettyOption(String string, int value) {
      return this.getConfig().getInt("checks.netty.b.options." + string, value);
   }

   public boolean isSilentFailures() {
      return this.silentFailures;
   }

   public boolean isAlertsOnJoin() {
      return this.alertsOnJoin;
   }

   public boolean isKickOnException() {
      return this.kickOnException;
   }

   public boolean isHiddenCommand() {
      return this.hiddenCommand;
   }

   public boolean isBypassPermission() {
      return this.bypassPermission;
   }

   public boolean isGeyser() {
      return this.geyser;
   }

   public boolean isAxiom() {
      return this.axiom;
   }

   public boolean isCheckUpdates() {
      return this.checkUpdates;
   }

   public boolean isBStatsEnabled() {
      return this.bStatsEnabled;
   }

   public boolean isClearTaskEnabled() {
      return this.clearTaskEnabled;
   }

   public int getClearTaskDelay() {
      return this.clearTaskDelay;
   }

   public PrinterConfig getPrinterConfig() {
      return this.printerConfig;
   }

   public ItemStripConfig getItemStripConfig() {
      return this.itemStripConfig;
   }

   public int getNetherPortalDelay() {
      return this.netherPortalDelay;
   }

   public boolean isNetherPortalDelayEnabled() {
      return this.netherPortalDelayEnabled;
   }

   public int getMaxArrowVelocity() {
      return this.maxArrowVelocity;
   }

   public boolean isMaxArrowVelocityEnabled() {
      return this.maxArrowVelocityEnabled;
   }

   public int getShearsCooldown() {
      return this.shearsCooldown;
   }

   public boolean isShearsCooldownEnabled() {
      return this.shearsCooldownEnabled;
   }

   public boolean isBreakCloseInventory() {
      return this.breakCloseInventory;
   }

   public boolean isTrapdoorRailRedstone() {
      return this.trapdoorRailRedstone;
   }

   public boolean isInteractContainerDelayEnabled() {
      return this.interactContainerDelayEnabled;
   }

   public long getInteractContainerDelay() {
      return this.interactContainerDelay;
   }

   public boolean isAlertsStore() {
      return this.alertsStore;
   }

   public int getAlertsDays() {
      return this.alertsDays;
   }

   public String getServer() {
      return this.server;
   }

   public int getPunishDelay() {
      return this.punishDelay;
   }

   public boolean isDebug() {
      return this.debug;
   }

   public boolean isLogCancelledPackets() {
      return this.logCancelledPackets;
   }

}
