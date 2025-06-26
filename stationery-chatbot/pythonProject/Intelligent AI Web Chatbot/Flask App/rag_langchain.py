import os
import requests
from dotenv import load_dotenv
from gpt4all import GPT4All
from langchain_huggingface import HuggingFaceEmbeddings
from langchain_community.vectorstores import Chroma

from langchain.schema import Document
from langchain.chains import RetrievalQA
from evaluate_with_gemini import get_gemini_response


# 2) Filter raw API → list of dict with English field names
def filter_products(data):
    filtered = []
    for p in data.get("result", []):
        item = {
            "id":                p.get("productId"),
            "title":             p.get("name"),
            "category":          p.get("category", {}).get("categoryName"),
            "description":       p.get("description"),
            "price":             p.get("minPrice"),
            "quantity":          p.get("quantity"),
            "sold_quantity":     p.get("soldQuantity"),
            "created_at":        p.get("createdAt"),
            "image_url":         p.get("img"),
            "colors":            [c.get("name", c.get("colorId")) for c in p.get("fetchColor", [])],
        }
        detail = p.get("productDetail", {}) or {}
        size_info = detail.get("size") or {}
        color_info = detail.get("color") or {}
        item.update({
            "detail_id":          detail.get("productDetailId"),
            "detail_name":        detail.get("name"),
            "available_quantity": detail.get("availableQuantity"),
            "original_price":     detail.get("originalPrice"),
            "discount_price":     detail.get("discountPrice"),
            "size":               size_info.get("name"),
            "detail_color":       color_info.get("name"),
            "promotions": [
                {
                    "promo_code":     promo.get("promotion", {}).get("promoCode"),
                    "discount_type":  promo.get("promotion", {}).get("discountType"),
                    "discount_value": promo.get("promotion", {}).get("discountValue"),
                }
                for promo in detail.get("productPromotions", [])
            ]
        })
        filtered.append(item)
    return filtered

# Utility to ensure metadata values are simple types

def _sanitize_metadata(value):
    if isinstance(value, list):
        # convert list to comma-separated string
        return ", ".join(str(v) for v in value)
    if isinstance(value, (str, int, float, bool)):
        return value
    # fallback to string
    return str(value)

# 3) Convert list of dict to LangChain Documents with sanitized metadata
# def make_documents(items):
#     docs = []
#     for it in items:
#         # Display fields in English in the content
#         text = f"{it['title']}. Price: {it['price']}₫. Available: {it['available_quantity']}"
#         # Sanitize metadata to primitive types
#         meta = {
#             k: _sanitize_metadata(v)
#             for k, v in it.items()
#             if k != 'title'
#         }
#         docs.append(Document(page_content=text, metadata=meta, id=str(it['id'])))
#     return docs

def make_documents(items):
    docs = []
    for it in items:
        text = (
            f"{it['title']}. "
            f"Category: {it.get('category', '')}. "
            f"Description: {it.get('description', '')}. "
            f"Discount Price: {it.get('discount_price', '')}₫. "
            f"Original Price: {it.get('original_price', '')}₫. "
            f"Available: {it.get('available_quantity')}. "
            f"Size: {it.get('size', '')}. "
            f"Color: {it.get('detail_color', '')}. "
            f"Colors: {', '.join(it.get('colors', []))}."
        )
        meta = {
            k: _sanitize_metadata(v)
            for k, v in it.items()
            if k != 'title'
        }
        docs.append(Document(page_content=text, metadata=meta, id=str(it['id'])))
    return docs

# 4) Initialize and persist vector store
def init_vectorstore(api_url: str, persist_dir: str = "chroma_data"):
    data = requests.get(api_url).json()
    items = filter_products(data)
    docs = make_documents(items)

    embedder = HuggingFaceEmbeddings(model_name="sentence-transformers/all-MiniLM-L6-v2")
    if os.path.exists(persist_dir):
        import shutil
        shutil.rmtree(persist_dir)

    vectordb = Chroma.from_documents(
        docs,
        embedder,
        persist_directory=persist_dir,
        collection_name="products"
    )
    vectordb.persist()
    print(f"[LangChain] Indexed {len(docs)} documents → {persist_dir}")
    return vectordb



def generate_answer_with_gpt4all(context: str, question: str) -> str:
    model_path = os.path.abspath("model/ggml-gpt4all-j-v1.3-groovy.bin")
    model = GPT4All(model_path, allow_download=False)  # thêm allow_download=False
    prompt = f"""You are an assistant.
    Context:
    {context}

    Question: {question}
    Answer:"""
    return model.generate(prompt, n_predict=256)



def answer_query(query, vectordb, k=3):
    # 1) Retrieval
    docs = vectordb.similarity_search(query, k=k)

    # 2) Build context từ page_content
    context = "\n\n".join(doc.page_content for doc in docs)

    # 3) Gọi GPT4All để generate dựa trên context + query
    #answer = generate_answer_with_gpt4all(context, query).
    answer = get_gemini_response(context, query)

    return answer



# if __name__ == "__main__":
#     API_URL = os.getenv(
#         "PRODUCTS_API_URL",
#         "http://localhost:8080/api/products/get-all-products-for-chatbot"
#     )
#     if os.environ.get("WERKZEUG_RUN_MAIN") == "true":
#         store = init_vectorstore(API_URL)
#
#     print("Enter your product query (type 'exit' to quit):")
#     while True:
#         q = input(">> ")
#         if q.lower() in ("exit", "quit"):
#             break
#         # Bây giờ chúng ta xem kết quả LLM 생성
#         print(answer_query(q, store), "\n")