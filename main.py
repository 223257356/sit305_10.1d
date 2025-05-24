from gradientai import Gradient
import os
from flask import Flask, request, jsonify
import re

token = 'LXkZBp5a1nGCP16xM7QieYKNz2Ns0tOW'
workspace_id = 'b5f958d9-ffd4-41bb-a492-704114e02c8e_workspace'

os.environ['GRADIENT_ACCESS_TOKEN'] = token
os.environ['GRADIENT_WORKSPACE_ID'] = workspace_id

app = Flask(__name__)

# Initialize the Gradient client
gradient = Gradient()

def fetchQuizFromLlama(student_topic):
    print("Fetching quiz from llama")
    base_model = gradient.get_base_model(base_model_slug="llama3-8b-chat")
    query = (
        f"[INST] Generate a quiz with 3 questions to test students on the provided topic. "
        f"For each question, generate 4 options where only one of the options is correct. "
        f"Format your response as follows:\n"
        f"QUESTION: [Your question here]?\n"
        f"OPTION A: [First option]\n"
        f"OPTION B: [Second option]\n"
        f"OPTION C: [Third option]\n"
        f"OPTION D: [Fourth option]\n"
        f"ANS: [Correct answer letter]\n\n"
        f"Ensure text is properly formatted. It needs to start with a question, then the options, and finally the correct answer."
        f"Follow this pattern for all questions."
        f"Here is the student topic:\n{student_topic}"
        f"[/INST]"
    )
    response = base_model.complete(query=query, max_generated_token_count=500).generated_output
    print(response)
    return response

def process_quiz(quiz_text):
    questions = []
    pattern = re.compile(r'QUESTION: (.+?)\n(?:OPTION A: (.+?)\n)+(?:OPTION B: (.+?)\n)+(?:OPTION C: (.+?)\n)+(?:OPTION D: (.+?)\n)+ANS: (.+?)', re.DOTALL)
    matches = pattern.findall(quiz_text)

    for match in matches:
        question = match[0].strip()
        options = match[1].strip(), match[2].strip(), match[3].strip(), match[4].strip()
        correct_ans = match[-1].strip()

        question_data = {
            "question": question,
            "options": options,
            "correct_answer": correct_ans
        }
        questions.append(question_data)

    return  questions

@app.route('/getQuiz', methods=['GET'])
def get_quiz():
    print("Request received")
    student_topic = request.args.get('topic')
    if student_topic is None:
        return jsonify({'error': 'Missing topic parameter'}), 400
    quiz = fetchQuizFromLlama(student_topic)
    print(quiz)
    return jsonify({'quiz': process_quiz(quiz)}), 200

@app.route('/test', methods=['GET'])
def run_test():
    return jsonify({'quiz': "test"}), 200


if __name__ == '__main__':
    port_num = 5002
    print(f"App running on port {port_num}")
    app.run(port=port_num, host="0.0.0.0")
