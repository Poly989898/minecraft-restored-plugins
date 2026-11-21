package com.ticxo.modelengine.api.utils.config;

import lombok.Getter;


@Getter
public class ConfigCache {
   private boolean emptyZero;
   private boolean renderFire;
   private boolean eagerGenerate;
   private boolean useNewRotation;
   private boolean overrideMoveTo;
   private String classic;
   private String slim;

   public void updateConfig() {
      this.emptyZero = ConfigProperty.EMPTY_ZERO.getBoolean();
      this.renderFire = ConfigProperty.RENDER_FIRE.getBoolean();
      this.eagerGenerate = ConfigProperty.EAGER_GENERATE.getBoolean();
      this.useNewRotation = ConfigProperty.USE_NEW_ROTATION.getBoolean();
      this.overrideMoveTo = ConfigProperty.OVERRIDE_MOVE_TO.getBoolean();
      this.classic = ConfigProperty.DEFAULT_CLASSIC_SKIN.getString();
      this.slim = ConfigProperty.DEFAULT_SLIM_SKIN.getString();
   }
}
