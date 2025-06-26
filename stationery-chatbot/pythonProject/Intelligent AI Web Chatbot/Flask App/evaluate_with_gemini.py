import sqlite3, os, json
import google.generativeai as genai
from dotenv import load_dotenv
import continuous_learning as cl
load_dotenv()
genai.configure(api_key=os.getenv("OPENAI_API_KEY"))
DB_PATH = "logs.db"
MODEL = "gemini-2.0-flash"  # tên model trong SDK cũ

def evaluate_interactions(batch_size=20):
    conn = sqlite3.connect(DB_PATH)
    cur = conn.cursor()
    cur.execute(
        "SELECT id, message, response FROM interactions WHERE gemini_flag IS NULL LIMIT ?",
        (batch_size,)
    )
    rows = cur.fetchall()
    model = genai.GenerativeModel(MODEL)

    for _id, question, answer in rows:
        prompt = (
            f"Bạn là trợ lý kiểm thử chất lượng.\n"
            f"User hỏi: \"{question}\"\n"
            f"Chatbot trả lời: \"{answer}\"\n"
            "Hãy trả về JSON {\"match\":\"yes\"/\"no\",\"explanation\":\"...\"}"
        )
        # gọi generate_content cho cả prompt (single-turn)
        resp = model.generate_content(contents=[prompt])
        content = resp.text.strip()

        try:
            data = json.loads(content)
            flag = 0 if data.get("match") == "yes" else 1
            feedback = data.get("explanation","")
        except:
            flag = 1
            feedback = content

        cur.execute("""
          UPDATE interactions
          SET gemini_flag = ?, gemini_feedback = ?
          WHERE id = ?
        """, (flag, feedback, _id))
        conn.commit()
    conn.close()

def get_gemini_response(context: str, question: str):
    try:
        model = genai.GenerativeModel(MODEL)
        history = cl.fetch_history(limit=2)
        history_text = "\n".join(f"User: {m}\nBot: {r}" for m, r in history)
        prompt = f"""Act as an assistant. Based on the context and the question, provide me with the most natural-sounding answer.
            {history_text}
            Context:
            {context}
            Question: {question}
            Answer:"""
        response = model.generate_content(prompt)
        return response.text
    except Exception as e:
        return f"Đã xảy ra lỗi: {e}"

if __name__ == "__main__":
    evaluate_interactions()
    print("Đã đánh giá batch xong.")
