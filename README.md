# AppShopBanHang Web Admin

Backend Spring Boot va web admin nam trong cung workspace voi Android project.

## Cong nghe

- Java 21
- Spring Boot 3.4.5
- Spring MVC + Thymeleaf
- Spring Data JPA
- MySQL/MariaDB XAMPP
- Maven

## Database

File import MySQL:

`C:\Users\ADMIN\Downloads\AppShopBanHang\database\mysql\appshopbanhang_mysql.sql`

Anh san pham va danh muc khong nhung truc tiep trong SQL. SQL chi luu URL anh trong cot `anh`, con file anh nam tai:

`C:\Users\ADMIN\Downloads\AppShopBanHang\web-admin\uploads\images`

Backend phuc vu thu muc nay qua URL `/images/**`.

Database mac dinh:

- Database: `appshopbanhang`
- Host: `localhost`
- Port: `3306`
- User: `root`
- Password: rong theo mac dinh XAMPP

Lenh import:

```powershell
cmd /c ""C:\xampp\mysql\bin\mysql.exe" -u root < "C:\Users\ADMIN\Downloads\AppShopBanHang\database\mysql\appshopbanhang_mysql.sql""
```

## Chay backend

```powershell
cd C:\Users\ADMIN\Downloads\AppShopBanHang\web-admin
mvn spring-boot:run
```

Hoac chay file jar da build:

```powershell
java -jar target\web-admin-0.0.1-SNAPSHOT.jar
```

Web admin:

`http://localhost:8080/admin/login`

Tai khoan demo:

- Username: `admin`
- Password: `1234`

## Chuc nang web admin

- Dashboard tong quan.
- Quan ly tai khoan.
- Quan ly danh muc san pham.
- Quan ly san pham va anh.
- Quan ly don hang va cap nhat trang thai.
- Xem/xoa danh gia.

## API cho Android app user

Auth:

- `POST /api/auth/login`

Danh muc va san pham:

- `GET /api/categories`
- `GET /api/categories/{id}/image`
- `GET /api/products`
- `GET /api/products?keyword=hoa`
- `GET /api/products?categoryId=12`
- `GET /api/products/{id}`
- `GET /api/products/{id}/image`

Don hang:

- `GET /api/orders?username=bac`
- `GET /api/orders/{id}`
- `POST /api/orders`

Body tao don hang:

```json
{
  "username": "bac",
  "customerName": "bac",
  "address": "ha noi",
  "phone": "03897822",
  "items": [
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

Danh gia:

- `GET /api/reviews?productId=2`
- `POST /api/reviews`

Body gui danh gia:

```json
{
  "productId": 2,
  "orderDetailId": 1,
  "username": "bac",
  "content": "San pham dep",
  "star1": "star2",
  "star2": "star2",
  "star3": "star2",
  "star4": "star2",
  "star5": "star2"
}
```

## VNPAY sandbox

Backend da cau hinh VNPAY sandbox trong `src/main/resources/application.properties`.

Thong tin dang dung:

- Return URL mac dinh: `http://localhost:8080/api/payments/vnpay/return`
- IPN URL mac dinh: `http://localhost:8080/api/payments/vnpay/ipn`

API tao thanh toan cho don hang da ton tai:

- `POST /api/payments/vnpay/create`

Body:

```json
{
  "orderId": 1,
  "bankCode": "NCB",
  "locale": "vn"
}
```

API tao don hang moi va tao link thanh toan VNPAY:

- `POST /api/payments/vnpay/create-order`

Body:

```json
{
  "username": "bac",
  "customerName": "bac",
  "address": "ha noi",
  "phone": "03897822",
  "bankCode": "NCB",
  "locale": "vn",
  "items": [
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

Response tra ve co `paymentUrl`. Android user chi can mo URL nay bang WebView hoac browser.

Callback:

- `GET /api/payments/vnpay/return`: hien ket qua sau khi VNPAY redirect nguoi dung ve.
- `GET /api/payments/vnpay/ipn`: endpoint VNPAY goi server-to-server de xac nhan giao dich.

Ghi chu chay local:

- `localhost` chi phu hop khi mo thanh toan tren chinh may dang chay backend.
- Neu Android emulator mo WebView, can set `VNPAY_RETURN_URL`, vi du `http://10.0.2.2:8080/api/payments/vnpay/return`.
- IPN tu VNPAY sandbox khong goi duoc may local neu khong co public URL. Khi test IPN that, dung ngrok hoac deploy backend len server public va cap nhat `VNPAY_IPN_URL`.
- Backend luu trang thai thanh toan trong `Dathang.trangthaithanhtoan`: `PENDING`, `PAID`, `FAILED`.

## Ghi chu

- Mat khau hien dang giu tu SQLite cu nen van la plain text. Nen hash mat khau o dot toi uu tiep theo.
- Web admin dang dung session MVC co ban, chua dung Spring Security.
- VNPAY sandbox da tich hop o backend. Khi len production can doi `VNPAY_TMN_CODE`, `VNPAY_HASH_SECRET`, `VNPAY_PAY_URL`, `VNPAY_RETURN_URL`, `VNPAY_IPN_URL`.
