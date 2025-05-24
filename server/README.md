# Quiz Generation Server

This is a Flask server that uses Gradient AI to generate quizzes based on student topics. The server provides an API endpoint that the Android app can use to fetch generated quizzes.

## Setup

1. Install Python dependencies:
```bash
pip install -r requirements.txt
```

2. Set up environment variables:
Create a `.env` file in the server directory with:
```
GRADIENT_ACCESS_TOKEN=your_access_token
GRADIENT_WORKSPACE_ID=your_workspace_id
```

## Running the Server

Run the server with:
```bash
python main.py
```

The server will start on port 5000.

## API Endpoints

### GET /getQuiz
Generates a quiz based on the provided topic.

Query Parameters:
- `topic`: The topic to generate a quiz for

Response:
```json
{
    "quiz": [
        {
            "question": "Question text",
            "options": ["Option A", "Option B", "Option C", "Option D"],
            "correct_answer": "A"
        }
    ]
}
```

### GET /test
Test endpoint to verify server is running.

Response:
```json
{
    "quiz": "test"
}
```
