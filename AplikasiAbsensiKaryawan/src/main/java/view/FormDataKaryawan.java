package view;

import config.Koneksi; // Memanggil koneksi database Anda
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FormDataKaryawan extends JFrame {

    // Deklarasi Komponen UI
    private JTextField txtNip, txtNama, txtJabatan;
    private JButton btnSimpan, btnUbah, btnHapus, btnReset, btnKembali;
    private JTable tabelKaryawan;
    private DefaultTableModel modelTabel;

    public FormDataKaryawan() {
        setTitle("Kelola Data Karyawan");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- PANEL HEADER ---
        JLabel lblHeader = new JLabel("Form Data Karyawan", JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(lblHeader, BorderLayout.NORTH);

        // --- PANEL KIRI (FORM INPUT) ---
        JPanel panelForm = new JPanel(new GridLayout(4, 2, 10, 15));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panelForm.add(new JLabel("NIP:"));
        txtNip = new JTextField();
        panelForm.add(txtNip);

        panelForm.add(new JLabel("Nama Karyawan:"));
        txtNama = new JTextField();
        panelForm.add(txtNama);

        panelForm.add(new JLabel("Jabatan:"));
        txtJabatan = new JTextField();
        panelForm.add(txtJabatan);

        // --- PANEL BAWAH FORM (TOMBOL AKSI) ---
        JPanel panelTombol = new JPanel(new GridLayout(1, 4, 10, 0));
        btnSimpan = new JButton("Simpan");
        btnUbah = new JButton("Ubah");
        btnHapus = new JButton("Hapus");
        btnReset = new JButton("Reset");

        panelTombol.add(btnSimpan);
        panelTombol.add(btnUbah);
        panelTombol.add(btnHapus);
        panelTombol.add(btnReset);

        // --- BUNGKUS FORM & TOMBOL AGAR TIDAK MELAR ---
        // Kita buat panel bantuan agar form dan tombol menyatu di bagian atas
        JPanel panelAksiAtas = new JPanel(new BorderLayout(0, 15));
        panelAksiAtas.add(panelForm, BorderLayout.NORTH);
        panelAksiAtas.add(panelTombol, BorderLayout.CENTER);

        // Gabungkan ke panel kiri utama
        JPanel panelKiri = new JPanel(new BorderLayout());
        panelKiri.add(panelAksiAtas, BorderLayout.NORTH); // Taruh bungkusan tadi di posisi ATAS (NORTH)

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

        // --- PANEL KANAN (TABEL DATA) ---
        modelTabel = new DefaultTableModel(new String[]{"ID", "NIP", "Nama", "Jabatan"}, 0);
        tabelKaryawan = new JTable(modelTabel);
        JScrollPane scrollTabel = new JScrollPane(tabelKaryawan);
        scrollTabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 20));
        add(scrollTabel, BorderLayout.CENTER);

        // --- PANGGIL FUNGSI ---
        loadData(); // Tampilkan data saat form dibuka
        aksiTombol(); // Aktifkan fungsi klik tombol
    }

    // Fungsi untuk mengambil data dari Database ke Tabel
    public void loadData() {
        // 1. Bersihkan tabel sebelum diisi ulang agar tidak menumpuk
        modelTabel.setRowCount(0);

        try {
            Connection conn = Koneksi.configDB();
            Statement stm = conn.createStatement();

            // 2. Gunakan ORDER BY id_absensi ASC agar ID 1 selalu di atas
            // Ubah kodingan di baris 101 menjadi:
            String sql = "SELECT * FROM karyawan ORDER BY id_karyawan ASC";
            ResultSet res = stm.executeQuery(sql);

            while (res.next()) {
               modelTabel.addRow(new Object[]{
                res.getString("id_karyawan"),
                res.getString("nip"),
                res.getString("nama"),
                res.getString("jabatan")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
        }
    }

    // Fungsi untuk membersihkan form input
    private void resetForm() {
        txtNip.setText("");
        txtNama.setText("");
        txtJabatan.setText("");
        txtNip.requestFocus();
    }

    // Kumpulan aksi ketika tombol diklik
    private void aksiTombol() {
        // Aksi Klik Tabel (Mengisi form dari tabel yang diklik)
        tabelKaryawan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int baris = tabelKaryawan.rowAtPoint(evt.getPoint());
                txtNip.setText(tabelKaryawan.getValueAt(baris, 1).toString());
                txtNama.setText(tabelKaryawan.getValueAt(baris, 2).toString());
                txtJabatan.setText(tabelKaryawan.getValueAt(baris, 3).toString());
            }
        });

        // Aksi Tombol Simpan
        btnSimpan.addActionListener(e -> {
            try {
                Connection conn = Koneksi.configDB();
                String sql = "INSERT INTO karyawan (nip, nama, jabatan) VALUES ('" + txtNip.getText() + "', '" + txtNama.getText() + "', '" + txtJabatan.getText() + "')";
                PreparedStatement pstm = conn.prepareStatement(sql);
                pstm.execute();
                JOptionPane.showMessageDialog(null, "Data Berhasil Disimpan!");
                loadData();
                resetForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal Menyimpan: " + ex.getMessage());
            }
        });

        // Aksi Tombol Ubah (Berdasarkan NIP)
        btnUbah.addActionListener(e -> {
            try {
                Connection conn = Koneksi.configDB();
                String sql = "UPDATE karyawan SET nama='" + txtNama.getText() + "', jabatan='" + txtJabatan.getText() + "' WHERE nip='" + txtNip.getText() + "'";
                PreparedStatement pstm = conn.prepareStatement(sql);
                pstm.execute();
                JOptionPane.showMessageDialog(null, "Data Berhasil Diubah!");
                loadData();
                resetForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal Mengubah: " + ex.getMessage());
            }
        });

        // Aksi Tombol Hapus (Berdasarkan NIP)
        btnHapus.addActionListener(e -> {
            try {
                Connection conn = Koneksi.configDB();
                String sql = "DELETE FROM karyawan WHERE nip='" + txtNip.getText() + "'";
                PreparedStatement pstm = conn.prepareStatement(sql);
                pstm.execute();
                JOptionPane.showMessageDialog(null, "Data Berhasil Dihapus!");
                loadData();
                resetForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal Menghapus: " + ex.getMessage());
            }
        });

        // Aksi Tombol Reset
        btnReset.addActionListener(e -> resetForm());

        // Aksi Tombol Kembali
        btnKembali.addActionListener(e -> {
            new Mainmenu().setVisible(true); // Buka kembali menu utama
            this.dispose(); // Tutup form karyawan
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormDataKaryawan().setVisible(true));
    }
}
