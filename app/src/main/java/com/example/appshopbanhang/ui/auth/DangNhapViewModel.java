package com.example.appshopbanhang.ui.auth;


import com.example.appshopbanhang.core.session.QuanLyPhien;
import com.example.appshopbanhang.data.model.KetQuaDangNhap;
import com.example.appshopbanhang.data.repository.TaiKhoanRepository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DangNhapViewModel extends AndroidViewModel {
    private final TaiKhoanRepository userRepository;
    private final QuanLyPhien sessionManager;
    private final MutableLiveData<TrangThaiDangNhapUi> loginState = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public DangNhapViewModel(@NonNull Application application) {
        super(application);
        userRepository = new TaiKhoanRepository(application);
        sessionManager = new QuanLyPhien(application);
    }

    public LiveData<TrangThaiDangNhapUi> getLoginState() {
        return loginState;
    }

    public void login(String username, String password) {
        executor.execute(() -> {
            try {
                KetQuaDangNhap result = userRepository.login(username, password);
                if (result.isSuccess()) {
                    sessionManager.saveLogin(result.getUsername(), result.getRole());
                    loginState.postValue(TrangThaiDangNhapUi.success(result.getUsername(), result.getRole()));
                } else {
                    loginState.postValue(TrangThaiDangNhapUi.error(result.getMessage()));
                }
            } catch (Exception exception) {
                loginState.postValue(TrangThaiDangNhapUi.error("Khong ket noi duoc backend"));
            }
        });
    }

    public void logout() {
        sessionManager.logout();
    }
}

