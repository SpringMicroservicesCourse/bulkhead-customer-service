# Bulkhead Customer Service ⚡

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Resilience4j](https://img.shields.io/badge/Resilience4j-2.x-blue.svg)](https://resilience4j.readme.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 專案介紹

本專案是一個基於 Spring Boot 3.x 和 Resilience4j 的微服務容錯解決方案，主要展示**隔艙模式（Bulkhead Pattern）**在微服務架構中的實際應用。透過限制併發調用數量和等待時間，有效保護下游服務免受大量併發請求的衝擊。

### 🎯 核心功能
- **併發控制** - 限制對下游服務的併發調用數量
- **等待時間管理** - 設定請求排隊的最大等待時間
- **服務保護** - 防止下游服務被大量請求壓垮
- **熔斷整合** - 結合 Circuit Breaker 提供完整的容錯機制

> 💡 **為什麼選擇隔艙模式？**
> - 防止下游依賴被大量併發請求衝擊
> - 避免上游服務因等待下游回應而耗盡執行緒資源
> - 提供可控的服務降級機制
> - 與熔斷器形成完整的容錯保護層

### 🎯 專案特色

- **雙重保護機制** - 結合熔斷器與隔艙模式提供全面容錯
- **彈性配置** - 支援針對不同業務場景的個別化配置
- **即時監控** - 整合 Actuator 提供即時監控與指標
- **服務發現** - 整合 Consul 實現動態服務註冊與發現

## 技術棧

### 核心框架
- **Spring Boot 3.4.5** - 應用程式主框架，提供自動配置與生產就緒功能
- **Spring Cloud** - 微服務生態系統，支援服務發現與配置管理
- **Resilience4j** - 容錯庫，提供熔斷器、隔艙模式等容錯機制

### 容錯與監控
- **Circuit Breaker** - 熔斷器模式，防止級聯故障
- **Bulkhead** - 隔艙模式，控制併發調用與資源隔離
- **Spring Boot Actuator** - 生產監控與管理端點

### 開發工具與輔助
- **OpenFeign** - 宣告式 HTTP 客戶端，簡化服務間調用
- **Consul** - 服務發現與配置中心
- **Apache HttpClient 5** - 高效能 HTTP 連線池管理
- **Lombok** - 減少樣板程式碼，提升開發效率

## 專案結構

```
bulkhead-customer-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── tw/fengqing/spring/springbucks/customer/
│   │   │       ├── controller/
│   │   │       │   └── CustomerController.java        # 客戶端控制器，展示隔艙模式應用
│   │   │       ├── integration/
│   │   │       │   ├── CoffeeService.java             # 咖啡服務 Feign 客戶端
│   │   │       │   └── CoffeeOrderService.java        # 訂單服務 Feign 客戶端
│   │   │       ├── model/
│   │   │       │   ├── Coffee.java                    # 咖啡實體類別
│   │   │       │   ├── CoffeeOrder.java               # 訂單實體類別
│   │   │       │   ├── NewOrderRequest.java           # 新訂單請求DTO
│   │   │       │   └── OrderState.java                # 訂單狀態列舉
│   │   │       ├── support/
│   │   │       │   ├── CustomConnectionKeepAliveStrategy.java  # 自訂連線保持策略
│   │   │       │   ├── MoneySerializer.java           # 金額序列化器
│   │   │       │   └── MoneyDeserializer.java         # 金額反序列化器
│   │   │       └── CustomerServiceApplication.java    # 主應用程式類別
│   │   └── resources/
│   │       ├── application.properties                  # 主要配置檔案
│   │       └── bootstrap.properties                    # 引導配置檔案
│   └── test/
│       └── java/
│           └── tw/fengqing/spring/springbucks/customer/
│               └── CustomerServiceApplicationTests.java
├── pom.xml                                            # Maven 專案配置
└── README.md
```

## 快速開始

### 前置需求
- **Java 21** 或更高版本
- **Maven 3.8+** 構建工具
- **Docker** (用於啟動 Consul)
- **Consul** 服務發現中心

### 安裝與執行

1. **啟動 Consul 服務：**
```bash
docker run -d --name consul -p 8500:8500 consul:latest agent -dev -ui -client 0.0.0.0
```

2. **克隆此倉庫：**
```bash
git clone https://github.com/fengqing-tw/spring-microservices.git
cd "Chapter 13 服務熔斷/bulkhead-customer-service"
```

3. **編譯專案：**
```bash
mvn clean compile
```

4. **執行應用程式：**
```bash
mvn spring-boot:run
# 或直接執行 JAR 檔案
mvn clean package -DskipTests
java -jar target/customer-service-0.0.1-SNAPSHOT.jar
```

5. **驗證服務啟動：**
```bash
# 檢查健康狀態
curl http://localhost:8090/actuator/health

# 檢查隔艙狀態
curl http://localhost:8090/actuator/bulkheads
```

## 隔艙模式配置說明

### 核心配置參數

```properties
# Menu 端點隔艙配置 - 適用於讀取操作
resilience4j.bulkhead.instances.menu.max-concurrent-calls=5
resilience4j.bulkhead.instances.menu.max-wait-duration=5s

# Order 端點隔艙配置 - 適用於寫入操作，更嚴格的控制
resilience4j.bulkhead.instances.order.max-concurrent-calls=1
resilience4j.bulkhead.instances.order.max-wait-duration=5s
```

### 使用方式

#### 1. 編程式使用（Menu 端點示例）
```java
@GetMapping("/menu")
public List<Coffee> readMenu() {
    return Try.ofSupplier(
            Bulkhead.decorateSupplier(bulkhead,
                    CircuitBreaker.decorateSupplier(circuitBreaker,
                            () -> coffeeService.getAll())))
            .recover(CallNotPermittedException.class, Collections.emptyList())
            .recover(BulkheadFullException.class, Collections.emptyList())
            .get();
}
```

#### 2. 註解式使用（Order 端點示例）
```java
@PostMapping("/order")
@io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "order")
@io.github.resilience4j.bulkhead.annotation.Bulkhead(name = "order")
public CoffeeOrder createOrder() {
    // 業務邏輯實作
}
```

## 進階說明

### 環境變數配置
```properties
# 服務基本配置
SERVER_PORT=8090
SPRING_APPLICATION_NAME=customer-service

# Consul 服務發現配置
CONSUL_HOST=localhost
CONSUL_PORT=8500

# Feign 客戶端超時配置
FEIGN_CONNECT_TIMEOUT=500
FEIGN_READ_TIMEOUT=500
```

### HttpClient 連線池配置
```java
/**
 * HttpClient 5.x 連線池配置
 * 提供高效能的 HTTP 連線管理
 */
@Bean
public CloseableHttpClient httpClient() {
    PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
            .setMaxConnTotal(200)  // 設定最大連線數
            .setMaxConnPerRoute(20) // 設定每個路由的最大連線數
            .setValidateAfterInactivity(TimeValue.ofSeconds(30)) // 空閒連線驗證時間
            .build();
    
    return HttpClients.custom()
            .setConnectionManager(connectionManager)
            .disableAutomaticRetries() // 停用自動重試
            .setKeepAliveStrategy(new CustomConnectionKeepAliveStrategy()) // 自定義 Keep-Alive 策略
            .build();
}
```

### 併發測試與驗證

使用 Apache Bench (ab) 進行併發測試：

```bash
# 正常併發測試（不超過隔艙限制）
ab -n 10 -c 5 http://localhost:8090/customer/menu

# 高併發測試（會觸發隔艙限制）
ab -n 20 -c 10 http://localhost:8090/customer/menu
```

## 監控與診斷

### 監控端點

| 端點 | 說明 | 用途 |
|------|------|------|
| `/actuator/health` | 應用程式健康檢查 | 確認服務運行狀態 |
| `/actuator/bulkheads` | 隔艙狀態監控 | 查看隔艙配置與使用情況 |
| `/actuator/circuitbreakers` | 熔斷器狀態監控 | 查看熔斷器狀態與統計 |
| `/actuator/metrics` | 應用程式指標 | 效能監控與分析 |

### 隔艙事件監控
```bash
# 查看隔艙事件
curl http://localhost:8090/actuator/bulkheadevents

# 查看熔斷器事件
curl http://localhost:8090/actuator/circuitbreakerevents
```

## 參考資源

- [Resilience4j 官方文件](https://resilience4j.readme.io/)
- [Spring Cloud Circuit Breaker 指南](https://docs.spring.io/spring-cloud-circuitbreaker/docs/current/reference/html/)
- [Bulkhead Pattern 設計模式說明](https://docs.microsoft.com/en-us/azure/architecture/patterns/bulkhead)
- [Spring Boot Actuator 監控指南](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

## 注意事項與最佳實踐

### ⚠️ 重要提醒

| 項目 | 說明 | 建議做法 |
|------|------|----------|
| 併發設定 | 根據下游服務能力設定合理的併發限制 | 進行壓力測試確定最佳值 |
| 等待時間 | 避免設定過長的等待時間 | 建議 5-10 秒內 |
| 異常處理 | 正確處理 BulkheadFullException | 提供有意義的降級回應 |
| 監控告警 | 設定隔艙滿載的監控告警 | 及時發現容量問題 |

### 🔒 生產環境最佳實踐指南

- **容量規劃** - 根據業務需求和下游服務能力合理設定隔艙大小
- **監控策略** - 建立完整的監控體系，包含隔艙使用率、拒絶率等關鍵指標
- **降級機制** - 為隔艙滿載情況準備有意義的降級邏輯
- **測試驗證** - 定期進行混沌工程測試，驗證隔艙模式的有效性
- **配置調優** - 根據實際運行數據持續調整隔艙參數

## 故障排除

### 常見問題

**Q: 為什麼隔艙沒有生效？**
- 檢查配置格式是否正確（使用 `instances` 而非 `backends`）
- 確認應用程式已正確載入 Resilience4j 自動配置
- 驗證方法上的註解是否正確配置

**Q: 如何調試隔艙拒絕的請求？**
- 查看 `/actuator/bulkheadevents` 端點的事件記錄
- 檢查應用程式日誌中的 BulkheadFullException
- 使用監控工具觀察併發調用模式

**Q: 隔艙與熔斷器如何協同工作？**
- 隔艙控制併發數量，熔斷器根據失敗率做出決策
- 建議將隔艙包裝在熔斷器外層，形成雙重保護
- 兩者的監控指標需要綜合分析

## 授權說明

本專案採用 MIT 授權條款，詳見 LICENSE 檔案。

## 關於我們

我們主要專注在敏捷專案管理、物聯網（IoT）應用開發和領域驅動設計（DDD）。喜歡把先進技術和實務經驗結合，打造好用又靈活的軟體解決方案。

## 聯繫我們

- **FB 粉絲頁**：[風清雲談 | Facebook](https://www.facebook.com/profile.php?id=61576838896062)
- **LinkedIn**：[linkedin.com/in/chu-kuo-lung](https://www.linkedin.com/in/chu-kuo-lung)
- **YouTube 頻道**：[雲談風清 - YouTube](https://www.youtube.com/channel/UCXDqLTdCMiCJ1j8xGRfwEig)
- **風清雲談 部落格**：[風清雲談](https://blog.fengqing.tw/)
- **電子郵件**：[fengqing.tw@gmail.com](mailto:fengqing.tw@gmail.com)

---

**📅 最後更新：2025-08-27**  
**👨‍💻 維護者：風清雲談團隊**
