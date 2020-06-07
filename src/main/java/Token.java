import exception.StoneException;

/**
 * Created by LPCTSTR_MSR
 * Date: 2020/06/07
 * Description:トーケン
 */
public abstract class Token {
    public static final Token EOF = new Token(-1) {
    };
    public static final String EOL = "\\n";

    private int lineNumber;

    public int getLineNumber() {
        return lineNumber;
    }

    protected Token(int line) {
        this.lineNumber = line;
    }



    public boolean isIdentifier() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public int getNumber() throws StoneException {
        throw new StoneException("Not number token");
    }

    public String getText() {
        return "";
    }
}
