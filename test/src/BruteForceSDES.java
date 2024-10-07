import java.util.ArrayList;
import java.util.List;


/**
 * 使用给定的1组明密文对进行暴力破解密钥
 */
public class BruteForceSDES {
    //对于传入的明文和密文进入处理
    public static List<String> crackKeyForInputAscii(String plainText, String cipherText){
        List<String> keys = null;
        for(int i=0;i<plainText.length();i++){
            List<String> currentKeys=crackKeyForCharacter(plainText.charAt(i),cipherText.charAt(i));
            if(i==0) keys=currentKeys;
            else keys.removeIf(element->!currentKeys.contains(element));
            if(keys.isEmpty()){
                System.out.println("出现错误，keys不存在");
                break;
            }
        }
        return keys;
    }
    public static List<String> crackKeyForInputBinary(String plainText,String cipherText){
        List<String> keys = null;
        //异常检测
        if(plainText.length()!=cipherText.length()||plainText.length()%8!=0){
            System.out.println("错误：明文与密文长度不同或长度不为8的整数");
            return keys;
        }
        for(int i=0;i<plainText.length();i+=8){
            int start=i,end=i+8;
            String plainTextBites=plainText.substring(start,end);
            String cipherTextBites=plainText.substring(start,end);

            List<String> currentKeys=crackKeyForBinary(plainTextBites,cipherTextBites);
            if(i==0) keys=currentKeys;
            else keys.removeIf(element->!currentKeys.contains(element));
            if(keys.isEmpty()){
                System.out.println("出现错误，keys不存在");
                break;
            }
        }
        return keys;
    }
    //单个字符暴力破解密钥
    public static List<String> crackKeyForCharacter(char plainTextCharacter, char cipherTextCharacter) {
        return crackAllKeysHelper(plainTextCharacter, cipherTextCharacter, true);
    }
   //8bit二进制串暴力破解密钥
    public static List<String> crackKeyForBinary(String plainTextBinary, String cipherTextBinary) {
        char plainTextCharacter = (char) Integer.parseInt(plainTextBinary, 2);
        char cipherTextCharacter = (char) Integer.parseInt(cipherTextBinary, 2);
        return crackAllKeysHelper(plainTextCharacter, cipherTextCharacter, false);
    }
   //暴力破解辅助函数
    private static List<String> crackAllKeysHelper(char plainTextCharacter, char cipherTextCharacter, boolean isCharacter) {
        List<String> validKeys = new ArrayList<>();
        for (int i = 0; i < 1024; i++) {
            String key = String.format("%10s", Integer.toBinaryString(i)).replace(' ', '0');

            char calculatedCipher;
            if (isCharacter) {
                calculatedCipher = SDES.encryptCharacter(plainTextCharacter, key);
            } else {
                String binaryInput = String.format("%8s", Integer.toBinaryString(plainTextCharacter)).replace(' ', '0');
                String encryptedBinary = SDES.encryptBinary(binaryInput, key);
                calculatedCipher = (char) Integer.parseInt(encryptedBinary, 2);
            }

            if (calculatedCipher == cipherTextCharacter) {
                validKeys.add(key);
            }
        }
        return validKeys;
    }
}
