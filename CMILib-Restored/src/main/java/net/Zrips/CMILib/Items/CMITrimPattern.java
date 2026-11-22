package net.Zrips.CMILib.Items;

import org.bukkit.inventory.meta.trim.TrimPattern;

public enum CMITrimPattern {
  BOLT,
  COAST,
  DUNE,
  EYE,
  FLOW,
  HOST,
  RAISER,
  RIB,
  SENTRY,
  SHAPER,
  SILENCE,
  SNOUT,
  SPIRE,
  TIDE,
  VEX,
  WARD,
  WAYFINDER,
  WILD;

  public TrimPattern get() {
    return getByName(this.toString());
  }

  public static TrimPattern getByName(String name) {
    switch (name.toUpperCase()) {
      case "BOLT":
        return TrimPattern.BOLT;
      case "COAST":
        return TrimPattern.COAST;
      case "DUNE":
        return TrimPattern.DUNE;
      case "EYE":
        return TrimPattern.EYE;
      case "FLOW":
        return TrimPattern.FLOW;
      case "HOST":
        return TrimPattern.HOST;
      case "RAISER":
        return TrimPattern.RAISER;
      case "RIB":
        return TrimPattern.RIB;
      case "SENTRY":
        return TrimPattern.SENTRY;
      case "SHAPER":
        return TrimPattern.SHAPER;
      case "SILENCE":
        return TrimPattern.SILENCE;
      case "SNOUT":
        return TrimPattern.SNOUT;
      case "SPIRE":
        return TrimPattern.SPIRE;
      case "TIDE":
        return TrimPattern.TIDE;
      case "VEX":
        return TrimPattern.VEX;
      case "WARD":
        return TrimPattern.WARD;
      case "WAYFINDER":
        return TrimPattern.WAYFINDER;
      case "WILD":
        return TrimPattern.WILD;
      default:
        return null;
    }
  }
}
