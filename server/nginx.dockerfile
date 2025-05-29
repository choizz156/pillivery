FROM debian:bullseye-slim

ENV NGINX_VERSION 1.22.1
ENV STICKY_MODULE_GIT_URL https://bitbucket.org/nginx-goodies/nginx-sticky-module-ng.git

# 1. 필수 패키지 설치 (ca-certificates 포함)
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    build-essential \
    libpcre3-dev \
    zlib1g-dev \
    libssl-dev \
    curl \
    git \
    ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# 2. Nginx 소스 다운로드 및 압축 해제
WORKDIR /usr/src
RUN curl -L http://nginx.org/download/nginx-${NGINX_VERSION}.tar.gz | tar -xz && \
    mv nginx-${NGINX_VERSION} nginx

# 3. Sticky 모듈 클론
RUN git clone ${STICKY_MODULE_GIT_URL} /usr/src/nginx-sticky-module-ng

# 4. Nginx 빌드 및 설치
WORKDIR /usr/src/nginx
RUN ./configure \
    --prefix=/etc/nginx \
    --sbin-path=/usr/sbin/nginx \
    --conf-path=/etc/nginx/nginx.conf \
    --error-log-path=/var/log/nginx/error.log \
    --http-log-path=/var/log/nginx/access.log \
    --pid-path=/var/run/nginx.pid \
    --lock-path=/var/run/nginx.lock \
    --with-pcre \
    --with-http_ssl_module \
    --with-http_stub_status_module \
    --add-module=/usr/src/nginx-sticky-module-ng \
    && make && make install && make clean

# 5. 사용자 및 로그 파일 설정
RUN groupadd -r nginx && useradd -r -g nginx -s /sbin/nologin -d /etc/nginx nginx
RUN ln -sf /dev/stdout /var/log/nginx/access.log && \
    ln -sf /dev/stderr /var/log/nginx/error.log

# 6. 사용자 설정 파일 복사
COPY nginx.conf /etc/nginx/nginx.conf

# 7. 포트 노출 및 실행
EXPOSE 80 443
CMD ["nginx", "-g", "daemon off;"]
