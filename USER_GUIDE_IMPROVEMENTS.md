# 📖 Hướng Dẫn Sử Dụng - Cải Tiến Giao Diện Cấp Phát Thuốc

## 🎯 Mục Tiêu Cải Tiến

Cải thiện trải nghiệm người dùng bằng cách:
1. ✅ Hiển thị **trạng thái rõ ràng** ("Đã cấp phát" / "Từ chối") trực tiếp trên bảng lịch sử
2. ✅ Thêm nút **"Xem chi tiết"** dạng modal để xem thông tin chi tiết
3. ✅ Cải tiến giao diện trang cấp phát thuốc với **hướng dẫn rõ ràng**
4. ✅ Quản lý kho thuốc **tự động** khi cấp phát

---

## 🏥 Hướng Dẫn Cho Bác Sĩ

### 1️⃣ Cấp Phát Thuốc

**Bước 1: Truy cập trang Cấp phát thuốc**
- Click vào menu "💊 Cấp phát thuốc"
- Bạn sẽ thấy danh sách đơn thuốc **chờ cấp phát** từ các bệnh nhân của bạn

**Bước 2: Xem hướng dẫn**
- Ở đầu trang, bạn sẽ thấy một **hộp thông tin xanh** với hướng dẫn
- Nó nhắc nhở: "Nếu cấp phát, số lượng thuốc trong kho sẽ được trừ tự động"

**Bước 3: Kiểm tra đơn thuốc**
- Xem danh sách bảng:
  - **Hồ sơ ID**: Mã hồ sơ khám bệnh
  - **Bệnh nhân**: Tên bệnh nhân cần cấp phát
  - **Tên thuốc**: Thuốc nào cần cấp phát
  - **Số lượng**: Liều lượng cần cấp phát
  - **Trạng thái**: Hiển thị "⏳ Chờ cấp phát" (badge vàng)

**Bước 4: Xác nhận hoặc Từ chối**
```
┌─────────────────────────────────────┐
│  Hành động:                         │
│  ✓ Cấp phát  (Nút xanh)            │
│  ✗ Từ chối   (Nút đỏ)              │
└─────────────────────────────────────┘
```
- Click "✓ Cấp phát" nếu bạn đồng ý cấp phát
  - Sẽ hiện hộp xác nhận: "Xác nhận phát thuốc này?"
  - Click "OK" để xác nhận
  - Số lượng thuốc trong kho sẽ **tự động trừ**
  - Bệnh nhân sẽ thấy trạng thái thay đổi thành "✓ Đã cấp phát"

- Click "✗ Từ chối" nếu bạn từ chối cấp phát
  - Sẽ hiện hộp xác nhận: "Từ chối cấp phát thuốc này?"
  - Click "OK" để xác nhận
  - Kho thuốc **không bị trừ**
  - Bệnh nhân sẽ thấy trạng thái thay đổi thành "✗ Từ chối"

### 2️⃣ Xem Lịch Sử Cấp Phát

**Bước 1: Click nút "📜 Xem lịch sử"**
- Nằm ở góc trên cùng bên phải trang
- Bạn sẽ được chuyển đến trang lịch sử cấp phát

**Bước 2: Xem danh sách lịch sử**
- Bảng hiển thị tất cả đơn thuốc đã được xử lý:
  - **Trạng thái**:
    - 🟢 "✓ Đã cấp phát" (badge xanh)
    - 🔴 "✗ Từ chối" (badge đỏ)

**Bước 3: Xem chi tiết đơn thuốc**
- Click nút "👁️ Xem chi tiết" ở cột "Hành động"
- Sẽ mở một modal (cửa sổ) hiển thị:
  - **Hồ sơ khám bệnh ID**: Mã hồ sơ
  - **Bệnh nhân**: Tên bệnh nhân
  - **Tên thuốc**: Tên thuốc cấp phát
  - **Liều lượng cấp phát**: Số lượng đã cấp phát
  - **Cách dùng**: Hướng dẫn sử dụng thuốc
  - **Trạng thái**: Đã cấp phát hay từ chối
  - **Chẩn đoán**: Chẩn đoán bệnh của bác sĩ
- Click "Đóng" để đóng modal

---

## 🏥 Hướng Dẫn Cho Admin

### 1️⃣ Quản Lý Cấp Phát Thuốc

**Giống như bác sĩ nhưng:**
- Bạn có thể xem **tất cả đơn thuốc** của **tất cả bác sĩ**
- Bảng hiển thị thêm cột "Bác sĩ" để bạn biết bác sĩ nào kê đơn

**Quy trình:**
1. Vào "💊 Quản lý Cấp phát thuốc" (trong Admin Dashboard)
2. Xem hộp thông tin hướng dẫn
3. Xem danh sách đơn thuốc chờ:
   - Bác sĩ
   - Bệnh nhân
   - Tên thuốc
   - Số lượng
4. Click "✓ Cấp phát" hoặc "✗ Từ chối"
5. Xem lịch sử bằng cách click "📜 Xem lịch sử"

### 2️⃣ Xem Chi Tiết Đơn Thuốc

- Cũng giống như bác sĩ
- Nhưng bạn sẽ thấy **bác sĩ kê đơn** trong chi tiết
- Điều này giúp bạn theo dõi công việc của các bác sĩ

---

## 👤 Hướng Dẫn Cho Bệnh Nhân

### 1️⃣ Xem Lịch Sử Khám Bệnh

**Bước 1: Vào menu "Lịch sử khám bệnh"**
- Click trên thanh điều hướng (navbar)
- Bạn sẽ thấy danh sách tất cả các lần khám

**Bước 2: Xem thông tin lần khám**
- Mỗi lần khám được hiển thị trong một **accordion** (hộp mở rộng)
- Click vào để xem chi tiết:
  - **Bác sĩ**: Bác sĩ khám bệnh
  - **Ngày khám**: Ngày khám bệnh
  - **Giờ khám**: Giờ khám
  - **Chẩn đoán**: Chẩn đoán của bác sĩ
  - **Hướng điều trị**: Hướng dẫn điều trị
  - **Danh sách đơn thuốc**: Các thuốc được kê đơn

### 2️⃣ Xem Trạng Thái Đơn Thuốc

Trong bảng "Đơn thuốc", bạn sẽ thấy trạng thái mỗi loại thuốc:

```
┌──────────────────────────────────────────────────┐
│  Trạng thái Đơn Thuốc:                          │
├──────────────────────────────────────────────────┤
│  ⏳ Chờ cấp phát (badge vàng)                    │
│     → Bác sĩ hoặc admin chưa phê duyệt          │
│     → Bạn chưa thể lấy thuốc                     │
│                                                   │
│  ✓ Đã cấp phát (badge xanh)                      │
│     → Bác sĩ hoặc admin đã phê duyệt            │
│     → Bạn có thể lấy thuốc ở hiệu thuốc         │
│                                                   │
│  ✗ Từ chối (badge đỏ)                           │
│     → Bác sĩ hoặc admin từ chối cấp phát       │
│     → Bạn không thể lấy thuốc này               │
│     → Hãy trao đổi với bác sĩ để hiểu lý do   │
└──────────────────────────────────────────────────┘
```

### 3️⃣ Xem Chi Tiết Lần Khám

- Click nút "Xem chi tiết" trong accordion
- Sẽ mở một modal (cửa sổ) lớn hơn hiển thị tất cả thông tin
- Scroll xuống để xem toàn bộ thông tin
- Click "Đóng" để đóng modal

---

## 💡 Các Tính Năng Chính

### ✅ 1. Quản Lý Kho Thuốc Tự Động

**Vấn đề cũ:**
- Cần tính toán số lượng thuốc còn lại thủ công
- Dễ gây nhầm lẫn và lỗi

**Cải tiến:**
- Khi bác sĩ/admin click "✓ Cấp phát"
- Hệ thống **tự động trừ** số lượng từ kho
- Ví dụ:
  - Kho ban đầu: Ibuprofen 100 viên
  - Cấp phát: 10 viên
  - Kho sau đó: Ibuprofen 90 viên ✓

### ✅ 2. Hiển Thị Trạng Thái Rõ Ràng

**Vấn đề cũ:**
- Bệnh nhân không biết đơn thuốc ở trạng thái nào

**Cải tiến:**
- Trạng thái hiển thị với **badge màu sắc**:
  - 🟡 **Vàng**: Chờ cấp phát (PENDING)
  - 🟢 **Xanh**: Đã cấp phát (DISPENSED)
  - 🔴 **Đỏ**: Từ chối (REJECTED)

### ✅ 3. Xem Chi Tiết Modal

**Vấn đề cũ:**
- Không có chỗ xem chi tiết đơn thuốc

**Cải tiến:**
- Bấm "👁️ Xem chi tiết"
- Mở modal hiển thị toàn bộ thông tin:
  - Hồ sơ ID
  - Bệnh nhân
  - Tên thuốc
  - Liều lượng
  - Cách dùng
  - Trạng thái
  - Chẩn đoán

### ✅ 4. Hướng Dẫn Rõ Ràng

**Vấn đề cũ:**
- Trang cấp phát thuốc không có hướng dẫn

**Cải tiến:**
- Thêm "hộp thông tin xanh" ở đầu trang
- Nội dung: "Kiểm tra thông tin đơn thuốc rồi xác nhận cấp phát hoặc từ chối. Nếu cấp phát, số lượng thuốc trong kho sẽ được trừ tự động."

---

## ⚠️ Các Lưu Ý Quan Trọng

### Cho Bác Sĩ/Admin:

1. **Kiểm tra tồn kho trước**
   - Nếu click "✓ Cấp phát" mà không đủ thuốc
   - Sẽ hiện thông báo lỗi: "Không đủ tồn kho để cấp phát"
   - Bạn cần từ chối hoặc nhập thêm thuốc

2. **Không thể huỷ/sửa sau cấp phát**
   - Sau khi click "✓ Cấp phát" hoặc "✗ Từ chối"
   - Không thể quay lại
   - Hãy chắc chắn trước khi xác nhận!

3. **Xem lịch sử để kiểm tra lại**
   - Vào "Lịch sử cấp phát thuốc"
   - Xem tất cả các đơn thuốc đã xử lý
   - Click "Xem chi tiết" để kiểm tra chi tiết

### Cho Bệnh Nhân:

1. **Các trạng thái đơn thuốc**
   - ⏳ **Chờ cấp phát**: Đừng lấy thuốc chưa
   - ✓ **Đã cấp phát**: Bạn có thể lấy thuốc
   - ✗ **Từ chối**: Hỏi bác sĩ lý do tại sao

2. **Kiểm tra lịch sử thường xuyên**
   - Để biết đơn thuốc ở trạng thái nào
   - Để lấy thuốc đúng thời điểm

---

## 🔍 Ví Dụ Thực Tế

### Scenario: Bác sĩ cấp phát thuốc cho bệnh nhân

```
1. Bác sĩ khám bệnh và chẩn đoán
   → Bệnh: Viêm họng
   → Kê đơn: Ibuprofen 10 viên, 3 lần/ngày

2. Hệ thống tạo "Đơn thuốc" với trạng thái: ⏳ PENDING

3. Bác sĩ vào "Cấp phát thuốc"
   → Xem hộp thông tin hướng dẫn
   → Xem danh sách đơn chờ
   → Tìm đơn của bệnh nhân

4. Bác sĩ kiểm tra:
   - Kho Ibuprofen: 100 viên ✓ (đủ)
   - Liều lượng: 10 viên ✓ (hợp lý)

5. Bác sĩ click "✓ Cấp phát"
   → Xác nhận: "Xác nhận phát thuốc này?"
   → Click "OK"

6. Hệ thống:
   - Trừ kho: 100 - 10 = 90 viên
   - Cập nhật trạng thái: ✓ DISPENSED
   - Bệnh nhân nhìn thấy: ✓ Đã cấp phát

7. Bệnh nhân lấy thuốc ở hiệu
   → Được cấp: Ibuprofen 10 viên

8. Lịch sử:
   - Bác sĩ vào "Lịch sử cấp phát thuốc"
   - Xem đơn với trạng thái: ✓ Đã cấp phát
   - Click "Xem chi tiết" để xem đầy đủ
```

---

## 📱 Responsive Design

Tất cả các trang đều được thiết kế **responsive** (thích ứng với mọi kích thước màn hình):
- 💻 **Desktop**: Đầy đủ tính năng
- 📱 **Mobile**: Bảng có thể cuộn ngang
- 📲 **Tablet**: Hiển thị tối ưu

---

## ❓ Câu Hỏi Thường Gặp (FAQ)

**Q: Nếu cấp phát nhầm, tôi có thể hoàn tác không?**
A: Không thể hoàn tác. Hãy liên hệ Admin để sửa kho thuốc.

**Q: Tại sao tôi không thể cấp phát thuốc?**
A: Có thể là:
- Không đủ tồn kho
- Quyền hạn không đủ
- Đơn thuốc đã được xử lý trước đó

**Q: Làm sao để biết bệnh nhân lấy thuốc chưa?**
A: Xem trạng thái "✓ Đã cấp phát". Nó chỉ có nghĩa là bác sĩ/admin phê duyệt, không phải bệnh nhân đã lấy.

**Q: Liệu tôi có thể xem lịch sử cấp phát của tất cả bác sĩ không?**
A: Nếu bạn là Admin thì có. Nếu bạn là Bác sĩ thì chỉ xem được lịch sử của chính mình.

---

**Cảm ơn bạn đã sử dụng hệ thống! 🙏**

