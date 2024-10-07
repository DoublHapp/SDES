import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class mainForm {
    private JPanel root;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JTextArea plaintextArea;
    private JTextArea keyArea;
    private JTextArea ciphertextArea;
    private JButton eButton;
    private JButton dButton;
    private JButton keyButton;
    private JTextPane outputPane;

    public mainForm() {
        eButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String plaintext = plaintextArea.getText();
                String key = keyArea.getText();

                if (!keyIsValid(key)) {
                    outputPane.setText("错误：密钥不合法，请输入10位2进制密钥\n");
                    return;
                }

                String[] texts = plaintext.split(",");
                StringBuilder result = new StringBuilder();
                for (String text : texts) {
                    if (isBinary(text) && text.length() % 8 == 0) {
                        result.append(SDES.encryptInputBinary(text, key));
                    } else {
                        result.append(SDES.encryptInputAscii(text, key));
                    }
                    result.append(",");
                }
                result.deleteCharAt(result.length() - 1);
                ciphertextArea.setText(result.toString());
            }
        });

        dButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ciphertext = ciphertextArea.getText();
                String key = keyArea.getText();

                if (!keyIsValid(key)) {
                    outputPane.setText("错误：密钥不合法，请输入10位2进制密钥\n");
                    return;
                }

                String[] texts = ciphertext.split(",");
                StringBuilder result = new StringBuilder();
                for (String text : texts) {
                    if (isBinary(text) && text.length() % 8 == 0) {
                        result.append(SDES.decryptInputBinary(text, key));
                    } else {
                        result.append(SDES.decryptInputAscii(text, key));
                    }
                    result.append(",");
                }
                result.deleteCharAt(result.length() - 1);
                plaintextArea.setText(result.toString());
            }
        });

        keyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String plaintext = plaintextArea.getText();
                String ciphertext = ciphertextArea.getText();

                String[] plaintexts = plaintext.split(",");
                String[] ciphertexts = ciphertext.split(",");

                if (plaintexts.length != ciphertexts.length) {
                    outputPane.setText("错误：明文和密文数量不匹配\n");
                    return;
                }

                List<String> potentialKeys = new ArrayList<>();
                List<String> resultKeys = new ArrayList<>();

                long startTime = System.currentTimeMillis(); // Start timing

                for (int i = 0; i < plaintexts.length; i++) {
                    if(isBinary(plaintexts[i]) && plaintexts[i].length() % 8 == 0) {
                        List<String> tempKeys = BruteForceSDES.crackKeyForInputBinary(plaintexts[i], ciphertexts[i]);
                        potentialKeys.addAll(tempKeys);
                    } else {
                        List<String> tempKeys = BruteForceSDES.crackKeyForInputAscii(plaintexts[i], ciphertexts[i]);
                        potentialKeys.addAll(tempKeys);
                    }
                }

                long endTime = System.currentTimeMillis(); // Stop timing
                long totalTime = endTime - startTime; // Calculate total time

                if(plaintexts.length >= 2) {
                    Map<String, Integer> countMap = new HashMap<>();
                    for (String key : potentialKeys) {
                        countMap.put(key, countMap.getOrDefault(key, 0) + 1);
                    }
                    for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
                        if (entry.getValue() >= 2) {
                            resultKeys.add(entry.getKey());
                        }
                    }
                }else{
                    resultKeys.addAll(potentialKeys);
                }

                if (resultKeys.isEmpty()) {
                    outputPane.setText("没有找到符合要求的密钥, 耗时：" + totalTime + "毫秒");
                } else {
                    StringBuilder output = new StringBuilder("可能的密钥有：");
                    for (String key : resultKeys) {
                        output.append(key).append(", ");
                    }
                    output.setLength(output.length() - 2);
                    output.append("\n耗时：").append(totalTime).append("毫秒");
                    outputPane.setText(output.toString());
                }
            }
        });
    }

    private boolean isBinary(String text) {
        return text.matches("[01]+");
    }

    private boolean keyIsValid(String key) {
        return key.matches("[01]{10}");
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("SDES Cipher Tool");
        mainForm mainForm = new mainForm();
        frame.setContentPane(mainForm.root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
