# =========================================
# Angular Development
# =========================================

FROM node:24.12.0-alpine

WORKDIR /app

# Install dependencies first for cache
COPY package*.json ./

RUN npm install

# Copy source
COPY . .

EXPOSE 4200

CMD ["npm", "start", "--", "--host", "0.0.0.0"]