import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.JSONObject;

public class ShamirSecretFinder {

    public static BigInteger lagrangeInterpolationAtZero(List<Map.Entry<Integer, BigInteger>> points) {
        int k = points.size();
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            BigInteger xi = BigInteger.valueOf(points.get(i).getKey());
            BigInteger yi = points.get(i).getValue();

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i == j)
                    continue;
                BigInteger xj = BigInteger.valueOf(points.get(j).getKey());
                numerator = numerator.multiply(xj.negate()); // (-xj)
                denominator = denominator.multiply(xi.subtract(xj)); // (xi - xj)
            }

            // Compute yi * (numerator / denominator)
            BigInteger term = yi.multiply(numerator).divide(denominator);
            result = result.add(term);
        }

        return result;
    }

    public static BigInteger computeSecretFromJSON(String filename) throws Exception {
        String jsonContent = new String(Files.readAllBytes(Paths.get(filename)));
        JSONObject obj = new JSONObject(jsonContent);

        int k = obj.getJSONObject("keys").getInt("k");

        TreeMap<Integer, BigInteger> points = new TreeMap<>();
        for (String key : obj.keySet()) {
            if (key.equals("keys"))
                continue;

            int x = Integer.parseInt(key);
            JSONObject pointObj = obj.getJSONObject(key);
            int base = Integer.parseInt(pointObj.getString("base"));
            BigInteger y = new BigInteger(pointObj.getString("value"), base);

            points.put(x, y);
        }

        List<Map.Entry<Integer, BigInteger>> selected = new ArrayList<>(points.entrySet()).subList(0, k);
        return lagrangeInterpolationAtZero(selected);
    }

    public static void main(String[] args) {
        try {
            BigInteger secret1 = computeSecretFromJSON("input1.json");
            BigInteger secret2 = computeSecretFromJSON("input2.json");

            System.out.println("Secret from Test Case 1: " + secret1);
            System.out.println("Secret from Test Case 2: " + secret2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
