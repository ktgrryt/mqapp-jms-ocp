### ---------- build stage ----------
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app

# 依存のダウンロードを先に済ませてキャッシュヒット率を上げる
COPY pom.xml .
RUN mvn -q dependency:go-offline

# ソース & ビルド
COPY src ./src
RUN mvn -q package -DskipTests

# --- IBM MQ Resource Adapter & Client ----
#   * wmq.jmsra.<ver>.rar   … JMS 2.0 Resource Adapter
#   * com.ibm.mq.allclient  … クライアント JAR（JMS/Java 両方入っている）
ARG MQ_VERSION=9.4.3.0
RUN mvn -q dependency:copy            \
      -Dartifact=com.ibm.mq:wmq.jmsra:${MQ_VERSION}:rar \
      -DoutputDirectory=target/mq  && \
    mvn -q dependency:copy            \
      -Dartifact=com.ibm.mq:com.ibm.mq.allclient:${MQ_VERSION} \
      -DoutputDirectory=target/mq

### ---------- runtime stage ----------
FROM icr.io/appcafe/open-liberty:kernel-slim-java17-openj9-ubi

# MQ ライブラリを shared へ
COPY --chown=1001:0 --from=builder /app/target/mq/wmq.jmsra*.rar \
     /opt/ol/wlp/usr/shared/resources/mq/
COPY --chown=1001:0 --from=builder /app/target/mq/com.ibm.mq.allclient*.jar \
     /opt/ol/wlp/usr/shared/resources/mq/

# Liberty 設定
COPY --chown=1001:0 src/main/liberty/config/ /config/
RUN features.sh            # server.xml で宣言した機能をインストール

# アプリ WAR
COPY --chown=1001:0 --from=builder /app/target/*.war /config/apps/
RUN configure.sh