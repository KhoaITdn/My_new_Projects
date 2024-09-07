package OOP.QuanLySInhvien;
import java.util.Objects;

public class Sinhvien {
    private String maSinhVien;
    private String hoVaTen;
    private int namSinh;
    private float diemTrungBinh;


    public Sinhvien(String maSinhVien, String hoVaTen, int namSinh, float diemTrungBinh) {
        this.maSinhVien = maSinhVien;
        this.hoVaTen = hoVaTen;
        this.namSinh = namSinh;
        this.diemTrungBinh = diemTrungBinh;
    }


    public Sinhvien() {
    }

    public String getMaSinhVien() {
        return this.maSinhVien;
    }

    public void setMaSinhVien(String maSinhVien) {
        this.maSinhVien = maSinhVien;
    }

    public String getHoVaTen() {
        return this.hoVaTen;
    }

    public void setHoVaTen(String hoVaTen) {
        this.hoVaTen = hoVaTen;
    }

    public int getNamSinh() {
        return this.namSinh;
    }

    public void setNamSinh(int namSinh) {
        this.namSinh = namSinh;
    }

    public float getDiemTrungBinh() {
        return this.diemTrungBinh;
    }

    public void setDiemTrungBinh(float diemTrungBinh) {
        this.diemTrungBinh = diemTrungBinh;
    }

    public Sinhvien maSinhVien(String maSinhVien) {
        setMaSinhVien(maSinhVien);
        return this;
    }

    public Sinhvien hoVaTen(String hoVaTen) {
        setHoVaTen(hoVaTen);
        return this;
    }

    public Sinhvien namSinh(int namSinh) {
        setNamSinh(namSinh);
        return this;
    }

    public Sinhvien diemTrungBinh(float diemTrungBinh) {
        setDiemTrungBinh(diemTrungBinh);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Sinhvien)) {
            return false;
        }
        Sinhvien sinhvien = (Sinhvien) o;
        return Objects.equals(maSinhVien, sinhvien.maSinhVien) && Objects.equals(hoVaTen, sinhvien.hoVaTen) && namSinh == sinhvien.namSinh && diemTrungBinh == sinhvien.diemTrungBinh;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSinhVien, hoVaTen, namSinh, diemTrungBinh);
    }

    @Override
    public String toString() {
        return "{" +
            " maSinhVien='" + getMaSinhVien() + "'" +
            ", hoVaTen='" + getHoVaTen() + "'" +
            ", namSinh='" + getNamSinh() + "'" +
            ", diemTrungBinh='" + getDiemTrungBinh() + "'" +
            "}";
    }




}
