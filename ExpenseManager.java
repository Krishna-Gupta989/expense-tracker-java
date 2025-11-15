import java.io.*;
import java.util.*;

public class ExpenseManager {
    private final String fileName = "expenses.csv";

    // Save expense (append)
    public void saveExpense(Expense exp) {
        try (FileWriter fw = new FileWriter(fileName, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(exp.toCSV());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    // Load all expenses
    public List<Expense> loadExpenses() {
        List<Expense> list = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 4) {
                    String date = parts[0];
                    String category = parts[1];
                    double amount = Double.parseDouble(parts[2]);
                    String note = parts[3];
                    list.add(new Expense(date, category, amount, note));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return list;
    }
}
