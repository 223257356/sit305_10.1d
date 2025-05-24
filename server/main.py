import os
import requests
from flask import Flask, request, jsonify
import re
from flask_cors import CORS
import logging
import json
from datetime import datetime
import stripe  # Add this import

# Set up logging
logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

app = Flask(__name__)
CORS(app)

# Initialize Stripe
stripe.api_key = 'sk_test_51RQ4I0Cx9uIPnzmfnw2wJibCYrPBjMGv4XPF41K2N7bGyKBPVLOvYj7z5ffh09L49XYQGS8BcVQx86Td95yYSlBr00BAG5P01r'  # Replace with your Stripe secret key

# Data storage
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
USERS_FILE = os.path.join(BASE_DIR, "users.json")
QUIZ_HISTORY_FILE = os.path.join(BASE_DIR, "quiz_history.json")

def load_data():
    print(f"Loading data from {USERS_FILE}")
    try:
        with open(USERS_FILE, 'r') as f:
            users = json.load(f)
            print(f"Successfully loaded users: {users}")
    except FileNotFoundError:
        print(f"Users file not found at {USERS_FILE}")
        users = {}

    try:
        with open(QUIZ_HISTORY_FILE, 'r') as f:
            quiz_history = json.load(f)
    except FileNotFoundError:
        quiz_history = {}

    return users, quiz_history

def save_data(users, quiz_history):
    with open(USERS_FILE, 'w') as f:
        json.dump(users, f, indent=2)
    with open(QUIZ_HISTORY_FILE, 'w') as f:
        json.dump(quiz_history, f, indent=2)

# Initialize data
users, quiz_history = load_data()

# Ollama API setup
OLLAMA_API_URL = "http://localhost:11434/api/generate"
MODEL = "gemma:2b"

def get_user_interests(user_id):
    return users.get(user_id, {}).get('interests', [])

def update_user_interests(user_id, interests):
    if user_id not in users:
        users[user_id] = {'interests': [], 'created_at': datetime.now().isoformat()}
    users[user_id]['interests'] = interests
    save_data(users, quiz_history)

def record_quiz_result(user_id, topic, score, total_questions):
    if user_id not in quiz_history:
        quiz_history[user_id] = []

    quiz_history[user_id].append({
        'topic': topic,
        'score': score,
        'total_questions': total_questions,
        'timestamp': datetime.now().isoformat()
    })
    save_data(users, quiz_history)

def get_user_performance(user_id, topic=None):
    if user_id not in quiz_history:
        return []

    user_history = quiz_history[user_id]
    if topic:
        return [q for q in user_history if q['topic'] == topic]
    return user_history

def fetchQuizFromLlama(student_topic, user_interests=None):
    print("Fetching quiz from local Ollama")

    # Create a more focused context-aware prompt
    context = ""
    if user_interests:
        context = (
            f"The user is interested in: {', '.join(user_interests)}. "
        )

    payload = {
        "model": MODEL,
        "prompt": (
            f"Generate EXACTLY 3 multiple-choice questions for a quiz titled: '{student_topic}'. "
            f"{context}"
            f"All questions must be highly relevant to the quiz title and, where possible, connect to the user's interests. "
            f"For each question, provide 4 options (A, B, C, D) with only one correct answer. "
            f"Format your response EXACTLY as follows:\n"
            f"**QUESTION 1:** [Your question here]\n\n"
            f"**A.** [First option]\n"
            f"**B.** [Second option]\n"
            f"**C.** [Third option]\n"
            f"**D.** [Fourth option]\n\n"
            f"**ANS:** [Correct answer letter]\n\n"
            f"**QUESTION 2:** [Your question here]\n\n"
            f"**A.** [First option]\n"
            f"**B.** [Second option]\n"
            f"**C.** [Third option]\n"
            f"**D.** [Fourth option]\n\n"
            f"**ANS:** [Correct answer letter]\n\n"
            f"**QUESTION 3:** [Your question here]\n\n"
            f"**A.** [First option]\n"
            f"**B.** [Second option]\n"
            f"**C.** [Third option]\n"
            f"**D.** [Fourth option]\n\n"
            f"**ANS:** [Correct answer letter]\n\n"
            f"IMPORTANT: Do not generate questions outside the scope of the quiz title. "
            f"Make sure all questions are specific to '{student_topic}' and, if possible, tailored to the user's interests."
        ),
        "stream": False
    }

    try:
        response = requests.post(OLLAMA_API_URL, json=payload)
        if response.status_code == 200:
            result = response.json()["response"]
            print("Generated response:", result)
            return result
        else:
            raise Exception(f"Ollama API request failed: {response.status_code} - {response.text}")
    except requests.exceptions.ConnectionError:
        raise Exception("Could not connect to Ollama. Make sure Ollama is running on port 11434")

def process_quiz(quiz_text):
    questions = []
    pattern = re.compile(
        r'\*\*QUESTION \d+:\*\* (.+?)\n\n'
        r'\*\*A\.\*\* (.+?)\n'
        r'\*\*B\.\*\* (.+?)\n'
        r'\*\*C\.\*\* (.+?)\n'
        r'\*\*D\.\*\* (.+?)\n\n'
        r'\*\*ANS:\*\* \*\*(.+?)\*\*',
        re.DOTALL
    )
    matches = pattern.findall(quiz_text)

    if not matches:
        # Try alternative pattern if the first one doesn't match
        pattern = re.compile(
            r'\*\*QUESTION \d+:\*\* (.+?)\n\n'
            r'\*\*A\.\*\* (.+?)\n'
            r'\*\*B\.\*\* (.+?)\n'
            r'\*\*C\.\*\* (.+?)\n'
            r'\*\*D\.\*\* (.+?)\n\n'
            r'\*\*ANS:\*\* (.+?)(?=\n\n|$)',
            re.DOTALL
        )
        matches = pattern.findall(quiz_text)

    for match in matches:
        question = match[0].strip()
        options = [match[1].strip(), match[2].strip(), match[3].strip(), match[4].strip()]
        correct_ans = match[5].strip().replace('*', '')  # Remove any asterisks from the answer

        question_data = {
            "question": question,
            "options": options,
            "correct_answer": correct_ans
        }
        questions.append(question_data)

    return questions

@app.route('/getQuiz', methods=['GET'])
def get_quiz():
    print("Request received")
    student_topic = request.args.get('topic')
    user_id = request.args.get('user_id')

    if not student_topic:
        return jsonify({'error': 'Missing topic parameter'}), 400

    try:
        # Get user interests if user_id is provided
        user_interests = get_user_interests(user_id) if user_id else None

        quiz = fetchQuizFromLlama(student_topic, user_interests)
        print(quiz)
        processed_quiz = process_quiz(quiz)
        if not processed_quiz:
            return jsonify({'error': 'Failed to parse quiz data', 'raw_response': quiz}), 500
        return jsonify({'quiz': processed_quiz}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/updateInterests', methods=['POST'])
def update_interests():
    data = request.get_json()
    user_id = data.get('user_id')
    interests = data.get('interests', [])

    if not user_id:
        return jsonify({'error': 'Missing user_id'}), 400

    update_user_interests(user_id, interests)
    return jsonify({'message': 'Interests updated successfully'}), 200

@app.route('/submitQuiz', methods=['POST'])
def submit_quiz():
    data = request.get_json()
    user_id = data.get('user_id')
    topic = data.get('topic')
    score = data.get('score')
    total_questions = data.get('total_questions')
    questions = data.get('questions', [])  # List of questions with correct answers
    user_answers = data.get('user_answers', [])  # List of user's selected answers

    if not all([user_id, topic, score is not None, total_questions, questions, user_answers]):
        return jsonify({'error': 'Missing required fields'}), 400

    if user_id not in quiz_history:
        quiz_history[user_id] = []

    # Generate a unique ID for the quiz
    quiz_id = f"{user_id}_{len(quiz_history[user_id])}_{datetime.now().strftime('%Y%m%d%H%M%S')}"

    quiz_history[user_id].append({
        'id': quiz_id,
        'topic': topic,
        'score': score,
        'total_questions': total_questions,
        'questions': questions,
        'user_answers': user_answers,
        'timestamp': datetime.now().isoformat()
    })
    save_data(users, quiz_history)
    return jsonify({'message': 'Quiz result recorded successfully'}), 200

@app.route('/getPerformance', methods=['GET'])
def get_performance():
    user_id = request.args.get('user_id')
    topic = request.args.get('topic')

    if not user_id:
        return jsonify({'error': 'Missing user_id'}), 400

    performance = get_user_performance(user_id, topic)
    return jsonify({'performance': performance}), 200

@app.route('/test', methods=['GET'])
def run_test():
    return jsonify({'quiz': "test"}), 200

@app.route('/verify-api', methods=['GET'])
def verify_api():
    try:
        response = requests.get("http://localhost:11434/api/tags")

        if response.status_code == 200:
            models = response.json().get("models", [])
            gemma_available = any(model["name"] == MODEL for model in models)

            return jsonify({
                'status': 200,
                'message': 'Successfully connected to Ollama',
                'model': MODEL,
                'model_available': gemma_available,
                'available_models': [model["name"] for model in models]
            }), 200
        else:
            return jsonify({
                'error': f"Ollama API request failed: {response.status_code}",
                'message': response.text
            }), 500
    except requests.exceptions.ConnectionError:
        return jsonify({
            'error': "Could not connect to Ollama. Make sure Ollama is running on port 11434"
        }), 500
    except Exception as e:
        return jsonify({
            'error': str(e)
        }), 500

@app.route('/getAvailableTopics', methods=['GET'])
def get_available_topics():
    logger.debug("Received request for /getAvailableTopics")
    user_id = request.args.get('user_id')
    logger.debug(f"User ID: {user_id}")

    if not user_id:
        logger.error("Missing user_id parameter")
        return jsonify({'error': 'Missing user_id parameter'}), 400

    user_interests = get_user_interests(user_id)
    logger.debug(f"User interests: {user_interests}")

    # Generate topic suggestions based on user interests
    payload = {
        "model": MODEL,
        "prompt": (
            f"Based on the user's interests: {', '.join(user_interests)}, "
            f"suggest 5 specific quiz topics that would be relevant and engaging. "
            f"Format your response as a simple list, one topic per line. "
            f"Make the topics specific and focused on areas where the user can test their knowledge. "
            f"For example, if the user is interested in 'Android Development', suggest topics like: "
            f"'Android UI Components', 'Activity Lifecycle', 'Material Design Guidelines', etc."
        ),
        "stream": False
    }

    try:
        logger.debug("Sending request to Ollama")
        response = requests.post(OLLAMA_API_URL, json=payload)
        if response.status_code == 200:
            topics = response.json()["response"].strip().split('\n')
            # Clean up the topics (remove any numbering or extra formatting)
            topics = [topic.strip().lstrip('123456789.- ') for topic in topics]
            logger.debug(f"Generated topics: {topics}")
            return jsonify({'topics': topics}), 200
        else:
            logger.error(f"Ollama API request failed: {response.status_code} - {response.text}")
            raise Exception(f"Ollama API request failed: {response.status_code} - {response.text}")
    except requests.exceptions.ConnectionError:
        logger.error("Could not connect to Ollama")
        raise Exception("Could not connect to Ollama. Make sure Ollama is running on port 11434")

@app.route('/register', methods=['POST'])
def register():
    data = request.get_json()
    username = data.get('username')
    password = data.get('password')
    email = data.get('email')
    phone = data.get('phone')

    if not all([username, password]):
        return jsonify({'error': 'Missing required fields'}), 400

    if username in users:
        return jsonify({'error': 'Username already exists'}), 409

    users[username] = {
        'password': password,  # In production, this should be hashed
        'email': email,
        'phone': phone,
        'interests': [],
        'created_at': datetime.now().isoformat()
    }
    save_data(users, quiz_history)
    return jsonify({'message': 'Registration successful'}), 201

@app.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    username = data.get('username')
    password = data.get('password')
    print(f"Login attempt: username='{username}', password='{password}'")
    print(f"Current users file path: {USERS_FILE}")
    print(f"All users in database: {users}")
    print(f"Stored for '{username}': {users.get(username)}")

    if not all([username, password]):
        return jsonify({'error': 'Missing username or password'}), 400

    user = users.get(username)
    if not user or user.get('password') != password:  # In production, use proper password hashing
        return jsonify({'error': 'Invalid username or password'}), 401

    return jsonify({
        'message': 'Login successful',
        'user': {
            'username': username,
            'email': user.get('email'),
            'interests': user.get('interests', [])
        }
    }), 200

@app.route('/getQuizHistory', methods=['GET'])
def get_quiz_history():
    user_id = request.args.get('user_id')
    if not user_id:
        return jsonify({'error': 'Missing user_id'}), 400
    history = quiz_history.get(user_id, [])
    return jsonify({'history': history}), 200

@app.route('/getPendingTopics', methods=['GET'])
def get_pending_topics():
    user_id = request.args.get('user_id')
    if not user_id:
        return jsonify({'error': 'Missing user_id parameter'}), 400

    # Get all available topics (reuse getAvailableTopics logic)
    user_interests = get_user_interests(user_id)
    payload = {
        "model": MODEL,
        "prompt": (
            f"Based on the user's interests: {', '.join(user_interests)}, "
            f"suggest 5 specific quiz topics that would be relevant and engaging. "
            f"Format your response as a simple list, one topic per line. "
            f"Make the topics specific and focused on areas where the user can test their knowledge. "
            f"For example, if the user is interested in 'Android Development', suggest topics like: "
            f"'Android UI Components', 'Activity Lifecycle', 'Material Design Guidelines', etc."
        ),
        "stream": False
    }
    try:
        response = requests.post(OLLAMA_API_URL, json=payload)
        if response.status_code == 200:
            topics = response.json()["response"].strip().split('\n')
            topics = [topic.strip().lstrip('123456789.- ') for topic in topics]
            # Get completed topics from quiz_history
            completed = set(q['topic'] for q in quiz_history.get(user_id, []))
            pending = [topic for topic in topics if topic not in completed]
            return jsonify({'pending_topics': pending}), 200
        else:
            return jsonify({'error': f'Ollama API request failed: {response.status_code} - {response.text}'}), 500
    except requests.exceptions.ConnectionError:
        return jsonify({'error': 'Could not connect to Ollama. Make sure Ollama is running on port 11434'}), 500

@app.route('/profile', methods=['GET'])
def profile():
    user_id = request.args.get('user_id')
    if not user_id:
        return jsonify({'error': 'Missing user_id parameter'}), 400
    user = users.get(user_id)
    if not user:
        return jsonify({'error': 'User not found'}), 404
    # Quiz stats
    history = quiz_history.get(user_id, [])
    quizzes_done = len(history)
    correct = 0
    incorrect = 0
    for entry in history:
        correct += entry.get('score', 0)
        total = entry.get('total_questions', 0)
        incorrect += (total - entry.get('score', 0))
    profile_data = {
        'username': user_id,
        'email': user.get('email'),
        'phone': user.get('phone'),
        'interests': user.get('interests', []),
        'created_at': user.get('created_at'),
        'quizzes_done': quizzes_done,
        'correct_answers': correct,
        'incorrect_answers': incorrect
    }
    return jsonify({'profile': profile_data}), 200

@app.route('/create-payment-intent', methods=['POST'])
def create_payment_intent():
    try:
        amount = request.form.get('amount', type=int)
        if amount is None: # Check for None explicitly as 0 is a valid amount
            return jsonify({'error': 'Amount is required'}), 400

        # Create or retrieve a customer
        # For demo purposes, creating a new customer each time
        # In production, you should store and reuse customer IDs
        customer = stripe.Customer.create()

        # Create an ephemeral key for the customer
        ephemeral_key = stripe.EphemeralKey.create(
            customer=customer.id,
            stripe_version="2024-04-10" # Updated to a stable API version
        )

        # Create a payment intent
        payment_intent = stripe.PaymentIntent.create(
            amount=amount * 100,  # Convert to cents
            currency='usd',
            customer=customer.id,
            automatic_payment_methods={
                'enabled': True
            }
        )

        return jsonify({
            'paymentIntent': payment_intent.client_secret,
            'ephemeralKey': ephemeral_key.secret,
            'customer': customer.id,
            'publishableKey': 'pk_test_51RQ4I0Cx9uIPnzmfXsKK2r66lInnfEnuJhsL4pNUQWL7osjBUp32yaaxbqHA4JYIiniAZJzV5icCmyuOQcurYjvt00E4KrkJEW'
        })
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    port_num = 5002
    print(f"Starting server on port {port_num}")
    print("Available routes:")
    print("- /test")
    print("- /verify-api")
    print("- /getQuiz")
    print("- /updateInterests")
    print("- /submitQuiz")
    print("- /getPerformance")
    print("- /getAvailableTopics")
    print("- /register")
    print("- /login")
    print("- /getQuizHistory")
    print("- /getPendingTopics")
    print("- /profile")
    print("- /create-payment-intent")
    app.run(host='0.0.0.0', port=port_num, debug=True)
