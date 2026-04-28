.PHONY: dev build up down logs

# 로컬 개발: DB + Redis만 실행
dev-infra:
	docker compose -f docker-compose.dev.yml up -d
	@echo "PostgreSQL: localhost:5432"
	@echo "Redis: localhost:6379"

dev-infra-down:
	docker compose -f docker-compose.dev.yml down

# 전체 빌드 및 실행 (프로덕션 모드)
build:
	docker compose build

up:
	docker compose up -d

down:
	docker compose down

logs:
	docker compose logs -f

# 백엔드만 재빌드
rebuild-backend:
	docker compose build backend
	docker compose up -d backend

status:
	docker compose ps
