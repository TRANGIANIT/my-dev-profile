# Japanese Self Introduction

## Simple Version

はじめまして。グエン・ミン・ロックと申します。
JavaとSpring Bootを中心にバックエンド開発を勉強しています。
REST API、データベース設計、保守しやすいコードを書くことに興味があります。
将来は、チームに貢献できるバックエンドエンジニアになりたいです。
よろしくお願いいたします。

## Vietnamese Meaning

Xin chào, tôi tên là Nguyễn Minh Lộc.
Tôi đang học phát triển backend chủ yếu với Java và Spring Boot.
Tôi quan tâm đến REST API, thiết kế database và viết code dễ bảo trì.
Trong tương lai, tôi muốn trở thành backend engineer có thể đóng góp tốt cho team.
Rất mong được giúp đỡ.

## Project Explanation - Ecommerce API

私のポートフォリオプロジェクトは、Spring Bootで作成したECサイト向けのバックエンドAPIです。
主な機能は、商品管理API、注文作成API、ユーザー登録・ログインAPIです。
レイヤードアーキテクチャを意識して、Controller、Service、Repository、DTO、Entityを分けて実装しました。

データベースはPostgreSQLを想定しており、Flywayでテーブル定義を管理しています。
また、注文作成時には商品の在庫数を確認し、在庫が足りない場合はエラーを返すようにしています。
同時更新の問題を考慮して、ProductエンティティにはOptimistic Lockingを追加しました。

セキュリティ面では、Spring SecurityとJWTを使って認証を実装しました。
商品登録・更新・削除はADMIN権限、注文APIはUSERまたはADMIN権限が必要です。
テストについては、MockMvcを使ったAPI結合テストと、Testcontainersを使ったPostgreSQL向けの結合テストを用意しました。

## Project Explanation - Vietnamese Meaning

Dự án portfolio của tôi là backend API cho hệ thống ecommerce, được xây dựng bằng Spring Boot.
Chức năng chính gồm Product API, Order API, Register/Login API.
Tôi tách rõ Controller, Service, Repository, DTO, Entity theo layered architecture.

Database hướng tới PostgreSQL và quản lý schema bằng Flyway.
Khi tạo order, hệ thống kiểm tra tồn kho; nếu không đủ hàng thì trả lỗi.
Để xử lý vấn đề cập nhật đồng thời, tôi thêm optimistic locking vào Product entity.

Về security, tôi dùng Spring Security và JWT.
Tạo/sửa/xóa product yêu cầu quyền ADMIN; order API yêu cầu USER hoặc ADMIN.
Về testing, tôi có MockMvc integration test và Testcontainers test cho PostgreSQL.

## Interview Answers

### なぜこの構成にしましたか

保守しやすくするために、役割ごとにクラスを分けました。
Controllerはリクエストとレスポンスを担当し、Serviceはビジネスロジック、RepositoryはDBアクセスを担当します。
Entityを直接APIで返さず、DTOを使うことでAPIの形を安定させやすくしています。

### バリデーションはどこで行っていますか

リクエストDTOにBean Validationを付けて、Controllerで`@Valid`を使っています。
入力エラーはGlobalExceptionHandlerで共通のエラーレスポンスに変換しています。

### Flywayを使った理由は何ですか

DBスキーマの変更履歴をコードとして管理するためです。
`ddl-auto=update`だけに依存すると、本番環境で意図しない変更が起きる可能性があります。
Flywayを使うことで、どのテーブル変更がいつ入ったかを明確にできます。

### JWT認証の流れを説明してください

ユーザーはログインAPIでユーザー名とパスワードを送ります。
認証に成功するとJWTを返します。
クライアントは次のリクエストで`Authorization: Bearer <token>`を付けます。
サーバー側ではJWTフィルターがトークンを検証し、認証情報をSecurityContextに設定します。

### 在庫更新で気をつけたことは何ですか

注文作成時に商品の在庫数を確認し、注文数量が在庫を超える場合はエラーにします。
また、同時に注文が入った場合の競合を考慮して、Productに`@Version`を追加し、Optimistic Lockingを使っています。

### テストでは何を確認していますか

Product APIとOrder APIの正常系・異常系をMockMvcで確認しています。
例えば、作成、取得、更新、削除、バリデーションエラー、権限エラー、在庫不足エラーをテストしています。
また、Dockerが利用できる環境ではTestcontainersでPostgreSQLを使った結合テストも実行できます。
