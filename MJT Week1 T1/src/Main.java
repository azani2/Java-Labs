public class Main {
    public static void main(String[] args) {
        System.out.println(IPValidator.validateIPv4Address("192.168.1.1"));
        System.out.println(IPValidator.validateIPv4Address("192.168.1.0"));
        System.out.println(IPValidator.validateIPv4Address("192.168.1.00"));
        System.out.println(IPValidator.validateIPv4Address("192.168@1.1"));
        System.out.println(IPValidator.validateIPv4Address("192.168..1"));
        System.out.println(IPValidator.validateIPv4Address("192.168"));

    }
}