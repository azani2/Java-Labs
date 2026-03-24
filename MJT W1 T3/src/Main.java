public class Main {
    public static void main(String[] args) {
        System.out.println(BrokenKeyboard.calculateFullyTypedWords("i love mjt", "qsf3o"));
        System.out.println(BrokenKeyboard.calculateFullyTypedWords("   secret      message info      ", "sms"));
        System.out.println(BrokenKeyboard.calculateFullyTypedWords("dve po 2 4isto novi beli kecove", "o2sf"));
        System.out.println(BrokenKeyboard.calculateFullyTypedWords("     ", "asd"));
        System.out.println(BrokenKeyboard.calculateFullyTypedWords(" - 1        @ - 4", "s"));
    }
}