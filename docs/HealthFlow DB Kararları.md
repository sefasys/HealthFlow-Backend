Bağlam
HealthFlow; kullanıcı, rol, hasta, doktor, müsaitlik ve randevu verilerini yönetir. Her hastanın bir portal hesabı bulunur; bir kullanıcı hem hasta hem doktor olabilir. Her randevu bir hasta ve bir doktorla ilişkilidir. Bu nedenle ilişkilerin tutarlılığı, veri bütünlüğü ve birlikte tamamlanması gereken işlemlerin transaction ile yönetilmesi önemlidir.
Alternatiflerin karşılaştırılması
NoSQL tek bir veri tabanı türü değildir. Belge, anahtar–değer ve graf gibi farklı modelleri kapsar. Bu kayıtta somut NoSQL alternatifi olarak belge tabanlı MongoDB değerlendirilmiştir.
Ölçüt	PostgreSQL	MongoDB (NoSQL)
Veri modeli	Birbiriyle ilişkili kayıtlar tablolar ve foreign key ile modellenebilir.	Veriler belgeler içinde gömülü veya belgeler arasında referanslarla modellenebilir.
Veri bütünlüğü	Foreign key, UNIQUE, NOT NULL ve CHECK ile kurallar veri tabanında uygulanabilir.	Şema doğrulaması ve benzersiz indeksler kullanılabilir; belgeler arası referans bütünlüğü ayrıca tasarlanmalıdır.
Transaction	Hasta ve hesap oluşturma gibi çok kayıtlı işlemleri birlikte yönetmeye uygundur.	Çok belgeli transaction desteği vardır; NoSQL seçeneği transaction eksikliği nedeniyle elenmemiştir.
HealthFlow'a uygunluk	Hasta–doktor–randevu ilişkileri ve birlikte sorgulama ihtiyacıyla doğrudan örtüşür.	Esnek belge modeli kullanılabilir; ancak mevcut ihtiyaçlar bu esnekliği öncelikli kılmamaktadır.


Gerekçe ve sonuç
PostgreSQL, HealthFlow'un ilişkisel veri yapısını doğrudan modelleyebilmek, bütünlük kurallarını veri tabanında uygulamak ve SQL ile ilişkili kayıtları sorgulayabilmek için seçilmiştir. Anahtar–değer veya graf odaklı bir ana veri tabanı gerektiren somut bir ihtiyaç henüz belirlenmemiştir.
Şema değişiklikleri sürümlü migration dosyalarıyla yönetilecektir. PostgreSQL seçimi randevu çakışmalarını kendiliğinden çözmez; eşzamanlı rezervasyon kuralları ayrıca uygulanacaktır. Oteo'nun mevcut altyapısı, bakım ve yedekleme süreçleriyle uyum mentorla doğrulanacaktır.