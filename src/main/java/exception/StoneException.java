package exception;

/**
 * Created by LPCTSTR_MSR
 * Date: 2020/06/07
 * Description:null
 */
public class StoneException extends Exception {
    public StoneException(String message) {
        super("Stone Exception: "+ message);
    }
}
