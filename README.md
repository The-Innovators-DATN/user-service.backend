# user_service.backend


curl -X POST http://localhost:8080/api/v1/user-service/auth/register   -H "Content-Type: application/json"   -d '{ "email": "user3@example.com", "full_name": "User Three", "password": "password123" }'

curl -i -X POST http://localhost:8080/api/v1/user-service/auth/login   -H "Content-Type: application/json"   -d '{ "email": "user3@example.com", "password": "password123" }'

http://160.191.49.128:8001/auth?client_id=759159252498-1tu1ben7amd25d8dfm2kljd3u05683i3.apps.googleusercontent.com&response_type=code&redirect_uri=https://example.com/auth/callback


curl -i -X POST http://localhost:8080/api/user/v0/auth/login   -H "Content-Type: application/json"   -d '{ "email":"user3@example.com", "password": "password123" }'


curl -i -X POST http://localhost:8000/api/user/auth/login   -H "Content-Type: application/json"   -d '{ "email":"user3@example.com", "password": "password123" }'

curl -i -X POST http://localhost:8000/user-service/auth/login   -H "Content-Type: application/json"   -d '{ "email":"user3@example.com", "password": "password123" }'



curl -i -X POST http://localhost:8000/api/user/auth/login   -H "Content-Type: application/json"   -d '{ "email":"user3@example.com", "password": "password123" }'
curl -i -X POST http://localhost:8000/user-service/auth/login   -H "Content-Type: application/json"   -d '{ "email":"user3@example.com", "password": "password123" }'

