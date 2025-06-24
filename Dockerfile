# S2I用に修正したDockerfile

# ---------- ビルドステージ ----------
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
# テストをスキップしてビルド
RUN mvn -Dmaven.test.skip=true package

# ---------- ランタイムステージ ----------
FROM icr.io/appcafe/open-liberty:kernel-slim-java17-openj9-ubi

# IBM MQ用のリソースアダプタ
COPY --chown=1001:0 ibm/wmq.jakarta.jmsra.rar /config/

# サーバー設定ファイル
COPY --chown=1001:0 src/main/liberty/config/ /config/

# 必要な Liberty 機能をインストール
RUN features.sh

# アプリケーション WAR
COPY --chown=1001:0 --from=builder /app/target/*.war /config/apps/

# Liberty の設定を最適化
RUN configure.sh

# S2I スクリプトとの互換性のために環境変数を設定
ENV PORT=9080

# コンテナがリッスンするポート
EXPOSE 9080

# ユーザー権限を設定
USER 1001