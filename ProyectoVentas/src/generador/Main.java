package generador;

import java.io.*;
import java.util.*;

/**
 * Clase que procesa los archivos generados en data/
 * para crear reportes de ventas de vendedores y productos.
 */
public class Main {

    private static final String DATA_FOLDER = "data";
    private static final String SALESMEN_FILE = "salesmen.txt";
    private static final String PRODUCTS_FILE = "products.txt";

    private static final String REPORT_SALESMEN = "report_salesmen.csv";
    private static final String REPORT_PRODUCTS = "report_products.csv";

    public static void main(String[] args) {
        try {
            // 1. Cargar vendedores
            Map<Long, String> salesmen = loadSalesmen();

            // 2. Cargar productos
            Map<String, Product> products = loadProducts();

            // 3. Procesar ventas de cada archivo sales_ID.txt
            Map<Long, Double> salesBySalesman = new HashMap<>();
            Map<String, Integer> salesByProduct = new HashMap<>();
            processSales(products, salesBySalesman, salesByProduct);

            // 4. Generar reporte de vendedores
            generateSalesmenReport(salesmen, salesBySalesman);

            // 5. Generar reporte de productos
            generateProductsReport(products, salesByProduct);

            System.out.println("✅ Reportes generados exitosamente en la carpeta data/");
        } catch (Exception e) {
            System.err.println("❌ Error al generar reportes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------------- MÉTODOS AUXILIARES ----------------

    private static Map<Long, String> loadSalesmen() throws IOException {
        Map<Long, String> salesmen = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(DATA_FOLDER, SALESMEN_FILE)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 4) {
                    long id = Long.parseLong(parts[1]);
                    String nombre = parts[2] + " " + parts[3];
                    salesmen.put(id, nombre);
                }
            }
        }
        return salesmen;
    }

    private static Map<String, Product> loadProducts() throws IOException {
        Map<String, Product> products = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(DATA_FOLDER, PRODUCTS_FILE)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    String codigo = parts[0]; // Ej: "P001"
                    String nombre = parts[1];
                    double precio = Double.parseDouble(parts[2]);
                    products.put(codigo, new Product(codigo, nombre, precio));
                }
            }
        }
        return products;
    }

    private static void processSales(Map<String, Product> products,
                                     Map<Long, Double> salesBySalesman,
                                     Map<String, Integer> salesByProduct) throws IOException {

        File folder = new File(DATA_FOLDER);
        File[] files = folder.listFiles((dir, name) -> name.startsWith("sales_") && name.endsWith(".txt"));

        if (files == null) return;

        for (File file : files) {
            // Obtener el ID del vendedor desde el nombre del archivo
            String filename = file.getName(); // Ej: sales_1001.txt
            long idVendedor = Long.parseLong(filename.replace("sales_", "").replace(".txt", ""));

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts.length == 2) {
                        String codigo = parts[0]; // Ej: "P005"
                        int cantidad = Integer.parseInt(parts[1]);

                        if (products.containsKey(codigo)) {
                            double total = products.get(codigo).precio * cantidad;

                            // Sumar al vendedor
                            salesBySalesman.put(idVendedor,
                                    salesBySalesman.getOrDefault(idVendedor, 0.0) + total);

                            // Sumar al producto
                            salesByProduct.put(codigo,
                                    salesByProduct.getOrDefault(codigo, 0) + cantidad);
                        }
                    }
                }
            }
        }
    }

    private static void generateSalesmenReport(Map<Long, String> salesmen,
                                               Map<Long, Double> salesBySalesman) throws IOException {
        List<Map.Entry<Long, Double>> list = new ArrayList<>(salesBySalesman.entrySet());
        list.sort((a, b) -> Double.compare(b.getValue(), a.getValue())); // Descendente

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(DATA_FOLDER, REPORT_SALESMEN)))) {
            for (Map.Entry<Long, Double> entry : list) {
                String nombre = salesmen.getOrDefault(entry.getKey(), "Desconocido");
                writer.write(nombre + ";" + entry.getValue());
                writer.newLine();
            }
        }
    }

    private static void generateProductsReport(Map<String, Product> products,
                                               Map<String, Integer> salesByProduct) throws IOException {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(salesByProduct.entrySet());
        list.sort((a, b) -> Integer.compare(b.getValue(), a.getValue())); // Descendente

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(DATA_FOLDER, REPORT_PRODUCTS)))) {
            for (Map.Entry<String, Integer> entry : list) {
                Product p = products.get(entry.getKey());
                writer.write(p.nombre + ";" + p.precio + ";" + entry.getValue());
                writer.newLine();
            }
        }
    }
}

// Clase auxiliar para productos
class Product {
    String codigo;
    String nombre;
    double precio;

    public Product(String codigo, String nombre, double precio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
    }
}
