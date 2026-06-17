package com.example.appshopbanhang.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appshopbanhang.R;
import com.example.appshopbanhang.data.model.TaiKhoan;
import com.example.appshopbanhang.data.repository.TaiKhoanRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaiKhoanAdapter extends BaseAdapter {
    private final Context context;
    private final List<TaiKhoan> taiKhoanList;
    private final TaiKhoanRepository accountRepository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public TaiKhoanAdapter(Context context, int layout, List<TaiKhoan> taiKhoanList) {
        this.context = context;
        this.taiKhoanList = taiKhoanList;
        this.accountRepository = new TaiKhoanRepository(context);
    }

    @Override
    public int getCount() {
        return taiKhoanList.size();
    }

    @Override
    public Object getItem(int position) {
        return taiKhoanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view == null
                ? View.inflate(viewGroup.getContext(), R.layout.ds_taikhoan, null)
                : view;

        TaiKhoan account = taiKhoanList.get(i);
        TextView tendn = row.findViewById(R.id.tdn1);
        TextView matkhau = row.findViewById(R.id.mk1);
        TextView quyenhang = row.findViewById(R.id.quyen1);
        ImageButton sua = row.findViewById(R.id.imgsua);
        ImageButton xoa = row.findViewById(R.id.imgxoa);

        tendn.setText(account.getTdn());
        matkhau.setText(account.getMk());
        quyenhang.setText(account.getQuyen());

        sua.setOnClickListener(v -> showEditDialog(account));
        xoa.setOnClickListener(v -> confirmDelete(account, i));

        return row;
    }

    private void showEditDialog(TaiKhoan account) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.activity_sua_tai_khoan, null);
        EditText editTdn = dialogView.findViewById(R.id.tdn);
        EditText editMk = dialogView.findViewById(R.id.mk);
        RadioButton user = dialogView.findViewById(R.id.user);
        RadioButton admin = dialogView.findViewById(R.id.admin);

        editTdn.setText(account.getTdn());
        editMk.setText(account.getMk());
        if ("admin".equals(account.getQuyen())) {
            admin.setChecked(true);
        } else {
            user.setChecked(true);
        }

        builder.setView(dialogView)
                .setPositiveButton("Luu", (dialog, which) -> {
                    String newUsername = editTdn.getText().toString().trim();
                    String newPassword = editMk.getText().toString().trim();
                    String role = user.isChecked() ? "user" : "admin";
                    executor.execute(() -> {
                        try {
                            TaiKhoan updated = accountRepository.saveAccount(account.getTdn(), newUsername, newPassword, role);
                            mainHandler.post(() -> {
                                account.setTdn(updated.getTdn());
                                account.setMk(updated.getMk());
                                account.setQuyen(updated.getQuyen());
                                notifyDataSetChanged();
                                Toast.makeText(context, "Cap nhat thanh cong", Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception exception) {
                            mainHandler.post(() -> Toast.makeText(context, "Cap nhat khong thanh cong", Toast.LENGTH_SHORT).show());
                        }
                    });
                })
                .setNegativeButton("Huy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void confirmDelete(TaiKhoan account, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Xac nhan")
                .setMessage("Ban co chac chan muon xoa tai khoan nay?")
                .setPositiveButton("Co", (dialog, which) -> executor.execute(() -> {
                    try {
                        accountRepository.deleteAccount(account.getTdn());
                        mainHandler.post(() -> {
                            taiKhoanList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Xoa tai khoan thanh cong", Toast.LENGTH_SHORT).show();
                        });
                    } catch (Exception exception) {
                        mainHandler.post(() -> Toast.makeText(context, "Khong xoa duoc tai khoan", Toast.LENGTH_SHORT).show());
                    }
                }))
                .setNegativeButton("Khong", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
