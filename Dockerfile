# ---------- ビルドステージ ----------
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /workspace

COPY pom.xml .
RUN mvn -q dependency:go-offline

COPY src ./src
RUN mvn -Dmaven.test.skip=true -DskipITs package

# ---------- ランタイム（S2I 互換） ----------
FROM icr.io/appcafe/open-liberty:kernel-slim-java17-openj9-ubi

LABEL \
  maintainer="you@example.com" \
  io.openshift.s2i.assemble.timeout="20m" \
  io.openshift.expose-services="9080:http"

USER root

# MQ リソース配置
RUN mkdir -p /opt/ol/wlp/usr/shared/resources/mq
COPY --chown=1001:0 ibm/wmq.jakarta.jmsra.rar \
                     ibm/com.ibm.mq.allclient*.jar \
     /opt/ol/wlp/usr/shared/resources/mq/

# Liberty 設定 & アプリ配置
COPY --chown=1001:0 src/main/liberty/config/ /config/
COPY --chown=1001:0 --from=builder /workspace/target/mqapp.war /config/apps/

# 必要な機能の自動インストール
RUN features.sh && configure.sh

# 注意：ここでは JAVA_TOOL_OPTIONS を設定しない（Deployment 側で設定）
USER 1001
EXPOSE 9080
ENV PORT=9080
