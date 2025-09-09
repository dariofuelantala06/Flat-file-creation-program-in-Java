package generador;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenerateInfoFiles {

    private static final String DATA_FOLDER = "data"; // Folder where the files will be saved
    private static final Random random = new Random();

    public static void main(String[] args) {
        // When you run the program, the sample files are generated

        try {
            createDataFolder();

          // Generate seller file (5 sellers)
            createSalesManInfoFile(5);

            // Generate products file (10 products)
            createProductsFile(10);

            // Generate sales file for each salesperson

            createSalesMenFile(5, "CC", 1001L);

            System.out.println("✅ Files generated in the 'data/' folder");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the "data" folder if it doesn't exist

     */
    private static void createDataFolder() {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    /**
     * Generates a file with vendor information
     * Format: DOC_TYPE; ID; FIRST_NAME; LAST_NAME
     */
    public static void createSalesManInfoFile(int salesmanCount) throws IOException {
        File file = new File(DATA_FOLDER, "salesmen.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 1; i <= salesmanCount; i++) {
                String docType = (i % 2 == 0) ? "CC" : "TI";
                long id = 1000 + i;
                String name = "Seller" + i;
                String lastName = "lastName" + i;
                writer.write(docType + ";" + id + ";" + name + ";" + lastName);
                writer.newLine();
            }
        }
    }

    /**
    * Generates a file with product information
    * Format: CODE;NAME;PRICE
    */
    public static void createProductsFile(int productsCount) throws IOException {
        File file = new File(DATA_FOLDER, "products.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 1; i <= productsCount; i++) {
                String code = "P" + String.format("%03d", i);
                String name = "Product" + i;
                int price = 1000 + random.nextInt(5000);
                writer.write(code + ";" + name + ";" + price);
                writer.newLine();
            }
        }
    }

     /**
     * Generates sales files for each salesperson
     * Format: PRODUCT_CODE;QUANTITY
     */
    public static void createSalesMenFile(int salesmenCount, String docType, long baseId) throws IOException {
        for (int i = 0; i < salesmenCount; i++) {
            long id = baseId + i;
            File file = new File(DATA_FOLDER, "sales_" + id + ".txt");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                int salesCount = 1 + random.nextInt(5); // number of random sales
                for (int j = 1; j <= salesCount; j++) {
                    String code = "P" + String.format("%03d", 1 + random.nextInt(10));
                    int amount = 1 + random.nextInt(10);
                    writer.write(code + ";" + amount);
                    writer.newLine();
                }
            }
        }
    }
}
