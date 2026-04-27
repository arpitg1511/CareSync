import requests
import json

url = "http://localhost:8080/api/appointments/book"
headers = {
    "Content-Type": "application/json",
    "Authorization": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsInVzZXJJZCI6MjAsInJvbGUiOiJST0xFX1BBVElFTlQiLCJpYXQiOjE3NzY5NDE3OTcsImV4cCI6MTc3NzAyODE5N30.fkH-L52r7OBmtYNS3QWIU5PeH2-Z_WbB0F3BFLoTaLI"
}
data = {
    "providerId": 2,
    "slotId": 101,
    "reason": "Python Automated Test",
    "serviceType": "CONSULTATION",
    "modeOfConsultation": "IN_PERSON"
}

try:
    response = requests.post(url, headers=headers, data=json.dumps(data))
    print(f"Status: {response.status_code}")
    print(f"Response: {response.text}")
except Exception as e:
    print(f"Error: {e}")
