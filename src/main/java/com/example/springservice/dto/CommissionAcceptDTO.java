package com.example.springservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommissionAcceptDTO {
    public String agreedPrice;     // ราคาที่ศิลปินตอบกลับ
    public Integer agreedDeadline; // จำนวนวันที่ใช้ในการส่งงาน (ไม่ใช่วันที่แน่นอน)
}