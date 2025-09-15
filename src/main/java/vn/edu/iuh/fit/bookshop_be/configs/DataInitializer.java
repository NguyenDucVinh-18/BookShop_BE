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
            Category kinhTeHoiKy = new Category("Kinh Tế", "Sách kinh tế hồi ký");
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

            // ==== LIÊN KẾT CHA - CON ====
            // Văn Học
            vanHoc.getSubCategories().add(tieuThuyet);
            vanHoc.getSubCategories().add(truyenNgan);
            vanHoc.getSubCategories().add(lightNovel);
            vanHoc.getSubCategories().add(ngonTinh);
            tieuThuyet.setParentCategory(vanHoc);
            truyenNgan.setParentCategory(vanHoc);
            lightNovel.setParentCategory(vanHoc);
            ngonTinh.setParentCategory(vanHoc);

            // Kinh Tế
            kinhTe.getSubCategories().add(nhanVat);
            kinhTe.getSubCategories().add(quanTri);
            kinhTe.getSubCategories().add(marketing);
            kinhTe.getSubCategories().add(phanTich);
            nhanVat.setParentCategory(kinhTe);
            quanTri.setParentCategory(kinhTe);
            marketing.setParentCategory(kinhTe);
            phanTich.setParentCategory(kinhTe);

            // Tâm Lý - Kỹ Năng Sống
            tamLyKyNangSong.getSubCategories().add(kyNangSong);
            tamLyKyNangSong.getSubCategories().add(renLuyenNhanCach);
            tamLyKyNangSong.getSubCategories().add(tamLy);
            tamLyKyNangSong.getSubCategories().add(sachTuoiMoiLon);
            kyNangSong.setParentCategory(tamLyKyNangSong);
            renLuyenNhanCach.setParentCategory(tamLyKyNangSong);
            tamLy.setParentCategory(tamLyKyNangSong);
            sachTuoiMoiLon.setParentCategory(tamLyKyNangSong);

            // Nuôi Dạy Con
            nuoiDayCon.getSubCategories().add(camNangChaMe);
            nuoiDayCon.getSubCategories().add(phuongPhapGd);
            nuoiDayCon.getSubCategories().add(phatTrienTriTue);
            nuoiDayCon.getSubCategories().add(kyNangTre);
            camNangChaMe.setParentCategory(nuoiDayCon);
            phuongPhapGd.setParentCategory(nuoiDayCon);
            phatTrienTriTue.setParentCategory(nuoiDayCon);
            kyNangTre.setParentCategory(nuoiDayCon);

            // Sách Thiếu Nhi
            sachThieuNhi.getSubCategories().add(manga);
            sachThieuNhi.getSubCategories().add(kienThucBachKhoa);
            sachThieuNhi.getSubCategories().add(sachTranh);
            sachThieuNhi.getSubCategories().add(vuaHocVuaChoi);
            manga.setParentCategory(sachThieuNhi);
            kienThucBachKhoa.setParentCategory(sachThieuNhi);
            sachTranh.setParentCategory(sachThieuNhi);
            vuaHocVuaChoi.setParentCategory(sachThieuNhi);

            // Tiểu Sử - Hồi Ký
            tieuSuHoiKy.getSubCategories().add(cauChuyen);
            tieuSuHoiKy.getSubCategories().add(chinhTri);
            tieuSuHoiKy.getSubCategories().add(kinhTeHoiKy);
            tieuSuHoiKy.getSubCategories().add(ngheThuat);
            cauChuyen.setParentCategory(tieuSuHoiKy);
            chinhTri.setParentCategory(tieuSuHoiKy);
            kinhTeHoiKy.setParentCategory(tieuSuHoiKy);
            ngheThuat.setParentCategory(tieuSuHoiKy);

            // Giáo Khoa - Tham Khảo
            giaoKhoa.getSubCategories().add(sachGiaoKhoa);
            giaoKhoa.getSubCategories().add(sachThamKhao);
            giaoKhoa.getSubCategories().add(luyenThi);
            giaoKhoa.getSubCategories().add(mauGiao);
            sachGiaoKhoa.setParentCategory(giaoKhoa);
            sachThamKhao.setParentCategory(giaoKhoa);
            luyenThi.setParentCategory(giaoKhoa);
            mauGiao.setParentCategory(giaoKhoa);

            // Sách Học Ngoại Ngữ
            sachNgoaiNgu.getSubCategories().add(tiengAnh);
            sachNgoaiNgu.getSubCategories().add(tiengNhat);
            sachNgoaiNgu.getSubCategories().add(tiengHoa);
            sachNgoaiNgu.getSubCategories().add(tiengHan);
            tiengAnh.setParentCategory(sachNgoaiNgu);
            tiengNhat.setParentCategory(sachNgoaiNgu);
            tiengHoa.setParentCategory(sachNgoaiNgu);
            tiengHan.setParentCategory(sachNgoaiNgu);

            // ==== LƯU DATABASE ====
            categoryRepository.save(vanHoc);
            categoryRepository.save(kinhTe);
            categoryRepository.save(tamLyKyNangSong);
            categoryRepository.save(nuoiDayCon);
            categoryRepository.save(sachThieuNhi);
            categoryRepository.save(tieuSuHoiKy);
            categoryRepository.save(giaoKhoa);
            categoryRepository.save(sachNgoaiNgu);
        }
    }
}
