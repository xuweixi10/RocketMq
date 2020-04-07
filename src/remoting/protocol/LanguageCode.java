package remoting.protocol;

/**
 * @author xuxiaoxi10
 * @version 1.0
 * @date 2020/4/4 22:46
 */
public enum LanguageCode {
    JAVA((byte) 0),
    CPP((byte) 1),
    DOTNET((byte) 2),
    PYTHON((byte) 3),
    DELPHI((byte) 4),
    ERLANG((byte) 5),
    RUBY((byte) 6),
    OTHER((byte) 7),
    HTTP((byte) 8);

    private byte code;

    LanguageCode(byte code) {
        this.code = code;
    }

    public static LanguageCode valueOf(byte code) {
        for (LanguageCode languageCode : LanguageCode.values()) {
            if (languageCode.getCode() == code) {
                return languageCode;
            }
        }
        return null;
    }

    public byte getCode() {
        return code;
    }
}
