package com.example.appshopbanhang.ui.support;


import com.example.appshopbanhang.R;
import com.example.appshopbanhang.data.model.TinNhanHoTro;
import com.example.appshopbanhang.ui.adapter.HoTroAdapter;
import com.example.appshopbanhang.ui.auth.DangNhap_Activity;
import com.example.appshopbanhang.ui.home.TrangchuNgdung_Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class HoTro_Activity extends AppCompatActivity {
    private EditText editText;
    private ImageButton buttonSend;
    private ListView listView;
    private List<String> messageList = new ArrayList<>();
    private HoTroAdapter adapter;
    private TinNhanHoTro chatbot;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box_actitvity);

        editText = findViewById(R.id.tinnhan);
        buttonSend = findViewById(R.id.btnsend);
        listView = findViewById(R.id.listview);
ImageButton btnback=findViewById(R.id.btnback);
        adapter = new HoTroAdapter(this, messageList);
        listView.setAdapter(adapter);
        chatbot = new TinNhanHoTro();

        // Gửi tin nhắn chào mừng ngay khi vào trang chatbox
        sendGreetingMessage();

        buttonSend.setOnClickListener(v -> {
            String userInput = editText.getText().toString();
            if (!userInput.isEmpty()) {
                messageList.add("Bạn: " + userInput);
                String botResponse = chatbot.getResponse(userInput);
                messageList.add("Bot: " + botResponse);
                adapter.notifyDataSetChanged();
                editText.setText("");
                listView.setSelection(messageList.size() - 1); // Cuộn xuống tin nhắn cuối cùng
            }
        });

        TextView textTendn = findViewById(R.id.tendn); // TextView hiển thị tên đăng nhập



        // Lấy tên đăng nhập từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String tendn = sharedPreferences.getString("tendn", null);

        // Kiểm tra tên đăng nhập
        if (tendn != null) {
            textTendn.setText(tendn);
        } else {
            Intent intent = new Intent(HoTro_Activity.this, DangNhap_Activity.class);
            startActivity(intent);
            finish(); // Kết thúc activity nếu chưa đăng nhập
            return;
        }


        // Gửi tên đăng nhập qua Intent trong sự kiện click
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HoTro_Activity.this, TrangchuNgdung_Activity.class);
                // Gửi tên đăng nhập qua Intent
                intent.putExtra("tendn", tendn); // sử dụng biến tendn đã được xác nhận
                startActivity(intent);
            }
        });
    }

    // Hàm gửi tin nhắn chào mừng
    private void sendGreetingMessage() {
        String greetingMessage = "Xin chào! Tôi là chatbot app bán hàng. Bạn có gì thắc mắc ?";
        messageList.add("Bot: " + greetingMessage);
        adapter.notifyDataSetChanged();
        listView.setSelection(messageList.size() - 1); // Cuộn xuống tin nhắn cuối cùng
    }
}
