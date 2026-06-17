# Cau truc MVVM hien tai

Ngay cap nhat: 2026-06-16

## Workspace dang dung

Chi lam viec trong:

`C:\Users\ADMIN\Downloads\AppShopBanHang`

## Quy uoc ten

- Package/folder dung tieng Anh theo convention Android: `core`, `data`, `ui`.
- Ten file/class nghiep vu dung tieng Viet khong dau khi hop ly.
- Cac hau to ky thuat nhu `Activity`, `Adapter`, `ViewModel`, `Repository` duoc giu de de nhan biet vai tro file.

Vi du da doi:

- `Database.java` -> `CoSoDuLieu.java`
- `DatabaseHelper.java` -> `TroGiupCoSoDuLieu.java`
- `DatabaseConfig.java` -> `CauHinhCoSoDuLieu.java`
- `DbContract.java` -> `HopDongCoSoDuLieu.java`
- `PrepackagedDatabaseCopier.java` -> `SaoChepCoSoDuLieuCoSan.java`
- `SessionManager.java` -> `QuanLyPhien.java`
- `Login_Activity.java` -> `DangNhap_Activity.java`
- `LoginViewModel.java` -> `DangNhapViewModel.java`
- `LoginResult.java` -> `KetQuaDangNhap.java`
- `Order.java` -> `DonHang.java`
- `OrderRepository.java` -> `DonHangRepository.java`
- `ProductRepository.java` -> `SanPhamRepository.java`
- `UserRepository.java` -> `TaiKhoanRepository.java`
- `ChatBox_Actitvity.java` -> `HoTro_Activity.java`

## Cay thu muc chinh

```text
app/src/main/java/com/example/appshopbanhang/
├── core/
│   ├── database/
│   └── session/
├── data/
│   ├── model/
│   └── repository/
└── ui/
    ├── account/
    ├── adapter/
    ├── auth/
    ├── cart/
    ├── category/
    ├── home/
    ├── launch/
    ├── order/
    ├── product/
    └── support/
```

## Man hinh da refactor sang MVVM

- `ui/auth/DangNhap_Activity`
  - ViewModel: `DangNhapViewModel`
  - Repository: `data/repository/TaiKhoanRepository`

- `ui/product/TimKiemSanPham_Activity`
- `ui/product/TatCaSanPham_Activity`
- `ui/product/DanhMucSanPham_Activity`
  - ViewModel: `DanhSachSanPhamViewModel`
  - Repository: `data/repository/SanPhamRepository`

- `ui/order/DonHang_User_Activity`
- `ui/order/DonHang_admin_Activity`
  - ViewModel: `DanhSachDonHangViewModel`
  - Repository: `data/repository/DonHangRepository`

## SQLite

- DB mau nam trong `app/src/main/assets/banhang.db`.
- `core/database/SaoChepCoSoDuLieuCoSan` copy DB mau vao internal database khi app mo DB lan dau.
- `core/database/HopDongCoSoDuLieu` gom ten bang/cot cho query moi.

## Kiem tra build

Lenh da chay:

`.\gradlew.bat :app:assembleDebug`

Ket qua:

- `BUILD SUCCESSFUL`
- Package/import/Manifest da cap nhat theo cau truc package English.

## Viec con lai

- Neu muon Viet hoa them ten file, nen lam theo tung nhom: adapter, activity admin, manager cu.
- Refactor `TrangchuNgdung_Activity` va `TrangchuAdmin_Activity` sang `HomeViewModel`.
- Refactor gio hang: thay `GioHangManager` singleton RAM bang `CartRepository`.
- Refactor dat hang: tao order trong transaction va tru ton kho.
- Refactor admin san pham/nhom/tai khoan sang repository rieng.
- Chuan hoa schema `Chitietdonhang.anh` dang `TEXT` nhung code doc nhu `BLOB`.
