#!/bin/bash

# Configuration
HOST="localhost"
PORT="8080"
ENDPOINT="/api/todos"
URL="http://$HOST:$PORT$ENDPOINT"
CONTENT_TYPE="application/json"
AUTH_TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBbGljZSIsImdyb3VwcyI6WyJhZG1pbnMiLCJ1c2VycyJdLCJpc3MiOiJ0b2RvLWFwcCIsImF1ZCI6InRvZG8tYXBwLXVzZXJzIiwiaWF0IjoxNzQ2Njk1NDQyLCJleHAiOjE3NDY3ODE4NDJ9.ntlcR2V5nKbOpCTPqWEAT2aiLOkdP0FbFudiXCApH-U"

# Create data directory if it doesn't exist
mkdir -p ../build/test-data

# Check if we have generated test data
if [ ! "$(ls -A ../build/test-data 2>/dev/null)" ]; then
    echo "Generating test data..."
    kotlin generate-test-data.kts
fi

# Function to create a single todo
create_todo() {
    local file=$1
    echo "Creating todo from $file..."
    curl -s -X POST -H "Content-Type: $CONTENT_TYPE" -H "Authorization: Bearer $AUTH_TOKEN" -d @$file $URL
    echo ""
}

# Create todos from all generated files
echo "Creating multiple todos with different payloads..."
for file in ../build/test-data/*.json; do
    create_todo "$file"
done

echo "All todos created!"
