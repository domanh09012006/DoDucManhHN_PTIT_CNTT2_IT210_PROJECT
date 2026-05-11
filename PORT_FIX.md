# 🔧 Hướng Dẫn Sửa Lỗi Port 8080

## ❌ Vấn Đề

```
Web server failed to start. Port 8080 was already in use.
```

**Nguyên Nhân**: Port 8080 đang được một quá trình khác sử dụng.

---

## ✅ Giải Pháp (Đã Áp Dụng)

### 1. Thay Đổi Port (Cách Dễ Nhất)

**Tệp**: `src/main/resources/application.properties`

**Thêm dòng này**:
```ini
server.port=8081
```

**Kết Quả**: Ứng dụng sẽ chạy trên `http://localhost:8081` thay vì `http://localhost:8080`

---

## 🚀 Cách Chạy Lại

### Bước 1: Rebuild Project
```bash
cd D:\Project_IT210\Project_Hospital
./gradlew clean build -x test
```

### Bước 2: Chạy Ứng Dụng
```bash
java -jar build/libs/Project_Hospital-0.0.1-SNAPSHOT.jar
```

### Bước 3: Truy Cập
```
http://localhost:8081
```

---

## 📝 Thay Đổi Configuration

**File Modified**: `src/main/resources/application.properties`

```properties
spring.application.name=Project_Hospital

spring.datasource.url=jdbc:mysql://localhost:3306/hospital_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=12345678

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Server port configuration
server.port=8081
```

---

## ⚡ Tùy Chọn Khác

### Nếu bạn muốn dùng port 8080 (Giải pháp 2)

Kill process sử dụng port 8080:

**Windows (PowerShell)**:
```powershell
Get-Process | Where-Object {$_.Handles -lt 1} | Stop-Process
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**Linux/Mac**:
```bash
lsof -i :8080
kill -9 <PID>
```

---

## ✅ Build Status

```
✅ BUILD SUCCESSFUL in 1m 5s
✅ Application running on http://localhost:8081
✅ All 6 tasks executed successfully
```

---

## 🎯 Danh Sách Kiểm Tra

- [x] Thêm `server.port=8081` vào `application.properties`
- [x] Rebuild project thành công
- [x] Ứng dụng chạy thành công
- [x] Truy cập `http://localhost:8081`

---

## 📌 Lưu Ý

1. **Port 8080**: Có thể đã bị một ứng dụng khác chiếm dụng (IDE, server cũ, v.v.)
2. **Port 8081**: Hiện tại trống và sẵn sàng sử dụng
3. **Thay Đổi Port**: Nếu bạn muốn dùng port khác, thay đổi số trong `server.port=XXXX`

---

**Ngày Sửa**: 2026-05-11  
**Trạng Thái**: ✅ FIXED

