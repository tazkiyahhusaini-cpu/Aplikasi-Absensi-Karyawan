package view;

import config.Koneksi;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FormLaporan extends JFrame {
    private JTable tabelLaporan;
    private DefaultTableModel modelTabel;
    private JButton btnKembali;

    public FormLaporan() {
        super("Form Laporan Absensi");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. Setup Tabel (di Tengah)
        modelTabel = new DefaultTableModel(new String[]{"Tanggal", "Nama Karyawan", "Status"}, 0);
        tabelLaporan = new JTable(modelTabel);
        add(new JScrollPane(tabelLaporan), BorderLayout.CENTER);

        // 2. Setup Tombol Kembali (di Bawah)
        btnKembali = new JButton("Kembali");
        btnKembali.setBackground(Color.RED); // Merah
        btnKembali.setForeground(Color.WHITE);
        btnKembali.setFocusPainted(false);
        // Ukuran tetap agar tidak melar mengikuti lebar layar
        btnKembali.setPreferredSize(new Dimension(120, 40)); 

        // 3. Panel Bawah (Menggunakan FlowLayout agar tombol rapi di kiri)
        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBawah.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Memberi jarak tepi
        panelBawah.add(btnKembali);

        // Tambahkan ke Frame di posisi SOUTH
        add(panelBawah, BorderLayout.SOUTH);

        // 4. Aksi Tombol
        btnKembali.addActionListener(e -> {
            new Mainmenu().setVisible(true);
            this.dispose();
        });

        // Panggil data
        loadData();
    }

    private void loadData() {
        modelTabel.setRowCount(0);
        try {
            Connection conn = Koneksi.configDB();
            String sql = "SELECT h.tanggal, k.nama, h.status " +
                         "FROM kehadiran h " +
                         "INNER JOIN karyawan k ON h.id_karyawan = k.id_karyawan " +
                         "ORDER BY h.tanggal DESC";
            
            Statement stm = conn.createStatement();
            ResultSet res = stm.executeQuery(sql);
            
            while (res.next()) {
                modelTabel.addRow(new Object[]{
                    res.getString("tanggal"),
                    res.getString("nama"),
                    res.getString("status")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat laporan: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new FormLaporan().setVisible(true);
    }
}