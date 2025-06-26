# continuous_learning.py

import sqlite3
import json
from datetime import datetime

DB_PATH = 'logs.db'
INTENTS_PATH = 'intents.json'

def init_db():
    """
    Khởi tạo (hoặc đảm bảo tạo nếu chưa có) bảng `interactions` với đủ các cột:
      - timestamp, user_id, message, response
      - predicted_intent, confidence (nếu xài sau)
      - corrected_intent      : cho review thủ công
      - gemini_flag INTEGER   : NULL = chưa đánh giá, 0 = match, 1 = mismatch
      - gemini_feedback TEXT  : lời giải thích từ Gemini
    """
    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()
    c.execute('''
        CREATE TABLE IF NOT EXISTS interactions (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            timestamp TEXT,
            user_id TEXT,
            message TEXT,
            response TEXT,
            predicted_intent TEXT,
            confidence REAL,
            corrected_intent TEXT,
            gemini_flag INTEGER DEFAULT NULL,
            gemini_feedback TEXT DEFAULT NULL
        )
    ''')
    conn.commit()
    conn.close()

def log_interaction(message, response, user_id=None):
    """
    Ghi một dòng mới vào interactions:
      - timestamp là thời điểm hiện tại (UTC)
      - user_id (hiện chỉ lưu None)
      - message, response
      - gemini_flag và gemini_feedback giữ NULL ban đầu
      - corrected_intent giữ NULL
    """
    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()
    c.execute(
        'INSERT INTO interactions(timestamp, user_id, message, response) VALUES (?, ?, ?, ?)',
        (datetime.utcnow().isoformat(), user_id, message, response)
    )
    conn.commit()
    conn.close()

def fetch_uncorrected_messages():
    """
    Trả về các bản ghi interactions có GEMINI_FLAG = 1 (đã được Gemini đánh giá mismatch)
    và CORRECTED_INTENT vẫn còn NULL (chưa được người xem gán intent mới).
    Sắp xếp từ mới → cũ (ta sẽ show lên giao diện review_gemini).
    """
    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()
    c.execute("""
        SELECT id, timestamp, user_id, message, response, gemini_feedback
        FROM interactions
        WHERE gemini_flag = 1
          AND corrected_intent IS NULL
        ORDER BY timestamp DESC
    """)
    rows = c.fetchall()
    conn.close()
    return rows

def fetch_all_uncorrected_for_human():
    """
    Nếu bạn muốn đồng thời hiển thị các interactions “chưa đánh giá bởi human review”
    (tức là GEMINI_FLAG = 1 but CORRECTED_INTENT IS NULL), thì có thể dùng hàm này.
    (Thực chất giống fetch_uncorrected_messages về mặt logic.)
    """
    return fetch_uncorrected_messages()

def fetch_all_messages():
    """
    Trả về tất cả messages chưa được human review, bất kể GEMINI_FLAG = 1 hay không?
    (Nếu bạn muốn show tất cả những tương tác nếu GEMINI_FLAG = 1 và CORRECTED_INTENT IS NULL,
      ta đã có fetch_uncorrected_messages;
     nếu muốn show tách biệt:
      - fetch_all_messages() chỉ show interaction CHƯA ĐƯỢC GEMINI đánh giá (GEMINI_FLAG IS NULL),
      - fetch_uncorrected_messages() show interaction GEMINI đánh giá mismatch.)
    Ở đây, ta giữ lại kiểu cũ: trả về các interaction CHƯA ĐƯỢC HUMAN REVIEW (corrected_intent IS NULL),
    nhưng không phân biệt GEMINI_FLAG. Nếu muốn tinh chỉnh, bạn có thể thay đổi điều kiện WHERE.
    """
    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()
    c.execute("""
        SELECT id, message, response, timestamp
        FROM interactions
        WHERE corrected_intent IS NULL
        ORDER BY timestamp ASC
    """)
    rows = c.fetchall()
    conn.close()
    return rows

def fetch_history(limit=2):
    """
    Lấy cặp (message, response) gần nhất, theo thứ tự time DESC,
    chỉ lấy tối đa `limit` bản ghi. Dùng cho get_gemini_response.
    Trả về list: [(message1, response1), (message2, response2), ...]
    """
    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()
    c.execute("""
        SELECT message, response
        FROM interactions
        ORDER BY timestamp DESC
        LIMIT ?
    """, (limit,))
    rows = c.fetchall()
    conn.close()
    return rows

def update_intents(corrections: dict, new_responses: dict = None):
    if new_responses is None:
        new_responses = {}

    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()

    with open(INTENTS_PATH, 'r', encoding='utf-8') as f:
        intents_data = json.load(f)

    for interaction_id, new_tag in corrections.items():
        c.execute(
            'UPDATE interactions SET corrected_intent = ? WHERE id = ?',
            (new_tag, interaction_id)
        )
        c.execute('SELECT message FROM interactions WHERE id = ?', (interaction_id,))
        row = c.fetchone()
        if not row:
            continue
        message = row[0]

        found = False
        for intent in intents_data['intents']:
            if intent['tag'] == new_tag:
                intent['patterns'].append(message)
                # Thêm phần responses
                if interaction_id in new_responses:
                    new_resp = new_responses[interaction_id]
                    if 'responses' not in intent or not isinstance(intent['responses'], list):
                        intent['responses'] = []
                    if new_resp not in intent['responses']:
                        intent['responses'].append(new_resp)
                found = True
                break

        if not found:
            # Tạo intent mới với patterns và responses
            responses = []
            if interaction_id in new_responses:
                responses.append(new_responses[interaction_id])
            intents_data['intents'].append({
                'tag': new_tag,
                'patterns': [message],
                'responses': responses,
                'context_set': ""
            })

    with open(INTENTS_PATH, 'w', encoding='utf-8') as f:
        json.dump(intents_data, f, ensure_ascii=False, indent=2)

    conn.commit()
    conn.close()


if __name__ == '__main__':
    import argparse
    parser = argparse.ArgumentParser(description='CLI for continuous learning')
    parser.add_argument('--init_db', action='store_true', help='Khởi tạo DB')
    parser.add_argument('--fetch_uncorrected', action='store_true', help='In ra các interaction chưa human review')
    parser.add_argument('--fetch_history', action='store_true', help='In ra history mới nhất')
    args = parser.parse_args()

    if args.init_db:
        init_db()
    elif args.fetch_uncorrected:
        for row in fetch_uncorrected_messages():
            print(row)
    elif args.fetch_history:
        for m, r in fetch_history():
            print(f"User: {m} → Bot: {r}")
    else:
        parser.print_help()
