# REST Assured Regression Test Suite

> Yazılım Test Mühendisliği Proje Ödevi — Java / Maven / JUnit 5 / Rest Assured

---

## 📋 Proje Hakkında

Bu proje, **Rest Assured** kütüphanesi kullanılarak **JSONPlaceholder** API'si üzerinde otomatik regresyon testleri içermektedir.

**Test edilen API:** `https://jsonplaceholder.typicode.com`

---

## 🏗️ Teknoloji Stack

| Araç | Versiyon | Amaç |
|------|----------|-------|
| Java | 11+ | Programlama dili |
| Maven | 3.8+ | Bağımlılık yönetimi & build |
| JUnit 5 | 5.10.2 | Test çatısı |
| Rest Assured | 5.4.0 | API test kütüphanesi |
| Jackson | 2.17.0 | JSON serialization/deserialization |
| Allure | 2.27.0 | Test raporlama |
| Hamcrest | 2.2 | Assertion kütüphanesi |

---

## 📂 Proje Yapısı

```
rest-assured-regression-tests/
├── pom.xml
└── src/
    └── test/
        └── java/
            └── com/testproject/
                ├── config/
                │   └── ApiConfig.java        # Base configuration (baseURI, request specs)
                ├── models/
                │   ├── Post.java             # Post POJO model
                │   └── User.java             # User POJO model
                └── tests/
                    ├── PostsGetTest.java     # GET /posts testleri (TC-001 ~ TC-005)
                    ├── PostsPostTest.java    # POST /posts testleri (TC-006 ~ TC-009)
                    └── UsersGetTest.java     # GET /users testleri (TC-010 ~ TC-012)
```

---

## 🧪 Test Senaryoları

### GET Testleri — `/posts`
| Test ID | Senaryo | Beklenen Sonuç |
|---------|---------|----------------|
| TC-001 | Tüm postları getir | 200 OK, 100 kayıt, ≤3000ms |
| TC-002 | Tek post getir (ID=1) | 200 OK, doğru alanlar |
| TC-003 | userId ile filtrele | 200 OK, sadece o kullanıcının postları |
| TC-004 | Geçersiz ID (9999) | 404 Not Found |
| TC-005 | Content-Type kontrolü | `application/json` |

### POST Testleri — `/posts`
| Test ID | Senaryo | Beklenen Sonuç |
|---------|---------|----------------|
| TC-006 | Geçerli body ile post oluştur | 201 Created, ID dönmeli |
| TC-007 | Tüm alanlarla post oluştur | 201 Created, tüm alanlar response'da |
| TC-008 | Ham JSON body ile post oluştur | 201 Created |
| TC-009 | POST response Content-Type | `application/json` |

### GET Testleri — `/users`
| Test ID | Senaryo | Beklenen Sonuç |
|---------|---------|----------------|
| TC-010 | Tüm kullanıcıları getir | 200 OK, 10 kayıt |
| TC-011 | Tek kullanıcı getir (ID=1) | 200 OK, doğru isim/email |
| TC-012 | Email formatı kontrolü | Tüm email'ler `@` içermeli |

### Her test senaryosunda kontrol edilen 3 temel kriter:
1. ✅ **Status code kontrolü** — beklenen HTTP status kodu
2. ✅ **Response body içerisinde beklenen değer kontrolleri** — JSON alanları ve değerleri
3. ✅ **x süre altında cevap dönüldüğünün kontrolü** — ≤3000ms yanıt süresi

---

## 🚀 Çalıştırma

### Ön gereksinimler
- Java 11 veya üzeri
- Maven 3.8 veya üzeri
- İnternet bağlantısı (API testleri için)

### Tüm testleri çalıştır:
```bash
mvn test
```

### Belirli bir test sınıfını çalıştır:
```bash
mvn test -Dtest=PostsGetTest
mvn test -Dtest=PostsPostTest
mvn test -Dtest=UsersGetTest
```

### Allure raporu oluştur ve görüntüle:
```bash
mvn test
mvn allure:serve
```

---

## 📊 Test Çıktısı Örneği

```
✅ TC-001 PASSED: 100 post alındı. Yanıt süresi: 312ms
✅ TC-002 PASSED: Post ID=1 alındı. Yanıt süresi: 201ms
✅ TC-003 PASSED: userId=1 için 10 post bulundu. Yanıt süresi: 189ms
✅ TC-004 PASSED: 404 status kodu alındı. Yanıt süresi: 175ms
✅ TC-005 PASSED: Content-Type doğrulandı. Yanıt süresi: 198ms
✅ TC-006 PASSED: Post oluşturuldu. ID=101. Yanıt süresi: 421ms
...
```

---

## 📚 Kaynaklar

- [Rest Assured Official Docs](https://rest-assured.io/)
- [Rest Assured GitHub](https://github.com/rest-assured/rest-assured)
- [JSONPlaceholder API](https://jsonplaceholder.typicode.com)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Allure Framework](https://allurereport.org/)
