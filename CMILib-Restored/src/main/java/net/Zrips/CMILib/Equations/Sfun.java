package net.Zrips.CMILib.Equations;

public class Sfun {
  public static final double EPSILON_SMALL = 1.1102230246252E-16;
  public static final double EPSILON_LARGE = 2.2204460492503E-16;
  private static final double[] COT_COEF =
      new double[] {
        0.2402591609829563,
        -0.016533031601500228,
        -4.299839193172402E-5,
        -1.5928322332754105E-7,
        -6.191093135129349E-10,
        -2.430197415072646E-12,
        -9.56093675880008E-15,
        -3.763537981945806E-17,
        -1.4816657464674657E-19
      };
  private static final double[] SINH_COEF =
      new double[] {
        0.1730421940471796,
        0.08759422192276048,
        0.00107947777456713,
        6.37484926075E-6,
        2.202366404E-8,
        4.98794E-11,
        7.973E-14,
        9.0E-17
      };
  private static final double[] TANH_COEF =
      new double[] {
        -0.2582875664363471,
        -0.11836106330053497,
        0.009869442648006398,
        -8.35798662344582E-4,
        7.0904321198943E-5,
        -6.01642431812E-6,
        5.105241908E-7,
        -4.3320729077E-8,
        3.675999055E-9,
        -3.11928496E-10,
        2.6468828E-11,
        -2.246023E-12,
        1.90587E-13,
        -1.6172E-14,
        1.372E-15,
        -1.16E-16,
        9.0E-18
      };
  private static final double[] ASINH_COEF =
      new double[] {
        -0.12820039911738187,
        -0.05881176118995177,
        0.004727465432212481,
        -4.938363162653618E-4,
        5.850620705855741E-5,
        -7.466998328931368E-6,
        1.00116935835582E-6,
        -1.3903543858708333E-7,
        1.9823169483172795E-8,
        -2.8847468417848845E-9,
        4.2672965467159937E-10,
        -6.397608465436636E-11,
        9.699168608906471E-12,
        -1.4844276972043772E-12,
        2.290373793902745E-13,
        -3.5588395132732646E-14,
        5.563969408005679E-15,
        -8.746250959962468E-16,
        1.381524884452669E-16,
        -2.1916688282900364E-17,
        3.490465852482756E-18
      };
  private static final double[] ATANH_COEF =
      new double[] {
        0.0943951023931955,
        0.04919843705578616,
        0.002102593522455433,
        1.0735544497761166E-4,
        5.978267249293031E-6,
        3.505062030889135E-7,
        2.1263743437653402E-8,
        1.3216945357155272E-9,
        8.36587550117807E-11,
        5.370503749311002E-12,
        3.4866594701571077E-13,
        2.284549509603433E-14,
        1.508407105944793E-15,
        1.0024188168041091E-16,
        6.69867473816507E-18,
        4.497954546494931E-19
      };
  private static final double[] GAMMA_COEF =
      new double[] {
        0.00857119559098933,
        0.004415381324841007,
        0.05685043681599363,
        -0.00421983539641856,
        0.0013268081812124603,
        -1.8930245297988805E-4,
        3.606925327441245E-5,
        -6.056761904460864E-6,
        1.0558295463022833E-6,
        -1.811967365542384E-7,
        3.117724964715322E-8,
        -5.354219639019687E-9,
        9.193275519859589E-10,
        -1.5779412802883398E-10,
        2.7079806229349544E-11,
        -4.64681865382573E-12,
        7.97335019200742E-13,
        -1.368078209830916E-13,
        2.3473194865638007E-14,
        -4.027432614949067E-15,
        6.910051747372101E-16,
        -1.185584500221993E-16,
        2.034148542496374E-17,
        -3.490054341717406E-18,
        5.987993856485306E-19,
        -1.027378057872228E-19
      };
  private static final double[] R9LGMC_COEF =
      new double[] {
        0.16663894804518634,
        -1.384948176067564E-5,
        9.81082564692473E-9,
        -1.809129475572494E-11,
        6.221098041892606E-14,
        -3.399615005417722E-16,
        2.683181998482699E-18
      };
  private static final double[] ERFC_COEF =
      new double[] {
        -0.049046121234691806,
        -0.14226120510371365,
        0.010035582187599796,
        -5.768764699767485E-4,
        2.741993125219606E-5,
        -1.1043175507344507E-6,
        3.8488755420345036E-8,
        -1.1808582533875466E-9,
        3.2334215826050907E-11,
        -7.991015947004549E-13,
        1.7990725113961456E-14,
        -3.718635487818693E-16,
        7.103599003714253E-18,
        -1.2612455119155226E-19
      };
  private static final double[] ERFC2_COEF =
      new double[] {
        -0.0696013466023095,
        -0.04110133936262089,
        0.003914495866689627,
        -4.906395650548979E-4,
        7.157479001377036E-5,
        -1.1530716341312328E-5,
        1.9946705902019974E-6,
        -3.642666471599223E-7,
        6.944372610005012E-8,
        -1.371220902104366E-8,
        2.7883896610071373E-9,
        -5.814164724331161E-10,
        1.2389204917527532E-10,
        -2.6906391453067435E-11,
        5.942614350847911E-12,
        -1.3323867357581197E-12,
        3.0280468061771323E-13,
        -6.966648814941033E-14,
        1.620854541053923E-14,
        -3.809934465250492E-15,
        9.040487815978831E-16,
        -2.1640061950896072E-16,
        5.222102233995855E-17,
        -1.2697296023645554E-17,
        3.1091455042761977E-18,
        -7.663762920320386E-19,
        1.9008192513627452E-19
      };
  private static final double[] ERFCC_COEF =
      new double[] {
        0.07151793102029248,
        -0.026532434337606717,
        0.0017111539779208558,
        -1.6375166345851787E-4,
        1.9871293500552038E-5,
        -2.843712412766555E-6,
        4.6061613089631305E-7,
        -8.227753025879209E-8,
        1.5921418727709012E-8,
        -3.295071362252843E-9,
        7.223439760400556E-10,
        -1.6648558133987297E-10,
        4.010392588237665E-11,
        -1.004816214425731E-11,
        2.608275913300334E-12,
        -6.991110560404025E-13,
        1.9294923332617072E-13,
        -5.470131188754331E-14,
        1.5896633097626975E-14,
        -4.726893980197555E-15,
        1.4358733767849847E-15,
        -4.449510561817358E-16,
        1.4048108847682335E-16,
        -4.5138183877642106E-17,
        1.474521541045133E-17,
        -4.8926214069457765E-18,
        1.6476121414106467E-18,
        -5.626817176329408E-19,
        1.9474433822320786E-19
      };

  private Sfun() {}

  public static double acosh(double x) {
    double ans;
    if (Double.isNaN(x) || x < 1.0) {
      ans = Double.NaN;
    } else if (x < 9.490626562E7) {
      ans = Math.log(x + Math.sqrt(x * x - 1.0));
    } else {
      ans = 0.6931471805599453 + Math.log(x);
    }

    return ans;
  }

  public static double asinh(double x) {
    double y = Math.abs(x);
    double ans;
    if (Double.isNaN(x)) {
      ans = Double.NaN;
    } else if (y <= 1.05367E-8) {
      ans = x;
    } else if (y <= 1.0) {
      ans = x * (1.0 + csevl(2.0 * x * x - 1.0, ASINH_COEF));
    } else if (y < 9.490626562E7) {
      ans = Math.log(y + Math.sqrt(y * y + 1.0));
    } else {
      ans = 0.6931471805599453 + Math.log(y);
    }

    if (x < 0.0) {
      ans = -ans;
    }

    return ans;
  }

  public static double atanh(double x) {
    double y = Math.abs(x);
    double ans;
    if (Double.isNaN(x)) {
      ans = Double.NaN;
    } else if (y < 1.82501E-8) {
      ans = x;
    } else if (y <= 0.5) {
      ans = x * (1.0 + csevl(8.0 * x * x - 1.0, ATANH_COEF));
    } else if (y < 1.0) {
      ans = 0.5 * Math.log((1.0 + x) / (1.0 - x));
    } else if (y == 1.0) {
      ans = x * (Double.POSITIVE_INFINITY);
    } else {
      ans = Double.NaN;
    }

    return ans;
  }

  public static double cosh(double x) {
    double y = Math.exp(Math.abs(x));
    double ans;
    if (Double.isNaN(x)) {
      ans = Double.NaN;
    } else if (Double.isInfinite(x)) {
      ans = x;
    } else if (y < 9.490626562E7) {
      ans = 0.5 * (y + 1.0 / y);
    } else {
      ans = 0.5 * y;
    }

    return ans;
  }

  public static double cot(double x) {
    double pi2rec = 0.011619772367581343;
    double y = Math.abs(x);
    if (y > 4.5036E15) {
      return Double.NaN;
    }

    double ainty = (int) y;
    double yrem = y - ainty;
    double prodbg = 0.625 * ainty;
    ainty = (int) prodbg;
    y = prodbg - ainty + 0.625 * yrem + y * pi2rec;
    double yIntPart = (int) y;
    ainty += yIntPart;
    y -= yIntPart;
    int ifn = (int) (ainty % 2.0);
    if (ifn == 1) {
      y = 1.0 - y;
    }

    double ans;
    if (y == 0.0) {
      ans = Double.POSITIVE_INFINITY;
    } else if (y <= 1.82501E-8) {
      ans = 1.0 / y;
    } else if (y <= 0.25) {
      ans = (0.5 + csevl(32.0 * y * y - 1.0, COT_COEF)) / y;
    } else if (y <= 0.5) {
      ans = (0.5 + csevl(8.0 * y * y - 1.0, COT_COEF)) / (0.5 * y);
      ans = (ans * ans - 1.0) * 0.5 / ans;
    } else {
      ans = (0.5 + csevl(2.0 * y * y - 1.0, COT_COEF)) / (0.25 * y);
      ans = (ans * ans - 1.0) * 0.5 / ans;
      ans = (ans * ans - 1.0) * 0.5 / ans;
    }

    if (x != 0.0) {
      ans = sign(ans, x);
    }

    if (ifn == 1) {
      ans = -ans;
    }

    return ans;
  }

  static double csevl(double x, double[] coef) {
    double bNext = 0.0;
    double bCurrent = 0.0;
    double bNextNext = 0.0;
    double twox = 2.0 * x;

    for (int i = coef.length - 1; i >= 0; i--) {
      bNextNext = bNext;
      bNext = bCurrent;
      bCurrent = twox * bNext - bNextNext + coef[i];
    }

    return 0.5 * (bCurrent - bNextNext);
  }

  public static double erf(double x) {
    double y = Math.abs(x);
    double ans;
    if (y <= 1.49012E-8) {
      ans = 2.0 * x / 1.772453850905516;
    } else if (y <= 1.0) {
      ans = x * (1.0 + csevl(2.0 * x * x - 1.0, ERFC_COEF));
    } else if (y < 6.013687357) {
      ans = sign(1.0 - erfc(y), x);
    } else {
      ans = sign(1.0, x);
    }

    return ans;
  }

  public static double erfc(double x) {
    double y = Math.abs(x);
    double ans;
    if (x <= -6.013687357) {
      ans = 2.0;
    } else if (y < 1.49012E-8) {
      ans = 1.0 - 2.0 * x / 1.772453850905516;
    } else {
      double ysq = y * y;
      if (y < 1.0) {
        ans = 1.0 - x * (1.0 + csevl(2.0 * ysq - 1.0, ERFC_COEF));
      } else if (y <= 4.0) {
        ans = Math.exp(-ysq) / y * (0.5 + csevl((8.0 / ysq - 5.0) / 3.0, ERFC2_COEF));
        if (x < 0.0) {
          ans = 2.0 - ans;
        }

        if (x < 0.0) {
          ans = 2.0 - ans;
        }

        if (x < 0.0) {
          ans = 2.0 - ans;
        }
      } else {
        ans = Math.exp(-ysq) / y * (0.5 + csevl(8.0 / ysq - 1.0, ERFCC_COEF));
        if (x < 0.0) {
          ans = 2.0 - ans;
        }
      }
    }

    return ans;
  }

  public static double fact(int n) {
    double ans = 1.0;
    if (Double.isNaN(n) || n < 0) {
      ans = Double.NaN;
    } else if (n > 170) {
      ans = Double.POSITIVE_INFINITY;
    } else {
      for (int k = 2; k <= n; k++) {
        ans *= k;
      }
    }

    return ans;
  }

  public static double gamma(double x) {
    double y = Math.abs(x);
    double ans;
    if (y <= 10.0) {
      int n = (int) x;
      if (x < 0.0) {
        n--;
      }

      y = x - n;
      n--;
      ans = 0.9375 + csevl(2.0 * y - 1.0, GAMMA_COEF);
      if (n != 0) {
        if (n < 0) {
          n = -n;
          if (x == 0.0) {
            ans = Double.NaN;
          } else if (y < 5.562684646268003E-309) {
            ans = Double.POSITIVE_INFINITY;
          } else {
            double xn = n - 2;
            if (x < 0.0 && x + xn == 0.0) {
              ans = Double.NaN;
            } else {
              for (int i = 0; i < n; i++) {
                ans /= x + i;
              }
            }
          }
        } else {
          for (int i = 1; i <= n; i++) {
            ans *= y + i;
          }
        }
      }
    } else if (x > 171.614) {
      ans = Double.POSITIVE_INFINITY;
    } else if (x < -170.56) {
      ans = 0.0;
    } else {
      ans = Math.exp((y - 0.5) * Math.log(y) - y + 0.9189385332046727 + r9lgmc(y));
      if (x < 0.0) {
        double sinpiy = Math.sin(Math.PI * y);
        if (sinpiy != 0.0 && Math.round(y) != y) {
          ans = -Math.PI / (y * sinpiy * ans);
        } else {
          ans = Double.NaN;
        }
      }
    }

    return ans;
  }

  static double r9lgmc(double x) {
    double ans;
    if (x < 10.0) {
      ans = Double.NaN;
    } else if (x < 9.490626562E7) {
      double y = 10.0 / x;
      ans = csevl(2.0 * y * y - 1.0, R9LGMC_COEF) / x;
    } else if (x < 1.39118E11) {
      ans = 1.0 / (12.0 * x);
    } else {
      ans = 0.0;
    }

    return ans;
  }

  private static double sign(double x, double y) {
    double abs_x = x < 0.0 ? -x : x;
    return y < 0.0 ? -abs_x : abs_x;
  }

  public static double sinh(double x) {
    double y = Math.abs(x);
    double ans;
    if (Double.isNaN(x)) {
      ans = Double.NaN;
    } else {
      if (Double.isInfinite(y)) {
        return x;
      }

      if (y < 2.58096E-8) {
        ans = x;
      } else if (y <= 1.0) {
        ans = x * (1.0 + csevl(2.0 * x * x - 1.0, SINH_COEF));
      } else {
        y = Math.exp(y);
        if (y >= 9.490626562E7) {
          ans = sign(0.5 * y, x);
        } else {
          ans = sign(0.5 * (y - 1.0 / y), x);
        }
      }
    }

    return ans;
  }

  public static double tanh(double x) {
    double y = Math.abs(x);
    double ans;
    if (Double.isNaN(x)) {
      ans = Double.NaN;
    } else if (y < 1.82501E-8) {
      ans = x;
    } else if (y <= 1.0) {
      ans = x * (1.0 + csevl(2.0 * x * x - 1.0, TANH_COEF));
    } else if (y < 7.977294885) {
      y = Math.exp(y);
      ans = sign((y - 1.0 / y) / (y + 1.0 / y), x);
    } else {
      ans = sign(1.0, x);
    }

    return ans;
  }
}
