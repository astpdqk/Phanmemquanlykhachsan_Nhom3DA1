package utilities;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RandomCode {

    private ReadWriteData readWriteData = new ReadWriteData();

    public String createCode(String firstCode, String fileName) {
        firstCode = firstCode.toUpperCase();
        int lastCode = 0;
        try {
            for (int i : readWriteData.doc(fileName)) {
                lastCode = i;
            }
        } catch (IOException ex) {
            Logger.getLogger(RandomCode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RandomCode.class.getName()).log(Level.SEVERE, null, ex);
        }
        String code = firstCode;
        if (lastCode < 10) {
            lastCode = lastCode + 1;
            code = firstCode + "0" + String.valueOf(lastCode);
        }

        return code;
    }

    public static void main(String[] args) {

    }
}
