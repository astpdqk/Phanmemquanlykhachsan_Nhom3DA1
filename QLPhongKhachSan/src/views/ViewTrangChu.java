package views;

import utilities.Tinhtien;
import com.google.zxing.qrcode.encoder.QRCode;
import java.awt.Color;
import java.awt.GraphicsEnvironment;
import service.CheckoutService;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import model.Bill;
import model.BillRoom;
import model.Client;
import model.PromotionR;
import model.Room;
import model.Service;
import viewModel.DVcheckout;
import viewModel.ThongtinCheckout;

import respository.RoomBillRepo;
import service.BillService;
import service.ClientService;
import service.PromotionRService;
import service.RoomBillService;
import service.RoomBillServiceService;
import service.RoomService;
import service.ServiceService;
import service.ViewModelItemService;
import utilities.Auth;
import utilities.DaysBetween2Dates;
import utilities.RandomCode;
import utilities.ReadWriteData;
import utilities.StringHandling;
import viewModel.ViewModelItem;
import static views.QrCode.client;

public class ViewTrangChu extends javax.swing.JFrame {

    private int temp = 0;
    private String tenPhong = "";
    private int tempCheck = 0;
    private JPanel jpanelTemp;
    public ButtonGroup gr = new ButtonGroup();
    private RandomCode rand = new RandomCode();
    private ReadWriteData readWriteData = new ReadWriteData();
    private DaysBetween2Dates between2Dates = new DaysBetween2Dates();
    private Auth auth;

    private ClientService clienService;
    private BillService billService;
    private RoomService roomService;

    private RoomBillService roomBillService;
    private ServiceService serviceService;
    private RoomBillServiceService roomBillServiceService;
    Calendar calendar = Calendar.getInstance();

    public ViewTrangChu() {
        initComponents();
        //set cbb
        cbbTrangthaiTP.removeAllItems();
        cbbTrangthaiTP.addItem("Chua thanh toán");
        cbbTrangthaiTP.addItem("Ðã thanh toán");

        this.setSize(GetMaxWidth(), GetMaxHeight());

        clienService = new ClientService();
        billService = new BillService();
        roomService = new RoomService();
        roomBillService = new RoomBillService();

        serviceService = new ServiceService();
        roomBillServiceService = new RoomBillServiceService();
        auth = new Auth();

        gr.add(rdNu);
        gr.add(rdNam);

        // chon ngay
        csTraPhong.setDate(new java.util.Date());
        csTraPhong.setMinSelectableDate(new java.util.Date());
        maxNs();
        new Thread() {
            public void run() {
                while (true) {
                    Calendar calendar = new GregorianCalendar();
                    String hour = (calendar.get(calendar.HOUR_OF_DAY) < 9) ? "0" + calendar.get(calendar.HOUR_OF_DAY) : "" + calendar.get(calendar.HOUR_OF_DAY) + "";
                    String minute = (calendar.get(calendar.MINUTE) < 9) ? "0" + calendar.get(calendar.MINUTE) : "" + calendar.get(calendar.MINUTE) + "";
                    int am_pm = calendar.get(calendar.AM_PM);
                    String day_night;
                    if (am_pm == calendar.AM) {
                        day_night = "AM";
                    } else {
                        day_night = "PM";
                    }
                    lbThoiGian.setText(hour + " : " + minute + " " + day_night);
                }
            }
        }.start();
        loadPhongSS();
        loadCbDv();
        loadSl();
        loadPanel("Tầng 1");
        loadPanel("Tầng 2");
        loadPanel("Tầng 3");
        // het init
    }

    public int GetMaxWidth() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
    }

    public int GetMaxHeight() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
    }

    public void loadPanel(String floor) {
        List<Room> list = new ArrayList<>();
        for (Room room : roomService.getAll()) {
            if (room.getLocation().equals(floor)) {
                list.add(room);
            }
        }
        for (Room room : list) {

            JPanel jPanel = new JPanel();
            JLabel jLabel = new JLabel();
            JLabel jLabel1 = new JLabel();
            JLabel jLabel2 = new JLabel();
            JLabel jLabel3 = new JLabel();
            JLabel jLabel4 = new JLabel();
            JLabel jLabel5 = new JLabel();
            JLabel jLabel6 = new JLabel();
            jPanel.setName(room.getRoomNumber());

            if (room.getStatus().equals("1")) {
                jPanel.setBackground(new java.awt.Color(204, 204, 255));
            }
            if (room.getStatus().equals("2")) {
                jPanel.setBackground(new java.awt.Color(204, 255, 255));
            }
            if (room.getStatus().equals("3")) {
                jPanel.setBackground(new java.awt.Color(204, 255, 204));
            }
            if (room.getStatus().equals("4")) {
                jPanel.setBackground(new java.awt.Color(221, 216, 216));
            }
            if (room.getStatus().equals("5")) {
                jPanel.setBackground(new java.awt.Color(255, 153, 0));
            }
            jPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, room.getRoomNumber(), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 20))); // NOI18N
            jPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

            jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
            jLabel1.setText("Loại:");

            jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
            jLabel2.setText("Diện tích:");

            jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
            jLabel3.setText("Giá:");

            jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
            jLabel4.setText(Integer.parseInt(room.getKindOfRoom()) == 1 ? "Phòng đơn" : Integer.parseInt(room.getKindOfRoom()) == 2 ? "Phòng đôi" : "Phòng VIP");

            jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
            jLabel5.setText(room.getArea() + " " + "m2");

            jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
            jLabel6.setText(room.getPrice() + " " + "VNĐ");

            javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel);
            jPanel.setLayout(jPanel10Layout);
            jPanel10Layout.setHorizontalGroup(
                    jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel1)
                                            .addComponent(jLabel3))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel6)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel5))
                                    .addContainerGap())
            );
            jPanel10Layout.setVerticalGroup(
                    jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                    .addGap(16, 16, 16)
                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel1)
                                            .addComponent(jLabel4))
                                    .addGap(21, 21, 21)
                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel5))
                                    .addGap(18, 18, 18)
                                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel6))
                                    .addContainerGap())
            );

            jPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    popupPhong.show(jPanel, evt.getX(), evt.getY());
                    tenPhong = jPanel.getName();
                    jpanelTemp = jPanel;
                }
            });

            if (room.getLocation().equals("Tầng 2")) {
                jPnTang2.add(jPanel);
                jPnTang2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tầng 2" + " " + "(" + list.size() + ")", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Segoe UI", 0, 20))); // NOI18N
            }
            if (room.getLocation().equals("Tầng 1")) {
                jPnTang1.add(jPanel);
                jPnTang1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tầng 1" + " " + "(" + list.size() + ")", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Segoe UI", 0, 20))); // NOI18N
            }
            if (room.getLocation().equals("Tầng 3")) {
                jPnTang3.add(jPanel);
                jPnTang3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tầng 3" + " " + "(" + list.size() + ")", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Segoe UI", 0, 20))); // NOI18N

            }
        }
    }

    void maxNs() {
        int year = calendar.get(Calendar.YEAR) - 14;
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        String maxNs = day + "/" + month + "/" + year;
        try {
            csNgaySinh.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(maxNs));
            csNgaySinh.setMaxSelectableDate(new SimpleDateFormat("dd/MM/yyyy").parse(maxNs));
        } catch (ParseException ex) {
            Logger.getLogger(ViewTrangChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadCbDv() {
        DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) cbDichVu.getModel();
        List<String> list = new ArrayList<>();
        int tempCB = 0;
        for (Service service : serviceService.getAll()) {
            for (int i = 0; i < cbDichVu.getMaximumRowCount(); i++) {
                if (service.getName().equals(cbDichVu.getItemAt(i))) {
                    tempCB = 1;
                    break;
                }
            }
            if (tempCB == 1) {
                tempCB = 0;
                break;
            }
            defaultComboBoxModel.addElement(service.getName());
        }
    }

    void loadPhongSS() {
        DefaultTableModel defaultTableModelds = (DefaultTableModel) tbDsPhong.getModel();
        defaultTableModelds.setRowCount(0);
        int stt = 0;
        for (Room room : roomService.getAll()) {
            if (room.getStatus().equals("1")) {
                stt = stt + 1;
                defaultTableModelds.addRow(new Object[]{stt, room.getRoomNumber(), Integer.parseInt(room.getKindOfRoom()) == 1 ? "Phòng đơn" : Integer.parseInt(room.getKindOfRoom()) == 2 ? "Phòng đôi" : "Phòng vip", room.getLocation(), room.getPrice()});
            }

        }
    }

    public void loadSl() {
        int ss = 0, ck = 0, cd = 0, dd = 0, sc = 0;
        for (Room room : roomService.getAll()) {
            if (room.getStatus().equals("1")) {
                ss = ss + 1;
            }
            if (room.getStatus().equals("2")) {
                ck = ck + 1;
            }
            if (room.getStatus().equals("3")) {
                cd = cd + 1;
            }
            if (room.getStatus().equals("4")) {
                dd = dd + 1;
            }
            if (room.getStatus().equals("5")) {
                sc = sc + 1;
            }
        }
        jLbAll.setText("(" + roomService.getAll().size() + ")");
        jLbSS.setText("(" + ss + ")");
        jLbCK.setText("(" + ck + ")");
        jLbCD.setText("(" + cd + ")");
        jLbDD.setText("(" + dd + ")");
        jLbSC.setText("(" + sc + ")");
    }

    void reset(Client client) {
        txtCCCD.setText(client.getIdPersonCard());
        txtMaKH.setText(client.getCode());
        txtSDT.setText(client.getCustomPhone());
        txtDiaChi.setText(client.getAddress());
        txtTenKhachHang.setText(client.getName());
        gr.clearSelection();
        maxNs();
        tempCheck = 0;
    }

    public void fillRoom(Room room) {
        txtMaPhong.setText(room.getCode());
        txtSoPhong.setText(room.getRoomNumber());
        txtAreaRoom.setText(room.getArea());
        String loaiPhong = "";
        if (room.getKindOfRoom() != null) {
            loaiPhong = Integer.parseInt(room.getKindOfRoom()) == 1 ? "Phòng đơn" : Integer.parseInt(room.getKindOfRoom()) == 2 ? "Phòng đôi" : "Phòng vip";
        }
        txtKindOfRoom.setText(loaiPhong);
        txtLocationRoom.setText(room.getLocation());
        txtGiaPhong.setText(room.getPrice());
    }

    void fillClient(Client client) {
        txtTenKhachHang.setText(client.getName());
        try {
            System.out.println(client.getDateOfBirth());
            if(client.getDateOfBirth()!= null){
            if (client.getDateOfBirth().indexOf("-") != -1) {
                client.setDateOfBirth(client.getDateOfBirth().substring(8) + "/" + client.getDateOfBirth().substring(5, 7) + "/" + client.getDateOfBirth().substring(0, 4));
            }
            csNgaySinh.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(client.getDateOfBirth()));
            }
        } catch (ParseException ex) {
            Logger.getLogger(ViewTrangChu.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        if (client.getSex().equals("Nam")) {
            rdNam.setSelected(true);
        } else {
            rdNu.setSelected(true);
        }
        txtCCCD.setText(client.getIdPersonCard());
        txtDiaChi.setText(client.getAddress());
        txtSDT.setText(client.getCustomPhone());
        txtMaKH.setText(client.getCode());
    }

    public class threadChuY extends Thread {

        public void run() {
            while (true) {
                try {
                    threadChuY.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ViewTrangChu.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (txtCCCD.getText().length() == 12) {
                    jLabel42.setText("");
                    Client client = new Client();
                    if (!clienService.checkTrung(txtCCCD.getText().trim()).isEmpty()) {
                        tempCheck = 1;
                        client = clienService.checkTrung(txtCCCD.getText().trim()).get(0);
                        fillClient(client);
                    }
                } else {
                    jLabel42.setText("*");
                    jLabel42.setForeground(Color.red);
                    jLabel42.setFont(new java.awt.Font("Segoe UI", 1, 16));
                }

                if (txtTenKhachHang.getText().length() != 0) {
                    jLabel28.setText("");
                } else {
                    jLabel28.setText("*");
                    jLabel28.setForeground(Color.red);
                    jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 16));
                }

                if (gr.isSelected(rdNam.getModel()) || gr.isSelected(rdNu.getModel())) {
                    jLabel41.setText("");
                } else {
                    jLabel41.setText("*");
                    jLabel41.setForeground(Color.red);
                    jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 16));
                }

                if (txtSDT.getText().length() == 10) {
                    jLabel43.setText("");
                } else {
                    jLabel43.setText("*");
                    jLabel43.setForeground(Color.red);
                    jLabel43.setFont(new java.awt.Font("Segoe UI", 1, 16));
                }

                if (txtDiaChi.getText().length() != 0) {
                    jLabel44.setText("");
                } else {
                    jLabel44.setText("*");
                    jLabel44.setForeground(Color.red);
                    jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 16));
                }
                if (jTabTrangChu.getSelectedIndex() != 1) {
                    return;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupPhong = new javax.swing.JPopupMenu();
        jMenuThuePhong = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuTrangThai = new javax.swing.JMenu();
        jMenuSS = new javax.swing.JMenuItem();
        jMenuCK = new javax.swing.JMenuItem();
        jMenuCD = new javax.swing.JMenuItem();
        jMenuDD = new javax.swing.JMenuItem();
        jMenuSC = new javax.swing.JMenuItem();
        menuDichVu = new javax.swing.JMenuItem();
        jClickCheckout = new javax.swing.JMenuItem();
        popupTang = new javax.swing.JPopupMenu();
        menuThemPhong = new javax.swing.JMenuItem();
        jPanel38 = new javax.swing.JPanel();
        txtTenKS = new javax.swing.JLabel();
        lbThoiGian = new javax.swing.JLabel();
        jTabTrangChu = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLbSS = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel36 = new javax.swing.JPanel();
        jLbCK = new javax.swing.JLabel();
        jPanel37 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel35 = new javax.swing.JPanel();
        jLbCD = new javax.swing.JLabel();
        jPanel34 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLbDD = new javax.swing.JLabel();
        jPanel33 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLbSC = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLbAll = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jPnTang3 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jPnTang2 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jPnTang1 = new javax.swing.JPanel();
        btnDx = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jPanel40 = new javax.swing.JPanel();
        pnInforKh = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txtTenKhachHang = new javax.swing.JTextField();
        txtCCCD = new javax.swing.JTextField();
        txtSDT = new javax.swing.JTextField();
        rdNam = new javax.swing.JRadioButton();
        rdNu = new javax.swing.JRadioButton();
        btnQuetMa = new javax.swing.JButton();
        btnThuePhong = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        txtMaKH = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        csNgaySinh = new com.toedter.calendar.JDateChooser();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDiaChi = new javax.swing.JTextArea();
        jLabel28 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbNoiThat = new javax.swing.JTable();
        txtMaPhong = new javax.swing.JTextField();
        txtSoPhong = new javax.swing.JTextField();
        txtAreaRoom = new javax.swing.JTextField();
        txtLocationRoom = new javax.swing.JTextField();
        txtKindOfRoom = new javax.swing.JTextField();
        txtGiaGiam = new javax.swing.JTextField();
        btnDoiPhong = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        txtGiaPhong = new javax.swing.JTextField();
        btnHuyPhong = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        csTraPhong = new com.toedter.calendar.JDateChooser();
        jScrollPane11 = new javax.swing.JScrollPane();
        tbDsPhong = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        csCheckinTP = new com.toedter.calendar.JDateChooser();
        jLabel54 = new javax.swing.JLabel();
        csCheckoutTP = new com.toedter.calendar.JDateChooser();
        jLabel55 = new javax.swing.JLabel();
        txtGiaphongTP = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        txtGiamgiaTP = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        txtCccdTP = new javax.swing.JTextField();
        txtKhachhangTP = new javax.swing.JTextField();
        txtSophongTP = new javax.swing.JTextField();
        txtMahdTP = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        txtThanhtienTP = new javax.swing.JTextField();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        cbbTrangthaiTP = new javax.swing.JComboBox<>();
        btnThanhtoanTP = new javax.swing.JButton();
        jLabel51 = new javax.swing.JLabel();
        txtPhuthuTP = new javax.swing.JTextField();
        jPanel20 = new javax.swing.JPanel();
        jScrollPane13 = new javax.swing.JScrollPane();
        tblDVTP = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jPanel39 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        txtSoPhongDV = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        txtMaDv = new javax.swing.JTextField();
        cbDichVu = new javax.swing.JComboBox<>();
        txtGiamGiaDV = new javax.swing.JTextField();
        txtGiaDv = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        btnThemDv = new javax.swing.JButton();
        btnHuyDv = new javax.swing.JButton();
        jLabel39 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        csNgaySd = new com.toedter.calendar.JDateChooser();
        jLabel40 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbTTDichVu = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblTTKhach = new javax.swing.JTable();
        jPanel16 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        txt_id = new javax.swing.JTextField();
        txt_code = new javax.swing.JTextField();
        txt_name = new javax.swing.JTextField();
        txt_price = new javax.swing.JTextField();
        txt_address = new javax.swing.JTextField();
        txt_status = new javax.swing.JTextField();
        txt_date = new javax.swing.JTextField();
        btn_them = new javax.swing.JButton();
        btn_sua = new javax.swing.JButton();
        btn_xoa = new javax.swing.JButton();
        jPanel18 = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        tbl_bill = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();

        jMenuThuePhong.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMenuThuePhong.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_rent_16px.png"))); // NOI18N
        jMenuThuePhong.setText("Thuê phòng");
        jMenuThuePhong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuThuePhongActionPerformed(evt);
            }
        });
        popupPhong.add(jMenuThuePhong);

        jMenuItem2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_sync_16px.png"))); // NOI18N
        jMenuItem2.setText("Sửa phòng");
        popupPhong.add(jMenuItem2);

        jMenuTrangThai.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_view_more_16px.png"))); // NOI18N
        jMenuTrangThai.setText("Đổi trạng thái");
        jMenuTrangThai.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jMenuSS.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMenuSS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_ok_16px.png"))); // NOI18N
        jMenuSS.setText("Sẵn sàng");
        jMenuSS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSSActionPerformed(evt);
            }
        });
        jMenuTrangThai.add(jMenuSS);

        jMenuCK.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMenuCK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_customer_16px.png"))); // NOI18N
        jMenuCK.setText("Có khách");
        jMenuCK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuCKActionPerformed(evt);
            }
        });
        jMenuTrangThai.add(jMenuCK);

        jMenuCD.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMenuCD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_broom_16px.png"))); // NOI18N
        jMenuCD.setText("Chưa dọn");
        jMenuCD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuCDActionPerformed(evt);
            }
        });
        jMenuTrangThai.add(jMenuCD);

        jMenuDD.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMenuDD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_vacuuming_16px.png"))); // NOI18N
        jMenuDD.setText("Đang dọn");
        jMenuDD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuDDActionPerformed(evt);
            }
        });
        jMenuTrangThai.add(jMenuDD);

        jMenuSC.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jMenuSC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_Tools_16px.png"))); // NOI18N
        jMenuSC.setText("Sửa chữa");
        jMenuSC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSCActionPerformed(evt);
            }
        });
        jMenuTrangThai.add(jMenuSC);

        popupPhong.add(jMenuTrangThai);

        menuDichVu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        menuDichVu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_service_16px.png"))); // NOI18N
        menuDichVu.setText("Thêm dịch vụ");
        menuDichVu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDichVuActionPerformed(evt);
            }
        });
        popupPhong.add(menuDichVu);

        jClickCheckout.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jClickCheckout.setText("Check Out");
        jClickCheckout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jClickCheckoutMouseClicked(evt);
            }
        });
        jClickCheckout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jClickCheckoutActionPerformed(evt);
            }
        });
        popupPhong.add(jClickCheckout);

        menuThemPhong.setText("Thêm phòng");
        popupTang.add(menuThemPhong);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 204, 204));

        jPanel38.setBackground(new java.awt.Color(255, 204, 204));
        jPanel38.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtTenKS.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtTenKS.setText("Phần mềm quản lý khách sạn Tây Côn Lĩnh");

        lbThoiGian.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lbThoiGian.setText("Thời gian");

        jTabTrangChu.setBackground(new java.awt.Color(255, 204, 204));
        jTabTrangChu.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabTrangChu.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setToolTipText("");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Sẵn sàng:");

        jLbSS.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLbSS.setText("(0)");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Có khách:");

        jPanel36.setBackground(new java.awt.Color(204, 255, 255));
        jPanel36.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel36.setPreferredSize(new java.awt.Dimension(15, 15));

        javax.swing.GroupLayout jPanel36Layout = new javax.swing.GroupLayout(jPanel36);
        jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );
        jPanel36Layout.setVerticalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        jLbCK.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLbCK.setText("(0)");

        jPanel37.setBackground(new java.awt.Color(204, 204, 255));
        jPanel37.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel37.setPreferredSize(new java.awt.Dimension(15, 15));

        javax.swing.GroupLayout jPanel37Layout = new javax.swing.GroupLayout(jPanel37);
        jPanel37.setLayout(jPanel37Layout);
        jPanel37Layout.setHorizontalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );
        jPanel37Layout.setVerticalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Chưa dọn:");

        jPanel35.setBackground(new java.awt.Color(204, 255, 204));
        jPanel35.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel35.setPreferredSize(new java.awt.Dimension(15, 15));

        javax.swing.GroupLayout jPanel35Layout = new javax.swing.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );
        jPanel35Layout.setVerticalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        jLbCD.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLbCD.setText("(0)");

        jPanel34.setBackground(new java.awt.Color(221, 216, 216));
        jPanel34.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel34.setPreferredSize(new java.awt.Dimension(15, 15));

        javax.swing.GroupLayout jPanel34Layout = new javax.swing.GroupLayout(jPanel34);
        jPanel34.setLayout(jPanel34Layout);
        jPanel34Layout.setHorizontalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );
        jPanel34Layout.setVerticalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText("Đang dọn:");

        jLbDD.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLbDD.setText("(0)");

        jPanel33.setBackground(new java.awt.Color(255, 153, 0));
        jPanel33.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel33.setPreferredSize(new java.awt.Dimension(15, 15));

        javax.swing.GroupLayout jPanel33Layout = new javax.swing.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setText("Sửa chữa:");

        jLbSC.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLbSC.setText("(0)");

        jLabel31.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel31.setText("Tất cả:");

        jLbAll.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLbAll.setText("(0)");

        jScrollPane3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setPreferredSize(new java.awt.Dimension(978, 500));

        jPanel9.setBackground(new java.awt.Color(255, 204, 204));

        jPnTang3.setBackground(new java.awt.Color(255, 255, 204));
        jPnTang3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tầng 3", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Segoe UI", 0, 20))); // NOI18N
        jPnTang3.setAutoscrolls(true);
        jPnTang3.setLayout(new java.awt.GridLayout(0, 5));
        jScrollPane9.setViewportView(jPnTang3);

        jPnTang2.setBackground(new java.awt.Color(255, 255, 204));
        jPnTang2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tầng 2", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Segoe UI", 0, 20))); // NOI18N
        jPnTang2.setAutoscrolls(true);
        jPnTang2.setLayout(new java.awt.GridLayout(0, 5));
        jScrollPane10.setViewportView(jPnTang2);

        jPnTang1.setBackground(new java.awt.Color(255, 255, 204));
        jPnTang1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tầng 1", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Segoe UI", 0, 20))); // NOI18N
        jPnTang1.setLayout(new java.awt.GridLayout(0, 5));
        jScrollPane8.setViewportView(jPnTang1);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane10)
                    .addComponent(jScrollPane8)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 1193, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(281, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jScrollPane10, jScrollPane8, jScrollPane9});

        jScrollPane3.setViewportView(jPanel9);

        btnDx.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnDx.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_Logout_16px.png"))); // NOI18N
        btnDx.setText("Đăng xuất");
        btnDx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLbAll)
                .addGap(50, 50, 50)
                .addComponent(jPanel37, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLbSS)
                .addGap(50, 50, 50)
                .addComponent(jPanel36, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLbCK)
                .addGap(50, 50, 50)
                .addComponent(jPanel35, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLbCD)
                .addGap(50, 50, 50)
                .addComponent(jPanel34, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLbDD)
                .addGap(50, 50, 50)
                .addComponent(jPanel33, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLbSC)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                .addComponent(btnDx)
                .addGap(25, 25, 25))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jPanel33, jPanel34, jPanel35, jPanel36, jPanel37});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel37, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLbAll)
                                .addComponent(jLabel31)
                                .addComponent(jLabel1)
                                .addComponent(jLbSS)
                                .addComponent(jLabel2)
                                .addComponent(jLbCK)
                                .addComponent(jLabel3)
                                .addComponent(jLbCD)
                                .addComponent(jLabel5)
                                .addComponent(jLbDD)
                                .addComponent(jLabel4)
                                .addComponent(jLbSC))
                            .addComponent(jPanel36, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                            .addComponent(jPanel35, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel34, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel33, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnDx)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLbCD, jLbCK, jLbDD, jLbSC, jLbSS, jPanel33, jPanel34, jPanel35, jPanel36, jPanel37});

        jTabTrangChu.addTab("Trang chủ", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 204, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setPreferredSize(new java.awt.Dimension(874, 400));

        jScrollPane7.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane7.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane7.setPreferredSize(new java.awt.Dimension(1200, 612));

        jPanel40.setBackground(new java.awt.Color(255, 204, 204));

        pnInforKh.setBackground(new java.awt.Color(255, 255, 204));
        pnInforKh.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông tin khách hàng", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Segoe UI", 0, 24))); // NOI18N
        pnInforKh.setPreferredSize(new java.awt.Dimension(380, 390));
        pnInforKh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pnInforKhMouseEntered(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel22.setText("Họ và Tên:");

        jLabel23.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel23.setText("Giới tính:");
        jLabel23.setPreferredSize(new java.awt.Dimension(85, 20));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel24.setText("Ngày Sinh:");

        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel25.setText("Số cccd:");
        jLabel25.setPreferredSize(new java.awt.Dimension(85, 20));

        jLabel26.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel26.setText("Số điện thoại:");

        jLabel27.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel27.setText("Địa chỉ:");

        txtTenKhachHang.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtTenKhachHang.setMinimumSize(new java.awt.Dimension(65, 22));

        txtCCCD.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtCCCD.setMinimumSize(new java.awt.Dimension(65, 22));

        txtSDT.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtSDT.setMinimumSize(new java.awt.Dimension(65, 22));

        rdNam.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        rdNam.setText("Nam");

        rdNu.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        rdNu.setText("Nữ");

        btnQuetMa.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnQuetMa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_Qr_Code_16px.png"))); // NOI18N
        btnQuetMa.setText("Quét mã");
        btnQuetMa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuetMaActionPerformed(evt);
            }
        });

        btnThuePhong.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnThuePhong.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_rent_16px.png"))); // NOI18N
        btnThuePhong.setText("Thuê phòng");
        btnThuePhong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThuePhongActionPerformed(evt);
            }
        });

        btnReset.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_reset_16px.png"))); // NOI18N
        btnReset.setText("Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        txtMaKH.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtMaKH.setEnabled(false);

        jLabel35.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel35.setText("Mã:");
        jLabel35.setPreferredSize(new java.awt.Dimension(85, 20));

        csNgaySinh.setDateFormatString("dd/MM/yyyy");
        csNgaySinh.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        csNgaySinh.setMaxSelectableDate(new java.util.Date(1136052108000L));
        csNgaySinh.setMinSelectableDate(new java.util.Date(-631173535000L));

        txtDiaChi.setColumns(20);
        txtDiaChi.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtDiaChi.setRows(5);
        txtDiaChi.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane2.setViewportView(txtDiaChi);

        jLabel28.setText(" ");

        jLabel41.setText(" ");

        jLabel42.setText(" ");

        jLabel43.setText(" ");

        jLabel44.setText(" ");

        javax.swing.GroupLayout pnInforKhLayout = new javax.swing.GroupLayout(pnInforKh);
        pnInforKh.setLayout(pnInforKhLayout);
        pnInforKhLayout.setHorizontalGroup(
            pnInforKhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnInforKhLayout.createSequentialGroup()
                .addContainerGap(52, Short.MAX_VALUE)
                .addGroup(pnInforKhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnInforKhLayout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62)
                        .addComponent(rdNam)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                        .addComponent(rdNu)
                        .addGap(36, 36, 36)
                        .addComponent(jLabel41)
                        .addGap(31, 31, 31))
                    .addGroup(pnInforKhLayout.createSequentialGroup()
                        .addGroup(pnInforKhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnInforKhLayout.createSequentialGroup()
                                .addComponent(btnQuetMa, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnThuePhong))
                            .addComponent(btnReset)
                            .addGroup(pnInforKhLayout.createSequentialGroup()
                                .addGroup(pnInforKhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel26)
                                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel24)
                                    .addComponent(jLabel22)
                                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel27))
                                .addGap(38, 38, 38)
                                .addGroup(pnInforKhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtSDT, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                                    .addComponent(txtCCCD, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                    .addComponent(csNgaySinh, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(txtMaKH, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnInforKhLayout.createSequentialGroup()
                                        .addComponent(txtTenKhachHang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(pnInforKhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel28)
                                    .addComponent(jLabel42)
                                    .addComponent(jLabel43)
                                    .addComponent(jLabel44))))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        pnInforKhLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnQuetMa, btnReset});

        pnInforKhLayout.setVerticalGroup(
            pnInforKhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnInforKhLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(pnInforKhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMaKH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnInforKhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtTenKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28))
                .addGap(18, 18, 18)
                .addGroup(pnInforKhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addComponent(csNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnInforKhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdNam)
                    .addComponent(rdNu)
                    .addComponent(jLabel41))
                .addGap(18, 18, 18)
                .addGroup(pnInforKhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCCCD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42))
                .addGap(18, 18, 18)
                .addGroup(pnInforKhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(txtSDT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43))
                .addGap(18, 18, 18)
                .addGroup(pnInforKhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27)
                    .addComponent(jLabel44)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addGroup(pnInforKhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnQuetMa, javax.swing.GroupLayout.PREFERRED_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(btnThuePhong))
                .addGap(18, 18, 18)
                .addComponent(btnReset)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pnInforKhLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnQuetMa, btnReset});

        pnInforKhLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {csNgaySinh, txtTenKhachHang});

        jPanel11.setBackground(new java.awt.Color(255, 255, 204));
        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông tin phòng", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Segoe UI", 0, 24))); // NOI18N
        jPanel11.setPreferredSize(new java.awt.Dimension(380, 490));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel14.setText("Mã Phòng:");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel15.setText("Số phòng:");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel16.setText("Diện tích:");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel17.setText("Vị trí:");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel18.setText("Giá:");

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel19.setText("Loại phòng:");

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel20.setText("Nội thất trong phòng");

        tbNoiThat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tbNoiThat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Tên", "Tình trạng", "Số lượng"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbNoiThat.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tbNoiThat);
        if (tbNoiThat.getColumnModel().getColumnCount() > 0) {
            tbNoiThat.getColumnModel().getColumn(0).setResizable(false);
            tbNoiThat.getColumnModel().getColumn(1).setResizable(false);
            tbNoiThat.getColumnModel().getColumn(2).setResizable(false);
        }

        txtMaPhong.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtMaPhong.setEnabled(false);
        txtMaPhong.setPreferredSize(new java.awt.Dimension(65, 22));

        txtSoPhong.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtSoPhong.setEnabled(false);
        txtSoPhong.setPreferredSize(new java.awt.Dimension(65, 22));

        txtAreaRoom.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtAreaRoom.setEnabled(false);
        txtAreaRoom.setMinimumSize(new java.awt.Dimension(65, 22));

        txtLocationRoom.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtLocationRoom.setEnabled(false);
        txtLocationRoom.setPreferredSize(new java.awt.Dimension(65, 22));

        txtKindOfRoom.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtKindOfRoom.setEnabled(false);
        txtKindOfRoom.setMinimumSize(new java.awt.Dimension(65, 22));

        txtGiaGiam.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtGiaGiam.setText("0");
        txtGiaGiam.setEnabled(false);
        txtGiaGiam.setMinimumSize(new java.awt.Dimension(65, 22));

        btnDoiPhong.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnDoiPhong.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_sync_16px.png"))); // NOI18N
        btnDoiPhong.setText("Đổi phòng");
        btnDoiPhong.setPreferredSize(new java.awt.Dimension(125, 26));
        btnDoiPhong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDoiPhongActionPerformed(evt);
            }
        });

        jLabel30.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel30.setText("Giảm giá:");

        txtGiaPhong.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtGiaPhong.setEnabled(false);

        btnHuyPhong.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnHuyPhong.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_cancel_16px.png"))); // NOI18N
        btnHuyPhong.setText("Hủy");
        btnHuyPhong.setPreferredSize(new java.awt.Dimension(125, 26));
        btnHuyPhong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHuyPhongActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel9.setText("VNĐ");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel10.setText("VNĐ");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel13.setText("m2");

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel21.setText("Ngày trả phòng:");

        csTraPhong.setDateFormatCalendar(null);
        csTraPhong.setDateFormatString("dd/MM/yyyy");
        csTraPhong.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        csTraPhong.setMaxSelectableDate(new java.util.Date(253370743302000L));
        csTraPhong.setMinSelectableDate(new java.util.Date(-62135791098000L));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel18))
                            .addComponent(jLabel16)
                            .addComponent(jLabel17)
                            .addComponent(jLabel19)
                            .addComponent(jLabel30))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(txtMaPhong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSoPhong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(txtAreaRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtLocationRoom, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtKindOfRoom, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(csTraPhong, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                    .addComponent(txtGiaPhong, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtGiaGiam, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel9)))))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(btnDoiPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnHuyPhong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnDoiPhong, btnHuyPhong});

        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMaPhong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSoPhong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAreaRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13)
                            .addComponent(jLabel16)))
                    .addComponent(jLabel14))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(txtLocationRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtKindOfRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19)))
                    .addComponent(jLabel17))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(csTraPhong, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGiaGiam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30)
                    .addComponent(jLabel10))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGiaPhong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDoiPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHuyPhong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel11Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnDoiPhong, btnHuyPhong});

        jPanel11Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {csTraPhong, txtAreaRoom, txtGiaGiam, txtGiaPhong, txtKindOfRoom, txtLocationRoom, txtMaPhong, txtSoPhong});

        tbDsPhong.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tbDsPhong.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "STT", "Số phòng", "Loại phòng", "Vị trí", "Giá tiền"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane11.setViewportView(tbDsPhong);
        if (tbDsPhong.getColumnModel().getColumnCount() > 0) {
            tbDsPhong.getColumnModel().getColumn(0).setResizable(false);
            tbDsPhong.getColumnModel().getColumn(1).setResizable(false);
            tbDsPhong.getColumnModel().getColumn(2).setResizable(false);
            tbDsPhong.getColumnModel().getColumn(3).setResizable(false);
            tbDsPhong.getColumnModel().getColumn(4).setResizable(false);
        }

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel6.setText("Danh sách phòng sẵn sàng cho thuê");

        javax.swing.GroupLayout jPanel40Layout = new javax.swing.GroupLayout(jPanel40);
        jPanel40.setLayout(jPanel40Layout);
        jPanel40Layout.setHorizontalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel40Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(pnInforKh, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel40Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel40Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(jLabel6)))
                .addContainerGap(315, Short.MAX_VALUE))
        );
        jPanel40Layout.setVerticalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel40Layout.createSequentialGroup()
                .addGroup(jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel40Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6))
                    .addGroup(jPanel40Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnInforKh, javax.swing.GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE)
                            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE))))
                .addContainerGap(70, Short.MAX_VALUE))
        );

        jScrollPane7.setViewportView(jPanel40);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1284, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
        );

        jTabTrangChu.addTab("Thuê phòng", jPanel2);

        jPanel10.setBackground(new java.awt.Color(255, 204, 204));

        jPanel19.setBackground(new java.awt.Color(255, 255, 153));

        jLabel48.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel48.setText("Khach hang:");

        jLabel49.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel49.setText("CCCD:");

        jLabel50.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel50.setText("Ma hoa don:");

        jLabel52.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel52.setText("So phong:");

        jLabel53.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel53.setText("Checkin:");

        csCheckinTP.setDateFormatCalendar(null);
        csCheckinTP.setDateFormatString("dd/MM/yyyy");
        csCheckinTP.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        csCheckinTP.setMaxSelectableDate(new java.util.Date(253370743302000L));
        csCheckinTP.setMinSelectableDate(new java.util.Date(-62135791098000L));

        jLabel54.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel54.setText("Checkout:");

        csCheckoutTP.setDateFormatCalendar(null);
        csCheckoutTP.setDateFormatString("dd/MM/yyyy");
        csCheckoutTP.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        csCheckoutTP.setMaxSelectableDate(new java.util.Date(253370743302000L));
        csCheckoutTP.setMinSelectableDate(new java.util.Date(-62135791098000L));

        jLabel55.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel55.setText("Giá phong:");

        txtGiaphongTP.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtGiaphongTP.setEnabled(false);

        jLabel56.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel56.setText("VNĐ");

        jLabel57.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel57.setText("Giam gia:");

        txtGiamgiaTP.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtGiamgiaTP.setEnabled(false);

        jLabel58.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel58.setText("VNĐ");

        txtCccdTP.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtCccdTP.setEnabled(false);

        txtKhachhangTP.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtKhachhangTP.setEnabled(false);

        txtSophongTP.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtSophongTP.setEnabled(false);

        txtMahdTP.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtMahdTP.setEnabled(false);

        jLabel59.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel59.setText("Thanh tien:");

        txtThanhtienTP.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtThanhtienTP.setEnabled(false);

        jLabel60.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel60.setText("VNĐ");

        jLabel61.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel61.setText("Trang thai:");

        cbbTrangthaiTP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbbTrangthaiTP.setEnabled(false);

        btnThanhtoanTP.setText("THANH TOAN");

        jLabel51.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel51.setText("Phu thu:");

        txtPhuthuTP.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCccdTP, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                            .addComponent(txtKhachhangTP)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel50)
                            .addComponent(jLabel52))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtSophongTP, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                            .addComponent(txtMahdTP))))
                .addGap(124, 124, 124)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel53)
                    .addComponent(jLabel54)
                    .addComponent(jLabel55)
                    .addComponent(jLabel57)
                    .addComponent(jLabel51))
                .addGap(97, 97, 97)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtGiamgiaTP)
                    .addComponent(csCheckoutTP, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                    .addComponent(csCheckinTP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtGiaphongTP)
                    .addComponent(txtPhuthuTP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel58)
                    .addComponent(jLabel56))
                .addGap(96, 96, 96)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnThanhtoanTP, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                                    .addComponent(txtThanhtienTP))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel60)))
                        .addContainerGap())
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(cbbTrangthaiTP, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(68, 68, 68))))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtKhachhangTP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel48))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCccdTP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel49))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel50)
                            .addComponent(txtMahdTP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSophongTP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel52)))
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel53, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(csCheckinTP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
                                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel54, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(csCheckoutTP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel61)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbbTrangthaiTP)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addComponent(jLabel59)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtThanhtienTP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel60))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnThanhtoanTP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel19Layout.createSequentialGroup()
                                        .addGap(1, 1, 1)
                                        .addComponent(jLabel51))
                                    .addComponent(txtPhuthuTP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(6, 6, 6)
                                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtGiaphongTP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel55)
                                    .addComponent(jLabel56))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtGiamgiaTP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel57)
                                    .addComponent(jLabel58))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblDVTP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Dich vu", "Ma DV", "So luong", "Gia DV", "Giam gia"
            }
        ));
        jScrollPane13.setViewportView(tblDVTP);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane13)
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(95, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabTrangChu.addTab("Trả phòng", jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 204, 204));

        jScrollPane6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel39.setBackground(new java.awt.Color(255, 204, 204));

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Dịch vụ", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        jLabel29.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel29.setText("Số phòng:");

        txtSoPhongDV.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtSoPhongDV.setPreferredSize(new java.awt.Dimension(85, 26));

        jLabel33.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel33.setText("Dịch vụ:");

        jLabel34.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel34.setText("Mã dịch vụ:");

        txtMaDv.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtMaDv.setEnabled(false);
        txtMaDv.setPreferredSize(new java.awt.Dimension(85, 26));

        cbDichVu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cbDichVu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Dịch vụ" }));
        cbDichVu.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbDichVuItemStateChanged(evt);
            }
        });
        cbDichVu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cbDichVuMouseClicked(evt);
            }
        });

        txtGiamGiaDV.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtGiamGiaDV.setText("0");
        txtGiamGiaDV.setEnabled(false);

        txtGiaDv.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtGiaDv.setEnabled(false);

        jLabel36.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel36.setText("Giảm giá:");

        jLabel37.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel37.setText("Giá:");

        btnThemDv.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnThemDv.setText("Thêm");
        btnThemDv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemDvActionPerformed(evt);
            }
        });

        btnHuyDv.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnHuyDv.setText("Hủy");

        jLabel39.setText("VNĐ");

        jLabel38.setText("VNĐ");

        csNgaySd.setDateFormatString("dd/MM/yyyy");
        csNgaySd.setEnabled(false);
        csNgaySd.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        csNgaySd.setPreferredSize(new java.awt.Dimension(85, 26));

        jLabel40.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel40.setText("Ngày sử dụng:");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel40)
                            .addComponent(jLabel36)
                            .addComponent(jLabel37))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtGiaDv, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                                        .addComponent(btnThemDv)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnHuyDv))
                                    .addComponent(txtGiamGiaDV, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel39)
                                    .addComponent(jLabel38)))
                            .addComponent(csNgaySd, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(txtMaDv, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel14Layout.createSequentialGroup()
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel33)
                                .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(36, 36, 36)
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtSoPhongDV, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cbDichVu, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        jPanel14Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cbDichVu, csNgaySd, txtGiaDv, txtGiamGiaDV, txtMaDv, txtSoPhongDV});

        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSoPhongDV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29))
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(cbDichVu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(txtMaDv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(csNgaySd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGiamGiaDV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36)
                    .addComponent(jLabel39))
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtGiaDv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37)
                    .addComponent(jLabel38))
                .addGap(27, 27, 27)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnHuyDv)
                    .addComponent(btnThemDv))
                .addGap(17, 17, 17))
        );

        tbTTDichVu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tbTTDichVu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã hóa đơn", "Số phòng", "Dịch vụ", "Mã dịch vụ", "Ngày sử dụng", "Số lần", "Giảm giá", "Giá", "Thành tiền"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbTTDichVu.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(tbTTDichVu);
        if (tbTTDichVu.getColumnModel().getColumnCount() > 0) {
            tbTTDichVu.getColumnModel().getColumn(0).setResizable(false);
            tbTTDichVu.getColumnModel().getColumn(1).setResizable(false);
            tbTTDichVu.getColumnModel().getColumn(2).setResizable(false);
            tbTTDichVu.getColumnModel().getColumn(3).setResizable(false);
            tbTTDichVu.getColumnModel().getColumn(4).setResizable(false);
            tbTTDichVu.getColumnModel().getColumn(5).setResizable(false);
            tbTTDichVu.getColumnModel().getColumn(6).setResizable(false);
            tbTTDichVu.getColumnModel().getColumn(7).setResizable(false);
            tbTTDichVu.getColumnModel().getColumn(8).setResizable(false);
        }

        javax.swing.GroupLayout jPanel39Layout = new javax.swing.GroupLayout(jPanel39);
        jPanel39.setLayout(jPanel39Layout);
        jPanel39Layout.setHorizontalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel39Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 803, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1403, Short.MAX_VALUE))
        );
        jPanel39Layout.setVerticalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel39Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(189, Short.MAX_VALUE))
        );

        jScrollPane6.setViewportView(jPanel39);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1288, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
        );

        jTabTrangChu.addTab("Dịch vụ", jPanel4);

        jPanel8.setBackground(new java.awt.Color(255, 204, 204));

        tblTTKhach.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã Kh", "Tên", "Tuổi", "Giới tính", "Số căn cước", "Số điện thoại", "Địa chỉ"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTTKhach.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(tblTTKhach);
        if (tblTTKhach.getColumnModel().getColumnCount() > 0) {
            tblTTKhach.getColumnModel().getColumn(0).setResizable(false);
            tblTTKhach.getColumnModel().getColumn(1).setResizable(false);
            tblTTKhach.getColumnModel().getColumn(2).setResizable(false);
            tblTTKhach.getColumnModel().getColumn(3).setResizable(false);
            tblTTKhach.getColumnModel().getColumn(4).setResizable(false);
            tblTTKhach.getColumnModel().getColumn(5).setResizable(false);
            tblTTKhach.getColumnModel().getColumn(6).setResizable(false);
        }

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 393, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 759, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(73, 73, 73))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        jTabTrangChu.addTab("Khách hàng", jPanel8);

        jPanel13.setBackground(new java.awt.Color(255, 204, 204));

        jPanel17.setBackground(new java.awt.Color(255, 255, 204));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel7.setText("Hóa Đơn");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel8.setText("ID");

        jLabel47.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel47.setText("Code");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel11.setText("Name");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel12.setText("Price");

        jLabel32.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel32.setText("Address");

        jLabel45.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel45.setText("Status");

        jLabel46.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel46.setText("Date");

        txt_id.setEnabled(false);
        txt_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_idActionPerformed(evt);
            }
        });

        txt_code.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_codeActionPerformed(evt);
            }
        });

        txt_name.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_nameActionPerformed(evt);
            }
        });

        txt_price.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_priceActionPerformed(evt);
            }
        });

        txt_address.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_addressActionPerformed(evt);
            }
        });

        txt_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_statusActionPerformed(evt);
            }
        });

        btn_them.setText("Thêm hóa đơn");
        btn_them.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_themActionPerformed(evt);
            }
        });

        btn_sua.setText("Sửa hóa đơn");
        btn_sua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_suaActionPerformed(evt);
            }
        });

        btn_xoa.setText("Xóa hóa đơn");
        btn_xoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_xoaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGap(214, 214, 214)
                        .addComponent(jLabel47))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(jLabel32)
                            .addComponent(jLabel45)
                            .addComponent(jLabel46))
                        .addGap(22, 22, 22)
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_address, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_name, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_code, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_status, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_price, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_date, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(btn_them)
                        .addGap(18, 18, 18)
                        .addComponent(btn_sua)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_xoa)))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel47)
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txt_code, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txt_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txt_price, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(txt_address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(txt_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46)
                    .addComponent(txt_date, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_them)
                    .addComponent(btn_sua)
                    .addComponent(btn_xoa))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        tbl_bill.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Code", "Name", "Price", "Address", "Status", "Date"
            }
        ));
        jScrollPane12.setViewportView(tbl_bill);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 791, Short.MAX_VALUE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(122, Short.MAX_VALUE)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabTrangChu.addTab("Hóa đơn", jPanel6);

        jPanel12.setBackground(new java.awt.Color(255, 204, 204));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1288, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 608, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabTrangChu.addTab("Nhân viên", jPanel5);

        jPanel15.setBackground(new java.awt.Color(255, 204, 204));

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1288, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 608, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabTrangChu.addTab("Cơ sở vật chất", jPanel7);

        javax.swing.GroupLayout jPanel38Layout = new javax.swing.GroupLayout(jPanel38);
        jPanel38.setLayout(jPanel38Layout);
        jPanel38Layout.setHorizontalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel38Layout.createSequentialGroup()
                        .addComponent(txtTenKS)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbThoiGian)
                        .addGap(39, 39, 39))
                    .addGroup(jPanel38Layout.createSequentialGroup()
                        .addComponent(jTabTrangChu)
                        .addContainerGap())))
        );
        jPanel38Layout.setVerticalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addComponent(jTabTrangChu, javax.swing.GroupLayout.PREFERRED_SIZE, 649, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTenKS)
                    .addComponent(lbThoiGian))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        reset(new Client());
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnThuePhongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThuePhongActionPerformed
        if (JOptionPane.showConfirmDialog(this, "Xác nhận cho thuê phòng.", "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (!txtMaPhong.getText().equals("")) {
                // them khach hang
                if (tempCheck == 0) {
                    txtMaKH.setText(rand.createCode("kh", "maKh.txt"));
                    for (Client client : clienService.getAll()) {
                        if (client.getCode().equals(txtMaKH.getText())) {
                            try {
                                readWriteData.ghidl(Integer.parseInt(txtMaKH.getText().substring(2)), "maKh.txt");

                            } catch (IOException ex) {
                                Logger.getLogger(ClientService.class
                                        .getName()).log(Level.SEVERE, null, ex);
                            }
                            btnThuePhongActionPerformed(evt);
                        }
                    }
                    Client client = new Client();
                    client.setName(txtTenKhachHang.getText().trim());
                    client.setAddress(txtDiaChi.getText().trim());
                    client.setCustomPhone(txtSDT.getText().trim());
                    client.setDateOfBirth(String.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(csNgaySinh.getDate())));

                    if (rdNu.isSelected()) {
                        client.setSex("Nữ");
                    } else {
                        client.setSex("Nam");
                    }
                    client.setIdPersonCard(txtCCCD.getText().trim());
                    client.setCode(txtMaKH.getText().trim());
                    String string = clienService.insert(client);
                    if (string != null) {
                        JOptionPane.showMessageDialog(this, string);
                    }
                }
                // them hoa don
                List<Client> listKh = clienService.checkTrung(txtCCCD.getText().trim());
                if (listKh == null || listKh.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "khong tim thay khach hang");
                    return;
                }
                String idClient = listKh.get(0).getId();

                long noDay = between2Dates.daysBetween2Dates(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()), new SimpleDateFormat("yyyy-MM-dd").format(csTraPhong.getDate()));
                if (noDay == 0) {
                    JOptionPane.showMessageDialog(this, "Chọn ngày trả phòng");
                    return;
                }
                if (billService.searchHd(idClient) == null) {
                    Bill bill = new Bill();
                    if (clienService.checkTrung(txtCCCD.getText().trim()) == null) {
                        return;
                    }
                    bill.setIdClient(idClient);
                    bill.setIdStaff(auth.id);

                    String maHd = rand.createCode("hd", "maHd.txt");
                    System.out.println(maHd);

                    bill.setCode(maHd);
                    bill.setPrice(String.valueOf(noDay * (Float.parseFloat(txtGiaPhong.getText().trim()) - Float.parseFloat(txtGiaGiam.getText().trim()))));
                    bill.setStatus("0");// 0 chua thanh toan
                    bill.setDate(String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())));
                    billService.insert(bill);
                }
                // thêm phòng vào hóa đơn chi tiết
                BillRoom roomBill = new BillRoom();
                Room room = roomService.getRoomByNumber(txtSoPhong.getText().trim()).get(0);
                roomBill.setBillId(billService.searchHd(idClient).get(0).getId());// id hoa don
                roomBill.setRoomId(room.getId());
                roomBill.setPriceRoom(txtGiaPhong.getText());
                roomBill.setPromotionRoom(txtGiaGiam.getText());
                roomBill.setDateCheckIn(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));

                roomBill.setDateCheckout(new SimpleDateFormat("yyyy-MM-dd 12:00:00").format(csTraPhong.getDate()));
                roomBillService.insert(roomBill);
                JOptionPane.showMessageDialog(this, "Thành Công!!");
                room.setStatus("2");
                roomService.update(room, room.getRoomNumber());
                if (room.getLocation().equals("Tầng 1")) {
                    jPnTang1.removeAll();
                }
                if (room.getLocation().equals("Tầng 2")) {
                    jPnTang2.removeAll();
                }
                if (room.getLocation().equals("Tầng 3")) {
                    jPnTang3.removeAll();
                }
                loadPanel(room.getLocation());
                btnResetActionPerformed(evt);
                btnHuyPhongActionPerformed(evt);
            } else {
                JOptionPane.showMessageDialog(this, "Chưa chọn phòng");
                return;
            }
        }
    }//GEN-LAST:event_btnThuePhongActionPerformed

    private void btnDoiPhongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDoiPhongActionPerformed

        if (temp == 0) {
            temp = 1;
            txtSoPhong.setEnabled(true);
            return;
        }
        if (temp == 1) {
            if (roomService.getRoomByNumber(txtSoPhong.getText().trim()) == null || !roomService.getRoomByNumber(txtSoPhong.getText().trim()).get(0).getStatus().equals("1")) {
                JOptionPane.showMessageDialog(this, "Nhập sai số phòng hoặc phòng chưa sẵn sàng.");
                return;
            }
            PromotionRService promotionRService = new PromotionRService();

            Room room = roomService.getRoomByNumber(txtSoPhong.getText().trim()).get(0);
            String ngay = String.valueOf(calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DATE));
            PromotionR pr = promotionRService.searchPromotionR(room.getIdPromotion(), ngay);
            if (pr != null) {
                txtGiaGiam.setText(pr.getValue());
            }
            if (room.getStatus().equals("1")) {
                fillRoom(room);
            }
            ViewModelItemService viewItemService = new ViewModelItemService();
            if (viewItemService.getAll(room.getId()) != null) {
                DefaultTableModel defaultTableModel = (DefaultTableModel) tbNoiThat.getModel();
                defaultTableModel.setRowCount(0);
                for (ViewModelItem item : viewItemService.getAll(room.getId())) {
                    defaultTableModel.addRow(new Object[]{item.getName(), item.getStatus(), item.getAmount()});
                }
            }
            txtSoPhong.setEnabled(false);
            temp = 0;
        }
    }//GEN-LAST:event_btnDoiPhongActionPerformed

    private void btnQuetMaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuetMaActionPerformed
        QrCode qrCode = new QrCode();
        if (qrCode.isVisible() == true) {
            qrCode.show();
        } else {
            qrCode.setVisible(true);
        }
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ViewTrangChu.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                    if (qrCode.temp == 1) {
                        fillClient(qrCode.client);
                        return;
                    }
                }
            }
        }.start();
        qrCode.temp = 0;
    }//GEN-LAST:event_btnQuetMaActionPerformed

    private void pnInforKhMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnInforKhMouseEntered
        threadChuY t1 = new threadChuY();
        t1.start();
    }//GEN-LAST:event_pnInforKhMouseEntered

    private void btnThemDvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemDvActionPerformed
        if (roomService.getRoomByNumber(txtSoPhongDV.getText().trim()) == null) {
            JOptionPane.showMessageDialog(this, "Xem lại số phòng");
            return;
        }
        String idRoom = roomService.getRoomByNumber(txtSoPhongDV.getText().trim()).get(0).getId();
        String idService = serviceService.getByCode(txtMaDv.getText().trim()).get(0).getId();
        String idBill = billService.getId(txtSoPhongDV.getText().trim(), new java.util.Date());
        model.RoomBillService roomBillService = new model.RoomBillService();
        roomBillService.setIdBill(idBill);
        roomBillService.setIdRoom(idRoom);
        roomBillService.setIdService(idService);
        roomBillService.setPriceService(txtGiaDv.getText().trim());
        roomBillService.setPromotionService(txtGiamGiaDV.getText().trim());
        roomBillService.setDateofHire(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        JOptionPane.showMessageDialog(this, roomBillServiceService.insert(roomBillService));

    }//GEN-LAST:event_btnThemDvActionPerformed

    private void cbDichVuItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbDichVuItemStateChanged
        for (Service service : serviceService.getAll()) {
            if (cbDichVu.getSelectedItem().equals(service.getName())) {
                csNgaySd.setDate(new java.util.Date());
                txtMaDv.setText(service.getCode());
                txtGiaDv.setText(service.getPrice());
            }
        }
    }//GEN-LAST:event_cbDichVuItemStateChanged

    private void btnHuyPhongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHuyPhongActionPerformed
        Room room = new Room();
        fillRoom(room);
        if (txtSoPhong.isEnabled() == true) {
            temp = 0;
            txtSoPhong.setEnabled(false);
        }
        csTraPhong.setDate(new java.util.Date());
        txtGiaGiam.setText("0");
    }//GEN-LAST:event_btnHuyPhongActionPerformed

    private void jMenuThuePhongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuThuePhongActionPerformed
        PromotionRService promotionRService = new PromotionRService();
        Room room = roomService.getRoomByNumber(tenPhong).get(0);
        if (room.getStatus().equals("1")) {
            String ngay = String.valueOf(calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DATE));
            PromotionR pr = promotionRService.searchPromotionR(room.getIdPromotion(), ngay);
            if (pr != null) {
                txtGiaGiam.setText(pr.getValue());
            }
            jTabTrangChu.setSelectedIndex(1);
            fillRoom(room);
        } else {
            JOptionPane.showMessageDialog(this, "Phòng chưa sẵn sàng cho thuê");
            txtSoPhong.setText("");
        }
    }//GEN-LAST:event_jMenuThuePhongActionPerformed

    private void cbDichVuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cbDichVuMouseClicked
        loadCbDv();
    }//GEN-LAST:event_cbDichVuMouseClicked

    private void btnDxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDxActionPerformed
        if (JOptionPane.showConfirmDialog(this, "Muốn đăng xuất khỏi chương trình?", "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            QrCode.client = null;
            System.exit(0);
        }
    }//GEN-LAST:event_btnDxActionPerformed

    private void jMenuSSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSSActionPerformed
        jpanelTemp.setBackground(new java.awt.Color(204, 204, 255));
        Room room = new Room();
        room.setStatus("1");
        room.setRoomNumber(tenPhong);
        roomService.update(room, room.getRoomNumber());
        loadSl();
        loadPhongSS();
    }//GEN-LAST:event_jMenuSSActionPerformed

    private void jMenuCKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuCKActionPerformed
        jpanelTemp.setBackground(new java.awt.Color(204, 255, 255));
        Room room = new Room();
        room.setStatus("2");
        room.setRoomNumber(tenPhong);
        roomService.update(room, room.getRoomNumber());
        loadSl();
    }//GEN-LAST:event_jMenuCKActionPerformed

    private void jMenuCDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuCDActionPerformed
        jpanelTemp.setBackground(new java.awt.Color(204, 255, 204));
        Room room = new Room();
        room.setStatus("3");
        room.setRoomNumber(tenPhong);
        roomService.update(room, room.getRoomNumber());
        loadSl();
    }//GEN-LAST:event_jMenuCDActionPerformed

    private void jMenuDDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuDDActionPerformed
        jpanelTemp.setBackground(new java.awt.Color(221, 216, 216));
        Room room = new Room();
        room.setStatus("4");
        room.setRoomNumber(tenPhong);
        roomService.update(room, room.getRoomNumber());
        loadSl();
    }//GEN-LAST:event_jMenuDDActionPerformed

    private void jMenuSCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSCActionPerformed
        jpanelTemp.setBackground(new java.awt.Color(255, 153, 0));
        Room room = new Room();
        room.setStatus("5");
        room.setRoomNumber(tenPhong);
        roomService.update(room, room.getRoomNumber());
        loadSl();

    }//GEN-LAST:event_jMenuSCActionPerformed

    private void menuDichVuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDichVuActionPerformed
        Room room = roomService.getRoomByNumber(tenPhong).get(0);
        if (room.getStatus().equals("2")) {
            jTabTrangChu.setSelectedIndex(3);
            txtSoPhongDV.setText(tenPhong);
        } else {
            JOptionPane.showMessageDialog(this, "Phòng chưa được thuê.");
        }
    }//GEN-LAST:event_menuDichVuActionPerformed

    private void txt_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_idActionPerformed
        
    }//GEN-LAST:event_txt_idActionPerformed

    private void txt_codeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_codeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_codeActionPerformed

    private void txt_nameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_nameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_nameActionPerformed

    private void txt_priceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_priceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_priceActionPerformed

    private void txt_addressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_addressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_addressActionPerformed

    private void txt_statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_statusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_statusActionPerformed

    private void btn_themActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_themActionPerformed
     

    }//GEN-LAST:event_btn_themActionPerformed

    private void btn_suaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_suaActionPerformed
       
    }//GEN-LAST:event_btn_suaActionPerformed

    private void btn_xoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_xoaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_xoaActionPerformed

    private void jClickCheckoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jClickCheckoutMouseClicked
        // 
    }//GEN-LAST:event_jClickCheckoutMouseClicked
    public void SetdataCheckout(ThongtinCheckout tt){
        Tinhtien tinh = new Tinhtien();
        double tongtien = Double.parseDouble(tinh.tinhTienDv(tt.getIdbill()))
        +Double.parseDouble
        (tinh.tinhTienPhong(tinh.tiSoCheckOut(String.valueOf(new java.util.Date()),tt.getIdbill()),
        tinh.tiSoCheckIn(tt.getCheckin(), tt.getIdbill()), tt.getIdbill()));
        txtKhachhangTP.setText(tt.getTenkhachhang());
        txtCccdTP.setText(tt.getCCCD());
        txtMahdTP.setText(tt.getMaHD());
        txtSophongTP.setText(tt.getSophong());
        csCheckinTP.setDateFormatString(tt.getCheckin());
        csCheckoutTP.setDate(new java.util.Date());
        txtGiaphongTP.setText(tt.getGiaphong());
        txtGiamgiaTP.setText(tt.getGiamgia());
        if(tt.getTrangthaiHD()==0){
           cbbTrangthaiTP.setSelectedIndex(0);
        }
        if(tt.getTrangthaiHD()==1){
            cbbTrangthaiTP.setSelectedIndex(1);
        }
        txtThanhtienTP.setText(String.valueOf(tongtien));
        
        double phuthu= tinh.tiSoCheckOut(String.valueOf(new java.util.Date()),tt.getIdbill());
        if(phuthu==1.3){
            txtPhuthuTP.setText("30% tien phong 1 ngay");
        }else if(phuthu ==1.5){
            txtPhuthuTP.setText("50% tien phong 1 ngay");
        }else if(phuthu ==2.0){
            txtPhuthuTP.setText("100% tien phong 1 ngay");
        }else{
            txtPhuthuTP.setText("0");
        }
    }
 
    private void jClickCheckoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jClickCheckoutActionPerformed
        // Checkout
        CheckoutService cs = new CheckoutService();
        Room room = roomService.getRoomByNumber(tenPhong).get(0);
        System.out.println(room.getId());
        if(room.getStatus().equals("2")){
            jTabTrangChu.setSelectedIndex(2);
            //Hien thi bang DV:
            DefaultTableModel defaultTableModelds = (DefaultTableModel) tblDVTP.getModel();
            defaultTableModelds.setRowCount(0);
            for (DVcheckout x : cs.GetDVcheckout(room.getId())) {
                defaultTableModelds.addRow(new Object[]{
                x.getTen(),x.getMa(),x.getSoluong(),x.getGia(),x.getGiamgia()});
            }
            //Hien thi thong tin len form
            for(ThongtinCheckout tt :cs.Getthongtincheckout(room.getId())){
                SetdataCheckout(tt);
            }
            
        }
        else{
            JOptionPane.showMessageDialog(this, "Chua co khach de check out");
            return;
        }
        
    }//GEN-LAST:event_jClickCheckoutActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ViewTrangChu.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ViewTrangChu.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ViewTrangChu.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ViewTrangChu.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                new ViewTrangChu().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDoiPhong;
    private javax.swing.JButton btnDx;
    private javax.swing.JButton btnHuyDv;
    private javax.swing.JButton btnHuyPhong;
    private javax.swing.JButton btnQuetMa;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnThanhtoanTP;
    private javax.swing.JButton btnThemDv;
    private javax.swing.JButton btnThuePhong;
    private javax.swing.JButton btn_sua;
    private javax.swing.JButton btn_them;
    private javax.swing.JButton btn_xoa;
    private javax.swing.JComboBox<String> cbDichVu;
    private javax.swing.JComboBox<String> cbbTrangthaiTP;
    private com.toedter.calendar.JDateChooser csCheckinTP;
    private com.toedter.calendar.JDateChooser csCheckoutTP;
    private com.toedter.calendar.JDateChooser csNgaySd;
    private com.toedter.calendar.JDateChooser csNgaySinh;
    private com.toedter.calendar.JDateChooser csTraPhong;
    private javax.swing.JMenuItem jClickCheckout;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLbAll;
    private javax.swing.JLabel jLbCD;
    private javax.swing.JLabel jLbCK;
    private javax.swing.JLabel jLbDD;
    private javax.swing.JLabel jLbSC;
    private javax.swing.JLabel jLbSS;
    private javax.swing.JMenuItem jMenuCD;
    private javax.swing.JMenuItem jMenuCK;
    private javax.swing.JMenuItem jMenuDD;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuSC;
    private javax.swing.JMenuItem jMenuSS;
    private javax.swing.JMenuItem jMenuThuePhong;
    private javax.swing.JMenu jMenuTrangThai;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel37;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel39;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPnTang1;
    private javax.swing.JPanel jPnTang2;
    private javax.swing.JPanel jPnTang3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabTrangChu;
    private javax.swing.JLabel lbThoiGian;
    private javax.swing.JMenuItem menuDichVu;
    private javax.swing.JMenuItem menuThemPhong;
    private javax.swing.JPanel pnInforKh;
    private javax.swing.JPopupMenu popupPhong;
    private javax.swing.JPopupMenu popupTang;
    private javax.swing.JRadioButton rdNam;
    private javax.swing.JRadioButton rdNu;
    private javax.swing.JTable tbDsPhong;
    private javax.swing.JTable tbNoiThat;
    private javax.swing.JTable tbTTDichVu;
    private javax.swing.JTable tblDVTP;
    private javax.swing.JTable tblTTKhach;
    private javax.swing.JTable tbl_bill;
    private javax.swing.JTextField txtAreaRoom;
    private javax.swing.JTextField txtCCCD;
    private javax.swing.JTextField txtCccdTP;
    private javax.swing.JTextArea txtDiaChi;
    private javax.swing.JTextField txtGiaDv;
    private javax.swing.JTextField txtGiaGiam;
    private javax.swing.JTextField txtGiaPhong;
    private javax.swing.JTextField txtGiamGiaDV;
    private javax.swing.JTextField txtGiamgiaTP;
    private javax.swing.JTextField txtGiaphongTP;
    private javax.swing.JTextField txtKhachhangTP;
    private javax.swing.JTextField txtKindOfRoom;
    private javax.swing.JTextField txtLocationRoom;
    private javax.swing.JTextField txtMaDv;
    private javax.swing.JTextField txtMaKH;
    private javax.swing.JTextField txtMaPhong;
    private javax.swing.JTextField txtMahdTP;
    private javax.swing.JTextField txtPhuthuTP;
    private javax.swing.JTextField txtSDT;
    private javax.swing.JTextField txtSoPhong;
    private javax.swing.JTextField txtSoPhongDV;
    private javax.swing.JTextField txtSophongTP;
    private javax.swing.JLabel txtTenKS;
    private javax.swing.JTextField txtTenKhachHang;
    private javax.swing.JTextField txtThanhtienTP;
    private javax.swing.JTextField txt_address;
    private javax.swing.JTextField txt_code;
    private javax.swing.JTextField txt_date;
    private javax.swing.JTextField txt_id;
    private javax.swing.JTextField txt_name;
    private javax.swing.JTextField txt_price;
    private javax.swing.JTextField txt_status;
    // End of variables declaration//GEN-END:variables

}
