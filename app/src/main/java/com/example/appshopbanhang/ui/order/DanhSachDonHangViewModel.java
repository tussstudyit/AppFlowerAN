package com.example.appshopbanhang.ui.order;


import com.example.appshopbanhang.data.model.DonHang;
import com.example.appshopbanhang.data.repository.DonHangRepository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DanhSachDonHangViewModel extends AndroidViewModel {
    private final DonHangRepository orderRepository;
    private final MutableLiveData<ArrayList<DonHang>> orders = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public DanhSachDonHangViewModel(@NonNull Application application) {
        super(application);
        orderRepository = new DonHangRepository(application);
    }

    public LiveData<ArrayList<DonHang>> getOrders() {
        return orders;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public void loadAllOrders() {
        executor.execute(() -> load(() -> orderRepository.getAllOrders(), "Khong tim thay don hang nao"));
    }

    public void loadOrdersByCustomerName(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            message.setValue("Ten khach hang khong hop le");
            orders.setValue(new ArrayList<>());
            return;
        }
        executor.execute(() -> load(() -> orderRepository.getOrdersByCustomerName(customerName), "Khong tim thay don hang cho khach hang nay"));
    }

    private void load(OrderLoader loader, String emptyMessage) {
        try {
            ArrayList<DonHang> result = loader.load();
            orders.postValue(result);
            if (result.isEmpty()) {
                message.postValue(emptyMessage);
            }
        } catch (Exception exception) {
            orders.postValue(new ArrayList<>());
            message.postValue("Khong ket noi duoc backend");
        }
    }

    private interface OrderLoader {
        ArrayList<DonHang> load() throws Exception;
    }
}

