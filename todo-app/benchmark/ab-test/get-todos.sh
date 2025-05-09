#!/bin/bash

# Configuration
HOST="localhost"
PORT="8080"
ENDPOINT="/api/todos"
URL="http://$HOST:$PORT$ENDPOINT"
NUM_REQUESTS=1000
CONCURRENCY=50
AUTH_TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBbGljZSIsImdyb3VwcyI6WyJhZG1pbnMiLCJ1c2VycyJdLCJpc3MiOiJ0b2RvLWFwcCIsImF1ZCI6InRvZG8tYXBwLXVzZXJzIiwiaWF0IjoxNzQ2Njk1NDQyLCJleHAiOjE3NDY3ODE4NDJ9.ntlcR2V5nKbOpCTPqWEAT2aiLOkdP0FbFudiXCApH-U"

echo "Running benchmark for GET todos..."

# Create a header file for ab
echo "Authorization: Bearer $AUTH_TOKEN" > ../build/auth_header.txt

# Run ab with the header file
ab -n $NUM_REQUESTS -c $CONCURRENCY -H "Authorization: Bearer $AUTH_TOKEN" $URL

# Clean up the header file
rm ../build/auth_header.txt

echo "Benchmark completed!"
