# ============================================
# CrèmeLogic - Makefile Global
# ============================================

.PHONY: up down status logs clean backend frontend rebuild

# --- Mode Docker Complet (Prod-like) ---
up:
	docker-compose up -d --build

down:
	docker-compose down

status:
	docker-compose ps

logs:
	docker-compose logs -f

rebuild:
	docker-compose up -d --build --force-recreate

# --- Mode Hybride (Backend Docker + Frontend Local) ---
backend:
	cd backend && make updev

backend-down:
	cd backend && make downdev

frontend:
	cd frontend && npm start

# --- Nettoyage ---
clean:
	docker system prune -f
	cd backend && mvn clean
	rm -rf frontend/dist
