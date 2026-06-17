package com.example.appshopbanhang.ui.cart;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.core.util.MoneyFormatter;
import com.example.appshopbanhang.data.model.GioHang;
import com.example.appshopbanhang.data.model.VnpayPayment;
import com.example.appshopbanhang.data.repository.DonHangRepository;
import com.example.appshopbanhang.data.repository.GioHangManager;
import com.example.appshopbanhang.ui.account.TrangCaNhan_nguoidung_Activity;
import com.example.appshopbanhang.ui.adapter.GioHangAdapter;
import com.example.appshopbanhang.ui.auth.DangNhap_Activity;
import com.example.appshopbanhang.ui.home.TrangchuNgdung_Activity;
import com.example.appshopbanhang.ui.order.DonHang_User_Activity;
import com.example.appshopbanhang.ui.payment.VnpayPayment_Activity;
import com.example.appshopbanhang.ui.product.TimKiemSanPham_Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GioHang_Activity extends AppCompatActivity {
    private static final int REQUEST_VNPAY_PAYMENT = 2001;

    private GioHangAdapter adapter;
    private GioHangManager gioHangManager;
    private DonHangRepository orderRepository;
    private TextView txtTongTien;
    private String tendn;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);

        TextView textTendn = findViewById(R.id.tendn);
        SharedPreferences sharedPre = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        tendn = sharedPre.getString("tendn", null);
        if (tendn == null) {
            startActivity(new Intent(this, DangNhap_Activity.class));
            finish();
            return;
        }
        textTendn.setText(tendn);

        txtTongTien = findViewById(R.id.tongtien);
        gioHangManager = GioHangManager.getInstance();
        orderRepository = new DonHangRepository(this);
        ListView listView = findViewById(R.id.listtk);
        adapter = new GioHangAdapter(this, gioHangManager.getGioHangList(), txtTongTien);
        listView.setAdapter(adapter);
        txtTongTien.setText(MoneyFormatter.format(gioHangManager.getTongTien()));

        Button thanhtoan = findViewById(R.id.btnthanhtoan);
        thanhtoan.setOnClickListener(v -> showPaymentDialog());
        setupNavigationButtons();
    }

    private void setupNavigationButtons() {
        ImageButton btntimkiem = findViewById(R.id.btntimkiem);
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btncard = findViewById(R.id.btncart);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btncanhan = findViewById(R.id.btncanhan);

        btntimkiem.setOnClickListener(view -> openWithUser(TimKiemSanPham_Activity.class));
        btntrangchu.setOnClickListener(view -> openWithUser(TrangchuNgdung_Activity.class));
        btncard.setOnClickListener(view -> openWithUser(GioHang_Activity.class));
        btndonhang.setOnClickListener(view -> openWithUser(DonHang_User_Activity.class));
        btncanhan.setOnClickListener(view -> openWithUser(TrangCaNhan_nguoidung_Activity.class));
    }

    private void showPaymentDialog() {
        if (gioHangManager.getGioHangList().isEmpty()) {
            Toast.makeText(this, "Gio hang dang trong", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_thong_tin_thanh_toan);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

        EditText edtTenKh = dialog.findViewById(R.id.tenkh);
        EditText edtDiaChi = dialog.findViewById(R.id.diachi);
        EditText edtSdt = dialog.findViewById(R.id.sdt);
        Button btnLuu = dialog.findViewById(R.id.btnxacnhandathang);
        TextView tvTongTien = dialog.findViewById(R.id.tienthanhtoan);
        tvTongTien.setText(txtTongTien.getText().toString());

        btnLuu.setOnClickListener(v -> {
            String tenKh = edtTenKh.getText().toString().trim();
            String diaChi = edtDiaChi.getText().toString().trim();
            String sdt = edtSdt.getText().toString().trim();
            if (tenKh.isEmpty() || diaChi.isEmpty() || sdt.isEmpty()) {
                Toast.makeText(this, "Vui long dien day du thong tin", Toast.LENGTH_SHORT).show();
                return;
            }
            startVnpayPayment(tenKh, diaChi, sdt);
            dialog.dismiss();
        });
        dialog.show();
    }

    private void startVnpayPayment(String tenKh, String diaChi, String sdt) {
        List<GioHang> orderItems = new ArrayList<>(gioHangManager.getGioHangList());
        Toast.makeText(this, "Dang tao thanh toan VNPAY", Toast.LENGTH_SHORT).show();
        executor.execute(() -> {
            try {
                VnpayPayment payment = orderRepository.createVnpayOrder(tendn, tenKh, diaChi, sdt, orderItems);
                mainHandler.post(() -> openVnpayPayment(payment));
            } catch (Exception exception) {
                mainHandler.post(() -> Toast.makeText(this, "Tao thanh toan VNPAY that bai", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void openVnpayPayment(VnpayPayment payment) {
        String paymentUrl = payment == null ? "" : payment.getPaymentUrl();
        if (paymentUrl == null || paymentUrl.trim().isEmpty()) {
            Toast.makeText(this, "Khong nhan duoc URL thanh toan VNPAY", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, VnpayPayment_Activity.class);
        intent.putExtra(VnpayPayment_Activity.EXTRA_PAYMENT_URL, paymentUrl);
        intent.putExtra(VnpayPayment_Activity.EXTRA_QR_CONTENT, payment.getQrContent());
        intent.putExtra(VnpayPayment_Activity.EXTRA_QR_IMAGE_BASE64, payment.getQrImageBase64());
        intent.putExtra(VnpayPayment_Activity.EXTRA_QR_MIME_TYPE, payment.getQrMimeType());
        intent.putExtra(VnpayPayment_Activity.EXTRA_EXPIRES_AT, payment.getExpiresAt());
        intent.putExtra(VnpayPayment_Activity.EXTRA_ORDER_ID, payment.getOrderId());
        intent.putExtra(VnpayPayment_Activity.EXTRA_AMOUNT, payment.getAmount());
        startActivityForResult(intent, REQUEST_VNPAY_PAYMENT);
        Toast.makeText(this, "Da tao QR VNPAY trong app", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_VNPAY_PAYMENT) {
            return;
        }
        if (resultCode == RESULT_OK) {
            gioHangManager.clearGioHang();
            txtTongTien.setText(MoneyFormatter.format(0));
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Thanh toan thanh cong", Toast.LENGTH_SHORT).show();
            openWithUser(DonHang_User_Activity.class);
        } else {
            Toast.makeText(this, "Giao dich VNPAY chua hoan tat", Toast.LENGTH_SHORT).show();
        }
    }

    private void openWithUser(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("tendn", tendn);
        startActivity(intent);
    }
}
