-- Normalize AppShopBanHang seed prices to Vietnamese Dong.
-- Some imported bouquet prices missed one trailing zero, e.g. 14000 should be 140000.

USE `appshopbanhang`;

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
