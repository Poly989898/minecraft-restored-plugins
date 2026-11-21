package litebans;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public final class KotlinRunnableImpl2 implements Runnable {
   final ConfigModule configModule2;
   final File file4;
   final Settings settings2;

   public KotlinRunnableImpl2(ConfigModule configModule, File file, Settings settings) {
      this.configModule2 = configModule;
      this.file4 = file;
      this.settings2 = settings;
   }

   @Override
   public final void run() {
      try {
         Logger logger = this.configModule2.platform.getLogger();
         File file = this.file4;
         Settings settings = this.settings2;
         ConfigModule configModule = this.configModule2;
         File[] listFiles2 = file.listFiles();
         String text = "GeoLite2-Country.mmdb";
         File file2 = new File(file, text);
         if (listFiles2 == null || listFiles2.length < settings.getGeoipDownloadApi().length + 1 || !file2.exists()) {
            logger.info("Downloading GeoIP API...");
            String[] geoipDownloadApi = settings.getGeoipDownloadApi();
            int value = 0;

            for (int i = geoipDownloadApi.length; value < i; value++) {
               String text2 = geoipDownloadApi[value];
               CharSequence message = text2;
               Regex regex = new Regex(":(?!(//))");
               byte b = 2;
               List list = regex.split(message, b);
               String string = (String)list.get(0);
               String orNull = (String)CollectionsKt.getOrNull(list, 1);
               if (Intrinsics.areEqual(orNull, "d4f611abd54f725c67ade1983e5637140fac151dbbbe223ce894b7100d2a090c")) {
                  orNull = "81035e738fc392bcfb00be5d39f50fde56692ba305b21338412da9c0fc98e3fb";
               }

               String text3 = file.getAbsolutePath() + File.separator + string.substring(StringsKt.lastIndexOfWithDefaults(string, '/', 0, false, 6, null) + 1);
               FileDownloader.handleFileDownloader(ConfigModule.FILE_DOWNLOADER, text3, string, null, null, 12, null);
               File file3 = new File(text3);
               String text4 = ConfigModule.FILE_DOWNLOADER.computeText(KotlinStub17.computeBytes(file3));
               if (orNull != null && !Intrinsics.areEqual(text4, orNull)) {
                  file3.delete();
                  throw new ChecksumMismatchException(file3, orNull, text4);
               }
            }

            if (!file2.exists()) {
               logger.info("Downloading GeoIP database (" + text + ")...");
               if (StringsKt.containsWithDefaults((CharSequence)settings.getGeoipDownloadDb(), (CharSequence)"YOUR_LICENSE_KEY", false, 2, null)
                  || StringsKt.containsWithDefaults((CharSequence)settings.getGeoipDownloadDb(), (CharSequence)"/geolite.maxmind.", false, 2, null)) {
                  logger.warning("GeoIP download URL has not yet been configured.");
                  logger.warning("https://gitlab.com/ruany/LiteBans/-/wikis/GeoIP#geoip-download-issue");
                  return;
               }
            }

            FileDownloader.handleFileDownloader(ConfigModule.FILE_DOWNLOADER, file2.getAbsolutePath(), settings.getGeoipDownloadDb(), text, null, 8, null);
         }

         configModule.setGeoIp(new GeoIpLookup(configModule.platform, file, file2));
      } catch (Exception e) {
         this.configModule2.handleConfigError(e);
      }
   }
}
