# BUILD
FROM node:18.15.0-alpine as build-step
RUN mkdir /app
WORKDIR /app
COPY package.json /app
RUN npm install
COPY . /app
RUN npm run build

# DIST
FROM nginx:stable-alpine
COPY --from=build-step /app/dist /usr/share/nginx/html
COPY default-custom.conf /etc/nginx/conf.d/default.conf
CMD ["nginx", "-g", "daemon off;"]