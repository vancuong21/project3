Phần mềm bán hàng hóa:

Category (danh muc hang hoa co id tu tang, name tên)

Product (sản phẩm gồm id tự tăng, name , price, description, image, Category ) - mỗi sản phẩm thuộc 1 danh mục
Khi tạo mới sản phẩm, upload file và lưu tại 1 thư mục. sau đó lấy url set vào product và lưu xuống database
Tạo download để tải ảnh về khi hiển thị ở search.

User (id, name, username, password, roles, email) - roles : dùng collectiontables one to many

Bill(id, buyDate, User user)

BillItems (id, Bill , Product , quantity, buyPrice) - mỗi billitem thuộc về 1 bill và 1 product

Viết các entity, và các lớp controller tương ứng.
-- Bảo mật admin mới được quyền vào CRUD. (Nhớ cho phép tạo 1 tài khoản admin trước, rồi sau đó mới giới hạn việc tạo )
