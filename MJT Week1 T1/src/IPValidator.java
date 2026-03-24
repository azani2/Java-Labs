public class IPValidator {
    public static boolean validateIPv4Address(String str) {
        String[] ipAddressOctets = str.split("\\.");

        if (ipAddressOctets.length != 4) //check if ip address has 4 octets
            return false;

        for (int i = 0; i < 4; i++) {
            String octet = ipAddressOctets[i];
            int length = octet.length();

            if (length == 0)// check if there is two consecutive dots
                return false;

            if (length >= 2 && octet.charAt(0) == '0')//check if octet starts with 0
                return false;

            for (int j = 0; j < length; j++) { //check if all chars are digits
                char c = octet.charAt(j);
                if (!Character.isDigit(c))
                    return false;
            }

            if (Integer.parseInt(octet) < 0 ||
                Integer.parseInt(octet) > 255)//check if all numbers are between 0 and 255
                return false;
        }

        return true;
    }
}