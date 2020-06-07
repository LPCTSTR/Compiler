import exception.ParseException;
import exception.StoneException;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LPCTSTR_MSR
 * Date: 2020/06/07
 * Description:null
 */
public class Lexer {
//    public static String regexPat
//            = "\\s*((//.*)|([0-9]+)|(\"(\\\\\"|\\\\\\\\|\\\\n|[^\"])*\")"
//            + "|[A-Z_a-z][A-Z_a-z0-9]*|==|<=|>=|&&|\\|\\|\\p{Punct})?";
    public static String regexPat
            = "\\s*((//.*)|([0-9]+)|(\"(\\\\\"|\\\\\\\\|\\\\n|[^\"])*\")"
            + "|[A-Z_a-z][A-Z_a-z0-9]*|==|<=|>=|&&|\\|\\||\\p{Punct})?";
    private Pattern pattern = Pattern.compile(regexPat);
    private ArrayList<Token> queue = new ArrayList<Token>();
    private boolean hasMore;
    private LineNumberReader reader;

    public Lexer(Reader r) {
        reader = new LineNumberReader(r);
        hasMore =true;
    }

    public Token read() {
        if (fillQueue(0))
            return queue.remove(0);
        return Token.EOF;
    }

    public Token peek(int i) {
        if (fillQueue(i))
            return queue.get(i);
        return Token.EOF;
    }

    private boolean fillQueue(int i) {
        while (i >= queue.size())
            if (hasMore) {
                try {
                    readLine();
                } catch (StoneException e) {
                    e.printStackTrace();
                }
            } else
                return false;
        return true;
    }


    private void readLine() throws StoneException {
        String line = null;

        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (line == null) {
            hasMore = false;
            return;
        }

        int lineNo = reader.getLineNumber();
        Matcher matcher = pattern.matcher(line);
        matcher.useTransparentBounds(true).useAnchoringBounds(false);
        int pos = 0;
        int endPos = line.length();
        while (pos < endPos) {
            matcher.region(pos, endPos);
            if (matcher.lookingAt()) {
                addToken(lineNo, matcher);
                pos = matcher.end();
            } else {
                throw new ParseException("Bad token at line " + lineNo);
            }
        }
        queue.add(new IdToken(lineNo, Token.EOL));

    }

    private void addToken(int lineNo, Matcher matcher) {
        /*
        To understand the function group() here ,
        we should review the definition of the pattern;
        \\s*((//.*)|([0-9]+)|(\"(\\\\\"|\\\\\\\\|\\\\n|[^\"])*\")|[A-Z_a-z][A-Z_a-z0-9]*|==|<=|>=|&&|\\|\\|\\p{Punct})?
         */
        String m = matcher.group(1);
        if (m == null)   //m is a space (The most outer parentheses)
            return;
        if (matcher.group(2) != null) // it's a comment
            return;
        if (matcher.group(3) != null)
            queue.add(new NumToken(lineNo, Integer.parseInt(m)));
        else if (matcher.group(4) != null)
            queue.add(new StrToken(lineNo, toStringLiteral(m)));
        else
            queue.add(new IdToken(lineNo,m));
    }


    protected String toStringLiteral(String s) {
        StringBuilder sb = new StringBuilder();
        int len = s.length() - 1;
        for (int i = 1; i < len; i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < len) { // Determine if it is an escape character '\'
                int c2 = s.charAt(i + 1);
                if (c2 == '"' || c2 == '\\') // \" | \\
                    c = s.charAt(++i);
                else if (c2 == 'n') { // \n
                    ++i;
                    c = '\n';
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    protected static class IdToken extends Token {

        private final String text;

        protected IdToken(int line, String id) {
            super(line);
            text = id;
        }

        @Override
        public boolean isIdentifier() {
            return true;
        }

        @Override
        public String getText() {
            return text;
        }
    }

    protected static class NumToken extends Token {

        private final int value;

        protected NumToken(int line, int v) {
            super(line);
            value = v;
        }

        @Override
        public boolean isNumber() {
            return true;
        }

        @Override
        public int getNumber() throws StoneException {
            return value;
        }

        @Override
        public String getText() {
            return Integer.toString(value);
        }
    }

    protected static class StrToken extends Token {
        private String literal;

        StrToken(int line, String literal) {
            super(line);
            this.literal = literal;
        }

        public boolean isString() {
            return true;
        }

        public String getText() {
            return literal;
        }
    }

}
