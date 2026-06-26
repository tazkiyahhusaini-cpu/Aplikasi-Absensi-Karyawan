package view;

import javax.swing.*;
import java.awt.*;

public class Mainmenu extends JFrame {

    public Mainmenu() {
        setTitle("Menu Utama - Aplikasi Absensi");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // PANEL HEADER
        JLabel lblHeader = new JLabel("Dashboard Absensi", JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblHeader, BorderLayout.NORTH);

        // PANEL MENU (GRID LAYOUT)
        JPanel panelMenu = new JPanel(new GridLayout(2, 2, 20, 20));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));

        // Membuat tombol menu
        panelMenu.add(buatTombolMenu("Data Karyawan"));
        panelMenu.add(buatTombolMenu("Input Absensi"));
        panelMenu.add(buatTombolMenu("Laporan"));
        panelMenu.add(buatTombolMenu("Logout"));

        add(panelMenu, BorderLayout.CENTER);
    }

    // Fungsi pembantu agar kodingan tidak berulang-ulang
    private JButton buatTombolMenu(String nama) {
        JButton btn = new JButton(nama);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI()); // Memaksa tombol menampilkan warna solid (tidak transparan)
        btn.setBackground(new Color(25, 118, 210)); // Biru
        btn.setForeground(Color.WHITE);

        // Aksi tombol
        btn.addActionListener(e -> {
            if (nama.equals("Logout")) {
                new FormLogin().setVisible(true);
                this.dispose();
            } else if (nama.equals("Data Karyawan")) {
                new FormDataKaryawan().setVisible(true);
                this.dispose();
            } else if (nama.equals("Input Absensi")) {
                new FormAbsensi().setVisible(true);
                this.dispose();
            } else if (nama.equals("Laporan")) {
                new FormLaporan().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Fitur " + nama + " belum tersedia.");
            }
        });
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Mainmenu().setVisible(true));
    }
}
