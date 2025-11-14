# Estágio 1: Build (A "Oficina")
# Usa a imagem oficial do Node 24 (alpine, que é leve)
FROM node:24-alpine AS build

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia os arquivos de definição de dependências
COPY package*.json ./

# Instala as dependências do projeto
RUN npm install

# Copia todo o resto do código-fonte do projeto
COPY . .

# Executa o build de produção do Angular
# ATENÇÃO: Verifique o seu angular.json ou package.json
# O nome do projeto pode ser 'frontend'
RUN npm run build -- --configuration production

# Estágio 2: Serve (O "Produto Final")
# Usa uma imagem super leve do Nginx
FROM nginx:1.27-alpine

# Copia os arquivos "buildados" do Estágio 1 para a pasta onde o Nginx serve HTML
# ATENÇÃO: O caminho /app/dist/frontend/browser deve bater com o nome do seu projeto
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

# Remove a configuração padrão do Nginx
RUN rm /etc/nginx/conf.d/default.conf

# Copia o nosso arquivo de configuração personalizado (nginx.conf)
COPY nginx.conf /etc/nginx/conf.d

# Expõe a porta 80 (padrão do Nginx)
EXPOSE 80