-- MySQL import for AppShopBanHang generated from app/src/main/assets/banhang.db
-- Target: XAMPP MySQL / MariaDB compatible schema
CREATE DATABASE IF NOT EXISTS `appshopbanhang` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `appshopbanhang`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `DanhGia`;
DROP TABLE IF EXISTS `Chitietdonhang`;
DROP TABLE IF EXISTS `Dathang`;
DROP TABLE IF EXISTS `sanpham`;
DROP TABLE IF EXISTS `nhomsanpham`;
DROP TABLE IF EXISTS `taikhoan`;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `taikhoan` (
  `tendn` VARCHAR(20) NOT NULL,
  `matkhau` VARCHAR(100) NOT NULL,
  `quyen` VARCHAR(50) NOT NULL DEFAULT 'user',
  PRIMARY KEY (`tendn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `nhomsanpham` (
  `maso` INT NOT NULL AUTO_INCREMENT,
  `tennsp` VARCHAR(200) NULL,
  `anh` VARCHAR(255) NULL,
  PRIMARY KEY (`maso`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `sanpham` (
  `masp` INT NOT NULL AUTO_INCREMENT,
  `tensp` VARCHAR(200) NULL,
  `dongia` DECIMAL(12,2) NULL,
  `mota` TEXT NULL,
  `ghichu` TEXT NULL,
  `soluongkho` INT NULL,
  `maso` INT NULL,
  `anh` VARCHAR(255) NULL,
  PRIMARY KEY (`masp`),
  INDEX `idx_sanpham_maso` (`maso`),
  CONSTRAINT `fk_sanpham_nhomsanpham` FOREIGN KEY (`maso`) REFERENCES `nhomsanpham` (`maso`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `Dathang` (
  `id_dathang` INT NOT NULL AUTO_INCREMENT,
  `tendn` VARCHAR(20) NULL,
  `tenkh` VARCHAR(255) NULL,
  `diachi` TEXT NULL,
  `sdt` VARCHAR(30) NULL,
  `tongthanhtoan` DECIMAL(12,2) NULL,
  `ngaydathang` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `trangthai` VARCHAR(100) NULL,
  `phuongthucthanhtoan` VARCHAR(30) NOT NULL DEFAULT 'COD',
  `trangthaithanhtoan` VARCHAR(30) NOT NULL DEFAULT 'UNPAID',
  `vnp_txn_ref` VARCHAR(100) NULL,
  `vnp_transaction_no` VARCHAR(100) NULL,
  `vnp_response_code` VARCHAR(20) NULL,
  `vnp_transaction_status` VARCHAR(20) NULL,
  `vnp_pay_date` DATETIME NULL,
  `vnp_bank_code` VARCHAR(30) NULL,
  `vnp_card_type` VARCHAR(30) NULL,
  PRIMARY KEY (`id_dathang`),
  INDEX `idx_dathang_tendn` (`tendn`),
  UNIQUE INDEX `ux_dathang_vnp_txn_ref` (`vnp_txn_ref`),
  CONSTRAINT `fk_dathang_taikhoan` FOREIGN KEY (`tendn`) REFERENCES `taikhoan` (`tendn`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `Chitietdonhang` (
  `id_chitiet` INT NOT NULL AUTO_INCREMENT,
  `id_dathang` INT NULL,
  `masp` INT NULL,
  `soluong` INT NULL,
  `dongia` DECIMAL(12,2) NULL,
  `anh` VARCHAR(255) NULL,
  PRIMARY KEY (`id_chitiet`),
  INDEX `idx_chitietdonhang_dathang` (`id_dathang`),
  INDEX `idx_chitietdonhang_sanpham` (`masp`),
  CONSTRAINT `fk_chitietdonhang_dathang` FOREIGN KEY (`id_dathang`) REFERENCES `Dathang` (`id_dathang`) ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_chitietdonhang_sanpham` FOREIGN KEY (`masp`) REFERENCES `sanpham` (`masp`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `DanhGia` (
  `id_danhgia` INT NOT NULL AUTO_INCREMENT,
  `masp` INT NULL,
  `id_chitiet` INT NULL,
  `noidung` TEXT NULL,
  `tendn` VARCHAR(20) NULL,
  `sao1` VARCHAR(20) NULL,
  `sao2` VARCHAR(20) NULL,
  `sao3` VARCHAR(20) NULL,
  `sao4` VARCHAR(20) NULL,
  `sao5` VARCHAR(20) NULL,
  PRIMARY KEY (`id_danhgia`),
  INDEX `idx_danhgia_masp` (`masp`),
  INDEX `idx_danhgia_tendn` (`tendn`),
  CONSTRAINT `fk_danhgia_sanpham` FOREIGN KEY (`masp`) REFERENCES `sanpham` (`masp`) ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT `fk_danhgia_chitietdonhang` FOREIGN KEY (`id_chitiet`) REFERENCES `Chitietdonhang` (`id_chitiet`) ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT `fk_danhgia_taikhoan` FOREIGN KEY (`tendn`) REFERENCES `taikhoan` (`tendn`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 0;
INSERT INTO `taikhoan` (`tendn`, `matkhau`, `quyen`) VALUES ('admin', '1234', 'admin');
INSERT INTO `taikhoan` (`tendn`, `matkhau`, `quyen`) VALUES ('khang', '111', 'user');
INSERT INTO `taikhoan` (`tendn`, `matkhau`, `quyen`) VALUES ('linh', '111', 'user');
INSERT INTO `taikhoan` (`tendn`, `matkhau`, `quyen`) VALUES ('bac', '111', 'user');
INSERT INTO `taikhoan` (`tendn`, `matkhau`, `quyen`) VALUES ('nhung', '111', 'user');
INSERT INTO `taikhoan` (`tendn`, `matkhau`, `quyen`) VALUES ('khue', '111', 'user');
INSERT INTO `nhomsanpham` (`maso`, `tennsp`, `anh`) VALUES (10, 'Cẩm Chướng', '/images/categories/category-10.png');
INSERT INTO `nhomsanpham` (`maso`, `tennsp`, `anh`) VALUES (11, 'Tú Cầu', '/images/categories/category-11.png');
INSERT INTO `nhomsanpham` (`maso`, `tennsp`, `anh`) VALUES (12, 'Hoa Hồng', '/images/categories/category-12.png');
INSERT INTO `nhomsanpham` (`maso`, `tennsp`, `anh`) VALUES (13, 'Hướng Dương', '/images/categories/category-13.png');
INSERT INTO `nhomsanpham` (`maso`, `tennsp`, `anh`) VALUES (14, 'Hoa Ly', '/images/categories/category-14.png');
INSERT INTO `nhomsanpham` (`maso`, `tennsp`, `anh`) VALUES (15, 'Hoa Nhí', '/images/categories/category-15.png');
INSERT INTO `nhomsanpham` (`maso`, `tennsp`, `anh`) VALUES (16, 'Hoa Cúc', '/images/categories/category-16.png');
INSERT INTO `nhomsanpham` (`maso`, `tennsp`, `anh`) VALUES (26, 'Râm Bụt', '/images/categories/category-26.png');
INSERT INTO `nhomsanpham` (`maso`, `tennsp`, `anh`) VALUES (27, 'Hoa Sen', '/images/categories/category-27.png');
INSERT INTO `nhomsanpham` (`maso`, `tennsp`, `anh`) VALUES (29, 'Hoa Tulip', '/images/categories/category-29.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (2, 'Bó Hoa Hồng Đỏ Tình Yêu Vĩnh Cửu', 55000.0, 'giá cả chất Lượng , phù hơp tui tiền', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 23, 12, '/images/products/product-2.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (3, 'Bó Hoa Hồng Đỏ Thanh Khiết', 77000.0, 'giá cả chất Lượng , phù hơp tui tiền', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 12, 12, '/images/products/product-3.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (4, 'Bó Hoa Hồng Hồng Dịu Dàng', 89000.0, 'giá cả chất Lượng , phù hơp tui tiền', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 5, 12, '/images/products/product-4.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (5, 'Bó Hoa Hồng Trắng Nồng Nàn', 190030.0, 'giá cả chất Lượng , phù hơp tui tiền', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 44, 12, '/images/products/product-5.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (7, 'Bó Hoa Hồng Phấn Ngọt Ngào', 110000.0, 'giá cả chất Lượng , phù hơp tui tiền', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 222, 12, '/images/products/product-7.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (8, 'Bó Hoa Hồng Vàng Ánh Dương', 23003.0, 'giá cả chất Lượng , phù hơp tui tiền', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 22, 12, '/images/products/product-8.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (9, 'Bó Hoa Hồng Phấn Ngọt Ngào', 35000.0, 'chất luợng luôn đặt lên hàng đầu', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 46, 12, '/images/products/product-9.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (10, 'Bó Hoa Hồng Lửa Đam Mê', 23450.0, 'chất luợng luôn đặt lên hàng đầu', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 22, 12, '/images/products/product-10.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (11, 'Bó Hoa Hồng Mix Nhí Sắc Màu Hạnh Phúc', 26000.0, 'chất luợng luôn đặt lên hàng đầu', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 444, 15, '/images/products/product-11.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (12, 'Bó Hoa Hứớng Dương Bình Yên', 23002.0, 'chất luợng luôn đặt lên hàng đầu', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 33, 13, '/images/products/product-12.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (13, 'Bó Hoa Hứớng dương Dễ Thương', 31000.0, 'chất luợng luôn đặt lên hàng đầu', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 4, 13, '/images/products/product-13.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (14, 'Bó Hoa Tulip Hồng Nhiệt Huyết', 30050.0, 'chất luợng luôn đặt lên hàng đầu', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 33, 29, '/images/products/product-14.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (15, 'Bó Hoa Quỳnh Trắng Tinh Khôi', 45004.0, 'chất luợng luôn đặt lên hàng đầu', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 34, 15, '/images/products/product-15.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (22, 'Bó Hoa Tulip Vàng Ánh Mặt Trời', 14000.0, 'chất luợng luôn đặt lên hàng đầu', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 34, 29, '/images/products/product-22.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (23, 'Bó Hoa Tulip Trắng Tinh Khôi', 16000.0, 'chất luợng luôn đặt lên hàng đầu', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 43453, 29, '/images/products/product-23.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (24, 'Bó Hoa Mix nhẹ nhàng', 67000.0, 'chất luợng luôn đặt lên hàng đầu', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 35, 15, '/images/products/product-24.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (25, 'Bó Hoa Cẩm Tú Cầu Hồng Dễ Thương', 17000.0, 'chất luợng luôn đặt lên hàng đầu', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 3435, 11, '/images/products/product-25.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (26, 'Bó Hoa Cẩm Tú Cầu Xanh Biển Bình Yên', 23000.0, 'chất luợng luôn đặt lên hàng đầu', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 345, 11, '/images/products/product-26.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (27, 'Bó Hoa Cẩm Tú Cầu Trắng Tinh Khiết', 25000.0, 'chất luợng luôn đặt lên hàng đầu', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 35, 11, '/images/products/product-27.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (28, 'Bó Hoa Cẩm Tú Cầu Tím Mộng Mơ', 45000.0, 'chất luợng luôn đặt lên hàng đầu', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 342, 11, '/images/products/product-28.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (29, 'Bó Hoa Sen Trắng Thanh Tịnh', 49000.0, 'chất luợng luôn đặt lên hàng đầu', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 5345, 27, '/images/products/product-29.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (30, 'Bó Hoa Sen Hồng Thuần Khiết', 41000.0, 'chất luợng luôn đặt lên hàng đầu', 'mang đên cho quý khách hàng trải nghiệm tôt nhất', 3423, 27, '/images/products/product-30.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (31, 'Bó Hoa Sen Vàng Phú Quý', 230000.0, 'yt', 'ẻt', 354, 27, '/images/products/product-31.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (32, 'Bó Hoa Ly Trắng Thanh Cao', 120000.0, 'gf', 'gfd', 436, 14, '/images/products/product-32.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (33, 'Hoa Hồng vàng mix hoa nhí', 140000.0, 'gfdg', 'gfd', 465, 15, '/images/products/product-33.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (34, 'Bó Hoa Ly Vàng Sang Trọng', 243000.0, 'hfhfg', 'hfgf', 456, 14, '/images/products/product-34.png');
INSERT INTO `sanpham` (`masp`, `tensp`, `dongia`, `mota`, `ghichu`, `soluongkho`, `maso`, `anh`) VALUES (35, 'Bó hoa hồng mix đồng tiền và hoa nhí nhẹ nhàng', 140000.0, 'hgfh', 'hfgf', 4574, 15, '/images/products/product-35.png');
INSERT INTO `Dathang` (`id_dathang`, `tendn`, `tenkh`, `diachi`, `sdt`, `tongthanhtoan`, `ngaydathang`, `trangthai`) VALUES (1, 'bac', 'bac', 'ha noi', '03897822', 294000.0, '2025-10-13 06:42:26', 'Đã Giao Cho DVVC');
INSERT INTO `Dathang` (`id_dathang`, `tendn`, `tenkh`, `diachi`, `sdt`, `tongthanhtoan`, `ngaydathang`, `trangthai`) VALUES (2, 'bac', 'bac', 'hai duong', '0892378922', 328050.0, '2025-10-13 06:42:57', 'Đang Chuẩn Bị Hàng');
INSERT INTO `Dathang` (`id_dathang`, `tendn`, `tenkh`, `diachi`, `sdt`, `tongthanhtoan`, `ngaydathang`, `trangthai`) VALUES (3, 'bac', 'bac', 'ha noi', '093287982', 66000.0, '2025-10-13 06:43:22', 'Đã Giao Cho DVVC');
INSERT INTO `Dathang` (`id_dathang`, `tendn`, `tenkh`, `diachi`, `sdt`, `tongthanhtoan`, `ngaydathang`, `trangthai`) VALUES (4, 'khang', 'khang', 'tuyen quang', '09878932', 227480.0, '2025-10-13 06:43:58', 'Đang Chuẩn Bị Hàng');
INSERT INTO `Dathang` (`id_dathang`, `tendn`, `tenkh`, `diachi`, `sdt`, `tongthanhtoan`, `ngaydathang`, `trangthai`) VALUES (5, 'khang', 'khang', 'tuyen quang', '098723892', 150050.0, '2025-10-13 06:44:22', 'Đã Giao Cho DVVC');
INSERT INTO `Dathang` (`id_dathang`, `tendn`, `tenkh`, `diachi`, `sdt`, `tongthanhtoan`, `ngaydathang`, `trangthai`) VALUES (6, 'nhung', 'nhung', 'ha noi', '098237223', 292000.0, '2025-10-13 06:44:58', 'Đã Giao Cho DVVC');
INSERT INTO `Dathang` (`id_dathang`, `tendn`, `tenkh`, `diachi`, `sdt`, `tongthanhtoan`, `ngaydathang`, `trangthai`) VALUES (7, 'nhung', 'nhung', 'hai duong', '089783233', 91000.0, '2025-10-13 06:45:29', 'Đơn Hàng Bị Hủy');
INSERT INTO `Dathang` (`id_dathang`, `tendn`, `tenkh`, `diachi`, `sdt`, `tongthanhtoan`, `ngaydathang`, `trangthai`) VALUES (8, 'khue', 'khue', 'ha tinh', '0897893232', 163500.0, '2025-10-13 06:52:15', 'Đang Chờ Duyệt');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (1, 1, 33, 1, 140000.0, '/images/order-items/order-item-1.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (2, 1, 35, 1, 140000.0, '/images/order-items/order-item-2.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (3, 1, 22, 1, 14000.0, '/images/order-items/order-item-3.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (4, 2, 2, 1, 55000.0, '/images/order-items/order-item-4.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (5, 2, 34, 1, 243000.0, '/images/order-items/order-item-5.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (6, 2, 14, 1, 30050.0, '/images/order-items/order-item-6.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (7, 3, 30, 1, 41000.0, '/images/order-items/order-item-7.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (8, 3, 27, 1, 25000.0, '/images/order-items/order-item-8.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (9, 4, 10, 1, 23450.0, '/images/order-items/order-item-9.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (10, 4, 5, 1, 190030.0, '/images/order-items/order-item-10.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (11, 4, 22, 1, 14000.0, '/images/order-items/order-item-11.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (12, 5, 14, 1, 30050.0, '/images/order-items/order-item-12.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (13, 5, 32, 1, 120000.0, '/images/order-items/order-item-13.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (14, 6, 26, 1, 23000.0, '/images/order-items/order-item-14.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (15, 6, 11, 1, 26000.0, '/images/order-items/order-item-15.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (16, 6, 34, 1, 243000.0, '/images/order-items/order-item-16.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (17, 7, 29, 1, 49000.0, '/images/order-items/order-item-17.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (18, 7, 25, 1, 17000.0, '/images/order-items/order-item-18.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (19, 7, 27, 1, 25000.0, '/images/order-items/order-item-19.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (20, 8, 14, 1, 30050.0, '/images/order-items/order-item-20.png');
INSERT INTO `Chitietdonhang` (`id_chitiet`, `id_dathang`, `masp`, `soluong`, `dongia`, `anh`) VALUES (21, 8, 7, 1, 110000.0, '/images/order-items/order-item-21.png');
INSERT INTO `DanhGia` (`id_danhgia`, `masp`, `id_chitiet`, `noidung`, `tendn`, `sao1`, `sao2`, `sao3`, `sao4`, `sao5`) VALUES (1, 33, 1, 'Hoa ở tiệm rất tươi, thơm lâu và được gói cực kỳ đẹp mắt', 'bac', 'star2', 'star2', 'star2', 'star2', 'star2');
INSERT INTO `DanhGia` (`id_danhgia`, `masp`, `id_chitiet`, `noidung`, `tendn`, `sao1`, `sao2`, `sao3`, `sao4`, `sao5`) VALUES (2, 35, 2, 'Nhân viên tư vấn nhiệt tình, giúp mình chọn được bó hoa phù hợp cho từng dịp.', 'bac', 'star2', 'star2', 'star2', 'star2', 'star1');
INSERT INTO `DanhGia` (`id_danhgia`, `masp`, `id_chitiet`, `noidung`, `tendn`, `sao1`, `sao2`, `sao3`, `sao4`, `sao5`) VALUES (3, 22, 3, 'Tiệm có nhiều loại hoa lạ và phối màu tinh tế, nhìn là mê luôn!', 'bac', 'star2', 'star2', 'star2', 'star2', 'star2');
INSERT INTO `DanhGia` (`id_danhgia`, `masp`, `id_chitiet`, `noidung`, `tendn`, `sao1`, `sao2`, `sao3`, `sao4`, `sao5`) VALUES (4, 30, 7, 'Dịch vụ giao hoa nhanh, đúng giờ và hoa đến nơi vẫn còn nguyên vẹn.', 'bac', 'star2', 'star2', 'star2', 'star2', 'star2');
INSERT INTO `DanhGia` (`id_danhgia`, `masp`, `id_chitiet`, `noidung`, `tendn`, `sao1`, `sao2`, `sao3`, `sao4`, `sao5`) VALUES (5, 27, 8, 'Giá cả hợp lý, chất lượng hoa vượt mong đợi.', 'bac', 'star2', 'star2', 'star2', 'star2', 'star1');
INSERT INTO `DanhGia` (`id_danhgia`, `masp`, `id_chitiet`, `noidung`, `tendn`, `sao1`, `sao2`, `sao3`, `sao4`, `sao5`) VALUES (6, 14, 12, 'Giá cả hợp lý, chất lượng hoa vượt mong đợi.', 'khang', 'star2', 'star2', 'star2', 'star2', 'star2');
INSERT INTO `DanhGia` (`id_danhgia`, `masp`, `id_chitiet`, `noidung`, `tendn`, `sao1`, `sao2`, `sao3`, `sao4`, `sao5`) VALUES (7, 32, 13, 'Tiệm trang trí hoa theo chủ đề rất chuyên nghiệp và sáng tạo.', 'khang', 'star2', 'star2', 'star2', 'star2', 'star1');
INSERT INTO `DanhGia` (`id_danhgia`, `masp`, `id_chitiet`, `noidung`, `tendn`, `sao1`, `sao2`, `sao3`, `sao4`, `sao5`) VALUES (8, 26, 14, 'Mình đặt hoa sinh nhật, bó hoa được gói sang trọng và cực kỳ tinh tế.', 'nhung', 'star2', 'star2', 'star2', 'star2', 'star2');
INSERT INTO `DanhGia` (`id_danhgia`, `masp`, `id_chitiet`, `noidung`, `tendn`, `sao1`, `sao2`, `sao3`, `sao4`, `sao5`) VALUES (9, 11, 15, 'Không gian tiệm sạch sẽ, thơm mùi hoa dễ chịu và nhân viên thân thiện.', 'nhung', 'star2', 'star2', 'star2', 'star2', 'star1');
INSERT INTO `DanhGia` (`id_danhgia`, `masp`, `id_chitiet`, `noidung`, `tendn`, `sao1`, `sao2`, `sao3`, `sao4`, `sao5`) VALUES (10, 34, 16, 'Hoa tươi lâu hơn mình tưởng, giữ được hơn 5 ngày vẫn đẹp.', 'nhung', 'star2', 'star2', 'star2', 'star2', 'star2');
SET FOREIGN_KEY_CHECKS = 1;

-- Row counts from source SQLite:
-- taikhoan: 6 rows
-- nhomsanpham: 10 rows
-- sanpham: 27 rows
-- Dathang: 8 rows
-- Chitietdonhang: 21 rows
-- DanhGia: 10 rows

-- Merged VNPAY migration:
-- Keeps this import file complete even when it is run against an older schema.
USE `appshopbanhang`;

ALTER TABLE `Dathang`
    ADD COLUMN IF NOT EXISTS `phuongthucthanhtoan` VARCHAR(30) NOT NULL DEFAULT 'COD',
    ADD COLUMN IF NOT EXISTS `trangthaithanhtoan` VARCHAR(30) NOT NULL DEFAULT 'UNPAID',
    ADD COLUMN IF NOT EXISTS `vnp_txn_ref` VARCHAR(100) NULL,
    ADD COLUMN IF NOT EXISTS `vnp_transaction_no` VARCHAR(100) NULL,
    ADD COLUMN IF NOT EXISTS `vnp_response_code` VARCHAR(20) NULL,
    ADD COLUMN IF NOT EXISTS `vnp_transaction_status` VARCHAR(20) NULL,
    ADD COLUMN IF NOT EXISTS `vnp_pay_date` DATETIME NULL,
    ADD COLUMN IF NOT EXISTS `vnp_bank_code` VARCHAR(30) NULL,
    ADD COLUMN IF NOT EXISTS `vnp_card_type` VARCHAR(30) NULL;

UPDATE `Dathang`
SET `phuongthucthanhtoan` = COALESCE(`phuongthucthanhtoan`, 'COD'),
    `trangthaithanhtoan` = COALESCE(`trangthaithanhtoan`, 'UNPAID');

-- Normalize Vietnamese Dong seed values:
-- Some imported bouquet prices missed one trailing zero, e.g. 14000 should be 140000.
UPDATE `sanpham`
SET `dongia` = `dongia` * 10
WHERE `dongia` > 0 AND `dongia` < 50000;

UPDATE `Chitietdonhang`
SET `dongia` = `dongia` * 10
WHERE `id_dathang` <= 8 AND `dongia` > 0 AND `dongia` < 50000;

UPDATE `Dathang` d
SET d.`tongthanhtoan` = (
    SELECT COALESCE(SUM(c.`soluong` * c.`dongia`), 0)
    FROM `Chitietdonhang` c
    WHERE c.`id_dathang` = d.`id_dathang`
)
WHERE d.`id_dathang` <= 8;
