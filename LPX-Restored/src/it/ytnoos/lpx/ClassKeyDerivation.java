package it.ytnoos.lpx;

import java.util.ArrayList;
import java.util.Vector;

public class ClassKeyDerivation implements KeySource {
   private long a;
   private int[] b;
   private KeySource c;
   private long d;
   private long[] j;
   private static long[] k = new long[64];
   static int[] e;
   private static ArrayList f;
   private static Vector m;
   private static int i;
   private static Object o;
   private static int h = 52;
   private static int l = 128;
   private static int n = 17;

   public static Object g() {
      return o;
   }

   public static KeySource a(long longValue, long longValue2, Object object) {
      KeyDerivationCore.a(longValue > 0L);
      KeySource t9 = d(longValue);
      KeySource t92 = d(longValue2);
      KeySource t93 = KeyDerivationCore.a(t9, t92);
      if (object != null) {
         m.add(object);
      }

      return t93;
   }

   static KeySource c(long longValue) {
      int value = (int)a(longValue, h, 63, e, k);
      if (value < i) {
         return (KeySource)f.get(value);
      }

      if (f.size() % l == 0) {
         e = (int[])e.clone();
      }

      ClassKeyDerivation _c = new ClassKeyDerivation(longValue);
      f.add(_c);
      return _c;
   }

   private static KeySource d(long longValue) {
      return new ClassKeyDerivation(longValue);
   }

   static void a(KeyDerivationCore h4) {
      i = f.size();
      c();
      h4.d();
   }

   static void b(KeyDerivationCore h4) {
      c();
      int[] values = e = new int[64];
      values[0] = -33;
      values[1] = -1;
      values[2] = 1;
      values[3] = -43;
      values[4] = -41;
      values[5] = -56;
      values[6] = -3;
      values[7] = -15;
      values[8] = -18;
      values[9] = 3;
      values[10] = -28;
      values[11] = -2;
      values[12] = -11;
      values[13] = 2;
      values[14] = -40;
      values[15] = -24;
      values[16] = -25;
      values[17] = -26;
      values[18] = -13;
      values[19] = -33;
      values[20] = -37;
      values[21] = -16;
      values[22] = 15;
      values[23] = 11;
      values[24] = -16;
      values[25] = -2;
      values[26] = 18;
      values[27] = 2;
      values[28] = -6;
      values[29] = -26;
      values[30] = -30;
      values[31] = 13;
      values[32] = -10;
      values[33] = 33;
      values[34] = 6;
      values[35] = -12;
      values[36] = -14;
      values[37] = 16;
      values[38] = 28;
      values[39] = 24;
      values[40] = 16;
      values[41] = 25;
      values[42] = 10;
      values[43] = 26;
      values[44] = -15;
      values[45] = 41;
      values[46] = 43;
      values[47] = 12;
      values[48] = -15;
      values[49] = -9;
      values[50] = 14;
      values[51] = -11;
      values[52] = 33;
      values[53] = -3;
      values[54] = 40;
      values[55] = 26;
      values[56] = 3;
      values[57] = 37;
      values[58] = 9;
      values[59] = 15;
      values[60] = 30;
      values[61] = 56;
      values[62] = 11;
      values[63] = 15;
      h4.c();
   }

   private ClassKeyDerivation(long longValue) {
      this.a = longValue;
      this.b = e;
      this.j = k;
   }

   @Override
   public long a(long longValue) {
      long longValue2 = this.a(8, 55);
      long longValue3 = this.a ^ longValue ^ this.d;
      this.a = longValue3;
      if (this.c != null) {
         this.c.a(longValue);
      }

      return longValue2;
   }

   @Override
   public void b(long longValue) {
      this.d = longValue;
   }

   @Override
   public void a(KeySource t9) {
      if (this != t9) {
         if (this.c == null) {
            this.c = t9;
         } else {
            this.c.a(t9);
         }
      }
   }

   @Override
   public int hashCode() {
      return (int)this.a(8);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return object instanceof ClassKeyDerivation ? this.a(56) == ((ClassKeyDerivation)object).a(56) : false;
      }
   }

   @Override
   public boolean b(KeySource t9) {
      if (this == t9) {
         return true;
      } else {
         return t9 instanceof ClassKeyDerivation ? this.a(56, 63) - ((ClassKeyDerivation)t9).a(56, 63) <= 0L : true;
      }
   }

   @Override
   public int[] b() {
      return this.b;
   }

   private long a(int value) {
      return this.a(0, value - 1);
   }

   private long a(int value, int value2) {
      return a(this.a, value, value2, this.b, this.j);
   }

   private static long a(long longValue, int value, int value2, int[] values, long[] longValues) {
      long longValue2 = 0L;
      int value3 = values.length;

      for (int value4 = 0; value4 < value3; value4++) {
         long longValue3 = longValue & k[value4];
         long longValue4 = (long)values[value4];
         if (longValue3 != 0L) {
            if (longValue4 > 0) {
               longValue3 >>>= longValue4;
            } else if (longValue4 < 0) {
               longValue3 <<= ~longValue4 + 1;
            }

            longValue2 |= longValue3;
         }
      }

      byte b2 = 64;
      long longValue5 = longValue2;
      long longValue6 = (long)(b2 - 1 - value2);
      if (longValue6 > 0) {
         longValue5 <<= longValue6;
      }

      long longValue7 = (long)(value + b2 - 1 - value2);
      if (longValue7 > 0) {
         longValue5 >>>= longValue7;
      }

      return longValue5;
   }

   private static void c() {
      byte b2 = 0;
      a(0, f.size() - 1, f, new ArrayList(f), b2);
   }

   private static void a(int value, int value2, ArrayList arrayList, ArrayList arrayList2, int value3) {
      if (value < value2) {
         int value4 = value + (value2 - value) / 2;
         if (++value3 < n) {
            a(value, value4, arrayList, arrayList2, value3);
            a(value4 + 1, value2, arrayList, arrayList2, value3);
         }

         a(value, value4, value2, arrayList, arrayList2);
      }
   }

   private static void a(int value, int value2, int value3, ArrayList arrayList, ArrayList arrayList2) {
      int value4 = value;
      int value5 = value2 + 1;

      for (int value6 = value; value6 <= value3; value6++) {
         arrayList2.set(value6, arrayList.get(value6));
      }

      while (value4 <= value2 && value5 <= value3) {
         KeySource t9;
         if (((KeySource)arrayList2.get(value4)).b((KeySource)arrayList2.get(value5))) {
            t9 = (KeySource)arrayList2.get(value4++);
         } else {
            t9 = (KeySource)arrayList2.get(value5++);
         }

         arrayList.set(value, t9);
         value++;
      }

      while (value4 <= value2) {
         arrayList.set(value, arrayList2.get(value4));
         value++;
         value4++;
      }
   }

   static {
      long var0 = 1L;

      for (int var2 = 0; var2 < 64; var2++) {
         k[var2] = var0;
         var0 <<= 1;
      }

      o = new Object();
      m = new Vector();
      f = new ArrayList();
      a0();
      i = f.size();
      c();
   }

   private static void a0() {
      e = new int[]{
         -41,
         -24,
         -7,
         -13,
         -55,
         -16,
         -18,
         -40,
         -15,
         7,
         -52,
         -20,
         -45,
         -38,
         -23,
         -28,
         13,
         -32,
         -17,
         -9,
         -9,
         16,
         -4,
         15,
         18,
         24,
         4,
         -3,
         9,
         9,
         3,
         20,
         -4,
         -20,
         -4,
         17,
         4,
         23,
         4,
         -11,
         -5,
         41,
         -10,
         28,
         -2,
         5,
         2,
         40,
         -6,
         32,
         11,
         38,
         10,
         20,
         6,
         -1,
         1,
         45,
         -3,
         55,
         -3,
         3,
         52,
         3
      };
      f.add(new ClassKeyDerivation(-7154263799324994033L));
      f.add(new ClassKeyDerivation(8959057450823685512L));
      f.add(new ClassKeyDerivation(8675435057541257620L));
      f.add(new ClassKeyDerivation(-3729368007451301495L));
      f.add(new ClassKeyDerivation(8403068564553465500L));
      f.add(new ClassKeyDerivation(-8186536665984344456L));
      f.add(new ClassKeyDerivation(-673532614427481718L));
      f.add(new ClassKeyDerivation(-4566675686878185011L));
      f.add(new ClassKeyDerivation(1502895865146520466L));
      f.add(new ClassKeyDerivation(5932947910480426530L));
      f.add(new ClassKeyDerivation(-8590527570311725571L));
      f.add(new ClassKeyDerivation(-94854929610018801L));
      f.add(new ClassKeyDerivation(8970325370926782662L));
      f.add(new ClassKeyDerivation(4188645392131374224L));
      f.add(new ClassKeyDerivation(-7026652024632721061L));
      f.add(new ClassKeyDerivation(-7035397626122310648L));
      f.add(new ClassKeyDerivation(-3689155494041777385L));
      f.add(new ClassKeyDerivation(-2430827181803184942L));
      f.add(new ClassKeyDerivation(321487233436982767L));
      f.add(new ClassKeyDerivation(6067902059341401566L));
      f.add(new ClassKeyDerivation(4536041355272769065L));
      f.add(new ClassKeyDerivation(-5900722292177685248L));
      f.add(new ClassKeyDerivation(-5869700147864426428L));
      f.add(new ClassKeyDerivation(1777512931784797499L));
      f.add(new ClassKeyDerivation(-4315161521855376610L));
      f.add(new ClassKeyDerivation(-5548909644130899548L));
      f.add(new ClassKeyDerivation(-7763650617726608346L));
      f.add(new ClassKeyDerivation(-992475452023132720L));
      f.add(new ClassKeyDerivation(1452545260009186663L));
      f.add(new ClassKeyDerivation(3933467877192663464L));
      f.add(new ClassKeyDerivation(-1989332876091739577L));
      f.add(new ClassKeyDerivation(-7770533098885921895L));
      f.add(new ClassKeyDerivation(-6166169272589493499L));
      f.add(new ClassKeyDerivation(-709065621075794315L));
      f.add(new ClassKeyDerivation(-1405153953950616154L));
      f.add(new ClassKeyDerivation(7363683626055020861L));
      f.add(new ClassKeyDerivation(8082914099825339544L));
      f.add(new ClassKeyDerivation(884418433796183200L));
      f.add(new ClassKeyDerivation(6541094757537618312L));
      f.add(new ClassKeyDerivation(-1530562216354526277L));
      f.add(new ClassKeyDerivation(-2991494045879642269L));
      f.add(new ClassKeyDerivation(4612154245663136502L));
      f.add(new ClassKeyDerivation(2469893950498468693L));
      f.add(new ClassKeyDerivation(-1969201100829229587L));
      f.add(new ClassKeyDerivation(2651980741666395439L));
      f.add(new ClassKeyDerivation(3490156379365797780L));
      f.add(new ClassKeyDerivation(-851982646180065428L));
      f.add(new ClassKeyDerivation(440141938037417114L));
      f.add(new ClassKeyDerivation(-4140798896359006625L));
      f.add(new ClassKeyDerivation(8428184266819120740L));
      f.add(new ClassKeyDerivation(6529978628446254850L));
      f.add(new ClassKeyDerivation(882451083416500588L));
      f.add(new ClassKeyDerivation(5361046618917608484L));
      f.add(new ClassKeyDerivation(5626279403230195939L));
      f.add(new ClassKeyDerivation(4056718167100200424L));
      f.add(new ClassKeyDerivation(907927693093181082L));
      f.add(new ClassKeyDerivation(8974046138062669875L));
      f.add(new ClassKeyDerivation(5382542153873282907L));
      f.add(new ClassKeyDerivation(-6996528038723459962L));
      f.add(new ClassKeyDerivation(4061438728134865495L));
      f.add(new ClassKeyDerivation(-8616398582067085654L));
      f.add(new ClassKeyDerivation(2776340726934265736L));
      f.add(new ClassKeyDerivation(8836185712704178365L));
      f.add(new ClassKeyDerivation(-9155338428484084477L));
      f.add(new ClassKeyDerivation(4202492739848110253L));
      f.add(new ClassKeyDerivation(6536467499810094119L));
      f.add(new ClassKeyDerivation(3177366722569123443L));
      f.add(new ClassKeyDerivation(-1260987344521378578L));
      f.add(new ClassKeyDerivation(3812012969180742945L));
      f.add(new ClassKeyDerivation(4823805848656360562L));
      f.add(new ClassKeyDerivation(-3866310891320589785L));
      f.add(new ClassKeyDerivation(7518592271529881975L));
      f.add(new ClassKeyDerivation(8537074950670331084L));
      f.add(new ClassKeyDerivation(-4875599979930612220L));
      f.add(new ClassKeyDerivation(761466107949822089L));
      f.add(new ClassKeyDerivation(-2544738163942244566L));
      f.add(new ClassKeyDerivation(-5884546555369187185L));
      f.add(new ClassKeyDerivation(-7492607686837200928L));
      f.add(new ClassKeyDerivation(7275440665105444136L));
      f.add(new ClassKeyDerivation(-989577122909676817L));
      f.add(new ClassKeyDerivation(8826913230287548694L));
      f.add(new ClassKeyDerivation(3382442100010952389L));
      f.add(new ClassKeyDerivation(1470629277463276757L));
      f.add(new ClassKeyDerivation(1748488689451330663L));
      f.add(new ClassKeyDerivation(5816852493715804890L));
      f.add(new ClassKeyDerivation(3814849502133758151L));
      f.add(new ClassKeyDerivation(-3580140778383685253L));
      f.add(new ClassKeyDerivation(-7622696323634171452L));
      f.add(new ClassKeyDerivation(-6886450409971027236L));
      f.add(new ClassKeyDerivation(6941626408678240286L));
      f.add(new ClassKeyDerivation(7222296663914787468L));
      f.add(new ClassKeyDerivation(-5841856820153003795L));
      f.add(new ClassKeyDerivation(3222340342977839097L));
      f.add(new ClassKeyDerivation(-4574426071498141486L));
      f.add(new ClassKeyDerivation(-654102828677295006L));
      f.add(new ClassKeyDerivation(-1821756179871780469L));
      f.add(new ClassKeyDerivation(-4997322848932812853L));
      f.add(new ClassKeyDerivation(3485763058989808232L));
      f.add(new ClassKeyDerivation(3355429512242959358L));
      f.add(new ClassKeyDerivation(8124557928216050169L));
      f.add(new ClassKeyDerivation(-949295490646273176L));
      e = (int[])e.clone();
      f.add(new ClassKeyDerivation(5666107261631649911L));
      f.add(new ClassKeyDerivation(6384636476095942259L));
      f.add(new ClassKeyDerivation(1886434361709797206L));
      f.add(new ClassKeyDerivation(-7762417742395435235L));
      f.add(new ClassKeyDerivation(7120516274991551092L));
      f.add(new ClassKeyDerivation(7987660079601132515L));
      f.add(new ClassKeyDerivation(7811247224629528706L));
      f.add(new ClassKeyDerivation(-5013148455023392566L));
      f.add(new ClassKeyDerivation(-1411236692831243018L));
      f.add(new ClassKeyDerivation(8796132714871145359L));
      f.add(new ClassKeyDerivation(-7581725752273525496L));
      f.add(new ClassKeyDerivation(8523927994683275126L));
      f.add(new ClassKeyDerivation(4822033417806703386L));
      f.add(new ClassKeyDerivation(4945675730809793556L));
      f.add(new ClassKeyDerivation(1591002644551389829L));
      f.add(new ClassKeyDerivation(-5848602176725866198L));
      f.add(new ClassKeyDerivation(4041961674220598446L));
      f.add(new ClassKeyDerivation(6823597337815050353L));
      f.add(new ClassKeyDerivation(8565941239278471678L));
      f.add(new ClassKeyDerivation(-3968381792386170161L));
      f.add(new ClassKeyDerivation(-2152398656229237408L));
      f.add(new ClassKeyDerivation(-5591660216732661136L));
      f.add(new ClassKeyDerivation(-6461154115363582151L));
      f.add(new ClassKeyDerivation(-8472759062665313824L));
      f.add(new ClassKeyDerivation(-2832517011727361061L));
      f.add(new ClassKeyDerivation(4352491801051239478L));
      f.add(new ClassKeyDerivation(4380693188668310633L));
      f.add(new ClassKeyDerivation(-1085231114539883258L));
      f.add(new ClassKeyDerivation(-5166237618488581889L));
      f.add(new ClassKeyDerivation(4217551528974398572L));
      f.add(new ClassKeyDerivation(8230929395716032897L));
      f.add(new ClassKeyDerivation(-8356962790401083118L));
      f.add(new ClassKeyDerivation(2361575514187134339L));
      f.add(new ClassKeyDerivation(2322582919740624740L));
      f.add(new ClassKeyDerivation(6070661757009848335L));
      f.add(new ClassKeyDerivation(-9037422444675598858L));
      f.add(new ClassKeyDerivation(2490249106396685144L));
      f.add(new ClassKeyDerivation(-5448889123125592410L));
      f.add(new ClassKeyDerivation(-2402479019605236266L));
      f.add(new ClassKeyDerivation(-6038323417978291438L));
      f.add(new ClassKeyDerivation(-3454421433549048047L));
      f.add(new ClassKeyDerivation(-8597658413460759227L));
      f.add(new ClassKeyDerivation(-8210562726038932796L));
      f.add(new ClassKeyDerivation(7354980354964564825L));
      f.add(new ClassKeyDerivation(8121370597467345306L));
      f.add(new ClassKeyDerivation(-4313611572948733631L));
      f.add(new ClassKeyDerivation(-3966204357283249551L));
      f.add(new ClassKeyDerivation(-4834757843260842424L));
      f.add(new ClassKeyDerivation(-3419789683886896740L));
      f.add(new ClassKeyDerivation(-6588570117975040911L));
      f.add(new ClassKeyDerivation(-5435648613368198047L));
      f.add(new ClassKeyDerivation(162051753251931879L));
      f.add(new ClassKeyDerivation(-8886428888335764976L));
      f.add(new ClassKeyDerivation(3917037268915547821L));
      f.add(new ClassKeyDerivation(6550652895341311299L));
      f.add(new ClassKeyDerivation(5225145237139151826L));
      f.add(new ClassKeyDerivation(-7869033175073156908L));
      f.add(new ClassKeyDerivation(3623015333880116233L));
      f.add(new ClassKeyDerivation(-4426133890836082519L));
      f.add(new ClassKeyDerivation(2763405993943363857L));
      f.add(new ClassKeyDerivation(-1846006621876687127L));
      f.add(new ClassKeyDerivation(7072888658477044964L));
      f.add(new ClassKeyDerivation(-6586978372319602229L));
      f.add(new ClassKeyDerivation(6392183390611149539L));
      f.add(new ClassKeyDerivation(6687837888137000887L));
      f.add(new ClassKeyDerivation(311333188061430854L));
      f.add(new ClassKeyDerivation(-3140991853113654823L));
      f.add(new ClassKeyDerivation(301051831629531694L));
      f.add(new ClassKeyDerivation(-4127401243913728209L));
      f.add(new ClassKeyDerivation(-4683827770051830866L));
      f.add(new ClassKeyDerivation(3957223243395700519L));
      f.add(new ClassKeyDerivation(-521879073045024481L));
      f.add(new ClassKeyDerivation(-4684099127928528742L));
      f.add(new ClassKeyDerivation(2504432536370226423L));
      f.add(new ClassKeyDerivation(-2522769378681012930L));
      f.add(new ClassKeyDerivation(-695233486949137402L));
      f.add(new ClassKeyDerivation(5930210267089990384L));
      f.add(new ClassKeyDerivation(-547164971842156824L));
      f.add(new ClassKeyDerivation(1504317322809202352L));
      f.add(new ClassKeyDerivation(-5415567770454150112L));
      f.add(new ClassKeyDerivation(7105910703762899980L));
      f.add(new ClassKeyDerivation(-7492146047427061097L));
      f.add(new ClassKeyDerivation(3938981502138134433L));
      f.add(new ClassKeyDerivation(4338283844339060146L));
      e = (int[])e.clone();
      f.add(new ClassKeyDerivation(3055016133090503595L));
      f.add(new ClassKeyDerivation(7095058244485068678L));
      f.add(new ClassKeyDerivation(-6452163086467526901L));
      f.add(new ClassKeyDerivation(5961842893177829280L));
      f.add(new ClassKeyDerivation(5546339307561516884L));
      f.add(new ClassKeyDerivation(-1685148386876096232L));
      f.add(new ClassKeyDerivation(6385214534332837228L));
      f.add(new ClassKeyDerivation(1307277006135675945L));
      f.add(new ClassKeyDerivation(-3014313810483201067L));
      f.add(new ClassKeyDerivation(-8748748396898825878L));
      f.add(new ClassKeyDerivation(-4890742436983614686L));
      f.add(new ClassKeyDerivation(1193471936343455513L));
      f.add(new ClassKeyDerivation(4390626910937517396L));
      f.add(new ClassKeyDerivation(5356700561184866415L));
      f.add(new ClassKeyDerivation(8071125876955737175L));
      f.add(new ClassKeyDerivation(3085608898928660942L));
      f.add(new ClassKeyDerivation(-8929998241112337542L));
      f.add(new ClassKeyDerivation(-220972367365270370L));
      f.add(new ClassKeyDerivation(-8351799413066134536L));
      f.add(new ClassKeyDerivation(1063685831781094791L));
      f.add(new ClassKeyDerivation(6984812815815459914L));
      f.add(new ClassKeyDerivation(-8065200237456375101L));
      f.add(new ClassKeyDerivation(-1831987739270135908L));
      f.add(new ClassKeyDerivation(-7338204166429066807L));
      f.add(new ClassKeyDerivation(290043202694143598L));
      f.add(new ClassKeyDerivation(-8483967276346584499L));
      f.add(new ClassKeyDerivation(6836957671945081415L));
      f.add(new ClassKeyDerivation(5482101576971598632L));
      f.add(new ClassKeyDerivation(-9222294441708866926L));
      f.add(new ClassKeyDerivation(-9015711756881822800L));
      f.add(new ClassKeyDerivation(-5038086300447968076L));
      f.add(new ClassKeyDerivation(-1380322766106264269L));
      f.add(new ClassKeyDerivation(1207774348845425518L));
      f.add(new ClassKeyDerivation(7262428407573351162L));
      f.add(new ClassKeyDerivation(-6011531064525678272L));
      f.add(new ClassKeyDerivation(-1831906034504587766L));
      f.add(new ClassKeyDerivation(4534020424995786533L));
      f.add(new ClassKeyDerivation(6372700149937147550L));
      f.add(new ClassKeyDerivation(-4399552301082046070L));
      f.add(new ClassKeyDerivation(-4740269029415316669L));
      f.add(new ClassKeyDerivation(6254826134306625849L));
      f.add(new ClassKeyDerivation(-3250405356415085563L));
      f.add(new ClassKeyDerivation(7951663823013385381L));
      f.add(new ClassKeyDerivation(5681721938612465646L));
      f.add(new ClassKeyDerivation(-1259988131489028776L));
      f.add(new ClassKeyDerivation(2465334771230484644L));
      f.add(new ClassKeyDerivation(-4719288288307271255L));
      f.add(new ClassKeyDerivation(-6422199427711875118L));
      f.add(new ClassKeyDerivation(1343215898819079305L));
      f.add(new ClassKeyDerivation(8360035630993925061L));
      f.add(new ClassKeyDerivation(5940876632962684213L));
      f.add(new ClassKeyDerivation(-9163140305014081363L));
      f.add(new ClassKeyDerivation(-3717775008217149215L));
      f.add(new ClassKeyDerivation(-7169918050127910160L));
      f.add(new ClassKeyDerivation(7676126672909513337L));
      f.add(new ClassKeyDerivation(7984175171404521580L));
      f.add(new ClassKeyDerivation(-4034469411707765888L));
      f.add(new ClassKeyDerivation(-6048946702651582735L));
      f.add(new ClassKeyDerivation(-9022438593901814923L));
      f.add(new ClassKeyDerivation(4065263585557890357L));
      f.add(new ClassKeyDerivation(7516064928828312773L));
      f.add(new ClassKeyDerivation(2894238454466158950L));
      f.add(new ClassKeyDerivation(3383007306181768316L));
      f.add(new ClassKeyDerivation(4496380774492054048L));
      f.add(new ClassKeyDerivation(5628912370042409052L));
      f.add(new ClassKeyDerivation(1359410905514645927L));
      f.add(new ClassKeyDerivation(5065354072038333480L));
      f.add(new ClassKeyDerivation(3359504908355020675L));
      f.add(new ClassKeyDerivation(-528315500075972703L));
      f.add(new ClassKeyDerivation(4932903538752357724L));
      f.add(new ClassKeyDerivation(-266290032716692015L));
      f.add(new ClassKeyDerivation(5248023979068505451L));
      f.add(new ClassKeyDerivation(5957821627603601683L));
      f.add(new ClassKeyDerivation(-1987763215292590482L));
      f.add(new ClassKeyDerivation(5953260326525859548L));
      f.add(new ClassKeyDerivation(-7857844567269225098L));
      f.add(new ClassKeyDerivation(2199024158578941868L));
      f.add(new ClassKeyDerivation(6062864540998683496L));
      f.add(new ClassKeyDerivation(8707189977575883673L));
      f.add(new ClassKeyDerivation(3772407320955786255L));
      f.add(new ClassKeyDerivation(2087060904766314147L));
      f.add(new ClassKeyDerivation(2169728240070238480L));
      f.add(new ClassKeyDerivation(-3429114182351035007L));
      f.add(new ClassKeyDerivation(-6050605775026053125L));
      f.add(new ClassKeyDerivation(7803609791674838014L));
      f.add(new ClassKeyDerivation(464562005866963416L));
      f.add(new ClassKeyDerivation(-1722096109557722590L));
      f.add(new ClassKeyDerivation(8965918183844229493L));
      f.add(new ClassKeyDerivation(7826672699318671852L));
      e = (int[])e.clone();
      f.add(new ClassKeyDerivation(6938088992840838206L));
      f.add(new ClassKeyDerivation(6364174139978334174L));
      f.add(new ClassKeyDerivation(2774625754947343529L));
      f.add(new ClassKeyDerivation(-8725629366029568635L));
      f.add(new ClassKeyDerivation(3369831147708992729L));
      f.add(new ClassKeyDerivation(7379551897613497704L));
      f.add(new ClassKeyDerivation(4183325692909324673L));
      f.add(new ClassKeyDerivation(-3282916430098747919L));
      f.add(new ClassKeyDerivation(-861317384829179278L));
      f.add(new ClassKeyDerivation(7417180826677642081L));
      f.add(new ClassKeyDerivation(-5009930810535170225L));
      f.add(new ClassKeyDerivation(6077121620047873799L));
      f.add(new ClassKeyDerivation(5066045332353061966L));
      f.add(new ClassKeyDerivation(1356584580004893140L));
      f.add(new ClassKeyDerivation(2037690325385237981L));
      f.add(new ClassKeyDerivation(642861916771809544L));
      f.add(new ClassKeyDerivation(-7465902959167641312L));
      f.add(new ClassKeyDerivation(-987149860448001240L));
      f.add(new ClassKeyDerivation(-4441824050732103937L));
      f.add(new ClassKeyDerivation(-2736756383048671696L));
      f.add(new ClassKeyDerivation(-6578722718394484227L));
      f.add(new ClassKeyDerivation(-4130993378778393471L));
      f.add(new ClassKeyDerivation(-4445642184212502892L));
      f.add(new ClassKeyDerivation(8800960475004133376L));
      f.add(new ClassKeyDerivation(-4121616811172711704L));
      f.add(new ClassKeyDerivation(-3702784539845371134L));
      f.add(new ClassKeyDerivation(1893991385377460905L));
      f.add(new ClassKeyDerivation(7563300962086484830L));
      f.add(new ClassKeyDerivation(-7141217725127871760L));
      f.add(new ClassKeyDerivation(-2283299721891826365L));
      f.add(new ClassKeyDerivation(8105612172871373840L));
      f.add(new ClassKeyDerivation(5391115577402069625L));
      f.add(new ClassKeyDerivation(-4754982239192160657L));
      f.add(new ClassKeyDerivation(-6453666419828159008L));
      f.add(new ClassKeyDerivation(-3834152981526153976L));
      f.add(new ClassKeyDerivation(3231353198203903028L));
      f.add(new ClassKeyDerivation(-6760168616715235338L));
      f.add(new ClassKeyDerivation(2452081553763585944L));
      f.add(new ClassKeyDerivation(5107715507343254449L));
      f.add(new ClassKeyDerivation(5372168491362120029L));
      f.add(new ClassKeyDerivation(454093272466928372L));
      f.add(new ClassKeyDerivation(4522826361975386497L));
      f.add(new ClassKeyDerivation(5789140558212519979L));
      f.add(new ClassKeyDerivation(-6276923131463109658L));
      f.add(new ClassKeyDerivation(7963651235194990407L));
      f.add(new ClassKeyDerivation(8403072651056144163L));
      f.add(new ClassKeyDerivation(-4152150094787229542L));
      f.add(new ClassKeyDerivation(-3156898686685098167L));
      f.add(new ClassKeyDerivation(-1993078268136530961L));
      f.add(new ClassKeyDerivation(5062187341489245826L));
      f.add(new ClassKeyDerivation(-4161159419953326404L));
      f.add(new ClassKeyDerivation(5356284398382218185L));
      f.add(new ClassKeyDerivation(2882883889575812005L));
      f.add(new ClassKeyDerivation(-3297040323715734766L));
      f.add(new ClassKeyDerivation(-2407697292053593716L));
      f.add(new ClassKeyDerivation(5198101471298173337L));
      f.add(new ClassKeyDerivation(6070035640428653666L));
      f.add(new ClassKeyDerivation(1331621247438040017L));
      f.add(new ClassKeyDerivation(1611969427824511357L));
      f.add(new ClassKeyDerivation(-2571308609817875023L));
      f.add(new ClassKeyDerivation(3487275747721013428L));
      f.add(new ClassKeyDerivation(8828172367920680564L));
      f.add(new ClassKeyDerivation(2742443298101049293L));
      f.add(new ClassKeyDerivation(-572765364567103483L));
      f.add(new ClassKeyDerivation(-5266972580173830197L));
      f.add(new ClassKeyDerivation(1928458077574163196L));
      f.add(new ClassKeyDerivation(-3543669496732331080L));
      f.add(new ClassKeyDerivation(-1553131105920648679L));
      f.add(new ClassKeyDerivation(6393123680859541181L));
      f.add(new ClassKeyDerivation(-2719564423148050696L));
      f.add(new ClassKeyDerivation(4390091713687778182L));
      f.add(new ClassKeyDerivation(211699523022205677L));
      f.add(new ClassKeyDerivation(2619502254343339937L));
      f.add(new ClassKeyDerivation(-532652041855053388L));
      f.add(new ClassKeyDerivation(-4014517578327199418L));
      e = (int[])e.clone();
      f.add(new ClassKeyDerivation(-4688377250669258250L));
      f.add(new ClassKeyDerivation(5799859733591784269L));
      f.add(new ClassKeyDerivation(504121711043198203L));
      f.add(new ClassKeyDerivation(-5877982805090198249L));
      f.add(new ClassKeyDerivation(-528901247373182327L));
      f.add(new ClassKeyDerivation(-1226190326070134933L));
      f.add(new ClassKeyDerivation(59835632364346292L));
      f.add(new ClassKeyDerivation(6535436088246437008L));
      f.add(new ClassKeyDerivation(4077415978171060041L));
      f.add(new ClassKeyDerivation(-8592424118599421019L));
      f.add(new ClassKeyDerivation(-7580848033836601905L));
      f.add(new ClassKeyDerivation(-3971174840113189836L));
      f.add(new ClassKeyDerivation(-5148580233937349465L));
      f.add(new ClassKeyDerivation(-6885589539290744018L));
      f.add(new ClassKeyDerivation(-2987600886041938892L));
      f.add(new ClassKeyDerivation(6509583002452447805L));
      f.add(new ClassKeyDerivation(7673863749721437218L));
      f.add(new ClassKeyDerivation(-6432831796175565088L));
      f.add(new ClassKeyDerivation(1652286552048125485L));
      f.add(new ClassKeyDerivation(-1430472277703079554L));
      f.add(new ClassKeyDerivation(5928798136235434675L));
      f.add(new ClassKeyDerivation(-242857869843272197L));
      f.add(new ClassKeyDerivation(2227508906776071862L));
      f.add(new ClassKeyDerivation(-9183198062329840700L));
      f.add(new ClassKeyDerivation(8707445496987403137L));
      f.add(new ClassKeyDerivation(433986268467985475L));
      f.add(new ClassKeyDerivation(-4568464924746953942L));
      f.add(new ClassKeyDerivation(-2102325272095125006L));
      f.add(new ClassKeyDerivation(-1980229113448728635L));
      f.add(new ClassKeyDerivation(3462342904227659255L));
      f.add(new ClassKeyDerivation(-271753078489397635L));
      f.add(new ClassKeyDerivation(7976533661481463955L));
      f.add(new ClassKeyDerivation(-7595826892291978902L));
      f.add(new ClassKeyDerivation(3945946339204216005L));
      f.add(new ClassKeyDerivation(5223730131437205480L));
      f.add(new ClassKeyDerivation(1043609695076823682L));
      f.add(new ClassKeyDerivation(-5325746594300081422L));
      f.add(new ClassKeyDerivation(-3734070567217015646L));
      f.add(new ClassKeyDerivation(7691604978222340291L));
      f.add(new ClassKeyDerivation(4327040111038713426L));
      f.add(new ClassKeyDerivation(-1729317318369873496L));
      f.add(new ClassKeyDerivation(-3452042047071568647L));
      f.add(new ClassKeyDerivation(1739943891988694735L));
      f.add(new ClassKeyDerivation(-4252947294502060795L));
      f.add(new ClassKeyDerivation(-1694615466691354446L));
      f.add(new ClassKeyDerivation(5336886278332864545L));
      f.add(new ClassKeyDerivation(-4713423120772033223L));
      f.add(new ClassKeyDerivation(-8870837495534531360L));
      f.add(new ClassKeyDerivation(-7281331427847752379L));
      f.add(new ClassKeyDerivation(2659898678242658480L));
      f.add(new ClassKeyDerivation(4662507201292109689L));
      f.add(new ClassKeyDerivation(3670722992336568209L));
      f.add(new ClassKeyDerivation(8523436603630406927L));
      f.add(new ClassKeyDerivation(4061393828267780592L));
      f.add(new ClassKeyDerivation(-2582068817070479457L));
      f.add(new ClassKeyDerivation(8654198321068439249L));
      f.add(new ClassKeyDerivation(8954996769497105508L));
      f.add(new ClassKeyDerivation(-8904295983715033974L));
      f.add(new ClassKeyDerivation(7067132256644656272L));
      f.add(new ClassKeyDerivation(896170351500380044L));
      f.add(new ClassKeyDerivation(8102542782623416980L));
      f.add(new ClassKeyDerivation(1913816009920565094L));
      f.add(new ClassKeyDerivation(8264372384449179468L));
      f.add(new ClassKeyDerivation(4523126918318490711L));
      f.add(new ClassKeyDerivation(-1864517625868666446L));
      f.add(new ClassKeyDerivation(-8870769929051771188L));
      f.add(new ClassKeyDerivation(-2539544408348455133L));
      f.add(new ClassKeyDerivation(6532505208500959238L));
      f.add(new ClassKeyDerivation(1911149733977213133L));
      f.add(new ClassKeyDerivation(6214518869477424065L));
      f.add(new ClassKeyDerivation(-224889200012828244L));
      f.add(new ClassKeyDerivation(-8148032600452064630L));
      f.add(new ClassKeyDerivation(-7145153998822153069L));
      f.add(new ClassKeyDerivation(-6428961858003557136L));
      f.add(new ClassKeyDerivation(-8061543215599197720L));
      f.add(new ClassKeyDerivation(7393255982806772905L));
      f.add(new ClassKeyDerivation(-2416727533835888332L));
      f.add(new ClassKeyDerivation(213495757822235615L));
      f.add(new ClassKeyDerivation(2326613010632970507L));
      f.add(new ClassKeyDerivation(-8027171293920768803L));
      f.add(new ClassKeyDerivation(-6129376575732407520L));
      f.add(new ClassKeyDerivation(-271776711137047463L));
      f.add(new ClassKeyDerivation(-2535512240508382590L));
      f.add(new ClassKeyDerivation(-5853439303425025609L));
      f.add(new ClassKeyDerivation(3033934069725420604L));
      f.add(new ClassKeyDerivation(7389189838860686572L));
      f.add(new ClassKeyDerivation(-6908758341876864523L));
      f.add(new ClassKeyDerivation(298998678736059306L));
      f.add(new ClassKeyDerivation(-7462909589706098481L));
      f.add(new ClassKeyDerivation(6342703323108681033L));
      f.add(new ClassKeyDerivation(5546389769533281324L));
      f.add(new ClassKeyDerivation(8855465939833538399L));
      f.add(new ClassKeyDerivation(-8437065030255766031L));
      f.add(new ClassKeyDerivation(2911731978280131469L));
      f.add(new ClassKeyDerivation(-1956262090866786153L));
      f.add(new ClassKeyDerivation(-6035469852941018679L));
      f.add(new ClassKeyDerivation(9114074418372518779L));
      f.add(new ClassKeyDerivation(-2713141159888934427L));
      f.add(new ClassKeyDerivation(-4973404483147567444L));
      f.add(new ClassKeyDerivation(3349206795992061157L));
      f.add(new ClassKeyDerivation(-4313836353895199990L));
      f.add(new ClassKeyDerivation(4469348050794612403L));
      f.add(new ClassKeyDerivation(2052879876559856256L));
      f.add(new ClassKeyDerivation(-569426183848683134L));
      f.add(new ClassKeyDerivation(-6616760379532368374L));
      f.add(new ClassKeyDerivation(-7344420595782859966L));
      f.add(new ClassKeyDerivation(-1262506606656139929L));
      f.add(new ClassKeyDerivation(-4297957025529414656L));
      f.add(new ClassKeyDerivation(3057416764550598768L));
      f.add(new ClassKeyDerivation(-2852771777754121432L));
      f.add(new ClassKeyDerivation(7681107222340527633L));
      f.add(new ClassKeyDerivation(-9057367271186668933L));
      f.add(new ClassKeyDerivation(-7426399917346015099L));
      f.add(new ClassKeyDerivation(-3110940815547481676L));
      f.add(new ClassKeyDerivation(-1537927210437234814L));
      f.add(new ClassKeyDerivation(9000244057918705035L));
      f.add(new ClassKeyDerivation(452393930211402911L));
      f.add(new ClassKeyDerivation(4379893866613162736L));
      f.add(new ClassKeyDerivation(4958647353506021086L));
      f.add(new ClassKeyDerivation(1879751351625181094L));
      f.add(new ClassKeyDerivation(3955442791058548891L));
      f.add(new ClassKeyDerivation(7949674169092640054L));
      f.add(new ClassKeyDerivation(495529895322387402L));
      f.add(new ClassKeyDerivation(-1847895387601447044L));
      f.add(new ClassKeyDerivation(8700176882440512974L));
      f.add(new ClassKeyDerivation(2205578241263781069L));
      f.add(new ClassKeyDerivation(-1113312445428431975L));
      f.add(new ClassKeyDerivation(-4463676540088140482L));
      f.add(new ClassKeyDerivation(5654894529350811009L));
      f.add(new ClassKeyDerivation(3478582313200965114L));
      f.add(new ClassKeyDerivation(-6884728992162817742L));
      f.add(new ClassKeyDerivation(-8060578716112392727L));
      f.add(new ClassKeyDerivation(8977965380317944300L));
      f.add(new ClassKeyDerivation(-9021558130507824928L));
      f.add(new ClassKeyDerivation(-7779391180713429410L));
      f.add(new ClassKeyDerivation(2797045810670872955L));
      f.add(new ClassKeyDerivation(-4285658818482928673L));
      f.add(new ClassKeyDerivation(-1524962873932075205L));
      f.add(new ClassKeyDerivation(929485508681995311L));
      f.add(new ClassKeyDerivation(-1574491661406710488L));
      f.add(new ClassKeyDerivation(7807244745514312658L));
      f.add(new ClassKeyDerivation(1330170001914178643L));
      f.add(new ClassKeyDerivation(579025388054996190L));
      f.add(new ClassKeyDerivation(7069530495326214070L));
      f.add(new ClassKeyDerivation(-4020799258793770966L));
      f.add(new ClassKeyDerivation(-3824262379306864616L));
      f.add(new ClassKeyDerivation(7073760336653058326L));
      f.add(new ClassKeyDerivation(-2102901550887006388L));
      f.add(new ClassKeyDerivation(1177206494837637348L));
      f.add(new ClassKeyDerivation(-2091186186830708175L));
      f.add(new ClassKeyDerivation(889691414244649983L));
      f.add(new ClassKeyDerivation(5944055573927398637L));
      f.add(new ClassKeyDerivation(6487494830976178736L));
      f.add(new ClassKeyDerivation(-407094047400422006L));
      f.add(new ClassKeyDerivation(192703618171019292L));
      f.add(new ClassKeyDerivation(9117479034051152425L));
      f.add(new ClassKeyDerivation(-2113335325776120704L));
      f.add(new ClassKeyDerivation(7508594419482270848L));
      f.add(new ClassKeyDerivation(4243999700145559797L));
      f.add(new ClassKeyDerivation(-1707568487532554889L));
      f.add(new ClassKeyDerivation(-6275988125857677567L));
      f.add(new ClassKeyDerivation(-4000110671405108795L));
      f.add(new ClassKeyDerivation(-6470656409167415910L));
      f.add(new ClassKeyDerivation(-5433160819645842031L));
      f.add(new ClassKeyDerivation(214317167242534548L));
      f.add(new ClassKeyDerivation(-7459280368339823097L));
      f.add(new ClassKeyDerivation(-8638790887753959929L));
      f.add(new ClassKeyDerivation(3359850088007551144L));
      f.add(new ClassKeyDerivation(-2149364122795084672L));
      f.add(new ClassKeyDerivation(4490000580895188631L));
      f.add(new ClassKeyDerivation(1325739759754197799L));
      f.add(new ClassKeyDerivation(-6178832054998588415L));
      f.add(new ClassKeyDerivation(4788314944431435383L));
      f.add(new ClassKeyDerivation(3755269046742623611L));
      f.add(new ClassKeyDerivation(8676906023125279604L));
      f.add(new ClassKeyDerivation(6102025147634717714L));
      f.add(new ClassKeyDerivation(-90477881810609545L));
      e = (int[])e.clone();
      f.add(new ClassKeyDerivation(-2103770377979846012L));
      f.add(new ClassKeyDerivation(-1869231450690659678L));
      f.add(new ClassKeyDerivation(6487881712820632842L));
      f.add(new ClassKeyDerivation(-8031891397602849459L));
      f.add(new ClassKeyDerivation(4229792198753452922L));
      f.add(new ClassKeyDerivation(-8878088354816326982L));
      f.add(new ClassKeyDerivation(2767512138439657308L));
      f.add(new ClassKeyDerivation(740983269103280024L));
      f.add(new ClassKeyDerivation(747302723301262275L));
      f.add(new ClassKeyDerivation(-2298191973553780361L));
      f.add(new ClassKeyDerivation(-1672792989526260918L));
      f.add(new ClassKeyDerivation(3032658089022227399L));
      f.add(new ClassKeyDerivation(5672187470915894763L));
      f.add(new ClassKeyDerivation(-3598255382370198479L));
      f.add(new ClassKeyDerivation(-3865880260877794242L));
      f.add(new ClassKeyDerivation(4919101707039434279L));
      f.add(new ClassKeyDerivation(-4994385781118080282L));
      f.add(new ClassKeyDerivation(-3708740307202356725L));
      f.add(new ClassKeyDerivation(-8212605834928944577L));
      f.add(new ClassKeyDerivation(-8349287928668205965L));
      f.add(new ClassKeyDerivation(-534020417749402418L));
      f.add(new ClassKeyDerivation(-5696805067813850054L));
      f.add(new ClassKeyDerivation(4039873905804734084L));
      f.add(new ClassKeyDerivation(-6727884852203363318L));
      f.add(new ClassKeyDerivation(7641993370943890300L));
      f.add(new ClassKeyDerivation(3777865117483216020L));
      f.add(new ClassKeyDerivation(-9164851320398647323L));
      f.add(new ClassKeyDerivation(2326116035443439213L));
      f.add(new ClassKeyDerivation(-7044391731639218213L));
      f.add(new ClassKeyDerivation(7358166466637095812L));
      f.add(new ClassKeyDerivation(-3007419545732265165L));
      f.add(new ClassKeyDerivation(-2981192532402807787L));
      f.add(new ClassKeyDerivation(2082677588856505382L));
      f.add(new ClassKeyDerivation(8231380007485028170L));
      f.add(new ClassKeyDerivation(2187158790872740119L));
      f.add(new ClassKeyDerivation(3624643689199803979L));
      f.add(new ClassKeyDerivation(2626676438645186282L));
      f.add(new ClassKeyDerivation(-2971928698581546828L));
      f.add(new ClassKeyDerivation(-1716337971185698496L));
      f.add(new ClassKeyDerivation(7237445753770862123L));
      f.add(new ClassKeyDerivation(-4282306300230335402L));
      f.add(new ClassKeyDerivation(8838981688617185977L));
      f.add(new ClassKeyDerivation(6828959013343967961L));
      f.add(new ClassKeyDerivation(-8044866022881432984L));
      f.add(new ClassKeyDerivation(6252920580660762488L));
      f.add(new ClassKeyDerivation(8369747438859673238L));
      f.add(new ClassKeyDerivation(3045950440681862307L));
      f.add(new ClassKeyDerivation(-4861115844778944209L));
      f.add(new ClassKeyDerivation(-8055771635886791676L));
      f.add(new ClassKeyDerivation(-118929690669253024L));
      f.add(new ClassKeyDerivation(8964403081895311524L));
      f.add(new ClassKeyDerivation(5623211069515669033L));
      f.add(new ClassKeyDerivation(1782726157760129138L));
      f.add(new ClassKeyDerivation(-4168937909753727597L));
      f.add(new ClassKeyDerivation(-3574048313304507809L));
      f.add(new ClassKeyDerivation(-6297943956365545839L));
      f.add(new ClassKeyDerivation(-4425690572509784516L));
      f.add(new ClassKeyDerivation(-7780852336126663712L));
      f.add(new ClassKeyDerivation(3462828950886045281L));
      f.add(new ClassKeyDerivation(-5877259187625249252L));
      f.add(new ClassKeyDerivation(-8199946445600596793L));
      f.add(new ClassKeyDerivation(884906028282918582L));
      f.add(new ClassKeyDerivation(-5300119724426149364L));
      f.add(new ClassKeyDerivation(3084358197649447193L));
      f.add(new ClassKeyDerivation(7702965153631211087L));
      f.add(new ClassKeyDerivation(-7774265508284233664L));
      f.add(new ClassKeyDerivation(39107783096933739L));
      f.add(new ClassKeyDerivation(-3967087650769736539L));
      f.add(new ClassKeyDerivation(-6288925457915797340L));
      f.add(new ClassKeyDerivation(-6039452871770415098L));
      f.add(new ClassKeyDerivation(5776059247569046851L));
      f.add(new ClassKeyDerivation(196089059284949136L));
      f.add(new ClassKeyDerivation(-5288530551080523745L));
      f.add(new ClassKeyDerivation(-8172074163064027563L));
      f.add(new ClassKeyDerivation(-4703609794464558365L));
      f.add(new ClassKeyDerivation(-6578387572437060376L));
      f.add(new ClassKeyDerivation(-8903296016844255495L));
      f.add(new ClassKeyDerivation(-2155791206690484871L));
      f.add(new ClassKeyDerivation(-6338345805775858011L));
      f.add(new ClassKeyDerivation(-5173999090051537394L));
      f.add(new ClassKeyDerivation(6079291181248683953L));
      f.add(new ClassKeyDerivation(-6309064726257332966L));
      f.add(new ClassKeyDerivation(4088311148087192112L));
      f.add(new ClassKeyDerivation(342125208507936543L));
      f.add(new ClassKeyDerivation(-3744836629382088835L));
      f.add(new ClassKeyDerivation(1070926416773266222L));
      f.add(new ClassKeyDerivation(3467927369150102793L));
      f.add(new ClassKeyDerivation(8938943967251265695L));
      f.add(new ClassKeyDerivation(-3453918074558314480L));
      f.add(new ClassKeyDerivation(-944680347204306885L));
      f.add(new ClassKeyDerivation(2480724220666387939L));
      f.add(new ClassKeyDerivation(6821635720833908445L));
      f.add(new ClassKeyDerivation(8362329576887647872L));
      f.add(new ClassKeyDerivation(-7890495228296610272L));
      f.add(new ClassKeyDerivation(-1681243899707005335L));
      f.add(new ClassKeyDerivation(-6181709020195463830L));
      f.add(new ClassKeyDerivation(7064791815610616932L));
      f.add(new ClassKeyDerivation(2023446181290869019L));
      f.add(new ClassKeyDerivation(7218375362835060969L));
      f.add(new ClassKeyDerivation(6941473046302552776L));
      f.add(new ClassKeyDerivation(-4741213256094922972L));
      f.add(new ClassKeyDerivation(-4707156573560542952L));
      f.add(new ClassKeyDerivation(3493694815144834064L));
      f.add(new ClassKeyDerivation(-366213209674733460L));
      f.add(new ClassKeyDerivation(-2440541804758097830L));
      f.add(new ClassKeyDerivation(-6602845276842615330L));
      f.add(new ClassKeyDerivation(-2117063700182989936L));
      f.add(new ClassKeyDerivation(-806454036740331294L));
      f.add(new ClassKeyDerivation(5527696817840021243L));
      f.add(new ClassKeyDerivation(-1969144900832702765L));
      f.add(new ClassKeyDerivation(726115513104942135L));
      f.add(new ClassKeyDerivation(7819606443567403989L));
      f.add(new ClassKeyDerivation(1627014966239050568L));
      f.add(new ClassKeyDerivation(1037580796863600430L));
      f.add(new ClassKeyDerivation(-1088804378221194140L));
      f.add(new ClassKeyDerivation(5921591079071737071L));
      f.add(new ClassKeyDerivation(-5563712753535226227L));
      f.add(new ClassKeyDerivation(9099078681470344546L));
      f.add(new ClassKeyDerivation(-3887532952517515340L));
      f.add(new ClassKeyDerivation(-1568123424417544581L));
      f.add(new ClassKeyDerivation(-9151837160970660380L));
      f.add(new ClassKeyDerivation(-3405643962589572643L));
      f.add(new ClassKeyDerivation(2602075769155970210L));
      f.add(new ClassKeyDerivation(8676210001937419828L));
      f.add(new ClassKeyDerivation(-2257137898010754758L));
      f.add(new ClassKeyDerivation(-5441195372430814851L));
      f.add(new ClassKeyDerivation(1463400703258719799L));
      f.add(new ClassKeyDerivation(-818154288853983709L));
      f.add(new ClassKeyDerivation(755425034828494851L));
      f.add(new ClassKeyDerivation(-3001009604269095652L));
      f.add(new ClassKeyDerivation(1042089480940792208L));
      f.add(new ClassKeyDerivation(-6010252528956038530L));
      f.add(new ClassKeyDerivation(6082715109468414234L));
      f.add(new ClassKeyDerivation(4347578022547417937L));
      f.add(new ClassKeyDerivation(3914823551292368306L));
      f.add(new ClassKeyDerivation(-7057510277603663192L));
      f.add(new ClassKeyDerivation(-119997762098271557L));
      f.add(new ClassKeyDerivation(2662211790231348389L));
      f.add(new ClassKeyDerivation(-8445743654780508271L));
      f.add(new ClassKeyDerivation(4799542052201727138L));
      f.add(new ClassKeyDerivation(4501446515766933443L));
      f.add(new ClassKeyDerivation(-3998465089895277629L));
      f.add(new ClassKeyDerivation(-2134784182619371437L));
      f.add(new ClassKeyDerivation(-8758944497622477952L));
      f.add(new ClassKeyDerivation(-7753484029449717445L));
      f.add(new ClassKeyDerivation(3474553790034302509L));
      f.add(new ClassKeyDerivation(6920768189363929571L));
      f.add(new ClassKeyDerivation(-5852950636233652280L));
      f.add(new ClassKeyDerivation(6670051512529343733L));
      f.add(new ClassKeyDerivation(-1536090674450824445L));
      f.add(new ClassKeyDerivation(-690211447580606040L));
      f.add(new ClassKeyDerivation(3467515736287706804L));
      f.add(new ClassKeyDerivation(-4277029594753261459L));
      f.add(new ClassKeyDerivation(-248710725907982174L));
      f.add(new ClassKeyDerivation(5909092692960946926L));
      f.add(new ClassKeyDerivation(3647205105641560124L));
      e = (int[])e.clone();
      f.add(new ClassKeyDerivation(2050135192651493405L));
      f.add(new ClassKeyDerivation(-6037544518723588954L));
      f.add(new ClassKeyDerivation(-528427386989529185L));
      f.add(new ClassKeyDerivation(-1131178248480316887L));
      f.add(new ClassKeyDerivation(3953803611155832061L));
      f.add(new ClassKeyDerivation(7373257017433731704L));
      f.add(new ClassKeyDerivation(4383911504766567190L));
      f.add(new ClassKeyDerivation(-3987397789226089927L));
      f.add(new ClassKeyDerivation(1344093664166942095L));
      f.add(new ClassKeyDerivation(-5467143462522765839L));
      f.add(new ClassKeyDerivation(-227979059320355358L));
      f.add(new ClassKeyDerivation(-946000737084131796L));
      f.add(new ClassKeyDerivation(-7430412735272298437L));
      f.add(new ClassKeyDerivation(2769811379115831720L));
      f.add(new ClassKeyDerivation(7128424127641963228L));
      f.add(new ClassKeyDerivation(5100620697191977250L));
      f.add(new ClassKeyDerivation(1481429362516263433L));
      f.add(new ClassKeyDerivation(7239814595875117789L));
      f.add(new ClassKeyDerivation(-405795519531969948L));
      f.add(new ClassKeyDerivation(-114900286816379636L));
      f.add(new ClassKeyDerivation(-2147379133605991313L));
      f.add(new ClassKeyDerivation(-6180356896479753757L));
      f.add(new ClassKeyDerivation(-4559798531260732010L));
      f.add(new ClassKeyDerivation(-4116618500644652645L));
      f.add(new ClassKeyDerivation(9140915329600327651L));
      f.add(new ClassKeyDerivation(2170086358203638752L));
      f.add(new ClassKeyDerivation(1456076346182585788L));
      f.add(new ClassKeyDerivation(-2984827539111435766L));
      f.add(new ClassKeyDerivation(5975071716379517214L));
      f.add(new ClassKeyDerivation(-7455058780820654620L));
      f.add(new ClassKeyDerivation(723746179597404245L));
      f.add(new ClassKeyDerivation(7832107533668508636L));
      f.add(new ClassKeyDerivation(1022911959793717767L));
      f.add(new ClassKeyDerivation(-7759221121611068102L));
      f.add(new ClassKeyDerivation(-7439596177250518221L));
      f.add(new ClassKeyDerivation(2359045178820507681L));
      f.add(new ClassKeyDerivation(-226361476296663181L));
      f.add(new ClassKeyDerivation(-800288544700955625L));
      f.add(new ClassKeyDerivation(-5554762126481148095L));
      f.add(new ClassKeyDerivation(-3881219833347782109L));
      f.add(new ClassKeyDerivation(6833370853467721682L));
      f.add(new ClassKeyDerivation(3059263331134201004L));
      f.add(new ClassKeyDerivation(-7716753651010276842L));
      f.add(new ClassKeyDerivation(-2255658845646570083L));
      f.add(new ClassKeyDerivation(-2590182434156093965L));
      f.add(new ClassKeyDerivation(1192518358646188718L));
      f.add(new ClassKeyDerivation(-6136019569823349916L));
      f.add(new ClassKeyDerivation(-6027630408965293195L));
      f.add(new ClassKeyDerivation(7116782922691828416L));
      f.add(new ClassKeyDerivation(-5727373939724988409L));
      f.add(new ClassKeyDerivation(-235952602453126870L));
      f.add(new ClassKeyDerivation(5684442177936892026L));
      f.add(new ClassKeyDerivation(-7630677783479822776L));
      f.add(new ClassKeyDerivation(-8919754353518794468L));
      f.add(new ClassKeyDerivation(3911502203022939704L));
      f.add(new ClassKeyDerivation(-3299862103360804955L));
      f.add(new ClassKeyDerivation(-2810708835493188216L));
      f.add(new ClassKeyDerivation(-2561069316135149827L));
      f.add(new ClassKeyDerivation(-2389205608677100615L));
      f.add(new ClassKeyDerivation(3897019145878735637L));
      f.add(new ClassKeyDerivation(3335747815530463533L));
      f.add(new ClassKeyDerivation(1164662240057224335L));
      f.add(new ClassKeyDerivation(7515781705563792694L));
      f.add(new ClassKeyDerivation(-1996370539761237021L));
      f.add(new ClassKeyDerivation(451799978048216335L));
      f.add(new ClassKeyDerivation(7123924161120999274L));
      f.add(new ClassKeyDerivation(6926814319493876539L));
      f.add(new ClassKeyDerivation(-9165842506025239754L));
      f.add(new ClassKeyDerivation(2037593643957140276L));
      f.add(new ClassKeyDerivation(-8319045480683962559L));
      f.add(new ClassKeyDerivation(-2842800599767014500L));
      f.add(new ClassKeyDerivation(7980937207628807798L));
      f.add(new ClassKeyDerivation(8831870174223819282L));
      f.add(new ClassKeyDerivation(5818348717060150541L));
      f.add(new ClassKeyDerivation(2758837229024235013L));
      f.add(new ClassKeyDerivation(-3598867379917405939L));
      f.add(new ClassKeyDerivation(-7620059356195434890L));
      f.add(new ClassKeyDerivation(-4553499218711917123L));
      f.add(new ClassKeyDerivation(-9030523997781224143L));
      e = (int[])e.clone();
      f.add(new ClassKeyDerivation(-1960677925728349313L));
      f.add(new ClassKeyDerivation(-1402071964730563305L));
      f.add(new ClassKeyDerivation(2625146094517595882L));
      f.add(new ClassKeyDerivation(-848984830169422447L));
      f.add(new ClassKeyDerivation(9085486253754483012L));
      f.add(new ClassKeyDerivation(-1550283147831446473L));
      f.add(new ClassKeyDerivation(1911480835894367047L));
      f.add(new ClassKeyDerivation(3486725250961801131L));
      f.add(new ClassKeyDerivation(7985487115610501315L));
      f.add(new ClassKeyDerivation(-8303103659887283303L));
      f.add(new ClassKeyDerivation(923088931141714151L));
      f.add(new ClassKeyDerivation(-3587266080268185966L));
      f.add(new ClassKeyDerivation(6107121124421515108L));
      f.add(new ClassKeyDerivation(-6719131289447277060L));
      f.add(new ClassKeyDerivation(4208722915581821074L));
      f.add(new ClassKeyDerivation(1794247673043096170L));
      f.add(new ClassKeyDerivation(1881188386883487829L));
      f.add(new ClassKeyDerivation(8425085895459663741L));
      f.add(new ClassKeyDerivation(-409615093939019389L));
      f.add(new ClassKeyDerivation(6534849081869813495L));
      f.add(new ClassKeyDerivation(-5906376588728440347L));
      f.add(new ClassKeyDerivation(-6435969736153117812L));
      f.add(new ClassKeyDerivation(-4750383198131459492L));
      f.add(new ClassKeyDerivation(-3264951786877864144L));
      f.add(new ClassKeyDerivation(6207207973266239801L));
      f.add(new ClassKeyDerivation(2796881043331485659L));
      f.add(new ClassKeyDerivation(4673882882110878492L));
      f.add(new ClassKeyDerivation(-7305584722046043120L));
      f.add(new ClassKeyDerivation(8973913517941111406L));
      f.add(new ClassKeyDerivation(6944159374928260895L));
      f.add(new ClassKeyDerivation(-2295028389275436396L));
      f.add(new ClassKeyDerivation(-8353869913029204351L));
      f.add(new ClassKeyDerivation(4392373050056012079L));
      f.add(new ClassKeyDerivation(5972225335457876133L));
      f.add(new ClassKeyDerivation(-7031417223973411654L));
      f.add(new ClassKeyDerivation(-5562903871101088573L));
      f.add(new ClassKeyDerivation(-7023715286989493749L));
      f.add(new ClassKeyDerivation(7687623673095896693L));
      f.add(new ClassKeyDerivation(2233302749856472166L));
      f.add(new ClassKeyDerivation(1626071239660335895L));
      f.add(new ClassKeyDerivation(-9156454419140891566L));
      f.add(new ClassKeyDerivation(-6301249757904954205L));
      f.add(new ClassKeyDerivation(1223307695963889998L));
      f.add(new ClassKeyDerivation(4675172359279645302L));
      f.add(new ClassKeyDerivation(-3118230956028433213L));
      f.add(new ClassKeyDerivation(1047140731545660020L));
      f.add(new ClassKeyDerivation(1202653103833143777L));
      f.add(new ClassKeyDerivation(-8489878029274093434L));
      f.add(new ClassKeyDerivation(-2982160612615001700L));
      f.add(new ClassKeyDerivation(7648818746026449359L));
      f.add(new ClassKeyDerivation(-4004395717958820379L));
      f.add(new ClassKeyDerivation(4481187834869447414L));
      f.add(new ClassKeyDerivation(6698202779785798364L));
      f.add(new ClassKeyDerivation(-8887631278213510789L));
      f.add(new ClassKeyDerivation(3792144926003876223L));
      f.add(new ClassKeyDerivation(-3836800087336618593L));
      f.add(new ClassKeyDerivation(4086562811745090429L));
      f.add(new ClassKeyDerivation(-6707245802147339512L));
      f.add(new ClassKeyDerivation(-5751054944344646693L));
      f.add(new ClassKeyDerivation(1200039542524071752L));
      f.add(new ClassKeyDerivation(8261168057306991805L));
      f.add(new ClassKeyDerivation(-6161428665148371732L));
      f.add(new ClassKeyDerivation(595313178252250890L));
      f.add(new ClassKeyDerivation(-9204585061415612263L));
      f.add(new ClassKeyDerivation(-7449635613876389897L));
      f.add(new ClassKeyDerivation(5535538873822940457L));
      f.add(new ClassKeyDerivation(-8041939298347455324L));
      f.add(new ClassKeyDerivation(-2868558612618434472L));
      f.add(new ClassKeyDerivation(5820795719610496727L));
      f.add(new ClassKeyDerivation(-512153517547761075L));
      f.add(new ClassKeyDerivation(-3393518313121102691L));
      f.add(new ClassKeyDerivation(7408817292925856131L));
      f.add(new ClassKeyDerivation(-4411122114170024586L));
      f.add(new ClassKeyDerivation(2190762790345032951L));
      f.add(new ClassKeyDerivation(3750170275350424243L));
      f.add(new ClassKeyDerivation(-2393194697032597377L));
      f.add(new ClassKeyDerivation(2338961910770685833L));
      f.add(new ClassKeyDerivation(-1228736684607638426L));
      f.add(new ClassKeyDerivation(6670697584694468497L));
      f.add(new ClassKeyDerivation(-1128429141227367533L));
      f.add(new ClassKeyDerivation(-689581217375176632L));
      f.add(new ClassKeyDerivation(-2528256427751253387L));
      f.add(new ClassKeyDerivation(1735609201794969905L));
      f.add(new ClassKeyDerivation(-4149625226670315814L));
      f.add(new ClassKeyDerivation(-98892589714990537L));
      f.add(new ClassKeyDerivation(-1135943447668268007L));
      f.add(new ClassKeyDerivation(6111232188618698762L));
      f.add(new ClassKeyDerivation(8108969003229303527L));
      f.add(new ClassKeyDerivation(-5995274812798862294L));
      f.add(new ClassKeyDerivation(4230629905715240791L));
      f.add(new ClassKeyDerivation(4535279004804419578L));
      f.add(new ClassKeyDerivation(-6049606306669043758L));
      f.add(new ClassKeyDerivation(-365910700767122827L));
      f.add(new ClassKeyDerivation(6642581778766908684L));
      f.add(new ClassKeyDerivation(344815644143505296L));
      f.add(new ClassKeyDerivation(-8029379870266959943L));
      f.add(new ClassKeyDerivation(3634995230409067208L));
      f.add(new ClassKeyDerivation(8968000217361621392L));
      f.add(new ClassKeyDerivation(-5863453750325453764L));
      f.add(new ClassKeyDerivation(8395951201314227662L));
      f.add(new ClassKeyDerivation(3896560034976981511L));
      f.add(new ClassKeyDerivation(-8632789134216814713L));
      f.add(new ClassKeyDerivation(-6173407634004345492L));
      f.add(new ClassKeyDerivation(8543722240475448864L));
      f.add(new ClassKeyDerivation(-2552601084405010757L));
      f.add(new ClassKeyDerivation(1651274940036106732L));
      f.add(new ClassKeyDerivation(3777122729005827841L));
      f.add(new ClassKeyDerivation(-2823898219909890388L));
      f.add(new ClassKeyDerivation(3913759130460234010L));
      f.add(new ClassKeyDerivation(8683963706244139917L));
      f.add(new ClassKeyDerivation(-4413899887897982446L));
      f.add(new ClassKeyDerivation(-7437236692727219821L));
      f.add(new ClassKeyDerivation(7507536151825284999L));
      f.add(new ClassKeyDerivation(481015650703444890L));
      f.add(new ClassKeyDerivation(3918070270709793321L));
      f.add(new ClassKeyDerivation(-510069734584542419L));
      f.add(new ClassKeyDerivation(2167742061319176884L));
      f.add(new ClassKeyDerivation(-7721234682615761441L));
      f.add(new ClassKeyDerivation(294092062273829932L));
      f.add(new ClassKeyDerivation(4963035246579595922L));
      f.add(new ClassKeyDerivation(6651824735710358362L));
      f.add(new ClassKeyDerivation(6532238233704730054L));
      f.add(new ClassKeyDerivation(-8193791657738929626L));
      f.add(new ClassKeyDerivation(-3854393257372802892L));
      f.add(new ClassKeyDerivation(5387581985891279681L));
      f.add(new ClassKeyDerivation(-6168053657938293616L));
      f.add(new ClassKeyDerivation(1445709382759263436L));
      f.add(new ClassKeyDerivation(5367953226064058091L));
      f.add(new ClassKeyDerivation(1357149892426654124L));
      f.add(new ClassKeyDerivation(-3717517097766465159L));
      f.add(new ClassKeyDerivation(-7999990674810630563L));
      f.add(new ClassKeyDerivation(1339149236671993002L));
      f.add(new ClassKeyDerivation(-3243728770171214107L));
      f.add(new ClassKeyDerivation(-7334214957103697056L));
      f.add(new ClassKeyDerivation(-1964155961316565689L));
      f.add(new ClassKeyDerivation(-1109420671515202277L));
      f.add(new ClassKeyDerivation(7104423598063669668L));
      f.add(new ClassKeyDerivation(-3421048609050439365L));
      f.add(new ClassKeyDerivation(-986048087227039479L));
      f.add(new ClassKeyDerivation(5538809577236736926L));
      f.add(new ClassKeyDerivation(2637016050639014080L));
      f.add(new ClassKeyDerivation(630919845228994129L));
      f.add(new ClassKeyDerivation(1457224886514608891L));
      f.add(new ClassKeyDerivation(5111407869912522756L));
      f.add(new ClassKeyDerivation(-4843955114536440754L));
      f.add(new ClassKeyDerivation(1163197943968414053L));
      f.add(new ClassKeyDerivation(-8923232370110861403L));
      f.add(new ClassKeyDerivation(-251919578209459747L));
      f.add(new ClassKeyDerivation(-1999796232196335093L));
      f.add(new ClassKeyDerivation(2520470967191686541L));
      f.add(new ClassKeyDerivation(5913897868207865689L));
      f.add(new ClassKeyDerivation(3072570404142978597L));
      f.add(new ClassKeyDerivation(8945892303418287793L));
      f.add(new ClassKeyDerivation(-1152328585002891365L));
      f.add(new ClassKeyDerivation(5056779527682788048L));
      f.add(new ClassKeyDerivation(-6443338177693602854L));
      f.add(new ClassKeyDerivation(4628139989903035735L));
      f.add(new ClassKeyDerivation(1047628659675989732L));
      f.add(new ClassKeyDerivation(-5460991851365863726L));
      f.add(new ClassKeyDerivation(5785801808934067130L));
      f.add(new ClassKeyDerivation(7809112290835851394L));
      e = (int[])e.clone();
      f.add(new ClassKeyDerivation(-4743110045836710719L));
      f.add(new ClassKeyDerivation(-9203626740812080921L));
      f.add(new ClassKeyDerivation(-4297075197466312572L));
      f.add(new ClassKeyDerivation(-2266512533597564477L));
      f.add(new ClassKeyDerivation(4645065834611931812L));
      f.add(new ClassKeyDerivation(-1680832192339515275L));
      f.add(new ClassKeyDerivation(-8019958075017288170L));
      f.add(new ClassKeyDerivation(-4019286350735969026L));
      f.add(new ClassKeyDerivation(-5000601905937037314L));
      f.add(new ClassKeyDerivation(3173287368367903200L));
      f.add(new ClassKeyDerivation(-4549376874923659483L));
      f.add(new ClassKeyDerivation(4613812332415417218L));
      f.add(new ClassKeyDerivation(-374966902774952941L));
      f.add(new ClassKeyDerivation(6116804384777621672L));
      f.add(new ClassKeyDerivation(-1295085516814480240L));
      f.add(new ClassKeyDerivation(1339397901764429870L));
      f.add(new ClassKeyDerivation(-2013805771725744065L));
      f.add(new ClassKeyDerivation(6792796929800008748L));
      f.add(new ClassKeyDerivation(5255763857656436823L));
      f.add(new ClassKeyDerivation(-3700608386247597636L));
      f.add(new ClassKeyDerivation(3628519144004910828L));
      f.add(new ClassKeyDerivation(4524774303675262274L));
      f.add(new ClassKeyDerivation(-6003806872211455069L));
      f.add(new ClassKeyDerivation(-430034970501238834L));
      f.add(new ClassKeyDerivation(-2833426643213011901L));
      f.add(new ClassKeyDerivation(1452823356049719824L));
      f.add(new ClassKeyDerivation(-4979148939056147820L));
      f.add(new ClassKeyDerivation(-4603057574233182985L));
      f.add(new ClassKeyDerivation(3515281463890319834L));
      f.add(new ClassKeyDerivation(6086968673760784605L));
      f.add(new ClassKeyDerivation(53911004420830377L));
      f.add(new ClassKeyDerivation(7684677693979314471L));
      f.add(new ClassKeyDerivation(-1296131546905436266L));
      f.add(new ClassKeyDerivation(1465932460167966752L));
      f.add(new ClassKeyDerivation(-1273336748919700876L));
      f.add(new ClassKeyDerivation(6234194477314187932L));
      f.add(new ClassKeyDerivation(-4305945649591264796L));
      f.add(new ClassKeyDerivation(348879488634141111L));
      f.add(new ClassKeyDerivation(-2683140900795532031L));
      f.add(new ClassKeyDerivation(-5030809297345692522L));
      f.add(new ClassKeyDerivation(2047681232716129315L));
      f.add(new ClassKeyDerivation(-5756812575561173031L));
      f.add(new ClassKeyDerivation(-7441883692730861348L));
      f.add(new ClassKeyDerivation(589131186733379550L));
      f.add(new ClassKeyDerivation(3093969254910335648L));
      f.add(new ClassKeyDerivation(8262825836475784314L));
      f.add(new ClassKeyDerivation(-1290852311355887504L));
      f.add(new ClassKeyDerivation(-403419109357284442L));
      f.add(new ClassKeyDerivation(6918802417212762234L));
      f.add(new ClassKeyDerivation(1053276202274000140L));
      f.add(new ClassKeyDerivation(6349559368768097110L));
      f.add(new ClassKeyDerivation(6642932185408070195L));
      f.add(new ClassKeyDerivation(-7194558711609117843L));
      f.add(new ClassKeyDerivation(2320767474637724076L));
      f.add(new ClassKeyDerivation(460958583914876510L));
      f.add(new ClassKeyDerivation(4336512291295456925L));
      f.add(new ClassKeyDerivation(-1286453538275963542L));
      f.add(new ClassKeyDerivation(-8895099050674970864L));
      f.add(new ClassKeyDerivation(-6772003981909496794L));
      f.add(new ClassKeyDerivation(-4684391428735237554L));
      f.add(new ClassKeyDerivation(-1263053013770853688L));
      f.add(new ClassKeyDerivation(5205113232053029476L));
      f.add(new ClassKeyDerivation(-285691120806753990L));
      f.add(new ClassKeyDerivation(8707807583256841335L));
      f.add(new ClassKeyDerivation(4214194495286587251L));
      f.add(new ClassKeyDerivation(-1416366239064497829L));
      f.add(new ClassKeyDerivation(-8342891376387720521L));
      f.add(new ClassKeyDerivation(-8047726604776810279L));
      f.add(new ClassKeyDerivation(3786619152326835622L));
      f.add(new ClassKeyDerivation(-1804409430636903943L));
      f.add(new ClassKeyDerivation(1164404283134739631L));
      f.add(new ClassKeyDerivation(7537312858013054415L));
      f.add(new ClassKeyDerivation(-5014905212437796054L));
      f.add(new ClassKeyDerivation(7110301554418231795L));
      f.add(new ClassKeyDerivation(1299868501952260882L));
      f.add(new ClassKeyDerivation(-6283961948499065778L));
      f.add(new ClassKeyDerivation(3191308331318940373L));
      f.add(new ClassKeyDerivation(-655631207190811959L));
      f.add(new ClassKeyDerivation(-2580704302854200037L));
      f.add(new ClassKeyDerivation(-6624155231898642566L));
      f.add(new ClassKeyDerivation(1894305570228707235L));
      f.add(new ClassKeyDerivation(1918243335277869447L));
      f.add(new ClassKeyDerivation(4936655635005234994L));
      f.add(new ClassKeyDerivation(-7586289749192954861L));
      f.add(new ClassKeyDerivation(7991410577325466161L));
      f.add(new ClassKeyDerivation(-5153097979643254267L));
      f.add(new ClassKeyDerivation(3480133302473373295L));
      f.add(new ClassKeyDerivation(-8066849100224913648L));
      f.add(new ClassKeyDerivation(-3821950446739950358L));
      f.add(new ClassKeyDerivation(8950123506560724986L));
      f.add(new ClassKeyDerivation(-7874682584461497332L));
      f.add(new ClassKeyDerivation(5347220342674359216L));
      f.add(new ClassKeyDerivation(-8609015432402687624L));
      f.add(new ClassKeyDerivation(9143373690490663474L));
      f.add(new ClassKeyDerivation(-1103769082776877802L));
      f.add(new ClassKeyDerivation(-5263104459761521943L));
      f.add(new ClassKeyDerivation(3467964992547782403L));
      f.add(new ClassKeyDerivation(-8028136233342566066L));
      f.add(new ClassKeyDerivation(-863220410909846274L));
      e = (int[])e.clone();
      f.add(new ClassKeyDerivation(1618607176171244059L));
      f.add(new ClassKeyDerivation(-110374500340716962L));
      f.add(new ClassKeyDerivation(2513213107666234164L));
      f.add(new ClassKeyDerivation(-7759643021438804463L));
      f.add(new ClassKeyDerivation(1048577882962461644L));
      f.add(new ClassKeyDerivation(-7054105429042717083L));
      f.add(new ClassKeyDerivation(-5415028624498735378L));
      f.add(new ClassKeyDerivation(2078007685637716529L));
      f.add(new ClassKeyDerivation(1441600826667879403L));
      f.add(new ClassKeyDerivation(5532271032114701910L));
      f.add(new ClassKeyDerivation(3069474838519959649L));
      f.add(new ClassKeyDerivation(-6324792961986828880L));
      f.add(new ClassKeyDerivation(-3102829708282736198L));
      f.add(new ClassKeyDerivation(-560451432926606826L));
      f.add(new ClassKeyDerivation(4663633504183594897L));
      f.add(new ClassKeyDerivation(-3157454832522228611L));
      f.add(new ClassKeyDerivation(4516288713987316753L));
      f.add(new ClassKeyDerivation(6214978492761398394L));
      f.add(new ClassKeyDerivation(-3018970547590060551L));
      f.add(new ClassKeyDerivation(-8719506666813158219L));
      f.add(new ClassKeyDerivation(5112014680153363045L));
      f.add(new ClassKeyDerivation(-675203240592845138L));
      f.add(new ClassKeyDerivation(1182484133207376007L));
      f.add(new ClassKeyDerivation(190879557496696736L));
      f.add(new ClassKeyDerivation(7398458421801587756L));
      f.add(new ClassKeyDerivation(1166843126418010815L));
      f.add(new ClassKeyDerivation(8102338663646453759L));
      f.add(new ClassKeyDerivation(5374822355354493523L));
      f.add(new ClassKeyDerivation(-1266541856016335074L));
      f.add(new ClassKeyDerivation(7832911220001658950L));
      f.add(new ClassKeyDerivation(1197681147757894493L));
      f.add(new ClassKeyDerivation(-3280013981335255378L));
      f.add(new ClassKeyDerivation(-6014274150091817064L));
      f.add(new ClassKeyDerivation(7819688577463836238L));
      f.add(new ClassKeyDerivation(-5330353560054329826L));
      f.add(new ClassKeyDerivation(-4408075648663390842L));
      f.add(new ClassKeyDerivation(615287966488997146L));
      f.add(new ClassKeyDerivation(-2830861585924406049L));
      f.add(new ClassKeyDerivation(3488073312048542600L));
      f.add(new ClassKeyDerivation(-4547894091666588599L));
      f.add(new ClassKeyDerivation(1649130098999343031L));
      f.add(new ClassKeyDerivation(3671924207522187416L));
      f.add(new ClassKeyDerivation(-2958938526962760399L));
      f.add(new ClassKeyDerivation(5628027333740801756L));
      f.add(new ClassKeyDerivation(-5435046283763261547L));
      f.add(new ClassKeyDerivation(-6330410120334605228L));
      f.add(new ClassKeyDerivation(-7192485947687498891L));
      f.add(new ClassKeyDerivation(8988921682904700866L));
      f.add(new ClassKeyDerivation(1491759896974804203L));
      f.add(new ClassKeyDerivation(-3246397854724414593L));
      f.add(new ClassKeyDerivation(-381029071347546996L));
      f.add(new ClassKeyDerivation(-3698173050256856309L));
      f.add(new ClassKeyDerivation(7223891316461913946L));
      f.add(new ClassKeyDerivation(5197647501261081759L));
      f.add(new ClassKeyDerivation(1783631474229759184L));
      f.add(new ClassKeyDerivation(7658374006109902128L));
      f.add(new ClassKeyDerivation(8512937557755462335L));
      f.add(new ClassKeyDerivation(5196683325430899157L));
      f.add(new ClassKeyDerivation(2941649705999969065L));
      f.add(new ClassKeyDerivation(1936295336183380524L));
      f.add(new ClassKeyDerivation(588166159653040456L));
      f.add(new ClassKeyDerivation(-2385573317596497388L));
      f.add(new ClassKeyDerivation(-2992794193984015290L));
      f.add(new ClassKeyDerivation(-7155292156771110785L));
      f.add(new ClassKeyDerivation(8827440389019115973L));
      f.add(new ClassKeyDerivation(163831073633205828L));
      f.add(new ClassKeyDerivation(-9073110293977014167L));
      f.add(new ClassKeyDerivation(-7770523149680550069L));
      f.add(new ClassKeyDerivation(31764762240795720L));
      f.add(new ClassKeyDerivation(7853244739560261543L));
      f.add(new ClassKeyDerivation(-2124736532882318590L));
      f.add(new ClassKeyDerivation(-4896311672607419718L));
      f.add(new ClassKeyDerivation(-6734701340805907344L));
      f.add(new ClassKeyDerivation(-7614468644138560997L));
      f.add(new ClassKeyDerivation(-6452485477126525499L));
      f.add(new ClassKeyDerivation(2199788644205978211L));
      f.add(new ClassKeyDerivation(-2403676048338896846L));
      f.add(new ClassKeyDerivation(-4025462771246495233L));
      f.add(new ClassKeyDerivation(2227136970243068378L));
      f.add(new ClassKeyDerivation(-8585956398324499307L));
      f.add(new ClassKeyDerivation(-1956948728595287071L));
      f.add(new ClassKeyDerivation(-8178585109692162297L));
      f.add(new ClassKeyDerivation(-3999975620890203311L));
      f.add(new ClassKeyDerivation(9004565624359228192L));
      f.add(new ClassKeyDerivation(3174739344151268185L));
      f.add(new ClassKeyDerivation(-6899870605552358208L));
      f.add(new ClassKeyDerivation(-6293508776355857517L));
      f.add(new ClassKeyDerivation(-4312599299180652313L));
      f.add(new ClassKeyDerivation(-4694994487167313881L));
      f.add(new ClassKeyDerivation(-5157340596445799086L));
      f.add(new ClassKeyDerivation(1357232108834995973L));
      f.add(new ClassKeyDerivation(1599331217447887961L));
      f.add(new ClassKeyDerivation(-1141520827429728518L));
      f.add(new ClassKeyDerivation(1926713854041247604L));
      f.add(new ClassKeyDerivation(7410265451670322854L));
      f.add(new ClassKeyDerivation(2901467387255054429L));
      f.add(new ClassKeyDerivation(162370457967069411L));
      f.add(new ClassKeyDerivation(2760842086046297634L));
      f.add(new ClassKeyDerivation(3776049455081265068L));
      f.add(new ClassKeyDerivation(-5604478967784015622L));
      f.add(new ClassKeyDerivation(2739144971296081526L));
      f.add(new ClassKeyDerivation(-6861727141915298789L));
      f.add(new ClassKeyDerivation(-4135668106098492858L));
      f.add(new ClassKeyDerivation(5236309331462981794L));
      f.add(new ClassKeyDerivation(1651434071038378543L));
      f.add(new ClassKeyDerivation(-9069826382710117107L));
      f.add(new ClassKeyDerivation(6493560625433782843L));
      f.add(new ClassKeyDerivation(-5149411696378039888L));
      f.add(new ClassKeyDerivation(-3824826662971461096L));
      f.add(new ClassKeyDerivation(153251333068783157L));
      f.add(new ClassKeyDerivation(-9202600567251183541L));
      f.add(new ClassKeyDerivation(6100807161551091969L));
      f.add(new ClassKeyDerivation(7668451174872204862L));
      f.add(new ClassKeyDerivation(5662974427452066649L));
      f.add(new ClassKeyDerivation(-3586911834119018277L));
      f.add(new ClassKeyDerivation(-7293351854458353991L));
      f.add(new ClassKeyDerivation(5093866584368073598L));
      f.add(new ClassKeyDerivation(-2858813123491869781L));
      f.add(new ClassKeyDerivation(7350253848183476665L));
      f.add(new ClassKeyDerivation(639285555919960527L));
      f.add(new ClassKeyDerivation(-4863353717869643388L));
      f.add(new ClassKeyDerivation(1445172357669731175L));
      f.add(new ClassKeyDerivation(8429327853508153738L));
      f.add(new ClassKeyDerivation(-3252424694163421759L));
      f.add(new ClassKeyDerivation(4227478154003476912L));
      f.add(new ClassKeyDerivation(6378995118016528219L));
      f.add(new ClassKeyDerivation(3797342663087931617L));
      f.add(new ClassKeyDerivation(166764240487985434L));
      f.add(new ClassKeyDerivation(8648568751970496649L));
      f.add(new ClassKeyDerivation(-550604947299877108L));
      f.add(new ClassKeyDerivation(2504783162415005379L));
      f.add(new ClassKeyDerivation(-3563913795671475431L));
      f.add(new ClassKeyDerivation(6234657034498350287L));
      f.add(new ClassKeyDerivation(1021923502747335398L));
      f.add(new ClassKeyDerivation(-103421754621957875L));
      f.add(new ClassKeyDerivation(-7595515985266425678L));
      f.add(new ClassKeyDerivation(-2295093635771862128L));
      f.add(new ClassKeyDerivation(5396847218042484640L));
      f.add(new ClassKeyDerivation(8126470971631672716L));
      f.add(new ClassKeyDerivation(-3727535526788833617L));
      f.add(new ClassKeyDerivation(-1810402339463508432L));
      f.add(new ClassKeyDerivation(-8197537349264713211L));
      f.add(new ClassKeyDerivation(-1671413116456282687L));
      f.add(new ClassKeyDerivation(3035794533700327762L));
      f.add(new ClassKeyDerivation(-3270331447146295977L));
      f.add(new ClassKeyDerivation(8833498848735185567L));
      f.add(new ClassKeyDerivation(-7204892311632759681L));
      f.add(new ClassKeyDerivation(-7016348583647362496L));
      f.add(new ClassKeyDerivation(-225120561523006616L));
      f.add(new ClassKeyDerivation(2316206545083114138L));
      f.add(new ClassKeyDerivation(-4005589133866401138L));
      f.add(new ClassKeyDerivation(-8787498121705430553L));
      f.add(new ClassKeyDerivation(7351443998767331426L));
      f.add(new ClassKeyDerivation(-2295532523226855089L));
      f.add(new ClassKeyDerivation(2908420611880588271L));
      f.add(new ClassKeyDerivation(2495987986985333219L));
      f.add(new ClassKeyDerivation(-8343353187176155060L));
      f.add(new ClassKeyDerivation(155527697009943160L));
      f.add(new ClassKeyDerivation(-5700641925569361811L));
      f.add(new ClassKeyDerivation(8680052832414890805L));
      f.add(new ClassKeyDerivation(4529412857441146563L));
      f.add(new ClassKeyDerivation(6668470237607176825L));
      f.add(new ClassKeyDerivation(-8726741352575025885L));
      f.add(new ClassKeyDerivation(4683320652162535902L));
      f.add(new ClassKeyDerivation(5949353276327119578L));
      f.add(new ClassKeyDerivation(-6144003039829312875L));
      f.add(new ClassKeyDerivation(-3565035526692427077L));
      f.add(new ClassKeyDerivation(7971440786811124934L));
      f.add(new ClassKeyDerivation(4046535008285053812L));
      f.add(new ClassKeyDerivation(-2155254721507337667L));
      f.add(new ClassKeyDerivation(-2243720243798609779L));
      e = (int[])e.clone();
      f.add(new ClassKeyDerivation(-3696812795697087679L));
      f.add(new ClassKeyDerivation(6640814787847230614L));
      f.add(new ClassKeyDerivation(4081879854215332394L));
      f.add(new ClassKeyDerivation(-4262285058445659222L));
      f.add(new ClassKeyDerivation(7224529192815399823L));
      f.add(new ClassKeyDerivation(-2118955904038975764L));
      f.add(new ClassKeyDerivation(6095276495929459395L));
      f.add(new ClassKeyDerivation(-2131155074202778844L));
      f.add(new ClassKeyDerivation(-1862101628948963147L));
      f.add(new ClassKeyDerivation(-2816393211816386137L));
      f.add(new ClassKeyDerivation(-3271888118166848757L));
      f.add(new ClassKeyDerivation(-8460992561550472262L));
      f.add(new ClassKeyDerivation(-8642544287696414374L));
      f.add(new ClassKeyDerivation(-4749632467057627218L));
      f.add(new ClassKeyDerivation(-3861818647560526012L));
      f.add(new ClassKeyDerivation(8672463906570667554L));
      f.add(new ClassKeyDerivation(1585283281762388825L));
      f.add(new ClassKeyDerivation(-3712902951740005784L));
      f.add(new ClassKeyDerivation(7969367461818238531L));
      f.add(new ClassKeyDerivation(-8588453728280428878L));
      f.add(new ClassKeyDerivation(-4428458097210482294L));
      f.add(new ClassKeyDerivation(-3856789063278659180L));
      f.add(new ClassKeyDerivation(1066959388567756484L));
      f.add(new ClassKeyDerivation(2633356758590227855L));
      f.add(new ClassKeyDerivation(2509405610765614347L));
      f.add(new ClassKeyDerivation(5115110350358280374L));
      f.add(new ClassKeyDerivation(5112146801339375729L));
      f.add(new ClassKeyDerivation(-8066941883979202435L));
      f.add(new ClassKeyDerivation(-6040905418608370723L));
      f.add(new ClassKeyDerivation(-4884154429065049931L));
      f.add(new ClassKeyDerivation(-4731137150302481337L));
      f.add(new ClassKeyDerivation(-3999413626020520502L));
      f.add(new ClassKeyDerivation(52739802235746297L));
      f.add(new ClassKeyDerivation(-7440083872668791816L));
      f.add(new ClassKeyDerivation(-1869657960385168229L));
      f.add(new ClassKeyDerivation(4622170852205480697L));
      f.add(new ClassKeyDerivation(-5571966889087309759L));
      f.add(new ClassKeyDerivation(-3675247754864426103L));
      f.add(new ClassKeyDerivation(-8167282284121972560L));
      f.add(new ClassKeyDerivation(-6445923795109871148L));
      f.add(new ClassKeyDerivation(4932684217895586364L));
      f.add(new ClassKeyDerivation(-2010709342477268285L));
      f.add(new ClassKeyDerivation(3382418369601343627L));
      f.add(new ClassKeyDerivation(8679022332497230187L));
      f.add(new ClassKeyDerivation(-8056863321134922702L));
      f.add(new ClassKeyDerivation(4653052417405725279L));
      f.add(new ClassKeyDerivation(-3015066305122339805L));
      f.add(new ClassKeyDerivation(-5734654293161550610L));
      f.add(new ClassKeyDerivation(1012194653684845332L));
      f.add(new ClassKeyDerivation(6053433022317335642L));
      f.add(new ClassKeyDerivation(-5014136479255693339L));
      f.add(new ClassKeyDerivation(-1849798082386019855L));
      f.add(new ClassKeyDerivation(-2253704392068471018L));
      f.add(new ClassKeyDerivation(3332694221191101541L));
      f.add(new ClassKeyDerivation(2764428307567687767L));
      f.add(new ClassKeyDerivation(-2448523998950995987L));
      f.add(new ClassKeyDerivation(5652214955789278162L));
      f.add(new ClassKeyDerivation(2785128516998989185L));
      f.add(new ClassKeyDerivation(7399813570345764619L));
      f.add(new ClassKeyDerivation(205771453204368485L));
      f.add(new ClassKeyDerivation(-5299222495744060603L));
      f.add(new ClassKeyDerivation(-5898157520955427237L));
      f.add(new ClassKeyDerivation(1476251447297302504L));
      f.add(new ClassKeyDerivation(-5296583989074760621L));
      f.add(new ClassKeyDerivation(2338337103261165478L));
      f.add(new ClassKeyDerivation(8083928839629566169L));
      f.add(new ClassKeyDerivation(-4029111650980000159L));
      f.add(new ClassKeyDerivation(-4897200289753193197L));
      f.add(new ClassKeyDerivation(7522352201818934241L));
      f.add(new ClassKeyDerivation(628606886462207503L));
      f.add(new ClassKeyDerivation(4616589885614992552L));
      f.add(new ClassKeyDerivation(-6186078466485420106L));
      f.add(new ClassKeyDerivation(-8022748106232527797L));
      f.add(new ClassKeyDerivation(-8044237623646687889L));
      f.add(new ClassKeyDerivation(-677942651680055467L));
      f.add(new ClassKeyDerivation(4615141981617163370L));
      f.add(new ClassKeyDerivation(-239970370987214747L));
      f.add(new ClassKeyDerivation(5388721050986065789L));
      f.add(new ClassKeyDerivation(7363195626528932630L));
      f.add(new ClassKeyDerivation(3353669404114915177L));
      f.add(new ClassKeyDerivation(-4884529931098864424L));
      f.add(new ClassKeyDerivation(-7292708564337343779L));
      f.add(new ClassKeyDerivation(2076227126784347326L));
      f.add(new ClassKeyDerivation(-5120157707689610847L));
      f.add(new ClassKeyDerivation(-3133076587153092177L));
      f.add(new ClassKeyDerivation(6350883406676053872L));
      f.add(new ClassKeyDerivation(-8627206750814844701L));
      f.add(new ClassKeyDerivation(7394073252875036948L));
      f.add(new ClassKeyDerivation(8079936896404006823L));
      f.add(new ClassKeyDerivation(2595340625516003962L));
      f.add(new ClassKeyDerivation(2901761473016271855L));
      f.add(new ClassKeyDerivation(-3886468850468143245L));
      f.add(new ClassKeyDerivation(7651449061868510678L));
      f.add(new ClassKeyDerivation(4803349973016805165L));
      f.add(new ClassKeyDerivation(-1381375105657145975L));
      f.add(new ClassKeyDerivation(1609639185088360170L));
      f.add(new ClassKeyDerivation(1883588941211780906L));
      f.add(new ClassKeyDerivation(2030430038200927911L));
      f.add(new ClassKeyDerivation(1887458055851073370L));
      f.add(new ClassKeyDerivation(-8752782080572408719L));
      f.add(new ClassKeyDerivation(6222401026003032110L));
      f.add(new ClassKeyDerivation(3751079454564650388L));
      f.add(new ClassKeyDerivation(2507065290369271485L));
      f.add(new ClassKeyDerivation(-5466726431880527190L));
      f.add(new ClassKeyDerivation(-4558681767012666969L));
      f.add(new ClassKeyDerivation(-2268334454799184991L));
      f.add(new ClassKeyDerivation(6072860470131566854L));
      f.add(new ClassKeyDerivation(-6569865003192764732L));
      f.add(new ClassKeyDerivation(-8161223106012246956L));
      f.add(new ClassKeyDerivation(7971558293144716718L));
      f.add(new ClassKeyDerivation(4655716245954704474L));
      f.add(new ClassKeyDerivation(-3284399042637757749L));
      f.add(new ClassKeyDerivation(-8863602943385844412L));
      f.add(new ClassKeyDerivation(-3108069568590486443L));
      f.add(new ClassKeyDerivation(-5722143586395380495L));
      f.add(new ClassKeyDerivation(6537543349962680373L));
      f.add(new ClassKeyDerivation(-5556662110220806002L));
      f.add(new ClassKeyDerivation(4086304319846368855L));
      f.add(new ClassKeyDerivation(-6129273271871739406L));
      f.add(new ClassKeyDerivation(-3574801133535185805L));
      f.add(new ClassKeyDerivation(7494191774314375081L));
      f.add(new ClassKeyDerivation(3795510723290511422L));
      f.add(new ClassKeyDerivation(-9031244321921120051L));
      f.add(new ClassKeyDerivation(497773753664780749L));
      f.add(new ClassKeyDerivation(-661063607312456700L));
      f.add(new ClassKeyDerivation(-2955155969482492165L));
      f.add(new ClassKeyDerivation(-1407764361835681045L));
      f.add(new ClassKeyDerivation(5253879826945052614L));
      f.add(new ClassKeyDerivation(8960760836267392316L));
      f.add(new ClassKeyDerivation(-5993746481630639622L));
      f.add(new ClassKeyDerivation(-250243006285305331L));
      f.add(new ClassKeyDerivation(16963648600156102L));
      f.add(new ClassKeyDerivation(5501098565353577489L));
      f.add(new ClassKeyDerivation(6114771205906345479L));
      f.add(new ClassKeyDerivation(-7858095761615990865L));
      f.add(new ClassKeyDerivation(-4108919712344494009L));
      f.add(new ClassKeyDerivation(-9013759843663761616L));
      f.add(new ClassKeyDerivation(-5285580033610800603L));
      f.add(new ClassKeyDerivation(1927056134836345809L));
      f.add(new ClassKeyDerivation(-2578046776862483779L));
      f.add(new ClassKeyDerivation(1325876886032574936L));
      f.add(new ClassKeyDerivation(-5437992575552994200L));
      f.add(new ClassKeyDerivation(-8190227413731659417L));
      f.add(new ClassKeyDerivation(-6183815127805219024L));
      f.add(new ClassKeyDerivation(-5300140124123654435L));
      f.add(new ClassKeyDerivation(63840703828281881L));
      f.add(new ClassKeyDerivation(1300127864496052028L));
      f.add(new ClassKeyDerivation(731406585916204376L));
      f.add(new ClassKeyDerivation(7115605558192859933L));
      f.add(new ClassKeyDerivation(3810247824212200652L));
      f.add(new ClassKeyDerivation(-4288863990804872653L));
      f.add(new ClassKeyDerivation(7952482369411959186L));
      f.add(new ClassKeyDerivation(9087792800856527757L));
      f.add(new ClassKeyDerivation(-9016454449006780058L));
      f.add(new ClassKeyDerivation(8975585203321548969L));
      f.add(new ClassKeyDerivation(-5432507573110852856L));
      f.add(new ClassKeyDerivation(7563515326952003240L));
      f.add(new ClassKeyDerivation(7350030084531756198L));
      f.add(new ClassKeyDerivation(-1380262335299520730L));
      f.add(new ClassKeyDerivation(4483106681556071851L));
      f.add(new ClassKeyDerivation(-4450942300909368237L));
      f.add(new ClassKeyDerivation(-7881458043851063601L));
      f.add(new ClassKeyDerivation(615075000533610859L));
      f.add(new ClassKeyDerivation(5648349200579409636L));
      f.add(new ClassKeyDerivation(4037068998641408647L));
      f.add(new ClassKeyDerivation(8830972327414827561L));
      f.add(new ClassKeyDerivation(-1706354575838368275L));
      f.add(new ClassKeyDerivation(-8169002480457046348L));
      f.add(new ClassKeyDerivation(-1517269018698245166L));
      f.add(new ClassKeyDerivation(-1865096798285750940L));
      f.add(new ClassKeyDerivation(-7454954458710833637L));
      f.add(new ClassKeyDerivation(-7043708288700131612L));
      f.add(new ClassKeyDerivation(-1958268371700970259L));
      f.add(new ClassKeyDerivation(-4408044288793552693L));
      f.add(new ClassKeyDerivation(-573238785113296129L));
      f.add(new ClassKeyDerivation(8963002463083085151L));
      f.add(new ClassKeyDerivation(8111364308978696521L));
      f.add(new ClassKeyDerivation(-1518112777312280595L));
      f.add(new ClassKeyDerivation(-564689544170773620L));
      f.add(new ClassKeyDerivation(1639730844618465434L));
      f.add(new ClassKeyDerivation(7849580307596285041L));
      f.add(new ClassKeyDerivation(3239808989399423781L));
      f.add(new ClassKeyDerivation(5970210467420768076L));
      f.add(new ClassKeyDerivation(-3155673563142650512L));
      f.add(new ClassKeyDerivation(8258016355108283405L));
      f.add(new ClassKeyDerivation(1348208680236893819L));
      f.add(new ClassKeyDerivation(320796840706253021L));
      f.add(new ClassKeyDerivation(2165780790736638170L));
      e = (int[])e.clone();
      f.add(new ClassKeyDerivation(-5170834144083751133L));
      f.add(new ClassKeyDerivation(4682502943397917648L));
      f.add(new ClassKeyDerivation(6382455574237700217L));
      f.add(new ClassKeyDerivation(5529206336174012676L));
      f.add(new ClassKeyDerivation(-8889873671500216352L));
      f.add(new ClassKeyDerivation(-7288545252948895240L));
      f.add(new ClassKeyDerivation(8127219865090357083L));
      f.add(new ClassKeyDerivation(3336923233243199168L));
      f.add(new ClassKeyDerivation(780199356726756473L));
      f.add(new ClassKeyDerivation(3364238982792466926L));
      f.add(new ClassKeyDerivation(-512294862330375486L));
      f.add(new ClassKeyDerivation(-697767694465393646L));
      f.add(new ClassKeyDerivation(-7880854175775162676L));
      f.add(new ClassKeyDerivation(-9166563185818725743L));
      f.add(new ClassKeyDerivation(-9170047559551664128L));
      f.add(new ClassKeyDerivation(-8624991645790212342L));
      f.add(new ClassKeyDerivation(-7871472836195564780L));
      f.add(new ClassKeyDerivation(1603772659219720790L));
      f.add(new ClassKeyDerivation(2230078168618194238L));
      f.add(new ClassKeyDerivation(4083263611865109979L));
      f.add(new ClassKeyDerivation(7994963917791650359L));
      f.add(new ClassKeyDerivation(5534069088131930722L));
      f.add(new ClassKeyDerivation(-3015526572475837947L));
      f.add(new ClassKeyDerivation(-4591288690928093235L));
      f.add(new ClassKeyDerivation(-3432092998771325133L));
      f.add(new ClassKeyDerivation(-2864980017416551509L));
      f.add(new ClassKeyDerivation(1362357058152930518L));
      f.add(new ClassKeyDerivation(7065306244617606118L));
      f.add(new ClassKeyDerivation(4765302202655830797L));
      f.add(new ClassKeyDerivation(-5475030956971255507L));
      f.add(new ClassKeyDerivation(8953195322098841314L));
      f.add(new ClassKeyDerivation(8420693268979132860L));
      f.add(new ClassKeyDerivation(1592259569986684351L));
      f.add(new ClassKeyDerivation(-6302743155161736909L));
      f.add(new ClassKeyDerivation(4766652625889855345L));
      f.add(new ClassKeyDerivation(-5839797816726290740L));
      f.add(new ClassKeyDerivation(-1116566426902534179L));
      f.add(new ClassKeyDerivation(7122655940803554489L));
      f.add(new ClassKeyDerivation(3621889342364943622L));
      f.add(new ClassKeyDerivation(-5420811935954805939L));
      f.add(new ClassKeyDerivation(-8299344003533499247L));
      f.add(new ClassKeyDerivation(-3867460832281287366L));
      f.add(new ClassKeyDerivation(438008035139500907L));
      f.add(new ClassKeyDerivation(1938179809987505662L));
      f.add(new ClassKeyDerivation(5258697785073258276L));
      f.add(new ClassKeyDerivation(-4751233162190788561L));
      f.add(new ClassKeyDerivation(7805329157142504435L));
      f.add(new ClassKeyDerivation(7682860500740207478L));
      f.add(new ClassKeyDerivation(-5405988514585366496L));
      f.add(new ClassKeyDerivation(-5175533721338331948L));
      f.add(new ClassKeyDerivation(-2012573104274855482L));
      f.add(new ClassKeyDerivation(2192283649085633727L));
      f.add(new ClassKeyDerivation(-4153182805666781192L));
      f.add(new ClassKeyDerivation(1343936939067487393L));
      f.add(new ClassKeyDerivation(6940983923087913353L));
      f.add(new ClassKeyDerivation(-5549513074626869489L));
      f.add(new ClassKeyDerivation(-7743192182813700215L));
      f.add(new ClassKeyDerivation(-5447838029642166639L));
      f.add(new ClassKeyDerivation(-4746296885200989253L));
      f.add(new ClassKeyDerivation(1778102873467060561L));
      f.add(new ClassKeyDerivation(-2686459831319446891L));
      f.add(new ClassKeyDerivation(7930226207935367258L));
      f.add(new ClassKeyDerivation(1156516969942460838L));
      f.add(new ClassKeyDerivation(8108409826560213513L));
      f.add(new ClassKeyDerivation(7419472541818343737L));
      f.add(new ClassKeyDerivation(3091455446087535533L));
      f.add(new ClassKeyDerivation(-3562072177384328426L));
      f.add(new ClassKeyDerivation(2464267090133547788L));
      f.add(new ClassKeyDerivation(-553299649618640581L));
      f.add(new ClassKeyDerivation(-4846269471739763295L));
      f.add(new ClassKeyDerivation(6498327703874352349L));
      f.add(new ClassKeyDerivation(3053057379646495129L));
      f.add(new ClassKeyDerivation(-2115211749005897293L));
      f.add(new ClassKeyDerivation(4819932117859436074L));
      f.add(new ClassKeyDerivation(-9193460415740268698L));
      f.add(new ClassKeyDerivation(-5906557138883349584L));
      f.add(new ClassKeyDerivation(8673423549203594727L));
      f.add(new ClassKeyDerivation(-6180679485326905745L));
      f.add(new ClassKeyDerivation(7654583022428275926L));
      f.add(new ClassKeyDerivation(-1514558778238796996L));
      f.add(new ClassKeyDerivation(-2394983736088906950L));
      f.add(new ClassKeyDerivation(-9157578447108876772L));
      f.add(new ClassKeyDerivation(2621224817893367049L));
      f.add(new ClassKeyDerivation(7353589053210267455L));
      f.add(new ClassKeyDerivation(321826920165962233L));
      f.add(new ClassKeyDerivation(-4277687729144207771L));
      f.add(new ClassKeyDerivation(-6724680069524574634L));
      f.add(new ClassKeyDerivation(-2586564109388990348L));
      f.add(new ClassKeyDerivation(6794953548894943202L));
      f.add(new ClassKeyDerivation(5056660297752898148L));
      e = (int[])e.clone();
      f.add(new ClassKeyDerivation(-7586592616962650938L));
      f.add(new ClassKeyDerivation(6987601832925537003L));
      f.add(new ClassKeyDerivation(8987453929331103816L));
      f.add(new ClassKeyDerivation(-8297639498965542072L));
      f.add(new ClassKeyDerivation(-8357262610697878583L));
      f.add(new ClassKeyDerivation(-3403002799988795469L));
      f.add(new ClassKeyDerivation(3481604710871477720L));
      f.add(new ClassKeyDerivation(8650588526040603644L));
      f.add(new ClassKeyDerivation(8513297910810520188L));
      f.add(new ClassKeyDerivation(-797764312223250354L));
      f.add(new ClassKeyDerivation(5223705850762569870L));
      f.add(new ClassKeyDerivation(6804920462090634880L));
      f.add(new ClassKeyDerivation(-5758242505912079440L));
      f.add(new ClassKeyDerivation(8703407679998076697L));
      f.add(new ClassKeyDerivation(-3419469360953003590L));
      f.add(new ClassKeyDerivation(-5413095673104962314L));
      f.add(new ClassKeyDerivation(2028283330885696122L));
      f.add(new ClassKeyDerivation(2339505301218371386L));
      f.add(new ClassKeyDerivation(-4269503761380242895L));
      f.add(new ClassKeyDerivation(5477818568951901885L));
      f.add(new ClassKeyDerivation(1448385852806177528L));
      f.add(new ClassKeyDerivation(-3684725189079470824L));
      f.add(new ClassKeyDerivation(4373632119973917198L));
      f.add(new ClassKeyDerivation(-8929587404750526397L));
      f.add(new ClassKeyDerivation(-6894333683835368592L));
      f.add(new ClassKeyDerivation(1899710955041528130L));
      f.add(new ClassKeyDerivation(-2426768736059555288L));
      f.add(new ClassKeyDerivation(768553937044401258L));
      f.add(new ClassKeyDerivation(-3447019606278752592L));
      f.add(new ClassKeyDerivation(-6914012941432533686L));
      f.add(new ClassKeyDerivation(3802556983875697223L));
      f.add(new ClassKeyDerivation(7551463745490928920L));
      f.add(new ClassKeyDerivation(-8352651669560893807L));
      f.add(new ClassKeyDerivation(-6419009661300650608L));
      f.add(new ClassKeyDerivation(-1853300601347309070L));
      f.add(new ClassKeyDerivation(-1246291208424296475L));
      f.add(new ClassKeyDerivation(7404617668954398545L));
      f.add(new ClassKeyDerivation(-1954632621665935641L));
      f.add(new ClassKeyDerivation(-3110115393182177108L));
      f.add(new ClassKeyDerivation(-1418878535355727663L));
      f.add(new ClassKeyDerivation(-5172014248920685525L));
      f.add(new ClassKeyDerivation(-708943657416579482L));
      f.add(new ClassKeyDerivation(-7290243174035282882L));
      f.add(new ClassKeyDerivation(-3545089193333792936L));
      f.add(new ClassKeyDerivation(3668757616149937533L));
      f.add(new ClassKeyDerivation(-4397748261937048611L));
      f.add(new ClassKeyDerivation(8113075472344584891L));
      f.add(new ClassKeyDerivation(-2535967116968983007L));
      f.add(new ClassKeyDerivation(-368169973390666181L));
      f.add(new ClassKeyDerivation(-5007095562042999900L));
      f.add(new ClassKeyDerivation(769119850707504440L));
      f.add(new ClassKeyDerivation(7272475405253612787L));
      f.add(new ClassKeyDerivation(4908362592923149994L));
      f.add(new ClassKeyDerivation(3755481701909320765L));
      f.add(new ClassKeyDerivation(4468119647505267940L));
      f.add(new ClassKeyDerivation(-2271595673765616127L));
      f.add(new ClassKeyDerivation(-2838280445734007365L));
      f.add(new ClassKeyDerivation(-8012427057514550143L));
      f.add(new ClassKeyDerivation(-2431605154280078226L));
      f.add(new ClassKeyDerivation(-5748097343320074580L));
      f.add(new ClassKeyDerivation(1187205827132811401L));
      f.add(new ClassKeyDerivation(9143524309722036617L));
      f.add(new ClassKeyDerivation(-4557518989963268983L));
      f.add(new ClassKeyDerivation(3923794445871942970L));
      f.add(new ClassKeyDerivation(2343183357672560922L));
      f.add(new ClassKeyDerivation(-2303200017010526420L));
      f.add(new ClassKeyDerivation(2479975461692024939L));
      f.add(new ClassKeyDerivation(8693297788276012476L));
      f.add(new ClassKeyDerivation(5977166826189389943L));
      f.add(new ClassKeyDerivation(784016527922166185L));
      f.add(new ClassKeyDerivation(-1260210442418973196L));
      f.add(new ClassKeyDerivation(-7025236549996360941L));
      f.add(new ClassKeyDerivation(2329906885996766374L));
      f.add(new ClassKeyDerivation(-2874213921241458424L));
      f.add(new ClassKeyDerivation(5918293163475962527L));
      f.add(new ClassKeyDerivation(763424195443011997L));
      f.add(new ClassKeyDerivation(3193818711158449611L));
      f.add(new ClassKeyDerivation(6209086048050890752L));
      f.add(new ClassKeyDerivation(8361599352442428850L));
      f.add(new ClassKeyDerivation(8807956691591440043L));
      f.add(new ClassKeyDerivation(7816045744020196632L));
      f.add(new ClassKeyDerivation(-6864063053087379210L));
      f.add(new ClassKeyDerivation(770366729469601214L));
      f.add(new ClassKeyDerivation(-6446859083811511846L));
      f.add(new ClassKeyDerivation(1192789056737933245L));
      f.add(new ClassKeyDerivation(-4597958281425389839L));
      f.add(new ClassKeyDerivation(9117415255562213928L));
      f.add(new ClassKeyDerivation(-411986337891021449L));
      f.add(new ClassKeyDerivation(-7342695252453480419L));
      f.add(new ClassKeyDerivation(6833629561488532849L));
      f.add(new ClassKeyDerivation(8425107441411917081L));
      f.add(new ClassKeyDerivation(4938178969035018226L));
      f.add(new ClassKeyDerivation(645087372806648667L));
      f.add(new ClassKeyDerivation(-8733838472596604481L));
      f.add(new ClassKeyDerivation(-7490649481360660691L));
      f.add(new ClassKeyDerivation(-8929049454460304388L));
      f.add(new ClassKeyDerivation(-9074357184755269874L));
      f.add(new ClassKeyDerivation(8255068550354259307L));
      f.add(new ClassKeyDerivation(1308748760574733850L));
      f.add(new ClassKeyDerivation(4206315059675650034L));
      f.add(new ClassKeyDerivation(-2855252470442231186L));
      f.add(new ClassKeyDerivation(8398744329477369607L));
      f.add(new ClassKeyDerivation(2478615449083799621L));
      f.add(new ClassKeyDerivation(2497866355489270915L));
      f.add(new ClassKeyDerivation(-2237929944748969829L));
      f.add(new ClassKeyDerivation(8229701053269544777L));
      f.add(new ClassKeyDerivation(-9168121525344111635L));
      f.add(new ClassKeyDerivation(7706568200737493448L));
      f.add(new ClassKeyDerivation(-4688937416124450913L));
      f.add(new ClassKeyDerivation(-2153096724840143497L));
      f.add(new ClassKeyDerivation(7995927653106289459L));
      f.add(new ClassKeyDerivation(-4975131401756250675L));
      f.add(new ClassKeyDerivation(3786368762443321441L));
      f.add(new ClassKeyDerivation(6805141792328486569L));
      f.add(new ClassKeyDerivation(-1962971259968722764L));
      e = (int[])e.clone();
      f.add(new ClassKeyDerivation(2939703233444555886L));
      f.add(new ClassKeyDerivation(8085252369297540371L));
      f.add(new ClassKeyDerivation(-1554830101347651840L));
      f.add(new ClassKeyDerivation(-1562986187307386329L));
      f.add(new ClassKeyDerivation(-545788735759545316L));
      f.add(new ClassKeyDerivation(1903462001590599493L));
      f.add(new ClassKeyDerivation(8233024628235039976L));
      f.add(new ClassKeyDerivation(-7315495698236694096L));
      f.add(new ClassKeyDerivation(-1993428553570231782L));
      f.add(new ClassKeyDerivation(-5465493151349390706L));
      f.add(new ClassKeyDerivation(4649254194219607527L));
      f.add(new ClassKeyDerivation(4621799549057408817L));
      f.add(new ClassKeyDerivation(8966979151640637616L));
      f.add(new ClassKeyDerivation(8659397660073471326L));
      f.add(new ClassKeyDerivation(-368350431925275734L));
      f.add(new ClassKeyDerivation(-994611797266605525L));
      f.add(new ClassKeyDerivation(3512771830920040768L));
      f.add(new ClassKeyDerivation(296099905194204766L));
      f.add(new ClassKeyDerivation(1309476949522932066L));
      f.add(new ClassKeyDerivation(5365812412900399442L));
      f.add(new ClassKeyDerivation(3319375130988885319L));
      f.add(new ClassKeyDerivation(3228245851209652178L));
      f.add(new ClassKeyDerivation(-8601176219469753899L));
      f.add(new ClassKeyDerivation(-8016118237608162835L));
      f.add(new ClassKeyDerivation(-1231957107275526876L));
      f.add(new ClassKeyDerivation(-1973003538156472521L));
      f.add(new ClassKeyDerivation(-962317684292137092L));
      f.add(new ClassKeyDerivation(-8327284901241432118L));
      f.add(new ClassKeyDerivation(1317545188407622869L));
      f.add(new ClassKeyDerivation(2179408854195897340L));
      f.add(new ClassKeyDerivation(-3570946525011960276L));
      f.add(new ClassKeyDerivation(5379947679396860650L));
      f.add(new ClassKeyDerivation(1186453442111105414L));
      f.add(new ClassKeyDerivation(-2524544963687587622L));
      f.add(new ClassKeyDerivation(-1145430219354377405L));
      f.add(new ClassKeyDerivation(-2668273469992294974L));
      f.add(new ClassKeyDerivation(1052938782936232336L));
      f.add(new ClassKeyDerivation(-6277088502702005334L));
      f.add(new ClassKeyDerivation(7664235440928693387L));
      f.add(new ClassKeyDerivation(-7051393382332246461L));
      f.add(new ClassKeyDerivation(7362089893334540972L));
      f.add(new ClassKeyDerivation(-6594638741115101279L));
      f.add(new ClassKeyDerivation(8232326198507404572L));
      f.add(new ClassKeyDerivation(5350428649543565487L));
      f.add(new ClassKeyDerivation(-7631204726479447342L));
      f.add(new ClassKeyDerivation(-4158179590409031991L));
      f.add(new ClassKeyDerivation(3664606990101390196L));
      f.add(new ClassKeyDerivation(4783769517965838363L));
      f.add(new ClassKeyDerivation(3638508452026978606L));
      f.add(new ClassKeyDerivation(-4129984913462253767L));
      f.add(new ClassKeyDerivation(-8918602924839421323L));
      f.add(new ClassKeyDerivation(3085855464440504222L));
      f.add(new ClassKeyDerivation(-7605576327729408507L));
      f.add(new ClassKeyDerivation(1510113970337414145L));
      f.add(new ClassKeyDerivation(-7178581786355403919L));
      f.add(new ClassKeyDerivation(-8614167376058194763L));
      f.add(new ClassKeyDerivation(7666069239496153036L));
      f.add(new ClassKeyDerivation(-1230529504590929252L));
      f.add(new ClassKeyDerivation(-8008476070995753662L));
      f.add(new ClassKeyDerivation(-7148846827803636184L));
      f.add(new ClassKeyDerivation(6951432365694873718L));
      f.add(new ClassKeyDerivation(8807455834971785278L));
      f.add(new ClassKeyDerivation(-4412369626331964789L));
      f.add(new ClassKeyDerivation(-9187853118737895633L));
      f.add(new ClassKeyDerivation(-6152874172197248027L));
      f.add(new ClassKeyDerivation(-4548319162238233379L));
      f.add(new ClassKeyDerivation(-7770519981872351824L));
      f.add(new ClassKeyDerivation(-3280358415719520482L));
      f.add(new ClassKeyDerivation(7076258689768970544L));
      f.add(new ClassKeyDerivation(8099688376606379704L));
      f.add(new ClassKeyDerivation(1941447969683411857L));
      f.add(new ClassKeyDerivation(-6469855100133236160L));
      f.add(new ClassKeyDerivation(7353314012602177520L));
      f.add(new ClassKeyDerivation(-1680714534518023208L));
      f.add(new ClassKeyDerivation(1316606181255004068L));
      f.add(new ClassKeyDerivation(-406691922005360579L));
      f.add(new ClassKeyDerivation(5511281352484208725L));
      f.add(new ClassKeyDerivation(-2730094114244170051L));
      f.add(new ClassKeyDerivation(1347435280917089695L));
      f.add(new ClassKeyDerivation(7560887488121536094L));
      f.add(new ClassKeyDerivation(7691333981902989611L));
      f.add(new ClassKeyDerivation(-990489206481444757L));
      f.add(new ClassKeyDerivation(-7866735801422533364L));
      f.add(new ClassKeyDerivation(7681011353915497406L));
      f.add(new ClassKeyDerivation(-8926431779895504924L));
      e = (int[])e.clone();
      f.add(new ClassKeyDerivation(-8190476530974202018L));
      f.add(new ClassKeyDerivation(5662122134345942923L));
      f.add(new ClassKeyDerivation(4061885006129060026L));
      f.add(new ClassKeyDerivation(-3532413088162330449L));
      f.add(new ClassKeyDerivation(6235098153724885873L));
      f.add(new ClassKeyDerivation(-2160789580873571686L));
      f.add(new ClassKeyDerivation(8830580434333712565L));
      f.add(new ClassKeyDerivation(-1824923847878705302L));
      f.add(new ClassKeyDerivation(-4149735315900203931L));
      f.add(new ClassKeyDerivation(-864305085857887099L));
      f.add(new ClassKeyDerivation(1308663815611835647L));
      f.add(new ClassKeyDerivation(3040389596058344218L));
      f.add(new ClassKeyDerivation(4653971862407858027L));
      f.add(new ClassKeyDerivation(-1660304305908058053L));
      f.add(new ClassKeyDerivation(-1387956159001952651L));
      f.add(new ClassKeyDerivation(-5739094555924022476L));
      f.add(new ClassKeyDerivation(-2443812753434851322L));
      f.add(new ClassKeyDerivation(-2967950201132441032L));
      f.add(new ClassKeyDerivation(-8049477099482945447L));
      f.add(new ClassKeyDerivation(1509954603906825222L));
      f.add(new ClassKeyDerivation(1034649019450986094L));
      f.add(new ClassKeyDerivation(-6419137401992071045L));
      f.add(new ClassKeyDerivation(-8016001064544988974L));
      f.add(new ClassKeyDerivation(-1237483107194313657L));
      f.add(new ClassKeyDerivation(4475789985404749460L));
      f.add(new ClassKeyDerivation(-3979597660104115401L));
      f.add(new ClassKeyDerivation(-8349717781091925404L));
      f.add(new ClassKeyDerivation(732856050545608598L));
      f.add(new ClassKeyDerivation(3188369936898880738L));
      f.add(new ClassKeyDerivation(-8438934286158558628L));
      f.add(new ClassKeyDerivation(7244086385854083855L));
      f.add(new ClassKeyDerivation(3480719428944739909L));
      f.add(new ClassKeyDerivation(-5756921136584289491L));
      f.add(new ClassKeyDerivation(7787531896557432037L));
      f.add(new ClassKeyDerivation(-3827568998017967608L));
      f.add(new ClassKeyDerivation(-84643209889589707L));
      f.add(new ClassKeyDerivation(-7028544431569050195L));
      f.add(new ClassKeyDerivation(-1697072358394667930L));
      f.add(new ClassKeyDerivation(3051834480625276391L));
      f.add(new ClassKeyDerivation(-5427988562148668620L));
      f.add(new ClassKeyDerivation(-7730760258318406680L));
      f.add(new ClassKeyDerivation(2655916153939431217L));
      f.add(new ClassKeyDerivation(3363246356906702294L));
      f.add(new ClassKeyDerivation(-5727820298321411753L));
      f.add(new ClassKeyDerivation(-2252674149589791335L));
      f.add(new ClassKeyDerivation(-2563287750404853143L));
      f.add(new ClassKeyDerivation(9105660389341856617L));
      f.add(new ClassKeyDerivation(-254494709044000761L));
      f.add(new ClassKeyDerivation(8840526288662790200L));
      f.add(new ClassKeyDerivation(-1815888630569000778L));
      f.add(new ClassKeyDerivation(750278804894341803L));
      f.add(new ClassKeyDerivation(-4588822569426476224L));
      f.add(new ClassKeyDerivation(1877040256289478205L));
      f.add(new ClassKeyDerivation(-2404046053776031284L));
      f.add(new ClassKeyDerivation(3182522289428257802L));
      f.add(new ClassKeyDerivation(-4864520740798547737L));
      f.add(new ClassKeyDerivation(2027804116776102188L));
      f.add(new ClassKeyDerivation(-7619713437645790136L));
      f.add(new ClassKeyDerivation(-6916808309897515507L));
      f.add(new ClassKeyDerivation(6636949592086082715L));
      f.add(new ClassKeyDerivation(7090183568287534632L));
      f.add(new ClassKeyDerivation(6948534356438732025L));
      f.add(new ClassKeyDerivation(7230848319165013751L));
      f.add(new ClassKeyDerivation(3604342302641537111L));
      f.add(new ClassKeyDerivation(3664069165506809875L));
      f.add(new ClassKeyDerivation(-7613827264427342365L));
      f.add(new ClassKeyDerivation(3780316800133506271L));
      f.add(new ClassKeyDerivation(-8631242440358754267L));
      f.add(new ClassKeyDerivation(-1710307405744467247L));
      f.add(new ClassKeyDerivation(-3005008442331433855L));
      f.add(new ClassKeyDerivation(3200135605160166138L));
      f.add(new ClassKeyDerivation(-7308642656377243559L));
      f.add(new ClassKeyDerivation(-6441094049290047087L));
      f.add(new ClassKeyDerivation(6790667474654633410L));
      f.add(new ClassKeyDerivation(-5853456527672769204L));
      f.add(new ClassKeyDerivation(-6319458767735659147L));
      f.add(new ClassKeyDerivation(2320735182127374650L));
      f.add(new ClassKeyDerivation(-9189610922763808614L));
      f.add(new ClassKeyDerivation(3374390005227115061L));
      f.add(new ClassKeyDerivation(-9202984562137773839L));
      f.add(new ClassKeyDerivation(3065942403304106473L));
      f.add(new ClassKeyDerivation(7391527464488398046L));
      f.add(new ClassKeyDerivation(8507439219851146666L));
      f.add(new ClassKeyDerivation(-9033338685172458120L));
      f.add(new ClassKeyDerivation(2794875935874217564L));
      f.add(new ClassKeyDerivation(5350534327288829545L));
      f.add(new ClassKeyDerivation(7527237296481261293L));
      f.add(new ClassKeyDerivation(-3307592456848371367L));
      f.add(new ClassKeyDerivation(4815264002657493931L));
      f.add(new ClassKeyDerivation(-1268114340040826891L));
      f.add(new ClassKeyDerivation(742969751028627065L));
      f.add(new ClassKeyDerivation(4354548361879324849L));
      f.add(new ClassKeyDerivation(4354397700853786295L));
      e = (int[])e.clone();
      f.add(new ClassKeyDerivation(-3141068534933210845L));
      f.add(new ClassKeyDerivation(7940180920101956596L));
      f.add(new ClassKeyDerivation(1507400842116601152L));
      f.add(new ClassKeyDerivation(1597616281475579640L));
      f.add(new ClassKeyDerivation(-6328728974360447842L));
      f.add(new ClassKeyDerivation(8384610393748601616L));
      f.add(new ClassKeyDerivation(3180177636710700381L));
      f.add(new ClassKeyDerivation(-9037707521721674582L));
      f.add(new ClassKeyDerivation(8797965256657988667L));
      f.add(new ClassKeyDerivation(19216024006989327L));
      f.add(new ClassKeyDerivation(-9016034040988119374L));
      f.add(new ClassKeyDerivation(-1433108501798853954L));
      f.add(new ClassKeyDerivation(-9214231935989294021L));
      f.add(new ClassKeyDerivation(7545873719567138063L));
      f.add(new ClassKeyDerivation(1465215757541634230L));
      f.add(new ClassKeyDerivation(-705640041441350681L));
      f.add(new ClassKeyDerivation(1054920317736101808L));
      f.add(new ClassKeyDerivation(-6428649537518351743L));
      f.add(new ClassKeyDerivation(-8739817773211559880L));
      f.add(new ClassKeyDerivation(4762611081440467096L));
      f.add(new ClassKeyDerivation(3468215037863671903L));
      f.add(new ClassKeyDerivation(3341021189638587091L));
      f.add(new ClassKeyDerivation(7248024026728051243L));
      f.add(new ClassKeyDerivation(-6050820844493340365L));
      f.add(new ClassKeyDerivation(-8346338721371548967L));
      f.add(new ClassKeyDerivation(-8886666171589785259L));
      f.add(new ClassKeyDerivation(3385978374069551130L));
      f.add(new ClassKeyDerivation(741612084832716501L));
      f.add(new ClassKeyDerivation(8422435954949424603L));
      f.add(new ClassKeyDerivation(-8732484577021085273L));
      f.add(new ClassKeyDerivation(-3592508477885748871L));
      f.add(new ClassKeyDerivation(-3597471396475762650L));
      f.add(new ClassKeyDerivation(7843553577856622038L));
      f.add(new ClassKeyDerivation(4932248787599991702L));
      f.add(new ClassKeyDerivation(-669906878118785391L));
      f.add(new ClassKeyDerivation(-429903361178054616L));
      f.add(new ClassKeyDerivation(-7902017300184265245L));
      f.add(new ClassKeyDerivation(8507207244526069407L));
      f.add(new ClassKeyDerivation(3386488553994815373L));
      f.add(new ClassKeyDerivation(6119456790167583766L));
      f.add(new ClassKeyDerivation(-386127390260928059L));
      f.add(new ClassKeyDerivation(-3548648352490240998L));
      f.add(new ClassKeyDerivation(1483793064697938957L));
      f.add(new ClassKeyDerivation(3464275231668059152L));
      f.add(new ClassKeyDerivation(51116711722478536L));
      f.add(new ClassKeyDerivation(7708535959431752065L));
      f.add(new ClassKeyDerivation(5677913939446323834L));
      f.add(new ClassKeyDerivation(-90060460306332340L));
      f.add(new ClassKeyDerivation(-4737343446602190415L));
      f.add(new ClassKeyDerivation(6833649285373404543L));
      f.add(new ClassKeyDerivation(-1717971128268574604L));
      f.add(new ClassKeyDerivation(5254895121682397610L));
      f.add(new ClassKeyDerivation(-8734673497920469152L));
      f.add(new ClassKeyDerivation(-5317434666542874190L));
      f.add(new ClassKeyDerivation(9006776170749159206L));
      f.add(new ClassKeyDerivation(195099533717013516L));
      f.add(new ClassKeyDerivation(2929709149377079591L));
      f.add(new ClassKeyDerivation(-4284014344199798161L));
      f.add(new ClassKeyDerivation(-8005943263593293811L));
      f.add(new ClassKeyDerivation(1185602748284883556L));
      f.add(new ClassKeyDerivation(-2529583498027619449L));
      f.add(new ClassKeyDerivation(-2092252333314635394L));
      f.add(new ClassKeyDerivation(8829376368329174552L));
      f.add(new ClassKeyDerivation(2800279879167397041L));
      f.add(new ClassKeyDerivation(-7909436520183427158L));
      f.add(new ClassKeyDerivation(6662254379261202984L));
      f.add(new ClassKeyDerivation(-7310382858101897064L));
      f.add(new ClassKeyDerivation(-5304449097311108423L));
      f.add(new ClassKeyDerivation(2648961029289087153L));
      f.add(new ClassKeyDerivation(6264134873028742795L));
      f.add(new ClassKeyDerivation(-965841500856908291L));
      f.add(new ClassKeyDerivation(-2584440339779911561L));
      f.add(new ClassKeyDerivation(-250118329330934814L));
      f.add(new ClassKeyDerivation(-2982749123213828866L));
      f.add(new ClassKeyDerivation(7260930071150489878L));
      f.add(new ClassKeyDerivation(1198316142108424857L));
      f.add(new ClassKeyDerivation(2178880921848551374L));
      f.add(new ClassKeyDerivation(1914725038230722248L));
      f.add(new ClassKeyDerivation(5919521809110162679L));
      f.add(new ClassKeyDerivation(898834122363152862L));
      f.add(new ClassKeyDerivation(5689544006936705298L));
      f.add(new ClassKeyDerivation(5339104128911465477L));
      f.add(new ClassKeyDerivation(3501247099889518020L));
      f.add(new ClassKeyDerivation(1360446637529399952L));
      f.add(new ClassKeyDerivation(-8350654647653335422L));
      f.add(new ClassKeyDerivation(-863149784174871200L));
      f.add(new ClassKeyDerivation(296749772186242937L));
      f.add(new ClassKeyDerivation(1173089367858184184L));
      f.add(new ClassKeyDerivation(-1108477506414831927L));
      f.add(new ClassKeyDerivation(-8468383668571497342L));
      f.add(new ClassKeyDerivation(-8476814525344897440L));
      f.add(new ClassKeyDerivation(5682864217623511556L));
      f.add(new ClassKeyDerivation(-6333361055507057308L));
      f.add(new ClassKeyDerivation(-514207055201764219L));
      f.add(new ClassKeyDerivation(3792763746638343286L));
      f.add(new ClassKeyDerivation(7084686885041119695L));
      f.add(new ClassKeyDerivation(-8296837343006116874L));
      f.add(new ClassKeyDerivation(1750257250787364203L));
      f.add(new ClassKeyDerivation(-9184402104784205683L));
      f.add(new ClassKeyDerivation(-1141879371616510009L));
      f.add(new ClassKeyDerivation(1312249230647906774L));
      f.add(new ClassKeyDerivation(-7765380040874716691L));
      f.add(new ClassKeyDerivation(-7175385471146662977L));
      f.add(new ClassKeyDerivation(-8493041785305209180L));
      f.add(new ClassKeyDerivation(6989386257514033197L));
      f.add(new ClassKeyDerivation(-3963882932085361359L));
      f.add(new ClassKeyDerivation(7818562720731362573L));
      f.add(new ClassKeyDerivation(8381402711322415708L));
      e = (int[])e.clone();
      f.add(new ClassKeyDerivation(7963788230045630388L));
      f.add(new ClassKeyDerivation(4046053604039403049L));
      f.add(new ClassKeyDerivation(2369182884386462084L));
      f.add(new ClassKeyDerivation(6412056606388571062L));
      f.add(new ClassKeyDerivation(-1579223990667395764L));
      f.add(new ClassKeyDerivation(2188743520116884521L));
      f.add(new ClassKeyDerivation(8120590074422308255L));
      f.add(new ClassKeyDerivation(-8145868489987530677L));
      f.add(new ClassKeyDerivation(1040366181431722517L));
      f.add(new ClassKeyDerivation(-4888660107844389510L));
      f.add(new ClassKeyDerivation(6684852657061058238L));
      f.add(new ClassKeyDerivation(7511035427596027910L));
      f.add(new ClassKeyDerivation(157517928803108886L));
      f.add(new ClassKeyDerivation(7073320846270368394L));
      f.add(new ClassKeyDerivation(7495680314827063990L));
      f.add(new ClassKeyDerivation(7235731388775405435L));
      f.add(new ClassKeyDerivation(5944751393743327786L));
      f.add(new ClassKeyDerivation(7677641120535832433L));
      f.add(new ClassKeyDerivation(-1417237255704531731L));
      f.add(new ClassKeyDerivation(-3840217752991844140L));
      f.add(new ClassKeyDerivation(-9052504379603131644L));
      f.add(new ClassKeyDerivation(7807604629902541287L));
      f.add(new ClassKeyDerivation(-851116461133666796L));
      f.add(new ClassKeyDerivation(-2235792145473515830L));
      f.add(new ClassKeyDerivation(5953272353086496544L));
      f.add(new ClassKeyDerivation(2463166162530993016L));
      f.add(new ClassKeyDerivation(-5451667228921951771L));
      f.add(new ClassKeyDerivation(756880851838369236L));
      f.add(new ClassKeyDerivation(4220785181327358277L));
      f.add(new ClassKeyDerivation(-5843491688381659324L));
      f.add(new ClassKeyDerivation(-4854957106145575099L));
      f.add(new ClassKeyDerivation(-3889344277305719553L));
      f.add(new ClassKeyDerivation(1322022929683305302L));
      f.add(new ClassKeyDerivation(-9057048459850019714L));
      f.add(new ClassKeyDerivation(-847424747418891717L));
      f.add(new ClassKeyDerivation(500899371001713881L));
      f.add(new ClassKeyDerivation(-1144176110490125448L));
      f.add(new ClassKeyDerivation(-2012545106893947730L));
      f.add(new ClassKeyDerivation(6237069115285155691L));
      f.add(new ClassKeyDerivation(3662351380468786463L));
      f.add(new ClassKeyDerivation(2225864932825909245L));
      f.add(new ClassKeyDerivation(-264354893931630847L));
      f.add(new ClassKeyDerivation(3922839571491896479L));
      f.add(new ClassKeyDerivation(-3576036883689173106L));
      f.add(new ClassKeyDerivation(9095873543210131966L));
      f.add(new ClassKeyDerivation(-5839309261168251834L));
      f.add(new ClassKeyDerivation(-4147926000618993122L));
      f.add(new ClassKeyDerivation(7675858418660565192L));
      f.add(new ClassKeyDerivation(5387643703122752916L));
      f.add(new ClassKeyDerivation(-6303938632641076338L));
      f.add(new ClassKeyDerivation(3085060359114193397L));
      f.add(new ClassKeyDerivation(-2239114476461806057L));
      f.add(new ClassKeyDerivation(318880954156623685L));
      f.add(new ClassKeyDerivation(-3564228181656812463L));
      f.add(new ClassKeyDerivation(7118028630198463426L));
      f.add(new ClassKeyDerivation(-73034750577336329L));
      f.add(new ClassKeyDerivation(3774540729555231518L));
      f.add(new ClassKeyDerivation(-9066100661860291278L));
      f.add(new ClassKeyDerivation(1307197411580832664L));
      f.add(new ClassKeyDerivation(4672682909262230663L));
      f.add(new ClassKeyDerivation(7506177088929573849L));
      f.add(new ClassKeyDerivation(-1123921398885953759L));
      f.add(new ClassKeyDerivation(8965568307264318109L));
      f.add(new ClassKeyDerivation(-6707350969212785487L));
      f.add(new ClassKeyDerivation(-8310187323864029845L));
      f.add(new ClassKeyDerivation(8678600920637383391L));
      f.add(new ClassKeyDerivation(-8432209829209157387L));
      f.add(new ClassKeyDerivation(-2963330821697026340L));
      f.add(new ClassKeyDerivation(5924511534394555431L));
      f.add(new ClassKeyDerivation(-4848994787272347784L));
      f.add(new ClassKeyDerivation(437349242900387725L));
      f.add(new ClassKeyDerivation(-5025172326050387572L));
      f.add(new ClassKeyDerivation(1732718772233161816L));
      f.add(new ClassKeyDerivation(-78041996303015176L));
      f.add(new ClassKeyDerivation(-9176160338369741365L));
      f.add(new ClassKeyDerivation(8700682739258297948L));
      f.add(new ClassKeyDerivation(2374955037223312116L));
      f.add(new ClassKeyDerivation(7930736758632680668L));
      f.add(new ClassKeyDerivation(4817735314185779155L));
      f.add(new ClassKeyDerivation(1762550841983535132L));
      f.add(new ClassKeyDerivation(-8326731558419394956L));
      f.add(new ClassKeyDerivation(2222271902520249871L));
      f.add(new ClassKeyDerivation(9006566270725970340L));
      f.add(new ClassKeyDerivation(-5569822418216660708L));
      f.add(new ClassKeyDerivation(6264071023068948935L));
      f.add(new ClassKeyDerivation(-2388748657192252069L));
      f.add(new ClassKeyDerivation(-3102199439733758422L));
      f.add(new ClassKeyDerivation(-1708425859194721252L));
      f.add(new ClassKeyDerivation(-7571483492481538616L));
      f.add(new ClassKeyDerivation(-7032556169692977980L));
      f.add(new ClassKeyDerivation(726906683137045146L));
      f.add(new ClassKeyDerivation(-7320701977872883146L));
      f.add(new ClassKeyDerivation(3492488240247267636L));
      f.add(new ClassKeyDerivation(-6167344905535750723L));
      f.add(new ClassKeyDerivation(-6420206034471384022L));
      f.add(new ClassKeyDerivation(917221258219362932L));
      f.add(new ClassKeyDerivation(-6274215767851133155L));
      f.add(new ClassKeyDerivation(5829610532186872477L));
      f.add(new ClassKeyDerivation(766735486456533564L));
      f.add(new ClassKeyDerivation(1892849730388446013L));
      f.add(new ClassKeyDerivation(-1813805794491256263L));
      f.add(new ClassKeyDerivation(2208701106303003516L));
      f.add(new ClassKeyDerivation(3751083413445056464L));
      f.add(new ClassKeyDerivation(-3007793660874086821L));
      f.add(new ClassKeyDerivation(-9052508993772293352L));
      f.add(new ClassKeyDerivation(-4425220855617204390L));
      f.add(new ClassKeyDerivation(1628770093032163919L));
      f.add(new ClassKeyDerivation(7848470375832423677L));
      f.add(new ClassKeyDerivation(-5739353764346477698L));
      f.add(new ClassKeyDerivation(9149602679688341355L));
      f.add(new ClassKeyDerivation(5654862490039618358L));
      f.add(new ClassKeyDerivation(185195273415556063L));
      f.add(new ClassKeyDerivation(777727504242102541L));
      f.add(new ClassKeyDerivation(-7038142231904861991L));
      f.add(new ClassKeyDerivation(4234435585058257791L));
      f.add(new ClassKeyDerivation(-6881665681557100678L));
      f.add(new ClassKeyDerivation(4945519917466402990L));
      f.add(new ClassKeyDerivation(-3888942819361558869L));
      f.add(new ClassKeyDerivation(7368773690394540983L));
      f.add(new ClassKeyDerivation(4789678067231937823L));
      f.add(new ClassKeyDerivation(9080691047117476845L));
      f.add(new ClassKeyDerivation(-8606457165261262454L));
      f.add(new ClassKeyDerivation(2801494667937349575L));
      f.add(new ClassKeyDerivation(7986764109208282072L));
      f.add(new ClassKeyDerivation(6121235576998037540L));
      f.add(new ClassKeyDerivation(2190555329596468229L));
      f.add(new ClassKeyDerivation(-8207123860216705815L));
      f.add(new ClassKeyDerivation(3643683816186005427L));
      f.add(new ClassKeyDerivation(-825166541858324177L));
      f.add(new ClassKeyDerivation(6647233453946184529L));
      f.add(new ClassKeyDerivation(2065026203979662301L));
      f.add(new ClassKeyDerivation(5360549313840586974L));
      f.add(new ClassKeyDerivation(3757105091151415996L));
      f.add(new ClassKeyDerivation(1771882129151084049L));
      f.add(new ClassKeyDerivation(-8323911128324040085L));
      f.add(new ClassKeyDerivation(6396257950083165429L));
      f.add(new ClassKeyDerivation(1049448713366867170L));
      f.add(new ClassKeyDerivation(-2973842781343350919L));
      f.add(new ClassKeyDerivation(4214780666105410491L));
      f.add(new ClassKeyDerivation(5929540748004323778L));
      f.add(new ClassKeyDerivation(8938595868920012115L));
      f.add(new ClassKeyDerivation(-4567793236473907172L));
      f.add(new ClassKeyDerivation(4194814525031305961L));
      f.add(new ClassKeyDerivation(499056076758548171L));
      f.add(new ClassKeyDerivation(-1702895091255502294L));
      f.add(new ClassKeyDerivation(-2983517895369570488L));
      f.add(new ClassKeyDerivation(-1422447125018115624L));
      f.add(new ClassKeyDerivation(1027416057408740711L));
      f.add(new ClassKeyDerivation(292818143117134490L));
      f.add(new ClassKeyDerivation(4975417183317852L));
      f.add(new ClassKeyDerivation(-1115016187288067298L));
      f.add(new ClassKeyDerivation(-5883852876950678595L));
      f.add(new ClassKeyDerivation(1875968878367979181L));
      f.add(new ClassKeyDerivation(2086441544418449163L));
      e = new int[]{
         -1,
         1,
         -19,
         -15,
         -13,
         -4,
         -10,
         -19,
         -41,
         4,
         -10,
         -32,
         -27,
         -33,
         -15,
         -45,
         10,
         13,
         15,
         -34,
         10,
         19,
         -16,
         -11,
         -11,
         -3,
         19,
         -36,
         3,
         15,
         -26,
         -16,
         -19,
         -24,
         11,
         11,
         -16,
         -7,
         16,
         27,
         -5,
         -13,
         -8,
         32,
         7,
         5,
         33,
         16,
         -14,
         41,
         8,
         19,
         16,
         34,
         13,
         -3,
         26,
         24,
         3,
         -2,
         45,
         2,
         14,
         36
      };
   }
}
