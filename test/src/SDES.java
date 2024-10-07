
/*
* 一轮加密SDES
* */
public class SDES {
    // 置换表
    private static final int[] IP = {2, 6, 3, 1, 4, 8, 5, 7};
    private static final int[] IP_INV = {4, 1, 3, 5, 7, 2, 8, 6};
    private static final int[] P10 = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};
    private static final int[] P8 = {6, 3, 7, 4, 8, 5, 10, 9};
    private static final int[] EP = {4, 1, 2, 3, 2, 3, 4, 1};
    private static final int[] P4 = {2, 4, 3, 1};

    // 替换盒
    private static final int[][] S0 = {
            {1, 0, 3, 2},
            {3, 2, 1, 0},
            {0, 2, 1, 3},
            {3, 1, 0, 2}
    };
    private static final int[][] S1 = {
            {0, 1, 2, 3},
            {2, 3, 1, 0},
            {3, 0, 1, 2},
            {2, 1, 0, 3}
    };

    /**
     * 从给定的10位密钥生成两个子密钥。
     */
    private static String[] keyGeneration(String key) {
        String permutedKey = permute(key, P10);
        String[] leftRight = split(permutedKey);
        String left = leftShift(leftRight[0], 1);
        String right = leftShift(leftRight[1], 1);
        String k1 = permute(left + right, P8);
        left = leftShift(left, 1);
        right = leftShift(right, 1);
        String k2 = permute(left + right, P8);
        return new String[]{k1, k2};
    }
    /**
     * 用输出的二进制字符串和密钥加密。
     */
    public static String encryptInputBinary(String plainText, String key){
        StringBuilder result= new StringBuilder();
        for(int i=0;i<plainText.length();i+=8){
            int start=i,end=i+8;
            String plainTextBites=plainText.substring(start,end);
            result.append(encryptBinary(plainTextBites, key));
        }
        return result.toString();
    }
    /**
     * 用输出的二进制字符串和密钥解密。
     */
    public static String decryptInputBinary(String cipherText,String key){
        StringBuilder result= new StringBuilder();
        for(int i=0;i<cipherText.length();i+=8){
            int start=i,end=i+8;
            String plainTextBites=cipherText.substring(start,end);
            result.append(decryptBinary(plainTextBites, key));
        }
        return result.toString();
    }
    /**
     * 用输出的ascii字符串和密钥加密。
     */
    public static String encryptInputAscii(String plainText, String key){
        StringBuilder result= new StringBuilder();
        for(int i=0;i<plainText.length();i++){
            result.append(encryptCharacter(plainText.charAt(i), key));
        }
        return result.toString();
    }
    /**
     * 用输出的ascii字符串和密钥解密。
     */
    public static String decryptInputAscii(String cipherText,String key){
        StringBuilder result= new StringBuilder();
        for(int i=0;i<cipherText.length();i++){
            result.append(decryptCharacter(cipherText.charAt(i), key));
        }
        return result.toString();
    }
    /**
     * 使用给定的密钥加密单个字符。
     */
    public static char encryptCharacter(char input, String key) {
        String binaryInput = String.format("%8s", Integer.toBinaryString(input)).replace(' ', '0');
        String[] keys = keyGeneration(key);
        String encryptedBinary = encrypt(binaryInput, keys[0], keys[1]);
        return (char) Integer.parseInt(encryptedBinary, 2);
    }

    /**
     * 使用给定的密钥解密单个字符。
     */
    public static char decryptCharacter(char input, String key) {
        String binaryInput = String.format("%8s", Integer.toBinaryString(input)).replace(' ', '0');
        String[] keys = keyGeneration(key);
        String decryptedBinary = decrypt(binaryInput, keys[0], keys[1]);
        return (char) Integer.parseInt(decryptedBinary, 2);
    }

    /**
     * 使用给定的密钥加密8位二进制字符串。
     */
    public static String encryptBinary(String binaryInput, String key) {
        if (binaryInput.length() != 8) {
            return "Invalid input. Must be exactly 8 bits.";
        }
        String[] keys = keyGeneration(key);
        return encrypt(binaryInput, keys[0], keys[1]);
    }

    /**
     * 使用给定的密钥解密8位二进制字符串。
     */
    public static String decryptBinary(String binaryInput, String key) {
        if (binaryInput.length() != 8) {
            return "Invalid input. Must be exactly 8 bits.";
        }
        String[] keys = keyGeneration(key);
        return decrypt(binaryInput, keys[0], keys[1]);
    }

    // 内部方法用于加密和解密
    private static String encrypt(String plaintext, String k1, String k2) {
        String ip = permute(plaintext, IP);
        String fk1 = fk(ip, k1);
        String switched = switchFunction(fk1);
        String fk2 = fk(switched, k2);
        return permute(fk2, IP_INV);
    }

    private static String decrypt(String ciphertext, String k1, String k2) {
        String ip = permute(ciphertext, IP);
        String fk2 = fk(ip, k2);
        String switched = switchFunction(fk2);
        String fk1 = fk(switched, k1);
        return permute(fk1, IP_INV);
    }

    // 辅助方法实现加密操作
    private static String fk(String input, String key) {
        String[] leftRight = split(input);
        String left = leftRight[0];
        String right = leftRight[1];
        String epRight = permute(right, EP);
        String xorResult = xor(epRight, key);
        String sboxResult = sbox(xorResult);
        String p4Result = permute(sboxResult, P4);
        String leftXor = xor(left, p4Result);
        return leftXor + right;
    }

    private static String permute(String input, int[] table) {
        StringBuilder output = new StringBuilder();
        for (int index : table) {
            output.append(input.charAt(index - 1));
        }
        return output.toString();
    }

    private static String xor(String s1, String s2) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s1.length(); i++) {
            result.append(s1.charAt(i) == s2.charAt(i) ? '0' : '1');
        }
        return result.toString();
    }

    private static String leftShift(String input, int times) {
        return input.substring(times) + input.substring(0, times);
    }

    private static String[] split(String input) {
        int mid = input.length() / 2;
        return new String[]{input.substring(0, mid), input.substring(mid)};
    }

    private static String sbox(String input) {
        String left = input.substring(0, 4);
        String right = input.substring(4, 8);
        int rowLeft = Integer.parseInt("" + left.charAt(0) + left.charAt(3), 2);
        int colLeft = Integer.parseInt("" + left.charAt(1) + left.charAt(2), 2);
        int rowRight = Integer.parseInt("" + right.charAt(0) + right.charAt(3), 2);
        int colRight = Integer.parseInt("" + right.charAt(1) + right.charAt(2), 2);
        String s0Result = Integer.toBinaryString(S0[rowLeft][colLeft]);
        String s1Result = Integer.toBinaryString(S1[rowRight][colRight]);
        return String.format("%2s", s0Result).replace(' ', '0') +
                String.format("%2s", s1Result).replace(' ', '0');
    }

    private static String switchFunction(String input) {
        String[] leftRight = split(input);
        return leftRight[1] + leftRight[0];
    }
}
