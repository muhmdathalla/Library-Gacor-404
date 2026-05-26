package report;

import java.awt.Component;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.Transaction;

public class ReportGenerator {

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void exportTransactions(Component parent, List<Transaction> txs, String format) {
        String ext = switch (format.toUpperCase()) {
            case "CSV" -> "csv";
            case "PDF" -> "pdf";
            default    -> "txt";
        };

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan Laporan Transaksi");
        chooser.setSelectedFile(new File("laporan_transaksi_gacor404." + ext));
        chooser.setFileFilter(new FileNameExtensionFilter("File " + ext.toUpperCase(), ext));

        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        String path = chooser.getSelectedFile().getAbsolutePath();
        if (!path.toLowerCase().endsWith("." + ext)) path += "." + ext;

        try {
            switch (format.toUpperCase()) {
                case "CSV" -> exportCsv(path, txs);
                case "PDF" -> exportPdf(path, txs);
                default    -> exportTxt(path, txs);
            }
            JOptionPane.showMessageDialog(parent,
                    "Laporan berhasil disimpan:\n" + path,
                    "Ekspor Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent,
                    "Gagal mengekspor laporan: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportTxt(String path, List<Transaction> txs) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(path, StandardCharsets.UTF_8))) {
            w.write("LAPORAN TRANSAKSI PERPUSTAKAAN DIGITAL GACOR404");
            w.newLine(); w.write("Dicetak: " + LocalDateTime.now().format(DT));
            w.newLine(); w.write("=".repeat(95)); w.newLine();
            w.write(String.format("%-6s %-22s %-30s %-12s %-12s %-10s %10s%n",
                    "ID", "Anggota", "Buku", "Pinjam", "Kembali", "Status", "Denda"));
            w.write("-".repeat(95)); w.newLine();
            double total = 0;
            for (Transaction t : txs) {
                w.write(String.format("%-6d %-22s %-30s %-12s %-12s %-10s %10.0f%n",
                        t.getTransactionId(), safe(t.getMemberName()), safe(t.getBookTitle()),
                        t.getBorrowDate(),
                        t.getReturnDate() != null ? t.getReturnDate() : "-",
                        t.getStatus(), t.getFine()));
                total += t.getFine();
            }
            w.write("=".repeat(95)); w.newLine();
            w.write(String.format("Total transaksi: %d  |  Total denda: Rp %.0f%n",
                    txs.size(), total));
        }
    }

    private void exportCsv(String path, List<Transaction> txs) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(path, StandardCharsets.UTF_8))) {
            w.write("transaction_id,member_name,book_title,borrow_date,return_date,status,fine");
            w.newLine();
            for (Transaction t : txs) {
                w.write(String.format("%d,%s,%s,%s,%s,%s,%.2f%n",
                        t.getTransactionId(), esc(t.getMemberName()), esc(t.getBookTitle()),
                        t.getBorrowDate(),
                        t.getReturnDate() != null ? t.getReturnDate() : "",
                        t.getStatus(), t.getFine()));
            }
        }
    }

    private void exportPdf(String path, List<Transaction> txs) throws IOException {
        // Simple raw PDF (no external lib needed)
        StringBuilder content = new StringBuilder();
        content.append("BT\n/F1 13 Tf\n50 800 Td (Laporan Transaksi - Perpustakaan Gacor404) Tj\nET\n");
        content.append("BT\n/F1 9 Tf\n50 780 Td (Dicetak: ").append(LocalDateTime.now().format(DT)).append(") Tj\nET\n");
        int y = 755;
        // Header line
        content.append("BT\n/F1 9 Tf\n50 ").append(y)
               .append(" Td (ID   Anggota              Buku                          Status    Denda) Tj\nET\n");
        y -= 16;
        for (Transaction t : txs) {
            if (y < 50) break;
            String line = String.format("%-5d %-20s %-29s %-10s %.0f",
                    t.getTransactionId(), safe(t.getMemberName()),
                    safe(t.getBookTitle()), t.getStatus(), t.getFine());
            if (line.length() > 90) line = line.substring(0, 90);
            content.append("BT\n/F1 8 Tf\n50 ").append(y)
                   .append(" Td (").append(pdfEsc(line)).append(") Tj\nET\n");
            y -= 13;
        }

        int len = content.length();
        String pdf = "%PDF-1.4\n"
                + "1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n"
                + "2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj\n"
                + "3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 595 842]"
                + "/Contents 4 0 R/Resources<</Font<</F1 5 0 R>>>>>>endobj\n"
                + "4 0 obj<</Length " + len + ">>stream\n"
                + content + "endstream\nendobj\n"
                + "5 0 obj<</Type/Font/Subtype/Type1/BaseFont/Courier>>endobj\n"
                + "xref\n0 6\n0000000000 65535 f \n"
                + "trailer<</Size 6/Root 1 0 R>>\nstartxref\n0\n%%EOF\n";

        try (FileWriter fw = new FileWriter(path, StandardCharsets.ISO_8859_1)) { fw.write(pdf); }
    }

    private String safe(String v) { return v == null ? "-" : v; }
    private String esc(String v)  {
        if (v == null) return "";
        return "\"" + v.replace("\"", "\"\"") + "\"";
    }
    private String pdfEsc(String t) {
        return t.replace("\\","\\\\").replace("(","\\(").replace(")","\\)");
    }
}
