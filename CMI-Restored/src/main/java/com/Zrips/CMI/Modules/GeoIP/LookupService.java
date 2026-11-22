package com.Zrips.CMI.Modules.GeoIP;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.ConcurrentHashMap;

public class LookupService {
  private RandomAccessFile file;
  private final File databaseFile;
  private static final Charset charset = Charset.forName("ISO-8859-1");
  private final CharsetDecoder charsetDecoder = charset.newDecoder();
  ConcurrentHashMap<String, IPLocation> cache = new ConcurrentHashMap<>();
  private byte databaseType = 1;
  private int[] databaseSegments;
  private int recordLength;
  private int dboptions;
  private byte[] dbbuffer;
  private byte[] index_cache;
  private long mtime;
  private static final int COUNTRY_BEGIN = 16776960;
  private static final int STATE_BEGIN_REV0 = 16700000;
  private static final int STATE_BEGIN_REV1 = 16000000;
  private static final int STRUCTURE_INFO_MAX_SIZE = 20;
  public static final int GEOIP_STANDARD = 0;
  public static final int GEOIP_MEMORY_CACHE = 1;
  public static final int GEOIP_CHECK_CACHE = 2;
  public static final int GEOIP_INDEX_CACHE = 4;
  public static final int GEOIP_UNKNOWN_SPEED = 0;
  public static final int GEOIP_DIALUP_SPEED = 1;
  public static final int GEOIP_CABLEDSL_SPEED = 2;
  public static final int GEOIP_CORPORATE_SPEED = 3;
  private static final int SEGMENT_RECORD_LENGTH = 3;
  private static final int STANDARD_RECORD_LENGTH = 3;
  private static final int ORG_RECORD_LENGTH = 4;
  private static final int MAX_RECORD_LENGTH = 4;
  private static final int FULL_RECORD_LENGTH = 60;
  private static final String[] countryCode =
      new String[] {
        "--", "AP", "EU", "AD", "AE", "AF", "AG", "AI", "AL", "AM", "CW", "AO", "AQ", "AR", "AS",
        "AT", "AU", "AW", "AZ", "BA", "BB", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BM", "BN",
        "BO", "BR", "BS", "BT", "BV", "BW", "BY", "BZ", "CA", "CC", "CD", "CF", "CG", "CH", "CI",
        "CK", "CL", "CM", "CN", "CO", "CR", "CU", "CV", "CX", "CY", "CZ", "DE", "DJ", "DK", "DM",
        "DO", "DZ", "EC", "EE", "EG", "EH", "ER", "ES", "ET", "FI", "FJ", "FK", "FM", "FO", "FR",
        "SX", "GA", "GB", "GD", "GE", "GF", "GH", "GI", "GL", "GM", "GN", "GP", "GQ", "GR", "GS",
        "GT", "GU", "GW", "GY", "HK", "HM", "HN", "HR", "HT", "HU", "ID", "IE", "IL", "IN", "IO",
        "IQ", "IR", "IS", "IT", "JM", "JO", "JP", "KE", "KG", "KH", "KI", "KM", "KN", "KP", "KR",
        "KW", "KY", "KZ", "LA", "LB", "LC", "LI", "LK", "LR", "LS", "LT", "LU", "LV", "LY", "MA",
        "MC", "MD", "MG", "MH", "MK", "ML", "MM", "MN", "MO", "MP", "MQ", "MR", "MS", "MT", "MU",
        "MV", "MW", "MX", "MY", "MZ", "NA", "NC", "NE", "NF", "NG", "NI", "NL", "NO", "NP", "NR",
        "NU", "NZ", "OM", "PA", "PE", "PF", "PG", "PH", "PK", "PL", "PM", "PN", "PR", "PS", "PT",
        "PW", "PY", "QA", "RE", "RO", "RU", "RW", "SA", "SB", "SC", "SD", "SE", "SG", "SH", "SI",
        "SJ", "SK", "SL", "SM", "SN", "SO", "SR", "ST", "SV", "SY", "SZ", "TC", "TD", "TF", "TG",
        "TH", "TJ", "TK", "TM", "TN", "TO", "TL", "TR", "TT", "TV", "TW", "TZ", "UA", "UG", "UM",
        "US", "UY", "UZ", "VA", "VC", "VE", "VG", "VI", "VN", "VU", "WF", "WS", "YE", "YT", "RS",
        "ZA", "ZM", "ME", "ZW", "A1", "A2", "O1", "AX", "GG", "IM", "JE", "BL", "MF", "BQ", "SS",
        "O1"
      };
  private static final String[] countryName =
      new String[] {
        "N/A",
        "Asia/Pacific Region",
        "Europe",
        "Andorra",
        "United Arab Emirates",
        "Afghanistan",
        "Antigua and Barbuda",
        "Anguilla",
        "Albania",
        "Armenia",
        "Curacao",
        "Angola",
        "Antarctica",
        "Argentina",
        "American Samoa",
        "Austria",
        "Australia",
        "Aruba",
        "Azerbaijan",
        "Bosnia and Herzegovina",
        "Barbados",
        "Bangladesh",
        "Belgium",
        "Burkina Faso",
        "Bulgaria",
        "Bahrain",
        "Burundi",
        "Benin",
        "Bermuda",
        "Brunei Darussalam",
        "Bolivia",
        "Brazil",
        "Bahamas",
        "Bhutan",
        "Bouvet Island",
        "Botswana",
        "Belarus",
        "Belize",
        "Canada",
        "Cocos (Keeling) Islands",
        "Congo, The Democratic Republic of the",
        "Central African Republic",
        "Congo",
        "Switzerland",
        "Cote D'Ivoire",
        "Cook Islands",
        "Chile",
        "Cameroon",
        "China",
        "Colombia",
        "Costa Rica",
        "Cuba",
        "Cape Verde",
        "Christmas Island",
        "Cyprus",
        "Czech Republic",
        "Germany",
        "Djibouti",
        "Denmark",
        "Dominica",
        "Dominican Republic",
        "Algeria",
        "Ecuador",
        "Estonia",
        "Egypt",
        "Western Sahara",
        "Eritrea",
        "Spain",
        "Ethiopia",
        "Finland",
        "Fiji",
        "Falkland Islands (Malvinas)",
        "Micronesia, Federated States of",
        "Faroe Islands",
        "France",
        "Sint Maarten (Dutch part)",
        "Gabon",
        "United Kingdom",
        "Grenada",
        "Georgia",
        "French Guiana",
        "Ghana",
        "Gibraltar",
        "Greenland",
        "Gambia",
        "Guinea",
        "Guadeloupe",
        "Equatorial Guinea",
        "Greece",
        "South Georgia and the South Sandwich Islands",
        "Guatemala",
        "Guam",
        "Guinea-Bissau",
        "Guyana",
        "Hong Kong",
        "Heard Island and McDonald Islands",
        "Honduras",
        "Croatia",
        "Haiti",
        "Hungary",
        "Indonesia",
        "Ireland",
        "Israel",
        "India",
        "British Indian Ocean Territory",
        "Iraq",
        "Iran, Islamic Republic of",
        "Iceland",
        "Italy",
        "Jamaica",
        "Jordan",
        "Japan",
        "Kenya",
        "Kyrgyzstan",
        "Cambodia",
        "Kiribati",
        "Comoros",
        "Saint Kitts and Nevis",
        "Korea, Democratic People's Republic of",
        "Korea, Republic of",
        "Kuwait",
        "Cayman Islands",
        "Kazakhstan",
        "Lao People's Democratic Republic",
        "Lebanon",
        "Saint Lucia",
        "Liechtenstein",
        "Sri Lanka",
        "Liberia",
        "Lesotho",
        "Lithuania",
        "Luxembourg",
        "Latvia",
        "Libya",
        "Morocco",
        "Monaco",
        "Moldova, Republic of",
        "Madagascar",
        "Marshall Islands",
        "Macedonia",
        "Mali",
        "Myanmar",
        "Mongolia",
        "Macau",
        "Northern Mariana Islands",
        "Martinique",
        "Mauritania",
        "Montserrat",
        "Malta",
        "Mauritius",
        "Maldives",
        "Malawi",
        "Mexico",
        "Malaysia",
        "Mozambique",
        "Namibia",
        "New Caledonia",
        "Niger",
        "Norfolk Island",
        "Nigeria",
        "Nicaragua",
        "Netherlands",
        "Norway",
        "Nepal",
        "Nauru",
        "Niue",
        "New Zealand",
        "Oman",
        "Panama",
        "Peru",
        "French Polynesia",
        "Papua New Guinea",
        "Philippines",
        "Pakistan",
        "Poland",
        "Saint Pierre and Miquelon",
        "Pitcairn Islands",
        "Puerto Rico",
        "Palestinian Territory",
        "Portugal",
        "Palau",
        "Paraguay",
        "Qatar",
        "Reunion",
        "Romania",
        "Russian Federation",
        "Rwanda",
        "Saudi Arabia",
        "Solomon Islands",
        "Seychelles",
        "Sudan",
        "Sweden",
        "Singapore",
        "Saint Helena",
        "Slovenia",
        "Svalbard and Jan Mayen",
        "Slovakia",
        "Sierra Leone",
        "San Marino",
        "Senegal",
        "Somalia",
        "Suriname",
        "Sao Tome and Principe",
        "El Salvador",
        "Syrian Arab Republic",
        "Swaziland",
        "Turks and Caicos Islands",
        "Chad",
        "French Southern Territories",
        "Togo",
        "Thailand",
        "Tajikistan",
        "Tokelau",
        "Turkmenistan",
        "Tunisia",
        "Tonga",
        "Timor-Leste",
        "Turkey",
        "Trinidad and Tobago",
        "Tuvalu",
        "Taiwan",
        "Tanzania, United Republic of",
        "Ukraine",
        "Uganda",
        "United States Minor Outlying Islands",
        "United States",
        "Uruguay",
        "Uzbekistan",
        "Holy See (Vatican City State)",
        "Saint Vincent and the Grenadines",
        "Venezuela",
        "Virgin Islands, British",
        "Virgin Islands, U.S.",
        "Vietnam",
        "Vanuatu",
        "Wallis and Futuna",
        "Samoa",
        "Yemen",
        "Mayotte",
        "Serbia",
        "South Africa",
        "Zambia",
        "Montenegro",
        "Zimbabwe",
        "Anonymous Proxy",
        "Satellite Provider",
        "Other",
        "Aland Islands",
        "Guernsey",
        "Isle of Man",
        "Jersey",
        "Saint Barthelemy",
        "Saint Martin",
        "Bonaire, Saint Eustatius and Saba",
        "South Sudan",
        "Other"
      };

  static {
    if (countryCode.length != countryName.length) {
      throw new AssertionError("countryCode.length!=countryName.length");
    }
  }

  public LookupService(File databaseFile, int value) throws IOException {
    this.databaseFile = databaseFile;
    if (databaseFile.isFile()) {
      this.file = new RandomAccessFile(databaseFile, "r");
    }

    this.dboptions = value;
    this.init();
  }

  private synchronized void init() throws IOException {
    if (this.file != null) {
      if ((this.dboptions & 2) != 0) {
        this.mtime = this.databaseFile.lastModified();
      }

      this.file.seek(this.file.length() - 3L);
      byte[] bs = new byte[3];
      byte[] buffer = new byte[3];

      for (int value = 0; value < 20; value++) {
        this.file.readFully(bs);
        if (bs[0] == -1 && bs[1] == -1 && bs[2] == -1) {
          this.databaseType = this.file.readByte();
          if (this.databaseType >= 106) {
            this.databaseType = (byte) (this.databaseType - 105);
          }

          if (this.databaseType == 7) {
            this.databaseSegments = new int[1];
            this.databaseSegments[0] = 16700000;
            this.recordLength = 3;
          } else if (this.databaseType == 3) {
            this.databaseSegments = new int[1];
            this.databaseSegments[0] = 16000000;
            this.recordLength = 3;
          } else if (this.databaseType == 6
              || this.databaseType == 2
              || this.databaseType == 5
              || this.databaseType == 23
              || this.databaseType == 4
              || this.databaseType == 22
              || this.databaseType == 11
              || this.databaseType == 24
              || this.databaseType == 9
              || this.databaseType == 21
              || this.databaseType == 32
              || this.databaseType == 33
              || this.databaseType == 31
              || this.databaseType == 30) {
            this.databaseSegments = new int[1];
            this.databaseSegments[0] = 0;
            if (this.databaseType != 6
                && this.databaseType != 2
                && this.databaseType != 21
                && this.databaseType != 32
                && this.databaseType != 33
                && this.databaseType != 31
                && this.databaseType != 30
                && this.databaseType != 9) {
              this.recordLength = 4;
            } else {
              this.recordLength = 3;
            }

            this.file.readFully(buffer);

            for (int index = 0; index < 3; index++) {
              this.databaseSegments[0] =
                  this.databaseSegments[0] + (unsignedByteToInt(buffer[index]) << index * 8);
            }
          }
          break;
        }

        this.file.seek(this.file.getFilePointer() - 4L);
      }

      if (this.databaseType == 1
          || this.databaseType == 12
          || this.databaseType == 8
          || this.databaseType == 10) {
        this.databaseSegments = new int[1];
        this.databaseSegments[0] = 16776960;
        this.recordLength = 3;
      }

      if ((this.dboptions & 1) == 1) {
        int fileLength = (int) this.file.length();
        this.dbbuffer = new byte[fileLength];
        this.file.seek(0L);
        this.file.readFully(this.dbbuffer, 0, fileLength);
        this.file.close();
      }

      if ((this.dboptions & 4) != 0) {
        int segmentBytes = this.databaseSegments[0] * this.recordLength * 2;
        this.index_cache = new byte[segmentBytes];
        this.file.seek(0L);
        this.file.readFully(this.index_cache, 0, segmentBytes);
      } else {
        this.index_cache = null;
      }
    }
  }

  public synchronized void close() {
    try {
      if (this.file != null) {
        this.file.close();
      }

      this.file = null;
    } catch (IOException ioException) {
    }
  }

  synchronized void _check_mtime() {
    try {
      if ((this.dboptions & 2) != 0) {
        long longValue = this.databaseFile.lastModified();
        if (longValue != this.mtime) {
          this.close();
          this.file = new RandomAccessFile(this.databaseFile, "r");
          this.init();
        }
      }
    } catch (IOException ioException) {
    }
  }

  public IPLocation getLocation(InetAddress inetAddress) throws IOException {
    return !this.databaseFile.isFile()
        ? getEmpty()
        : this.getLocation(bytesToLong(inetAddress.getAddress()));
  }

  public IPLocation getLocation(String string) throws IOException {
    if (string == null) {
      return getEmpty();
    }

    IPLocation ipLocation = this.cache.get(string);
    if (ipLocation != null) {
      return ipLocation;
    }

    if (!this.databaseFile.isFile()) {
      this.cache.put(string, getEmpty());
      return getEmpty();
    }

    InetAddress inetAddress;
    try {
      inetAddress = InetAddress.getByName(string);
    } catch (UnknownHostException unknownHostException) {
      this.cache.put(string, getEmpty());
      return getEmpty();
    }

    IPLocation location = this.getLocation(inetAddress);
    this.cache.put(string, location);
    return location;
  }

  private synchronized IPLocation getLocation(long longValue) throws IOException {
    if (!this.databaseFile.isFile()) {
      return getEmpty();
    }

    int value = this.seekCountry(longValue);
    return this.readCityRecord(value);
  }

  private static IPLocation getEmpty() {
    IPLocation ipLocation = new IPLocation();
    ipLocation.setCountryCode("--");
    ipLocation.setCountryName("N/A");
    return ipLocation;
  }

  private IPLocation readCityRecord(int value) throws IOException {
    if (value == this.databaseSegments[0]) {
      return getEmpty();
    }

    ByteBuffer byteBuffer = this.readRecordBuf(value, 60);
    IPLocation ipLocation = new IPLocation();

    try {
      int unsignedByte = unsignedByteToInt(byteBuffer.get());
      if (unsignedByte == 0) {
        int ipOffset = value - 16776960;
        ipLocation.setCountryCode(countryCode[ipOffset]);
        ipLocation.setCountryName(countryName[ipOffset]);
      } else {
        ipLocation.setCountryCode(countryCode[unsignedByte]);
        ipLocation.setCountryName(countryName[unsignedByte]);
      }

      ipLocation.setRegion(this.readString(byteBuffer));
      ipLocation.setCity(this.readString(byteBuffer));
      ipLocation.setPostalCode(this.readString(byteBuffer));
    } catch (Exception | Error value4) {
    }

    return ipLocation;
  }

  private ByteBuffer readRecordBuf(int value, int length) throws IOException {
    int offset = value + (2 * this.recordLength - 1) * this.databaseSegments[0];
    ByteBuffer byteBuffer;
    if ((this.dboptions & 1) == 1) {
      byteBuffer =
          ByteBuffer.wrap(this.dbbuffer, offset, Math.min(this.dbbuffer.length - offset, length));
    } else {
      byte[] bs = new byte[length];
      this.file.seek(offset);
      this.file.read(bs);
      byteBuffer = ByteBuffer.wrap(bs);
    }

    return byteBuffer;
  }

  private String readString(ByteBuffer byteBuffer) throws CharacterCodingException {
    int value = byteBuffer.position();
    int originalLimit = byteBuffer.limit();

    while (byteBuffer.hasRemaining() && byteBuffer.get() != 0) {}

    int end = byteBuffer.position() - 1;
    String string = null;
    if (end > value) {
      byteBuffer.position(value);
      byteBuffer.limit(end);
      string = this.charsetDecoder.decode(byteBuffer).toString();
      byteBuffer.limit(originalLimit);
    }

    byteBuffer.position(end + 1);
    return string;
  }

  private synchronized int seekCountry(long longValue) {
    byte[] bs = new byte[8];
    int[] values = new int[2];
    int value = 0;
    this._check_mtime();

    for (int bit = 31; bit >= 0; bit--) {
      this.readNode(bs, values, value);
      if ((longValue & 1 << bit) > 0L) {
        if (values[1] >= this.databaseSegments[0]) {
          return values[1];
        }

        value = values[1];
      } else {
        if (values[0] >= this.databaseSegments[0]) {
          return values[0];
        }

        value = values[0];
      }
    }

    return 0;
  }

  private void readNode(byte[] bs, int[] values, int value) {
    if ((this.dboptions & 1) == 1) {
      System.arraycopy(this.dbbuffer, 2 * this.recordLength * value, bs, 0, 2 * this.recordLength);
    } else if ((this.dboptions & 4) != 0) {
      System.arraycopy(
          this.index_cache, 2 * this.recordLength * value, bs, 0, 2 * this.recordLength);
    } else {
      try {
        this.file.seek(2 * this.recordLength * value);
        this.file.read(bs);
      } catch (IOException ioException) {
      }
    }

    for (int half = 0; half < 2; half++) {
      values[half] = 0;

      for (int byteIndex = 0; byteIndex < this.recordLength; byteIndex++) {
        short shortValue = bs[half * this.recordLength + byteIndex];
        if (shortValue < 0) {
          shortValue += 256;
        }

        values[half] += shortValue << byteIndex * 8;
      }
    }
  }

  private static long bytesToLong(byte[] bs) {
    long longValue = 0L;

    for (int value = 0; value < 4; value++) {
      long byteValue = bs[value];
      if (byteValue < 0L) {
        byteValue += 256L;
      }

      longValue += byteValue << (3 - value) * 8;
    }

    return longValue;
  }

  private static int unsignedByteToInt(byte b) {
    return b & 0xFF;
  }
}
