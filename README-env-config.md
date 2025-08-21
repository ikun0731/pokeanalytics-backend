# PokéAnalytics 环境变量配置指南

本文档提供了部署 PokéAnalytics 系统时所需的环境变量配置指南。为了安全起见，所有敏感信息都通过环境变量的方式提供，而不是直接硬编码在配置文件中。

## 必需的环境变量

以下是运行 PokéAnalytics 系统必须配置的环境变量：

### 数据库配置

```bash
# 数据库连接信息 (所有服务共用)
DB_HOST=localhost
DB_PORT=3306
DB_USERNAME=root
DB_PASSWORD=your_database_password

# 各服务的数据库名称 (根据服务不同设置)
# user-team-service 使用
DB_NAME=pokemon_user
# pokedex-data-service 使用
DB_NAME=pokemon
# poke-stats-service 使用
DB_NAME=pokemon_stats
```

### Redis 配置

```bash
# Redis 连接信息 (所有服务共用)
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password

# Redis 数据库编号 (根据服务不同设置)
# pokedex-data-service 使用 0 号库 (默认)
# user-team-service 使用
REDIS_DATABASE=1
# poke-stats-service 使用
REDIS_DATABASE=2
```

### JWT 配置 (user-team-service)

```bash
# JWT 签名密钥 (必须是 Base64 编码的字符串)
JWT_SECRET=your_base64_encoded_jwt_secret_key
# JWT 令牌过期时间 (毫秒)，默认为 7 天 (604800000)
JWT_EXPIRATION=604800000
```

### 邮件服务配置 (user-team-service)

```bash
# SMTP 服务器信息
MAIL_HOST=smtp.qq.com
MAIL_PORT=587
MAIL_USERNAME=your_email@qq.com
MAIL_PASSWORD=your_smtp_authorization_code
```

### 智谱 AI API 配置 (user-team-service)

```bash
# 智谱 AI API 密钥
ZHIPU_API_KEY=your_zhipu_ai_api_key
```

### Nacos 服务发现配置 (所有服务)

```bash
# Nacos 服务器地址
NACOS_SERVER_ADDR=localhost:8848
```

## 环境变量设置方法

### Linux/Unix 系统

```bash
# 将以下内容添加到 ~/.bashrc 或 ~/.bash_profile 文件中
export DB_HOST=localhost
export DB_PORT=3306
# ... 添加其他环境变量
```

完成后，运行 `source ~/.bashrc` 使配置生效。

### Windows 系统

在系统设置中添加环境变量：

1. 右键点击 "计算机" > "属性" > "高级系统设置" > "环境变量"
2. 在用户变量或系统变量部分，点击 "新建"
3. 输入变量名和变量值
4. 点击确定保存

### Docker 部署方式

如果使用 Docker 部署，可以在 docker-compose.yml 文件中设置环境变量：

```yaml
version: '3'
services:
  user-team-service:
    image: poke-analytics/user-team-service:latest
    environment:
      - DB_HOST=mysql
      - DB_PORT=3306
      - DB_NAME=pokemon_user
      - DB_USERNAME=root
      - DB_PASSWORD=your_database_password
      # ... 其他环境变量
```

### Spring Boot 应用程序

也可以通过命令行参数启动 Spring Boot 应用程序：

```bash
java -jar user-team-service.jar \
  --DB_HOST=localhost \
  --DB_PASSWORD=your_database_password \
  # ... 其他参数
```

## 环境变量优先级

环境变量的优先级如下：

1. 命令行参数
2. 系统环境变量
3. 应用程序配置文件中的默认值

## 注意事项

- 在生产环境中，请确保使用足够强的密码和密钥
- 定期更换敏感信息，如 JWT 密钥和数据库密码
- 确保环境变量仅对需要访问的用户可见
- 不要将包含敏感信息的环境变量配置文件提交到版本控制系统中
