# Kiem tra SQLite va nen tang MVVM

Ngay cap nhat: 2026-06-16

## 1. Workspace moi

Workspace Android dang dung:

`C:\Users\ADMIN\Downloads\AppShopBanHang`

Ly do doi:

- Path cu co ky tu tieng Viet co dau nen Android Gradle Plugin chan build tren Windows.
- Workspace moi la path ASCII ngan, da build debug thanh cong.

## 2. SQLite trong project

Trang thai truoc khi sua:

- Trong Android project khong co file `.db`.
- File SQLite mau `banhang.db` nam ngoai project, tai workspace cu.
- Neu cai app moi tren thiet bi khac, app co nguy co tao database rong.

Trang thai sau khi sua:

- Da copy database mau vao:
  - `app/src/main/assets/banhang.db`
- Da them `PrepackagedDatabaseCopier` de copy DB tu `assets` sang internal database lan dau app mo DB.
- `Database.java` va `DatabaseHelper.java` da dung chung:
  - `DatabaseConfig.DATABASE_NAME`
  - `DatabaseConfig.DATABASE_VERSION`

Schema DB mau:

- `taikhoan`: 6 rows
- `nhomsanpham`: 10 rows
- `sanpham`: 27 rows
- `Dathang`: 8 rows
- `Chitietdonhang`: 21 rows
- `DanhGia`: 10 rows
- `android_metadata`: 1 row
- `sqlite_sequence`: 5 rows

Can xu ly tiep:

- `Chitietdonhang.anh` trong DB dang la `TEXT`, nhung mot so code doc bang `getBlob`. Nen chot lai 1 kieu: `BLOB` neu luu anh binary, hoac `TEXT` neu luu path.
- Nen gom toan bo schema vao mot `DbContract` va migration ro rang.

## 3. Nen tang MVVM da them

Package moi sau khi sap xep lai thu muc:

- `core/database`
  - `CauHinhCoSoDuLieu`
  - `HopDongCoSoDuLieu`
  - `SaoChepCoSoDuLieuCoSan`
- `core/session`
  - `QuanLyPhien`
- `data/model`
  - `KetQuaDangNhap`
- `data/repository`
  - `DonHangRepository`
  - `SanPhamRepository`
  - `TaiKhoanRepository`
- `ui/auth`
  - `DangNhapViewModel`
  - `TrangThaiDangNhapUi`
- `ui/order`
  - `DanhSachDonHangViewModel`
- `ui/product`
  - `DanhSachSanPhamViewModel`

Luon refactor dau tien:

- `DangNhap_Activity` khong query SQLite truc tiep nua.
- `DangNhap_Activity` chi doc input, observe state va dieu huong.
- `TaiKhoanRepository` xu ly query dang nhap bang parameterized query.
- `DangNhapViewModel` goi repository va luu session qua `QuanLyPhien`.
- `TimKiemSanPham_Activity`, `TatCaSanPham_Activity`, `DanhMucSanPham_Activity` da doc san pham qua `DanhSachSanPhamViewModel`.
- `DonHang_User_Activity`, `DonHang_admin_Activity` da doc don hang qua `DanhSachDonHangViewModel`.

## 4. Build

Lenh da chay:

`.\gradlew.bat :app:assembleDebug`

Ket qua:

- `BUILD SUCCESSFUL`
- APK debug duoc tao thanh cong.
- Van con warning ve deprecated API tu code cu.

Lan build gan nhat sau refactor product/order:

- `.\gradlew.bat :app:assembleDebug`
- `BUILD SUCCESSFUL`

## 5. Flow tiep theo de refactor MVVM

De xuat thu tu:

1. Refactor `DangKiTaiKhoan_Activity` va `DoiMatKhau_Activity` sang `UserRepository` + ViewModel.
2. Tao `ProductRepository` cho san pham/nhom san pham.
3. Refactor trang chu user/admin doc san pham qua ViewModel.
4. Tao `OrderRepository` va doi flow dat hang sang transaction.
5. Tao `CartRepository` thay cho `GioHangManager` singleton RAM.
6. Tao `DbContract` gom ten bang/cot/status.
7. Chuyen cac query noi chuoi con lai sang parameterized query.
