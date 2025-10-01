package vn.edu.iuh.fit.bookshop_be.configs;

import vn.edu.iuh.fit.bookshop_be.models.Category;
import vn.edu.iuh.fit.bookshop_be.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            // ==== DANH MỤC CHA ====
            Category vanHoc = new Category("VĂN HỌC", "Danh mục văn học");
            Category kinhTe = new Category("KINH TẾ", "Danh mục kinh tế");
            Category tamLyKyNangSong = new Category("TÂM LÝ - KỸ NĂNG SỐNG", "Danh mục tâm lý - kỹ năng sống");
            Category nuoiDayCon = new Category("NUÔI DẠY CON", "Danh mục nuôi dạy con");
            Category sachThieuNhi = new Category("SÁCH THIẾU NHI", "Danh mục sách thiếu nhi");
            Category tieuSuHoiKy = new Category("TIỂU SỬ - HỒI KÝ", "Danh mục tiểu sử - hồi ký");
            Category giaoKhoa = new Category("GIÁO KHOA - THAM KHẢO", "Danh mục giáo khoa - tham khảo");
            Category sachNgoaiNgu = new Category("SÁCH HỌC NGOẠI NGỮ", "Danh mục sách học ngoại ngữ");
            Category dungCuHocSinh = new Category("DỤng cụ học sinh", "Dụng cụ học sinh");

            // ==== DANH MỤC CON ====
            // Văn Học
            Category tieuThuyet = new Category("Tiểu Thuyết", "Sách tiểu thuyết");
            Category truyenNgan = new Category("Truyện Ngắn - Tản Văn", "Sách truyện ngắn - tản văn");
            Category lightNovel = new Category("Light Novel", "Sách light novel");
            Category ngonTinh = new Category("Ngôn Tình", "Sách ngôn tình");

            // Kinh Tế
            Category nhanVat = new Category("Nhân Vật - Bài Học Kinh Doanh", "Sách kinh doanh và nhân vật");
            Category quanTri = new Category("Quản Trị - Lãnh Đạo", "Sách quản trị lãnh đạo");
            Category marketing = new Category("Marketing - Bán Hàng", "Sách marketing - bán hàng");
            Category phanTich = new Category("Phân Tích Kinh Tế", "Sách phân tích kinh tế");

            // Tâm Lý - Kỹ Năng Sống
            Category kyNangSong = new Category("Kỹ Năng Sống", "Sách kỹ năng sống");
            Category renLuyenNhanCach = new Category("Rèn Luyện Nhân Cách", "Sách rèn luyện nhân cách");
            Category tamLy = new Category("Tâm Lý", "Sách tâm lý");
            Category sachTuoiMoiLon = new Category("Sách Cho Tuổi Mới Lớn", "Sách dành cho tuổi mới lớn");

            // Nuôi Dạy Con
            Category camNangChaMe = new Category("Cẩm Nang Làm Cha Mẹ", "Sách cẩm nang cho cha mẹ");
            Category phuongPhapGd = new Category("Phương Pháp Giáo Dục Trẻ", "Phương pháp giáo dục trẻ");
            Category phatTrienTriTue = new Category("Phát Triển Trí Tuệ Cho Trẻ", "Sách phát triển trí tuệ cho trẻ");
            Category kyNangTre = new Category("Phát Triển Kỹ Năng Cho Trẻ", "Sách phát triển kỹ năng cho trẻ");

            // Sách Thiếu Nhi
            Category manga = new Category("Manga - Comic", "Sách truyện tranh, comic");
            Category kienThucBachKhoa = new Category("Kiến Thức Bách Khoa", "Sách kiến thức bách khoa");
            Category sachTranh = new Category("Sách Tranh Kỹ Năng Sống", "Sách tranh kỹ năng sống");
            Category vuaHocVuaChoi = new Category("Vừa Học - Vừa Chơi", "Sách thiếu nhi vừa học vừa chơi");

            // Tiểu Sử - Hồi Ký
            Category cauChuyen = new Category("Câu Chuyện Cuộc Đời", "Sách kể chuyện cuộc đời");
            Category chinhTri = new Category("Chính Trị", "Sách chính trị");
            // 🔥 Đổi tên để không trùng slug với "KINH TẾ"
            Category kinhTeHoiKy = new Category("Kinh Tế Hồi Ký", "Sách kinh tế hồi ký");
            Category ngheThuat = new Category("Nghệ Thuật - Giải Trí", "Sách nghệ thuật - giải trí");

            // Giáo Khoa - Tham Khảo
            Category sachGiaoKhoa = new Category("Sách Giáo Khoa", "Sách giáo khoa");
            Category sachThamKhao = new Category("Sách Tham Khảo", "Sách tham khảo");
            Category luyenThi = new Category("Luyện Thi THPT Quốc Gia", "Sách luyện thi THPT quốc gia");
            Category mauGiao = new Category("Mẫu Giáo", "Sách mẫu giáo");

            // Sách Học Ngoại Ngữ
            Category tiengAnh = new Category("Tiếng Anh", "Sách học tiếng Anh");
            Category tiengNhat = new Category("Tiếng Nhật", "Sách học tiếng Nhật");
            Category tiengHoa = new Category("Tiếng Hoa", "Sách học tiếng Hoa");
            Category tiengHan = new Category("Tiếng Hàn", "Sách học tiếng Hàn");

            // Dụng cụ học sinh
            Category gomTay = new Category("Gôm - Tẩy", "Gôm tẩy");
            Category gotButChi = new Category("Gọt Bút Chì", "Gọt bút chì");
            Category thuoc = new Category("Thước Học Sinh", "Thước");
            Category boDungCu = new Category("Bộ Dụng Cụ Học Tập", "Bộ dụng cụ học tập");

            // ==== LIÊN KẾT CHA - CON ====
            vanHoc.getSubCategories().addAll(List.of(tieuThuyet, truyenNgan, lightNovel, ngonTinh));
            tieuThuyet.setParentCategory(vanHoc);
            truyenNgan.setParentCategory(vanHoc);
            lightNovel.setParentCategory(vanHoc);
            ngonTinh.setParentCategory(vanHoc);

            kinhTe.getSubCategories().addAll(List.of(nhanVat, quanTri, marketing, phanTich));
            nhanVat.setParentCategory(kinhTe);
            quanTri.setParentCategory(kinhTe);
            marketing.setParentCategory(kinhTe);
            phanTich.setParentCategory(kinhTe);

            tamLyKyNangSong.getSubCategories().addAll(List.of(kyNangSong, renLuyenNhanCach, tamLy, sachTuoiMoiLon));
            kyNangSong.setParentCategory(tamLyKyNangSong);
            renLuyenNhanCach.setParentCategory(tamLyKyNangSong);
            tamLy.setParentCategory(tamLyKyNangSong);
            sachTuoiMoiLon.setParentCategory(tamLyKyNangSong);

            nuoiDayCon.getSubCategories().addAll(List.of(camNangChaMe, phuongPhapGd, phatTrienTriTue, kyNangTre));
            camNangChaMe.setParentCategory(nuoiDayCon);
            phuongPhapGd.setParentCategory(nuoiDayCon);
            phatTrienTriTue.setParentCategory(nuoiDayCon);
            kyNangTre.setParentCategory(nuoiDayCon);

            sachThieuNhi.getSubCategories().addAll(List.of(manga, kienThucBachKhoa, sachTranh, vuaHocVuaChoi));
            manga.setParentCategory(sachThieuNhi);
            kienThucBachKhoa.setParentCategory(sachThieuNhi);
            sachTranh.setParentCategory(sachThieuNhi);
            vuaHocVuaChoi.setParentCategory(sachThieuNhi);

            tieuSuHoiKy.getSubCategories().addAll(List.of(cauChuyen, chinhTri, kinhTeHoiKy, ngheThuat));
            cauChuyen.setParentCategory(tieuSuHoiKy);
            chinhTri.setParentCategory(tieuSuHoiKy);
            kinhTeHoiKy.setParentCategory(tieuSuHoiKy);
            ngheThuat.setParentCategory(tieuSuHoiKy);

            giaoKhoa.getSubCategories().addAll(List.of(sachGiaoKhoa, sachThamKhao, luyenThi, mauGiao));
            sachGiaoKhoa.setParentCategory(giaoKhoa);
            sachThamKhao.setParentCategory(giaoKhoa);
            luyenThi.setParentCategory(giaoKhoa);
            mauGiao.setParentCategory(giaoKhoa);

            sachNgoaiNgu.getSubCategories().addAll(List.of(tiengAnh, tiengNhat, tiengHoa, tiengHan));
            tiengAnh.setParentCategory(sachNgoaiNgu);
            tiengNhat.setParentCategory(sachNgoaiNgu);
            tiengHoa.setParentCategory(sachNgoaiNgu);
            tiengHan.setParentCategory(sachNgoaiNgu);

            dungCuHocSinh.getSubCategories().addAll(List.of(gomTay, gotButChi, thuoc, boDungCu));
            gomTay.setParentCategory(dungCuHocSinh);
            gotButChi.setParentCategory(dungCuHocSinh);
            thuoc.setParentCategory(dungCuHocSinh);
            boDungCu.setParentCategory(dungCuHocSinh);

            // ==== LƯU DATABASE ====
            categoryRepository.saveAll(List.of(
                    vanHoc, kinhTe, tamLyKyNangSong, nuoiDayCon,
                    sachThieuNhi, tieuSuHoiKy, giaoKhoa,
                    sachNgoaiNgu, dungCuHocSinh
            ));
        }
    }
}
