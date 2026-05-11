# 📋 Kịch Bản Test Chức Năng Cấp Phát Thuốc

## 🎯 Mục Tiêu
Test toàn bộ quy trình cấp phát thuốc từ lúc bác sĩ chẩn đoán cho đến khi admin/bác sĩ cấp phát hoặc từ chối thuốc.

---

## 👥 Tài Khoản Test

### Admin
- **Username:** admin123
- **Password:** admin123
- **Vai trò:** Quản lý hệ thống, cấp phát thuốc, quản lý kho

### Bác sĩ
- **Username:** doctor1
- **Password:** doctor1
- **Vai trò:** Khám bệnh, kê đơn, cấp phát thuốc

### Bệnh nhân
- **Username:** patient1
- **Password:** patient1
- **Vai trò:** Đặt lịch khám, xem lịch sử

---

## 📌 BƯỚC 1: Chuẩn Bị Dữ Liệu

### 1.1 Kiểm tra Kho Thuốc (Admin)
1. Đăng nhập với tài khoản **admin123**
2. Click **"Quản lý Kho thuốc"** trên sidebar
3. **Kiểm tra:** Phải có ít nhất 2 loại thuốc với số lượng > 2
   - Ví dụ: Paracetamol (Kho: 10), Ibuprofen (Kho: 5)
4. **Nếu chưa có thuốc:** Click nút thêm thuốc mới

### 1.2 Kiểm tra Danh sách Bác sĩ
1. Click **"Quản lý Bác sĩ"** trên sidebar
2. **Kiểm tra:** Phải có ít nhất 1 bác sĩ (ví dụ: doctor1 - Chuyên khoa Nội khoa)

### 1.3 Kiểm tra Danh sách Bệnh nhân
1. Click **"Quản lý Bệnh nhân"** trên sidebar
2. **Kiểm tra:** Phải có ít nhất 1 bệnh nhân (ví dụ: patient1)

---

## 📌 BƯỚC 2: Đặt Lịch Khám (Bệnh Nhân)

### 2.1 Đăng nhập và Đặt Lịch
1. Đăng xuất Admin
2. Đăng nhập với tài khoản **patient1**
3. Click **"Trang chủ"** → **"+ Đặt lịch mới"**
4. **Điền thông tin:**
   - Chọn Bác sĩ: doctor1 (Nội khoa)
   - Chọn Ngày: Ngày hôm nay hoặc ngày mai
   - Chọn Giờ: 08:00-09:00
   - Lý do khám: "Đau đầu, mệt mỏi"
5. Click **"Đặt lịch"**
6. **Kết quả mong đợi:** 
   - ✅ Thông báo "Đặt lịch thành công"
   - ✅ Lịch khám hiển thị trong "Lịch khám của tôi"

---

## 📌 BƯỚC 3: Bác Sĩ Khám Bệnh & Kê Đơn

### 3.1 Đăng nhập và Xem Lịch Khám
1. Đăng xuất bệnh nhân
2. Đăng nhập với tài khoản **doctor1**
3. Click **"Danh sách lịch khám"** trên menu
4. **Kiểm tra:** Thấy lịch khám của patient1 với status "PENDING"

### 3.2 Khám Bệnh & Kê Đơn
1. Click nút **"Khám bệnh"** trên lịch khám của patient1
2. **Trang "Viết Hồ sơ Bệnh án":**
   - **Chẩn đoán bệnh:** Nhập "Cảm cúm thường gặp"
   - **Hướng điều trị:** Nhập "Nghỉ ngơi 3 ngày, uống đủ nước"
   - **Kê đơn thuốc:**
     - Click **"Thêm thuốc"** 
     - Chọn thuốc thứ 1: **Paracetamol**
     - Số lượng: **2**
     - Cách dùng: **Sáng 1, tối 1 viên**
     - Click **"Thêm thuốc"** lần 2
     - Chọn thuốc thứ 2: **Ibuprofen**
     - Số lượng: **1**
     - Cách dùng: **Khi đau**
3. Click **"LƯU BỆNH ÁN & HOÀN THÀNH"**
4. **Kết quả mong đợi:**
   - ✅ Thông báo "Bệnh án đã lưu, đơn thuốc đã được chuyển sang hàng chờ cấp phát"
   - ✅ Redirect đến trang **"/doctor/dispense"**

---

## 📌 BƯỚC 4: Bác Sĩ Cấp Phát Thuốc (Lựa Chọn A)

### 4.1 Xem Danh Sách Đơn Thuốc Chờ Cấp Phát
Bạn đã ở trang `/doctor/dispense`
1. **Kiểm tra bảng:** Phải thấy 2 dòng:
   - Paracetamol - Số lượng: 2 - Trạng thái: "⏳ Chờ cấp phát"
   - Ibuprofen - Số lượng: 1 - Trạng thái: "⏳ Chờ cấp phát"

### 4.2 Cấp Phát Thuốc Thứ 1
1. Click **"✓ Cấp phát"** cho dòng Paracetamol
2. Confirm: **"Xác nhận phát thuốc này?"**
3. **Kết quả mong đợi:**
   - ✅ Thông báo "Đã xác nhận phát thuốc"
   - ✅ Dòng Paracetamol biến mất khỏi danh sách chờ
   - ✅ **Kho Paracetamol giảm từ 10 → 8** (trừ 2)

### 4.3 Từ Chối Cấp Phát Thuốc Thứ 2
1. Click **"✗ Từ chối"** cho dòng Ibuprofen
2. Confirm: **"Từ chối cấp phát thuốc này?"**
3. **Kết quả mong đợi:**
   - ✅ Thông báo "Đã từ chối cấp phát thuốc"
   - ✅ Dòng Ibuprofen biến mất khỏi danh sách chờ
   - ✅ **Kho Ibuprofen vẫn giữ nguyên 5** (không trừ)

### 4.4 Xem Lịch Sử Cấp Phát
1. Click **"📜 Xem lịch sử"** trên trang dispense
2. **Kiểm tra bảng lịch sử:**
   - Paracetamol - Status: "✓ Đã cấp phát"
   - Ibuprofen - Status: "✗ Từ chối"

---

## 📌 BƯỚC 5: Admin Cấp Phát Thuốc (Lựa Chọn B)

> **Nếu muốn test cách khác:** Làm lại từ BƯỚC 2 với bệnh nhân/bác sĩ khác

### 5.1 Admin Xem Danh Sách Chờ Cấp Phát
1. Đăng xuất bác sĩ
2. Đăng nhập **admin123**
3. Click **"💊 Cấp phát thuốc"** trên sidebar
4. **Kiểm tra:** Nên thấy danh sách đơn thuốc chờ cấp phát (nếu có từ bác sĩ khác kê đơn)

### 5.2 Admin Cấp Phát/Từ Chối
1. Chọn 1 đơn thuốc bất kỳ
2. Click **"✓ Cấp phát"** hoặc **"✗ Từ chối"**
3. **Kết quả mong đợi:**
   - ✅ Thông báo xác nhận
   - ✅ Đơn thuốc biến mất khỏi danh sách chờ
   - ✅ Kho được cập nhật (nếu cấp phát)

### 5.3 Xem Lịch Sử
1. Click **"📜 Xem lịch sử"**
2. **Kiểm tra:** Thấy tất cả các đơn thuốc đã xử lý với trạng thái tương ứng

---

## 📌 BƯỚC 6: Bệnh Nhân Xem Lịch Sử Khám

### 6.1 Xem Danh Sách Lịch Sử Khám
1. Đăng xuất Admin
2. Đăng nhập **patient1**
3. Click **"Lịch sử khám bệnh"** trên menu
4. **Kiểm tra:** Thấy lần khám vừa rồi

### 6.2 Xem Chi Tiết Lần Khám
1. Click **"Xem chi tiết"** trên lần khám
2. **Thông tin hiển thị:**
   - Bác sĩ: doctor1
   - Ngày/Giờ khám: [Ngày đặt lịch]
   - Chẩn đoán: "Cảm cúm thường gặp"
   - Hướng điều trị: "Nghỉ ngơi 3 ngày, uống đủ nước"
   - **Đơn thuốc:**
     - Paracetamol - SL: 2 - Status: **"✓ Đã cấp phát"**
     - Ibuprofen - SL: 1 - Status: **"✗ Từ chối"**

---

## 📌 BƯỚC 7: Kiểm Tra Kho Thuốc Sau Cấp Phát

### 7.1 Admin Kiểm Tra Kho
1. Đăng nhập Admin
2. Click **"Quản lý Kho thuốc"**
3. **Kiểm tra số lượng:**
   - Paracetamol: **8** (từ 10, trừ 2) ✓
   - Ibuprofen: **5** (giữ nguyên, bị từ chối) ✓

---

## 🧪 Test Case - Kiểm Tra Edge Cases

### Test 1: Không Đủ Kho
1. Tạo đơn thuốc yêu cầu **6 viên Ibuprofen** (chỉ có 5 trong kho)
2. Bác sĩ cấp phát → **❌ Lỗi: "Không đủ tồn kho để cấp phát"**
3. ✅ **Kết quả:** Không cấp được, kho vẫn nguyên

### Test 2: Cấp Phát Hai Lần
1. Cấp phát cùng một đơn thuốc 2 lần
2. Lần thứ 2 → **❌ Cảnh báo: "Đơn thuốc này đã được xác nhận trước đó"**
3. ✅ **Kết quả:** Chặn được

### Test 3: Bác Sĩ Khác Cấp Phát Đơn Của Bác Sĩ Khác
1. Bác sĩ B cố gắng cấp phát đơn của bác sĩ A
2. → **❌ Lỗi: "Bạn không có quyền xác nhận đơn thuốc này"**
3. ✅ **Kết quả:** Chặn được

---

## 📊 Bảng Kiểm Tra Kết Quả

| # | Tính Năng | Status | Ghi Chú |
|---|-----------|--------|---------|
| 1 | Menu Cấp phát xuất hiện trên Sidebar Admin | ✅ | Đã thêm |
| 2 | Admin xem được danh sách chờ cấp phát | ? | Test cần |
| 3 | Bác sĩ xem được danh sách chờ cấp phát | ✅ | Đã có |
| 4 | Cấp phát → Trừ kho thuốc | ✅ | Logic có |
| 5 | Từ chối → Không trừ kho | ✅ | Logic có |
| 6 | Lịch sử hiển thị trạng thái | ✅ | Đã có |
| 7 | Bệnh nhân xem được trạng thái đơn | ✅ | Đã có |
| 8 | Xác thực quyền bác sĩ | ✅ | Đã có |
| 9 | Chặn cấp phát lần 2 | ✅ | Đã có |
| 10 | Chặn khi không đủ kho | ✅ | Đã có |

---

## 📝 SQL Kiểm Tra Nhanh (Tùy Chọn)

```sql
-- Kiểm tra số lượng thuốc sau cấp phát
SELECT id, name, quantity FROM medicine WHERE id IN (1, 2);

-- Kiểm tra trạng thái prescription
SELECT p.id, p.medicine_id, p.dosage, p.status, p.medical_record_id 
FROM prescription p 
WHERE p.medical_record_id = [ID_RECORD] 
ORDER BY p.id;

-- Kiểm tra lịch sử cấp phát
SELECT p.id, m.name, p.dosage, p.status, mr.id as medical_record_id
FROM prescription p
JOIN medicine m ON p.medicine_id = m.id
JOIN medical_record mr ON p.medical_record_id = mr.id
WHERE p.status IN ('DISPENSED', 'REJECTED')
ORDER BY p.id DESC;
```

---

## ✅ Kết Luận

Nếu tất cả các test case đều **PASS** ✓, chức năng cấp phát thuốc đã:
- ✅ Hiển thị giao diện đúng
- ✅ Trừ kho đúng
- ✅ Cập nhật lịch sử đúng
- ✅ Bảo vệ dữ liệu và quyền hạn

**Chúc bạn test vui vẻ! 🎉**

