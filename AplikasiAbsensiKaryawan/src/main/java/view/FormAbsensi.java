package view;

import config.Koneksi;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormAbsensi extends JFrame {

    // Deklarasi Komponen UI
    private JTextField txtTanggal;
    private JComboBox<String> cbStatus, cbNamaKaryawan; // Mengganti txtIdKaryawan jadi cbNamaKaryawan
    private JButton btnSimpan, btnHapus, btnReset, btnKembali;
    private JTable tabelAbsensi;
    private DefaultTableModel modelTabel;

    public FormAbsensi() {
        setTitle("Input Data Absensi");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- PANEL HEADER ---
        JLabel lblHeader = new JLabel("Form Input Absensi", JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(lblHeader, BorderLayout.NORTH);

        // --- PANEL KIRI (FORM INPUT) ---
        JPanel panelForm = new JPanel(new GridLayout(3, 2, 10, 15));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panelForm.add(new JLabel("Pilih Karyawan:"));
        cbNamaKaryawan = new JComboBox<>();
        isiComboKaryawan(); // Isi data dropdown saat form dibuka
        panelForm.add(cbNamaKaryawan);

        panelForm.add(new JLabel("Tanggal (YYYY-MM-DD):"));
        txtTanggal = new JTextField();
        txtTanggal.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        panelForm.add(txtTanggal);

        panelForm.add(new JLabel("Status Kehadiran:"));
        cbStatus = new JComboBox<>(new String[]{"Hadir", "Sakit", "Izin", "Alfa"});
        panelForm.add(cbStatus);

        // --- PANEL TOMBOL ---
        JPanel panelTombol = new JPanel(new GridLayout(1, 3, 10, 0));
        btnSimpan = new JButton("Simpan");
        btnHapus = new JButton("Hapus");
        btnReset = new JButton("Reset");
        panelTombol.add(btnSimpan);
        panelTombol.add(btnHapus);
        panelTombol.add(btnReset);

        JPanel panelAksiAtas = new JPanel(new BorderLayout(0, 15));
        panelAksiAtas.add(panelForm, BorderLayout.NORTH);
        panelAksiAtas.add(panelTombol, BorderLayout.CENTER);

        JPanel panelKiri = new JPanel(new BorderLayout());
        panelKiri.add(panelAksiAtas, BorderLayout.NORTH);

       // Tombol Kembali
        btnKembali = new JButton("Kembali");
        btnKembali.setBackground(new Color(220, 53, 69)); // Merah
        btnKembali.setForeground(Color.WHITE);
        btnKembali.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnKembali.setPreferredSize(new Dimension(180, 35));
        JPanel panelBawahKiri = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        panelBawahKiri.add(btnKembali);
        panelKiri.add(panelBawahKiri, BorderLayout.SOUTH);

        add(panelKiri, BorderLayout.WEST);

        // --- PANEL KANAN (TABEL) ---
        modelTabel = new DefaultTableModel(new String[]{"ID Absen", "ID Karyawan", "Tanggal", "Status"}, 0);
        tabelAbsensi = new JTable(modelTabel);
        add(new JScrollPane(tabelAbsensi), BorderLayout.CENTER);

        loadData();
        aksiTombol();
    }

    // Fungsi mengambil nama karyawan untuk Dropdown
    private void isiComboKaryawan() {
        try {
            Connection conn = Koneksi.configDB();
            Statement stm = conn.createStatement();
            ResultSet res = stm.executeQuery("SELECT nama FROM karyawan");
            while (res.next()) {
                cbNamaKaryawan.addItem(res.getString("nama"));
            }
        } catch (Exception e) {
            System.out.println("Gagal load combo: " + e.getMessage());
        }
    }

    // Fungsi mencari ID Karyawan dari Nama (untuk proses simpan)
    private String getIdKaryawan(String nama) {
        String id = "";
        try {
            Connection conn = Koneksi.configDB();
            Statement stm = conn.createStatement();
            ResultSet res = stm.executeQuery("SELECT id_karyawan FROM karyawan WHERE nama = '" + nama + "'");
            if (res.next()) id = res.getString("id_karyawan");
        } catch (Exception e) { e.printStackTrace(); }
        return id;
    }

    private void loadData() {
        modelTabel.setRowCount(0);
        try {
            Connection conn = Koneksi.configDB();
            // Urutkan ID agar rapi
            ResultSet res = conn.createStatement().executeQuery("SELECT * FROM kehadiran ORDER BY id_absensi ASC");
            while (res.next()) {
                modelTabel.addRow(new Object[]{
                    res.getString("id_absensi"), res.getString("id_karyawan"), 
                    res.getString("tanggal"), res.getString("status")
                });
            }
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }

    private void resetForm() {
        cbNamaKaryawan.setSelectedIndex(0);
        txtTanggal.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }

    private void aksiTombol() {
        // Klik Tabel (Mapping balik ID ke Nama)
        tabelAbsensi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int baris = tabelAbsensi.rowAtPoint(evt.getPoint());
                String idKaryawanTabel = tabelAbsensi.getValueAt(baris, 1).toString();
                
                // Cari nama berdasarkan ID untuk ditampilkan di ComboBox
                try {
                    Connection conn = Koneksi.configDB();
                    ResultSet res = conn.createStatement().executeQuery("SELECT nama FROM karyawan WHERE id_karyawan = '" + idKaryawanTabel + "'");
                    if (res.next()) cbNamaKaryawan.setSelectedItem(res.getString("nama"));
                } catch (Exception e) { e.printStackTrace(); }
                
                txtTanggal.setText(tabelAbsensi.getValueAt(baris, 2).toString());
                cbStatus.setSelectedItem(tabelAbsensi.getValueAt(baris, 3).toString());
            }
        });

        // Simpan
        btnSimpan.addActionListener(e -> {
            String idKaryawan = getIdKaryawan(cbNamaKaryawan.getSelectedItem().toString());
            try {
                Connection conn = Koneksi.configDB();
                String sql = "INSERT INTO kehadiran (id_karyawan, tanggal, status) VALUES ('"
                        + idKaryawan + "', '" + txtTanggal.getText() + "', '" + cbStatus.getSelectedItem() + "')";
                conn.prepareStatement(sql).execute();
                JOptionPane.showMessageDialog(null, "Berhasil!");
                loadData();
                resetForm();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage()); }
        });

        // Hapus
        btnHapus.addActionListener(e -> {
            String idKaryawan = getIdKaryawan(cbNamaKaryawan.getSelectedItem().toString());
            try {
                Connection conn = Koneksi.configDB();
                String sql = "DELETE FROM kehadiran WHERE id_karyawan='" + idKaryawan + "' AND tanggal='" + txtTanggal.getText() + "'";
                conn.prepareStatement(sql).execute();
                JOptionPane.showMessageDialog(null, "Dihapus!");
                loadData();
                resetForm();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage()); }
        });

        btnReset.addActionListener(e -> resetForm());
        btnKembali.addActionListener(e -> { new Mainmenu().setVisible(true); dispose(); });
    }
}