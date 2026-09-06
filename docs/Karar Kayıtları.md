# HealthFlow Karar Kayıtları

## 1 - Hangi Web API ve API Specification Mimarisini Kullanacağız?

Web API'lar client ile server arasında nasıl şekilde iletişim kurulması gerektiğini kontratlar üzerinden belirleyen arayüzlerdir.

Projenin bu aşamasında, geliştireceğimiz projeye uyan Web API seçimini yapacağız ve seçimimize göre dokümantasyon için hangi araçları kullanacağımızı belirleyeceğiz. 


### RESTful API / OpenAPI

RESTful API, oldukça güçlü bir arayüz sunar. Fazla kompleks olmayan request-response işlemleri için idealdir ve endüstri standardı haline gelmiştir.

Özellikle geliştirilecek projenin yapısında CRUD işlemleri ön plandaysa sunduğu kullanım kolaylığı ile birlikte tercih edilesi bir opsiyon olacaktır.

Diğer API'lar daha çok request-response işlemleri sırasında veriyi uygun şekilde formatlama isterken RESTful API bu konuda oldukça esnektir.

Ayrıca bahsetmemiz gereken diğer özellikleri ise RESTful API stateless, cacheable bir yapı sunmasıdır.

Stateless: Her request-response işlemi içerisinde tuttuğu değer aynı olsa dahi birbirinden bağımsız şekilde gerçekleşir.
Stateless olduğu için de aynı zamanda request response işlemi sırasında gereken tüm bilgileri HTTP request ve response taşır.

Cacheable: Önbellekleme özelliği sunar. Birçok yazılım şirketi, CDN serverları ile ağ'a gelen istekleri cacheleyerek hız ve veri transferini optimize eder.
CDN server'da bu istekler tutulur ve eğer son girilen zamana göre o web sayfasında bir değişiklik olmamışsa CDN server önbellekteki isteğe göre aynı URL'yi response olarak gönderir.
HTTP status code olarak ise 200 OK yerine 304 Not Modified kodu döner. Bu önbellektenn alındığını gösterir.

Sunduğu katmanlı yapı sayesinde de güvenlik, yük dengeleme, ön belleğe alma gibi ekstra özelliklerin kolayca entegre edilmesini sağlar.

Özetle, bize coupling'i düşük, tarayıcı-istemci uyumu kusursuz, yönetim açısından az maliyetli bir yapı sunar. 



### GraphQL / schemas

Daha karmaşık yapıları göstermek üzere tasarlanan arayüzler için kullanımı idealdir. Özellikle ilişkisel dashboardların yoğun kullanıldığı
örneğin kullanıcı, sipariş, fatura, kargo durumu gibi farklı farklı özellikleri tek ekranda, tek bir URL üzerinde toplayan yapılarda oldukça kullanılası bir yapı sunar.
Dashboard örneği aslında en iyi açıklamadır GraphQL için. 40 tane tablolu veri için ayrı ayrı endpointler açtığımız REST senaryosu yerine GraphQL mantıklı bir seçim olabilir.

Eksileri: Yönetim açısından zaman ve maddi olarak maliyetlidir. Performans açısından ağ yükü az olsa dahi bazı durumlarda kolayca yük binebilir üzerine.


### SOAP / WSDL

**Tercih Sebepleri**

1. **Katı contract:** WSDL ile giriş/çıkış formatları çok net tanımlanır.
2. **Güçlü güvenlik standartları:** WS-Security, imza, sertifika, mesaj güvenliği.
3. **Enterprise özellikleri:** Reliable messaging, transaction gibi kurumsal ihtiyaçlara hazır standartlar sunar.
4. **Legacy uyumluluğu:** Banka, devlet, sağlık gibi yerlerde mevcut sistemlerle entegrasyonu korur.

**Tercih Edilmeme Sebepleri**

1. **Ağır ve karmaşık:** XML + SOAP envelope yapısı gereksiz verbose olabilir.
2. **Geliştirmesi daha zor:** REST/JSON’a kıyasla tooling ve debugging daha zahmetlidir.
3. **Daha fazla veri taşır:** XML nedeniyle payload genellikle daha büyüktür.
4. **Modern web/mobile için gereğinden fazla:** Çoğu CRUD/API ihtiyacında REST daha basit ve pratiktir.


## KARAR: RESTful API kullanacağız. 



## Contract-First vs Code-First


**Neden Contract-First tercih edildi?**

* API sözleşmesi implementasyondan önce netleşir.
* Frontend ve backend ekipleri aynı contract üzerinden paralel çalışabilir.
* Request/response yapıları ve hata kodları daha kontrollü tasarlanır.
* API dokümantasyonu koddan bağımsız, merkezi bir kaynak olur.

**Code-First neden tercih edilmedi?**

* API tasarımı implementation detaylarına fazla bağımlı hale gelebilir.
* Contract değişiklikleri daha geç fark edilebilir.
* Ekipler arası entegrasyonda tutarsızlık riski artabilir.

## KARAR: Contract-First yaklaşımı tercih edildi.



