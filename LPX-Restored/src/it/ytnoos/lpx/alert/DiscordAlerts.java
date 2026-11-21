package it.ytnoos.lpx.alert;

import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.api.check.FlagInfo;
import it.ytnoos.lpx.api.check.abstraction.Check;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.config.DiscordConfig;
import it.ytnoos.lpx.utils.DiscordWebhook;
import java.awt.Color;
import java.io.IOException;
import java.time.Instant;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DiscordAlerts {
   private static final Logger log = LogManager.getLogger(DiscordAlerts.class);
   private static final Pattern URL_PATTERN = Pattern.compile("(?:https?://)?(?:\\w+\\.)?\\w+\\.\\w+/api(?:/v\\d+)?/webhooks/(\\d+)/([\\w-]+)(?:/(?:\\w+)?)?");
   private final LPXBukkit lpxBukkit;
   private Color color;
   private String url;

   public DiscordAlerts(LPXBukkit lpxBukkit2) {
      this.lpxBukkit = lpxBukkit2;
   }

   public void reload() {
      DiscordConfig discordConfig = this.lpxBukkit.getConfigManager().getDiscordConfig();
      if (discordConfig.isEnabled()) {
         String string = discordConfig.getUrl();
         if (string != null && !string.isEmpty()) {
            Matcher matcher = URL_PATTERN.matcher(string);
            if (!matcher.matches()) {
               throw new IllegalArgumentException("Webhook URL is not valid! " + string);
            }

            this.color = Color.decode(discordConfig.getColor());
            this.url = string;
         }
      }
   }

   public void sendAlert(ProtocolPlayer protocolPlayer, Check check, FlagInfo[] flagInfos) {
      FlagInfo[] flagInfos2 = flagInfos;
      if (this.url != null) {
         StringBuilder stringBuilder = new StringBuilder();

         for (Object var18_o : this.lpxBukkit.getConfigManager().getDiscordConfig().getContent()) { String string = (String) var18_o;
            stringBuilder.append(string).append("\n");
         }

         String string2 = this.lpxBukkit.getAlertManager().replace(stringBuilder.toString(), protocolPlayer, check, flagInfos2);
         string2 = string2.replaceAll("§[a-zA-Z0-9]", "");
         string2 = string2.replace("_", "\\_");
         DiscordWebhook discordWebhook = new DiscordWebhook(this.url);
         discordWebhook.addEmbed(
            new DiscordWebhook.EmbedObject()
               .setImage("https://i.stack.imgur.com/Fzh0w.png")
               .setThumbnail("https://crafthead.net/helm/" + protocolPlayer.getUsername())
               .setColor(this.color)
               .setTitle("**LPX**")
               .setDescription(string2)
               .setTimestamp(Instant.now())
         );

         try {
            discordWebhook.execute();
         } catch (IOException ioException) {
            if (ioException.getMessage() == null || !ioException.getMessage().contains("429")) {
               this.lpxBukkit
                  .getLogger()
                  .log(Level.WARNING, "Unable to send alert to Discord due to IOException: " + ioException.getMessage() + ", Payload: " + discordWebhook.getJson(), ioException);
            }
         } catch (IllegalArgumentException illegalArgumentException) {
            this.lpxBukkit.getLogger().log(Level.WARNING, "Unable to send alert to Discord: " + illegalArgumentException.getMessage() + ", Payload: " + discordWebhook.getJson(), illegalArgumentException);
         } catch (Exception exception) {
            this.lpxBukkit.getLogger().log(Level.WARNING, "An unexpected error occurred while trying to send Discord alert: " + discordWebhook.getJson(), exception);
         }
      }
   }
}
