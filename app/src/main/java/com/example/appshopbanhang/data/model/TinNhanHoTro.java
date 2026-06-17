package com.example.appshopbanhang.data.model;

public class TinNhanHoTro {
    public String getResponse(String userInput) {
        // Chuyển đổi input về chữ thường để dễ so sánh
        String input = userInput.toLowerCase();

        // Logic phản hồi với từ ngữ gần đúng
        if (input.contains("xin chào")) {
            return "Chào bạn! Rất vui được gặp bạn. Nếu bạn có bất kỳ câu hỏi nào về sản phẩm điện thoại, hãy cho tôi biết!";
        } else if (input.contains("giá điện thoại")) {
            return "Hôm nay, chúng tôi có nhiều mẫu điện thoại với giá cả hợp lý. Bạn muốn biết giá của mẫu nào cụ thể?";
        } else if (input.contains("tính năng điện thoại")) {
            return "Các sản phẩm điện thoại của chúng tôi đi kèm với nhiều tính năng hiện đại. Bạn đang quan tâm đến tính năng nào?";
        } else if (input.contains("thông tin chi tiết")) {
            return "Nếu bạn quan tâm đến một sản phẩm cụ thể, hãy cho tôi biết để tôi cung cấp thông tin chi tiết hơn.";
        } else if (input.contains("thông tin liên hệ")) {
            return "Số điện thoại: 0367456697. Chúng tôi luôn sẵn sàng giúp đỡ bạn!";
        } else if (input.contains("tên của")) {
            return "Chúng tôi là cửa hàng điện thoại XYZ.";
        }

        return "Xin lỗi, tôi không hiểu bạn nói gì. Vui lòng liên hệ số điện thoại 0367456697 để được biết thêm chi tiết.";
    }
}
