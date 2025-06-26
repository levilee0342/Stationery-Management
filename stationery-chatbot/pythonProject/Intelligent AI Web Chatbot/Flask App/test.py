from gpt4all import GPT4All
import os

def generate_answer_with_gpt4all(context: str, question: str) -> str:
    model_path = os.path.abspath("model/ggml-gpt4all-j-v1.3-groovy.bin")
    model = GPT4All(model_path, allow_download=False)  # thêm allow_download=False
    prompt = f"""You are an assistant.
    Context:
    {context}

    Question: {question}
    Answer:"""
    return model.generate(prompt, n_predict=256)


print(generate_answer_with_gpt4all("hi", "hello there"))
