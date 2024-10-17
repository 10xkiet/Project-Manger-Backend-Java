# Project Manager với Spring framework

Đây là dự án lấy điểm bài tập lớn cho môn Lập trình hướng đối tượng nhóm 8 lớp `D22-151` năm học 2024-2025.

## Trước khi bắt đầu

Yêu cầu: `openssl`

Trong dự án này, chúng tôi sử dụng asymmetric key để tạo `JWT` để tăng tính bảo mật của token được tạo ra.

Chúng ta cần thực hiện câu lệnh sau ở thư mục gốc của repository để tạo thư mục chứa public key và private key:

```
cd ./src/main/resources
mkdir certs
cd certs
```

Sau khi tạo thành công thư mục, chúng ta cần tạo key pair ở thư mục này bằng câu lệnh:

```
openssl genrsa -out keypair.pem 2048
```

Sau khi tạo thành công key pair, chúng ta chạy câu lệnh sau để tạo public key và private key từ key pair.

```
openssl rsa -in keypair.pem -pubout -out public.pem
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.pem
```

Sau khi tạo thành công public key và private key, bạn có thể xóa `keypair.pem` nếu muốn.

## Chạy nhanh ứng dụng

Yêu cầu: Java 17

Hãy chắc chắn là bạn hoàn thành bước tạo key cho JWT thành công trước khi thực hiện bước này.

Để chạy nhanh ứng dụng, tại thư mục gốc của repository này, thực hiện câu lệnh:

```
./gradlew bootRun
```

Đối với cmd/powershell của Windows, chúng ta sử dụng lệnh sau:

```
gradlew.bat bootRun
```

## Tạo file jar từ ứng dụng

Để tạo file jar phục vụ cho mục đích deploy website sau này, chúng ta thực hiện câu lệnh:

```
./gradlew bootJar
```

Tương tự đối với cmd/powershell của Windows, chúng ta sử dụng lệnh sau:

```
gradlew.bat bootJar
```

Sau khi câu lệnh khởi chạy thành công, ở thư mục `build/libs` sẽ xuất hiện file jar như mục đích ban đầu.

Bạn có thể chạy file jar đó với câu lệnh:

```
java -jar projectmanager-0.0.1-SNAPSHOT.jar
```
