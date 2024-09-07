package OOP.QuanLySInhvien;

import java.util.ArrayList;

public class DanhSachSinhVien {
    private ArrayList <Sinhvien> danhSach;

    public DanhSachSinhVien() {
        this.danhSach = new ArrayList<Sinhvien> ();
    }

    public DanhSachSinhVien(ArrayList<Sinhvien> danhSach) {
        this.danhSach = danhSach;
    }

public void themSinhVien(Sinhvien sv){
    this.danhSach.add(sv);
}





}
