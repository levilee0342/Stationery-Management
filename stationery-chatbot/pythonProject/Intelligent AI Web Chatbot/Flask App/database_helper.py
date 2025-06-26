import mysql.connector
import os
from dotenv import load_dotenv

# Tải biến môi trường từ .env
load_dotenv()

def query_products():
    """
    Truy vấn bảng product trong MySQL.
    """
    try:
        # Lấy thông tin từ .env
        host = os.getenv("DB_HOST")
        port = int(os.getenv("DB_PORT"))  # Mặc định 3306 nếu không có giá trị
        user = os.getenv("DB_USER")
        password = os.getenv("DB_PASSWORD")
        database = os.getenv("DB_NAME")

        # Kết nối tới MySQL
        conn = mysql.connector.connect(
            host=host,
            port=port,
            user=user,
            password=password,
            database=database
        )
        cursor = conn.cursor(dictionary=True)

        # Câu lệnh SQL
        query = """
                    SELECT p.name, p.description, c.category_name 
                    FROM product p
                    JOIN category c ON p.category_id = c.category_id
                """
        cursor.execute(query)

        # Lấy dữ liệu
        results = cursor.fetchall()

        # Đóng kết nối
        cursor.close()
        conn.close()

        return results
    except mysql.connector.Error as err:
        print(f"Lỗi: {err}")
        return None

# Kiểm tra chạy thử
if __name__ == "__main__":
    products = query_products()
    for product in products:
        print(f"Tên: {product['name']}")
        print(f"Mô tả: {product['description']}")
        print(f"Loại: {product['category_name']}")
        print("-" * 30)
