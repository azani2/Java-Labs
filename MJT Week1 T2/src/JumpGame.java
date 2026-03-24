public class JumpGame {
    public static boolean canWin(int[] array) {
        int i = 0;

        while (array[i] != 0 && i < array.length - 1) {
            int current = array[i];

            for (int j = 1; j <= current; j++) {
                //skip jumping on 0 if possible OR jump on 0 if it's the end of the array
                if (i + j < array.length && array[i + j] != 0 || i + j == array.length - 1) {
                    i += j;
                    break;
                }
                //if nothing works make the biggest jump
                if (j == current)
                    i+= j;
            }
        }
        return i == array.length - 1;
    }
}
