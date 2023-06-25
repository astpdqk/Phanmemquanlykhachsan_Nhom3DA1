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
 
    private void jClickCheckoutActionPerformed(java.awt.event.ActionEvent evt) {                                               
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
        
    }                   