package io.lumine.mythic.bukkit.utils.maps;

import io.lumine.mythic.bukkit.utils.logging.Log;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.bukkit.map.MapPalette;

public class MapImage {
   public static Optional<byte[]> imageToMapPixels(File file, int size) {
      return imageToPixels(file, size, size);
   }

   public static Optional<byte[]> imageToPixels(File file, int height, int width) {
      ImageReader reader = getImageReader(file);
      if (reader == null) {
         Log.error("Could not read image {0}", file.getAbsolutePath());
         return Optional.empty();
      } else {
         try {
            return Optional.ofNullable(drawImage(reader, height, width));
         } catch (Error | Exception e) {
            Log.error("Error converting file {0} to Minecraft pixels", file.getAbsolutePath());
            e.printStackTrace();
            return Optional.empty();
         }
      }
   }

   private static ImageReader getImageReader(File file) {
      try {
         ImageInputStream inputStream = ImageIO.createImageInputStream(file);
         ImageReader reader = ImageIO.getImageReaders(inputStream).next();
         reader.setInput(inputStream);
         return reader;
      } catch (Error | Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   private static byte[] drawImage(ImageReader reader, int width, int height) throws IOException {
      int originalWidth = reader.getWidth(0);
      int originalHeight = reader.getHeight(0);
      BufferedImage tmpImage = new BufferedImage(originalWidth, originalHeight, 6);
      Graphics2D tmpGraphics = tmpImage.createGraphics();
      tmpGraphics.setBackground(new Color(0, 0, 0, 0));
      BufferedImage tmpScaledImage = new BufferedImage(width, height, 2);
      Graphics2D tmpScaledGraphics = tmpScaledImage.createGraphics();
      tmpScaledGraphics.setBackground(new Color(0, 0, 0, 0));
      byte[] renderedImage = new byte[width * height];
      int imageLeft = 0;
      int imageTop = 0;
      BufferedImage image = reader.read(0);
      tmpGraphics.drawImage(image, imageLeft, imageTop, null);
      image.flush();
      tmpScaledGraphics.drawImage(tmpImage, 0, 0, width, height, null);
      int[] rgbaPixels = ((DataBufferInt)tmpScaledImage.getRaster().getDataBuffer()).getData();
      IntStream.range(0, rgbaPixels.length).parallel().forEach(pixelIndex -> renderedImage[pixelIndex] = pixelToIndex(rgbaPixels[pixelIndex]));
      reader.dispose();
      tmpGraphics.dispose();
      tmpImage.flush();
      tmpScaledGraphics.dispose();
      tmpScaledImage.flush();
      return renderedImage;
   }

   public static byte pixelToIndex(int pixel) {
      return MapPalette.matchColor(new Color(pixel, true));
   }
}
