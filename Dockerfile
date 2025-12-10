FROM node:22-alpine

WORKDIR /app

COPY package*.json /app/

RUN npm install --silent

COPY . /app

# Construir (si quieres construir producción) - opcional según tu flujo
RUN npm run build -- --configuration production || true

EXPOSE 4200

# Ejecuta ng serve; pasar argumentos para que escuche en todas las interfaces dentro del contenedor
ENTRYPOINT ["npm", "start", "--", "--host", "0.0.0.0", "--port", "4200"]
