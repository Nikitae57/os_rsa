import java.math.BigInteger;
import java.util.Random;

public class RSA {

    public static void main(String[] args) {
        RSA rsa = new RSA();
        String enc = rsa.encrypt("HEllo, world!",
                new BigInteger(rsa.getPublicExponent()),
                new BigInteger(rsa.getModulus()));

        System.out.println(enc);

        String decr = decrypt(enc,
                new BigInteger(rsa.getPrivateExponent()),
                new BigInteger(rsa.getModulus()));

        System.out.println(decr);
    }

    private static int BITS_NUMBER = 400;
    private BigInteger modulus; // n is the modulus for both the private and public keys
    private BigInteger publicExponent; // e is the exponent of the public key
    private BigInteger privateExponent; // d is the exponent of the private key

    public String getPrivateExponent() {
        return String.valueOf(privateExponent);
    }

    public String getPublicExponent() {
        return String.valueOf(publicExponent);
    }

    public String getModulus() { return String.valueOf(modulus); }

    public RSA() {

        Random rnd = new Random();
        BigInteger p = new BigInteger(BITS_NUMBER,100,rnd);
        BigInteger q = new BigInteger(BITS_NUMBER,100,rnd);

        // Step 2: Compute n by the equation n = p * q.
        modulus = p.multiply(q);

        // Step 3: Compute phi(n) = (p-1) * (q-1)
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        // Step 4: Select a small odd integer e that is relatively prime to phi(n).
        // By convention the prime 65537 is used as the public exponent.
        do publicExponent = new BigInteger(phi.bitLength(), rnd);
        while (publicExponent.compareTo(BigInteger.ONE) <= 0
                || publicExponent.compareTo(phi) >= 0
                || !publicExponent.gcd(phi).equals(BigInteger.ONE));

        privateExponent = publicExponent.modInverse(phi);
    }

    public static String encrypt(String text, BigInteger publicExponent, BigInteger modulus) {
        BigInteger textBytes = new BigInteger(text.getBytes()); // m is the original clear text
        return String.valueOf(textBytes.modPow(publicExponent, modulus)) ;
    }

    public static String decrypt(String text, BigInteger privateExponent, BigInteger modulus) {
        BigInteger decr = (new BigInteger(text)).modPow(privateExponent, modulus);
        return new String(decr.toByteArray());
    }
}