
package viewModel;


public class PhieuDV {
    String id;
    String ma;
    String ten;
    String gia;
    String giamgia;
    String ngaythue;
    String maphong;
    String maHD;

    public PhieuDV() {
    }

    public PhieuDV(String id, String ma, String ten, String gia, String giamgia, String ngaythue, String maphong, String maHD) {
        this.id = id;
        this.ma = ma;
        this.ten = ten;
        this.gia = gia;
        this.giamgia = giamgia;
        this.ngaythue = ngaythue;
        this.maphong = maphong;
        this.maHD = maHD;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getGia() {
        return gia;
    }

    public void setGia(String gia) {
        this.gia = gia;
    }

    public String getGiamgia() {
        return giamgia;
    }

    public void setGiamgia(String giamgia) {
        this.giamgia = giamgia;
    }

    public String getNgaythue() {
        return ngaythue;
    }

    public void setNgaythue(String ngaythue) {
        this.ngaythue = ngaythue;
    }

    public String getMaphong() {
        return maphong;
    }

    public void setMaphong(String maphong) {
        this.maphong = maphong;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }
    
}
