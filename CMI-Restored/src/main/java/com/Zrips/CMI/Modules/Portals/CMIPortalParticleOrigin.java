package com.Zrips.CMI.Modules.Portals;

public enum CMIPortalParticleOrigin {
  Any,
  Top,
  Bottom,
  Center;

  public CMIPortalParticleOrigin getNext() {
    return values()[(this.ordinal() + 1) % values().length];
  }

  public CMIPortalParticleOrigin getPrevious() {
    return values()[(this.ordinal() + values().length - 1) % values().length];
  }

  public static CMIPortalParticleOrigin get(String string) {
    CMIPortalParticleOrigin[] cmiPortalParticleOrigins;
    int value = (cmiPortalParticleOrigins = values()).length;

    for (int index = 0; index < value; index++) {
      CMIPortalParticleOrigin cmiPortalParticleOrigin = cmiPortalParticleOrigins[index];
      if (cmiPortalParticleOrigin.name().equalsIgnoreCase(string)) {
        return cmiPortalParticleOrigin;
      }
    }

    return null;
  }
}
