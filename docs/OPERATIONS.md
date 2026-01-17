# Operations & Deployment Guide

## 1. Build Requirements
- **JDK**: Java 17 or higher.
- **Build Tool**: Maven 3.8+.
- **Database**: MySQL 8.0+.

---

## 2. Build Lifecycle

### 2.1 Standard Build
Compile and package the application into an executable JAR.
```bash
mvn clean package -DskipTests
```
*Artifact Location*: `target/property-management-system-1.0-SNAPSHOT.jar`

### 2.2 Dependency Management
Update dependencies to latest stable versions:
```bash
mvn versions:use-latest-releases
```

---

## 3. Deployment

### 3.1 Configuration
Environment variables are preferred for production secrets.
Map `.env` variables to Spring properties:

| Env Variable | Spring Property | Default |
| :--- | :--- | :--- |
| `DB_HOST` | `spring.datasource.url` | `localhost:3306` |
| `DB_USER` | `spring.datasource.username` | `root` |
| `AI_API_KEY` | `ai.api-key` | N/A |

### 3.2 Execution
**Systemd Service (Linux Production):**
```ini
[Unit]
Description=Property Management Service
After=syslog.target

[Service]
User=appuser
ExecStart=/usr/bin/java -jar /opt/app/property-system.jar
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
```

**Manual Start:**
```bash
nohup java -jar target/property-management-system-1.0-SNAPSHOT.jar > app.log 2>&1 &
```

---

## 4. Troubleshooting

### 4.1 Common Issues
- **Port Conflict (8081)**:
  `lsof -i :8081` to identify process. Override with `--server.port=8090`.
- **Database Connection**:
  Ensure MySQL is running and `db.properties` matches the server credentials.
- **AI Service Timeout**:
  Check outbound connectivity to `dashscope.aliyuncs.com`. Verify valid API Key.

### 4.2 Logging
- **Application Logs**: Standard Interface (`std out`) or `app.log`.
- **GC Logs**: Enable with `-Xlog:gc*`.
