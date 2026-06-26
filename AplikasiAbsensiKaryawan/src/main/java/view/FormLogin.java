package view;

import config.Koneksi;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class FormLogin extends JFrame {

    // Deklarasi Komponen UI
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JToggleButton btnTogglePassword;

    // Objek Ikon Gambar
    private ImageIcon iconOpenEye;
    private ImageIcon iconBlindEye;

    public FormLogin() {
        buatIkonGambarOtomatis(); // Memanggil pembuat ikon otomatis
        initComponents();
    }

    // ================= FITUR SPESIAL: PEMBUAT IKON OTOMATIS =================
    private void buatIkonGambarOtomatis() {
        // 1. Menggambar Ikon Mata Terbuka
        BufferedImage imgOpen = new BufferedImage(22, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gOpen = imgOpen.createGraphics();
        gOpen.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gOpen.setColor(Color.GRAY);
        gOpen.drawOval(1, 2, 18, 12);
        gOpen.fillOval(7, 5, 6, 6);
        gOpen.dispose();
        iconOpenEye = new ImageIcon(imgOpen);

        // 2. Menggambar Ikon Mata Tertutup (Dicoret)
        BufferedImage imgBlind = new BufferedImage(22, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gBlind = imgBlind.createGraphics();
        gBlind.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gBlind.setColor(Color.GRAY);
        gBlind.drawOval(1, 2, 18, 12);
        gBlind.fillOval(7, 5, 6, 6);
        gBlind.setStroke(new BasicStroke(2f));
        gBlind.setColor(new Color(220, 53, 69)); // Warna merah coretan
        gBlind.drawLine(2, 14, 18, 2);
        gBlind.dispose();
        iconBlindEye = new ImageIcon(imgBlind);
    }
    // =======================================================================

    private void initComponents() {
        setTitle("Login Sistem Absensi");
        setSize(360, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // PANEL UTAMA
        JPanel panelUtama = new JPanel();
        panelUtama.setBackground(Color.WHITE);
        panelUtama.setLayout(null);

        JLabel lblJudul = new JLabel("Selamat Datang Pengguna");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblJudul.setBounds(0, 30, 345, 30);
        lblJudul.setHorizontalAlignment(SwingConstants.CENTER);
        panelUtama.add(lblJudul);

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUsername.setBounds(50, 90, 100, 20);
        panelUtama.add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(50, 110, 240, 35);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(0, 5, 0, 0)));
        panelUtama.add(txtUsername);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPassword.setBounds(50, 160, 100, 20);
        panelUtama.add(lblPassword);

        // KOTAK INPUT PASSWORD & IKON MATA
        JPanel panelPassword = new JPanel();
        panelPassword.setLayout(new BorderLayout());
        panelPassword.setBounds(50, 180, 240, 35);
        panelPassword.setBackground(Color.WHITE);
        panelPassword.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        txtPassword = new JPasswordField();
        txtPassword.setEchoChar('\u2022');
        txtPassword.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        panelPassword.add(txtPassword, BorderLayout.CENTER);

        btnTogglePassword = new JToggleButton();
        btnTogglePassword.setIcon(iconBlindEye); // Default tertutup
        btnTogglePassword.setSelectedIcon(iconOpenEye); // Otomatis ganti ke terbuka saat ditekan
        btnTogglePassword.setFocusPainted(false);
        btnTogglePassword.setBorderPainted(false);
        btnTogglePassword.setContentAreaFilled(false);
        btnTogglePassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTogglePassword.setMargin(new Insets(0, 0, 0, 5));
        panelPassword.add(btnTogglePassword, BorderLayout.EAST);

        // LOGIKA MATA TERBUKA / TERTUTUP
        btnTogglePassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnTogglePassword.isSelected()) {
                    txtPassword.setEchoChar((char) 0);
                } else {
                    txtPassword.setEchoChar('\u2022');
                }
            }
        });

        panelUtama.add(panelPassword);

        // TOMBOL LOGIN (ORANYE)
        btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(204, 102, 0)); // Oranye gelap
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setUI(new javax.swing.plaf.basic.BasicButtonUI()); // Kebal dari tema Windows
        btnLogin.setBounds(50, 250, 240, 40);
        panelUtama.add(btnLogin);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prosesLogin();
            }
        });

        add(panelUtama);
    }

    private void prosesLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        try {
            Connection conn = Koneksi.configDB();
            Statement stm = conn.createStatement();
            String sql = "SELECT * FROM user WHERE username='" + username + "' AND password='" + password + "'";
            ResultSet res = stm.executeQuery(sql);

            if (res.next()) {
//                JOptionPane.showMessageDialog(this, "Login Berhasil! Selamat Datang, " + username, "Sukses", JOptionPane.INFORMATION_MESSAGE);
                Mainmenu menu = new Mainmenu();
                menu.setVisible(true);
                this.dispose();

            } else {
                JOptionPane.showMessageDialog(this, "Login Gagal! Username atau Password salah.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan koneksi database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new FormLogin().setVisible(true);
        });
    }
}
