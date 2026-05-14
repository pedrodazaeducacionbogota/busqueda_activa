#!/bin/bash
# ============================================================
# SICobertura - Busqueda Activa - Smoke Test E2E
#
# Valida endpoints BA contra stack local o AWS.
#
# Uso:
#   bash smoke-test.sh                  # localhost por defecto (puerto 9107)
#   bash smoke-test.sh <HOST>           # ej: 54.90.124.49 (AWS)
#   bash smoke-test.sh <HOST> <PORT>    # ej: 54.90.124.49 9107
#
# Exit code: 0 = todos OK, 1 = al menos uno fallo
# ============================================================

set -u

HOST="${1:-localhost}"
PORT="${2:-9107}"
AUTH_PORT="${3:-9101}"

BA_URL="http://${HOST}:${PORT}"
AUTH_URL="http://${HOST}:${AUTH_PORT}"

PASS=0
FAIL=0
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# ============================================================
# Helpers
# ============================================================

assert_code() {
  local name="$1"
  local actual="$2"
  local expected="$3"
  if [ "$actual" = "$expected" ]; then
    echo -e "  ${GREEN}PASS${NC} $name (HTTP $actual)"
    PASS=$((PASS+1))
  else
    echo -e "  ${RED}FAIL${NC} $name (esperado $expected, got $actual)"
    FAIL=$((FAIL+1))
  fi
}

assert_contains() {
  local name="$1"
  local body="$2"
  local needle="$3"
  if echo "$body" | grep -q "$needle"; then
    echo -e "  ${GREEN}PASS${NC} $name (contiene '$needle')"
    PASS=$((PASS+1))
  else
    echo -e "  ${RED}FAIL${NC} $name (no contiene '$needle'). Body: $(echo "$body" | head -c 200)"
    FAIL=$((FAIL+1))
  fi
}

assert_eq() {
  local name="$1"
  local actual="$2"
  local expected="$3"
  if [ "$actual" = "$expected" ]; then
    echo -e "  ${GREEN}PASS${NC} $name ($actual)"
    PASS=$((PASS+1))
  else
    echo -e "  ${RED}FAIL${NC} $name (esperado $expected, got $actual)"
    FAIL=$((FAIL+1))
  fi
}

# ============================================================
echo -e "${YELLOW}=== SICobertura BA Smoke Test ===${NC}"
echo "BA endpoint:   $BA_URL"
echo "Auth endpoint: $AUTH_URL"
echo ""

# ============================================================
# 1. Health check
# ============================================================
echo -e "${YELLOW}[1] Health check${NC}"
HEALTH_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BA_URL/actuator/health")
assert_code "GET /actuator/health" "$HEALTH_CODE" "200"
echo ""

# ============================================================
# 2. Swagger UI accesible
# ============================================================
echo -e "${YELLOW}[2] Swagger UI${NC}"
SWAGGER_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$BA_URL/swagger-ui/index.html")
assert_code "GET /swagger-ui/index.html" "$SWAGGER_CODE" "200"

OPENAPI=$(curl -s "$BA_URL/v3/api-docs")
assert_contains "OpenAPI spec contiene tag busqueda-activa-controller" "$OPENAPI" "busqueda-activa"
echo ""

# ============================================================
# 3. Login admin (auth msv)
# ============================================================
echo -e "${YELLOW}[3] Login admin via auth msv${NC}"
LOGIN_BODY=$(curl -s -X POST "$AUTH_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"usuario":"admin.2025","clave":"@dm1n.202S"}')
TOKEN=$(echo "$LOGIN_BODY" | grep -oE '"token":"[^"]*"' | sed 's/.*:"//;s/"//')

if [ -z "$TOKEN" ]; then
  echo -e "  ${RED}FAIL${NC} Login no devolvio token. Body: $LOGIN_BODY"
  FAIL=$((FAIL+1))
  echo ""
  echo -e "${RED}=== Abortando tests (sin token) ===${NC}"
  exit 1
else
  echo -e "  ${GREEN}PASS${NC} Login OK (token len=${#TOKEN})"
  PASS=$((PASS+1))
fi
AUTH_HEADER="Authorization: Bearer $TOKEN"
echo ""

# ============================================================
# 4. GET /api/busqueda-activa/questions
# ============================================================
echo -e "${YELLOW}[4] GET /api/busqueda-activa/questions${NC}"
QUESTIONS=$(curl -s -H "$AUTH_HEADER" "$BA_URL/api/busqueda-activa/questions")
QUESTIONS_COUNT=$(echo "$QUESTIONS" | grep -oE '"id":[0-9]+' | wc -l)
assert_eq "40 preguntas configuradas" "$QUESTIONS_COUNT" "40"
assert_contains "Pregunta 1 = Primer Nombre del Estudiante" "$QUESTIONS" "Primer Nombre del Estudiante"
assert_contains "Tiene pregunta tipoDocumento (case 5)" "$QUESTIONS" "Tipo de Documento"
echo ""

# ============================================================
# 5. GET /api/busqueda-activa/registros (vacio inicial OK)
# ============================================================
echo -e "${YELLOW}[5] GET /api/busqueda-activa/registros (lista)${NC}"
REGISTROS_CODE=$(curl -s -o /dev/null -w "%{http_code}" -H "$AUTH_HEADER" "$BA_URL/api/busqueda-activa/registros")
assert_code "GET /registros" "$REGISTROS_CODE" "200"

REGISTROS_FILTRO_CODE=$(curl -s -o /dev/null -w "%{http_code}" -H "$AUTH_HEADER" "$BA_URL/api/busqueda-activa/registros?estado=Pendiente&etapa=1")
assert_code "GET /registros con filtros" "$REGISTROS_FILTRO_CODE" "200"
echo ""

# ============================================================
# 6. POST /api/busqueda-activa/registros (crear registro)
# ============================================================
echo -e "${YELLOW}[6] POST /api/busqueda-activa/registros${NC}"
CREATE_BODY=$(curl -s -X POST -H "$AUTH_HEADER" -H "Content-Type: application/json" \
  -d '{}' \
  "$BA_URL/api/busqueda-activa/registros")
CREATED_ID=$(echo "$CREATE_BODY" | grep -oE '"id":[0-9]+' | head -1 | sed 's/"id"://')

if [ -z "$CREATED_ID" ]; then
  echo -e "  ${RED}FAIL${NC} POST /registros no devolvio id. Body: $(echo "$CREATE_BODY" | head -c 300)"
  FAIL=$((FAIL+1))
else
  echo -e "  ${GREEN}PASS${NC} POST /registros (id=$CREATED_ID)"
  PASS=$((PASS+1))
fi
assert_contains "Registro tiene numeroRegistro generado (BA-XXX)" "$CREATE_BODY" "BA-"
assert_contains "Estado inicial = Pendiente" "$CREATE_BODY" "Pendiente"
echo ""

# ============================================================
# 7. GET /api/busqueda-activa/registros/{id}
# ============================================================
if [ -n "$CREATED_ID" ]; then
  echo -e "${YELLOW}[7] GET /api/busqueda-activa/registros/$CREATED_ID${NC}"
  GET_BODY=$(curl -s -H "$AUTH_HEADER" "$BA_URL/api/busqueda-activa/registros/$CREATED_ID")
  assert_contains "Get by id retorna registro" "$GET_BODY" "\"id\":$CREATED_ID"

  echo ""

  # ============================================================
  # 8. POST batch answers (algunas preguntas)
  # ============================================================
  echo -e "${YELLOW}[8] POST /api/busqueda-activa/registros/$CREATED_ID/questions/batch${NC}"
  BATCH_BODY=$(curl -s -X POST -H "$AUTH_HEADER" -H "Content-Type: application/json" \
    -d '{"estudiante_primerNombre":"Juan","estudiante_primerApellido":"Perez","estudiante_tipoDocumento":"CC","estudiante_numeroDocumento":"123456789"}' \
    "$BA_URL/api/busqueda-activa/registros/$CREATED_ID/questions/batch")
  assert_contains "Batch respondio con siguientePregunta" "$BATCH_BODY" "siguientePregunta"
  assert_contains "Historia contiene response Juan" "$BATCH_BODY" "Juan"
  echo ""

  # ============================================================
  # 9. GET flow inicial
  # ============================================================
  echo -e "${YELLOW}[9] GET /api/busqueda-activa/registros/$CREATED_ID/flow${NC}"
  FLOW_CODE=$(curl -s -o /dev/null -w "%{http_code}" -H "$AUTH_HEADER" "$BA_URL/api/busqueda-activa/registros/$CREATED_ID/flow")
  assert_code "GET /flow" "$FLOW_CODE" "200"
  echo ""

  # ============================================================
  # 10. Reporte PDF
  # ============================================================
  echo -e "${YELLOW}[10] GET /api/busqueda-activa/registros/$CREATED_ID/reporte (PDF)${NC}"
  PDF_HEADERS=$(curl -s -I -H "$AUTH_HEADER" "$BA_URL/api/busqueda-activa/registros/$CREATED_ID/reporte")
  PDF_CODE=$(echo "$PDF_HEADERS" | head -1 | awk '{print $2}')
  assert_eq "GET /reporte HTTP" "$PDF_CODE" "200"
  CONTENT_TYPE=$(echo "$PDF_HEADERS" | grep -i "content-type" | tr -d '\r\n')
  if echo "$CONTENT_TYPE" | grep -qi "pdf"; then
    echo -e "  ${GREEN}PASS${NC} Content-Type es PDF"
    PASS=$((PASS+1))
  else
    echo -e "  ${RED}FAIL${NC} Content-Type no es PDF: $CONTENT_TYPE"
    FAIL=$((FAIL+1))
  fi
fi

# ============================================================
# 11. Endpoint inexistente devuelve 404 (no crashea)
# ============================================================
echo ""
echo -e "${YELLOW}[11] Resiliencia - id inexistente${NC}"
NOT_FOUND_CODE=$(curl -s -o /dev/null -w "%{http_code}" -H "$AUTH_HEADER" "$BA_URL/api/busqueda-activa/registros/999999")
assert_code "GET /registros/999999" "$NOT_FOUND_CODE" "404"

# ============================================================
# Resumen
# ============================================================
echo ""
echo -e "${YELLOW}=== Resumen ===${NC}"
echo -e "  ${GREEN}PASS:${NC} $PASS"
echo -e "  ${RED}FAIL:${NC} $FAIL"

if [ "$FAIL" -gt 0 ]; then
  exit 1
fi
exit 0
