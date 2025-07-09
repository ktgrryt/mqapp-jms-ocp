# ---------- ビルドステージ ----------
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -Dmaven.test.skip=true -DskipITs package

# ---------- ランタイム（S2I互換） ----------
FROM icr.io/appcafe/open-liberty:kernel-slim-java17-openj9-ubi

# ----- メタデータ -----
LABEL \
  maintainer="you@example.com" \
  io.openshift.s2i.assemble.timeout="20m" \
  io.openshift.expose-services="9080:http"

USER root
# shared リソース配置
RUN mkdir -p /opt/ol/wlp/usr/shared/resources/mq
COPY --chown=1001:0 ibm/wmq.jakarta.jmsra.rar \
     ibm/com.ibm.mq.allclient*.jar \
     /opt/ol/wlp/usr/shared/resources/mq/

# サーバー設定
COPY --chown=1001:0 src/main/liberty/config/ /config/

# アプリケーション
COPY --chown=1001:0 --from=builder /workspace/target/mqapp.war /config/apps/

# 必要な機能をインストール
RUN features.sh && configure.sh

USER 1001
EXPOSE 9080
ENV PORT 9080
