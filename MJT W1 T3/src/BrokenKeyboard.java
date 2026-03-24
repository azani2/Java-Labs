import java.util.Arrays;

public class BrokenKeyboard {
    public static int calculateFullyTypedWords(String message, String brokenKeys) {
        int fullWordsCount = 0;
        String[] words = message.split(" ", 0);

        for (String word : words) {

            boolean fullWord = true;
            int wordLen = word.length();

            //check if word is empty string
            if (wordLen == 0)
                fullWord = false;

            //check if word has tabs
            if (word.indexOf('\t') >= 0)
                fullWord = false;

            //check if word contains a broken key character
            for (int j = 0; j < wordLen; j++) {
                if (brokenKeys.indexOf(word.charAt(j)) >= 0)
                    fullWord = false;
            }

            if (fullWord) {
                fullWordsCount++;
            }
        }

        return fullWordsCount;
    }
}
