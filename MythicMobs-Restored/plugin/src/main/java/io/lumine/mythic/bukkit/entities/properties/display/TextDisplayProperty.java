package io.lumine.mythic.bukkit.entities.properties.display;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.TextDisplay.TextAlignment;

public class TextDisplayProperty extends DisplayProperty {
   private PlaceholderString text;
   private byte opacity;
   private boolean defaultBackground;
   private boolean shadowed;
   private boolean seeThrough;
   private int lineWidth;
   private TextAlignment alignment;
   private Color backgroundColor;

   public TextDisplayProperty(MythicConfig config) {
      super(config);
      this.text = config.getPlaceholderString("DisplayOptions.Text", "Give This Poor Dude A Text To Display");
      this.opacity = (byte)config.getInt("DisplayOptions.Opacity", 255);
      this.defaultBackground = config.getBoolean("DisplayOptions.DefaultBackground", false);
      this.alignment = config.getEnum("DisplayOptions.Alignment", TextAlignment.class, TextAlignment.CENTER);
      this.lineWidth = config.getInt("DisplayOptions.LineWidth", 200);
      this.shadowed = config.getBoolean("DisplayOptions.Shadowed", false);
      this.seeThrough = config.getBoolean("DisplayOptions.SeeThrough", false);
      String colorStr = config.getString("DisplayOptions.BackgroundColor", "1073741824");
      if (colorStr.contains(",")) {
         try {
            String[] splitStr = colorStr.split(",");
            int a = Integer.parseInt(splitStr[0]);
            int r = Integer.parseInt(splitStr[1]);
            int g = Integer.parseInt(splitStr[2]);
            int b = Integer.parseInt(splitStr[3]);
            this.backgroundColor = Color.fromARGB(a, r, g, b);
         } catch (Throwable e) {
            this.backgroundColor = Color.fromARGB(0);
            MythicLogger.errorGenericConfig(config, "Invalid BackgroundColor (must be in 'a,r,g,b'): " + colorStr);
         }
      } else {
         try {
            int a = Integer.parseInt(colorStr);
            this.backgroundColor = Color.fromARGB(a);
         } catch (NumberFormatException ex) {
            this.backgroundColor = Color.fromARGB(0);
            MythicLogger.errorGenericConfig(config, "Invalid BackgroundColor (must be an integer): " + colorStr);
         }
      }
   }

   @Override
   public Entity applyProperties(Entity entity) {
      entity = super.applyProperties(entity);
      if (entity instanceof TextDisplay textDisplay) {
         if (ServerVersion.isPaper()) {
            textDisplay.text(Text.parse(this.text.get()));
         } else {
            textDisplay.setText(this.text.get());
         }

         textDisplay.setTextOpacity(this.opacity);
         textDisplay.setDefaultBackground(this.defaultBackground);
         textDisplay.setAlignment(this.alignment);
         textDisplay.setLineWidth(this.lineWidth);
         textDisplay.setShadowed(this.shadowed);
         textDisplay.setSeeThrough(this.seeThrough);
         textDisplay.setBackgroundColor(this.backgroundColor);
      }

      return entity;
   }
}
