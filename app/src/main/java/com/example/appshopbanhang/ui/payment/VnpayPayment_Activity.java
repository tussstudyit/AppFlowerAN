package com.example.appshopbanhang.ui.payment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.core.network.ApiConfig;
import com.example.appshopbanhang.core.util.MoneyFormatter;
import com.example.appshopbanhang.data.model.DonHang;
import com.example.appshopbanhang.data.repository.DonHangRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VnpayPayment_Activity extends AppCompatActivity {
    public static final String EXTRA_PAYMENT_URL = "paymentUrl";
    public static final String EXTRA_QR_CONTENT = "qrContent";
    public static final String EXTRA_QR_IMAGE_BASE64 = "qrImageBase64";
    public static final String EXTRA_QR_MIME_TYPE = "qrMimeType";
    public static final String EXTRA_EXPIRES_AT = "expiresAt";
    public static final String EXTRA_ORDER_ID = "orderId";
    public static final String EXTRA_AMOUNT = "amount";

    private static final long PAYMENT_STATUS_POLL_INTERVAL_MS = 2_000L;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private DonHangRepository orderRepository;
    private TextView statusText;
    private TextView amountText;
    private TextView expireText;
    private ProgressBar progressBar;
    private Button finishButton;
    private Button openVnpayButton;
    private Runnable pollRunnable;
    private Runnable autoFinishRunnable;
    private int orderId;
    private boolean pollingStopped;
    private boolean paymentSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnpay_payment);

        ApiConfig.load(this);
        orderRepository = new DonHangRepository(this);

        progressBar = findViewById(R.id.progressVnpay);
        statusText = findViewById(R.id.tvVnpayStatus);
        amountText = findViewById(R.id.tvVnpayAmount);
        expireText = findViewById(R.id.tvVnpayExpire);
        finishButton = findViewById(R.id.btnFinishVnpay);
        openVnpayButton = findViewById(R.id.btnOpenVnpay);
        ImageView qrImageView = findViewById(R.id.imgVnpayQr);
        Button closeButton = findViewById(R.id.btnCloseVnpay);

        closeButton.setOnClickListener(v -> finishWithCurrentResult());
        finishButton.setOnClickListener(v -> finishWithCurrentResult());

        Intent intent = getIntent();
        orderId = intent.getIntExtra(EXTRA_ORDER_ID, 0);
        float amount = intent.getFloatExtra(EXTRA_AMOUNT, 0f);
        String paymentUrl = intent.getStringExtra(EXTRA_PAYMENT_URL);
        String qrImageBase64 = intent.getStringExtra(EXTRA_QR_IMAGE_BASE64);
        long expiresAt = intent.getLongExtra(EXTRA_EXPIRES_AT, 0L);

        amountText.setText(formatAmount(amount));
        bindExpiration(expiresAt);
        bindQrImage(qrImageView, qrImageBase64);
        bindOpenButton(paymentUrl);
        startPaymentStatusPolling();
    }

    private void bindQrImage(ImageView qrImageView, String qrImageBase64) {
        if (qrImageBase64 == null || qrImageBase64.trim().isEmpty()) {
            statusText.setText("Khong nhan duoc anh QR tu backend.");
            qrImageView.setVisibility(View.GONE);
            return;
        }

        try {
            byte[] qrBytes = Base64.decode(qrImageBase64, Base64.DEFAULT);
            Bitmap qrBitmap = BitmapFactory.decodeByteArray(qrBytes, 0, qrBytes.length);
            if (qrBitmap == null) {
                statusText.setText("Khong hien thi duoc anh QR VNPAY.");
                qrImageView.setVisibility(View.GONE);
                return;
            }
            qrImageView.setImageBitmap(qrBitmap);
            qrImageView.setVisibility(View.VISIBLE);
            statusText.setText("Quet ma QR de thanh toan. App se tu cap nhat ket qua.");
        } catch (IllegalArgumentException exception) {
            statusText.setText("Du lieu QR VNPAY khong hop le.");
            qrImageView.setVisibility(View.GONE);
        }
    }

    private void bindOpenButton(String paymentUrl) {
        boolean hasPaymentUrl = paymentUrl != null && !paymentUrl.trim().isEmpty();
        openVnpayButton.setEnabled(hasPaymentUrl);
        openVnpayButton.setOnClickListener(v -> {
            if (!hasPaymentUrl) {
                return;
            }
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl)));
            } catch (Exception exception) {
                Toast.makeText(this, "Khong mo duoc trang VNPAY", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startPaymentStatusPolling() {
        if (orderId <= 0) {
            progressBar.setVisibility(View.GONE);
            expireText.setText("Khong co ma don hang de kiem tra trang thai.");
            return;
        }
        pollRunnable = this::pollPaymentStatus;
        mainHandler.post(pollRunnable);
    }

    private void pollPaymentStatus() {
        if (pollingStopped) {
            return;
        }
        executor.execute(() -> {
            try {
                DonHang order = orderRepository.getOrder(orderId);
                mainHandler.post(() -> handlePaymentStatus(order));
            } catch (Exception exception) {
                mainHandler.post(() -> {
                    if (!pollingStopped) {
                        expireText.setText("Dang cho ket qua thanh toan...");
                        scheduleNextPoll();
                    }
                });
            }
        });
    }

    private void handlePaymentStatus(DonHang order) {
        if (pollingStopped || order == null) {
            return;
        }

        String paymentStatus = order.getPaymentStatus();
        if (isSuccessStatus(paymentStatus)) {
            markPaymentSuccess();
        } else if (isFailureStatus(paymentStatus)) {
            markPaymentFailed(paymentStatus);
        } else {
            scheduleNextPoll();
        }
    }

    private void scheduleNextPoll() {
        if (!pollingStopped && pollRunnable != null) {
            mainHandler.postDelayed(pollRunnable, PAYMENT_STATUS_POLL_INTERVAL_MS);
        }
    }

    private void markPaymentSuccess() {
        pollingStopped = true;
        paymentSuccess = true;
        progressBar.setVisibility(View.GONE);
        finishButton.setEnabled(true);
        openVnpayButton.setEnabled(false);
        statusText.setText("Thanh toan VNPAY thanh cong.");
        expireText.setText("Don hang da duoc cap nhat thanh cong.");
        setResult(RESULT_OK);
        Toast.makeText(this, "Thanh toan thanh cong", Toast.LENGTH_SHORT).show();
        autoFinishRunnable = this::finishWithCurrentResult;
        mainHandler.postDelayed(autoFinishRunnable, 900);
    }

    private void markPaymentFailed(String paymentStatus) {
        pollingStopped = true;
        paymentSuccess = false;
        progressBar.setVisibility(View.GONE);
        finishButton.setEnabled(true);
        statusText.setText("Thanh toan VNPAY chua thanh cong.");
        expireText.setText("Trang thai: " + (paymentStatus == null ? "FAILED" : paymentStatus));
        setResult(RESULT_CANCELED);
    }

    private boolean isSuccessStatus(String paymentStatus) {
        return "PAID".equalsIgnoreCase(paymentStatus) || "SUCCESS".equalsIgnoreCase(paymentStatus);
    }

    private boolean isFailureStatus(String paymentStatus) {
        return "FAILED".equalsIgnoreCase(paymentStatus)
                || "EXPIRED".equalsIgnoreCase(paymentStatus)
                || "PAYMENT_FAILED".equalsIgnoreCase(paymentStatus)
                || "CANCELLED".equalsIgnoreCase(paymentStatus);
    }

    private String formatAmount(float amount) {
        if (amount <= 0) {
            return MoneyFormatter.format(0);
        }
        return MoneyFormatter.format(amount);
    }

    private void bindExpiration(long expiresAt) {
        if (expiresAt <= 0L) {
            expireText.setText("Dang cho thanh toan...");
            return;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        expireText.setText("Hieu luc den " + formatter.format(new Date(expiresAt)));
    }

    private void finishWithCurrentResult() {
        pollingStopped = true;
        setResult(paymentSuccess ? RESULT_OK : RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishWithCurrentResult();
    }

    @Override
    protected void onDestroy() {
        pollingStopped = true;
        if (pollRunnable != null) {
            mainHandler.removeCallbacks(pollRunnable);
        }
        if (autoFinishRunnable != null) {
            mainHandler.removeCallbacks(autoFinishRunnable);
        }
        executor.shutdownNow();
        super.onDestroy();
    }
}
