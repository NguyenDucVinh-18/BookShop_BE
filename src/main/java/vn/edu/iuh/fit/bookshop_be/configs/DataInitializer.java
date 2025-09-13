package vn.edu.iuh.fit.bookshop_be.configs;

import vn.edu.iuh.fit.bookshop_be.models.Category;
import vn.edu.iuh.fit.bookshop_be.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem bảng categories đã có dữ liệu chưa
        if (categoryRepository.count() == 0) {
            // Danh mục cấp 1
            Category sachTrongNuoc = new Category("Sách Trong Nước", "Danh mục sách trong nước");
            Category foreignBooks = new Category("FOREIGN BOOKS", "Danh mục sách ngoại văn");
            Category vpp = new Category("VPP - Dụng Cụ Học Sinh", "Danh mục dụng cụ học sinh");
            Category doChoi = new Category("Đồ Chơi", "Danh mục đồ chơi");
            Category lamDep = new Category("Làm Đẹp - Sức Khỏe", "Danh mục làm đẹp và sức khỏe");
            Category sachGiaoKhoa = new Category("Sách Giáo Khoa 2025", "Danh mục sách giáo khoa năm 2025");
            Category vppDchs = new Category("VPP - DCHS Theo Thống Hiệu", "Danh mục dụng cụ học sinh theo thống hiệu");
            Category doChoiTheoThongHieu = new Category("Đồ Chơi Theo Thống Hiệu", "Danh mục đồ chơi theo thống hiệu");
            Category bachHoaOnline = new Category("Bách Hóa Online - Lưu Niệm", "Danh mục bách hóa online và lưu niệm");

            // Danh mục con của Sách Trong Nước
            Category vanHoc = new Category("VĂN HỌC", "Sách văn học");
            Category kinhTe = new Category("KINH TẾ", "Sách kinh tế");
            Category tamLyKiNangSong = new Category("TÂM LÝ - KỸ NĂNG SỐNG", "Sách tâm lý và kỹ năng sống");
            Category nuoiDayCon = new Category("NUÔI DẠY CON", "Sách nuôi dạy con");
            Category sachThieuNhi = new Category("SÁCH THIẾU NHI", "Sách thiếu nhi");
            Category tiepSuHoiKy = new Category("TIẾU SỬ - HỒI KÝ", "Sách tiểu sử và hồi ký");
            Category giaoKhoaThamKhao = new Category("GIÁO KHOA - THAM KHẢO", "Sách giáo khoa và tham khảo");
            Category sachHocNgoaiNgu = new Category("SÁCH HỌC NGOẠI NGỮ", "Sách học ngoại ngữ");
            Category sachMoi = new Category("SÁCH MỚI", "Sách mới");
            Category sachBanChay = new Category("SÁCH BÁN CHẠY", "Sách bán chạy");

            // Danh mục con của VPP - Dụng Cụ Học Sinh
            Category butViet = new Category("BÚT - VIẾT", "Danh mục bút và dụng cụ viết");
            Category dungCuHocSinh = new Category("DỤNG CỤ HỌC SINH", "Danh mục dụng cụ học sinh");
            Category dungCuVanPhong = new Category("DỤNG CỤ VĂN PHÒNG", "Danh mục dụng cụ văn phòng");
            Category dungCuVe = new Category("DỤNG CỤ VẼ", "Danh mục dụng cụ vẽ");
            Category sanPhamVeGiai = new Category("SẢN PHẨM VẼ GIẢI", "Danh mục sản phẩm vẽ giải");
            Category sanPhamKhac = new Category("SẢN PHẨM KHÁC", "Danh mục sản phẩm khác");
            Category sanPhamDienTu = new Category("SẢN PHẨM ĐIỆN TỬ", "Danh mục sản phẩm điện tử");
            Category sanPhamMoi = new Category("SẢN PHẨM MỚI", "Danh mục sản phẩm mới");
            Category sanPhamBanChay = new Category("SẢN PHẨM BÁN CHẠY", "Danh mục sản phẩm bán chạy");

            // Liên kết danh mục con với Sách Trong Nước
            sachTrongNuoc.getSubCategories().add(vanHoc);
            sachTrongNuoc.getSubCategories().add(kinhTe);
            sachTrongNuoc.getSubCategories().add(tamLyKiNangSong);
            sachTrongNuoc.getSubCategories().add(nuoiDayCon);
            sachTrongNuoc.getSubCategories().add(sachThieuNhi);
            sachTrongNuoc.getSubCategories().add(tiepSuHoiKy);
            sachTrongNuoc.getSubCategories().add(giaoKhoaThamKhao);
            sachTrongNuoc.getSubCategories().add(sachHocNgoaiNgu);
            sachTrongNuoc.getSubCategories().add(sachMoi);
            sachTrongNuoc.getSubCategories().add(sachBanChay);

            // Đặt parentCategory cho các danh mục con của Sách Trong Nước
            vanHoc.setParentCategory(sachTrongNuoc);
            kinhTe.setParentCategory(sachTrongNuoc);
            tamLyKiNangSong.setParentCategory(sachTrongNuoc);
            nuoiDayCon.setParentCategory(sachTrongNuoc);
            sachThieuNhi.setParentCategory(sachTrongNuoc);
            tiepSuHoiKy.setParentCategory(sachTrongNuoc);
            giaoKhoaThamKhao.setParentCategory(sachTrongNuoc);
            sachHocNgoaiNgu.setParentCategory(sachTrongNuoc);
            sachMoi.setParentCategory(sachTrongNuoc);
            sachBanChay.setParentCategory(sachTrongNuoc);

            // Liên kết danh mục con với VPP - Dụng Cụ Học Sinh
            vpp.getSubCategories().add(butViet);
            vpp.getSubCategories().add(dungCuHocSinh);
            vpp.getSubCategories().add(dungCuVanPhong);
            vpp.getSubCategories().add(dungCuVe);
            vpp.getSubCategories().add(sanPhamVeGiai);
            vpp.getSubCategories().add(sanPhamKhac);
            vpp.getSubCategories().add(sanPhamDienTu);
            vpp.getSubCategories().add(sanPhamMoi);
            vpp.getSubCategories().add(sanPhamBanChay);

            // Đặt parentCategory cho các danh mục con của VPP - Dụng Cụ Học Sinh
            butViet.setParentCategory(vpp);
            dungCuHocSinh.setParentCategory(vpp);
            dungCuVanPhong.setParentCategory(vpp);
            dungCuVe.setParentCategory(vpp);
            sanPhamVeGiai.setParentCategory(vpp);
            sanPhamKhac.setParentCategory(vpp);
            sanPhamDienTu.setParentCategory(vpp);
            sanPhamMoi.setParentCategory(vpp);
            sanPhamBanChay.setParentCategory(vpp);

            // Lưu tất cả danh mục vào cơ sở dữ liệu
            categoryRepository.save(sachTrongNuoc);
            categoryRepository.save(foreignBooks);
            categoryRepository.save(vpp);
            categoryRepository.save(doChoi);
            categoryRepository.save(lamDep);
            categoryRepository.save(sachGiaoKhoa);
            categoryRepository.save(vppDchs);
            categoryRepository.save(doChoiTheoThongHieu);
            categoryRepository.save(bachHoaOnline);
        }
    }
}