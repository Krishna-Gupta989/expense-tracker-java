import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
// import java.awt.event.*;
import java.util.List;

public class ExpenseTrackerGUI extends JFrame {
    private JTextField txtDate, txtCategory, txtAmount, txtNote;
    private JTable table;
    private DefaultTableModel model;
    private ExpenseManager manager;

    public ExpenseTrackerGUI() {
        super("Expense Tracker");
        manager = new ExpenseManager();
        setLayout(new BorderLayout());

        // ----- Input Panel -----
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        txtDate = new JTextField("2025-11-10");
        txtCategory = new JTextField();
        txtAmount = new JTextField();
        txtNote = new JTextField();

        inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(new JLabel("Note:"));

        inputPanel.add(txtDate);
        inputPanel.add(txtCategory);
        inputPanel.add(txtAmount);
        inputPanel.add(txtNote);

        add(inputPanel, BorderLayout.NORTH);

        // ----- Table Panel -----
        model = new DefaultTableModel(new Object[]{"Date", "Category", "Amount", "Note"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // ----- Button Panel -----
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add Expense");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnReload = new JButton("Reload File");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnReload);
        add(buttonPanel, BorderLayout.SOUTH);

        // ----- Load existing data -----
        loadTable();

        // ----- Button Actions -----
        btnAdd.addActionListener(e -> addExpense());
        btnDelete.addActionListener(e -> deleteSelected());
        btnReload.addActionListener(e -> loadTable());

        // Window settings
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addExpense() {
        try {
            String date = txtDate.getText().trim();
            String category = txtCategory.getText().trim();
            double amount = Double.parseDouble(txtAmount.getText().trim());
            String note = txtNote.getText().trim();

            Expense exp = new Expense(date, category, amount, note);
            manager.saveExpense(exp);
            model.addRow(new Object[]{date, category, amount, note});

            txtCategory.setText("");
            txtAmount.setText("");
            txtNote.setText("");

            JOptionPane.showMessageDialog(this, "Expense added!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input! Please check fields.");
        }
    }

    private void loadTable() {
        model.setRowCount(0); // clear old
        List<Expense> list = manager.loadExpenses();
        for (Expense e : list) {
            model.addRow(new Object[]{e.getDate(), e.getCategory(), e.getAmount(), e.getNote()});
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            model.removeRow(row);
            // Save updated file (rewrite all)
            try {
                java.util.List<Expense> all = new java.util.ArrayList<>();
                for (int i = 0; i < model.getRowCount(); i++) {
                    all.add(new Expense(
                            model.getValueAt(i, 0).toString(),
                            model.getValueAt(i, 1).toString(),
                            Double.parseDouble(model.getValueAt(i, 2).toString()),
                            model.getValueAt(i, 3).toString()
                    ));
                }
                // rewrite file
                java.io.FileWriter fw = new java.io.FileWriter("expenses.csv");
                for (Expense e : all) fw.write(e.toCSV() + "\n");
                fw.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Expense deleted!");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenseTrackerGUI::new);
    }
}
